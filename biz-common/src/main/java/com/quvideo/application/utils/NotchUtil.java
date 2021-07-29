package com.quvideo.application.utils;

import android.content.Context;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 挖孔屏手机适配工具类
 *
 */
public class NotchUtil {

  /**
   * 挖孔屏手机的model名字常量
   * 目前XiaoMI只针对android O & P提供接口
   */
  private static final List<String> NOTCHLIST =
      Arrays.asList("ANE-AL00", "ANE-TL00", "LLD-AL20", "PADM00", "vivo Y83A", "Redmi 6 Pro",
          "MI 8", "MI 8 SE", "MI8 Explorer Edition", "Nokia X6", "X6", "Lenovo L78011");

  /**
   * 挖孔屏刘海屏适配
   */
  public static boolean isNotchDevice(Context context) {
    return NOTCHLIST.contains(android.os.Build.MODEL)
        || hasNotchAtHuawei(context)
        || hasNotchAtOPPO(context)
        || hasNotchAtVivo(context);
  }

  /////////////////////////////////////////HuaWei Start/////////////////////////////////////////////

  /**
   * 判断是否是挖孔屏
   */
  private static boolean hasNotchAtHuawei(Context context) {
    boolean ret = false;
    try {
      ClassLoader cl = context.getClassLoader();
      Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
      Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
      ret = (boolean) get.invoke(HwNotchSizeUtil);
    } catch (ClassNotFoundException e) {
    } catch (NoSuchMethodException e) {
    } catch (Exception e) {
    } finally {
      return ret;
    }
  }

  /**
   * 获取刘海尺寸：width、height
   * int[0]值为刘海宽度 int[1]值为刘海高度
   */
  public static int[] getNotchSizeAtHuawei(Context context) {
    int[] ret = new int[] { 0, 0 };
    try {
      ClassLoader cl = context.getClassLoader();
      Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
      Method get = HwNotchSizeUtil.getMethod("getNotchSize");
      ret = (int[]) get.invoke(HwNotchSizeUtil);
    } catch (Throwable e) {
    } finally {
      return ret;
    }
  }

  /////////////////////////////////////////HuaWei End/////////////////////////////////////////////

  /////////////////////////////////////////Vivo Start/////////////////////////////////////////////
  private static final int VIVO_NOTCH = 0x00000020;//是否有刘海
  private static final int VIVO_FILLET = 0x00000008;//是否有圆角

  public static boolean hasNotchAtVivo(Context context) {
    boolean ret = false;
    try {
      ClassLoader classLoader = context.getClassLoader();
      Class FtFeature = classLoader.loadClass("android.util.FtFeature");
      Method method = FtFeature.getMethod("isFeatureSupport", int.class);
      ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
    } catch (Throwable e) {
    } finally {
      return ret;
    }
  }

  //vivo不提供接口获取刘海尺寸，目前vivo的刘海宽为100dp,高为27dp
  /////////////////////////////////////////Vivo End/////////////////////////////////////////////

  /////////////////////////////////////////OPPO Start/////////////////////////////////////////////

  public static boolean hasNotchAtOPPO(Context context) {
    boolean ret = false;
    try {
      ret =
          context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    } catch (Exception e) {
    } finally {
      return ret;
    }
  }

  //OPPO不提供接口获取刘海尺寸，目前其有刘海屏的机型尺寸规格都是统一的。不排除以后机型会有变化。
  //其显示屏宽度为1080px，高度为2280px。刘海区域则都是宽度为324px, 高度为80px。
  /////////////////////////////////////////OPPO End/////////////////////////////////////////////
}
