package com.quvideo.application.gallery.media.adapter;

import android.text.TextUtils;
import android.util.SparseIntArray;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.gallery.R;
import com.quvideo.application.gallery.adapterhelper.BaseMultiItemQuickAdapter;
import com.quvideo.application.gallery.media.adapter.holder.GalleryViewHolder;
import com.quvideo.application.gallery.media.decoration.utils.FullSpanUtil;
import com.quvideo.application.gallery.model.MediaModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MediaAdapter
    extends BaseMultiItemQuickAdapter<PinnedHeaderEntity<MediaModel>, GalleryViewHolder> {

  public static final int TYPE_HEADER = 1;
  public static final int TYPE_DATA = 2;
  public static final int TYPE_FOOTER = 3;

  public MediaAdapter(List<PinnedHeaderEntity<MediaModel>> data) {
    super(data);

    addItemType(TYPE_HEADER, R.layout.gallery_media_header_view_layout);
    addItemType(TYPE_DATA, R.layout.gallery_media_item_view_layout);
    addItemType(TYPE_FOOTER, R.layout.gallery_media_item_footer_layout);
  }

  @Override protected void convert(@NonNull GalleryViewHolder helper,
      PinnedHeaderEntity<MediaModel> item) {

    helper.setData(item);
  }

  @Override protected void convertPayloads(@NonNull GalleryViewHolder helper,
      PinnedHeaderEntity<MediaModel> item, @NonNull List<Object> payloads) {
    if (payloads.isEmpty()) {
      convert(helper, item);
    } else {
      List<MediaItemUpdateBean> list = new ArrayList<>();
      for (Object object : payloads) {
        if (object instanceof MediaItemUpdateBean) {
          list.add((MediaItemUpdateBean) object);
        }
      }
      updateItemData(helper, list);
    }
  }

  @Override public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, TYPE_HEADER, TYPE_FOOTER);
  }

  private void updateItemData(@NonNull GalleryViewHolder helper,
      List<MediaItemUpdateBean> list) {
    Integer orderUpdateEntity = null;
    for (MediaItemUpdateBean bean : list) {
      if (bean.getOrderUpdateBean() != null) {
        orderUpdateEntity = bean.getOrderUpdateBean();
      }
    }
    if (orderUpdateEntity != null) {
      helper.updateOrder(orderUpdateEntity);
    }
  }

  public void clearOrder(Map<MediaModel, SparseIntArray> orderMediaMap) {
    if (orderMediaMap == null || orderMediaMap.isEmpty()) {
      return;
    }
    for (SparseIntArray sparseArray : orderMediaMap.values()) {
      if (sparseArray != null && sparseArray.size() > 0) {
        //int order = sparseArray.keyAt(0);
        int adapterPosition = sparseArray.valueAt(0);
        updateItemDataOrder(adapterPosition, 0);
        MediaItemUpdateBean updateBean =
            new MediaItemUpdateBean.Builder().orderUpdateBean(0).build();
        notifyItemChanged(adapterPosition, updateBean);
      }
    }
  }

  public void updateOrder(Map<MediaModel, SparseIntArray> orderMediaMap) {
    if (orderMediaMap == null || orderMediaMap.isEmpty()) {
      return;
    }
    for (SparseIntArray sparseArray : orderMediaMap.values()) {
      if (sparseArray != null && sparseArray.size() > 0) {
        int order = sparseArray.keyAt(0);
        int adapterPosition = sparseArray.valueAt(0);
        updateItemDataOrder(adapterPosition, order);
        MediaItemUpdateBean updateBean =
            new MediaItemUpdateBean.Builder().orderUpdateBean(order).build();
        notifyItemChanged(adapterPosition, updateBean);
      }
    }
  }

  private void updateItemDataOrder(int position, int order) {
    if (mData == null || mData.isEmpty()) {
      return;
    }
    PinnedHeaderEntity<MediaModel> entity = getItem(position);
    if (entity != null) {
      MediaModel entityData = entity.getData();
      if (entityData != null) {
        entityData.setOrder(order);
      }
    }
  }

  public int getMediaPosition(MediaModel model) {
    if (model == null || TextUtils.isEmpty(model.getFilePath())) {
      return -1;
    }
    int position = -1;
    if (mData != null && !mData.isEmpty()) {
      for (int i = 0; i < mData.size(); i++) {
        PinnedHeaderEntity<MediaModel> entity = mData.get(i);
        MediaModel entityData = entity.getData();
        if (entityData == null) {
          //header item
          continue;
        }
        if (model.getFilePath().equals(entityData.getFilePath())) {
          position = i;
          break;
        }
      }
    }
    return position;
  }
}
