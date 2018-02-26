package com.hoshi.graduationproject;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hoshi.graduationproject.application.AppCache;
import com.hoshi.graduationproject.application.ForegroundObserver;
import com.hoshi.graduationproject.service.PlayService;
import com.hoshi.graduationproject.storage.db.DBManager;

public class MyApplication extends Application {

  public static Context CONTEXT;

  public static Context getContext() {
    return CONTEXT;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    CONTEXT = getApplicationContext();
    AppCache.get().init(this);
    ForegroundObserver.init(this);
    DBManager.get().init(this);
    Fresco.initialize(this);

    Intent intent = new Intent(this, PlayService.class);
    startService(intent);
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
