package com.hoshi.graduationproject.personal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.PlayActivity;
import com.hoshi.graduationproject.fragment.BaseFragment;
import com.hoshi.graduationproject.utils.ClickManager;

public class PersonalFragment extends BaseFragment implements View.OnClickListener {
  private View mRootView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_personal, container, false);
    return mRootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ClickManager.init(getView(), this,
            R.id.avatar_nickname_layout,
            R.id.trends_layout,
            R.id.follows_layout,
            R.id.fans_layout,
            R.id.music_clock_layout,
            R.id.change_skin_layout,
            R.id.night_mode_layout,
            R.id.feedback_layout,
            R.id.logout_textview,
            R.id.loading_button);

    Uri uri = Uri.parse("http://cdn.aixifan.com/acfun-pc/2.0.97/img/niudan/niudango.png");
    SimpleDraweeView draweeView = (SimpleDraweeView) getActivity().findViewById(R.id.head_pic);
    draweeView.setImageURI(uri);

    /*mButton = (Button)getActivity().findViewById(R.id.testButton);
    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getActivity(),PicTestActivity.class));
      }
    });*/
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.avatar_nickname_layout:
        startActivity(new Intent(getActivity(), LoginActivity.class));
        break;
      case R.id.trends_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.follows_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.fans_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.music_clock_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.change_skin_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.night_mode_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.feedback_layout:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.logout_textview:
        Toast.makeText(getActivity(), "暂未开发", Toast.LENGTH_SHORT).show();
        break;
      case R.id.loading_button:
        startActivity(new Intent(getActivity(), PlayActivity.class));
        break;
    }
  }
}
