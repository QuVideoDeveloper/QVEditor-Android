package com.quvideo.application.gallery.widget.crop.edge;

import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import com.quvideo.application.gallery.widget.crop.util.AspectRatioUtil;

/**
 * Enum representing an edge in the crop window.
 *
 * @author 765
 */
public enum Edge {

  /**
   * left
   */
  LEFT,
  /**
   * top
   */
  TOP,
  /**
   * right
   */
  RIGHT,
  /**
   * bottom
   */
  BOTTOM;

  // Private Constants ///////////////////////////////////////////////////////////////////////////

  // Minimum distance in pixels that one edge can get to its opposing edge.
  // This is an arbitrary value that simply prevents the crop window from becoming too small.
  private static int MIN_CROP_LENGTH_Ver = 100;
  private static int MIN_CROP_LENGTH_Hor = 100;

  private static float MAX_ASPECT_RATIO = 5.F;
  // Member Variables ////////////////////////////////////////////////////////////////////////////

  // The coordinate value of this edge.
  // This will be the x-coordinate for LEFT and RIGHT edges and the y-coordinate for TOP and BOTTOM edges.
  private float mCoordinate;

  // Public Methods //////////////////////////////////////////////////////////////////////////////

  /**
   * Sets the min length on the y-coordinate of the Edge
   *
   * @param px Minimum distance in pixels
   */
  public void setMinCropLengthVer(int px) {
    MIN_CROP_LENGTH_Ver = px;
  }

  /**
   * Sets the min length on the x-coordinate of the Edge
   *
   * @param px Minimum distance in pixels
   */
  public void setMinCropLengthHor(int px) {
    MIN_CROP_LENGTH_Hor = px;
  }

  /**
   * Sets the coordinate of the Edge. The coordinate will represent the x-coordinate for LEFT and
   * RIGHT Edges and the y-coordinate for TOP and BOTTOM edges.
   *
   * @param coordinate the position of the edge
   */
  public void setCoordinate(float coordinate) {
    mCoordinate = coordinate;
  }

  /**
   * Add the given number of pixels to the current coordinate position of this Edge.
   *
   * @param distance the number of pixels to add
   */
  public void offset(float distance) {
    mCoordinate += distance;
  }

  /**
   * Gets the coordinate of the Edge
   *
   * @return the Edge coordinate (x-coordinate for LEFT and RIGHT Edges and the y-coordinate for
   * TOP and BOTTOM edges)
   */
  public float getCoordinate() {
    return mCoordinate;
  }

  /**
   * Sets the Edge to the given x-y coordinate but also adjusting for snapping to the image bounds
   * and parent view border constraints.
   *
   * @param x the x-coordinate
   * @param y the y-coordinate
   * @param imageRect the bounding rectangle of the image
   * @param imageSnapRadius the radius (in pixels) at which the edge should snap to the image
   */
  public void adjustCoordinate(float x, float y, @NonNull RectF imageRect, float imageSnapRadius,
      float aspectRatio) {

    switch (this) {
      case LEFT:
        mCoordinate = adjustLeft(x, imageRect, imageSnapRadius, aspectRatio);
        break;
      case TOP:
        mCoordinate = adjustTop(y, imageRect, imageSnapRadius, aspectRatio);
        break;
      case RIGHT:
        mCoordinate = adjustRight(x, imageRect, imageSnapRadius, aspectRatio);
        break;
      case BOTTOM:
        mCoordinate = adjustBottom(y, imageRect, imageSnapRadius, aspectRatio);
        break;
      default:
        break;
    }
  }

