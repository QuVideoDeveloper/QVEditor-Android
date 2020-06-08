package com.quvideo.application.camera.recorder;

import android.text.TextUtils;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import xiaoying.utils.LogUtils;

public class RecorderMgr {

  public static final int RECORDER_STATE_IDLE = 0;
  public static final int RECORDER_STATE_RECORDING = 1;
  public static final int RECORDER_STATE_PAUSE = 2;

  private List<RecorderClipInfo> clipList = new ArrayList<>();

  private String curFilePath;
  private int orientation;
  private String applyEffectPath;
  // 美颜程度。-1表示未添加美颜
  private int applyCamFBValue = -1;

  private int recorderState = RECORDER_STATE_IDLE;

  public void startRecorder(String curFilePath) {
    this.curFilePath = curFilePath;
    recorderState = RECORDER_STATE_RECORDING;

    LogUtils.d("RecorderMgr", "startRecorder");
  }

  public void stopRecorder(int[] recorderPos) {
    if (recorderPos != null) {
      addClip(recorderPos);
    }

    recorderState = RECORDER_STATE_IDLE;

    LogUtils.d("RecorderMgr", "stopRecorder = " + new Gson().toJson(recorderPos));
  }

  public void resumeRecorder() {
    recorderState = RECORDER_STATE_RECORDING;

    LogUtils.d("RecorderMgr", "resumeRecorder");
  }

  public void pauseRecorder(int[] recorderPos) {
    addClip(recorderPos);
    recorderState = RECORDER_STATE_PAUSE;

    LogUtils.d("RecorderMgr", "pauseRecorder = " + new Gson().toJson(recorderPos));
  }

  public int getRecorderState() {
    return recorderState;
  }

  public void onEffectApplied(String applyEffectPath) {
    this.applyEffectPath = applyEffectPath;
  }

  public String getApplyEffectPath() {
    return applyEffectPath;
  }

  private void addClip(int[] recorderPos) {
    if (TextUtils.isEmpty(curFilePath)) {
      return;
    }

    RecorderClipInfo clipInfo = new RecorderClipInfo(curFilePath);
    clipInfo.setDateTaken(System.currentTimeMillis());
    clipInfo.setOrientation(orientation);
    if (recorderPos.length == 2) {
      clipInfo.setRecorderPos(new Integer[] { recorderPos[0], recorderPos[1] });
    }
    if (!TextUtils.isEmpty(applyEffectPath)) {
      clipInfo.setEffectItem(new RecorderClipInfo.EffectItem(applyEffectPath));
    }
    clipList.add(clipInfo);

    LogUtils.d("RecorderMgr", "addClip = " + new Gson().toJson(clipInfo));
  }

  public List<RecorderClipInfo> getClipList() {
    return clipList;
  }

  public String getCurFilePath() {
    return curFilePath;
  }

  public void setCamFBValue(int value) {
    applyCamFBValue = value;
  }

  public int getCamFBValue() {
    return applyCamFBValue;
  }
}
