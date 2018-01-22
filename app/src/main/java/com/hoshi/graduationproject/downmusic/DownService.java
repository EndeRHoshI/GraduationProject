package com.hoshi.graduationproject.downmusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.DownActivity;
import com.hoshi.graduationproject.net.HttpUtil;
import com.hoshi.graduationproject.provider.DownFileStore;
import com.hoshi.graduationproject.util.CommonUtils;
import com.hoshi.graduationproject.util.IConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by wm on 2016/12/13.
 */
public class DownService extends Service {
    public static final String ADD_DOWNTASK = "com.hoshi.graduationproject.downtaskadd";
    public static final String ADD_MULTI_DOWNTASK = "com.hoshi.graduationproject.multidowntaskadd";
    public static final String CANCLE_DOWNTASK = "com.hoshi.graduationproject.cacletask";
    public static final String CANCLE_ALL_DOWNTASK = "com.hoshi.graduationproject.caclealltask";
    public static final String START_ALL_DOWNTASK = "com.hoshi.graduationproject.startalltask";
    public static final String RESUME_START_DOWNTASK = "com.hoshi.graduationproject.resumestarttask";
    public static final String PAUSE_TASK = "com.hoshi.graduationproject.pausetask";
    public static final String PAUSE_ALLTASK = "com.hoshi.graduationproject.pausealltask";

    public static final String UPDATE_DOWNSTAUS = "com.hoshi.graduationproject.updatedown";
    public static final String TASK_STARTDOWN = "com.hoshi.graduationproject.taskstart";
    public static final String TASKS_CHANGED = "com.hoshi.graduationproject.taskchanges";

    private boolean d = true;
    private static final String TAG = "DownService";
    private static DownFileStore downFileStore;
    private ExecutorService executorService;
    private static ArrayList<String> prepareTaskList = new ArrayList<>();
    private int downTaskCount = 0;
    private int downTaskDownloaded = -1;
    private DownloadTask currentTask;
    private NotificationManager mNotificationManager;
    private Context mContext;
    private RemoteViews remoteViews;
    private int notificationid = 10;
    private boolean isForeground;
    private DownloadTaskListener listener = new DownloadTaskListener() {
        @Override
        public void onPrepare(DownloadTask downloadTask) {
        }

        @Override
        public void onStart(DownloadTask downloadTask) {
            Intent intent = new Intent(TASK_STARTDOWN);
            intent.putExtra("completesize", downloadTask.getCompletedSize());
            intent.putExtra("totalsize", downloadTask.getTotalSize());
            intent.setPackage(IConstants.PACKAGE);
            sendBroadcast(intent);
        }

        @Override
        public void onDownloading(DownloadTask downloadTask) {
            Intent intent = new Intent(UPDATE_DOWNSTAUS);
            intent.putExtra("completesize", downloadTask.getCompletedSize());
            intent.putExtra("totalsize", downloadTask.getTotalSize());
            intent.setPackage(IConstants.PACKAGE);
            sendBroadcast(intent);
        }

        @Override
        public void onPause(DownloadTask downloadTask) {
            sendIntent(TASKS_CHANGED);
            if (prepareTaskList.size() > 0) {
                if(currentTask != null)
                prepareTaskList.remove(currentTask.getId());
            }
            currentTask = null;
            upDateNotification();
            startTask();
        }

        @Override
        public void onCancel(DownloadTask downloadTask) {
            sendIntent(TASKS_CHANGED);
            if (prepareTaskList.size() > 0) {
                if(currentTask != null)
                prepareTaskList.remove(currentTask.getId());
            }
            currentTask = null;
            upDateNotification();
            startTask();
        }

        @Override
        public void onCompleted(DownloadTask downloadTask) {
            sendIntent(TASKS_CHANGED);
            if (prepareTaskList.size() > 0) {
                if(currentTask != null)
                prepareTaskList.remove(currentTask.getId());
            }
            currentTask = null;
            downTaskDownloaded++;
            startTask();

        }

        @Override
        public void onError(DownloadTask downloadTask, int errorCode) {
            startTask();
        }
    };

