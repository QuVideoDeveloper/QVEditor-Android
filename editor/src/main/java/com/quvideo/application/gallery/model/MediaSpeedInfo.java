package com.quvideo.application.gallery.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;
import java.util.List;

/**
 * Create by zhengjunfei on 2020-02-28
 */
public class MediaSpeedInfo implements Parcelable {
  public static final int ERROR_DURATION = -1;

  private int limitVideoSpeed; //最高变速倍数
  private String limitAddText; //不能添加的文案
  private String limitExchangeText; //不能交换的文案
  private String chooseId; //要替换的位置的id，代表未选择
  private String limitClickText; //当前选中位置重复点击需要的提示的文案
  private String limitFullText; //当前填充满的时候的提示文案
  private String templateTtid; //模板id
  private String templateTtTitle; //模板名称
  private List<MediaModel> list; //每个坑位信息

  public long getPitDuration(int pos) {
    if (pos < 0 || null == list || list.size() - 1 < pos || null == list.get(pos)) {
      return ERROR_DURATION;
    }

    return list.get(pos).getPitDuration();
  }

  public List<MediaModel> getList() {
    return list;
  }

  public void setList(List<MediaModel> list) {
    this.list = list;
  }

  public String toString() {
    return new Gson().toJson(this);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.limitVideoSpeed);
    dest.writeString(this.limitAddText);
    dest.writeString(this.limitExchangeText);
    dest.writeString(this.chooseId);
    dest.writeString(this.limitClickText);
    dest.writeString(this.limitFullText);
    dest.writeString(this.templateTtid);
    dest.writeString(this.templateTtTitle);
    dest.writeTypedList(this.list);
  }

  protected MediaSpeedInfo(Parcel in) {
    this.limitVideoSpeed = in.readInt();
    this.limitAddText = in.readString();
    this.limitExchangeText = in.readString();
    this.chooseId = in.readString();
    this.limitClickText = in.readString();
    this.limitFullText = in.readString();
    this.templateTtid = in.readString();
    this.templateTtTitle = in.readString();
    this.list = in.createTypedArrayList(MediaModel.CREATOR);
  }

  public static final Creator<MediaSpeedInfo> CREATOR = new Creator<MediaSpeedInfo>() {
    @Override public MediaSpeedInfo createFromParcel(Parcel source) {
      return new MediaSpeedInfo(source);
    }

    @Override public MediaSpeedInfo[] newArray(int size) {
      return new MediaSpeedInfo[size];
    }
  };
}
