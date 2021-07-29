package com.quvideo.application.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.quvideo.application.common.R;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * VEToast is class to avoid to many toast instance showing at the same time.
 * now VEToast will be one instance to the same resId.
 * Note: please don't used in muti-thread
 */
public class ToastUtils {
  static Toast mToast;
  static String mMessage = "";
  static WeakReference<Thread> mThreadRef = null;
  static Handler mHandler = new Handler(Looper.getMainLooper());
  /**
   * 304dp(<dimen name="editor_board_whole_height">304dp</dimen>) 播放区域离底部的高度  + 36dp margin
   */
  private static float COMMON_VERTICAL_MARGIN = DeviceSizeUtil.dpToPixel(350);

  public static void show(Context context, int resId, int duration, int gravity) {
    if (context == null) {
      return;
    }

    try {
      String msg = context.getString(resId);
      if (!TextUtils.isEmpty(msg)) {
        show(context, msg, duration, gravity, 0, COMMON_VERTICAL_MARGIN);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void show(Context context, String msg, int duration, int gravity) {
    if (context == null) {
      return;
    }
    if (!TextUtils.isEmpty(msg)) {
      show(context, msg, duration, gravity, 0, COMMON_VERTICAL_MARGIN);
    }
  }

  public static void show(Context context, int resId, int duration) {
    if (context == null) {
      return;
    }
    try {
      String msg = context.getString(resId);
      if (!TextUtils.isEmpty(msg)) {
        show(context, msg, duration, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
            COMMON_VERTICAL_MARGIN);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void show(Context context, String msg, int duration) {
    show(context, msg, duration, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
        COMMON_VERTICAL_MARGIN);
  }

  @SuppressLint("ShowToast")
  public static void show(Context context, String msg, int duration, int gravity, float horiMargin,
      float vertiMargin) {
    boolean isEmpty = TextUtils.isEmpty(msg);
    if (isEmpty) {
      return;
    }

    Thread hThread = Thread.currentThread();
    try {
      mMessage = msg;
      if (mToast == null || mThreadRef == null || mThreadRef.get() != hThread) {
        mToast = new Toast(context.getApplicationContext());
        handleChangeToastHandle(mToast);
      }
      View view = LayoutInflater.from(context.getApplicationContext())
          .inflate(R.layout.common_toast_layout, null);
      TextView textView = view.findViewById(R.id.toast_text);
      textView.setText(msg);
      if (mToast != null) {
        mToast.cancel();
        mToast.setView(view);
        mToast.setDuration(duration);
        mToast.setGravity(gravity, (int) horiMargin, (int) vertiMargin);
        mHandler.postDelayed(new Runnable() {
          @Override public void run() {
            if (mToast != null) {
              mToast.show();
            }
          }
        }, 100);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    mThreadRef = new WeakReference<Thread>(hThread);
  }

  public static void hide() {
    try {
      if (mToast != null) {
        mToast.cancel();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressLint("ShowToast")
  public static void bottomShow(Context context, int msgId, int duration) {
    try {
      String msg = context.getString(msgId);
      boolean isEmpty = TextUtils.isEmpty(msg);
      if (isEmpty) {
        return;
      }

      mMessage = msg;
      if (mToast == null) {
        mToast = Toast.makeText(context.getApplicationContext(), msg, duration);
        handleChangeToastHandle(mToast);
      } else {
        mToast.setText(msg);
      }
      if (mToast != null) {
        mToast.show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void shortShow(Context context, String msg) {
    if (context == null) {
      return;
    }
    show(context, msg, Toast.LENGTH_SHORT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
  }

  public static void longShow(Context context, String msg) {
    if (context == null) {
      return;
    }
    show(context, msg, Toast.LENGTH_LONG, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
  }

  public static void shortShow(Context context, int resId) {
    if (context == null) {
      return;
    }
    try {
      String msg = context.getString(resId);
      show(context, msg, Toast.LENGTH_SHORT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void longShow(Context context, int resId) {
    if (context == null) {
      return;
    }
    try {
      String msg = context.getString(resId);
      show(context, msg, Toast.LENGTH_LONG, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //**********************对于API版本**********************************************

  private static boolean isReflectedHandler = false;

  private static void handleChangeToastHandle(Toast toast) {
    int sdkInt = Build.VERSION.SDK_INT;
    if (sdkInt >= Build.VERSION_CODES.N && sdkInt < Build.VERSION_CODES.O && !isReflectedHandler) {
      reflectTNHandler(toast);
      //这里为了避免多次反射，使用一个标识来限制
      isReflectedHandler = true;
    }
  }

  private static void reflectTNHandler(Toast toast) {
    try {
      Field tNField = toast.getClass().getDeclaredField("mTN");
      if (tNField == null) {
        return;
      }
      tNField.setAccessible(true);
      Object TN = tNField.get(toast);
      if (TN == null) {
        return;
      }
      Field handlerField = TN.getClass().getDeclaredField("mHandler");
      if (handlerField == null) {
        return;
      }
      handlerField.setAccessible(true);
      handlerField.set(TN, new ProxyTNHandler(TN));
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  //Toast$TN持有的Handler变量
  private static class ProxyTNHandler extends Handler {
    private Object tnObject;
    private Method handleShowMethod;
    private Method handleHideMethod;

    ProxyTNHandler(Object tnObject) {
      this.tnObject = tnObject;
      try {
        this.handleShowMethod = tnObject.getClass().getDeclaredMethod("handleShow", IBinder.class);
        this.handleShowMethod.setAccessible(true);
        this.handleHideMethod = tnObject.getClass().getDeclaredMethod("handleHide");
        this.handleHideMethod.setAccessible(true);
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }

    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0: {
          //SHOW
          IBinder token = (IBinder) msg.obj;
          if (handleShowMethod != null) {
            try {
              handleShowMethod.invoke(tnObject, token);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            } catch (WindowManager.BadTokenException e) {
              //显示Toast时添加BadTokenException异常捕获
              e.printStackTrace();
            }
          }
          break;
        }

        case 1: {
          //HIDE
          if (handleHideMethod != null) {
            try {
              handleHideMethod.invoke(tnObject);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            }
          }
          break;
        }
        case 2: {
          //CANCEL
          if (handleHideMethod != null) {
            try {
              handleHideMethod.invoke(tnObject);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            }
          }
          break;
        }
        default:
          break;
      }
      super.handleMessage(msg);
    }
  }
}
