package com.quvideo.application.editor.effect.subtitle;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.subtitle.view.ColorBarBgView;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.model.SubtitleEffect;
import com.quvideo.mobile.engine.model.effect.StrokeInfo;
import com.quvideo.mobile.engine.model.effect.TextBubble;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMultiSubtitleStroke;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;

public class EffectSubtitleStrokeDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private int textIndex = 0;

  // 字体颜色选取
  private ColorBarBgView mTextColorSeekBar;
  private CustomSeekbarPop mCustomSeekbarPop;

  public EffectSubtitleStrokeDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
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
    return MenuType.EffectSubtitleStroke;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_subtitle_stroke;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mTextColorSeekBar = view.findViewById(R.id.colorbar_stroke_color);
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);

    SubtitleEffect curEffect = (SubtitleEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    TextBubble textBubble = curEffect.getTextBubbleInfo().mTextBubbleList.get(textIndex);

    StrokeInfo strokeInfo = textBubble.mStrokeInfo;
    if (strokeInfo == null) {
      strokeInfo = new StrokeInfo();
    }
    mTextColorSeekBar.setCurrColor(strokeInfo.strokeColor);
    mTextColorSeekBar.setCallback(new ColorBarBgView.Callback() {
      @Override public void OnSeekBarChanged(ColorBarBgView colorBarBgView, int currColor) {
        StrokeInfo strokeInfo = new StrokeInfo();
        strokeInfo.strokeColor = currColor;
        strokeInfo.strokeWPersent = mCustomSeekbarPop.getProgress() / 100f;
        EffectOPMultiSubtitleStroke effectOPSubtitleFont = new EffectOPMultiSubtitleStroke(effectIndex, textIndex, strokeInfo);
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
        .progress((int) (strokeInfo.strokeWPersent * 100))
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            StrokeInfo strokeInfo = new StrokeInfo();
            strokeInfo.strokeColor = mTextColorSeekBar.getCurrColor();
            strokeInfo.strokeWPersent = progress / 100f;
            EffectOPMultiSubtitleStroke effectOPSubtitleFont = new EffectOPMultiSubtitleStroke(effectIndex, textIndex, strokeInfo);
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
