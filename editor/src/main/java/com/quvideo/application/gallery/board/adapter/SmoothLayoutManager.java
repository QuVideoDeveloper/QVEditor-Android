package com.quvideo.application.gallery.board.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc 名副其实的smooth(润滑型)LinearLayoutManager
 * @since 2018/4/28
 */
public class SmoothLayoutManager extends LinearLayoutManager {

  /**
   * MILLISECONDS_PER_INCH是常量，源码默认等于25f
   */
  private static final float MILLISECONDS_PER_INCH = 150f;

  public SmoothLayoutManager(Context context) {
    super(context);
  }

  public SmoothLayoutManager(Context context, int orientation, boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  @Override
  public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
      final int position) {

    LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
      // 返回：滑过1px时经历的时间(ms)。
      @Override protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
      }
    };

    smoothScroller.setTargetPosition(position);
    startSmoothScroll(smoothScroller);
  }
}
