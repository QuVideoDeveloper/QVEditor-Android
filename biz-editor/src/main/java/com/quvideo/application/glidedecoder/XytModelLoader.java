package com.quvideo.application.glidedecoder;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

public class XytModelLoader implements ModelLoader<EffectThumbParams, Bitmap> {

  @Nullable @Override
  public LoadData<Bitmap> buildLoadData(@NonNull EffectThumbParams effectThumbParams, int width,
      int height, @NonNull Options options) {
    return new LoadData<>(new ObjectKey(effectThumbParams.getSignature()),
        new XytDataFetcher(effectThumbParams));
  }

  @Override public boolean handles(@NonNull EffectThumbParams model) {
    return true;
  }
}
