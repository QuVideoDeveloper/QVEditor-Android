package com.quvideo.application.editor.fake

import android.graphics.Canvas
import android.graphics.PointF
import android.view.MotionEvent
import com.quvideo.application.utils.DeviceSizeUtil

abstract class IFakeDraw {

  protected var dp1px = DeviceSizeUtil.dpToPixel(1f)
  protected var dp2px = 2 * dp1px
  protected var dp6px = 6 * dp1px

  /****************** 手势信息参数部分  */
  private val SINGLE_MODE_MOVE = 0
  private val SINGLE_MODE_SCALE_Y_TOP = 1
  private val SINGLE_MODE_SCALE_Y_BOTTOM = 2
  private val SINGLE_MODE_SCALE_X_LEFT = 3
  private val SINGLE_MODE_SCALE_X_RIGHT = 4

  /** 单指点击的区域：0-move移动区域 1,2-radius横向放大区域 3,4-radius_x横向放大区域  */
  private var singleMode = SINGLE_MODE_MOVE

  /** 单指点击记录的x  */
  private var firstLastX: Float = 0f

  /** 单指点击记录的y  */
  private var firstLastY: Float = 0f

  /** 是否点击按下  */
  private var isActionDown = false

  /** 是否还可以执行单指拖动  */
  private var isCanActionDrag = false

  /** 首次按下时间，用于快速点击判断使用  */
  private var actionDownTime: Long = 0

  /** 是否可以开始旋转  */
  private var mIsInRomating = false

  /** 是否可以开始缩放  */
  private var mIsInScaling = false

  /** 初始手指距离  */
  private var mInitialDistance: Float = 0f

  /** 初始手指角度  */
  private var mInitialRotation: Float = 0.0f

  /** mask移动前的中心点x  */
  private var mOldCenterX: Float = 0.0f

  /** mask移动前的中心点y  */
  private var mOldCenterY: Float = 0.0f

  /** mask缩放前宽度  */
  private var mOldWidth: Float = 0.0f

  /** mask缩放前半径  */
  private var mOldHeight: Float = 0.0f

  /** mask旋转前角度  */
  private var mOldMaskRotation: Float = 0.0f

  var fakeLimitPos: FakeLimitPos? = null

  var fakePosInfo: FakePosInfo? = null

  var fakeViewListener: IFakeViewListener? = null

  abstract fun drawView(
    canvas: Canvas,
    fakePosInfo: FakePosInfo
  )

  /** 是否单指操作都作为拖拽处理,蒙版的会有作为单边拉长拉短的操作 */
  open fun supportSingleSideDrag(): Boolean {
    return false
  }

  /** 是否支持单指拖拽 */
  open fun supportDrag(): Boolean {
    return true
  }

  /** 是否支持多指缩放，不是所有的都需要支持 */
  open fun supportMultiScale(): Boolean {
    return false
  }

  /** 是否支持多指旋转，不是所有的都需要支持 */
  open fun supportMultiRotate(): Boolean {
    return true
  }

  fun onTouchEvent(event: MotionEvent): Boolean {
    if (fakePosInfo == null) {
      return true
    }
    if (event.action == MotionEvent.ACTION_DOWN) {
      handleDown(event)
    } else if (event.action == MotionEvent.ACTION_UP
        || event.action == MotionEvent.ACTION_CANCEL) {
      // 松手点击
      handleUp(event)
    } else if (event.action == MotionEvent.ACTION_MOVE) {
      // 移动
      if (event.pointerCount == 1) {
        // 单指拖拽
        handleDrag(event)
      } else if (event.pointerCount > 1) {
        handleRotateAndScale(event)
      }
    }
    return true
  }

  /** 点击结束 */
  private fun handleDown(event: MotionEvent) {
    // 点击下去
    if (event.pointerCount == 1 && !isActionDown) {
      isActionDown = true
      isCanActionDrag = true
      actionDownTime = System.currentTimeMillis()
    }
    firstLastX = event.getX(0)
    firstLastY = event.getY(0)
    mOldCenterX = fakePosInfo?.centerX!!
    mOldCenterY = fakePosInfo?.centerY!!
    mOldWidth = fakePosInfo?.width!!
    mOldHeight = fakePosInfo?.height!!
    singleMode = getSingleTouchMode()
    fakeViewListener?.onEffectMoveStart()
  }

