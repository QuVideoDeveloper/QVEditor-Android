package com.quvideo.application.editor.base;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class MenuContainer extends RelativeLayout {

  private ArrayList<BaseMenuLayer> mBaseMenuLayers = new ArrayList<>();

  private OnMenuListener mOnMenuListener;

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

  public void setOnMenuListener(OnMenuListener onMenuListener) {
    mOnMenuListener = onMenuListener;
  }

  /**
   * 处理返回键
   */
  public final boolean handleBackPress() {
    if (mBaseMenuLayers.size() > 0) {
      mBaseMenuLayers.get(mBaseMenuLayers.size() - 1).handleBackPress();
      return true;
    }
    return false;
  }

  /**
   * 获取当前最上层的menulayer类型
   */
  public synchronized BaseMenuLayer.MenuType getTopMenuType() {
    if (mBaseMenuLayers.size() > 0) {
      return mBaseMenuLayers.get(mBaseMenuLayers.size() - 1).getMenuType();
    }
    return null;
  }

  public synchronized void addMenuLayer(BaseMenuLayer menuLayer) {
    mBaseMenuLayers.add(menuLayer);
    LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    addView(menuLayer, params);
    changeCurMenu();
  }

  public synchronized void removeMenuLayer(BaseMenuLayer menuLayer) {
    mBaseMenuLayers.remove(menuLayer);
    removeView(menuLayer);
    changeCurMenu();
  }

  private void changeCurMenu() {
    if (mBaseMenuLayers.size() > 0) {
      BaseMenuLayer baseMenuLayer = mBaseMenuLayers.get(mBaseMenuLayers.size() - 1);
      if (baseMenuLayer != null && mOnMenuListener != null) {
        mOnMenuListener.onMenuChange(baseMenuLayer.getMenuType());
      }
    }
  }

  public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (mBaseMenuLayers.size() > 0) {
      int count = mBaseMenuLayers.size();
      BaseMenuLayer baseMenuLayer;
      for (int index = count - 1; index >= 0; index--) {
        baseMenuLayer = mBaseMenuLayers.get(index);
        if (baseMenuLayer != null && baseMenuLayer.onActivityResult(requestCode, resultCode, data)) {
          return true;
        }
      }
    }
    return false;
  }

  public interface OnMenuListener {
    void onMenuChange(BaseMenuLayer.MenuType menuType);
  }
}
