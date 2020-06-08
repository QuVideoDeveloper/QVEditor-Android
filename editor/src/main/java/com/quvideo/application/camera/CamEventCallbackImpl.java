package com.quvideo.application.camera;

import com.google.gson.Gson;
import com.mediarecorder.engine.basicdef.QExpressionPasterStatus;
import com.quvideo.mobile.engine.camera.ICameraEventCallback;
import xiaoying.utils.LogUtils;
import xiaoying.utils.WorkThreadTaskItem;

public class CamEventCallbackImpl implements ICameraEventCallback {

  private static final String TAG = "CamEventCallbackImpl";

  @Override public void onCaptureDone(String filePath) {
    LogUtils.d(TAG, "onCaptureDone : " + filePath);
  }

  @Override public void onRecorderRunning(long duration) {
    LogUtils.d(TAG, "onRecorderRunning : " + duration);
  }

  @Override public void onRecorderStop(WorkThreadTaskItem workThreadTaskItem) {
    LogUtils.d(TAG, "onRecorderStop : " + new Gson().toJson(workThreadTaskItem));
  }

  @Override public void onRecorderPaused() {
    LogUtils.d(TAG, "onRecorderPaused ...");
  }

  @Override public void onRecorderReady() {
    LogUtils.d(TAG, "onRecorderReady ... ");
  }

  @Override public void onRecorderDurationExceeded() {
    LogUtils.d(TAG, "onRecorderDurationExceeded ... ");
  }

  @Override public void onRecorderSizeExceeded() {
    LogUtils.d(TAG, "onRecorderSizeExceeded ... ");
  }

  @Override public void onFaceDetectResult(boolean isDetected) {
    LogUtils.d(TAG, "onFaceDetectResult : " + isDetected);
  }

  @Override public void onConnectResult(boolean isConnected) {
    LogUtils.d(TAG, "onConnectResult : " + isConnected);
  }

  @Override public void onDisConnect() {
    LogUtils.d(TAG, "onDisConnect ... ");
  }

  @Override public void onPreviewStart() {
    LogUtils.d(TAG, "onPreviewStart ... ");
  }

  @Override public void onPreviewStop() {
    LogUtils.d(TAG, "onPreviewStop ... ");
  }

  @Override public void onPipSrcObjEnd() {
    LogUtils.d(TAG, "onPipSrcObjEnd ... ");
  }

  @Override public void onPasterDisplayStatusChanged(QExpressionPasterStatus status) {
    LogUtils.d(TAG, "onPasterDisplayStatusChanged : " + new Gson().toJson(status));
  }
}
