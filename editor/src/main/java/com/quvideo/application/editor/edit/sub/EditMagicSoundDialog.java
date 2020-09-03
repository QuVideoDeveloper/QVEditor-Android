package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPMagicSound;

public class EditMagicSoundDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int clipIndex = 0;

  public EditMagicSoundDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;

    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipMagicSound;
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
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("-60")
        .end("60")
        .progress((int) clipData.getSoundTone())
        .seekRange(new CustomSeekbarPop.SeekRange(-60, 60)));
  }

  @Override public void onClick(View v) {
    magicSoundClip();
  }

  private void magicSoundClip() {
    float value = mCustomSeekbarPop.getProgress();
    ClipOPMagicSound clipOPMagicSound = new ClipOPMagicSound(clipIndex, value);
    mWorkSpace.handleOperation(clipOPMagicSound);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_change_voice);
  }
}
