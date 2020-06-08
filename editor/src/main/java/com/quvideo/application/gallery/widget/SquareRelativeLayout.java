package com.quvideo.application.gallery.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/2/2019
 */
public class SquareRelativeLayout extends RelativeLayout {
  public SquareRelativeLayout(Context context) {
    super(context);
  }

  public SquareRelativeLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // For simple implementation, or internal size is always 0.
    // We depend on the container to specify the layout size of
    // our view. We can't really know what it is since we will be
    // adding and removing different arbitrary views and do not
    // want the layout to change as this happens.
    this.setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
        getDefaultSize(0, heightMeasureSpec));
    // Children are just made to fill our space.
    int childWidthSize = getMeasuredWidth();
    //设置高度与宽度一样
    heightMeasureSpec =
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }
}