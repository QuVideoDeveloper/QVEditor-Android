package com.quvideo.application.utils.image;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author wangjieming
 * @date 2019-05-17.
 */
public class ImageLoader {

  public static void loadImage(int placeHolder, String url, ImageView imageView) {
    if (imageView == null) {
      return;
    }
    if (GlideUtils.isActFinish(imageView.getContext())) {
      return;
    }
    RequestOptions requestOptions =
        new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(placeHolder);
    Glide.with(imageView.getContext()).load(url).apply(requestOptions).into(imageView);
  }

  public static void loadImage(int placeHolder, String url, ImageView imageView,
      Transformation transformation) {
    if (imageView == null) {
      return;
    }
    if (GlideUtils.isActFinish(imageView.getContext())) {
      return;
    }

    RequestOptions requestOptions =
        GlideUtils.appendCommonRequestOptions(null, LoaderScaleType.CUSTOM_TRANSFORM,
            transformation).placeholder(placeHolder);
    Glide.with(imageView.getContext()).load(url).apply(requestOptions).into(imageView);
  }

  public static void loadImage(@RawRes @DrawableRes int resId, ImageView imageView) {
    if (imageView == null) {
      return;
    }
    if (GlideUtils.isActFinish(imageView.getContext())) {
      return;
    }
    RequestOptions requestOptions =
        GlideUtils.appendCommonRequestOptions(null, LoaderScaleType.NULL, null);
    Glide.with(imageView.getContext())
        .load(resId)
        .apply(GlideUtils.appendCommonRequestOptions(requestOptions, LoaderScaleType.NULL, null))
        .into(imageView);
  }

  public static void loadImage(final String url, final ImageView imageView) {
    if (imageView == null) {
      return;
    }
    if (GlideUtils.isActFinish(imageView.getContext())) {
      return;
    }
    RequestOptions requestOptions =
        GlideUtils.appendCommonRequestOptions(null, LoaderScaleType.NULL, null);
    Glide.with(imageView.getContext())
        .load(url)
        .apply(GlideUtils.appendCommonRequestOptions(requestOptions, LoaderScaleType.NULL, null))
        .into(imageView);
  }

  public static void loadImage(final String url, final ImageView imageView,
      Transformation transformation) {
    if (imageView == null) {
      return;
    }
    if (GlideUtils.isActFinish(imageView.getContext())) {
      return;
    }
    RequestOptions requestOptions =
        GlideUtils.appendCommonRequestOptions(null, LoaderScaleType.CUSTOM_TRANSFORM,
            transformation);
    Glide.with(imageView.getContext()).load(url).apply(requestOptions).into(imageView);
  }

  public static void loadImage(final Uri uri, final ImageView imageView,
      Transformation transformation) {
    if (imageView == null) {
      return;
    }
    if (GlideUtils.isActFinish(imageView.getContext())) {
      return;
    }
    RequestOptions requestOptions =
        GlideUtils.appendCommonRequestOptions(null, LoaderScaleType.CUSTOM_TRANSFORM,
            transformation);
    Glide.with(imageView.getContext()).load(uri).apply(requestOptions).into(imageView);
  }

  public static void loadImage(final int resId, final ImageView imageView,
      Transformation transformation) {
    if (imageView == null) {
      return;
    }
    if (GlideUtils.isActFinish(imageView.getContext())) {
      return;
    }
    RequestOptions requestOptions =
        GlideUtils.appendCommonRequestOptions(null, LoaderScaleType.CUSTOM_TRANSFORM,
            transformation);
    Glide.with(imageView.getContext()).load(resId).apply(requestOptions).into(imageView);
  }

  public static void resumeRequest(Context ctx) {
    Glide.with(ctx).resumeRequests();
  }

  public static void pauseRequest(Context ctx) {
    Glide.with(ctx).pauseRequests();
  }

  public static void listenScrollState(RecyclerView recyclerView, int newState) {
    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
      resumeRequest(recyclerView.getContext());
    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
      resumeRequest(recyclerView.getContext());
    } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
      pauseRequest(recyclerView.getContext());
    }
  }

  public static void clearCache(Context context) {
    Glide.get(context).clearMemory();
  }
}
