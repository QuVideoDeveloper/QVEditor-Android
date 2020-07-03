package com.quvideo.application.widget.softkeyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import com.quvideo.application.utils.DeviceSizeUtil;

public class OppoInputPanelRuler extends BaseInputPanelRuler {
  @Override
  public void onSpeakShow(Activity activity) {
  }

  @Override
  public void onSpeakHide(Activity activity) {
  }

  @Override
  public int getDifference(Context ctx, Rect r) {
    int difference = DeviceSizeUtil.getScreenHeight() - r.bottom;
    if (NavigationBarUtil.isNavigationBarShow(ctx)) {
      if (difference == NavigationBarUtil.getNavigationBarHeight(ctx) - r.top ||
          difference == NavigationBarUtil.getNavigationBarHeight(ctx)) {
        difference = 0;
      }
    }
    return difference;
  }

  /**
   * 是否刘海屏
   */
  public static boolean isHeteromorphism(Context ctx) {
    return ctx.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
  }
}
