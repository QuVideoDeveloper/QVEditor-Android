package com.quvideo.application.gallery.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import com.quvideo.application.gallery.preview.listener.PlayerCallback;
import java.io.IOException;

/**
 * Create by zhengjunfei on 2019/9/17
 */
public class StretchTextureView extends TextureView
    implements TextureView.SurfaceTextureListener {
  private String TEXTUREVIDEO_TAG = StretchTextureView.class.getSimpleName();
  private String mUrl;
  private MediaPlayer mMediaPlayer;
  private int mVideoWidth;//視頻寬度
  private int mVideoHeight;//視頻高度
  public static final int CENTER_CROP_MODE = 1;//中心裁剪模式
  public static final int FIT_XY = 2;//一邊中心填充模式
  public int mVideoMode = 0;
  private int mCurPosition;
  private PlayerCallback mListener;
  private boolean mIsPlayed = false;
  private boolean mIsSeekAndPlay = false;

  public void setPlayCallback(PlayerCallback listener) {
    this.mListener = listener;
  }

  public StretchTextureView(Context context) {
    super(context);
  }

  public StretchTextureView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public StretchTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void init(String url, PlayerCallback listener) {
    this.mListener = listener;
    this.mUrl = url;
    setSurfaceTextureListener(this);
  }

  public void play(int seekMsec) {
    if (mMediaPlayer != null) {
      mIsPlayed = true;
      mIsSeekAndPlay = true;
      seekTo(seekMsec);
    }
  }

  public void pause() {
    if (mMediaPlayer == null) {
      return;
    }
    try {
      mMediaPlayer.pause();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (null != mListener) {
      mListener.onPauseListener();
    }
  }

  /**
   * 8.0及其以的api，seekTo加上mode的话，不会出现seek不准确的问题
   */
  public void seekTo(int msec) {
    if (null == mMediaPlayer || msec < 0) {
      return;
    }
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        mMediaPlayer.seekTo(msec, MediaPlayer.SEEK_CLOSEST);
      } else {
        mMediaPlayer.seekTo(msec);
      }
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public int getCurPosition() {
    mCurPosition = null != mMediaPlayer ? mMediaPlayer.getCurrentPosition() : 0;
    return mCurPosition;
  }

  public boolean isPlaying() {
    return null != mMediaPlayer && mMediaPlayer.isPlaying();
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    if (mMediaPlayer == null) {
      mMediaPlayer = new MediaPlayer();
      try {
        mMediaPlayer.setDataSource(mUrl);
        //拿到要展示的圖形界面
        Surface mediaSurface = new Surface(surface);
        //把surface
        mMediaPlayer.setSurface(mediaSurface);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setScreenOnWhilePlaying(true);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
          @Override public void onPrepared(MediaPlayer mp) {
            //當MediaPlayer對象處於Prepared狀態的時候，可以調整音頻/視頻的屬性，如音量，播放時是否一直亮屏，循環播放等。
            mMediaPlayer.setVolume(1f, 1f);
            if (!mIsPlayed) {//特殊处理，不然首次seek会导致
              mMediaPlayer.start();
              mMediaPlayer.pause();
            }
          }
        });

        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
          @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
                && null != mListener
                && mIsPlayed) {
              mListener.onStartListener();
            }
            return true;
          }
        });

        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
          @Override public void onSeekComplete(MediaPlayer mp) {
            if (mIsSeekAndPlay) {
              mMediaPlayer.start();
              mIsSeekAndPlay = false;
              if (null != mListener) {
                mListener.onStartListener();
              }
            }
          }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
          @Override public boolean onError(MediaPlayer mp, int what, int extra) {
            if (null != mListener) {
              mListener.onErrListener(what, extra);
              mCurPosition = 0;
            }
            return true;
          }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
          @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (null != mListener && null != mp && mIsPlayed) {
              mCurPosition = mp.getDuration() * percent / 100;
              mListener.onProgresslistener(mp.getCurrentPosition() * percent);
            }
            //此方法獲取的是緩衝的狀態，
            // 重点：只有播放网络视频才会回调此方法
          }
        });

        //播放完成的監聽
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
          @Override public void onCompletion(MediaPlayer mp) {
            if (null != mListener) {
              mCurPosition = 100;
              mListener.onCompleteListener();
            }
          }
        });

        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
          @Override public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoHeight = mMediaPlayer.getVideoHeight();
            mVideoWidth = mMediaPlayer.getVideoWidth();
            updateTextureViewSize(mVideoMode);
            if (null != mListener) {
              mListener.onVideoSizeChanged(mVideoWidth, mVideoHeight);
            }
          }
        });
        mMediaPlayer.prepareAsync();
      } catch (IOException e) {
      }
    }
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    updateTextureViewSize(mVideoMode);
  }

  @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    try {
      if (mMediaPlayer != null) {
        mMediaPlayer.pause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
      }
    } catch (Exception ignore) {
    }

    if (null != mListener) {
      mListener.onTextureDestory();
    }
    return false;
  }

  public void release() {
    if (mMediaPlayer != null) {
      try {
        mMediaPlayer.pause();
        mMediaPlayer.stop();
      } catch (Exception ignore) {
      } finally {
        mMediaPlayer.release();
      }
    }
  }

  @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {

  }

  public void setVideoMode(int mode) {
    mVideoMode = mode;
  }

  /**
   * @param mode Pass {@link #CENTER_CROP_MODE} or {@link #FIT_XY}. Default
   * value is 0.
   */
  public void updateTextureViewSize(int mode) {
    if (mode == FIT_XY) {
      updateTextureViewSizeCenter();
    } else if (mode == CENTER_CROP_MODE) {
      updateTextureViewSizeCenterCrop();
    }
  }

  /**
   * 宽大于高
   */
  public boolean isWidthBig() {
    return mVideoWidth > mVideoHeight;
  }

  //重新計算video的顯示位置，裁剪後全屏顯示
  private void updateTextureViewSizeCenterCrop() {

    float sx = (float) getWidth() / (float) mVideoWidth;
    float sy = (float) getHeight() / (float) mVideoHeight;

    Matrix matrix = new Matrix();
    float maxScale = Math.max(sx, sy);

    //第1步:把視頻區移動到View區,使兩者中心點重合.
    matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);

    //第2步:因爲默認視頻是fitXY的形式顯示的,所以首先要縮放還原回來.
    matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());

    //第3步,等比例放大或縮小,直到視頻區的一邊超過View一邊, 另一邊與View的另一邊相等. 因爲超過的部分超出了View的範圍,所以是不會顯示的,相當於裁剪了.
    matrix.postScale(maxScale, maxScale, getWidth() / 2,
        getHeight() / 2);//後兩個參數座標是以整個View的座標系以參考的

    setTransform(matrix);
    postInvalidate();
  }

  //重新計算video的顯示位置，讓其全部顯示並據中
  public void updateTextureViewSizeCenter() {
    int width = getWidth();
    int height = getHeight();
    if (width == 0 || height == 0 || mVideoWidth == 0 || mVideoHeight == 0) {
      return;
    }

    boolean isVertical = false;
    if (null != mListener) {
      isVertical = mListener.isRotateVertical();
    }
    float preSx = mVideoWidth / (float) width;
    float preSy = mVideoHeight / (float) height;

    float postSx =
        isVertical ? (float) height / (float) mVideoWidth : (float) width / (float) mVideoWidth;
    float postSy = isVertical ? (float) width / (float) mVideoHeight
        : (float) height / (float) mVideoHeight;

    Matrix matrix = new Matrix();
    float minPostScale = Math.min(postSx, postSy);

    //第1步:把视频区移动到View区，使两者中心点重合.
    matrix.preTranslate((width - mVideoWidth) / 2, (height - mVideoHeight) / 2);

    //第2步:因为默认视频是fitXY的形式显示的，所以首先要缩放还原回来.
    matrix.preScale(preSx, preSy);

    //第3步:等比例放大或缩小，直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
    matrix.postScale(minPostScale, minPostScale, width / 2, height / 2);

    setTransform(matrix);
    postInvalidate();
  }

  public int getDisplayWidth() {
    if (null == mListener) {
      return 0;
    }
    if (mVideoWidth == 0 || mVideoHeight == 0) {
      return 0;
    }

    boolean isVertical = mListener.isRotateVertical();
    if (isVertical && isWidthBig()) {
      return getWidth() * mVideoHeight / mVideoWidth;
    } else if (isVertical && !isWidthBig()) {
      return getWidth();
    } else if (!isVertical && isWidthBig()) {
      return getWidth();
    } else if (!isVertical && !isWidthBig()) {
      return getHeight() * mVideoWidth / mVideoHeight;
    }
    return 0;
  }

  public int getDisplayHeight() {
    if (null == mListener) {
      return 0;
    }
    if (mVideoWidth == 0 || mVideoHeight == 0) {
      return 0;
    }

    boolean isVertical = mListener.isRotateVertical();
    if (isVertical && isWidthBig()) {
      return getWidth();
    } else if (isVertical && !isWidthBig()) {
      return getWidth() * mVideoWidth / mVideoHeight;
    } else if (!isVertical && isWidthBig()) {
      return getWidth() * mVideoHeight / mVideoWidth;
    } else if (!isVertical && !isWidthBig()) {
      return getHeight();
    }
    return 0;
  }
}
