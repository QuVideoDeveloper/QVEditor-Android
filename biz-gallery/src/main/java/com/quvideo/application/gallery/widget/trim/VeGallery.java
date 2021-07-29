package com.quvideo.application.gallery.widget.trim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.Scroller;
import com.quvideo.application.gallery.R;
import java.lang.reflect.Field;

/**
 * A view that shows items in a center-locked, horizontally scrolling list.
 * <p>
 * The default values for the Gallery assume you will be using
 * as the background for
 * each View given to the Gallery from the Adapter. If you are not doing this,
 * you may need to adjust some Gallery properties, such as the spacing.
 * <p>
 * Views given to the Gallery should use as their
 * layout parameters type.
 *
 * @attr ref android.R.styleable#Gallery_animationDuration
 * @attr ref android.R.styleable#Gallery_spacing
 * @attr ref android.R.styleable#Gallery_gravity
 */

@SuppressLint("WrongCall") public class VeGallery extends VeAbsSpinner
    implements GestureDetector.OnGestureListener {

  private static final String TAG = "VeGallery";

  private static final boolean localLOGV = false;

  /**
   * Duration in milliseconds from the start of a scroll during which we're
   * unsure whether the user is scrolling or flinging.
   */
  private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;

  /**
   * Horizontal spacing between items.
   */
  private int mSpacing = 0;

  /**
   * How long the transition animation should run when a child view changes
   * position, measured in milliseconds.
   */
  private int mAnimationDuration = 400;

  /**
   * The alpha of items that are not selected.
   */
  private float mUnselectedAlpha;

  /**
   * Left most edge of a child seen so far during layout.
   */
  private int mLeftMost;

  /**
   * Right most edge of a child seen so far during layout.
   */
  private int mRightMost;

  private int mGravity;

  /**
   * Helper for detecting touch gestures.
   */
  private final GestureDetector mGestureDetector;

  /**
   * The position of the item that received the user's down touch.
   */
  private int mDownTouchPosition;

  /**
   * The view of the item that received the user's down touch.
   */
  private View mDownTouchView;

  /**
   * Executes the delta scrolls from a fling or scroll movement.
   */
  private final FlingRunnable mFlingRunnable = new FlingRunnable();

  /**
   * Sets mSuppressSelectionChanged = false. This is used to set it to false
   * in the future. It will also trigger a selection changed.
   */
  private final Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
    @Override public void run() {
      mSuppressSelectionChanged = false;
      selectionChanged();
    }
  };

  /**
   * When fling runnable runs, it resets this to false. Any method along the
   * path until the end of its run() can set this to true to abort any
   * remaining fling. For example, if we've reached either the leftmost or
   * rightmost item, we will set this to true.
   */
  private boolean mShouldStopFling;

  /**
   * The currently selected item's child.
   */
  private View mSelectedChild;

  /**
   * Whether to continuously callback on the item selected listener during a
   * fling.
   */
  private boolean mShouldCallbackDuringFling = true;

  /**
   * Whether to callback when an item that is not selected is clicked.
   */
  private boolean mShouldCallbackOnUnselectedItemClick = true;

  /**
   * If true, do not callback to item selected listener.
   */
  private boolean mSuppressSelectionChanged;

  /**
   * If true, we have received the "invoke" (center or enter buttons) key
   * down. This is checked before we action on the "invoke" key up, and is
   * subsequently cleared.
   */
  private boolean mReceivedInvokeKeyDown;

  private AdapterContextMenuInfo mContextMenuInfo;

  /**
   * If true, this onScroll is the first for this user's drag (remember, a
   * drag sends many onScrolls).
   */
  private boolean mIsFirstScroll;

  private boolean mIsCenterLocked = false;

  private boolean mIsAllowedIdlySpace = false;

  private boolean mCanSendMoveStop = false;

  protected int mChildWidth = 0;

  protected int mCenterOffsetIndex = -1;

  protected boolean mApplySingleTap = false;

  protected boolean mInterceptTouchEvent = false;

  protected int mSelectionIndexOnLayout = -1;

  protected int mSelectionOffsetOnLayout = 0;

  /**
   * gallery 左边bar的offset
   * 滚动模式下使用
   */
  protected int mLeftLimitMoveOffset = 0;

  protected int mRightLimitMoveOffset = 0;

  protected boolean mIsPressedStatus = false;

  protected boolean mHookAllKeyEvent = true;

  private boolean mIgnoreScrollEvent = false;

  private boolean mIgnoreLayoutRequest = false;

  protected OnGalleryOperationListener mOnGalleryOperationListener = null;

  protected OnTouchListener mOnGalleryChildTouchedListener = null;

  private OnGalleryDrawListener mOnGalleryDrawListener = null;

  private OnPinchZoomGestureListener mOnPinchZoomGestureListener = null;

  private OnLayoutListener mOnLayoutListener = null;

  private OnGalleryDoubleTapListener mOnGalleryDoubleTapListener = null;

  private final GalleryAutoScrollRunnable mGalleryAutoScrollRunnable =
      new GalleryAutoScrollRunnable();

  private OnPrepareChildLisener mOnPrepareChildListener = null;

  private boolean mDisallowChildInterceptOnMoving = false;

  private boolean mHaveMotionTarget = false;

  private MotionEvent mCurrentDownEvent;

  private int mTouchSlopSquare = 0;

  private boolean mIgnoreTouchEvent = false;

  private boolean mDispatchPressed = true;

  private boolean mFillToCenter = false; // Fill all child views to center
  // when load a story

  private boolean mLeftToCenter = false; // Fill all child views left to
  // center or center to center

  private int mLeftToCenterOffset = 0;

  protected boolean mEnableTouchEvent = true;

  protected boolean mEnableLayout = true;

  protected boolean mEnableMoutichTouchEvent = false;

  private int mClientFocusIndex = 0;
  private int mLastDownTouchPosition = -1;

  protected final static int NONE = 0;
  protected final static int DRAG = 1;
  protected final static int ZOOM = 2;
  protected int mTouchMode = NONE;
  public static final int ON_DELETE_VIEW = 0;
  public static final int ON_MOVE_VIEW = 1;
  public static final int ON_ADD_VIEW = 2;

  public VeGallery(Context context) {
    this(context, null);
  }

  public VeGallery(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.galleryStyle);
  }

  public VeGallery(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mGestureDetector = new GestureDetector(context, this);
    mGestureDetector.setIsLongpressEnabled(true);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VeGallery, defStyle, 0);

    int index = a.getInt(R.styleable.VeGallery_android_gravity, -1);
    if (index >= 0) {
      setGravity(index);
    }

    int animationDuration = a.getInt(R.styleable.VeGallery_android_animationDuration, -1);
    if (animationDuration > 0) {
      setAnimationDuration(animationDuration);
    }

    int spacing = a.getDimensionPixelOffset(R.styleable.VeGallery_android_spacing, 0);
    setSpacing(spacing);

    float unselectedAlpha = a.getFloat(R.styleable.VeGallery_android_unselectedAlpha, 0.5f);
    setUnselectedAlpha(unselectedAlpha);

    a.recycle();

    setStaticTransformationsEnabled(true);
    // We draw the selected item last (because otherwise the item to the
    // right overlaps it)
    // mGroupFlags |= FLAG_USE_CHILD_DRAWING_ORDER;
    // mGroupFlags |= FLAG_SUPPORT_STATIC_TRANSFORMATIONS;

    int touchSlop;
    final ViewConfiguration configuration = ViewConfiguration.get(context);
    touchSlop = configuration.getScaledTouchSlop();
    mTouchSlopSquare = touchSlop * touchSlop;
    // LogUtils.e("init",this.getClass().toString() + " - " + this);
  }

  /**
   * Interface definition for a callback to be invoked when gallery has been
   * moved.
   */
  public interface OnGalleryOperationListener {

    /**
     * Callback method to be invoked when Gallery is moving.
     *
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to
     * access the data associated with the selected item.
     *
     * @param view The view within the VeAdapterView that was clicked (this
     * will be a view provided by the adapter)
     */
    void onMoveStart(View view);

    void onMoving(View view, int movedistance);

    void onMoveStoped(View view);

    void onChildReLocation(View child, int fromX, int toX, int type);

    void onDown();

    void onUp();

    void onEmptyAreaClick();
  }

  public interface OnPinchZoomGestureListener {
    void onStartPinchZoom(float spacing);

    void onZoomChanged(float spacing);

    void onStopPinchZoom();
  }

  public interface OnGalleryDoubleTapListener {
    boolean onDubleTap(VeAdapterView<?> parent, View view, int position);
  }

  public interface OnPrepareChildLisener {
    void onPrepareChildToAdd(View child, LayoutParams lp, int childIndex, int childCount);

    void onPrepareChildToDelete(int childIndex, int childCount);
  }

  public void setOnPrepareChildListener(OnPrepareChildLisener listener) {
    mOnPrepareChildListener = listener;
  }

  public void setOnGalleryOperationListener(OnGalleryOperationListener listener) {
    mOnGalleryOperationListener = listener;
  }

  public void setOnPinchZoomGestureListener(OnPinchZoomGestureListener listener) {
    mOnPinchZoomGestureListener = listener;
  }

  public void setOnDoubleTapListener(OnGalleryDoubleTapListener listener) {
    mOnGalleryDoubleTapListener = listener;
    if (listener != null) {
      mGestureDetector.setOnDoubleTapListener(mGalleryOnDoubleTapListener);
    } else {
      mGestureDetector.setOnDoubleTapListener(null);
    }
  }

  private boolean dispatchDoubleTap() {
    if (mOnGalleryDoubleTapListener != null
        && mDownTouchPosition >= 0
        && mDownTouchPosition == mLastDownTouchPosition) {
      View child = this.getChildAt(mDownTouchPosition - mFirstPosition);
      return mOnGalleryDoubleTapListener.onDubleTap(this, child, mDownTouchPosition);
    }
    return false;
  }

  private final OnDoubleTapListener mGalleryOnDoubleTapListener = new OnDoubleTapListener() {

    @Override public boolean onDoubleTap(MotionEvent e) {
      // TODO Auto-generated method stub
      dispatchDoubleTap();
      return true;
    }

    @Override public boolean onDoubleTapEvent(MotionEvent e) {
      // TODO Auto-generated method stub
      return false;
    }

    @Override public boolean onSingleTapConfirmed(MotionEvent e) {
      onSingleTap(e);
      return false;
    }
  };

  /**
   * Interface definition for a callback to be invoked when gallery is
   * drawing.
   */
  public interface OnGalleryDrawListener {
    void onDraw(Canvas canvas);
  }

  public interface OnLayoutListener {
    void onLayout(View view);
  }

  public void setOnGalleryDrawListener(OnGalleryDrawListener listener) {
    mOnGalleryDrawListener = listener;
  }

  public void setOnLayoutListener(OnLayoutListener listener) {
    mOnLayoutListener = listener;
  }

  /**
   * Whether or not to callback on any {@link #getOnItemSelectedListener()}
   * while the items are being flinged. If false, only the final selected item
   * will cause the callback. If true, all items between the first and the
   * final will cause callbacks.
   *
   * @param shouldCallback Whether or not to callback on the listener while the items are
   * being flinged.
   */
  public void setCallbackDuringFling(boolean shouldCallback) {
    mShouldCallbackDuringFling = shouldCallback;
  }

  /**
   * Whether or not to callback when an item that is not selected is clicked.
   * If false, the item will become selected (and re-centered). If true, the
   * {@link #getOnItemClickListener()} will get the callback.
   *
   * @param shouldCallback Whether or not to callback on the listener when a item that is
   * not selected is clicked.
   * @hide
   */
  public void setCallbackOnUnselectedItemClick(boolean shouldCallback) {
    mShouldCallbackOnUnselectedItemClick = shouldCallback;
  }

  /**
   * Sets how long the transition animation should run when a child view
   * changes position. Only relevant if animation is turned on.
   *
   * @param animationDurationMillis The duration of the transition, in milliseconds.
   * @attr ref android.R.styleable#Gallery_animationDuration
   */
  public void setAnimationDuration(int animationDurationMillis) {
    mAnimationDuration = animationDurationMillis;
  }

  /**
   * Sets the spacing between items in a Gallery
   *
   * @param spacing The spacing in pixels between items in the Gallery
   * @attr ref android.R.styleable#Gallery_spacing
   */
  public void setSpacing(int spacing) {
    mSpacing = spacing;
  }

  public int getSapcing() {
    return mSpacing;
  }

  /**
   * Sets the alpha of items that are not selected in the Gallery.
   *
   * @param unselectedAlpha the alpha for the items that are not selected.
   * @attr ref android.R.styleable#Gallery_unselectedAlpha
   */
  public void setUnselectedAlpha(float unselectedAlpha) {
    mUnselectedAlpha = unselectedAlpha;
  }

  @Override protected boolean getChildStaticTransformation(View child, Transformation t) {

    t.clear();
    t.setAlpha(child == mSelectedChild ? 1.0f : mUnselectedAlpha);

    return true;
  }

  @Override protected int computeHorizontalScrollExtent() {
    // Only 1 item is considered to be selected
    return 1;
  }

  @Override protected int computeHorizontalScrollOffset() {
    // Current scroll position is the same as the selected position
    if (!mIsCenterLocked) {
      return mFirstPosition;
    }

    return mSelectedPosition;
  }

  @Override protected int computeHorizontalScrollRange() {
    // Scroll range is the same as the item count
    return mItemCount;
  }

  @Override protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof LayoutParams;
  }

  @Override protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return new LayoutParams(p);
  }

  @Override public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  @Override protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
    /*
     * Gallery expects Gallery.LayoutParams.
     */
    return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
  }

  @Override public void requestLayout() {
    if (!mBlockLayoutRequests) {
      super.requestLayout();
      enableLayout(true);
    }
  }

  public void blockLayoutRequests(boolean isBlock) {
    mBlockLayoutRequests = isBlock;
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    /*
     * Remember that we are in layout to prevent more layout request from
     * being generated.
     */

    if (!mEnableLayout) {
      return;
    }

    mInLayout = true;
    if (!isInEditMode()) {
      layout(0, false);
    }
    mInLayout = false;
  }

  public void enableLayout(boolean enable) {
    mEnableLayout = enable;
  }

  @Override int getChildHeight(View child) {
    return child.getMeasuredHeight();
  }

  /**
   * Tracks a motion scroll. In reality, this is used to do just about any
   * movement to items (touch scroll, arrow-key scroll, set an item as
   * selected).
   *
   * @param deltaX Change in X from the previous event.
   */
  int trackMotionScroll(int deltaX, boolean enableNotifyStartAndEnd) {

    if (getChildCount() == 0 || deltaX == 0) {
      return 0;
    }

    boolean toLeft = deltaX < 0;
    int gallerWidth = getWidth() - getPaddingLeft() - getPaddingRight();

    int limitedDeltaX = getLimitedMotionScrollAmount(toLeft, deltaX);

    if (limitedDeltaX != 0) {
      if (limitedDeltaX >= gallerWidth) {
        limitedDeltaX = gallerWidth - 1;
      }

      if (limitedDeltaX <= -gallerWidth) {
        limitedDeltaX = -gallerWidth + 1;
      }

      offsetChildrenLeftAndRight(limitedDeltaX);

      detachOffScreenChildren(toLeft);

      if (toLeft) {
        // If moved left, there will be empty space on the right
        fillToGalleryRight();
      } else {
        // Similarly, empty space on the left
        fillToGalleryLeft();
      }

      // Clear unused views
      mRecycler.clear();

      if (mIsCenterLocked) {
        setSelectionToCenterChild();
      }

      onGalleryMoved(limitedDeltaX);

      if (mOnGalleryOperationListener != null) {
        if (mIsFirstScroll && enableNotifyStartAndEnd) {
          mOnGalleryOperationListener.onMoveStart(this);
          mIsFirstScroll = false;
        }
        if (enableNotifyStartAndEnd) {
          mCanSendMoveStop = true;
        }

        mOnGalleryOperationListener.onMoving(this, limitedDeltaX);
      }

      invalidate();
    }

    if (limitedDeltaX != deltaX) {
      // The above call returned a limited amount, so stop any
      // scrolls/flings
      mFlingRunnable.endFling(false);
      onFinishedMovement();

      if (toLeft) {
        // Similarly, empty space on the left
        fillToGalleryLeft();
      } else {
        // If moved left, there will be empty space on the right
        fillToGalleryRight();
      }
    }

    return limitedDeltaX;
  }

  protected void onGalleryMoved(int moveDistance) {

  }

  int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX) {
    int extremeItemPosition = motionToLeft ? mItemCount - 1 : 0;
    View extremeChild = getChildAt(extremeItemPosition - mFirstPosition);
    final int galleryLeft = getPaddingLeft();
    final int galleryRight = getWidth() - getPaddingRight();
    int galleryCenter = mIsAllowedIdlySpace ? getCenterOfGallery() : 0;

    if (extremeChild == null) {

      if (mIsAllowedIdlySpace && mIsCenterLocked) {
        return deltaX;
      } else {
        int allowedspace = 0;

        if (motionToLeft) {
          int iLastPosition = getLastVisiblePosition();
          extremeChild = getChildAt(iLastPosition - mFirstPosition);
          if (extremeChild == null) {
            return allowedspace;
          }
          if (iLastPosition < mItemCount - 1) {
            allowedspace = (mItemCount - 1 - iLastPosition) * mChildWidth;
          }

          allowedspace += (extremeChild.getRight() - galleryRight);

          allowedspace += (mSpacing * (mItemCount - 1 - iLastPosition));

          if (mIsAllowedIdlySpace) {
            allowedspace += (galleryCenter - galleryLeft);
          }

          if (mIsCenterLocked) {
            allowedspace -= mChildWidth / 2;
          }

          allowedspace -= mRightLimitMoveOffset;

          return Math.max(-allowedspace, deltaX);
        } else {
          extremeChild = getChildAt(0);
          if (extremeChild == null) {
            return allowedspace;
          }
          allowedspace = mFirstPosition * mChildWidth;
          allowedspace += (-extremeChild.getLeft() + galleryLeft);
          allowedspace += (mSpacing * mFirstPosition);

          if (mIsAllowedIdlySpace) {
            allowedspace += (galleryCenter - galleryLeft);
          }
          if (mIsCenterLocked) {
            allowedspace -= mChildWidth / 2;
          }

          allowedspace += mLeftLimitMoveOffset;

          return Math.min(allowedspace, deltaX);
        }
      }
    }

    int extremeChildCenter = mIsAllowedIdlySpace ? getCenterOfView(extremeChild) : 0;

    if (motionToLeft) {
      if (mIsAllowedIdlySpace) {
        if (mIsCenterLocked) {
          if (extremeChildCenter <= galleryCenter) {

            // The extreme child is past his boundary point!
            return 0;
          }
        } else {
          if (extremeChild.getRight() <= galleryCenter + mRightLimitMoveOffset) {
            return 0;
          }
        }
      } else {
        View extremeChildLeft = getChildAt(0);
        if (null != extremeChildLeft) {
          // left first.
          if (extremeChildLeft.getLeft() > galleryLeft) {
            int delta = galleryLeft - extremeChildLeft.getLeft();
            if (delta < deltaX) {
              return delta;
            } else {
              return deltaX;
            }
          }
        }

        if (extremeChild.getRight() <= galleryRight + mRightLimitMoveOffset) {
          return 0;
        } else {
          int distance = galleryRight + mRightLimitMoveOffset - extremeChild.getRight();
          if (Math.abs(distance) > Math.abs(deltaX)) {
            return deltaX;
          } else {
            return distance;
          }
        }
      }
    } else {
      if (mIsAllowedIdlySpace) {
        if (mIsCenterLocked) {
          if (extremeChildCenter >= galleryCenter) {

            // The extreme child is past his boundary point!
            return 0;
          }
        } else {
          if (extremeChild.getLeft() >= galleryCenter + mLeftLimitMoveOffset) {

            // The extreme child is past his boundary point!
            return 0;
          }
        }
      } else {
        if (isInEditMode()) {
          if (extremeChild.getLeft() >= galleryLeft) {
            return 0;
          }
        } else {
          if (extremeChild.getLeft() > galleryLeft + mLeftLimitMoveOffset) {
            return galleryLeft + mLeftLimitMoveOffset - extremeChild.getLeft();
          } else if (extremeChild.getLeft() == galleryLeft + mLeftLimitMoveOffset) {
            return 0;
          } else {
            return deltaX;
          }
        }
      }
    }
    if (mIsAllowedIdlySpace) {
      int centerDifference;

      if (mIsCenterLocked) {
        centerDifference = galleryCenter - extremeChildCenter;
      } else {
        if (motionToLeft) {
          centerDifference = galleryCenter - extremeChild.getRight();
          centerDifference += mRightLimitMoveOffset;
        } else {
          centerDifference = galleryCenter - extremeChild.getLeft();
          centerDifference += mLeftLimitMoveOffset;
        }
      }

      return motionToLeft ? Math.max(centerDifference, deltaX)
          : Math.min(centerDifference, deltaX);
    } else {

      return motionToLeft ? Math.max(galleryRight - extremeChild.getRight(), deltaX)
          : Math.min(galleryLeft - extremeChild.getLeft(), deltaX);
    }
  }

  /**
   * Offset the horizontal location of all children of this view by the
   * specified number of pixels.
   *
   * @param offset the number of pixels to offset
   */
  private void offsetChildrenLeftAndRight(int offset) {
    for (int i = getChildCount() - 1; i >= 0; i--) {
      getChildAt(i).offsetLeftAndRight(offset);
    }
  }

  /**
   * @return The center of this Gallery.
   */
  public int getCenterOfGallery() {
    return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
  }

  /**
   * @return The center of the given view.
   */
  public static int getCenterOfView(View view) {
    return view.getLeft() + view.getWidth() / 2;
  }

  /**
   * Detaches children that are off the screen (i.e.: Gallery bounds).
   *
   * @param toLeft Whether to detach children to the left of the Gallery, or to
   * the right.
   */
  public void detachOffScreenChildren(boolean toLeft) {
    int numChildren = getChildCount();
    int firstPosition = mFirstPosition;
    int start = 0;
    int count = 0;

    if (toLeft) {
      final int galleryLeft = getPaddingLeft();
      for (int i = 0; i < numChildren; i++) {
        final View child = getChildAt(i);
        if (child.getRight() >= galleryLeft) {
          break;
        } else {
          count++;
          mRecycler.put(firstPosition + i, child);
        }
      }
    } else {
      final int galleryRight = getWidth() - getPaddingRight();
      for (int i = numChildren - 1; i >= 0; i--) {
        final View child = getChildAt(i);
        if (child.getLeft() <= galleryRight) {
          break;
        } else {
          start = i;
          count++;
          mRecycler.put(firstPosition + i, child);
        }
      }
    }

    detachViewsFromParent(start, count);

    if (toLeft) {
      mFirstPosition += count;
    }
  }

  public void detachViewFromParent(int start, int count) {
    if (null != mOnPrepareChildListener && count > 0) {
      mOnPrepareChildListener.onPrepareChildToDelete(start, getChildCount());
    }
    detachViewsFromParent(start, count);
  }

  public int getChildPosition(View view) {

    // Search the children for the list item
    final int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      if (getChildAt(i).equals(view)) {
        return i;
      }
    }
    // Child not found!
    return INVALID_POSITION;
  }

  public void addChildView(View child, int absoluteIndex, int x) {
    if (absoluteIndex < mFirstPosition) {
      mFirstPosition += 1;
      return;
    } else if (absoluteIndex > getLastVisiblePosition()) {
      return;
    }
    addViewToParent(child, absoluteIndex - mFirstPosition, x);
    mOldItemCount = mItemCount;
    mItemCount += 1;
  }

  public boolean deleteChildView(int start, int count, int direction) {
    if (start < 0 || count <= 0 || start >= getCount() || start + count > getCount()) {
      return false;
    }
    int leftX = 0, rightX = 0, interval;
    int delCount = count;

    if (start + count - 1 < mFirstPosition || start > getLastVisiblePosition()) {
      mOldItemCount = mItemCount;
      mItemCount -= count;

      if (start + count - 1 < mFirstPosition) {
        mFirstPosition -= count;
      }

      return true;
    }

    start -= mFirstPosition;

    if (start < 0) {
      delCount -= (0 - start);
      start = 0;
    }

    if (start + delCount > getChildCount()) {
      delCount = getChildCount() - start;
    }

    View child;
    View startView = getChildAt(start);
    View endView = getChildAt(start + delCount - 1);

    if (startView != null) {
      leftX = startView.getLeft();
    }

    if (endView != null) {
      rightX = endView.getRight();
    }
    interval = rightX - leftX + mSpacing;

    for (int i = start; i < start + delCount; i++) {
      child = getChildAt(i);
      if (child != null) {
        mRecycler.put(mFirstPosition + i, child);
      }
    }

    detachViewFromParent(start, delCount);

    mOldItemCount = mItemCount;
    mItemCount -= count;

    if (start == 0) {
      mFirstPosition += delCount;
      mFirstPosition -= count;
      if (mFirstPosition < 0) {
        mFirstPosition = 0;
      }
    }

    if (direction == 0) {
      // left view to Close in
      for (int i = start - 1; i >= 0; i--) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft();
          child.offsetLeftAndRight(interval);
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_DELETE_VIEW);
          }
        }
      }
      child = getChildAt(0);
      fillToGalleryLeft();
      if (child != null) {
        int newPosition = getChildPosition(child);
        for (int i = newPosition - 1; i >= 0; i--) {
          child = getChildAt(i);
          if (child != null) {
            int fromX = child.getLeft() - interval;
            int toX = child.getLeft();
            if (mOnGalleryOperationListener != null) {
              mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_DELETE_VIEW);
            }
          }
        }
      }
    } else if (direction == 1) {
      // right view to Close in
      for (int i = start; i < getChildCount(); i++) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft();
          child.offsetLeftAndRight(-interval);
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_DELETE_VIEW);
          }
        }
      }
      int childLastIndex = getChildCount() - 1;
      fillToGalleryRight();

      for (int i = childLastIndex + 1; i < getChildCount(); i++) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft() + interval;
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_DELETE_VIEW);
          }
        }
      }
    } else if (direction == 2) {
      int half = interval / 2;
      for (int i = start - 1; i >= 0; i--) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft();
          child.offsetLeftAndRight(half);
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_DELETE_VIEW);
          }
        }
      }
      child = getChildAt(0);
      fillToGalleryLeft();
      if (child != null) {
        int newPosition = getChildPosition(child);
        for (int i = newPosition - 1; i >= 0; i--) {
          child = getChildAt(i);
          if (child != null) {
            int fromX = child.getLeft() - interval;
            int toX = child.getLeft();
            if (mOnGalleryOperationListener != null) {
              mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_DELETE_VIEW);
            }
          }
        }
      }
      for (int i = start; i < getChildCount(); i++) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft();
          child.offsetLeftAndRight(-(interval - half));
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_DELETE_VIEW);
          }
        }
      }
      int childLastIndex = getChildCount() - 1;
      fillToGalleryRight();

      for (int i = childLastIndex + 1; i < getChildCount(); i++) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft() + interval;
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_DELETE_VIEW);
          }
        }
      }
    }

    invalidate();
    return true;
  }

  public void AddAndDecFirstPosition(boolean isAdd) {
    if (isAdd) {
      mFirstPosition++;
    } else {
      mFirstPosition--;
    }
  }

  public boolean moveViewToIndex(View[] viewArray, int dstIndex, int initIndex) {
    if (viewArray == null) {
      return false;
    }
    int viewCount = viewArray.length;
    int oldIndex = getChildPosition(viewArray[0]);
    int oldIndexEnd = getChildPosition(viewArray[viewCount - 1]);
    int newInsertPos = dstIndex - mFirstPosition;
    int newPosX = 0;
    int curViewPosX;
    int totalWidth = 0;

    if (dstIndex == initIndex) {
      return false;
    }
    if ((newInsertPos > oldIndex && oldIndex + viewCount > newInsertPos) || (newInsertPos
        < oldIndex && oldIndex - viewCount < newInsertPos)) {
      return false;
    }

    int curChildIndex = newInsertPos;

    View childCur = getChildAt(curChildIndex);

    if (childCur != null) {
      newPosX = childCur.getLeft();
    }
    for (int i = 0; i < viewCount; i++) {
      oldIndex = getChildPosition(viewArray[i]);
      if (oldIndex >= 0) {
        detachViewFromParent(oldIndex, 1);
      } else {
        if (initIndex < dstIndex) {
          newInsertPos++;
          mFirstPosition--;
        }
      }
    }

    if (newInsertPos > getChildCount()) {
      newInsertPos = getChildCount();
    }

    for (int i = 0; i < viewCount; i++) {
      if (viewArray[i] != null) {
        addViewToParent(viewArray[i], newInsertPos + i, 0);
        curViewPosX = viewArray[i].getLeft();

        viewArray[i].offsetLeftAndRight(newPosX - curViewPosX);
        newPosX += viewArray[i].getWidth() + mSpacing;
        totalWidth += viewArray[i].getWidth() + mSpacing;
      }
    }

    View child;

    if ((oldIndex >= 0 && oldIndex < dstIndex - mFirstPosition) || (oldIndex < 0
        && initIndex < dstIndex)) {

      for (int i = newInsertPos - 1; i >= oldIndex; i--) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft();
          child.offsetLeftAndRight(-totalWidth);
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_MOVE_VIEW);
          }
        }
      }
      child = getChildAt(newInsertPos - 1);
      if (child != null && viewArray != null && viewArray[0] != null) {
        int right = child.getRight() + mSpacing;
        if (right != viewArray[0].getLeft()) {
          int interval = right - viewArray[0].getLeft();
          for (int i = 0; i < viewCount; i++) {
            if (viewArray[i] != null) {
              viewArray[i].offsetLeftAndRight(interval);
            }
          }
        }
      }
    } else {
      newInsertPos += viewCount;
      if (oldIndex >= 0) {
        oldIndex += viewCount;
      } else {
        oldIndex = getChildCount();
      }

      for (int i = newInsertPos; i < oldIndex; i++) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft();
          child.offsetLeftAndRight(totalWidth);
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_MOVE_VIEW);
          }
        }
      }
    }

    invalidate();

    return true;
  }

  public void addViewToPosition(View[] viewArray, int insertIndex, int x) {
    int viewCount = viewArray.length;
    int curViewPosX;
    int newPosX = x;
    int right = x;
    int childCount = getChildCount();

    for (int i = 0; i < viewCount; i++) {
      if (viewArray[i] != null) {
        addViewToParent(viewArray[i], insertIndex + i, x);
        curViewPosX = viewArray[i].getLeft();

        viewArray[i].offsetLeftAndRight(newPosX - curViewPosX);
        newPosX += viewArray[i].getWidth() + mSpacing;
        right += viewArray[i].getWidth() + mSpacing;
      }
    }

    // mOldItemCount = mItemCount;
    // mItemCount += viewCount;

    int interval = 0;
    View child;

    for (int i = insertIndex - 1; i >= 0; i--) {
      child = getChildAt(i);
      if (child != null) {

        if (i == insertIndex - 1) {
          interval = x - child.getRight() - mSpacing;
          if (interval == 0) {
            break;
          }
        }
        int fromX = child.getLeft();
        child.offsetLeftAndRight(interval);
        int toX = child.getLeft();
        if (mOnGalleryOperationListener != null) {
          mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_ADD_VIEW);
        }
      }
    }

    child = getChildAt(0);
    fillToGalleryLeft();
    if (child != null) {
      int newPosition = getChildPosition(child);
      insertIndex += newPosition;
      for (int i = newPosition - 1; i >= 0; i--) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft() - interval;
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_ADD_VIEW);
          }
        }
      }
    }

    childCount = getChildCount();
    for (int i = insertIndex + viewCount; i < childCount; i++) {
      child = getChildAt(i);
      if (child != null) {

        if (i == insertIndex + viewCount) {
          interval = right - child.getLeft();
          if (interval == 0) {
            break;
          }
        }
        int fromX = child.getLeft();
        child.offsetLeftAndRight(interval);
        int toX = child.getLeft();
        if (mOnGalleryOperationListener != null) {
          mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_ADD_VIEW);
        }
      }
    }

    int childLastIndex = getChildCount() - 1;
    fillToGalleryRight();

    for (int i = childLastIndex + 1; i < getChildCount(); i++) {
      child = getChildAt(i);
      if (child != null) {
        int fromX = child.getLeft() + interval;
        int toX = child.getLeft();
        if (mOnGalleryOperationListener != null) {
          mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_ADD_VIEW);
        }
      }
    }
    // In the initial state, center need to set.
    if (1 == childCount) {
      // setSelectionToCenterChild();
    }
  }

  public void addViewToCenter(View[] viewArray, int insertIndex) {
    int center = getCenterOfGallery();
    int viewCount = viewArray.length;
    int curViewPosX;
    int newPosX = center;
    int right = center;
    int childCount = getChildCount();

    for (int i = 0; i < viewCount; i++) {
      if (viewArray[i] != null) {
        addViewToParent(viewArray[i], insertIndex + i, 0);
        curViewPosX = viewArray[i].getLeft();

        viewArray[i].offsetLeftAndRight(newPosX - curViewPosX);
        newPosX += viewArray[i].getWidth() + mSpacing;
        right += viewArray[i].getWidth() + mSpacing;
      }
    }

    mOldItemCount = mItemCount;
    mItemCount += viewCount;

    int interval = 0;
    View child;

    for (int i = insertIndex - 1; i >= 0; i--) {
      child = getChildAt(i);
      if (child != null) {

        if (i == insertIndex - 1) {
          interval = center - child.getRight() - mSpacing;
          if (interval == 0) {
            break;
          }
        }
        int fromX = child.getLeft();
        child.offsetLeftAndRight(interval);
        int toX = child.getLeft();
        if (mOnGalleryOperationListener != null) {
          mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_ADD_VIEW);
        }
      }
    }

    child = getChildAt(0);
    fillToGalleryLeft();
    if (child != null) {
      int newPosition = getChildPosition(child);
      insertIndex += newPosition;
      for (int i = newPosition - 1; i >= 0; i--) {
        child = getChildAt(i);
        if (child != null) {
          int fromX = child.getLeft() - interval;
          int toX = child.getLeft();
          if (mOnGalleryOperationListener != null) {
            mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_ADD_VIEW);
          }
        }
      }
    }

    childCount = getChildCount();
    for (int i = insertIndex + viewCount; i < childCount; i++) {
      child = getChildAt(i);
      if (child != null) {

        if (i == insertIndex + viewCount) {
          interval = right - child.getLeft();
          if (interval == 0) {
            break;
          }
        }
        int fromX = child.getLeft();
        child.offsetLeftAndRight(interval);
        int toX = child.getLeft();
        if (mOnGalleryOperationListener != null) {
          mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_ADD_VIEW);
        }
      }
    }

    int childLastIndex = getChildCount() - 1;
    fillToGalleryRight();

    for (int i = childLastIndex + 1; i < getChildCount(); i++) {
      child = getChildAt(i);
      if (child != null) {
        int fromX = child.getLeft() + interval;
        int toX = child.getLeft();
        if (mOnGalleryOperationListener != null) {
          mOnGalleryOperationListener.onChildReLocation(child, fromX, toX, ON_ADD_VIEW);
        }
      }
    }
    // In the initial state, center need to set.
    if (1 == childCount) {
      setSelectionToCenterChild();
    }
  }

  public void addViewToParent(View child, int index, int x) {
    LayoutParams lp = (LayoutParams) child.getLayoutParams();
    if (lp == null) {
      lp = (LayoutParams) generateDefaultLayoutParams();
    }

    if (null != mOnPrepareChildListener) {
      mOnPrepareChildListener.onPrepareChildToAdd(child, lp, index, getChildCount());
    }

    addViewInLayout(child, index, lp);

    child.setSelected(true);

    // Get measure specs
    int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec,
        mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
    int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
        mSpinnerPadding.left + mSpinnerPadding.right, lp.width);

    // Measure child
    child.measure(childWidthSpec, childHeightSpec);

    int childLeft;
    int childRight;

    // Position vertically based on gravity setting
    int childTop = calculateTop(child, true);
    int childBottom = childTop + child.getMeasuredHeight();

    int width = child.getMeasuredWidth();

    childLeft = x;
    childRight = childLeft + width;
    child.layout(childLeft, childTop, childRight, childBottom);
    // In the initial state, center need to set.
    if (1 == getChildCount()) {
      setSelectionToCenterChild();
    }

    invalidate();
  }

  public void addViewAtPosition(View child, int index, int x) {
    addViewToParent(child, index, x);
    mOldItemCount = mItemCount;
    mItemCount += 1;
  }

  public void detachViewAtPosition(int start, int count) {
    detachViewFromParent(start, count);
    mOldItemCount = mItemCount;
    mItemCount -= count;
  }

  /**
   * Scrolls the items so that the selected item is in its 'slot' (its center
   * is the gallery's center).
   */
  private void scrollIntoSlots() {

    if (getChildCount() == 0 || mSelectedChild == null) {
      return;
    }

    if (!mIsCenterLocked) {
      onFinishedMovement();
      return;
    }

    int selectedCenter = getCenterOfView(mSelectedChild);
    int targetCenter = getCenterOfGallery();

    int scrollAmount = targetCenter - selectedCenter;
    if (scrollAmount != 0) {
      mFlingRunnable.startUsingDistance(scrollAmount);
    } else {
      onFinishedMovement();
    }
  }

  private void sendMoveStopedMsg() {
    if (mOnGalleryOperationListener != null && mCanSendMoveStop && !mIsPressedStatus) {
      mCanSendMoveStop = false;
      mOnGalleryOperationListener.onMoveStoped(this);
    }
  }

  private void onFinishedMovement() {
    if (mSuppressSelectionChanged) {
      mSuppressSelectionChanged = false;
      // We haven't been callbacking during the fling, so do it now
      super.selectionChanged();
    }

    mIgnoreTouchEvent = false;
    sendMoveStopedMsg();
    invalidate();
  }

  @Override void selectionChanged() {
    if (!mSuppressSelectionChanged) {
      super.selectionChanged();
    }
  }

  /**
   * Looks for the child that is closest to the center and sets it as the
   * selected child.
   */
  private void setSelectionToCenterChild() {
    View selView = mSelectedChild;
    int galleryCenter = getPaddingLeft();
    if (null != selView) {
      // Common case where the current selected position is correct
      if (selView.getLeft() <= galleryCenter && selView.getRight() >= galleryCenter) {
        return;
      }
    }

    // TODO better search
    int closestEdgeDistance = Integer.MAX_VALUE;
    int newSelectedChildIndex = 0;
    for (int i = getChildCount() - 1; i >= 0; i--) {

      View child = getChildAt(i);

      if (child.getLeft() <= galleryCenter && child.getRight() >= galleryCenter) {
        // This child is in the center
        newSelectedChildIndex = i;
        break;
      }

      int childClosestEdgeDistance = Math.min(Math.abs(child.getLeft() - galleryCenter),
          Math.abs(child.getRight() - galleryCenter));
      if (childClosestEdgeDistance < closestEdgeDistance) {
        closestEdgeDistance = childClosestEdgeDistance;
        newSelectedChildIndex = i;
      }
    }

    int newPos = mFirstPosition + newSelectedChildIndex;

    if (newPos != mSelectedPosition) {
      setSelectedPositionInt(newPos);
      setNextSelectedPositionInt(newPos);
      checkSelectionChanged();
    }
  }

  private int getLeftPaddingValue() {
    // VEGallery 是需要支持滚动的
    if (this instanceof VeAdvanceTrimGallery) {
      int width = getWidth();
      int itemWidth = getChildWidth();
      int count = getCount();
      if (width > 0 && count > 0 && itemWidth > 0 && count * itemWidth < width) {
        isIgnoreScrollEvent(true);
        return (width - count * itemWidth) / 2;
      }
    }
    isIgnoreScrollEvent(false);
    return 0;
  }

  /**
   * Creates and positions all views for this Gallery.
   * <p>
   * We layout rarely, most of the time  takes
   * care of repositioning, adding, and removing children.
   *
   * @param delta Change in the selected position. +1 means the selection is
   * moving to the right, so views are scrolling to the left. -1
   * means the selection is moving to the left.
   */

  @SuppressLint("WrongCall") @Override void layout(int delta, boolean animate) {
    int childrenLeft = mSpinnerPadding.left + getLeftPaddingValue();
    int childrenWidth = getRight() - getLeft() - mSpinnerPadding.left - mSpinnerPadding.right;
    int count = getCount();

    if (mDataChanged) {
      handleDataChanged();
    }

    // Handle an empty gallery by removing all views.
    if (mItemCount == 0) {
      resetList();
      mFirstPosition = 0;
      if (mOnLayoutListener != null) {
        mOnLayoutListener.onLayout(this);
      }
      return;
    }

    if (mSelectionIndexOnLayout >= 0) {
      mNextSelectedPosition = mSelectionIndexOnLayout;
    }
    // Update to the new selected position.
    if (mNextSelectedPosition >= 0) {
      setSelectedPositionInt(mNextSelectedPosition);
    }

    // All views go in recycler while we are in layout

    recycleAllViews();

    // Clear out old views
    // removeAllViewsInLayout();
    detachAllViewsFromParent();
    /*
     * These will be used to give initial positions to views entering the
     * gallery as we scroll
     */
    mRightMost = 0;
    mLeftMost = 0;

    // Make selected view and center it

    /*
     * mFirstPosition will be decreased as we add views to the left later
     * on. The 0 for x will be offset in a couple lines down.
     */

    mFirstPosition = mSelectedPosition;
    View sel = makeAndAddView(mSelectedPosition, 0, 0, true);
    if (sel != null) {
      // Put the selected child in the center
      if (mIsCenterLocked) {
        // int selectedOffset = childrenLeft + (childrenWidth / 2) -
        // (sel.getWidth() / 2);
        int selectedOffset = childrenLeft + (childrenWidth / 2);

        if (mIsAllowedIdlySpace || mCenterOffsetIndex <= 0) {
          sel.offsetLeftAndRight(selectedOffset);
        } else {
          if (mCenterOffsetIndex > 0) {

            if (mSelectedPosition >= mCenterOffsetIndex
                && mSelectedPosition < count - mCenterOffsetIndex
                && count >= mCenterOffsetIndex * 2 + 1) {
              sel.offsetLeftAndRight(selectedOffset);
            } else if (mSelectedPosition < mCenterOffsetIndex
                || count < mCenterOffsetIndex * 2 + 1) {
              selectedOffset = mChildWidth * mSelectedPosition;

              sel.offsetLeftAndRight(selectedOffset + getPaddingLeft());
            } else {
              int diff = mSelectedPosition - (count - mCenterOffsetIndex) + 1;

              if (diff > 0) {
                selectedOffset = mChildWidth * (mCenterOffsetIndex + diff);
                sel.offsetLeftAndRight(selectedOffset + getPaddingLeft());
              }
            }
          }
        }
      } else {
        if (mSelectionIndexOnLayout >= 0) {
          sel.offsetLeftAndRight(childrenLeft + mSelectionOffsetOnLayout);
        } else {
          sel.offsetLeftAndRight(childrenLeft);
        }
      }
    }

    if (mFillToCenter) {
      fillToGalleryCenter();
    } else {
      fillToGalleryRight();
      fillToGalleryLeft();
    }

    if (!mIgnoreLayoutRequest) {
      // Flush any cached views that did not get reused above
      mRecycler.clear();
    }
    if (mOnLayoutListener != null) {
      mOnLayoutListener.onLayout(this);
    }

    if (!mEnableLayout) {
      mSelectionIndexOnLayout = -1;
      mSelectionOffsetOnLayout = -1;
    }

    invalidate();
    checkSelectionChanged();

    mDataChanged = false;
    mNeedSync = false;
    setNextSelectedPositionInt(mSelectedPosition);

    updateSelectedItemMetadata();
  }

  /**
   * Creates and positions all views for this Gallery.
   * <p>
   * We layout rarely, most of the time  takes
   * care of repositioning, adding, and removing children.
   *
   * @param delta Change in the selected position. +1 means the selection is
   * moving to the right, so views are scrolling to the left. -1
   * means the selection is moving to the left.
   */
  // @Override
  void layout2(int delta, boolean animate) {
    mFirstPosition = getFirstVisiblePosition();
    int mLastPosition = getLastVisiblePosition();
    if (mLastPosition > mFirstPosition && mLastPosition < mNextSelectedPosition) {
      mFirstPosition = mFirstPosition + mNextSelectedPosition - mLastPosition;
    }

    if (!isInEditMode() && mFirstPosition != mClientFocusIndex && mClientFocusIndex == 0) {
      mFirstPosition = mClientFocusIndex;
    }

    if (mDataChanged) {
      handleDataChanged();
    }

    // Handle an empty gallery by removing all views.
    if (mItemCount == 0) {
      resetList();
      return;
    }

    // Update to the new selected position.
    if (mNextSelectedPosition >= 0) {
      setSelectedPositionInt(mNextSelectedPosition);
    }

    // All views go in recycler while we are in layout
    recycleAllViews();

    // Clear out old views
    // removeAllViewsInLayout();
    detachAllViewsFromParent();

    /*
     * These will be used to give initial positions to views entering the
     * gallery as we scroll
     */
    mRightMost = 0;
    mLeftMost = 0;

    // Make selected view and center it

    /*
     * mFirstPosition will be decreased as we add views to the left later
     * on. The 0 for x will be offset in a couple lines down.
     */
    // mFirstPosition = mSelectedPosition;
    View sel = makeAndAddView(mFirstPosition, 0, 0, true);

    // Put the selected child in the center
    // int selectedOffset = childrenLeft + (childrenWidth / 2) -
    // (sel.getWidth() / 2);
    // sel.offsetLeftAndRight(selectedOffset);

    fillToGalleryRight();
    fillToGalleryLeft();

    // Flush any cached views that did not get reused above
    mRecycler.clear();

    invalidate();
    checkSelectionChanged();

    mDataChanged = false;
    mNeedSync = false;
    setNextSelectedPositionInt(mSelectedPosition);

    updateSelectedItemMetadata();
  }

  public void fillToGalleryCenter() {
    int itemSpacing = mSpacing;
    int galleryRight = getRight() - getLeft() - getPaddingRight();
    int numChildren = getChildCount();
    int numItems = mItemCount;

    if (!mLeftToCenter) {
      View prevIterationView = getChildAt(0);
      int curPosition = mFirstPosition - 1;
      int curLeftEdge = getCenterOfGallery() - mLeftToCenterOffset;
      int curRightEdge = curLeftEdge + prevIterationView.getWidth();
      prevIterationView.offsetLeftAndRight(curLeftEdge - prevIterationView.getLeft());

      while (curPosition >= 0) {
        prevIterationView =
            makeAndAddView(curPosition, curPosition - mSelectedPosition, curRightEdge, false);
        if (prevIterationView != null) {
          if (prevIterationView.getLeft() != curLeftEdge) {
            int offset = curLeftEdge - prevIterationView.getLeft();
            curRightEdge += offset;
            prevIterationView.offsetLeftAndRight(offset);
          }
        }
        mFirstPosition = curPosition;
        curPosition--;
      }

      curPosition = mSelectedPosition + 1;
      while (curPosition < numItems) {
        prevIterationView =
            makeAndAddView(curPosition, curPosition - mSelectedPosition, curLeftEdge, true);
        curPosition++;
      }
      return;
    }

    // Set state for initial iteration
    View prevIterationView = getChildAt(numChildren - 1);
    int curPosition;
    int curLeftEdge;
    int curRightEdge;

    if (prevIterationView != null) {
      curPosition = mFirstPosition + numChildren;
      curLeftEdge = prevIterationView.getLeft();
      curRightEdge = prevIterationView.getRight() + itemSpacing;
    } else {
      // mFirstPosition = curPosition = mItemCount - 1;
      curPosition = mFirstPosition + numChildren;
      curLeftEdge = getPaddingLeft();
      curRightEdge = galleryRight;
      mShouldStopFling = true;
    }

    while (curPosition < numItems && curRightEdge < galleryRight) {
      prevIterationView =
          makeAndAddView(curPosition, curPosition - mSelectedPosition, curLeftEdge, true);

      // Set state for next iteration
      // curLeftEdge = prevIterationView.getRight() + itemSpacing;
      if (prevIterationView != null) {
        curRightEdge += prevIterationView.getWidth() + itemSpacing;
        curPosition++;
      }
    }
  }

  public void fillToGalleryLeft() {
    int itemSpacing = mSpacing;
    int galleryLeft = getPaddingLeft();

    // Set state for initial iteration
    View prevIterationView = getChildAt(0);
    int curPosition;
    int curRightEdge;

    if (prevIterationView != null) {
      curPosition = mFirstPosition - 1;
      curRightEdge = prevIterationView.getLeft() - itemSpacing;
    } else {
      // No children available!
      curPosition = mFirstPosition - 1;
      curRightEdge = getRight() - getLeft() - getPaddingRight();
      mShouldStopFling = true;
    }

    while (curRightEdge > galleryLeft && curPosition >= 0) {
      prevIterationView =
          makeAndAddView(curPosition, curPosition - mSelectedPosition, curRightEdge, false);
      if (prevIterationView != null) {
        // Remember some state
        mFirstPosition = curPosition;
        // Set state for next iteration
        curRightEdge = prevIterationView.getLeft() - itemSpacing;
        curPosition--;
      }
    }
  }

  public void fillToGalleryRight() {
    int itemSpacing = mSpacing;
    int galleryRight = getRight() - getLeft() - getPaddingRight();
    int numChildren = getChildCount();
    int numItems = mItemCount;

    // Set state for initial iteration
    View prevIterationView = getChildAt(numChildren - 1);
    int curPosition;
    int curLeftEdge;

    if (prevIterationView != null) {
      curPosition = mFirstPosition + numChildren;
      curLeftEdge = prevIterationView.getRight() + itemSpacing;
    } else {
      // mFirstPosition = curPosition = mItemCount - 1;
      curPosition = mFirstPosition + numChildren;
      curLeftEdge = getPaddingLeft();
      mShouldStopFling = true;
    }

    while (curLeftEdge < galleryRight && curPosition < numItems) {
      prevIterationView =
          makeAndAddView(curPosition, curPosition - mSelectedPosition, curLeftEdge, true);
      if (prevIterationView != null) {
        // Set state for next iteration
        curLeftEdge = prevIterationView.getRight() + itemSpacing;
        curPosition++;
      }
    }
  }

  /**
   * Obtain a view, either by pulling an existing view from the recycler or by
   * getting a new one from the adapter. If we are animating, make sure there
   * is enough information in the view's layout parameters to animate from the
   * old to new positions.
   *
   * @param position Position in the gallery for the view to obtain
   * @param offset Offset from the selected position
   * @param x X-coordintate indicating where this view should be placed.
   * This will either be the left or right edge of the view,
   * depending on the fromLeft paramter
   * @param fromLeft Are we posiitoning views based on the left edge? (i.e.,
   * building from left to right)?
   * @return A view that has been added to the gallery
   */
  private View makeAndAddView(int position, int offset, int x, boolean fromLeft) {

    View child = null;

    if (!mDataChanged && position != getSelectedItemPosition() && (mOldItemCount
        <= mItemCount)) {
      child = mRecycler.get(position);
      if (child != null) {
        // Can reuse an existing view
        int childLeft = child.getLeft();

        // Remember left and right edges of where views have been placed
        mRightMost = Math.max(mRightMost, childLeft + child.getMeasuredWidth());
        mLeftMost = Math.min(mLeftMost, childLeft);
        // Position the view
        setUpChild(child, offset, x, fromLeft);

        return child;
      }
    }
    if (mAdapter != null) {
      // Nothing found in the recycler -- ask the adapter for a view
      child = mAdapter.getView(position, null, this);
      // Position the view
      setUpChild(child, offset, x, fromLeft);
    }

    return child;
  }

  /**
   * Helper for makeAndAddView to set the position of a view and fill out its
   * layout paramters.
   *
   * @param child The view to position
   * @param offset Offset from the selected position
   * @param x X-coordintate indicating where this view should be placed.
   * This will either be the left or right edge of the view,
   * depending on the fromLeft paramter
   * @param fromLeft Are we posiitoning views based on the left edge? (i.e.,
   * building from left to right)?
   */
  private void setUpChild(View child, int offset, int x, boolean fromLeft) {

    // Respect layout params that are already in the view. Otherwise
    // make some up...
    LayoutParams lp = (LayoutParams) child.getLayoutParams();
    if (lp == null) {
      lp = (LayoutParams) generateDefaultLayoutParams();
    }

    addViewInLayout(child, fromLeft ? -1 : 0, lp);

    child.setSelected(offset == 0);

    // Get measure specs
    int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec,
        mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
    int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
        mSpinnerPadding.left + mSpinnerPadding.right, lp.width);

    // Measure child
    child.measure(childWidthSpec, childHeightSpec);

    int childLeft;
    int childRight;

    // Position vertically based on gravity setting
    int childTop = calculateTop(child, true);
    int childBottom = childTop + child.getMeasuredHeight();

    int width = child.getMeasuredWidth();
    if (fromLeft) {
      childLeft = x;
      childRight = childLeft + width;
    } else {
      childLeft = x - width;
      childRight = x;
    }

    child.layout(childLeft, childTop, childRight, childBottom);
  }

  /**
   * Figure out vertical placement based on mGravity
   *
   * @param child Child to place
   * @return Where the top of the child should be
   */
  public int calculateTop(View child, boolean duringLayout) {
    int myHeight = duringLayout ? getMeasuredHeight() : getHeight();
    int childHeight = duringLayout ? child.getMeasuredHeight() : child.getHeight();

    int childTop = 0;

    switch (mGravity) {
      case Gravity.TOP:
        childTop = mSpinnerPadding.top;
        break;
      case Gravity.CENTER_VERTICAL:
        int availableSpace =
            myHeight - mSpinnerPadding.bottom - mSpinnerPadding.top - childHeight;
        childTop = mSpinnerPadding.top + (availableSpace / 2);
        break;
      case Gravity.BOTTOM:
        childTop = myHeight - mSpinnerPadding.bottom - childHeight;
        break;
    }
    return childTop;
  }

  public void disallowChildInterceptOnMoving(boolean disallowIntercept) {
    mDisallowChildInterceptOnMoving = disallowIntercept;
  }

  public void enableTouchEvent(boolean enable) {
    mEnableTouchEvent = enable;
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    // mGestureDetector.setIsLongpressEnabled(true);
    if (!mEnableTouchEvent
        && ev.getAction() != MotionEvent.ACTION_UP
        && ev.getAction() != MotionEvent.ACTION_CANCEL) {
      return true;
    }

    final int action = ev.getAction();

    if (action == MotionEvent.ACTION_DOWN) {
      if (mOnGalleryOperationListener != null) {
        mOnGalleryOperationListener.onDown();
      }
      mLastDownTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
    }

    if (mDisallowChildInterceptOnMoving) {

      final float y = ev.getY();
      final float x = ev.getX();

      if (action == MotionEvent.ACTION_DOWN) {
        mCurrentDownEvent = MotionEvent.obtain(ev);
        mHaveMotionTarget = true;
        mInterceptTouchEvent = false;
        requestDisallowInterceptTouchEvent(true);
      } else if (action == MotionEvent.ACTION_MOVE && mHaveMotionTarget) {

        if (mCurrentDownEvent != null) {
          final int deltaX = (int) (x - mCurrentDownEvent.getX());
          final int deltaY = (int) (y - mCurrentDownEvent.getY());
          int distance = (deltaX * deltaX) + (deltaY * deltaY);

          if (distance > mTouchSlopSquare) {
            mInterceptTouchEvent = true;
            requestDisallowInterceptTouchEvent(false);
            super.dispatchTouchEvent(ev);
            onTouchEvent(mCurrentDownEvent);
            mCurrentDownEvent = null;
          }
        }
      }
    }

    return upActionProcess(ev, action);
  }

  private boolean upActionProcess(MotionEvent ev, final int action) {
    boolean ret = super.dispatchTouchEvent(ev);
    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
      if (mOnGalleryOperationListener != null) {
        mOnGalleryOperationListener.onUp();
      }
    }
    return ret;
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    return mInterceptTouchEvent;
  }

  public void setInterceptTouchEvent(boolean flag) {
    mInterceptTouchEvent = flag;
  }

  public void enableMoutichTouchEvent(boolean enableMoutichTouchEvent) {
    mEnableMoutichTouchEvent = enableMoutichTouchEvent;
  }

  @Override public boolean onTouchEvent(MotionEvent event) {

    // Matrix matrix = new Matrix();
    // Matrix savedMatrix = new Matrix();
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist;
    // We can be in one of these 3 states

    if (mIgnoreTouchEvent
        && event.getAction() != MotionEvent.ACTION_UP
        && event.getAction() != MotionEvent.ACTION_CANCEL) {
      return true;
    }

    // Give everything to the gesture detector
    boolean retValue = false;
    if (mTouchMode != ZOOM) {
      retValue = mGestureDetector.onTouchEvent(event);
    }

    int action = event.getAction() & MotionEvent.ACTION_MASK;

    if (action == MotionEvent.ACTION_DOWN) {
      mHaveMotionTarget = false;
      start.set(event.getX(), event.getY());
      mTouchMode = DRAG;
    } else if (action == MotionEvent.ACTION_POINTER_DOWN && mEnableMoutichTouchEvent) {
      if (!mCanSendMoveStop) {
        oldDist = spacing(event);
        midPoint(mid, event);
        mTouchMode = ZOOM;
        removeLongClickMessages();

        if (mOnPinchZoomGestureListener != null) {
          mOnPinchZoomGestureListener.onStartPinchZoom(oldDist);
        }
        retValue = true;
      }
    } else if (action == MotionEvent.ACTION_MOVE) {
      if (mTouchMode == ZOOM) {
        if (event.getPointerCount() >= 2) {
          float newDist = spacing(event);

          if (mOnPinchZoomGestureListener != null) {
            mOnPinchZoomGestureListener.onZoomChanged(newDist);
          }
        }
        retValue = true;
      }
    } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
      // Helper method for lifted finger
      // if(mTouchMode != ZOOM)
      if (action == MotionEvent.ACTION_UP) {
        onUp();
      }

      if (action == MotionEvent.ACTION_POINTER_UP
          && mEnableMoutichTouchEvent
          && mTouchMode == ZOOM
          && mOnPinchZoomGestureListener != null) {
        retValue = true;
        mOnPinchZoomGestureListener.onStopPinchZoom();
      }
      if (action == MotionEvent.ACTION_UP) {
        mTouchMode = NONE;
      }
    } else if (action == MotionEvent.ACTION_CANCEL) {
      onCancel();
      mTouchMode = NONE;
    }

    return retValue;
  }

  private float spacing(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return (float) Math.sqrt(x * x + y * y);
  }

  private void midPoint(PointF point, MotionEvent event) {
    float x = event.getX(0) + event.getX(1);
    float y = event.getY(0) + event.getY(1);
    point.set(x / 2, y / 2);
  }

  protected boolean onSingleTap(MotionEvent e) {
    if (mDownTouchPosition >= 0) {

      if (mApplySingleTap) {
        // An item tap should make it selected, so scroll to this child.
        scrollToChild(mDownTouchPosition - mFirstPosition);
      }
      // Also pass the click so the client knows, if it wants to.
      if (mShouldCallbackOnUnselectedItemClick || mDownTouchPosition == mSelectedPosition) {
        performItemClick(mDownTouchView, mDownTouchPosition,
            mAdapter.getItemId(mDownTouchPosition));
      }

      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean onSingleTapUp(MotionEvent e) {
    return mOnGalleryDoubleTapListener == null && onSingleTap(e);
  }

  public void sendItemClick(View touchView, int position, long id) {
    performItemClick(touchView, position, id);
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
      float velocityY) {

    if (mIgnoreScrollEvent || mTouchMode == ZOOM) {
      return true;
    }

    if (!mShouldCallbackDuringFling) {
      // We want to suppress selection changes

      // Remove any future code to set mSuppressSelectionChanged = false
      removeCallbacks(mDisableSuppressSelectionChangedRunnable);

      // This will get reset once we scroll into slots
      if (!mSuppressSelectionChanged) {
        mSuppressSelectionChanged = true;
      }
    }

    // Fling the gallery!
    mFlingRunnable.startUsingVelocity((int) -velocityX);

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
      float distanceY) {

    if (localLOGV) {
      Log.i(TAG, String.valueOf(e2.getX() - e1.getX()));
    }

    /*
     * Now's a good time to tell our parent to stop intercepting our events!
     * The user has moved more than the slop amount, since GestureDetector
     * ensures this before calling this method. Also, if a parent is more
     * interested in this touch's events than we are, it would have
     * intercepted them by now (for example, we can assume when a Gallery is
     * in the ListView, a vertical scroll would not end up in this method
     * since a ListView would have intercepted it by now).
     */
    if (mIgnoreScrollEvent || mTouchMode == ZOOM) {
      return true;
    }

    getParent().requestDisallowInterceptTouchEvent(true);

    // As the user scrolls, we want to callback selection changes so
    // related-
    // info on the screen is up-to-date with the gallery's selection
    if (!mShouldCallbackDuringFling) {
      if (mIsFirstScroll) {
        /*
         * We're not notifying the client of selection changes during
         * the fling, and this scroll could possibly be a fling. Don't
         * do selection changes until we're sure it is not a fling.
         */
        if (!mSuppressSelectionChanged) {
          mSuppressSelectionChanged = true;
        }
        postDelayed(mDisableSuppressSelectionChangedRunnable,
            SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT);
      }
    } else {
      if (mSuppressSelectionChanged) {
        mSuppressSelectionChanged = false;
      }
    }

    // Track the motion
    trackMotionScroll(-1 * (int) distanceX, true);

    // mIsFirstScroll = false;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean onDown(MotionEvent e) {
    if (mIgnoreTouchEvent) {
      return true;
    }
    // Kill any existing fling/scroll
    mFlingRunnable.stop(false);

    sendMoveStopedMsg();

    mLastDownTouchPosition = mDownTouchPosition;
    // Get the item's view that was touched
    mDownTouchPosition = pointToPosition((int) e.getX(), (int) e.getY());
    if (mDownTouchPosition >= 0) {

      mDownTouchView = getChildAt(mDownTouchPosition - mFirstPosition);
      if (mDispatchPressed) {
        mDownTouchView.setPressed(true);
      }
    } else {
      removeLongClickMessages();
    }

    // Reset the multiple-scroll tracking state
    mIsFirstScroll = true;
    mIsPressedStatus = true;
    mCanSendMoveStop = false;
    // Must return true to get matching events for this down event.
    return true;
  }

  public void removeLongClickMessages() {
    try {
      Class cls = null;
      try {
        cls = Class.forName("android.view.GestureDetector");
      } catch (ClassNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      if (cls != null) {
        Field flagHandle = cls.getDeclaredField("mHandler");
        Field flagLongPress = cls.getDeclaredField("LONG_PRESS");
        flagHandle.setAccessible(true);
        flagLongPress.setAccessible(true);
        try {
          Object objHandle = flagHandle.get(mGestureDetector);
          int longPress = flagLongPress.getInt(mGestureDetector);

          if (objHandle instanceof Handler) {
            Handler handle = (Handler) objHandle;
            handle.removeMessages(longPress);
          }
        } catch (IllegalArgumentException err) {
          // TODO Auto-generated catch block
          err.printStackTrace();
        } catch (IllegalAccessException err) {
          // TODO Auto-generated catch block
          err.printStackTrace();
        }
      }
    } catch (SecurityException err) {
      // TODO Auto-generated catch block
      err.printStackTrace();
    } catch (NoSuchFieldException err) {
      // TODO Auto-generated catch block
      err.printStackTrace();
    }
  }

  /**
   * Called when a touch event's action is MotionEvent.ACTION_UP.
   */
  void onUp() {
    mIsPressedStatus = false;

    if (mFlingRunnable.mScroller.isFinished()) {
      scrollIntoSlots();
    }

    dispatchUnpress();
  }

  /**
   * Called when a touch event's action is MotionEvent.ACTION_CANCEL.
   */
  void onCancel() {
    onUp();
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onLongPress(MotionEvent e) {
    try {
      if (mDownTouchPosition < 0) {
        return;
      }

      performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
      long id = getItemIdAtPosition(mDownTouchPosition);
      dispatchLongPress(mDownTouchView, mDownTouchPosition, id);
    } catch (Exception ex) {
      Log.e(TAG, "Exception message:" + ex.getMessage());
    }
  }

  // Unused methods from GestureDetector.OnGestureListener below

  /**
   * {@inheritDoc}
   */
  @Override public void onShowPress(MotionEvent e) {
  }

  // Unused methods from GestureDetector.OnGestureListener above

  private void dispatchPress(View child) {

    if (!mDispatchPressed) {
      return;
    }
    if (child != null) {
      child.setPressed(true);
    }

    setPressed(true);
  }

  private void dispatchUnpress() {
    if (!mDispatchPressed) {
      return;
    }
    for (int i = getChildCount() - 1; i >= 0; i--) {
      getChildAt(i).setPressed(false);
    }

    setPressed(false);
  }

  public void isDispatchPress(boolean isDispatch) {
    mDispatchPressed = isDispatch;
  }

  @SuppressLint("WrongCall") @Override protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    if (mOnGalleryDrawListener != null) {
      mOnGalleryDrawListener.onDraw(canvas);
    }
  }

  @Override public void dispatchSetSelected(boolean selected) {
    /*
     * We don't want to pass the selected state given from its parent to its
     * children since this widget itself has a selected state to give to its
     * children.
     */
  }

  @Override protected void dispatchSetPressed(boolean pressed) {

    // Show the pressed state on the selected child
    if (mSelectedChild != null) {
      mSelectedChild.setPressed(pressed);
    }
  }

  @Override protected ContextMenuInfo getContextMenuInfo() {
    return mContextMenuInfo;
  }

  @Override public boolean showContextMenuForChild(View originalView) {

    final int longPressPosition = getPositionForView(originalView);
    if (longPressPosition < 0) {
      return false;
    }

    final long longPressId = mAdapter.getItemId(longPressPosition);
    return dispatchLongPress(originalView, longPressPosition, longPressId);
  }

  @Override public boolean showContextMenu() {

    if (isPressed() && mSelectedPosition >= 0) {
      int index = mSelectedPosition - mFirstPosition;
      View v = getChildAt(index);
      return dispatchLongPress(v, mSelectedPosition, mSelectedRowId);
    }

    return false;
  }

  private boolean dispatchLongPress(View view, int position, long id) {
    boolean handled = false;

    if (mOnItemLongClickListener != null) {
      handled =
          mOnItemLongClickListener.onItemLongClick(this, mDownTouchView, mDownTouchPosition, id);
    }

    if (!handled) {
      mContextMenuInfo = new AdapterContextMenuInfo(view, position, id);
      handled = super.showContextMenuForChild(this);
    }

    if (handled) {
      performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    return handled;
  }

  @Override public boolean dispatchKeyEvent(KeyEvent event) {
    // Gallery steals all key events
    if (mHookAllKeyEvent) {
      return event.dispatch(this);
    } else {
      return super.dispatchKeyEvent(event);
    }
  }

  public void isHookAllKeyEvent(boolean isHook) {
    mHookAllKeyEvent = isHook;
  }

  /**
   * Handles left, right, and clicking
   *
   * @see View#onKeyDown
   */
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {

      case KeyEvent.KEYCODE_DPAD_LEFT:
        if (movePrevious()) {
          playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
        }
        return true;

      case KeyEvent.KEYCODE_DPAD_RIGHT:
        if (moveNext()) {
          playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT);
        }
        return true;

      case KeyEvent.KEYCODE_DPAD_CENTER:
      case KeyEvent.KEYCODE_ENTER:
        mReceivedInvokeKeyDown = true;
        // fallthrough to default handling
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_DPAD_CENTER:
      case KeyEvent.KEYCODE_ENTER: {

        if (mReceivedInvokeKeyDown) {
          if (mItemCount > 0) {

            dispatchPress(mSelectedChild);
            postDelayed(new Runnable() {
              @Override public void run() {
                dispatchUnpress();
              }
            }, ViewConfiguration.getPressedStateDuration());

            int selectedIndex = mSelectedPosition - mFirstPosition;
            performItemClick(getChildAt(selectedIndex), mSelectedPosition,
                mAdapter.getItemId(mSelectedPosition));
          }
        }

        // Clear the flag
        mReceivedInvokeKeyDown = false;

        return true;
      }
    }

    return super.onKeyUp(keyCode, event);
  }

  boolean movePrevious() {
    if (mItemCount > 0 && mSelectedPosition > 0) {
      scrollToChild(mSelectedPosition - mFirstPosition - 1);
      return true;
    } else {
      return false;
    }
  }

  boolean moveNext() {
    if (mItemCount > 0 && mSelectedPosition < mItemCount - 1) {
      scrollToChild(mSelectedPosition - mFirstPosition + 1);
      return true;
    } else {
      return false;
    }
  }

  private boolean scrollToChild(int childPosition) {
    View child = getChildAt(childPosition);
    if (child != null) {
      int distance = getCenterOfGallery() - getCenterOfView(child);
      mFlingRunnable.startUsingDistance(distance);
      return true;
    }

    return false;
  }

  @Override void setSelectedPositionInt(int position) {
    super.setSelectedPositionInt(position);

    // Updates any metadata we keep about the selected item.
    updateSelectedItemMetadata();
  }

  private void updateSelectedItemMetadata() {

    View oldSelectedChild = mSelectedChild;

    View child = mSelectedChild = getChildAt(mSelectedPosition - mFirstPosition);
    if (child == null) {
      return;
    }

    child.setSelected(true);
    child.setFocusable(true);

    if (hasFocus()) {
      child.requestFocus();
    }

    // We unfocus the old child down here so the above hasFocus check
    // returns true
    if (oldSelectedChild != null) {

      // Make sure its drawable state doesn't contain 'selected'
      oldSelectedChild.setSelected(false);

      // Make sure it is not focusable anymore, since otherwise arrow keys
      // can make this one be focused
      oldSelectedChild.setFocusable(false);
    }
  }

  /**
   * Describes how the child views are aligned.
   *
   * @attr ref android.R.styleable#Gallery_gravity
   */
  public final void setGravity(int gravity) {
    if (mGravity != gravity) {
      mGravity = gravity;
      requestLayout();
    }
  }

  @Override protected int getChildDrawingOrder(int childCount, int i) {
    int selectedIndex = mSelectedPosition - mFirstPosition;

    // Just to be safe
    if (selectedIndex < 0) {
      return i;
    }

    if (i == childCount - 1) {
      // Draw the selected child last
      return selectedIndex;
    } else if (i >= selectedIndex) {
      // Move the children to the right of the selected child earlier one
      return i + 1;
    } else {
      // Keep the children to the left of the selected child the same
      return i;
    }
  }

  @Override
  protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

    /*
     * The gallery shows focus by focusing the selected item. So, give focus
     * to our selected item instead. We steal keys from our selected item
     * elsewhere.
     */
    if (gainFocus && mSelectedChild != null) {
      mSelectedChild.requestFocus(direction);
    }
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int galleryWidth = getMeasuredWidth();

    if (!mIsCenterLocked) {
      return;
    }

    if (!mIsAllowedIdlySpace && mChildWidth > 0) {
      mCenterOffsetIndex = galleryWidth / mChildWidth / 2;
    }
  }

  @Override public void setSelection(int position) {
    if (position < 0) {
      position = 0;
    }
    if (position > getCount() - 1) {
      position = getCount() - 1;
    }

    super.setSelection(position);
  }

  @Override public void setSelection(int position, boolean animate) {
    if (position < 0) {
      position = 0;
    }
    if (position > getCount() - 1) {
      position = getCount() - 1;
    }

    super.setSelection(position, animate);
  }

  /**
   * layout时 gallery滚动offset
   */
  public void setSelectionInfoOnLayout(int selectionIndex, int selectionOffset) {
    mSelectionIndexOnLayout = selectionIndex;
    mSelectionOffsetOnLayout = selectionOffset;
  }

  public void forceEndFling(boolean scrollIntoSlots) {
    mFlingRunnable.endFling(scrollIntoSlots);
  }

  public void setChildWidth(int iChildWidth) {
    mChildWidth = iChildWidth;
  }

  public int getChildWidth() {
    return mChildWidth;
  }

  public void setLimitMoveOffset(int leftOffset, int rightOffset) {
    mLeftLimitMoveOffset = leftOffset;
    mRightLimitMoveOffset = rightOffset;
  }

  public int scroll(int distanceX) {
    if (mIgnoreScrollEvent) {
      return 0;
    }

    return trackMotionScroll(distanceX, false);
  }

  public void startScroll(int distanceX, boolean ignoreTouchEvent) {
    if (distanceX == 0 || mIgnoreTouchEvent) {
      return;
    }

    mIgnoreTouchEvent = ignoreTouchEvent;
    if (!mIsFirstScroll) {
      mIsFirstScroll = true;
    }
    mFlingRunnable.startUsingDistance(distanceX);
  }

  public void startScrollUsingVelocity(int distanceX, boolean ignoreTouchEvent) {
    if (distanceX == 0 || mIgnoreTouchEvent) {
      return;
    }

    mIgnoreTouchEvent = ignoreTouchEvent;
    if (!mIsFirstScroll) {
      mIsFirstScroll = true;
    }
    mFlingRunnable.startUsingVelocity(distanceX);
  }

  public boolean isFling() {

    return mFlingRunnable.isFling();
  }

  public void isIgnoreScrollEvent(boolean ignore) {
    mIgnoreScrollEvent = ignore;
  }

  public boolean isIgoneScrollEvent() {
    return mIgnoreScrollEvent;
  }

  public void isIgnoreLayoutRequest(boolean ignore) {
    mIgnoreLayoutRequest = ignore;
  }

  public void isCenterLocked(boolean locked) {
    mIsCenterLocked = locked;
  }

  public void isAllowedIdlySpaceOnEnds(boolean allowed) {
    mIsAllowedIdlySpace = allowed;
  }

  public void applySingleTap(boolean applySingleTap) {
    mApplySingleTap = applySingleTap;
  }

  public boolean isInPressedStatus() {
    return mIsPressedStatus;
  }

  public void setIsLongpressEnabled(boolean enable) {
    mGestureDetector.setIsLongpressEnabled(enable);
  }

  /**
   * Responsible for fling behavior. Use {@link #startUsingVelocity(int)} to
   * initiate a fling. Each frame of the fling is handled in {@link #run()}. A
   * FlingRunnable will keep re-posting itself until the fling is done.
   */
  private class FlingRunnable implements Runnable {
    /**
     * Tracks the decay of a fling scroll
     */
    private final Scroller mScroller;

    /**
     * X value reported by mScroller on the previous fling
     */
    private int mLastFlingX;

    private boolean mIsMore = false;

    public FlingRunnable() {
      mScroller = new Scroller(getContext());
    }

    private void startCommon() {
      // Remove any pending flings
      removeCallbacks(this);
    }

    public void startUsingVelocity(int initialVelocity) {
      if (initialVelocity == 0) {
        return;
      }

      startCommon();

      int initialX = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
      mLastFlingX = initialX;
      mScroller.fling(initialX, 0, initialVelocity, 0, 0, Integer.MAX_VALUE, 0,
          Integer.MAX_VALUE);
      post(this);
    }

    public void startUsingDistance(int distance) {
      if (distance == 0) {
        return;
      }

      startCommon();
      mLastFlingX = 0;
      mScroller.startScroll(0, 0, -distance, 0, mAnimationDuration);
      post(this);
    }

    public boolean isFling() {
      return !mScroller.isFinished() || mScroller.computeScrollOffset() || mIsMore;
    }

    public void stop(boolean scrollIntoSlots) {
      removeCallbacks(this);
      endFling(scrollIntoSlots);
    }

    private void endFling(boolean scrollIntoSlots) {
      /*
       * Force the scroller's status to finished (without setting its
       * position to the end)
       */
      mIsMore = false;
      mIgnoreTouchEvent = false;
      mScroller.forceFinished(true);
      if (scrollIntoSlots) {
        scrollIntoSlots();
      }
    }

    @Override public void run() {

      if (mItemCount == 0) {
        endFling(true);
        return;
      }

      mShouldStopFling = false;

      final Scroller scroller = mScroller;
      boolean more = scroller.computeScrollOffset();
      final int x = scroller.getCurrX();

      mIsMore = more;
      // Flip sign to convert finger direction to list items direction
      // (e.g. finger moving down means list is moving towards the top)
      int delta = mLastFlingX - x;

      // Pretend that each frame of a fling scroll is a touch scroll
      if (delta > 0) {
        // Moving towards the left. Use first view as mDownTouchPosition
        mDownTouchPosition = mFirstPosition;

        // Don't fling more than 1 screen
        delta = Math.min(getWidth() - getPaddingLeft() - getPaddingRight() - 1, delta);
      } else {
        // Moving towards the right. Use last view as mDownTouchPosition
        int offsetToLast = getChildCount() - 1;
        mDownTouchPosition = mFirstPosition + offsetToLast;

        // Don't fling more than 1 screen
        delta = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), delta);
      }

      trackMotionScroll(delta, true);

      if (more && !mShouldStopFling) {
        mLastFlingX = x;
        post(this);
      } else {
        endFling(true);
      }
    }
  }

  public void startAutoScroll(boolean toLeft) {
    mGalleryAutoScrollRunnable.start(toLeft);
  }

  public void stopAutoScroll() {
    mGalleryAutoScrollRunnable.stop();
  }

  public boolean isAutoScrollStarted() {
    return mGalleryAutoScrollRunnable.isStarted();
  }

  private class GalleryAutoScrollRunnable implements Runnable {
    private boolean mScrollToLeft = false;
    private boolean mScroll = false;

    public GalleryAutoScrollRunnable() {

    }

    private void startCommon() {
      removeCallbacks(this);
    }

    public void start(boolean toLeft) {

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
      int scrollDistance = mChildWidth;
      int scroll;

      if (mScrollToLeft) {
        scroll = scroll(-scrollDistance);
      } else {
        scroll = scroll(scrollDistance);
      }

      if (mScroll) {
        startScroll(scroll, true);
        stop();
      }

      // if(scroll != 0){
      // if (mScroll) {
      // postDelayed(this, 50);
      // }
      // } else {
      // stop();
      // }

    }
  }

  /**
   * Gallery extends LayoutParams to provide a place to hold current
   * Transformation information along with previous position/transformation
   * info.
   */
  public static class LayoutParams extends ViewGroup.LayoutParams {
    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int w, int h) {
      super(w, h);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }
  }

  public void setFillToCenter(boolean fillToCenter) {
    mFillToCenter = fillToCenter;
  }

  public boolean getFillToCenter() {
    return mFillToCenter;
  }

  public void setLeftToCenter(boolean leftToCenter) {
    mLeftToCenter = leftToCenter;
  }

  public boolean getLeftToCenter() {
    return mLeftToCenter;
  }

  public void setLeftToCenterOffset(int offset) {
    mLeftToCenterOffset = offset;
  }

  public int getRightLimitMoveOffset() {
    return mRightLimitMoveOffset;
  }

  /**
   * @return timeLine左边的offset
   */
  public int getLeftLimitMoveOffset() {
    return mLeftLimitMoveOffset;
  }

  public void setmOnGalleryChildTouchedListener(
      OnTouchListener mOnGalleryChildTouchedListener) {
    this.mOnGalleryChildTouchedListener = mOnGalleryChildTouchedListener;
  }

  public int getmClientFocusIndex() {
    return mClientFocusIndex;
  }

  public void setmClientFocusIndex(int mClientFocusIndex) {
    this.mClientFocusIndex = mClientFocusIndex;
  }

  public int getmLastDownTouchPosition() {
    return mLastDownTouchPosition;
  }

  public int getmDownTouchPosition() {
    return mDownTouchPosition;
  }

  public void scrollToLastPosition(int lastIndex) {
    View child = getChildAt(0);
    if (child == null) {
      return;
    }
    int lastpos = getLastVisiblePosition();
    if (lastpos < lastIndex) {
      // move to left
      int distanceX = (lastIndex - lastpos) * child.getWidth();
      while (getWidth() < Math.abs(distanceX)) {
        scroll(-getWidth());
        distanceX = distanceX - getWidth();
      }
      scroll(-distanceX);
    }
  }
}
