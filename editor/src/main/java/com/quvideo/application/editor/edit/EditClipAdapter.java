package com.quvideo.application.editor.edit;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DeviceSizeUtil;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by santa on 2020-04-17.
 */
public class EditClipAdapter extends RecyclerView.Adapter<EditClipAdapter.TemplateHolder> {

  private IQEWorkSpace workSpace;
  private int clipSize = 0;

  private int selectIndex = 0;

  private OnClipAddListener onAddClipListener;

  private List<ClipData> mClipData = new ArrayList<>();

  private HashMap<String, Bitmap> thumbnailCache = new HashMap<>();

  private HashMap<String, String> loadingCache = new HashMap<>();

  private int thumbnailSize = (int) DeviceSizeUtil.dpToPixel(60f);
  private int thumbRoundedCorners = (int) DeviceSizeUtil.dpToPixel(4);

  public interface OnClipAddListener {
    void onClipAdd();
  }

  EditClipAdapter(IQEWorkSpace workSpace) {
    this.workSpace = workSpace;
  }

  int getSelClipIndex() {
    return selectIndex;
  }

  void updateClipList() {
    mClipData = workSpace.getClipAPI().getClipList();
    clipSize = mClipData.size();
    notifyDataSetChanged();
  }

  void release() {
    mClipData.clear();
    notifyDataSetChanged();
    Set<String> keys = thumbnailCache.keySet();
    Iterator<String> iterator = keys.iterator();
    for (; iterator.hasNext(); ) {
      String key = iterator.next();
      Bitmap bitmap = thumbnailCache.get(key);
      if (bitmap != null && !bitmap.isRecycled()) {
        bitmap.recycle();
      }
    }
    thumbnailCache.clear();
  }

  void setOnAddClipListener(OnClipAddListener listener) {
    onAddClipListener = listener;
  }

  private void loadCacheBitmap(int position, final String clipKey) {
    if (!loadingCache.containsKey(clipKey)) {
      loadingCache.put(clipKey, clipKey);
      Observable.just(true)
          .subscribeOn(Schedulers.newThread())
          .observeOn(Schedulers.newThread())
          .map(aBoolean -> {
            return workSpace.getClipThumbnail(position, thumbnailSize, thumbnailSize);
          })
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new io.reactivex.Observer<Bitmap>() {
            @Override public void onSubscribe(Disposable d) {
            }

            @Override public void onNext(Bitmap bitmap) {
              if (bitmap != null) {
                thumbnailCache.put(clipKey, bitmap);
                notifyDataSetChanged();
              }
              loadingCache.remove(clipKey);
            }

            @Override public void onError(Throwable e) {
              loadingCache.remove(clipKey);
            }

            @Override public void onComplete() {
            }
          });
    }
  }

  @NonNull
  @Override
  public TemplateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new TemplateHolder(
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_edit_clip_item, parent, false));
  }

  void changeSelect(int index) {
    int oldIndex = selectIndex;
    selectIndex = index;
    if (oldIndex >= 0 && oldIndex < getItemCount()) {
      notifyItemChanged(oldIndex);
    }
    notifyItemChanged(selectIndex);
  }

  @Override public int getItemViewType(int position) {
    if (position == clipSize) {
      return 1;
    } else {
      return 2;
    }
  }

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    boolean isSelected = position == selectIndex;
    if (position == clipSize) {
      holder.mImageView.setImageBitmap(null);
      holder.mImageView.setBackgroundResource(R.drawable.edit_icon_add_clip);
    } else {
      holder.mImageView.setBackground(null);
      ClipData clipData = mClipData.get(position);
      Bitmap bitmap = thumbnailCache.get(clipData.getUniqueId());
      if (bitmap != null) {
        Glide.with(holder.mImageView)
            .load(bitmap)
            .apply(RequestOptions.bitmapTransform(new RoundedCorners(thumbRoundedCorners)))
            .into(holder.mImageView);
      } else {
        loadCacheBitmap(position, clipData.getUniqueId());
      }
      holder.mImageFocus.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    }
    holder.mImageView.setOnClickListener(v -> {
      if (position < clipSize) {
        changeSelect(position);
        if (workSpace != null
            && workSpace.getPlayerAPI() != null
            && workSpace.getPlayerAPI().getPlayerControl() != null) {
          ClipData item = workSpace.getClipAPI().getClipByIndex(position);
          int time = 0;
          if (item != null) {
            time = item.getDestRange().getPosition();
          }
          VeRange preRange = null;
          if (position != 0) {
            ClipData preItem = workSpace.getClipAPI().getClipByIndex(position - 1);
            if (preItem != null) {
              preRange = preItem.getDestRange();
            }
          }
          if (preRange != null) {
            time = (time + preRange.getLimitValue()) / 2;
          }
          workSpace.getPlayerAPI().getPlayerControl().seek(time);
        }
      } else if (onAddClipListener != null) {
        onAddClipListener.onClipAdd();
      }
    });
  }

  @Override
  public int getItemCount() {
    return clipSize + 1;
  }

  static class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatImageView mImageView;
    private AppCompatImageView mImageFocus;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mImageView = itemView.findViewById(R.id.home_template_item_image);
      mImageFocus = itemView.findViewById(R.id.imgFocus);
    }
  }
}
