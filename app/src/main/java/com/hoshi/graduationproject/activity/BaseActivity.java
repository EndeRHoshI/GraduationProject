package com.hoshi.graduationproject.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.service.PlayService;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.PermissionReq;
import com.hoshi.graduationproject.utils.binding.ViewBinder;

/**
 * 基类<br>
 * 如果继承本类，需要在 layout 中添加 {@link Toolbar} ，并将 AppTheme 继承 Theme.AppCompat.NoActionBar 。
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseActivity extends AppCompatActivity {
  protected Handler handler;
  protected PlayService playService;
  private ServiceConnection serviceConnection;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setSystemBarTransparent();
    setVolumeControlStream(AudioManager.STREAM_MUSIC);
    handler = new Handler(Looper.getMainLooper());
    bindService();
  }

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    initView();
  }

  @Override
  public void setContentView(View view) {
    super.setContentView(view);
    initView();
  }

  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(view, params);
    initView();
  }

  private void initView() {
    ViewBinder.bind(this);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar == null) {
      throw new IllegalStateException("Layout is required to include a Toolbar with id 'toolbar'");
    }
    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
  }

  private void bindService() {
    Intent intent = new Intent();
    intent.setClass(this, PlayService.class);
    serviceConnection = new PlayServiceConnection();
    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
  }

  private class PlayServiceConnection implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      playService = ((PlayService.PlayBinder) service).getService();
      onServiceBound();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      Log.e(getClass().getSimpleName(), "service disconnected");
    }
  }

  protected void onServiceBound() {
  }

  private void setSystemBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      // LOLLIPOP解决方案
      getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(Color.TRANSPARENT);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // KITKAT解决方案
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void showProgress() {
    showProgress(getString(R.string.loading));
  }

  public void showProgress(String message) {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(false);
    }
    progressDialog.setMessage(message);
    if (!progressDialog.isShowing()) {
      progressDialog.show();
    }
  }

  public void cancelProgress() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.cancel();
    }
  }

  @Override
  protected void onStart() {
    setListener();
    super.onStart();
  }

  protected void setListener() {
  }

  private void showPlayingFragment() {

    //startActivity(new Intent(this, PlayActivity.class));
    /*if (isPlayFragmentShow) {
      return;
    }

    Toast.makeText(this, "click", Toast.LENGTH_LONG).show();

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
    if (mPlayFragment == null) {
      mPlayFragment = new PlayFragment();
      ft.add(R.id.fragment_play, mPlayFragment);
      ft.commit();
    } else {
      ft.show(mPlayFragment);
    }
    ft.commitAllowingStateLoss();
    isPlayFragmentShow = true;*/
  }

  private void hidePlayingFragment() {
    /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(0, R.anim.fragment_slide_down);
    ft.hide(mPlayFragment);
    ft.commitAllowingStateLoss();
    isPlayFragmentShow = false;*/
  }

  @Override
  public void onBackPressed() {
    //Toast.makeText(this, "clickback", Toast.LENGTH_LONG).show();
    /*if (mPlayFragment != null && isPlayFragmentShow) {
      hidePlayingFragment();
      return;
    }*/
    super.onBackPressed();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  protected void onDestroy() {
    if (serviceConnection != null) {
      unbindService(serviceConnection);
    }
    super.onDestroy();
  }
}
