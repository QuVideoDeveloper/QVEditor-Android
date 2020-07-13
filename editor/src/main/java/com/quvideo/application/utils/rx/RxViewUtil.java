package com.quvideo.application.utils.rx;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.view.View;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import java.util.concurrent.TimeUnit;

/**
 * @desc 目前屏蔽闪击
 */
public class RxViewUtil {

  private static final int MIN_CLICK_DEFAULT_DELAY_TIME = 500;

  /**
   * 防止重复点击
   *
   * @param target 目标view
   * @param listener 监听器
   */
  @SuppressWarnings("ResultOfMethodCallIgnored") @SuppressLint("CheckResult")
  public static void setOnClickListener(final RxClickAction<View> listener,
      @NonNull View... target) {
    for (View view : target) {
      RxViewUtil.onClick(view)
          .throttleFirst(MIN_CLICK_DEFAULT_DELAY_TIME, TimeUnit.MILLISECONDS)
          .subscribe(new Consumer<View>() {
            @Override public void accept(@NonNull View view) throws Exception {
              if (listener != null) {
                listener.onClick(view);
              }
            }
          });
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored") @SuppressLint("CheckResult")
  public static void setOnClickListener(final RxClickAction<View> listener, long timeMills,
      @NonNull View... target) {
    for (View view : target) {
      RxViewUtil.onClick(view)
          .throttleFirst(timeMills, TimeUnit.MILLISECONDS)
          .subscribe(new Consumer<View>() {
            @Override public void accept(@NonNull View view) throws Exception {
              if (listener != null) {
                listener.onClick(view);
              }
            }
          });
    }
  }

  /**
   * 监听onclick事件防抖动
   */
  @CheckResult @NonNull private static Observable<View> onClick(@NonNull View view) {
    checkNotNull(view, "view == null");
    return Observable.create(new ViewClickOnSubscribe(view));
  }

  /**
   * onclick事件防抖动
   * 返回view
   */
  private static class ViewClickOnSubscribe implements ObservableOnSubscribe<View> {
    private View view;

    ViewClickOnSubscribe(View view) {
      this.view = view;
    }

    @Override public void subscribe(@NonNull final ObservableEmitter<View> e) throws Exception {
      checkUiThread();

      View.OnClickListener listener = new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (!e.isDisposed()) {
            e.onNext(view);
          }
        }
      };
      view.setOnClickListener(listener);
    }
  }

  /**
   * A one-argument action. 点击事件转发接口
   *
   * @param <V> the first argument type
   */
  public interface RxClickAction<V> {
    /**
     * 点击interface
     *
     * @param v View
     */
    void onClick(V v);
  }

  private static <T> T checkNotNull(T value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
    return value;
  }

  private static void checkUiThread() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
      throw new IllegalStateException(
          "Must be called from the main thread. Was: " + Thread.currentThread());
    }
  }
}
