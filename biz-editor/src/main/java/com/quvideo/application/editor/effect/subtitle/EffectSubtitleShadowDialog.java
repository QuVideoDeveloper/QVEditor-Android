package com.quvideo.application.editor.effect.subtitle;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.ColorBarBgView;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.model.SubtitleEffect;
import com.quvideo.mobile.engine.model.effect.ShadowInfo;
import com.quvideo.mobile.engine.model.effect.TextBubble;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMultiSubtitleShadow;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;

public class EffectSubtitleShadowDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private int textIndex = 0;

  // 字体颜色选取
  private ColorBarBgView mTextColorSeekBar;
  private CustomSeekbarPop mCustomSeekbarPop;

  public EffectSubtitleShadowDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
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
    return MenuType.EffectSubtitleShadow;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_subtitle_shadow;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mTextColorSeekBar = view.findViewById(R.id.colorbar_shadow_color);
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);

    SubtitleEffect curEffect = (SubtitleEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    TextBubble textBubble = curEffect.getTextBubbleInfo().mTextBubbleList.get(textIndex);

    ShadowInfo shadowInfo = textBubble.mShadowInfo;
    if (shadowInfo == null) {
      shadowInfo = new ShadowInfo();
    }
    mTextColorSeekBar.setColorType(ColorBarBgView.ColorType.TEXT_SHADOW);
    mTextColorSeekBar.setCurrColor(shadowInfo.shadowColor);
    mTextColorSeekBar.setCallback(new ColorBarBgView.Callback() {
      @Override public void OnSeekBarChanged(ColorBarBgView colorBarBgView, int currColor) {
        ShadowInfo shadowInfo = new ShadowInfo();
        shadowInfo.shadowColor = currColor;
        shadowInfo.shadowBlurRadius = mCustomSeekbarPop.getProgress() / 100f;
        if (shadowInfo.shadowBlurRadius == 0) {
          shadowInfo.enable = false;
        } else {
          shadowInfo.enable = true;
        }
        EffectOPMultiSubtitleShadow effectOPSubtitleFont = new EffectOPMultiSubtitleShadow(effectIndex, textIndex, shadowInfo);
        mWorkSpace.handleOperation(effectOPSubtitleFont);
      }

      @Override public void OnSeekStart() {
      }

      @Override public void OnSeekEnd(int currColor) {
      }
    });
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("100")
        .progress(shadowInfo.enable ? (int) (shadowInfo.shadowBlurRadius * 100) : 0)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            ShadowInfo shadowInfo = new ShadowInfo();
            shadowInfo.shadowColor = mTextColorSeekBar.getCurrColor();
            shadowInfo.shadowBlurRadius = progress / 100f;
            if (progress == 0) {
              shadowInfo.enable = false;
            } else {
              shadowInfo.enable = true;
            }
            EffectOPMultiSubtitleShadow effectOPSubtitleFont = new EffectOPMultiSubtitleShadow(effectIndex, textIndex, shadowInfo);
            mWorkSpace.handleOperation(effectOPSubtitleFont);
          }
        }));
  }

  @Override protected void releaseAll() {
    EffectOPStaticPic effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, false);
    mWorkSpace.handleOperation(effectOPStaticPic);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_subtitle_stroke);
  }
}
