package com.quvideo.application.export;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import java.io.File;
import java.util.Locale;

public class DBUtils {
  private static final int TYPE_UNKNOWN = 0;
  private static final int TYPE_IMAGE = 1;
  private static final int TYPE_AUDIO = 2;
  private static final int TYPE_VIDEO = 3;



  public static boolean insert(Context context, String strFullPath, VideoInfo vInfo) {
    if (context == null) {
      return false;
    }

    if (null == strFullPath || strFullPath.lastIndexOf(".") < 0) {
      return false;
    }

    ContentResolver resolver = context.getContentResolver();
    Uri mUri;
    int iMediaType;
    if (MediaFileUtils.isImageFileType(strFullPath)) {
      iMediaType = TYPE_IMAGE;
    } else if (MediaFileUtils.isVideoFileType(strFullPath)) {
      iMediaType = TYPE_VIDEO;
    } else {
      return false;
    }

    File sourceFile = new File(strFullPath);
    if (sourceFile.isFile()) {
      String strFileName = sourceFile.getName();
      if (strFileName.lastIndexOf(".") < 0) {
        return false;
      }

      // Uri uri = null;
      String strMimeType = null;
      String strFileNameWithoutExt = strFileName.substring(0, strFileName.lastIndexOf("."));

      if (iMediaType == TYPE_IMAGE) {
        ContentValues newValues = new ContentValues(6);
        newValues.put(MediaStore.Images.Media.TITLE, strFileNameWithoutExt);
        newValues.put(MediaStore.Images.Media.DISPLAY_NAME, sourceFile.getName());
        newValues.put(MediaStore.Images.Media.DATA, sourceFile.getPath());
        newValues.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
        newValues.put(MediaStore.Images.Media.SIZE, sourceFile.length());
        strMimeType = getMimeTypeByMediaFileName(strFullPath);
        if (strMimeType == null) {
          strMimeType = "image/jpeg";
        }
        newValues.put(MediaStore.Images.Media.MIME_TYPE, strMimeType);
        mUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        resolver.insert(mUri, newValues);
      } else if (iMediaType == TYPE_VIDEO) {
        int valueCount = (vInfo == null ? 6 : 8);
        ContentValues newValues = new ContentValues(valueCount);
        newValues.put(MediaStore.Video.Media.TITLE, strFileNameWithoutExt);
        newValues.put(MediaStore.Video.Media.DISPLAY_NAME, sourceFile.getName());
        newValues.put(MediaStore.Video.Media.DATA, sourceFile.getPath());
        newValues.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
        newValues.put(MediaStore.Video.Media.SIZE, sourceFile.length());
        strMimeType = getMimeTypeByMediaFileName(strFullPath);
        if (strMimeType == null) {
          strMimeType = "video/mp4";
        }
        newValues.put(MediaStore.Video.Media.MIME_TYPE, strMimeType);

        if (null != vInfo) {
          int iDuration = vInfo.duration;
          int iFrameWidth = vInfo.frameWidth;
          int iFrameHeight = vInfo.frameHeight;
          String strResolution = iFrameWidth + "x" + iFrameHeight;
          newValues.put(MediaStore.Video.VideoColumns.DURATION, iDuration);
          newValues.put(MediaStore.Video.VideoColumns.RESOLUTION, strResolution);
      }

        mUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        resolver.insert(mUri, newValues);
      }

      return true;
    } else {
      return false;
    }
  }

  public static boolean delete(Context context, String strFilePath) {
    if (context == null) {
      return false;
    }
    try {
      ContentResolver resolver = context.getContentResolver();
      String where = MediaStore.Images.Media.DATA + " = ?";
      String[] selectionArgs = new String[] { strFilePath };
      resolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, where, selectionArgs);
    } catch (Throwable ignore) {
    }
    return true;
  }

  private static String getMimeTypeByMediaFileName(String strFullPath) {
    if (strFullPath == null) {
      return null;
    }

    String strExt = strFullPath.substring(strFullPath.lastIndexOf(".") + 1);

    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    if (mimeTypeMap == null) {
      return null;
    }

    return mimeTypeMap.getMimeTypeFromExtension(strExt.toLowerCase(Locale.US));
  }
}
