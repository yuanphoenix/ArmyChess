package com.example.armychess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class basedPanel extends AppCompatActivity {
    private static final String TAG="basedPanel";
    private String receive="miss";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        receive=intent.getStringExtra("address");
        Log.d(TAG, "onCreate: "+receive);
        setContentView(R.layout.activity_based_panel);
    }
    public String initBusiness(String a)
    {
        a=this.receive;
        return a;
    }
}
