package com.quvideo.application.editor.effect;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.FloatEffect;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPLock;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPPosInfo;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;

public class EditEffectPositionDialog extends BaseMenuView {

  private EditSeekBarController startSeekBarController;
  private EditSeekBarController endSeekBarController;

  private int groupId = 0;
  private int effectIndex = 0;
  private EffectPosInfo effectPosInfo;

  public EditEffectPositionDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    startSeekBarController = new EditSeekBarController();
    endSeekBarController = new EditSeekBarController();
    showMenu(container, l);
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_position;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    startSeekBarController.bindView(view.findViewById(R.id.seekbar_start));
    endSeekBarController.bindView(view.findViewById(R.id.seekbar_end));

    startSeekBarController.setTitle("x");
    endSeekBarController.setTitle("y");

    initData();
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    startSeekBarController.setSeekBarStartText("0");
    endSeekBarController.setSeekBarStartText("0");
    VeMSize streamSize = mWorkSpace.getStoryboardAPI().getStreamSize();
    startSeekBarController.setSeekBarEndText(streamSize.width + "");
    endSeekBarController.setSeekBarEndText(streamSize.height + "");
    startSeekBarController.setMaxProgress(streamSize.width);
    endSeekBarController.setMaxProgress(streamSize.height);
    effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
    startSeekBarController.setSeekBarProgress((int) effectPosInfo.centerPosX);
    endSeekBarController.setSeekBarProgress((int) effectPosInfo.centerPosY);

    startSeekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        effectPosInfo.centerPosX = progress;
        refreshPosInfo();
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        doOnPosStart();
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        doOnPosEnd();
      }
    });

    endSeekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        effectPosInfo.centerPosY = progress;
        refreshPosInfo();
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        doOnPosStart();
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        doOnPosEnd();
      }
    });
  }

  private void refreshPosInfo() {
    EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, effectPosInfo);
    effectOPPosInfo.setFastRefresh(true);
    mWorkSpace.handleOperation(effectOPPosInfo);
  }

  private void doOnPosStart() {
    EffectOPLock effectOPLock = new EffectOPLock(groupId, effectIndex, true);
    mWorkSpace.handleOperation(effectOPLock);
    EffectOPStaticPic
        effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, true);
    mWorkSpace.handleOperation(effectOPStaticPic);
    EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, effectPosInfo);
    effectOPPosInfo.setFastRefresh(true);
    mWorkSpace.handleOperation(effectOPPosInfo);
  }

  private void doOnPosEnd() {
    EffectOPLock effectOPLock = new EffectOPLock(groupId, effectIndex, false);
    mWorkSpace.handleOperation(effectOPLock);
    EffectOPStaticPic
        effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, false);
    mWorkSpace.handleOperation(effectOPStaticPic);
    EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, effectPosInfo);
    effectOPPosInfo.setFastRefresh(false);
    mWorkSpace.handleOperation(effectOPPosInfo);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_effect_position);
  }
}
