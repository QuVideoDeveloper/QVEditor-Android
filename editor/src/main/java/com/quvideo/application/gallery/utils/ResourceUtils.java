package com.quvideo.application.gallery.utils;

import android.content.res.AssetManager;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
  public static AssetManager mAssetsManager;
  public static void setContext(AssetManager assetsManager) {
    mAssetsManager = assetsManager;
  }


  public static boolean isAssetsFileExisted(AssetManager assetManager, String srcFile) {
    if (null == assetManager || TextUtils.isEmpty(srcFile))
      return false;
    boolean bFileExisted = false;
    InputStream in = null;
    try {
      in = assetManager.open(srcFile);
      bFileExisted = (in != null);
    } catch(Exception e) {

    } finally {
      if(in != null) {
        try {
          in.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return bFileExisted;
  }



}
