package com.quvideo.application.widget.softkeyboard;

import android.content.Context;
import android.content.res.Resources;
import com.quvideo.application.utils.DeviceSizeUtil;

public class NavigationBarUtil {

  public static boolean isNavigationBarShow(Context ctx) {
    Resources resources = ctx.getResources();
    int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
    return id > 0 && resources.getBoolean(id);
  }

  public static int getStatusBarHeight(Context ctx) {
    return DeviceSizeUtil.getStatusBarHeight(ctx);
  }

  public static int getNavigationBarHeight(Context ctx) {
    Resources resources = ctx.getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return resources.getDimensionPixelSize(resourceId);
    }
    return 0;
  }
}
