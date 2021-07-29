package com.quvideo.application.widget.sort;

import android.view.View;

public interface OnItemMoveListener {

  /** Item在开始拖拽 */
  void onOrderStart();

  /** Item在拖拽结束/滑动结束后触发 */
  void onOrderFinish(View itemView);

  void onItemMove(int fromPosition, int toPosition);
}
