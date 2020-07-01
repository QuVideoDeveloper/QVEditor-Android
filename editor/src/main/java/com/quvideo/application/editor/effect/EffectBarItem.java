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
  public static final int ACTION_DUPLICATE = 9;
  public static final int ACTION_DEL = 10;

  private int action;
  private int resId;
  private String title;

  public EffectBarItem(int action, int resId, String title) {
    this.action = action;
    this.resId = resId;
    this.title = title;
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
}
