package com.quvideo.application.camera;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.quvideo.application.FileCopyHelper;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.camera.recorder.IRecorderListener;
import com.quvideo.application.camera.recorder.RecorderClipInfo;
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.editor.R;
import com.quvideo.mobile.engine.camera.XYCameraConst;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import xiaoying.utils.LogUtils;

public class CameraActivity extends AppCompatActivity {

  public static final String INTENT_EXT_KEY_CAMERA = "CameraLaunchParam";

  private CameraMgr cameraMgr;
  private CamControlViewMgr cameraControlViewMgr;
  private CamParamSeekViewMgr cameraParamViewMgr;
  private CamFilterControlViewMgr camFilterControlViewMgr;
  private GestureDetector gestureDetector;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    runTestCode();

    setContentView(R.layout.activity_camera);

    FrameLayout surfaceContainer = findViewById(R.id.surfaceContainer);
    cameraMgr = new CameraMgr(this, surfaceContainer);
    cameraMgr.setRecorderListener(new IRecorderListener() {
      @Override public void onRecording(@NotNull String filePath, long duration) {
        List<RecorderClipInfo> clipList = cameraMgr.getRecorderClipList();
        long realDuration = duration;
        for (RecorderClipInfo clipInfo : clipList) {
          if (TextUtils.equals(clipInfo.getFilePath(), filePath)) {
            // 当前正在录制的文件
            break;
          }

          // 计算之前录制的文件时长
          realDuration += (clipInfo.getRecorderPos()[1] - clipInfo.getRecorderPos()[0]);
        }
        cameraControlViewMgr.onRecording(TimeFormatUtil.INSTANCE.formatTime(realDuration));
        cameraControlViewMgr.setBtnRatioValid(false);
        cameraParamViewMgr.hideView();
      }

      @Override public void onRecorderPaused() {
        cameraControlViewMgr.onRecordStop(cameraMgr.getRecorderClipCount());
      }

      @Override public void onRecorderStopped() {
        cameraControlViewMgr.onRecordStop(cameraMgr.getRecorderClipCount());
        cameraControlViewMgr.setBtnRatioValid(cameraMgr.getRecorderClipCount() == 0);
      }
    });

    cameraControlViewMgr = new CamControlViewMgr();
    cameraControlViewMgr.bindView(this, onControlListener);

    cameraParamViewMgr = new CamParamSeekViewMgr();
    cameraParamViewMgr.bindView(this, onParamChangedListener);

    camFilterControlViewMgr = new CamFilterControlViewMgr();
    camFilterControlViewMgr.bindView(this, new CamFilterControlViewMgr.OnFilterSelectListener() {
      @Override public void onFilterSelected(String filePath) {
        cameraMgr.setEffect(filePath);
      }
    });

