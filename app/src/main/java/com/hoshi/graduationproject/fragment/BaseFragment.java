package com.hoshi.graduationproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.hoshi.graduationproject.activity.PlayActivity;
import com.hoshi.graduationproject.utils.PermissionReq;
import com.hoshi.graduationproject.utils.binding.ViewBinder;
import com.hwangjr.rxbus.RxBus;

/**
 * 基类<br>
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseFragment extends Fragment{
  protected Handler handler;

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    handler = new Handler(Looper.getMainLooper());
    ViewBinder.bind(this, getView());
    RxBus.get().register(this);
  }

  @Override
  public void onStart() {
    super.onStart();
    setListener();
  }

  protected void setListener() {
  }

  private void showPlayingFragment() {
    startActivity(new Intent(getActivity(), PlayActivity.class));
    /*if (isPlayFragmentShow) {
      return;
    }

    Toast.makeText(getContext(), "click", Toast.LENGTH_LONG).show();

    FragmentTransaction ft = getFragmentManager().beginTransaction();
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
    /*FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.setCustomAnimations(0, R.anim.fragment_slide_down);
    ft.hide(mPlayFragment);
    ft.commitAllowingStateLoss();
    isPlayFragmentShow = false;*/
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  public void onDestroy() {
    RxBus.get().unregister(this);
    super.onDestroy();
  }
}
