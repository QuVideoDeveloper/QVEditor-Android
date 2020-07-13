package com.quvideo.application.editor.effect;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPVolume;

public class EditEffectVolumeDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectVolumeDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectVolume;
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
    int volume = baseEffect.audioVolume;
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("200")
        .progress(volume)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 200)));
  }

  @Override public void onClick(View v) {
    setClipVolume();
  }

  private void setClipVolume() {
    int toVolume = mCustomSeekbarPop.getProgress();
    EffectOPVolume effectOPVolume = new EffectOPVolume(groupId, effectIndex, toVolume);
    mWorkSpace.handleOperation(effectOPVolume);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_volume);
  }
}
