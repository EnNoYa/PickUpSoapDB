package com.example.user.ast;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ai extends AppCompatActivity {
    private RequestQueue mRQ;
    private TextView Tgas[] = new TextView[6];// SO2 CO O3 PM10 PM2.5 NO2
    private String sarr[] = new String[79]; //紀錄觀測站順序
    private String idname;//當前觀測站名稱
    private int id = 0;//index 觀測站
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ai);
    Tgas[0] = (TextView)findViewById(R.id.aiiso2);
    Tgas[1] = (TextView)findViewById(R.id.aiico);
    Tgas[2] = (TextView)findViewById(R.id.aiio3);
    Tgas[3] = (TextView)findViewById(R.id.aiipm10);
    Tgas[4] = (TextView)findViewById(R.id.aiipm2_5);
    Tgas[5] = (TextView)findViewById(R.id.aiino2);
    mRQ = Volley.newRequestQueue(this);

    /*存 資料*/
    SharedPreferences saveid = getApplication().getSharedPreferences("ssssid",Context.MODE_PRIVATE);
    idname = saveid.getString("idsave","");

    jsonParse();
  }
  private void jsonParse(){
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
                                id=i;
                                break;
                            }
                        }
                        JSONObject tmp = response.getJSONObject(id); //json物件
                        if(!tmp.isNull("SO2Ans")){
                            String SO2  = tmp.getString("SO2Ans");;
                            Tgas[0].setText(SO2);
                        }
                        else
                            Tgas[0].setText("不知道");

                        if(!tmp.isNull("COAns")){
                            String CO  = tmp.getString("COAns");;
                            Tgas[1].setText(CO);
                        }
                        else
                            Tgas[1].setText("不知道");

                        if(!tmp.isNull("PM10Ans")){
                            String PM10  = tmp.getString("PM10Ans");
                            Tgas[3].setText(PM10);
                        }
                        else
                            Tgas[3].setText("不知道");

                        if(!tmp.isNull("PM25Ans")){
                            String PM25  = tmp.getString("PM25Ans");;
                            Tgas[4].setText(PM25);
                        }
                        else
                            Tgas[4].setText("不知道");

                        if(!tmp.isNull("NO2Ans")){
                            String NO2  = tmp.getString("NO2Ans");;
                            Tgas[5].setText(NO2);
                        }
                        else
                            Tgas[5].setText("不知道");

                        if(!tmp.isNull("O3ans")){
                        String O3  = tmp.getString("O3Ans");;
                            Tgas[2].setText(O3);
                        }
                        else
                            Tgas[2].setText("不知道");

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
