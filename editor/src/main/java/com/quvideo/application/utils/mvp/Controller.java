package com.quvideo.application.utils.mvp;

/**
 * Every Controller in the app must either implement this interface or extend BaseController
 * indicating the MvpView type that wants to be attached with.
 */
public interface Controller<V extends MvpView> {

  /**
   * attach host
   *
   * @param mvpView host which implement mvp interface
   */
  void attachView(V mvpView);

  /**
   * detach from host
   */
  void detachView();
}
