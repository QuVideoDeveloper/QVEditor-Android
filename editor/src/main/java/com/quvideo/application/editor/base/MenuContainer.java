package com.quvideo.application.editor.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import java.util.ArrayList;

/**
 * @author wuzhongyou
 * @date 2020/5/27.
 */
public class MenuContainer extends RelativeLayout {

  private ArrayList<BaseMenuLayer> mBaseMenuLayers = new ArrayList<>();

  public MenuContainer(Context context) {
    super(context);
  }

  public MenuContainer(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MenuContainer(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public MenuContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  /**
   * 处理返回键
   */
  public final boolean handleBackPress() {
    if (mBaseMenuLayers.size() > 0) {
      mBaseMenuLayers.get(mBaseMenuLayers.size() -1).handleBackPress();
      return true;
    }
    return false;
  }

  public void addMenuLayer(BaseMenuLayer menuLayer) {
    mBaseMenuLayers.add(menuLayer);
    LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    addView(menuLayer, params);
  }

  public void removeMenuLayer(BaseMenuLayer menuLayer) {
    mBaseMenuLayers.remove(menuLayer);
    removeView(menuLayer);
  }
}
