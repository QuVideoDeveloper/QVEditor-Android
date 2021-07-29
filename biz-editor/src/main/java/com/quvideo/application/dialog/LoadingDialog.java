package com.quvideo.application.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quvideo.application.editor.R;

public class LoadingDialog {

  private ProgressDialog loadingDialog = null;
  private TextView loadingTitle;

  private Activity mActivity;

  public void showDownloading(Activity activity) {
    showLoading(activity);
  }

  private void showLoading(Activity activity) {
    this.mActivity = activity;
    if (loadingDialog != null) {
      dismissLoading();
    }
    loadingDialog = new ProgressDialog(activity, R.style.DimDisAlertDialogStyle);
    loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    try {
      Window window = loadingDialog.getWindow();
      WindowManager manager = window.getWindowManager();
      DisplayMetrics outMetrics = new DisplayMetrics();
      manager.getDefaultDisplay().getMetrics(outMetrics);
      window.getDecorView().setPadding(0, 0, 0, 0);
      window.setBackgroundDrawableResource(android.R.color.transparent);
      ViewGroup root = window.getDecorView().findViewById(android.R.id.content);
      //设置窗口大小为屏幕大小
      WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
      Point screenSize = new Point();
      wm.getDefaultDisplay().getSize(screenSize);
      root.setLayoutParams(new LinearLayout.LayoutParams(screenSize.x, screenSize.y));

      loadingDialog.show();
    } catch (Throwable e) {
      e.printStackTrace();
      return;
    }
    try {
      loadingDialog.setContentView(R.layout.dialogue_loading_content_layout);
      loadingTitle = loadingDialog.findViewById(R.id.tv_title);
      ImageView loadingIv = loadingDialog.findViewById(R.id.iv_loading);

      loadingTitle.setVisibility(View.VISIBLE);
      loadingTitle.setText(R.string.mn_app_brvah_loading);

      Glide.with(activity).load(R.drawable.loading_icon).listener(new RequestListener<Drawable>() {
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

      loadingDialog.setCancelable(false);
      loadingDialog.setCanceledOnTouchOutside(false);
    } catch (Exception ignore) {
    }
  }

  public void dismissLoading() {
    if (null != loadingDialog) {
      try {
        loadingDialog.dismiss();
      } catch (Exception ignore) {
      }
    }
    loadingDialog = null;
    loadingTitle = null;
    if (mActivity != null) {
      mActivity = null;
    }
  }
}
