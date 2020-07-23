package com.quvideo.application.editor.base;

import android.content.Context;
import android.widget.RelativeLayout;
import com.quvideo.mobile.engine.project.IQEWorkSpace;

public abstract class BaseMenuLayer extends RelativeLayout {

  protected IQEWorkSpace mWorkSpace;

  public BaseMenuLayer(Context context, IQEWorkSpace workSpace) {
    super(context);
    this.mWorkSpace = workSpace;
  }

  /**
   * 处理返回键
   */
  public final void handleBackPress() {
    dismissMenu();
  }

  public abstract void dismissMenu();

  public abstract MenuType getMenuType();

  public enum MenuType {
    Theme, // 主题
    ClipEdit, // clip编辑
    ClipAdjust, // clip参数调节
    ClipFilter, // clip滤镜
    ClipFxFilter, // clip特效滤镜
    ClipMagicSound, // clip变声
    ClipSpeed, // clip变速
    ClipSplit, // clip分割
    ClipCrop, // clip裁剪
    ClipPosInfo, // clip位置
    ClipBG, // clip背景
    ClipTrans, // clip转场
    ClipTrim, // clip裁切
    ClipVolume, // clip音量
    EffectEdit, // effect编辑
    EffectAdd, // effect添加
    EffectSubtitleInput, // effect字幕修改
    EffectAlpha, // effect透明度
    EffectTone, // effect变声
    EffectTrim, // effect裁切
    EffectVolume, // effect音量
    EffectMask, // effect蒙版
    EffectChroma, // effect抠色
    Audio, // 音频
    AudioRecord, // 音频录音
    AudioAdd, // 音频添加
  }

}
