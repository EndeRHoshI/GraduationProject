package com.hoshi.graduationproject.friends;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.PlayActivity;
import com.hoshi.graduationproject.discover.PicTestActivity;
import com.hoshi.graduationproject.fragment.BaseFragment;
import com.hoshi.graduationproject.utils.ClickManager;

public class FriendsFragment extends BaseFragment implements View.OnClickListener {

  private View mRootView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_friends, container, false);
    return mRootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    ClickManager.init(getActivity(), this, R.id.loading_button,
            R.id.testButton);
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.loading_button:
        startActivity(new Intent(getActivity(), PlayActivity.class));
        break;
      case R.id.testButton:
        startActivity(new Intent(getActivity(), PicTestActivity.class));
        break;
      default:
        break;
    }
  }
}
