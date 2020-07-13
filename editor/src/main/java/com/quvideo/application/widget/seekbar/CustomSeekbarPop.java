package com.quvideo.application.widget.seekbar;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DeviceSizeUtil;
import com.quvideo.application.utils.RTLUtil;

/**
 * seekbar， 带一个pop显示
 * 设置区间以后，setProgress和getPorgress都会按区间里的给
 * 样式：
 *
 * [pop]
 * [title] ========||--------- progress
 */
public class CustomSeekbarPop extends RelativeLayout {

  private Context mContext;
  private DoubleSeekbar mSeekBar;
  private TextView mTvTitle, mTvStart, mTvEnd;

  private ProgressPopupWin progressPopupWin;

  private DoubleSeekbar.OnSeekbarListener mSeekOverListener;

  private int seekbarY;
  private int tipHalfW;
  private int leftOffset = -1;

  private int dp1px;
  private int dp10px;
  private int dp15px;

  public CustomSeekbarPop(Context context) {
    super(context);
    mContext = context;
    initUI();
  }

  public CustomSeekbarPop(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    initUI();
  }

  public CustomSeekbarPop(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mContext = context;
    initUI();
  }

  private void initUI() {
    LayoutInflater.from(mContext).inflate(R.layout.editor_custom_seekbar_pop_layout, this, true);
    mSeekBar = findViewById(R.id.seekbar_pop_progress);
    mSeekBar.setOnSeekbarListener(onSeekBarChangeListener);
    mTvTitle = findViewById(R.id.seekbar_pop_tv_title);
    mTvStart = findViewById(R.id.seekbar_pop_tv_start);
    mTvEnd = findViewById(R.id.seekbar_pop_tv_end);

    progressPopupWin = new ProgressPopupWin(mContext);

    dp1px = DeviceSizeUtil.getFitPxFromDp(1);
    dp10px = dp1px * 10;
    dp15px = dp1px * 15;
  }

  public void init(InitBuilder initBuilder) {
    if (initBuilder.titleId != 0) {
      mTvTitle.setVisibility(View.VISIBLE);
      mTvTitle.setText(initBuilder.titleId);
    } else {
      mTvTitle.setVisibility(View.GONE);
    }
    if (!TextUtils.isEmpty(initBuilder.start)) {
      mTvStart.setVisibility(VISIBLE);
      mTvStart.setText(initBuilder.start);
    } else {
      mTvStart.setVisibility(GONE);
    }
    if (!TextUtils.isEmpty(initBuilder.end)) {
      mTvEnd.setVisibility(VISIBLE);
      mTvEnd.setText(initBuilder.end);
    } else {
      mTvEnd.setVisibility(GONE);
    }
    if (initBuilder.seekRange != null) {
      mSeekBar.setProgressRange(initBuilder.seekRange.min, initBuilder.seekRange.max, initBuilder.minRange);
    }
    mSeekBar.setFirstProgress(initBuilder.progress);
    if (initBuilder.isDoubleMode) {
      mSeekBar.setSecondProgress(initBuilder.secondProgress);
      mSeekBar.setDoubleMode(initBuilder.isDoubleMode);
    }
    updateProgress(initBuilder.progress);
    updateTipPosition(mSeekBar.getSeekbarPos(true));
    // 这个放更新前面，是因为设置文字有可能需要listener的转换
    mSeekOverListener = initBuilder.seekOverListener;
  }

  public void setProgress(int progress) {
     mSeekBar.setFirstProgress(progress);
  }

  public int getProgress() {
    return getFirstProgress();
  }

  public int getFirstProgress() {
    return mSeekBar.getFirstProgress();
  }

  public int getSecondProgress() {
    return mSeekBar.getSecondProgress();
  }

  /**
   * @param realProgress 真实区间的进度
   */
  private void updateProgress(int realProgress) {
    String value = realProgress + "";
    progressPopupWin.setPopValue(value);
  }

