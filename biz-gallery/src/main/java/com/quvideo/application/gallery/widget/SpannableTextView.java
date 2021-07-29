package com.quvideo.application.gallery.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import com.quvideo.application.gallery.R;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpannableTextView extends AppCompatTextView {

  private Context mContext = null;
  protected OnLineCountListener mOnLineCountListener;
  protected int maxLine = Integer.MAX_VALUE;
  private SpannableString spanableInfo;
  private int defaultHighColor;

  public interface OnSpannableTextClickListener {
    void onTextClicked(View view, String text);
  }

  public SpannableTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mContext = context;
    disableMove();
  }

  public SpannableTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    disableMove();
  }

  public SpannableTextView(Context context) {
    super(context);
    mContext = context;
    disableMove();
  }

  private void disableMove() {
    defaultHighColor = mContext.getResources().getColor(R.color.gallery_color_d9d9d9);
    setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        TextView widget = (TextView) v;
        Object text = widget.getText();
        if (text instanceof Spanned) {
          Spanned buffer = (Spanned) text;

          int action = event.getAction();

          if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            try {
              x -= widget.getTotalPaddingLeft();
              y -= widget.getTotalPaddingTop();

              x += widget.getScrollX();
              y += widget.getScrollY();
            } catch (NullPointerException ignore) {
              //https://fabric.io/quvideos-projects/android/apps/com.quvideo.xiaoying/issues/5a3ceee38cb3c2fa636e0fe2
            }

            Layout layout = widget.getLayout();
            if (layout != null) {
              int line = layout.getLineForVertical(y);
              int off = layout.getOffsetForHorizontal(line, x);

              ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

              if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                  link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                  // Selection only works on Spannable text. In our case setSelection doesn't work on spanned text
                  //Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                }
                return true;
              }
            }
          }
        }
        return false;
      }
    });
    getViewTreeObserver().addOnGlobalLayoutListener(this::refrshEndView);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    refrshEndView();
  }

  private void refrshEndView() {
    if (mOnLineCountListener != null) {
      mOnLineCountListener.onLineCountCallback();
    }
  }

  public int checkLineCount() {
    //获取当前textview的行数
    int lineCount = getLineCount();
    if (maxLine != Integer.MAX_VALUE) {
      try {
        if (getLayout() != null && getLayout().getEllipsisCount(maxLine - 1) >= 0) {
          //mLayout.getEllipsisCount获取textView对应行数索引出的省略的字符数,没有省略返回0
          lineCount = maxLine + 1;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      refreshTextEnd();
    }
    return lineCount;
  }

  @Override public void setMaxLines(int maxlines) {
    this.maxLine = maxlines;
    refreshTextEnd();
    super.setMaxLines(maxlines);
  }

  /**
   * 替换...处理
   */
  private void refreshTextEnd() {
    if (spanableInfo != null) {
      int lineCount = getLineCount();
      if (maxLine == Integer.MAX_VALUE || lineCount < maxLine) {
        setText(spanableInfo);
      } else {
        if (getLayout() != null && lineCount > 1) {
          int endOfLastLine = getLayout().getLineEnd(maxLine - 1);
          if (endOfLastLine <= 3) {
            return;
          }
          if (getText().length() > 3) {
            String newVal = getText().subSequence(0, endOfLastLine - 3) + "...";
            SpannableString shortSpan = null;
            if (spanInfoData != null) {
              shortSpan = getSpanableInfo(newVal, spanInfoData);
            } else if (spanListInfoData != null) {
              shortSpan = getSpanableInfo(newVal, spanListInfoData);
            } else if (spanTextInfoData != null) {
              shortSpan = getSpanableInfo(newVal, spanTextInfoData);
            } else {
              shortSpan = new SpannableString(newVal);
            }
            setText(shortSpan);
          } else {
            setText(spanableInfo);
          }
        }
      }
    }
  }

  public void setOnLineCountListener(OnLineCountListener onLineCountListener) {
    this.mOnLineCountListener = onLineCountListener;
  }

  public interface OnLineCountListener {
    void onLineCountCallback();
  }

  /**
   * 清楚span缓存
   */
  public void clearSpan() {
    spanableInfo = null;
    spanInfoData = null;
    spanListInfoData = null;
    spanTextInfoData = null;
  }

  //*********************************************************

  private static Pattern pattern = Pattern.compile("\t|\r|\n");

  public static String replaceBlank(String str) {
    String dest = "";
    if (str != null) {
      Matcher m = pattern.matcher(str);
      dest = m.replaceAll(" ");
    }
    return dest;
  }

  //********************************************************************

  private SpanInfoData spanInfoData;

  public void setSpanText(String text, int spanStart, int spanEnd, int spanColor,
      OnClickListener spanOnClickListener) {
    text = replaceBlank(text);
    spanInfoData = new SpanInfoData(text, spanStart, spanEnd, spanColor, spanOnClickListener);
    spanableInfo = getSpanableInfo(text, spanInfoData);
    spanListInfoData = null;
    spanTextInfoData = null;
    setText(spanableInfo);
    setMovementMethod(LocalLinkMovementMethod.getInstance());
    setHighlightColor(defaultHighColor);
    setBackgroundResource(R.color.transparent);
    if (maxLine != Integer.MAX_VALUE) {
      refreshTextEnd();
    }
  }

  //********************************************************************

  private SpanListInfoData spanListInfoData;

  public void setSpanText(String text, ArrayList<int[]> spanRegionList, int spanColor,
      int highColor, int bgResId, OnClickListener spanOnClickListener) {
    text = replaceBlank(text);
    spanListInfoData =
        new SpanListInfoData(text, spanRegionList, spanColor, spanOnClickListener);
    spanableInfo = getSpanableInfo(text, spanListInfoData);
    spanInfoData = null;
    spanTextInfoData = null;
    setMovementMethod(LocalLinkMovementMethod.getInstance());
    setText(spanableInfo);
    if (highColor != -1) {
      setHighlightColor(defaultHighColor);
    }
    setBackgroundResource(bgResId);
    if (maxLine != Integer.MAX_VALUE) {
      refreshTextEnd();
    }
  }

  //********************************************************************

  private SpanTextInfoData spanTextInfoData;

  public void setSpanText(@NonNull SpannableTextInfo info,
      OnSpannableTextClickListener spanOnClickListener) {
    try {
      info.text = replaceBlank(info.text);
      spanTextInfoData = new SpanTextInfoData(info, spanOnClickListener);
      spanableInfo = getSpanableInfo(info.text, spanTextInfoData);
      spanInfoData = null;
      spanListInfoData = null;
      if (spanOnClickListener != null) {
        setMovementMethod(LocalLinkMovementMethod.getInstance());
      }
      setText(spanableInfo);
      setHighlightColor(Color.TRANSPARENT);
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
    if (maxLine != Integer.MAX_VALUE) {
      refreshTextEnd();
    }
  }

  //********************************************************************

  private SpannableString getSpanableInfo(String text, SpanInfoData spanInfoData) {
    SpannableString result = new SpannableString(text);
    int spanStart = spanInfoData.spanStart;
    int spanEnd = spanInfoData.spanEnd > text.length() ? text.length() : spanInfoData.spanEnd;
    if (spanStart <= spanEnd && spanStart <= text.length() - 1) {
      result.setSpan(
          new CustomClickableSpan(spanInfoData.spanColor, spanInfoData.spanOnClickListener),
          spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return result;
  }

  private SpannableString getSpanableInfo(String text, SpanListInfoData spanListInfoData) {
    SpannableString result = new SpannableString(text);
    int spanStart;
    int spanEnd;
    for (int[] indexes : spanListInfoData.spanRegionList) {
      spanStart = indexes[0];
      spanEnd = (indexes[1] + 1) > text.length() ? text.length() : (indexes[1] + 1);
      if (spanStart <= spanEnd && spanStart <= text.length() - 1) {
        if (spanListInfoData.spanOnClickListener != null) {
          result.setSpan(new CustomClickableSpan(indexes, spanListInfoData.spanColor,
                  spanListInfoData.spanOnClickListener), spanStart, spanEnd,
              Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
          result.setSpan(new ForegroundColorSpan(spanListInfoData.spanColor), spanStart, spanEnd,
              Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
      }
    }
    return result;
  }

  private SpannableString getSpanableInfo(String text, SpanTextInfoData spanTextInfoData) {
    SpannableString result = new SpannableString(text);
    int spanStart;
    int spanEnd;
    for (final SpannableTextInfo.SpanInfoBean spanInfo : spanTextInfoData.info.spanTextList) {
      spanStart = spanInfo.startIndexOfText;
      spanEnd = spanInfo.startIndexOfText + spanInfo.spanText.length();
      spanEnd = spanEnd > text.length() ? text.length() : spanEnd;
      if (spanStart <= spanEnd && spanStart <= text.length() - 1) {
        if (spanInfo.type == 1) {
          result.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), spanStart, spanEnd,
              Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          if (spanTextInfoData.spanOnClickListener != null) {
            result.setSpan(
                new CustomClickableSpanEx(new int[] { spanStart, spanEnd }, spanInfo.spanText,
                    spanInfo.spanColor, spanTextInfoData.spanOnClickListener), spanStart,
                spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          } else {
            result.setSpan(new ForegroundColorSpan(spanInfo.spanColor), spanStart, spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          }
        } else {
          if (spanTextInfoData.spanOnClickListener != null) {
            result.setSpan(
                new CustomClickableSpanEx(new int[] { spanStart, spanEnd }, spanInfo.spanText,
                    spanInfo.spanColor, spanTextInfoData.spanOnClickListener), spanStart,
                spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          } else {
            result.setSpan(new ForegroundColorSpan(spanInfo.spanColor), spanStart, spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          }
        }
      }
    }
    return result;
  }
  //********************************************************************

  private class SpanInfoData {
    String text;
    int spanStart;
    int spanEnd;
    int spanColor;
    OnClickListener spanOnClickListener;

    public SpanInfoData(String text, int spanStart, int spanEnd, int spanColor,
        OnClickListener spanOnClickListener) {
      this.text = text;
      this.spanStart = spanStart;
      this.spanEnd = spanEnd;
      this.spanColor = spanColor;
      this.spanOnClickListener = spanOnClickListener;
    }
  }

  private class SpanListInfoData {
    String text;
    ArrayList<int[]> spanRegionList;
    int spanColor;
    OnClickListener spanOnClickListener;

    public SpanListInfoData(String text, ArrayList<int[]> spanRegionList, int spanColor,
        OnClickListener spanOnClickListener) {
      this.text = text;
      this.spanRegionList = spanRegionList;
      this.spanColor = spanColor;
      this.spanOnClickListener = spanOnClickListener;
    }
  }

  private class SpanTextInfoData {
    SpannableTextInfo info;
    OnSpannableTextClickListener spanOnClickListener;

    public SpanTextInfoData(SpannableTextInfo info,
        OnSpannableTextClickListener spanOnClickListener) {
      this.info = info;
      this.spanOnClickListener = spanOnClickListener;
    }
  }

  //********************************************************************

  private class CustomClickableSpan extends ClickableSpan implements OnClickListener {
    private final OnClickListener mListener;
    private int mTextColor;
    private int[] mRegionOfParentText;

    @Override public void updateDrawState(TextPaint ds) {
      ds.setColor(mTextColor);
      ds.setUnderlineText(false);
    }

    public CustomClickableSpan(int textColor, OnClickListener l) {
      mListener = l;
      mTextColor = textColor;
    }

    public CustomClickableSpan(int[] regionOfParentText, int textColor, OnClickListener l) {
      mListener = l;
      mTextColor = textColor;
      mRegionOfParentText = regionOfParentText;
    }

    @Override public void onClick(View v) {
      if (mListener != null) {
        if (mRegionOfParentText != null) {
          v.setTag(mRegionOfParentText);
        }
        mListener.onClick(v);
      }
    }
  }

  private class CustomClickableSpanEx extends ClickableSpan implements OnClickListener {
    private final OnSpannableTextClickListener mListener;
    private int mTextColor;
    private String text;
    private int[] mRegionOfParentText;

    @Override public void updateDrawState(TextPaint ds) {
      ds.setColor(mTextColor);
      ds.setUnderlineText(false);
    }

    public CustomClickableSpanEx(int[] regionOfParentText, String text, int textColor,
        OnSpannableTextClickListener l) {
      mListener = l;
      mTextColor = textColor;
      mRegionOfParentText = regionOfParentText;
      this.text = text;
    }

    @Override public void onClick(View v) {
      if (mListener != null) {
        if (mRegionOfParentText != null) {
          v.setTag(mRegionOfParentText);
        }
        mListener.onTextClicked(v, text);
      }
    }
  }

  private static class LocalLinkMovementMethod extends LinkMovementMethod {
    static LocalLinkMovementMethod sInstance;

    public static LocalLinkMovementMethod getInstance() {
      if (sInstance == null) {
        sInstance = new LocalLinkMovementMethod();
      }

      return sInstance;
    }

    @Override public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
      int action = event.getAction();

      if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        try {
          int line = layout.getLineForVertical(y);
          int off = layout.getOffsetForHorizontal(line, x);

          ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

          if (link.length != 0) {
            if (action == MotionEvent.ACTION_UP) {
              link[0].onClick(widget);
            } else if (action == MotionEvent.ACTION_DOWN) {
              Selection.setSelection(buffer, buffer.getSpanStart(link[0]),
                  buffer.getSpanEnd(link[0]));
            }
            return true;
          } else {
            Selection.removeSelection(buffer);
            Touch.onTouchEvent(widget, buffer, event);
            return false;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return false;
    }
  }
}
