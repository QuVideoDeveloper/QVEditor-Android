package com.quvideo.application.gallery;

import com.quvideo.application.gallery.model.MediaModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc Gallery status
 * @since 5/24/2019
 */
public class GalleryStatus {
  private static GalleryStatus instance;

  private boolean hasFileOrdered;
  private List<MediaModel> mPhotoList = new ArrayList<>();
  private List<MediaModel> mSelectedList = new ArrayList<>();

  private GalleryStatus() {
  }

  public static GalleryStatus getInstance() {
    if (instance == null) {
      instance = new GalleryStatus();
    }
    return instance;
  }

  public void setSelectedList(List<MediaModel> selectedList) {
    if (null == selectedList) {
      return;
    }
    this.mSelectedList.clear();
    this.mSelectedList.addAll(selectedList);
  }

  public List<MediaModel> getSelectedList() {
    return mSelectedList;
  }

  public List<MediaModel> getPhotoList() {
    return mPhotoList;
  }

  public synchronized void setPhotoList(List<MediaModel> photoList) {
    if (null == photoList) {
      return;
    }
    this.mPhotoList.clear();
    this.mPhotoList.addAll(photoList);
  }

  public boolean hasFileOrdered() {
    return hasFileOrdered;
  }

  public void setFileOrdered(boolean ordered) {
    this.hasFileOrdered = ordered;
  }

  public void reset() {
    hasFileOrdered = false;
    if (mPhotoList != null) {
      mPhotoList.clear();
    }
    if (mSelectedList != null) {
      mSelectedList.clear();
    }
  }
}
