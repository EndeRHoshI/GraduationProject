package com.hoshi.graduationproject.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.adapter.SongListAdapter;
import com.hoshi.graduationproject.model.SongList;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class SongListActivity extends AppCompatActivity implements View.OnClickListener{

  private TextView tv_my_music_null_content, tv_song_list_title;
  private ImageView iv_add_or_search_song_list;
  private RecyclerView mRecyclerView;

  private List<SongList> mCollection = new ArrayList<>();
  private List<SongList> mPresonal = new ArrayList<>();

  private Handler handler;
  private final int READY = 1;
  private final int ADD_READY = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_song_list);

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case READY:
            if (getIntent().getIntExtra("type", -1) == 0) { // 歌单收藏
              if (mCollection.size() != 0) {
                tv_my_music_null_content.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                SongListAdapter mSongListAdapter = new SongListAdapter(mCollection, getBaseContext());
                mRecyclerView.setAdapter(mSongListAdapter);
              } else {
                tv_my_music_null_content.setVisibility(View.VISIBLE);
                tv_my_music_null_content.setText(R.string.no_song_list);
                mRecyclerView.setVisibility(View.GONE);
              }
            } else { // 个人歌单
              if (mPresonal.size() != 0) {
                tv_my_music_null_content.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                SongListAdapter mSongListAdapter = new SongListAdapter(mPresonal, getBaseContext());
                mRecyclerView.setAdapter(mSongListAdapter);
              } else {
                tv_my_music_null_content.setVisibility(View.VISIBLE);
                tv_my_music_null_content.setText(R.string.no_song_list);
                mRecyclerView.setVisibility(View.GONE);
              }
            }
            break;
          case ADD_READY:
            sendGetPSongListRequest();
            break;
          default:
            break;
        }
      }
    };

    initPage();
    sendGetPSongListRequest();

    ClickManager.init(this, this,
            R.id.song_list_back,
            R.id.add_or_search_song_list);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (Preferences.getNickname().equals(""))
      tv_my_music_null_content.setVisibility(View.GONE);
    else tv_my_music_null_content.setVisibility(View.VISIBLE);
  }

  private void initPage() {
    tv_song_list_title = findViewById(R.id.song_list_title);
    iv_add_or_search_song_list = findViewById(R.id.add_or_search_song_list);
    tv_my_music_null_content = findViewById(R.id.my_music_null_content);

    mRecyclerView = findViewById(R.id.song_list_recyclerView);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);//设置布局管理器
    mRecyclerView.setLayoutManager(layoutManager);//设置为垂直布局，这也是默认的

    layoutManager.setOrientation(OrientationHelper. VERTICAL);//设置Adapter
    if (getIntent().getIntExtra("type", -1) == 0) { // 歌单收藏
      iv_add_or_search_song_list.setImageResource(R.drawable.ic_menu_search);
      tv_song_list_title.setText(R.string.my_collection);
    } else { // 个人歌单
      iv_add_or_search_song_list.setImageResource(R.drawable.ic_plus_white);
      tv_song_list_title.setText(R.string.personal_song_list);
    }
  }

  private void sendGetPSongListRequest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("id", "" + Preferences.getId());
    OkhttpUtil.postFormRequest(ServerPath.GET_P_SONG_LIST, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONArray dataSuccessJson = new JSONArray(result);
        mPresonal.clear();
        for (int i = 0; i < dataSuccessJson.length(); i++) {
          JSONObject tempJSONObject = dataSuccessJson.getJSONObject(i);
          SongList mSongList = new SongList();
          mSongList.setList_id(tempJSONObject.getInt("id"));
          mSongList.setList_avatar(tempJSONObject.getString("list_avatar"));
          mSongList.setList_name(tempJSONObject.getString("list_name"));
          mSongList.setList_author_id(tempJSONObject.getInt("list_author_id"));
          mPresonal.add(mSongList);
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

  private void showSongListNameDialog() {
    // 使用LayoutInflater来加载dialog_setname.xml布局
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View nameView = layoutInflater.inflate(R.layout.dialog_change_nickname, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // 使用setView()方法将布局显示到dialog
    alertDialogBuilder.setView(nameView);

    final EditText userInput = (EditText) nameView.findViewById(R.id.change_profile_edit);

    userInput.setHint(R.string.song_list_name);

    // 设置Dialog按钮
    alertDialogBuilder
            .setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        // 获取edittext的内容
                        if (!userInput.getText().toString().equals(Preferences.getNickname())) {
                          if (getIntent().getIntExtra("type", -1) == 0) { // 歌单收藏
                            sendAddCSongListRequest(userInput.getText().toString());
                          } else sendAddPSongListRequest(userInput.getText().toString()); // 个人歌单
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

  private void sendAddCSongListRequest(String songListName) {
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

        Message message = new Message();
        message.what = ADD_READY;
        handler.sendMessage(message);
      }

      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.song_list_back:
        finish();
        break;
      case R.id.add_or_search_song_list:
        showSongListNameDialog();
        break;
      default:
        break;
    }
  }
}
