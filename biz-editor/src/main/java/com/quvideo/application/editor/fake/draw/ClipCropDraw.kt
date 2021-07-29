package com.quvideo.application.editor.fake.draw

import android.graphics.Canvas
import android.graphics.Path
import com.quvideo.application.editor.fake.FakePosInfo

class ClipCropDraw : IMaskFakeDraw() {

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    canvas.save()
    canvas.rotate(fakePosInfo.degrees, fakePosInfo.centerX, fakePosInfo.centerY)
    // 画矩形
    canvas.drawRect(
        fakePosInfo.centerX - fakePosInfo.width / 2,
        fakePosInfo.centerY - fakePosInfo.height / 2,
        fakePosInfo.centerX + fakePosInfo.width / 2,
        fakePosInfo.centerY + fakePosInfo.height / 2,
        dashPaint
    )
    // 横条1
    val horlinePath1 = Path()
    horlinePath1.moveTo(fakePosInfo.centerX - fakePosInfo.width / 2, fakePosInfo.centerY - fakePosInfo.height / 6)
    horlinePath1.lineTo(fakePosInfo.centerX + fakePosInfo.width / 2, fakePosInfo.centerY - fakePosInfo.height / 6)
    canvas.drawPath(horlinePath1, dashPaint)
    // 横条2
    val horlinePath2 = Path()
    horlinePath2.moveTo(fakePosInfo.centerX - fakePosInfo.width / 2, fakePosInfo.centerY + fakePosInfo.height / 6)
    horlinePath2.lineTo(fakePosInfo.centerX + fakePosInfo.width / 2, fakePosInfo.centerY + fakePosInfo.height / 6)
    canvas.drawPath(horlinePath2, dashPaint)
    // 竖条1
    val verlinePath1 = Path()
    verlinePath1.moveTo(fakePosInfo.centerX - fakePosInfo.width / 6, fakePosInfo.centerY - fakePosInfo.height / 2)
    verlinePath1.lineTo(fakePosInfo.centerX - fakePosInfo.width / 6, fakePosInfo.centerY + fakePosInfo.height / 2)
    canvas.drawPath(verlinePath1, dashPaint)
    // 竖条2
    val verlinePath2 = Path()
    verlinePath2.moveTo(fakePosInfo.centerX + fakePosInfo.width / 6, fakePosInfo.centerY - fakePosInfo.height / 2)
    verlinePath2.lineTo(fakePosInfo.centerX + fakePosInfo.width / 6, fakePosInfo.centerY + fakePosInfo.height / 2)
    canvas.drawPath(verlinePath2, dashPaint)
    // 画横拉放大的把手
    canvas.drawLine(
        fakePosInfo.centerX - fakePosInfo.width / 2 + dp6px,
        fakePosInfo.centerY - dp6px,
        fakePosInfo.centerX - fakePosInfo.width / 2 + dp6px,
        fakePosInfo.centerY + dp6px,
        paint
    )
    canvas.drawLine(
        fakePosInfo.centerX + fakePosInfo.width / 2 - dp6px,
        fakePosInfo.centerY - dp6px,
        fakePosInfo.centerX + fakePosInfo.width / 2 - dp6px,
        fakePosInfo.centerY + dp6px,
        paint
    )
    // 画横拉放大的把手
    canvas.drawLine(
        fakePosInfo.centerX - dp6px,
        fakePosInfo.centerY - fakePosInfo.height / 2 + dp6px,
        fakePosInfo.centerX + dp6px,
        fakePosInfo.centerY - fakePosInfo.height / 2 + dp6px,
        paint
    )
    canvas.drawLine(
        fakePosInfo.centerX - dp6px,
        fakePosInfo.centerY + fakePosInfo.height / 2 - dp6px,
        fakePosInfo.centerX + dp6px,
        fakePosInfo.centerY + fakePosInfo.height / 2 - dp6px,
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

  override fun supportMultiRotate(): Boolean {
    return false
  }
}