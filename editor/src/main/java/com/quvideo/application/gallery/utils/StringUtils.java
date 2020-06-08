package com.quvideo.application.gallery.utils;

/**
 * Create by zhengjunfei on 2019/9/2
 */
public class StringUtils {
  private static final char HEX_DIGITS[] = {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };

  public static String toHexString(byte[] b, String strSplit) {
    StringBuilder sb = new StringBuilder(b.length * 2);
    int i = 0;
    for (; i < b.length - 1; i++) {
      sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
      sb.append(HEX_DIGITS[b[i] & 0x0f]);
      if (strSplit != null) {
        sb.append(strSplit);
      }
    }

    sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
    sb.append(HEX_DIGITS[b[i] & 0x0f]);
    return sb.toString();
  }
}
