package com.quvideo.application.editor.effect.fake

import com.quvideo.application.editor.effect.fake.FakePosInfo.FakePosType.Position

/**
 * @author wuzhongyou
 * @date 2020/6/24.
 */
class FakePosInfo(
  var centerX: Float,
  var centerY: Float,
  var width: Float,
  var height: Float,
  var degrees: Float
) {

  var fakePosType: FakePosType = Position

  enum class FakePosType {
    Position,
    Chroma,
    Mask_Linear,
    Mask_Mirror,
    Mask_Radial,
    Mask_Rect
  }
}