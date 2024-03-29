package com.quvideo.application.editor.effect;

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
import com.quvideo.application.AssetConstants;
import com.quvideo.application.DPUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.sound.AudioTemplate;
import com.quvideo.application.glidedecoder.EffectThumbParams;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import java.util.ArrayList;
import java.util.List;

public class EditEffectAdapter extends RecyclerView.Adapter<EditEffectAdapter.TemplateHolder> {

  private List<BaseEffect> mDataList = new ArrayList<>();

  private OnEffectClickListener mOnEffectClickListener;

  private int thumbWidth = DPUtils.dpToPixel(EditorApp.Companion.getInstance().getApp(), 60);
  private int thumbHeight = DPUtils.dpToPixel(EditorApp.Companion.getInstance().getApp(), 60);

  private int groupId;

  private int addOffset = 0;

  private int selectIndex = -1;

  private IQEWorkSpace mWorkSpace;

  public EditEffectAdapter(IQEWorkSpace workSpace, LifecycleOwner activity, int groupId,
      OnEffectClickListener effectlickListener) {
    this.mWorkSpace = workSpace;
    this.mOnEffectClickListener = effectlickListener;
    this.groupId = groupId;
    addOffset = QEGroupConst.GROUP_ID_BGMUSIC == groupId && mDataList.size() > 0 ? 0 : 1;
  }

  public void updateList(List<BaseEffect> dataList, int index) {
    this.mDataList = dataList;
    addOffset = QEGroupConst.GROUP_ID_BGMUSIC == groupId && mDataList.size() > 0 ? 0 : 1;
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
      holder.mImgFocus.setVisibility(View.GONE);
      holder.mImageView.setImageResource(R.drawable.edit_add_icon);
      holder.mImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mOnEffectClickListener != null) {
            mOnEffectClickListener.onClick(-1, null);
          }
        }
      });
      return;
    }
    boolean isSelected = position == selectIndex + addOffset;
    holder.mImgFocus.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    final BaseEffect item = mDataList.get(position - addOffset);
    if (item.groupId == QEGroupConst.GROUP_ID_COLLAGES && !TextUtils.isEmpty(item.mEffectPath)) {
      Glide.with(EditorApp.Companion.getInstance().getApp())
          .load(item.mEffectPath)
          .into(holder.mImageView);
    } else if (item.groupId == QEGroupConst.GROUP_ID_BGMUSIC
        || item.groupId == QEGroupConst.GROUP_ID_DUBBING) {
      // 音乐/音效
      int thumbRes = 0;
      boolean isFind = false;
      for (AudioTemplate audio : AssetConstants.TEST_MUSIC_TID) {
        if (audio.getAudioPath() != null && audio.getAudioPath().equals(item.mEffectPath)) {
          thumbRes = audio.getThumbnailResId();
          isFind = true;
          break;
        }
      }
      if (!isFind) {
        for (AudioTemplate audio : AssetConstants.TEST_DUB_TID) {
          if (audio.getAudioPath().equals(item.mEffectPath)) {
            thumbRes = audio.getThumbnailResId();
            isFind = true;
            break;
          }
        }
      }
      if (!TextUtils.isEmpty(item.mEffectPath) && !isFind) {
        thumbRes = R.drawable.editor_icon_tool_music_direct;
      }
      if (thumbRes != 0) {
        Glide.with(holder.mImageView)
            .load(thumbRes)
            .into(holder.mImageView);
      } else {
        XytInfo xytInfo = XytManager.getXytInfo(mWorkSpace.getStoryboardAPI().getThemeId());
        if (xytInfo != null) {
          final String filterPath = xytInfo.filePath;
          int thumbWidth = DPUtils.dpToPixel(holder.mImageView.getContext(), 60);
          int thumbHeight = DPUtils.dpToPixel(holder.mImageView.getContext(), 60);
          EffectThumbParams effectThumbParams =
              new EffectThumbParams(filterPath, thumbWidth, thumbHeight);
          Glide.with(holder.mImageView)
              .load(effectThumbParams)
              .into(holder.mImageView);
        }
      }
    } else if (item.groupId == QEGroupConst.GROUP_ID_RECORD) {
      Glide.with(holder.mImageView)
          .load(R.drawable.edit_icon_voice)
          .into(holder.mImageView);
    } else if (!TextUtils.isEmpty(item.mEffectPath)) {
      EffectThumbParams effectThumbParams =
          new EffectThumbParams(item.mEffectPath, thumbWidth, thumbHeight);
      Glide.with(holder.mImageView)
          .load(effectThumbParams)
          .into(holder.mImageView);
    }

    // 录音显示时长
    if (item.groupId == QEGroupConst.GROUP_ID_RECORD) {
      holder.mTvContent.setVisibility(View.VISIBLE);
      holder.mTvContent.setText(TimeFormatUtil.INSTANCE.formatTime(item.trimRange.getTimeLength()));
    } else {
      holder.mTvContent.setVisibility(View.GONE);
    }
    holder.mImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnEffectClickListener != null) {
          changeSelect(position);
          mOnEffectClickListener.onClick(selectIndex, item);
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

  public interface OnEffectClickListener {
    void onClick(int index, BaseEffect item);
  }
}
