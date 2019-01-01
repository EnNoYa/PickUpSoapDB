package com.example.user.ast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/*抓狀態之service*/
public class MyService extends Service {

    private RequestQueue mRQ;
    private String idname;//當前觀測站名稱
    private int id = -1;//index 觀測站
    SharedPreferences HealthRecord;// 存檔用
    SharedPreferences.Editor editor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        mRQ = Volley.newRequestQueue(this);
        /*拿資料*/
        SharedPreferences saveid = getApplication().getSharedPreferences("ssssid",Context.MODE_PRIVATE);
        idname = saveid.getString("idsave","");
        HealthRecord = getApplication().getSharedPreferences("healthresult", Context.MODE_PRIVATE);
        editor = HealthRecord.edit();
        Log.d("shit","開始");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid){ //內容、額外資訊、啟動編號
        Log.d("shit","執行指令");
        /*AQI指標*/
        jsonParse();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("shit","結束");
    }


    public void jsonParse(){
        String url = "http://140.136.149.239:9487/recentAQI";
        JsonArrayRequest request  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i<response.length(); ++i) {
                                JSONObject tmp = response.getJSONObject(i); //json物件
                                if(tmp.getString("SiteName").equals(idname)){//觀測站名稱
                                    id =i;
                                    break;
                                }
                            }
                            if(id==-1){//沒有這個觀測站
                                editor.putInt("acp2", 0).commit();
                                Log.d("shit","數值維修");
                            }
                            else {
                                List<Double> aqilist = new ArrayList<>(); //暫存aqi數值
                                JSONObject tmp = response.getJSONObject(id); //json物件
                                if (!tmp.isNull("SO2Ans")) {
                                    String SO2 = tmp.getString("SO2Ans");
                                    int val = Integer.valueOf(SO2);
                                    aqilist.add(Double.valueOf(val));
                                }

                                if (!tmp.isNull("COAns")) {
                                    String CO = tmp.getString("COAns");
                                    int val = Integer.valueOf(CO);
                                    aqilist.add(Double.valueOf(val));
                                }

                                if (!tmp.isNull("PM10Ans")) {
                                    String PM10 = tmp.getString("PM10Ans");
                                    int val = Integer.valueOf(PM10);
                                    aqilist.add(Double.valueOf(val));
                                }

                                if (!tmp.isNull("PM25Ans")) {
                                    String PM25 = tmp.getString("PM25Ans");
                                    int val = Integer.valueOf(PM25);
                                    aqilist.add(Double.valueOf(val));
                                }

                                if (!tmp.isNull("NO2Ans")) {
                                    String NO2 = tmp.getString("NO2Ans");
                                    int val = Integer.valueOf(NO2);
                                    aqilist.add(Double.valueOf(val));
                                }

                                if (!tmp.isNull("O3Ans")) {
                                    String O3 = tmp.getString("O3Ans");
                                    int val = Integer.valueOf(O3);
                                    aqilist.add(Double.valueOf(val));
                                }
                                Collections.sort(aqilist); //排序
                                Collections.reverse(aqilist); //由大到小
                                /*acp等級*/
                                if(HealthRecord.getInt("acp2",-1) != -1)//不是空的就刪除
                                    editor.remove("acp2").commit();
                                if(aqilist.size()>1){
                                    editor.putInt("acp2", (int)Math.ceil((aqilist.get(0)+aqilist.get(1))/2)).commit();
                                    Log.d("shit","數值"+String.valueOf(Math.ceil((aqilist.get(0)+aqilist.get(1))/2)));
                                }
                                else if(aqilist.size() == 1){
                                    editor.putInt("acp2", aqilist.get(0).intValue()).commit();
                                    Log.d("shit","數值"+String.valueOf(aqilist.get(0).intValue()));
                                }
                                else{
                                    editor.putInt("acp2", 0).commit();
                                    Log.d("shit","數值維修");
                                }
                            }
                            sendBroadcast(new Intent("com.example.user.ast.task"));
                            Log.d("shit","完成+廣播");
                            stopSelf(); //停止自己
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
}