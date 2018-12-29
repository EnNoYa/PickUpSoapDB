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
    private String sarr[] = new String[79]; //紀錄觀測站順序(暫存)
    private int id = -1;//index 觀測站
    private double mon = 0, son = 0; //分子分母
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

    public void calaulate(int val){
        son += val;
        mon++;
    }

    public void jsonParse(){
        Log.d("shit","測試");
        String url = "http://140.136.149.239:9487/recentAQI";
        JsonArrayRequest request  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i<response.length(); ++i) {
                                JSONObject tmp = response.getJSONObject(i); //json物件
                                sarr[i] = tmp.getString("SiteName");//觀測站名稱
                            }
                            for(int i=0; i<sarr.length; ++i){
                                if(sarr[i].equals(idname)){
                                    id =i;
                                    break;
                                }
                            }
                            if(id==-1){//沒有這個觀測站
                            }
                            else {
                                JSONObject tmp = response.getJSONObject(id); //json物件
                                if (!tmp.isNull("SO2Ans")) {
                                    String SO2 = tmp.getString("SO2Ans");
                                    int val = Integer.valueOf(SO2);
                                    calaulate(val);//計算
                                }

                                if (!tmp.isNull("COAns")) {
                                    String CO = tmp.getString("COAns");
                                    int val = Integer.valueOf(CO);
                                    calaulate(val);//計算
                                }

                                if (!tmp.isNull("PM10Ans")) {
                                    String PM10 = tmp.getString("PM10Ans");
                                    int val = Integer.valueOf(PM10);
                                    calaulate(val);//計算
                                }

                                if (!tmp.isNull("PM25Ans")) {
                                    String PM25 = tmp.getString("PM25Ans");
                                    int val = Integer.valueOf(PM25);
                                    calaulate(val);//計算
                                }

                                if (!tmp.isNull("NO2Ans")) {
                                    String NO2 = tmp.getString("NO2Ans");
                                    int val = Integer.valueOf(NO2);
                                    calaulate(val);//計算
                                }

                                if (!tmp.isNull("O3Ans")) {
                                    String O3 = tmp.getString("O3Ans");
                                    int val = Integer.valueOf(O3);
                                    calaulate(val);//計算
                                }
                                /*acp等級*/
                                if(HealthRecord.getInt("acp",-1) != -1)//不是空的就刪除
                                    editor.remove("acp").commit();
                                editor.putInt("acp", (int)Math.ceil(son/mon)).commit();
                                Log.d("shit","數值"+String.valueOf(Math.ceil(son/mon)));
                            }
                            sendBroadcast(new Intent("com.example.user.ast.task"));
                            Log.d("shit","完成+廣播");
                            stopSelf();
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