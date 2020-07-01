package com.quvideo.application.slide;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DateUtils;
import com.quvideo.mobile.engine.slide.SlideInfo;
import java.util.ArrayList;
import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.TemplateHolder> {

  private List<SlideInfo> mDataList = new ArrayList<>();

  private Activity mActivity;

  private OnSlideClickListener mOnSlideClickListener;
  private int select = 0;

  public SlideAdapter(AppCompatActivity activity, OnSlideClickListener onSlideClickListener) {
    this.mActivity = activity;
    this.mOnSlideClickListener = onSlideClickListener;
  }

  public void updateList(List<SlideInfo> dataList) {
    this.mDataList = dataList;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public TemplateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new TemplateHolder(
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.slide_node_list_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    final SlideInfo item = mDataList.get(position);
    holder.tvTime.setText(DateUtils.getFormatDuration(item.duration));
    boolean isSelect = position == select;
    holder.ivCover.setSelected(isSelect);
    if (isSelect) {
      holder.ivReplace.setVisibility(View.VISIBLE);
    } else {
      holder.ivReplace.setVisibility(View.GONE);
    }
    if (!TextUtils.isEmpty(item.filePath)) {
      Glide.with(mActivity).load(item.filePath).into(holder.ivCover);
    }
    holder.ivReplace.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mOnSlideClickListener != null) {
          mOnSlideClickListener.onReplaceClick(item);
        }
      }
    });
    holder.ivCover.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int oldSlt = select;
        select = position;
        if (mOnSlideClickListener != null) {
          mOnSlideClickListener.onClick(item);
        }
        notifyItemChanged(oldSlt);
        notifyItemChanged(select);
      }
    });
  }

  @Override
  public int getItemCount() {
    return mDataList.size();
  }

  class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView tvTime;
    private AppCompatImageView ivCover;
    private AppCompatImageView ivReplace;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      tvTime = itemView.findViewById(R.id.slide_node_item_text);
      ivCover = itemView.findViewById(R.id.slide_node_item_image);
      ivReplace = itemView.findViewById(R.id.slide_node_replace);
    }
  }

  public interface OnSlideClickListener {
    void onClick(SlideInfo item);

    void onReplaceClick(SlideInfo item);
  }
}
