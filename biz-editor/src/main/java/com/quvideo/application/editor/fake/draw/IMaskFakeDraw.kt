package com.quvideo.application.editor.fake.draw

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import androidx.core.content.ContextCompat
import com.quvideo.application.EditorApp
import com.quvideo.application.editor.R
import com.quvideo.application.editor.fake.IFakeDraw

abstract class IMaskFakeDraw : IFakeDraw() {

  /** 实线画笔  */
  protected var paint: Paint

  /** 虚线画笔  */
  protected var dashPaint: Paint

  init {
    paint = Paint()
    paint.setColor(
        ContextCompat.getColor(EditorApp.instance.app, R.color.color_fe3d42)
    )
    paint.isAntiAlias = true
    paint.isDither = true
    paint.style = STROKE
    paint.strokeWidth = dp2px.toFloat()
    dashPaint = Paint()
    dashPaint.color = ContextCompat.getColor(EditorApp.instance.app, R.color.color_fe3d42)
    dashPaint.isAntiAlias = true
    dashPaint.isDither = true
    dashPaint.style = STROKE
    dashPaint.strokeWidth = dp1px.toFloat()
    dashPaint.pathEffect = DashPathEffect(floatArrayOf(dp2px.toFloat(), dp2px.toFloat()), 0f)
  }
}