package com.quvideo.application.editor.effect.plugin;

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
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.model.effect.SubPluginInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EffectPluginAdapter extends RecyclerView.Adapter<EffectPluginAdapter.TemplateHolder> {

  private List<SubPluginInfo> mDataList = new ArrayList<>();

  private OnPluginClickListener mOnPluginClickListener;

  private int thumbWidth = DPUtils.dpToPixel(EditorApp.Companion.getInstance().getApp(), 60);
  private int thumbHeight = DPUtils.dpToPixel(EditorApp.Companion.getInstance().getApp(), 60);

  private static final int addOffset = 1;

  private int selectIndex = -1;

  public EffectPluginAdapter(LifecycleOwner activity, OnPluginClickListener effectlickListener) {
    this.mOnPluginClickListener = effectlickListener;
  }

  public void updateList(List<SubPluginInfo> dataList, int index) {
    this.mDataList = dataList;
    if (index < 0 || index >= dataList.size()) {
      if (selectIndex < 0 || selectIndex >= dataList.size()) {
        selectIndex = dataList.size() > 0 ? 0 : -1;
      }
    } else {
      selectIndex = index;
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
    if (addOffset > 0 && position == 0) {
      holder.mImageView.setSelected(false);
      holder.mImageView.setImageResource(R.drawable.edit_add_icon);
      holder.mImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mOnPluginClickListener != null) {
            mOnPluginClickListener.onClick(-1, null);
          }
        }
      });
      holder.mImgFocus.setVisibility(View.GONE);
      holder.mTvContent.setText("");
      return;
    }
    boolean isSelected = position == selectIndex + addOffset;
    holder.mImgFocus.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    final SubPluginInfo item = mDataList.get(position - addOffset);
    Glide.with(holder.mImageView)
        .load(R.drawable.editor_icon_collage_tool_framework)
        .into(holder.mImageView);

    XytInfo xytInfo = XytManager.getXytInfo(item.subPluginPath);
    holder.mTvContent.setVisibility(View.VISIBLE);
    if (xytInfo != null) {
      holder.mTvContent.setText(xytInfo.getTitle(Locale.getDefault()));
    } else {
      holder.mTvContent.setText("plugin");
    }
    holder.mImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnPluginClickListener != null) {
          changeSelect(position);
          mOnPluginClickListener.onClick(selectIndex, item);
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

  public interface OnPluginClickListener {
    void onClick(int index, SubPluginInfo item);
  }
}
