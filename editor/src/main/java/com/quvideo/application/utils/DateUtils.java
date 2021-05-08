package com.quvideo.application.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 */
public class DateUtils {

  public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

  public static final String FORMAT_MM_DD_HH_MM = "MM-dd HH:mm";

  /**
   * 按照指定格式获得时间字符串
   */
  public static String formatDateCustom(Date date, String format) {
    return new SimpleDateFormat(format, Locale.US).format(date);
  }

  /**
   * 按照"yyyy-MM-dd hh:mm:ss"格式格式化时间
   */
  public static String formatFullDate(long date) {
    return formatDateCustom(new Date(date), DateUtils.FORMAT_YYYY_MM_DD_HH_MM_SS);
  }

  /**
   * 按照"MM-dd hh:mm"格式格式化时间
   */
  public static String formatMDHMDate(long date) {
    return formatDateCustom(new Date(date), DateUtils.FORMAT_MM_DD_HH_MM);
  }

  // duration in million second
  public static String getFormatDuration(long duration) {
    String durationStr = "";
    if (duration < 0) {
      duration = 0;
    }
    duration = (duration + 500) / 1000;
    try {
      if (duration >= 3600) {
        durationStr =
            String.format(Locale.US, "%02d:%02d:%02d", duration / 3600, (duration % 3600) / 60,
                duration % 60);
      } else {
        durationStr = String.format(Locale.US, "%2d:%02d", (duration % 3600) / 60, duration % 60);
      }
    } catch (Exception ex) {
    }
    return durationStr.trim();
  }
}
