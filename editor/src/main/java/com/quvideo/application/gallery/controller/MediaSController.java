package com.quvideo.application.gallery.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.MediaConfig;
import com.quvideo.application.gallery.enums.BROWSE_TYPE;
import com.quvideo.application.gallery.enums.GROUP_MEDIA_TYPE;
import com.quvideo.application.gallery.enums.MediaType;
import com.quvideo.application.gallery.manager.MediaManager;
import com.quvideo.application.gallery.media.adapter.MediaAdapter;
import com.quvideo.application.gallery.media.adapter.PinnedHeaderEntity;
import com.quvideo.application.gallery.model.ExtMediaItem;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaGroupItem;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.utils.mvp.BaseController;
import com.quvideo.application.utils.rx.RetryWithDelay;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MediaSController extends BaseController<IMedia> {

  private static final String TAG = "MediaSController";

  private CompositeDisposable compositeDisposable;

  /**
   * local media data manager
   */
  private MediaManager mLocalManager;

  public MediaSController(IMedia mvpView, boolean root) {
    super(mvpView);
    compositeDisposable = new CompositeDisposable();

    if (root) {
      GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
      MediaConfig.init(settings.getExportVideoPath(), settings.getCameraVideoPath());
      MediaConfig.setAppCountryCode(settings.getCountryCode());
      MediaConfig.GIF_AVAILABLE = GallerySettings.GIF_AVAILABLE;
      //local media
      prepareLocalTotalMedia(mvpView.getContext());
    }
  }

  private void prepareLocalTotalMedia(Context context) {
    Log.i("zjf FolderListData", "  MediaSController prepareLocalTotalMedia");
    getLocalMediaManager(context, GalleryDef.TYPE_UNKNOWN, 0).observeOn(
        AndroidSchedulers.mainThread()).subscribe(new Observer<MediaManager>() {
      @Override public void onSubscribe(Disposable d) {
        compositeDisposable.add(d);
      }

      @Override public void onNext(MediaManager mediaManager) {
        mLocalManager = mediaManager;
        Log.i("zjf FolderListData", "  MediaSController prepareLocalTotalMedia : onNext");
        if (null == getMvpView() || null == mLocalManager) {
          return;
        }
        getMvpView().onMediaGroupListReady(mLocalManager.getGroupItemList());
      }

      @Override public void onError(Throwable e) {
        Log.i("zjf FolderListData",
            "  MediaSController prepareLocalTotalMedia : onError , msg = " + e.getMessage());
      }

      @Override public void onComplete() {
        Log.i("zjf FolderListData", "  MediaSController prepareLocalTotalMedia : onComplete");
      }
    });
  }

  private Observable<MediaManager> getLocalMediaManager(final Context context, int sourceType,
      long timeDelay) {
    return Observable.just(true)
        .subscribeOn(Schedulers.io())
        .delay(timeDelay, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())
        .map(aBoolean -> {
          if (mLocalManager == null) {
            MediaManager mediaManager = new MediaManager();

            BROWSE_TYPE browseType = BROWSE_TYPE.PHOTO_AND_VIDEO;
            if (sourceType == GalleryDef.TYPE_VIDEO) {
              browseType = BROWSE_TYPE.VIDEO;
            } else if (sourceType == GalleryDef.TYPE_PHOTO) {
              browseType = BROWSE_TYPE.PHOTO;
            }

            mediaManager.init(context, browseType);

            mLocalManager = mediaManager;
          }
          return mLocalManager;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .doOnNext(new Consumer<MediaManager>() {
          @Override public void accept(MediaManager manager) throws Exception {
            MediaGroupItem recentGroupItem = manager.getRecentGroupItem();
            if ((recentGroupItem == null
                || recentGroupItem.mediaItemList == null
                || recentGroupItem.mediaItemList.size() == 0) && (BROWSE_TYPE.PHOTO
                == manager.getBrowseType() || BROWSE_TYPE.VIDEO == manager.getBrowseType())) {
              Exceptions.propagate(new RuntimeException("media data empty,please retry!"));
            }
          }
        })
        .retryWhen(new RetryWithDelay(15, GalleryDef.BASE_TIME_DELAY));
  }

  public List<MediaGroupItem> getMediaGroupList(Context ctx) {
    List<MediaGroupItem> mediaGroupItemList = new ArrayList<>();
    if (mLocalManager != null) {
      List<MediaGroupItem> localMediaGroupList = mLocalManager.getGroupItemList();
      MediaGroupItem localSystemAlbum = generateLocalSystemAlbum(localMediaGroupList);
      mediaGroupItemList.add(localSystemAlbum);
      mediaGroupItemList.addAll(localMediaGroupList);
      Log.i("zjf FolderListData",
          "  MediaSController getMediaGroupList : mLocalManager != null, mediaGroupItemList = "
              + (null == mediaGroupItemList ? "null" : mediaGroupItemList.size()));
    }
    Log.i("zjf FolderListData",
        "  MediaSController getMediaGroupList : last, mediaGroupItemList = " + (
            null == mediaGroupItemList ? "null" : mediaGroupItemList.size()));
    return mediaGroupItemList;
  }

  private MediaGroupItem generateLocalSystemAlbum(List<MediaGroupItem> localMediaGroupList) {
    MediaGroupItem systemGroup = new MediaGroupItem();
    systemGroup.strGroupDisplayName =
        getMvpView().getContext().getString(R.string.mn_gallery_local_system_album_title);
    List<ExtMediaItem> mediaItemList = new ArrayList<>();
    for (MediaGroupItem groupItem : localMediaGroupList) {
      if (groupItem.mediaItemList != null && !groupItem.mediaItemList.isEmpty()) {
        mediaItemList.addAll(groupItem.mediaItemList);
      }
    }
    systemGroup.mediaItemList = mediaItemList;

    return systemGroup;
  }

  public void getLocalMedia(Context context, @GalleryDef.SourceType final int sourceType,
      long timeDelay) {
    getLocalMediaManager(context, sourceType, timeDelay).observeOn(Schedulers.io())
        .map((Function<MediaManager, List<PinnedHeaderEntity<MediaModel>>>) mediaManager -> {
          MediaGroupItem systemGroupItem = mediaManager.getRecentGroupItem();

          if (systemGroupItem != null) {
            getMvpView().onMediaGroupReady(systemGroupItem);
            return transformMediaGroup(context, sourceType, systemGroupItem);
          }
          return new ArrayList<>();
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<PinnedHeaderEntity<MediaModel>>>() {
          @Override public void onSubscribe(Disposable d) {
            compositeDisposable.add(d);
          }

          @Override public void onNext(List<PinnedHeaderEntity<MediaModel>> entityList) {
            getMvpView().onMediaListReady(entityList);
            Log.i("zjf FolderListData",
                "  MediaSController getLocalMedia : onNext , null == entityList ? " + (
                    null == entityList ? "null" : entityList.size()));
          }

          @Override public void onError(Throwable e) {
            getMvpView().onMediaListReady(null);
            Log.i("zjf FolderListData",
                "  MediaSController getLocalMedia : onError , msg = " + e.getMessage());
          }

          @Override public void onComplete() {
            Log.i("zjf FolderListData", "  MediaSController getLocalMedia : onComplete");
          }
        });
  }

  public void updateMediaGroupData(@NonNull Context context,
      @GalleryDef.SourceType int sourceType, @NonNull MediaGroupItem systemGroupItem) {
    Single.just(true)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map(aBoolean -> transformMediaGroup(context, sourceType, systemGroupItem))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<List<PinnedHeaderEntity<MediaModel>>>() {
          @Override public void onSubscribe(Disposable d) {
            compositeDisposable.add(d);
          }

          @Override public void onSuccess(List<PinnedHeaderEntity<MediaModel>> entityList) {
            getMvpView().onMediaListReady(entityList);
          }

          @Override public void onError(Throwable e) {
            getMvpView().onMediaListReady(null);
          }
        });
  }

  private List<PinnedHeaderEntity<MediaModel>> transformMediaGroup(@NonNull Context context,
      @GalleryDef.SourceType int sourceType, @NonNull MediaGroupItem systemGroupItem) {

    MediaManager mediaManager = new MediaManager();
    mediaManager.setGroupType(GROUP_MEDIA_TYPE.GROUP_MEDIA_TYPE_DATE);
    mediaManager.init(context, systemGroupItem);

    List<PinnedHeaderEntity<MediaModel>> resultList = new ArrayList<>();
    int groupCount = mediaManager.getGroupCount();
    for (int i = 0; i < groupCount; i++) {
      MediaGroupItem groupItem = mediaManager.getGroupItem(i);
      if (groupItem != null
          && groupItem.mediaItemList != null
          && !groupItem.mediaItemList.isEmpty()) {
        PinnedHeaderEntity<MediaModel> headerEntity =
            new PinnedHeaderEntity<>(null, MediaAdapter.TYPE_HEADER,
                groupItem.strGroupDisplayName);
        resultList.add(headerEntity);

        boolean isMediaExistInGroup = false;
        for (ExtMediaItem mediaItem : groupItem.mediaItemList) {
          int itemSourceType = GalleryDef.TYPE_PHOTO;
          if (mediaItem.mediaType == MediaType.MEDIA_TYPE_VIDEO) {
            itemSourceType = GalleryDef.TYPE_VIDEO;
          }
          if (itemSourceType != sourceType) {
            continue;
          }

          MediaModel model = new MediaModel.Builder().sourceType(sourceType)
              .filePath(mediaItem.path)
              .duration(mediaItem.duration)
              .build();
          int order = getMvpView().getMediaOrder(model);
          model.setOrder(++order);
          PinnedHeaderEntity<MediaModel> entity =
              new PinnedHeaderEntity<>(model, MediaAdapter.TYPE_DATA, "");
          resultList.add(entity);
          isMediaExistInGroup = true;
        }
        if (!isMediaExistInGroup) {
          resultList.remove(headerEntity);
        }
      }
    }

    if (!resultList.isEmpty()) {
      PinnedHeaderEntity<MediaModel> footerEntity =
          new PinnedHeaderEntity<>(null, MediaAdapter.TYPE_FOOTER, null);
      resultList.add(footerEntity);
    }
    return resultList;
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeDisposable != null) {
      compositeDisposable.clear();
    }
    if (mLocalManager != null) {
      mLocalManager.unInit();
      mLocalManager = null;
    }
  }
}
