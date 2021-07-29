package com.quvideo.application.gallery.db.impl;

import android.text.TextUtils;
import com.quvideo.application.gallery.db.bean.DaoSession;
import com.quvideo.application.gallery.db.bean.MediaBeen;
import com.quvideo.application.gallery.db.bean.MediaBeenDao;
import com.quvideo.application.gallery.model.GRange;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.utils.MediaUtils;
import java.util.ArrayList;
import java.util.List;

public class MediaDaoImpl {

  private DaoSession mediaDaoSession;
  private MediaBeenDao mediaBeenDao;

  public MediaDaoImpl(DaoSession daoSession) {
    this.mediaDaoSession = daoSession;
    this.mediaBeenDao = daoSession.getMediaBeenDao();
  }

  public ArrayList<MediaModel> mediaQueryList() {
    ArrayList<MediaModel> mediaList = new ArrayList<>();
    List<MediaBeen> dbMediaBeens =
        mediaBeenDao.queryBuilder().orderDesc(MediaBeenDao.Properties._id).build().list();

    if (dbMediaBeens == null) {
      return mediaList;
    }

    for (MediaBeen mediaBeen : dbMediaBeens) {
      MediaModel missionModel = new MediaModel.Builder().sourceType(mediaBeen.sourceType)
          .duration(mediaBeen.duration)
          .rotation(mediaBeen.rotation)
          .filePath(mediaBeen.filePath)
          .rawFilepath(mediaBeen.rawFilepath)
          .rangeInFile(mediaBeen.rangeInFile)
          .build();
      mediaList.add(missionModel);
    }

    return mediaList;
  }

  public void mediaUpdate(MediaModel missionModel) {
    if (missionModel == null) {
      return;
    }
    MediaBeen mediaBeen = new MediaBeen();
    mediaBeen.sourceType = missionModel.getSourceType();
    mediaBeen.duration = missionModel.getDuration();
    mediaBeen.rotation = missionModel.getRotation();
    mediaBeen.filePath = missionModel.getFilePath();
    mediaBeen.rawFilepath = missionModel.getRawFilepath();
    mediaBeen.rangeInFile = missionModel.getRangeInFile();

    mediaBeenDao.insertOrReplace(mediaBeen);
  }

  public void mediaUpdate(List<MediaModel> modelList) {
    if (modelList == null || modelList.isEmpty()) {
      return;
    }
    mediaDaoSession.runInTx(() -> {
      for (MediaModel model : modelList) {
        mediaUpdate(model);
      }
    });
  }

  /**
   * 查询视频类数据
   */
  public MediaModel getDBVideoMediaModel(String videoPath, GRange range) {
    if (TextUtils.isEmpty(videoPath) || range == null) {
      return null;
    }
    boolean isVideoType = MediaUtils.isVideoFileType(videoPath);
    if (!isVideoType) {
      return null;
    }
    List<MediaBeen> mediaBeenList;
    try {
      mediaBeenList = mediaBeenDao.queryBuilder()
          .where(MediaBeenDao.Properties.RawFilepath.eq(videoPath))
          .build()
          .list();
    } catch (Exception e) {
      return null;
    }

    if (mediaBeenList == null || mediaBeenList.isEmpty()) {
      return null;
    }
    MediaModel finalMediaModel = null;
    for (MediaBeen mediaBeen : mediaBeenList) {
      if (TextUtils.equals(videoPath, mediaBeen.getRawFilepath())) {
        if (range.equals(mediaBeen.rangeInFile)) {
          finalMediaModel = new MediaModel.Builder().filePath(videoPath)
              .rawFilepath(mediaBeen.getRawFilepath())
              .duration(mediaBeen.getDuration())
              .rotation(mediaBeen.getRotation())
              .sourceType(GalleryDef.TYPE_VIDEO)
              .rangeInFile(range)
              .build();
          break;
        }
      }
    }
    return finalMediaModel;
  }

  /**
   * 查询非视频类数据（图片，GIF）
   */
  public MediaModel getDBMediaModel(String filePath) {
    if (TextUtils.isEmpty(filePath)) {
      return null;
    }
    boolean isVideoType = MediaUtils.isVideoFileType(filePath);
    if (isVideoType) {
      return null;
    }

    List<MediaBeen> mediaBeenList = mediaBeenDao.queryBuilder()
        .where(MediaBeenDao.Properties.RawFilepath.eq(filePath))
        .build()
        .list();
    if (mediaBeenList == null || mediaBeenList.isEmpty()) {
      return null;
    }
    MediaModel finalMediaModel = null;

    for (MediaBeen mediaBeen : mediaBeenList) {
      if (TextUtils.equals(filePath, mediaBeen.getRawFilepath())) {
        finalMediaModel = new MediaModel.Builder().filePath(mediaBeen.getFilePath())
            .rawFilepath(mediaBeen.getRawFilepath())
            .rotation(mediaBeen.getRotation())
            .sourceType(GalleryDef.TYPE_PHOTO)
            .build();
        break;
      }
    }

    return finalMediaModel;
  }
}
