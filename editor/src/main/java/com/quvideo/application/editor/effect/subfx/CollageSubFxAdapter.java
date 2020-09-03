package com.quvideo.application.editor.effect.subfx;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.quvideo.application.DPUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.glidedecoder.EffectThumbParams;
import com.quvideo.mobile.engine.model.effect.EffectSubFx;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by santa on 2020-04-17.
 */
public class CollageSubFxAdapter extends RecyclerView.Adapter<CollageSubFxAdapter.TemplateHolder> {

  private List<EffectSubFx> mDataList = new ArrayList<>();

  private OnSubFxClickListener mOnSubFxClickListener;

  private int thumbWidth = DPUtils.dpToPixel(EditorApp.Companion.getInstance().getApp(), 60);
  private int thumbHeight = DPUtils.dpToPixel(EditorApp.Companion.getInstance().getApp(), 60);

  private int groupId;

  private final int addOffset = 1;

  private int selectIndex = -1;

  private IQEWorkSpace mWorkSpace;

  public CollageSubFxAdapter(IQEWorkSpace workSpace, LifecycleOwner activity, int groupId,
      OnSubFxClickListener effectlickListener) {
    this.mWorkSpace = workSpace;
    this.mOnSubFxClickListener = effectlickListener;
    this.groupId = groupId;
  }

  public void updateList(List<EffectSubFx> dataList) {
    if (dataList == null) {
      dataList = new ArrayList<>();
    }
    this.mDataList = dataList;
    if (selectIndex >= dataList.size() || selectIndex < 0) {
      selectIndex = dataList.size() > 0 ? 0 : -1;
    }
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public TemplateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new TemplateHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.view_edit_clip_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    if (position == 0) {
      holder.mImageView.setSelected(false);
      holder.mImgFocus.setVisibility(View.GONE);
      holder.mImageView.setImageResource(R.drawable.edit_add_icon);
      holder.mImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mOnSubFxClickListener != null) {
            mOnSubFxClickListener.onClick(-1, null);
          }
        }
      });
      return;
    }
    boolean isSelected = position == selectIndex + addOffset;
    holder.mImgFocus.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    final EffectSubFx item = mDataList.get(position - addOffset);
    if (!TextUtils.isEmpty(item.getSubFxPath())) {
      EffectThumbParams effectThumbParams =
          new EffectThumbParams(item.getSubFxPath(), thumbWidth, thumbHeight);
      Glide.with(holder.mImageView)
          .load(effectThumbParams)
          .into(holder.mImageView);
    }

    // 录音显示时长
    holder.mTvContent.setVisibility(View.GONE);
    holder.mImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnSubFxClickListener != null) {
          changeSelect(position);
          mOnSubFxClickListener.onClick(selectIndex, item);
        }
      }
    });
  }

  private void changeSelect(int index) {
    int oldIndex = selectIndex;
    selectIndex = index - addOffset;
    if (oldIndex >= 0 && oldIndex < getItemCount()) {
      notifyItemChanged(oldIndex + addOffset);
    }
    notifyItemChanged(selectIndex + addOffset);
  }

  public void setSelectIndex(int index) {
    changeSelect(index + 1);
  }

  public int getSelectIndex() {
    return selectIndex;
  }

  @Override
  public int getItemCount() {
    return mDataList.size() + addOffset;
  }

  class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatImageView mImageView;
    private AppCompatImageView mImgFocus;
    private AppCompatTextView mTvContent;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mImageView = itemView.findViewById(R.id.home_template_item_image);
      mImgFocus = itemView.findViewById(R.id.imgFocus);
      mTvContent = itemView.findViewById(R.id.tvContent);
    }
  }

  public interface OnSubFxClickListener {
    void onClick(int index, EffectSubFx item);
  }
}
