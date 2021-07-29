package com.quvideo.application.gallery.widget.crop.handle;

import android.graphics.RectF;
import androidx.annotation.NonNull;
import com.quvideo.application.gallery.widget.crop.edge.Edge;
import com.quvideo.application.gallery.widget.crop.edge.EdgePair;

/**
 * HandleHelper class to handle corner Handles (i.e. top-left, top-right, bottom-left, and
 * bottom-right handles).
 */
class CornerHandleHelper extends HandleHelper {

  // Constructor /////////////////////////////////////////////////////////////////////////////////

  CornerHandleHelper(Edge horizontalEdge, Edge verticalEdge) {
    super(horizontalEdge, verticalEdge);
  }

  // HandleHelper Methods ////////////////////////////////////////////////////////////////////////

  @Override void updateCropWindow(float x, float y, float targetAspectRatio,
      @NonNull RectF imageRect, float snapRadius) {

    final EdgePair activeEdges = getActiveEdges(x, y, targetAspectRatio);
    final Edge primaryEdge = activeEdges.primary;
    final Edge secondaryEdge = activeEdges.secondary;

    primaryEdge.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio);
    secondaryEdge.adjustCoordinate(targetAspectRatio);

    if (secondaryEdge.isOutsideMargin(imageRect, snapRadius)) {
      secondaryEdge.snapToRect(imageRect);
      primaryEdge.adjustCoordinate(targetAspectRatio);
    }
  }
}
