package com.quvideo.application.frame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;

/**
 *
 */
public class ProcessorAdapter extends RecyclerView.Adapter<ProcessorAdapter.TemplateHolder> {

  private String[] operateList = new String[] {
      "滤镜", "背景", "转场"
  };

  private OnOperateClickListener mOnOperateClickListener;

  private int selectIndex = 0;

  public ProcessorAdapter(OnOperateClickListener onOperateClickListener) {
    this.mOnOperateClickListener = onOperateClickListener;
  }

  @NonNull
  @Override
  public TemplateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new TemplateHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.view_capture_operate_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    boolean isSelected = position == selectIndex;
    String item = operateList[position];
    holder.mTvContent.setText(item);
    if (isSelected) {
      holder.mTvContent.setBackgroundResource(R.drawable.edit_item_bg_selected);
    } else {
      holder.mTvContent.setBackgroundResource(R.drawable.edit_item_bg_normal_gray);
    }
    holder.mTvContent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnOperateClickListener != null) {
          changeSelect(position);
          if (position >= 0) {
            mOnOperateClickListener.onClick(position);
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
    return operateList.length;
  }

  static class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView mTvContent;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mTvContent = itemView.findViewById(R.id.tvContent);
    }
  }

  public interface OnOperateClickListener {
    void onClick(int index);
  }
}
