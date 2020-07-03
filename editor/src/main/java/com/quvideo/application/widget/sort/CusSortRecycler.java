package com.quvideo.application.widget.sort;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by shengsheng(Kyle)
 * on 2019/11/26
 */
public class CusSortRecycler extends RecyclerView implements OnItemMoveListener {

  private SelectSceneListener sceneListener;

  private int mStartIndex = -1;

  private int mEndIndex = -1;

  public CusSortRecycler(@NonNull Context context) {
    super(context);
  }

  public CusSortRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public CusSortRecycler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setSceneListener(SelectSceneListener listener) {
    sceneListener = listener;
  }

  @Override public void onItemMove(int fromPosition, int toPosition) {
    if (mStartIndex == -1) {
      mStartIndex = fromPosition;
    }
    mEndIndex = toPosition;
    if (fromPosition != toPosition) {
      getAdapter().notifyItemMoved(fromPosition, toPosition);
    }
  }

  @Override public void onOrderStart() {
    if (sceneListener != null) {
      sceneListener.onOrderStart();
    }
  }

  @Override public void onOrderFinish(View itemView) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      itemView.setForeground(null);
    }
    if (sceneListener != null && mStartIndex != mEndIndex) {
      sceneListener.onOrderChanged(mStartIndex, mEndIndex);
    }
    mStartIndex = -1;
    mEndIndex = -1;
  }

  public interface SelectSceneListener {

    void onOrderStart();

    void onOrderChanged(int from, int to);
  }
}
