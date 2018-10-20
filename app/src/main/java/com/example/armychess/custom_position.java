package com.example.armychess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class custom_position extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_position);
        customview custom= findViewById(R.id.custom);
        Button save= findViewById(R.id.savebuju);
        custom.setButton(save);

    }
}
