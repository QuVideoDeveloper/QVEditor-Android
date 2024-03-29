package com.quvideo.application.editor.fake

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.quvideo.application.editor.effect.mask.FakeMaskPosData
import com.quvideo.application.editor.fake.draw.ChromaDraw
import com.quvideo.application.editor.fake.draw.PosDraw
import com.quvideo.mobile.engine.entity.VeMSize
import com.quvideo.mobile.engine.model.clip.ClipPosInfo
import com.quvideo.mobile.engine.model.effect.EffectPosInfo
import com.quvideo.mobile.engine.slide.SlidePosInfo
import com.quvideo.mobile.engine.utils.QESizeUtil
import xiaoying.engine.base.QTransformInfo

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

  private fun setTarget(iFakeDraw: IFakeDraw?, fakePosInfo: FakePosInfo?, fakeLimitPos: FakeLimitPos?) {
    this.iFakeDraw?.fakeViewListener = null
    this.iFakeDraw = iFakeDraw
    this.iFakeDraw?.fakePosInfo = fakePosInfo
    this.iFakeDraw?.fakeLimitPos = fakeLimitPos
    this.iFakeDraw?.fakeViewListener = object : IFakeViewListener {
      override fun onEffectMoving(pointX: Float, pointY: Float) {
        invalidate()
        fakeViewListener?.onEffectMoving(pointX, pointY)
      }

      override fun onEffectMoveStart() {
        fakeViewListener?.onEffectMoveStart()
      }

      override fun onEffectMoveEnd(moved: Boolean) {
        fakeViewListener?.onEffectMoveEnd(moved)
      }

      override fun checkEffectTouchHit(pointF: PointF) {
        pointF.x = (pointF.x - offsetX) / scaleWidth
        pointF.y = (pointF.y - offsetY) / scaleHeight
        fakeViewListener?.checkEffectTouchHit(pointF)
      }
    }
    invalidate()
  }

  override fun setClipTarget(iFakeDraw: IFakeDraw?, cropRect: Rect) {
    val fakeLimitPos = FakeLimitPos(RectF(offsetX.toFloat(), offsetY.toFloat(),
        (measuredWidth - offsetX).toFloat(), (measuredHeight - offsetY).toFloat()),
        0F,
        ((measuredWidth - 2 * offsetX) * 2).toFloat(),
        ((measuredHeight - 2 * offsetY) * 2).toFloat())
    var fakePosInfo = FakePosInfo(
        (cropRect.left + cropRect.right) * scaleWidth / 2 + offsetX,
        (cropRect.top + cropRect.bottom) * scaleHeight / 2 + offsetY,
        (cropRect.right - cropRect.left) * scaleWidth,
        (cropRect.bottom - cropRect.top) * scaleHeight,
        0F, 0F, 0F)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setSlideClipTarget(iFakeDraw: IFakeDraw?, slidePosInfo: SlidePosInfo?, size: VeMSize) {
    val fakeLimitPos = FakeLimitPos(RectF(offsetX.toFloat(), offsetY.toFloat(),
        (measuredWidth - offsetX).toFloat(), (measuredHeight - offsetY).toFloat()),
        0F,
        ((measuredWidth - 2 * offsetX) * 2).toFloat(),
        ((measuredHeight - 2 * offsetY) * 2).toFloat())
    if (slidePosInfo == null) {
      this.setTarget(iFakeDraw, null, fakeLimitPos)
      return
    }
    var fakePosInfo = FakePosInfo(
        slidePosInfo.centerX * scaleWidth + offsetX,
        slidePosInfo.centerY * scaleHeight + offsetY,
        slidePosInfo.width * scaleWidth,
        slidePosInfo.height * scaleHeight,
        slidePosInfo.mAngle.toFloat(), 0f, 0f)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setClipTarget(iFakeDraw: IFakeDraw?, clipPosInfo: ClipPosInfo?, size: VeMSize) {
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
        clipPosInfo.widthScale * size.width * scaleWidth,
        clipPosInfo.heightScale * size.height * scaleHeight,
        clipPosInfo.degree, 0F, 0F)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setPlayerTarget(iFakeDraw: IFakeDraw?, transform: QTransformInfo, size: VeMSize) {
    val fakeLimitPos = FakeLimitPos(RectF(-50F * measuredWidth,
        -50F * measuredHeight,
        50F * measuredWidth,
        50F * measuredHeight),
        0F,
        ((measuredWidth - 2 * offsetX) * 50).toFloat(),
        ((measuredHeight - 2 * offsetY) * 50).toFloat())
    var fakePosInfo = FakePosInfo(
        transform.mShiftX * size.width * scaleWidth + offsetX,
        transform.mShiftY * size.height * scaleHeight + offsetY,
        transform.mScaleX * size.width * scaleWidth,
        transform.mScaleY * size.height * scaleHeight,
        transform.mAngleZ, 0F, 0F)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setPaintTarget(iFakeDraw: IFakeDraw?, size: VeMSize) {
    val fakeLimitPos = FakeLimitPos(RectF(-Float.MAX_VALUE,
        -Float.MAX_VALUE,
        Float.MAX_VALUE,
        Float.MAX_VALUE),
        0F,
        Float.MAX_VALUE,
        Float.MAX_VALUE)
    var fakePosInfo = FakePosInfo(
        size.width / 2F,
        size.height / 2F,
        size.width.toFloat(),
        size.height.toFloat(),
        0F, 0F, 0F)
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
    val fakePosInfo = FakePosInfo(
        effectPosInfo.center.x * scaleWidth + offsetX,
        effectPosInfo.center.y * scaleHeight + offsetY,
        effectPosInfo.size.x * scaleWidth,
        effectPosInfo.size.y * scaleHeight,
        effectPosInfo.degree.z, effectPosInfo.anchorOffset.x * scaleWidth, effectPosInfo.anchorOffset.y * scaleHeight)
      Log.d(TAG, "setTarget() fake pos: $fakePosInfo, fake limit: $fakeLimitPos")
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?, startDegree: Float) {
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
        effectPosInfo.center.x * scaleWidth + offsetX,
        effectPosInfo.center.y * scaleHeight + offsetY,
        effectPosInfo.size.x * scaleWidth,
        effectPosInfo.size.y * scaleHeight,
        startDegree, effectPosInfo.anchorOffset.x * scaleWidth, effectPosInfo.anchorOffset.y * scaleHeight)
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
        effectPosInfo.degree.z,
        effectPosInfo.size.x * 2 * scaleWidth,
        effectPosInfo.size.y * 2 * scaleHeight)
    var fakePosInfo = FakePosInfo(
        effectPosInfo.center.x * scaleWidth + offsetX,
        effectPosInfo.center.y * scaleHeight + offsetY,
        effectPosInfo.size.x * scaleWidth,
        effectPosInfo.size.y * scaleHeight,
        effectPosInfo.degree.z, effectPosInfo.anchorOffset.x * scaleWidth, effectPosInfo.anchorOffset.y * scaleHeight)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun setTarget(iFakeDraw: IFakeDraw?, effectPosInfo: EffectPosInfo?, maskInfo: FakeMaskPosData?) {
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
        effectPosInfo.degree.z,
        effectPosInfo.size.x * 2 * scaleWidth,
        effectPosInfo.size.y * 2 * scaleHeight)
    var fakePosInfo = FakePosInfo(maskInfo.centerX * scaleWidth + offsetX,
        maskInfo.centerY * scaleHeight + offsetY,
        maskInfo.radiusX * 2 * scaleWidth,
        maskInfo.radiusY * 2 * scaleHeight,
        maskInfo.rotation, effectPosInfo.anchorOffset.x * scaleWidth, effectPosInfo.anchorOffset.y * scaleHeight)
    this.setTarget(iFakeDraw, fakePosInfo, fakeLimitPos)
  }

  override fun getFakePosInfo(): FakePosInfo? {
    if (iFakeDraw == null || iFakeDraw!!.fakePosInfo == null)
      return null
    return FakePosInfo((iFakeDraw!!.fakePosInfo!!.centerX - offsetX) / scaleWidth,
        (iFakeDraw!!.fakePosInfo!!.centerY - offsetY) / scaleHeight,
        iFakeDraw!!.fakePosInfo!!.width / scaleWidth,
        iFakeDraw!!.fakePosInfo!!.height / scaleHeight,
        iFakeDraw!!.fakePosInfo!!.degrees,
        iFakeDraw!!.fakePosInfo!!.anchorOffsetX / scaleWidth,
        iFakeDraw!!.fakePosInfo!!.anchorOffsetY / scaleHeight)
  }

  override fun updateChromaColor(color: Int) {
    if (iFakeDraw is ChromaDraw) {
      (iFakeDraw as ChromaDraw).updateColor(color)
      invalidate()
    }
  }

  override fun setOldAnchor(oldAnchor: PointF) {
    if (iFakeDraw is PosDraw) {
      (iFakeDraw as PosDraw).oldAnchorPointF = PointF(oldAnchor.x * scaleWidth + offsetX, oldAnchor.y * scaleHeight + offsetY)
    }
  }

  override fun getOldAnchor(): PointF? {
    if (iFakeDraw is PosDraw) {
      var oldAnchor = (iFakeDraw as PosDraw).oldAnchorPointF
      return PointF((oldAnchor.x - offsetX) / scaleWidth, (oldAnchor.y - offsetY) / scaleHeight)
    }
    return null
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

  override fun trans2StreamPoint(pointX: Float, pointY: Float): PointF {
    val resultX = (pointX - offsetX) / scaleWidth;
    val resultY = (pointY - offsetY) / scaleHeight;
    return PointF(resultX, resultY)
  }

  override fun setFakeViewListener(listener: IFakeViewListener?) {
    fakeViewListener = listener
  }

  override fun getHostView(): View = this

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

    companion object {
        private const val TAG = "FakeView"
    }
}