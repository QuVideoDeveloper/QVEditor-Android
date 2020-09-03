package com.quvideo.application.editor.theme;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.mobile.engine.model.ThemeSubtitleEffect;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by santa on 2020-04-17.
 */
public class ThemeSubtitleAdapter extends RecyclerView.Adapter<ThemeSubtitleAdapter.TemplateHolder> {

  private List<ThemeSubtitleEffect> mDataList = new ArrayList<>();

  private OnSubtitleClickListener mOnSubtitleClickListener;

  private int selectIndex = 0;

  public ThemeSubtitleAdapter(List<ThemeSubtitleEffect> dataList, OnSubtitleClickListener onSubtitleClickListener) {
    this.mDataList = dataList;
    this.mOnSubtitleClickListener = onSubtitleClickListener;
  }

  public void updateDataList(List<ThemeSubtitleEffect> dataList) {
    this.mDataList = dataList;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public TemplateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new TemplateHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.view_edit_subtitle_text_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    boolean isSelected = position == selectIndex;
    ThemeSubtitleEffect item = mDataList.get(position);
    if (TextUtils.isEmpty(item.mText)) {
      holder.mTvContent.setText(R.string.mn_edit_tips_input_text);
    } else {
      holder.mTvContent.setText(item.mText);
    }
    if (isSelected) {
      holder.mTvContent.setBackgroundResource(R.drawable.edit_item_bg_selected);
    } else {
      holder.mTvContent.setBackgroundResource(R.drawable.edit_item_bg_normal);
    }
    holder.mTvContent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnSubtitleClickListener != null) {
          changeSelect(position);
          if (position == 0) {
            mOnSubtitleClickListener.onClick(position);
          }
        }
      }
    });
  }

  private void changeSelect(int index) {
    int oldIndex = selectIndex;
    selectIndex = index;
    if (oldIndex >= 0 && oldIndex < getItemCount()) {
      notifyItemChanged(oldIndex);
    }
    notifyItemChanged(selectIndex);
  }

  public int getSelectIndex() {
    return selectIndex;
  }

  @Override
  public int getItemCount() {
    return mDataList.size();
  }

  static class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView mTvContent;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mTvContent = itemView.findViewById(R.id.tvContent);
    }
  }

  public interface OnSubtitleClickListener {
    void onClick(int index);
  }
}
