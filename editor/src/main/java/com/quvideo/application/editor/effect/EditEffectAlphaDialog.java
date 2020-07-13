package com.quvideo.application.editor.effect;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.FloatEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAlpha;

public class EditEffectAlphaDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectAlphaDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectAlpha;
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
    int alpha = 100;
    if (baseEffect instanceof FloatEffect) {
      alpha = ((FloatEffect) baseEffect).alpha;
    }
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("100")
        .progress(alpha)
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
    int alpha = mCustomSeekbarPop.getProgress();
    setClipVolume(alpha);
    dismissMenu();
  }

  private void setClipVolume(int alpha) {
    EffectOPAlpha clipOPVolume = new EffectOPAlpha(groupId, effectIndex, alpha);
    mWorkSpace.handleOperation(clipOPVolume);
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_alpha_change);
  }
}