    public static ArrayList<String> getPrepareTasks() {
        return prepareTaskList;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        executorService = Executors.newSingleThreadExecutor();
        downFileStore = DownFileStore.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            mNotificationManager.cancel(notificationid);
        }
        String action = null;
        try {
            action = intent.getAction();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (action == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        switch (action) {
            case ADD_DOWNTASK:
                String name = intent.getStringExtra("name");
                String artist = intent.getStringExtra("artist");
                String url = intent.getStringExtra("url");
                addDownloadTask(name, artist, url);
                break;
            case ADD_MULTI_DOWNTASK:
                String[] names = intent.getStringArrayExtra("names");
                String[] artists = intent.getStringArrayExtra("artists");
                ArrayList<String> urls = intent.getStringArrayListExtra("urls");
                addDownloadTask(names, artists, urls);
                break;
            case RESUME_START_DOWNTASK:
                String taskid = intent.getStringExtra("downloadid");
                resume(taskid);
                break;
            case PAUSE_TASK:
                String taskid1 = intent.getStringExtra("downloadid");
                pause(taskid1);
                break;
            case CANCLE_DOWNTASK:
                String taskid3 = intent.getStringExtra("downloadid");
                cancel(taskid3);
                break;
            case CANCLE_ALL_DOWNTASK:
                if (prepareTaskList.size() > 1) {
                    prepareTaskList.clear();
                    if(currentTask != null)
                    prepareTaskList.add(currentTask.getId());
                }
                if(currentTask != null)
                cancel(currentTask.getId());
                downFileStore.deleteDowningTasks();
                sendIntent(TASKS_CHANGED);
                break;
            case START_ALL_DOWNTASK:
                String[] ids = downFileStore.getDownLoadedListAllDowningIds();
                for (int i = 0; i < ids.length; i++) {
                    if (!prepareTaskList.contains(ids[i])) {
                        prepareTaskList.add(ids[i]);
                    }
                }
                startTask();

                break;
            case PAUSE_ALLTASK:

                if (prepareTaskList.size() > 1) {
                    prepareTaskList.clear();
                    if(currentTask != null)
                    prepareTaskList.add(currentTask.getId());
                }
                if(currentTask != null)
                pause(currentTask.getId());
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(notificationid);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private String getDownSave() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/remusic/");
            if (!file.exists()) {
                boolean r = file.mkdirs();
                if (!r) {
                    Toast.makeText(mContext, "储存卡无法创建文件", Toast.LENGTH_SHORT).show();
                    return null;
                }
                return file.getAbsolutePath() + "/";
            }
            return file.getAbsolutePath() + "/";
        } else {
            Toast.makeText(mContext, "没有储存卡", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void addDownloadTask(String[] names, String[] artists, ArrayList<String> urls) {

        int len = urls.size();
        for (int i = 0; i < len; i++) {
            DownloadDBEntity dbEntity = new DownloadDBEntity((urls.get(i)).hashCode() + "", 0l,
                    0l, urls.get(i), getDownSave(), names[i], artists[i], DownloadStatus.DOWNLOAD_STATUS_INIT);
            downFileStore.insert(dbEntity);
            prepareTaskList.add(dbEntity.getDownloadId());
            downTaskCount++;
        }
        Toast.makeText(mContext,"已加入到下载", Toast.LENGTH_SHORT).show();
        upDateNotification();
        if (currentTask != null) {
            return;
        }

        startTask();

    }

    private void addDownloadTask(String name, String artist, String url) {


        DownloadDBEntity dbEntity = new DownloadDBEntity((url).hashCode() + "", 0l,
                0l, url, getDownSave(), name, artist, DownloadStatus.DOWNLOAD_STATUS_INIT);
        downFileStore.insert(dbEntity);
        prepareTaskList.add(dbEntity.getDownloadId());
        downTaskCount++;
        upDateNotification();
        Toast.makeText(mContext,"已加入到下载", Toast.LENGTH_SHORT).show();
        if (currentTask != null) {
            return;
        }

        startTask();

    }

    private void upDateNotification() {
        if (currentTask == null) {
            return;
        }
        if (!isForeground) {
            startForeground(notificationid, getNotification(false));
            isForeground = true;
        } else {
            mNotificationManager.notify(notificationid, getNotification(false));
        }
    }

    private void cancleNotification() {
        stopForeground(true);
        isForeground = false;
        mNotificationManager.notify(notificationid, getNotification(true));
        downTaskCount = 0;
        downTaskDownloaded = -1;

    }


    public void startTask() {
        if (currentTask != null) {
            return;
        }
        if (prepareTaskList.size() > 0) {
            DownloadTask downloadTask = null;
            DownloadDBEntity entity = downFileStore.getDownLoadedList(prepareTaskList.get(0));

            if (entity != null) {
                downloadTask = DownloadTask.parse(entity, mContext);
            }
            if (downloadTask == null) {
                return;
            }
            if (downloadTask.getDownloadStatus() != DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PREPARE);
                downloadTask.setdownFileStore(downFileStore);
                downloadTask.setHttpClient(HttpUtil.mOkHttpClient);
                downloadTask.addDownloadListener(listener);
                executorService.submit(downloadTask);
                currentTask = downloadTask;
                upDateNotification();
                sendIntent(TASKS_CHANGED);
            }
        } else {
            cancleNotification();
        }
    }

    /**
     * if return null,the task does not exist
     *
     * @param taskId
     * @return
     */
    public void resume(String taskId) {

        downTaskCount++;
        prepareTaskList.add(taskId);
        upDateNotification();
        sendIntent(TASKS_CHANGED);
        if (currentTask == null) {
            startTask();
        }

    }


    public void cancel(String taskId) {
        if (currentTask != null) {
            if (taskId.equals(currentTask.getId())) {
                currentTask.cancel();
                currentTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
            }
        }

        if (prepareTaskList.contains(taskId)) {
            downTaskCount--;
            prepareTaskList.remove(taskId);
        }

        if (prepareTaskList.size() == 0) {
            currentTask = null;
        }
        downFileStore.deleteTask(taskId);
        upDateNotification();
        sendIntent(TASKS_CHANGED);
    }

    public void pause(String taskid) {
        downTaskCount--;

        if (currentTask != null && taskid.equals(currentTask.getId())) {
            currentTask.pause();
        }
        prepareTaskList.remove(taskid);
        if (prepareTaskList.size() == 0) {
            currentTask = null;
        }
        upDateNotification();
        sendIntent(TASKS_CHANGED);
    }

    private Notification getNotification(boolean complete) {

        if (downTaskCount == 0) {
            downTaskCount = prepareTaskList.size();
        }
        if (downTaskDownloaded == -1) {
            downTaskDownloaded = 0;
        }
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.down_notification);

        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0,
                new Intent(this.getApplicationContext(), DownActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        final Intent nowPlayingIntent = new Intent();
        nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        nowPlayingIntent.setComponent(new ComponentName("com.hoshi.graduationproject", "com.hoshi.graduationproject.activity.DownActivity"));
        PendingIntent clickIntent = PendingIntent.getActivity(this,0,nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.image, R.drawable.placeholder_disk_210);
        if(complete){
            remoteViews.setTextViewText(R.id.title, "remusic" );
            remoteViews.setTextViewText(R.id.text, "下载完成，点击查看" );
            remoteViews.setTextViewText(R.id.time, showDate());
        }else {
            remoteViews.setTextViewText(R.id.title, "下载进度：" + downTaskDownloaded + "/" + downTaskCount);
            remoteViews.setTextViewText(R.id.text, "正在下载：" + currentTask.getFileName());
            remoteViews.setTextViewText(R.id.time, showDate());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContent(remoteViews)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(clickIntent);

        if (CommonUtils.isJellyBeanMR1()) {
            builder.setShowWhen(false);
        }
        return builder.build();
    }

    public static String showDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("a hh:mm");
        String date = sDateFormat.format(new Date());
        return date;
    }

    private void sendIntent(String action){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setPackage(IConstants.PACKAGE);
        sendBroadcast(intent);
    }


}
