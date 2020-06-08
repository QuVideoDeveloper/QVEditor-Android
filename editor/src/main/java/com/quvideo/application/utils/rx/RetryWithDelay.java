package com.quvideo.application.utils.rx;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import java.util.concurrent.TimeUnit;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc rx error retrywhen with delay timemillis
 * @since 2018/7/6
 */

public class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {
  private static final String TAG = "RetryWithDelay";
  /**
   * 重试Function
   * maxRetries 重试次数
   * retryDelayMillis 重试间隔
   */
  private final int maxRetries;
  private final int retryDelayMillis;
  private int retryCount;

  public RetryWithDelay(int maxRetries, int retryDelayMillis) {
    this.maxRetries = maxRetries;
    this.retryDelayMillis = retryDelayMillis;
  }

  @Override public Observable<?> apply(Observable<? extends Throwable> attempts) {
    return attempts.flatMap(new Function<Throwable, Observable<?>>() {
      @Override public Observable<?> apply(Throwable throwable) {
        if (++retryCount <= maxRetries) {
          // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).

          return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
        }
        // Max retries hit. Just pass the error along.
        return Observable.error(throwable);
      }
    });
  }
}
