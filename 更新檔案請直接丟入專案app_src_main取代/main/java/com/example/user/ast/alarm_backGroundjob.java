package com.example.user.ast;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.AsyncTask;

import android.os.Bundle;
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

import java.util.Collections;

public class alarm_backGroundjob extends JobService {

    private JobParameters myjobp; //變數
    private RequestQueue mRQ;
    static final int MIN_TIME = 5000; //位置更新條件：1秒
    static final float MIN_DIST = 10;   //位置更新條件：10 公尺
    LocationManager mgr;    // 定位管理員
    LocationListener lis;    // 定位聆聽
    LatLng currPoint;   //現在的點
    private int M = 79; //最大值
    boolean isGPSEnabled;      //GPS定位是否可用
    boolean isNetworkEnabled;  //網路定位是否可用
    String sitename; //觀測站名稱
    int AQI[] = new int[6]; // SO2 CO O3 PM10 PM2.5 NO2

    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("mjob","背景執行開始");
        myjobp = params;
        mRQ = Volley.newRequestQueue(this);
        mgr = (LocationManager)getSystemService(LOCATION_SERVICE);
        lis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("mjob","定位更新");
                currPoint = new LatLng(location.getLatitude(), location.getLongitude());
                save_data(shortest_place(currPoint)); //存現在位置觀測站編號
                sendBroadcast(new Intent("gps_ok"));//開廣播 王小明
                /*發警告通知檢查*/
                jsonParse();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //檢查 GPS 與網路定位是否可用
        isGPSEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSEnabled||isNetworkEnabled) {
            Log.d("mjob","GPS");
            mgr.requestLocationUpdates(   //向 GPS 定位提供者註冊位置事件監聽器
                    LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, lis);
            Log.d("mjob","WIFI");
            mgr.requestLocationUpdates(   //向網路定位提供者註冊位置事件監聽器
                    LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, lis);
        }
        else{
            Toast.makeText(this,"請開啟定位，才行哦",Toast.LENGTH_LONG).show();
        }



        jobFinished(myjobp, true);
        Log.d("mjob","背景執行結束");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("mjob","背景執行結束");
        return false;
    }

    public void jsonParse(){

        final SharedPreferences sp = getApplication().getSharedPreferences("user_aqi",Context.MODE_PRIVATE);//api load
        /*讀取使用值*/
        for(int i=0; i<6; ++i){
            AQI[i] = sp.getInt("aqi" + String.valueOf(i), -1);
            Log.d("mjob","AOI"+String.valueOf(i)+" "+String.valueOf(AQI[i]));
        }

        String url = "http://140.136.149.239:9487/recentAQI";
        JsonArrayRequest request  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            int id = -1; // 觀測站代碼
                            for(int i=0; i<response.length(); ++i) {
                                JSONObject tmp = response.getJSONObject(i); //json物件
                                if(tmp.getString("SiteName").equals(sitename)){//觀測站名稱
                                    id = i;
                                    break;
                                }
                            }
                            if(id==-1){//沒有這個觀測站
                                //e04
                            }
                            else {
                                sp.edit().clear().apply(); //清空檔案避免過大

                                JSONObject tmp = response.getJSONObject(id); //json物件
                                if (!tmp.isNull("SO2Ans")) {
                                    String SO2 = tmp.getString("SO2Ans");
                                    int val = Integer.valueOf(SO2);
                                    if(val > AQI[0]){ // SO2嚴重了
                                        AQI[0] = val;
                                        Notice("SO2", 1); // 通知
                                    }
                                }

                                if (!tmp.isNull("COAns")) {
                                    String CO = tmp.getString("COAns");
                                    int val = Integer.valueOf(CO);
                                    if(val > AQI[1]){ // CO嚴重了
                                        AQI[1] = val;
                                        Notice("CO", 1); // 通知
                                    }
                                }

                                if (!tmp.isNull("O3Ans")) {
                                    String O3 = tmp.getString("O3Ans");
                                    int val = Integer.valueOf(O3);
                                    if(val > AQI[2]){ // O3嚴重了
                                        AQI[2] = val;
                                        Notice("O3", 1); // 通知
                                    }
                                }

                                if (!tmp.isNull("PM10Ans")) {
                                    String PM10 = tmp.getString("PM10Ans");
                                    int val = Integer.valueOf(PM10);
                                    if(val > AQI[3]){ // PM10嚴重了
                                        AQI[3] = val;
                                        Notice("PM10", 1); // 通知
                                    }

                                }

                                if (!tmp.isNull("PM25Ans")) {
                                    String PM25 = tmp.getString("PM25Ans");
                                    int val = Integer.valueOf(PM25);
                                    if(val > AQI[4]){ // PM25嚴重了
                                        AQI[4] = val;
                                        Notice("PM25", 1); // 通知
                                    }
                                }

                                if (!tmp.isNull("NO2Ans")) {
                                    String NO2 = tmp.getString("NO2Ans");
                                    int val = Integer.valueOf(NO2);
                                    if(val > AQI[5]){ // NO2嚴重了
                                        AQI[5] = val;
                                        Notice("NO2", 1); // 通知
                                    }
                                }
                                boolean safe = true;
                                for(int i=0; i<6; ++i){
                                    if(AQI[i] != 1){
                                        if(AQI[i] !=0){
                                            safe = false;
                                            break;
                                        }
                                    }
                                }
                                if(safe)
                                    Notice("",0);
                                for(int i=0; i<6; ++i){ //記得存檔回去
                                    sp.edit().putInt("aqi"+String.valueOf(i), AQI[i]).apply();
                                }

                            }
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

    private void Notice(String message, int chid){ // 警告通知
        NotificationCompat.Builder notificationBuilder;
        if(chid == 1){
            String ch = "ch1"; //頻道
            notificationBuilder = new NotificationCompat.Builder(this, ch)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.fa)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.fa))
                    .setContentTitle("提醒您!!!")
                    .setContentText("此地區" + message + "濃度變高了");


        }
        else{
            String ch = "ch2"; //頻道
            notificationBuilder = new NotificationCompat.Builder(this, ch)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.fa)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.fa))
                    .setContentTitle("恭喜您!!!")
                    .setContentText("此地區現在空氣非常優質");
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
