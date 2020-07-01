package com.quvideo.application.editor.effect.chroma;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.application.editor.effect.fake.FakePosInfo;
import com.quvideo.application.editor.effect.fake.FakePosUtils;
import com.quvideo.application.editor.effect.fake.IFakeViewApi;
import com.quvideo.application.editor.effect.fake.IFakeViewListener;
import com.quvideo.application.editor.effect.fake.draw.ChromaDraw;
import com.quvideo.mobile.engine.entity.ChromaColor;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.effect.EffectChromaInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPChroma;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPCreateChromaColor;
import org.jetbrains.annotations.NotNull;

public class EffectChromaDialog extends BaseEffectMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private EditSeekBarController seekBarController;
  private View seekView;
  private TextView btnPick;
  private TextView btnReset;
  private boolean isPicking = false;
  private ChromaColor mChromaColor = null;

  public EffectChromaDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    seekBarController = new EditSeekBarController();
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
    seekView = view.findViewById(R.id.seekbar);
    btnPick = view.findViewById(R.id.btn_chroma_pick);
    btnReset = view.findViewById(R.id.btn_chroma_reset);

    seekBarController.bindView(view.findViewById(R.id.seekbar));
    seekBarController.setSeekBarTextColor(Color.parseColor("#80FFFFFF"));
    seekBarController.setSeekBarStartText("0");
    seekBarController.setSeekBarEndText("5000");
    seekBarController.setMaxProgress(5000);

    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarController.setProgressText(progress + "");
        AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        if (baseEffect.mEffectChromaInfo != null) {
          EffectChromaInfo effectChromaInfo = baseEffect.mEffectChromaInfo;
          effectChromaInfo.accuracy = progress;
          effectChromaInfo.enable = true;
          EffectOPChroma effectOPChroma = new EffectOPChroma(groupId, effectIndex, effectChromaInfo);
          mWorkSpace.handleOperation(effectOPChroma);
        }
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
    btnReset.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        EffectOPChroma effectOPChroma = new EffectOPChroma(groupId, effectIndex, null);
        mWorkSpace.handleOperation(effectOPChroma);
        mFakeApi.setTarget(null, null, null);
        seekView.setVisibility(View.INVISIBLE);
        isPicking = false;
      }
    });

    btnPick.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (!isPicking) {
          isPicking = true;
          AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
          mFakeApi.setChromaTarget(new ChromaDraw(), baseEffect.mEffectPosInfo);
          if (baseEffect.mEffectChromaInfo != null && baseEffect.mEffectChromaInfo.enable) {
            seekBarController.setProgressText("" + baseEffect.mEffectChromaInfo.accuracy);
            seekBarController.setSeekBarProgress(baseEffect.mEffectChromaInfo.accuracy);
            seekView.setVisibility(View.VISIBLE);
          } else {
            seekView.setVisibility(View.INVISIBLE);
          }
        }
      }
    });
    AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (baseEffect.mEffectChromaInfo != null) {
      seekBarController.setProgressText("" + baseEffect.mEffectChromaInfo.accuracy);
      seekBarController.setSeekBarProgress(baseEffect.mEffectChromaInfo.accuracy);
    }
    initFakeView();
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

  private void initFakeView() {
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    mFakeApi.setTarget(null, null, null);
    seekView.setVisibility(View.INVISIBLE);
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
            seekBarController.setProgressText("" + effectChromaInfo.accuracy);
            seekBarController.setSeekBarProgress(effectChromaInfo.accuracy);
            seekView.setVisibility(View.VISIBLE);
          }
        } else {
          effectChromaInfo = new EffectChromaInfo();
          seekView.setVisibility(View.VISIBLE);
          seekBarController.setProgressText("" + effectChromaInfo.accuracy);
          seekBarController.setSeekBarProgress(effectChromaInfo.accuracy);
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
