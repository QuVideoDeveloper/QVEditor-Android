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
import com.quvideo.mobile.engine.work.operate.clip.ClipOPSpeed;

public class EditSpeedDialog extends BaseMenuView {

  private EditSeekBarController seekBarController;
  private TextView tvProgress;

  private int clipIndex = 0;

  public EditSpeedDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;

    seekBarController = new EditSeekBarController();
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipSpeed;
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
    seekBarController.setSeekBarStartText("0.25x");
    seekBarController.setSeekBarEndText("4x");
    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvProgress.setText(SpeedFormatUtils.mathClipSpeedText(progress, 100));
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    int speed = exchangeScale2Progress(clipData.getTimeScale());
    seekBarController.setSeekBarProgress(speed);
  }

  private int exchangeScale2Progress(float scale) {
    float speed = 100 / scale / 100;

    int halfMaxProgress = 100 / 2;
    if (speed == 1.0f) {
      return halfMaxProgress;
    }

    if (speed < 1.0f) {
      return (int) ((speed - 0.25f) * halfMaxProgress / 0.75);
    }

    return (int) ((speed - 1.0f) * halfMaxProgress / 3f + halfMaxProgress);
  }

  @Override public void onClick(View v) {
    speedClip();
  }

  private void speedClip() {
    float speedSel = SpeedFormatUtils.mathSpeednValue(seekBarController.getSeekBarProgress(), 100);
    ClipOPSpeed clipOPSpeed = new ClipOPSpeed(clipIndex, 1.0f / speedSel, true);
    mWorkSpace.handleOperation(clipOPSpeed);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_speed);
  }
}
