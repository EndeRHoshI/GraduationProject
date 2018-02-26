package com.hoshi.graduationproject.executor;

import android.app.Activity;
import android.text.TextUtils;

import com.hoshi.graduationproject.http.HttpCallback;
import com.hoshi.graduationproject.http.HttpClient;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.model.OnlineMusic;
import com.hoshi.graduationproject.utils.FileUtils;

import java.io.File;

/**
 * 播放在线音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class PlayOnlineMusic extends PlayMusic {
  private OnlineMusic mOnlineMusic;

  public PlayOnlineMusic(Activity activity, OnlineMusic onlineMusic) {
    super(activity, 3);
    mOnlineMusic = onlineMusic;
  }

  @Override
  protected void getPlayInfo() {
    String artist = mOnlineMusic.getArtist_name();
    String title = mOnlineMusic.getTitle();

    music = new Music();
    music.setType(Music.Type.ONLINE);
    music.setTitle(title);
    music.setArtist(artist);
    music.setAlbum(mOnlineMusic.getAlbum_title());

    // 下载歌词
    String lrcFileName = FileUtils.getLrcFileName(artist, title);
    File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
    if (!lrcFile.exists() && !TextUtils.isEmpty(mOnlineMusic.getLrclink())) {
      downloadLrc(mOnlineMusic.getLrclink(), lrcFileName);
    }
    mCounter++;

    // 下载封面
    String albumFileName = FileUtils.getAlbumFileName(artist, title);
    File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
    String picUrl = mOnlineMusic.getPic_big();
    if (TextUtils.isEmpty(picUrl)) {
      picUrl = mOnlineMusic.getPic_small();
    }
    if (!albumFile.exists() && !TextUtils.isEmpty(picUrl)) {
      downloadAlbum(picUrl, albumFileName);
    }
    mCounter++;
    music.setCoverPath(albumFile.getPath());

    long tempDuration = Long.valueOf(mOnlineMusic.getDuration());
    if (tempDuration == 0) {
      mCounter --;
    } else {
      music.setPath("http://music.163.com/song/media/outer/url?id=" + mOnlineMusic.getSong_id() + ".mp3");
      music.setDuration(mOnlineMusic.getDuration());
    }
    checkCounter();

    // 获取歌曲播放链接
    /*HttpClient.getMusicDownloadInfo(mOnlineMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
      @Override
      public void onSuccess(DownloadInfo response) {
        *//*if (response == null || response.getBitrate() == null) {
          onFail(null);
          return;
        }*//*

        ToastUtils.show( "1" + mOnlineMusic.getSong_id());
        music.setPath("http://music.163.com/song/media/outer/url?id=" + mOnlineMusic.getSong_id() + ".mp3");
        music.setDuration(290 * 1000);
        checkCounter();
      }

      @Override
      public void onFail(Exception e) {
        ToastUtils.show( "2" + mOnlineMusic.getSong_id());
        onExecuteFail(e);
      }
    });*/
  }

  private void downloadLrc(String url, String fileName) {
    HttpClient.downloadFile(url, FileUtils.getLrcDir(), fileName, new HttpCallback<File>() {
      @Override
      public void onSuccess(File file) {
      }

      @Override
      public void onFail(Exception e) {
      }

      @Override
      public void onFinish() {

      }
    });
  }

  private void downloadAlbum(String picUrl, String fileName) {
    HttpClient.downloadFile(picUrl, FileUtils.getAlbumDir(), fileName, new HttpCallback<File>() {
      @Override
      public void onSuccess(File file) {
      }

      @Override
      public void onFail(Exception e) {
      }

      @Override
      public void onFinish() {
      }
    });
  }
}
