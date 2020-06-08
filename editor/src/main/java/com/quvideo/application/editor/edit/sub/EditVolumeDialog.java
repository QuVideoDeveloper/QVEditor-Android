package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.SeekBar;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPVolume;

public class EditVolumeDialog extends BaseMenuView {

  private EditSeekBarController seekBarController;

  private int clipIndex = 0;

  public EditVolumeDialog(Context context, MenuContainer container, IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;

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
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    int volume = clipData.getAudioVolume();
    seekBarController.setSeekBarProgress(volume);
    seekBarController.setSeekBarStartText("0");
    seekBarController.setSeekBarEndText("100");
    seekBarController.setProgressText(volume + "");

    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarController.setProgressText(progress + "");
        setClipVolume(progress);
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
  }

  @Override public void onClick(View v) {
    int toVolume = seekBarController.getSeekBarProgress();
    setClipVolume(toVolume);
    dismissMenu();
  }

  private void setClipVolume(int volume) {
    ClipOPVolume clipOPVolume = new ClipOPVolume(clipIndex, volume);
    mWorkSpace.handleOperation(clipOPVolume);
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_volume);
  }
}
