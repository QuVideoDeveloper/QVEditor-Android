package com.quvideo.application.editor.fake.draw

import android.graphics.Canvas
import android.graphics.Path
import com.quvideo.application.editor.fake.FakePosInfo
import com.quvideo.application.utils.DeviceSizeUtil

class MaskLinearDraw : IMaskFakeDraw() {

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    canvas.save()
    canvas.rotate(fakePosInfo.degrees, fakePosInfo.centerX, fakePosInfo.centerY)
    // 中心点的圆
    canvas.drawCircle(fakePosInfo.centerX - fakePosInfo.anchorOffsetX, fakePosInfo.centerY - fakePosInfo.anchorOffsetY,
        dp6px.toFloat(), paint)
    // 画线
    val lineLeftPath = Path()
    lineLeftPath.moveTo(-1f * DeviceSizeUtil.getScreenWidth() - fakePosInfo.anchorOffsetX, fakePosInfo.centerY - fakePosInfo.anchorOffsetY)
    lineLeftPath.lineTo(fakePosInfo.centerX - fakePosInfo.anchorOffsetX - dp6px, fakePosInfo.centerY - fakePosInfo.anchorOffsetY)
    val lineRightPath = Path()
    lineRightPath.moveTo(fakePosInfo.centerX - fakePosInfo.anchorOffsetX + dp6px, fakePosInfo.centerY - fakePosInfo.anchorOffsetY)
    lineRightPath.lineTo(DeviceSizeUtil.getScreenWidth() * 2f - fakePosInfo.anchorOffsetX, fakePosInfo.centerY - fakePosInfo.anchorOffsetY)
    canvas.drawPath(lineLeftPath, dashPaint)
    canvas.drawPath(lineRightPath, dashPaint)
    canvas.restore()
  }
}