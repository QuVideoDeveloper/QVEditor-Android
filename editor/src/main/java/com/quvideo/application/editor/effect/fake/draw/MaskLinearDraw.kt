package com.quvideo.application.editor.effect.fake.draw

import android.graphics.Canvas
import android.graphics.Path
import com.quvideo.application.editor.effect.fake.FakePosInfo
import com.quvideo.application.utils.DeviceSizeUtil

/**
 * @author wuzhongyou
 * @date 2020/6/24.
 */
class MaskLinearDraw : IMaskFakeDraw() {

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    canvas.save()
    canvas.rotate(fakePosInfo.degrees, fakePosInfo.centerX, fakePosInfo.centerY)
    // 中心点的圆
    canvas.drawCircle(fakePosInfo.centerX, fakePosInfo.centerY, dp6px.toFloat(), paint)
    // 画线
    val lineLeftPath = Path()
    lineLeftPath.moveTo(-1f * DeviceSizeUtil.getsScreenWidth(), fakePosInfo.centerY)
    lineLeftPath.lineTo(fakePosInfo.centerX - dp6px, fakePosInfo.centerY)
    val lineRightPath = Path()
    lineRightPath.moveTo(fakePosInfo.centerX + dp6px, fakePosInfo.centerY)
    lineRightPath.lineTo(DeviceSizeUtil.getsScreenWidth() * 2f, fakePosInfo.centerY)
    canvas.drawPath(lineLeftPath, dashPaint)
    canvas.drawPath(lineRightPath, dashPaint)
    canvas.restore()
  }
}