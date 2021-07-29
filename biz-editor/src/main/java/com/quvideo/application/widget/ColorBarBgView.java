package com.quvideo.application.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.quvideo.application.DPUtils;
import com.quvideo.application.editor.R;

public class ColorBarBgView extends View {
  public interface ColorType {
    int TEXT = 1;
    int TEXT_SHADOW = 2;
  }

  public static final int CURR_DEF_COLOR = 0xffffffff;

  private static final int STATUS_INIT = 0;
  /**
   * 移动了action bar
   */
  private static final int STATUS_SEEK = 1;
  private static int STATUS;
  /**
   * 颜色列表的类型
   * 分两种：\
   * TEXT：文字SeekBar颜色快列表
   * BG：镜头背景SeekBar颜色快列表
   */
  public static int[] TEXT_COLORS = new int[] {
      0xfffefffe, 0xff999999, 0xff333333, 0xff000000, 0xffa90116, 0xffec001c, 0xffff441c,
      0xffff8514, 0xffffbd18, 0xfffff013, 0xffadd321, 0xff23c203, 0xff007f23, 0xff0ce397,
      0xff06a998, 0xff00d0fe, 0xff1975ff, 0xff2c2ad4, 0xff4a07b7, 0xff7621ff, 0xffb52fe3,
      0xffff5ab0, 0xffde07a2, 0xffde0755, 0xff7b0039, 0xff422922, 0xff602c12, 0xff8b572a, 0xffae7a28
  };
  /**
   *
   */
  public static int[] TEXT_SHADOW_COLORS = new int[] {
      0xaafefffe, 0xaa999999, 0xaa333333, 0xaa000000, 0xaaa90116, 0xaaec001c, 0xaaff441c,
      0xaaaa8514, 0xaaffbd18, 0xaafff013, 0xaaadd321, 0xaa23c203, 0xaa007f23, 0xaa0ce397,
      0xaa06a998, 0xaa00d0fe, 0xaa1975ff, 0xaa2c2ad4, 0xaa4a07b7, 0xaa7621ff, 0xaab52fe3,
      0xaaff5ab0, 0xaade07a2, 0xaade0755, 0xaa7b0039, 0xaa422922, 0xaa602c12, 0xaa8b572a, 0xaaae7a28
  };

  private Paint mPaint = new Paint();
  private Paint mBorderPaint = new Paint();
  /**
   * 背景颜色块的高度
   */
  private int bgHeight;
  /**
   * 颜色列表的类型
   * 分两种：
   * TEXT：文字SeekBar颜色快列表
   * BG：镜头背景SeekBar颜色快列表
   */
  private int colorType;
  /**
   * 滑块宽度
   */
  private int thumbWidth;
  /**
   * 滑块高度
   */
  private int thumbHeight;
  /**
   * 滑块边框宽度
   */
  private int thumbBorderWidth;
  /**
   * 滑块边框颜色
   */
  private int thumbBorderColor;
  /**
   * 背景左边距离
   */
  private int bgLeftPadding;
  /**
   * 背景右边距离
   */
  private int bgRightPadding;
  /**
   * SeekBar背景与顶部圆形View的距离
   */
  private int topBgSpace;
  /**
   * 背景颜色块宽度
   */
  private float itemWidth;
  /**
   * 整个背景颜色块view的宽度
   */
  private int width;
  /**
   * 使用的颜色块颜色列表
   */
  public int[] colors;
  /**
   * 背景颜色块每个item的left坐标
   */
  private int itemLeft;
  /**
   * 背景颜色块RectF
   */
  private RectF mBgRectF = new RectF();
  /**
   * 滑块边框块RectF
   */
  private RectF strokeRectF;
  /**
   * 滑块RectF
   */
  private RectF mThumbRectF = new RectF();
  /**
   * 当前滑块显示的颜色
   */
  private int currColor = CURR_DEF_COLOR;

  private Callback mCallback;
  private boolean isRtl;
  private boolean isFromUser = true;

  public ColorBarBgView(Context context) {
    super(context);
    initAttrs(context, null);
  }

