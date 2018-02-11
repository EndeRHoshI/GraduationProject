package com.hoshi.graduationproject.friends;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hoshi.graduationproject.MyApplication;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.discover.PicTestActivity;
import com.hoshi.graduationproject.activity.PlayingActivity;
import com.hoshi.graduationproject.fragment.BaseFragment;

public class FriendsFragment extends BaseFragment {

  private View mRootView;
  Button mButton;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_friends, container, false);
    return mRootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mButton = (Button)getActivity().findViewById(R.id.testButton);
    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MyApplication.getContext(),PlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
      }
    });
  }
}
