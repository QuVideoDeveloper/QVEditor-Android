package com.quvideo.application.glidedecoder;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.quvideo.mobile.engine.QEThumbnailTools;
import xiaoying.utils.LogUtils;

public class XytDataFetcher implements DataFetcher<Bitmap> {

  private final EffectThumbParams mEffectThumbParams;

  public XytDataFetcher(EffectThumbParams effectThumbParams) {
    mEffectThumbParams = effectThumbParams;
  }

  @Override
  public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Bitmap> callback) {
    LogUtils.d("XytDataFetcher", "mEffectThumbParams = "
        + mEffectThumbParams
        + ",getSignature="
        + mEffectThumbParams.getSignature());
    Bitmap srcBitmap = QEThumbnailTools.getTemplateThumbnail(mEffectThumbParams.mStylePath,
        mEffectThumbParams.mFrameWidth, mEffectThumbParams.mFrameHeight);
    callback.onDataReady(srcBitmap);
  }

  @Override public void cleanup() {

  }

  @Override public void cancel() {

  }

  @NonNull @Override public Class<Bitmap> getDataClass() {
    return Bitmap.class;
  }

  @NonNull @Override public DataSource getDataSource() {
    return DataSource.LOCAL;
  }
}
