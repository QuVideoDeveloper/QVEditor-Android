package com.quvideo.application.download;

import android.text.TextUtils;
import android.util.Log;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.quvideo.application.EditorApp;
import com.quvideo.application.StorageUtils;
import com.quvideo.mobile.component.template.XytInstallListener;
import com.quvideo.mobile.component.template.XytManager;
import java.util.HashMap;
import java.util.List;
import okhttp3.OkHttpClient;

/**
 * @date 2019-11-27.
 * 素材下载+安装管理
 */
public class TemplateDownloadImpl implements ITemplateDownload {

  public static final String TAG = TemplateDownloadImpl.class.getSimpleName();

  public static final int ERROR_INIT = -999;
  public static final int ERROR_XYT_ERROR = -998;
  public static final int ERROR_DOWNLOAD_ERROR = -997;
  /**
   * URL做Key,
   */
  private HashMap<String, String> mDownloadingMap = new HashMap<>();

  /**
   * 下载路径，RootDir
   */
  private static final String SAVE_DIR_BASE = StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().app);

  public TemplateDownloadImpl() {
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
        .build();
    AndroidNetworking.initialize(EditorApp.Companion.getInstance().app, okHttpClient);
  }

  @Override public void download(String templateCode, String url, ITemplateDownload.TemplateDownloadListener listener) {
    if (TextUtils.isEmpty(templateCode)) {
      if (listener != null) {
        listener.onFailed(templateCode, ERROR_INIT,
            "templateChild == null || mQETemplateInfo == null || templateCode == null");
      }
      return;
    }
    if (mDownloadingMap.get(url) != null) {
      //正在下载中
      return;
    }
    mDownloadingMap.put(url, templateCode);
    String fileName = templateCode + ".zip";
    String filePath = SAVE_DIR_BASE + fileName;

    AndroidNetworking.download(url, SAVE_DIR_BASE, fileName)
        .setPriority(Priority.MEDIUM)
        .setTag(templateCode)
        .build()
        .setDownloadProgressListener(new TemplateProgressListener(templateCode, url, listener))
        .startDownload(new TemplateDownloadListener(templateCode, url, filePath, listener));
  }

  @Override public void cancelDownload(String templateCode) {
    AndroidNetworking.cancel(templateCode);
  }

  private static class TemplateProgressListener implements DownloadProgressListener {

    private String templateCode;
    private String url;
    private ITemplateDownload.TemplateDownloadListener listener;

    TemplateProgressListener(String templateCode, String url, ITemplateDownload.TemplateDownloadListener listener) {
      this.templateCode = templateCode;
      this.url = url;
      this.listener = listener;
    }

    @Override public void onProgress(long bytesDownloaded, long totalBytes) {
      if (listener != null) {
        listener.onProgress(templateCode, (int) (bytesDownloaded * 100 / totalBytes));
      }
    }
  }

  private class TemplateDownloadListener implements DownloadListener {

    private String templateCode;
    private String filePath;
    private String url;
    ITemplateDownload.TemplateDownloadListener listener;

    TemplateDownloadListener(String templateCode, String url, String filePath,
        ITemplateDownload.TemplateDownloadListener listener) {
      this.templateCode = templateCode;
      this.filePath = filePath;
      this.url = url;
      this.listener = listener;
    }

    @Override public void onDownloadComplete() {
      //下载完成,安装素材
      XytManager.install(filePath, new XytInstallListener() {
        @Override public void onSuccess() {
          //安装成功再移除
          if (mDownloadingMap != null) {
            mDownloadingMap.remove(url);
          }
          if (listener != null) {
            listener.onSuccess(templateCode);
          }
        }

        @Override public void onFailed(List<String> filePaths, int errorCode) {
          //安装失败再移除
          if (mDownloadingMap != null) {
            mDownloadingMap.remove(url);
          }
          if (listener != null) {
            listener.onFailed(templateCode, ERROR_XYT_ERROR,
                "Xyt Install Error [" + errorCode + "]");
          }
        }
      });
    }

    @Override public void onError(ANError anError) {
      //网络失败再移除
      if (mDownloadingMap != null) {
        mDownloadingMap.remove(url);
      }
      Log.d(TAG, "onError:"
          + anError.getErrorCode()
          + ",getErrorDetail="
          + anError.getErrorDetail()
          + ",getErrorDetail="
          + anError.getErrorDetail()
          + ",getErrorBody="
          + anError.getErrorBody());
      if (listener != null) {
        listener.onFailed(templateCode, ERROR_DOWNLOAD_ERROR,
            "Template Download Error[" + anError.getErrorCode() + "," + anError.getMessage() + "]");
      }
    }
  }
}
