package com.quvideo.application.utils.softkeyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import com.quvideo.application.utils.DeviceSizeUtil;

public class BaseInputPanelRuler {

  public void onSpeakShow(Activity activity) {
  }

  public void onSpeakHide(Activity activity) {

  }

  public int getDifference(Context ctx, Rect r) {
    return DeviceSizeUtil.getScreenHeight() - r.bottom;
  }
}
