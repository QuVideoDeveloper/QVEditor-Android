package com.quvideo.application.editor.effect.subtitle;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.mobile.engine.model.effect.TextBubble;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMultiSubtitleAlign;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;

public class EffectSubtitleAlignDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private int textIndex = 0;

  private ImageView mIvAlignLeft;
  private ImageView mIvAlignCenter;
  private ImageView mIvAlignRight;

  public EffectSubtitleAlignDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, int textIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    this.textIndex = textIndex;
    showMenu(container, null);
    EffectOPStaticPic effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, true);
    mWorkSpace.handleOperation(effectOPStaticPic);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectSubtitleAlign;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_subtitle_align;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mIvAlignLeft = view.findViewById(R.id.btn_align_left);
    mIvAlignCenter = view.findViewById(R.id.btn_align_center);
    mIvAlignRight = view.findViewById(R.id.btn_align_right);

    mIvAlignLeft.setOnClickListener(mOnClickListener);
    mIvAlignCenter.setOnClickListener(mOnClickListener);
    mIvAlignRight.setOnClickListener(mOnClickListener);
  }

  private OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(mIvAlignLeft)) {
        EffectOPMultiSubtitleAlign effectOPSubtitleAlign = new EffectOPMultiSubtitleAlign(effectIndex, textIndex,
            TextBubble.ALIGNMENT_LEFT | TextBubble.ALIGNMENT_VER_CENTER);
        mWorkSpace.handleOperation(effectOPSubtitleAlign);
      } else if (v.equals(mIvAlignCenter)) {
        EffectOPMultiSubtitleAlign effectOPSubtitleAlign = new EffectOPMultiSubtitleAlign(effectIndex, textIndex,
            TextBubble.ALIGNMENT_HOR_CENTER | TextBubble.ALIGNMENT_VER_CENTER);
        mWorkSpace.handleOperation(effectOPSubtitleAlign);
      } else if (v.equals(mIvAlignRight)) {
        EffectOPMultiSubtitleAlign effectOPSubtitleAlign = new EffectOPMultiSubtitleAlign(effectIndex, textIndex,
            TextBubble.ALIGNMENT_RIGHT | TextBubble.ALIGNMENT_VER_CENTER);
        mWorkSpace.handleOperation(effectOPSubtitleAlign);
      }
    }
  };

  @Override protected void releaseAll() {
    EffectOPStaticPic effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, false);
    mWorkSpace.handleOperation(effectOPStaticPic);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_subtitle_align);
  }
}
