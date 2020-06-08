package com.quvideo.application.utils.mvp;

/**
 * @author Elijah <a href="zhonghu.liu@quvideo.com">Contact me.</a>
 * @since 2018/1/12
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
