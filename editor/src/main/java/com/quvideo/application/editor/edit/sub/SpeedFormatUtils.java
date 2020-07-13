package com.quvideo.application.editor.edit.sub;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class SpeedFormatUtils {

  public static String mathSpeedText(int progress, int maxProgress) {
    int halfMaxProgress = maxProgress / 2;
    if (progress == halfMaxProgress) {
      return "1.0";
    }

    if (progress < halfMaxProgress) {
      float scale = 0.25f + 0.75f / halfMaxProgress * progress;
      scale = Math.round(scale * 100) / 100f;
      return String.valueOf(scale);
    }

    float scale = 1f + 3f / halfMaxProgress * (progress - halfMaxProgress);
    scale = Math.round(scale * 10) / 10f;
    DecimalFormat df = new DecimalFormat("#.#");
    return df.format(scale);
  }

  public static float mathSpeednValue(int progress, int maxProgress) {
    int halfMaxProgress = maxProgress / 2;
    if (progress == halfMaxProgress) {
      return 1f;
    }

    if (progress < halfMaxProgress) {
      float scale = 0.25f + 0.75f / halfMaxProgress * progress;
      scale = Math.round(scale * 100) / 100f;
      return scale;
    }

    float scale = 1f + 3f / halfMaxProgress * (progress - halfMaxProgress);
    scale = Math.round(scale * 10) / 10f;
    BigDecimal b = new BigDecimal(scale);
    return b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
  }

  public static String mathClipSpeedText(int progress, int maxProgress) {
    int halfMaxProgress = maxProgress / 2;
    if (progress == halfMaxProgress) {
      return "1.0";
    }

    if (progress < halfMaxProgress) {
      float scale = 0.25f + 0.75f / halfMaxProgress * progress;
      scale = Math.round(scale * 100) / 100f;
      DecimalFormat df = new DecimalFormat(scale > 1f ? "#.0" : "0.00");
      return df.format(scale);
    }

    float scale = 1f + 3f / halfMaxProgress * (progress - halfMaxProgress);
    scale = Math.round(scale * 10) / 10f;
    DecimalFormat df = new DecimalFormat(scale > 1f ? "#.0" : "0.00");
    return df.format(scale);
  }

  public static String formatSpeedValue(float speedValue) {
    if (speedValue == 1f || speedValue <= 0) {
      return "";
    }

    DecimalFormat df = new DecimalFormat(speedValue > 1f ? "#.0" : "0.00");
    return df.format(speedValue) + "x";
  }
}
