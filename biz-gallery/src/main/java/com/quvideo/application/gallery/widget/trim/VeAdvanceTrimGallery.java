/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.quvideo.application.gallery.widget.trim;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SpinnerAdapter;
import com.quvideo.application.gallery.R;
import com.quvideo.application.gallery.utils.GSizeUtil;

/**
 * 定长timeline
 * 通过dispatchDraw()方法进行持续绘制
 */
public class VeAdvanceTrimGallery extends VeGallery {

  //TrimBar的额外高度。0的话默认与timeline等高
  private int EXTRA_VER_PADDING = GSizeUtil.getFitPxFromDp(getContext(), 10f);

  private static final float EXPAND_DP = 15F;

  private Drawable mDrawableLeftTrimBar = null;
  private Drawable mDrawableRightTrimBar = null;
  private Drawable mDrawableLeftTrimBarPush = null;
  private Drawable mDrawableRightTrimBarPush = null;
  private Drawable mDrawableLeftTrimBarDis = null;
  private Drawable mDrawableRightTrimBarDis = null;
  private Drawable mDrawableTrimContent = null;
  private Drawable mDrawableTrimContentDis = null;
  private Drawable mDrawableCurTimeNeedle = null;

  /**
   * 时间气泡
   */
  private final Drawable mDrawableBubble =
      getContext().getResources().getDrawable(R.drawable.gallery_icon_timeline_bubble);

  private final int bubbleTextColor = R.color.color_333333;

  private final int BUBBLE_TEXT_HEIGHT_DP = 12;

  private Paint mTextPaint = new Paint();

  private boolean isCenterAlign = false;

  private int mbDragSatus = 0;
  private int mxEventDown = 0;
  private int mxOldEvent = 0;
  private int mOldposTrimBar = 0;
  private OnTrimGalleryListener mOnTrimGalleryListener = null;
  private boolean mIsShowTrimInfo = false;
  private float mTrimAlpha = 0.0f;
  private int mDoShowTrimAnimation = 0;
  private int mClipIndex = 0;
  private int mClipDuration = 0;
  private int mPerChildDuration = 0;
  private int mTrimLeftValue = 0;
  private int mTrimLeftPos = 0;
  private int mTrimRightValue = 0;
  private int mTrimRightPos = 0;
  private boolean isRangeAttainLimit = false;

  private boolean isSeeking = false;
  private int mCurPlayTime = -1;
  private boolean isPlaying = false;
  private volatile boolean isLeftDraging = true;

  private boolean mEmptyAreaPressed = false;
  private int mMinTrimTouchWidth = 120;
  private int mParentViewOffset = 0;
  private int mMaskLayerColorNormal = 0xFF000000;
  /**
   * 边界以外的不透明度
   */
  private int mMaskAlpha = 0xCC;
  private AutoScrollRunnable mAutoScrollRunnable = new AutoScrollRunnable();
  private int mParentDownTouchPosition = -1;
  public static int MIN_TRIM_INTERVAL = 500;

  /**
   * 滚动模式下左右bar边界
   */
  public int minLeftPos;
  public int maxRightPos;
  /**
   * 左侧TrimBar的LeftMargin
   */
  private int mTrimLeftBarLeftMargin = 0;
  /**
   * 右侧TrimBar的LeftMargin
   */
  private int mTrimRightBarLeftMargin = 0;
  private int mMinBarDistance = 0;
  private Paint paint = new Paint();

  /**
   * 当前TrimBar左侧节点的时间，举例：0:00.0
   */
  private String mLeftMessage = null;
  /**
   * 当前TrimBar右侧节点的时间，举例：1:02.0
   */
  private String mRightMessage = null;

  private boolean mTouchScreen = false;

  /**
   * 左右value是否被修正过  用于记录修正前左右value差值是否小于500（提示不能修剪更小了）
   */
  private boolean trimValueModified = false;

  /**
   * 反向trim模式 seek时，游标从左边bar过来和从右边Bar过去，在左右bar中间时游标停顿的位置要区分
   */
  private boolean trimPlayNeedleInLeft = true;

  public VeAdvanceTrimGallery(Context context) {
    super(context);
    mEnableMoutichTouchEvent = true;
  }

