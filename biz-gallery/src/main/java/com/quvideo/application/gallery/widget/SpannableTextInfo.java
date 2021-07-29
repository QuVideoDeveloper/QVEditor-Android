package com.quvideo.application.gallery.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Spannable text 封装， 用户处理Spannable text
 *
 */

public class SpannableTextInfo {

  public String text;
  public List<SpanInfoBean> spanTextList;

  public class SpanInfoBean {
    public String spanText;
    public int startIndexOfText;
    public int spanColor;
    //0是默认，1是特殊处理feed的前面名字加粗的type
    public int type = 0;
  }

  public SpannableTextInfo(String text) {
    this.text = text;
    spanTextList = new ArrayList<>();
  }

  public void addSpanInfo(String spanText, int startIndexOfText, int spanColor) {
    addSpanInfo(spanText, startIndexOfText, spanColor, 0);
  }

  public void addSpanInfo(String spanText, int startIndexOfText, int spanColor, int type) {
    SpanInfoBean infoBean = new SpanInfoBean();
    infoBean.spanText = spanText;
    infoBean.spanColor = spanColor;
    infoBean.startIndexOfText = startIndexOfText;
    infoBean.type = type;
    if (type == 1) {
      spanTextList.add(0, infoBean);
    } else {
      spanTextList.add(infoBean);
    }
  }
}
