package com.quvideo.application.gallery.preview.utils;

import java.util.Locale;

public class TimeUtil {

  public static String getFloatFormatDuration(int duration) {
    String durationStr = "";
    if (duration < 0) {
      duration = 0;
    }
    int fduration = duration / 100;
    try {
      if (fduration >= 36000) {
        durationStr = String.format(Locale.US, "%02d:%02d:%02d.%1d", fduration / 36000,
            (fduration % 36000) / 600, (fduration % 60) / 10, fduration % 10);
      } else {
        durationStr = String.format(Locale.US, "%2d:%02d.%1d", (fduration % 36000) / 600,
            (fduration % 600) / 10, fduration % 10);
      }
    } catch (Exception ignore) {
    }
    return durationStr.trim();
  }
}
