package com.example.user.ast;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;



public class MessageInBackground extends JobService{
    SharedPreferences TimeToMessage;
    SharedPreferences Healthrecord;
    JobParameters Jpar;
    int messagehour;
    int messagemin;
    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d("ExampleService","JOBSTART");
        TimeToMessage = getApplication().getSharedPreferences("timetomessage",0);
        Healthrecord = getApplication().getSharedPreferences("healthresult",0);
        messagehour=TimeToMessage.getInt("SetHour",24);
        messagemin=TimeToMessage.getInt("SetMin",60);
        Log.d("Exam",String.valueOf(messagehour));
        Log.d("Exam",String.valueOf(messagemin));
        Jpar = params;
        new task().execute(); //go

        return true;
    }

    public class task extends AsyncTask<Void, Void, Void >{

        @Override
        protected Void doInBackground(Void...parm) {
            while (messagehour != 24 && messagemin != 60) {
                int pm = new GregorianCalendar().get(Calendar.AM_PM);
                int hour = new GregorianCalendar().get(Calendar.HOUR);
                int min = new GregorianCalendar().get(Calendar.MINUTE);
                Log.d("Exam","ampm:"+String.valueOf(pm));
                Log.d("Exam","hour"+String.valueOf(hour));
                Log.d("Exam","min"+String.valueOf(min));
                if ((pm== 1 && hour == (messagehour-12) && min == messagemin) || (hour == messagehour && min == messagemin)) {
                    notificationcall(Healthrecord.getInt("acp", 0));
                    jobFinished(Jpar, true); //end
                    Log.d("ExampleService", "JOBOVER");
                    break;
                }
                try {
                    Thread.sleep(1000);//sleep 1s
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("ExampleService", "JOBEND");
        return false;
    }

    public void notificationcall(int num){
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

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.fa)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.fa))
                .setContentTitle("今日的空氣狀況是!!!")
                .setContentText(noteMessage);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notificationBuilder.build());


    }
}
