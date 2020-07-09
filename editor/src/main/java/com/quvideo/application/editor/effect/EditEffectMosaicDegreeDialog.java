package com.quvideo.application.editor.effect;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.SeekBar;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.MosaicEffect;
import com.quvideo.mobile.engine.model.effect.MosaicInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMosaicInfo;

public class EditEffectMosaicDegreeDialog extends BaseMenuView {

  private EditSeekBarController seekBarController;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectMosaicDegreeDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    seekBarController = new EditSeekBarController();
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectTone;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_volume;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    seekBarController.bindView(view.findViewById(R.id.seekbar));
    seekBarController.setSeekBarTextColor(Color.parseColor("#80FFFFFF"));

    initData();
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    int degree = 0;
    if (baseEffect instanceof MosaicEffect) {
      degree = (int) ((MosaicEffect) baseEffect).getMosaicInfo().horValue;
    }
    seekBarController.setSeekBarStartText("0");
    seekBarController.setSeekBarEndText("100");
    seekBarController.setMaxProgress(100);
    seekBarController.setProgressText(degree + "");
    seekBarController.setSeekBarProgress(degree);

    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarController.setProgressText(progress + "");
        MosaicInfo mosaicInfo = new MosaicInfo(progress, progress);
        EffectOPMosaicInfo effectOpTone = new EffectOPMosaicInfo(effectIndex, mosaicInfo);
        mWorkSpace.handleOperation(effectOpTone);
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_mosaic_degree);
  }
}
