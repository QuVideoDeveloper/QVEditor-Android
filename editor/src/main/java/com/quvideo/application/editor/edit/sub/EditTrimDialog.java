package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPTrimRange;

import xiaoying.utils.LogUtils;

public class EditTrimDialog extends BaseMenuView {

    private EditSeekBarController startSeekBarController;
    private EditSeekBarController endSeekBarController;

    private int clipIndex = 0;

    public EditTrimDialog(Context context, MenuContainer container,
                          IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
        super(context, workSpace);
        this.clipIndex = clipIndex;

        startSeekBarController = new EditSeekBarController();
        endSeekBarController = new EditSeekBarController();
        showMenu(container, l);
    }

    @Override
    protected int getCustomLayoutId() {
        return R.layout.dialog_edit_trim;
    }

    @Override
    protected void initCustomMenu(Context context, View view) {
        startSeekBarController.bindView(view.findViewById(R.id.seekbar_start));
        endSeekBarController.bindView(view.findViewById(R.id.seekbar_end));

        startSeekBarController.setTitle(context.getString(R.string.mn_edit_title_start));
        endSeekBarController.setTitle(context.getString(R.string.mn_edit_title_end));

        initData();
    }

    @Override
    protected void releaseAll() {
    }

    private void initData() {
        ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
        startSeekBarController.setSeekBarStartText(TimeFormatUtil.INSTANCE.formatTime(0));
        endSeekBarController.setSeekBarStartText(TimeFormatUtil.INSTANCE.formatTime(0));
        startSeekBarController.setSeekBarEndText(TimeFormatUtil.INSTANCE.formatTime(clipData.getSrcRange().getTimeLength()));
        endSeekBarController.setSeekBarEndText(TimeFormatUtil.INSTANCE.formatTime(clipData.getSrcRange().getTimeLength()));
    }

    @Override
    public void onClick(View v) {
        trimClip();
    }

    private void trimClip() {
        int progressStart = startSeekBarController.getSeekBarProgress();
        int progressEnd = endSeekBarController.getSeekBarProgress();

        if (progressStart >= progressEnd) {
            ToastUtils.show(getContext(), R.string.mn_edit_tips_no_allow_trim, Toast.LENGTH_SHORT);
            return;
        }

        ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
        int trimStart = progressStart * clipData.getSrcRange().getTimeLength() / 100 + clipData.getSrcRange().getPosition();
        int trimEnd = progressEnd * clipData.getSrcRange().getTimeLength() / 100 + clipData.getSrcRange().getPosition();
        LogUtils.d("ClipOP", "trimStart = " + trimStart + " , trimEnd = " + trimEnd);
        ClipOPTrimRange clipOPTrimRange = new ClipOPTrimRange(clipIndex, new VeRange(trimStart, trimEnd - trimStart));
        mWorkSpace.handleOperation(clipOPTrimRange);

        dismissMenu();
    }

    @Override
    protected String getBottomTitle() {
        return getContext().getString(R.string.mn_edit_title_trim);
    }
}
