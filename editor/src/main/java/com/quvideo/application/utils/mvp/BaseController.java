package com.quvideo.application.utils.mvp;

/**
 * @author Elijah <a href="zhonghu.liu@quvideo.com">Contact me.</a>
 * @since 2018/1/12
 * Base class that implements the Controller interface and provides a base implementation for
 * {@link #attachView(T mvpView)}and {@link #detachView()}. It also handles keeping a reference
 * to the MvpView that
 * can be accessed from the children classes by calling {@link #getMvpView()}.
 */
public abstract class BaseController<T extends MvpView> implements Controller<T> {

  private T mvpView;

  public BaseController(T mvpView) {
    this.attachView(mvpView);
  }

  @Override public void attachView(T mvpView) {
    this.mvpView = mvpView;
  }

  @Override public void detachView() {
    mvpView = null;
  }

  public boolean isViewAttached() {
    return mvpView != null;
  }

  public T getMvpView() {
    return mvpView;
  }

  public void checkViewAttached() {
    if (!isViewAttached()) {
      throw new ViewNotAttachedException();
    }
  }

  public static class ViewNotAttachedException extends RuntimeException {
    public ViewNotAttachedException() {
      super("Please call Controller.attachView(MvpView) before"
          + " requesting data to the Controller");
    }
  }
}
