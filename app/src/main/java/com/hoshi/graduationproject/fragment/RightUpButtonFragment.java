package com.hoshi.graduationproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoshi.graduationproject.MyApplication;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.PlayingActivity;

public class RightUpButtonFragment extends Fragment {

  private View rootView;

  public static RightUpButtonFragment newInstance() {
    return new RightUpButtonFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_right_up_button, container, false);
    rootView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MyApplication.getContext(), PlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
      }
    });
    return rootView;
  }
}