  /** 点击结束 */
  private fun handleUp(event: MotionEvent) {
    mInitialDistance = 0f
    mInitialRotation = 0f
    mIsInRomating = false
    mIsInScaling = false
    isCanActionDrag = false
    fakeViewListener?.onEffectMoveEnd(!isActionDown)
    if (!isActionDown) {
      return
    }
    isActionDown = false
    if (System.currentTimeMillis() - actionDownTime < 300) {
      if (event == null)
        return
      // 这边判断是轻点了一下咯
      fakeViewListener?.checkEffectTouchHit(PointF(event?.getX(), event?.getY()))
    }
  }

  /** 处理单指移动 */
  private fun handleDrag(event: MotionEvent) {
    if (!isCanActionDrag || !supportDrag()) {
      return
    }
    // 单点处理
    val firstNewX = event.getX(0)
    val firstNewY = event.getY(0)
    if (isActionDown) {
      val offsetX = firstNewX - firstLastX
      val offsetY = firstNewY - firstLastY
      val moveOffset = Math.sqrt(offsetX * offsetX + offsetY + offsetY.toDouble())
          .toFloat()
      if (moveOffset <= dp2px) {
        // 移动距离不多，先不处理，以防误触
        return
      }
      isActionDown = false
    }
    if (singleMode == SINGLE_MODE_MOVE) {
      // 拖拽移动中心点
      val offsetX = firstNewX - firstLastX
      val offsetY = firstNewY - firstLastY
      var newPoint = PointF(mOldCenterX + offsetX, mOldCenterY + offsetY)
      if (fakeLimitPos?.limitRectF != null) {
        // 有移动限制区域
        val centerX: Float = fakeLimitPos?.limitRectF!!.centerX()
        val centerY: Float = fakeLimitPos?.limitRectF!!.centerY()
        val newRelPoint: PointF = FakePosUtils.calcNewPoint(newPoint, PointF(centerX, centerY), -fakeLimitPos?.limitRotation!!)
        if (newRelPoint.x > fakeLimitPos?.limitRectF!!.right) {
          newRelPoint.x = fakeLimitPos?.limitRectF!!.right
        } else if (newRelPoint.x < fakeLimitPos?.limitRectF!!.left) {
          newRelPoint.x = fakeLimitPos?.limitRectF!!.left
        }
        if (newRelPoint.y > fakeLimitPos?.limitRectF!!.bottom) {
          newRelPoint.y = fakeLimitPos?.limitRectF!!.bottom
        } else if (newRelPoint.y < fakeLimitPos?.limitRectF!!.top) {
          newRelPoint.y = fakeLimitPos?.limitRectF!!.top
        }
        // 把点再旋转回来
        newPoint = FakePosUtils.calcNewPoint(newRelPoint, PointF(centerX, centerY), fakeLimitPos?.limitRotation!!)
      }
      if (!newPoint.equals(fakePosInfo?.centerX!!, fakePosInfo?.centerY!!)) {
        fakePosInfo?.centerX = newPoint.x
        fakePosInfo?.centerY = newPoint.y
        fakeViewListener?.onEffectMoving()
      }
    } else {
      val lastOldPoint: PointF = FakePosUtils.calcNewPoint(
          PointF(firstLastX, firstLastY), PointF(fakePosInfo?.centerX!!, fakePosInfo?.centerY!!),
          -fakePosInfo?.degrees!!
      )
      val newOldPoint: PointF = FakePosUtils.calcNewPoint(
          PointF(firstNewX, firstNewY), PointF(fakePosInfo?.centerX!!, fakePosInfo?.centerY!!),
          -fakePosInfo?.degrees!!
      )
      val offsetX = newOldPoint.x - lastOldPoint.x
      val offsetY = newOldPoint.y - lastOldPoint.y
      val isCheckHeight = singleMode == SINGLE_MODE_SCALE_Y_TOP || singleMode == SINGLE_MODE_SCALE_Y_BOTTOM
      val isCheckLeft = singleMode == SINGLE_MODE_SCALE_Y_TOP || singleMode == SINGLE_MODE_SCALE_X_LEFT
      val offset = if (isCheckHeight) offsetY else offsetX
      val old = if (isCheckHeight) mOldHeight else mOldWidth
      val newValue = if (isCheckLeft) (old - offset) else (old + offset)
      if (newValue > 0) {
        if (isCheckHeight) {
          fakePosInfo?.height = newValue
          if (fakePosInfo?.height!! > fakeLimitPos?.maxHeight!!) {
            fakePosInfo?.height = fakeLimitPos?.maxHeight!!
          }
        } else {
          fakePosInfo?.width = newValue
          if (fakePosInfo?.width!! > fakeLimitPos?.maxWidth!!) {
            fakePosInfo?.width = fakeLimitPos?.maxWidth!!
          }
        }
        fakeViewListener?.onEffectMoving()
      }
    }
  }

