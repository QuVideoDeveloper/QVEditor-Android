package com.quvideo.application.gallery.media.adapter;

import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.adapterhelper.BaseQuickAdapter;
import com.quvideo.application.gallery.adapterhelper.BaseViewHolder;
import com.quvideo.application.gallery.model.MediaGroupItem;
import com.quvideo.application.gallery.utils.GalleryUtil;
import com.quvideo.application.gallery.widget.RoundImageView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by zhengjunfei on 2019/9/9
 */
public class FolderListAdapter extends BaseQuickAdapter<MediaGroupItem, BaseViewHolder> {
  public static final int TYPE_SINGLE = 1;
  public static final int TYPE_MULTI = 2;
  private Map<String, Integer> mMultiSelectedName = new HashMap<>();
  private int mLastCheckPos = -1;
  private int mType;

  public FolderListAdapter(@Nullable List<MediaGroupItem> data, int type) {
    super(R.layout.gallery_media_layout_folder_list_item, data);
    this.mType = type;
  }

  @Override protected void convert(@NonNull BaseViewHolder holder, MediaGroupItem item) {
    RoundImageView img = holder.getView(R.id.folder_item_cover_img);

    if (!TextUtils.isEmpty(item.getCoverPhotoUrl())) {
      GalleryUtil.loadCover(img.getContext(), img,
          R.drawable.gallery_media_folder_item_cover_def_icon, item.getCoverPhotoUrl());
    } else if (null != item.mediaItemList
        && item.mediaItemList.size() > 0
        && null != item.mediaItemList.get(0)) {
      GalleryUtil.loadCover(img.getContext(), img,
          R.drawable.gallery_media_folder_item_cover_def_icon, item.mediaItemList.get(0).path);
    } else {
      GalleryUtil.loadCover(img.getContext(), img,
          R.drawable.gallery_media_folder_item_cover_def_icon, null);
    }

    holder.setText(R.id.folder_item_name, item.getStrGroupDisplayName());
    String sizeText = null != item.mediaItemList ? String.valueOf(item.mediaItemList.size()) : "0";
    holder.setText(R.id.folder_item_number, sizeText);

    if (TYPE_MULTI == mType) {
      holder.getView(R.id.folder_item_checkbox)
          .setVisibility(mMultiSelectedName.containsKey(item.strGroupDisplayName) ? View.VISIBLE
              : View.INVISIBLE);
      holder.getView(R.id.folder_item_layout_root)
          .setSelected(mMultiSelectedName.containsKey(item.strGroupDisplayName));
    } else {
      holder.getView(R.id.folder_item_checkbox)
          .setVisibility(
              mLastCheckPos == holder.getAdapterPosition() ? View.VISIBLE : View.INVISIBLE);
      holder.getView(R.id.folder_item_layout_root)
          .setSelected(mLastCheckPos == holder.getAdapterPosition());
    }
  }

  public void updateItemCheckState(int pos, boolean isClearLastCheck) {
    if (null == getData() || getData().size() - 1 < pos || null == getItem(pos) || pos < 0) {
      return;
    }
    int lastCheckPos = mLastCheckPos;
    boolean isCheck = -1 == mLastCheckPos ? true : mLastCheckPos == pos ? false : true;
    changeItemCheckState(pos, isCheck);//改变本条item的选中状态
    if (isClearLastCheck && -1 != lastCheckPos && pos != lastCheckPos) {
      changeItemCheckState(lastCheckPos, false);//清除上次选中item的选中状态
    }
    mLastCheckPos = isCheck ? pos : -1;
  }

  public void changeItemCheckState(int pos, boolean isCheck) {
    View layoutItemRoot = getViewByPosition(pos, R.id.folder_item_layout_root);
    View itemCheckView = getViewByPosition(pos, R.id.folder_item_checkbox);
    if (null == layoutItemRoot || null == itemCheckView) {
      return;
    }

    MediaGroupItem info = getItem(pos);
    if (TYPE_MULTI == mType) {
      layoutItemRoot.setSelected(isCheck);
      itemCheckView.setVisibility(isCheck ? View.VISIBLE : View.INVISIBLE);
      changeSelectData(info);
    } else {
      layoutItemRoot.setSelected(isCheck);
      itemCheckView.setVisibility(isCheck ? View.VISIBLE : View.INVISIBLE);
    }
  }

  private void changeSelectData(MediaGroupItem info) {
    if (mMultiSelectedName.containsKey(info.strGroupDisplayName)) {
      mMultiSelectedName.remove(info.strGroupDisplayName);
    } else {
      mMultiSelectedName.put(info.strGroupDisplayName, 1);
    }
  }

  public boolean posIsSingleCheck(int pos) {
    return mLastCheckPos == pos ? true : false;
  }

  public boolean posIsNultiCheck(String name) {
    return null != mMultiSelectedName
        && !TextUtils.isEmpty(name)
        && mMultiSelectedName.containsKey(name);
  }
}
