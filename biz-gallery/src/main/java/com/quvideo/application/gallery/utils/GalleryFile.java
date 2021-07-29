package com.quvideo.application.gallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.constant.StorageInfo;
import com.quvideo.application.gallery.model.GSzie;
import com.quvideo.application.utils.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.quvideo.application.gallery.model.GalleryDef.LIMIT_1080P;
import static com.quvideo.application.gallery.model.GalleryDef.LIMIT_720P;
import static com.quvideo.application.gallery.model.GalleryDef.LIMIT_VGA;

public class GalleryFile {

  private static String exportImagePath = null;

  private static final String GIF_SUFFIX = ".gif";

  private static final String APP_DEFAULT_PNG_EXT = ".png";
  private static final String APP_DEFAULT_PHOTO_EXT = ".jpg";

  public static final GSzie OUTPUT_1080P = new GSzie(1920, 1080);
  public static final GSzie OUTPUT_720P = new GSzie(1080, 720);
  public static final GSzie OUTPUT_VGA = new GSzie(640, 480);

  public static String getExportImagePath() {
    if (!TextUtils.isEmpty(exportImagePath)) {
      return exportImagePath;
    }

    // xiaoying path
    String strMainStorage = StorageInfo.getMainStorage();
    String strExtStorage = StorageInfo.getExtStorage();

    String[] storageList = new String[] { strMainStorage, strExtStorage };

    for (String strStorage : storageList) {
      if (strStorage == null) {
        continue;
      }

      if (null != strStorage) {
        exportImagePath = strStorage + GalleryClient.getInstance().getGallerySettings().getExportImagePath();
        return exportImagePath;
      }
    }
    return null;
  }

  public static boolean isGifFile(String path) {
    return path != null && path.toLowerCase().contains(GIF_SUFFIX);
  }

  public static GSzie getOutputLimitSize() {
    GSzie limitSize;
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    int photoLimit = settings.getPhotoLimit();
    switch (photoLimit) {
      case LIMIT_720P:
        limitSize = OUTPUT_720P;
        break;
      case LIMIT_1080P:
        limitSize = OUTPUT_1080P;
        break;
      case LIMIT_VGA:
      default:
        limitSize = OUTPUT_VGA;
        break;
    }
    return limitSize;
  }

  public static boolean isVideoFile(String filePath) {
    if (filePath == null) {
      return false;
    }
    return MediaUtils.isVideoFileType(filePath);
  }

