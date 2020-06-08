package com.quvideo.application.camera.recorder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import xiaoying.utils.LogUtils;

/**
 * 处理录制时播放音乐
 */
public class RecorderMusicMgr {

  private static final String TAG = RecorderMusicMgr.class.getSimpleName();

  private static final int MSG_CHECK_PLAY_RANGE = 0x1001;

  private MediaPlayer mediaPlayer;
  private int[] playRange = new int[2];

  private IRecorderMusicListener recorderMusicListener;
  private CustomHandler handler;

  public interface IRecorderMusicListener {
    void onMusicPlayComplete();
  }

  private class CustomHandler extends Handler {

    @Override public void handleMessage(@NonNull Message msg) {
      switch (msg.what) {
        case MSG_CHECK_PLAY_RANGE:
          if (!mediaPlayer.isPlaying()) {
            return;
          }

          int curPlayPos = mediaPlayer.getCurrentPosition();
          LogUtils.d(TAG, "MSG_CHECK_PLAY_RANGE = " + curPlayPos);
          if (curPlayPos >= playRange[1]) {
            if (recorderMusicListener != null) {
              recorderMusicListener.onMusicPlayComplete();
            }
          } else {
            sendEmptyMessageDelayed(MSG_CHECK_PLAY_RANGE, playRange[1] - curPlayPos);
          }
          break;
        default:
          break;
      }
    }
  }

  public RecorderMusicMgr(IRecorderMusicListener listener) {
    recorderMusicListener = listener;
    handler = new CustomHandler();

    mediaPlayer = new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
  }

  public void setMusicSource(@NonNull String sourcePath, int startPos, int endPos) {
    try {
      playRange[0] = startPos;
      playRange[1] = endPos;
      mediaPlayer.setDataSource(sourcePath);
      mediaPlayer.prepare();

      if (startPos > 0) {
        // seek 到对应位置
        mediaPlayer.seekTo(startPos);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void playMusic() {
    try {
      mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mp) {
          if (recorderMusicListener != null) {
            recorderMusicListener.onMusicPlayComplete();
          }
        }
      });
      mediaPlayer.start();
      checkPlayRange();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void checkPlayRange() {
    if (playRange[1] > 0 && playRange[1] < mediaPlayer.getDuration()) {
      int delayToStop = playRange[1] - mediaPlayer.getCurrentPosition();
      LogUtils.d(TAG, "delayToStop = " + delayToStop);
      handler.sendEmptyMessageDelayed(MSG_CHECK_PLAY_RANGE,
          delayToStop);
    }
  }

  public void pauseMusic() {
    mediaPlayer.pause();
    handler.removeMessages(MSG_CHECK_PLAY_RANGE);
  }

  public void resumeMusic() {
    mediaPlayer.start();
    checkPlayRange();
  }

  public boolean isMusicAdded() {
    return mediaPlayer.getDuration() > 0;
  }

  public void stopMusic() {
    mediaPlayer.stop();
    mediaPlayer.reset();
    handler.removeMessages(MSG_CHECK_PLAY_RANGE);
  }

  public void release() {
    mediaPlayer.release();
  }
}
