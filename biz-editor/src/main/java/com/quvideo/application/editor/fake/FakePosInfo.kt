package com.quvideo.application.editor.fake

import com.quvideo.application.editor.fake.FakePosInfo.FakePosType.Position

data class FakePosInfo(
  var centerX: Float,
  var centerY: Float,
  var width: Float,
  var height: Float,
  var degrees: Float,
  var anchorOffsetX: Float,
  var anchorOffsetY: Float
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