package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPVolume;

public class EditVolumeDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int clipIndex = 0;

  public EditVolumeDialog(Context context, MenuContainer container, IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;

    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipVolume;
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
    int volume = clipData.getAudioVolume();
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("100")
        .progress(volume)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            setClipVolume(progress);
          }
        }));
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  private void setClipVolume(int volume) {
    ClipOPVolume clipOPVolume = new ClipOPVolume(clipIndex, volume);
    mWorkSpace.handleOperation(clipOPVolume);
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_volume);
  }
}
