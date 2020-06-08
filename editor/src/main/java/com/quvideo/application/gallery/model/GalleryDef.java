package com.quvideo.application.gallery.model;

import androidx.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/2/2019
 */
public class GalleryDef {

  public static final int BASE_TIME_DELAY = 100;
  public static final int DEFAULT_TIME_DELAY = 300;

  /**
   * video & photo
   */
  public static final int MODE_BOTH = 0;
  /**
   * only video
   */
  public static final int MODE_VIDEO = 1;
  /**
   * only photo
   */
  public static final int MODE_PHOTO = 2;

  /**
   * unknown source type
   */
  public static final int TYPE_UNKNOWN = -1;
  /**
   * video source type
   */
  public static final int TYPE_VIDEO = 0;
  /**
   * photo source type
   */
  public static final int TYPE_PHOTO = 1;
  /**
   * gif source type
   */
  public static final int TYPE_GIF = 2;

  /**
   * photo output limit size(1080, 720)
   */
  public static final int LIMIT_720P = 0;
  /**
   * photo output limit size(1920, 1080)
   */
  public static final int LIMIT_1080P = 1;

  /**
   * photo output limit size(640, 480)
   */
  public static final int LIMIT_VGA = 2;

  /**
   * 普通有资源样式类型item
   */
  public static final int VIEW_TYPE_SOURCE_NORMAL = 0;

  /**
   * 普通无资源选中样式类型item
   */
  public static final int VIEW_TYPE_SOURCE_SELECT = 1;

  /**
   * 添加无资源未选中样式类型item
   */
  public static final int VIEW_TYPE_ADD_UNSELECT = 2;

  /**
   * 添加选中样式类型item
   */
  public static final int VIEW_TYPE_ADD_SELECT = 3;

  /**
   * 添加选中样式类型item
   */
  public static final int VIEW_TYPE_DEF_DRAG = 4;

  @IntDef({
      MODE_BOTH, MODE_VIDEO, MODE_PHOTO
  }) @Retention(RetentionPolicy.SOURCE) @Target({ ElementType.FIELD, ElementType.PARAMETER })
  public @interface ShowMode {
  }

  @IntDef({
      TYPE_UNKNOWN, TYPE_VIDEO, TYPE_PHOTO, TYPE_GIF
  }) @Retention(RetentionPolicy.SOURCE) @Target({ ElementType.FIELD, ElementType.PARAMETER })
  public @interface SourceType {
  }

  @IntDef({
      LIMIT_VGA, LIMIT_720P, LIMIT_1080P
  }) @Retention(RetentionPolicy.SOURCE) @Target({ ElementType.FIELD, ElementType.PARAMETER })
  public @interface PhotoLimit {
  }

  @IntDef({
      VIEW_TYPE_SOURCE_NORMAL, VIEW_TYPE_SOURCE_SELECT, VIEW_TYPE_ADD_UNSELECT,
      VIEW_TYPE_ADD_SELECT, VIEW_TYPE_DEF_DRAG
  }) @Retention(RetentionPolicy.SOURCE) @Target({ ElementType.FIELD, ElementType.PARAMETER })
  public @interface MediaViewType {

  }
}
