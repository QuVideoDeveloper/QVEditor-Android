package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPTrimRange;
import xiaoying.utils.LogUtils;

public class EditTrimDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int clipIndex = 0;

  private boolean isChanged = false;

  public EditTrimDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipTrim;
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
    int start = clipData.getSrcRange().getPosition() + (clipData.getSrcRange().getPosition() == 0 ? 0 : 1);
    int end = clipData.getSrcRange().getLimitValue();
    VeRange srcTrimRange = new VeRange((int) (clipData.getTrimRange().getPosition() / clipData.getTimeScale()),
        (int) (clipData.getTrimRange().getTimeLength() / clipData.getTimeScale()));
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start(TimeFormatUtil.INSTANCE.formatTime(start))
        .end(TimeFormatUtil.INSTANCE.formatTime(end))
        .progress(srcTrimRange.getPosition())
        .secondProgress(srcTrimRange.getLimitValue())
        .minRange(1)
        .seekRange(new CustomSeekbarPop.SeekRange(start, end))
        .progressExchange(progress -> {
          String base = TimeFormatUtil.INSTANCE.formatTime(progress);
          return base + "." + progress % 1000;
        })
        .isDoubleMode(true).seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
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
      int progressStart = mCustomSeekbarPop.getFirstProgress();
      int progressEnd = mCustomSeekbarPop.getSecondProgress();

      if (progressStart >= progressEnd) {
        ToastUtils.show(getContext(), R.string.mn_edit_tips_no_allow_trim, Toast.LENGTH_SHORT);
        return;
      }
      LogUtils.d("ClipOP", "progressStart = " + progressStart + " , progressEnd = " + progressEnd);
      ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
      VeRange trimRange = new VeRange((int) (progressStart * clipData.getTimeScale()),
          (int) ((progressEnd - progressStart) * clipData.getTimeScale()));
      ClipOPTrimRange clipOPTrimRange = new ClipOPTrimRange(clipIndex, trimRange);
      mWorkSpace.handleOperation(clipOPTrimRange);
    }
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_trim);
  }
}
