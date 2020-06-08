package com.quvideo.application.editor.effect;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.quvideo.application.editor.R;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPDestRange;

public class EditEffectTrimDialog extends BaseMenuView {

  private EditSeekBarController startSeekBarController;
  private EditSeekBarController endSeekBarController;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectTrimDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    startSeekBarController = new EditSeekBarController();
    endSeekBarController = new EditSeekBarController();
    showMenu(container, l);
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_trim;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    startSeekBarController.bindView(view.findViewById(R.id.seekbar_start));
    endSeekBarController.bindView(view.findViewById(R.id.seekbar_end));

    startSeekBarController.setTitle(context.getString(R.string.mn_edit_title_start));
    endSeekBarController.setTitle(context.getString(R.string.mn_edit_title_end));

    initData();
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    startSeekBarController.setSeekBarStartText(TimeFormatUtil.INSTANCE.formatTime(0));
    endSeekBarController.setSeekBarStartText(TimeFormatUtil.INSTANCE.formatTime(0));
    startSeekBarController.setSeekBarEndText(TimeFormatUtil.INSTANCE.formatTime(mWorkSpace.getStoryboardAPI().getDuration()));
    endSeekBarController.setSeekBarEndText(TimeFormatUtil.INSTANCE.formatTime(mWorkSpace.getStoryboardAPI().getDuration()));
    startSeekBarController.setMaxProgress(mWorkSpace.getStoryboardAPI().getDuration() / 1000);
    endSeekBarController.setMaxProgress(mWorkSpace.getStoryboardAPI().getDuration() / 1000);
    startSeekBarController.setSeekBarProgress(baseEffect.destRange.getPosition() / 1000);
    int end = mWorkSpace.getStoryboardAPI().getDuration() / 1000;
    if (baseEffect.destRange.getTimeLength() >= 0) {
      end = Math.min(end, baseEffect.destRange.getLimitValue() / 1000);
    }
    endSeekBarController.setSeekBarProgress(end);
  }

  @Override public void onClick(View v) {
    trimClip();
  }

  private void trimClip() {
    int progressStart = startSeekBarController.getSeekBarProgress();
    int progressEnd = endSeekBarController.getSeekBarProgress();

    if (progressStart >= progressEnd) {
      ToastUtils.show(getContext(), R.string.mn_edit_tips_no_allow_trim, Toast.LENGTH_SHORT);
      return;
    }
    EffectOPDestRange effectOpDestRange = new EffectOPDestRange(groupId, effectIndex, new VeRange(progressStart * 1000,
        (progressEnd - progressStart) * 1000));
    mWorkSpace.handleOperation(effectOpDestRange);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_trim);
  }
}
