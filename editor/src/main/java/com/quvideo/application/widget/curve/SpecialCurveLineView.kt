package com.quvideo.application.widget.curve

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Paint.Cap
import android.graphics.Paint.Join.ROUND
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.Path.Op.INTERSECT
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import com.quvideo.application.editor.R
import com.quvideo.application.utils.DeviceSizeUtil
import com.quvideo.mobile.engine.entity.VeRange
import xiaoying.utils.QPoint
import java.util.LinkedList
import kotlin.math.pow
import kotlin.math.sqrt

class SpecialCurveLineView @JvmOverloads constructor(
  context: Context?,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  enum class ColorSpLineType {
    RGB,
  }

  //************************ 绘制相关 *************************/
  private lateinit var mCurvePaint: Paint
  private lateinit var mKnotPaint: Paint
  private lateinit var mTextPaint: Paint
  private lateinit var mBgPaint: Paint
  private lateinit var mSlideBtnPaint: Paint
  private lateinit var mDeleteBtnPaint: Paint

  private var selectPath = Path()
  private var unSelectPath = Path()
  private var curRectPath = Path() //记录曲线轨迹的大致范围，用来判断触摸点是否在曲线轨迹附近
  private var curSelectKnotPath = Path()   //当前选中点的范围path，用来判断触摸点是否在选中点
  private var bottomTriangleSlidePath = Path() //下滑动按钮三角箭头path
  private var rightTriangleSlidePath = Path() //右滑动按钮三角箭头path
  private var bottomSlideBtnPath = Path() //下滑动按钮path,用来判断触摸是否在该按钮上
  private var rightSlideBtnPath = Path() //右滑动按钮path,用来判断触摸是否在该按钮上
  private var deleteBtnPath = Path() //删除按钮path,用来判断触摸是否在该按钮上

  /**整个View的背景 */
  private var mBgColor = resources.getColor(R.color.opacity_5_black)

  /**背景格子线颜色 */
  private var mBgLineColor = resources.getColor(R.color.color_33ffffff)

  /**选中控制点外边框颜色 */
  private var mSelectKnotStrokeColor = resources.getColor(R.color.white)

  /**背景格子数 */
  private var mBgLineCount = 4

  /**背景格子线粗细 */
  private var mBgLineWidth = DeviceSizeUtil.dpToPixel(0.5f)

  /**曲线线条粗细 */
  private var mCurveLineWidth = DeviceSizeUtil.dpToPixel(1f)

  /**被选中控制点大小 */
  private var mSelectedKnotRadius = DeviceSizeUtil.dpToPixel(6f)

  /**曲线控制点默认大小 */
  private var mDefaultKnotRadius = DeviceSizeUtil.dpToPixel(4.5f)

  /**控制点边框粗细 */
  private var mKnotStrokeWidth = DeviceSizeUtil.dpToPixel(1f)

  /**控制点最小间距 */
  private var minKnotsSpacing = mSelectedKnotRadius * 2 - mCurveLineWidth;

  /**边界空余大小，防止边界处无法移动控制点 */
  private val mFixSize = DeviceSizeUtil.dpToPixel(8f)

  /**可滑动按钮区域 */
  private val mSlideBtnRangeSize = DeviceSizeUtil.dpToPixel(24f)

  /**可滑动按钮宽度 */
  private val mSlideBtnWidth = DeviceSizeUtil.dpToPixel(14f)

  /**可滑动按钮三角高度 */
  private val mSlideBtnTriangleHeight = DeviceSizeUtil.dpToPixel(3.5f)

  /**可滑动按钮圆角 */
  private val mSlideBtnRadius = DeviceSizeUtil.dpToPixel(1f)

  /**可滑动按钮间距 */
  private val mSlideBtnMargin = DeviceSizeUtil.dpToPixel(14f)

  /**删除按钮宽 */
  private val mDeleteBtnWidth = DeviceSizeUtil.dpToPixel(32f)

  /**删除按钮高 */
  private val mDeleteBtnHeight = DeviceSizeUtil.dpToPixel(26f)

  /**删除按钮间距 */
  private val mDeleteBtnMargin = DeviceSizeUtil.dpToPixel(12f)

  /**删除按钮圆角 */
  private val mDeleteBtnRadius = DeviceSizeUtil.dpToPixel(4f)

  /**删除图标上边距 */
  private val mDeleteIconMarginTop = DeviceSizeUtil.dpToPixel(3f)

  /**删除图标右边距 */
  private val mDeleteIconMarginRight = DeviceSizeUtil.dpToPixel(5f)

  //************************ 开关相关 *************************/

  private var enableMoveEvent = false
  private var showDeleteBtn = false
  private var selectNewPoint = false
  private var isTouchDeleteBtn = false
  private var onGestureIntercept = false
  private var enableBottomSlideMoveEvent = false
  private var enableRightSlideMoveEvent = false

  //************************ 数据相关 *************************/
  private var curveDatas: ArrayList<CurveData> = ArrayList()
  private var curKnotIdx = -1
  private var curCurveIndex = 0

  private var xRange: VeRange = VeRange(0, 100)
  private var yRange: VeRange = VeRange(0, 100)

  // 填充数据使用的
  private val sip = SpecialLineInterpolator()

  private var mWidth = 0
  private var mHeight = 0
  private var mStartX = 0
  private var mStartY = 0
  private var mEndX = 0
  private var mEndY = 0

  private var mDownX = 0f
  private var mDownY = 0f

  private var mDeleteBitmap: Bitmap? = null

  private var mGestureDetector: GestureDetector? = null

  private var mCallBack: OnCtrPointsUpdateCallBack? = null
  private var mConvertCallBack: OnCtrPontTextConvertCallBack? = null

  init {
    reloadPaints()
    mGestureDetector = GestureDetector(context, GestureListener())
  }

  private fun reloadPaints() {
    mBgPaint = Paint()
    mCurvePaint = Paint()
    mKnotPaint = Paint()
    mTextPaint = Paint()
    mSlideBtnPaint = Paint()
    mDeleteBtnPaint = Paint()

    mCurvePaint.flags = mCurvePaint.flags or Paint.ANTI_ALIAS_FLAG
    mCurvePaint.strokeWidth = mCurveLineWidth
    mCurvePaint.isDither = true
    mCurvePaint.style = STROKE
    mCurvePaint.strokeJoin = ROUND
    mCurvePaint.strokeCap = Cap.ROUND
    mCurvePaint.isAntiAlias = true

    mTextPaint.textAlign = Align.CENTER
    mTextPaint.textSize = DeviceSizeUtil.dpToPixel(9f)
    mTextPaint.color = resources.getColor(R.color.color_4E4E51)
    mTextPaint.isAntiAlias = true

    mBgPaint.isAntiAlias = true

    mKnotPaint.isAntiAlias = true

    mSlideBtnPaint.isAntiAlias = true
    mSlideBtnPaint.color = resources.getColor(R.color.white)

    mDeleteBtnPaint.isAntiAlias = true
    mDeleteBtnPaint.color = resources.getColor(R.color.color_3f3f3f)
  }

  private fun initKontList() {
    resetKnotsList()
    switchSpLineType(curCurveIndex, false)
  }

  private fun resetKnotsList() {
    for (item: CurveData in curveDatas) {
      if (item.curveType == 0) {
        item.knotsList.add(PointF(mStartX.toFloat(), mEndY.toFloat()))
        item.knotsList.add(PointF(mEndX.toFloat(), mStartY.toFloat()))
      } else if (item.curveType == 1) {
        item.knotsList.add(PointF(mStartX.toFloat(), mEndY.toFloat() / 2))
        item.knotsList.add(PointF(mEndX.toFloat(), mEndY.toFloat() / 2))
      }
    }
  }

  fun switchSpLineType(curveIndex: Int, refreshUI: Boolean) {
    curCurveIndex = curveIndex
    curKnotIdx = -1
    if (refreshUI) {
      invalidate()
    }
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    rescaleView(w, h)
    initKontList()
    invalidate()
  }

  private fun rescaleView(width: Int, height: Int) {
    mWidth = width
    mHeight = height
    mStartX = mFixSize.toInt()
    mStartY = mFixSize.toInt()
    mEndX = (mWidth - mFixSize - mSlideBtnRangeSize).toInt()
    mEndY = (mHeight - mFixSize - mSlideBtnRangeSize).toInt()
  }

  override fun draw(canvas: Canvas?) {
    super.draw(canvas)
    drawBg(canvas)
    if (curveDatas.size > 0)
      drawLineAndKnotsAndCtrl(canvas)
  }

  /** 背景 */
  private fun drawBg(canvas: Canvas?) {
    val rect = Rect(mStartX, mStartY, mEndX, mEndY)
    mBgPaint.color = mBgColor
    canvas?.drawRect(rect, mBgPaint)
    mBgPaint.color = mBgLineColor
    mBgPaint.strokeWidth = mBgLineWidth
    val dx = (mWidth - 2 * mFixSize - mSlideBtnRangeSize) / mBgLineCount
    val dy = (mHeight - 2 * mFixSize - mSlideBtnRangeSize) / mBgLineCount
    for (i in 1 until mBgLineCount) {
      canvas?.drawLine(
          (mStartX + dx * i).toFloat(), mStartY.toFloat(), (mStartX + dx * i).toFloat(),
          mEndY.toFloat(), mBgPaint
      )
      canvas?.drawLine(
          mStartX.toFloat(), (mStartY + dy * i).toFloat(), mEndX.toFloat(),
          (mStartY + dy * i).toFloat(), mBgPaint
      )
    }
  }

  /** 画选中的节点 */
  private fun drawLineAndKnotsAndCtrl(canvas: Canvas?) {
    for (index in curveDatas.indices) {
      var item = curveDatas[index]
      if (curCurveIndex != index) {
        // 未选中的曲线信息
        unSelectPath = Path()
        if (item.curveType == 0) {
          setSplinePath(unSelectPath, item.knotsList, false)
        } else if (item.curveType == 1) {
          setSinPath(unSelectPath, item.knotsList, false)
        }
        mCurvePaint.color = item.lineColor and 0x33FFFFFF
        canvas?.drawPath(unSelectPath, mCurvePaint)
      }
    }
    for (index in curveDatas.indices) {
      var item = curveDatas[index]
      if (curCurveIndex == index) {
        // 选中的曲线信息
        curRectPath.reset()
        selectPath = Path();
        if (item.curveType == 0) {
          setSplinePath(selectPath, item.knotsList, true)
        } else if (item.curveType == 1) {
          setSinPath(selectPath, item.knotsList, true)
        }
        mCurvePaint.color = item.lineColor
        canvas?.drawPath(selectPath, mCurvePaint)
        // 选中的点
        for (i in item.knotsList.indices) {
          if (i != curKnotIdx) {
            mKnotPaint.color = item.lineColor
            canvas?.drawCircle(item.knotsList[i].x, item.knotsList[i].y, mDefaultKnotRadius, mKnotPaint)
          } else {
            mKnotPaint.color = mSelectKnotStrokeColor
            canvas?.drawCircle(item.knotsList[curKnotIdx].x, item.knotsList[curKnotIdx].y,
                mSelectedKnotRadius + mKnotStrokeWidth,
                mKnotPaint)
            mKnotPaint.color = item.lineColor
            canvas?.drawCircle(item.knotsList[curKnotIdx].x, item.knotsList[curKnotIdx].y, mSelectedKnotRadius, mKnotPaint)
            drawSlideCtrlBtn(canvas, item.knotsList[curKnotIdx])
            drawDeleteBtn(canvas, item.knotsList[curKnotIdx])
            drawSelectPointText(canvas, item.knotsList[curKnotIdx])
          }
        }
      }
    }
  }

  /** 画选中点的拖动把手 */
  private fun drawSlideCtrlBtn(canvas: Canvas?, curPoint: PointF?) {
    curPoint?.let {
      val x = curPoint.x
      val y = curPoint.y
      //底部滑动按钮
      bottomTriangleSlidePath?.let {
        it.reset()
        val bottomRectLeft = x - mSlideBtnWidth / 2
        val bottomRectTop = mEndY + mSlideBtnMargin
        val bottomRectRight = x + mSlideBtnWidth / 2
        val bottomRectBottom = mEndY + mSlideBtnMargin + mSlideBtnWidth
        //矩形
        canvas?.drawRoundRect(bottomRectLeft, bottomRectTop, bottomRectRight, bottomRectBottom
            , mSlideBtnRadius, mSlideBtnRadius, mSlideBtnPaint)
        //三角箭头
        it.moveTo(x - mSlideBtnTriangleHeight, bottomRectTop)
        it.lineTo(x, bottomRectTop - mSlideBtnTriangleHeight)
        it.lineTo(x + mSlideBtnTriangleHeight, bottomRectTop)
        it.close()
        bottomSlideBtnPath.reset()
        bottomSlideBtnPath.addRect(bottomRectLeft
            , bottomRectTop - mSlideBtnTriangleHeight,
            bottomRectRight, bottomRectBottom, CW)
        canvas?.drawPath(it, mSlideBtnPaint)
      }
      //右侧滑动按钮
      rightTriangleSlidePath?.let {
        it.reset()
        val rightRectLeft = mEndX + mSlideBtnMargin
        val rightRectTop = y - mSlideBtnWidth / 2
        val rightRectRight = mEndX + mSlideBtnMargin + mSlideBtnWidth
        val rightRectBottom = y + mSlideBtnWidth / 2
        //矩形
        canvas?.drawRoundRect(rightRectLeft, rightRectTop, rightRectRight, rightRectBottom
            , mSlideBtnRadius, mSlideBtnRadius, mSlideBtnPaint)
        //三角箭头
        it.moveTo(rightRectLeft, y - mSlideBtnTriangleHeight)
        it.lineTo(rightRectLeft - mSlideBtnTriangleHeight, y)
        it.lineTo(rightRectLeft, y + mSlideBtnTriangleHeight)
        it.close()
        rightSlideBtnPath.reset()
        rightSlideBtnPath.addRect(rightRectLeft - mSlideBtnTriangleHeight
            , rightRectTop,
            rightRectRight, rightRectBottom, CW)
        canvas?.drawPath(it, mSlideBtnPaint)
      }

    }
  }

  /** 画选中点的删除按钮 */
  private fun drawDeleteBtn(canvas: Canvas?, curPoint: PointF?) {
    if (!showDeleteBtn) {
      return
    }
    if (mDeleteBitmap == null) {
      mDeleteBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.edit_icon_delete_nor)
    }
    if (mDeleteBitmap?.isRecycled == true) {
      mDeleteBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.edit_icon_delete_nor)
    }
    deleteBtnPath.reset()
    curPoint?.let {
      var curX = curPoint.x
      var curY = curPoint.y
      var left = 0f
      var top = 0f
      var right = 0f
      var bottom = 0f
      if (curX <= DeviceSizeUtil.dpToPixel(20f)) {
        curX = mStartX.toFloat() + mDeleteBtnWidth / 2
      }
      if (curX >= mEndX - DeviceSizeUtil.dpToPixel(20f)) {
        curX = mEndX.toFloat() - mDeleteBtnWidth / 2
      }
      if (curY <= mStartY + DeviceSizeUtil.dpToPixel(20f)) {
        top = curY + mDeleteBtnMargin
        bottom = curY + mDeleteBtnHeight + mDeleteBtnMargin
      } else {
        top = curY - mDeleteBtnHeight - mDeleteBtnMargin
        bottom = curY - mDeleteBtnMargin
      }
      left = curX - mDeleteBtnWidth / 2
      right = curX + mDeleteBtnWidth / 2
      deleteBtnPath.addRect(left, top, right, bottom, CW)
      canvas?.drawRoundRect(left, top, right, bottom
          , mDeleteBtnRadius, mDeleteBtnRadius, mDeleteBtnPaint)
      canvas?.drawBitmap(mDeleteBitmap!!, left + mDeleteIconMarginRight
          , top + mDeleteIconMarginTop, mDeleteBtnPaint)
    }
  }

  /** 画选中点的坐标位置 */
  private fun drawSelectPointText(canvas: Canvas?, curPoint: PointF?) {
    curPoint?.let {
      var x = xRange.position + xRange.timeLength * ((curPoint.x - mStartX) / (mWidth - 2 * mFixSize - mSlideBtnRangeSize))
      var y = yRange.position + yRange.timeLength * ((mEndY - curPoint.y) / (mHeight - 2 * mFixSize - mSlideBtnRangeSize))
      var text = x.toInt().toString() + "," + y.toInt()
      if (mConvertCallBack != null) {
        text = mConvertCallBack!!.onConvert(Point(x.toInt(), y.toInt())).toString()
      }
      val posx = mStartX + DeviceSizeUtil.dpToPixel(20f)
      val posy = mStartY + DeviceSizeUtil.dpToPixel(15f)
      canvas?.drawText(text, posx, posy, mTextPaint)
    }
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    val x = limitX(event!!.x)
    val y = limitY(event.y)
    if (event.pointerCount == 1 && mGestureDetector != null) {
      mGestureDetector?.onTouchEvent(event)
    }
    if (onGestureIntercept) {
      onGestureIntercept = false
      return true
    }
    if (curCurveIndex < 0 || curCurveIndex >= curveDatas.size) {
      return true
    }
    var curKnotsList = curveDatas.get(curCurveIndex).knotsList
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        mDownX = x
        mDownY = y
        if (!isTouchDeleteBtn(event.x, event.y)) {
          showDeleteBtn = false
          if (!deleteBtnPath.isEmpty) {
            deleteBtnPath.reset()
          }
        } else {
          isTouchDeleteBtn = true
          return true
        }
        if (isTouchBottomSlideBtn(event.x, event.y) && curKnotIdx != -1) {
          enableBottomSlideMoveEvent = true
          return true
        }
        if (isTouchRightSlideBtn(event.x, event.y) && curKnotIdx != -1) {
          enableRightSlideMoveEvent = true
          return true
        }
        if (isPointInCurSpPath(x, y)) {
          enableMoveEvent = true
          addNewKnot(x, y)
          refreshCurSelectKnotPath()
        } else {
          curKnotIdx = -1
          enableMoveEvent = false
        }
        invalidate()
      }
      MotionEvent.ACTION_MOVE -> {
        val minMove = sqrt((y - mDownY).toDouble().pow(2) + (x - mDownX).toDouble().pow(2))
        if (minMove <= mDefaultKnotRadius) {
          return true
        }
        if (isTouchDeleteBtn) {
          return true
        }

        val limitX = minKnotsSpacing
        val curPoint = curKnotsList.getOrNull(curKnotIdx)
        if (enableBottomSlideMoveEvent && curPoint != null) {
          if (curKnotIdx >= 1 && x <= curKnotsList[curKnotIdx - 1].x + limitX) {
            curPoint.x = curKnotsList[curKnotIdx - 1].x + limitX
          } else if (curKnotIdx < curKnotsList.size - 1 && x >= curKnotsList[curKnotIdx + 1].x - limitX) {
            curPoint.x = curKnotsList[curKnotIdx + 1].x - limitX
          } else {
            curPoint.x = x
          }
          mCallBack?.onUpdate(getFixArrayPoints(curKnotsList), curCurveIndex)
          invalidate()
          return true
        }
        if (enableRightSlideMoveEvent && curPoint != null) {
          curPoint.y = y
          mCallBack?.onUpdate(getFixArrayPoints(curKnotsList), curCurveIndex)
          invalidate()
          return true
        }
        if (enableMoveEvent && curPoint != null) {
          curPoint.y = y
          if (curKnotIdx >= 1 && x <= curKnotsList[curKnotIdx - 1].x + limitX) {
            curPoint.x = curKnotsList[curKnotIdx - 1].x + limitX
          } else if (curKnotIdx < curKnotsList.size - 1 && x >= curKnotsList[curKnotIdx + 1].x - limitX) {
            curPoint.x = curKnotsList[curKnotIdx + 1].x - limitX
          } else {
            curPoint.x = x
          }
          mCallBack?.onUpdate(getFixArrayPoints(curKnotsList), curCurveIndex)
          invalidate()
        }
      }
      MotionEvent.ACTION_UP -> {
        if (enableMoveEvent || enableBottomSlideMoveEvent || enableRightSlideMoveEvent) {
          mCallBack?.onUpdate(getFixArrayPoints(curKnotsList), curCurveIndex)
        }
        isTouchDeleteBtn = false
        enableMoveEvent = false
        enableBottomSlideMoveEvent = false
        enableRightSlideMoveEvent = false
      }
    }
    return true
  }

  private fun refreshCurSelectKnotPath() {
    curSelectKnotPath?.reset()
    if (curCurveIndex < 0 || curCurveIndex >= curveDatas.size) {
      return
    }
    var curKnotsList = curveDatas.get(curCurveIndex).knotsList
    val curPoint = curKnotsList.getOrNull(curKnotIdx)
    curPoint?.let {
      val fixSize = mDefaultKnotRadius * 2
      val rectangle =
        RectF(it.x - fixSize, it.y - fixSize, it.x + fixSize, it.y + fixSize)
      curSelectKnotPath?.addRect(rectangle, CW)
    }
  }

  /**
   * @param moveY y方向平移
   */
  private fun setSplinePath(path: Path?, points: LinkedList<PointF>, isSetSelect: Boolean) {
    path?.reset()
    val length: Int = points.size
    val arrayX = DoubleArray(points.size)
    val arrayY = DoubleArray(points.size)
    for (n in 0 until length) {
      arrayX[n] = points[n].x.toDouble()
      arrayY[n] = points[n].y.toDouble()
    }
    path?.moveTo(mStartX.toFloat(), arrayY[0].toFloat())
    path?.lineTo(arrayX[0].toFloat(), arrayY[0].toFloat())
    val fixSize = mDefaultKnotRadius
    if (length <= 1) {
      return
    }
    if (length > 2) {
      sip.interpolate(arrayX, arrayY)
      val totalPoints = 200
      val dx = ((arrayX[length - 1] - arrayX[0]) / totalPoints)
      var m = 0
      while (m < totalPoints) {
        val curX = arrayX[0] + m * dx
        var curY = sip.value(curX.toDouble()).toFloat()
        curY = limitY(curY)
        if (m % 3 == 0 && isSetSelect) {
          curRectPath.addRect(
              RectF(
                  curX.toFloat() - fixSize, (curY ?: 0f) - fixSize, curX.toFloat() + fixSize,
                  (curY ?: 0f) + fixSize
              ), CW
          )
        }
        path?.lineTo(curX.toFloat(), curY ?: 0f)
        m++
      }
    } else if (length == 2) {
      if (isSetSelect) {
        curRectPath.moveTo(points[0].x - fixSize, points[0].y - fixSize)
        curRectPath.lineTo(points[1].x - fixSize, points[1].y - fixSize)
        curRectPath.lineTo(points[1].x + fixSize, points[1].y + fixSize)
        curRectPath.lineTo(points[0].x + fixSize, points[0].y + fixSize)
        curRectPath.close()
        curRectPath.moveTo(points[0].x + fixSize, points[0].y - fixSize)
        curRectPath.lineTo(points[1].x + fixSize, points[1].y - fixSize)
        curRectPath.lineTo(points[1].x - fixSize, points[1].y + fixSize)
        curRectPath.lineTo(points[0].x - fixSize, points[0].y + fixSize)
        curRectPath.close()
      }
    }
    if (isSetSelect) {
      curRectPath.addRect(
          RectF(
              mStartX.toFloat(), points[0].y - fixSize, points[0].x, points[0].y + fixSize
          ), CW
      )
      curRectPath.addRect(
          RectF(
              points[length - 1].x, points[length - 1].y - fixSize, mEndX.toFloat(),
              points[length - 1].y + fixSize
          ), CW
      )
    }
    path?.lineTo(
        arrayX[points.size - 1].toFloat(), arrayY[points.size - 1].toFloat()
    )
    path?.lineTo(
        mEndX.toFloat(), arrayY[points.size - 1].toFloat()
    )
  }

  /**
   * @param moveY y方向平移
   */
  private fun setSinPath(path: Path?, points: LinkedList<PointF>, isSetSelect: Boolean) {
    path?.reset()
    val length: Int = points.size
    val arrayX = DoubleArray(points.size)
    val arrayY = DoubleArray(points.size)
    for (n in 0 until length) {
      arrayX[n] = points[n].x.toDouble()
      arrayY[n] = points[n].y.toDouble()
    }
    // 保证起头的点的线
    path?.moveTo(mStartX.toFloat(), arrayY[0].toFloat())
    path?.lineTo(arrayX[0].toFloat(), arrayY[0].toFloat())
    if (length <= 1) {
      return
    }
    var inited = false;
    var lastX: Double = 1.0
    var lastY: Double = 1.0
    val fixSize = mDefaultKnotRadius
    for (i in arrayX.indices) {
      if (inited) {
        if (arrayY[i] == lastY) {
          path?.lineTo(arrayX[i].toFloat(), arrayY[i].toFloat())
          if (isSetSelect) {
            curRectPath.moveTo(points[0].x - fixSize, points[0].y - fixSize)
            curRectPath.lineTo(points[1].x - fixSize, points[1].y - fixSize)
            curRectPath.lineTo(points[1].x + fixSize, points[1].y + fixSize)
            curRectPath.lineTo(points[0].x + fixSize, points[0].y + fixSize)
            curRectPath.close()
            curRectPath.moveTo(points[0].x + fixSize, points[0].y - fixSize)
            curRectPath.lineTo(points[1].x + fixSize, points[1].y - fixSize)
            curRectPath.lineTo(points[1].x - fixSize, points[1].y + fixSize)
            curRectPath.lineTo(points[0].x - fixSize, points[0].y + fixSize)
            curRectPath.close()
          }
        } else if (arrayY[i] < lastY) {
          var count = 200
          var waveHeight = lastY - arrayY[i]
          var xOffset = (arrayX[i] - lastX) / count
          for (pos in 0..count) {
            var y = (waveHeight / 2 + waveHeight / 2 * Math.sin(pos.toFloat() / count * Math.PI + Math.PI / 2))
            path?.lineTo((lastX + pos * xOffset).toFloat(), (y + arrayY[i]).toFloat())
            if (pos % 3 == 0 && isSetSelect) {
              curRectPath.addRect(RectF(
                  (lastX + pos * xOffset).toFloat() - fixSize,
                  (y + arrayY[i]).toFloat() - fixSize,
                  (lastX + pos * xOffset).toFloat() + fixSize,
                  (y + arrayY[i]).toFloat() + fixSize
              ), CW
              )
            }
          }
        } else if (arrayY[i] > lastY) {
          var count = 200
          var waveHeight = arrayY[i] - lastY
          var xOffset = (arrayX[i] - lastX) / count
          for (pos in 0..count) {
            var y = (waveHeight / 2 + waveHeight / 2 * Math.sin(pos.toFloat() / count * Math.PI - Math.PI / 2))
            path?.lineTo((lastX + pos * xOffset).toFloat(), (y + lastY).toFloat())
            if (pos % 3 == 0 && isSetSelect) {
              curRectPath.addRect(RectF(
                  (lastX + pos * xOffset).toFloat() - fixSize,
                  (y + lastY).toFloat() - fixSize,
                  (lastX + pos * xOffset).toFloat() + fixSize,
                  (y + lastY).toFloat() + fixSize
              ), CW
              )
            }
          }
        }
      }
      lastX = arrayX[i]
      lastY = arrayY[i]
      inited = true;
    }
    if (isSetSelect) {
      curRectPath.addRect(
          RectF(mStartX.toFloat(), points[0].y - fixSize, points[0].x, points[0].y + fixSize), CW
      )
      curRectPath.addRect(
          RectF(
              points[length - 1].x, points[length - 1].y - fixSize, mEndX.toFloat(),
              points[length - 1].y + fixSize
          ), CW
      )
    }
    path?.lineTo(
        arrayX[points.size - 1].toFloat(), arrayY[points.size - 1].toFloat()
    )
    // 保证结尾的点的线
    path?.lineTo(
        mEndX.toFloat(), arrayY[points.size - 1].toFloat()
    )
  }

  private fun limitX(pX: Float): Float {
    return when {
      pX > mEndX -> (mEndX).toFloat()
      pX < mStartX -> (mStartX).toFloat()
      else -> pX
    }
  }

  private fun limitY(pY: Float): Float {
    return when {
      pY > mEndY -> (mEndY).toFloat()
      pY < mStartY -> (mStartY).toFloat()
      else -> pY
    }
  }

  private fun isPointInCurSpPath(x: Float, y: Float): Boolean {
    if (curCurveIndex < 0 || curCurveIndex >= curveDatas.size) {
      return false
    }
    var curKnotsList = curveDatas.get(curCurveIndex).knotsList
    for (i in curKnotsList.indices) {
      val fixSize = mSelectedKnotRadius
      val knotPath = Path()
      knotPath.moveTo(curKnotsList[i].x, curKnotsList[i].y)
      val knotRectangle =
        RectF(
            curKnotsList[i].x - fixSize, curKnotsList[i].y - fixSize, curKnotsList[i].x + fixSize,
            curKnotsList[i].y + fixSize
        )
      knotPath.addRect(knotRectangle, CW)
      if (isPointAroundPath(x, y, knotPath)) {
        return true
      }
    }
    return isPointAroundPath(x, y, curRectPath)
  }

  private fun isTouchDeleteBtn(x: Float, y: Float): Boolean {
    return isPointAroundPath(x, y, deleteBtnPath)
  }

  private fun isTouchBottomSlideBtn(x: Float, y: Float): Boolean {
    return isPointAroundPath(x, y, bottomSlideBtnPath)
  }

  private fun isTouchRightSlideBtn(x: Float, y: Float): Boolean {
    return isPointAroundPath(x, y, rightSlideBtnPath)
  }

  fun isPointAroundPath(x: Float, y: Float, path: Path?): Boolean {
    val fixSize = DeviceSizeUtil.dpToPixel(9f)
    val tempPath = Path()
    tempPath.moveTo(x, y)
    val rectangle =
      RectF(x - fixSize, y - fixSize, x + fixSize, y + fixSize)
    tempPath.addRect(rectangle, CW)
    tempPath.op(path, INTERSECT)
    return !tempPath.isEmpty
  }

  private fun addNewKnot(x: Float, y: Float) {
    if (curCurveIndex < 0 || curCurveIndex >= curveDatas.size) {
      return
    }
    var curKnotsList = curveDatas.get(curCurveIndex).knotsList
    for (i in curKnotsList.indices) {
      val fixSize = mSelectedKnotRadius
      val knotPath = Path()
      val tempPath = Path()
      knotPath.moveTo(curKnotsList[i].x, curKnotsList[i].y)
      tempPath.moveTo(x, y)
      val rectangle =
        RectF(x - fixSize, y - fixSize, x + fixSize, y + fixSize)
      val knotRectangle =
        RectF(
            curKnotsList[i].x - fixSize, curKnotsList[i].y - fixSize, curKnotsList[i].x + fixSize,
            curKnotsList[i].y + fixSize
        )
      tempPath.addRect(rectangle, CW)
      knotPath.addRect(knotRectangle, CW)
      tempPath.op(knotPath, INTERSECT)
      if (!tempPath.isEmpty) { //判断触摸点是否在已添加控制点的附近
        if (curKnotIdx != i) {
          selectNewPoint = true
        }
        curKnotIdx = i
        return
      } else { //新增控制点
        val fixSize = minKnotsSpacing
        val maxX = curKnotsList.last
            .x
        val minX = curKnotsList.first
            .x
        if (x > maxX + fixSize) { //插入到队尾
          selectNewPoint = true
          curKnotsList.add(PointF(x, curKnotsList.last.y))
          curKnotIdx = curKnotsList.size - 1
          return
        }

        if (x < minX - fixSize) { //插入到队头
          selectNewPoint = true
          curKnotsList.addFirst(PointF(x, curKnotsList.first.y))
          curKnotIdx = 0
          return
        }

        val preX = curKnotsList[i]
            .x
        val nextPoint = curKnotsList.getOrNull(i + 1) ?: continue
        val nextX = nextPoint.x
        if (x in preX..nextX) {
          if (x > (preX + fixSize) && x < (nextX - fixSize)) { //插入队中间
            selectNewPoint = true
            if (curKnotsList.size == 2) {
              val k = (curKnotsList[1].y - curKnotsList[0].y) / (curKnotsList[1].x - curKnotsList[0].x)
              val a = curKnotsList[0].y - k * curKnotsList[0].x
              curKnotsList.add(i + 1, PointF(x, k * x + a))
            } else {
              var insertY = sip?.value(x.toDouble())?.toFloat() ?: x
              if (insertY <= mStartY) {
                insertY = mStartY.toFloat()
              }
              if (insertY >= mEndY) {
                insertY = mEndY.toFloat()
              }
              var insertX = x
              if (insertY <= mStartY) {
                insertY = mStartY.toFloat()
              }
              if (insertY >= mEndY) {
                insertY = mEndY.toFloat()
              }
              curKnotsList.add(i + 1, PointF(x, insertY))
            }
            curKnotIdx = i + 1
          }
        }
      }
    }
  }

  fun deleteCurKnot() {
    if (curCurveIndex < 0 || curCurveIndex >= curveDatas.size) {
      return
    }
    var curKnotsList = curveDatas.get(curCurveIndex).knotsList
    showDeleteBtn = false
    deleteBtnPath.reset()
    curKnotsList.removeAt(curKnotIdx)
    curKnotIdx = -1
  }

  fun setOnCtrPointsUpdateCallBack(callBack: OnCtrPointsUpdateCallBack) {
    this.mCallBack = callBack
  }

  fun setOnCtrPontTextConvertCallBack(convertCallBack: OnCtrPontTextConvertCallBack) {
    this.mConvertCallBack = convertCallBack
  }

  interface OnCtrPointsUpdateCallBack {
    fun onUpdate(points: ArrayList<QPoint>, selectIndex: Int)
  }

  interface OnCtrPontTextConvertCallBack {
    fun onConvert(curPoint: Point): String
  }

  inner class GestureListener : SimpleOnGestureListener() {

    override fun onSingleTapUp(e: MotionEvent): Boolean {
      if (curCurveIndex < 0 || curCurveIndex >= curveDatas.size) {
        return true
      }
      var curKnotsList = curveDatas.get(curCurveIndex).knotsList
      if (!showDeleteBtn && curKnotIdx != -1 && isPointAroundPath(e.x, e.y, curSelectKnotPath) && curKnotsList.size > 2) {
        if (selectNewPoint) {
          selectNewPoint = false
          return super.onSingleTapUp(e)
        }
        onGestureIntercept = true
        showDeleteBtn = true
        postInvalidateDelayed(200)
      } else if (showDeleteBtn && isPointAroundPath(e.x, e.y, deleteBtnPath) && curKnotIdx != -1 && curKnotsList.size > 2) {
        deleteCurKnot()
        onGestureIntercept = true
        mCallBack?.onUpdate(getFixArrayPoints(curKnotsList), curCurveIndex) //删除控制点需要更新引擎
        invalidate()
      } else if (showDeleteBtn && curKnotIdx != -1 && isPointAroundPath(e.x, e.y, curSelectKnotPath)) {
        showDeleteBtn = false
        invalidate()
      }
      if (isPointAroundPath(e.x, e.y, curSelectKnotPath)) {
        onGestureIntercept = true
      }
      return super.onSingleTapUp(e)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
      if (e != null) {
        if (curCurveIndex < 0 || curCurveIndex >= curveDatas.size) {
          return true
        }
        var curKnotsList = curveDatas.get(curCurveIndex).knotsList
        if (curKnotIdx != -1 && isPointAroundPath(e.x, e.y, curSelectKnotPath) && curKnotsList.size > 2) {
          deleteCurKnot()
          postDelayed({
            mCallBack?.onUpdate(getFixArrayPoints(curKnotsList), curCurveIndex) //删除控制点需要更新引擎
          }, 100)
          onGestureIntercept = true
          invalidate()
        }
      }
      return super.onDoubleTap(e)
    }
  }

  fun initRange(rangeX: VeRange, rangeY: VeRange) {
    xRange = rangeX
    yRange = rangeY
  }

  fun setCurveDate(selectIndex: Int, dateList: List<CurveData>?) {
    curveDatas.clear()
    if (dateList == null) {
      resetKnotsList()
      switchSpLineType(selectIndex, true)
      return
    }
    curveDatas.addAll(dateList)
    for (item in curveDatas) {
      if (item.knotsList == null || item.knotsList.size < 2) {
        if (item.curveType == 0) {
          item.knotsList.add(PointF(mStartX.toFloat(), mEndY.toFloat()))
          item.knotsList.add(PointF(mEndX.toFloat(), mStartY.toFloat()))
        } else if (item.curveType == 1) {
          item.knotsList.add(PointF(mStartX.toFloat(), mEndY.toFloat() / 2))
          item.knotsList.add(PointF(mEndX.toFloat(), mEndY.toFloat() / 2))
        }
      }
    }
    switchSpLineType(selectIndex, true)
  }

  public fun getFixArrayPoints(index: Int): ArrayList<QPoint> {
    val list = curveDatas.get(index).knotsList
    return getFixArrayPoints(list)
  }

  public fun getFixArrayPoints(list: LinkedList<PointF>): ArrayList<QPoint> {
    val qpoints = ArrayList<QPoint>()
    if (list.size > 0) {
      if (list.get(0).x.toInt() != mStartX) {
        list.add(0, PointF(mStartX.toFloat(), list.get(0).y))
      }
      if (list.get(list.size - 1).x.toInt() != mEndX) {
        list.add(PointF(mEndX.toFloat(), list.get(list.size - 1).y))
      }
    }
    for (pointf in list) {
      val x = xRange.position + xRange.timeLength * ((pointf.x - mStartX) / (mWidth - 2 * mFixSize - mSlideBtnRangeSize))
      val y = yRange.position + yRange.timeLength * ((mEndY - pointf.y) / (mHeight - 2 * mFixSize - mSlideBtnRangeSize))
      qpoints.add(QPoint(x.toInt(), y.toInt()))
    }

    return qpoints
  }

  public fun getRealPoints(points: List<QPoint>): LinkedList<PointF> {
    val pointFs = LinkedList<PointF>()
    for (point in points) {
      val x = mStartX + (mWidth - 2 * mFixSize - mSlideBtnRangeSize) * (point.x.toFloat() / xRange.timeLength)
      val y = mEndY - (mHeight - 2 * mFixSize - mSlideBtnRangeSize) * (point.y.toFloat() / yRange.timeLength)
      pointFs.add(PointF(x, y))
    }
    return pointFs
  }
}