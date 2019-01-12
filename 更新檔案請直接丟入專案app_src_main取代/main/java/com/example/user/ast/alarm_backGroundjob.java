package com.example.user.ast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;


public class alarm_backGroundjob extends JobService {

    private RequestQueue mRQ;
    static final int MIN_TIME = 5 * 60 * 1000; //位置更新條件：60s
    static final float MIN_DIST = 100;   //位置更新條件：100 公尺
    LocationManager mgr;    // 定位管理員
    LocationListener lis;    // 定位聆聽
    LatLng currPoint;   //現在的點
    private int M = 79; //最大值
    boolean isGPSEnabled;      //GPS定位是否可用
    boolean isNetworkEnabled;  //網路定位是否可用
    String sitename; //觀測站名稱
    int AQI[] = new int[6]; // SO2 CO O3 PM10 PM2.5 NO2
    int cas; //狀況 處理
    String uri; //音樂
    SharedPreferences sp; //設定的存檔
    SharedPreferences tc; //thread的存檔
    String strgzil2; //句子2
    String strgzil1; //句子1
    SharedPreferences HealthRecord;// 存檔用 病例 acp
    SharedPreferences.Editor editor;
    JobParameters jobpar;  // job額外參數
    myprocess tmp = new myprocess(); //子 只有1 個
    int maxdegree = 0, whomax = -1; // 最大acp ， 跟誰最大

    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("mjob","背景執行開始");

        sp = getApplication().getSharedPreferences("settingsave",Context.MODE_PRIVATE); // 設定音樂
        tc = getApplication().getSharedPreferences("thread_cnt",Context.MODE_PRIVATE); // 讀thread數量
        uri = sp.getString("music_rw",""); //拿音樂位置

        jobpar = params;

        /*清空句子*/
        strgzil2 = ""; //句子2
        strgzil1 = ""; //句子1

        HealthRecord = getApplication().getSharedPreferences("healthresult", Context.MODE_PRIVATE); //病例 存檔
        editor = HealthRecord.edit();

        mRQ = Volley.newRequestQueue(this);
        mgr = (LocationManager)getSystemService(LOCATION_SERVICE);

        tmp.start(); // 開始thread

