package com.quvideo.application.editor.fake

import android.graphics.PointF
import android.view.MotionEvent
import com.quvideo.mobile.engine.entity.VeMSize
import com.quvideo.mobile.engine.model.clip.ClipPosInfo
import com.quvideo.mobile.engine.model.effect.EffectMaskInfo
import com.quvideo.mobile.engine.model.effect.EffectPosInfo
import com.quvideo.mobile.engine.utils.QESizeUtil

object FakePosUtils {

  /**
   * FakePosInfo转EffectPosInfo
   */
  fun updateEffectPosByFakePos(
    fakePosInfo: FakePosInfo?,
    effectPosInfo: EffectPosInfo?
  ) {
    if (fakePosInfo == null || effectPosInfo == null) {
      return
    }
    effectPosInfo.center.x = fakePosInfo.centerX
    effectPosInfo.center.y = fakePosInfo.centerY
    effectPosInfo.size.x = fakePosInfo.width
    effectPosInfo.size.y = fakePosInfo.height
    effectPosInfo.degree.z = fakePosInfo.degrees
  }

  /**
   * FakePosInfo转EffectPosInfo
   */
  fun updateEffectPosByFakePos(
    fakePosInfo: FakePosInfo?,
    effectPosInfo: EffectPosInfo?,
    curAxle: Int,
    oldAnchor: PointF?
  ) {
    if (fakePosInfo == null || effectPosInfo == null) {
      return
    }
    effectPosInfo.size.x = fakePosInfo.width
    effectPosInfo.size.y = fakePosInfo.height
    if (curAxle == 0) {
      effectPosInfo.degree.z = fakePosInfo.degrees
    } else if (curAxle == 1) {
      effectPosInfo.degree.x = fakePosInfo.degrees
    } else if (curAxle == 2) {
      effectPosInfo.degree.y = fakePosInfo.degrees
    } else {
      effectPosInfo.degree.z = fakePosInfo.degrees
    }
    if (curAxle == 3 && oldAnchor != null) {
      effectPosInfo.center.x = oldAnchor.x
      effectPosInfo.center.y = oldAnchor.y
      val rotatePoint = calcNewPoint(PointF(fakePosInfo.centerX - oldAnchor.x, fakePosInfo.centerY - oldAnchor.y),
          PointF(0f, 0f), -effectPosInfo.degree.z)
      effectPosInfo.anchorOffset.x = -rotatePoint.x
      effectPosInfo.anchorOffset.y = -rotatePoint.y
    } else {
      //      val rotatePoint = calcNewPoint(PointF(-effectPosInfo.anchor.x, -effectPosInfo.anchor.y),
      //          PointF(0f, 0f), effectPosInfo.degree.z)
      effectPosInfo.center.x = fakePosInfo.centerX
      effectPosInfo.center.y = fakePosInfo.centerY
    }
  }

  /**
   * FakePosInfo转EffectMaskInfo
   */
  fun updateMaskPos2FakePos(
    fakePosInfo: FakePosInfo?,
    maskInfo: EffectMaskInfo?
  ) {
    if (fakePosInfo == null || maskInfo == null) {
      return
    }
    maskInfo.centerX = fakePosInfo.centerX
    maskInfo.centerY = fakePosInfo.centerY
    maskInfo.radiusX = fakePosInfo.width / 2
    maskInfo.radiusY = fakePosInfo.height / 2
    maskInfo.rotation = fakePosInfo.degrees
  }

  /**
   * FakePosInfo转ClipPosInfo
   */
  fun updateClipPos2FakePos(
    fakePosInfo: FakePosInfo?,
    clipPosInfo: ClipPosInfo?,
    streamSize: VeMSize?,
    clipSize: VeMSize?
  ) {
    if (fakePosInfo == null || clipPosInfo == null || streamSize == null || clipSize == null) {
      return
    }
    clipPosInfo.centerPosX = fakePosInfo.centerX
    clipPosInfo.centerPosY = fakePosInfo.centerY
    clipPosInfo.degree = fakePosInfo.degrees
    val targetSize = QESizeUtil.getFitInSize(clipSize, streamSize) ?: return
    if (targetSize.width == streamSize.width) {
      clipPosInfo.widthScale = fakePosInfo.width / streamSize.width
      clipPosInfo.heightScale = clipPosInfo.widthScale
    } else if (targetSize.height == streamSize.height) {
      clipPosInfo.widthScale = fakePosInfo.height / streamSize.height
      clipPosInfo.heightScale = clipPosInfo.widthScale
    } else {
      return
    }
  }

