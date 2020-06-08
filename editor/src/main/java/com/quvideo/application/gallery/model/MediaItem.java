package com.quvideo.application.gallery.model;

import android.graphics.Bitmap;
import com.quvideo.application.editor.BuildConfig;
import com.quvideo.application.gallery.enums.MediaType;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class MediaItem implements Serializable {
  public int mediaId;
  public String title;
  public String displayTitle;
  public String path;
  public long duration;
  public String resolution;
  public long date;
  public String artist;
  public String album;
  public int leftTimeStamp;
  public int rightTimeStamp;
  public Bitmap mThumb;
  public boolean isFromDownloaded = false;
  public long lTemplateId = 0L;
  public boolean isDownloading = false;
  public MediaType mediaType;
  public int nMask = -1;//TemplateConstDef.TEMPLATE_MASK
  public int iState = -1;

  public MediaItem() {
  }

  public MediaItem(String strJSONString) {
    fromJSONString(strJSONString);
  }

  public final void fromJSONString(String strJSONString) {
    try {
      JSONObject jsonObj = new JSONObject(strJSONString);
      title = jsonObj.optString("title");
      displayTitle = jsonObj.optString("displayTitle");
      path = jsonObj.optString("path");
      resolution = jsonObj.optString("resolution");
      artist = jsonObj.optString("artist");
      album = jsonObj.optString("album");
      mediaId = jsonObj.optInt("mediaId", 0);
      duration = jsonObj.optLong("duration", 0L);
      date = jsonObj.optLong("date", 0L);
    } catch (Exception e) {

    }
  }

  public String toJSONString() {
    JSONObject jsonObj = new JSONObject();

    try {
      if (title != null) {
        jsonObj.put("title", title);
      }

      if (displayTitle != null) {
        jsonObj.put("displayTitle", displayTitle);
      }

      if (path != null) {
        jsonObj.put("path", path);
      }

      if (resolution != null) {
        jsonObj.put("resolution", resolution);
      }

      if (artist != null) {
        jsonObj.put("artist", artist);
      }

      if (album != null) {
        jsonObj.put("album", album);
      }

      jsonObj.put("mediaId", mediaId);
      jsonObj.put("duration", duration);
      jsonObj.put("date", date);
    } catch (JSONException e) {
      if (BuildConfig.DEBUG) {
        e.printStackTrace();
      }
    }

    return jsonObj.toString();
  }
}
