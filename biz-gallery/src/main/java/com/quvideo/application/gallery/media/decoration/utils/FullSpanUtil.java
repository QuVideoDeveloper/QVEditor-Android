package com.quvideo.application.gallery.media.decoration.utils;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FullSpanUtil {

  public static void onAttachedToRecyclerView(RecyclerView recyclerView,
      final RecyclerView.Adapter adapter, final int pinnedHeaderType,
      final int pinnedFooterType) {
    // 如果是网格布局，这里处理标签的布局占满一行
    final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    if (layoutManager instanceof GridLayoutManager) {
      final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
      final GridLayoutManager.SpanSizeLookup oldSizeLookup =
          gridLayoutManager.getSpanSizeLookup();
      gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        @Override public int getSpanSize(int position) {
          if (adapter.getItemViewType(position) == pinnedHeaderType
              || adapter.getItemViewType(position) == pinnedFooterType) {
            return gridLayoutManager.getSpanCount();
          }
          if (oldSizeLookup != null) {
            return oldSizeLookup.getSpanSize(position);
          }
          return 1;
        }
      });
    }
  }
}
