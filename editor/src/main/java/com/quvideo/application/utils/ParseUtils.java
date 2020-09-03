package com.quvideo.application.utils;

import android.text.TextUtils;

public class ParseUtils {
  /**
   * Integer.parseInt代理函数,统一做容错处理
   */
  public static int parseInt(String value) {
    return parseInt(value, 0);
  }

  /**
   * Integer.parseInt代理函数,统一做容错处理
   *
   * @param fallback 异常情况返回值
   */
  public static int parseInt(String value, int fallback) {
    if (TextUtils.isEmpty(value)) {
      return fallback;
    }
    try {
      return Integer.parseInt(value);
    } catch (Exception ex) {
      return fallback;
    }
  }

  /**
   * Long.decode代理函数,统一做容错处理
   * 特定进制如0x #开头的字符串直接parseLong处理不了，用decodeLong处理
   *
   * @param fallback 异常情况返回值
   */
  public static long decodeLong(String value, long fallback) {
    if (TextUtils.isEmpty(value)) {
      return fallback;
    }
    try {
      return Long.decode(value);
    } catch (NumberFormatException e) {
      return fallback;
    }
  }

  /**
   * Long.decode代理函数,统一做容错处理
   * 特定进制如0x #开头的字符串直接parseLong处理不了，用decodeLong处理
   */
  public static long decodeLong(String value) {
    return decodeLong(value, 0);
  }

  /**
   * Long.parseLong代理函数,统一做容错处理
   */
  public static long parseLong(String value) {
    return parseLong(value, 0);
  }

  /**
   * Long.parseLong代理函数,统一做容错处理
   *
   * @param fallback 异常情况返回值
   */
  public static long parseLong(String value, long fallback) {
    if (TextUtils.isEmpty(value)) {
      return fallback;
    }
    try {
      return Long.parseLong(value);
    } catch (Exception ex) {
      return fallback;
    }
  }

  /**
   * Double.parseDouble代理函数,统一做容错处理
   */
  public static double parseDouble(String value) {
    return parseDouble(value, 0);
  }

  /**
   * Double.parseDouble代理函数,统一做容错处理
   *
   * @param fallback 异常情况返回值
   */
  public static double parseDouble(String value, double fallback) {
    if (TextUtils.isEmpty(value)) {
      return fallback;
    }
    try {
      return Double.parseDouble(value);
    } catch (Exception ex) {
      return fallback;
    }
  }

  /**
   * Float.parseFloat代理函数,统一做容错处理
   */
  public static float parseFloat(String value) {
    return parseFloat(value, 0);
  }

  /**
   * Float.parseFloat代理函数,统一做容错处理
   *
   * @param fallback 异常情况返回值
   */
  public static float parseFloat(String value, float fallback) {
    if (TextUtils.isEmpty(value)) {
      return fallback;
    }
    try {
      return Float.parseFloat(value);
    } catch (Exception ex) {
      return fallback;
    }
  }
}
