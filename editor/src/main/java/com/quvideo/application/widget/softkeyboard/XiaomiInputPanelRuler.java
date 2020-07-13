package com.quvideo.application.widget.softkeyboard;

import android.content.Context;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import java.lang.reflect.Method;

public class XiaomiInputPanelRuler extends BaseInputPanelRuler{
  @Override public int getDifference(Context ctx, Rect r) {
    int diff = super.getDifference(ctx,r);
    if (isXiaomiAllPanel(ctx)){
      diff += getNavigationBarHeight(ctx);
    }
    return diff;
  }

  public static boolean isXiaomiAllPanel(Context context){
    return Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
  }

  public static int getNavigationBarHeight(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    DisplayMetrics dm = new DisplayMetrics();
    try {
      @SuppressWarnings("rawtypes")
      Class c = Class.forName("android.view.Display");
      @SuppressWarnings("unchecked")
      Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
      method.invoke(display, dm);
      return dm.heightPixels - display.getHeight();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
}
