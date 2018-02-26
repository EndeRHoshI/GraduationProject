package com.hoshi.graduationproject.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.adapter.OnlineMusicAdapter;
import com.hoshi.graduationproject.executor.PlayOnlineMusic;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.model.OnlineMusic;
import com.hoshi.graduationproject.service.AudioPlayer;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

  private EditText ed_search_music;
  private ListView lv_searchSongList;
  private List<OnlineMusic> mMusicList = new ArrayList<>();
  private OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(mMusicList);

  final int UPDATE_SEARCH = 1;
  final int UPDATE_GET_DURATION = 2;

  private Handler handler;
  private long tempDuration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    ed_search_music = findViewById(R.id.et_search_music);

    lv_searchSongList = findViewById(R.id.search_music_listview);
    lv_searchSongList.setOnItemClickListener(this);

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case UPDATE_SEARCH:
            loadSearchData();//装入json数据
            break;
          case UPDATE_GET_DURATION:
            /*loadDurationData();//装入json数据*/
            break;
          default:
            break;
        }
      }
    };

    ClickManager.init(this, this, R.id.search_back,
            R.id.search_button);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.search_back:
        finish();
        break;
      case R.id.search_button:
        lv_searchSongList.setVisibility(View.GONE);
        String query = ed_search_music.getText().toString();
        searchMusic(query);
        break;
      default:
        break;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    OnlineMusic tempOnlineMusic = (OnlineMusic) parent.getAdapter().getItem(position);
    getMusicDuration(tempOnlineMusic.getSong_id());
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        tempOnlineMusic.setDuration(tempDuration);
        play(tempOnlineMusic);
      }
    }, 250);
  }

  public long getMusicDuration(String song_id) {
    OkHttpClient mOkHttpClient = new OkHttpClient();
    final Request searchRequest = new Request.Builder()
            .url("http://music.163.com/api/song/enhance/player/url?id=" + song_id + "&ids=[" + song_id + "]&br=320000")
            .build();

    Call rankDynamicCall = mOkHttpClient.newCall(searchRequest);
    rankDynamicCall.enqueue(new Callback() {
      @Override
      public void onFailure(Call failureCall, IOException e) {
      }

      @Override
      public void onResponse(Call responseCall, final Response response) throws IOException {
        String htmlStr = response.body().string();
        try {
          JSONObject dataJson = new JSONObject(htmlStr);
          long searchSongBr = dataJson.getJSONArray("data").getJSONObject(0)
                  .getLong("br");
          long searchSongSize = dataJson.getJSONArray("data").getJSONObject(0)
                  .getLong("size");
          long searchSongDuration = searchSongSize * 7998 / searchSongBr;
          tempDuration = searchSongDuration;
          /*Message message = new Message();
          message.what = UPDATE_GET_DURATION;
          handler.sendMessage(message);*/
        } catch (JSONException e) {
        }
      }
    });
    return tempDuration;
  }

  public void loadSearchData() {
    lv_searchSongList.setVisibility(View.VISIBLE);
    mAdapter.notifyDataSetChanged();
    lv_searchSongList.setAdapter(mAdapter);
  }

  public void searchMusic(String query) {
    OkHttpClient mOkHttpClient = new OkHttpClient();
    final Request rankDynamicRequest = new Request.Builder()
            .url("http://s.music.163.com/search/get/?type=1&s='" + query + "'&limit=60&offset=0")
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
          JSONArray searchSongListArray = dataJson.getJSONObject("result").getJSONArray("songs");
          mMusicList.clear();
          for (int i = 0; i < searchSongListArray.length(); i++) {
            JSONObject tempJSONObject = searchSongListArray.getJSONObject(i);
            String name = tempJSONObject.getString("name");
            int id = tempJSONObject.getInt("id");
            String alias = "";
            String singer = tempJSONObject.getJSONArray("artists")
                    .getJSONObject(0).getString("name");
            String picUrl = tempJSONObject.getJSONObject("album")
                    .getString("picUrl");

            OnlineMusic tempOnlineMusic = new OnlineMusic();
            tempOnlineMusic.setTitle(name);
            tempOnlineMusic.setSong_id("" + id);
            tempOnlineMusic.setArtist_name(singer);
            tempOnlineMusic.setAlias(alias);
            tempOnlineMusic.setPic_big(picUrl);
            tempOnlineMusic.setTitle(name);
            tempOnlineMusic.setIndex(i + 1);

            mMusicList.add(tempOnlineMusic);
          }
          Message message = new Message();
          message.what = UPDATE_SEARCH;
          handler.sendMessage(message);
        } catch (JSONException e) {
        }
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
}
