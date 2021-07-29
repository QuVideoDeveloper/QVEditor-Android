package com.quvideo.application.editor.sound;

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
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.QETools;
import com.quvideo.mobile.engine.export.IExportListener;
import com.quvideo.mobile.engine.model.export.ExportParams;
import com.quvideo.mobile.engine.project.IQEWorkSpace;

public class AudioDirectDialog {

  private ProgressDialog loadingDialog = null;
  private TextView loadingTitle;

  private Activity mActivity;

  private IExportListener mExportListener;

  public AudioDirectDialog(Activity activity, IExportListener exportListener) {
    this.mExportListener = exportListener;
    showLoading(activity);
  }

  public void beginAudioDirecting(String srcPath, ExportParams exportParams, IQEWorkSpace workSpace) {
    QETools.directAudio(srcPath, exportParams, mIExportListener);
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
      ImageView loadingIv = loadingDialog.findViewById(R.id.iv_loading);

      loadingTitle = loadingDialog.findViewById(R.id.tv_title);
      loadingTitle.setVisibility(View.VISIBLE);
      loadingTitle.setText(R.string.mn_edit_audio_direct);

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

  /**
   * 导出视频监听器
   */
  private IExportListener mIExportListener = new IExportListener() {
    @Override public void onExportReady() {
      if (mExportListener != null) {
        mExportListener.onExportReady();
      }
    }

    @Override public void onExportRunning(int percent) {
      if (loadingTitle != null) {
        String text = EditorApp.Companion.getInstance().app.getString(R.string.mn_edit_audio_direct)
            + percent + "%";
        loadingTitle.setText(text);
      }
      if (mExportListener != null) {
        mExportListener.onExportRunning(percent);
      }
    }

    @Override public void onExportSuccess(String exportPath) {
      dismissLoading();
      if (mExportListener != null) {
        mExportListener.onExportSuccess(exportPath);
      }
    }

    @Override public void onExportCancel() {
      dismissLoading();
      if (mExportListener != null) {
        mExportListener.onExportCancel();
      }
    }

    @Override public void onExportFailed(int nErrCode, String errMsg) {
      String text = EditorApp.Companion.getInstance().app.getString(R.string.mn_edit_export_failed)
          + " ：errCode=" + nErrCode + ", errMsg=" + errMsg;
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(), text, Toast.LENGTH_LONG);
      dismissLoading();
      if (mExportListener != null) {
        mExportListener.onExportFailed(nErrCode, errMsg);
      }
    }

    @Override public void onProducerReleased() {
      if (mExportListener != null) {
        mExportListener.onProducerReleased();
      }
    }
  };
}