  /**
   * 图片文件处理
   *
   * @param strSrcFile 源文件路径
   * @param strDestPath 目标路径
   * @param needConvertPng jpg/jpeg是否需要转换成png
   * @return 处理后的文件路径
   */
  public static String compressFile(String strSrcFile, String strDestPath,
      boolean needConvertPng) {
    if (strDestPath == null) {
      return null;
    }
    Bitmap bitmap;
    FileOutputStream outStream = null;
    try {
      File fSrc = new File(strSrcFile);
      if (!fSrc.exists()) {
        return null;
      }

      boolean isJpeg =
          MediaUtils.FILE_TYPE_JPEG == MediaUtils.getFileMediaType(strSrcFile);
      if (!needCompress(strSrcFile) && !(isJpeg && needConvertPng)) {
        return strSrcFile;
      }

      String strFilePathNew;
      if (isJpeg && !needConvertPng) {
        strFilePathNew = generateDestFile(strDestPath, strSrcFile, APP_DEFAULT_PHOTO_EXT);
      } else {
        strFilePathNew = generateDestFile(strDestPath, strSrcFile, APP_DEFAULT_PNG_EXT);
      }

      if (strFilePathNew == null) {
        return null;
      }

      bitmap = getImageBitmap(strSrcFile, getOutputLimitSize());
      if (bitmap == null) {
        return null;
      }

      // create import path before save file
      if (!FileUtils.isDirectoryExisted(strDestPath)) {
        FileUtils.createMultilevelDirectory(strDestPath);
      }

      File f = new File(strFilePathNew);
      if (f.exists()) {
        f.delete();
      }

      outStream = new FileOutputStream(new File(strFilePathNew));

      if (isJpeg && !needConvertPng) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        int nOrientation = getFileOrientation(strSrcFile);
        ExifInterface exifInterface = new ExifInterface(strFilePathNew);
        // exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
        // ""+ExifInterface.ORIENTATION_NORMAL);
        exifInterface.setAttribute(ExifInterface.TAG_MODEL, "VivaCut@" + nOrientation);
        exifInterface.saveAttributes();
      } else {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
      }

      return strFilePathNew;
    } catch (Exception ex) {
      return null;
    } finally {
      try {
        if (outStream != null) {
          outStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static boolean needCompress(String filePath) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(filePath, options);
    int outHeight = options.outHeight;
    int outWidth = options.outWidth;

    GSzie maxOutputSize = getOutputLimitSize();
    return outWidth * outHeight > maxOutputSize.width * maxOutputSize.height;
  }

  /**
   * 获取多媒体文件Exif的角度信息；
   */
  public static int getFileOrientation(String filePath) {
    if (filePath == null) {
      return 0;
    }
    int degree = 0;
    if (!isVideoFile(filePath)) {
      ExifInterface exif = null;
      try {
        exif = new ExifInterface(filePath);
      } catch (IOException ex) {

      }
      degree = getOrientation(exif);
    }
    return degree;
  }

  /**
   * 从Exif中提取角度信息
   */
  private static int getOrientation(ExifInterface exif) {
    int degree = 0;

    if (exif != null) {
      int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
      if (orientation != -1) {
        // We only recognize a subset of orientation tag values.
        switch (orientation) {
          case ExifInterface.ORIENTATION_ROTATE_90:
            degree = 90;
            break;
          case ExifInterface.ORIENTATION_ROTATE_180:
            degree = 180;
            break;
          case ExifInterface.ORIENTATION_ROTATE_270:
            degree = 270;
            break;
          default:
            break;
        }
      }
    }
    return degree;
  }

  /**
   * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
   * object when decoding bitmaps using the decode* methods from
   * {@link BitmapFactory}. This implementation calculates the closest
   * inSampleSize that will result in the final decoded bitmap having a width
   * and height equal to or larger than the requested width and height. This
   * implementation does not ensure a power of 2 is returned for inSampleSize
   * which can be faster when decoding but results in a larger bitmap which
   * isn't as useful for caching purposes.
   *
   * @param options An options object with out* params already populated (run
   * through a decode* method with inJustDecodeBounds==true
   * @param reqWidth The requested width of the resulting bitmap
   * @param reqHeight The requested height of the resulting bitmap
   * @return The value to be used for inSampleSize
   */
  private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
      int reqHeight, boolean bNeedRound) {
    // Raw height and width of image
    int height = options.outHeight;
    int width = options.outWidth;
    if (reqWidth == 0) {
      reqWidth = Math.min(OUTPUT_VGA.width, width);
    }

    if (reqHeight == 0) {
      reqHeight = Math.min(OUTPUT_VGA.height, height);
    }

    int inSampleSize = 0;
    boolean bNeedSwap = false;
    if (height > width) {
      if (reqWidth > reqHeight) {
        bNeedSwap = true;
      }
    } else {
      if (reqWidth < reqHeight) {
        bNeedSwap = true;
      }
    }

    if (bNeedSwap) {
      //swap
      int nTmp = reqWidth;
      reqWidth = reqHeight;
      reqHeight = nTmp;
    }

    if (height > reqHeight || width > reqWidth) {
      int nSampleSize1 = 0;
      int nSampleSize2 = 0;
      if (bNeedRound) {
        nSampleSize1 = Math.round(1.0f * height / reqHeight);
        nSampleSize2 = Math.round(1.0f * width / reqWidth);
      } else {
        nSampleSize1 = height / reqHeight;
        nSampleSize2 = width / reqWidth;
      }
      inSampleSize = Math.min(nSampleSize1, nSampleSize2);
    }
    return inSampleSize;
  }

  private static Bitmap getImageBitmap(String filePath, GSzie outputMinSize) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(filePath, options);

    return bilinearDecodeBitmapFromImageFile(filePath, outputMinSize.width, outputMinSize.height,
        true);
  }

