package com.quvideo.application.gallery.db.bean;

import com.google.gson.Gson;
import com.quvideo.application.gallery.model.GRange;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.converter.PropertyConverter;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 6/27/2019
 */
@androidx.annotation.Keep @Entity(nameInDb = "Media") public class MediaBeen {
  @Id(autoincrement = true) @Property(nameInDb = "_id") public Long _id;

  @Property(nameInDb = "sourceType") public int sourceType;

  @Property(nameInDb = "duration") public long duration;

  @Property(nameInDb = "rotation") public int rotation;

  @Property(nameInDb = "filePath") public String filePath;

  @Property(nameInDb = "rawFilepath") public String rawFilepath;

  @Property(nameInDb = "rangeInFile")
  @Convert(converter = RangeConverter.class, columnType = String.class) public GRange
      rangeInFile;

  @Keep public MediaBeen(Long _id, int sourceType, long duration, int rotation, String filePath,
      String rawFilepath, GRange rangeInFile) {
    this._id = _id;
    this.sourceType = sourceType;
    this.duration = duration;
    this.rotation = rotation;
    this.filePath = filePath;
    this.rawFilepath = rawFilepath;
    this.rangeInFile = rangeInFile;
  }

  @Keep public MediaBeen() {
  }

  public Long get_id() {
    return this._id;
  }

  public void set_id(Long _id) {
    this._id = _id;
  }

  public int getSourceType() {
    return sourceType;
  }

  public void setSourceType(int sourceType) {
    this.sourceType = sourceType;
  }

  public long getDuration() {
    return this.duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public int getRotation() {
    return this.rotation;
  }

  public void setRotation(int rotation) {
    this.rotation = rotation;
  }

  public String getFilePath() {
    return this.filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getRawFilepath() {
    return this.rawFilepath;
  }

  public void setRawFilepath(String rawFilepath) {
    this.rawFilepath = rawFilepath;
  }

  public GRange getRangeInFile() {
    return this.rangeInFile;
  }

  public void setRangeInFile(GRange rangeInFile) {
    this.rangeInFile = rangeInFile;
  }

  public static class RangeConverter implements PropertyConverter<GRange, String> {
    @Override public GRange convertToEntityProperty(String databaseValue) {
      if (databaseValue == null) {
        return null;
      }
      return new Gson().fromJson(databaseValue, GRange.class);
    }

    @Override public String convertToDatabaseValue(GRange entityProperty) {
      if (entityProperty == null) {
        return null;
      }
      return new Gson().toJson(entityProperty);
    }
  }
}
