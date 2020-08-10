package com.quvideo.application.template;

import android.app.Activity;

public abstract class SimpleTemplate {

  private long templateId;
  private String title;
  public int thumbnailResId;

  public SimpleTemplate(long templateId, String title, int thumbnailResId) {
    this.templateId = templateId;
    this.title = title;
    this.thumbnailResId = thumbnailResId;
  }

  public SimpleTemplate(long templateId) {
    this.templateId = templateId;
  }

  public long getTemplateId() {
    return templateId;
  }

  public String getTitle() {
    return title;
  }

  public int getThumbnailResId() {
    return thumbnailResId;
  }

  public abstract void onClick(Activity activity);
}
