package com.quvideo.application.editor.effect;

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
import com.quvideo.mobile.engine.entity.VideoInfo;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPDestRange;

public class EditEffectTrimDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int groupId = 0;
  private int effectIndex = 0;

  private boolean isChanged = false;

  public EditEffectTrimDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectTrim;
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
    VideoInfo videoInfo = MediaFileUtils.getVideoInfo(baseEffect.mEffectPath);
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start(TimeFormatUtil.INSTANCE.formatTime(0))
        .end(TimeFormatUtil.INSTANCE.formatTime(mWorkSpace.getStoryboardAPI().getDuration()))
        .progress(baseEffect.destRange.getPosition())
        .secondProgress(baseEffect.destRange.getTimeLength() < 0 ?
            (mWorkSpace.getStoryboardAPI().getDuration()) : baseEffect.destRange.getLimitValue())
        .minRange(1)
        .seekRange(new CustomSeekbarPop.SeekRange(0, mWorkSpace.getStoryboardAPI().getDuration()))
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
      EffectOPDestRange effectOpDestRange = new EffectOPDestRange(groupId, effectIndex, new VeRange(progressStart,
          (progressEnd - progressStart)));
      mWorkSpace.handleOperation(effectOpDestRange);
    }
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_trim);
  }
}
