package com.quvideo.application.gallery.controller;

import android.app.Activity;
import android.content.Context;
import com.quvideo.application.gallery.media.adapter.PinnedHeaderEntity;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaGroupItem;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.utils.mvp.MvpView;
import java.util.List;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/2/2019
 */
public interface IMedia extends MvpView {

  Activity getActivity();

  Context getContext();

  int getMediaOrder(MediaModel model);

  default int getSourceType() {
    return GalleryDef.TYPE_UNKNOWN;
  }

  default void onMediaGroupReady(MediaGroupItem groupItem) {

  }

  default void onMediaListReady(List<PinnedHeaderEntity<MediaModel>> entityList) {

  }

  default void onMediaGroupListReady(List<MediaGroupItem> groupItemList) {

  }
}
