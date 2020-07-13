package com.quvideo.application.editor.fake.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.quvideo.application.editor.fake.FakePosInfo
import com.quvideo.application.editor.fake.IFakeDraw

class PosDraw : IFakeDraw() {

  private val targetRectPaint: Paint = Paint().apply {
    style = Paint.Style.STROKE
    isAntiAlias = true
    color = Color.WHITE
    strokeWidth = dp1px
  }

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    // 框出范围
    canvas.save()
    canvas.rotate(fakePosInfo.degrees, fakePosInfo.centerX, fakePosInfo.centerY)
    val rectDraw = RectF(
        fakePosInfo.centerX - fakePosInfo.width / 2,
        fakePosInfo.centerY - fakePosInfo.height / 2,
        fakePosInfo.centerX + fakePosInfo.width / 2,
        fakePosInfo.centerY + fakePosInfo.height / 2
    )
    // 画矩形
    canvas.drawRoundRect(rectDraw, dp2px, dp2px, targetRectPaint)
    canvas.restore()
  }

  override fun supportMultiScale(): Boolean {
    return true
  }
}