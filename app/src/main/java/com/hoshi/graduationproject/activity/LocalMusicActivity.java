package com.hoshi.graduationproject.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.adapter.OnMoreClickListener;
import com.hoshi.graduationproject.adapter.PlaylistAdapter;
import com.hoshi.graduationproject.application.AppCache;
import com.hoshi.graduationproject.constants.Keys;
import com.hoshi.graduationproject.constants.RequestCode;
import com.hoshi.graduationproject.constants.RxBusTags;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.service.AudioPlayer;
import com.hoshi.graduationproject.utils.ClickManager;
import com.hoshi.graduationproject.utils.MusicUtils;
import com.hoshi.graduationproject.utils.PermissionReq;
import com.hoshi.graduationproject.utils.ToastUtils;
import com.hoshi.graduationproject.utils.binding.Bind;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import java.io.File;
import java.util.List;

public class LocalMusicActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, OnMoreClickListener {

  @Bind(R.id.lv_local_music)
  private ListView lvLocalMusic;
  @Bind(R.id.v_searching)
  private TextView vSearching;

  private PlaylistAdapter mAdapter;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_local_music);

    mAdapter = new PlaylistAdapter(AppCache.get().getLocalMusicList());
    mAdapter.setOnMoreClickListener(this);
    lvLocalMusic.setAdapter(mAdapter);
    if (AppCache.get().getLocalMusicList().isEmpty()) {
      scanMusic(null);
    }

    ClickManager.init(this,this,
            R.id.back_local_music,
            R.id.loading_button);
  }

  @Subscribe(tags = {@Tag(RxBusTags.SCAN_MUSIC)})
  public void scanMusic(Object object) {
    lvLocalMusic.setVisibility(View.GONE);
    vSearching.setVisibility(View.VISIBLE);
    PermissionReq.with(this)
            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .result(new PermissionReq.Result() {
              @SuppressLint("StaticFieldLeak")
              @Override
              public void onGranted() {
                new AsyncTask<Void, Void, List<Music>>() {
                  @Override
                  protected List<Music> doInBackground(Void... params) {
                    return MusicUtils.scanMusic(getBaseContext());
                  }

                  @Override
                  protected void onPostExecute(List<Music> musicList) {
                    AppCache.get().getLocalMusicList().clear();
                    AppCache.get().getLocalMusicList().addAll(musicList);
                    lvLocalMusic.setVisibility(View.VISIBLE);
                    vSearching.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                  }
                }.execute();
              }

              @Override
              public void onDenied() {
                ToastUtils.show(R.string.no_permission_storage);
                lvLocalMusic.setVisibility(View.VISIBLE);
                vSearching.setVisibility(View.GONE);
              }
            })
            .request();
  }

  @Override
  protected void setListener() {
    lvLocalMusic.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Music music = AppCache.get().getLocalMusicList().get(position);
    AudioPlayer.get().addAndPlay(music);
    ToastUtils.show("已添加到播放列表");
  }

  @Override
  public void onMoreClick(final int position) {
    Music music = AppCache.get().getLocalMusicList().get(position);
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(music.getTitle());
    dialog.setItems(R.array.local_music_dialog, (dialog1, which) -> {
      switch (which) {
        case 0:// 分享
          shareMusic(music);
          break;
        case 1:// 设为铃声
          requestSetRingtone(music);
          break;
        case 2:// 查看歌曲信息
          MusicInfoActivity.start(this, music);
          break;
        case 3:// 删除
          deleteMusic(music);
          break;
      }
    });
    dialog.show();
  }

  /**
   * 分享音乐
   */
  private void shareMusic(Music music) {
    File file = new File(music.getPath());
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("audio/*");
    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
    startActivity(Intent.createChooser(intent, getString(R.string.share)));
  }

  private void requestSetRingtone(final Music music) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
      ToastUtils.show(R.string.no_permission_setting);
      Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
      intent.setData(Uri.parse("package:" + getPackageName()));
      startActivityForResult(intent, RequestCode.REQUEST_WRITE_SETTINGS);
    } else {
      setRingtone(music);
    }
  }

  /**
   * 设置铃声
   */
  private void setRingtone(Music music) {
    Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getPath());
    // 查询音乐文件在媒体库是否存在
    Cursor cursor = getContentResolver().query(uri, null,
            MediaStore.MediaColumns.DATA + "=?", new String[]{music.getPath()}, null);
    if (cursor == null) {
      return;
    }
    if (cursor.moveToFirst() && cursor.getCount() > 0) {
      String _id = cursor.getString(0);
      ContentValues values = new ContentValues();
      values.put(MediaStore.Audio.Media.IS_MUSIC, true);
      values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
      values.put(MediaStore.Audio.Media.IS_ALARM, false);
      values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
      values.put(MediaStore.Audio.Media.IS_PODCAST, false);

      getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?",
              new String[]{music.getPath()});
      Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
      RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);
      ToastUtils.show(R.string.setting_ringtone_success);
    }
    cursor.close();
  }

  private void deleteMusic(final Music music) {
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    String title = music.getTitle();
    String msg = getString(R.string.delete_music, title);
    dialog.setMessage(msg);
    dialog.setPositiveButton(R.string.delete, (dialog1, which) -> {
      File file = new File(music.getPath());
      if (file.delete()) {
        AppCache.get().getLocalMusicList().remove(music);
        mAdapter.notifyDataSetChanged();
        // 刷新媒体库
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
        sendBroadcast(intent);
      }
    });
    dialog.setNegativeButton(R.string.cancel, null);
    dialog.show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RequestCode.REQUEST_WRITE_SETTINGS) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
        ToastUtils.show(R.string.grant_permission_setting);
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    int position = lvLocalMusic.getFirstVisiblePosition();
    int offset = (lvLocalMusic.getChildAt(0) == null) ? 0 : lvLocalMusic.getChildAt(0).getTop();
    outState.putInt(Keys.LOCAL_MUSIC_POSITION, position);
    outState.putInt(Keys.LOCAL_MUSIC_OFFSET, offset);
  }

  public void onRestoreInstanceState(final Bundle savedInstanceState) {
    lvLocalMusic.post(() -> {
      int position = savedInstanceState.getInt(Keys.LOCAL_MUSIC_POSITION);
      int offset = savedInstanceState.getInt(Keys.LOCAL_MUSIC_OFFSET);
      lvLocalMusic.setSelectionFromTop(position, offset);
    });
  }
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back_local_music:
        finish();
        break;
      case R.id.loading_button:
        startActivity(new Intent(this, PlayActivity.class));
        break;
    }
  }
}
