package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPSplit;
import xiaoying.utils.LogUtils;

public class EditSplitDialog extends BaseMenuView {

  private EditSeekBarController seekBarController;

  private int clipIndex = 0;

  public EditSplitDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;

    seekBarController = new EditSeekBarController();
    showMenu(container, l);
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_split;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    seekBarController.bindView(view.findViewById(R.id.seekbar));
    seekBarController.setTitle(context.getString(R.string.mn_edit_split_progress_title));

    initData();
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    seekBarController.setSeekBarStartText(TimeFormatUtil.INSTANCE.formatTime(0));
    seekBarController.setSeekBarEndText(
        TimeFormatUtil.INSTANCE.formatTime(clipData.getTrimRange().getTimeLength()));
  }

  @Override public void onClick(View v) {
    splitClip();
  }

  private void splitClip() {
    int progress = seekBarController.getSeekBarProgress();
    if (progress != 0 && progress != 100) {
      ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
      int splitTime = progress * clipData.getTrimRange().getTimeLength() / 100;
      LogUtils.d("ClipOP", "split time = " + splitTime);
      ClipOPSplit clipOPSplit = new ClipOPSplit(clipIndex, splitTime);
      mWorkSpace.handleOperation(clipOPSplit);
    }

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_split);
  }
}
