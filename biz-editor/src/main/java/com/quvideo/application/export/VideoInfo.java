package com.quvideo.application.export;

/**
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