  /** 双指缩放旋转 */
  private fun handleRotateAndScale(event: MotionEvent) {
    isActionDown = false
    isCanActionDrag = false
    // 多点缩放旋转
    if (mInitialDistance > 0) {
      val curDistance: Float = FakePosUtils.distance(event)
      val curRotation: Float = FakePosUtils.getRotation(event)
      val disOffset = curDistance - mInitialDistance
      val disRotate = curRotation - mInitialRotation
      var isChanged = false
      if (supportMultiScale()) {
        if (mIsInScaling) {
          var scale = curDistance / mInitialDistance
          if (mOldHeight * scale > fakeLimitPos?.maxHeight!!) {
            scale = fakeLimitPos?.maxHeight!! / mOldHeight
          }
          if (mOldWidth * scale > fakeLimitPos?.maxWidth!!) {
            scale = fakeLimitPos?.maxWidth!! / mOldWidth
          }
          fakePosInfo?.height = mOldHeight * scale
          fakePosInfo?.width = mOldWidth * scale
          isChanged = true
        } else if (Math.abs(disOffset) > dp6px) {
          // 大于6dp的时候才开始转缩放，这样才能在旋转的时候比较稳定
          mIsInScaling = true
          mInitialDistance = FakePosUtils.distance(event)
        }
      }
      if (supportMultiRotate()) {
        if (mIsInRomating) {
          fakePosInfo?.degrees = mOldMaskRotation + disRotate
          fakePosInfo?.degrees = FakePosUtils.calcNewRotation(fakePosInfo?.degrees!!)
          isChanged = true
        } else if (Math.abs(disRotate) > 5) {
          // disRotate保证一定是正数了，大于5度才开始转圈圈，这样才能在缩放的时候比较稳定
          mIsInRomating = true
          mInitialRotation = FakePosUtils.getRotation(event)
          mOldMaskRotation = fakePosInfo?.degrees!!
        }
      }
      if (isChanged) {
        fakeViewListener?.onEffectMoving()
      }
    } else {
      mInitialDistance = FakePosUtils.distance(event)
      mInitialRotation = FakePosUtils.getRotation(event)
      mOldMaskRotation = fakePosInfo?.degrees!!
      mOldHeight = fakePosInfo?.height!!
      mOldWidth = fakePosInfo?.width!!
    }
  }

  /** 根据单指点击位置，计算操作类型 */
  private fun getSingleTouchMode(): Int {
    if (fakePosInfo == null || !supportSingleSideDrag()) {
      return SINGLE_MODE_MOVE
    }
    // 旋转前的坐标点
    val originalPoint: PointF = FakePosUtils.calcNewPoint(
        PointF(firstLastX, firstLastY),
        PointF(fakePosInfo!!.centerX + fakePosInfo!!.anchorOffsetX, fakePosInfo!!.centerY + fakePosInfo!!.anchorOffsetY),
        -fakePosInfo!!.degrees)
    if (originalPoint.y <= fakePosInfo!!.centerY + fakePosInfo!!.anchorOffsetY - fakePosInfo!!.height / 2 + fakePosInfo!!.height / 10) {
      // 正方形的上下两边是拉高的区域,区域上方
      return SINGLE_MODE_SCALE_Y_TOP
    } else if (originalPoint.y >= fakePosInfo!!.centerY + fakePosInfo!!.anchorOffsetY + fakePosInfo!!.height / 2 - fakePosInfo!!.height / 10) {
      // 正方形的上下两边是拉高的区域,区域下方
      return SINGLE_MODE_SCALE_Y_BOTTOM
    } else {
      if (originalPoint.x <= fakePosInfo!!.centerX + fakePosInfo!!.anchorOffsetX - fakePosInfo!!.width / 2 + fakePosInfo!!.width / 10) {
        // 正方形的左右两边是拉宽的区域,区域右方
        return SINGLE_MODE_SCALE_X_LEFT
      } else if (originalPoint.x >= fakePosInfo!!.centerX + fakePosInfo!!.anchorOffsetX + fakePosInfo!!.width / 2 - fakePosInfo!!.width / 10) {
        return SINGLE_MODE_SCALE_X_RIGHT
      } else {
        // 中间，移动处理
        return SINGLE_MODE_MOVE
      }
    }
  }
}