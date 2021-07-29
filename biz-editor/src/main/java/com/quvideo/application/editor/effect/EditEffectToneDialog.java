package com.quvideo.application.editor.effect;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.mobile.engine.model.AudioEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAudioTone;

public class EditEffectToneDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectToneDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectTone;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_volume;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    initData();
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    int tone = 0;
    if (baseEffect instanceof AudioEffect) {
      tone = (int) ((AudioEffect) baseEffect).getAudioInfo().soundTone;
    }
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("-60")
        .end("60")
        .progress(tone)
        .seekRange(new CustomSeekbarPop.SeekRange(-60, 60)));
  }

  private String getToneStr(int progress) {
    return (progress - 60) + "";
  }

  @Override public void onClick(View v) {
    setClipVolume();
  }

  private void setClipVolume() {
    int toVolume = mCustomSeekbarPop.getProgress();
    EffectOPAudioTone effectOpTone = new EffectOPAudioTone(groupId, effectIndex, toVolume);
    mWorkSpace.handleOperation(effectOpTone);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_change_voice);
  }
}
