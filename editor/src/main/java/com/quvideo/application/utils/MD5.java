package com.quvideo.application.utils;

import java.security.MessageDigest;

public class MD5 {

  private static final char HEX_DIGITS[] = {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };

  public static String md5(String strPlainText) {
    MessageDigest md5;
    try {
      md5 = MessageDigest.getInstance("MD5");
      byte[] strText = strPlainText.getBytes();
      md5.update(strText, 0, strText.length);
      return toHexString(md5.digest(), null);
    } catch (Exception e) {
      return null;
    }
  }

  static String toHexString(byte[] b, String strSplit) {
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
