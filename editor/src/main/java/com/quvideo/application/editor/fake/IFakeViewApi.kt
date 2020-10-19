package com.quvideo.application.editor.fake

import android.graphics.PointF
import android.graphics.Rect
import com.quvideo.application.editor.effect.mask.FakeMaskPosData
import com.quvideo.mobile.engine.entity.VeMSize
import com.quvideo.mobile.engine.model.clip.ClipPosInfo
import com.quvideo.mobile.engine.model.effect.EffectPosInfo

interface IFakeViewApi {

  /** 设置对应的clip */
  fun setClipTarget(iFakeDraw: IFakeDraw?, cropRect: Rect)

  /** 设置对应的clip */
  fun setClipTarget(iFakeDraw: IFakeDraw?, clipPosInfo: ClipPosInfo?, size: VeMSize)

  /** 设置对应的Effect */
  fun setTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?)

  fun setTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?, startDegree: Float)

  fun setChromaTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?)

  /** 设置对应的Effect */
  fun setTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?, maskInfo: FakeMaskPosData?)

  /** 获取fakeview上的位置信息 */
  fun getFakePosInfo(): FakePosInfo?

  /** 更新chroma时的画笔颜色 */
  fun updateChromaColor(color :Int)

  fun setOldAnchor(oldAnchor :PointF)

  fun getOldAnchor() : PointF?

  /**
   * 设置video stream size
   * 用于转换effect position
   */
  fun setStreamSize(size: VeMSize)

  /** 设置回调 */
  fun setFakeViewListener(listener: IFakeViewListener?)
}