package com.quvideo.application.editor.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.effect.EffectBarItem;
import com.quvideo.mobile.engine.project.IQEWorkSpace;

public abstract class BaseEffectMenuView extends BaseMenuLayer implements View.OnClickListener, IEffectEditClickListener {

  protected MenuContainer mMenuContainer;
  private IEffectEditClickListener mItemOnClickListener;

  protected IQEWorkSpace mWorkSpace;

  public BaseEffectMenuView(Context context, IQEWorkSpace workSpace) {
    super(context);
    this.mWorkSpace = workSpace;
  }

  public void showMenu(MenuContainer container, IEffectEditClickListener itemOnClickListener) {
    init(getContext());
    mMenuContainer = container;
    mItemOnClickListener = itemOnClickListener;
    mMenuContainer.addMenuLayer(this);
  }

  private void init(Context context) {
    View view = LayoutInflater.from(context).inflate(getCustomLayoutId(), this, true);
    initCustomMenu(context, view);
    ((TextView) view.findViewById(R.id.title)).setText(getBottomTitle());
    view.findViewById(R.id.confirm).setOnClickListener(this);
    view.setOnClickListener(v -> {
    });
  }

  protected abstract int getCustomLayoutId();

  protected abstract void initCustomMenu(Context context, View view);

  protected abstract String getBottomTitle();

  protected abstract void releaseAll();

  protected final AppCompatActivity getActivity() {
    return (AppCompatActivity) getContext();
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    releaseAll();
  }

  @Override
  public void onClick(View v) {
    dismissMenu();
  }

  @Override public void onClick(View view, EffectBarItem operate) {
    if (mItemOnClickListener != null) {
      mItemOnClickListener.onClick(view, operate);
    }
    dismissMenu();
  }

  @Override
  public void dismissMenu() {
    if (mMenuContainer != null) {
      mMenuContainer.removeMenuLayer(this);
    }
  }
}
