package com.quvideo.application.player;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DateUtils;
import com.quvideo.mobile.engine.player.PlayerAPI;
import com.quvideo.mobile.engine.player.QEPlayerListener;

public class PlayerControllerView extends LinearLayout {

  private ImageButton ibPlay;
  private SeekBar sbProgress;
  private TextView tvCurTime;
  private TextView tvDuration;

  private PlayerAPI mPlayerAPI;

  private boolean isPlaying = false;

  public PlayerControllerView(Context context) {
    super(context);
    initView(context);
  }

  public PlayerControllerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public PlayerControllerView(Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(context);
  }

  public PlayerControllerView(Context context, @Nullable AttributeSet attrs,
      int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initView(context);
  }

  private void initView(Context context) {
    LayoutInflater.from(context).inflate(R.layout.layout_player_controller, this, true);
    ibPlay = findViewById(R.id.ib_pc_play);
    sbProgress = findViewById(R.id.seekbar_pc_progress);
    tvCurTime = findViewById(R.id.tv_pc_current_time);
    tvDuration = findViewById(R.id.tv_pc_duration);

    sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      volatile boolean isStartTracking = false;

      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isStartTracking && fromUser) {
          if (mPlayerAPI != null && mPlayerAPI.getPlayerControl() != null) {
            mPlayerAPI.getPlayerControl().seek(progress);
            tvCurTime.setText(DateUtils.getFormatDuration(progress));
          }
        }
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        isStartTracking = true;
        if (mPlayerAPI != null && mPlayerAPI.getPlayerControl() != null) {
          mPlayerAPI.getPlayerControl().pause();
          if (mPlayerAPI.getPlayerControl().isPlaying()) {
            ibPlay.setImageResource(R.drawable.editorx_player_pause);
          } else {
            ibPlay.setImageResource(R.drawable.editorx_player_play);
          }
        }
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        if (isStartTracking) {
          isStartTracking = false;
          if (mPlayerAPI != null && mPlayerAPI.getPlayerControl() != null) {
            int progress = seekBar.getProgress();
            mPlayerAPI.getPlayerControl().seek(progress);
            tvCurTime.setText(DateUtils.getFormatDuration(progress));
          }
        }
      }
    });
    ibPlay.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mPlayerAPI != null && mPlayerAPI.getPlayerControl() != null) {
          mPlayerAPI.getPlayerControl().playOrPause();
          if (mPlayerAPI.getPlayerControl().isPlaying()) {
            ibPlay.setImageResource(R.drawable.editorx_player_pause);
          } else {
            ibPlay.setImageResource(R.drawable.editorx_player_play);
          }
        }
      }
    });
  }

  public void setPlayerAPI(PlayerAPI playerAPI) {
    this.mPlayerAPI = playerAPI;
    mPlayerAPI.registerListener(mPlayerListener);
    refreshPlayerDuration();
  }

  public void seekPlayer(int progress) {
    if (mPlayerAPI != null && mPlayerAPI.getPlayerControl() != null) {
      mPlayerAPI.getPlayerControl().pause();
      mPlayerAPI.getPlayerControl().seek(progress);
      tvCurTime.setText(DateUtils.getFormatDuration(progress));
      sbProgress.setProgress(progress);
    }
  }

  private void refreshPlayerDuration() {
    if (mPlayerAPI != null && mPlayerAPI.getPlayerControl() != null) {
      sbProgress.setMax(mPlayerAPI.getPlayerControl().getPlayerDuration());
      sbProgress.setProgress(mPlayerAPI.getPlayerControl().getCurrentPlayerTime());
      refreshTimeView(mPlayerAPI.getPlayerControl().getCurrentPlayerTime(), mPlayerAPI.getPlayerControl().getPlayerDuration());
    }
  }

  private void refreshTimeView(int curTime, int duration) {
    tvCurTime.setText(DateUtils.getFormatDuration(curTime));
    tvDuration.setText(DateUtils.getFormatDuration(duration));
  }

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      if (playerStatus == PlayerStatus.STATUS_STOP
          || playerStatus == PlayerStatus.STATUS_PAUSE) {
        ibPlay.setImageResource(R.drawable.editorx_player_play);
        isPlaying = false;
        tvCurTime.setText(DateUtils.getFormatDuration(progress));
        sbProgress.setProgress(progress);
      } else if (playerStatus == PlayerStatus.STATUS_PLAYING) {
        if (!isPlaying) {
          isPlaying = true;
          ibPlay.setImageResource(R.drawable.editorx_player_pause);
        }
        tvCurTime.setText(DateUtils.getFormatDuration(progress));
        sbProgress.setProgress(progress);
      } else if (playerStatus == PlayerStatus.STATUS_SEEKING) {
        tvCurTime.setText(DateUtils.getFormatDuration(progress));
        sbProgress.setProgress(progress);
      } else {
        isPlaying = false;
      }
    }

    @Override public void onPlayerRefresh() {
      refreshPlayerDuration();
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };
}
