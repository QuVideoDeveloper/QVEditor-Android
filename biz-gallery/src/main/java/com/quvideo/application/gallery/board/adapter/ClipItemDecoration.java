package com.quvideo.application.gallery.board.adapter;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.utils.RTLUtil;

/**
 * @desc CLip List ItemDecoration
 */

public class ClipItemDecoration extends RecyclerView.ItemDecoration {
  private int edgeSpace;

  public ClipItemDecoration(int edgeSpace) {
    this.edgeSpace = edgeSpace;
  }

  @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
      @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
    if (parent.getAdapter() == null) {
      return;
    }
    if (RTLUtil.isRTL()) {
      if (parent.getChildAdapterPosition(view) == 0) {
        outRect.right = edgeSpace;
      } else if (parent.getChildAdapterPosition(view)
          == parent.getAdapter().getItemCount() - 1) {
        outRect.left = edgeSpace;
      }
    } else {
      if (parent.getChildAdapterPosition(view) == 0) {
        outRect.left = edgeSpace;
      } else if (parent.getChildAdapterPosition(view)
          == parent.getAdapter().getItemCount() - 1) {
        outRect.right = edgeSpace;
      }
    }
  }
}
