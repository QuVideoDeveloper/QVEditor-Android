package com.quvideo.application.editor.control;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.quvideo.application.editor.R;

public class EditSeekBarController {

  private TextView tvTitle;
  private SeekBar seekBar;
  private TextView tvSeekStart;
  private TextView tvSeekEnd;
  private TextView tvProgress;

  public void bindView(View rootView) {
    tvTitle = rootView.findViewById(R.id.seek_title);
    seekBar = rootView.findViewById(R.id.seekBar);
    tvSeekStart = rootView.findViewById(R.id.seek_start);
    tvSeekEnd = rootView.findViewById(R.id.seek_end);
    tvProgress = rootView.findViewById(R.id.tv_progress);
  }

  public void setTitle(String title) {
    tvTitle.setText(title);
  }

  public void setSeekBarStartText(String text) {
    tvSeekStart.setText(text);
  }

  public void setSeekBarEndText(String text) {
    tvSeekEnd.setText(text);
  }

  public int getSeekBarProgress() {
    return seekBar.getProgress();
  }

  public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
    seekBar.setOnSeekBarChangeListener(listener);
  }

  public void setSeekBarProgress(int progress) {
    seekBar.setProgress(progress);
  }

  public void setMaxProgress(int maxProgress) {
    seekBar.setMax(maxProgress);
  }

  public void setProgressText(String text) {
    tvProgress.setText(text);
  }

  public void setSeekBarTextColor(int color) {
    tvSeekStart.setTextColor(color);
    tvSeekEnd.setTextColor(color);
  }
}