  /**
   * Adjusts this Edge position such that the resulting window will have the given aspect ratio.
   *
   * @param aspectRatio the aspect ratio to achieve
   */
  public void adjustCoordinate(float aspectRatio) {

    final float left = Edge.LEFT.getCoordinate();
    final float top = Edge.TOP.getCoordinate();
    final float right = Edge.RIGHT.getCoordinate();
    final float bottom = Edge.BOTTOM.getCoordinate();

    switch (this) {
      case LEFT:
        mCoordinate = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);
        break;
      case TOP:
        mCoordinate = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);
        break;
      case RIGHT:
        mCoordinate = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);
        break;
      case BOTTOM:
        mCoordinate = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);
        break;
      default:
        break;
    }
  }

  /**
   * Returns whether or not you can re-scale the image based on whether any edge would be out of
   * bounds. Checks all the edges for a possibility of jumping out of bounds.
   *
   * @param edge the Edge that is about to be expanded
   * @param imageRect the rectangle of the picture
   * @param aspectRatio the desired aspectRatio of the picture
   * @return whether or not the new image would be out of bounds.
   */
  public boolean isNewRectangleOutOfBounds(@NonNull Edge edge, @NonNull RectF imageRect,
      float aspectRatio) {

    final float offset = edge.snapOffset(imageRect);

    switch (this) {
      case LEFT:
        if (edge.equals(Edge.TOP)) {
          final float top = imageRect.top;
          final float bottom = Edge.BOTTOM.getCoordinate() - offset;
          final float right = Edge.RIGHT.getCoordinate();
          final float left = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);

          return isOutOfBounds(top, left, bottom, right, imageRect);
        } else if (edge.equals(Edge.BOTTOM)) {
          final float bottom = imageRect.bottom;
          final float top = Edge.TOP.getCoordinate() - offset;
          final float right = Edge.RIGHT.getCoordinate();
          final float left = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);

          return isOutOfBounds(top, left, bottom, right, imageRect);
        }
        break;

      case TOP:
        if (edge.equals(Edge.LEFT)) {
          final float left = imageRect.left;
          final float right = Edge.RIGHT.getCoordinate() - offset;
          final float bottom = Edge.BOTTOM.getCoordinate();
          final float top = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);

          return isOutOfBounds(top, left, bottom, right, imageRect);
        } else if (edge.equals(Edge.RIGHT)) {
          final float right = imageRect.right;
          final float left = Edge.LEFT.getCoordinate() - offset;
          final float bottom = Edge.BOTTOM.getCoordinate();
          final float top = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);

          return isOutOfBounds(top, left, bottom, right, imageRect);
        }
        break;

      case RIGHT:
        if (edge.equals(Edge.TOP)) {
          final float top = imageRect.top;
          final float bottom = Edge.BOTTOM.getCoordinate() - offset;
          final float left = Edge.LEFT.getCoordinate();
          final float right = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);

          return isOutOfBounds(top, left, bottom, right, imageRect);
        } else if (edge.equals(Edge.BOTTOM)) {
          final float bottom = imageRect.bottom;
          final float top = Edge.TOP.getCoordinate() - offset;
          final float left = Edge.LEFT.getCoordinate();
          final float right = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);

          return isOutOfBounds(top, left, bottom, right, imageRect);
        }
        break;

      case BOTTOM:
        if (edge.equals(Edge.LEFT)) {
          final float left = imageRect.left;
          final float right = Edge.RIGHT.getCoordinate() - offset;
          final float top = Edge.TOP.getCoordinate();
          final float bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);

          return isOutOfBounds(top, left, bottom, right, imageRect);
        } else if (edge.equals(Edge.RIGHT)) {

          final float right = imageRect.right;
          final float left = Edge.LEFT.getCoordinate() - offset;
          final float top = Edge.TOP.getCoordinate();
          final float bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);

          return isOutOfBounds(top, left, bottom, right, imageRect);
        }
        break;
      default:
        break;
    }
    return true;
  }

  /**
   * Returns whether the new rectangle would be out of bounds.
   *
   * @param imageRect the Image to be compared with
   * @return whether it would be out of bounds
   */
  private boolean isOutOfBounds(float top, float left, float bottom, float right,
      @NonNull RectF imageRect) {
    return (top < imageRect.top
        || left < imageRect.left
        || bottom > imageRect.bottom
        || right > imageRect.right);
  }

  /**
   * Snap this Edge to the given image boundaries.
   *
   * @param imageRect the bounding rectangle of the image to snap to
   * @return the amount (in pixels) that this coordinate was changed (i.e. the new coordinate
   * minus the old coordinate value)
   */
  public float snapToRect(@NonNull RectF imageRect) {

    final float oldCoordinate = mCoordinate;

    switch (this) {
      case LEFT:
        mCoordinate = imageRect.left;
        break;
      case TOP:
        mCoordinate = imageRect.top;
        break;
      case RIGHT:
        mCoordinate = imageRect.right;
        break;
      case BOTTOM:
        mCoordinate = imageRect.bottom;
        break;
      default:
        break;
    }

    return mCoordinate - oldCoordinate;
  }

  /**
   * Returns the potential snap offset of snapToRect, without changing the coordinate.
   *
   * @param imageRect the bounding rectangle of the image to snap to
   * @return the amount (in pixels) that this coordinate was changed (i.e. the new coordinate
   * minus the old coordinate value)
   */
  public float snapOffset(@NonNull RectF imageRect) {

    final float oldCoordinate = mCoordinate;
    final float newCoordinate;

    switch (this) {
      case LEFT:
        newCoordinate = imageRect.left;
        break;
      case TOP:
        newCoordinate = imageRect.top;
        break;
      case RIGHT:
        newCoordinate = imageRect.right;
        break;
      default: // BOTTOM
        newCoordinate = imageRect.bottom;
        break;
    }

    return newCoordinate - oldCoordinate;
  }

  /**
   * Gets the current width of the crop window.
   */
  public static float getWidth() {
    return Edge.RIGHT.getCoordinate() - Edge.LEFT.getCoordinate();
  }

  /**
   * Gets the current height of the crop window.
   */
  public static float getHeight() {
    return Edge.BOTTOM.getCoordinate() - Edge.TOP.getCoordinate();
  }

  /**
   * Determines if this Edge is outside the image frame of the given bounding
   * rectangle.
   */
  public boolean isOutsideFrame(Rect rect) {

    double margin = 0;
    boolean result = false;

    switch (this) {
      case LEFT:
        result = mCoordinate - rect.left < margin;
        break;
      case TOP:
        result = mCoordinate - rect.top < margin;
        break;
      case RIGHT:
        result = rect.right - mCoordinate < margin;
        break;
      case BOTTOM:
        result = rect.bottom - mCoordinate < margin;
        break;
    }
    return result;
  }

  /**
   * Determines if this Edge is outside the inner margins of the given bounding rectangle. The
   * margins come inside the actual frame by SNAPRADIUS amount; therefore, determines if the point
   * is outside the inner "margin" frame.
   */
  public boolean isOutsideMargin(@NonNull RectF rect, float margin) {

    final boolean result;

    switch (this) {
      case LEFT:
        result = mCoordinate - rect.left < margin;
        break;
      case TOP:
        result = mCoordinate - rect.top < margin;
        break;
      case RIGHT:
        result = rect.right - mCoordinate < margin;
        break;
      default: // BOTTOM
        result = rect.bottom - mCoordinate < margin;
        break;
    }
    return result;
  }

  // Private Methods /////////////////////////////////////////////////////////////////////////////

  /**
   * Get the resulting x-position of the left edge of the crop window given the handle's position
   * and the image's bounding box and snap radius.
   *
   * @param x the x-position that the left edge is dragged to
   * @param imageRect the bounding box of the image that is being cropped
   * @param imageSnapRadius the snap distance to the image edge (in pixels)
   * @return the actual x-position of the left edge
   */
  private static float adjustLeft(float x, @NonNull RectF imageRect, float imageSnapRadius,
      float aspectRatio) {

    float resultX;

    if (x - imageRect.left < imageSnapRadius) {

      resultX = imageRect.left;
    } else {

      // Select the minimum of the three possible values to use
      float resultXHoriz = Float.POSITIVE_INFINITY;
      float resultXVert = Float.POSITIVE_INFINITY;

      // Checks if the window is too small horizontally
      if (x >= Edge.RIGHT.getCoordinate() - MIN_CROP_LENGTH_Hor) {
        resultXHoriz = Edge.RIGHT.getCoordinate() - MIN_CROP_LENGTH_Hor;
      }

      //if (Edge.RIGHT.getCoordinate() - x>)
      // Checks if the window is too small vertically
      //if (((Edge.RIGHT.getCoordinate() - x) / aspectRatio) <= MIN_CROP_LENGTH_Ver) {
      //  resultXVert = Edge.RIGHT.getCoordinate() - (MIN_CROP_LENGTH_Ver * aspectRatio);
      //}
      resultX = Math.min(x, resultXHoriz);

      float minX = (Edge.BOTTOM.getCoordinate() - Edge.TOP.getCoordinate()) / MAX_ASPECT_RATIO;
      if (Edge.RIGHT.getCoordinate() - resultX < minX) {
        resultX = Edge.RIGHT.getCoordinate() - minX;
      }
    }
    return resultX;
  }

  /**
   * Get the resulting x-position of the right edge of the crop window given the handle's position
   * and the image's bounding box and snap radius.
   *
   * @param x the x-position that the right edge is dragged to
   * @param imageRect the bounding box of the image that is being cropped
   * @param imageSnapRadius the snap distance to the image edge (in pixels)
   * @return the actual x-position of the right edge
   */
  private static float adjustRight(float x, @NonNull RectF imageRect, float imageSnapRadius,
      float aspectRatio) {

    float resultX;

    // If close to the edge...
    if (imageRect.right - x < imageSnapRadius) {

      resultX = imageRect.right;
    } else {

      // Select the maximum of the three possible values to use
      float resultXHoriz = Float.NEGATIVE_INFINITY;
      float resultXVert = Float.NEGATIVE_INFINITY;

      // Checks if the window is too small horizontally
      if (x <= Edge.LEFT.getCoordinate() + MIN_CROP_LENGTH_Hor) {
        resultXHoriz = Edge.LEFT.getCoordinate() + MIN_CROP_LENGTH_Hor;
      }
      // Checks if the window is too small vertically
      //if (((x - Edge.LEFT.getCoordinate()) / aspectRatio) <= MIN_CROP_LENGTH_Ver) {
      //  resultXVert = Edge.LEFT.getCoordinate() + (MIN_CROP_LENGTH_Ver * aspectRatio);
      //}
      resultX = Math.max(x, resultXHoriz);

      float minX = (Edge.BOTTOM.getCoordinate() - Edge.TOP.getCoordinate()) / MAX_ASPECT_RATIO;
      if (resultX - Edge.LEFT.getCoordinate() < minX) {
        resultX = Edge.LEFT.getCoordinate() + minX;
      }
    }
    return resultX;
  }

  /**
   * Get the resulting y-position of the top edge of the crop window given the handle's position
   * and the image's bounding box and snap radius.
   *
   * @param y the x-position that the top edge is dragged to
   * @param imageRect the bounding box of the image that is being cropped
   * @param imageSnapRadius the snap distance to the image edge (in pixels)
   * @return the actual y-position of the top edge
   */
  private static float adjustTop(float y, @NonNull RectF imageRect, float imageSnapRadius,
      float aspectRatio) {

    float resultY;

    if (y - imageRect.top < imageSnapRadius) {

      resultY = imageRect.top;
    } else {

      // Select the minimum of the three possible values to use
      float resultYVert = Float.POSITIVE_INFINITY;
      float resultYHoriz = Float.POSITIVE_INFINITY;

      // Checks if the window is too small vertically
      if (y >= Edge.BOTTOM.getCoordinate() - MIN_CROP_LENGTH_Ver) {
        resultYHoriz = Edge.BOTTOM.getCoordinate() - MIN_CROP_LENGTH_Ver;
      }

      // Checks if the window is too small horizontally
      //if (((Edge.BOTTOM.getCoordinate() - y) * aspectRatio) <= MIN_CROP_LENGTH_Hor) {
      //  resultYVert = Edge.BOTTOM.getCoordinate() - (MIN_CROP_LENGTH_Hor / aspectRatio);
      //}

      resultY = Math.min(y, resultYHoriz);

      float miny = (Edge.RIGHT.getCoordinate() - Edge.LEFT.getCoordinate()) / MAX_ASPECT_RATIO;
      if (Edge.BOTTOM.getCoordinate() - resultY < miny) {
        resultY = Edge.BOTTOM.getCoordinate() - miny;
      }
    }
    return resultY;
  }

  /**
   * Get the resulting y-position of the bottom edge of the crop window given the handle's
   * position and the image's bounding box and snap radius.
   *
   * @param y the x-position that the bottom edge is dragged to
   * @param imageRect the bounding box of the image that is being cropped
   * @param imageSnapRadius the snap distance to the image edge (in pixels)
   * @return the actual y-position of the bottom edge
   */
  private static float adjustBottom(float y, @NonNull RectF imageRect, float imageSnapRadius,
      float aspectRatio) {

    float resultY;

    if (imageRect.bottom - y < imageSnapRadius) {

      resultY = imageRect.bottom;
    } else {

      // Select the maximum of the three possible values to use
      float resultYVert = Float.NEGATIVE_INFINITY;
      float resultYHoriz = Float.NEGATIVE_INFINITY;

      // Checks if the window is too small vertically
      if (y <= Edge.TOP.getCoordinate() + MIN_CROP_LENGTH_Ver) {
        resultYVert = Edge.TOP.getCoordinate() + MIN_CROP_LENGTH_Ver;
      }
      // Checks if the window is too small horizontally
      //if (((y - Edge.TOP.getCoordinate()) * aspectRatio) <= MIN_CROP_LENGTH_Hor) {
      //  resultYHoriz = Edge.TOP.getCoordinate() + (MIN_CROP_LENGTH_Hor / aspectRatio);
      //}
      resultY = Math.max(y, resultYVert);

      float miny = (Edge.RIGHT.getCoordinate() - Edge.LEFT.getCoordinate()) / MAX_ASPECT_RATIO;
      if (resultY - Edge.TOP.getCoordinate() < miny) {
        resultY = Edge.TOP.getCoordinate() + miny;
      }
    }
    return resultY;
  }
}
