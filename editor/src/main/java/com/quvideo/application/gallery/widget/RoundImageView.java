package com.quvideo.application.gallery.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.utils.GSizeUtil;

public class RoundImageView extends AppCompatImageView {

  private float cornerRadius = -1f;
  private Path mClipPath = new Path();
  private RectF rect = new RectF();

  public RoundImageView(Context context) {
    this(context, null);
  }

  public RoundImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    cornerRadius =
        getCornerRadius(context.obtainStyledAttributes(attrs, R.styleable.RoundImageView));
  }

  private float getCornerRadius(TypedArray array) {
    return array.getDimension(R.styleable.RoundImageView_cornerRadius, cornerRadius);
  }

  @Override protected void onDraw(Canvas canvas) {
    if (isInEditMode()) {
      super.onDraw(canvas);
      return;
    }
    int w = this.getWidth();
    int h = this.getHeight();

    if (cornerRadius < 0) {
      cornerRadius = GSizeUtil.getFitPxFromDp(getContext(), 4.0f);
    }

    float fx = cornerRadius;
    if (rect == null) {
      rect = new RectF();
    }
    rect.left = 0;
    rect.top = 0;
    rect.right = w;
    rect.bottom = h;
    mClipPath.addRoundRect(rect, fx, fx, Path.Direction.CW);
    canvas.clipPath(mClipPath);
    try {
      super.onDraw(canvas);
    } catch (Exception ignore) {
    }
  }
}
