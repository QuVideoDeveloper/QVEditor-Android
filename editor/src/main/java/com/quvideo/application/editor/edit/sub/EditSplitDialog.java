package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPSplit;
import xiaoying.utils.LogUtils;

public class EditSplitDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int clipIndex = 0;

  public EditSplitDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipSplit;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_split;
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
        .start(TimeFormatUtil.INSTANCE.formatTime(0))
        .end(TimeFormatUtil.INSTANCE.formatTime(clipData.getTrimRange().getTimeLength()))
        .progress(0)
        .seekRange(new CustomSeekbarPop.SeekRange(0, clipData.getTrimRange().getTimeLength())));
  }

  @Override public void onClick(View v) {
    splitClip();
  }

  private void splitClip() {
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    int progress = mCustomSeekbarPop.getProgress();
    if (progress != 0 && progress != clipData.getTrimRange().getTimeLength()) {
      LogUtils.d("ClipOP", "split time = " + progress);
      ClipOPSplit clipOPSplit = new ClipOPSplit(clipIndex, progress);
      mWorkSpace.handleOperation(clipOPSplit);
    }

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_split);
  }
}
