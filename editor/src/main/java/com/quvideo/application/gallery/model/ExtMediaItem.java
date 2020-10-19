package com.quvideo.application.gallery.model;

import androidx.annotation.NonNull;
import com.google.gson.Gson;

/**
 *
 * @Description
 */
public class ExtMediaItem extends MediaItem {
  public long lFlag;
  public long lGroupKey;
  public int nFromtype;
  public String strMisc;
  public String id;
  public String thumbUrl;

  @NonNull @Override public String toString() {
    return new Gson().toJson(this);
  }
}
