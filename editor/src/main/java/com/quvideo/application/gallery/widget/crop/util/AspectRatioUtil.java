package com.quvideo.application.gallery.widget.crop.util;

import android.graphics.RectF;
import androidx.annotation.NonNull;

/**
 * Utility class for handling calculations involving a fixed aspect ratio.
 */
public class AspectRatioUtil {

  /**
   * Calculates the aspect ratio given a rectangle.
   */
  public static float calculateAspectRatio(float left, float top, float right, float bottom) {
    final float width = right - left;
    final float height = bottom - top;
    return width / height;
  }

  /**
   * Calculates the aspect ratio given a rectangle.
   */
  public static float calculateAspectRatio(@NonNull RectF rect) {
    return rect.width() / rect.height();
  }

  /**
   * Calculates the x-coordinate of the left edge given the other sides of the rectangle and an
   * aspect ratio.
   */
  public static float calculateLeft(float top, float right, float bottom, float targetAspectRatio) {

    final float height = bottom - top;
    // targetAspectRatio = width / height
    // width = targetAspectRatio * height
    // right - left = targetAspectRatio * height
    return right - (targetAspectRatio * height);
  }

  /**
   * Calculates the y-coordinate of the top edge given the other sides of the rectangle and an
   * aspect ratio.
   */
  public static float calculateTop(float left, float right, float bottom, float targetAspectRatio) {

    final float width = right - left;
    // targetAspectRatio = width / height
    // width = targetAspectRatio * height
    // height = width / targetAspectRatio
    // bottom - top = width / targetAspectRatio
    return bottom - (width / targetAspectRatio);
  }

  /**
   * Calculates the x-coordinate of the right edge given the other sides of the rectangle and an
   * aspect ratio.
   */
  public static float calculateRight(float left, float top, float bottom, float targetAspectRatio) {

    final float height = bottom - top;
    // targetAspectRatio = width / height
    // width = targetAspectRatio * height
    // right - left = targetAspectRatio * height
    return (targetAspectRatio * height) + left;
  }

  /**
   * Calculates the y-coordinate of the bottom edge given the other sides of the rectangle and an
   * aspect ratio.
   */
  public static float calculateBottom(float left, float top, float right, float targetAspectRatio) {

    final float width = right - left;
    // targetAspectRatio = width / height
    // width = targetAspectRatio * height
    // height = width / targetAspectRatio
    // bottom - top = width / targetAspectRatio
    return (width / targetAspectRatio) + top;
  }

  /**
   * Calculates the width of a rectangle given the top and bottom edges and an aspect ratio.
   */
  public static float calculateWidth(float height, float targetAspectRatio) {
    return targetAspectRatio * height;
  }

  /**
   * Calculates the height of a rectangle given the left and right edges and an aspect ratio.
   */
  public static float calculateHeight(float width, float targetAspectRatio) {
    return width / targetAspectRatio;
  }
}