  public VeAdvanceTrimGallery(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VeTrimGallery);
    mDrawableLeftTrimBar = array.getDrawable(R.styleable.VeTrimGallery_LeftTrimBar);
    mDrawableRightTrimBar = array.getDrawable(R.styleable.VeTrimGallery_RightTrimBar);
    array.recycle();
    mEnableMoutichTouchEvent = true;
  }

  public VeAdvanceTrimGallery(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mEnableMoutichTouchEvent = true;
  }

  public interface OnTrimGalleryListener {
    void onTrimStart(int clipIndex, boolean isLeftTrim, int trimPosition);

    void onTrimPosChanged(int clipIndex, boolean isLeftTrim, int trimPosition);

    void onTrimEnd(int clipIndex, boolean isLeftTrim, int trimPosition);

    /**
     * <p>
     * seekbar operations .
     * </p>
     */
    void onSeekStart(int seekTime);

    void onSeekPosChange(int trimPosition);

    void onSeekEnd(int trimPosition);

    void onTrimAnimationEnd(boolean isShowAnimation);

    boolean onDispatchKeyDown(int keyCode, KeyEvent event);

    boolean onDispatchKeyUp(int keyCode, KeyEvent event);

    boolean onAttainLimit();
  }

  public void setOnTrimGalleryListener(OnTrimGalleryListener listener) {
    mOnTrimGalleryListener = listener;
  }

  public void setLeftTrimBarDrawable(Drawable trimBar, Drawable trimBarPush) {
    mDrawableLeftTrimBar = trimBar;
    mDrawableLeftTrimBarPush = trimBarPush;
  }

  public void setRightTrimBarDrawable(Drawable trimBar, Drawable trimBarPush) {
    mDrawableRightTrimBar = trimBar;
    mDrawableRightTrimBarPush = trimBarPush;
  }

  public void setClipIndex(int index) {
    mClipIndex = index;
  }

  public int getClipIndex() {
    return mClipIndex;
  }

  public void setClipDuration(int duration) {
    mClipDuration = duration;
  }

  public void setTrimLeftValue(int val) {
    mTrimLeftValue = val;
    mTrimLeftPos = getTrimPositionByValue(val, getCount(), mPerChildDuration);
    limitDetect();
    invalidate();
  }

  /**
   * 设置mTrimLeftValue
   * 不做500ms检测
   * 原因：正反向trim切换的时候 set左或者右value会弹提示，实际上不需要弹
   */
  public void setTrimLeftValueWithoutLimitDetect(int val) {
    mTrimLeftValue = val;
    mTrimLeftPos = getTrimPositionByValue(val, getCount(), mPerChildDuration);
    invalidate();
  }

  public int getTrimLeftValue() {
    return mTrimLeftValue;
  }

  public int getmTrimLeftPos() {
    return mTrimLeftPos;
  }

  public int getmTrimRightPos() {
    return mTrimRightPos;
  }

  public void setTrimRightValue(int val) {
    mTrimRightValue = val;
    mTrimRightPos = getTrimPositionByValue(val, getCount(), mPerChildDuration);
    //兼容长视频左右bar都滑到左边时，右边bar不能再往后滑到的情况
    //由于mTrimLeftPos和mTrimRightPos都是0，导致isTouchInTrimBar函数里disLenth为0，总是focus在了左边，这里把右边bar位置兼容成1
    if (mTrimRightPos == 0) {
      mTrimRightPos = 1;
    }
    limitDetect();
    invalidate();
  }

  public int getTrimValueAfterMoving(int moveDistance) {
    int curPos;
    if (isLeftChoosen()) {
      curPos = mTrimLeftPos;
    } else {
      curPos = mTrimRightPos;
    }
    return getTrimValueByPosition(curPos + moveDistance, getCount());
  }

  /**
   * 设置mTrimRightValue
   * 不做500ms检测
   * 原因：正反向trim切换的时候 set左或者右value会弹提示，实际上不需要弹
   */
  public void setTrimRightValueWithoutLimitDetect(int val) {
    mTrimRightValue = val;
    mTrimRightPos = getTrimPositionByValue(val, getCount(), mPerChildDuration);
    //兼容长视频左右bar都滑到左边时，右边bar不能再往后滑到的情况
    //由于mTrimLeftPos和mTrimRightPos都是0，导致isTouchInTrimBar函数里disLenth为0，总是focus在了左边，这里把右边bar位置兼容成1
    if (mTrimRightPos == 0) {
      mTrimRightPos = 1;
    }
    invalidate();
  }

  /**
   * 500ms的限制
   * 区分正向和反向trim
   * 正向是两个bar中间
   * 反向是两个bar分别与对应两端之和
   */
  private void limitDetect() {
    if (mTrimRightValue > 0 && mTrimLeftValue >= 0) {
      int length = mTrimRightValue - mTrimLeftValue;
      if (((length - MIN_TRIM_INTERVAL < 10) || trimValueModified)
          && mClipDuration > MIN_TRIM_INTERVAL) {
        if (!isRangeAttainLimit) {
          isRangeAttainLimit = true;
          if (mOnTrimGalleryListener != null) {
            mOnTrimGalleryListener.onAttainLimit();
          }
        }
      } else {
        isRangeAttainLimit = false;
      }
    }
  }

  public int getTrimRightValue() {
    return mTrimRightValue;
  }

  public void setPerChildDuration(int val) {
    mPerChildDuration = val;
  }

  public int getMaxTrimRange() {
    return mChildWidth * getCount();
  }

  public int getMaxTrimRange(int count) {
    return mChildWidth * count;
  }

  public int getTrimPositionByValue(int trimValue, int count, int perChildDuration) {
    if (perChildDuration == 0) {
      return 0;
    }
    if (perChildDuration < 0) {
      perChildDuration = mPerChildDuration;
    }
    int iResiduePos = trimValue % perChildDuration;
    int iResidueDuration = mClipDuration - 1 - (count - 1) * perChildDuration;
    int iRelativepos;
    int trimPos;

    if (trimValue > mClipDuration - iResidueDuration) {
      iResiduePos = trimValue - (count - 1) * perChildDuration;
    }

    int childCount = trimValue / perChildDuration;

    if (childCount > count - 1) {
      childCount = count - 1;
    }

    if (childCount < count - 1) {
      iRelativepos = mChildWidth * iResiduePos / perChildDuration;
    } else {
      if (iResidueDuration == 0) {
        iRelativepos = mChildWidth * iResiduePos / perChildDuration;
      } else {
        iRelativepos = mChildWidth * iResiduePos / iResidueDuration;
      }
    }

    trimPos = (childCount - getFirstVisiblePosition()) * mChildWidth + iRelativepos;
    if (isSupportScroll()) {
      //若设置了偏移，则通过pos算value时，需要加上偏移的那一段
      trimPos += Math.abs(getLeftLimitMoveOffset());
    }
    if (trimPos < 0) {
      trimPos = 0;
    }

    if (trimPos > getMaxTrimRange(count)) {
      trimPos = getMaxTrimRange(count);
    }
    return trimPos;
  }

  public int getTrimValueByPosition(int trimPos, int count) {
    if(0 == mChildWidth){
      return mClipDuration - 1;
    }
    if (isSupportScroll()) {
      //若设置了偏移，则通过pos算value时，需要减掉偏移的那一段
      trimPos -= Math.abs(getLeftLimitMoveOffset());
    }
    int iResidueDuration = mClipDuration - (count - 1) * mPerChildDuration;
    int trimValue;
    int childCount = trimPos / mChildWidth;
    int childOffset = trimPos % mChildWidth;

    if (isSupportScroll()) {
      int firstPos = getFirstVisiblePosition();
      if (firstPos > 0) {
        childCount += firstPos;
      }
    }
    trimValue = childCount * mPerChildDuration;

    if (childCount < count - 1) {
      trimValue = trimValue + childOffset * mPerChildDuration / mChildWidth;
    } else {
      trimValue = trimValue + childOffset * iResidueDuration / mChildWidth;
    }
    if (trimValue >= mClipDuration) {
      trimValue = mClipDuration - 1;
    }

    if (trimPos == getMaxTrimRange()) {
      trimValue = mClipDuration - 1;
    }

    return trimValue;
  }

  public void checkTrimPos(boolean bIsleft) {
    if (mTrimRightValue - mTrimLeftValue >= MIN_TRIM_INTERVAL || mPerChildDuration <= 0) {
      return;
    }
    trimValueModified = true;
    int minIntervalCellCount = 0;
    if (MIN_TRIM_INTERVAL < mClipDuration) {
      minIntervalCellCount = MIN_TRIM_INTERVAL / mPerChildDuration;
    }
    int minIntervalCellOffset =
        (mChildWidth * (MIN_TRIM_INTERVAL % mPerChildDuration)) / mPerChildDuration
            + minIntervalCellCount * mChildWidth;
    int count = getCount();
    if (minIntervalCellOffset == 0) {
      minIntervalCellOffset = 1;
    }

    if (bIsleft) {
      int newLeftPos = mTrimRightPos - minIntervalCellOffset;
      int newLeftValue = getTrimValueByPosition(newLeftPos, count);
      int interval = mTrimRightValue - newLeftValue;

      while (interval < MIN_TRIM_INTERVAL) {
        newLeftPos--;
        if (newLeftPos < 0) {
          break;
        }
        newLeftValue = getTrimValueByPosition(newLeftPos, count);
        interval = mTrimRightValue - newLeftValue;
        if (interval >= MIN_TRIM_INTERVAL) {
          break;
        }
      }

      mTrimLeftPos = newLeftPos;
      mTrimLeftValue = getTrimValueByPosition(mTrimLeftPos, count);
    } else {
      int newRightPos = mTrimLeftPos + minIntervalCellOffset;
      int newRightValue = getTrimValueByPosition(newRightPos, count);
      int interval = newRightValue - mTrimLeftValue;

      while (interval < MIN_TRIM_INTERVAL) {
        newRightPos++;
        if (newRightPos >= getMaxTrimRange()) {
          break;
        }
        newRightValue = getTrimValueByPosition(newRightPos, count);
        interval = newRightValue - mTrimLeftValue;
        if (interval >= MIN_TRIM_INTERVAL) {
          break;
        }
      }

      mTrimRightPos = newRightPos;
      mTrimRightValue = getTrimValueByPosition(mTrimRightPos, count);
    }
  }

  private boolean isTouchInTrimBar(MotionEvent ev) {
    int x = (int) ev.getX();
    int y = (int) ev.getY();

    int leftBoundTrimPos = getLeftBoundTrimPos();
    int leftTrimPos = mTrimLeftPos - leftBoundTrimPos;
    int rightTrimPos = mTrimRightPos - leftBoundTrimPos;
    int disLenth = Math.abs(x - leftTrimPos) - Math.abs(x - rightTrimPos);

    if (disLenth > 0) {
      if (isInTouchArea(x, y, rightTrimPos, mDrawableRightTrimBar)) {
        mbDragSatus = 2;
        isLeftDraging = false;
        return true;
      }
    } else {
      if (isInTouchArea(x, y, leftTrimPos, mDrawableLeftTrimBar)) {
        mbDragSatus = 1;
        isLeftDraging = true;
        return true;
      }
    }

    mbDragSatus = 0;
    return false;
  }

  private boolean isInTouchArea(int x, int y, int trimPos, Drawable sourceDrawable) {
    boolean result = false;
    if (sourceDrawable != null) {
      int expandY = 20;
      int margin = (getHeight()
          - getPaddingTop()
          - getPaddingBottom()
          - sourceDrawable.getIntrinsicHeight()) - 2;

      int expandSize = 0;
      int intrinsicWidth = mDrawableLeftTrimBar.getIntrinsicWidth();
      if (mMinTrimTouchWidth - intrinsicWidth > 0) {
        expandSize = (mMinTrimTouchWidth - intrinsicWidth) / 2;
      }

      Rect rt =
          new Rect(trimPos - intrinsicWidth / 2 - expandSize, getPaddingTop() - expandY - margin,
              trimPos + intrinsicWidth / 2 + expandSize,
              getPaddingTop() + mDrawableLeftTrimBar.getIntrinsicHeight() + expandY + margin);
      result = rt.contains(x, y);
    }
    return result;
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    if (!mEnableTouchEvent) {
      return true;
    }
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        if (isTouchInTrimBar(ev)) {
          mTouchScreen = true;
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        mTouchScreen = false;
        break;
      default:
        break;
    }

    if (!isPlaying()) {
      boolean result = trimToucnProcess(ev);
      if (result) {
        return true;
      }
    } else {
      if (seekEventProcess(ev)) {
        return true;
      }
    }

    return super.dispatchTouchEvent(ev);
  }

  private boolean seekEventProcess(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      int x = (int) ev.getX();
      mOldposTrimBar = x + getLeftBoundTrimPos();
      boolean betweenBar =
          mOldposTrimBar >= mTrimLeftPos - GSizeUtil.getFitPxFromDp(getContext(), EXPAND_DP)
              && mOldposTrimBar <= mTrimRightPos + GSizeUtil.getFitPxFromDp(getContext(),
              EXPAND_DP);
      if (betweenBar) {
        mParentDownTouchPosition = -1;
        mxEventDown = x;
        mxOldEvent = x;
        isSeeking = true;
        int time = getTrimValueByPosition(mOldposTrimBar, getCount());
        mCurPlayTime = time;
        invalidate();
        if (mOnTrimGalleryListener != null) {
          mOnTrimGalleryListener.onSeekStart(time);
        }
        return true;
      }
      return false;
    } else {
      if (isSeeking) {
        int curPlayPos = (int) (ev.getX() + getLeftBoundTrimPos());
        int curTime = getTrimValueByPosition(curPlayPos, getCount());
        //TrimReverseOnSplitMode不需要有Bar的限制
        if (curTime < mTrimLeftValue) {
          curTime = mTrimLeftValue;
        }

        if (curTime > mTrimRightValue) {
          curTime = mTrimRightValue;
        }

        mCurPlayTime = curTime;
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
          if (mOnTrimGalleryListener != null) {
            mOnTrimGalleryListener.onSeekPosChange(curTime);
          }
          invalidate();
          return true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP
            || ev.getAction() == MotionEvent.ACTION_CANCEL) {
          if (mOnTrimGalleryListener != null) {
            mOnTrimGalleryListener.onSeekEnd(curTime);
          }
          isSeeking = false;
          invalidate();
          return true;
        }
      }
      return false;
    }
  }

  public boolean trimToucnProcess(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      int x = (int) ev.getX();
      mParentDownTouchPosition = -1;
      mxEventDown = x;
      mxOldEvent = x;
      if (isTouchInTrimBar(ev)) {
        if (mbDragSatus == 1) {
          mOldposTrimBar = mTrimLeftPos;
        } else {
          mOldposTrimBar = mTrimRightPos;
        }
        invalidate();
        if (mOnGalleryOperationListener != null) {
          mOnGalleryOperationListener.onDown();
        }
        if (mOnTrimGalleryListener != null) {
          mOnTrimGalleryListener.onTrimStart(mClipIndex, mbDragSatus == 1,
              (mbDragSatus == 1) ? mTrimLeftValue : mTrimRightValue);
        }
        return true;
      }
    } else {
      if (mbDragSatus > 0) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
          int interval = (int) (ev.getX() - mxEventDown);
          int leftBoundTrimPos = getLeftBoundTrimPos();
          int left = getPaddingLeft();
          int right = getWidth() - getPaddingRight();
          int curX = (int) ev.getX();
          int count = getCount();

          if (mbDragSatus == 1) {
            mTrimLeftPos = mOldposTrimBar + interval;
            if (mTrimRightPos - mTrimLeftPos < mMinBarDistance) {
              mTrimLeftPos = mTrimRightPos - mMinBarDistance;
            }

            if (mTrimLeftPos < 0) {
              mTrimLeftPos = 0;
            } else if (mTrimLeftPos > mTrimRightPos - 1) {
              mTrimLeftPos = mTrimRightPos - 1;
            }

            mTrimLeftValue = getTrimValueByPosition(mTrimLeftPos, count);
            if (mTrimRightValue - mTrimLeftValue < MIN_TRIM_INTERVAL) {
              mAutoScrollRunnable.stop();
              checkTrimPos(true);
            } else if (isSupportScroll() && mTrimLeftPos < minLeftPos) {
              //touch超过minLeft，只更新value 不处理事件
              mTrimLeftPos = minLeftPos;
              mTrimLeftValue = getTrimValueByPosition(mTrimLeftPos, getCount());
            } else if (isSupportScroll() && mTrimRightPos > maxRightPos) {
              //touch超过maxRight 只更新value 不处理事件
              mTrimRightPos = maxRightPos;
              mTrimRightValue = getTrimValueByPosition(mTrimRightPos, getCount());
            } else {
              trimValueModified = false;
              int leftTrimPosR = mTrimLeftPos - leftBoundTrimPos;
              int leftTrimPosL = leftTrimPosR;

              if (mDrawableRightTrimBar != null) {
                int intrinsicWidth = mDrawableLeftTrimBar.getIntrinsicWidth();
                if (isCenterAlign()) {
                  intrinsicWidth = intrinsicWidth / 2;
                }
                leftTrimPosL -= intrinsicWidth;
              }

              if (leftTrimPosR >= right) {
                if (!mAutoScrollRunnable.isStarted()) {
                  if (curX > mxOldEvent) {
                    mAutoScrollRunnable.start(true);
                  }
                }
              } else if (leftTrimPosL <= left) {
                if (!mAutoScrollRunnable.isStarted()) {
                  if (curX < mxOldEvent) {
                    mAutoScrollRunnable.start(false);
                  }
                }
              } else {
                if (mAutoScrollRunnable.isStarted()) {
                  mAutoScrollRunnable.stop();
                }
              }
            }
          } else if (mbDragSatus == 2) {
            int rightpos = getMaxTrimRange();

            mTrimRightPos = mOldposTrimBar + interval;
            if (mTrimRightPos - mTrimLeftPos < mMinBarDistance) {
              mTrimRightPos = mTrimLeftPos + mMinBarDistance;
            }

            if (mTrimRightPos > rightpos) {
              mTrimRightPos = rightpos;
            } else if (mTrimRightPos < mTrimLeftPos + 1) {
              mTrimRightPos = mTrimLeftPos + 1;
            }
            mTrimRightValue = getTrimValueByPosition(mTrimRightPos, count);

            if (mTrimRightValue - mTrimLeftValue < MIN_TRIM_INTERVAL) {
              mAutoScrollRunnable.stop();
              checkTrimPos(false);
            } else if (isSupportScroll() && mTrimLeftPos < minLeftPos) {
              mTrimLeftPos = minLeftPos;
              mTrimLeftValue = getTrimValueByPosition(mTrimLeftPos, getCount());
            } else if (isSupportScroll() && mTrimRightPos > maxRightPos) {
              mTrimRightPos = maxRightPos;
              mTrimRightValue = getTrimValueByPosition(mTrimRightPos, getCount());
            } else {
              trimValueModified = false;
              int rightTrimPosL = mTrimRightPos - leftBoundTrimPos;
              int rightTrimPosR = rightTrimPosL;

              if (mDrawableRightTrimBar != null) {
                int intrinsicWidth = mDrawableRightTrimBar.getIntrinsicWidth();
                if (isCenterAlign()) {
                  intrinsicWidth = intrinsicWidth / 2;
                }
                rightTrimPosR += intrinsicWidth;
              }

              if (rightTrimPosR >= right) {
                if (!mAutoScrollRunnable.isStarted()) {
                  if (curX > mxOldEvent) {
                    mAutoScrollRunnable.start(true);
                  }
                }
              } else if (rightTrimPosL <= left) {
                if (!mAutoScrollRunnable.isStarted()) {
                  if (curX < mxOldEvent) {
                    mAutoScrollRunnable.start(false);
                  }
                }
              } else {
                if (mAutoScrollRunnable.isStarted()) {
                  mAutoScrollRunnable.stop();
                }
              }
            }
          }
          if (mOnTrimGalleryListener != null) {
            mOnTrimGalleryListener.onTrimPosChanged(mClipIndex, mbDragSatus == 1,
                (mbDragSatus == 1) ? mTrimLeftValue : mTrimRightValue);
          }
          limitDetect();
          mxOldEvent = curX;
          invalidate();
          return true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP
            || ev.getAction() == MotionEvent.ACTION_CANCEL) {
          if (mbDragSatus > 0) {
            mAutoScrollRunnable.stop();
            if (mOnTrimGalleryListener != null) {
              mOnTrimGalleryListener.onTrimEnd(mClipIndex, mbDragSatus == 1,
                  (mbDragSatus == 1) ? mTrimLeftValue : mTrimRightValue);
            }
            if (mOnGalleryOperationListener != null) {
              mOnGalleryOperationListener.onUp();
            }
            mbDragSatus = 0;
            invalidate();
            return true;
          }
        }
      }
    }
    return false;
  }

  public void isShowTrimInfo(boolean isShow, boolean showAnimation) {
    mIsShowTrimInfo = isShow;

    if (showAnimation) {
      mTrimAlpha = 0.0f;
      mDoShowTrimAnimation = 1;
    } else {
      mTrimAlpha = 1.0f;
      mDoShowTrimAnimation = -1;
    }

    invalidate();
  }

  public int getLeftBoundTrimPos() {
    if (isSupportScroll()) {
      //可拖动模式下 leftBounds包含在了trimPos里面
      return 0;
    }
    int leftBoundTrimPos;
    int firstIndex = getFirstVisiblePosition();

    View child0 = getChildAt(0);

    leftBoundTrimPos = firstIndex * mChildWidth;
    if (child0 != null) {
      leftBoundTrimPos -= (child0.getLeft());
    }

    return leftBoundTrimPos;
  }

  public int getRightBoundTrimPos(int leftBoundTrimPos) {
    int rightBoundTrimPos;

    if (leftBoundTrimPos < 0) {
      int firstIndex = getFirstVisiblePosition();

      View child0 = getChildAt(0);

      leftBoundTrimPos = firstIndex * mChildWidth;
      if (child0 != null) {
        leftBoundTrimPos -= (child0.getLeft());
      }
    }

    rightBoundTrimPos = leftBoundTrimPos + getWidth();
    return rightBoundTrimPos;
  }

  @Override protected void dispatchDraw(Canvas canvas) {
    int count = getCount();
    if (count == 0) {
      return;
    }
    super.dispatchDraw(canvas);

    boolean bDrawhandle = mClipDuration > MIN_TRIM_INTERVAL;
    if (mIsShowTrimInfo) {
      int left = getPaddingLeft();
      int alpha = 255;
      boolean bNofify = false;
      int leftBoundTrimPos = getLeftBoundTrimPos();
      int rightBoundTrimPos = getRightBoundTrimPos(leftBoundTrimPos);

      if (mDoShowTrimAnimation != 0) {
        if (mDoShowTrimAnimation > 0) {
          mTrimAlpha += 0.1f;
          if (mTrimAlpha >= 1.f) {
            mTrimAlpha = 1.0f;
            mDoShowTrimAnimation = 0;
            bNofify = true;
          }
        } else {
          mTrimAlpha -= 0.1f;
          if (mTrimAlpha <= 0.f) {
            mTrimAlpha = 0.0f;
            mDoShowTrimAnimation = 0;
            bNofify = true;
            mIsShowTrimInfo = false;
          }
        }

        alpha = (int) (alpha * mTrimAlpha);
        if (!bNofify) {
          invalidate();
        }
      }

      if (!isPlaying()) {
        drawTrimContentMask(canvas, !bDrawhandle);
      }
      drawLeftBar(canvas, bDrawhandle, left, alpha, leftBoundTrimPos, paint);
      drawRightBar(canvas, bDrawhandle, left, alpha, leftBoundTrimPos, rightBoundTrimPos, paint);

      if (isPlaying()) {
        drawPlayerNeedle(canvas, count, bDrawhandle, alpha, leftBoundTrimPos);
      } else {
        drawBoundLine(canvas, leftBoundTrimPos, paint);
      }

      if (bNofify && mOnTrimGalleryListener != null) {
        mOnTrimGalleryListener.onTrimAnimationEnd(mTrimAlpha >= 1.0f);
      }
    }
  }

  private final int LINE_HEIGHT = GSizeUtil.getFitPxFromDp(getContext(), 2);
  private final int EXTEND_WIDTH = GSizeUtil.getFitPxFromDp(getContext(), 2);

  private void drawBoundLine(Canvas canvas, int leftBoundTrimPos, Paint paint) {
    int left = mTrimLeftPos - leftBoundTrimPos - EXTEND_WIDTH;
    int right = mTrimRightPos - leftBoundTrimPos + EXTEND_WIDTH;

    int topMargin =
        (getHeight() - getChildWidth()) / 2 - LINE_HEIGHT + GSizeUtil.getFitPxFromDp(getContext(),
            0.3f);
    Rect topLineRect = new Rect(left, topMargin, right, topMargin + LINE_HEIGHT);
    paint.setColor(Color.WHITE);
    canvas.save();
    canvas.drawRect(topLineRect, paint);
    canvas.restore();

    topMargin =
        (getHeight() - getChildWidth()) / 2 + getChildWidth() - GSizeUtil.getFitPxFromDp(
            getContext(), 0.3f);
    Rect bottomLineRect = new Rect(left, topMargin, right, topMargin + LINE_HEIGHT);
    paint.setColor(Color.WHITE);
    canvas.save();
    canvas.drawRect(bottomLineRect, paint);
    canvas.restore();
  }

  /**
   * 绘制播放器进度的游标
   */
  public void drawPlayerNeedle(Canvas canvas, int count, boolean bDrawhandle, int alpha,
      int leftBoundTrimPos) {

    Drawable drawableNeedle = mDrawableCurTimeNeedle;
    if (drawableNeedle != null && bDrawhandle) {
      //游标的宽度
      int needleWidth = drawableNeedle.getIntrinsicWidth();
      int trimPositionByValue = getTrimPositionByValue(mCurPlayTime, count, mPerChildDuration);
      //TrimReverseOnSplitMode时不需要限制trimBar
      if (trimPositionByValue < mTrimLeftPos) {
        trimPositionByValue = mTrimLeftPos;
      }
      int leftTrimPos = trimPositionByValue - leftBoundTrimPos;
      mTrimLeftBarLeftMargin = leftTrimPos - needleWidth / 2;
      int needleHeight = drawableNeedle.getIntrinsicHeight();
      int leftTopMarg = (getHeight() - needleHeight) / 2;
      canvas.save();
      canvas.translate(mTrimLeftBarLeftMargin, leftTopMarg);

      drawableNeedle.setBounds(0, 0, needleWidth, needleHeight);
      drawableNeedle.setAlpha(alpha);
      drawableNeedle.draw(canvas);
      canvas.restore();
      //如果按下状态才绘制。
      Drawable bubbleDrawable;
      if (mTouchScreen) {
        bubbleDrawable = mDrawableBubble;
        //时间中点减去bubble宽度的一半
        int bubbleLeftMargin = leftTrimPos - bubbleDrawable.getIntrinsicWidth() / 2;
        drawBubble(canvas, bubbleDrawable, bubbleLeftMargin, mLeftMessage);
      }
    }
  }

  public void drawRightBar(Canvas canvas, boolean bDrawhandle, int left, int alpha,
      int leftBoundTrimPos, int rightBoundTrimPos, Paint paint) {
    int top;
    int bottom;

    if (mTrimRightPos <= rightBoundTrimPos) {
      int rightTrimPos = mTrimRightPos - leftBoundTrimPos;
      int rightMaskpos = getMaxTrimRange() - leftBoundTrimPos;
      if (isSupportScroll() && rightTrimPos > maxRightPos) {
        mTrimRightPos = maxRightPos;
        rightTrimPos = maxRightPos;
      }
      int leftMaskpos = rightTrimPos;

      top = (getHeight() - getChildWidth()) / 2;
      bottom = top + getChildWidth();

      if (leftMaskpos < left) {
        leftMaskpos = left;
      }

      if (rightMaskpos > getRight() - left) {
        rightMaskpos = getRight() - left;
      }

      leftMaskpos += EXTEND_WIDTH;
      if (rightMaskpos > leftMaskpos) {
        int color = (mbDragSatus == 2) ? mMaskLayerColorNormal
            : mMaskLayerColorNormal;// mMaskLayerColorPushed
        Rect rtRightMask = new Rect(leftMaskpos, top, rightMaskpos, bottom);
        paint.setColor(color);
        paint.setAlpha((int) (mMaskAlpha * mTrimAlpha));
        canvas.save();
        canvas.drawRect(rtRightMask, paint);
        canvas.restore();
      }

      if (isPlaying()) {
        return;
      }
      boolean isRightChosen = (mbDragSatus == 2) || !isLeftDraging;
      Drawable drawableRight = isRightChosen ? mDrawableRightTrimBarPush : mDrawableRightTrimBar;
      if (drawableRight == null) {
        return;
      }
      Drawable workDrawable = bDrawhandle ? drawableRight : mDrawableRightTrimBarDis;
      //右TrimBar的宽度
      int intrinsicWidth = workDrawable.getIntrinsicWidth();
      if (isCenterAlign()) {
        rightTrimPos = rightTrimPos - intrinsicWidth / 2;
      }
      mTrimRightBarLeftMargin = rightTrimPos;
      //右TrimBar的高度
      int intrinsicHeight = getChildWidth() + EXTRA_VER_PADDING;
      //右TrimBar的topMargin
      int topMargin = (getHeight() - intrinsicHeight) / 2;
      canvas.save();
      canvas.translate(mTrimRightBarLeftMargin, topMargin);

      workDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
      workDrawable.setAlpha(alpha);
      workDrawable.draw(canvas);
      canvas.restore();

      //绘制TrimBar上面的Bubble
      Drawable bubbleDrawable;
      if (isRightChosen && mTouchScreen) {
        bubbleDrawable = mDrawableBubble;
        float bubbleX =
            mTrimRightBarLeftMargin - (bubbleDrawable.getIntrinsicWidth() - intrinsicWidth) / 2;
        drawBubble(canvas, bubbleDrawable, bubbleX, mRightMessage);
      }
    }
  }

  private void drawLeftBar(Canvas canvas, boolean bDrawhandle, int left, int alpha,
      int leftBoundTrimPos, Paint paint) {

    int top;
    int bottom;
    if (mTrimLeftPos >= leftBoundTrimPos) {
      int leftTrimPos = mTrimLeftPos - leftBoundTrimPos;
      if (leftTrimPos < minLeftPos) {
        //确保leftBar画在屏幕内，同时更新实际trimPos
        leftTrimPos = minLeftPos;
        mTrimLeftPos = minLeftPos;
      }
      int leftMaskPos = left;

      top = (getHeight() - getChildWidth()) / 2;
      bottom = top + getChildWidth();
      int color = (mbDragSatus == 1) ? mMaskLayerColorNormal : mMaskLayerColorNormal;
      if (leftBoundTrimPos < 0 && leftMaskPos < Math.abs(leftBoundTrimPos)) {
        leftMaskPos = Math.abs(leftBoundTrimPos);
      }
      int leftTrimPos1 = leftTrimPos - 2 * EXTEND_WIDTH;
      if (leftTrimPos1 > leftMaskPos) {
        Rect rtLeftMask = new Rect(leftMaskPos, top, leftTrimPos1, bottom);
        paint.setColor(color);
        paint.setAlpha((int) (mMaskAlpha * mTrimAlpha));
        canvas.save();
        canvas.drawRect(rtLeftMask, paint);
        canvas.restore();
      }

      if (isPlaying()) {
        return;
      }
      boolean isLeftChosen = (mbDragSatus == 1) || isLeftDraging;
      Drawable drawableLeft = isLeftChosen ? mDrawableLeftTrimBarPush : mDrawableLeftTrimBar;
      if (drawableLeft == null) {
        return;
      }
      Drawable workDrawable = bDrawhandle ? drawableLeft : mDrawableLeftTrimBarDis;
      int intrinsicWidth = workDrawable.getIntrinsicWidth();
      if (isCenterAlign()) {
        mTrimLeftBarLeftMargin = leftTrimPos - intrinsicWidth / 2;
      } else {
        mTrimLeftBarLeftMargin = leftTrimPos - intrinsicWidth;
      }

      //设定TrimBar的高度
      int intrinsicHeight = getChildWidth() + EXTRA_VER_PADDING;
      //设定TrimBar的TopMargin
      int leftTopMargin = (getHeight() - intrinsicHeight) / 2;
      canvas.save();
      canvas.translate(mTrimLeftBarLeftMargin, leftTopMargin);

      workDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
      workDrawable.setAlpha(alpha);
      workDrawable.draw(canvas);
      canvas.restore();
      //绘制TrimBar上面的Bubble
      boolean isvisible = isLeftChosen && mTouchScreen;
      Drawable bubbleDrawable;
      if (isvisible) {
        bubbleDrawable = mDrawableBubble;
        float bubbleX =
            mTrimLeftBarLeftMargin - (bubbleDrawable.getIntrinsicWidth() - intrinsicWidth) / 2;
        drawBubble(canvas, bubbleDrawable, bubbleX, mLeftMessage);
      }
    }
  }

  /**
   * 绘制TrimBar上方的Bubble
   *
   * @param canvas 画布
   * @param bubbleX 气泡的X坐标
   * @param message 气泡上面的文字
   */
  private void drawBubble(Canvas canvas, Drawable bubbleDrawable, float bubbleX,
      String message) {
    if (bubbleDrawable == null) {
      return;
    }
    int topMargin = GSizeUtil.getFitPxFromDp(getContext(), 3);
    //画TrimBar上方的时间Bubble.
    mTextPaint.setAntiAlias(true);
    mTextPaint.setTextSize(GSizeUtil.getFitPxFromDp(getContext(), BUBBLE_TEXT_HEIGHT_DP));
    mTextPaint.setColor(getResources().getColor(bubbleTextColor));
    canvas.save();
    canvas.translate(bubbleX, 0);
    bubbleDrawable.setBounds(0, topMargin, bubbleDrawable.getIntrinsicWidth(),
        topMargin + bubbleDrawable.getIntrinsicHeight());
    bubbleDrawable.draw(canvas);
    canvas.restore();
    canvas.save();
    float offsetX = (bubbleDrawable.getIntrinsicWidth() - mTextPaint.measureText(message)) / 2;
    float offsetY = bubbleDrawable.getIntrinsicHeight() * 11 / 13.f
        - GSizeUtil.getFitPxFromDp(getContext(), BUBBLE_TEXT_HEIGHT_DP) / 2.f;
    canvas.drawText(message, bubbleX + offsetX, offsetY, mTextPaint);
    canvas.restore();
  }

  private void drawTrimContentMask(Canvas canvas, boolean isDisableState) {
    int leftBoundTrimPos = getLeftBoundTrimPos();
    int rightBoundTrimPos = getRightBoundTrimPos(leftBoundTrimPos);
    int intrinsicWidth = 0;
    int intrinsicWidth2 = 0;
    canvas.save();
    Drawable workDrawable = isDisableState ? mDrawableTrimContentDis : mDrawableTrimContent;
    if (mTrimLeftPos < leftBoundTrimPos && mTrimRightPos > rightBoundTrimPos) {
      int maskwidth = rightBoundTrimPos - leftBoundTrimPos;
      canvas.translate(0, (getHeight() - workDrawable.getIntrinsicHeight()) / 2);
      workDrawable.setBounds(0, 0, maskwidth, workDrawable.getIntrinsicHeight());
      workDrawable.draw(canvas);
    } else {
      if (mTrimLeftPos >= leftBoundTrimPos && mTrimRightPos <= rightBoundTrimPos) {
        int maskwidth;
        int leftTrimPos;
        if (isCenterAlign()) {
          maskwidth = mTrimRightPos - mTrimLeftPos;
          leftTrimPos = mTrimLeftPos - leftBoundTrimPos;
        } else {
          maskwidth = mTrimRightPos - mTrimLeftPos - intrinsicWidth - intrinsicWidth2;
          leftTrimPos = mTrimLeftPos - leftBoundTrimPos + intrinsicWidth;
        }

        int th = getChildWidth();
        canvas.translate(leftTrimPos, (getHeight() - th) / 2);
        workDrawable.setBounds(0, 0, maskwidth, th);
        workDrawable.draw(canvas);
      } else if (mTrimLeftPos < leftBoundTrimPos && mTrimRightPos <= rightBoundTrimPos) {
        int maskwidth;
        if (isCenterAlign()) {
          maskwidth = mTrimRightPos - mTrimLeftPos;
        } else {
          maskwidth = mTrimRightPos - leftBoundTrimPos - intrinsicWidth;
        }
        int th = getChildWidth();
        canvas.translate(0, (getHeight() - th) / 2);
        workDrawable.setBounds(0, 0, maskwidth, th);
        workDrawable.draw(canvas);
      } else if (mTrimLeftPos >= leftBoundTrimPos && mTrimRightPos > rightBoundTrimPos) {
        int maskwidth;
        int leftTrimPos;
        if (isCenterAlign()) {
          maskwidth = mTrimRightPos - mTrimLeftPos;
          leftTrimPos = mTrimLeftPos - leftBoundTrimPos;
        } else {
          maskwidth = mTrimRightPos - leftBoundTrimPos - intrinsicWidth2;
          leftTrimPos = mTrimLeftPos - leftBoundTrimPos + intrinsicWidth;
        }
        int th = getChildWidth();
        canvas.translate(leftTrimPos, (getHeight() - th) / 2);
        workDrawable.setBounds(0, 0, maskwidth, th);
        workDrawable.draw(canvas);
      }
    }
    canvas.restore();
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mOnTrimGalleryListener != null) {
      if (mOnTrimGalleryListener.onDispatchKeyDown(keyCode, event)) {
        return true;
      }
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (mOnTrimGalleryListener != null) {
      if (mOnTrimGalleryListener.onDispatchKeyUp(keyCode, event)) {
        return true;
      }
    }

    return super.onKeyUp(keyCode, event);
  }

  private class AutoScrollRunnable implements Runnable {
    private boolean mScrollToLeft = false;
    private boolean mScroll = false;

    public AutoScrollRunnable() {

    }

    private void startCommon() {
      removeCallbacks(this);
    }

    public void start(boolean toLeft) {
      if (isIgoneScrollEvent()) {
        return;
      }

      if (toLeft == mScrollToLeft && mScroll) {
        return;
      }

      mScrollToLeft = toLeft;
      startCommon();
      mScroll = true;
      postDelayed(this, 500);
    }

    public boolean isStarted() {
      return mScroll;
    }

    public void stop() {
      if (mScroll) {
        mScroll = false;
        removeCallbacks(this);
      }
    }

    @Override public void run() {
      int scrollDistance = 10;
      int scroll;
      int count = getCount();

      if (mScrollToLeft) {
        scroll = scroll(-scrollDistance);
      } else {
        scroll = scroll(scrollDistance);
      }

      if (scroll != 0) {
        scroll = -scroll;
        if (mbDragSatus == 1) {
          mTrimLeftPos += scroll;
          mOldposTrimBar += scroll;

          if (mTrimLeftPos < 0) {
            mOldposTrimBar += (-mTrimLeftPos);
            mTrimLeftPos = 0;
            stop();
          } else if (mTrimLeftPos > mTrimRightPos - 1) {
            mOldposTrimBar += (mTrimRightPos - 1 - mTrimLeftPos);
            mTrimLeftPos = mTrimRightPos - 1;
            stop();
          }

          mTrimLeftValue = getTrimValueByPosition(mTrimLeftPos, count);
        } else {
          int rightpos = getMaxTrimRange();

          mTrimRightPos += scroll;
          mOldposTrimBar += scroll;

          if (mTrimRightPos > rightpos) {
            mOldposTrimBar += (rightpos - mTrimRightPos);
            mTrimRightPos = rightpos;
            stop();
          } else if (mTrimRightPos < mTrimLeftPos + 1) {
            mOldposTrimBar += (mTrimLeftPos + 1 - mTrimRightPos);
            mTrimRightPos = mTrimLeftPos + 1;
            stop();
          }

          mTrimRightValue = getTrimValueByPosition(mTrimRightPos, count);
        }

        if (mTrimRightValue - mTrimLeftValue < MIN_TRIM_INTERVAL) {
          stop();

          int oldPos = (mbDragSatus == 1) ? mTrimLeftPos : mTrimRightPos;
          checkTrimPos(true);
          int newPos = (mbDragSatus == 1) ? mTrimLeftPos : mTrimRightPos;
          int interval = newPos - oldPos;
          mOldposTrimBar += interval;
        } else {
          trimValueModified = false;
        }

        if (mOnTrimGalleryListener != null) {
          mOnTrimGalleryListener.onTrimPosChanged(mClipIndex, mbDragSatus == 1,
              (mbDragSatus == 1) ? mTrimLeftValue : mTrimRightValue);
        }
      } else {
        stop();
      }

      if (mScroll) {
        postDelayed(this, 50);
      }
    }
  }

  private void layoutParentChild(boolean animation) {

    View parent = (View) getParent();
    if (parent == null || !(parent instanceof VeGallery)) {
      return;
    }

    VeGallery gallery = (VeGallery) parent;
    int curPosition = gallery.getChildPosition(this);
    int leftBounds = getChildLeftMostBounds() - mParentViewOffset;
    int childCount = gallery.getChildCount();
    int rightBounds = getChildRightMostBounds() + mParentViewOffset;

    if (curPosition > 0) {
      View child = gallery.getChildAt(curPosition - 1);
      if (child != null) {
        int right = child.getRight();
        if (leftBounds > 0 || (leftBounds < 0 && right > 0)) {
          for (int i = 0; i < curPosition; i++) {
            child = gallery.getChildAt(i);
            if (child != null) {
              int fromX = child.getLeft();
              child.offsetLeftAndRight(leftBounds - right);
              int toX = child.getLeft();
              if (animation && mOnGalleryOperationListener != null) {
                mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_MOVE_VIEW);
              }
            }
          }
        }
      }
    }

    if (curPosition < childCount - 1) {
      View child = gallery.getChildAt(curPosition + 1);
      if (child != null) {
        int left = child.getLeft();
        int galleryright = gallery.getWidth() - gallery.getPaddingRight();
        if (rightBounds < galleryright || (rightBounds > galleryright && left < galleryright)) {
          for (int i = curPosition + 1; i < childCount; i++) {
            child = gallery.getChildAt(i);
            if (child != null) {
              int fromX = child.getLeft();
              child.offsetLeftAndRight(rightBounds - left);
              int toX = child.getLeft();
              if (animation && mOnGalleryOperationListener != null) {
                mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_MOVE_VIEW);
              }
            }
          }
        }
      }
    }

    gallery.fillToGalleryLeft();
    gallery.fillToGalleryRight();
  }

  @Override protected void onGalleryMoved(int moveDistance) {
    layoutParentChild(false);
    mEmptyAreaPressed = false;
    mParentDownTouchPosition = -1;
  }

  public void setParentViewOffset(int offset) {
    mParentViewOffset = offset;
  }

  public void setMinLeftPos(int minLeftPos) {
    this.minLeftPos = minLeftPos;
  }

  public void setMaxRightPos(int maxRightPos) {
    this.maxRightPos = maxRightPos;
  }

  /**
   * gallery是否支持拖动
   * 只有拖动模式才会设置minLeftPos 和maxRightPos
   *
   * 拖动模式和定长模式处理position、value不一样，主要是拖动模式timeLine上有offset空出位置放bar
   */
  private boolean isSupportScroll() {
    return minLeftPos > 0 && maxRightPos > 0;
  }

  public int getChildLeftMostBounds() {
    int left = 0;

    View child = getChildAt(0);
    if (child != null) {
      left = child.getLeft();
    }

    return left;
  }

  public int getChildRightMostBounds() {
    int right = 0;
    int childCount = getChildCount();
    View child = getChildAt(childCount - 1);
    if (child != null) {
      right = child.getRight();
    }
    return right;
  }

  @Override protected boolean onSingleTap(MotionEvent e) {
    if (mEmptyAreaPressed) {
      mEmptyAreaPressed = false;
      if (mOnGalleryOperationListener != null) {
        mOnGalleryOperationListener.onEmptyAreaClick();
      }
      return true;
    } else {
      if (mParentDownTouchPosition >= 0) {
        View parent = (View) getParent();
        if (parent instanceof VeGallery) {
          VeGallery gallery = (VeGallery) parent;
          SpinnerAdapter adapter = gallery.getAdapter();
          View touchView = gallery.getChildAt(mParentDownTouchPosition);
          int index = mParentDownTouchPosition + gallery.getFirstVisiblePosition();
          if (adapter != null) {
            gallery.sendItemClick(touchView, index, adapter.getItemId(index));
          }
        }
        return true;
      }
      return super.onSingleTap(e);
    }
  }

  public int getCurPlayPos() {
    return mCurPlayTime;
  }

  public void setCurPlayPos(int mCurPlayPos) {
    this.mCurPlayTime = mCurPlayPos;
    postInvalidate();
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  public void setPlaying(boolean isPlaying) {
    this.isPlaying = isPlaying;
    postInvalidate();
  }

  public void setmDrawableTrimContent(Drawable mDrawableTrimContent) {
    this.mDrawableTrimContent = mDrawableTrimContent;
  }

  public boolean isCenterAlign() {
    return isCenterAlign;
  }

  public void setCenterAlign(boolean isCenterAlign) {
    this.isCenterAlign = isCenterAlign;
  }

  public boolean isLeftDraging() {
    return isLeftDraging;
  }

  public void setLeftDraging(boolean isLeftDraging) {
    this.isLeftDraging = isLeftDraging;
  }

  /**
   * 设置当前播放器的游标
   */
  public void setDrawableCurTimeNeedle(Drawable mDrawableCurTimeNeedle) {
    this.mDrawableCurTimeNeedle = mDrawableCurTimeNeedle;
  }

  public void setmDrawableLeftTrimBarDis(Drawable mDrawableLeftTrimBarDis) {
    this.mDrawableLeftTrimBarDis = mDrawableLeftTrimBarDis;
  }

  public void setmDrawableRightTrimBarDis(Drawable mDrawableRightTrimBarDis) {
    this.mDrawableRightTrimBarDis = mDrawableRightTrimBarDis;
  }

  public void setmDrawableTrimContentDis(Drawable mDrawableTrimContentDis) {
    this.mDrawableTrimContentDis = mDrawableTrimContentDis;
  }

  public void setMbDragSatus(int mbDragSatus) {
    this.mbDragSatus = mbDragSatus;
  }

  /**
   * 左侧TrimBar按住时，bubble上显示的时间
   */
  public void setLeftMessage(String leftMessage) {
    mLeftMessage = leftMessage;
  }

  /**
   * 右侧TrimBar按住时，bubble上显示的时间
   */
  public void setRightMessage(String rightMessage) {
    mRightMessage = rightMessage;
  }

  /**
   * Needle按住时，bubble上显示的时间
   */
  public void setSplitMessage(String splitMessage) {
    mLeftMessage = splitMessage;
  }

  /**
   * 是否focus在左边
   */
  public boolean isLeftChoosen() {
    return (mbDragSatus == 1) || isLeftDraging;
  }
}
