package com.quvideo.application.gallery.preview.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.utils.GalleryUtil;
import com.quvideo.application.gallery.widget.photo.PhotoView;
import com.quvideo.application.utils.image.ImageLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by zhengjunfei on 2019/9/12
 */
public class PhotoPagerAdapter extends PagerAdapter {
  private PhotoView mCurrentView;

  private List<MediaModel> mPhotoList = new ArrayList<>();
  private PhotoCropperCallback mCallback;

  public PhotoPagerAdapter(PhotoCropperCallback callback) {
    this.mCallback = callback;
  }

  public void setData(List<MediaModel> list) {
    if (null != list) {
      mPhotoList.clear();
      mPhotoList.addAll(list);
      notifyDataSetChanged();
    }
  }

  @Override public int getCount() {
    return mPhotoList.size();
  }

  @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
    return (view == o);
  }

  @Override public void setPrimaryItem(@NonNull ViewGroup container, int position,
      @NonNull Object object) {
    if (object instanceof PhotoView) {
      mCurrentView = (PhotoView) object;
    }
    refreshImageView(mCurrentView);
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    if (object instanceof PhotoView) {
      ((PhotoView) object).uninit();
    }
    container.removeView((View) object);
  }

  @Override public int getItemPosition(@NonNull Object object) {
    return POSITION_NONE;
  }

  @NonNull @Override
  public Object instantiateItem(@NonNull ViewGroup container, int position) {
    PhotoView photoView = new PhotoView(container.getContext());
    photoView.setCropViewEnable(false);
    if (position >= 0 && position < mPhotoList.size()) {
      MediaModel itemInfo = mPhotoList.get(position);
      photoView.setOnClickListener(v -> {
        //...
        if (null != mCallback) {
          mCallback.onPhotoItemClick();
        }
      });
      container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
      try {
        GalleryUtil.loadCoverFitCenter(photoView.getContext(),
            photoView, R.drawable.gallery_default_pic_cover, itemInfo.getFilePath());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } else {
      ImageLoader.loadImage(R.drawable.gallery_default_pic_cover, photoView);
    }
    return photoView;
  }

  public PhotoView getCurrentPhotoView() {
    return mCurrentView;
  }

  private void refreshImageView(PhotoView view) {
    Drawable drawable = view.getDrawable();
    if (drawable != null) {
      Drawable drawable2 = drawable;
      if (drawable instanceof TransitionDrawable) {
        TransitionDrawable transDrawable = (TransitionDrawable) drawable;
        drawable2 = transDrawable.getDrawable(transDrawable.getNumberOfLayers() - 1);
      }
      if (drawable2 instanceof BitmapDrawable) {
        view.setImageDrawable(drawable2);
      }
    }
  }

  public interface PhotoCropperCallback {
    void onPhotoItemClick();
  }
}
