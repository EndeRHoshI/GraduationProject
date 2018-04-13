package com.hoshi.graduationproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.LocalMusicActivity;
import com.hoshi.graduationproject.activity.PlayActivity;
import com.hoshi.graduationproject.activity.PlaylistActivity;
import com.hoshi.graduationproject.activity.SongListActivity;
import com.hoshi.graduationproject.service.AudioPlayer;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.MusicUtils;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

public class MyMusicFragment extends BaseFragment implements View.OnClickListener {

  private View rootView = null;

  TextView tv_localMusicNum, tv_recentPlayedNum, tv_myCollectionNum,
          tv_personalSongListNum, tv_myMusicNoLogin;

  private int localMusicCount, recentMusicCount, myCollectionCount, personalSongListCount;
  private final int READY = 1;
  private Handler handler;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (rootView == null) {
      rootView = inflater.inflate(R.layout.fragment_mymusic, container, false);
    }
    ViewGroup parent = (ViewGroup) rootView.getParent();
    if (parent != null) {
      parent.removeView(rootView);
    }

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case READY:
            loadCount();
            break;
          default:
            break;
        }
      }
    };

    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    ClickManager.init(getView(), this,
            R.id.local_music,
            R.id.loading_button,
            R.id.recent_played,
            R.id.my_collection,
            R.id.personal_song_list);

    tv_localMusicNum = getActivity().findViewById(R.id.local_music_num);
    tv_recentPlayedNum = getActivity().findViewById(R.id.recent_played_num);
    tv_myCollectionNum = getActivity().findViewById(R.id.my_collection_num);
    tv_personalSongListNum = getActivity().findViewById(R.id.personal_song_list_num);
    tv_myMusicNoLogin = getActivity().findViewById(R.id.my_music_no_login);
  }

  @Override
  public void onResume() {
    super.onResume();
    checkLogin();
  }

  public void checkLogin() {
    String nickname = "" + Preferences.getNickname();
    if (nickname.equals("")) {
      tv_myMusicNoLogin.setVisibility(View.VISIBLE);
      return;
    }
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    OkhttpUtil.postFormRequest(ServerPath.CHECK_LOGIN_MY_MUSIC, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        String sessionId = dataSuccessJson.getString("sessionId");
        if (!sessionId.equals(Preferences.getSessionId())) {
          tv_myMusicNoLogin.setVisibility(View.VISIBLE);
        } else {
          myCollectionCount = dataSuccessJson.getInt("collectionSongList");
          personalSongListCount = dataSuccessJson.getInt("presonalSongList");
          tv_myMusicNoLogin.setVisibility(View.GONE);
        }

        Message message = new Message();
        message.what = READY;
        handler.sendMessage(message);
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void loadCount() {
    localMusicCount = MusicUtils.scanMusicAndReturnNum(getContext());
    recentMusicCount = AudioPlayer.get().getMusicList().size();
    tv_localMusicNum.setText("（" + localMusicCount + "）");
    tv_recentPlayedNum.setText("（" + recentMusicCount + "）");
    tv_myCollectionNum.setText("（" + myCollectionCount + "）");
    tv_personalSongListNum.setText("（" + personalSongListCount + "）");
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.local_music:
        startActivity(new Intent(getActivity(), LocalMusicActivity.class));
        break;
      case R.id.loading_button:
        startActivity(new Intent(getActivity(), PlayActivity.class));
        break;
      case R.id.recent_played:
        startActivity(new Intent(getActivity(), PlaylistActivity.class).putExtra("title", 1));
        break;
      case R.id.my_collection:
        startActivity(new Intent(getActivity(), SongListActivity.class).putExtra("type", 0));
        break;
      case R.id.personal_song_list:
        startActivity(new Intent(getActivity(), SongListActivity.class).putExtra("type", 1));
        break;
      default:
        break;
    }
  }
}
