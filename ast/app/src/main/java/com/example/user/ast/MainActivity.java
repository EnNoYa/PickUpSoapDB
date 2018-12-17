package com.example.user.ast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent();

        final Bundle bds = new Bundle();
        Button bms = (Button)findViewById(R.id.bs);
        bms.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(MainActivity.this,SettingsA.class);
                bds.putInt("btnid", R.id.bs);
                intent.putExtras(bds);
                startActivity(intent);
            }
        });

        final Bundle bdgm = new Bundle();
        Button bmm = (Button)findViewById(R.id.map);
        bmm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(MainActivity.this,am.class);
                bdgm.putInt("btnid", R.id.map);
                intent.putExtras(bdgm);
                startActivity(intent);
            }
        });

        final Bundle bdlc = new Bundle();
        CardView lc = (CardView) findViewById(R.id.locatedcv);
        lc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(MainActivity.this,ai.class);
                bdlc.putInt("btnid", R.id.locatedcv);
                intent.putExtras(bdlc);
                startActivity(intent);
            }
        });

        final Bundle bdlk = new Bundle();
        CardView lk = (CardView) findViewById(R.id.lockedcv);
        lk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(MainActivity.this,ais.class);
                bdlk.putInt("btnid", R.id.lockedcv);
                intent.putExtras(bdlk);
                startActivity(intent);
            }
        });
    }
}
