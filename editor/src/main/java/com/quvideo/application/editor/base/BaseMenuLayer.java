package com.quvideo.application.editor.base;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * @author wuzhongyou
 * @date 2020/5/27.
 */
public abstract class BaseMenuLayer extends RelativeLayout {

  public BaseMenuLayer(Context context) {
    super(context);
  }

  /**
   * 处理返回键
   */
  public final void handleBackPress() {
    dismissMenu();
  }

  public abstract void dismissMenu();
}
