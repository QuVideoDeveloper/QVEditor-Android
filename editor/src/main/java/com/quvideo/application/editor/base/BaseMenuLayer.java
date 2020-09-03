package com.quvideo.application.editor.base;

import android.content.Context;
import android.content.Intent;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
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

  public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    return false;
  }

  public enum MenuType {
    Theme, // 主题
    ThemeSubtitle, // 主题字幕
    ThemeSubtitleInput, // 主题字幕编辑
    ClipEdit, // clip编辑
    ClipAdjust, // clip参数调节
    ClipCurveAdjust, // clip曲线调色
    ClipFilter, // clip滤镜
    ClipFxFilter, // clip特效滤镜
    ClipMagicSound, // clip变声
    ClipCurveSpeed, // clip曲线变速
    ClipSpeed, // clip变速
    ClipSplit, // clip分割
    ClipCrop, // clip裁剪
    ClipPosInfo, // clip位置
    ClipKeyFrame, // clip关键帧
    ClipBG, // clip背景
    ClipTrans, // clip转场
    ClipTrim, // clip裁切
    ClipVolume, // clip音量
    EffectEdit, // effect编辑
    EffectPlugin, // effect编辑效果插件
    EffectPluginAttri, // effect编辑效果插件属性
    EffectAdd, // effect添加
    EffectAddPlugin, // effect添加效果插件
    EffectSubtitleInput, // effect字幕修改
    EffectSubtitleShadow, // effect字幕阴影
    EffectSubtitleStroke, // effect字幕描边
    EffectSubtitleAlign, // effect字幕对齐
    EffectAlpha, // effect透明度
    EffectTone, // effect变声
    EffectTrim, // effect裁切
    EffectVolume, // effect音量
    EffectMask, // effect蒙版
    EffectChroma, // effect抠色
    EffectAxle, // effect选择轴
    EffectSubtitle, // effect字幕
    EffectKeyFrame, // effect关键帧
    CollageFilter, // 画中画滤镜
    CollageFx, // 画中画特效
    CollageFxAdd, // 画中画特效添加
    CollageFxTrim, // 画中画特效裁切
    CollageOverlay, // 画中画混合模式
    CollageAdjust, // 画中画参数调节
    CollageCurveAdjust, // 画中画曲线调色
    Audio, // 音频
    AudioRecord, // 音频录音
    AudioAdd, // 音频添加
  }
}
