package com.quvideo.application.gallery.utils;

import android.os.Environment;
import com.quvideo.application.editor.R;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 文件夹名字映射关系工具类
 * </p>
 *
 * @author johnson
 */
public class MediaFolderNameMapUtils {
  private static Map<String, Integer> mDisplayNameMap = null;

  public static Map<String, Integer> getNameMap() {
    if (mDisplayNameMap == null) {
      initDisplayNameMap();
    }
    return mDisplayNameMap;
  }

  private static void initDisplayNameMap() {
    mDisplayNameMap = new HashMap<String, Integer>();
    // first time running
    // system camera path
    String strCameraPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
            + File.separator
            + "Camera/";
    mDisplayNameMap.put(strCameraPath, R.string.mn_gallery_camera_album_title);
  }
}
