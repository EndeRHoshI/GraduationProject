package com.hoshi.graduationproject.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.hoshi.graduationproject.executor.PlayOnlineMusic;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.model.OnlineMusic;
import com.hoshi.graduationproject.service.AudioPlayer;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.uploadimg.Auth;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.OkhttpUtil;
import com.hoshi.graduationproject.utils.ServerPath;
import com.hoshi.graduationproject.utils.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class SongListDetailActivity extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemClickListener
        , OnMoreClickListener {

  private List<OnlineMusic> mListSong = new ArrayList<>();

  private ListView lv_song_list_detail_recyclerView;
  private SimpleDraweeView sdv_list_cover;
  private TextView tv_song_list_detail_null_content,
          tv_song_list_name,
          tv_song_list_author,
          tv_song_list_collect;

  private Handler handler;
  private final int READY = 1;
  private final int GALLERY_ACTIVITY_CODE = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(Preferences.getTheme());
    setContentView(R.layout.activity_song_list_detail);

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case READY:
            if (mListSong.size() != 0) {
              tv_song_list_detail_null_content.setVisibility(View.GONE);
              lv_song_list_detail_recyclerView.setVisibility(View.VISIBLE);
              OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(mListSong);
              mAdapter.setOnMoreClickListener(SongListDetailActivity.this);
              lv_song_list_detail_recyclerView.setAdapter(mAdapter);
              lv_song_list_detail_recyclerView.setOnItemClickListener(SongListDetailActivity.this);
            } else {
              tv_song_list_detail_null_content.setVisibility(View.VISIBLE);
              lv_song_list_detail_recyclerView.setVisibility(View.GONE);
            }
            break;
          default:
            break;
        }
      }
    };

    ClickManager.init(this, this,
            R.id.back_song_list_detail,
            R.id.list_detail_loading_button,
            R.id.song_list_name,
            R.id.simpleDraweeView_list_cover);
  }

  @Override
  protected void onResume() {
    super.onResume();
    initPage();
    sendGetListSongRequest(getIntent().getIntExtra("list_id", 0));
  }

  private void initPage() {
    lv_song_list_detail_recyclerView = findViewById(R.id.song_list_detail_recyclerView);
    sdv_list_cover = findViewById(R.id.simpleDraweeView_list_cover);
    tv_song_list_detail_null_content = findViewById(R.id.song_list_detail_null_content);
    tv_song_list_name = findViewById(R.id.song_list_name);
    tv_song_list_author = findViewById(R.id.song_list_author);
    tv_song_list_collect = findViewById(R.id.song_list_collect);

    tv_song_list_name.setText(getIntent().getStringExtra("list_name"));
    tv_song_list_author.setText(getIntent().getStringExtra("list_author_name"));
    sdv_list_cover.setImageURI(getIntent().getStringExtra("list_avatar"));

    if (getIntent().getIntExtra("list_type", -1) == 2) { // 个人
      tv_song_list_collect.setVisibility(View.GONE);
      tv_song_list_name.setClickable(true);
      findViewById(R.id.song_list_detail_setting).setVisibility(View.VISIBLE);
    } else if (getIntent().getIntExtra("list_type", -1) == 3) { // 已收藏
      tv_song_list_collect.setVisibility(View.GONE);
      tv_song_list_name.setClickable(false);
      findViewById(R.id.song_list_detail_setting).setVisibility(View.GONE);
    } else { // 未收藏歌单
      tv_song_list_collect.setVisibility(View.VISIBLE);
      tv_song_list_name.setClickable(false);
      findViewById(R.id.song_list_detail_setting).setVisibility(View.GONE);
    }
  }

  private void showChangeListNameDialog() {
    // 使用LayoutInflater来加载dialog_setname.xml布局
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View nameView = layoutInflater.inflate(R.layout.dialog_change_nickname, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // 使用setView()方法将布局显示到dialog
    alertDialogBuilder.setView(nameView);

    final EditText userInput = (EditText) nameView.findViewById(R.id.change_nickname_edit);

    userInput.setHint(R.string.song_list_name);

    // 设置Dialog按钮
    alertDialogBuilder
            .setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        // 获取edittext的内容
                        if (!userInput.getText().toString().equals(Preferences.getNickname())) {
                          sendChangeListNameRequest(userInput.getText().toString());
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

  private void sendChangeListNameRequest(String list_name) {
    HashMap<String, String> params = new HashMap<>();
    params.put("list_id", "" + getIntent().getIntExtra("list_id", -1));
    params.put("list_name", list_name);
    OkhttpUtil.postFormRequest(ServerPath.CHANGE_SONG_LIST_NAME, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        String info = dataSuccessJson.getString("info");
        ToastUtils.show(info);

        if (dataSuccessJson.getBoolean("error")) {
          tv_song_list_name.setText(list_name);
        } else ToastUtils.show(info);
      }
      @Override
      public void requestFailure(Request request, IOException e) {
        ToastUtils.show(R.string.request_fail);
      }
    });
  }

  private void sendGetListSongRequest(int list_id) {
    HashMap<String, String> params = new HashMap<>();
    params.put("list_id", "" + list_id);
    OkhttpUtil.postFormRequest(ServerPath.GET_LIST_SONG, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONArray listSongArray = new JSONArray(result);
        mListSong.clear();
        for (int i = 0; i < listSongArray.length(); i++) {
          JSONObject tempJSONObject = listSongArray.getJSONObject(i);
          OnlineMusic tempOnlineMusic = new OnlineMusic();
          tempOnlineMusic.setTitle(tempJSONObject.getString("song_title"));
          tempOnlineMusic.setSong_id("" + tempJSONObject.getInt("song_id"));
          tempOnlineMusic.setArtist_name(tempJSONObject.getString("song_artist_name"));
          tempOnlineMusic.setAlias(tempJSONObject.getString("song_alias"));
          tempOnlineMusic.setPic_big(tempJSONObject.getString("song_cover"));
          tempOnlineMusic.setIndex(i + 1);
          tempOnlineMusic.setDuration(tempJSONObject.getLong("song_duration"));
          mListSong.add(tempOnlineMusic);
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

  private void sendDelListSongRequest(String song_id, int list_id) {
    HashMap<String, String> params = new HashMap<>();
    params.put("song_id", song_id);
    params.put("list_id", "" + list_id);
    OkhttpUtil.postFormRequest(ServerPath.DEL_LIST_SONG, params, new OkhttpUtil.DataCallBack() {
      @Override
      public void requestSuccess(String result) throws Exception {
        sendGetListSongRequest(list_id);
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
      case R.id.back_song_list_detail:
        finish();
        break;
      case R.id.list_detail_loading_button:
        startActivity(new Intent(this, PlayActivity.class));
        break;
      case R.id.song_list_name:
        showChangeListNameDialog();
        break;
      case R.id.simpleDraweeView_list_cover:
        startActivityForResult(new Intent(Intent.ACTION_PICK, null).setType("image/*"), GALLERY_ACTIVITY_CODE);
        break;
      default:
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
    final OnlineMusic onlineMusic = mListSong.get(position);
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(mListSong.get(position).getTitle());
    int itemsId = R.array.p_song_list_dialog;
    dialog.setItems(itemsId, (dialog1, which) -> {
      switch (which) {
        case 0:// 从歌单中移除
          if (getIntent().getIntExtra("list_type", -1) == 2)
            sendDelListSongRequest(onlineMusic.getSong_id(), getIntent().getIntExtra("list_id", -1));
          else ToastUtils.show(R.string.cannot_remove_collection);
          break;
      }
    });
    dialog.show();
  }

  private void play(OnlineMusic onlineMusic) {
    new PlayOnlineMusic(this, onlineMusic) {
      @Override
      public void onPrepare() {
      }

      @Override
      public void onExecuteSuccess(Music music) {
        AudioPlayer.get().addAndPlay(music);
        ToastUtils.show("已添加到播放列表");
      }

      @Override
      public void onExecuteFail(Exception e) {
        ToastUtils.show(R.string.unable_to_play);
      }
    }.execute();
  }


  private void uploadImg2QiNiu(String picPath) {
    UploadManager uploadManager = new UploadManager();
    // 设置图片名字
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    int list_id = getIntent().getIntExtra("list_id", -1);
    String key = "list"  + list_id + "_" + sdf.format(new Date());
    Log.i("", "picPath: " + picPath);
    uploadManager.put(picPath, key, Auth.create(Auth.AccessKey, Auth.SecretKey).uploadToken("hoshimusic"), new UpCompletionHandler() {
      @Override
      public void complete(String key, ResponseInfo info, JSONObject res) {
        // info.error中包含了错误信息，可打印调试
        // 上传成功后将key值上传到自己的服务器
        if (info.isOK()) {
          sendChangeListAvatarRequest(list_id, "http://p7eu09pkq.bkt.clouddn.com/" + key);
        }
      }
    }, null);
  }

  private void sendChangeListAvatarRequest(int list_id, String path) {
    HashMap<String, String> params = new HashMap<>();
    params.put("list_id", "" + list_id);
    params.put("list_avatar", path);
    OkhttpUtil.postFormRequest(ServerPath.CHANGE_SONG_LIST_AVATAR, params, new OkhttpUtil.DataCallBack(){
      @Override
      public void requestSuccess(String result) throws Exception {
        JSONObject dataSuccessJson = new JSONObject(result);
        if (dataSuccessJson.getBoolean("error")) {
          sdv_list_cover.setImageURI(path);
        } else {
          ToastUtils.show(dataSuccessJson.getString("info"));
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case GALLERY_ACTIVITY_CODE:
          String[] proj = {MediaStore.Images.Media.DATA};
          //好像是android多媒体数据库的封装接口，具体的看Android文档
          Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
          //按我个人理解 这个是获得用户选择的图片的索引值
          int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          //将光标移至开头 ，这个很重要，不小心很容易引起越界
          cursor.moveToFirst();
          //最后根据索引值获取图片路径
          String path = cursor.getString(column_index);
          //ToastUtils.show(path);
          uploadImg2QiNiu(path);
          break;
      }
    }
  }
}
