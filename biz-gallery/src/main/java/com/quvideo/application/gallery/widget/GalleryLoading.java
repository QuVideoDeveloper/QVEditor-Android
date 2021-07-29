package com.quvideo.application.gallery.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quvideo.application.gallery.R;

public class GalleryLoading {

  private static ProgressDialog loadingDialog = null;
  private static TextView loadingTitle;

  public static synchronized void showLoading(Context context) {
    showLoading(context, null, false);
  }

  public static synchronized void showLoading(Context context, String message) {
    showLoading(context, message, false);
  }

  public static synchronized void showLoading(Context context, String message,
      boolean cancelable) {
    if (loadingDialog != null) {
      dismissLoading();
    }
    if (!isActivityAlive(context)) {
      return;
    }

    loadingDialog = new ProgressDialog(context, R.style.LoadingStyle);
    loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    try {
      loadingDialog.show();
    } catch (Throwable e) {
      e.printStackTrace();
      return;
    }
    try {
      loadingDialog.setContentView(R.layout.dialogue_loading_content_layout);
      ImageView loadingIv = loadingDialog.findViewById(R.id.iv_loading);

      loadingTitle = loadingDialog.findViewById(R.id.tv_title);
      if (!TextUtils.isEmpty(message)) {
        loadingTitle.setVisibility(View.VISIBLE);
        loadingTitle.setText(message);
      } else {
        loadingTitle.setVisibility(View.GONE);
      }

      Glide.with(context).load(R.drawable.loading_icon).listener(new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable final GlideException e, final Object model,
            final Target<Drawable> target, final boolean isFirstResource) {
          return false;
        }

        @Override public boolean onResourceReady(final Drawable resource, final Object model,
            final Target<Drawable> target, final DataSource dataSource,
            final boolean isFirstResource) {
          if (resource instanceof WebpDrawable) {
            final WebpDrawable webpDrawable = ((WebpDrawable) resource);
            webpDrawable.start();
          }
          return false;
        }
      }).into(loadingIv);

      loadingDialog.setCancelable(cancelable);
      loadingDialog.setCanceledOnTouchOutside(false);
    } catch (Exception ignore) {
    }
  }

  public static synchronized boolean isShowing() {
    return loadingDialog != null && loadingDialog.isShowing();
  }

  public static void updateLoadingTitle(String title) {
    if (loadingTitle != null) {
      loadingTitle.setText(title);
    }
  }

  public static synchronized void dismissLoading() {
    if (null != loadingDialog) {
      try {
        loadingDialog.dismiss();
      } catch (Exception ignore) {
      }
    }
    loadingDialog = null;
    loadingTitle = null;
  }

  private static boolean isActivityAlive(Context context) {
    return !(!(context instanceof Activity)
        || ((Activity) context).getWindow() == null
        || ((Activity) context).isFinishing());
  }
}