  public ColorBarBgView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    initAttrs(context, attrs);
  }

  public ColorBarBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrs(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ColorBarBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initAttrs(context, attrs);
  }

  private void initAttrs(Context ctx, AttributeSet attrs) {
    ColorBarBgBuilder builder = new ColorBarBgBuilder(ctx);
    if (null == attrs) {
      apply(builder);
      return;
    }

    TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.ColorSeekBar);
    bgHeight =
        ta.getDimensionPixelOffset(R.styleable.ColorSeekBar_csb_bg_height, builder.bgHeight);
    colorType = ta.getInt(R.styleable.ColorSeekBar_csb_color_type, builder.colorType);
    thumbWidth =
        ta.getDimensionPixelOffset(R.styleable.ColorSeekBar_csb_thumb_width, builder.thumbWidth);
    thumbHeight =
        ta.getDimensionPixelOffset(R.styleable.ColorSeekBar_csb_thumb_height, builder.thumbHeight);
    thumbBorderWidth = ta.getDimensionPixelOffset(R.styleable.ColorSeekBar_csb_thumb_border_width,
        builder.thumbBorderWidth);
    thumbBorderColor = ta.getColor(R.styleable.ColorSeekBar_csb_thumb_border_color,
        builder.thumbBorderColor);
    bgLeftPadding = ta.getDimensionPixelOffset(R.styleable.ColorSeekBar_csb_bg_left_padding,
        builder.bgLeftPadding);
    bgRightPadding = ta.getDimensionPixelOffset(R.styleable.ColorSeekBar_csb_bg_right_padding,
        builder.bgRightPadding);
    topBgSpace = ta.getDimensionPixelOffset(R.styleable.ColorSeekBar_csb_top_text_bg_space,
        builder.topBgSpace);
    currColor = ta.getColor(R.styleable.ColorSeekBar_csb_default_color, builder.defaultColor);
    init();
  }

  private void init() {
    initColorType();
    STATUS = STATUS_INIT;
  }

  private void initColorType() {
    if (colorType == ColorType.TEXT_SHADOW) {
      colors = TEXT_SHADOW_COLORS;
    } else {
      colors = TEXT_COLORS;
    }
  }

  private void apply(ColorBarBgBuilder builder) {
    this.bgHeight = builder.bgHeight;
    this.colorType = builder.colorType;
    this.thumbWidth = builder.thumbWidth;
    this.thumbHeight = builder.thumbHeight;
    this.thumbBorderWidth = builder.thumbBorderWidth;
    this.thumbBorderColor = builder.thumbBorderColor;
    this.bgLeftPadding = builder.bgLeftPadding;
    this.bgRightPadding = builder.bgRightPadding;
    this.topBgSpace = builder.topBgSpace;
    this.currColor = builder.defaultColor;
    init();
  }

  private int currThumbOffset;

  @Override public boolean onTouchEvent(MotionEvent event) {
    switch (event.getActionMasked()) {//点击时
      case MotionEvent.ACTION_DOWN:
        currThumbOffset = (int) event.getX();
        STATUS = STATUS_SEEK;
        getParent().requestDisallowInterceptTouchEvent(true);
        if (null != mCallback) {
          mCallback.OnSeekStart();
        }
        invalidate();
        break;
      //滑动时
      case MotionEvent.ACTION_MOVE:
        currThumbOffset = (int) event.getX();
        invalidate();
        thumbLeft = getThumbLeft();
        if (isFromUser) {
          currColor = getCurrColor(thumbLeft - (isRtl ? bgRightPadding : bgLeftPadding));
        }
        if (null != mCallback) {
          mCallback.OnSeekBarChanged(thumbCenterX, currColor, currSeekPos);
          mCallback.OnSeekBarChanged(this, currColor);
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        getParent().requestDisallowInterceptTouchEvent(false);
        if (null != mCallback) {
          mCallback.OnSeekEnd(currColor);
        }
        break;
    }
    return true;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(getMeasuredWidth(), thumbHeight);

    isRtl = View.LAYOUT_DIRECTION_RTL == getLayoutDirection();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    width = getMeasuredWidth();
    bgItemWidth = (getMeasuredWidth() - bgLeftPadding - bgRightPadding) / colors.length;
    mPaint.setAntiAlias(true);
    mBorderPaint.setAntiAlias(true);
    mBorderPaint.setColor(thumbBorderColor);
    mBorderPaint.setStyle(Paint.Style.STROKE);
    mBorderPaint.setStrokeWidth(thumbBorderWidth);

    drawBg(canvas);
    if (STATUS_SEEK == STATUS) {
      drawScrollThumb(canvas);
    } else {
      drawInitThumb(canvas);
    }
    isFromUser = true;
  }

  private void drawBg(Canvas canvas) {
    itemWidth = (float) (width - bgLeftPadding - bgRightPadding) / colors.length;
    itemLeft = isRtl ? bgRightPadding : bgLeftPadding;

    for (int i = 0; i < colors.length; i++) {
      mPaint.setColor(colors[i]);
      mBgRectF.left = itemLeft;
      mBgRectF.top = (thumbHeight - bgHeight) / 2;
      mBgRectF.right = itemLeft + itemWidth;
      mBgRectF.bottom = bgHeight + mBgRectF.top;
      canvas.drawRoundRect(mBgRectF, 0, 0, mPaint);
      itemLeft += itemWidth;
    }
  }

  public void setColorType(int colorType) {
    this.colorType = colorType;
    initColorType();
    invalidate();
  }

  private int currColorPosition;
  private float bgItemWidth;
  private float thumbLeft;
  private float thumbCenterX;

  private void drawInitThumb(Canvas canvas) {
    thumbLeft = (isRtl ? bgRightPadding : bgLeftPadding) - (thumbWidth / 2 - bgItemWidth / 2);
    if (CURR_DEF_COLOR == currColor) {
      currColor = colors[0];
    }
    for (int i = 0; i < colors.length; i++) {
      if (currColor == colors[i]) {
        currColorPosition = i;
        thumbLeft = thumbLeft + i * bgItemWidth;
        break;
      }
    }
    mPaint.setColor(currColor);
    drawThumb(canvas);
  }

  private void drawScrollThumb(Canvas canvas) {
    thumbLeft = getThumbLeft();
    if (isFromUser) {
      currColor = getCurrColor(thumbLeft - (isRtl ? bgRightPadding : bgLeftPadding));
    }

    mPaint.setColor(currColor);
    thumbLeft = (isRtl ? bgRightPadding : bgLeftPadding) + currSeekPos * itemWidth
        - (thumbWidth - itemWidth) / 2;
    thumbCenterX =
        (isRtl ? bgRightPadding : bgLeftPadding) + currSeekPos * itemWidth + itemWidth / 2;
    drawThumb(canvas);
  }

  private int getThumbLeft() {
    int left;
    if (currThumbOffset < (isRtl ? bgRightPadding : bgLeftPadding)) {
      left = isRtl ? bgRightPadding : bgLeftPadding;
    } else if (currThumbOffset > width - bgRightPadding) {
      left = width - bgRightPadding;
    } else {
      left = currThumbOffset;
    }
    return left;
  }

  private void drawThumb(Canvas canvas) {
    mThumbRectF.left = thumbLeft;
    mThumbRectF.top = 0;
    mThumbRectF.right = thumbLeft + thumbWidth;
    mThumbRectF.bottom = thumbHeight;

    canvas.drawRoundRect(mThumbRectF, 0, 0, mPaint);
    if (0 != thumbBorderWidth) {
      canvas.drawRoundRect(getStrokeRect(mThumbRectF), 0, 0, mBorderPaint);
    }
  }


  private int currSeekPos = 0;

  private int getCurrColor(float left) {
    currSeekPos = (int) (left / itemWidth);
    if (currSeekPos < colors.length && currSeekPos > 0) {
    } else if (currSeekPos < 0) {
      currSeekPos = 0;
    } else if (currSeekPos >= colors.length) {
      currSeekPos = colors.length - 1;
    }
    return colors[currSeekPos];
  }

  private RectF getStrokeRect(RectF rectF) {
    strokeRectF = new RectF(rectF);
    strokeRectF.inset(thumbBorderWidth / 2, thumbBorderWidth / 2);
    return strokeRectF;
  }

  public void setCallback(Callback callback) {
    this.mCallback = callback;
  }

  public ColorBarBgView(ColorBarBgBuilder builder) {
    super(builder.ctx);
    apply(builder);
    init();
  }

  public static ColorBarBgBuilder with(Context ctx) {
    return new ColorBarBgBuilder(ctx);
  }

  public void setCurrColor(int color) {
    this.currColor = color;
    isFromUser = false;//标识是否是用户手动话都不敢选的颜色
    compatibleNoColor(color);
    invalidate();
  }

  public int getCurrColor() {
    return currColor;
  }

  /**
   * 兼容当前指定的颜色没有找到的情况
   * 如果为发现则第一个替换成当前指定颜色，去除第24个
   */
  private void compatibleNoColor(int color) {
    if (null == colors || ColorType.TEXT != colorType ||
        (ColorType.TEXT == colorType && colors.length < 25)) {
      return;
    }

    boolean isFind = false;
    int listSize = colors.length;
    for (int i = 0; i < listSize; i++) {
      if (color == colors[i]) {
        isFind = true;
        currSeekPos = i;
        break;
      }
    }
    if (isFind) {
      return;
    }
    int[] newColors = new int[listSize];
    newColors[0] = color;
    int j = 1;
    for (int i = 0; i < listSize; i++) {
      newColors[j] = colors[i];
      if (i != 24) {
        j++;
      }
    }
    colors = newColors;
  }

  public static class ColorBarBgBuilder {
    private Context ctx;
    private int bgHeight;
    private int colorType;
    private int thumbWidth;
    private int thumbHeight;
    private int thumbBorderWidth;
    private int thumbBorderColor;
    private int bgLeftPadding;
    private int bgRightPadding;
    private int topBgSpace;
    private int defaultColor;

    public ColorBarBgBuilder(Context ctx) {
      this.ctx = ctx;
      this.bgHeight = DPUtils.dpToPixel(ctx, 24);
      this.colorType = ColorType.TEXT;
      this.thumbWidth = DPUtils.dpToPixel(ctx, 19);
      this.thumbHeight = DPUtils.dpToPixel(ctx, 28);
      this.thumbBorderWidth = DPUtils.dpToPixel(ctx, 1);
      this.thumbBorderColor = 0xffffffff;
      this.bgLeftPadding = DPUtils.dpToPixel(ctx, 17);
      this.bgRightPadding = DPUtils.dpToPixel(ctx, 17);
      this.topBgSpace = DPUtils.dpToPixel(ctx, 24);
      this.defaultColor = CURR_DEF_COLOR;
    }

    public ColorBarBgBuilder(ColorBarBgBuilder builder) {
      this.bgHeight = builder.bgHeight;
      this.colorType = builder.colorType;
    }

    public ColorBarBgBuilder setBgHeight(int bgHeight) {
      this.bgHeight = bgHeight;
      return this;
    }

    public ColorBarBgBuilder setColorType(int colorType) {
      this.colorType = colorType;
      return this;
    }

    public ColorBarBgBuilder setThumbWidth(int thumbWidth) {
      this.thumbWidth = thumbWidth;
      return this;
    }

    public ColorBarBgBuilder setThumbHeight(int thumbHeight) {
      this.thumbHeight = thumbHeight;
      return this;
    }

    public ColorBarBgBuilder setThumbBorderWidth(int thumbBorderWidth) {
      this.thumbBorderWidth = thumbBorderWidth;
      return this;
    }

    public ColorBarBgBuilder setThumbBorderColor(int thumbBorderColor) {
      this.thumbBorderColor = thumbBorderColor;
      return this;
    }

    public ColorBarBgBuilder setBgLeftPadding(int bgLeftPadding) {
      this.bgLeftPadding = bgLeftPadding;
      return this;
    }

    public ColorBarBgBuilder setBgRightPadding(int bgRightPadding) {
      this.bgRightPadding = bgRightPadding;
      return this;
    }

    public ColorBarBgBuilder setTopBgSpace(int topBgSpace) {
      this.topBgSpace = topBgSpace;
      return this;
    }

    public ColorBarBgBuilder setDefaultColor(int defaultColor) {
      this.defaultColor = defaultColor;
      return this;
    }

    public ColorBarBgView build() {
      return new ColorBarBgView(this);
    }
  }

  public interface Callback {
    default void OnSeekBarChanged(float thumbCenterX, int currColor, int currPos) {
    }

    void OnSeekBarChanged(ColorBarBgView colorBarBgView, int currColor);

    void OnSeekStart();

    void OnSeekEnd(int currColor);
  }
}
