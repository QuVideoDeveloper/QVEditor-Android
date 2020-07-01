package com.quvideo.application.editor.effect.fake.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.quvideo.application.EditorApp
import com.quvideo.application.editor.R
import com.quvideo.application.editor.effect.fake.FakePosInfo
import com.quvideo.application.editor.effect.fake.IFakeDraw
import com.quvideo.application.utils.DeviceSizeUtil

/**
 * @author wuzhongyou
 * @date 2020/6/24.
 */
class ChromaDraw : IFakeDraw() {

  /**
   * 缓存用的矩形
   */
  private val boundRectF = RectF()

  /** 正常的画笔 画十字瞄准 */
  private var normalPaint: Paint

  /** 圆环颜色画笔 */
  private var colorPaint: Paint

  /** 内外描边画笔*/
  private var strokePaint: Paint

  /** 取色圆环描边宽度 */
  private var colorStrokeWidth = 0f

  /** 外圆半径 */
  private var outerRadius = 0f

  /** 取色圆环半径 */
  private var strokeRadius = 0f

  /** 内圆半径 */
  private var innerRadius = 0f

  init {
    colorStrokeWidth = DeviceSizeUtil.dpToPixel(9f)
    outerRadius = DeviceSizeUtil.dpToPixel(40f) - dp1px / 2
    strokeRadius = DeviceSizeUtil.dpToPixel(39f) - colorStrokeWidth / 2
    innerRadius = DeviceSizeUtil.dpToPixel(30f) - dp1px / 2

    val normalColor = ContextCompat.getColor(EditorApp.instance.app, R.color.color_fe3d42)
    normalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    normalPaint.color = normalColor

    colorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    colorPaint.style = STROKE
    colorPaint.strokeWidth = colorStrokeWidth

    strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    strokePaint.color = normalColor
    strokePaint.style = STROKE
    strokePaint.strokeWidth = dp1px
  }

  override fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  ) {
    canvas.save()
    //画外描边
    canvas.drawCircle(fakePosInfo.centerX, fakePosInfo.centerY, outerRadius, strokePaint)
    //画圆环
    canvas.drawCircle(fakePosInfo.centerX, fakePosInfo.centerY, strokeRadius, colorPaint)
    //画内描边
    canvas.drawCircle(fakePosInfo.centerX, fakePosInfo.centerY, innerRadius, strokePaint)

    //十字线左边
    boundRectF.set(
        fakePosInfo.centerX - colorStrokeWidth, fakePosInfo.centerY - dp1px / 2,
        fakePosInfo.centerX - dp1px,
        fakePosInfo.centerY + dp1px / 2
    )
    canvas.drawRoundRect(boundRectF, dp1px / 2, dp1px / 2, normalPaint)
    //十字线右边
    boundRectF.set(
        fakePosInfo.centerX + dp1px, fakePosInfo.centerY - dp1px / 2, fakePosInfo.centerX + colorStrokeWidth,
        fakePosInfo.centerY + dp1px / 2
    )
    canvas.drawRoundRect(boundRectF, dp1px / 2, dp1px / 2, normalPaint)
    //十字线上边
    boundRectF.set(
        fakePosInfo.centerX - dp1px / 2, fakePosInfo.centerY - colorStrokeWidth,
        fakePosInfo.centerX + dp1px / 2,
        fakePosInfo.centerY - dp1px
    )
    canvas.drawRoundRect(boundRectF, dp1px / 2, dp1px / 2, normalPaint)
    //十字线下边
    boundRectF.set(
        fakePosInfo.centerX - dp1px / 2, fakePosInfo.centerY + dp1px,
        fakePosInfo.centerX + dp1px / 2,
        fakePosInfo.centerY + colorStrokeWidth
    )
    canvas.drawRoundRect(boundRectF, dp1px / 2, dp1px / 2, normalPaint)
    canvas.restore()
  }

  fun updateColor(color: Int) {
    colorPaint.color = color
  }

}