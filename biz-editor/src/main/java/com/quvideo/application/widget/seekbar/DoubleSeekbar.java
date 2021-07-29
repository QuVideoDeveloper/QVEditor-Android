package com.quvideo.application.widget.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DeviceSizeUtil;

public class DoubleSeekbar extends View {

  private float dp1px = DeviceSizeUtil.dpToPixel(1f);
  private float dp4px = dp1px * 4;
  private float dp7px = dp1px * 7;
  private float dp14px = dp1px * 14;
  private float dp28px = dp1px * 28;

  private Paint backPaint;
  private Paint progressPaint;
  private Paint seekbarPaint;

  private int minProgress;
  private int maxProgress = 100;
  private int minRange = 0;
  private long count = maxProgress - minProgress;

  /** 第一格进度 */
  private int firstProgress;
  /** 第二格进度 */
  private int secondProgress;
  /** 是否双向模式 */
  private boolean isDoubleMode;
  /** seek监听 */
  private OnSeekbarListener mOnSeekbarListener;

  private float firstX = -1;
  private float secondX = -1;
  private float centerY = -1;

  /** 是否点击按下 */
  private boolean isActionDown = false;
  /** 是否还可以执行单指拖动 */
  private boolean isCanActionDrag = false;
  /** 是否拖拽第一个seekbar */
  private boolean isDragFirst = true;

  public DoubleSeekbar(Context context) {
    super(context);
    init();
  }

