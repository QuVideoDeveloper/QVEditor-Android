package com.quvideo.application.utils.image;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
    LoaderScaleType.NULL, LoaderScaleType.CENTER_CROP, LoaderScaleType.FIT_CENTER,
    LoaderScaleType.CENTER_INSIDE, LoaderScaleType.CIRCLE_CROP, LoaderScaleType.CUSTOM_TRANSFORM
}) @Retention(RetentionPolicy.SOURCE) public @interface LoaderScaleType {

  int NULL = -1;
  int FIT_CENTER = 2;
  int CIRCLE_CROP = 3;
  int CENTER_INSIDE = 5;
  int CENTER_CROP = 6;
  int CUSTOM_TRANSFORM = 999;
}