package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPSpeed;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class EditSpeedDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;
  private TextView tvProgress;

  private int clipIndex = 0;

  public EditSpeedDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;

    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipSpeed;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_speed;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    tvProgress = view.findViewById(R.id.tv_progress);

    initData();
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    int speed = exchangeScale2Progress(clipData.getTimeScale());
    tvProgress.setText(mathClipSpeedText(speed, 100));
    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0.25x")
        .end("4x")
        .progress(speed)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .progressExchange(new CustomSeekbarPop.IProgressExchange() {
          @Override public String onProgressExchange(int progress) {
            return mathClipSpeedText(progress, 100);
          }
        }).seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            tvProgress.setText(mathClipSpeedText(progress, 100));
          }
        }));
  }

  private int exchangeScale2Progress(float scale) {
    float speed = 100 / scale / 100;

    int halfMaxProgress = 100 / 2;
    if (speed == 1.0f) {
      return halfMaxProgress;
    }

    if (speed < 1.0f) {
      return (int) ((speed - 0.25f) * halfMaxProgress / 0.75);
    }

    return (int) ((speed - 1.0f) * halfMaxProgress / 3f + halfMaxProgress);
  }

  @Override public void onClick(View v) {
    speedClip();
  }

  private void speedClip() {
    float speedSel = mathSpeednValue(mCustomSeekbarPop.getProgress(), 100);
    ClipOPSpeed clipOPSpeed = new ClipOPSpeed(clipIndex, 1.0f / speedSel, true);
    mWorkSpace.handleOperation(clipOPSpeed);

    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_speed);
  }

  private static float mathSpeednValue(int progress, int maxProgress) {
    int halfMaxProgress = maxProgress / 2;
    if (progress == halfMaxProgress) {
      return 1f;
    }

    if (progress < halfMaxProgress) {
      float scale = 0.25f + 0.75f / halfMaxProgress * progress;
      scale = Math.round(scale * 100) / 100f;
      return scale;
    }

    float scale = 1f + 3f / halfMaxProgress * (progress - halfMaxProgress);
    scale = Math.round(scale * 10) / 10f;
    BigDecimal b = new BigDecimal(scale);
    return b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
  }

  private static String mathClipSpeedText(int progress, int maxProgress) {
    int halfMaxProgress = maxProgress / 2;
    if (progress == halfMaxProgress) {
      return "1.0";
    }

    if (progress < halfMaxProgress) {
      float scale = 0.25f + 0.75f / halfMaxProgress * progress;
      scale = Math.round(scale * 100) / 100f;
      DecimalFormat df = new DecimalFormat(scale > 1f ? "#.00" : "0.00");
      return df.format(scale);
    }

    float scale = 1f + 3f / halfMaxProgress * (progress - halfMaxProgress);
    scale = Math.round(scale * 10) / 10f;
    DecimalFormat df = new DecimalFormat(scale > 1f ? "#.00" : "0.00");
    return df.format(scale);
  }
}
