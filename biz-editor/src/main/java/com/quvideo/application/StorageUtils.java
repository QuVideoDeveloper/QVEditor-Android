package com.quvideo.application;

import android.content.Context;
import android.text.TextUtils;
import com.quvideo.application.utils.FileUtils;
import java.io.File;

public class StorageUtils {

  private static final String PATH_ROOT = ".demo/";
  private static final String PATH_TEMPLATE = ".template/";
  private static final String PATH_AUDIODOT = "audioDot/";
  private static final String PATH_SEG_MASK = "segMask/";
  private static final String PATH_SKELETON_MASK = "skeletonMask/";
  private static final String PATH_PAINT_GROUP = "paintGroup/";
  private static final String PATH_EFFECT_XML = "effectXml/";

  // 素材目录
  private static String mTemplatePath;
  // 踩点目录
  private static String mAudioAppDir;
  // 人体分割目录
  private static String mSegMaskAppDir;
  // 人体骨骼点目录
  private static String mSkeletonMaskAppDir;
  // 画笔临时目录
  private static String mPaintGroupAppDir;
  // EffectXml文件目录
  private static String mEffectXmlDir;

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
    mSegMaskAppDir = outerAppDir + PATH_ROOT + PATH_SEG_MASK;
    mSkeletonMaskAppDir = outerAppDir + PATH_ROOT + PATH_SKELETON_MASK;
    mPaintGroupAppDir = outerAppDir + PATH_ROOT + PATH_PAINT_GROUP;
    mEffectXmlDir = outerAppDir + PATH_ROOT + PATH_EFFECT_XML;
    FileUtils.createMultilevelDirectory(mTemplatePath);
    FileUtils.createNoMediaFileInPath(mTemplatePath);
    FileUtils.createMultilevelDirectory(mAudioAppDir);
    FileUtils.createNoMediaFileInPath(mAudioAppDir);
    FileUtils.createMultilevelDirectory(mSegMaskAppDir);
    FileUtils.createNoMediaFileInPath(mSegMaskAppDir);
    FileUtils.createMultilevelDirectory(mSkeletonMaskAppDir);
    FileUtils.createNoMediaFileInPath(mSkeletonMaskAppDir);
    FileUtils.createMultilevelDirectory(mPaintGroupAppDir);
    FileUtils.createNoMediaFileInPath(mPaintGroupAppDir);
    FileUtils.createMultilevelDirectory(mEffectXmlDir);
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

  public static synchronized String getSegMaskAppDir(Context context) {
    if (TextUtils.isEmpty(mSegMaskAppDir)) {
      init(context);
    }
    return mSegMaskAppDir;
  }

  public static synchronized String getSkeletonMaskAppDir(Context context) {
    if (TextUtils.isEmpty(mSkeletonMaskAppDir)) {
      init(context);
    }
    return mSkeletonMaskAppDir;
  }

  public static synchronized String getPaintGroupAppDir(Context context) {
    if (TextUtils.isEmpty(mPaintGroupAppDir)) {
      init(context);
    }
    return mPaintGroupAppDir;
  }

  public static synchronized String getEffectXmlDir(Context context) {
    if (TextUtils.isEmpty(mEffectXmlDir)) {
      init(context);
    }
    return mEffectXmlDir;
  }
}