    gestureDetector = new GestureDetector(this, new CustomGestureDetector());
  }

  private void runTestCode() {
    try {
      // 默认开启硬件编码, 建议放到setting中
      String testMusicPath = getExternalCacheDir().getAbsolutePath() + "/test.mp3";
      boolean isTestFileExist = new File(testMusicPath).exists();
      if (!isTestFileExist) {
        FileCopyHelper.copyFilesFromRaw(this, R.raw.unravel, "test.mp3",
            getExternalCacheDir().getAbsolutePath());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    return gestureDetector.onTouchEvent(event);
  }

  @Override public void onBackPressed() {
    if (cameraParamViewMgr.isViewShown()) {
      cameraParamViewMgr.hideView();
      cameraControlViewMgr.showView();
      return;
    } else if (camFilterControlViewMgr.isViewShown()) {
      hideFilterControlIfShown();
      return;
    }

    super.onBackPressed();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (cameraMgr.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
      return;
    }

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

    @Override public boolean onSingleTapUp(MotionEvent e) {
      cameraMgr.autoFocus();
      return true;
    }
  }

  private CamControlViewMgr.OnControlListener onControlListener =
      new CamControlViewMgr.OnControlListener() {
        @Override public void onFrontBtnClick() {
          cameraMgr.switchCamera();
        }

        @Override public boolean onFlashBtnClick() {
          return cameraMgr.switchFlash();
        }

        @Override public void onZoomBtnClick() {
          hideFilterControlIfShown();
          if (cameraParamViewMgr.getCurMode() == CamParamSeekViewMgr.ParamMode.MODE_ZOOM) {
            cameraParamViewMgr.hideView();
          } else {
            cameraParamViewMgr.showView(CamParamSeekViewMgr.ParamMode.MODE_ZOOM,
                cameraMgr.getCamZoom(),
                cameraMgr.getCamZoomMax());
          }
        }

        @Override public void onExposureBtnClick() {
          hideFilterControlIfShown();
          if (cameraParamViewMgr.getCurMode() == CamParamSeekViewMgr.ParamMode.MODE_EXPOSURE) {
            cameraParamViewMgr.hideView();
          } else {
            int defaultValue = cameraMgr.getCamExposure();
            int offsetProgress = cameraMgr.getCamExposureMin();
            cameraParamViewMgr.showView(CamParamSeekViewMgr.ParamMode.MODE_EXPOSURE,
                defaultValue - offsetProgress,
                cameraMgr.getCamExposureMax() - offsetProgress);
          }
        }

        @Override public XYCameraConst.RatioMode onRatioBtnClick() {
          return cameraMgr.switchCamRatio();
        }

        @Override public void onCaptureBtnClick() {
          String filePath = getExternalCacheDir().getAbsolutePath() + "/test.jpg";
          LogUtils.d("Camera", "take picture = " + filePath);
          cameraMgr.takePicture(filePath);
        }

        @Override public void onFilterBtnClick() {
          if (camFilterControlViewMgr.isViewShown()) {
            hideFilterControlIfShown();
          } else {
            cameraParamViewMgr.hideView();
            cameraControlViewMgr.hideRecordBtn();
            camFilterControlViewMgr.showView();
          }
        }

        @Override public void onFbModeBtnClick() {
          hideFilterControlIfShown();
          if (cameraParamViewMgr.getCurMode() == CamParamSeekViewMgr.ParamMode.MODE_FACE_BEAUTY) {
            cameraParamViewMgr.hideView();
          } else {
            int defaultValue = cameraMgr.setFbModeOn(50);
            cameraParamViewMgr.showView(CamParamSeekViewMgr.ParamMode.MODE_FACE_BEAUTY,
                defaultValue, 100);
          }
        }

        @Override public void onRecordBtnClick() {
          String filePath =
              getExternalCacheDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4";
          cameraMgr.handleRecordAction(filePath);
        }

        @Override public void onRecorderDoneBtnClick() {
          cameraMgr.stopRecording();

          List<RecorderClipInfo> clipInfoList = cameraMgr.getRecorderClipList();
          Intent intent = new Intent(CameraActivity.this, EditorActivity.class);
          intent.putExtra(INTENT_EXT_KEY_CAMERA, new Gson().toJson(clipInfoList));
          startActivity(intent);
          finish();
        }

        @Override public void onClipDelBtnClick() {
          List<RecorderClipInfo> clipInfoList = cameraMgr.getRecorderClipList();
          if (clipInfoList.isEmpty()) {
            return;
          }
          cameraMgr.stopRecording();

          RecorderClipInfo delItem = clipInfoList.get(clipInfoList.size() - 1);
          Integer[] range = delItem.getRecorderPos();
          int offsetTime = range[1] - range[0];
          clipInfoList.remove(clipInfoList.size() - 1);
          cameraMgr.reduceMusic(offsetTime);
          int realDuration = 0;
          for (RecorderClipInfo clipInfo : clipInfoList) {
            realDuration += (clipInfo.getRecorderPos()[1] - clipInfo.getRecorderPos()[0]);
          }
          cameraControlViewMgr.setRecordTime(TimeFormatUtil.INSTANCE.formatTime(realDuration));
        }

        @Override public void onBackBtnClick() {
          finish();
        }

        @Override public void onAddMusicBtnClick() {
          String filePath = getExternalCacheDir().getAbsolutePath() + "/test.mp3";
          LogUtils.d("Camera", "music is exist = " + new File(filePath).exists());
          cameraControlViewMgr.setMusicTitle("test");
          cameraMgr.setMusicModeOn(filePath, this::onRecorderDoneBtnClick);
        }
      };

  private void hideFilterControlIfShown() {
    if (camFilterControlViewMgr.isViewShown()) {
      camFilterControlViewMgr.hideView();
      cameraControlViewMgr.showRecordBtn();
    }
  }

  private CamParamSeekViewMgr.OnParamChangedListener onParamChangedListener =
      new CamParamSeekViewMgr.OnParamChangedListener() {
        @Override public void onCamZoomChanged(int value) {
          cameraParamViewMgr.setProgressText(String.valueOf(value));
          cameraMgr.setCamZoom(value);
        }

        @Override public void onCamExposureChanged(int value) {
          int offsetProgress = cameraMgr.getCamExposureMin();
          float realValue = (value + offsetProgress) * cameraMgr.getCamExposureStep();
          DecimalFormat df = new DecimalFormat("#.#");
          cameraParamViewMgr.setProgressText(df.format(realValue));

          cameraMgr.setCamExposure(value + offsetProgress);
        }

        @Override public void onCamFbParamChanged(int value) {
          cameraParamViewMgr.setProgressText(String.valueOf(value));
          cameraMgr.setFbModeParam(value);
        }
      };
}
