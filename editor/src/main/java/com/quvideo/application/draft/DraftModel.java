package com.quvideo.application.draft;

public class DraftModel implements Comparable<DraftModel> {
  public long id;
  public String projectUrl;
  public long createTime;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getProjectUrl() {
    return projectUrl;
  }

  public void setProjectUrl(String projectUrl) {
    this.projectUrl = projectUrl;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  @Override public int compareTo(DraftModel o) {
    if (id > o.id) {
      return -1;
    } else {
      return 1;
    }
  }
}
