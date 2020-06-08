package com.quvideo.application.editor.effect;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.SeekBar;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.FloatEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAlpha;

public class EditEffectAlphaDialog extends BaseMenuView {

  private EditSeekBarController seekBarController;

  private int groupId = 0;
  private int effectIndex = 0;

  public EditEffectAlphaDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    seekBarController = new EditSeekBarController();
    showMenu(container, l);
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_volume;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    seekBarController.bindView(view.findViewById(R.id.seekbar));
    seekBarController.setSeekBarTextColor(Color.parseColor("#80FFFFFF"));
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
    seekBarController.setSeekBarProgress(alpha);
    seekBarController.setSeekBarStartText("0");
    seekBarController.setSeekBarEndText("100");
    seekBarController.setMaxProgress(100);
    seekBarController.setProgressText(alpha + "");

    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarController.setProgressText(progress + "");
        setClipVolume(progress);
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
  }

  @Override public void onClick(View v) {
    int alpha = seekBarController.getSeekBarProgress();
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