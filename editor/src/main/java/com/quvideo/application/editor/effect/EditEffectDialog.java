package com.quvideo.application.editor.effect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.DPUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.application.download.DownloadDialog;
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.IEffectEditClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.chroma.EffectChromaDialog;
import com.quvideo.application.editor.effect.collage.CollageAdjustDialog;
import com.quvideo.application.editor.effect.collage.CollageCurveAdjustDialog;
import com.quvideo.application.editor.effect.collage.CollageFilterDialog;
import com.quvideo.application.editor.effect.collage.CollageOverlayDialog;
import com.quvideo.application.editor.effect.keyframe.EffectKeyFrameDialog;
import com.quvideo.application.editor.effect.mask.EffectMaskDialog;
import com.quvideo.application.editor.effect.plugin.EffectPluginDialog;
import com.quvideo.application.editor.effect.subfx.CollageSubFxDialog;
import com.quvideo.application.editor.effect.subtitle.EditEffectInputDialog;
import com.quvideo.application.editor.effect.subtitle.EffectSubtitleDialog;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.PosDraw;
import com.quvideo.application.editor.sound.EditDubDialog;
import com.quvideo.application.editor.sound.EffectAddMusicDialog;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.utils.RandomUtil;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.QEXytUtil;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.Ve3DDataF;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.AudioEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.FloatEffect;
import com.quvideo.mobile.engine.model.SubtitleEffect;
import com.quvideo.mobile.engine.model.effect.AudioFade;
import com.quvideo.mobile.engine.model.effect.EffectAddItem;
import com.quvideo.mobile.engine.model.effect.EffectKeyFrameInfo;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.BaseKeyFrame;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAdd;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAudioFade;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAudioRepeat;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAudioReplace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPCopy;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPDel;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPDestRange;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPKeyFrameUpdateOffset;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPKeyFrameUpdateOffsetAll;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPLock;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMirror;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMultiSubtitleText;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPPosInfo;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPTrimRange;
import com.quvideo.mobile.engine.work.operate.theme.ThemeOPBgmReset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class EditEffectDialog extends BaseEffectMenuView {

  private static final String[] AUDIO_RECORD_PERMISSIONS = {
      Manifest.permission.RECORD_AUDIO,
  };

  private int groupId;

  private RecyclerView mRecyclerView;
  private EditEffectAdapter mEffectAdapter;

  private EffectOperateAdapter mEffectOperateAdapter;

  private PosDraw mPosDraw = new PosDraw();

  private EffectPosInfo oldEffectPosInfo = null;
  private EffectKeyFrameInfo oldEffectKeyFrameInfo = null;

  private int currentTime = 0;
  private int startTime = 0;
  private int maxTime = 0;

  private boolean isHadKeyFrame = false;

  public EditEffectDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    workSpace.getPlayerAPI().getPlayerControl().pause();
    currentTime = workSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    mWorkSpace.addObserver(mBaseObserver);
    showMenu(container, mItemOnClickListener, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectEdit;
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPAdd
          || operate instanceof EffectOPDel
          || operate instanceof EffectOPCopy
          || operate instanceof EffectOPTrimRange
          || operate instanceof EffectOPDestRange
          || operate instanceof EffectOPAudioReplace
          || operate instanceof EffectOPAudioRepeat
          || operate instanceof EffectOPAudioFade
          || operate instanceof ThemeOPBgmReset) {
        // 刷新数据
        if (mWorkSpace.getPlayerAPI() != null
            && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
          mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        }
        isHadKeyFrame = false;
        List<BaseEffect> dataList = mWorkSpace.getEffectAPI().getEffectList(groupId);
        mEffectAdapter.updateList(dataList);
        int selectIndex = mEffectAdapter.getSelectIndex();
        if (selectIndex >= 0) {
          BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, selectIndex);
          if (baseEffect != null) {
            if (mWorkSpace.getPlayerAPI() != null
                && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
              mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());
            }
            if (baseEffect instanceof AnimEffect) {
              isHadKeyFrame = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.positionList.size() > 0
                  || ((AnimEffect) baseEffect).mEffectKeyFrameInfo.rotationList.size() > 0
                  || ((AnimEffect) baseEffect).mEffectKeyFrameInfo.scaleList.size() > 0;
            } else {
              isHadKeyFrame = false;
            }
            startTime = baseEffect.destRange.getPosition();
            if (baseEffect.destRange.getTimeLength() > 0) {
              maxTime = baseEffect.destRange.getLimitValue();
            } else {
              maxTime = mWorkSpace.getStoryboardAPI().getDuration();
            }
            updateFakeView(selectIndex, baseEffect);
          }
        } else {
          isHadKeyFrame = false;
          if (mFakeApi != null) {
            mFakeApi.setTarget(null, null);
          }
        }
        if (mEffectOperateAdapter != null) {
          mEffectOperateAdapter.updateList(getOperateList());
        }
      } else if (operate instanceof EffectOPMultiSubtitleText) {
        int selectIndex = mEffectAdapter.getSelectIndex();
        isHadKeyFrame = false;
        if (selectIndex >= 0
            && selectIndex == ((EffectOPMultiSubtitleText) operate).getEffectIndex()) {
          BaseEffect curEffect = mWorkSpace.getEffectAPI().getEffect(groupId, selectIndex);
          if (curEffect instanceof AnimEffect) {
            isHadKeyFrame = ((AnimEffect) curEffect).mEffectKeyFrameInfo.positionList.size() > 0
                || ((AnimEffect) curEffect).mEffectKeyFrameInfo.rotationList.size() > 0
                || ((AnimEffect) curEffect).mEffectKeyFrameInfo.scaleList.size() > 0;
            if (isHadKeyFrame) {
              EffectPosInfo effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, selectIndex, currentTime - startTime);
              try {
                oldEffectPosInfo = effectPosInfo.clone();
                oldEffectKeyFrameInfo = ((AnimEffect) curEffect).mEffectKeyFrameInfo.clone();
              } catch (Throwable ignore) {
              }
              mFakeApi.setTarget(mPosDraw, effectPosInfo);
            } else {
              EffectPosInfo effectPosInfo = ((FloatEffect) curEffect).mEffectPosInfo;
              try {
                oldEffectPosInfo = effectPosInfo.clone();
              } catch (Throwable ignore) {
              }
              mFakeApi.setTarget(mPosDraw, effectPosInfo);
            }
          } else {
            if (curEffect != null) {
              EffectPosInfo effectPosInfo = ((FloatEffect) curEffect).mEffectPosInfo;
              try {
                oldEffectPosInfo = effectPosInfo.clone();
                if (curEffect instanceof AnimEffect) {
                  oldEffectKeyFrameInfo = ((AnimEffect) curEffect).mEffectKeyFrameInfo.clone();
                }
              } catch (Throwable ignore) {
              }
              mFakeApi.setTarget(mPosDraw, effectPosInfo);
            }
          }
        }
      }
    }
  };

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      currentTime = progress;
      if (playerStatus == PlayerStatus.STATUS_PAUSE
          || playerStatus == PlayerStatus.STATUS_PLAYING
          || playerStatus == PlayerStatus.STATUS_STOP
          || playerStatus == PlayerStatus.STATUS_SEEKING) {
        updateFakeFocus(progress);
      }
    }

    @Override public void onPlayerRefresh() {
      if (mWorkSpace != null
          && mWorkSpace.getPlayerAPI() != null
          && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
        currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
        updateFakeFocus(currentTime);
      }
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };

  private void updateFakeFocus(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      isHadKeyFrame = false;
      if (mFakeApi != null) {
        mFakeApi.setTarget(null, null);
      }
    } else {
      if (isHadKeyFrame) {
        int selectIndex = mEffectAdapter.getSelectIndex();
        if (selectIndex >= 0) {
          EffectPosInfo effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, selectIndex, curTime - startTime);
          try {
            oldEffectPosInfo = effectPosInfo.clone();
            BaseEffect curEffect = mWorkSpace.getEffectAPI().getEffect(groupId, selectIndex);
            if (curEffect instanceof AnimEffect) {
              oldEffectKeyFrameInfo = ((AnimEffect) curEffect).mEffectKeyFrameInfo.clone();
            }
          } catch (Throwable ignore) {
          }
          mFakeApi.setTarget(mPosDraw, effectPosInfo);
        }
      }
    }
  }

  private void updateFakeView(final int index, BaseEffect curEffect) {
    if (mFakeApi == null) {
      return;
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_MOSAIC
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_WATERMARK
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
      EffectPosInfo effectPosInfo = ((FloatEffect) curEffect).mEffectPosInfo;
      try {
        oldEffectPosInfo = effectPosInfo.clone();
        if (curEffect instanceof AnimEffect) {
          oldEffectKeyFrameInfo = ((AnimEffect) curEffect).mEffectKeyFrameInfo.clone();
        }
      } catch (Throwable ignore) {
      }
      mFakeApi.setTarget(mPosDraw, effectPosInfo);
      mFakeApi.setFakeViewListener(new IFakeViewListener() {

        @Override public void onEffectMoving() {
          FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
          BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
          EffectPosInfo targetPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
          EffectPosInfo backupPosInfo = null;
          try {
            backupPosInfo = targetPosInfo.clone();
          } catch (Throwable ignore) {
          }
          FakePosUtils.INSTANCE.updateEffectPosByFakePos(curFakePos, targetPosInfo);
          if (isHadKeyFrame) {
            updateKeyFrameOffset(targetPosInfo, false);
            if (backupPosInfo != null && oldEffectKeyFrameInfo != null && oldEffectKeyFrameInfo.scaleList.size() > 1) {
              // 放大倍数是相对effectposinfo的，所以size不可以变化
              targetPosInfo.size = new Ve3DDataF(backupPosInfo.size);
            }
          }
          EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, index, targetPosInfo);
          effectOPPosInfo.setFastRefresh(true);
          mWorkSpace.handleOperation(effectOPPosInfo);
        }

        @Override public void onEffectMoveStart() {
          EffectOPLock effectOPLock = new EffectOPLock(groupId, index, true);
          mWorkSpace.handleOperation(effectOPLock);
          EffectOPStaticPic
              effectOPStaticPic = new EffectOPStaticPic(groupId, index, true);
          mWorkSpace.handleOperation(effectOPStaticPic);
        }

        @Override public void onEffectMoveEnd(boolean moved) {
          EffectOPLock effectOPLock = new EffectOPLock(groupId, index, false);
          mWorkSpace.handleOperation(effectOPLock);
          EffectOPStaticPic
              effectOPStaticPic = new EffectOPStaticPic(groupId, index, false);
          mWorkSpace.handleOperation(effectOPStaticPic);
          FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
          BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
          EffectPosInfo targetPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
          EffectPosInfo backupPosInfo = null;
          try {
            backupPosInfo = targetPosInfo.clone();
          } catch (Throwable ignore) {
          }
          FakePosUtils.INSTANCE.updateEffectPosByFakePos(curFakePos, targetPosInfo);
          if (isHadKeyFrame) {
            updateKeyFrameOffset(targetPosInfo, true);
            if (backupPosInfo != null && oldEffectKeyFrameInfo != null && oldEffectKeyFrameInfo.scaleList.size() > 1) {
              // 放大倍数是相对effectposinfo的，所以size不可以变化
              targetPosInfo.size = new Ve3DDataF(backupPosInfo.size);
            }
          }
          // 放大倍数是相对effectposinfo的，所以size不可以变化
          EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, index, targetPosInfo);
          effectOPPosInfo.setFastRefresh(false);
          mWorkSpace.handleOperation(effectOPPosInfo);
        }

        @Override public void checkEffectTouchHit(@NotNull PointF pointF) {
          List<BaseEffect> list = mWorkSpace.getEffectAPI().getEffectList(groupId);
          if (list == null || list.isEmpty()) {
            mEffectAdapter.setSelectIndex(-1);
            return;
          }
          for (int i = 0; i < list.size(); i++) {
            BaseEffect effect = list.get(i);
            if (effect.destRange.contains(currentTime)) {
              EffectPosInfo effectPosInfo = ((FloatEffect) effect).mEffectPosInfo;
              RectF targetRect = effectPosInfo.getRectArea();
              if (targetRect != null
                  && targetRect.contains(pointF.x, pointF.y)) {
                if (mEffectAdapter.getSelectIndex() != i) {
                  // focus选中的效果
                  isHadKeyFrame = false;
                  if (effect instanceof AnimEffect) {
                    isHadKeyFrame = ((AnimEffect) effect).mEffectKeyFrameInfo.positionList.size() > 0
                        || ((AnimEffect) effect).mEffectKeyFrameInfo.rotationList.size() > 0
                        || ((AnimEffect) effect).mEffectKeyFrameInfo.scaleList.size() > 0;
                  }
                  updateFakeView(i, effect);
                  mEffectAdapter.setSelectIndex(i);
                } else if (groupId == QEGroupConst.GROUP_ID_SUBTITLE
                    && effect instanceof SubtitleEffect) {
                  // 点击同一个字幕弹起编辑
                  new EditEffectInputDialog(getContext(), mMenuContainer, mWorkSpace, groupId, i, 0);
                }
                return;
              }
            }
          }
        }
      });
    } else {
      mFakeApi.setTarget(null, null);
    }
    updateFakeFocus(currentTime);
  }

  /**
   * 更新关键帧的属性
   */
  private void updateKeyFrameOffset(EffectPosInfo targetPosInfo, boolean isEnd) {
    if (oldEffectPosInfo != null && oldEffectKeyFrameInfo != null) {
      int selectIndex = mEffectAdapter.getSelectIndex();
      Ve3DDataF curPosOffset = null;
      if (oldEffectKeyFrameInfo.positionList.size() > 1) {
        curPosOffset = new Ve3DDataF(targetPosInfo.center.x - oldEffectPosInfo.center.x,
            targetPosInfo.center.y - oldEffectPosInfo.center.y,
            targetPosInfo.center.z - oldEffectPosInfo.center.z);
        curPosOffset.x += oldEffectKeyFrameInfo.positionList.get(0).baseOffset.x;
        curPosOffset.y += oldEffectKeyFrameInfo.positionList.get(0).baseOffset.y;
        curPosOffset.z += oldEffectKeyFrameInfo.positionList.get(0).baseOffset.z;
        if (isEnd) {
          EffectOPKeyFrameUpdateOffset posKeyFrameOffsetOP = new EffectOPKeyFrameUpdateOffset(groupId, selectIndex,
              BaseKeyFrame.KeyFrameType.Position, curPosOffset);
          mWorkSpace.handleOperation(posKeyFrameOffsetOP);
        }
      }
      Ve3DDataF curRotateOffset = null;
      if (oldEffectKeyFrameInfo.rotationList.size() > 1) {
        curRotateOffset = new Ve3DDataF(targetPosInfo.degree.x - oldEffectPosInfo.degree.x,
            targetPosInfo.degree.y - oldEffectPosInfo.degree.y,
            targetPosInfo.degree.z - oldEffectPosInfo.degree.z);
        curRotateOffset.x += oldEffectKeyFrameInfo.rotationList.get(0).baseOffset.x;
        curRotateOffset.y += oldEffectKeyFrameInfo.rotationList.get(0).baseOffset.y;
        curRotateOffset.z += oldEffectKeyFrameInfo.rotationList.get(0).baseOffset.z;
        if (isEnd) {
          EffectOPKeyFrameUpdateOffset rotateKeyFrameOffsetOP = new EffectOPKeyFrameUpdateOffset(groupId, selectIndex,
              BaseKeyFrame.KeyFrameType.Rotation, curRotateOffset);
          mWorkSpace.handleOperation(rotateKeyFrameOffsetOP);
        }
      }
      Ve3DDataF curAnchorOffset = null;
      if (oldEffectKeyFrameInfo.anchorOffsetList.size() > 1) {
        curAnchorOffset = new Ve3DDataF(targetPosInfo.anchorOffset.x - oldEffectPosInfo.anchorOffset.x,
            targetPosInfo.anchorOffset.y - oldEffectPosInfo.anchorOffset.y,
            targetPosInfo.anchorOffset.z - oldEffectPosInfo.anchorOffset.z);
        curAnchorOffset.x += oldEffectKeyFrameInfo.anchorOffsetList.get(0).baseOffset.x;
        curAnchorOffset.y += oldEffectKeyFrameInfo.anchorOffsetList.get(0).baseOffset.y;
        curAnchorOffset.z += oldEffectKeyFrameInfo.anchorOffsetList.get(0).baseOffset.z;
        if (isEnd) {
          EffectOPKeyFrameUpdateOffset anchorKeyFrameOffsetOP = new EffectOPKeyFrameUpdateOffset(groupId, selectIndex,
              BaseKeyFrame.KeyFrameType.AnchorOffset, curAnchorOffset);
          mWorkSpace.handleOperation(anchorKeyFrameOffsetOP);
        }
      }
      Ve3DDataF curScaleOffset = null;
      if (oldEffectKeyFrameInfo.scaleList.size() > 1) {
        curScaleOffset = new Ve3DDataF(targetPosInfo.size.x / oldEffectPosInfo.size.x,
            targetPosInfo.size.y / oldEffectPosInfo.size.y,
            1.0f);
        curScaleOffset.x *= oldEffectKeyFrameInfo.scaleList.get(0).baseOffset.x;
        curScaleOffset.y *= oldEffectKeyFrameInfo.scaleList.get(0).baseOffset.y;
        curScaleOffset.z = 1.0f;
        if (isEnd) {
          EffectOPKeyFrameUpdateOffset scaleKeyFrameOffsetOP = new EffectOPKeyFrameUpdateOffset(groupId, selectIndex,
              BaseKeyFrame.KeyFrameType.Scale, curScaleOffset);
          mWorkSpace.handleOperation(scaleKeyFrameOffsetOP);
        }
      }
      if (!isEnd) {
        EffectOPKeyFrameUpdateOffsetAll opKeyFrameUpdateOffsetAll = new EffectOPKeyFrameUpdateOffsetAll(groupId, selectIndex,
            curPosOffset, curRotateOffset, curScaleOffset, curAnchorOffset);
        mWorkSpace.handleOperation(opKeyFrameUpdateOffsetAll);
      }
    }
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    View rootView = view.findViewById(R.id.root_layout);
    rootView.setOnClickListener(v -> {
      // 只是为了拦击点击事件
    });
    mEffectAdapter =
        new EditEffectAdapter(mWorkSpace, getActivity(), groupId, mOnEffectClickListener);
    // effect
    mRecyclerView = view.findViewById(R.id.clip_recyclerview);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mRecyclerView.setAdapter(mEffectAdapter);
    mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
          @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int dp2 = DPUtils.dpToPixel(getContext(), 4);
        outRect.left = dp2;
        outRect.right = dp2;
      }
    });
    List<BaseEffect> dataList = mWorkSpace.getEffectAPI().getEffectList(groupId);
    mEffectAdapter.updateList(dataList);
    int selectIndex = mEffectAdapter.getSelectIndex();
    if (selectIndex >= 0) {
      BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, selectIndex);
      if (baseEffect != null) {
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());
        startTime = baseEffect.destRange.getPosition();
        if (baseEffect.destRange.getTimeLength() > 0) {
          maxTime = baseEffect.destRange.getLimitValue();
        } else {
          maxTime = mWorkSpace.getStoryboardAPI().getDuration();
        }
        if (baseEffect instanceof AnimEffect) {
          isHadKeyFrame = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.positionList.size() > 0
              || ((AnimEffect) baseEffect).mEffectKeyFrameInfo.rotationList.size() > 0
              || ((AnimEffect) baseEffect).mEffectKeyFrameInfo.scaleList.size() > 0;
        } else {
          isHadKeyFrame = false;
        }
        updateFakeView(selectIndex, baseEffect);
      }
    }
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
    // 操作view
    RecyclerView editRecyclerView = view.findViewById(R.id.operate_recyclerview);
    editRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mEffectOperateAdapter = new EffectOperateAdapter(getOperateList(), mItemOnClickListener);
    editRecyclerView.setAdapter(mEffectOperateAdapter);
  }

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.removeObserver(mBaseObserver);
      mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
    }
  }

  private IEffectEditClickListener mItemOnClickListener = new IEffectEditClickListener() {
    @Override public void onClick(View view, EffectBarItem operate) {
      if (operate != null && operate.getAction() == EffectBarItem.ACTION_QRCODE) {
        ZXingManager.go2CaptureActivity(getActivity(), EditorActivity.INTENT_REQUEST_QRCODE);
        return;
      }
      int index = mEffectAdapter.getSelectIndex();
      if (index < 0) {
        ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
            R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        return;
      }
      BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
      if (baseEffect == null) {
        ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
            R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        return;
      }
      switch (operate.getAction()) {
        case EffectBarItem.ACTION_EDIT:
          if (groupId == QEGroupConst.GROUP_ID_BGMUSIC) {
            new EffectAddMusicDialog(getContext(), mMenuContainer, mWorkSpace, groupId);
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_TRIM:
          new EditEffectTrimDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_CUT:
          new EditEffectCutDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_DUPLICATE:
          EffectOPCopy effectOPCopy = new EffectOPCopy(groupId, index);
          mWorkSpace.handleOperation(effectOPCopy);
          break;
        case EffectBarItem.ACTION_SUBTITLE_EDIT:
          if (baseEffect instanceof SubtitleEffect) {
            new EffectSubtitleDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_VOLUME:
          if (baseEffect.isHadAudio) {
            new EditEffectVolumeDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index
            );
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_ALPHA:
          new EditEffectAlphaDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_MAGIC:
          if (baseEffect instanceof AudioEffect) {
            new EditEffectToneDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index
            );
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_MIRROR:
          //if (baseEffect instanceof SubtitleEffect) {
          //  if (((SubtitleEffect) baseEffect).getTextBubbleInfo().isDftTemplate) {
          //    // 默认字幕背景素材，不支持镜像翻转
          //    ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          //        getContext().getString(R.string.mn_edit_tips_no_support),
          //        Toast.LENGTH_LONG);
          //    return;
          //  }
          //}
          if (baseEffect instanceof FloatEffect) {
            FloatEffect.Mirror mirror = ((FloatEffect) baseEffect).mMirror;
            if (mirror == FloatEffect.Mirror.EFFECT_FLIP_NONE) {
              mirror = FloatEffect.Mirror.EFFECT_FLIP_X;
            } else if (mirror == FloatEffect.Mirror.EFFECT_FLIP_X) {
              mirror = FloatEffect.Mirror.EFFECT_FLIP_XY;
            } else if (mirror == FloatEffect.Mirror.EFFECT_FLIP_XY) {
              mirror = FloatEffect.Mirror.EFFECT_FLIP_Y;
            } else {
              mirror = FloatEffect.Mirror.EFFECT_FLIP_NONE;
            }
            EffectOPMirror effectOPMirror = new EffectOPMirror(groupId, index, mirror);
            mWorkSpace.handleOperation(effectOPMirror);
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_MASK:
          new EffectMaskDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index, mFakeApi);
          break;
        case EffectBarItem.ACTION_CHROMA:
          new EffectChromaDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index, mFakeApi);
          break;
        case EffectBarItem.ACTION_MOSAIC_DEGREE:
          new EditEffectMosaicDegreeDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_FX_PLUGIN:
          new EffectPluginDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_ROTATE_AXLE:
          new EffectRotateAxleDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index, mFakeApi);
          break;
        case EffectBarItem.ACTION_COLLAGE_FILTER:
          new CollageFilterDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_COLLAGE_FX:
          new CollageSubFxDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_COLLAGE_OVERLAY:
          new CollageOverlayDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_COLLAGE_ADJUST:
          new CollageAdjustDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_COLLAGE_CURVE_ADJUST:
          new CollageCurveAdjustDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_KEYFRAME:
          mWorkSpace.getPlayerAPI().getPlayerControl().pause();
          new EffectKeyFrameDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index, mFakeApi);
          break;
        case EffectBarItem.ACTION_AUDIO_FADE_IN:
          boolean isFadeOn = ((AudioEffect) baseEffect).getAudioInfo().getAudioFadeIn().duration > 0;
          EffectOPAudioFade effectOPAudioFadeIn = new EffectOPAudioFade(groupId, index,
              new AudioFade(AudioFade.Type.FadeIn, isFadeOn ? 0 : 2000));
          mWorkSpace.handleOperation(effectOPAudioFadeIn);
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              isFadeOn ? R.string.mn_edit_music_fade_in_turn_off : R.string.mn_edit_music_fade_in_turn_on,
              Toast.LENGTH_SHORT);
          break;
        case EffectBarItem.ACTION_AUDIO_FADE_OUT:
          boolean isFadeOut = ((AudioEffect) baseEffect).getAudioInfo().getAudioFadeOut().duration > 0;
          EffectOPAudioFade effectOPAudioFadeOut = new EffectOPAudioFade(groupId, index,
              new AudioFade(AudioFade.Type.FadeOut, isFadeOut ? 0 : 2000));
          mWorkSpace.handleOperation(effectOPAudioFadeOut);
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              isFadeOut ? R.string.mn_edit_music_fade_out_turn_off : R.string.mn_edit_music_fade_out_turn_on,
              Toast.LENGTH_SHORT);
          break;
        case EffectBarItem.ACTION_BGM_REPEAT:
          boolean isRepeat = ((AudioEffect) baseEffect).getAudioInfo().isRepeat;
          EffectOPAudioRepeat effectOPAudioRepeat = new EffectOPAudioRepeat(groupId, index, !isRepeat);
          mWorkSpace.handleOperation(effectOPAudioRepeat);
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              isRepeat ? R.string.mn_edit_music_repeat_turn_off : R.string.mn_edit_music_repeat_turn_on,
              Toast.LENGTH_SHORT);
          break;
        case EffectBarItem.ACTION_BGM_RESET_TO_THEME:
          ThemeOPBgmReset themeOPBgmReset = new ThemeOPBgmReset();
          mWorkSpace.handleOperation(themeOPBgmReset);
          break;
        case EffectBarItem.ACTION_DEL:
          EffectOPDel effectOPDel = new EffectOPDel(groupId, index);
          mWorkSpace.handleOperation(effectOPDel);
          break;
        default:
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              getContext().getString(R.string.mn_edit_tips_no_define),
              Toast.LENGTH_LONG);
          break;
      }
    }
  };

  @Override public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == EditorActivity.INTENT_REQUEST_QRCODE && data != null) {
      String result = data.getStringExtra(ZXingManager.ZXING_RESULT_QRMSG);
      if (!TextUtils.isEmpty(result)) {
        try {
          JSONObject jsonObject = new JSONObject(result);
          String ttid = jsonObject.optString("ttid");
          String url = jsonObject.optString("url");
          if (groupId != QEGroupConst.GROUP_ID_STICKER
              && groupId != QEGroupConst.GROUP_ID_STICKER_FX
              && groupId != QEGroupConst.GROUP_ID_SUBTITLE) {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
            return false;
          }
          if (!TextUtils.isEmpty(ttid) && !TextUtils.isEmpty(url)) {
            if (groupId == QEGroupConst.GROUP_ID_STICKER && !(ttid.contains("0x05") && !ttid.contains("0x05000000003"))) {
              // 需要贴纸，但不是贴纸素材
              // 无滤镜
              ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                  R.string.mn_edit_tips_template_qrcode_error, Toast.LENGTH_LONG);
              return true;
            } else if (groupId == QEGroupConst.GROUP_ID_STICKER_FX && !ttid.contains("0x06")) {
              // 需要特效，但不是特效素材
              // 无滤镜
              ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                  R.string.mn_edit_tips_template_qrcode_error, Toast.LENGTH_LONG);
              return true;
            } else if (groupId == QEGroupConst.GROUP_ID_SUBTITLE && !ttid.contains("0x09")) {
              // 需要字幕，但不是字幕素材
              // 无滤镜
              ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                  R.string.mn_edit_tips_template_qrcode_error, Toast.LENGTH_LONG);
              return true;
            }
            DownloadDialog downloadDialog = new DownloadDialog(new DownloadDialog.OnTemplateDownloadOver() {
              @Override public void onDownloadOver(String templateCode) {
                handleAddEffect(templateCode);
              }
            });
            downloadDialog.showDownloading(getActivity(), ttid, url);
            return true;
          }
        } catch (Exception ignore) {
        }
      }
    }
    return false;
  }

  private void handleAddEffect(String templateCode) {
    long templateId = QEXytUtil.ttidHexStrToLong(templateCode);
    if (templateId <= 0) {
      // 无滤镜
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    XytInfo info = XytManager.getXytInfo(templateId);
    if (info == null) {
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    EffectAddItem effectAddItem = new EffectAddItem();
    effectAddItem.mEffectPath = info.filePath;
    effectAddItem.destRange
        = new VeRange(mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime(), 0);
    VeMSize streamSize = mWorkSpace.getStoryboardAPI().getStreamSize();
    EffectPosInfo effectPosInfo = new EffectPosInfo();
    effectPosInfo.center.x = streamSize.width * RandomUtil.randInt(1000, 9000) / 10000f;
    effectPosInfo.center.y = streamSize.height * RandomUtil.randInt(1000, 9000) / 10000f;
    effectAddItem.mEffectPosInfo = effectPosInfo;
    if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      effectAddItem.subtitleTexts = Collections.singletonList(
          EditorApp.Companion.getInstance().app.getString(R.string.mn_edit_tips_input_text));
    }
    EffectOPAdd effectOPAdd = new EffectOPAdd(groupId, 0, effectAddItem);
    mWorkSpace.handleOperation(effectOPAdd);
  }

  private EditEffectAdapter.OnEffectClickListener mOnEffectClickListener =
      new EditEffectAdapter.OnEffectClickListener() {
        @Override public void onClick(int index, BaseEffect item) {
          if (item == null || index < 0) {
            // TODO 添加
            if (QEGroupConst.GROUP_ID_COLLAGES == groupId) {
              currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
              go2choosePhoto();
            } else if (QEGroupConst.GROUP_ID_BGMUSIC == groupId
                || QEGroupConst.GROUP_ID_DUBBING == groupId) {
              new EffectAddMusicDialog(getContext(), mMenuContainer, mWorkSpace, groupId);
            } else if (QEGroupConst.GROUP_ID_RECORD == groupId) {
              if (hasPermissionsGranted(getActivity())) {
                new EditDubDialog(getContext(), mMenuContainer, mWorkSpace);
              } else {
                ActivityCompat.requestPermissions(getActivity(), AUDIO_RECORD_PERMISSIONS, 1);
              }
            } else {
              new EffectAddDialog(getContext(), mMenuContainer, mWorkSpace, groupId);
            }
          } else {
            BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
            isHadKeyFrame = false;
            if (baseEffect != null) {
              mWorkSpace.getPlayerAPI().getPlayerControl().pause();
              mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());
              currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
              startTime = baseEffect.destRange.getPosition();
              if (baseEffect.destRange.getTimeLength() > 0) {
                maxTime = baseEffect.destRange.getLimitValue();
              } else {
                maxTime = mWorkSpace.getStoryboardAPI().getDuration();
              }
              if (baseEffect instanceof AnimEffect) {
                isHadKeyFrame = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.positionList.size() > 0
                    || ((AnimEffect) baseEffect).mEffectKeyFrameInfo.rotationList.size() > 0
                    || ((AnimEffect) baseEffect).mEffectKeyFrameInfo.scaleList.size() > 0;
              } else {
                isHadKeyFrame = false;
              }
              updateFakeView(index, baseEffect);
            }
          }
        }
      };

  private boolean hasPermissionsGranted(Activity activity) {
    for (String permission : AUDIO_RECORD_PERMISSIONS) {
      if (ActivityCompat.checkSelfPermission(activity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private void go2choosePhoto() {
    GallerySettings settings = new GallerySettings.Builder()
        .minSelectCount(1)
        .maxSelectCount(1)
        .showMode(GalleryDef.MODE_BOTH)
        .build();

    GalleryClient.getInstance().initSetting(settings);
    //enter gallery
    GalleryClient.getInstance().performLaunchGallery(getActivity());

    GalleryClient.getInstance().initProvider(new IGalleryProvider() {
      @Override
      public boolean checkFileEditAble(String filePath) {
        int res = MediaFileUtils.checkFileEditAble(filePath);
        return res == SDKErrCode.RESULT_OK;
      }

      @Override
      public void onGalleryFileDone(ArrayList<MediaModel> mediaList) {
        super.onGalleryFileDone(mediaList);
        if (mediaList != null && mediaList.size() > 0 && mWorkSpace != null) {
          EffectAddItem effectAddItem = new EffectAddItem();
          effectAddItem.mEffectPath = mediaList.get(0).getFilePath();
          effectAddItem.destRange = new VeRange(currentTime, 0);
          VeMSize streamSize = mWorkSpace.getStoryboardAPI().getStreamSize();
          EffectPosInfo effectPosInfo = new EffectPosInfo();
          effectPosInfo.center.x = streamSize.width * RandomUtil.randInt(1000, 9000) / 10000f;
          effectPosInfo.center.y = streamSize.height * RandomUtil.randInt(1000, 9000) / 10000f;
          effectAddItem.mEffectPosInfo = effectPosInfo;
          EffectOPAdd effectOPAdd = new EffectOPAdd(groupId, 0, effectAddItem);
          mWorkSpace.handleOperation(effectOPAdd);
        }
      }
    });
  }

  @Override
  protected String getBottomTitle() {
    if (groupId == QEGroupConst.GROUP_ID_MOSAIC) {
      return getContext().getString(R.string.mn_edit_title_mosaic);
    } else if (groupId == QEGroupConst.GROUP_ID_STICKER) {
      return getContext().getString(R.string.mn_edit_title_sticker);
    } else if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      return getContext().getString(R.string.mn_edit_title_subtitle);
    } else if (groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      return getContext().getString(R.string.mn_edit_title_collages);
    } else if (groupId == QEGroupConst.GROUP_ID_STICKER_FX) {
      return getContext().getString(R.string.mn_edit_title_fx);
    } else if (groupId == QEGroupConst.GROUP_ID_BGMUSIC) {
      return getContext().getString(R.string.mn_edit_title_bgm);
    } else if (groupId == QEGroupConst.GROUP_ID_DUBBING) {
      return getContext().getString(R.string.mn_edit_title_dubbing);
    }
    return getContext().getString(R.string.mn_edit_title_edit);
  }

  private List<EffectBarItem> getOperateList() {
    List<EffectBarItem> list = new ArrayList<>();
    int selectIndex = mEffectAdapter.getSelectIndex();
    boolean isOpEnabled = selectIndex >= 0;
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_STICKER_FX
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      if (ZXingManager.isHadSuperZXing()) {
        list.add(new EffectBarItem(EffectBarItem.ACTION_QRCODE, R.drawable.editor_tool_qrcode_scan,
            getContext().getString(R.string.mn_edit_qrcode_scan), true));
      }
    }
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_EDIT, R.drawable.edit_icon_edit_nor,
              getContext().getString(R.string.mn_edit_bgm_edit), isOpEnabled));
    }
    if (groupId != QEGroupConst.GROUP_ID_BGMUSIC) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_TRIM, R.drawable.edit_icon_trim_n,
              getContext().getString(R.string.mn_edit_title_trim), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC
        || groupId == QEGroupConst.GROUP_ID_DUBBING
        || groupId == QEGroupConst.GROUP_ID_RECORD
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_CUT, R.drawable.edit_icon_crop_n,
              getContext().getString(R.string.mn_edit_title_crop), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_DUPLICATE, R.drawable.edit_icon_duplicate,
          getContext().getString(R.string.mn_edit_duplicate_title), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_SUBTITLE_EDIT, R.drawable.edit_icon_key_nor,
          getContext().getString(R.string.mn_edit_subtitle_input), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC
        || groupId == QEGroupConst.GROUP_ID_DUBBING
        || groupId == QEGroupConst.GROUP_ID_RECORD
        //  暂时隐藏，后续支持在线贴纸后打开
        /*|| groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_STICKER_FX*/
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_VOLUME, R.drawable.edit_icon_muteoff_n,
          getContext().getString(R.string.mn_edit_title_volume), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_WATERMARK
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_ALPHA, R.drawable.edit_icon_alpha_nor,
          getContext().getString(R.string.mn_edit_alpha_change), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC
        || groupId == QEGroupConst.GROUP_ID_DUBBING
        || groupId == QEGroupConst.GROUP_ID_RECORD) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_MAGIC, R.drawable.edit_icon_changevoice_nor,
          getContext().getString(R.string.mn_edit_title_change_voice), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_MOSAIC) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_MOSAIC_DEGREE, R.drawable.edit_icon_adjust_nor,
              getContext().getString(R.string.mn_edit_mosaic_degree), isOpEnabled));
    }

    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_MIRROR, R.drawable.edit_icon_mirror_nor,
          getContext().getString(R.string.mn_edit_title_mirror), isOpEnabled));
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_ROTATE_AXLE, R.drawable.edit_icon_scale_nor,
              getContext().getString(R.string.mn_edit_title_rotate), isOpEnabled));
    }
    if (EditorApp.Companion.getInstance().getEditorConfig().isEffectMaskValid()
        && (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_COLLAGES)) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_MASK, R.drawable.editor_icon_collage_tool_mask,
              getContext().getString(R.string.mn_edit_title_mask), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_CHROMA, R.drawable.editor_icon_collage_tool_chroma,
              getContext().getString(R.string.mn_edit_title_chroma), isOpEnabled));
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_FX_PLUGIN, R.drawable.editor_icon_collage_tool_framework,
              getContext().getString(R.string.mn_edit_tools_plugin_title), isOpEnabled));
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_COLLAGE_FILTER, R.drawable.edit_icon_filter_nor,
              getContext().getString(R.string.mn_edit_title_filter), isOpEnabled));
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_COLLAGE_OVERLAY, R.drawable.editor_icon_collage_tool_overlay,
              getContext().getString(R.string.mn_edit_title_collage_overlay), isOpEnabled));
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_COLLAGE_ADJUST, R.drawable.edit_icon_adjust_nor,
              getContext().getString(R.string.mn_edit_title_adjust), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_COLLAGES
        || groupId == QEGroupConst.GROUP_ID_STICKER) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_COLLAGE_CURVE_ADJUST, R.drawable.editor_tool_adjust_curve,
              getContext().getString(R.string.mn_edit_title_adjust_curve), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_COLLAGE_FX, R.drawable.edit_icon_effect_nor,
              getContext().getString(R.string.mn_edit_title_fx), isOpEnabled));
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_KEYFRAME, R.drawable.editor_tool_keyframeanimator_icon,
              getContext().getString(R.string.mn_edit_keyframe_animator_title), isOpEnabled));
    }
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC
        || groupId == QEGroupConst.GROUP_ID_DUBBING) {
      int index = mEffectAdapter.getSelectIndex();
      if (index >= 0) {
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
        if (baseEffect != null) {
          boolean isFadeIn = ((AudioEffect) baseEffect).getAudioInfo().getAudioFadeIn().duration > 0;
          boolean isFadeOut = ((AudioEffect) baseEffect).getAudioInfo().getAudioFadeOut().duration > 0;
          list.add(
              new EffectBarItem(EffectBarItem.ACTION_AUDIO_FADE_IN,
                  isFadeIn ? R.drawable.editor_icon_music_inside_slc : R.drawable.editor_icon_music_inside_n,
                  getContext().getString(
                      isFadeIn ? R.string.mn_edit_music_fade_in_turn_on : R.string.mn_edit_music_fade_in_turn_off),
                  isOpEnabled));
          list.add(
              new EffectBarItem(EffectBarItem.ACTION_AUDIO_FADE_OUT,
                  isFadeOut ? R.drawable.editor_icon_music_outside_slc : R.drawable.editor_icon_music_outside_n,
                  getContext().getString(
                      isFadeOut ? R.string.mn_edit_music_fade_out_turn_on : R.string.mn_edit_music_fade_out_turn_off),
                  isOpEnabled));
        }
      } else {
        list.add(
            new EffectBarItem(EffectBarItem.ACTION_AUDIO_FADE_IN, R.drawable.editor_icon_music_inside_n,
                getContext().getString(R.string.mn_edit_music_fade_in_turn_off), isOpEnabled));
        list.add(
            new EffectBarItem(EffectBarItem.ACTION_AUDIO_FADE_OUT, R.drawable.editor_icon_music_outside_n,
                getContext().getString(R.string.mn_edit_music_fade_out_turn_off), isOpEnabled));
      }
    }
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_BGM_REPEAT, R.drawable.editor_tool_effect_sound_icon,
              getContext().getString(R.string.mn_edit_title_bgm_repeat), isOpEnabled));
      if (mWorkSpace.getStoryboardAPI().getThemeId() != 0) {
        list.add(
            new EffectBarItem(EffectBarItem.ACTION_BGM_RESET_TO_THEME, R.drawable.editor_icon_collage_tool_chroma_reset,
                getContext().getString(R.string.mn_edit_reset_text), isOpEnabled));
      }
    }
    list.add(new EffectBarItem(EffectBarItem.ACTION_DEL, R.drawable.edit_icon_delete_nor,
        getContext().getString(R.string.mn_edit_title_delete), isOpEnabled));
    return list;
  }
}
