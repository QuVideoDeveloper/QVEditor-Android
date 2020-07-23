package com.quvideo.application.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.quvideo.application.EditorApp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class FileUtils {

  public static final String ASSETS_THEME = "assets_android://";

  /**
   *
   */
  public static boolean isDirectoryExisted(String strDiretory) {
    if (TextUtils.isEmpty(strDiretory)) return false;

    File file = new File(strDiretory);
    return (file.exists() && file.isDirectory());
  }

  /**
   *
   */
  public static boolean isFileExisted(String strFullFileName) {
    if (TextUtils.isEmpty(strFullFileName)) return false;
    if (strFullFileName.startsWith(ASSETS_THEME)) {
      String strAssetsFile = strFullFileName.substring(ASSETS_THEME.length());
      if (TextUtils.isEmpty(strAssetsFile)) return false;
      return isAssetsFileExisted(EditorApp.Companion.getInstance().getApp().getAssets(), strAssetsFile);
    } else {
      File file = new File(strFullFileName);
      return (file.isFile() && file.exists());
    }
  }

  public static boolean isAssetsFileExisted(AssetManager assetManager, String srcFile) {
    if (null == assetManager || TextUtils.isEmpty(srcFile)) {
      return false;
    }
    boolean bFileExisted = false;
    InputStream in = null;
    try {
      in = assetManager.open(srcFile);
      bFileExisted = (in != null);
    } catch (Exception e) {

    } finally {
      if (in != null) {
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

  /**
   *
   */
  public static boolean createMultilevelDirectory(String strPath) {
    if (TextUtils.isEmpty(strPath)) return false;

    File dir = null;
    dir = new File(strPath);
    if (dir.exists() && dir.isDirectory()) {
      return true;
    }

    //make dirs
    dir.mkdirs();

    //check again
    File dirNew = new File(strPath);
    return dirNew.exists() && dirNew.isDirectory();
  }

  public static boolean copyFileFromAssets(String srcFile, String destFile, AssetManager assetManager) {
    if (null == assetManager || TextUtils.isEmpty(srcFile)
        || TextUtils.isEmpty(destFile)) {
      return false;
    }
    String destDir = getFileParentPath(destFile);
    File destDirFile = new File(destDir);
    if (!destDirFile.exists() || !destDirFile.isDirectory()) {
      createMultilevelDirectory(destDir);
    }
    InputStream inAssets = null;
    InputStream in = null;
    OutputStream fout = null;
    BufferedOutputStream bos = null;
    try {
      in = inAssets = assetManager.open(srcFile);
      if (!in.markSupported()) {
        in = new BufferedInputStream(in, 16 * 1024);
        in.mark(1024);
      }

      String newFileName = destFile;
      File f = new File(newFileName);
      if (f.exists() && isSameData(in, f)) {
        return false;// same file and data, do not need copy
      }

      in.reset();
      fout = new FileOutputStream(destFile);
      byte[] buffer = new byte[2048];
      bos = new BufferedOutputStream(fout, buffer.length);
      int read;
      while ((read = in.read(buffer)) != -1) {
        bos.write(buffer, 0, read);
      }
    } catch (Exception e) {
      return false;
    } finally {
      try {
        if (bos != null) {
          bos.flush();
          bos.close();
        }
      } catch (Throwable ignore) {
      }
      try {
        if (fout != null) {
          fout.flush();
          fout.close();
        }
      } catch (Throwable ignore) {
      }
      try {
        if (in != null && in != inAssets) {
          in.close();
        }
      } catch (Throwable ignore) {
      }
      try {
        if (inAssets != null) {
          inAssets.close();
        }
      } catch (Throwable ignore) {
      }
    }
    return true;
  }

  private static boolean isSameData(InputStream is, File file) {
    boolean bIsSame = true;

    long lSrcLen = 0;
    long lDstLen = 0;
    try {
      lSrcLen = is.available();
      lDstLen = file.length();
      if (lSrcLen != lDstLen) {
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    long lFileSize = lSrcLen;
    byte[] bufferSrc = new byte[128];
    byte[] bufferDst = new byte[128];
    FileInputStream dst = null;
    try {
      dst = new FileInputStream(file);
      // check header
      lDstLen = dst.read(bufferDst);
      lSrcLen = is.read(bufferSrc);
      if (lSrcLen != lDstLen) {
        return false;
      }

      for (int i = 0; i < lSrcLen; i++) {
        if (bufferDst[i] != bufferSrc[i]) {
          return false;
        }
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (dst != null) {
        try {
          dst.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    bIsSame = true;
    return true;
  }

  public static String getFileParentPath(String path) {
    String outpath = "";
    if (!TextUtils.isEmpty(path)) {
      try {
        int index = path.lastIndexOf(File.separator);
        if (index > 0) {
          outpath = path.substring(0, index) + "/";
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return outpath;
  }

  /**
   * get the file name only. ex:(sdcard/test/test.jpg)=test
   */
  public static String getFileName(String fullFilePath) {
    File f = new File(fullFilePath);
    String strFileName = "";
    if (f != null) {
      strFileName = f.getName();
      if (!TextUtils.isEmpty(strFileName)) {
        int pos = strFileName.lastIndexOf(".");
        if (pos > 0) {
          strFileName = strFileName.substring(0, pos);
        }
      }
    }
    return strFileName;
  }

  public static void saveBitmap(String fullPath, Bitmap bitmap, int nQuality) {
    if (fullPath == null || bitmap == null) return;
    File f = new File(fullPath);
    try {
      if (f.exists()) {
        f.delete();
      }
    } catch (Throwable ignore) {
      return;
    }
    FileOutputStream fOut;
    try {
      fOut = new FileOutputStream(f);
    } catch (FileNotFoundException ignore) {
      return;
    }

    String strFile = fullPath.toUpperCase(Locale.US);
    Bitmap.CompressFormat cf = Bitmap.CompressFormat.JPEG;
    if (strFile.endsWith(".PNG")) cf = Bitmap.CompressFormat.PNG;

    bitmap.compress(cf, nQuality, fOut);
    try {
      fOut.flush();
      fOut.close();
    } catch (IOException ignore) {
    }
  }

  private static final String NO_MEDIA_STRING = ".nomedia";

  /**
   * 在文件夹中创建.nomedia
   */
  public static void createNoMediaFileInPath(String strPath) {
    if (strPath == null) {
      return;
    }
    if (!strPath.endsWith(File.separator)) {
      strPath += File.separator;
    }
    createMultilevelDirectory(strPath);
    strPath += NO_MEDIA_STRING;
    File fileNoMedia = new File(strPath);
    try {
      if (!fileNoMedia.exists()) {
        fileNoMedia.createNewFile();
      } else if (fileNoMedia.isDirectory()) {
        fileNoMedia.delete();
        fileNoMedia.createNewFile();
      }
    } catch (Exception ignore) {
    }
  }
}
