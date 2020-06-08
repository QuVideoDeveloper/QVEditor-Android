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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DateUtils;
import com.quvideo.mobile.engine.slide.SlideInfo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.TemplateHolder> {

  private List<SlideInfo> mDataList = new ArrayList<>();

  private MutableLiveData<LinkedList<Integer>> mSelected = new MutableLiveData<>();

  private Activity mActivity;

  private OnSlideClickListener mOnSlideClickListener;

  public SlideAdapter(AppCompatActivity activity, OnSlideClickListener onSlideClickListener) {
    this.mActivity = activity;
    this.mOnSlideClickListener = onSlideClickListener;
    mSelected.setValue(new LinkedList<Integer>() {{
      offer(0);
      offer(0);
    }});
    mSelected.observe(activity, new Observer<List<Integer>>() {
      @Override
      public void onChanged(List<Integer> integers) {
        for (Integer pos : integers) {
          notifyItemChanged(pos);
        }
      }
    });
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
            .inflate(R.layout.view_home_sample_template_list_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    final SlideInfo item = mDataList.get(position);
    holder.mTextView.setText(DateUtils.getFormatDuration(item.duration));
    holder.mImageView.setSelected(false);
    if (!TextUtils.isEmpty(item.filePath)) {
      Glide.with(mActivity).load(item.filePath).into(holder.mImageView);
    }
    holder.mImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnSlideClickListener != null) {
          mOnSlideClickListener.onClick(item);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mDataList.size();
  }

  class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView mTextView;
    private AppCompatImageView mImageView;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mTextView = itemView.findViewById(R.id.home_template_item_text);
      mImageView = itemView.findViewById(R.id.home_template_item_image);
    }
  }

  public interface OnSlideClickListener {
    void onClick(SlideInfo item);
  }
}
