package com.quvideo.application.editor.effect;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.operate.EffectOPCustomKeyOffset;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.PosDraw;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.Ve3DDataF;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.FloatEffect;
import com.quvideo.mobile.engine.model.SubtitleEffect;
import com.quvideo.mobile.engine.model.effect.EffectKeyFrameInfo;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPLock;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPPosInfo;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;
import org.jetbrains.annotations.NotNull;

public class EffectRotateAxleDialog extends BaseEffectMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private Button btnZAxle;
  private Button btnXAxle;
  private Button btnYAxle;
  private Button btnAnchor;

  // 0-Z轴 1-X轴 2-Y轴 3-anchor
  private int curAxle = 0;

  private PosDraw mPosDraw = new PosDraw();

  private int currentTime = 0;
  private int startTime = 0;
  private int maxTime = 0;

  private boolean isHadKeyFrame = false;

  private EffectPosInfo oldEffectPosInfo = null;
  private EffectKeyFrameInfo oldEffectKeyFrameInfo = null;

  public EffectRotateAxleDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectAxle;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_rotate_axle;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    btnZAxle = view.findViewById(R.id.btn_axle_z);
    btnXAxle = view.findViewById(R.id.btn_axle_x);
    btnYAxle = view.findViewById(R.id.btn_axle_y);
    btnAnchor = view.findViewById(R.id.btn_axle_anchor);
    btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_selected);

    btnZAxle.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        curAxle = 0;
        btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_selected);
        btnXAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnYAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnAnchor.setBackgroundResource(R.drawable.edit_item_bg_normal);
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo effectPosInfo;
        if (isHadKeyFrame) {
          effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, currentTime - startTime);
          try {
            oldEffectPosInfo = effectPosInfo.clone();
            oldEffectKeyFrameInfo = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.clone();
          } catch (Throwable ignore) {
          }
        } else {
          effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        }
        mPosDraw.setNormalFake(1, effectPosInfo.degree.z);
        mFakeApi.setTarget(mPosDraw, effectPosInfo);
        focusStartTime(currentTime);
      }
    });
    btnXAxle.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        curAxle = 1;
        btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnXAxle.setBackgroundResource(R.drawable.edit_item_bg_selected);
        btnYAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnAnchor.setBackgroundResource(R.drawable.edit_item_bg_normal);
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo effectPosInfo;
        if (isHadKeyFrame) {
          effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, currentTime - startTime);
          try {
            oldEffectPosInfo = effectPosInfo.clone();
            oldEffectKeyFrameInfo = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.clone();
          } catch (Throwable ignore) {
          }
        } else {
          effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        }
        mPosDraw.setNormalFake(2, effectPosInfo.degree.z);
        mFakeApi.setTarget(mPosDraw, effectPosInfo, effectPosInfo.degree.x);
        focusStartTime(currentTime);
      }
    });
    btnYAxle.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        curAxle = 2;
        btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnXAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnYAxle.setBackgroundResource(R.drawable.edit_item_bg_selected);
        btnAnchor.setBackgroundResource(R.drawable.edit_item_bg_normal);
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo effectPosInfo;
        if (isHadKeyFrame) {
          effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, currentTime - startTime);
          try {
            oldEffectPosInfo = effectPosInfo.clone();
            oldEffectKeyFrameInfo = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.clone();
          } catch (Throwable ignore) {
          }
        } else {
          effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        }
        mPosDraw.setNormalFake(2, effectPosInfo.degree.z);
        mFakeApi.setTarget(mPosDraw, effectPosInfo, effectPosInfo.degree.y);
        focusStartTime(currentTime);
      }
    });
    btnAnchor.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        if (baseEffect instanceof SubtitleEffect) {
          if (((SubtitleEffect) baseEffect).getTextBubbleInfo().isDftTemplate) {
            ToastUtils.show(getContext(), R.string.mn_edit_tips_no_support, Toast.LENGTH_SHORT);
            return;
          }
        }
        curAxle = 3;
        btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnXAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnYAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnAnchor.setBackgroundResource(R.drawable.edit_item_bg_selected);
        EffectPosInfo effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        mPosDraw.setNormalFake(3, effectPosInfo.degree.z);
        PointF oldCenter = new PointF(effectPosInfo.center.x, effectPosInfo.center.y);
        mFakeApi.setOldAnchor(oldCenter);
        mFakeApi.setTarget(mPosDraw, effectPosInfo);
        focusStartTime(currentTime);
      }
    });
    initFakeView();
  }

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      currentTime = progress;
      if (playerStatus == PlayerStatus.STATUS_PAUSE
          || playerStatus == PlayerStatus.STATUS_STOP
          || playerStatus == PlayerStatus.STATUS_SEEKING) {
        updateFakeFocus(progress);
      } else if (playerStatus == PlayerStatus.STATUS_PLAYING) {
        if (mFakeApi != null) {
          mFakeApi.setTarget(null, null);
        }
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
      mFakeApi.setTarget(null, null);
    } else {
      if (isHadKeyFrame) {
        EffectPosInfo effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, curTime - startTime);
        try {
          oldEffectPosInfo = effectPosInfo.clone();
          BaseEffect curEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
          if (curEffect instanceof AnimEffect) {
            oldEffectKeyFrameInfo = ((AnimEffect) curEffect).mEffectKeyFrameInfo.clone();
          }
        } catch (Throwable ignore) {
        }
        mFakeApi.setTarget(mPosDraw, effectPosInfo);
      }
    }
  }

  private void focusStartTime(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime);
    }
  }

  private void initFakeView() {
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (baseEffect instanceof AnimEffect) {
      isHadKeyFrame = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.positionList.size() > 0
          || ((AnimEffect) baseEffect).mEffectKeyFrameInfo.rotationList.size() > 0
          || ((AnimEffect) baseEffect).mEffectKeyFrameInfo.scaleList.size() > 0;
    } else {
      isHadKeyFrame = false;
    }
    if (isHadKeyFrame) {
      btnAnchor.setVisibility(INVISIBLE);
    }
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    startTime = baseEffect.destRange.getPosition();
    if (baseEffect.destRange.getTimeLength() > 0) {
      maxTime = baseEffect.destRange.getLimitValue();
    } else {
      maxTime = mWorkSpace.getStoryboardAPI().getDuration();
    }
    currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
    EffectPosInfo effectPosInfo;
    if (isHadKeyFrame) {
      effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, currentTime - startTime);
      try {
        oldEffectPosInfo = effectPosInfo.clone();
        oldEffectKeyFrameInfo = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.clone();
      } catch (Throwable ignore) {
      }
    } else {
      effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
    }
    mPosDraw.setNormalFake(1, 0);
    mFakeApi.setTarget(mPosDraw, effectPosInfo);
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving(float pointX, float pointY) {
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo targetPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        EffectPosInfo backupPosInfo = null;
        try {
          backupPosInfo = targetPosInfo.clone();
        } catch (Throwable ignore) {
        }
        FakePosUtils.INSTANCE.updateEffectPosByFakePos(curFakePos, targetPosInfo, curAxle, mFakeApi.getOldAnchor());
        if (isHadKeyFrame) {
          updateKeyFrameOffset(targetPosInfo, backupPosInfo, true);
        } else {
          EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, targetPosInfo);
          effectOPPosInfo.setFastRefresh(true);
          mWorkSpace.handleOperation(effectOPPosInfo);
        }
      }

      @Override public void onEffectMoveStart() {
        EffectOPLock effectOPLock = new EffectOPLock(groupId, effectIndex, true);
        mWorkSpace.handleOperation(effectOPLock);
        if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
          EffectOPStaticPic
              effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, true);
          mWorkSpace.handleOperation(effectOPStaticPic);
        }
      }

      @Override public void onEffectMoveEnd(boolean moved) {
        EffectOPLock effectOPLock = new EffectOPLock(groupId, effectIndex, false);
        mWorkSpace.handleOperation(effectOPLock);
        if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
          EffectOPStaticPic
              effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, false);
          mWorkSpace.handleOperation(effectOPStaticPic);
        }
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo targetPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        EffectPosInfo backupPosInfo = null;
        try {
          backupPosInfo = targetPosInfo.clone();
        } catch (Throwable ignore) {
        }
        FakePosUtils.INSTANCE.updateEffectPosByFakePos(curFakePos, targetPosInfo, curAxle, mFakeApi.getOldAnchor());
        if (isHadKeyFrame) {
          updateKeyFrameOffset(targetPosInfo, backupPosInfo, false);
        } else {
          // 放大倍数是相对effectposinfo的，所以size不可以变化
          EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, targetPosInfo);
          effectOPPosInfo.setFastRefresh(false);
          mWorkSpace.handleOperation(effectOPPosInfo);
        }
      }

      @Override public void checkEffectTouchHit(@NotNull PointF pointF) {
      }
    });
    updateFakeFocus(currentTime);
  }

  /**
   * 更新关键帧的属性
   */
  private void updateKeyFrameOffset(EffectPosInfo targetPosInfo, EffectPosInfo backupPosInfo, boolean fastRefresh) {
    if (oldEffectPosInfo != null && oldEffectKeyFrameInfo != null) {
      Ve3DDataF curPosOffset = null;
      if (oldEffectKeyFrameInfo.positionList.size() > 0) {
        curPosOffset = new Ve3DDataF(targetPosInfo.center.x - oldEffectPosInfo.center.x,
            targetPosInfo.center.y - oldEffectPosInfo.center.y,
            targetPosInfo.center.z - oldEffectPosInfo.center.z);
        curPosOffset.x += oldEffectKeyFrameInfo.positionList.get(0).baseOffset.x;
        curPosOffset.y += oldEffectKeyFrameInfo.positionList.get(0).baseOffset.y;
        curPosOffset.z += oldEffectKeyFrameInfo.positionList.get(0).baseOffset.z;
      }
      Ve3DDataF curRotateOffset = null;
      if (oldEffectKeyFrameInfo.rotationList.size() > 0) {
        curRotateOffset = new Ve3DDataF(targetPosInfo.degree.x - oldEffectPosInfo.degree.x,
            targetPosInfo.degree.y - oldEffectPosInfo.degree.y,
            targetPosInfo.degree.z - oldEffectPosInfo.degree.z);
        curRotateOffset.x += oldEffectKeyFrameInfo.rotationList.get(0).baseOffset.x;
        curRotateOffset.y += oldEffectKeyFrameInfo.rotationList.get(0).baseOffset.y;
        curRotateOffset.z += oldEffectKeyFrameInfo.rotationList.get(0).baseOffset.z;
      }
      Ve3DDataF curAnchorOffset = null;
      if (oldEffectKeyFrameInfo.anchorOffsetList.size() > 0) {
        curAnchorOffset = new Ve3DDataF(targetPosInfo.anchorOffset.x - oldEffectPosInfo.anchorOffset.x,
            targetPosInfo.anchorOffset.y - oldEffectPosInfo.anchorOffset.y,
            targetPosInfo.anchorOffset.z - oldEffectPosInfo.anchorOffset.z);
        curAnchorOffset.x += oldEffectKeyFrameInfo.anchorOffsetList.get(0).baseOffset.x;
        curAnchorOffset.y += oldEffectKeyFrameInfo.anchorOffsetList.get(0).baseOffset.y;
        curAnchorOffset.z += oldEffectKeyFrameInfo.anchorOffsetList.get(0).baseOffset.z;
      }
      Ve3DDataF curScaleOffset = null;
      if (oldEffectKeyFrameInfo.scaleList.size() > 0) {
        curScaleOffset = new Ve3DDataF(targetPosInfo.size.x / oldEffectPosInfo.size.x,
            targetPosInfo.size.y / oldEffectPosInfo.size.y,
            1.0f);
        curScaleOffset.x *= oldEffectKeyFrameInfo.scaleList.get(0).baseOffset.x;
        curScaleOffset.y *= oldEffectKeyFrameInfo.scaleList.get(0).baseOffset.y;
        curScaleOffset.z = 1.0f;
      }
      if (backupPosInfo != null && oldEffectKeyFrameInfo.scaleList.size() > 1) {
        // 放大倍数是相对effectposinfo的，所以size不可以变化
        targetPosInfo.size = new Ve3DDataF(backupPosInfo.size);
      }
      EffectOPCustomKeyOffset effectOPCustomKeyOffset = new EffectOPCustomKeyOffset(groupId, effectIndex,
          curPosOffset, curRotateOffset, curScaleOffset, curAnchorOffset,
          fastRefresh, targetPosInfo);
      mWorkSpace.handleOperation(effectOPCustomKeyOffset);
    }
  }

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
    }
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_rotate);
  }
}