  private static String generateDestFile(String strDestPath, String strSrcFile,
      String strDefaultExt) {
    String strFileName;
    int index2 = strSrcFile.lastIndexOf('.');
    if (index2 < 0) {
      return null;
    }
    int index1 = strSrcFile.lastIndexOf('/');
    strFileName = strSrcFile.substring(index1 + 1, index2);

    String strExt = strDefaultExt;
    if (strExt == null) {
      strExt = strSrcFile.substring(index2);
    }
    int index = 0;
    do {
      index++;
      String strDestFile = strDestPath + strFileName + "_" + index + strExt;
      File file = new File(strDestFile);
      if (!(file.isFile() && file.exists())) {
        return file.getAbsolutePath();
      }
    } while (true);
  }

  /**
   * get a bitmap use bilinear method.
   */
  private static synchronized Bitmap bilinearDecodeBitmapFromImageFile(String filename,
      int reqWidth, int reqHeight, boolean bKeepRatio) {
    try {
      Bitmap bitmapOri = null;
      int nDegrees = getFileOrientation(filename);

      // First decode with inJustDecodeBounds=true to check dimensions
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(filename, options);

      // Calculate inSampleSize
      int inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, false);
      options.inJustDecodeBounds = false;
      options.inSampleSize = inSampleSize;
      Bitmap bitmap = BitmapFactory.decodeFile(filename, options);
      if (bitmap != null) {
        int nDecWidth = bitmap.getWidth();
        int nDecHeight = bitmap.getHeight();
        if (nDecHeight * 9 >= nDecWidth * 10) {
          int nTmp = reqWidth;
          reqWidth = reqHeight;
          reqHeight = nTmp;
        }
        {
          int nCropW = 0;
          int nCropH = 0;
          if (bKeepRatio) {
            nCropW = nDecWidth >> 2 << 2;
            nCropH = nDecHeight >> 2 << 2;
            reqWidth = nCropW;
            reqHeight = nCropH;
          } else {
            if (reqWidth * nDecHeight >= reqHeight * nDecWidth) {
              nCropW = nDecWidth;
              nCropH = (nDecWidth * reqHeight / reqWidth) >> 2 << 2;
            } else {
              nCropH = nDecHeight;
              nCropW = (nDecHeight * reqWidth / reqHeight) >> 2 << 2;
            }
          }

          //make sure crop size and offset is right
          nCropW = Math.min(nDecWidth, nCropW);
          nCropH = Math.min(nDecHeight, nCropH);

          int nOffsetX = (((nDecWidth - nCropW) / 2) >> 2) << 2;
          int nOffsetY = (((nDecHeight - nCropH) / 2) >> 2) << 2;

          Bitmap bitmapCrop = Bitmap.createBitmap(bitmap, nOffsetX, nOffsetY, nCropW, nCropH);
          bitmapOri = Bitmap.createScaledBitmap(bitmapCrop, reqWidth, reqHeight, false);
          if (bitmapOri != bitmapCrop) {
            bitmapCrop.recycle();
            bitmapCrop = null;
          }

          if (bitmapCrop != bitmap) {
            if (!bitmap.isRecycled()) {
              bitmap.recycle();
            }
          }
        }
      }

      //bitmapOri = getImageThumbFromFile(filename, reqWidth, reqHeight);
      if (bitmapOri == null || bitmapOri.getHeight() <= 0 || bitmapOri.getWidth() <= 0) {
        return null;
      }

      Bitmap bitmapRotation = null;
      Matrix matrix = new Matrix();
      matrix.postRotate(nDegrees);
      try {
        //must be 4 multiple, otherwise C layer crash
        bitmapRotation = Bitmap.createBitmap(bitmapOri, 0, 0, bitmapOri.getWidth() >> 2 << 2,
            bitmapOri.getHeight() >> 2 << 2, matrix, true);
      } catch (Throwable ignore) {
      } finally {
        if (bitmapOri != bitmapRotation) {
          bitmapOri.recycle();
        }
      }

      return bitmapRotation;
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return null;
  }
}
