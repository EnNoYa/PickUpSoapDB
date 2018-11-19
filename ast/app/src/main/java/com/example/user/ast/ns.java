package com.example.user.ast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

public class ns extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ns);

        intent = new Intent();

        final Bundle bdi = new Bundle();
        final Button ins = (Button)findViewById(R.id.irt);
        ins.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(ns.this, insa.class);
                bdi.putInt("btnid", R.id.irt);
                intent.putExtras(bdi);
                startActivity(intent);
            }
        });

        final Bundle bdr = new Bundle();
        final Button rns = (Button) findViewById(R.id.rrt);
        rns.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.setClass(ns.this, rnsa.class);
                bdr.putInt("btnid", R.id.rrt);
                intent.putExtras(bdr);
                startActivity(intent);
            }
        });

        Spinner sp = (Spinner)findViewById(R.id.rnssp);
        ArrayAdapter<CharSequence> sprna = ArrayAdapter.createFromResource(
                ns.this, R.array.sra,
                android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(sprna);

        Switch ics = (Switch)findViewById(R.id.inss);
        final Switch icv = (Switch)findViewById(R.id.insv);
        final Switch icr = (Switch)findViewById(R.id.irts);
        ins.setEnabled(false);
        icv.setEnabled(false);
        icr.setEnabled(false);
        ins.setEnabled(false);
        ics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    icv.setEnabled(true);
                    icr.setEnabled(true);
                } else {
                    icv.setEnabled(false);
                    icr.setEnabled(false);
                    ins.setEnabled(false);
                }
            }
        });
        icr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ins.setEnabled(true);
                } else {
                    ins.setEnabled(false);
                }
            }
        });

        Switch rcs = (Switch)findViewById(R.id.rnss);
        final Switch rcv = (Switch)findViewById(R.id.rnsv);
        final Switch rcr = (Switch)findViewById(R.id.rrts);
        final Spinner rsp = (Spinner)findViewById(R.id.rnssp);
        rns.setEnabled(false);
        rcv.setEnabled(false);
        rcr.setEnabled(false);
        rns.setEnabled(false);
        rsp.setEnabled(false);
        rcs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rcv.setEnabled(true);
                    rcr.setEnabled(true);
                    rsp.setEnabled(true);
                } else{
                    rcv.setEnabled(false);
                    rcr.setEnabled(false);
                    rns.setEnabled(false);
                    rsp.setEnabled(false);
                }
            }
        });
        rcr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rns.setEnabled(true);
                } else {
                    rns.setEnabled(false);
                }
            }
        });
    }
}
