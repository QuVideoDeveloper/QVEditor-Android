package com.quvideo.application.editor.fake.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.quvideo.application.EditorApp
import com.quvideo.application.editor.R
import com.quvideo.application.editor.fake.FakePosInfo
import com.quvideo.application.editor.fake.IFakeDraw

class KeyFramePosDraw : IFakeDraw() {

  // 0-只可位移  1-只可缩放 2-只可旋转 3-都不行
  private var keyFramePosMode: Int = 0

  private val targetRectPaint: Paint = Paint().apply {
    style = Paint.Style.STROKE
    isAntiAlias = true
    color = Color.WHITE
    strokeWidth = dp1px
  }

  private val anchorPaint: Paint = Paint().apply {
    style = Paint.Style.STROKE
    isAntiAlias = true
    color = ContextCompat.getColor(EditorApp.instance.app, R.color.color_fe3d42)
    strokeWidth = dp2px
  }

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    // 框出范围
    canvas.save()
    canvas.rotate(fakePosInfo.degrees, fakePosInfo.centerX, fakePosInfo.centerY)
    val rectDraw = RectF(
        fakePosInfo.centerX - fakePosInfo.anchorOffsetX - fakePosInfo.width / 2,
        fakePosInfo.centerY - fakePosInfo.anchorOffsetY - fakePosInfo.height / 2,
        fakePosInfo.centerX - fakePosInfo.anchorOffsetX + fakePosInfo.width / 2,
        fakePosInfo.centerY - fakePosInfo.anchorOffsetY + fakePosInfo.height / 2
    )
    // 画矩形
    canvas.drawRoundRect(rectDraw, dp2px, dp2px, targetRectPaint)
    canvas.restore()
    canvas.drawCircle(fakePosInfo.centerX, fakePosInfo.centerY, dp6px.toFloat(), anchorPaint)
  }

  fun setNormalFake(mode: Int) {
    this.keyFramePosMode = mode
  }

  override fun supportDrag(): Boolean {
    return keyFramePosMode == 0
  }

  override fun supportMultiScale(): Boolean {
    return keyFramePosMode == 1
  }

  override fun supportMultiRotate(): Boolean {
    return keyFramePosMode == 2
  }
}