package com.quvideo.application.editor.fake

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.quvideo.application.editor.fake.draw.ChromaDraw
import com.quvideo.mobile.engine.entity.VeMSize
import com.quvideo.mobile.engine.model.clip.ClipPosInfo
import com.quvideo.mobile.engine.model.effect.EffectMaskInfo
import com.quvideo.mobile.engine.model.effect.EffectPosInfo
import com.quvideo.mobile.engine.utils.QESizeUtil

/**
 * Effect手势View
 */
class FakeView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IFakeViewApi {

  private var iFakeDraw: IFakeDraw? = null
  private var fakeViewListener: IFakeViewListener? = null

  private var scaleWidth = 1f
  private var scaleHeight = 1f
  private var offsetX = 0
  private var offsetY = 0

  private fun setTarget(iFakeDraw: IFakeDraw?, fakePosInfo: FakePosInfo?, fakeLimitPos: FakeLimitPos) {
    this.iFakeDraw?.fakeViewListener = null
    this.iFakeDraw = iFakeDraw
    this.iFakeDraw?.fakePosInfo = fakePosInfo
    this.iFakeDraw?.fakeLimitPos = fakeLimitPos
    this.iFakeDraw?.fakeViewListener = object : IFakeViewListener {
      override fun onEffectMoving() {
        invalidate()
        fakeViewListener?.onEffectMoving()
      }

      override fun onEffectMoveStart() {
        fakeViewListener?.onEffectMoveStart()
      }

      override fun onEffectMoveEnd(moved: Boolean) {
        fakeViewListener?.onEffectMoveEnd(moved)
      }

      override fun checkEffectTouchHit(pointF: PointF) {
        pointF.x -= offsetX
        pointF.y -= offsetY
        fakeViewListener?.checkEffectTouchHit(pointF)
      }
    }
    invalidate()
  }

