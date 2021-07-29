package com.quvideo.application.gallery.widget.crop.handle;

import android.graphics.RectF;
import androidx.annotation.NonNull;
import com.quvideo.application.gallery.widget.crop.edge.Edge;
import com.quvideo.application.gallery.widget.crop.util.AspectRatioUtil;

/**
 * HandleHelper class to handle vertical handles (i.e. left and right handles).
 */
class VerticalHandleHelper extends HandleHelper {

  // Member Variables ////////////////////////////////////////////////////////////////////////////

  private Edge mEdge;

  // Constructor /////////////////////////////////////////////////////////////////////////////////

  VerticalHandleHelper(Edge edge) {
    super(null, edge);
    mEdge = edge;
  }

  // HandleHelper Methods ////////////////////////////////////////////////////////////////////////

  @Override void updateCropWindow(float x, float y, float targetAspectRatio,
      @NonNull RectF imageRect, float snapRadius) {

    // Adjust this Edge accordingly.
    mEdge.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio);

    float top = Edge.TOP.getCoordinate();
    float bottom = Edge.BOTTOM.getCoordinate();

    // After this Edge is moved, our crop window is now out of proportion.
    final float targetHeight =
        AspectRatioUtil.calculateHeight(Edge.getWidth(), targetAspectRatio);

    // Adjust the crop window so that it maintains the given aspect ratio by
    // moving the adjacent edges symmetrically in or out.
    final float difference = targetHeight - Edge.getHeight();
    final float halfDifference = difference / 2;
    top -= halfDifference;
    bottom += halfDifference;

    Edge.TOP.setCoordinate(top);
    Edge.BOTTOM.setCoordinate(bottom);

    // Check if we have gone out of bounds on the top or bottom, and fix.
    if (Edge.TOP.isOutsideMargin(imageRect, snapRadius) && !mEdge.isNewRectangleOutOfBounds(
        Edge.TOP, imageRect, targetAspectRatio)) {

      final float offset = Edge.TOP.snapToRect(imageRect);
      Edge.BOTTOM.offset(-offset);
      mEdge.adjustCoordinate(targetAspectRatio);
    }

    if (Edge.BOTTOM.isOutsideMargin(imageRect, snapRadius) && !mEdge.isNewRectangleOutOfBounds(
        Edge.BOTTOM, imageRect, targetAspectRatio)) {

      final float offset = Edge.BOTTOM.snapToRect(imageRect);
      Edge.TOP.offset(-offset);
      mEdge.adjustCoordinate(targetAspectRatio);
    }
  }
}
