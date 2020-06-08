package com.quvideo.application.utils;

import java.util.Locale;

/**
 */
public class DateUtils {
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
