package com.quvideo.application.gallery.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @desc gallery size model
 */
public class GSzie implements Parcelable {
  /** The width. */
  public int width;
  /** The height. */
  public int height;

  /**
   * This is the default constructor of the size object.
   */
  public GSzie() {
  }

  /**
   * This is a constructor of the size object.
   *
   * @param cx The width.
   * @param cy The height.
   */
  public GSzie(int cx, int cy) {
    width = cx;
    height = cy;
  }

  @Override public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("(width:").append(width).append(",height:").append(height).append(")");
    return sb.toString();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(width);
    dest.writeInt(height);
  }

  public static final Creator<GSzie> CREATOR = new Creator<GSzie>() {
    @Override public GSzie createFromParcel(Parcel in) {
      return new GSzie(in);
    }

    @Override public GSzie[] newArray(int size) {
      return new GSzie[size];
    }
  };

  private GSzie(Parcel in) {
    width = in.readInt();
    height = in.readInt();
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GSzie gSzie = (GSzie) o;

    if (width != gSzie.width) {
      return false;
    }
    return height == gSzie.height;
  }

  @Override public int hashCode() {
    int result = width;
    result = 31 * result + height;
    return result;
  }
}
