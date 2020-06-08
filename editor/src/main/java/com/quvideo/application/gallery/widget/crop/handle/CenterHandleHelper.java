package com.quvideo.application.gallery.widget.crop.handle;

import android.graphics.RectF;
import androidx.annotation.NonNull;
import com.quvideo.application.gallery.widget.crop.edge.Edge;

/**
 * HandleHelper class to handle the center handle.
 */
class CenterHandleHelper extends HandleHelper {

  // Constructor /////////////////////////////////////////////////////////////////////////////////

  CenterHandleHelper() {
    super(null, null);
  }

  // HandleHelper Methods ////////////////////////////////////////////////////////////////////////

  @Override void updateCropWindow(float x, float y, @NonNull RectF imageRect, float snapRadius) {

    float left = Edge.LEFT.getCoordinate();
    float top = Edge.TOP.getCoordinate();
    float right = Edge.RIGHT.getCoordinate();
    float bottom = Edge.BOTTOM.getCoordinate();

    final float currentCenterX = (left + right) / 2;
    final float currentCenterY = (top + bottom) / 2;

    final float offsetX = x - currentCenterX;
    final float offsetY = y - currentCenterY;

    // Adjust the crop window.
    Edge.LEFT.offset(offsetX);
    Edge.TOP.offset(offsetY);
    Edge.RIGHT.offset(offsetX);
    Edge.BOTTOM.offset(offsetY);

    // Check if we have gone out of bounds on the sides, and fix.
    if (Edge.LEFT.isOutsideMargin(imageRect, snapRadius)) {
      final float offset = Edge.LEFT.snapToRect(imageRect);
      Edge.RIGHT.offset(offset);
    } else if (Edge.RIGHT.isOutsideMargin(imageRect, snapRadius)) {
      final float offset = Edge.RIGHT.snapToRect(imageRect);
      Edge.LEFT.offset(offset);
    }

    // Check if we have gone out of bounds on the top or bottom, and fix.
    if (Edge.TOP.isOutsideMargin(imageRect, snapRadius)) {
      final float offset = Edge.TOP.snapToRect(imageRect);
      Edge.BOTTOM.offset(offset);
    } else if (Edge.BOTTOM.isOutsideMargin(imageRect, snapRadius)) {
      final float offset = Edge.BOTTOM.snapToRect(imageRect);
      Edge.TOP.offset(offset);
    }
  }

  @Override void updateCropWindow(float x, float y, float targetAspectRatio,
      @NonNull RectF imageRect, float snapRadius) {

    updateCropWindow(x, y, imageRect, snapRadius);
  }
}
