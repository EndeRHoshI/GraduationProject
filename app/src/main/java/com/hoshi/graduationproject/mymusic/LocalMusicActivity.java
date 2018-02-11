package com.hoshi.graduationproject.mymusic;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.BaseActivity;
import com.hoshi.graduationproject.util.ClickManager;

public class LocalMusicActivity extends BaseActivity implements View.OnClickListener{

  @Override
  public void onCreate(final Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_local_music);

    ClickManager.init(this,this,R.id.back_local_music);

    String[] title = {"单曲", "歌手", "专辑", "文件夹"};
    LocalMusicFragment fragment = LocalMusicFragment.newInstance(0, title);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.add(R.id.tab_container, fragment);
    transaction.commitAllowingStateLoss();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_local_music:
        finish();
        break;
    }
  }
}
