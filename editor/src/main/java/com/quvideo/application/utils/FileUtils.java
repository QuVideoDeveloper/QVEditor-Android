package com.quvideo.application.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.mobile.engine.QELogger;
import com.quvideo.mobile.engine.error.SDKErrCode;
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
  private static final String TAG = "QEFileUtils";

  public static final String ASSETS_THEME = "assets_android://";

  public static final long MVE_SAVE_MIN_SPACE = (500 * 1024);

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

  /**
   * 删除文件夹
   */
  public static boolean deleteDirectory(String dir) {
    if (!dir.endsWith(File.separator)) {
      dir = dir + File.separator;
    }
    File dirFile = new File(dir);
    if (!dirFile.exists() || !dirFile.isDirectory()) {
      return false;
    }
    boolean flag = true;
    File[] files = dirFile.listFiles();
    if (files == null) return true;

    for (int i = 0; i < files.length; i++) {
      if (files[i].isFile()) {
        flag = deleteFile(files[i].getAbsolutePath());
        if (!flag) {
          break;
        }
      } else {
        flag = deleteDirectory(files[i].getAbsolutePath());
        if (!flag) {
          break;
        }
      }
    }

    if (!flag) {
      return false;
    }

    if (dirFile.delete()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *
   */
  public static boolean deleteFile(String strFullFileName) {
    if (TextUtils.isEmpty(strFullFileName)) return false;

    File file = new File(strFullFileName);
    if (file.isFile()) {
      try {
        file.delete();
      } catch (Exception exception) {
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   * 查看路径目录是否可用
   *
   * @param dirPath 目录
   */
  public static int checkFileSystemPreSave(String dirPath) {
    if (!dirPath.endsWith(File.separator)) {
      dirPath = getFileParentPath(dirPath);
    }
    boolean bPathCreated = createMultilevelDirectory(dirPath);
    if (!bPathCreated) {
      return SDKErrCode.ERR_INVALID_PARAMETER;
    }
    File file = new File(dirPath);
    boolean bPathWritable = file.canWrite();
    if (!bPathWritable) {
      return SDKErrCode.ERR_DISK_PERMISSION;
    }
    long lFreeSpace = getFreeSpace(dirPath);
    if (lFreeSpace > 0 && lFreeSpace <= MVE_SAVE_MIN_SPACE) {
      return SDKErrCode.ERR_NO_DISK;
    }
    return SDKErrCode.RESULT_OK;
  }

  /**
   *
   */
  public static long getFreeSpace(String strPath) {
    if (TextUtils.isEmpty(strPath)) return 0;

    String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    if (strPath.startsWith(storagePath)) {
      return new File(storagePath).getUsableSpace();
    }

    File file = new File(strPath);
    while (!file.exists()) {
      file = file.getParentFile();
      if (file == null) {
        file = new File(File.separator);
        break;
      }

      if (file.getAbsolutePath().equals(File.separator)) break;
    }
    return file.getUsableSpace();
  }

  /**
   *
   */
  public static long getFileDate(String strPath) {
    File file = new File(strPath);
    long date = 0;
    if (file.exists()) {
      date = file.lastModified();
    }
    return date;
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

  /**
   * <p>
   * always override old dest file
   * </p>
   */
  public static boolean copyFile(String strSrcFile, String strDestFile) {
    if (strSrcFile == null || strDestFile == null || strSrcFile.equals(strDestFile)) return false;

    File srcFile = new File(strSrcFile);

    if (!srcFile.exists()) {
      return false;
    } else if (!srcFile.isFile()) return false;

    File destFile = new File(strDestFile);

    int byteread = 0;
    InputStream in = null;
    OutputStream out = null;

    byte[] buffer = new byte[4096];// 4K
    try {
      in = new FileInputStream(srcFile);
      out = new FileOutputStream(destFile);
      while ((byteread = in.read(buffer)) != -1) {
        out.write(buffer, 0, byteread);
      }
      out.flush();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (out != null) out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        if (in != null) in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
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

  public static String getFileExtFromAbPath(String path) {
    String outpath = "";
    if (!TextUtils.isEmpty(path)) {
      try {
        int index = path.lastIndexOf('.');
        if (index > 0) {
          outpath = path.substring(index);
        }
      } catch (Exception ex) {
        QELogger.e(TAG, "exception ex=" + ex.getMessage());
      }
    }
    return outpath;
  }

  /**
   * 格式化文件大小，显示KB/MB/GB
   *
   * @return String
   */
  public static String formatFileSize(long size) {
    long SIZE_KB = 1024;
    long SIZE_MB = SIZE_KB * 1024;
    long SIZE_GB = SIZE_MB * 1024;

    if (size < SIZE_KB) {
      return String.format(Locale.US, "%d B", (int) size);
    } else if (size < SIZE_MB) {
      return String.format(Locale.US, "%.2f KB", (float) size / SIZE_KB);
    } else if (size < SIZE_GB) {
      return String.format(Locale.US, "%.2f MB", (float) size / SIZE_MB);
    } else {
      return String.format(Locale.US, "%.2f GB", (float) size / SIZE_GB);
    }
  }

  /**
   *
   */
  public static boolean renameFile(String strSrc, String strTo) {
    if (TextUtils.isEmpty(strSrc) || TextUtils.isEmpty(strTo)) return false;

    File fileSrc = new File(strSrc);
    File fileTo = new File(strTo);
    if (fileSrc.isFile() && fileSrc.renameTo(fileTo)) {
      return true;
    }
    return false;
  }

  /**
   *
   */
  public static long fileSize(String strFullFileName) {
    if (TextUtils.isEmpty(strFullFileName)) return 0;

    File file = new File(strFullFileName);
    return sizeOf(file);
  }

  public static long sizeOf(File file) {

    if (!file.exists()) {
      // String message = file + " does not exist";
      // throw new IllegalArgumentException(message);
      return 0;
    }

    if (file.isDirectory()) {
      return sizeOfDirectory(file);
    } else {
      return file.length();
    }
  }

  public static long sizeOfDirectory(File directory) {
    try {
      checkDirectory(directory);
    } catch (Exception ex) {
      // directory not exist.
      return 0;
    }

    final File[] files = directory.listFiles();
    if (files == null) { // null if security restricted
      return 0L;
    }
    long size = 0;

    for (final File file : files) {
      try {
        if (!isSymlink(file)) {
          size += sizeOf(file);
          if (size < 0) {
            break;
          }
        }
      } catch (IOException ioe) {
        // Ignore exceptions caught when asking if a File is a symlink.
      }
    }

    return size;
  }

  private static void checkDirectory(File directory) {
    if (!directory.exists()) {
      throw new IllegalArgumentException(directory + " does not exist");
    }
    if (!directory.isDirectory()) {
      throw new IllegalArgumentException(directory + " is not a directory");
    }
  }

  public static boolean isSymlink(File file) throws IOException {
    if (file == null) {
      throw new NullPointerException("File must not be null");
    }
    // if (FilenameUtils.isSystemWindows()) {
    // return false;
    // }
    File fileInCanonicalDir = null;
    if (file.getParent() == null) {
      fileInCanonicalDir = file;
    } else {
      File canonicalDir = file.getParentFile().getCanonicalFile();
      fileInCanonicalDir = new File(canonicalDir, file.getName());
    }

    if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
      return false;
    } else {
      return true;
    }
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
