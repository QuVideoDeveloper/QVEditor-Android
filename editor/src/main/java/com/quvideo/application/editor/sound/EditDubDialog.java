package com.quvideo.application.editor.sound;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.quvideo.application.TimeFormatUtil;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.camera.XYAudioRecorder;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.effect.EffectAddItem;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAdd;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import xiaoying.utils.LogUtils;

public class EditDubDialog extends BaseMenuView {

  private String AUDIO_FILE_PATH;

  private XYAudioRecorder audioRecorder;

  private ImageView btnRecording;
  private TextView tvRecordTime;
  private boolean isRecording;

  private int curPlayerTime = 0;
  private int startTime = 0;
  private int maxTime = 0;

  private int addRecordPos = 0;

  private int count = 0;

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

  @Override protected void initCustomMenu(final Context context, View view) {
    curPlayerTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    maxTime = mWorkSpace.getPlayerAPI().getPlayerControl().getPlayerDuration();

    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
    List<BaseEffect> audio = mWorkSpace.getEffectAPI().getEffectList(QEGroupConst.GROUP_ID_RECORD);
    count = audio == null ? 0 : audio.size();

    btnRecording = view.findViewById(R.id.btnRecord);
    tvRecordTime = view.findViewById(R.id.tvRecorderTime);
    btnRecording.setOnClickListener(v -> {
      onRecordClick(context);
    });
    audioRecorder = new XYAudioRecorder();
  }

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      curPlayerTime = progress;
      if (playerStatus == PlayerStatus.STATUS_PAUSE || playerStatus == PlayerStatus.STATUS_STOP) {
        if (isRecording) {
          onRecordClick(getContext());
        }
      }
    }

    @Override public void onPlayerRefresh() {
      if (mWorkSpace != null
          && mWorkSpace.getPlayerAPI() != null
          && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
        curPlayerTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
      }
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };

  @Override protected void releaseAll() {
    mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
    if (audioRecorder != null) {
      audioRecorder.unInit();
      audioRecorder = null;
    }
  }

  private void onRecordClick(Context context) {
    if (isRecording) {
      btnRecording.setImageResource(R.drawable.cam_shape_recording_bg);
      tvRecordTime.setTextColor(Color.WHITE);

      audioRecorder.stopRecord();
      isRecording = false;

      if (timerDisposable != null) {
        timerDisposable.dispose();
      }
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      mWorkSpace.getPlayerAPI().getPlayerControl().setVolume(100);
      addAudio();
    } else {
      if (maxTime - curPlayerTime < 500) {
        ToastUtils.show(context, R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        return;
      }
      AUDIO_FILE_PATH = context.getExternalCacheDir().getAbsolutePath() + "/testAudio_" + System.currentTimeMillis() + ".mp4";
      LogUtils.d("EditDubDialog", "AUDIO_FILE_PATH = " + AUDIO_FILE_PATH);
      btnRecording.setImageResource(R.drawable.cam_shape_recording_stop_bg);
      tvRecordTime.setTextColor(Color.RED);
      audioRecorder.startRecord(AUDIO_FILE_PATH);
      isRecording = true;
      addRecordPos = curPlayerTime;
      mWorkSpace.getPlayerAPI().getPlayerControl().setVolume(0);
      mWorkSpace.getPlayerAPI().getPlayerControl().play();
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
    int startPos = addRecordPos;
    int recordLength = audioRecorder.getRecordDuration();
    effectAddItem.destRange = new VeRange(startPos, recordLength);
    effectAddItem.trimRange = new VeRange(0, recordLength);
    LogUtils.d("EditDubDialog", "startPos = " + startPos + ", recordLength = " + recordLength);

    EffectOPAdd effectOPAdd = new EffectOPAdd(QEGroupConst.GROUP_ID_RECORD, count, effectAddItem);
    mWorkSpace.handleOperation(effectOPAdd);
    count++;
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
