package com.hoshi.graduationproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoshi.graduationproject.R;

public class FriendsAboutFragment extends Fragment {

  private TextView tv_friend_sex, tv_friend_birthday, tv_friend_profile;
  private String friend_birthday, friend_profile;
  private int friend_sex;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      friend_sex = getArguments().getInt("friend_sex");
      friend_birthday = getArguments().getString("friend_birthday");
      friend_profile = getArguments().getString("friend_profile");
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_friends_about, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    tv_friend_sex = getActivity().findViewById(R.id.textview_friend_sex);
    tv_friend_birthday = getActivity().findViewById(R.id.textview_friend_birthday);
    tv_friend_profile = getActivity().findViewById(R.id.textview_friend_profile);

    String sFormat = getString(R.string.friend_sex);
    String sFinal;
    switch (friend_sex) {
      case 0: // 保密
        sFinal = String.format(sFormat, "保密");
        tv_friend_sex.setText(sFinal);
        break;
      case 1: // 男
        sFinal = String.format(sFormat, "男");
        tv_friend_sex.setText(sFinal);
        break;
      case 2: // 女
        sFinal = String.format(sFormat, "女");
        tv_friend_sex.setText(sFinal);
        break;
    }

    sFormat = getString(R.string.friend_birthday);
    sFinal = String.format(sFormat, friend_birthday);
    tv_friend_birthday.setText(sFinal);

    if (friend_profile.equals("")) {
      tv_friend_profile.setText(R.string.no_profile);
    } else tv_friend_profile.setText(friend_profile);
  }
}
