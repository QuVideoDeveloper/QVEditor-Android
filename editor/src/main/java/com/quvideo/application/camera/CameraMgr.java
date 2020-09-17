package com.quvideo.application.camera;

import android.hardware.Camera;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.quvideo.application.camera.recorder.IRecorderListener;
import com.quvideo.application.camera.recorder.RecorderClipInfo;
import com.quvideo.application.camera.recorder.RecorderMgr;
import com.quvideo.application.camera.recorder.RecorderMusicMgr;
import com.quvideo.mobile.engine.camera.XYCameraConst;
import com.quvideo.mobile.engine.camera.XYCameraEngine;
import com.quvideo.mobile.engine.camera.XYRecorderParam;
import com.quvideo.mobile.engine.entity.VeMSize;
import java.util.List;
import xiaoying.utils.WorkThreadTaskItem;

class CameraMgr implements LifecycleObserver {

  private AppCompatActivity mActivity;

  private boolean mIsFlashOn = false;
  private RecorderMgr mRecorderMgr;
  private RecorderMusicMgr mRecorderMusicMgr;
  private IRecorderListener mRecorderListener;

  private XYCameraEngine mXYCamera;
  private FrameLayout mSurfaceContainer;

  CameraMgr(AppCompatActivity activity, FrameLayout surfaceContainer) {
    mActivity = activity;
    activity.getLifecycle().addObserver(this);

    DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
    VeMSize screenSize = new VeMSize();
    screenSize.height = dm.heightPixels;
    screenSize.width = dm.widthPixels;
    mXYCamera = new XYCameraEngine(activity, screenSize, new CameraEventCallback());
    mSurfaceContainer = surfaceContainer;

    mRecorderMgr = new RecorderMgr();
  }

  void setRecorderListener(IRecorderListener listener) {
    mRecorderListener = listener;
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
  public void onCreate() {
    mXYCamera.initPreview(mSurfaceContainer);
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  public void onResume() {
    if (!CamPermissionMgr.hasPermissionsGranted(mActivity)) {
      CamPermissionMgr.requestVideoPermissions(mActivity);
      return;
    }

    mXYCamera.openCamera();
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  public void onPause() {
    if (mRecorderMgr.getRecorderState() == RecorderMgr.RECORDER_STATE_RECORDING) {
      stopRecording();
    }

    mXYCamera.closeCamera();
  }

  private class CameraEventCallback extends CamEventCallbackImpl {

    @Override public void onConnectResult(boolean isConnected) {
      super.onConnectResult(isConnected);

      if (isConnected) {
        mXYCamera.setDeviceIsPortrait(true, XYCameraConst.CameraDegrees.DEGREES_PORTRAIT);
        // 启动预览
        mXYCamera.startPreview();
      }
    }

    @Override public void onDisConnect() {
      super.onDisConnect();

      mXYCamera.stopPreview();
    }

    @Override public void onPreviewStart() {
      super.onPreviewStart();

      // 设置当前已设置的效果
      if (mRecorderMgr.getCamFBValue() >= 0) {
        setFbModeOn(mRecorderMgr.getCamFBValue());
      }
      if (!TextUtils.isEmpty(mRecorderMgr.getApplyEffectPath())) {
        setEffect(mRecorderMgr.getApplyEffectPath());
      }
    }

    @Override public void onPreviewStop() {
      super.onPreviewStop();
    }

    @Override public void onCaptureDone(String filePath) {
      super.onCaptureDone(filePath);
      Toast.makeText(mActivity, "onCaptureDone = " + filePath, Toast.LENGTH_SHORT).show();
    }

    @Override public void onRecorderRunning(long duration) {
      super.onRecorderRunning(duration);
      if (mRecorderListener != null) {
        String filePath = mRecorderMgr.getCurFilePath();
        mRecorderListener.onRecording(filePath, duration);
      }
    }

    @Override public void onRecorderPaused() {
      super.onRecorderPaused();
      if (mRecorderListener != null) {
        mRecorderListener.onRecorderPaused();
      }
    }

    @Override public void onRecorderStop(WorkThreadTaskItem workThreadTaskItem) {
      super.onRecorderStop(workThreadTaskItem);
      if (mRecorderListener != null) {
        mRecorderListener.onRecorderStopped();
      }
    }
  }

  void switchCamera() {
    int recordState = mRecorderMgr.getRecorderState();
    if (recordState != RecorderMgr.RECORDER_STATE_IDLE) {
      stopRecording();
    }

    if (mXYCamera.getCurCameraId() == XYCameraConst.CameraId.CAMERA_BACK) {
      mXYCamera.switchCameraId(XYCameraConst.CameraId.CAMERA_FRONT);
    } else if (mXYCamera.getCurCameraId() == XYCameraConst.CameraId.CAMERA_FRONT) {
      mXYCamera.switchCameraId(XYCameraConst.CameraId.CAMERA_BACK);
    }
  }

  /**
   * @return if flash on/off
   */
  boolean switchFlash() {
    if (mActivity == null || mXYCamera.getCurCameraId() != XYCameraConst.CameraId.CAMERA_BACK) {
      return mIsFlashOn;
    }

    if (mIsFlashOn) {
      mXYCamera.getCameraDevice().setFlashMode(XYCameraConst.FlashMode.FLASH_OFF);
    } else {
      mXYCamera.getCameraDevice().setFlashMode(XYCameraConst.FlashMode.FLASH_TORCH);
    }
    mIsFlashOn = !mIsFlashOn;

    return mIsFlashOn;
  }

  void autoFocus() {
    if (mActivity == null || mXYCamera.getCurCameraId() != XYCameraConst.CameraId.CAMERA_BACK) {
      return;
    }

    mXYCamera.getCameraDevice().autoFocus((success, camera) -> {
    });
  }

  void setCamZoom(int zoomValue) {
    mXYCamera.getCameraDevice().setCameraZoom(zoomValue);
  }

  int getCamZoomMax() {
    return mXYCamera.getCameraDevice().getCameraZoomMax();
  }

  int getCamZoom() {
    return mXYCamera.getCameraDevice().getCameraZoom();
  }

  int getCamExposureMax() {
    return mXYCamera.getCameraDevice().getCameraExposureMax();
  }

  int getCamExposureMin() {
    return mXYCamera.getCameraDevice().getCameraExposureMin();
  }

  int getCamExposure() {
    return mXYCamera.getCameraDevice().getCameraExposure();
  }

  float getCamExposureStep() {
    return mXYCamera.getCameraDevice().getCameraExposureStep();
  }

  void setCamExposure(int value) {
    mXYCamera.getCameraDevice().setCameraExposure(value);
  }

  XYCameraConst.RatioMode switchCamRatio() {
    XYCameraConst.RatioMode curRatioMode = mXYCamera.getRatioMode();
    XYCameraConst.RatioMode nextRatioMode;
    if (curRatioMode == XYCameraConst.RatioMode.RATIO_1_1) {
      nextRatioMode = XYCameraConst.RatioMode.RATIO_4_3;
    } else if (curRatioMode == XYCameraConst.RatioMode.RATIO_4_3) {
      nextRatioMode = XYCameraConst.RatioMode.RATIO_16_9;
    } else {
      nextRatioMode = XYCameraConst.RatioMode.RATIO_1_1;
    }
    mXYCamera.setRatio(nextRatioMode, 200);
    return nextRatioMode;
  }

  void takePicture(@NonNull final String filePath) {
    mXYCamera.getCameraDevice().autoFocus(new Camera.AutoFocusCallback() {
      @Override public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
          mXYCamera.takePicture(filePath);
        }
      }
    });
  }

  void setEffect(String effectPath) {
    mXYCamera.setEffect(effectPath);
    mRecorderMgr.onEffectApplied(effectPath);
  }

  int setFbModeOn(int value) {
    int defaultValue = mRecorderMgr.getCamFBValue();
    if (defaultValue < 0) {
      defaultValue = value;
    }
    mXYCamera.initFaceBeautyMode(defaultValue);
    mRecorderMgr.setCamFBValue(defaultValue);
    return defaultValue;
  }

  void setFbModeOff() {
    mXYCamera.clearFaceBeautyParam();
    mRecorderMgr.setCamFBValue(-1);
  }

  void setFbModeParam(int value) {
    mXYCamera.setFaceBeautyParam(value);
    mRecorderMgr.setCamFBValue(value);
  }

  boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (mActivity == null) {
      return false;
    }

    boolean isAllowCamera =
        CamPermissionMgr.checkCamPermissionValid(mActivity, requestCode, permissions,
            grantResults);
    if (isAllowCamera) {
      mXYCamera.openCamera();
      return true;
    }

    return false;
  }

