package com.quvideo.application.gallery.utils;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * @desc A {@link BitmapTransformation} which rotate the Angle of a bitmap.
 */
public class RotateTransformation extends BitmapTransformation {

  private static final String ID = "com.vivavideo.gallery.util.RotateTransformation";
  private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

  private int rotateRotationAngle;

  public RotateTransformation(int rotateRotationAngle) {
    super();
    this.rotateRotationAngle = rotateRotationAngle;
  }

  @Override
  protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth,
      int outHeight) {
    return TransformationUtils.rotateImage(toTransform, rotateRotationAngle);
  }

  @Override public boolean equals(Object o) {
    return o instanceof CenterCrop;
  }

  @Override public int hashCode() {
    return ID.hashCode();
  }

  @Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    messageDigest.update(ID_BYTES);

    byte[] radiusData = ByteBuffer.allocate(4).putInt(rotateRotationAngle).array();
    messageDigest.update(radiusData);
  }
}
