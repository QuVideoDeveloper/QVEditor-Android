package com.quvideo.application.camera;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import com.quvideo.application.editor.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

class CamParamSeekViewMgr {

  interface OnParamChangedListener {

    void onCamZoomChanged(int value);

    void onCamExposureChanged(int value);

    void onCamFbParamChanged(int value);
  }

  enum ParamMode {
    MODE_ZOOM,
    MODE_EXPOSURE,
    MODE_FACE_BEAUTY
  }

  private View rootView;
  private AppCompatTextView tvTitle;
  private AppCompatTextView tvParamValue;
  private SeekBar paramSeekBar;

  private ParamMode paramMode = ParamMode.MODE_ZOOM;
  private OnParamChangedListener onParamChangedListener;

  void bindView(@NonNull Activity activity, @NonNull OnParamChangedListener listener) {
    rootView = activity.findViewById(R.id.paramSettingView);
    if (rootView == null) {
      throw new RuntimeException("invalid param seek view");
    }

    tvTitle = activity.findViewById(R.id.tvTitle);
    tvParamValue = activity.findViewById(R.id.tvParamValue);
    paramSeekBar = activity.findViewById(R.id.seekBar);

    if (tvTitle == null || tvParamValue == null || paramSeekBar == null) {
      throw new RuntimeException("invalid param seek view");
    }

    this.onParamChangedListener = listener;
    initSeekBar();
  }

  private void initSeekBar() {
    Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override public void subscribe(final ObservableEmitter<Integer> emitter) throws Exception {
        paramSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

          @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            emitter.onNext(progress);
          }

          @Override public void onStartTrackingTouch(SeekBar seekBar) {

          }

          @Override public void onStopTrackingTouch(SeekBar seekBar) {

          }
        });
      }
    }).debounce(50, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Integer>() {
          @Override public void onSubscribe(Disposable d) {

          }

          @Override public void onNext(Integer integer) {
            switch (paramMode) {
              case MODE_ZOOM:
                onParamChangedListener.onCamZoomChanged(integer);
                break;
              case MODE_EXPOSURE:
                onParamChangedListener.onCamExposureChanged(integer);
                break;
              case MODE_FACE_BEAUTY:
                onParamChangedListener.onCamFbParamChanged(integer);
                break;
            }
          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onComplete() {

          }
        });
  }

  private void initMode(int defaultValue, int maxValue) {
    switch (paramMode) {
      case MODE_ZOOM:
        tvTitle.setText(R.string.mn_cam_func_zoom);
        break;
      case MODE_EXPOSURE:
        tvTitle.setText(R.string.mn_cam_func_exposure);
        break;
      case MODE_FACE_BEAUTY:
        tvTitle.setText(R.string.mn_cam_func_face_beauty);
    }
    paramSeekBar.setMax(maxValue);
    paramSeekBar.setProgress(defaultValue);
  }

  void showView(ParamMode mode, int defaultValue, int maxValue) {
    paramMode = mode;
    initMode(defaultValue, maxValue);

    rootView.setVisibility(View.VISIBLE);
  }

  void hideView() {
    rootView.setVisibility(View.GONE);
  }

  boolean isViewShown() {
    return rootView.getVisibility() == View.VISIBLE;
  }

  ParamMode getCurMode() {
    return paramMode;
  }

  void setProgressText(String text) {
    tvParamValue.setText(text);
  }
}
