package com.quvideo.application.gallery.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc gallery range model
 * @since 5/25/2019
 */
public class GRange implements Parcelable, Comparable<GRange> {
  private int mLeftValue;
  private int mLength;
  private int mRightValue;
  public static final int START = 0;
  public static final int LENGTH = 1;

  public GRange() {
  }

  public GRange(int leftValue, int rightValue) {
    this.mLeftValue = leftValue;
    this.mRightValue = rightValue;
    setLength();
  }


  public int getLeftValue(){
    return mLeftValue;
  }

  public int getRightValue(){
    return mRightValue;
  }

  public int getLength() {
    return mLength;
  }

  public void setLeftValue(int leftVaule){
    mLeftValue = leftVaule;
    setLength();
  }

  public void setRightValue(int rightVaule){
    mRightValue = rightVaule;
    setLength();
  }

  public void setLength(){
    mLength = mRightValue - mLeftValue;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.mLeftValue);
    dest.writeInt(this.mLength);
    dest.writeInt(this.mRightValue);
  }

  protected GRange(Parcel in) {
    this.mLeftValue = in.readInt();
    this.mLength = in.readInt();
    this.mRightValue = in.readInt();
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GRange gRange = (GRange) o;

    if (mLeftValue != gRange.mLeftValue) {
      return false;
    }
    return mRightValue == gRange.mRightValue;
  }

  @Override public int hashCode() {
    int result = mLeftValue;
    result = 31 * result + mRightValue;
    return result;
  }

  public static final Creator<GRange> CREATOR = new Creator<GRange>() {
    @Override public GRange createFromParcel(Parcel source) {
      return new GRange(source);
    }

    @Override public GRange[] newArray(int size) {
      return new GRange[size];
    }
  };

  @Override public int compareTo(GRange o) {
    if (o != null) {
      if (this.getLeftValue() > o.getLeftValue()) {
        return 1;
      } else if (this.getLeftValue() < o.getLeftValue()) {
        return -1;
      }
    }
    return 0;
  }
}
