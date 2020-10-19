package com.quvideo.application;

import android.content.Context;
import android.text.TextUtils;
import com.quvideo.application.utils.FileUtils;
import java.io.File;

public class StorageUtils {

  private static final String PATH_ROOT = ".demo/";
  private static final String PATH_TEMPLATE = ".template/";
  private static final String PATH_AUDIODOT = "audioDot/";

  // 素材目录
  private static String mTemplatePath;
  // 踩点目录
  private static String mAudioAppDir;

  private static String outerAppDir;

  public static void init(Context context) {
    String innerDir = context.getFilesDir().getAbsolutePath();
    if (!innerDir.endsWith(File.separator)) {
      innerDir += File.separator;
    }
    File outerFile = context.getExternalFilesDir(null);
    outerAppDir = null;
    if (outerFile != null) {
      outerAppDir = outerFile.getAbsolutePath();
      if (!TextUtils.isEmpty(outerAppDir) && !outerAppDir.endsWith(File.separator)) {
        outerAppDir += File.separator;
      }
    }
    if (TextUtils.isEmpty(outerAppDir)) {
      outerAppDir = innerDir;
    }
    mTemplatePath = outerAppDir + PATH_ROOT + PATH_TEMPLATE;
    mAudioAppDir = outerAppDir + PATH_ROOT + PATH_AUDIODOT;
    FileUtils.createMultilevelDirectory(mTemplatePath);
    FileUtils.createNoMediaFileInPath(mTemplatePath);
    FileUtils.createMultilevelDirectory(mAudioAppDir);
    FileUtils.createNoMediaFileInPath(mAudioAppDir);
  }

  public static synchronized String getAppPath(Context context) {
    if (TextUtils.isEmpty(outerAppDir)) {
      init(context);
    }
    return outerAppDir;
  }

  public static synchronized String getTemplatePath(Context context) {
    if (TextUtils.isEmpty(mTemplatePath)) {
      init(context);
    }
    return mTemplatePath;
  }

  public static synchronized String getAudioAppDir(Context context) {
    if (TextUtils.isEmpty(mAudioAppDir)) {
      init(context);
    }
    return mAudioAppDir;
  }
}
