package com.quvideo.application.editor.fake.draw

import android.graphics.Canvas
import com.quvideo.application.editor.fake.FakePosInfo

/**
 * @author wuzhongyou
 * @date 2020/6/24.
 */
class MaskRadialDraw : IMaskFakeDraw() {

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    canvas.save()
    canvas.rotate(fakePosInfo.degrees, fakePosInfo.centerX, fakePosInfo.centerY)
    // 中心点的圆
    canvas.drawCircle(fakePosInfo.centerX, fakePosInfo.centerY, dp6px.toFloat(), paint)
    // 画圆
    canvas.drawOval(
        fakePosInfo.centerX - fakePosInfo.width / 2,
        fakePosInfo.centerY - fakePosInfo.height / 2,
        fakePosInfo.centerX + fakePosInfo.width / 2,
        fakePosInfo.centerY + fakePosInfo.height / 2,
        dashPaint
    )
    // 画横拉放大的把手
    canvas.drawLine(
        fakePosInfo.centerX - fakePosInfo.width / 2 - dp6px,
        fakePosInfo.centerY - dp6px,
        fakePosInfo.centerX - fakePosInfo.width / 2 - dp6px,
        fakePosInfo.centerY + dp6px,
        paint
    )
    canvas.drawLine(
        fakePosInfo.centerX + fakePosInfo.width / 2 + dp6px,
        fakePosInfo.centerY - dp6px,
        fakePosInfo.centerX + fakePosInfo.width / 2 + dp6px,
        fakePosInfo.centerY + dp6px,
        paint
    )
    // 画横拉放大的把手
    canvas.drawLine(
        fakePosInfo.centerX - dp6px,
        fakePosInfo.centerY - fakePosInfo.height / 2 - dp6px,
        fakePosInfo.centerX + dp6px,
        fakePosInfo.centerY - fakePosInfo.height / 2 - dp6px,
        paint
    )
    canvas.drawLine(
        fakePosInfo.centerX - dp6px,
        fakePosInfo.centerY + fakePosInfo.height / 2 + dp6px,
        fakePosInfo.centerX + dp6px,
        fakePosInfo.centerY + fakePosInfo.height / 2 + dp6px,
        paint
    )
    canvas.restore()
  }

  override fun supportSingleSideDrag(): Boolean {
    return true
  }

  override fun supportMultiScale(): Boolean {
    return true
  }
}