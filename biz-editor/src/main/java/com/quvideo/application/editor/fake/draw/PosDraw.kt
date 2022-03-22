package com.quvideo.application.editor.fake.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import androidx.core.content.ContextCompat
import com.quvideo.application.EditorApp
import com.quvideo.application.editor.R
import com.quvideo.application.editor.fake.FakePosInfo
import com.quvideo.application.editor.fake.IFakeDraw
import com.quvideo.application.utils.DeviceSizeUtil

class PosDraw : IFakeDraw() {

  // 0-正常 1-z轴旋转 2-x/y轴旋转 3-锚点
  private var posFakeMode: Int = 0

  private var drawRotate: Float = 0f;

  //
  public var oldAnchorPointF: PointF = PointF(0f, 0f)

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

  /** 虚线画笔  */
  private val dashPaint: Paint = Paint().apply {
    style = Paint.Style.STROKE
    isAntiAlias = true
    isDither = true
    pathEffect = DashPathEffect(floatArrayOf(dp2px.toFloat(), dp2px.toFloat()), 0f)
    color = ContextCompat.getColor(EditorApp.instance.app, R.color.color_fe3d42)
    strokeWidth = dp2px
  }

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    Log.d(logTag, "drawView() fake pos: $fakePosInfo")
    // 框出范围
    canvas.save()
    if (posFakeMode == 0 || posFakeMode == 1) {
      canvas.rotate(fakePosInfo.degrees, fakePosInfo.centerX, fakePosInfo.centerY)
    } else if (posFakeMode == 3) {
      canvas.rotate(drawRotate, oldAnchorPointF.x, oldAnchorPointF.y)
    } else {
      canvas.rotate(drawRotate, fakePosInfo.centerX, fakePosInfo.centerY)
    }
    if (posFakeMode != 3) {
      val rectDraw = RectF(
          fakePosInfo.centerX - fakePosInfo.anchorOffsetX - fakePosInfo.width / 2,
          fakePosInfo.centerY - fakePosInfo.anchorOffsetY - fakePosInfo.height / 2,
          fakePosInfo.centerX - fakePosInfo.anchorOffsetX + fakePosInfo.width / 2,
          fakePosInfo.centerY - fakePosInfo.anchorOffsetY + fakePosInfo.height / 2
      )
      Log.d(logTag, "drawView() draw rect $rectDraw")
      // 画矩形
      canvas.drawRoundRect(rectDraw, dp2px, dp2px, targetRectPaint)
    } else {
      // 锚点的圆
      canvas.drawCircle(oldAnchorPointF.x, oldAnchorPointF.y, dp6px.toFloat(), anchorPaint)
    }
    canvas.restore()
    if (posFakeMode != 0 && posFakeMode != 3) {
      canvas.drawCircle(fakePosInfo.centerX, fakePosInfo.centerY, dp6px.toFloat(), anchorPaint)
      // 画线
      val lineLeftPath = Path()
      lineLeftPath.moveTo(-1f * DeviceSizeUtil.getScreenWidth() - fakePosInfo.centerX, fakePosInfo.centerY)
      lineLeftPath.lineTo(fakePosInfo.centerX - dp6px, fakePosInfo.centerY)
      val lineRightPath = Path()
      lineRightPath.moveTo(fakePosInfo.centerX + dp6px, fakePosInfo.centerY)
      lineRightPath.lineTo(DeviceSizeUtil.getScreenWidth() * 2f, fakePosInfo.centerY)
      val lineUpPath = Path()
      lineUpPath.moveTo(fakePosInfo.centerX, -1f * DeviceSizeUtil.getScreenHeight() - fakePosInfo.centerY)
      lineUpPath.lineTo(fakePosInfo.centerX, fakePosInfo.centerY - dp6px)
      val lineDownPath = Path()
      lineDownPath.moveTo(fakePosInfo.centerX, fakePosInfo.centerY + dp6px)
      lineDownPath.lineTo(fakePosInfo.centerX, DeviceSizeUtil.getScreenHeight() * 2f)
      canvas.drawPath(lineLeftPath, dashPaint)
      canvas.drawPath(lineRightPath, dashPaint)
      canvas.drawPath(lineUpPath, dashPaint)
      canvas.drawPath(lineDownPath, dashPaint)
    }
  }

  fun setNormalFake(mode: Int, degree: Float) {
    this.posFakeMode = mode
    this.drawRotate = degree
  }

  override fun supportMultiScale(): Boolean {
    return posFakeMode == 0
  }

  override fun supportMultiRotate(): Boolean {
    return posFakeMode != 3
  }
}