package com.quvideo.application.gallery.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.quvideo.application.gallery.R;
import com.quvideo.application.gallery.model.GSzie;
import com.quvideo.application.gallery.preview.listener.PlayerCallback;
import com.quvideo.application.gallery.utils.GalleryToast;
import com.quvideo.application.gallery.utils.GalleryUtil;
import java.io.File;

public class PlayerView extends FrameLayout implements PlayerCallback {
  private ImageView mCoverView;
  private StretchTextureView mTextureView;
  private int mRotation;
  private String mVideoPath;
  private PlayerCallback mCallback;

  public PlayerView(@NonNull Context context) {
    super(context);
    init();
  }

  public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    View view = LayoutInflater.from(getContext())
        .inflate(R.layout.gallery_media_layout_player, this, true);
    mTextureView = view.findViewById(R.id.textureview);
    mCoverView = view.findViewById(R.id.player_cover);
  }

  public void initPlayer(String videoPath, PlayerCallback callback) {
    File file = new File(videoPath);
    if (!file.exists()) {
      GalleryToast.show(getContext(),
          getContext().getResources().getString(R.string.mn_gallery_vide_trim_path_error));
      return;
    }
    this.mVideoPath = videoPath;
    this.mCallback = callback;
    initData();
    initCover();
  }

  private void initData() {
    mTextureView.setVideoMode(StretchTextureView.FIT_XY);
    mTextureView.setPlayCallback(this);
    mTextureView.init(mVideoPath, this);
  }

  private void initCover() {
    GalleryUtil.loadCoverFitCenter(getContext(), mCoverView,
        R.drawable.gallery_default_pic_cover, mVideoPath);
  }

  public void seek(int msec) {
    if (null != mCoverView && View.VISIBLE == mCoverView.getVisibility()) {
      mCoverView.setVisibility(View.GONE);
    }
    if (null != mTextureView) {
      mTextureView.seekTo(msec);
    }
  }

  public void start(int seekMsec) {
    if (null != mTextureView) {
      mTextureView.play(seekMsec);
    }
  }

  public void pause() {
    if (null != mTextureView) {
      mTextureView.pause();
    }
  }

  public void release() {
    if (null != mTextureView) {
      mTextureView.release();
    }
  }

  public GSzie getDisplaySize() {
    if (mTextureView == null) {
      return null;
    }
    int displayWidth = mTextureView.getDisplayWidth();
    int displayHeight = mTextureView.getDisplayHeight();
    return new GSzie(displayWidth, displayHeight);
  }

  public int getViewRotation() {
    return mRotation;
  }

  public void rotatePlayerView() {
    if (null == mTextureView || null == mCoverView) {
      return;
    }
    mRotation = mRotation + 90;
    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "rotation", mRotation);
    animator.setDuration(200);
    animator.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {
        if (null != mCallback) {
          mCallback.onRotateStart();
        }
      }

      @Override public void onAnimationEnd(Animator animation) {
        if (null != mCallback) {
          mCallback.onRotateEnd();
        }
      }

      @Override public void onAnimationCancel(Animator animation) {

      }

      @Override public void onAnimationRepeat(Animator animation) {

      }
    });
    animator.start();
    if (!mTextureView.isWidthBig()) {
      changeCoverViewSize();
      mTextureView.updateTextureViewSize(StretchTextureView.FIT_XY);
    }
  }

  //封面跟随旋转改变大小
  private void changeCoverViewSize() {
    if (mCoverView == null) {
      return;
    }
    boolean isVertical = isRotateVertical();
    int imgWidth = mCoverView.getWidth();
    int imgHeight = mCoverView.getHeight();

    float scale = (float) imgWidth / imgHeight;

    int resultWidth = isVertical ? (int) (imgWidth * scale) : (int) (imgWidth / scale);
    int resultHeight = isVertical ? imgWidth : (int) (imgHeight / scale);

    LayoutParams layoutParams =
        (LayoutParams) mCoverView.getLayoutParams();
    layoutParams.width = resultWidth;
    layoutParams.height = resultHeight;
    layoutParams.gravity = Gravity.CENTER;
    mCoverView.setLayoutParams(layoutParams);
  }

  public int getCurPosition() {
    return null != mTextureView ? mTextureView.getCurPosition() : 0;
  }

  public boolean isPlaying() {
    return null != mTextureView && mTextureView.isPlaying();
  }

  @Override public void onStartListener() {
    if (null != mCoverView) {
      mCoverView.setVisibility(View.GONE);
    }
    if (null != mCallback) {
      mCallback.onStartListener();
    }
  }

  @Override public void onPauseListener() {
    if (null != mCallback) {
      mCallback.onPauseListener();
    }
  }

  @Override public void onProgresslistener(int time) {
    if (null != mCallback) {
      mCallback.onProgresslistener(time);
    }
  }

  @Override public boolean isRotateVertical() {
    return mRotation % 180 != 0;
  }

  @Override public void onCompleteListener() {
    if (null != mCoverView) {
      mCoverView.setVisibility(View.VISIBLE);
    }
    if (null != mCallback) {
      mCallback.onCompleteListener();
    }
  }

  @Override public void onErrListener(int what, int extra) {
    if (null != mCoverView) {
      mCoverView.setVisibility(View.VISIBLE);
    }
    if (null != mCallback) {
      mCallback.onErrListener(what, extra);
    }
  }

  public int getDisplayWidth() {
    if (null == mTextureView) {
      return 0;
    }
    return mTextureView.getDisplayWidth();
  }

  public int getDisplayHeight() {
    if (null == mTextureView) {
      return 0;
    }
    return mTextureView.getDisplayHeight();
  }
}
