package com.quvideo.application.export;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
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
import com.quvideo.mobile.engine.export.IExportListener;
import com.quvideo.mobile.engine.model.export.ExportParams;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.slide.ISlideWorkSpace;

public class ExportDialog {

  private ProgressDialog loadingDialog = null;
  private TextView loadingTitle;

  private Activity mActivity;

  private String thumbnail;

  public ExportDialog() {
  }

  public void showExporting(Activity activity, String thumbnail, ExportParams exportParams, ISlideWorkSpace workSpace) {
    showLoading(activity);
    this.thumbnail = thumbnail;
    workSpace.startExport(exportParams, mIExportListener);
  }

  public void showExporting(Activity activity, String thumbnail, ExportParams exportParams, IQEWorkSpace workSpace) {
    showLoading(activity);
    this.thumbnail = thumbnail;
    workSpace.startExport(exportParams, mIExportListener);
  }

  private void showLoading(Activity activity) {
    this.mActivity = activity;
    if (loadingDialog != null) {
      dismissLoading();
    }
    loadingDialog = new ProgressDialog(activity, R.style.MyAlertDialogStyle);
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
      loadingTitle.setVisibility(View.VISIBLE);
      loadingTitle.setText("开始导出...");

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
    }

    @Override public void onExportRunning(int percent) {
      if (loadingTitle != null) {
        loadingTitle.setText("导出中:" + percent + "%");
      }
    }

    @Override public void onExportSuccess(String exportPath) {
      PreviewActivity.go2PreviewActivity(mActivity, thumbnail, exportPath);
      dismissLoading();
    }

    @Override public void onExportCancel() {
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(), R.string.mn_edit_tips_export_cancel, Toast.LENGTH_LONG);
      dismissLoading();
    }

    @Override public void onExportFailed(int nErrCode, String errMsg) {
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(), "导出失败：errCode=" + nErrCode + ", errMsg=" + errMsg, Toast.LENGTH_LONG);
      dismissLoading();
    }

    @Override public void onProducerReleased() {
    }
  };
}