  override fun setClipTarget(iFakeDraw: IFakeDraw?, clipPosInfo: ClipPosInfo?) {
    val fakeLimitPos = FakeLimitPos(RectF(offsetX.toFloat() - (measuredWidth - 2 * offsetY),
        offsetY.toFloat() - (measuredHeight - 2 * offsetY),
        (measuredWidth - offsetX).toFloat() + (measuredWidth - 2 * offsetY),
        (measuredHeight - offsetY).toFloat() + (measuredHeight - 2 * offsetY)),
        0F,
        ((measuredWidth - 2 * offsetX) * 2).toFloat(),
        ((measuredHeight - 2 * offsetY) * 2).toFloat())
    if (clipPosInfo == null) {
      this.setTarget(iFakeDraw, null, fakeLimitPos)
      return
    }
    var fakePosInfo = FakePosInfo(
        clipPosInfo.centerPosX * scaleWidth + offsetX,
        clipPosInfo.centerPosY * scaleHeight + offsetY,
        clipPosInfo.widthScale * measuredWidth * scaleWidth,
        clipPosInfo.heightScale * measuredHeight * scaleHeight,
        clipPosInfo.degree)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?) {
    val fakeLimitPos = FakeLimitPos(RectF(offsetX.toFloat(), offsetY.toFloat(),
        (measuredWidth - offsetX).toFloat(), (measuredHeight - offsetY).toFloat()),
        0F,
        ((measuredWidth - 2 * offsetX) * 2).toFloat(),
        ((measuredHeight - 2 * offsetY) * 2).toFloat())
    if (effectPosInfo == null) {
      this.setTarget(iFakeDraw, null, fakeLimitPos)
      return
    }
    var fakePosInfo = FakePosInfo(
        effectPosInfo.centerPosX * scaleWidth + offsetX,
        effectPosInfo.centerPosY * scaleHeight + offsetY,
        effectPosInfo.width * scaleWidth,
        effectPosInfo.height * scaleHeight,
        effectPosInfo.degree)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setChromaTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?) {
    var fakeLimitPos = FakeLimitPos(
        RectF(offsetX.toFloat(), offsetY.toFloat(),
            (measuredWidth - offsetX).toFloat(), (measuredHeight - offsetY).toFloat()),
        0F,
        ((measuredWidth - 2 * offsetX) * 2).toFloat(),
        ((measuredHeight - 2 * offsetY) * 2).toFloat())
    if (effectPosInfo == null) {
      this.setTarget(iFakeDraw, null, fakeLimitPos)
      return
    }
    var old = effectPosInfo.rectArea
    fakeLimitPos = FakeLimitPos(RectF(old.left * scaleWidth + offsetX, old.top * scaleHeight + offsetY,
        old.right * scaleWidth + offsetX, old.bottom * scaleHeight + offsetY),
        effectPosInfo.degree,
        effectPosInfo.width * 2 * scaleWidth,
        effectPosInfo.height * 2 * scaleHeight)
    var fakePosInfo = FakePosInfo(
        effectPosInfo.centerPosX * scaleWidth + offsetX,
        effectPosInfo.centerPosY * scaleHeight + offsetY,
        effectPosInfo.width * scaleWidth,
        effectPosInfo.height * scaleHeight,
        effectPosInfo.degree)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?, maskInfo: EffectMaskInfo?) {
    var fakeLimitPos = FakeLimitPos(
        RectF(offsetX.toFloat(), offsetY.toFloat(),
            (measuredWidth - offsetX).toFloat(), (measuredHeight - offsetY).toFloat()),
        0F,
        ((measuredWidth - 2 * offsetX) * 2).toFloat(),
        ((measuredHeight - 2 * offsetY) * 2).toFloat())
    if (maskInfo == null || effectPosInfo == null) {
      this.setTarget(iFakeDraw, null, fakeLimitPos)
      return
    }
    var old = effectPosInfo.rectArea
    fakeLimitPos = FakeLimitPos(RectF(old.left * scaleWidth + offsetX, old.top * scaleHeight + offsetY,
        old.right * scaleWidth + offsetX, old.bottom * scaleHeight + offsetY),
        effectPosInfo.degree,
        effectPosInfo.width * 2 * scaleWidth,
        effectPosInfo.height * 2 * scaleHeight)
    var fakePosInfo = FakePosInfo(maskInfo.centerX * scaleWidth + offsetX,
        maskInfo.centerY * scaleHeight + offsetY,
        maskInfo.radiusX * 2 * scaleWidth,
        maskInfo.radiusY * 2 * scaleHeight,
        maskInfo.rotation)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun getFakePosInfo(): FakePosInfo? {
    if (iFakeDraw == null || iFakeDraw!!.fakePosInfo == null)
      return null
    return FakePosInfo((iFakeDraw!!.fakePosInfo!!.centerX - offsetX) / scaleWidth,
        (iFakeDraw!!.fakePosInfo!!.centerY - offsetY) / scaleHeight,
        iFakeDraw!!.fakePosInfo!!.width / scaleWidth,
        iFakeDraw!!.fakePosInfo!!.height / scaleHeight,
        iFakeDraw!!.fakePosInfo!!.degrees)
  }

  override fun updateChromaColor(color: Int) {
    if (iFakeDraw is ChromaDraw) {
      (iFakeDraw as ChromaDraw).updateColor(color)
      invalidate()
    }
  }

  override fun setStreamSize(size: VeMSize) {
    // 计算video在View中的size
    val fitInSize = QESizeUtil.getFitInSize(size, VeMSize(measuredWidth, measuredHeight))
    // 计算offset
    offsetX = ((measuredWidth - fitInSize.width) / 2).coerceAtLeast(0)
    offsetY = ((measuredHeight - fitInSize.height) / 2).coerceAtLeast(0)
    // 计算比例
    scaleWidth = fitInSize.width.toFloat() / size.width
    scaleHeight = fitInSize.height.toFloat() / size.height

  }

  override fun setFakeViewListener(listener: IFakeViewListener) {
    fakeViewListener = listener
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    if (canvas == null || iFakeDraw == null || iFakeDraw?.fakePosInfo == null) {
      return
    }
    iFakeDraw?.fakePosInfo?.let { iFakeDraw?.drawView(canvas, it) }
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (event == null || iFakeDraw == null || iFakeDraw?.fakePosInfo == null || iFakeDraw?.fakeLimitPos == null) {
      return false
    }
    return iFakeDraw!!.onTouchEvent(event)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    this.iFakeDraw?.fakeViewListener = null
    this.iFakeDraw = null
    this.fakeViewListener = null
  }
}