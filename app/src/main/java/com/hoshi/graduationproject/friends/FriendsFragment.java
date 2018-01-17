package com.hoshi.graduationproject.friends;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.discover.PicTestActivity;

public class FriendsFragment extends Fragment {

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
        startActivity(new Intent(getActivity(),PicTestActivity.class));
      }
    });
  }

}
