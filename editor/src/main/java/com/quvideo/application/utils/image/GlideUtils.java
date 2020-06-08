package com.quvideo.application.utils.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author wangjieming
 * @date 2019-05-17.
 */
public class GlideUtils {

  /**
   * 加载之前处理的通用参数
   * 1.指定diskCacheStrategy RESOURCE
   * 2.处理ScaleType Transformation 动图几个的关系
   */
  public static RequestOptions appendCommonRequestOptions(RequestOptions requestOptions,
      @LoaderScaleType int type, Transformation<Bitmap> customTransform) {
    if (requestOptions == null) {
      requestOptions = new RequestOptions();
    }
    Transformation<Bitmap> transformation;
    if (customTransform != null && type == LoaderScaleType.CUSTOM_TRANSFORM) {
      transformation = customTransform;
    } else {
      transformation = GlideUtils.newScaleTypeTransform(type);
    }
    if (transformation != null) {
      requestOptions = requestOptions.optionalTransform(transformation)
          .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(transformation));
    }

    requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    return requestOptions;
  }

  /**
   * 根据设置添加ScaleType 默认是CENTER_CROP
   */
  private static Transformation<Bitmap> newScaleTypeTransform(@LoaderScaleType int type) {
    //webp的图片单独处理
    Transformation<Bitmap> transformation;
    switch (type) {
      case LoaderScaleType.FIT_CENTER:
        transformation = new FitCenter();
        break;
      case LoaderScaleType.CENTER_INSIDE:
        transformation = new CenterInside();
        break;
      case LoaderScaleType.CIRCLE_CROP:
        transformation = new CircleCrop();
        break;
      case LoaderScaleType.NULL:
      case LoaderScaleType.CENTER_CROP:
      case LoaderScaleType.CUSTOM_TRANSFORM:
      default:
        transformation = new CenterCrop();
        break;
    }
    return transformation;
  }

  /**
   * 如果是Activity，判断是否销毁
   */
  static boolean isActFinish(Context context) {
    if (context instanceof Activity) {
      if (((Activity) context).isFinishing()) {
        return true;
      } else {
        return ((Activity) context).isDestroyed();
      }
    }
    return false;
  }
}
