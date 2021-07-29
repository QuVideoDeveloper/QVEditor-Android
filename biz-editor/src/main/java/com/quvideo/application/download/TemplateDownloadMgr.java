package com.quvideo.application.download;

/**
 * @date 2019-10-23.
 * 素材接口
 */
public class TemplateDownloadMgr {

  //素材下载接口
  private ITemplateDownload sTemplateDownload;

  private volatile static TemplateDownloadMgr instanse;

  private TemplateDownloadMgr() {
    sTemplateDownload = new TemplateDownloadImpl();
  }

  public static TemplateDownloadMgr getInstanse() {
    if (instanse == null) {
      synchronized (TemplateDownloadMgr.class) {
        if (instanse == null) {
          instanse = new TemplateDownloadMgr();
        }
      }
    }
    return instanse;
  }

  public void download(String templateCode, String url, ITemplateDownload.TemplateDownloadListener listener) {
    sTemplateDownload.download(templateCode, url, listener);
  }

  /**
   * 释放缓存
   */
  public void release() {
  }
}
