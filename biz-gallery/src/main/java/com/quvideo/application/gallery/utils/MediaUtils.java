package com.quvideo.application.gallery.utils;

import android.text.TextUtils;
import com.quvideo.application.utils.FilePathUtils;
import java.util.HashMap;
import java.util.Locale;

/**
 * MediaScanner helper class.
 *
 * {@hide}
 */
public class MediaUtils {
  private static final String TAG = "MediaFileUtils";

  public static final int FILE_TYPE_UNKNOWN = 0;
  // Audio file types
  public static final int FILE_TYPE_MP3 = 1;
  public static final int FILE_TYPE_M4A = 2;
  public static final int FILE_TYPE_WAV = 3;
  public static final int FILE_TYPE_AMR = 4;
  public static final int FILE_TYPE_AWB = 5;
  public static final int FILE_TYPE_WMA = 6;
  public static final int FILE_TYPE_OGG = 7;
  public static final int FILE_TYPE_AAC = 8; // not support by android.
  // MIDI file types
  public static final int FILE_TYPE_MID = 101;
  public static final int FILE_TYPE_SMF = 102;
  public static final int FILE_TYPE_IMY = 103;
  // Video file types
  public static final int FILE_TYPE_MP4 = 201;
  public static final int FILE_TYPE_M4V = 202;
  public static final int FILE_TYPE_3GPP = 203;
  public static final int FILE_TYPE_3GPP2 = 204;
  public static final int FILE_TYPE_WMV = 205;
  public static final int FILE_TYPE_SKM = 206; // not support by android.
  public static final int FILE_TYPE_K3G = 207; // not support by android.
  public static final int FILE_TYPE_AVI = 208; // not support by android.
  public static final int FILE_TYPE_ASF = 209; // not support by android.
  public static final int FILE_TYPE_MOV = 210;
  public static final int FILE_TYPE_FLV = 211;
  private static final int FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4;
  private static final int LAST_VIDEO_FILE_TYPE = 299;
  // Image file types
  public static final int FILE_TYPE_JPEG = 301;
  public static final int FILE_TYPE_GIF = 302;
  public static final int FILE_TYPE_PNG = 303;
  public static final int FILE_TYPE_BMP = 304;
  public static final int FILE_TYPE_WBMP = 305;
  public static final int FILE_TYPE_WEBP = 306;
  private static final int FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG;
  private static final int LAST_IMAGE_FILE_TYPE = 399;
  // Playlist file types
  public static final int FILE_TYPE_M3U = 401;
  public static final int FILE_TYPE_PLS = 402;
  public static final int FILE_TYPE_WPL = 403;

  private static class MediaFileType {

    private int m_iFileType;
    private String m_strMimeType;

    MediaFileType(int iFileType, String strMimeType) {
      this.m_iFileType = iFileType;
      this.m_strMimeType = strMimeType;
    }

    public int GetFileType() {
      return m_iFileType;
    }

    public String GetMimeType() {
      return m_strMimeType;
    }
  }

  private static HashMap<String, MediaFileType> s_FileTypeMap = new HashMap<>();

  private static void addFileType(String strExtension, int iFileType, String strMimeType) {
    s_FileTypeMap.put(strExtension, new MediaFileType(iFileType, strMimeType));
  }

  static {
    // audio
    addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg");
    addFileType("M4A", FILE_TYPE_M4A, "audio/mp4");
    addFileType("M4A", FILE_TYPE_M4A, "audio/3gpp");
    addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav");
    addFileType("AMR", FILE_TYPE_AMR, "audio/amr");
    addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb");
    addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma");
    addFileType("OGG", FILE_TYPE_OGG, "application/ogg");
    addFileType("OGA", FILE_TYPE_OGG, "application/ogg");
    addFileType("AAC", FILE_TYPE_AAC, "audio/aac");
    // MIDI
    addFileType("MID", FILE_TYPE_MID, "audio/midi");
    addFileType("MIDI", FILE_TYPE_MID, "audio/midi");
    addFileType("XMF", FILE_TYPE_MID, "audio/midi");
    addFileType("RTTTL", FILE_TYPE_MID, "audio/midi");
    addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi");
    addFileType("IMY", FILE_TYPE_IMY, "audio/imelody");
    addFileType("RTX", FILE_TYPE_MID, "audio/midi");
    addFileType("OTA", FILE_TYPE_MID, "audio/midi");
    //video
    addFileType("MP4", FILE_TYPE_MP4, "video/mp4");
    addFileType("M4V", FILE_TYPE_M4V, "video/mp4");
    addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp");
    addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp");
    addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2");
    addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2");
    addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv");
    addFileType("SKM", FILE_TYPE_SKM, "video/skm");
    addFileType("K3G", FILE_TYPE_K3G, "video/k3g");
    addFileType("AVI", FILE_TYPE_AVI, "video/avi");
    addFileType("ASF", FILE_TYPE_ASF, "video/asf");
    addFileType("MOV", FILE_TYPE_MOV, "video/mp4");
    addFileType("FLV", FILE_TYPE_FLV, "video/mp4");
    // image
    addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg");
    addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg");
    addFileType("GIF", FILE_TYPE_GIF, "image/gif");
    addFileType("PNG", FILE_TYPE_PNG, "image/png");
    addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp");
    addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");
    addFileType("WEBP", FILE_TYPE_WEBP, "image/webp");
    // Playlist
    addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl");
    addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls");
    addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl");
  }

  /**
   * 获取文件类型
   */
  public static int getFileMediaType(String filePath) {
    if (TextUtils.isEmpty(filePath)) {
      return FILE_TYPE_UNKNOWN;
    }
    if (FilePathUtils.isContentUri(filePath)) {
      filePath = FilePathUtils.transUriPath2FilePath(filePath);
    }
    int iLastDot = filePath.lastIndexOf(".");
    if (iLastDot < 0 || iLastDot == filePath.length() - 1) {
      return FILE_TYPE_UNKNOWN;
    }
    MediaFileType mediaFileType = s_FileTypeMap.get(filePath.substring(iLastDot + 1).toUpperCase(Locale.US));
    if (null == mediaFileType) {
      return FILE_TYPE_UNKNOWN;
    }
    return mediaFileType.m_iFileType;
  }

  /**
   * 是否视频文件
   */
  public static boolean isVideoFileType(String filePath) {
    int mediaType = getFileMediaType(filePath);
    return isVideoFileType(mediaType);
  }

  /**
   * 是否图片文件
   */
  public static boolean isImageFileType(String filePath) {
    int mediaType = getFileMediaType(filePath);
    return isImageFileType(mediaType);
  }

  /**
   * 是否gif文件
   */
  public static boolean isGifFileType(String filePath) {
    int mediaType = getFileMediaType(filePath);
    return mediaType == FILE_TYPE_GIF;
  }

  private static boolean isVideoFileType(int iFileType) {
    return (iFileType >= FIRST_VIDEO_FILE_TYPE && iFileType <= LAST_VIDEO_FILE_TYPE);
  }

  private static boolean isImageFileType(int iFileType) {
    return (iFileType >= FIRST_IMAGE_FILE_TYPE && iFileType <= LAST_IMAGE_FILE_TYPE);
  }
}
