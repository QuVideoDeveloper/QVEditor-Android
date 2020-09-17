package com.quvideo.application.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.quvideo.application.EditorApp;
import java.util.concurrent.ConcurrentHashMap;
public class FilePathUtils {

  private static ConcurrentHashMap<String, String> uriKeyMaps = new ConcurrentHashMap<>();

  /**
   * 是否是target 29以后的媒体类路径
   */
  public static boolean isContentUri(String path) {
    return path.startsWith("content://");
  }

  //******************************************为了兼容target10做的uri和path的转化*******************************************

  /**
   * 适配api19及以上,根据uri获取图片的绝对路径
   *
   * @param uriPath Uri地址
   * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
   */
  public static String transUriPath2FilePath(String uriPath) {
    if (TextUtils.isEmpty(uriPath)) {
      return "";
    }
    if (uriKeyMaps.containsKey(uriPath)) {
      return uriKeyMaps.get(uriPath);
    }
    Context context = EditorApp.Companion.getInstance().getApp().getApplicationContext();
    String[] projection;
    if (uriPath.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
      projection = new String[] { MediaStore.Images.Media.DATA };
    } else if (uriPath.startsWith(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString())) {
      projection = new String[] { MediaStore.Video.Media.DATA };
    } else {
      projection = new String[] { MediaStore.Audio.Media.DATA };
    }
    Uri uri = Uri.parse(uriPath);
    String filePath = null;
    if (DocumentsContract.isDocumentUri(context, uri)) {
      // 如果是document类型的 uri, 则通过document id来进行处理
      String documentId = DocumentsContract.getDocumentId(uri);
      if (isMediaDocument(uri)) {
        // MediaProvider 使用':'分割
        String id = documentId.split(":")[1];
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = { id };
        filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, projection, selectionArgs);
      } else if (isDownloadsDocument(uri)) {
        // DownloadsProvider
        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(documentId));
        filePath = getDataColumn(context, contentUri, null, projection, null);
      }
    } else if ("content".equalsIgnoreCase(uri.getScheme())) {
      // 如果是 content 类型的 Uri
      filePath = getDataColumn(context, uri, null, projection, null);
    } else if ("file".equals(uri.getScheme())) {
      // 如果是 file 类型的 Uri,直接获取图片对应的路径
      filePath = uri.getPath();
    }
    if (!TextUtils.isEmpty(filePath)) {
      uriKeyMaps.put(uriPath, filePath);
      return filePath;
    }
    return uriPath;
  }

  /**
   * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
   */
  private static String getDataColumn(Context context, Uri uri, String selection, String[] projection, String[] selectionArgs) {
    String path = null;
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
        path = cursor.getString(columnIndex);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return path;
  }

  private static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  private static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  enum MediaType {
    Image,
    Video,
    Audio
  }
}
