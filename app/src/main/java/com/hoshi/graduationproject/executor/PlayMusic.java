package com.hoshi.graduationproject.executor;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.model.Music;
import com.hoshi.graduationproject.storage.preference.Preferences;
import com.hoshi.graduationproject.utils.NetworkUtils;

/**
 * Created by hzwangchenyan on 2017/1/20.
 */
public abstract class PlayMusic implements IExecutor<Music> {
    private Activity mActivity;
    protected Music music;
    private int mTotalStep;
    protected int mCounter = 0;

    public PlayMusic(Activity activity, int totalStep) {
        mActivity = activity;
        mTotalStep = totalStep;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkPlay = Preferences.enableMobileNetworkPlay();
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkPlay) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.play_tips);
            builder.setPositiveButton(R.string.play_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Preferences.saveMobileNetworkPlay(true);
                    getPlayInfoWrapper();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            getPlayInfoWrapper();
        }
    }

    private void getPlayInfoWrapper() {
        onPrepare();
        getPlayInfo();
    }

    protected abstract void getPlayInfo();

    protected void checkCounter() {
        mCounter++;
        Log.d("haha", "" + mCounter);
        if (mCounter == mTotalStep) {
            onExecuteSuccess(music);
            /*Log.d("haha" ,"id" + music.getId());
            Log.d("haha" ,"type" + music.getType());
            Log.d("haha" ,"songid" + music.getSongId());
            Log.d("haha" ,"title" + music.getTitle());
            Log.d("haha" ,"artist" + music.getArtist());
            Log.d("haha" ,"album" + music.getAlbum());
            Log.d("haha" ,"albumid" + music.getAlbumId());
            Log.d("haha" ,"coverpath" + music.getCoverPath());
            Log.d("haha" ,"duration" + music.getDuration());
            Log.d("haha" ,"path" + music.getPath());
            Log.d("haha" ,"filename" + music.getFileName());
            Log.d("haha" ,"filesize" + music.getFileSize());*/
            /*Log.d("haha" ,"pic" + music.getCoverPath());*/
        }
        if (mCounter < mTotalStep) {
            onExecuteFail(null);
        }
    }
}
