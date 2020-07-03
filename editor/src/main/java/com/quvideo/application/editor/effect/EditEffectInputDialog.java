package com.quvideo.application.editor.effect;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.utils.DeviceSizeUtil;
import com.quvideo.application.widget.softkeyboard.DifferenceCalculator;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.SubtitleEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubtitleText;

public class EditEffectInputDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;

  private View mBgView;
  private EditText mEditText;

  private String curText = "";

  private boolean mNeedShowKey = true;

  public EditEffectInputDialog(Context context, MenuContainer container, IQEWorkSpace workSpace, int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null);
    EffectOPStaticPic effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, true);
    mWorkSpace.handleOperation(effectOPStaticPic);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectSubtitleInput;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_input;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    if (mNeedShowKey) {
      initKey(view);
      showKeyBoard();
      mNeedShowKey = false;
    }
    mEditText = view.findViewById(R.id.et_edit);
    mBgView = view.findViewById(R.id.viewBg);
    Button btnConfirm = view.findViewById(R.id.btn_confirm);

    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (baseEffect instanceof SubtitleEffect) {
      curText = ((SubtitleEffect) baseEffect).getTextBubbleInfo().getFirstText();
      if (curText != null) {
        mEditText.setText(curText);
      }
    }

    mEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void afterTextChanged(Editable editable) {
        String endStr = editable.toString();
        if (!TextUtils.equals(endStr, curText)) {
          EffectOPSubtitleText effectOPSubtitleText = new EffectOPSubtitleText(effectIndex, endStr);
          mWorkSpace.handleOperation(effectOPSubtitleText);
        }
        curText = endStr;
      }
    });
    mEditText.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        showKeyBoard();
      }
    });
    btnConfirm.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        hideKeyboard();
        dismissMenu();
      }
    });
  }

  @Override protected String getBottomTitle() {
    return "";
  }

  @Override protected void releaseAll() {
    EffectOPStaticPic effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, false);
    mWorkSpace.handleOperation(effectOPStaticPic);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    hideKeyboard();
  }

  public void dismissMenu() {
    hideKeyboard();
    if (mMenuContainer != null) {
      mMenuContainer.removeView(this);
    }
  }

  private int mKeyboardHeight;

  private boolean enableLayoutChange = true;

  public void setEnableLayoutChange(boolean enableLayoutChange) {
    this.enableLayoutChange = enableLayoutChange;
  }

  public void initKey(final View view) {
    if (mKeyboardHeight > 0) {
      ViewGroup.LayoutParams layoutParams = mBgView.getLayoutParams();
      //layoutParams.setMargins(0, TextSeekBar.dip2px(getContext(), 8), 0,
      //    mKeyboardHeight);
      layoutParams.height = mKeyboardHeight;
      mBgView.setLayoutParams(layoutParams);
      mBgView.requestLayout();
      //showKeyBoard();
      mBgView.postDelayed(new Runnable() {
        @Override public void run() {
          measureKeyboard(view);
        }
      }, 200);
      //miniProgressBarHelper.setBlockTouch(true);
    } else {
      measureKeyboard(view);
      //showKeyBoard();
    }
  }

  private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

  public void measureKeyboard(View view) {
    if (onGlobalLayoutListener == null) {
      onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
          if (!enableLayoutChange) {
            return;
          }
          Rect r = new Rect();
          //获取当前界面可视部分
          view.getWindowVisibleDisplayFrame(r);

          int heightDifference = DifferenceCalculator.getInstance().getDifference(getContext(), r);
          if (heightDifference > DeviceSizeUtil.getScreenHeight() / 6) {
            mKeyboardHeight = heightDifference;
          } else if (heightDifference < DeviceSizeUtil.getScreenHeight() / 6) {
          }
        }
      };
      view.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }
  }

  private void showKeyBoard() {
    if (mEditText == null) {
      return;
    }
    mEditText.setFocusable(true);
    mEditText.setFocusableInTouchMode(true);
    mEditText.requestFocus();
    mEditText.findFocus();

    InputMethodManager inputManager = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.showSoftInput(mEditText, 0);
  }

  public void hideKeyboard() {
    mEditText.clearFocus();
    InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
  }
}
