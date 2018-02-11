package com.hoshi.graduationproject.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hoshi.graduationproject.MyApplication;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.BaseActivity;
import com.hoshi.graduationproject.activity.MusicStateListener;
import com.hoshi.graduationproject.activity.PlayingActivity;
import com.hoshi.graduationproject.util.ClickManager;

/**
 * Created by wm on 2016/3/17.
 */
public class BaseFragment extends Fragment implements MusicStateListener, View.OnClickListener{

  //private RightUpButtonFragment fragment; //右上角音乐播放按钮
  public Activity mContext;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    this.mContext = activity;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    /*FragmentTransaction ft = getFragmentManager().beginTransaction();
    if (fragment == null) {
      fragment = RightUpButtonFragment.newInstance();
      ft.add(R.id.right_up_button, fragment).commitAllowingStateLoss();
    } else {
      ft.show(fragment).commitAllowingStateLoss();
    }*/
    ClickManager.init(getActivity(), this, R.id.loading_button);
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.loading_button:
        Intent intent = new Intent(MyApplication.getContext(),PlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
        break;
      default:
        break;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
    reloadAdapter();
  }

  @Override
  public void onStop() {
    super.onStop();
    ((BaseActivity) getActivity()).removeMusicStateListenerListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void updateTrackInfo() {
  }

  @Override
  public void updateTime() {
  }

  @Override
  public void changeTheme() {
  }

  @Override
  public void reloadAdapter() {
  }
}