  public DoubleSeekbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public DoubleSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public DoubleSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    int backColor = ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_33ffffff);
    int whiteColor = ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.white);
    backPaint = new Paint();
    backPaint.setColor(backColor);
    backPaint.setAntiAlias(true);
    backPaint.setStyle(Paint.Style.FILL);
    backPaint.setStrokeWidth(dp4px);
    progressPaint = new Paint();
    progressPaint.setColor(whiteColor);
    progressPaint.setAntiAlias(true);
    progressPaint.setStyle(Paint.Style.FILL);
    progressPaint.setStrokeWidth(dp4px);
    seekbarPaint = new Paint();
    seekbarPaint.setColor(whiteColor);
    seekbarPaint.setAntiAlias(true);
    seekbarPaint.setStyle(Paint.Style.FILL);
    seekbarPaint.setStrokeWidth(dp1px * 18);
  }

  public void setProgressRange(int start, int end, int minRange) {
    if (start > end || ((long) end - start) < minRange) {
      return;
    }
    this.minProgress = start;
    this.maxProgress = end;
    this.minRange = minRange;
    firstProgress = Math.min(Math.max(firstProgress, minProgress), maxProgress);
    secondProgress = Math.min(Math.max(secondProgress, minProgress), maxProgress);
    count = maxProgress - minProgress;
    firstX = -1;
    secondX = -1;
    invalidate();
  }

  public void setFirstProgress(int progress) {
    if (firstProgress != progress) {
      firstProgress = Math.min(Math.max(minProgress, progress), maxProgress);
      if (isDoubleMode && firstProgress > secondProgress) {
        secondProgress = firstProgress;
      }
      firstX = -1;
      secondX = -1;
      invalidate();
    }
  }

  public int getFirstProgress() {
    return firstProgress;
  }

  public void setSecondProgress(int progress) {
    if (secondProgress != progress) {
      secondProgress = Math.min(Math.max(minProgress, progress), maxProgress);
      if (isDoubleMode && firstProgress > secondProgress) {
        firstProgress = secondProgress;
      }
      firstX = -1;
      secondX = -1;
      invalidate();
    }
  }

  public int getSecondProgress() {
    return secondProgress;
  }

  public void setDoubleMode(boolean doubleMode) {
    isDoubleMode = doubleMode;
    if (firstProgress > secondProgress) {
      secondProgress = firstProgress;
    }
    firstX = -1;
    secondX = -1;
    invalidate();
  }

  public void setOnSeekbarListener(OnSeekbarListener onSeekbarListener) {
    mOnSeekbarListener = onSeekbarListener;
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (centerY <= 0) {
      centerY = getMeasuredHeight() / 2f;
    }
    if (centerY <= 0) {
      return;
    }
    // start的圆
    canvas.drawCircle(dp14px, centerY, dp1px, backPaint);
    // end的圆
    canvas.drawCircle(getMeasuredWidth() - dp14px, centerY, dp1px, backPaint);
    // back的线
    canvas.drawLine(dp14px, centerY, getMeasuredWidth() - dp14px, centerY, backPaint);
    if (firstX < 0) {
      float length = getMeasuredWidth() - dp28px;
      firstX = length / count * (firstProgress - minProgress) + dp14px;
    }
    if (isDoubleMode) {
      if (secondX < 0) {
        float length = getMeasuredWidth() - dp28px;
        secondX = length / count * (secondProgress - minProgress) + dp14px;
      }
      // progress的线
      canvas.drawLine(firstX, centerY, secondX, centerY, progressPaint);
      // first的seekbar
      canvas.drawCircle(firstX, getMeasuredHeight() / 2f, dp14px, seekbarPaint);
      // second的seekbar
      canvas.drawCircle(secondX, getMeasuredHeight() / 2f, dp14px, seekbarPaint);
    } else {
      // start的进度圆
      canvas.drawCircle(dp14px, getMeasuredHeight() / 2f, dp1px, progressPaint);
      // progress的线
      canvas.drawLine(dp14px, centerY, firstX, centerY, progressPaint);
      // first的seekbar
      canvas.drawCircle(firstX, getMeasuredHeight() / 2f, dp14px, seekbarPaint);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      handleDown(event);
    } else if (event.getAction() == MotionEvent.ACTION_UP
        || event.getAction() == MotionEvent.ACTION_CANCEL) {
      // 松手点击
      handleUp();
    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
      // 移动
      if (event.getPointerCount() == 1) {
        // 单指拖拽
        handleDrag(event);
      } else if (event.getPointerCount() > 1) {
        isActionDown = false;
        isCanActionDrag = false;
      }
    }
    return true;
  }

  public float getSeekbarPos(boolean isDragFirst) {
    return isDragFirst ? firstX : secondX;
  }

  /**
   * 点击
   */
  private void handleDown(MotionEvent event) {
    // 点击下去
    if (event.getPointerCount() == 1 && !isActionDown) {
      isActionDown = true;
      isCanActionDrag = true;
      float touchX = event.getX(0);
      float length = getMeasuredWidth() - dp28px;
      if (!isDoubleMode) {
        isDragFirst = true;
        firstX = Math.min(Math.max(touchX, dp14px), length + dp14px);
        firstProgress = (int) ((firstX - dp14px) / (length / (count))) + minProgress;
      } else {
        if (Math.abs(touchX - firstX) <= Math.abs(touchX - secondX)) {
          if (firstX == secondX && firstX == dp14px) {
            // 两个按钮在最左边重叠
            isDragFirst = false;
            float mixSecond = length / count * (firstProgress - minProgress + minRange) + dp14px;
            secondX = Math.max(mixSecond, Math.max(firstX, Math.min(Math.max(touchX, dp14px), length + dp14px)));
            int progress = (int) ((secondX - dp14px) / (length / (count))) + minProgress;
            if (progress >= firstProgress) {
              secondProgress = progress;
            }
          } else {
            isDragFirst = true;
            float maxFirst = length / count * (secondProgress - minProgress - minRange) + dp14px;
            firstX = Math.min(maxFirst, Math.min(secondX, Math.min(Math.max(touchX, dp14px), length + dp14px)));
            int progress = (int) ((firstX - dp14px) / (length / (count))) + minProgress;
            if (progress <= secondProgress) {
              firstProgress = progress;
            }
          }
        } else {
          isDragFirst = false;
          float mixSecond = length / count * (firstProgress - minProgress + minRange) + dp14px;
          secondX = Math.max(mixSecond, Math.max(firstX, Math.min(Math.max(touchX, dp14px), length + dp14px)));
          int progress = (int) ((secondX - dp14px) / (length / (count))) + minProgress;
          if (progress >= firstProgress) {
            secondProgress = progress;
          }
        }
      }
      if (mOnSeekbarListener != null) {
        mOnSeekbarListener.onSeekStart(isDragFirst, isDragFirst ? firstProgress : secondProgress);
      }
    }
    invalidate();
  }

  /**
   * 点击结束
   */
  private void handleUp() {
    isCanActionDrag = false;
    isActionDown = false;
    if (mOnSeekbarListener != null) {
      mOnSeekbarListener.onSeekOver(isDragFirst, isDragFirst ? firstProgress : secondProgress);
    }
    invalidate();
  }

  /**
   * 处理单指移动
   */
  private void handleDrag(MotionEvent event) {
    if (!isCanActionDrag) {
      return;
    }
    // 单点处理
    float touchX = event.getX(0);
    if (isActionDown) {
      float length = getMeasuredWidth() - dp28px;
      if (isDragFirst) {
        if (isDoubleMode) {
          float maxFirst = length / count * (secondProgress - minProgress - minRange) + dp14px;
          firstX = Math.min(maxFirst, Math.min(secondX, Math.min(Math.max(touchX, dp14px), length + dp14px)));
        } else {
          firstX = Math.min(Math.max(touchX, dp14px), length + dp14px);
        }
        int progress = (int) ((firstX - dp14px) / (length / (count))) + minProgress;
        if (!isDoubleMode || progress <= secondProgress) {
          firstProgress = progress;
        }
      } else {
        float mixSecond = length / count * (firstProgress - minProgress + minRange) + dp14px;
        secondX = Math.max(mixSecond, Math.max(firstX, Math.min(Math.max(touchX, dp14px), length + dp14px)));
        int progress = (int) ((secondX - dp14px) / (length / (count))) + minProgress;
        if (progress >= firstProgress) {
          secondProgress = progress;
        }
      }
      if (mOnSeekbarListener != null) {
        mOnSeekbarListener.onSeekChange(isDragFirst, isDragFirst ? firstProgress : secondProgress);
      }
    }
    invalidate();
  }

  /**
   * 位置被修改修改回调
   */
  public interface OnSeekbarListener {

    /** 开始 */
    void onSeekStart(boolean isFirst, int progress);

    /** 结束 */
    void onSeekOver(boolean isFirst, int progress);

    /** 修改过程 */
    void onSeekChange(boolean isFirst, int progress);
  }
}
