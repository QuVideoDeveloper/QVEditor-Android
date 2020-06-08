package com.quvideo.application.gallery.preview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GRange;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.preview.listener.PlayerCallback;
import com.quvideo.application.gallery.utils.GSizeUtil;
import com.quvideo.application.gallery.utils.GalleryToast;
import com.quvideo.application.gallery.widget.PlayerView;
import com.quvideo.application.gallery.widget.crop.CropImageView;
import com.quvideo.application.gallery.widget.trim.TrimContentPanel;
import com.quvideo.application.utils.pop.Pop;
import com.quvideo.application.utils.rx.RxViewUtil;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.concurrent.TimeUnit;

public class VideoTrimActivity extends AppCompatActivity
    implements View.OnClickListener, PlayerCallback {
  public static final String EXTRAC_MEDIA_MODEL = "extrac_media_model";
  private static final int MIN_CLIP_DURATION = 100;
  private static final int MIN_START_DURATION = 2000;//最短的播放时长

  private CropImageView mCropView;
  private ImageView mBackBtn;
  private PlayerView mPlayerview;
  private ImageView mPlayIcon;
  private ImageButton mRotateBtn, mCropBtn;
  private TextView mConfirmBtn;

  private RelativeLayout mRotateLayout, mCropLayout;

  private MediaModel mExtracMediaModel;
  private TrimContentPanel mTrimContentPanel;
  private ConstraintLayout mLayoutOperation;

  private Disposable mDisposable;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gallery_media_activity_video_trim);

    initView();
    initListener();
    getIntentData();
    initVideoData();
    initTrimContentPanel();
  }

  private void initView() {
    mBackBtn = findViewById(R.id.video_trim_btn_back);
    mPlayerview = findViewById(R.id.video_trim_playerview);
    mLayoutOperation = findViewById(R.id.video_trim_layout_operation);
    mPlayIcon = findViewById(R.id.video_trim_play_icon);
    mConfirmBtn = findViewById(R.id.video_trim_btn_done);

    mCropView = findViewById(R.id.crop_view);
    mRotateLayout = findViewById(R.id.layout_rotate);
    mRotateBtn = findViewById(R.id.btn_rotate);
    mCropLayout = findViewById(R.id.layout_crop);
    mCropBtn = findViewById(R.id.btn_crop);

    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    mCropLayout.setVisibility(View.GONE);
    mRotateLayout.setVisibility(View.GONE);
  }

  private void initListener() {
    mBackBtn.setOnClickListener(this);
    mPlayIcon.setOnClickListener(this);
    mConfirmBtn.setOnClickListener(this);
    RxViewUtil.setOnClickListener(view -> {
      playBtnClick();
    }, mPlayIcon);

    RxViewUtil.setOnClickListener(view -> {
      Pop.showDeepSoftly(mRotateBtn);
      rotatePlayerView();
    }, mRotateLayout);

    RxViewUtil.setOnClickListener(view -> {
      Pop.showDeepSoftly(mCropBtn);
      mCropBtn.setSelected(!mCropBtn.isSelected());
      boolean cropping = mCropBtn.isSelected();
      mCropBtn.setSelected(cropping);
      mCropView.setVisibility(cropping ? View.VISIBLE : View.GONE);
    }, mCropLayout);
  }

  private void initTrimContentPanel() {
    mTrimContentPanel = new TrimContentPanel(mLayoutOperation, 0);
    mTrimContentPanel.setOnTrimListener(trimListener);
    mTrimContentPanel.setVideoPath(mExtracMediaModel);
    //设置最大时长
    mTrimContentPanel.setMinTrimInterval(getMinClipDuration());
    mTrimContentPanel.setNotAvailableWidth(
        GSizeUtil.getFitPxFromDp(getApplicationContext(), 32));
    mTrimContentPanel.loadPanel();
  }

  private int getMinClipDuration() {
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    if (null != settings && 0 != settings.getVideoMinDuration()) {
      return (int) settings.getVideoMinDuration();
    }
    return MIN_CLIP_DURATION;
  }

  private TrimContentPanel.OnTrimListener trimListener = new TrimContentPanel.OnTrimListener() {
    @Override public void onTrimStart(boolean bLeft) {
      if (null != mPlayerview && null != mPlayIcon) {
        mPlayerview.pause();
        mPlayIcon.setSelected(false);
      }
    }

    @Override public void onTrimPosChange(int position) {
      updateTrimTimeView(position);

      if (null != mPlayerview) {
        mPlayerview.seek(position);
      }
    }

    @Override public void onTrimEnd(boolean isLeftTrim, int progress) {
      updateTrimTimeView(progress);
    }
  };

  private void updateTrimTimeView(int curTime) {
    if (mTrimContentPanel != null) {
      mTrimContentPanel.updateCurrentPlayCursor(curTime);
    }
  }

  private void getIntentData() {
    mExtracMediaModel = getIntent().getParcelableExtra(EXTRAC_MEDIA_MODEL);
    if (null != mExtracMediaModel) {//初始化rang
      GRange gRange = new GRange(0, (int) mExtracMediaModel.getDuration());
      mExtracMediaModel.setRangeInFile(gRange);
    }
  }

  private void initVideoData() {
    if (null == mExtracMediaModel) {
      GalleryToast.show(getApplicationContext(), getApplicationContext().getResources()
          .getString(R.string.mn_gallery_vide_trim_path_error));
      return;
    }
    mPlayerview.initPlayer(mExtracMediaModel.getFilePath(), this);
  }

  @Override public void onClick(View v) {
    if (v.equals(mBackBtn)) {
      finish();
    } else if (v.equals(mConfirmBtn)) {
      completeTrim();
    }
  }

  private void completeTrim() {
    if (null != mTrimContentPanel) {
      MediaModel mediaModel = mTrimContentPanel.getMediaModel();
      boolean cropping = mCropView.isShown();
      if (cropping) {
        mediaModel.setCropped(true);
        RectF rect = rotateCropRect(mCropView.getCroppedRect(), mPlayerview.getViewRotation());
        mediaModel.setCropRect(rect);
      }

      Intent intent = getIntent();
      Bundle data = new Bundle();
      data.putParcelable(EXTRAC_MEDIA_MODEL, mediaModel);
      intent.putExtras(data);
      setResult(RESULT_OK, intent);
    }
    finish();
  }

  private void rotatePlayerView() {
    if (null == mPlayerview) {
      return;
    }
    mPlayerview.rotatePlayerView();
    if (null != mTrimContentPanel && null != mTrimContentPanel.getMediaModel()) {
      mTrimContentPanel.getMediaModel().setRotation(mPlayerview.getViewRotation() % 360);
    }
  }

  private void playBtnClick() {
    mPlayIcon.setSelected(!mPlayerview.isPlaying());
    if (null == mPlayerview) {
      return;
    }
    int curPosition = mPlayerview.getCurPosition();
    if (null != mTrimContentPanel
        && null != mTrimContentPanel.getMediaModel()
        && null != mTrimContentPanel.getMediaModel().getRangeInFile()
        && mTrimContentPanel.getMediaModel().getRangeInFile().getLeftValue() > curPosition) {
      curPosition = mTrimContentPanel.getMediaModel().getRangeInFile().getLeftValue();
    }
    if (null != mTrimContentPanel
        && null != mTrimContentPanel.getMediaModel()
        && null != mTrimContentPanel.getMediaModel().getRangeInFile()
        && (curPosition >= mTrimContentPanel.getMediaModel().getRangeInFile().getRightValue()
        || mTrimContentPanel.getMediaModel().getRangeInFile().getRightValue() - curPosition
        < MIN_START_DURATION)) {
      curPosition =
          mTrimContentPanel.getMediaModel().getRangeInFile().getLength() < MIN_START_DURATION
              ? mTrimContentPanel.getMediaModel().getRangeInFile().getLeftValue() :
              mTrimContentPanel.getMediaModel().getRangeInFile().getRightValue()
                  - MIN_START_DURATION;
    }

    if (!mPlayerview.isPlaying()) {
      mPlayerview.start(curPosition);
    } else {
      mPlayerview.pause();
      clearTrimUpdate();
    }
  }

  private RectF rotateCropRect(RectF src, int rotate) {
    int angle = rotate % 360;
    RectF dst = new RectF(src);
    switch (angle) {
      case 90:
        dst.left = src.top;
        dst.top = 10000 - src.right;
        dst.right = src.bottom;
        dst.bottom = 10000 - src.left;
        break;
      case 180:
        dst.left = 10000 - src.right;
        dst.top = 10000 - src.bottom;
        dst.right = 10000 - src.left;
        dst.bottom = 10000 - src.top;
        break;
      case 270:
        dst.left = 10000 - src.bottom;
        dst.top = src.left;
        dst.right = 10000 - src.top;
        dst.bottom = src.right;
        break;
      default:
        break;
    }
    return dst;
  }

  public static void launchVideoTrim(Activity activity, int requestCode, View scaleUpSourceView,
      MediaModel mediaModel) {
    Intent intent = new Intent(activity, VideoTrimActivity.class);
    intent.putExtra(EXTRAC_MEDIA_MODEL, mediaModel);
    try {
      if (scaleUpSourceView != null) {
        ActivityOptionsCompat compat =
            ActivityOptionsCompat.makeScaleUpAnimation(scaleUpSourceView,
                scaleUpSourceView.getWidth() / 2, scaleUpSourceView.getHeight(), 0, 0);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, compat.toBundle());
      } else {
        activity.startActivityForResult(intent, requestCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void startUpdateTrim() {
    if (null == mDisposable && null != mTrimContentPanel && null != mPlayerview) {
      mDisposable = Flowable.interval(0, 100, TimeUnit.MILLISECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Consumer<Long>() {
            @Override public void accept(Long aLong) throws Exception {
              if (null != mTrimContentPanel
                  && null != mTrimContentPanel.getMediaModel()
                  && null != mTrimContentPanel.getMediaModel().getRangeInFile()
                  && null != mPlayerview
                  && mPlayerview.getCurPosition() >= mTrimContentPanel.getMediaModel()
                  .getRangeInFile()
                  .getRightValue()) {
                clearTrimUpdate();
                mTrimContentPanel.setPlaying(false);
                mPlayerview.pause();
                mPlayerview.seek(
                    mTrimContentPanel.getMediaModel().getRangeInFile().getLeftValue());
                return;
              }
              if (!mTrimContentPanel.isPlaying()) {
                mTrimContentPanel.setPlaying(true);
              }

              mTrimContentPanel.setCurPlayPos(mPlayerview.getCurPosition());
            }
          });
    }
  }

  private void clearTrimUpdate() {
    if (null != mDisposable) {
      mDisposable.dispose();
      mDisposable = null;
    }
    if (null != mTrimContentPanel) {
      mTrimContentPanel.setPlaying(false);
    }
    if (null != mTrimContentPanel
        && null != mTrimContentPanel.getMediaModel()
        && null != mTrimContentPanel.getMediaModel().getRangeInFile()) {
      mPlayerview.seek(mTrimContentPanel.getMediaModel().getRangeInFile().getLeftValue());
    }
  }

  @Override protected void onPause() {
    super.onPause();
    clearTrimUpdate();
    if (isFinishing()) {
      if (null != mPlayerview) {
        mPlayerview.release();
      }
    } else {
      if (null != mPlayerview) {
        mPlayerview.pause();
      }
    }
  }

  @Override public void onStartListener() {
    if (null != mPlayIcon) {
      mPlayIcon.setSelected(true);
      startUpdateTrim();
    }
  }

  @Override public void onPauseListener() {
    clearTrimUpdate();
    if (null != mPlayIcon) {
      mPlayIcon.setSelected(false);
    }
  }

  @Override public void onCompleteListener() {
    clearTrimUpdate();
    if (null != mPlayIcon) {
      mPlayIcon.setSelected(false);
    }
  }

  @Override public void onErrListener(int what, int extra) {
    clearTrimUpdate();
    if (null != mPlayIcon) {
      mPlayIcon.setSelected(false);
    }
  }

  @Override public void onRotateStart() {
    mCropView.setVisibility(View.GONE);
  }

  @Override public void onRotateEnd() {
    if (mCropBtn.isSelected()) {
      mCropView.setVisibility(View.VISIBLE);
    }
  }
}
