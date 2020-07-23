package com.quvideo.application.export;

/**
 * Created by Administrator on 2017/11/15.
 */
public class VideoInfo {
  public int frameWidth;
  public int frameHeight;
  public int duration;

  public VideoInfo() {
  }

  public VideoInfo(int frameW, int frameH, int dura) {
    frameWidth = frameW;
    frameHeight = frameH;
    duration = dura;
  }
}
