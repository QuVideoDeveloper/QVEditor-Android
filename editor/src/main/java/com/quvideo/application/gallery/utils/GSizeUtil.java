package com.quvideo.application.gallery.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import androidx.annotation.NonNull;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc dp sp Pixel exchange
 * @since 2019/5/13
 */
public class GSizeUtil {

  private static float sPixelDensity = -1;
  private static int sScreenWidth;
  private static int sScreenHeight;

  public static int getScreenHeight(Context context) {
    if (sScreenHeight != 0) {
      return sScreenHeight;
    }
    initScreenSize(context);
    return sScreenHeight;
  }

  public static int getsScreenWidth(Context context) {
    if (sScreenWidth != 0) {
      return sScreenWidth;
    }
    initScreenSize(context);
    return sScreenWidth;
  }

  public static int getFitPxFromDp(Context context,float dp) {
    return (int) (dp * getPixelDensity(context) + 0.5f);
  }


  public static float pixel2dp(@NonNull Context context, float dp) {
    return dp * getPixelDensity(context) + 0.5f;
  }

  public static float dp2Pixel(@NonNull Context context, float dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        context.getResources().getDisplayMetrics());
  }

  public static int pixel2sp(@NonNull Context context, float pxValue) {
    return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
  }

  private static float getPixelDensity(@NonNull Context context) {
    if (sPixelDensity != -1) {
      return sPixelDensity;
    }
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    sPixelDensity = metrics.density;
    return sPixelDensity;
  }

  private static void initScreenSize(@NonNull Context context) {
    DisplayMetrics dm = context.getResources().getDisplayMetrics();
    sScreenHeight = dm.heightPixels;
    sScreenWidth = dm.widthPixels;
  }

  /**
   * 获取设备statusBar高度
   */
  public static int getStatusBarHeight(Context ctx) {
    int height = 0;
    int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      height = ctx.getResources().getDimensionPixelSize(resourceId);
    }
    return height;
  }
}
