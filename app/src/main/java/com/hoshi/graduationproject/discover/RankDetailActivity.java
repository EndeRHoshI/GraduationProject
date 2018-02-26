package com.hoshi.graduationproject.discover;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.BaseActivity;
import com.hoshi.graduationproject.activity.PlayActivity;
import com.hoshi.graduationproject.adapter.OnMoreClickListener;
import com.hoshi.graduationproject.adapter.OnlineMusicAdapter;
import com.hoshi.graduationproject.executor.PlayOnlineMusic;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.model.OnlineMusic;
import com.hoshi.graduationproject.service.AudioPlayer;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.FileUtils;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

  TextView tv_bookedCount;
  TextView tv_commentCount;
  TextView tv_shareCount;
  TextView tv_rankSongListLoading;
  ListView lv_rankSongList;

  int mBookedCount;
  int mCommentCount;
  int mShareCount;
  String rankAvatarUrl;

  SimpleDraweeView sd_rankAvatar;

  private Handler handler;
  private List<OnlineMusic> mMusicList = new ArrayList<>();
  private OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(mMusicList);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rank_detail);

    tv_rankSongListLoading = (TextView) findViewById(R.id.rank_song_list_loading);
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
          default:
            break;
        }
      }
    };

    ClickManager.init(this, this, R.id.back_rank,
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
    String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(onlineMusic.getArtist_name(), onlineMusic.getTitle());
    File file = new File(path);
    int itemsId = file.exists() ? R.array.online_music_dialog_without_download : R.array.online_music_dialog;
    dialog.setItems(itemsId, (dialog1, which) -> {
      switch (which) {
        case 0:// 分享
          //share(onlineMusic);
          break;
        case 1:// 查看歌手信息
          //artistInfo(onlineMusic);
          break;
        case 2:// 下载
          //download(onlineMusic);
          break;
      }
    });
    dialog.show();
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
            tempOnlineMusic.setTitle(name);
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

  public void loadRankData() {
    sd_rankAvatar = (SimpleDraweeView) findViewById(R.id.simpleDraweeView_rank_avatar);
    tv_bookedCount = (TextView) findViewById(R.id.bookedCount);
    tv_commentCount = (TextView) findViewById(R.id.commentCount);
    tv_shareCount = (TextView) findViewById(R.id.shareCount);
    lv_rankSongList = (ListView) findViewById(R.id.rank_songList);

    sd_rankAvatar.setImageURI(rankAvatarUrl);
    tv_bookedCount.setText("" + mBookedCount);
    tv_commentCount.setText("" + mCommentCount);
    tv_shareCount.setText("" + mShareCount);
  }

  public void loadRankSongData() {
    lv_rankSongList.setAdapter(mAdapter);
    lv_rankSongList.setOnItemClickListener(this);
    /*lv_rankSongList.setAdapter(new rankListSongAdapter(getLayoutInflater(), mSongList));*/
    tv_rankSongListLoading.setVisibility(View.GONE);
    lv_rankSongList.setVisibility(View.VISIBLE);
  }
}