  /**
   * @param seekbarXPos 进度条进度
   */
  private void updateTipPosition(float seekbarXPos) {
    if (progressPopupWin.isShowing()) {
      progressPopupWin.update(getThumbCenterX(seekbarXPos), getThumbTopY(),
          LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
  }

  private int getThumbCenterX(float seekbarXPos) {
    if (leftOffset < 0) {
      Rect rectSeek = new Rect();
      mSeekBar.getGlobalVisibleRect(rectSeek);
      // 兼容中东布局？
      if (rectSeek.right > rectSeek.left) {
        leftOffset = rectSeek.left;
      } else {
        leftOffset = rectSeek.right;
      }
    }
    if (RTLUtil.isRTL()) {
      return leftOffset + (int) seekbarXPos - getTipHalfW();
    }
    return leftOffset + (int) seekbarXPos - getTipHalfW();
  }

  private int getThumbTopY() {
    if (seekbarY == 0) {
      Rect rectP = new Rect();
      mSeekBar.getGlobalVisibleRect(rectP);
      seekbarY = rectP.top - (rectP.bottom - rectP.top) - dp15px;
    }
    return seekbarY;
  }

  private int getTipHalfW() {
    if (tipHalfW == 0) {
      Rect rectP = new Rect();
      progressPopupWin.getPopView().getGlobalVisibleRect(rectP);
      if (rectP.right > rectP.left) {
        tipHalfW = (rectP.right - rectP.left) / 2;
      } else {
        tipHalfW = (rectP.left - rectP.right) / 2;
      }
    }
    return tipHalfW;
  }

  private DoubleSeekbar.OnSeekbarListener onSeekBarChangeListener =
      new DoubleSeekbar.OnSeekbarListener() {

        @Override public void onSeekChange(boolean isFirst, int progress) {
          updateProgress(progress);
          updateTipPosition(mSeekBar.getSeekbarPos(isFirst));
          if (mSeekOverListener != null) {
            mSeekOverListener.onSeekChange(isFirst, progress);
          }
        }

        @Override public void onSeekStart(boolean isFirst, int progress) {
          float seekbarXPos = mSeekBar.getSeekbarPos(isFirst);
          updateTipPosition(seekbarXPos);
          progressPopupWin.showAtLocation(CustomSeekbarPop.this,
              Gravity.LEFT | Gravity.START | Gravity.TOP,
              getThumbCenterX(seekbarXPos), getThumbTopY());
          if (mSeekOverListener != null) {
            mSeekOverListener.onSeekStart(isFirst, progress);
          }
        }

        @Override public void onSeekOver(boolean isFirst, int progress) {
          updateProgress(progress);
          CustomSeekbarPop.this.postDelayed(new Runnable() {
            @Override public void run() {
              progressPopupWin.dismiss();
            }
          }, 250);
          if (mSeekOverListener != null) {
            mSeekOverListener.onSeekOver(isFirst, progress);
          }
        }
      };

  public interface ISeekOverListener {
    /** 选择结束 */
    void onSeekOver(int progress, int seekBegin, boolean fromUser);
  }

  /**
   * 初始化参数
   */
  public static final class InitBuilder {

    int titleId;
    int progress;
    int secondProgress;
    boolean isDoubleMode;
    String start;
    String end;
    int minRange;
    SeekRange seekRange;
    DoubleSeekbar.OnSeekbarListener seekOverListener;

    public InitBuilder() {
    }

    /**
     * 设置标题，不设置情况默认是隐藏了的
     */
    public InitBuilder titleId(int val) {
      titleId = val;
      return this;
    }

    public InitBuilder progress(int val) {
      progress = val;
      return this;
    }

    public InitBuilder secondProgress(int val) {
      secondProgress = val;
      return this;
    }

    public InitBuilder isDoubleMode(boolean val) {
      isDoubleMode = val;
      return this;
    }

    public InitBuilder start(String val) {
      start = val;
      return this;
    }

    public InitBuilder end(String val) {
      end = val;
      return this;
    }

    public InitBuilder minRange(int val) {
      minRange = val;
      return this;
    }

    /**
     * 设置seekbar区间,设置区间以后，setProgress和getPorgress都会按区间里的给
     * 默认是0~100
     */
    public InitBuilder seekRange(SeekRange val) {
      seekRange = val;
      return this;
    }

    /**
     * 监听器
     */
    public InitBuilder seekOverListener(DoubleSeekbar.OnSeekbarListener val) {
      seekOverListener = val;
      return this;
    }
  }

  public static class SeekRange {
    int min;
    int max;

    public SeekRange(int min, int max) {
      this.min = min;
      this.max = max;
    }
  }

  private class ProgressPopupWin extends PopupWindow {
    private View popView;
    private TextView tvPopTiv;

    public ProgressPopupWin(Context context) {
      super(context);
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      popView = inflater.inflate(R.layout.editor_custom_seekbar_popwin_view, null);
      tvPopTiv = popView.findViewById(R.id.seekbar_pop_tv_tip);
      this.setContentView(popView);
      this.setWidth(LayoutParams.WRAP_CONTENT);
      this.setHeight(LayoutParams.WRAP_CONTENT);
      this.setInputMethodMode(INPUT_METHOD_NOT_NEEDED);
      this.setFocusable(false);
      this.setBackgroundDrawable(new BitmapDrawable());
      this.setOutsideTouchable(true);
    }

    public View getPopView() {
      return popView;
    }

    void setPopValue(String popValue) {
      tvPopTiv.setText(popValue);
    }
  }
}
