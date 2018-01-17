package com.hoshi.graduationproject;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hoshi.graduationproject.discover.DiscoverFragment;
import com.hoshi.graduationproject.friends.FriendsFragment;
import com.hoshi.graduationproject.mymusic.MyMusicFragment;
import com.hoshi.graduationproject.personal.PersonalFragment;

public class MainActivity extends AppCompatActivity {

  private static final String DISCOVER = "discover";

  private static final String FRIENDS = "friends";

  private static final String MYMUSIC = "mymusic";

  private static final String PERSONAL = "personal";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
