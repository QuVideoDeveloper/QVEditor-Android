package com.quvideo.application.editor.effect.chroma;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.ChromaDraw;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.entity.ChromaColor;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.effect.EffectChromaInfo;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPChroma;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPCreateChromaColor;
import org.jetbrains.annotations.NotNull;

public class EffectChromaDialog extends BaseEffectMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private CustomSeekbarPop mCustomSeekbarPop;
  private TextView btnPick;
  private TextView btnReset;
  private boolean isPicking = false;
  private ChromaColor mChromaColor = null;

  private int currentTime = 0;
  private int startTime = 0;
  private int maxTime = 0;

  public EffectChromaDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    mWorkSpace.addObserver(mBaseObserver);
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectChroma;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_chroma;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    btnPick = view.findViewById(R.id.btn_chroma_pick);
    btnReset = view.findViewById(R.id.btn_chroma_reset);

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("5000")
        .progress(1000)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 5000))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
            focusStartTime(currentTime);
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
            if (baseEffect.mEffectChromaInfo != null) {
              EffectChromaInfo effectChromaInfo = baseEffect.mEffectChromaInfo;
              effectChromaInfo.accuracy = progress;
              effectChromaInfo.enable = true;
              EffectOPChroma effectOPChroma = new EffectOPChroma(groupId, effectIndex, effectChromaInfo);
              mWorkSpace.handleOperation(effectOPChroma);
            }
          }
        }));

    btnReset.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        EffectOPChroma effectOPChroma = new EffectOPChroma(groupId, effectIndex, null);
        mWorkSpace.handleOperation(effectOPChroma);
        mFakeApi.setTarget(null, null);
        mCustomSeekbarPop.setVisibility(View.INVISIBLE);
        isPicking = false;
        focusStartTime(currentTime);
      }
    });

    btnPick.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (!isPicking) {
          isPicking = true;
          AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
          mFakeApi.setChromaTarget(new ChromaDraw(), baseEffect.mEffectPosInfo);
          if (baseEffect.mEffectChromaInfo != null && baseEffect.mEffectChromaInfo.enable) {
            mCustomSeekbarPop.setProgress(baseEffect.mEffectChromaInfo.accuracy);
            mCustomSeekbarPop.setVisibility(View.VISIBLE);
          } else {
            mCustomSeekbarPop.setVisibility(View.INVISIBLE);
          }
        }
        focusStartTime(currentTime);
      }
    });
    AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (baseEffect.mEffectChromaInfo != null) {
      mCustomSeekbarPop.setProgress(baseEffect.mEffectChromaInfo.accuracy);
    }
    initFakeView();
    updateFakeFocus(currentTime);
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPCreateChromaColor) {
        if (operate.success()) {
          mChromaColor = ((EffectOPCreateChromaColor) operate).getChromaColor();
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
      mFakeApi.setTarget(null, null);
    }
  }

  private void focusStartTime(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime);
    }
  }

  private void initFakeView() {
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    mFakeApi.setTarget(null, null);
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    startTime = baseEffect.destRange.getPosition();
    if (baseEffect.destRange.getTimeLength() > 0) {
      maxTime = baseEffect.destRange.getLimitValue();
    } else {
      maxTime = mWorkSpace.getStoryboardAPI().getDuration();
    }
    currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
    mCustomSeekbarPop.setVisibility(View.INVISIBLE);
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving() {
        if (mChromaColor == null) {
          return;
        }
        AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectChromaInfo effectChromaInfo = null;
        if (baseEffect.mEffectChromaInfo != null) {
          effectChromaInfo = baseEffect.mEffectChromaInfo;
          if (!effectChromaInfo.enable) {
            mCustomSeekbarPop.setProgress(effectChromaInfo.accuracy);
            mCustomSeekbarPop.setVisibility(View.VISIBLE);
          }
        } else {
          effectChromaInfo = new EffectChromaInfo();
          mCustomSeekbarPop.setVisibility(View.VISIBLE);
          mCustomSeekbarPop.setProgress(effectChromaInfo.accuracy);
        }
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        PointF pointF = FakePosUtils.INSTANCE.getChromaColorPosByFakePos(curFakePos, baseEffect.mEffectPosInfo);
        effectChromaInfo.color = mChromaColor.getColorByPosition(pointF.x, pointF.y);
        mFakeApi.updateChromaColor(effectChromaInfo.color);
        effectChromaInfo.enable = true;
        EffectOPChroma effectOPChroma = new EffectOPChroma(groupId, effectIndex, effectChromaInfo);
        mWorkSpace.handleOperation(effectOPChroma);
      }

      @Override public void onEffectMoveStart() {
      }

      @Override public void onEffectMoveEnd(boolean moved) {
      }

      @Override public void checkEffectTouchHit(@NotNull PointF pointF) {
      }
    });
    EffectOPCreateChromaColor effectOPCreateChromaColor = new EffectOPCreateChromaColor(groupId, effectIndex);
    mWorkSpace.handleOperation(effectOPCreateChromaColor);
  }

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.removeObserver(mBaseObserver);
      mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
    }
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_chroma);
  }
}
