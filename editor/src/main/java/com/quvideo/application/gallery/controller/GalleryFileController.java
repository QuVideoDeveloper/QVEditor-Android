package com.quvideo.application.gallery.controller;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.db.GalleryDBUtil;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.gallery.utils.GalleryFile;
import com.quvideo.application.gallery.utils.FileUtils;
import com.quvideo.application.gallery.widget.GalleryLoading;
import com.quvideo.application.utils.mvp.BaseController;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/11/2019
 */
public class GalleryFileController extends BaseController<IGalleryFile> {
  private Disposable compressDisposable, dismissDownloadDialogDisposable;

  private boolean isVideoTransRunning = false;
  private boolean isPhotoCompressing = false;
  private int importProgress = 0;
  private ArrayList<MediaModel> finalMediaModelList = new ArrayList<>();

  @Override public void detachView() {
    super.detachView();
    GalleryLoading.dismissLoading();
    if (dismissDownloadDialogDisposable != null) {
      dismissDownloadDialogDisposable.dispose();
      dismissDownloadDialogDisposable = null;
    }
    if (compressDisposable != null) {
      compressDisposable.dispose();
    }
  }

  public GalleryFileController(IGalleryFile mvpView) {
    super(mvpView);

    GalleryDBUtil.initDB(mvpView.getContext());
  }

  public void processFileCompress(@NonNull ArrayList<MediaModel> modelList) {
    ArrayList<MediaModel> needDownloadMediaList = checkDownloadMedia(modelList);
    if (needDownloadMediaList != null && !needDownloadMediaList.isEmpty()) {
      startDownloadNetMedia(needDownloadMediaList);
      return;
    }

    finalMediaModelList = modelList;
    IGalleryProvider galleryProvider = GalleryClient.getInstance().getGalleryProvider();
    if (null == galleryProvider) {
      return;
    }
    List<MediaModel> needTransModelList = new ArrayList<>();
    for (MediaModel model : modelList) {
      if (GalleryFile.isVideoFile(model.getFilePath())) {
        MediaModel dbVideoMediaModel =
            GalleryDBUtil.getDBVideoMediaModel(model.getFilePath(), model.getRangeInFile());
        if (dbVideoMediaModel == null) {
          needTransModelList.add(model);
        } else {
          model.setFilePath(dbVideoMediaModel.getFilePath());
          model.setRawFilepath(dbVideoMediaModel.getRawFilepath());
        }
      }
    }
    showNormalLoading();

    if (compressDisposable != null) {
      compressDisposable.dispose();
      compressDisposable = null;
    }

    compressDisposable = Single.just(true)
        .subscribeOn(Schedulers.io())
        .delay(GalleryDef.DEFAULT_TIME_DELAY, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())
        .flatMap((Function<Boolean, SingleSource<Boolean>>) aBoolean -> {
          Context context = getMvpView().getContext();
          GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
          boolean convertPng = settings.isPhotoConvertPng();
          for (MediaModel model : modelList) {
            if (isPhoto(model)) {
              String rawFilePath = model.getFilePath();
              MediaModel dbMediaModel = GalleryDBUtil.getDBMediaModel(rawFilePath);
              if (dbMediaModel == null) {
                String newFilePath =
                    GalleryFile.compressFile(rawFilePath, GalleryFile.getExportImagePath(),
                        convertPng);
                if (FileUtils.isFileExisted(newFilePath)) {
                  //update file path info
                  model.setRawFilepath(rawFilePath);
                  model.setFilePath(newFilePath);

                  //update db file info
                  GalleryDBUtil.mediaUpdate(model);
                }
              } else {
                model.setRawFilepath(dbMediaModel.getRawFilepath());
                model.setFilePath(dbMediaModel.getFilePath());
              }
            }
            synchronized (GalleryFileController.this) {
              importProgress++;
              //updateLoadingProgress();
            }
          }
          return Single.just(true);
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aBoolean -> {
          isPhotoCompressing = false;
          processFinish();
        });
  }

  public ArrayList<MediaModel> checkDownloadMedia(@NonNull List<MediaModel> modelList) {
    ArrayList<MediaModel> needDownloadMediaList = new ArrayList<>();
    for (MediaModel model : modelList) {
      if (TextUtils.isEmpty(model.getFilePath())) {
        continue;
      }
      if (isNetMedia(model.getFilePath())) {
        MediaModel dbMediaModel = GalleryDBUtil.getDBMediaModel(model.getFilePath());
        if (dbMediaModel == null) {
          needDownloadMediaList.add(model);
        } else {
          model.setRawFilepath(dbMediaModel.getRawFilepath());
          model.setFilePath(dbMediaModel.getFilePath());
        }
      }
    }

    return needDownloadMediaList;
  }

  public boolean isNetMedia(String filePath) {
    if (TextUtils.isEmpty(filePath)) {
      return false;
    }
    return filePath.startsWith("http") || filePath.startsWith("https");
  }

  public void startDownloadNetMedia(List<MediaModel> mediaList) {
    if (mediaList == null || mediaList.isEmpty()) {
      return;
    }
    showDownloadProgressDialog(mediaList.size());

    Single.just(true).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).map(aBoolean -> {
      Context context = getMvpView().getContext();
      for (MediaModel model : mediaList) {
        if (model == null) {
          continue;
        }
        String filePath = model.getFilePath();
        if (TextUtils.isEmpty(filePath)) {
          continue;
        }
      }
      return true;
    }).subscribe();
  }

  private void showDownloadProgressDialog(int max) {
  }

  private void showNormalLoading() {
    if (getMvpView() == null || getMvpView().getContext() == null) {
      return;
    }
    if (GalleryLoading.isShowing()) {
      return;
    }
    Context context = getMvpView().getContext();
    GalleryLoading.showLoading(context,
        context.getString(R.string.mn_gallery_file_import_tip_message));
  }

  private void showTransProcessDialog() {
  }

  private void updateLoadingProgress() {
  }

  private void processFinish() {
    if (isVideoTransRunning || isPhotoCompressing) {
      return;
    }
    GalleryLoading.dismissLoading();
    /**
     * file ready,do exit with {@link finalMediaModelList}
     */
    getMvpView().onFileDone(finalMediaModelList);
  }

  private boolean isPhoto(@NonNull MediaModel model) {
    return model.getSourceType() != GalleryDef.TYPE_VIDEO && !GalleryFile.isGifFile(
        model.getFilePath());
  }
}
