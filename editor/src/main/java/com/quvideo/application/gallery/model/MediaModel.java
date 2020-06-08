package com.quvideo.application.gallery.model;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/2/2019
 */
public class MediaModel implements Parcelable {
  private String id;
  private @GalleryDef.SourceType int sourceType;
  private @GalleryDef.MediaViewType int mediaViewType;
  private int order;
  private String filePath;

  private long duration;
  private long pitDuration;//坑位对应的时间
  private int rotation;
  private String rawFilepath;

  private GRange rangeInFile;
  private GRange cropRange; //业务方裁剪的range
  public Boolean cropped = false;
  /**
   * the crop rect when has cropped
   */
  public RectF cropRect = null;

  public MediaModel(){}

  private MediaModel(Builder builder) {
    id = builder.id;
    sourceType = builder.sourceType;
    mediaViewType = builder.mediaViewType;
    order = builder.order;
    filePath = builder.filePath;
    duration = builder.duration;
    pitDuration = builder.pitDuration;
    rotation = builder.rotation;
    rawFilepath = builder.rawFilepath;
    rangeInFile = builder.rangeInFile;
    cropRange = builder.cropRange;
    cropped = builder.cropped;
    cropRect = builder.cropRect;
  }

  public MediaModel copy(){
    MediaModel cloneModel = new MediaModel();
    cloneModel.id = this.id;
    cloneModel.sourceType = this.sourceType;
    cloneModel.mediaViewType = this.mediaViewType;
    cloneModel.order = this.order;
    cloneModel.filePath = this.filePath;
    cloneModel.duration = this.duration;
    cloneModel.pitDuration = this.pitDuration;
    cloneModel.rotation = this.rotation;
    cloneModel.rawFilepath = this.rawFilepath;
    cloneModel.rangeInFile = this.rangeInFile;
    cloneModel.cropRange = this.cropRange;
    cloneModel.cropped = this.cropped;
    cloneModel.cropRect = this.cropRect;
    return cloneModel;
  }

  public void cover(MediaModel mediaModel){
    this.id = mediaModel.id;
    this.sourceType = mediaModel.sourceType;
    this.mediaViewType = mediaModel.mediaViewType;
    this.order = mediaModel.order;
    this.filePath = mediaModel.filePath;
    this.duration = mediaModel.duration;
    this.pitDuration = mediaModel.pitDuration;
    this.rotation = mediaModel.rotation;
    this.rawFilepath = mediaModel.rawFilepath;
    this.rangeInFile = mediaModel.rangeInFile;
    this.cropRange = mediaModel.cropRange;
    this.cropped = mediaModel.cropped;
    this.cropRect = mediaModel.cropRect;
  }

  public void coverItem(MediaModel mediaModel){
    this.sourceType = mediaModel.sourceType;
    this.mediaViewType = mediaModel.mediaViewType;
    this.order = mediaModel.order;
    this.filePath = mediaModel.filePath;
    this.duration = mediaModel.duration;
    this.rotation = mediaModel.rotation;
    this.rawFilepath = mediaModel.rawFilepath;
    this.rangeInFile = mediaModel.rangeInFile;
    this.cropRange = mediaModel.cropRange;
    this.cropped = mediaModel.cropped;
    this.cropRect = mediaModel.cropRect;
  }

