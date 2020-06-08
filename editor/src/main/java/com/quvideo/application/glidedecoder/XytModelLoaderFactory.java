package com.quvideo.application.glidedecoder;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class XytModelLoaderFactory implements ModelLoaderFactory<EffectThumbParams, Bitmap> {

  @NonNull @Override public ModelLoader<EffectThumbParams, Bitmap> build(
      @NonNull MultiModelLoaderFactory multiFactory) {
    return new XytModelLoader();
  }

  @Override public void teardown() {

  }
}
