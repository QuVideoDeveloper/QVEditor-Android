package com.quvideo.application.editor.effect.mask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.model.effect.EffectMaskInfo;
import java.util.ArrayList;
import java.util.List;

public class EffectMaskAdapter extends RecyclerView.Adapter<EffectMaskAdapter.TemplateHolder> {

  private List<MaskItem> mMaskItems = new ArrayList<>();

  private Context mContext;

  private BaseEffectMenuView mBaseMenuView;

  private OnItemSelectListener mOnItemClickListener;

  private EffectMaskInfo.MaskType selectType = EffectMaskInfo.MaskType.MASK_NONE;

  private boolean isEnable = false;

  public interface OnItemSelectListener {
    void onItemSelected(MaskItem template);
  }

  public EffectMaskAdapter(Context context, BaseEffectMenuView baseMenuView, List<MaskItem> maskItems) {
    this.mContext = context;
    this.mBaseMenuView = baseMenuView;
    this.mMaskItems = maskItems;
  }

  public void setSelectType(EffectMaskInfo.MaskType selectType, boolean reverse) {
    this.selectType = selectType;
    for (MaskItem item : mMaskItems) {
      if (item.maskType == selectType) {
        item.reverse = reverse;
      }
    }
    notifyDataSetChanged();
  }

  public void setEnable(boolean enable) {
    isEnable = enable;
  }

  public void setOnItemClickListener(OnItemSelectListener listener) {
    mOnItemClickListener = listener;
  }

  @NonNull
  @Override
  public TemplateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new TemplateHolder(
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_effect_mask_list_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    final MaskItem item = mMaskItems.get(position);
    boolean select = selectType == item.maskType;
    holder.mImageView.setSelected(select);
    if (item.reverse && select) {
      holder.mTextView.setText(R.string.mn_edit_mask_invert);
    } else {
      holder.mTextView.setText(item.titleResId);
    }
    Glide.with(holder.mImageView).load(item.iconResId).into(holder.mImageView);
    holder.mImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!isEnable) {
          ToastUtils.show(mContext, R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
          return;
        }
        if (mOnItemClickListener != null && item.maskType != selectType) {
          item.reverse = false;
          mOnItemClickListener.onItemSelected(item);
          selectType = item.maskType;
          notifyDataSetChanged();
        } else if (item.maskType != EffectMaskInfo.MaskType.MASK_NONE) {
          item.reverse = !item.reverse;
          mOnItemClickListener.onItemSelected(item);
          notifyDataSetChanged();
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mMaskItems.size();
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

  static class MaskItem {
    EffectMaskInfo.MaskType maskType;
    boolean reverse;
    int iconResId;
    int titleResId;

    public MaskItem(EffectMaskInfo.MaskType maskType, int iconResId, int titleResId) {
      this.maskType = maskType;
      this.iconResId = iconResId;
      this.titleResId = titleResId;
    }
  }
}
