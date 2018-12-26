package com.example.user.ast;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;
import java.util.*;

public class MyService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{
    private MediaPlayer mMediaPlayer = null;
    private boolean mbIsInitialised = true;
    Calendar mycal = new GregorianCalendar();
    int hour = mycal.get(Calendar.HOUR);
    int minute = mycal.get(Calendar.MINUTE);
    boolean MessageCheck = false;
    public MyService() {
        super();
    }

    @Override
    public IBinder onBind(Intent arg0){Toast.makeText(MyService.this,"成功",Toast.LENGTH_LONG);return null;}

    @Override
    public void onCreate(){
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.skygate);

        try{
            mMediaPlayer.setDataSource(this,uri);
        }catch (Exception e){
            Toast.makeText(MyService.this,"錯誤",Toast.LENGTH_LONG).show();
        }
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(hour==18&&minute==30)
            MessageCheck = true;
        if(MessageCheck) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                if (mbIsInitialised) {
                    mMediaPlayer.prepareAsync();
                    mbIsInitialised = false;
                } else
                    mMediaPlayer.start();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer){
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
        Toast.makeText(MyService.this,"播放",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer,int what,int extra){
        mediaPlayer.release();
        mediaPlayer=null;
        Toast.makeText(MyService.this,"停止播放",Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer){
        mMediaPlayer.release();
        mMediaPlayer=null;
        stopForeground(true);
        mbIsInitialised = true;
    }
}