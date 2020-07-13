package com.quvideo.application.gallery;

import com.quvideo.application.gallery.constant.StorageInfo;

public class MediaConfig {
  public static String VIVA_GALLERY_KEY_HTTP_HEADER_REFERER = "http://xiaoying.tv";

  public static String APP_DEFAULT_EXPORT_PATH;
  public static String CAMERA_VIDEO_RELATIVE_PATH;

  public static String APP_COUNTRY_CODE;

  /**
   * media gif source available
   */
  public static boolean GIF_AVAILABLE = false;

  public static void init() {
    StorageInfo.init();
  }

  public static void init(String exportPath, String cameraPath) {
    APP_DEFAULT_EXPORT_PATH = exportPath;
    CAMERA_VIDEO_RELATIVE_PATH = cameraPath;
    StorageInfo.init();
  }

  public static void setAppCountryCode(String appCountryCode) {
    APP_COUNTRY_CODE = appCountryCode;
  }
}
