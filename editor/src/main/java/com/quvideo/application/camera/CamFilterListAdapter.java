package com.quvideo.application.camera;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.DPUtils;
import com.quvideo.application.editor.R;
import com.quvideo.application.glidedecoder.EffectThumbParams;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import java.util.ArrayList;
import java.util.List;

class CamFilterListAdapter
    extends RecyclerView.Adapter<CamFilterListAdapter.ItemViewHolder> {

  private ListViewModel listViewModel;
  private CamFilterControlViewMgr.OnFilterSelectListener onFilterSelectListener;

  CamFilterListAdapter(AppCompatActivity activity,
      @NonNull CamFilterControlViewMgr.OnFilterSelectListener listener) {
    onFilterSelectListener = listener;

    listViewModel = new ListViewModel();
    listViewModel.imgPathList = new ArrayList<>();
    listViewModel.imgPathList.add("None");
    for (long tid : AssetConstants.TEST_CAM_FILTER_TID) {
      XytInfo xytInfo = XytManager.getXytInfo(tid);
      listViewModel.imgPathList.add(xytInfo.filePath);
    }

    listViewModel.curSelectItemPos.observe(activity, new Observer<Integer>() {
      @Override public void onChanged(Integer integer) {
        if (listViewModel.lastSelectedItemPos == integer) {
          return;
        }

        if (listViewModel.lastSelectedItemPos >= 0) {
          notifyItemChanged(listViewModel.lastSelectedItemPos);
        }

        notifyItemChanged(integer);
        listViewModel.lastSelectedItemPos = integer;
      }
    });
  }

  @NonNull @Override
  public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View rootView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.view_camera_filter_list_item, parent, false);

    return new ItemViewHolder(rootView);
  }

  @Override public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
    boolean isItemSelected = listViewModel.curSelectItemPos.getValue() != null
        && position == listViewModel.curSelectItemPos.getValue();
    holder.imgItem.setSelected(isItemSelected);

    if (position == 0) {
      holder.imgItem.setBackgroundResource(R.drawable.cam_sel_no_filter_bg);
    } else {
      holder.imgItem.setBackgroundResource(R.drawable.cam_sel_filter_item_bg);
    }

    final String filterPath = listViewModel.imgPathList.get(position);
    int thumbWidth = DPUtils.dpToPixel(holder.imgItem.getContext(), 60);
    int thumbHeight = DPUtils.dpToPixel(holder.imgItem.getContext(), 60);
    EffectThumbParams effectThumbParams =
        new EffectThumbParams(filterPath, thumbWidth, thumbHeight);
    Glide.with(holder.imgItem).load(effectThumbParams).into(holder.imgItem);

    holder.imgItem.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        boolean isChanged = handleItemClick(position);
        if (isChanged) {
          onFilterSelectListener.onFilterSelected(filterPath);
        }
      }
    });
  }

  private boolean handleItemClick(int position) {
    if (listViewModel.lastSelectedItemPos == position) {
      return false;
    }

    listViewModel.curSelectItemPos.postValue(position);
    return true;
  }

  @Override public int getItemCount() {
    return listViewModel.imgPathList.size();
  }

  static class ItemViewHolder extends RecyclerView.ViewHolder {

    private AppCompatImageView imgItem;

    ItemViewHolder(@NonNull View itemView) {
      super(itemView);

      imgItem = itemView.findViewById(R.id.imgItem);
    }
  }

  private static class ListViewModel extends ViewModel {

    private List<String> imgPathList;

    private int lastSelectedItemPos = -1;

    private MutableLiveData<Integer> curSelectItemPos = new MutableLiveData<>();
  }
}
