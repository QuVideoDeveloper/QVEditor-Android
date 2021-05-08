package com.quvideo.application.editor.effect;

public class EffectBarItem {

  public static final int ACTION_EDIT = 0;
  public static final int ACTION_TRIM = 1;
  public static final int ACTION_SUBTITLE_EDIT = 2;
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
  public static final int ACTION_FX_PLUGIN = 14;
  public static final int ACTION_KEYFRAME = 15;
  public static final int ACTION_AUDIO_FADE_IN = 16;
  public static final int ACTION_AUDIO_FADE_OUT = 17;
  public static final int ACTION_BGM_REPEAT = 18;
  public static final int ACTION_BGM_RESET_TO_THEME = 19;
  public static final int ACTION_QRCODE = 20;
  public static final int ACTION_COLLAGE_FILTER = 21;
  public static final int ACTION_COLLAGE_FX = 22;
  public static final int ACTION_COLLAGE_OVERLAY = 23;
  public static final int ACTION_COLLAGE_ADJUST = 24;
  public static final int ACTION_COLLAGE_CURVE_ADJUST = 25;
  public static final int ACTION_COLLAGE_SUBEFFECT_DISABLE = 27;
  public static final int ACTION_COLLAGE_TIMESCALE = 28;
  public static final int ACTION_COLLAGE_RESERVE = 29;
  public static final int ACTION_COLLAGE_UP_TO_TOP = 30;
  public static final int ACTION_SUBTITLE_PENETRATE_HUMAN = 31;

  // 暂不对外的操作
  public static final int ACTION_BGM_DOT = 101;
  // 音频变速
  public static final int ACTION_AUDIO_SPEED = 102;
  // 画面拼贴
  public static final int ACTION_COLLAGE_MOTION = 103;
  // 涂抹抠像
  public static final int ACTION_SMEAR = 106;
  // 播放器缩放
  public static final int ACTION_PLAYER_SCALE = 107;
  // 字幕动画
  public static final int ACTION_SUBTITLE_ANIM = 108;
  // 保存至xml
  public static final int ACTION_SAVE_XML = 110;
  // 导入xml
  public static final int ACTION_ADD_XML = 111;

  // 编辑组管理
  public static final int ACTION_EFFECT_GROUP_MANAGER = 121;
  // 编辑组解散
  public static final int ACTION_EFFECT_GROUP_DISSOLVE = 122;
  // 编辑组位置
  public static final int ACTION_EFFECT_GROUP_LOCATION = 123;

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
