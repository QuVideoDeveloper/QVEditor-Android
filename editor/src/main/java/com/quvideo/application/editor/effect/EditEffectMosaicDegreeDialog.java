package com.quvideo.application.editor.effect;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.MosaicEffect;
import com.quvideo.mobile.engine.model.effect.MosaicInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMosaicInfo;

public class EditEffectMosaicDegreeDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectMosaicDegreeDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectTone;
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
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    int degree = 0;
    if (baseEffect instanceof MosaicEffect) {
      degree = (int) ((MosaicEffect) baseEffect).getMosaicInfo().horValue;
    }
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("100")
        .progress(degree)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            MosaicInfo mosaicInfo = new MosaicInfo(progress, progress);
            EffectOPMosaicInfo effectOpTone = new EffectOPMosaicInfo(effectIndex, mosaicInfo);
            mWorkSpace.handleOperation(effectOpTone);
          }
        }));
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_mosaic_degree);
  }
}
