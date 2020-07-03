package com.quvideo.application.editor.fake.draw

import android.graphics.Canvas
import android.graphics.Path
import com.quvideo.application.editor.fake.FakePosInfo
import com.quvideo.application.utils.DeviceSizeUtil

/**
 * @author wuzhongyou
 * @date 2020/6/24.
 */
class MaskMirrorDraw : IMaskFakeDraw() {

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    canvas.save()
    canvas.rotate(fakePosInfo.degrees, fakePosInfo.centerX, fakePosInfo.centerY)
    // 中心点的圆
    canvas.drawCircle(fakePosInfo.centerX, fakePosInfo.centerY, dp6px.toFloat(), paint)
    // 画上下线
    val startLine = Path()
    startLine.moveTo(-1f * DeviceSizeUtil.getsScreenWidth(), fakePosInfo.centerY - fakePosInfo.height / 2)
    startLine.lineTo(DeviceSizeUtil.getsScreenWidth() * 2f, fakePosInfo.centerY - fakePosInfo.height / 2)
    val endLine = Path()
    endLine.moveTo(-1f * DeviceSizeUtil.getsScreenWidth(), fakePosInfo.centerY + fakePosInfo.height / 2)
    endLine.lineTo(DeviceSizeUtil.getsScreenWidth() * 2f, fakePosInfo.centerY + fakePosInfo.height / 2)
    canvas.drawPath(startLine, dashPaint)
    canvas.drawPath(endLine, dashPaint)
    canvas.restore()
  }

  override fun supportMultiScale(): Boolean {
    return true
  }
}