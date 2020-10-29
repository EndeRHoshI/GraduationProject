package com.hoshi.graduationproject.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.adapter.OnMoreClickListener;
import com.hoshi.graduationproject.adapter.OnlineMusicAdapter;
import com.hoshi.graduationproject.adapter.SongListAdapter;
import com.hoshi.graduationproject.executor.PlayOnlineMusic;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.model.OnlineMusic;
import com.hoshi.graduationproject.model.SongList;
import com.hoshi.graduationproject.service.AudioPlayer;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RankDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener
        , OnMoreClickListener {

  final int UPDATE_RANK_DYNAMIC = 1;
  final int UPDATE = 2;
  final int SONG_LIST_READY = 3;
  final int ADD_SONG_LIST_READY = 4;

  TextView tv_bookedCount, tv_commentCount, tv_shareCount, tv_rankSongListLoading;
  ListView lv_rankSongList;

  int mBookedCount, mCommentCount, mShareCount;
  String rankAvatarUrl;
  String song_id, song_title, song_artist_name, song_alias, song_cover, song_duration;

  SimpleDraweeView sd_rankAvatar;

  // 以下是dialog的一些控件声明
  RecyclerView rv_add_to_song_list_recyclerView;
  SongListAdapter mSongListAdapter;
  View bottomView; // dialog的view

  private Handler handler;
  private List<OnlineMusic> mMusicList = new ArrayList<>();
  private List<SongList> mData = new ArrayList<>();
  private OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(mMusicList);
  private AlertDialog songListPickDialog;

  @SuppressLint("HandlerLeak")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rank_detail);

    tv_rankSongListLoading = findViewById(R.id.rank_song_list_loading);
    bottomView = View.inflate(this, R.layout.dialog_add_to_song_list, null); // dialog的view
    String id = getIntent().getExtras().get("id").toString();
    rankAvatarUrl = getIntent().getExtras().get("avatarUrl").toString();
    getRankSongDynamic(id);
    getRanksongList(id);

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what)
        {
          case UPDATE_RANK_DYNAMIC:
            loadRankData();//装入json数据
            break;
          case UPDATE:
            loadRankSongData();
            break;
          case SONG_LIST_READY:
            showSongListPickDialog();
            break;
          case ADD_SONG_LIST_READY:
            rv_add_to_song_list_recyclerView
                    = bottomView.findViewById(R.id.add_to_song_list_recyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());//设置布局管理器
            rv_add_to_song_list_recyclerView.setLayoutManager(layoutManager);//设置为垂直布局，这也是默认的
            mSongListAdapter = new SongListAdapter(mData, getBaseContext());
            mSongListAdapter.setOnItemClickLitener((view, position) -> {
              SongList tempSongList = mData.get(position);
              sendAddSongToSongListRequest(
                      song_id,
                      song_title,
                      song_artist_name,
                      song_alias,
                      song_cover,
                      song_duration,
                      tempSongList.getList_id());
              songListPickDialog.dismiss();
            });
            rv_add_to_song_list_recyclerView.setAdapter(mSongListAdapter);
            break;
          default:
            break;
        }
      }
    };

    ClickManager.init(this, this,
            R.id.back_rank,
            R.id.loading_button);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_rank:
        finish();
        break;
      case R.id.loading_button:
        startActivity(new Intent(this, PlayActivity.class));
        break;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    OnlineMusic tempOnlineMusic = (OnlineMusic) parent.getAdapter().getItem(position);
    //ToastUtils.show("" + tempOnlineMusic.getSong_id());
    play(tempOnlineMusic);
  }

  @Override
  public void onMoreClick(int position) {
    final OnlineMusic onlineMusic = mMusicList.get(position);
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(mMusicList.get(position).getTitle());
    int itemsId = R.array.rank_song_list_dialog;
    dialog.setItems(itemsId, (dialog1, which) -> {
      switch (which) {
        case 0: // 添加到歌单
          song_id = onlineMusic.getSong_id();
          song_title = onlineMusic.getTitle();
          song_artist_name = onlineMusic.getArtist_name();
          song_alias = onlineMusic.getAlias();
          song_cover = onlineMusic.getPic_big();
          song_duration = "" + onlineMusic.getDuration();
          sendGetPSongListRequest();
          break;
      }
    });
    dialog.show();
  }

  private void showSongListNameDialog() {
    // 使用LayoutInflater来加载dialog_setname.xml布局
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View nameView = layoutInflater.inflate(R.layout.dialog_change_nickname, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // 使用setView()方法将布局显示到dialog
    alertDialogBuilder.setView(nameView);

    final EditText userInput = nameView.findViewById(R.id.change_nickname_edit);

    userInput.setHint(R.string.song_list_name);

    // 设置Dialog按钮
    alertDialogBuilder
            .setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        // 获取edittext的内容
                        if (!userInput.getText().toString().equals(Preferences.getNickname())) {
                          sendAddPSongListRequest(userInput.getText().toString()); // 个人歌单
                        }
                      }
                    })
            .setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                      }
                    });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();

    // show it
    alertDialog.show();
  }

  private void sendAddPSongListRequest(String songListName) {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    params.put("list_name", songListName);
    OkhttpUtil.postFormRequest(ServerPath.ADD_SONG_LIST, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (!dataSuccessJson.getBoolean("error")) {
          ToastUtils.show(dataSuccessJson.getString("info"));
        }

        sendGetPSongListRequest2();
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void showSongListPickDialog() {
    if (songListPickDialog == null) {
      songListPickDialog = new AlertDialog.Builder(this)
              .setTitle("添加到歌单").setView(bottomView)
              .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              }).create();
      songListPickDialog.show();
    } else songListPickDialog.show();

    rv_add_to_song_list_recyclerView
            = bottomView.findViewById(R.id.add_to_song_list_recyclerView);
    bottomView.findViewById(R.id.add_to_song_list_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showSongListNameDialog();
      }
    });
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);//设置布局管理器
    rv_add_to_song_list_recyclerView.setLayoutManager(layoutManager);//设置为垂直布局，这也是默认的
    mSongListAdapter = new SongListAdapter(mData, getBaseContext());
    mSongListAdapter.setOnItemClickLitener(new SongListAdapter.OnItemClickLitener() {
      @Override
      public void onItemClick(View view, int position) {
        SongList tempSongList = mData.get(position);
        sendAddSongToSongListRequest(
                song_id,
                song_title,
                song_artist_name,
                song_alias,
                song_cover,
                song_duration,
                tempSongList.getList_id());
        songListPickDialog.dismiss();
      }
    });
    rv_add_to_song_list_recyclerView.setAdapter(mSongListAdapter);
  }

  private void sendAddSongToSongListRequest(String song_id, String song_title,
                          String song_artist_name, String song_alias, String song_cover,
                          String song_duration, int list_id) {
    HashMap<String, String> params = new HashMap<>();
    params.put("song_id", song_id);
    params.put("song_title", song_title);
    params.put("song_artist_name", song_artist_name);
    params.put("song_alias", song_alias);
    params.put("song_cover", song_cover);
    params.put("song_duration", song_duration);
    params.put("list_id", "" + list_id);
    OkhttpUtil.postFormRequest(ServerPath.ADD_SONG_TO_SONG_LIST, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (!dataSuccessJson.getBoolean("error")) {
          ToastUtils.show(dataSuccessJson.getString("info"));
        }
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void play(OnlineMusic onlineMusic) {
    new PlayOnlineMusic(this, onlineMusic) {
      @Override
      public void onPrepare() {
        showProgress();
      }

      @Override
      public void onExecuteSuccess(Music music) {
        cancelProgress();
        AudioPlayer.get().addAndPlay(music);
        ToastUtils.show("已添加到播放列表");
      }

      @Override
      public void onExecuteFail(Exception e) {
        cancelProgress();
        ToastUtils.show(R.string.unable_to_play);
      }
    }.execute();
  }

  public void getRankSongDynamic(String id) {
    OkHttpClient mOkHttpClient = new OkHttpClient();
    final Request rankDynamicRequest = new Request.Builder()
            .url("http://music.163.com/api/playlist/detail/dynamic?id=" + id)
            .build();

    Call rankDynamicCall = mOkHttpClient.newCall(rankDynamicRequest);
    rankDynamicCall.enqueue(new Callback() {
      @Override
      public void onFailure(Call failureCall, IOException e) {
      }

      @Override
      public void onResponse(Call responseCall, final Response response) throws IOException {
        String htmlStr = response.body().string();
        try {
          JSONObject dataJson = new JSONObject(htmlStr);
          mBookedCount = dataJson.getInt("bookedCount");
          mCommentCount = dataJson.getInt("commentCount");
          mShareCount = dataJson.getInt("shareCount");

          Message message = new Message();
          message.what = UPDATE_RANK_DYNAMIC;
          handler.sendMessage(message);
        } catch (JSONException e) {
        }
      }
    });
  }

  public void getRanksongList(String id) {
    OkHttpClient mOkHttpClient = new OkHttpClient();
    final Request rankDetailRequest = new Request.Builder()
            .url("http://music.163.com/api/playlist/detail?id=" + id)
            .build();

    Call rankDetailCall = mOkHttpClient.newCall(rankDetailRequest);
    rankDetailCall.enqueue(new Callback() {
      @Override
      public void onFailure(Call failureCall, IOException e) {
      }

      @Override
      public void onResponse(Call responseCall, final Response response) throws IOException {
        String htmlStr = response.body().string();
        try {
          JSONObject dataJson = new JSONObject(htmlStr);
          JSONArray rankSongListArray = dataJson.getJSONObject("result").getJSONArray("tracks");
          for (int i = 0; i < rankSongListArray.length(); i++) {
            JSONObject tempJSONObject = rankSongListArray.getJSONObject(i);
            String name = tempJSONObject.getString("name");
            int id = tempJSONObject.getInt("id");
            String alias = "";
            if (tempJSONObject.getJSONArray("alias") != null
                    && tempJSONObject.getJSONArray("alias").length() != 0) {
              alias = tempJSONObject.getJSONArray("alias").getString(0);
            }

            String singer = tempJSONObject.getJSONArray("artists")
                            .getJSONObject(0).getString("name");
            String picUrl = tempJSONObject.getJSONObject("album")
                            .getString("picUrl");

            long duration = tempJSONObject.getLong("duration");

            OnlineMusic tempOnlineMusic = new OnlineMusic();
            tempOnlineMusic.setTitle(name);
            tempOnlineMusic.setSong_id("" + id);
            tempOnlineMusic.setArtist_name(singer);
            tempOnlineMusic.setAlias(alias);
            tempOnlineMusic.setPic_big(picUrl);
            tempOnlineMusic.setIndex(i + 1);
            tempOnlineMusic.setDuration(duration);

            mMusicList.add(tempOnlineMusic);
          }
          mAdapter.notifyDataSetChanged();
          Message message = new Message();
          message.what = UPDATE;
          handler.sendMessage(message);
        } catch (JSONException e) {
        }
      }
    });
  }

  private void sendGetPSongListRequest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    OkhttpUtil.postFormRequest(ServerPath.GET_P_SONG_LIST, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONArray dataSuccessJson = new JSONArray(result);
        mData.clear();
        for (int i = 0; i < dataSuccessJson.length(); i++) {
          JSONObject tempJSONObject = dataSuccessJson.getJSONObject(i);
          SongList mSongList = new SongList();
          mSongList.setList_id(tempJSONObject.getInt("id"));
          mSongList.setList_avatar(tempJSONObject.getString("list_avatar"));
          mSongList.setList_name(tempJSONObject.getString("list_name"));
          mSongList.setList_author_id(tempJSONObject.getInt("list_author_id"));
          mSongList.setList_author_name(tempJSONObject.getString("list_author_name"));
          mSongList.setList_length(tempJSONObject.getInt("list_length"));
          mData.add(mSongList);
        }

        Message message = new Message();
        message.what = SONG_LIST_READY;
        handler.sendMessage(message);
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendGetPSongListRequest2() { // 另一种情形下要用到的请求
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    OkhttpUtil.postFormRequest(ServerPath.GET_P_SONG_LIST, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONArray dataSuccessJson = new JSONArray(result);
        mData.clear();
        for (int i = 0; i < dataSuccessJson.length(); i++) {
          JSONObject tempJSONObject = dataSuccessJson.getJSONObject(i);
          SongList mSongList = new SongList();
          mSongList.setList_id(tempJSONObject.getInt("id"));
          mSongList.setList_avatar(tempJSONObject.getString("list_avatar"));
          mSongList.setList_name(tempJSONObject.getString("list_name"));
          mSongList.setList_author_id(tempJSONObject.getInt("list_author_id"));
          mSongList.setList_author_name(tempJSONObject.getString("list_author_name"));
          mSongList.setList_length(tempJSONObject.getInt("list_length"));
          mData.add(mSongList);
        }

        Message message = new Message();
        message.what = ADD_SONG_LIST_READY;
        handler.sendMessage(message);
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  public void loadRankData() {
    sd_rankAvatar = findViewById(R.id.simpleDraweeView_rank_avatar);
    tv_bookedCount = findViewById(R.id.bookedCount);
    tv_commentCount = findViewById(R.id.commentCount);
    tv_shareCount = findViewById(R.id.shareCount);
    lv_rankSongList = findViewById(R.id.rank_songList);

    sd_rankAvatar.setImageURI(rankAvatarUrl);
    tv_bookedCount.setText("" + mBookedCount);
    tv_commentCount.setText("" + mCommentCount);
    tv_shareCount.setText("" + mShareCount);
  }

  public void loadRankSongData() {
    lv_rankSongList.setAdapter(mAdapter);
    lv_rankSongList.setOnItemClickListener(this);
    mAdapter.setOnMoreClickListener(this);
    /*lv_rankSongList.setAdapter(new rankListSongAdapter(getLayoutInflater(), mSongList));*/
    tv_rankSongListLoading.setVisibility(View.GONE);
    lv_rankSongList.setVisibility(View.VISIBLE);
  }
}


