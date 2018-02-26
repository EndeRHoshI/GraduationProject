package com.hoshi.graduationproject;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hoshi.graduationproject.discover.DiscoverFragment;
import com.hoshi.graduationproject.friends.FriendsFragment;
import com.hoshi.graduationproject.mymusic.MyMusicFragment;
import com.hoshi.graduationproject.personal.PersonalFragment;
import com.hoshi.graduationproject.utils.PermissionHelper;

public class MainActivity extends AppCompatActivity {

  private static final String DISCOVER = "discover";

  private static final String FRIENDS = "friends";

  private static final String MYMUSIC = "mymusic";

  private static final String PERSONAL = "personal";

  private PermissionHelper mPermissionHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mPermissionHelper = new PermissionHelper(this);
    mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
      @Override
      public void onAfterApplyAllPermission() {
      }
    });
    if (Build.VERSION.SDK_INT < 23) {
      // 如果系统版本低于23，直接跑应用的逻辑
    } else {
      // 如果权限全部申请了，那就直接跑应用逻辑
      if (mPermissionHelper.isAllRequestedPermissionGranted()) {
      } else {
        // 如果还有权限未申请，而且系统版本大于23，执行申请权限逻辑
        mPermissionHelper.applyPermissions();
      }
    }

    FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
    tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

    addTab(tabHost, DISCOVER, DiscoverFragment.class, getIndicatorView(R.layout.tab_discover));
    addTab(tabHost, MYMUSIC , MyMusicFragment .class, getIndicatorView(R.layout.tab_mymusic));
    addTab(tabHost, FRIENDS , FriendsFragment .class, getIndicatorView(R.layout.tab_friends));
    addTab(tabHost, PERSONAL, PersonalFragment.class, getIndicatorView(R.layout.tab_personal));
  }

  private static void addTab(FragmentTabHost tabHost, String title, Class<?> fragmentClazz, View indicator) {
    Bundle arguments = new Bundle();
    arguments.putString("text", title);
    tabHost.addTab(tabHost.newTabSpec(title).setIndicator(indicator),
            fragmentClazz, arguments);
  }

  private View getIndicatorView(int resId) {
    View v = null;
    if (resId != 0) {
      v = getLayoutInflater().inflate(resId, null);
    }
    return v;
  }
}
