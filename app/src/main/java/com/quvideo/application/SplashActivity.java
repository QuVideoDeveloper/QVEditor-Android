package com.quvideo.application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.quvideo.application.utils.rx.RetryWithDelay;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {
  private static final String TAG = "SplashActivity";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_layout);
    processLaunchMain();
  }

  private void processLaunchMain() {
    Observable.create((ObservableOnSubscribe<Boolean>) emitter -> emitter.onNext(true))
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .delay(2500, TimeUnit.MILLISECONDS)
        .map(a -> {
          if (!EditorApplication.initApplicationOver) {
            throw new RuntimeException("App Asset not ready,please retry!");
          } else {
            return true;
          }
        })
        .retryWhen(new RetryWithDelay(20, 350))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Boolean>() {
          @Override public void onSubscribe(Disposable d) {
          }

          @Override public void onNext(Boolean result) {
            go2MainActivity();
          }

          @Override public void onError(Throwable e) {
            //app data init error after retry
            Log.e(TAG, "App data init error after retry...");
            go2MainActivity();
          }

          @Override public void onComplete() {
          }
        });
  }

  @Override public void onBackPressed() {
  }

  private void go2MainActivity() {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}