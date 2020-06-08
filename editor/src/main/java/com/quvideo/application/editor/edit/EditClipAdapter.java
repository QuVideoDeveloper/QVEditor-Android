package com.quvideo.application.editor.edit;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.DPUtils;
import com.quvideo.application.editor.R;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by santa on 2020-04-17.
 */
public class EditClipAdapter extends RecyclerView.Adapter<EditClipAdapter.TemplateHolder> {

  private IQEWorkSpace workSpace;
  private int clipSize = 0;

  private int selectIndex = 0;

  private OnClipAddListener onAddClipListener;

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
    clipSize = workSpace.getClipAPI().getClipList().size();
    notifyDataSetChanged();
  }

  void setOnAddClipListener(OnClipAddListener listener) {
    onAddClipListener = listener;
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

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    boolean isSelected = position == selectIndex;

    if (position == clipSize) {
      holder.mImageView.setImageBitmap(null);
      holder.mImageView.setBackgroundResource(R.drawable.edit_icon_add_clip);
      holder.mImageView.setPadding(0, 0, 0, 0);
    } else {
      holder.mImageView.setBackgroundResource(R.drawable.cam_sel_filter_item_bg);
      int dp2 = DPUtils.dpToPixel(holder.itemView.getContext(), 2);
      holder.mImageView.setPadding(dp2, dp2, dp2, dp2);
      Observable.just(true)
          .subscribeOn(Schedulers.newThread())
          .observeOn(Schedulers.newThread())
          .map(aBoolean -> {
            int size = DPUtils.dpToPixel(holder.itemView.getContext(), 60);
            return workSpace.getClipThumbnail(position, size, size);
          })
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new io.reactivex.Observer<Bitmap>() {
            @Override public void onSubscribe(Disposable d) {

            }

            @Override public void onNext(Bitmap bitmap) {
              holder.mImageView.setImageBitmap(bitmap);
            }

            @Override public void onError(Throwable e) {

            }

            @Override public void onComplete() {

            }
          });

      holder.mImageView.setSelected(isSelected);
    }
    holder.mImageView.setOnClickListener(v -> {
      if (position < clipSize) {
        changeSelect(position);
        if (workSpace != null && workSpace.getPlayerAPI() != null && workSpace.getPlayerAPI().getPlayerControl() != null) {
          int time = workSpace.getClipAPI().getClipRealStartPosition(position);
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

  class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatImageView mImageView;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mImageView = itemView.findViewById(R.id.home_template_item_image);
    }
  }
}
