package com.quvideo.application.gallery;

import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaSpeedInfo;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/4/2019
 */
public class GallerySettings {

  public static int NO_LIMIT_UP_FLAG = -1;
  public static int NO_LIMIT_DOWN_FLAG = 1;

  /**
   * gallery media shown columns,default 3
   */
  public static int SHOWN_COLUMNS = 3;

  /**
   * media gif source available
   */
  public static boolean GIF_AVAILABLE = false;

  private String countryCode = "";
  private @GalleryDef.ShowMode int showMode;
  private int minSelectCount;
  private int maxSelectCount;
  private long videoMinDuration;
  private long videoMaxDuration;
  private @GalleryDef.PhotoLimit int photoLimit;

  private MediaSpeedInfo mediaSpeedInfo;
  private String exportVideoPath;
  private String exportImagePath;
  private String cameraVideoPath;
  private boolean isPhotoConvertPng;
  private boolean onlySupportFragment;
  private boolean photoTabFocus;
  private long limitVideoDuration = 0;

  private GallerySettings(Builder builder) {
    countryCode = builder.countryCode;
    showMode = builder.showMode;
    minSelectCount = builder.minSelectCount;
    maxSelectCount = builder.maxSelectCount;
    videoMinDuration = builder.videoMinDuration;
    videoMaxDuration = builder.videoMaxDuration;
    photoLimit = builder.photoLimit;
    mediaSpeedInfo = builder.mediaSpeedInfo;
    exportVideoPath = builder.exportVideoPath;
    exportImagePath = builder.exportImagePath;
    cameraVideoPath = builder.cameraVideoPath;
    isPhotoConvertPng = builder.isPhotoConvertPng;
    photoTabFocus = builder.photoTabFocus;
    limitVideoDuration = builder.limitVideoDuration;
    GIF_AVAILABLE = builder.gifAvaliable;
    MediaConfig.GIF_AVAILABLE = GIF_AVAILABLE;
  }

  public boolean isPhotoTabFocus() {
    return photoTabFocus;
  }

  public void setShowMode(int showMode) {
    this.showMode = showMode;
  }

  public void setMinSelectCount(int minSelectCount) {
    this.minSelectCount = minSelectCount;
  }

  public void setMaxSelectCount(int maxSelectCount) {
    this.maxSelectCount = maxSelectCount;
  }

  public long getVideoMinDuration() {
    return videoMinDuration;
  }

  public void setVideoMinDuration(long videoMinDuration) {
    this.videoMinDuration = videoMinDuration;
  }

  public long getVideoMaxDuration() {
    return videoMaxDuration;
  }

  public void setVideoMaxDuration(long videoMaxDuration) {
    this.videoMaxDuration = videoMaxDuration;
  }

  public void setPhotoLimit(int photoLimit) {
    this.photoLimit = photoLimit;
  }

  public void setMediaSpeedInfo(MediaSpeedInfo mediaSpeedInfo) {
    this.mediaSpeedInfo = mediaSpeedInfo;
  }

  public void setPhotoConvertPng(boolean photoConvertPng) {
    isPhotoConvertPng = photoConvertPng;
  }

  public void setOnlySupportFragment(boolean onlySupportFragment) {
    this.onlySupportFragment = onlySupportFragment;
  }

  public boolean isOnlySupportFragment() {
    return onlySupportFragment;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public boolean isPhotoConvertPng() {
    return isPhotoConvertPng;
  }

  public int getPhotoLimit() {
    return photoLimit;
  }

  public MediaSpeedInfo getMediaSpeedInfo() {
    return mediaSpeedInfo;
  }

  public int getShowMode() {
    return showMode;
  }

  public int getMinSelectCount() {
    return minSelectCount;
  }

  public int getMaxSelectCount() {
    return maxSelectCount;
  }

  public String getCameraVideoPath() {
    return cameraVideoPath;
  }

  public String getExportVideoPath() {
    return exportVideoPath;
  }

  public String getExportImagePath() {
    return exportImagePath;
  }

  public long getLimitVideoDuration() {
    return limitVideoDuration;
  }

  public void setLimitVideoDuration(long limitVideoDuration) {
    this.limitVideoDuration = limitVideoDuration;
  }

  public static final class Builder {
    private String countryCode = "";
    private @GalleryDef.ShowMode int showMode = GalleryDef.MODE_BOTH;
    private int minSelectCount = NO_LIMIT_DOWN_FLAG;
    private int maxSelectCount = NO_LIMIT_UP_FLAG;
    private long videoMinDuration = NO_LIMIT_UP_FLAG;
    private long videoMaxDuration = NO_LIMIT_UP_FLAG;
    private @GalleryDef.PhotoLimit int photoLimit;
    private MediaSpeedInfo mediaSpeedInfo;
    private String exportVideoPath;
    private String cameraVideoPath;
    private String exportImagePath;

    /**
     * 图片格式转换：默认输出PNG格式
     */
    private boolean isPhotoConvertPng = true;
    private boolean photoTabFocus;

    /**
     * 限制选择的视频时长
     */
    private long limitVideoDuration;

    private boolean gifAvaliable;

    public Builder() {
    }

    public Builder photoTabFocus(boolean photoTabFocus) {
      this.photoTabFocus = photoTabFocus;
      return this;
    }

    public Builder countryCode(String countryCode) {
      this.countryCode = countryCode;
      return this;
    }

    public Builder isPhotoConvetPng(boolean photoConvertPng) {
      isPhotoConvertPng = photoConvertPng;
      return this;
    }

    public Builder showMode(@GalleryDef.ShowMode int val) {
      showMode = val;
      return this;
    }

    public Builder photoLimit(@GalleryDef.PhotoLimit int val) {
      photoLimit = val;
      return this;
    }

    public Builder minSelectCount(int val) {
      minSelectCount = val;
      return this;
    }

    public Builder maxSelectCount(int val) {
      maxSelectCount = val;
      return this;
    }

    public long getVideoMinDuration() {
      return videoMinDuration;
    }

    public Builder setVideoMinDuration(long videoMinDuration) {
      this.videoMinDuration = videoMinDuration;
      return this;
    }

    public long getVideoMaxDuration() {
      return videoMaxDuration;
    }

    public Builder setVideoMaxDuration(long videoMaxDuration) {
      this.videoMaxDuration = videoMaxDuration;
      return this;
    }

    public Builder exportVideoPath(String val) {
      cameraVideoPath = val;
      return this;
    }

    public Builder exportImagePath(String exportImagePath) {
      this.exportImagePath = exportImagePath;
      return this;
    }

    public Builder setMediaSpeedInfo(MediaSpeedInfo mediaSpeedInfo) {
      this.mediaSpeedInfo = mediaSpeedInfo;
      return this;
    }

    public Builder cameraVideoPath(String val) {
      cameraVideoPath = val;
      return this;
    }

    public Builder setGifAvaliable(boolean gifAvaliable) {
      this.gifAvaliable = gifAvaliable;
      return this;
    }

    public Builder setLimitVideoDuration(long limitVideoDuration) {
      this.limitVideoDuration = limitVideoDuration;
      return this;
    }

    public GallerySettings build() {
      return new GallerySettings(this);
    }
  }
}
