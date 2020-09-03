package com.quvideo.application.download;

public interface ITemplateDownload {

  /**
   * 下载
   */
  void download(String templateCode, String url, TemplateDownloadListener listener);

  /**
   * 取消下载
   */
  void cancelDownload(String templateCode);

  interface TemplateDownloadListener {

    void onProgress(String templateCode, int progress);

    void onSuccess(String templateCode);

    void onFailed(String templateCode, int errorCode, String errorMsg);
  }
}
