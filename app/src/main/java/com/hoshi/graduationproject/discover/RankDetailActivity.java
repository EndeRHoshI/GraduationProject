package com.hoshi.graduationproject.discover;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.BaseActivity;
import com.hoshi.graduationproject.info.MusicInfo;
import com.hoshi.graduationproject.util.ClickManager;

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

public class RankDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

  private Context mContext;

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
  private List<songList> mSongList= new ArrayList<songList>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
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

    ClickManager.init(this, this, R.id.back_rank);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_rank:
        finish();
        break;
    }
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
            songList tempSongList = new songList(name, id, (i + 1), alias, singer);
            mSongList.add(tempSongList);
          }
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
    lv_rankSongList.setAdapter(new rankListSongAdapter(getLayoutInflater(), mSongList));
    tv_rankSongListLoading.setVisibility(View.GONE);
    lv_rankSongList.setVisibility(View.VISIBLE);
    lv_rankSongList.setOnItemClickListener(this);
  }

  public class songList {
    String name;
    int id;
    int index;
    String alias;
    String singer;

    public songList (String name, int id, int index, String alias, String singer) {
      this.name = name;
      this.id = id;
      this.index = index;
      this.alias = alias;
      this.singer = singer;
    }
  }
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
                          long id) {
    // TODO Auto-generated method stub
    String text= lv_rankSongList.getItemAtPosition(position)+"";
    Toast.makeText(this, "position="+position+"text="+text,
            Toast.LENGTH_SHORT).show();
  }

  public class rankListSongAdapter extends BaseAdapter {

    private List<songList> mData;//定义数据。
    private ArrayList <MusicInfo> mList;
    private LayoutInflater mInflater;//定义Inflater,加载我们自定义的布局。

    /*
    定义构造器，在Activity创建对象Adapter的时候将数据data和Inflater传入自定义的Adapter中进行处理。
    */
    public rankListSongAdapter(LayoutInflater inflater, List<songList> data){
      mInflater = inflater;
      mData = data;
    }

    @Override
    public int getCount() {
      return mData.size();
    }

    @Override
    public Object getItem(int position) {
      return position;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
      //获得ListView中的view
      View viewSongList = mInflater.inflate(R.layout.listview_songs,null);
      //获得排行榜歌曲对象
      songList mSongList = mData.get(position);
      //获得自定义布局中每一个控件的对象。
      TextView name = (TextView) viewSongList.findViewById(R.id.rank_songList_name);
      TextView alias = (TextView) viewSongList.findViewById(R.id.rank_songList_alias);
      TextView name_singer = (TextView) viewSongList.findViewById(R.id.rank_songList_name_singer);
      TextView index = (TextView) viewSongList.findViewById(R.id.rank_index);
      //将数据一一添加到自定义的布局中。
      name.setText(mSongList.name);
      if (!mSongList.alias.equals("") && mSongList.alias != null) {
        alias.setText("（" + mSongList.alias + "）");
      } else {
        alias.setVisibility(View.INVISIBLE);
      }
      name_singer.setText(mSongList.singer + " - " + mSongList.name);
      if (mSongList.index < 10) {
        index.setText("0" + mSongList.index);
      }
      else {
        index.setText("" + mSongList.index);
      }
      return viewSongList;
    }

    /*public void onClick(View v) {
      HandlerUtil.getInstance(mContext).postDelayed(new Runnable() {
        @Override
        public void run() {
          long[] list = new long[mData.size()];
          HashMap<Long, MusicInfo> infos = new HashMap();
          for (int i = 0; i < mData.size(); i++) {
            MusicInfo info = mData.get(i);
            list[i] = info.songId;
            info.islocal = true;
            info.albumData = MusicUtils.getAlbumArtUri(info.albumId) + "";
            infos.put(list[i], mData.get(i));
          }
          MusicPlayer.playAll(infos, list, 0, false);
        }
      }, 70);
    }*/
  }
}


