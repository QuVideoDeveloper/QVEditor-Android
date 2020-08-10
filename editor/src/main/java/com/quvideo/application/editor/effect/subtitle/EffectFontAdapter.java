package com.quvideo.application.editor.effect.subtitle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.editor.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by santa on 2020-04-17.
 */
public class EffectFontAdapter extends RecyclerView.Adapter<EffectFontAdapter.TemplateHolder> {

  private List<String> mDataList = new ArrayList<>(Arrays.asList(AssetConstants.TEST_FONT_TID));

  private OnFontClickListener mOnFontClickListener;

  private int selectIndex = 0;

  public EffectFontAdapter(OnFontClickListener onFontClickListener) {
    this.mOnFontClickListener = onFontClickListener;
  }

  public void updateSelectPath(String fontPath) {
    if (TextUtils.isEmpty(fontPath)) {
      selectIndex = 0;
    } else {
      String path;
      boolean isFind = false;
      for (int index = 0; index < mDataList.size(); index++) {
        path = mDataList.get(index);
        if (fontPath.equals(path)) {
          selectIndex = index + 1;
          isFind = true;
          break;
        }
      }
      if (!isFind) {
        selectIndex = 0;
      }
    }
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
    holder.mTvContent.setText(R.string.mn_edit_subtitle_font);
    if (isSelected) {
      holder.mTvContent.setBackgroundResource(R.drawable.edit_item_bg_selected);
    } else {
      holder.mTvContent.setBackgroundResource(R.drawable.edit_item_bg_normal);
    }
    holder.mTvContent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnFontClickListener != null) {
          changeSelect(position);
          if (position == 0) {
            mOnFontClickListener.onClick("");
          } else {
            String fontPath = mDataList.get(position - 1);
            mOnFontClickListener.onClick(fontPath);
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

  @Override
  public int getItemCount() {
    return mDataList.size() + 1;
  }

  static class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView mTvContent;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mTvContent = itemView.findViewById(R.id.tvContent);
    }
  }

  public interface OnFontClickListener {
    void onClick(String fontPath);
  }
}
