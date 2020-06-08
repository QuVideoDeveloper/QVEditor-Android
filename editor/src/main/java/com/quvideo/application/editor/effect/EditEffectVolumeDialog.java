package com.quvideo.application.editor.effect;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.SeekBar;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPVolume;

public class EditEffectVolumeDialog extends BaseMenuView {

  private EditSeekBarController seekBarController;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectVolumeDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    seekBarController = new EditSeekBarController();
    showMenu(container, l);
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
    int volume = baseEffect.audioVolume;
    seekBarController.setSeekBarProgress(volume);
    seekBarController.setSeekBarStartText("0");
    seekBarController.setSeekBarEndText("200");
    seekBarController.setMaxProgress(200);
    seekBarController.setProgressText(volume + "");

    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarController.setProgressText(progress + "");
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
  }

  @Override public void onClick(View v) {
    setClipVolume();
  }

  private void setClipVolume() {
    int toVolume = seekBarController.getSeekBarProgress();
    EffectOPVolume effectOPVolume = new EffectOPVolume(groupId, effectIndex, toVolume);
    mWorkSpace.handleOperation(effectOPVolume);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_volume);
  }
}
