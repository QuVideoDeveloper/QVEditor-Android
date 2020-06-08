package com.quvideo.application.camera;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;

class CamFilterControlViewMgr {

  interface OnFilterSelectListener {
    void onFilterSelected(String filePath);
  }

  private View rootView;
  private RecyclerView recyclerView;

  void bindView(@NonNull final AppCompatActivity activity,
      @NonNull OnFilterSelectListener listener) {
    rootView = activity.findViewById(R.id.filterControlView);
    if (rootView == null) {
      throw new RuntimeException("invalid camera filter view");
    }

    recyclerView = activity.findViewById(R.id.recyclerView);
    initRecyclerView(activity, listener);
  }

  void showView() {
    rootView.setVisibility(View.VISIBLE);
  }

  void hideView() {
    rootView.setVisibility(View.GONE);
  }

  private void initRecyclerView(@NonNull final AppCompatActivity activity,
      @NonNull OnFilterSelectListener listener) {
    if (recyclerView == null) {
      throw new RuntimeException("invalid camera filter view");
    }

    recyclerView.setLayoutManager(
        new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
    recyclerView.setAdapter(new CamFilterListAdapter(activity, listener));

    recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
          @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
          outRect.left = 60;
        } else {
          outRect.left = 15;
        }
      }
    });
  }

  boolean isViewShown() {
    return rootView.getVisibility() == View.VISIBLE;
  }
}
