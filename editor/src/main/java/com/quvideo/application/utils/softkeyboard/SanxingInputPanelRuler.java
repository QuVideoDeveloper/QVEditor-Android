package com.quvideo.application.utils.softkeyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import com.quvideo.application.utils.DeviceSizeUtil;
import com.quvideo.application.utils.SystemUtils;

public class SanxingInputPanelRuler extends BaseInputPanelRuler {

  @Override
  public void onSpeakShow(Activity activity) {
    if (NavigationBarUtil.isNavigationBarShow(activity.getApplicationContext())) {
      SystemUtils.setHidenOfStatusBar(true, activity);
    }
  }

  @Override
  public void onSpeakHide(Activity activity) {
    if (NavigationBarUtil.isNavigationBarShow(activity.getApplicationContext())) {
      SystemUtils.setHidenOfStatusBar(false, activity);
    }
  }

  @Override
  public int getDifference(Context ctx, Rect r) {
    return DeviceSizeUtil.getScreenHeight()
        - (NavigationBarUtil.isNavigationBarShow(ctx) ? NavigationBarUtil.getNavigationBarHeight(ctx)
        : 0)
        - r.bottom;
  }
}
