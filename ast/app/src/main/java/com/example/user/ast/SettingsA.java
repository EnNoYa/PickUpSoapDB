package com.example.user.ast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsA extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intent = new Intent();

        final Bundle bdn = new Bundle();
        Button bns = (Button)findViewById(R.id.bns);
        bns.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(SettingsA.this, ns.class);
                bdn.putInt("btnid", R.id.bns);
                intent.putExtras(bdn);
                startActivity(intent);
            }
        });

        final Bundle bdm = new Bundle();
        Button bnm = (Button)findViewById(R.id.bmrs);
        bnm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(SettingsA.this, mrsa.class);
                bdm.putInt("btnid", R.id.bmrs);
                intent.putExtras(bdm);
                startActivity(intent);
            }
        });
    }
}