package com.quvideo.application.editor.effect.subtitle;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.subtitle.view.ColorBarBgView;
import com.quvideo.mobile.engine.model.SubtitleEffect;
import com.quvideo.mobile.engine.model.effect.TextBubble;
import com.quvideo.mobile.engine.model.effect.TextBubbleInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMultiSubtitleBlod;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMultiSubtitleColor;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMultiSubtitleFont;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMultiSubtitleItalic;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubtitleAnim;

public class EffectSubtitleDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;

  //字幕列表
  private RecyclerView mRvText;
  //字体列表
  private RecyclerView mRvFont;
  // 字体颜色选取
  private ColorBarBgView mTextColorSeekBar;
  // 修改文本、动画
  private ImageView mIvInput;
  // 修改动画
  private Button mBtnAnim;
  // 修改描边、阴影、对齐
  private Button mBtnStroke, mBtnShadow, mBtnAlign, mBtnBlod, mBtnItalic;

  private EffectSubtitleAdapter mEffectSubtitleAdapter;
  private EffectFontAdapter mEffectFontAdapter;

  public EffectSubtitleDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    mWorkSpace.addObserver(mBaseObserver);
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectSubtitle;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_subtitle_edit;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mRvText = view.findViewById(R.id.subtitle_recyclerview);
    mRvFont = view.findViewById(R.id.subtitle_font_recyclerview);
    mTextColorSeekBar = view.findViewById(R.id.colorbar_text_color);
    mIvInput = view.findViewById(R.id.btn_edit);
    mBtnAnim = view.findViewById(R.id.btn_anim);
    mBtnBlod = view.findViewById(R.id.btn_blod);
    mBtnItalic = view.findViewById(R.id.btn_italic);
    mBtnStroke = view.findViewById(R.id.btn_stroke);
    mBtnShadow = view.findViewById(R.id.btn_shadow);
    mBtnAlign = view.findViewById(R.id.btn_align);

    mIvInput.setOnClickListener(mOnClickListener);
    mBtnAnim.setOnClickListener(mOnClickListener);
    mBtnBlod.setOnClickListener(mOnClickListener);
    mBtnItalic.setOnClickListener(mOnClickListener);
    mBtnStroke.setOnClickListener(mOnClickListener);
    mBtnShadow.setOnClickListener(mOnClickListener);
    mBtnAlign.setOnClickListener(mOnClickListener);

    SubtitleEffect curEffect = (SubtitleEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (curEffect.getTextBubbleInfo().mBubbleSubtype == TextBubbleInfo.BUBBLE_SUBTYPE_ANIM) {
      mBtnAnim.setEnabled(true);
      mBtnBlod.setEnabled(true);
      mBtnItalic.setEnabled(true);
    } else {
      mBtnAnim.setEnabled(false);
      mBtnBlod.setEnabled(false);
      mBtnItalic.setEnabled(false);
      mBtnAnim.setAlpha(0.1f);
      mBtnBlod.setAlpha(0.1f);
      mBtnItalic.setAlpha(0.1f);
    }
    if (curEffect.getTextBubbleInfo().isAnimOn) {
      mBtnAnim.setText(R.string.mn_edit_subtitle_anim_state_on);
    } else {
      mBtnAnim.setText(R.string.mn_edit_subtitle_anim_state_off);
    }
    mBtnBlod.setText(R.string.mn_edit_subtitle_blod_off);
    mBtnItalic.setText(R.string.mn_edit_subtitle_italic_off);

    mRvText.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mEffectSubtitleAdapter = new EffectSubtitleAdapter(curEffect.getTextBubbleInfo().mTextBubbleList,
        new EffectSubtitleAdapter.OnSubtitleClickListener() {
          @Override public void onClick(int index) {
            TextBubble textBubble = mEffectSubtitleAdapter.getSelectItem();
            if (!textBubble.isBold) {
              mBtnBlod.setText(R.string.mn_edit_subtitle_blod_off);
            } else {
              mBtnBlod.setText(R.string.mn_edit_subtitle_blod_on);
            }
            if (!textBubble.isItalic) {
              mBtnItalic.setText(R.string.mn_edit_subtitle_italic_off);
            } else {
              mBtnItalic.setText(R.string.mn_edit_subtitle_italic_on);
            }
            mEffectFontAdapter.updateSelectPath(mEffectSubtitleAdapter.getSelectItem().mFontPath);
            mTextColorSeekBar.setCurrColor(mEffectSubtitleAdapter.getSelectItem().mTextColor);
          }
        });
    mRvText.setAdapter(mEffectSubtitleAdapter);
    mRvFont.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mEffectFontAdapter = new EffectFontAdapter(new EffectFontAdapter.OnFontClickListener() {
      @Override public void onClick(String fontPath) {
        // 切换字体
        int textIndex = mEffectSubtitleAdapter.getSelectIndex();
        EffectOPMultiSubtitleFont effectOPSubtitleFont = new EffectOPMultiSubtitleFont(effectIndex, textIndex, fontPath);
        mWorkSpace.handleOperation(effectOPSubtitleFont);
      }
    });
    mRvFont.setAdapter(mEffectFontAdapter);
    mEffectFontAdapter.updateSelectPath(mEffectSubtitleAdapter.getSelectItem().mFontPath);

    if (!mEffectSubtitleAdapter.getSelectItem().isBold) {
      mBtnBlod.setText(R.string.mn_edit_subtitle_blod_off);
    } else {
      mBtnBlod.setText(R.string.mn_edit_subtitle_blod_on);
    }
    if (!mEffectSubtitleAdapter.getSelectItem().isItalic) {
      mBtnItalic.setText(R.string.mn_edit_subtitle_italic_off);
    } else {
      mBtnItalic.setText(R.string.mn_edit_subtitle_italic_on);
    }

    mTextColorSeekBar.setCurrColor(mEffectSubtitleAdapter.getSelectItem().mTextColor);
    mTextColorSeekBar.setCallback(new ColorBarBgView.Callback() {
      @Override public void OnSeekBarChanged(ColorBarBgView colorBarBgView, int currColor) {
        int textIndex = mEffectSubtitleAdapter.getSelectIndex();
        EffectOPMultiSubtitleColor effectOPSubtitleFont = new EffectOPMultiSubtitleColor(effectIndex, textIndex, currColor);
        mWorkSpace.handleOperation(effectOPSubtitleFont);
      }

      @Override public void OnSeekStart() {
      }

      @Override public void OnSeekEnd(int currColor) {
      }
    });
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPMultiSubtitleItalic
          || operate instanceof EffectOPMultiSubtitleBlod) {
        SubtitleEffect curEffect = (SubtitleEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        mEffectSubtitleAdapter.updateDataList(curEffect.getTextBubbleInfo().mTextBubbleList);
        if (!mEffectSubtitleAdapter.getSelectItem().isBold) {
          mBtnBlod.setText(R.string.mn_edit_subtitle_blod_off);
        } else {
          mBtnBlod.setText(R.string.mn_edit_subtitle_blod_on);
        }
        if (!mEffectSubtitleAdapter.getSelectItem().isItalic) {
          mBtnItalic.setText(R.string.mn_edit_subtitle_italic_off);
        } else {
          mBtnItalic.setText(R.string.mn_edit_subtitle_italic_on);
        }
      }
    }
  };

  private OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(mIvInput)) {
        new EditEffectInputDialog(getContext(), mMenuContainer, mWorkSpace, groupId, effectIndex, mEffectSubtitleAdapter.getSelectIndex());
      } else if (v.equals(mBtnAnim)) {
        SubtitleEffect curEffect = (SubtitleEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        if (!curEffect.getTextBubbleInfo().isAnimOn) {
          mBtnAnim.setText(R.string.mn_edit_subtitle_anim_state_on);
        } else {
          mBtnAnim.setText(R.string.mn_edit_subtitle_anim_state_off);
        }
        EffectOPSubtitleAnim effectOPSubtitleAnim = new EffectOPSubtitleAnim(effectIndex, !curEffect.getTextBubbleInfo().isAnimOn);
        mWorkSpace.handleOperation(effectOPSubtitleAnim);
      } else if (v.equals(mBtnBlod)) {
        TextBubble textBubble = mEffectSubtitleAdapter.getSelectItem();
        if (textBubble.isBold) {
          mBtnBlod.setText(R.string.mn_edit_subtitle_blod_off);
        } else {
          mBtnBlod.setText(R.string.mn_edit_subtitle_blod_on);
        }
        EffectOPMultiSubtitleBlod effectOPSubtitleBlod = new EffectOPMultiSubtitleBlod(effectIndex,
            mEffectSubtitleAdapter.getSelectIndex(), !textBubble.isBold);
        mWorkSpace.handleOperation(effectOPSubtitleBlod);
      } else if (v.equals(mBtnItalic)) {
        TextBubble textBubble = mEffectSubtitleAdapter.getSelectItem();
        if (textBubble.isItalic) {
          mBtnItalic.setText(R.string.mn_edit_subtitle_italic_off);
        } else {
          mBtnItalic.setText(R.string.mn_edit_subtitle_italic_on);
        }
        EffectOPMultiSubtitleItalic effectOPSubtitleItalic = new EffectOPMultiSubtitleItalic(effectIndex,
            mEffectSubtitleAdapter.getSelectIndex(), !textBubble.isItalic);
        mWorkSpace.handleOperation(effectOPSubtitleItalic);
      } else if (v.equals(mBtnStroke)) {
        new EffectSubtitleStrokeDialog(getContext(), mMenuContainer, mWorkSpace, groupId, effectIndex,
            mEffectSubtitleAdapter.getSelectIndex());
      } else if (v.equals(mBtnShadow)) {
        new EffectSubtitleShadowDialog(getContext(), mMenuContainer, mWorkSpace, groupId, effectIndex,
            mEffectSubtitleAdapter.getSelectIndex());
      } else if (v.equals(mBtnAlign)) {
        new EffectSubtitleAlignDialog(getContext(), mMenuContainer, mWorkSpace, groupId, effectIndex,
            mEffectSubtitleAdapter.getSelectIndex());
      }
    }
  };

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.removeObserver(mBaseObserver);
    }
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_subtitle_input);
  }
}
