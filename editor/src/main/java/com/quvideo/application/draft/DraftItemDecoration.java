package com.quvideo.application.draft;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DeviceSizeUtil;

public class DraftItemDecoration extends RecyclerView.ItemDecoration {

  private Drawable mDivider;

  private int mLeftMargin;

  public DraftItemDecoration(Context context) {
    mLeftMargin = (int) DeviceSizeUtil.dpToPixel(16);
    mDivider = context.getResources().getDrawable(R.drawable.editor_shape_draft_item_decoration);
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent) {
    drawVertical(c, parent);
  }

  public void drawVertical(Canvas c, RecyclerView parent) {
    final int left = parent.getPaddingLeft() + mLeftMargin;
    final int right = parent.getWidth() - parent.getPaddingRight();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      if (i == 0 || i == childCount - 1) {
        continue;
      }
      final View child = parent.getChildAt(i);
      if (child == null) {
        return;
      }
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
          .getLayoutParams();
      final int top = child.getBottom() + params.bottomMargin;
      final int bottom = top + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }
}
