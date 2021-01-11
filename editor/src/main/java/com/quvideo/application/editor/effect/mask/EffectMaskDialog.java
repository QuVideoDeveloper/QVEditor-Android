package com.quvideo.application.editor.effect.mask;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.keyframe.KeyFrameTimeline;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.MaskLinearDraw;
import com.quvideo.application.editor.fake.draw.MaskMirrorDraw;
import com.quvideo.application.editor.fake.draw.MaskRadialDraw;
import com.quvideo.application.editor.fake.draw.MaskRectDraw;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.effect.EffectMaskInfo;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyAttributeInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyBAttrInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyMaskPosInfo;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMaskInfo;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMaskKeyFrame;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class EffectMaskDialog extends BaseEffectMenuView {

  private int groupId = 0;
  private int effectIndex = 0;

  private ImageView btnKeyFrameAdd;
  private ImageView btnKeyFrameDel;

  private KeyFrameTimeline mKeyFrameTimeline;

  private CustomSeekbarPop mCustomSeekbarPop;

  private EffectMaskAdapter mEffectMaskAdapter;

  private VeMSize streamSize;

  private int currentTime = 0;
  private int startTime = 0;
  private int maxTime = 0;

  public EffectMaskDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    streamSize = workSpace.getStoryboardAPI().getStreamSize();
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectMask;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_mask;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    btnKeyFrameAdd = view.findViewById(R.id.btn_keyframe_add);
    btnKeyFrameDel = view.findViewById(R.id.btn_keyframe_del);
    mKeyFrameTimeline = view.findViewById(R.id.v_keyframe_timeline);
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    btnKeyFrameAdd.setOnClickListener(mOnClickListener);
    btnKeyFrameDel.setOnClickListener(mOnClickListener);

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("10000")
        .progress(0)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 10000))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
            focusStartTime(currentTime);
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
            if (baseEffect.mEffectMaskInfo != null) {
              if (baseEffect.mEffectMaskInfo.isHadKeyFrame()) {
                int focusTs = mKeyFrameTimeline.getFocusTs();
                // 包含关键帧，则添加或更新关键帧
                if (focusTs < 0) {
                  int relTime = currentTime - startTime;
                  if (relTime < 0 || relTime > maxTime - startTime) {
                    return;
                  }
                  // 添加关键帧
                  EffectMaskInfo targetMaskInfo = mWorkSpace.getMaskKeyFrameByTime(groupId, effectIndex, relTime);
                  KeyMaskPosInfo maskPosInfo = new KeyMaskPosInfo(relTime, targetMaskInfo.centerX, targetMaskInfo.centerY,
                      targetMaskInfo.radiusX, targetMaskInfo.radiusY);
                  KeyAttributeInfo rotationKey = new KeyAttributeInfo(relTime, targetMaskInfo.rotation);
                  KeyAttributeInfo softnessKey = new KeyAttributeInfo(relTime, mCustomSeekbarPop.getProgress());
                  KeyBAttrInfo keyBAttrInfo = new KeyBAttrInfo(relTime, false);
                  baseEffect.mEffectMaskInfo.maskKeyFrameInfo.maskPosList.add(maskPosInfo);
                  baseEffect.mEffectMaskInfo.maskKeyFrameInfo.rotationList.add(rotationKey);
                  baseEffect.mEffectMaskInfo.maskKeyFrameInfo.softnessList.add(softnessKey);
                  baseEffect.mEffectMaskInfo.maskKeyFrameInfo.reverseList.add(keyBAttrInfo);
                  EffectOPMaskKeyFrame effectOPMaskKeyFrame = new EffectOPMaskKeyFrame(groupId, effectIndex,
                      baseEffect.mEffectMaskInfo.maskKeyFrameInfo);
                  mWorkSpace.handleOperation(effectOPMaskKeyFrame);
                } else {
                  EffectMaskInfo targetMaskInfo = mWorkSpace.getMaskKeyFrameByTime(groupId, effectIndex, focusTs);
                  // 修改关键帧
                  for (KeyAttributeInfo softnessInfo : baseEffect.mEffectMaskInfo.maskKeyFrameInfo.softnessList) {
                    if (softnessInfo.relativeTime == focusTs) {
                      softnessInfo.attrValue = targetMaskInfo.softness;
                      break;
                    }
                  }
                  EffectOPMaskKeyFrame effectOPMaskKeyFrame = new EffectOPMaskKeyFrame(groupId, effectIndex,
                      baseEffect.mEffectMaskInfo.maskKeyFrameInfo);
                  mWorkSpace.handleOperation(effectOPMaskKeyFrame);
                }
              } else {
                EffectMaskInfo effectMaskInfo = baseEffect.mEffectMaskInfo;
                effectMaskInfo.softness = progress;
                EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, effectMaskInfo);
                mWorkSpace.handleOperation(effectOPMaskInfo);
              }
            }
          }
        }));

    ArrayList<EffectMaskAdapter.MaskItem> maskItems = new ArrayList<>();
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_NONE, R.drawable.editor_icon_collage_mask_none_n,
        R.string.mn_edit_none));
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_LINEAR, R.drawable.editor_icon_collage_mask_linear_n,
        R.string.mn_edit_mask_linear));
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_MIRROR, R.drawable.editor_icon_collage_mask_mirror_n,
        R.string.mn_edit_mask_mirror));
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_RADIAL, R.drawable.editor_icon_collage_mask_radial_n,
        R.string.mn_edit_mask_radial));
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_RECTANGLE, R.drawable.editor_icon_collage_mask_rect_n,
        R.string.mn_edit_mask_rectangle));

    mEffectMaskAdapter = new EffectMaskAdapter(context, this, maskItems);
    clipRecyclerView.setAdapter(mEffectMaskAdapter);

    AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    startTime = baseEffect.destRange.getPosition();
    if (baseEffect.destRange.getTimeLength() > 0) {
      maxTime = baseEffect.destRange.getLimitValue();
    } else {
      maxTime = mWorkSpace.getStoryboardAPI().getDuration();
    }
    currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    if (baseEffect.mEffectMaskInfo != null) {
      mEffectMaskAdapter.setSelectType(baseEffect.mEffectMaskInfo.maskType, baseEffect.mEffectMaskInfo.reverse);
      mCustomSeekbarPop.setProgress(baseEffect.mEffectMaskInfo.softness);
    }
    initFakeView();
    mEffectMaskAdapter.setOnItemClickListener(this::changeMaskType);

    mKeyFrameTimeline.setMaxOffsetTime(maxTime - startTime, new KeyFrameTimeline.OnKeyFrameListener() {
      @Override public void onKeyFrameClick(int focusTs) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime + focusTs);
        currentTime = startTime + focusTs;
        mKeyFrameTimeline.setCurOffsetTime(focusTs);
        updateBtnEnable(focusTs);
        updateMaskInfo(currentTime);
      }

      @Override public void onOtherClick(int offsetTime) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime + offsetTime);
        currentTime = startTime + offsetTime;
        mKeyFrameTimeline.setCurOffsetTime(offsetTime);
        updateBtnEnable(-1);
        updateMaskInfo(currentTime);
      }
    });
    updateKeyFrameTimeline();
  }

  private void updateKeyFrameTimeline() {
    mCustomSeekbarPop.setVisibility(VISIBLE);
    AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (effect.mEffectMaskInfo != null && effect.mEffectMaskInfo.maskKeyFrameInfo != null) {
      mKeyFrameTimeline.setKeyFrameData(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_ff7b2e),
          effect.mEffectMaskInfo.maskKeyFrameInfo.maskPosList);
    } else {
      mKeyFrameTimeline.setKeyFrameData(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_ff7b2e),
          new ArrayList<KeyMaskPosInfo>());
    }
    mKeyFrameTimeline.setCurOffsetTime(currentTime - startTime);
    updateBtnEnable(mKeyFrameTimeline.getFocusTs());
  }

  private void updateBtnEnable(int focusTs) {
    if (currentTime < startTime || currentTime > maxTime) {
      btnKeyFrameAdd.setEnabled(false);
      btnKeyFrameAdd.setAlpha(0.1f);
      if (mEffectMaskAdapter != null) {
        mEffectMaskAdapter.setEnable(false);
      }
    } else {
      AnimEffect animEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      btnKeyFrameAdd.setEnabled(animEffect.mEffectMaskInfo != null && focusTs < 0);
      btnKeyFrameAdd.setAlpha((animEffect.mEffectMaskInfo != null && focusTs < 0) ? 1f : 0.1f);
      if (mEffectMaskAdapter != null) {
        mEffectMaskAdapter.setEnable(true);
      }
    }
    btnKeyFrameDel.setEnabled(focusTs >= 0);
    btnKeyFrameDel.setAlpha((focusTs >= 0) ? 1f : 0.1f);
  }

  private void updateMaskInfo(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      mCustomSeekbarPop.setVisibility(GONE);
      mFakeApi.setTarget(null, null);
    } else {
      AnimEffect animEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      EffectPosInfo effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, currentTime - startTime);
      if (animEffect.mEffectMaskInfo != null && animEffect.mEffectMaskInfo.isHadKeyFrame()) {
        EffectMaskInfo targetMaskInfo = mWorkSpace.getMaskKeyFrameByTime(groupId, effectIndex, currentTime - startTime);
        mCustomSeekbarPop.setProgress(targetMaskInfo.softness);
        // 包含关键帧，则添加或更新关键帧
        changeFakeView(targetMaskInfo.maskType, effectPosInfo, targetMaskInfo);
      } else {
        if (animEffect.mEffectMaskInfo != null) {
          changeFakeView(animEffect.mEffectMaskInfo.maskType, effectPosInfo, animEffect.mEffectMaskInfo);
        } else {
          mCustomSeekbarPop.setVisibility(GONE);
          mFakeApi.setTarget(null, null);
        }
      }
      mCustomSeekbarPop.setVisibility(VISIBLE);
    }
  }

  private OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(btnKeyFrameAdd)) {
        // 新增关键帧
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        int relTime = currentTime - startTime;
        if (relTime < 0 || relTime > maxTime - startTime) {
          return;
        }
        EffectMaskInfo targetMaskInfo = mWorkSpace.getMaskKeyFrameByTime(groupId, effectIndex, relTime);
        if (targetMaskInfo == null) {
          return;
        }
        KeyMaskPosInfo maskPosInfo = new KeyMaskPosInfo(relTime, targetMaskInfo.centerX, targetMaskInfo.centerY,
            targetMaskInfo.radiusX, targetMaskInfo.radiusY);
        KeyAttributeInfo rotationKey = new KeyAttributeInfo(relTime, targetMaskInfo.rotation);
        KeyAttributeInfo softnessKey = new KeyAttributeInfo(relTime, mCustomSeekbarPop.getProgress());
        KeyBAttrInfo keyBAttrInfo = new KeyBAttrInfo(relTime, false);
        effect.mEffectMaskInfo.maskKeyFrameInfo.maskPosList.add(maskPosInfo);
        effect.mEffectMaskInfo.maskKeyFrameInfo.rotationList.add(rotationKey);
        effect.mEffectMaskInfo.maskKeyFrameInfo.softnessList.add(softnessKey);
        effect.mEffectMaskInfo.maskKeyFrameInfo.reverseList.add(keyBAttrInfo);
        EffectOPMaskKeyFrame effectOPMaskKeyFrame = new EffectOPMaskKeyFrame(groupId, effectIndex,
            effect.mEffectMaskInfo.maskKeyFrameInfo);
        mWorkSpace.handleOperation(effectOPMaskKeyFrame);
      } else if (v.equals(btnKeyFrameDel)) {
        // 删除关键帧
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        int focusTs = mKeyFrameTimeline.getFocusTs();
        if (focusTs >= 0) {
          // 修改关键帧
          for (KeyMaskPosInfo maskPosInfo : effect.mEffectMaskInfo.maskKeyFrameInfo.maskPosList) {
            if (maskPosInfo.relativeTime == focusTs) {
              effect.mEffectMaskInfo.maskKeyFrameInfo.maskPosList.remove(maskPosInfo);
              break;
            }
          }
          for (KeyAttributeInfo rotationKey : effect.mEffectMaskInfo.maskKeyFrameInfo.rotationList) {
            if (rotationKey.relativeTime == focusTs) {
              effect.mEffectMaskInfo.maskKeyFrameInfo.rotationList.remove(rotationKey);
              break;
            }
          }
          for (KeyAttributeInfo softnessInfo : effect.mEffectMaskInfo.maskKeyFrameInfo.softnessList) {
            if (softnessInfo.relativeTime == focusTs) {
              effect.mEffectMaskInfo.maskKeyFrameInfo.softnessList.remove(softnessInfo);
              break;
            }
          }
          for (KeyBAttrInfo reverseKey : effect.mEffectMaskInfo.maskKeyFrameInfo.reverseList) {
            if (reverseKey.relativeTime == focusTs) {
              effect.mEffectMaskInfo.maskKeyFrameInfo.reverseList.remove(reverseKey);
              break;
            }
          }
          EffectOPMaskKeyFrame effectOPMaskKeyFrame = new EffectOPMaskKeyFrame(groupId, effectIndex,
              effect.mEffectMaskInfo.maskKeyFrameInfo);
          mWorkSpace.handleOperation(effectOPMaskKeyFrame);
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
        mKeyFrameTimeline.setCurOffsetTime(progress - startTime);
        updateBtnEnable(mKeyFrameTimeline.getFocusTs());
        updateMaskInfo(currentTime);
      }
    }

    @Override public void onPlayerRefresh() {
      if (mWorkSpace != null
          && mWorkSpace.getPlayerAPI() != null
          && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
        currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
        mKeyFrameTimeline.setCurOffsetTime(currentTime - startTime);
        updateBtnEnable(mKeyFrameTimeline.getFocusTs());
        updateMaskInfo(currentTime);
      }
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPMaskInfo
          || operate instanceof EffectOPMaskKeyFrame) {
        // 刷新数据
        updateKeyFrameTimeline();
      }
    }
  };

  private void focusStartTime(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime);
    }
  }

  private void initFakeView() {
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    mWorkSpace.addObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving(float pointX, float pointY) {
        updateFakeMove();
      }

      @Override public void onEffectMoveStart() {
      }

      @Override public void onEffectMoveEnd(boolean moved) {
        updateFakeMove();
      }

      @Override public void checkEffectTouchHit(@NotNull PointF pointF) {
      }
    });
    updateMaskInfo(currentTime);
  }

  /**
   * 处理fake移动操作
   */
  private void updateFakeMove() {
    FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
    AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (baseEffect.mEffectMaskInfo != null && baseEffect.mEffectMaskInfo.isHadKeyFrame()) {
      int focusTs = mKeyFrameTimeline.getFocusTs();
      // 包含关键帧，则添加或更新关键帧
      if (focusTs < 0) {
        int relTime = currentTime - startTime;
        if (relTime < 0 || relTime > maxTime - startTime) {
          return;
        }
        // 添加关键帧
        EffectMaskInfo targetMaskInfo = mWorkSpace.getMaskKeyFrameByTime(groupId, effectIndex, relTime);
        fill2EffectMaskInfo(groupId, curFakePos,
            mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, relTime), targetMaskInfo);
        KeyMaskPosInfo maskPosInfo = new KeyMaskPosInfo(relTime, targetMaskInfo.centerX, targetMaskInfo.centerY,
            targetMaskInfo.radiusX, targetMaskInfo.radiusY);
        KeyAttributeInfo rotationKey = new KeyAttributeInfo(relTime, targetMaskInfo.rotation);
        KeyAttributeInfo softnessKey = new KeyAttributeInfo(relTime, mCustomSeekbarPop.getProgress());
        KeyBAttrInfo keyBAttrInfo = new KeyBAttrInfo(relTime, false);
        baseEffect.mEffectMaskInfo.maskKeyFrameInfo.maskPosList.add(maskPosInfo);
        baseEffect.mEffectMaskInfo.maskKeyFrameInfo.rotationList.add(rotationKey);
        baseEffect.mEffectMaskInfo.maskKeyFrameInfo.softnessList.add(softnessKey);
        baseEffect.mEffectMaskInfo.maskKeyFrameInfo.reverseList.add(keyBAttrInfo);
        EffectOPMaskKeyFrame effectOPMaskKeyFrame = new EffectOPMaskKeyFrame(groupId, effectIndex,
            baseEffect.mEffectMaskInfo.maskKeyFrameInfo);
        mWorkSpace.handleOperation(effectOPMaskKeyFrame);
      } else {
        EffectMaskInfo targetMaskInfo = mWorkSpace.getMaskKeyFrameByTime(groupId, effectIndex, focusTs);
        fill2EffectMaskInfo(groupId, curFakePos,
            mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, focusTs), targetMaskInfo);
        // 修改关键帧
        for (KeyMaskPosInfo maskPosInfo : baseEffect.mEffectMaskInfo.maskKeyFrameInfo.maskPosList) {
          if (maskPosInfo.relativeTime == focusTs) {
            maskPosInfo.centerX = targetMaskInfo.centerX;
            maskPosInfo.centerY = targetMaskInfo.centerY;
            maskPosInfo.radiusX = targetMaskInfo.radiusX;
            maskPosInfo.radiusY = targetMaskInfo.radiusY;
            break;
          }
        }
        for (KeyAttributeInfo rotationInfo : baseEffect.mEffectMaskInfo.maskKeyFrameInfo.rotationList) {
          if (rotationInfo.relativeTime == focusTs) {
            rotationInfo.attrValue = targetMaskInfo.rotation;
            break;
          }
        }
        EffectOPMaskKeyFrame effectOPMaskKeyFrame = new EffectOPMaskKeyFrame(groupId, effectIndex,
            baseEffect.mEffectMaskInfo.maskKeyFrameInfo);
        mWorkSpace.handleOperation(effectOPMaskKeyFrame);
      }
    } else {
      EffectMaskInfo targetMaskInfo = baseEffect.mEffectMaskInfo;
      int relTime = currentTime - startTime;
      if (relTime < 0 || relTime > maxTime - startTime) {
        return;
      }
      fill2EffectMaskInfo(groupId, curFakePos,
          mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, relTime), targetMaskInfo);
      EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, targetMaskInfo);
      mWorkSpace.handleOperation(effectOPMaskInfo);
    }
  }

  /**
   * 更新填充位置信息
   */
  private void fill2EffectMaskInfo(int groupId, FakePosInfo fakePosInfo,
      EffectPosInfo effectPosInfo, EffectMaskInfo effectMaskInfo) {
    FakeMaskPosData fakeMaskPosData = FakePosUtils.INSTANCE.updateMaskPos2FakePos(fakePosInfo);
    MaskPosUtil.fill2EffectMaskInfo(groupId, streamSize, fakeMaskPosData, effectPosInfo, effectMaskInfo);
  }

  private void changeFakeView(EffectMaskInfo.MaskType maskType, EffectPosInfo effectPosInfo, EffectMaskInfo effectMaskInfo) {
    FakeMaskPosData fakeMaskPosData = MaskPosUtil.conver2FakeMaskData(groupId, streamSize, effectMaskInfo, effectPosInfo);
    if (maskType == EffectMaskInfo.MaskType.MASK_LINEAR) {
      mFakeApi.setTarget(new MaskLinearDraw(), effectPosInfo, fakeMaskPosData);
      mCustomSeekbarPop.setVisibility(View.VISIBLE);
      mCustomSeekbarPop.setProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_MIRROR) {
      mFakeApi.setTarget(new MaskMirrorDraw(), effectPosInfo, fakeMaskPosData);
      mCustomSeekbarPop.setVisibility(View.VISIBLE);
      mCustomSeekbarPop.setProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_RADIAL) {
      mFakeApi.setTarget(new MaskRadialDraw(), effectPosInfo, fakeMaskPosData);
      mCustomSeekbarPop.setVisibility(View.VISIBLE);
      mCustomSeekbarPop.setProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_RECTANGLE) {
      mFakeApi.setTarget(new MaskRectDraw(), effectPosInfo, fakeMaskPosData);
      mCustomSeekbarPop.setVisibility(View.VISIBLE);
      mCustomSeekbarPop.setProgress(effectMaskInfo.softness);
    } else {
      mFakeApi.setTarget(null, null);
      mCustomSeekbarPop.setVisibility(View.INVISIBLE);
    }
  }

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.removeObserver(mBaseObserver);
      mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
    }
  }

  private void changeMaskType(EffectMaskAdapter.MaskItem maskItem) {
    EffectMaskInfo effectMaskInfo = null;
    if (maskItem.maskType != EffectMaskInfo.MaskType.MASK_NONE) {
      AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      EffectPosInfo effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, currentTime - startTime);
      if (baseEffect.mEffectMaskInfo != null) {
        effectMaskInfo = baseEffect.mEffectMaskInfo;
      } else {
        effectMaskInfo = new EffectMaskInfo();
        if (groupId != QEGroupConst.GROUP_ID_SUBTITLE) {
          effectMaskInfo.centerX = 5000;
          effectMaskInfo.centerY = 5000;
          effectMaskInfo.radiusX = 5000;
          effectMaskInfo.radiusY = 5000;
        } else {
          effectMaskInfo.centerX = effectPosInfo.center.x / streamSize.width * 10000f;
          effectMaskInfo.centerY = effectPosInfo.center.y / streamSize.height * 10000f;
          effectMaskInfo.radiusX = effectPosInfo.size.x / streamSize.width * 10000f / 2f;
          effectMaskInfo.radiusY = effectPosInfo.size.y / streamSize.height * 10000f / 2f;
        }
        effectMaskInfo.rotation = 0;
      }
      effectMaskInfo.reverse = maskItem.reverse;
      effectMaskInfo.maskType = maskItem.maskType;
      changeFakeView(maskItem.maskType, effectPosInfo, effectMaskInfo);
      focusStartTime(currentTime);
    } else {
      mFakeApi.setTarget(null, null);
      mCustomSeekbarPop.setVisibility(View.INVISIBLE);
    }
    EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, effectMaskInfo);
    mWorkSpace.handleOperation(effectOPMaskInfo);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_mask);
  }
}
