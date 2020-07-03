package com.quvideo.application.editor.fake

import android.graphics.RectF

/**
 * @author wuzhongyou
 * @date 2020/6/28.
 */
data class FakeLimitPos(

  /**
   * 中心点可移动区域，如果矩形不正，则用limitRotation设置旋转角度
   * 默认为null，不限制移动范围，
   * 坐标系参考BaseMaskData的坐标系
   */
  var limitRectF: RectF? = null,
  /** 可移动区域的旋转角度，坐标系参考BaseMaskData的坐标系，旋转中心点limitRectF的中心点  */
  var limitRotation: Float = 0f,
  /** 最大横向宽度  */
  var maxWidth: Float = 0f,
  /** 最大纵向高度  */
  var maxHeight: Float = 0f
) {

}