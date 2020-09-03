package com.quvideo.application;

import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule public class CusGlideModule extends AppGlideModule {

  @Override public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
    int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取系统分配给应用的总内存大小
    int memoryCacheSize = maxMemory / 8;//设置图片内存缓存占用十二分之一
    //设置内存缓存大小
    builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
  }
}
