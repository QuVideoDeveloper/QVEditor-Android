package com.quvideo.application.export;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.bumptech.glide.Glide;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DeviceSizeUtil;
import com.quvideo.mobile.engine.entity.VideoInfo;
import com.quvideo.mobile.engine.utils.MediaFileUtils;

public class PreviewActivity extends AppCompatActivity {

  private static final String INTENT_KEY_COVER_PATH = "intent_key_cover_path";

  private static final String INTENT_KEY_VIDEO_PATH = "intent_key_video_path";

  private View mTitleView;
  private ImageView ivBack;
  private TextView tvTitle;
  private Button btnBackHome;

  private TextView tvVideoPath;

  private View mExportContainerView;
  /** 封面 */
  private ImageView ivCover;
  /** 用于播放视频 */
  private TextureView textureView;
  /** 播放按钮 */
  private ImageView ivPlay;

  private MediaPlayer mMediaPlayer;
  private Surface mSurface;

  private String coverPath;
  private String videoPath;

  public static final int REQUEST_CODE_PREVIEW = 0x1001;

  /**
   * 进入预览页
   */
  public static void go2PreviewActivity(Activity context, String coverPath, String videoPath) {
    Intent intent = new Intent(context, PreviewActivity.class);
    intent.putExtra(INTENT_KEY_COVER_PATH, coverPath);
    intent.putExtra(INTENT_KEY_VIDEO_PATH, videoPath);
    context.startActivityForResult(intent, REQUEST_CODE_PREVIEW);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video_preview);
    coverPath = getIntent().getStringExtra(INTENT_KEY_COVER_PATH);
    videoPath = getIntent().getStringExtra(INTENT_KEY_VIDEO_PATH);
    initView();
    initListener();
  }

  private void initView() {
    ivBack = findViewById(R.id.btn_back);
    mTitleView = findViewById(R.id.title_layout);
    tvTitle = findViewById(R.id.title);
    btnBackHome = findViewById(R.id.btn_back_home);

    tvVideoPath = findViewById(R.id.video_path);

    mExportContainerView = findViewById(R.id.export_container_view);
    ivCover = findViewById(R.id.iv_cover);
    textureView = findViewById(R.id.export_textureview);
    ivPlay = findViewById(R.id.iv_play);
    tvVideoPath.setText(videoPath);

    VideoInfo videoInfo = MediaFileUtils.getVideoInfo(videoPath);
    resetViewsParams(videoInfo);
    if (TextUtils.isEmpty(coverPath)) {
      ivCover.setVisibility(View.GONE);
    } else {
      ivCover.setVisibility(View.VISIBLE);
      Glide.with(ivCover).load(coverPath).into(ivCover);
    }
    loadVideo(videoPath);
  }

  private void initListener() {
    getLifecycle().addObserver(activityLifecycleObserver);
    ivBack.setOnClickListener(v -> {
      handleBack();
    });
    btnBackHome.setOnClickListener(v -> {
      //startActivity(new Intent(this, MainActivity.class));
      setResult(Activity.RESULT_OK);
      finish();
    });
    textureView.setOnClickListener(v -> {
      if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
        ivPlay.setVisibility(View.VISIBLE);
      }
    });
    ivPlay.setOnClickListener(v -> {
      if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
        mMediaPlayer.start();
        ivCover.setVisibility(View.GONE);
        ivPlay.setVisibility(View.GONE);
      }
    });
    textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
      @Override
      public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        if (mMediaPlayer != null) {
          mMediaPlayer.setSurface(mSurface);
        }
      }

      @Override
      public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
      }

      @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
          mMediaPlayer.pause();
          ivCover.setVisibility(View.VISIBLE);
          ivPlay.setVisibility(View.VISIBLE);
        }
        return true;
      }

      @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
      }
    });
  }

  /**
   * 重置封面之类的大小
   * 限制在两个矩形区域中
   *
   * |----------------|
   * |    |      |    |
   * |    |      |    |
   * |    |------|    |
   * |                |
   * |----------------|
   */
  private void resetViewsParams(VideoInfo videoInfo) {
    float scale = 1f;
    int maxW = DeviceSizeUtil.getScreenWidth();
    int maxH = DeviceSizeUtil.getScreenHeight() * 3 / 4;
    int minWH = DeviceSizeUtil.getScreenWidth() * 3 / 4;

    Rect rectVisiable = new Rect();
    mExportContainerView.getGlobalVisibleRect(rectVisiable);
    int containerH = rectVisiable.bottom - rectVisiable.top;
    if (containerH != 0 && maxH > containerH) {
      maxH = containerH;
    }

    int paramW = minWH;
    int paramH = paramW;
    if (videoInfo.frameWidth > 0) {
      paramH = paramW * videoInfo.frameHeight / videoInfo.frameWidth;
    }
    if (paramH > maxH) {
      paramH = maxH;
      paramW = paramH;
      if (videoInfo.frameHeight > 0) {
        paramW = paramH * videoInfo.frameWidth / videoInfo.frameHeight;
      }
    } else if (paramH < minWH) {
      paramH = minWH;
      paramW = paramH;
      if (videoInfo.frameHeight > 0) {
        paramW = paramH * videoInfo.frameWidth / videoInfo.frameHeight;
      }
      if (paramW > maxW) {
        paramW = maxW;
        paramH = paramW;
        if (videoInfo.frameWidth > 0) {
          paramH = paramW * videoInfo.frameHeight / videoInfo.frameWidth;
        }
      }
    }
    ViewGroup.LayoutParams coverParam = ivCover.getLayoutParams();
    coverParam.width = paramW;
    coverParam.height = paramH;
    ivCover.setLayoutParams(coverParam);
    if (textureView != null) {
      ViewGroup.LayoutParams textureParam = textureView.getLayoutParams();
      textureParam.width = paramW;
      textureParam.height = paramH;
      textureView.setLayoutParams(textureParam);
    }
  }

  @Override public void onPause() {
    super.onPause();
    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
      mMediaPlayer.pause();
      ivCover.setVisibility(View.VISIBLE);
      ivPlay.setVisibility(View.VISIBLE);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    handleRelease();
  }

  private void handleRelease() {
    if (mMediaPlayer != null) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.stop();
      }
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
    if (textureView != null) {
      textureView = null;
    }
  }

  public void handleBack() {
    setResult(Activity.RESULT_CANCELED);
    finish();
  }

  private void loadVideo(String path) {
    try {
      mMediaPlayer = new MediaPlayer();
      mMediaPlayer.setDataSource(path);
      mMediaPlayer.setSurface(mSurface);
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mMediaPlayer.setOnPreparedListener(mp -> {
        ivPlay.setVisibility(View.VISIBLE);
      });
      mMediaPlayer.prepare();
      mMediaPlayer.setOnCompletionListener(mp -> {
        mMediaPlayer.seekTo(0);
        ivPlay.setVisibility(View.VISIBLE);
      });
    } catch (Exception ignore) {
      if (mMediaPlayer != null) {
        mMediaPlayer.release();
      }
      mMediaPlayer = null;
    }
  }

  //******************************************************

  private LifecycleObserver activityLifecycleObserver = new LifecycleObserver() {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME) public void onActivityResume() {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      ;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) public void onActivityPause() {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  };
}
