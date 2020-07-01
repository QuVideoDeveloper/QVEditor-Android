package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPMagicSound;

public class EditMagicSoundDialog extends BaseMenuView {

  private EditSeekBarController seekBarController;
  private TextView tvProgress;

  private int clipIndex = 0;

  public EditMagicSoundDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;

    seekBarController = new EditSeekBarController();
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipMagicSound;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_speed;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    seekBarController.bindView(view.findViewById(R.id.seekbar));
    tvProgress = view.findViewById(R.id.tv_progress);

    initData();
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    seekBarController.setSeekBarStartText("-50");
    seekBarController.setSeekBarEndText("50");
    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvProgress.setText((progress - 50) + "");
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    seekBarController.setSeekBarProgress((int) clipData.getSoundTone() + 50);
  }

  @Override public void onClick(View v) {
    magicSoundClip();
  }

  private void magicSoundClip() {
    float value = seekBarController.getSeekBarProgress() - 50;
    ClipOPMagicSound clipOPMagicSound = new ClipOPMagicSound(clipIndex, value);
    mWorkSpace.handleOperation(clipOPMagicSound);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_change_voice);
  }
}
