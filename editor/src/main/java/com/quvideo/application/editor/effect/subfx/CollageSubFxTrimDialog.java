package com.quvideo.application.editor.effect.subfx;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.quvideo.application.EditorApp;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.effect.EffectSubFx;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubFxDestRange;
import java.util.List;

public class CollageSubFxTrimDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int groupId = 0;
  private int effectIndex = 0;
  private int subType = 0;

  private boolean isChanged = false;

  public CollageSubFxTrimDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex, int subType) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    this.subType = subType;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.CollageFxTrim;
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
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    List<EffectSubFx> effectSubFxes = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex)).mEffectSubFxList;
    for (EffectSubFx item : effectSubFxes) {
      if (item.getSubType() == subType) {
        int maxLength = baseEffect.destRange.getTimeLength() < 0 ?
            (mWorkSpace.getStoryboardAPI().getDuration() - item.getDestRange().getPosition()) : baseEffect.destRange.getTimeLength();
        mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
            .start(TimeFormatUtil.INSTANCE.formatTime(0))
            .end(TimeFormatUtil.INSTANCE.formatTime(maxLength))
            .progress(item.getDestRange().getPosition())
            .secondProgress(item.getDestRange().getTimeLength() < 0 ?
                maxLength : item.getDestRange().getLimitValue())
            .minRange(1)
            .seekRange(new CustomSeekbarPop.SeekRange(0, maxLength))
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
        return;
      }
    }
    ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
        R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
    dismissMenu();
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
      EffectOPSubFxDestRange effectOPSubFxDestRange = new EffectOPSubFxDestRange(groupId, effectIndex, subType, new VeRange(progressStart,
          (progressEnd - progressStart)));
      mWorkSpace.handleOperation(effectOPSubFxDestRange);
    }
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_trim);
  }
}
