package com.quvideo.application.editor.sound;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.quvideo.application.editor.R;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.camera.XYAudioRecorder;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.effect.EffectAddItem;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAdd;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;
import xiaoying.utils.LogUtils;

public class EditDubDialog extends BaseMenuView {

  private String AUDIO_FILE_PATH;

  private XYAudioRecorder audioRecorder;

  private ImageView btnRecording;
  private TextView tvRecordTime;
  private boolean isRecording;

  public EditDubDialog(Context context, MenuContainer container, IQEWorkSpace workSpace) {
    super(context, workSpace);
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.AudioRecord;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_dub;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    AUDIO_FILE_PATH = context.getExternalCacheDir().getAbsolutePath() + "/testAudio.mp4";
    LogUtils.d("EditDubDialog", "AUDIO_FILE_PATH = " + AUDIO_FILE_PATH);

    btnRecording = view.findViewById(R.id.btnRecord);
    tvRecordTime = view.findViewById(R.id.tvRecorderTime);

    btnRecording.setOnClickListener(v -> {
      onRecordClick();
    });

    audioRecorder = new XYAudioRecorder();
  }

  @Override protected void releaseAll() {
    if (audioRecorder != null) {
      audioRecorder.unInit();
      audioRecorder = null;
    }
  }

  private void onRecordClick() {
    if (isRecording) {
      btnRecording.setImageResource(R.drawable.cam_shape_recording_bg);
      tvRecordTime.setTextColor(Color.WHITE);

      audioRecorder.stopRecord();
      isRecording = false;

      if (timerDisposable != null) {
        timerDisposable.dispose();
      }
      addAudio();
    } else {
      btnRecording.setImageResource(R.drawable.cam_shape_recording_stop_bg);
      tvRecordTime.setTextColor(Color.RED);

      audioRecorder.startRecord(AUDIO_FILE_PATH);
      isRecording = true;

      startTimer();
    }
  }

  private Disposable timerDisposable;

  private void startTimer() {
    Observable.interval(0, 1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Long>() {
          @Override public void onSubscribe(Disposable d) {
            timerDisposable = d;
          }

          @Override public void onNext(Long aLong) {
            tvRecordTime.setText(TimeFormatUtil.INSTANCE.formatTime(aLong * 1000));
          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onComplete() {

          }
        });
  }

  private void addAudio() {
    EffectAddItem effectAddItem = new EffectAddItem();
    effectAddItem.mEffectPath = AUDIO_FILE_PATH;

    int startPos = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();

    int recordLength = audioRecorder.getRecordDuration();
    effectAddItem.destRange = new VeRange(startPos, recordLength);
    effectAddItem.trimRange = new VeRange(0, recordLength);
    LogUtils.d("EditDubDialog", "startPos = " + startPos + ", recordLength = " + recordLength);

    EffectOPAdd effectOPAdd = new EffectOPAdd(QEGroupConst.GROUP_ID_RECORD, 0, effectAddItem);
    mWorkSpace.handleOperation(effectOPAdd);
  }

  @Override public void onClick(View v) {
    if (!isRecording) {
      super.onClick(v);
    } else {
      ToastUtils.show(getContext(), "录音中...", Toast.LENGTH_LONG);
    }
  }

  @Override public void dismissMenu() {
    if (!isRecording) {
      super.dismissMenu();
    } else {
      ToastUtils.show(getContext(), "录音中...", Toast.LENGTH_LONG);
    }
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_dub_record);
  }
}
