package com.quvideo.application.frame.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.quvideo.application.frame.Camera2Manager;
import com.quvideo.application.frame.FrameProcesserManager;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import xiaoying.engine.base.QXYEffect;

public class CameraV2Renderer implements GLSurfaceView.Renderer {

  public static final String TAG = "Filter_CameraV2Renderer";

  GLSurfaceView mCameraV2GLSurfaceView;
  Camera2Manager mCamera;
  boolean bIsPreviewStarted;
  private int mOESTextureId = -1;
  private SurfaceTexture mSurfaceTexture;
  private float[] transformMatrix = new float[16];

  private OpenGLUtils mOpenGLUtils;
  private OpenGLUtils mOpenGLUtilsNormal;

  private int mViewWidth;
  private int mViewHeight;
  private volatile boolean initHandled = false;

  public volatile FrameProcesserManager mFrameProcesserManager = null;

  public void init(GLSurfaceView surfaceView, Camera2Manager camera, boolean isPreviewStarted) {
    mCameraV2GLSurfaceView = surfaceView;
    mCamera = camera;
    bIsPreviewStarted = isPreviewStarted;
    if (mSurfaceTexture == null && initHandled) {
      mCameraV2GLSurfaceView.requestRender();
    }
  }

  public void releaseAll() {
    mCameraV2GLSurfaceView.queueEvent(new Runnable() {
      @Override public void run() {
        if (mFrameProcesserManager != null) {
          mFrameProcesserManager.releaseAll();
        }
      }
    });
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    mOESTextureId = OpenGLUtils.createOESTextureObject();
    mOpenGLUtils = new OpenGLUtils(true);
    mOpenGLUtilsNormal = new OpenGLUtils(false);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    mViewWidth = width;
    mViewHeight = height;
    GLES20.glViewport(0, 0, width, height);
    Log.i(TAG, "onSurfaceChanged: " + width + ", " + height);
  }

  @Override
  public void onDrawFrame(GL10 gl) {

    if (mSurfaceTexture != null) {
      //更新纹理图像
      mSurfaceTexture.updateTexImage();
      //获取外部纹理的矩阵，用来确定纹理的采样位置，没有此矩阵可能导致图像翻转等问题
      mSurfaceTexture.getTransformMatrix(transformMatrix);
    }

    if (!bIsPreviewStarted) {
      bIsPreviewStarted = initSurfaceTexture();
      if (mCamera == null) {
        return;
      }
      bIsPreviewStarted = true;
      return;
    }
    float[] matrix = new float[] {
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f
    };
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    QXYEffect.QXYEffectData outFrame = null;
    if (mFrameProcesserManager != null && mCamera.mFinalPreviewSize != null) {
      QXYEffect.QXYEffectData inFrame = new QXYEffect.QXYEffectData();
      inFrame.width = mCamera.mFinalPreviewSize.getWidth();
      inFrame.height = mCamera.mFinalPreviewSize.getHeight();

      inFrame.matrix = transformMatrix;
      inFrame.texID = mOESTextureId;
      outFrame = mFrameProcesserManager.handleFramePreview(inFrame);
    }

    GLES20.glViewport(0, 0, mViewWidth, mViewHeight);
    if (outFrame.texID == mOESTextureId) {
      mOpenGLUtils.onDraw(mOESTextureId, transformMatrix);
    } else {
      mOpenGLUtilsNormal.onDraw(outFrame != null ? outFrame.texID : mOESTextureId, matrix);
    }
  }

  public boolean initSurfaceTexture() {
    initHandled = true;
    if (mCamera == null || mCameraV2GLSurfaceView == null) {
      Log.i(TAG, "mCamera or mGLSurfaceView is null!");
      return false;
    }
    if (mSurfaceTexture == null) {
      //根据外部纹理ID创建SurfaceTexture
      mSurfaceTexture = new SurfaceTexture(mOESTextureId);
      mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
          //每获取到一帧数据时请求OpenGL ES进行渲染
          mCameraV2GLSurfaceView.requestRender();
        }
      });
    }
    //讲此SurfaceTexture作为相机预览输出
    mCamera.setPreviewTexture(mSurfaceTexture);
    //开启预览
    mCamera.startPreview(mCamera.mFacing);
    //创建帧数据处理
    if (mFrameProcesserManager == null) {
      mFrameProcesserManager = new FrameProcesserManager();
    }
    return true;
  }
}
