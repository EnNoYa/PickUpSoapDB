package com.example.user.ast;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.content.BroadcastReceiver;


public class ais extends AppCompatActivity {

    TextView name;  //當前測站名字
    Button caredata;    //詳細資料
    TextView str[] = new TextView[2]; //句子
    TextView curState ; //當前狀態
    SharedPreferences HealthRecord;//read file
    GPSbrrc receiver; //廣播接收者
    gift gpsrec; //GPS 廣播接收
    JobScheduler myScheduler; //管理員
    JobInfo Jinfo; //工作須知

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ais);
        caredata = findViewById(R.id.obs_get);

        checkPermission();//檢查權限
        /*read */
        HealthRecord = getApplication().getSharedPreferences("healthresult", Context.MODE_PRIVATE);
        name = findViewById(R.id.area);
        str[0] = findViewById(R.id.str1);
        str[1] = findViewById(R.id.str2);
        curState = findViewById(R.id.currState);

        /*廣播接收器*/
        receiver = new GPSbrrc();
        Toast.makeText(this,"地點抓取中請稍後...",Toast.LENGTH_LONG).show();

        gpsrec = new gift();

        /*job工作排程*/
        myScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        Jinfo = new JobInfo.Builder(1, new ComponentName(this, alarm_backGroundjob.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)// WIFI網路就執行
                        .setPersisted(true)
                        .setPeriodic( 15 * 60 * 1000) // 週期15分鐘
                        .build();
        int result = myScheduler.schedule(Jinfo);
        if(result == JobScheduler.RESULT_SUCCESS){
            Log.d("mjob","背景執行規劃");
        }
        else{
            Log.d("mjob","背景執行規劃失敗");
        }

        caredata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_activity();
            }
        });
    }
    //檢查若尚未授權, 則向使用者要求定位權限
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(ais.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(ais.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ais.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter gpsfilt = new IntentFilter("gps_ok");//新增gps廣播事件
        registerReceiver(gpsrec, gpsfilt);


        str[0].setText(HealthRecord.getString("gzil1",""));
        str[1].setText(HealthRecord.getString("gzil2",""));

        IntentFilter filter = new IntentFilter("com.example.user.ast.task");//新增過濾事件
        registerReceiver(receiver, filter);

        StartTime(); //計時器
    }
    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
        unregisterReceiver(gpsrec);
    }
    private void open_activity(){//開啟觀測站資訊頁面
        Intent intent;
        intent = new Intent();
        intent.setClass(ais.this,ai.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 200){
            if (grantResults.length >= 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&  grantResults[1] == PackageManager.PERMISSION_GRANTED) {  // 使用者允許權限
                if(ContextCompat.checkSelfPermission(ais.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ais.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ais.this, "定位授權成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "程式需要定位權限才能運作", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    /*設定嚴重等級*/
    public void colorSet(int cas, TextView mystate){
        switch (cas){
            case 0:
                mystate.setText("維修");
                mystate.setTextColor(getResources().getColor(R.color.gray));
                break;
            case 1:
                mystate.setText(getResources().getString(R.string.strgas_good));
                mystate.setTextColor(getResources().getColor(R.color.g));
                break;
            case 2:
                mystate.setText(getResources().getString(R.string.strgas_normal));
                mystate.setTextColor(getResources().getColor(R.color.y));
                break;
            case 3:
                mystate.setText(getResources().getString(R.string.strgas_notgood));
                mystate.setTextColor(getResources().getColor(R.color.o));
                break;
            case 4:
                mystate.setText(getResources().getString(R.string.strgas_bad));
                mystate.setTextColor(getResources().getColor(R.color.r));
                break;
            case 5:
                mystate.setText(getResources().getString(R.string.strgas_verybad));
                mystate.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
            case 6:
                mystate.setText(getResources().getString(R.string.strgas_god));
                mystate.setTextColor(getResources().getColor(R.color.p));
                break;
            case 7:
                mystate.setText(getResources().getString(R.string.strgas_god));
                mystate.setTextColor(getResources().getColor(R.color.br));
                break;
        }
    }

    long mTimeLeftMillis = 6000000;
    private void StartTime(){
         new CountDownTimer(mTimeLeftMillis,5000){  //5秒更新
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftMillis=millisUntilFinished;
                updatetext();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    int k=0;
    void updatetext(){
        if(k==0){
            str[0].setText(HealthRecord.getString("gzil1",""));
            str[1].setText("");
            if(HealthRecord.getString("gzil2","not").equals("")||HealthRecord.getString("gzil2","not").equals("not")){
                k=0;
            }
            else{
                k=1;
            }
        }
        else{
            str[1].setText(HealthRecord.getString("gzil2",""));
            str[0].setText("");
            if(HealthRecord.getString("gzil1","not").equals("")||HealthRecord.getString("gzil1","not").equals("not")){
                k=1;
            }
            else{
                k=0;
            }
        }
    }

    /*自定義廣播接收物件*/
    public class GPSbrrc extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("shit","廣播訊息:設定顏色囉");
            colorSet(HealthRecord.getInt("acp", -1), curState);
        }
    }
    /*自定義廣播接收物件2 地點更新接收物件*/
    public class gift extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("mjob","地點更新了，開始任務");
            SharedPreferences saveid = getApplication().getSharedPreferences("ssssid", Context.MODE_PRIVATE);
            name.setText("觀測站-"+saveid.getString("idsave", "沒收到"));

            /*開始任務*/
            startService(new Intent(ais.this, MyService.class));
        }
    }
}
