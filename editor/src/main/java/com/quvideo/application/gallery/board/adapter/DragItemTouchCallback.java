package com.quvideo.application.gallery.board.adapter;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Elijah <a href="zhonghu.liu@quvideo.com">Contact me.</a>
 * @since 2018/1/24
 */
public class DragItemTouchCallback extends ItemTouchHelper.Callback {

  private ItemTouchAdapter itemTouchAdapter;

  private Drawable background;
  private int bkcolor = -1;
  private OnDragListener onDragListener;
  private int startMovePos = -1;
  private int endMovePos = -1;
  private boolean isLongPressDragEnabled;

  public DragItemTouchCallback(ItemTouchAdapter itemTouchAdapter,
      boolean isLongPressDragEnabled) {
    this.itemTouchAdapter = itemTouchAdapter;
    this.isLongPressDragEnabled = isLongPressDragEnabled;
  }

  @Override public boolean isLongPressDragEnabled() {
    return isLongPressDragEnabled;
  }

  @Override public boolean isItemViewSwipeEnabled() {
    return true;
  }

  @Override public int getMovementFlags(@NonNull RecyclerView recyclerView,
      @NonNull RecyclerView.ViewHolder viewHolder) {
    if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
      final int dragFlags = ItemTouchHelper.UP
          | ItemTouchHelper.DOWN
          | ItemTouchHelper.LEFT
          | ItemTouchHelper.RIGHT;
      final int swipeFlags = 0;
      return makeMovementFlags(dragFlags, swipeFlags);
    } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
      int dragFlags;
      int swipeFlags;
      int orientation = ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation();
      if (orientation == LinearLayoutManager.HORIZONTAL) {
        dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        swipeFlags = 0;
      } else {
        dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        swipeFlags = 0;
      }

      return makeMovementFlags(dragFlags, swipeFlags);
    } else {
      final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
      //final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
      final int swipeFlags = 0;
      return makeMovementFlags(dragFlags, swipeFlags);
    }
  }

  @Override public boolean onMove(@NonNull RecyclerView recyclerView,
      @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
    //拖动ViewHolder的position
    int fromPosition = viewHolder.getAdapterPosition();
    //目标ViewHolder的position
    int toPosition = target.getAdapterPosition();
    if (startMovePos == -1) {
      startMovePos = fromPosition;
    }
    endMovePos = toPosition;
    itemTouchAdapter.onMove(fromPosition, toPosition);
    return true;
  }

  @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    int position = viewHolder.getAdapterPosition();
    itemTouchAdapter.onSwiped(position);
  }

  @Override public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
      @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
      boolean isCurrentlyActive) {
    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
      //滑动时改变Item的透明度
      final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
      viewHolder.itemView.setAlpha(alpha);
      viewHolder.itemView.setTranslationX(dX);
    } else {
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
  }

  @Override public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      if (background == null && bkcolor == -1) {
        Drawable drawable = viewHolder.itemView.getBackground();
        if (drawable == null) {
          bkcolor = 0;
        } else {
          background = drawable;
        }
      }
      if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
        if (onDragListener != null) {
          onDragListener.onStartDrag(viewHolder.itemView, viewHolder.getAdapterPosition());
        }
      }
    }
    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override public void clearView(@NonNull RecyclerView recyclerView,
      @NonNull RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);

    viewHolder.itemView.setAlpha(1.0f);
    if (background != null) {
      viewHolder.itemView.setBackgroundDrawable(background);
    }
    if (bkcolor != -1) {
      viewHolder.itemView.setBackgroundColor(bkcolor);
    }

    //viewHolder.itemView.setBackgroundColor(0);

    if (onDragListener != null) {
      onDragListener.onFinishDrag(viewHolder.itemView, startMovePos, endMovePos);
    }

    //reset pos
    startMovePos = -1;
    endMovePos = -1;
  }

  public void setOnDragListener(OnDragListener onDragListener) {
    this.onDragListener = onDragListener;
  }

  public interface OnDragListener {
    void onStartDrag(View view, int pos);

    void onFinishDrag(View view, int startPos, int endPos);
  }

  public interface ItemTouchAdapter {
    void onMove(int fromPosition, int toPosition);

    void onSwiped(int position);
  }
}