  /**
   * 获取相对位置
   */
  fun getChromaColorPosByFakePos(fakePosInfo: FakePosInfo?, effectPosInfo: EffectPosInfo?): PointF {
    if (fakePosInfo == null || effectPosInfo == null) {
      return PointF(0F, 0F)
    }
    val curRectF = effectPosInfo.rectArea;
    val newPointF = calcNewPoint(PointF(fakePosInfo.centerX, fakePosInfo.centerY),
        PointF(effectPosInfo.center.x, effectPosInfo.center.y), effectPosInfo.degree.z)
    return PointF(newPointF.x - effectPosInfo.center.x + effectPosInfo.size.x / 2,
        newPointF.y - effectPosInfo.center.y + effectPosInfo.size.y / 2)
  }

  fun distance(event: MotionEvent): Float {
    val x = event.getX(0) - event.getX(1)
    val y = event.getY(0) - event.getY(1)
    return Math.sqrt(x * x + y * y.toDouble()).toFloat()
  }

  fun distance(p1: PointF, p2: PointF): Float {
    val x = p1.x - p2.x
    val y = p1.y - p2.y
    return Math.sqrt(x * x + y * y.toDouble()).toFloat()
  }

  fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val x = x1 - x2
    val y = y1 - y2
    return Math.sqrt(x * x + y * y.toDouble()).toFloat()
  }

  fun midpoint(event: MotionEvent, point: PointF) {
    val x1 = event.getX(0)
    val y1 = event.getY(0)
    val x2 = event.getX(1)
    val y2 = event.getY(1)
    midpoint(x1, y1, x2, y2, point)
  }

  fun midpoint(x1: Float, y1: Float, x2: Float, y2: Float, point: PointF) {
    point.x = (x1 + x2) / 2.0f
    point.y = (y1 + y2) / 2.0f
  }

  /**
   * 取旋转角度
   */
  fun getRotation(event: MotionEvent): Float {
    // 坐标系的原因，y需要取反，因为y的方向和坐标系是反的
    return angle(event.getX(0), event.getY(1),
        event.getX(1), event.getY(0))
  }

  /**
   * 确保角度在0~360
   * @param rotation
   * @return
   */
  fun calcNewRotation(rotation: Float): Float {
    var rotation = rotation
    while (true) {
      if (rotation > 360) {
        rotation -= 360f
      } else if (rotation < 0) {
        rotation += 360f
      } else {
        break
      }
    }
    return rotation
  }

  /**
   * 计算旋转后的位置
   */
  fun calcNewPoint(oldPoint: PointF, centerPoint: PointF, rotation: Float): PointF {
    // calc arc
    val agree = Math.toRadians(rotation.toDouble())
    //sin/cos value
    val cosv = Math.cos(agree)
    val sinv = Math.sin(agree)
    // calc new point
    val newX = ((oldPoint.x - centerPoint.x) * cosv - (oldPoint.y - centerPoint.y) * sinv + centerPoint.x).toFloat()
    val newY = ((oldPoint.x - centerPoint.x) * sinv + (oldPoint.y - centerPoint.y) * cosv + centerPoint.y).toFloat()
    return PointF(newX, newY)
  }

  fun angle(p1: PointF, p2: PointF): Float {
    return angle(p1.x, p1.y, p2.x, p2.y)
  }

  fun angle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val radians = Math.atan2(y2 - y1.toDouble(), x2 - x1.toDouble())
    var agree = Math.toDegrees(radians).toFloat()
    if (agree > 0) {
      while (agree > 360) {
        agree = agree - 360
      }
      agree = 360 - agree
    } else {
      agree = Math.abs(agree)
      while (agree >= 360) {
        agree = agree - 360
      }
    }
    return agree
  }
}