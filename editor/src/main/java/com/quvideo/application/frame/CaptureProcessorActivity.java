/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.quvideo.application.frame;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.camera.CamPermissionMgr;
import com.quvideo.application.editor.R;
import com.quvideo.application.frame.opengl.CameraV2Renderer;
import com.quvideo.application.frame.view.FrameBGMenuPopwin;
import com.quvideo.application.frame.view.FrameFilterMenuPopwin;
import com.quvideo.application.frame.view.FrameTransMenuPopwin;
import com.quvideo.mobile.engine.process.param.BGParam;
import com.quvideo.mobile.engine.process.param.FilterParam;
import com.quvideo.mobile.engine.process.param.TransParam;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureProcessorActivity extends AppCompatActivity {

  private boolean checkPermission = false;

  private GLSurfaceView mGLSurfaceView;

  private Camera2Manager mCamera2Manager;

  private CameraV2Renderer mCameraV2Renderer;

  private FrameFilterMenuPopwin mFrameFilterMenuPopwin;
  private FrameBGMenuPopwin mFrameBGMenuPopwin;
  private FrameTransMenuPopwin mFrameTransMenuPopwin;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    setContentView(R.layout.activity_frame);
    initView();
  }

  private void initView() {
    mGLSurfaceView = findViewById(R.id.preview_view);

    mGLSurfaceView.setEGLContextClientVersion(2);

    mCameraV2Renderer = new CameraV2Renderer();
    mGLSurfaceView.setRenderer(mCameraV2Renderer);
    mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    mCamera2Manager = new Camera2Manager(getApplicationContext());

    RecyclerView rvOperate = findViewById(R.id.operate_recyclerview);
    rvOperate.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    ProcessorAdapter processorAdapter = new ProcessorAdapter(new ProcessorAdapter.OnOperateClickListener() {
      @Override public void onClick(int index) {
        if (index == 0) {
          // 滤镜
          if (mFrameFilterMenuPopwin == null) {
            mFrameFilterMenuPopwin =
                new FrameFilterMenuPopwin(CaptureProcessorActivity.this, new FrameFilterMenuPopwin.OnParamSelectCallback() {
                  @Override public void onParamChange(FilterParam filterParam) {
                    mGLSurfaceView.queueEvent(new Runnable() {
                      @Override public void run() {
                        if (mCameraV2Renderer.mFrameProcesserManager != null) {
                          mCameraV2Renderer.mFrameProcesserManager.setFilterParam(filterParam);
                        }
                      }
                    });
                  }
                });
          }
          mFrameFilterMenuPopwin.showAtLocation(mGLSurfaceView, Gravity.CENTER, 0, 0);
        } else if (index == 1) {
          // 背景
          if (mFrameBGMenuPopwin == null) {
            mFrameBGMenuPopwin =
                new FrameBGMenuPopwin(CaptureProcessorActivity.this, new FrameBGMenuPopwin.OnParamSelectCallback() {
                  @Override public void onParamChange(BGParam bgParam) {
                    mGLSurfaceView.queueEvent(new Runnable() {
                      @Override public void run() {
                        if (mCameraV2Renderer.mFrameProcesserManager != null) {
                          mCameraV2Renderer.mFrameProcesserManager.setBGParam(bgParam);
                        }
                      }
                    });
                  }
                });
          }
          mFrameBGMenuPopwin.showAtLocation(mGLSurfaceView, Gravity.CENTER, 0, 0);
        } else if (index == 2) {
          // 转场
          if (mFrameTransMenuPopwin == null) {
            mFrameTransMenuPopwin =
                new FrameTransMenuPopwin(CaptureProcessorActivity.this, new FrameTransMenuPopwin.OnParamSelectCallback() {
                  @Override public void onParamChange(TransParam transParam) {
                    mGLSurfaceView.queueEvent(new Runnable() {
                      @Override public void run() {
                        if (mCameraV2Renderer.mFrameProcesserManager != null) {
                          mCameraV2Renderer.mFrameProcesserManager.setTransParam(transParam);
                        }
                      }
                    });
                  }
                });
          }
          mFrameTransMenuPopwin.showAtLocation(mGLSurfaceView, Gravity.CENTER, 0, 0);
        }
      }
    });
    rvOperate.setAdapter(processorAdapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    // The activity was paused but not stopped, so the surface still exists. Therefore
    // surfaceCreated() won't be called, so init the camera here.
    if (!CamPermissionMgr.hasPermissionsGranted(this) && !checkPermission) {
      checkPermission = true;
      CamPermissionMgr.requestVideoPermissions(this);
      return;
    }
    initGlSurfaceView();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    boolean isAllowCamera =
        CamPermissionMgr.checkCamPermissionValid(this, requestCode, permissions, grantResults);
    if (isAllowCamera) {
      initGlSurfaceView();
      return;
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private void initGlSurfaceView() {
    mCameraV2Renderer.init(mGLSurfaceView, mCamera2Manager, false);
  }

  @Override
  protected void onPause() {
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    mCamera2Manager.stopPreview();
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mFrameFilterMenuPopwin != null && mFrameFilterMenuPopwin.isShowing()) {
      mFrameFilterMenuPopwin.dismiss();
    }
    if (mFrameBGMenuPopwin != null && mFrameBGMenuPopwin.isShowing()) {
      mFrameBGMenuPopwin.dismiss();
    }
    if (mFrameTransMenuPopwin != null && mFrameTransMenuPopwin.isShowing()) {
      mFrameTransMenuPopwin.dismiss();
    }
    if (mCameraV2Renderer != null) {
      mCameraV2Renderer.releaseAll();
      mCameraV2Renderer = null;
    }
    mCamera2Manager.destory();
  }
}
