package com.quvideo.application.glidedecoder;

import java.util.Locale;

public class EffectThumbParams {

  public final String mStylePath;
  public final int mFrameWidth;
  public final int mFrameHeight;

  //mTextState.mStylePath, mTextState.mExampleThumbPos,
  //mTextState.mFrameWidth, mTextState.mFrameHeight

  public EffectThumbParams(String mStylePath, int mFrameWidth, int mFrameHeight) {
    this.mStylePath = mStylePath;
    this.mFrameWidth = mFrameWidth;
    this.mFrameHeight = mFrameHeight;
  }

  /**
   * 缓存KEY，相同则会拿缓存
   */
  public String getSignature() {
    // TODO make sure it's unique for every possible instance of GenerateParams
    // because it will affect how the resulting bitmap is cached
    // the below is correct correct for the current fields, if those change this has to change
    return String.format(Locale.ROOT, "%s-%08x-%08x", mStylePath, mFrameWidth, mFrameHeight);
  }

  @Override public String toString() {
    return "EffectThumbParams{"
        + "mStylePath='"
        + mStylePath
        + '\''
        + ", mFrameWidth="
        + mFrameWidth
        + ", mFrameHeight="
        + mFrameHeight
        + '}';
  }
}
