package com.hoshi.graduationproject;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.hoshi.graduationproject.permissions.Nammu;

public class MyApplication extends Application {

  public static Context CONTEXT;

  private static Gson gson;

  public static Context getContext() {
    return CONTEXT;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    CONTEXT = this;
    Fresco.initialize(this);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Nammu.init(this);
    }
  }

  public static Gson gsonInstance() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }


  /**
   * 获得当前进程的名字
   *
   * @param context
   * @return 进程号
   */
  public static String getCurProcessName(Context context) {

    int pid = android.os.Process.myPid();

    ActivityManager activityManager = (ActivityManager) context
            .getSystemService(Context.ACTIVITY_SERVICE);

    for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
            .getRunningAppProcesses()) {

      if (appProcess.pid == pid) {
        return appProcess.processName;
      }
    }
    return null;
  }
}
