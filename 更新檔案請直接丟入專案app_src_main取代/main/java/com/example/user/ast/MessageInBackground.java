package com.example.user.ast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;



public class MessageInBackground extends JobService{
    SharedPreferences TimeToMessage; //抓時間
    SharedPreferences Healthrecord; // 病例
    SharedPreferences Setting;  //設定
    JobParameters Jpar;
    int messagehour;
    int messagemin;
    String uri;  // 路徑

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("ExampleService","JOBSTART");
        TimeToMessage = getApplication().getSharedPreferences("timetomessage",0);
        Healthrecord = getApplication().getSharedPreferences("healthresult",0);
        Setting = getApplication().getSharedPreferences("settingsave",Context.MODE_PRIVATE); //讀音樂資料
        messagehour=TimeToMessage.getInt("SetHour",24);
        messagemin=TimeToMessage.getInt("SetMin",60);
        Log.d("Exam",String.valueOf(messagehour));
        Log.d("Exam",String.valueOf(messagemin));
        Jpar = params;

        uri = Setting.getString("music_rw2","");
        Log.d("Exam","3小音樂怒"+uri);

        new task().execute();



        return false;
    }

    public class task extends AsyncTask<Void, Void, Void >{

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("Exam", "進入AsyncTask");

            while (messagehour != 24 && messagemin != 60) {

                //如果發現跟之前不一樣 停止
                int f1 = TimeToMessage.getInt("SetHour",24);
                int f2 = TimeToMessage.getInt("SetMin",60);
                if(f1 != messagehour || f2 != messagemin){
                    Log.d("ExampleService", "GG");
                    return null; //新工作開始
                }

                //得到時間
                int pm = new GregorianCalendar().get(Calendar.AM_PM);
                int hour = new GregorianCalendar().get(Calendar.HOUR);
                int min = new GregorianCalendar().get(Calendar.MINUTE);
                Log.d("Exam","ampm:"+String.valueOf(pm)+" hour: "+String.valueOf(hour)
                        + " min: "+String.valueOf(min));
                // 轉換12小時制
                if ( ( ( pm == 1 ) && ( hour == (messagehour-12) ) && ( min == messagemin ) ) ||  ( ( hour == messagehour ) && ( min == messagemin ) ) ) {
                    notificationcall(Healthrecord.getInt("acp", 0));
                    jobFinished(Jpar, true); //end 釋放資源
                    Log.d("ExampleService", "JOBOVER");
                    break;
                }
                try {
                    Thread.sleep(1000);//sleep1s
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("ExampleService", "prev JobEnd");
        return false;
    }

    public void notificationcall(int num){
        Log.d("ExampleService", "通知");
        String noteMessage;
        noteMessage="";
        switch (num){
            case 0:
                break;
            case 1:
                noteMessage+="多C一點O氣";
                break;
            case 2:
                noteMessage+="準備個口罩";
                break;
            case 3:
                noteMessage+="不建議久留外面";
                break;
            case 4:
                noteMessage+="別出門了吧";
                break;
            case 5:
                noteMessage+="外面的世界是很危險的!";
                break;
            case 6:
                noteMessage+="絕對不能出門，出門會直接傷害到您的生命";
                break;
            case 7:
                noteMessage+="絕對不能出門，外面應該是世界末日了!";
                break;
        }


        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this,"ch3")
                .setSmallIcon(R.drawable.fa)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.fa))
                .setContentTitle("今日的空氣狀況是!!!")
                .setContentText(noteMessage);
        if(!uri.equals("") && (Setting.getBoolean("rcschecked",true) == true) ){
            if(Setting.getBoolean("rcrchecked",true) == true)
                notificationBuilder.setSound(Uri.parse(uri)) ; //音樂來一夏
        }
        if(Setting.getBoolean("rcvchecked",true) == true){
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build()); //送通知id:1


    }
}
