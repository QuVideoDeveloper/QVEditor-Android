package com.quvideo.application.gallery.enums;

/**
 * Created by liuzhonghu on 2017/8/1.
 *
 * @Description
 */

public enum GROUP_MEDIA_TYPE {

  GROUP_MEDIA_TYPE_DATE(1), GROUP_MEDIA_TYPE_FOLDER(2), GROUP_MEDIA_TYPE_TITLE(3);

  GROUP_MEDIA_TYPE(int typeCode) {
  }

  public static GROUP_MEDIA_TYPE getType(int code) {
    switch (code) {
      case 1:
        return GROUP_MEDIA_TYPE_DATE;
      case 2:
        return GROUP_MEDIA_TYPE_FOLDER;
      case 3:
        return GROUP_MEDIA_TYPE_TITLE;
      default:
        return GROUP_MEDIA_TYPE_FOLDER;
    }
  }

  public String getName() {
    return this.name();
  }
}
