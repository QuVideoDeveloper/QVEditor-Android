package com.quvideo.application.editor.effect;

/**
 * Created by santa on 2020-04-29.
 */
public class EffectBarItem {

  public static final int ACTION_EDIT = 0;
  public static final int ACTION_TRIM = 1;
  public static final int ACTION_INPUT = 2;
  public static final int ACTION_VOLUME = 3;
  public static final int ACTION_ALPHA = 4;
  public static final int ACTION_MAGIC = 5;
  public static final int ACTION_MIRROR = 6;
  public static final int ACTION_MASK = 7;
  public static final int ACTION_CHROMA = 8;
  public static final int ACTION_MOSAIC_DEGREE = 9;
  public static final int ACTION_DUPLICATE = 10;
  public static final int ACTION_DEL = 11;
  public static final int ACTION_CUT = 12;
  public static final int ACTION_ROTATE_AXLE = 13;

  private int action;
  private int resId;
  private String title;
  private boolean isEnabled;

  public EffectBarItem(int action, int resId, String title) {
    this.action = action;
    this.resId = resId;
    this.title = title;
  }

  public EffectBarItem(int action, int resId, String title, boolean enabled) {
    this.action = action;
    this.resId = resId;
    this.title = title;
    this.isEnabled = enabled;
  }

  public int getAction() {
    return action;
  }

  public int getResId() {
    return resId;
  }

  public String getTitle() {
    return title;
  }

  public boolean isEnabled() {
    return isEnabled;
  }
}
