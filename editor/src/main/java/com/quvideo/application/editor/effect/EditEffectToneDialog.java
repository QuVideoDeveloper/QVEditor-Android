package com.quvideo.application.editor.effect;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.SeekBar;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.AudioEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAudioTone;

public class EditEffectToneDialog extends BaseMenuView {

  private EditSeekBarController seekBarController;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectToneDialog(Context context, MenuContainer container,
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
    int tone = 0;
    if (baseEffect instanceof AudioEffect) {
      tone = (int) ((AudioEffect) baseEffect).getAudioInfo().soundTone;
    }
    seekBarController.setSeekBarStartText("-60");
    seekBarController.setSeekBarEndText("60");
    seekBarController.setMaxProgress(120);
    seekBarController.setProgressText(getToneStr(tone + 60));
    seekBarController.setSeekBarProgress(tone + 60);

    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarController.setProgressText(getToneStr(progress));
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
  }

  private String getToneStr(int progress) {
    return (progress - 60) + "";
  }

  @Override public void onClick(View v) {
    setClipVolume();
  }

  private void setClipVolume() {
    int toVolume = seekBarController.getSeekBarProgress() - 60;
    EffectOPAudioTone effectOpTone = new EffectOPAudioTone(groupId, effectIndex, toVolume);
    mWorkSpace.handleOperation(effectOpTone);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_change_voice);
  }
}