  void setMusicModeOn(String musicPath, RecorderMusicMgr.IRecorderMusicListener listener) {
    if (mRecorderMusicMgr == null) {
      mRecorderMusicMgr = new RecorderMusicMgr(listener);
    }

    mRecorderMusicMgr.setMusicSource(musicPath, 5000, 10000);
  }

  void setMusicModeOff() {
    if (mRecorderMusicMgr != null) {
      mRecorderMusicMgr.release();
      mRecorderMusicMgr = null;
    }
  }

  private boolean isMusicMode() {
    return mRecorderMusicMgr != null && mRecorderMusicMgr.isMusicAdded();
  }

  void stopRecording() {
    int recordState = mRecorderMgr.getRecorderState();
    if (recordState == RecorderMgr.RECORDER_STATE_RECORDING) {
      int[] range = mXYCamera.stopRecording();
      mRecorderMgr.stopRecorder(range);

      if (isMusicMode()) {
        mRecorderMusicMgr.stopMusic();
      }
    } else {
      mXYCamera.stopRecording();
      mRecorderMgr.stopRecorder(null);
    }
  }

  void reduceMusic(int offsetTime) {
    if (isMusicMode()) {
      mRecorderMusicMgr.reduceMusic(offsetTime);
    }
  }

  void handleRecordAction(@NonNull String filePath) {
    int recordState = mRecorderMgr.getRecorderState();
    if (recordState == RecorderMgr.RECORDER_STATE_IDLE) {
      mXYCamera.startRecording(new XYRecorderParam(filePath, mXYCamera.getOutPutSize(),
          mXYCamera.getCurCameraId() == XYCameraConst.CameraId.CAMERA_FRONT));
      mRecorderMgr.startRecorder(filePath);

      if (isMusicMode()) {
        mRecorderMusicMgr.playMusic();
      }
    } else if (recordState == RecorderMgr.RECORDER_STATE_RECORDING) {
      int[] range = mXYCamera.pauseRecording();
      mRecorderMgr.pauseRecorder(range);

      if (isMusicMode()) {
        mRecorderMusicMgr.pauseMusic();
      }
    } else if (recordState == RecorderMgr.RECORDER_STATE_PAUSE) {
      mXYCamera.resumeRecording();
      mRecorderMgr.resumeRecorder();

      if (isMusicMode()) {
        mRecorderMusicMgr.resumeMusic();
      }
    }
  }

  int getRecorderClipCount() {
    return mRecorderMgr.getClipList().size();
  }

  List<RecorderClipInfo> getRecorderClipList() {
    return mRecorderMgr.getClipList();
  }
}
