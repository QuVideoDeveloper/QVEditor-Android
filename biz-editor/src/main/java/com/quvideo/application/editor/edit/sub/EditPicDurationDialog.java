package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPPicTrim;

public class EditPicDurationDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int clipIndex = 0;

  private boolean isChanged = false;

  public EditPicDurationDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipPicDuration;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_trim;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar_trim);
    initData();
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    int start = 100;
    int end = 30000;
    VeRange srcTrimRange = mWorkSpace.convertSpeedRange(clipIndex,
        new VeRange(clipData.getTrimRange()), false);
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start(TimeFormatUtil.INSTANCE.formatTime(start))
        .end(TimeFormatUtil.INSTANCE.formatTime(end))
        .progress(srcTrimRange.getTimeLength())
        .seekRange(new CustomSeekbarPop.SeekRange(start, end))
        .progressExchange(progress -> {
          String base = TimeFormatUtil.INSTANCE.formatTime(progress);
          return base + "." + progress % 1000;
        })
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
            isChanged = true;
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
          }
        }));
  }

  @Override public void onClick(View v) {
    trimClip();
  }

  private void trimClip() {
    if (isChanged) {
      int progress = mCustomSeekbarPop.getProgress();

      ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
      VeRange trimRange = new VeRange(0, (int) (progress * clipData.getTimeScale()));
      ClipOPPicTrim clipOPTrimRange = new ClipOPPicTrim(clipIndex, trimRange.getTimeLength());
      mWorkSpace.handleOperation(clipOPTrimRange);
    }
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_clip_img_duration);
  }
}
