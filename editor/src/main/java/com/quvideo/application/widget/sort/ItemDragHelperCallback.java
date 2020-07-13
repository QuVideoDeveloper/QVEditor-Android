package com.quvideo.application.widget.sort;

import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.quvideo.application.editor.R;

public class ItemDragHelperCallback extends ItemTouchHelper.Callback {

  private OnItemMoveListener mItemMoveListener;

  public void setOnItemMoveListener(OnItemMoveListener onItemMoveListener) {
    this.mItemMoveListener = onItemMoveListener;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int dragFlags;
    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
    if (manager instanceof LinearLayoutManager
        || manager instanceof GridLayoutManager
        || manager instanceof StaggeredGridLayoutManager) {
      dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    } else {
      dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    }
    // 如果想支持滑动(删除)操作, swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END
    int swipeFlags = 0;
    return makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
      RecyclerView.ViewHolder target) {
    // 不同Type之间不可移动
    if (viewHolder.getItemViewType() != target.getItemViewType()) {
      return false;
    }
    if (mItemMoveListener != null) {
      mItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }
    Log.d("测试item", "item:translateX-" + viewHolder.itemView.getTranslationX()
        + ",translateY-" + viewHolder.itemView.getTranslationY() + "\n"
        + "X-" + viewHolder.itemView.getX() + ",Y-" + viewHolder.itemView.getY() + "\n"
        + "scrollX-" + viewHolder.itemView.getScrollX() + ",scrollY-" + viewHolder.itemView.getScrollY());
    return true;
  }

  @Override public float getMoveThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
    return super.getMoveThreshold(viewHolder);
  }

  @Override public boolean canDropOver(@NonNull RecyclerView recyclerView,
      @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
    return super.canDropOver(recyclerView, current, target);
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
  }

  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    // 不在闲置状态
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        viewHolder.itemView.setForeground(
            viewHolder.itemView.getContext().getDrawable(R.drawable.editor_shape_order_item_fg));
      }
      if (mItemMoveListener != null) {
        mItemMoveListener.onOrderStart();
      }
    }
    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      viewHolder.itemView.setForeground(null);
    }
    if (mItemMoveListener != null) {
      mItemMoveListener.onOrderFinish(viewHolder.itemView);
    }
    super.clearView(recyclerView, viewHolder);
  }

  @Override
  public boolean isLongPressDragEnabled() {
    // 不支持长按拖拽功能 手动控制
    return true;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    // 不支持滑动功能
    return false;
  }
}
