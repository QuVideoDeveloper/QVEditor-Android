package com.quvideo.application.widget.round;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @since 10/21/2019
 * 自定义圆角属性的RelativeLayout
 */
public class RCRelativeLayout extends RelativeLayout implements RCAttrs {
  RCHelper mRCHelper;

  public RCRelativeLayout(Context context) {
    this(context, null);
  }

  public RCRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RCRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mRCHelper = new RCHelper();
    mRCHelper.initAttrs(context, attrs);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mRCHelper.onSizeChanged(this, w, h);
  }

  @Override protected void dispatchDraw(Canvas canvas) {
    canvas.saveLayer(mRCHelper.mLayer, null, Canvas.ALL_SAVE_FLAG);
    super.dispatchDraw(canvas);
    mRCHelper.onClipDraw(canvas);
    canvas.restore();
  }

  @Override public void draw(Canvas canvas) {
    if (mRCHelper.mClipBackground) {
      canvas.save();
      canvas.clipPath(mRCHelper.mClipPath);
      super.draw(canvas);
      canvas.restore();
    } else {
      super.draw(canvas);
    }
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    int action = ev.getAction();
    if (action == MotionEvent.ACTION_DOWN && !mRCHelper.mAreaRegion.contains((int) ev.getX(),
        (int) ev.getY())) {
      return false;
    }
    if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
      refreshDrawableState();
    } else if (action == MotionEvent.ACTION_CANCEL) {
      setPressed(false);
      refreshDrawableState();
    }
    return super.dispatchTouchEvent(ev);
  }

  //--- 公开接口 ----------------------------------------------------------------------------------

  public void setClipBackground(boolean clipBackground) {
    mRCHelper.mClipBackground = clipBackground;
    invalidate();
  }

  public void setRoundAsCircle(boolean roundAsCircle) {
    mRCHelper.mRoundAsCircle = roundAsCircle;
    invalidate();
  }

  public void setRadius(int radius) {
    for (int i = 0; i < mRCHelper.radii.length; i++) {
      mRCHelper.radii[i] = radius;
    }
    invalidate();
  }

  public void setTopLeftRadius(int topLeftRadius) {
    mRCHelper.radii[0] = topLeftRadius;
    mRCHelper.radii[1] = topLeftRadius;
    invalidate();
  }

  public void setTopRightRadius(int topRightRadius) {
    mRCHelper.radii[2] = topRightRadius;
    mRCHelper.radii[3] = topRightRadius;
    invalidate();
  }

  public void setBottomLeftRadius(int bottomLeftRadius) {
    mRCHelper.radii[6] = bottomLeftRadius;
    mRCHelper.radii[7] = bottomLeftRadius;
    invalidate();
  }

  public void setBottomRightRadius(int bottomRightRadius) {
    mRCHelper.radii[4] = bottomRightRadius;
    mRCHelper.radii[5] = bottomRightRadius;
    invalidate();
  }

  public void setStrokeWidth(int strokeWidth) {
    mRCHelper.mStrokeWidth = strokeWidth;
    invalidate();
  }

  public void setStrokeColor(int strokeColor) {
    mRCHelper.mStrokeColor = strokeColor;
    invalidate();
  }

  @Override public void invalidate() {
    if (null != mRCHelper) {
      mRCHelper.refreshRegion(this);
    }
    super.invalidate();
  }

  public boolean isClipBackground() {
    return mRCHelper.mClipBackground;
  }

  public boolean isRoundAsCircle() {
    return mRCHelper.mRoundAsCircle;
  }

  public float getTopLeftRadius() {
    return mRCHelper.radii[0];
  }

  public float getTopRightRadius() {
    return mRCHelper.radii[2];
  }

  public float getBottomLeftRadius() {
    return mRCHelper.radii[4];
  }

  public float getBottomRightRadius() {
    return mRCHelper.radii[6];
  }

  public int getStrokeWidth() {
    return mRCHelper.mStrokeWidth;
  }

  public int getStrokeColor() {
    return mRCHelper.mStrokeColor;
  }
}
