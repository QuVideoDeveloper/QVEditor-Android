package com.quvideo.application.gallery.widget.crop.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import androidx.annotation.NonNull;
import com.quvideo.application.editor.R;

/**
 * Utility class for handling all of the Paint used to draw the CropOverlayView.
 */
public class PaintUtil {

  // Public Methods //////////////////////////////////////////////////////////

  private static final String DEFAULT_BACKGROUND_COLOR_ID = "#B0000000";
  private static final float DEFAULT_LINE_THICKNESS_DP = 3;
  private static final String SEMI_TRANSPARENT = "#AAFFFFFF";

  /**
   * Creates the Paint object for drawing the crop window border.
   *
   * @param context the Context
   * @return new Paint object
   */
  public static Paint newBorderPaint(Context context) {

    // Set the line thickness for the crop window border.
    final float lineThicknessPx =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_THICKNESS_DP,
            context.getResources().getDisplayMetrics());

    final Paint borderPaint = new Paint();
    borderPaint.setColor(Color.parseColor(SEMI_TRANSPARENT));
    borderPaint.setStrokeWidth(lineThicknessPx);
    borderPaint.setStyle(Paint.Style.STROKE);

    return borderPaint;
  }

  /**
   * Creates the Paint object for drawing the crop window border.
   */
  public static Paint newBorderPaint(@NonNull Resources resources) {

    final Paint paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(resources.getDimension(R.dimen.border_thickness));
    paint.setColor(resources.getColor(R.color.border));

    return paint;
  }

  /**
   * Creates the Paint object for drawing the crop window guidelines.
   */
  public static Paint newGuidelinePaint(@NonNull Resources resources) {

    final Paint paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(resources.getDimension(R.dimen.guideline_thickness));
    paint.setColor(resources.getColor(R.color.guideline));

    return paint;
  }

  /**
   * Creates the Paint object for drawing the translucent overlay outside the crop window.
   *
   * @return the new Paint object
   */
  public static Paint newSurroundingAreaOverlayPaint(@NonNull Resources resources) {

    final Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(resources.getColor(R.color.surrounding_area));

    return paint;
  }

  /**
   * Creates the Paint object for drawing the corners of the border
   */
  public static Paint newCornerPaint(@NonNull Resources resources) {

    final Paint paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(resources.getDimension(R.dimen.corner_thickness));
    paint.setColor(Color.WHITE);

    return paint;
  }

  /**
   * Creates the Paint object for drawing the translucent overlay outside the
   * crop window.
   *
   * @param context the Context
   * @return the new Paint object
   */
  public static Paint newBackgroundPaint(Context context) {

    final Paint paint = new Paint();
    paint.setColor(Color.parseColor(DEFAULT_BACKGROUND_COLOR_ID));

    return paint;
  }
}
