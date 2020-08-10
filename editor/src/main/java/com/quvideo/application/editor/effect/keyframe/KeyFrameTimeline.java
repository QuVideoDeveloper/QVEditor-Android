package com.quvideo.application.editor.effect.keyframe;

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
import com.quvideo.mobile.engine.model.effect.keyframe.BaseKeyFrame;
import java.util.List;

public class KeyFrameTimeline extends View {

  private float dp1px = DeviceSizeUtil.dpToPixel(1f);
  private float dp8px = dp1px * 8;
  private float dp20px = dp1px * 20;

  private Paint progressPaint;
  private Paint keyFramePaint;
  private Paint keyFrameFocusPaint;

  private volatile int curOffsetTime = -1;

  private volatile int maxOffsetTime = 1;

  private List<? extends BaseKeyFrame> mKeyFrameList;

  private volatile int mFocusTs = -1;

  private OnKeyFrameListener mOnKeyFrameListener;

  public KeyFrameTimeline(Context context) {
    super(context);
    init();
  }

  public KeyFrameTimeline(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public KeyFrameTimeline(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public KeyFrameTimeline(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  public void setMaxOffsetTime(int maxOffsetTime, OnKeyFrameListener onKeyFrameListener) {
    if (maxOffsetTime > 0) {
      this.maxOffsetTime = maxOffsetTime;
    }
    mOnKeyFrameListener = onKeyFrameListener;
  }

  public int getFocusTs() {
    return mFocusTs;
  }

  public void setCurOffsetTime(int curOffsetTime) {
    this.curOffsetTime = curOffsetTime;
    updateFocusTs();
    invalidate();
  }

  public void setKeyFrameData(int keyFrameColor, List<? extends BaseKeyFrame> keyFrames) {
    keyFramePaint.setColor(keyFrameColor);
    this.mKeyFrameList = keyFrames;
    updateFocusTs();
    invalidate();
  }

  private void updateFocusTs() {
    int focus = -1;
    if (mKeyFrameList != null) {
      int curTimePos = -1;
      if (curOffsetTime >= 0 && curOffsetTime <= maxOffsetTime) {
        curTimePos = curOffsetTime * getMeasuredWidth() / maxOffsetTime;
        // 当前进度的线
        int offset = 0;
        int timeOffset = 0;
        for (BaseKeyFrame item : mKeyFrameList) {
          if (item.relativeTime == curOffsetTime) {
            // focus了
            focus = item.relativeTime;
            break;
          }
          timeOffset = item.relativeTime * getMeasuredWidth() / maxOffsetTime;
          if (curTimePos >= timeOffset - dp8px && curTimePos <= timeOffset + dp8px) {
            if (focus < 0 || Math.abs(curOffsetTime - item.relativeTime) < offset) {
              focus = item.relativeTime;
              offset = Math.abs(curOffsetTime - item.relativeTime);
            }
          }
        }
      }
    }
    mFocusTs = focus;
  }

  private void init() {
    int whiteColor = ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.white);
    progressPaint = new Paint();
    progressPaint.setColor(whiteColor);
    progressPaint.setAntiAlias(true);
    progressPaint.setStyle(Paint.Style.STROKE);
    progressPaint.setStrokeWidth(dp1px);
    keyFramePaint = new Paint();
    keyFramePaint.setColor(whiteColor);
    keyFramePaint.setAntiAlias(true);
    keyFramePaint.setStyle(Paint.Style.FILL);
    keyFramePaint.setStrokeWidth(dp8px);
    keyFrameFocusPaint = new Paint();
    keyFrameFocusPaint.setColor(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_fe3d42));
    keyFrameFocusPaint.setAntiAlias(true);
    keyFrameFocusPaint.setStyle(Paint.Style.FILL);
    keyFrameFocusPaint.setStrokeWidth(dp8px);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    // 框
    canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), progressPaint);
    if (curOffsetTime >= 0 && curOffsetTime <= maxOffsetTime) {
      int curTimePos = curOffsetTime * getMeasuredWidth() / maxOffsetTime;
      // 当前进度的线
      canvas.drawLine(curTimePos, 0, curTimePos, getMeasuredHeight(), progressPaint);
    }
    if (mKeyFrameList != null) {
      int timeOffset;
      for (BaseKeyFrame item : mKeyFrameList) {
        timeOffset = item.relativeTime * getMeasuredWidth() / maxOffsetTime;
        if (item.relativeTime == mFocusTs) {
          // focus了
          canvas.drawCircle(timeOffset, getMeasuredHeight() / 2f, dp8px, keyFrameFocusPaint);
        } else {
          canvas.drawCircle(timeOffset, getMeasuredHeight() / 2f, dp8px, keyFramePaint);
        }
      }
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_UP
        || event.getAction() == MotionEvent.ACTION_CANCEL) {
      // 松手点击
      handleUp(event);
    }
    return true;
  }

  /**
   * 点击结束
   */
  private void handleUp(MotionEvent event) {
    // 单点处理
    float touchX = event.getX(0);
    BaseKeyFrame baseKeyFrame = null;
    int focusTs = -1;
    int clickOffsetTime = 0;
    if (mKeyFrameList != null) {
      float length = getMeasuredWidth();
      int progress = (int) (touchX / (length / maxOffsetTime));
      int abs = 0;
      int offset = 0;
      for (BaseKeyFrame item : mKeyFrameList) {
        offset = Math.abs(progress - item.relativeTime);
        if (focusTs < 0 || offset < abs) {
          focusTs = item.relativeTime;
          abs = offset;
        }
      }
      if (focusTs >= 0) {
        int focusTimePos = focusTs * getMeasuredWidth() / maxOffsetTime;
        if (Math.abs(focusTimePos - touchX) > dp20px) {
          clickOffsetTime = progress;
          focusTs = -1;
        }
      } else {
        clickOffsetTime = progress;
      }
    }
    if (focusTs >= 0) {
      if (mOnKeyFrameListener != null) {
        mOnKeyFrameListener.onKeyFrameClick(focusTs);
      }
    } else {
      if (mOnKeyFrameListener != null) {
        mOnKeyFrameListener.onOtherClick(clickOffsetTime);
      }
    }
  }

  /**
   * 位置被修改修改回调
   */
  public interface OnKeyFrameListener {

    /** 点击了某个关键帧 */
    void onKeyFrameClick(int focusTs);

    /** 点击了非关键帧时间 */
    void onOtherClick(int offsetTime);
  }
}