  public void clearMedia(){
    this.sourceType = 0;
    this.mediaViewType = 0;
    this.order = 0;
    this.filePath = null;
    this.duration = 0;
    this.rotation = 0;
    this.rawFilepath = null;
    this.rangeInFile = null;
    this.cropRange = null;
    this.cropped = false;
    this.cropRect = null;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Boolean isCropped() {
    return cropped;
  }

  public void setCropped(Boolean cropped) {
    this.cropped = cropped;
  }

  public RectF getCropRect() {
    return cropRect;
  }

  public void setCropRect(RectF cropRect) {
    this.cropRect = cropRect;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public void setRangeInFile(GRange rangeInFile) {
    this.rangeInFile = rangeInFile;
  }

  public GRange getCropRange() {
    return cropRange;
  }

  public void setCropRange(GRange cropRange) {
    this.cropRange = cropRange;
  }

  public void setRawFilepath(String rawFilepath) {
    this.rawFilepath = rawFilepath;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public int getSourceType() {
    return sourceType;
  }

  public int getOrder() {
    return order;
  }

  public String getFilePath() {
    return filePath;
  }

  public long getDuration() {
    return duration;
  }

  public long getPitDuration() {
    return pitDuration;
  }

  public void setPitDuration(long pitDuration) {
    this.pitDuration = pitDuration;
  }

  public int getRotation() {
    return rotation;
  }

  public void setRotation(int rotation) {
    this.rotation = rotation;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public void setSourceType(int sourceType) {
    this.sourceType = sourceType;
  }

  public String getRawFilepath() {
    return rawFilepath;
  }

  public GRange getRangeInFile() {
    return rangeInFile;
  }

  public int getMediaViewType() {
    return mediaViewType;
  }

  public void setMediaViewType(int mediaViewType) {
    this.mediaViewType = mediaViewType;
  }

  public String toString(){
    return new Gson().toJson(this);
  }

  public static final class Builder implements Parcelable {
    private String id;
    private int sourceType;
    private int mediaViewType;
    private int order;
    private String filePath;
    private long duration;
    private long pitDuration;
    private int rotation;
    private String rawFilepath;
    private GRange rangeInFile;
    private GRange cropRange;
    private Boolean cropped;
    private RectF cropRect;

    public Builder() {
    }

    public Builder id(String val) {
      id = val;
      return this;
    }

    public Builder sourceType(int val) {
      sourceType = val;
      return this;
    }

    public Builder mediaViewType(int val) {
      mediaViewType = val;
      return this;
    }

    public Builder order(int val) {
      order = val;
      return this;
    }

    public Builder filePath(String val) {
      filePath = val;
      return this;
    }

    public Builder duration(long val) {
      duration = val;
      return this;
    }

    public Builder pitDuration(long val) {
      pitDuration = val;
      return this;
    }

    public Builder rotation(int val) {
      rotation = val;
      return this;
    }

    public Builder rawFilepath(String val) {
      rawFilepath = val;
      return this;
    }

    public Builder rangeInFile(GRange val) {
      rangeInFile = val;
      return this;
    }

    public Builder cropRange(GRange val) {
      cropRange = val;
      return this;
    }

    public Builder cropped(Boolean val) {
      cropped = val;
      return this;
    }

    public Builder cropRect(RectF val) {
      cropRect = val;
      return this;
    }

    public MediaModel build() {
      return new MediaModel(this);
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.sourceType);
      dest.writeInt(this.order);
      dest.writeString(this.filePath);
      dest.writeLong(this.duration);
      dest.writeInt(this.rotation);
      dest.writeString(this.rawFilepath);
      dest.writeParcelable(this.rangeInFile, flags);
      dest.writeValue(this.cropped);
      dest.writeParcelable(this.cropRect, flags);
    }

    protected Builder(Parcel in) {
      this.sourceType = in.readInt();
      this.order = in.readInt();
      this.filePath = in.readString();
      this.duration = in.readLong();
      this.rotation = in.readInt();
      this.rawFilepath = in.readString();
      this.rangeInFile = in.readParcelable(GRange.class.getClassLoader());
      this.cropped = (Boolean) in.readValue(Boolean.class.getClassLoader());
      this.cropRect = in.readParcelable(RectF.class.getClassLoader());
    }

    public static final Creator<Builder> CREATOR = new Creator<Builder>() {
      @Override public Builder createFromParcel(Parcel source) {
        return new Builder(source);
      }

      @Override public Builder[] newArray(int size) {
        return new Builder[size];
      }
    };
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeInt(this.sourceType);
    dest.writeInt(this.mediaViewType);
    dest.writeInt(this.order);
    dest.writeString(this.filePath);
    dest.writeLong(this.duration);
    dest.writeLong(this.pitDuration);
    dest.writeInt(this.rotation);
    dest.writeString(this.rawFilepath);
    dest.writeParcelable(this.rangeInFile, flags);
    dest.writeParcelable(this.cropRange, flags);
    dest.writeValue(this.cropped);
    dest.writeParcelable(this.cropRect, flags);
  }

  protected MediaModel(Parcel in) {
    this.id = in.readString();
    this.sourceType = in.readInt();
    this.mediaViewType = in.readInt();
    this.order = in.readInt();
    this.filePath = in.readString();
    this.duration = in.readLong();
    this.pitDuration = in.readLong();
    this.rotation = in.readInt();
    this.rawFilepath = in.readString();
    this.rangeInFile = in.readParcelable(GRange.class.getClassLoader());
    this.cropRange = in.readParcelable(GRange.class.getClassLoader());
    this.cropped = (Boolean) in.readValue(Boolean.class.getClassLoader());
    this.cropRect = in.readParcelable(RectF.class.getClassLoader());
  }

  public static final Creator<MediaModel> CREATOR = new Creator<MediaModel>() {
    @Override public MediaModel createFromParcel(Parcel source) {
      return new MediaModel(source);
    }

    @Override public MediaModel[] newArray(int size) {
      return new MediaModel[size];
    }
  };
}