        return false;
    }

    @SuppressLint("MissingPermission")
    private class myprocess extends Thread{
        /*開始任務*/
        @Override
        public void run() {
            Log.d("mjob","進入Thread");
            lis =new LocationListener() {
                @Override
                public void onLocationChanged (Location location){
                    Log.d("mjob", "定位更新");
                    currPoint = new LatLng(location.getLatitude(), location.getLongitude());
                    save_data(shortest_place(currPoint)); //存現在位置觀測站編號

                    /*發警告通知檢查*/
                    jsonParse();

                    sendBroadcast(new Intent("gps_ok"));//開廣播 王小明
                }
                @Override
                public void onStatusChanged (String provider,int status, Bundle extras){
                }
                @Override
                public void onProviderEnabled (String provider){
                }
                @Override
                public void onProviderDisabled (String provider){
                    Toast.makeText(alarm_backGroundjob.this, "您人生是不是沒有方向，汝尋彼岸否，要開定位", Toast.LENGTH_SHORT).show();
                }
            };

            Looper.prepare();
            //檢查 GPS 與網路定位是否可用
            isGPSEnabled =mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled =mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isGPSEnabled||isNetworkEnabled) {
                Log.d("mjob", "GPS");
                mgr.requestLocationUpdates(   //向 GPS 定位提供者註冊位置事件監聽器
                        LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, lis, Looper.myLooper());
                Log.d("mjob", "WIFI");
                mgr.requestLocationUpdates(   //向網路定位提供者註冊位置事件監聽器
                        LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, lis, Looper.myLooper());
            }
            else {
                Toast.makeText(alarm_backGroundjob.this, "請開啟定位，才行哦", Toast.LENGTH_LONG).show();
            }
            Log.d("mjob","背景執行迴圈");

            Looper.loop();  //格黨 無限
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("mjob","背景執行END");
        return false;
    }


    public void jsonParse(){

        final int state; // 使用者狀態
        final SharedPreferences sp = getApplication().getSharedPreferences("user_aqi",Context.MODE_PRIVATE);//api load
        /*讀取使用值*/
        for(int i=0; i<6; ++i){
            AQI[i] = sp.getInt("aqi" + String.valueOf(i), -1);
        }
        state = sp.getInt("st", 0); //取值

        String url = "http://140.136.149.239:9487/recentAQI";
        JsonArrayRequest request  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            int id = -1; // 觀測站代碼
                            cas = state; // default
                            boolean noti = false, good = false;
                            String str = "";
                            maxdegree = 0;
                            whomax = -1;
                            int primecheck = 0; //質數選擇空氣

                            for(int i=0; i<response.length(); ++i) {
                                JSONObject tmp = response.getJSONObject(i); //json物件
                                if(tmp.getString("SiteName").equals(sitename)){//觀測站名稱
                                    id = i;
                                    break;
                                }
                            }
                            if(id==-1){//沒有這個觀測站
                                //e04
                                editor.putInt("acp", 0).commit();
                                Log.d("mjob","數值維修");
                            }
                            else {
                                for(int i=0; i<6; i++){
                                    if(sp.contains("AQI"+String.valueOf(i))){
                                        sp.edit().clear().apply(); //清空檔案避免過大
                                        break;
                                    }
                                }

                                JSONObject tmp = response.getJSONObject(id); //json物件
                                if (!tmp.isNull("SO2Ans")) {
                                    String SO2 = tmp.getString("SO2Ans");
                                    int val = Integer.valueOf(SO2);
                                    if(AQI[0] !=-1){// 第一次的判斷
                                        if(val > AQI[0]){ // SO2嚴重了
                                            cas = 2;
                                            noti = true;
                                            str += "SO2、";
                                        }
                                        else if(val < AQI[0])
                                            good = true;
                                    }
                                    AQI[0] = val;
                                    if(val >= maxdegree)
                                    {
                                        maxdegree = val;whomax=2;
                                        primecheck |= 4;
                                    }
                                }

                                if (!tmp.isNull("COAns")) {
                                    String CO = tmp.getString("COAns");
                                    int val = Integer.valueOf(CO);
                                    if(AQI[1] !=-1){
                                        if(val > AQI[1]){ // CO嚴重了
                                            cas = 2;
                                            noti = true;
                                            str += "CO、";
                                        }
                                        else if(val < AQI[1])
                                            good = true;
                                    }
                                    AQI[1] = val;
                                    if(val >= maxdegree)
                                    {
                                        maxdegree=val;whomax=0;
                                        primecheck |= 1;
                                    }
                                }

                                if (!tmp.isNull("O3Ans")) {
                                    String O3 = tmp.getString("O3Ans");
                                    int val = Integer.valueOf(O3);
                                    if(AQI[2] !=-1){
                                        if(val > AQI[2]){ // O3嚴重了
                                            cas = 2;
                                            noti = true;
                                            str += "O3、";
                                        }
                                        else if(val < AQI[2])
                                            good = true;
                                    }
                                    AQI[2] = val;
                                    if(val >= maxdegree)
                                    {
                                        maxdegree=val;whomax=5;
                                        primecheck |= 32;
                                    }
                                }

                                if (!tmp.isNull("PM10Ans")) {
                                    String PM10 = tmp.getString("PM10Ans");
                                    int val = Integer.valueOf(PM10);
                                    if(AQI[3] !=-1){
                                        if(val > AQI[3]){ // PM10嚴重了
                                            cas = 2;
                                            noti = true;
                                            str += "PM10、";
                                        }
                                        else if(val < AQI[3])
                                            good = true;
                                    }
                                    AQI[3] = val;
                                    if(val >= maxdegree)
                                    {
                                        maxdegree=val;whomax=4;
                                        primecheck |= 16;
                                    }
                                }

                                if (!tmp.isNull("PM25Ans")) {
                                    String PM25 = tmp.getString("PM25Ans");
                                    int val = Integer.valueOf(PM25);
                                    if(AQI[4] !=-1){
                                        if(val > AQI[4]){ // PM25嚴重了
                                            cas = 2;
                                            noti = true;
                                            str += "PM25、";
                                        }
                                        else if(val < AQI[4])
                                            good = true;
                                    }
                                    AQI[4] = val;
                                    if(val >= maxdegree)
                                    {
                                        maxdegree=val;whomax=1;
                                        primecheck |= 2;
                                    }
                                }

                                if (!tmp.isNull("NO2Ans")) {
                                    String NO2 = tmp.getString("NO2Ans");
                                    int val = Integer.valueOf(NO2);
                                    if(AQI[5] !=-1){
                                        if(val > AQI[5]){ // NO2嚴重了
                                            cas = 2;
                                            noti = true;
                                            str += "NO2、";
                                        }
                                        else if(val < AQI[5])
                                            good = true;
                                    }
                                    AQI[5] = val;
                                    if(val >= maxdegree)
                                    {
                                        maxdegree=val;whomax=3;
                                        primecheck |= 8;
                                    }
                                }

                                boolean allclear = true; // 空氣乾淨
                                for(int i=0; i<6; ++i){ //記得存檔回去
                                    sp.edit().putInt("aqi"+String.valueOf(i), AQI[i]).apply();
                                    if(AQI[i] > 1)
                                        allclear = false;
                                }
                                if(good && !noti){ // 表示任意一個值有下降，但不能有壞消息
                                    good = down_fsm(cas, allclear);
                                }

                                sp.edit().putInt("st",cas).apply(); //狀態save

                                Log.d("mjob","通知檢查+哪個cas"+String.valueOf(cas));
                                if(noti){ //bad 通知訊息
                                    Notice(str.substring(0, str.length()-1), 1);
                                }
                                else if(good){
                                    Notice("",0);
                                }

                                /*acp等級*/
                                if(HealthRecord.getInt("acp",-1) != -1)//不是空的就刪除
                                    editor.remove("acp").commit();
                                if(maxdegree != 0){
                                    editor.putInt("acp", maxdegree).commit();
                                    Log.d("mjob","數值"+String.valueOf(maxdegree));
                                }
                                else{
                                    editor.putInt("acp", 0).commit();
                                    Log.d("mjob","數值維修");
                                }
                                sendBroadcast(new Intent("com.example.user.ast.task"));
                            }
                            aboutyourbreath(maxdegree, whomax, primecheck);

                            /*句子1*/
                            if(!HealthRecord.getString("gzil1","").equals(""))//不是空的就刪除
                                editor.remove("gzil1").commit();
                            editor.putString("gzil1", strgzil1).commit();

                            /*句子2*/
                            if(!HealthRecord.getString("gzil2","").equals(""))
                                editor.remove("gzil2").commit();
                            if(!strgzil2.isEmpty()){
                                strgzil2 = "可能引起:　" + strgzil2;
                            }
                            editor.putString("gzil2",strgzil2).commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        mRQ.add(request);
    }

    /*當前狀態--下降機制*/
    private boolean down_fsm(int state, boolean clear){
        switch (state){
            case 2:
                if(!clear){
                    cas = 1;
                    return false;
                }
                else{
                    cas = 0;
                    return true;
                }
            case 1:
                cas = 0;
                return true;
            default:
                cas = 0;
                return false;
        }
    }

    public void aboutyourbreath(int num, int who, int gas_select) {//引發疾病

        if(who==-1){
            strgzil1 ="維修中哦~~";
            who=0;
        }
        if(HealthRecord.getBoolean("checkedHeartDisease",true)==false&&HealthRecord.getBoolean("checkedDVC",true)==false&&HealthRecord.getBoolean("checkedRespiratoryDisease",true)==false&&HealthRecord.getBoolean("checkedConjunctivitis",true)==false&&HealthRecord.getBoolean("checkedAllergicRhinitis",true)==false||who==0){
            if (num ==1) {
                strgzil1 ="非常新鮮的空氣，多到戶外走走吧!";
            } else if (num ==2) {
                strgzil1 ="新鮮的空氣，放心到戶外走走吧!";
            } else if (num ==3) {
                strgzil1 ="正常不正常邊緣的空氣，盡量別待在戶外太久!";
            } else if(num==4) {
                strgzil1 = "戴一下口罩吧!這空氣有點髒!";
            }else if(num==5){
                strgzil1 ="別出門了，要不然戴個防毒面具好不?";
            }else if(num==6){
                strgzil1 ="求您了，別出門，防毒面具也救不了您";
            }else if(num==7){
                strgzil1 ="求您了，別出門，生命是很寶貴的!";
            }
        }
        else{
            if(HealthRecord.getBoolean("checkedHeartDisease",true)==false&&HealthRecord.getBoolean("checkedDVC",true)==false&&HealthRecord.getBoolean("checkedRespiratoryDisease",true)==true&&HealthRecord.getBoolean("checkedConjunctivitis",true)==false&&HealthRecord.getBoolean("checkedAllergicRhinitis",true)==false){
                if (num ==1) {
                    strgzil1 ="非常新鮮的空氣，多到戶外走走吧!";
                } else if (num ==2) {
                    strgzil1 ="新鮮的空氣，放心到戶外走走吧!";
                } else if (num ==3) {
                    strgzil1 ="正常不正常邊緣的空氣，記得要戴口罩喔!";
                } else if(num==4) {
                    strgzil1  = "這空氣有點髒!還是別出門了吧!";
                }else if(num==5){
                    strgzil1 ="沒防毒面具救別出門了!";
                }else if(num==6){
                    strgzil1 ="求您了，別出門，防毒面具也救不了您";
                }else if(num==7){
                    strgzil1 ="求您了，別出門，生命是很寶貴的!";
                }
            }
            else{
                if (num ==1) {
                    strgzil1 ="多C一點O氣";
                } else if (num ==2) {
                    strgzil1 =" 可以正常出門";
                } else if (num ==3) {
                    strgzil1 ="要出門要戴口罩!";
                } else if(num==4) {
                    strgzil1 = "不建議出門";
                }else if(num==5){
                    strgzil1 ="不能出門，出門會造成身體危害";
                }else if(num==6){
                    strgzil1 ="絕對不能出門，出門會直接傷害到您的生命";
                }else if(num==7){
                    strgzil1 ="絕對不能出門，外面應該是世界末日了!";
                }
            }
        }

        boolean checkmul=false;

        if ( (gas_select & 2) == 2 && num > 2 || (gas_select & 4) == 4 && num > 2 || (gas_select & 8) == 8 && num > 2 || (gas_select & 16) == 16 && num > 2 || (gas_select & 32) == 32 && num > 2)
        {
            checkmul=true;
            strgzil2+="肺功能下降";
        }
        if ( (gas_select & 2) == 2 && num > 2 || (gas_select & 4) == 4 && num > 2 || (gas_select & 8) == 8 && num > 2  || (gas_select & 32) == 32 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="咳嗽";
        }
        if ( (gas_select & 2)==2 && num > 2|| (gas_select & 32) == 32 && num > 2) {
            if(checkmul)strgzil2+="、";
            checkmul=true;
            strgzil2+="肺癌、血癌、自律神經失調";
        }
        if ( (gas_select & 4) == 4 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="呼吸困難、呼吸道阻塞";
        }
        if ( (gas_select & 32) == 32 && num > 2) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="加速老化、皮膚疾病";
        }
        if ( (gas_select & 8) == 8 && num > 2|| (gas_select & 16) == 16 && num > 2 ) {
            if(checkmul) strgzil2+="、";
            checkmul=true;
            strgzil2+="頭痛";
        }
        if ( (gas_select & 16) == 16 && num > 2 ) {
            if(checkmul) strgzil2+="、";
            strgzil2+="噁心、虛弱";
        }
    }

    private String judge(){
        switch (maxdegree){
            case 2:
                return "acp為普通，此地區還可以待一陣子。";
            case 3:
                return "acp為不好，我建議您出門戴個口罩!!";
            case 4:
                return "acp為糟，不建議出門。若出門此地不宜久留";
            case 5:
                return "acp為很糟，開著空氣清淨機，離開這裡";
            case 6:
                return "acp為極度危險，您還看得見天空嗎???";
            case 7:
                return "災害等級:核爆，不建議留在這裡。";
            default:
                return "";
        }
    }
    private void Notice(String message, int chid){ // 警告通知
        NotificationCompat.Builder notificationBuilder;
        if(chid == 1){
            String ch = "ch1"; //頻道
            String s_content = judge(); // 字串內容
            NotificationCompat.InboxStyle t = new NotificationCompat.InboxStyle(); //暫時存用 自訂折疊式通知
            notificationBuilder = new NotificationCompat.Builder(this, ch)
                    .setSmallIcon(R.drawable.fa)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.fa))
                    .setContentTitle("提醒您!!!")
                    .setContentText("此地區" + message + "濃度變高了"); //內容

            t.addLine("此地區" + message + "濃度變高了").addLine(s_content);
            if(maxdegree == 6){
                t.addLine("快離開這裡，此地危險。");
            }
            else if(maxdegree == 7){
                t.addLine("勸您迅速脫離此地區。");
                t.addLine("或選擇攜帶空氣清淨機，並且停止呼吸");
            }
            if(maxdegree > 3){
                t.addLine("再次貼心提醒---口罩不離身");
            }
            notificationBuilder.setStyle(t); //自訂 帥帥
        }
        else{
            String ch = "ch2"; //頻道
            notificationBuilder = new NotificationCompat.Builder(this, ch)
                    .setSmallIcon(R.drawable.fa)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.fa))
                    .setContentTitle("恭喜您!!!")
                    .setContentText("此地區現在空氣非常優質，請多C一點O氣");
        }
        if(!uri.equals("") && (sp.getBoolean("icschecked",true) == true) ){ //設定有開
            if(sp.getBoolean("icrchecked",true) == true)  //鈴聲 開關
                notificationBuilder.setSound(Uri.parse(uri)) ; //音樂來一夏
        }
        if(sp.getBoolean("icvchecked",true) == true){ //震動開關
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); //管理員取得服務
        notificationManager.notify(9487, notificationBuilder.build());//通知開始
    }

    private void save_data(int id){//存入當前地區
        sitename = place_name[id];
        SharedPreferences saveid = getApplication().getSharedPreferences("ssssid", Context.MODE_PRIVATE);
        if(!saveid.getString("idsave","").equals(""))
            saveid.edit().remove("idsave").apply();
        saveid.edit().putString("idsave", sitename).apply();
    }

    public int shortest_place(LatLng curL){//查出是離哪個觀測站最近
        double dis = 1000.0;
        int id=0;
        for(int i=0; i<M; ++i){
            double diff1=curL.latitude-LLplace[i].latitude,diff2=curL.longitude-LLplace[i].longitude;
            if(diff1*diff1+diff2*diff2<dis){
                id=i;
                dis=diff1*diff1+diff2*diff2;
            }
        }
        return id;
    }

    private String place_name[] = {
            "富貴角", "陽明", "萬里", "淡水", "基隆", "士林", "林口", "三重", "菜寮", "汐止", "大同", "中山", "大園", "松山",
            "萬華", "新莊", "觀音", "古亭", "永和", "板橋", "桃園", "土城", "新店", "平鎮", "中壢", "龍潭", "湖口", "新竹",
            "頭份", "苗栗", "三義", "豐原", "沙鹿", "西屯", "忠明", "線西", "大里", "彰化", "埔里", "二林", "南投", "竹山",
            "麥寮", "臺西", "斗六", "新港", "圤子", "嘉義", "新營", "善化", "安南", "臺南", "美濃", "橋頭", "楠梓", "仁武",
            "左營", "屏東", "前金", "鳳山", "復興", "前鎮", "小港", "大寮", "潮州", "林園", "恆春", "宜蘭", "冬山", "花蓮",
            "關山", "臺東", "馬祖", "金門", "馬公", "臺南", "彰化", "崙背", "屏東"
    };

    /*觀測站位置資訊*/
    public LatLng LLplace[] = {
            new LatLng(25.29743611,121.537975), new LatLng(25.18272222,121.5295833), new LatLng(25.17966667,121.6898806),
            new LatLng(25.1645,121.4492389), new LatLng(25.12916667,121.7600556), new LatLng(25.10591667,121.5145),
            new LatLng(25.07857,121.3657028), new LatLng(25.07261111,121.4938056), new LatLng(25.06895,121.4810278),
            new LatLng(25.06566944,121.6408), new LatLng(25.0632,	121.5133111), new LatLng(25.06236111,121.5265278),
            new LatLng(25.06034444,	121.2018111), new LatLng(25.05,	121.5786111), new LatLng(25.04650278,	121.5079722),
            new LatLng(25.03797222,	121.4325), new LatLng(25.03550278,	121.0827611), new LatLng(25.02060833,	121.5295556),
            new LatLng(25.017,	121.5163056), new LatLng(25.01297222,	121.4586667), new LatLng(24.98677778,	121.3087222),
            new LatLng(24.98252778,	121.4518611), new LatLng(24.97722222,	121.5377778), new LatLng(24.95420833,	121.2049806),
            new LatLng(24.95327778,	121.2216667), new LatLng(24.86386944,	121.21635), new LatLng(24.90014167,	121.0386528),
            new LatLng(24.80561944,	120.972075), new LatLng(24.69696944,	120.8985722), new LatLng(24.56526944,	120.8202),
            new LatLng(24.38294167,	120.7588333), new LatLng(24.25658611,	120.7417111), new LatLng(24.22562778,	120.5687944),
            new LatLng(24.16219722,	120.6169167), new LatLng(24.15195833,	120.6410917), new LatLng(24.13167222,	120.4690611),
            new LatLng(24.09961111,	120.6776889), new LatLng(24.066,	120.5415194), new LatLng(23.96884167,	120.9679028),
            new LatLng(23.925175,	120.4096528), new LatLng(23.913,	120.6853056), new LatLng(23.75638889,	120.6773056),
            new LatLng(23.75350556,	120.251825), new LatLng(23.71753333,	120.2028417), new LatLng(23.71185278,	120.5449944),
            new LatLng(23.55483889,	120.3455306), new LatLng(23.46530833,	120.24735), new LatLng(23.46277778,	120.4408333),
            new LatLng(23.30563333,	120.31725), new LatLng(23.11509722,	120.2971417), new LatLng(23.04916667,	120.2183333),
            new LatLng(22.98458056,	120.2026167), new LatLng(22.88358333,	120.5305417), new LatLng(22.75750556,	120.3056889),
            new LatLng(22.73366667,	120.3282889), new LatLng(22.68905556,	120.3326306), new LatLng(22.67486111,	120.2929167),
            new LatLng(22.67308056,	120.4880333), new LatLng(22.63256667,	120.2880861), new LatLng(22.62739167,	120.3580833),
            new LatLng(22.60871111,	120.3120167), new LatLng(22.60538611,	120.3075639), new LatLng(22.56583333,	120.3377361),
            new LatLng(22.56413611,	120.425311), new LatLng(22.52310833,	120.561175), new LatLng(22.4795,	120.41175),
            new LatLng(21.95806944,	120.7889278), new LatLng(24.74791667,	121.7463944), new LatLng(24.63220278,	121.7929278),
            new LatLng(23.97130556,	121.5997694), new LatLng(23.04508333,	121.1619333), new LatLng(22.75535833,	121.15045),
            new LatLng(26.15361111,	119.9525), new LatLng(24.43213333,	118.3122556), new LatLng(23.56903056,	119.5661583),
            new LatLng(23.12216944,	120.4697361), new LatLng(23.84315833,	120.2818139), new LatLng(23.75754722,	120.3487417),
            new LatLng(22.35222222,	120.3772222),
    };
}
