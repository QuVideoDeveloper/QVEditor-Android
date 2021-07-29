package com.quvideo.application.gallery.board.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.utils.rx.RxViewUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaBoardAdapter extends RecyclerView.Adapter<MediaBoardAdapter.ClipViewHolder>
    implements DragItemTouchCallback.ItemTouchAdapter {
  private Context context;
  private boolean bInDragging;
  private ArrayList<MediaModel> missionModelList = new ArrayList<>();

  private MediaBoardAdapterListener listener;
  public int choosePos = 0;

  public MediaBoardAdapter(Context context) {
    this.context = context;
  }

  public void setListener(MediaBoardAdapterListener listener) {
    this.listener = listener;
  }

  public void dragStateChanged(@NonNull View view, final boolean dragging) {
    if (bInDragging == dragging) {
      return;
    }
    bInDragging = dragging;
    view.post(() -> {
      MediaBoardItemUpdate itemUpdate =
          new MediaBoardItemUpdate.Builder().showOrderEntity(dragging).build();
      notifyItemRangeChanged(0, getItemCount(), itemUpdate);
    });
  }

  public void addMissionItem(MediaModel model) {
    this.missionModelList.add(model);
    if (getItemCount() > 1) {
      notifyItemChanged(getItemCount() - 2);
    }
    notifyItemInserted(getItemCount() - 1);
    //notifyDataSetChanged();
  }

  public void replaceMissionItem(MediaModel model) {
    if (null == model
        || choosePos < 0
        || null == missionModelList
        || missionModelList.size() - 1 < choosePos
        || null == missionModelList.get(choosePos)) {
      return;
    }
    MediaModel mediaModel = replaceMediaModel(missionModelList.get(choosePos), model);
    missionModelList.remove(choosePos);
    missionModelList.add(choosePos, mediaModel);
    notifyItemChanged(choosePos);
  }

  private MediaModel replaceMediaModel(MediaModel oldModel, MediaModel newModel) {
    oldModel.setOrder(newModel.getOrder());
    oldModel.setFilePath(newModel.getFilePath());
    oldModel.setCropped(newModel.isCropped());
    oldModel.setCropRect(newModel.getCropRect());
    oldModel.setRangeInFile(newModel.getRangeInFile());
    oldModel.setRawFilepath(newModel.getRawFilepath());
    oldModel.setRotation(newModel.getRotation());
    oldModel.setDuration(newModel.getDuration());
    oldModel.setSourceType(newModel.getSourceType());
    return oldModel;
  }

  public void addMissionItem(List<MediaModel> modelList) {
    this.missionModelList.addAll(modelList);
    //notifyItemInserted(getItemCount() - 1);
    notifyDataSetChanged();
  }

  public boolean removeMissionItem(int position) {
    if (position >= 0 && position < missionModelList.size()) {
      missionModelList.remove(position);
      notifyItemRemoved(position);
      return true;
    }
    return false;
  }

  public void clearMissionItem() {
    this.missionModelList.clear();
    notifyDataSetChanged();
  }

  public ArrayList<MediaModel> getMissionList() {
    return missionModelList;
  }

  public int getMediaPosition(MediaModel model) {
    if (model == null || missionModelList == null || missionModelList.isEmpty()) {
      return -1;
    }
    int position = -1;
    for (int i = 0; i < missionModelList.size(); i++) {
      MediaModel mediaModel = missionModelList.get(i);
      if (mediaModel != null && model.getFilePath().equals(mediaModel.getFilePath())) {
        position = i;
        break;
      }
    }
    return position;
  }

  @NonNull @Override
  public ClipViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    BaseMediaBoardItemView view = new MediaBoardItemView(context);
    return new ClipViewHolder(view);
  }

  @Override public void onBindViewHolder(@NonNull ClipViewHolder holder, int position,
      @NonNull List<Object> payloads) {
    if (payloads.isEmpty()) {
      onBindViewHolder(holder, position);
    } else {
      List<MediaBoardItemUpdate> list = new ArrayList<>();
      for (Object object : payloads) {
        if (object instanceof MediaBoardItemUpdate) {
          list.add((MediaBoardItemUpdate) object);
        }
      }
      updateItemData(holder, list);
    }
  }

  @Override public void onBindViewHolder(@NonNull ClipViewHolder viewHolder, int position) {
    MediaModel mediaModel = missionModelList.get(position);
    if (mediaModel == null || null == context || ((Activity) context).isFinishing()) {
      return;
    }
    resetMediaViewType(mediaModel, position);
    viewHolder.itemView.update(mediaModel, position);
    viewHolder.itemView.updateOrderState(position + 1, bInDragging);
    ImageButton itemViewDeleteBtn = viewHolder.itemView.getDeleteBtn();
    if (itemViewDeleteBtn != null) {
      RxViewUtil.setOnClickListener(view -> {
        if (listener != null) {
          listener.onItemDeleted(viewHolder.getAdapterPosition());
        }
      }, itemViewDeleteBtn);
    }
    RxViewUtil.setOnClickListener(view -> {
      updateItemChooseState(position);
    }, viewHolder.itemView);
  }

  /**
   * 给MediaModel的mediaViewType赋值
   */
  private void resetMediaViewType(MediaModel mediaModel, int pos) {
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    if (null == settings || null == mediaModel || pos < 0 || choosePos < 0) {
      return;
    }
    if (pos != choosePos) {
      mediaModel.setMediaViewType(GalleryDef.VIEW_TYPE_SOURCE_NORMAL);
    } else {
      mediaModel.setMediaViewType(GalleryDef.VIEW_TYPE_SOURCE_SELECT);
    }
  }

  private void updateItemData(@NonNull ClipViewHolder holder,
      @NonNull List<MediaBoardItemUpdate> list) {
    if (list.isEmpty()) {
      return;
    }
    Boolean showOrderEntity = null;
    for (MediaBoardItemUpdate item : list) {
      if (item.getShowOrderEntity() != null) {
        showOrderEntity = item.getShowOrderEntity();
      }
    }
    if (showOrderEntity != null) {
      holder.itemView.updateOrderState(holder.getAdapterPosition() + 1, showOrderEntity);
    }
  }

  /**
   * 更新item的选中状态
   */
  public void updateItemChooseState(int pos) {
    if (null == getMissionList()
        || pos < 0
        || choosePos == pos
        || getItemCount() <= 0
        || getItemCount() - 1 < choosePos
        || getItemCount() - 1 < pos) {
      return;
    }

    if (choosePos >= 0) {
      notifyItemChanged(choosePos);
    }

    notifyItemChanged(pos);
    this.choosePos = pos;
  }

  @Override public int getItemCount() {
    return missionModelList.size();
  }

  @Override public void onMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(missionModelList, i, i + 1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(missionModelList, i, i - 1);
      }
    }

    notifyItemMoved(fromPosition, toPosition);
  }

  @Override public void onSwiped(int position) {
    missionModelList.remove(position);
    notifyItemRemoved(position);
  }

  class ClipViewHolder extends RecyclerView.ViewHolder {
    BaseMediaBoardItemView itemView;

    ClipViewHolder(@NonNull BaseMediaBoardItemView itemView) {
      super(itemView);
      this.itemView = itemView;
    }
  }

  public interface MediaBoardAdapterListener {
    void onItemDeleted(int position);
  }
}
