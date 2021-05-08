package com.quvideo.application.frame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import androidx.annotation.NonNull;
import java.util.Arrays;

/**
 * @author wuzhongyou
 * @date 2021/1/29.
 */
public class Camera2Manager {

  /** 后置摄像头 */
  public static int FACING_BACK = 0;
  /** 前置摄像头 */
  public static int FACING_FRONT = 1;

  private static final SparseIntArray INTERNAL_FACINGS = new SparseIntArray();

  public static final Size mPreviewSize = new Size(1280, 720);

  public Size mFinalPreviewSize;

  static {
    INTERNAL_FACINGS.put(FACING_BACK, CameraCharacteristics.LENS_FACING_BACK);
    INTERNAL_FACINGS.put(FACING_FRONT, CameraCharacteristics.LENS_FACING_FRONT);
  }

  /** 相机系统服务，用于管理和连接相机设备 */
  private CameraManager mCameraManager;
  /** 相机正反 */
  public int mFacing;
  /** 相机id */
  private String mCameraId;
  /** 相机信息 */
  private CameraCharacteristics mCameraCharacteristics;

  /** 相机设备类 */
  private CameraDevice mCamera;
  /** 请求抓取相机图像帧的会话 */
  private CameraCaptureSession mCaptureSession;
  /** CaptureRequest的构造器 */
  private CaptureRequest.Builder mPreviewRequestBuilder;

  private HandlerThread handlerThread;
  private Handler mCameraHandler;

  private SurfaceTexture mSurfaceTexture;

  private Context mContext;

  public Camera2Manager(Context context) {
    this.mContext = context;
    mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    handlerThread = new HandlerThread("camera2");
    handlerThread.start();
    mCameraHandler = new Handler(handlerThread.getLooper());
  }

  public void destory() {
    mCameraManager = null;
    mCameraHandler.removeCallbacksAndMessages(null);
    mCameraHandler = null;
    handlerThread.quitSafely();
    handlerThread = null;
  }

  public void setPreviewTexture(SurfaceTexture surfaceTexture) {
    mSurfaceTexture = surfaceTexture;
  }

  public Size startPreview(int cameraId) {
    if (!chooseCameraIdByFacing(cameraId)) {
      return null;
    }
    mFinalPreviewSize = getPreviewSize(mCameraCharacteristics, SurfaceHolder.class, mPreviewSize.getWidth(), mPreviewSize.getHeight());
    startOpeningCamera();
    return mFinalPreviewSize;
  }

  public void stopPreview() {
    if (mCaptureSession != null) {
      try {
        mCaptureSession.stopRepeating();
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
      mCaptureSession.close();
      mCaptureSession = null;
    }
    if (mCamera != null) {
      mCamera.close();
      mCamera = null;
    }
    mSurfaceTexture = null;
  }

  private boolean chooseCameraIdByFacing(int cameraId) {
    try {
      int internalFacing = INTERNAL_FACINGS.get(cameraId);
      final String[] ids = mCameraManager.getCameraIdList();
      if (ids.length == 0) { // No camera
        throw new RuntimeException("No camera available.");
      }
      for (String id : ids) {
        CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(id);
        Integer internal = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (internal == null) {
          throw new NullPointerException("Unexpected state: LENS_FACING null");
        }
        if (internal == internalFacing) {
          mCameraId = id;
          mCameraCharacteristics = characteristics;
          this.mFacing = cameraId;
          return true;
        }
      }
      // Not found
      mCameraId = ids[0];
      mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);

      Integer internal = mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
      if (internal == null) {
        throw new NullPointerException("Unexpected state: LENS_FACING null");
      }
      for (int i = 0, count = INTERNAL_FACINGS.size(); i < count; i++) {
        if (INTERNAL_FACINGS.valueAt(i) == internal) {
          mFacing = INTERNAL_FACINGS.keyAt(i);
          return true;
        }
      }
      // The operation can reach here when the only camera device is an external one.
      // We treat it as facing back.
      mFacing = FACING_BACK;
      return true;
    } catch (CameraAccessException e) {
      throw new RuntimeException("Failed to get a list of camera devices", e);
    }
  }

  @SuppressLint("MissingPermission")
  private void startOpeningCamera() {
    try {
      mCameraManager.openCamera(mCameraId, mCameraDeviceCallback, mCameraHandler);
    } catch (CameraAccessException e) {
      //TODO
      throw new RuntimeException("Failed to open camera: " + mCameraId, e);
    }
  }

  void startCaptureSession() {
    if (!isCameraOpened()) {
      return;
    }
    try {
      mSurfaceTexture.setDefaultBufferSize(mFinalPreviewSize.getWidth(), mFinalPreviewSize.getHeight());
      Surface surface = new Surface(mSurfaceTexture);
      mPreviewRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
      mPreviewRequestBuilder.addTarget(surface);
      mCamera.createCaptureSession(Arrays.asList(surface), mSessionCallback, mCameraHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to start camera session");
    }
  }

  boolean isCameraOpened() {
    return mCamera != null;
  }

  private Size getPreviewSize(CameraCharacteristics cameraCharacteristics, Class clazz, int maxWidth, int maxHeight) {
    float aspectRatio = ((float) maxWidth) / maxHeight;
    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    if (streamConfigurationMap == null) {
      return null;
    }
    Size[] supportedSizes = streamConfigurationMap.getOutputSizes(clazz);
    if (supportedSizes == null) {
      return null;
    }
    for (Size size : supportedSizes) {
      if (((float) size.getWidth()) / size.getHeight() == aspectRatio
          && size.getHeight() <= maxHeight && size.getWidth() <= maxWidth) {
        return size;
      }
    }
    return null;
  }

  private final CameraDevice.StateCallback mCameraDeviceCallback = new CameraDevice.StateCallback() {

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
      mCamera = camera;
      startCaptureSession();
    }

    @Override
    public void onClosed(@NonNull CameraDevice camera) {
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
      mCamera = null;
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
      Log.e("DEBUG", "onError: " + camera.getId() + " (" + error + ")");
      stopPreview();
    }
  };

  private final CameraCaptureSession.StateCallback mSessionCallback = new CameraCaptureSession.StateCallback() {
    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
      if (mCamera == null) {
        return;
      }
      mCaptureSession = session;
      mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

      try {
        mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mCameraHandler);
      } catch (CameraAccessException e) {
        Log.e("DEBUG", "Failed to start camera preview because it couldn't access camera", e);
      } catch (IllegalStateException e) {
        Log.e("DEBUG", "Failed to start camera preview.", e);
      }
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
      Log.e("DEBUG", "Failed to configure capture session.");
    }

    @Override
    public void onClosed(@NonNull CameraCaptureSession session) {
      if (mCaptureSession != null && mCaptureSession.equals(session)) {
        mCaptureSession = null;
      }
    }
  };
}
