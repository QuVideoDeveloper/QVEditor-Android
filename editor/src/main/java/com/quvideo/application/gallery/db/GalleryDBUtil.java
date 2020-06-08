package com.quvideo.application.gallery.db;

import android.content.Context;
import com.quvideo.application.gallery.model.GRange;
import com.quvideo.application.gallery.model.MediaModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc gallery db util
 * @since 6/27/2019
 */
public class GalleryDBUtil {

  public static void initDB(Context context) {
    GalleryDBFactory.getInstance().initDB(context.getApplicationContext());
  }

  public static ArrayList<MediaModel> queryMediaList() {
    return GalleryDBFactory.getInstance().getMediaDaoImpl().mediaQueryList();
  }

  public static void mediaUpdate(MediaModel model) {
    GalleryDBFactory.getInstance().getMediaDaoImpl().mediaUpdate(model);
  }

  public static void mediaUpdate(List<MediaModel> modelList) {
    GalleryDBFactory.getInstance().getMediaDaoImpl().mediaUpdate(modelList);
  }

  public static MediaModel getDBVideoMediaModel(String videoPath, GRange range) {
    return GalleryDBFactory.getInstance()
        .getMediaDaoImpl()
        .getDBVideoMediaModel(videoPath, range);
  }

  public static MediaModel getDBMediaModel(String filePath) {
    return GalleryDBFactory.getInstance().getMediaDaoImpl().getDBMediaModel(filePath);
  }
}
