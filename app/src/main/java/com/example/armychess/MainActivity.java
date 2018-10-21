package com.example.armychess;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity{
    private Button CreateButton;
    private Button JoinButton;
    private Button CreateBuju;
    private String TAG = "主页";
    List<String> base = new ArrayList<>();

    BluetoothSPP bt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = new BluetoothSPP(this);
        /***
         *对本地数据库进行初始化。
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null)
        {
            actionBar.hide();
        }
        LitePal.getDatabase();
        List<distribution> kong = DataSupport.findAll(distribution.class);
        if (kong.size() == 0) {
            distribution disk = new distribution();
            createdatabses();
            disk.setStore(base);
            disk.setName("默认布局");
            disk.save();
        }
        /*
         *
         * 开始实例化Button
         * */
        CreateButton = (Button) findViewById(R.id.Joingame);
        JoinButton = (Button) findViewById(R.id.Creategame);
        CreateBuju=(Button) findViewById(R.id.Createbuju);
        JoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), basedPanel.class);
                intent.putExtra("address" ,"miss");
                startActivity(intent);
            }
        });
        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });
        CreateBuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,custom_position.class);
                startActivity(intent);
            }
        });


        /**
         * 监听蓝牙状态
         *
         * */
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();

                //可能需要加一个判断条件。
                Intent intent = new Intent(MainActivity.this, basedPanel.class);
                startActivity(intent);
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void createdatabses() {
        base.add("8,0,3");
        base.add("8,1,9");
        base.add("8,2,4");
        base.add("8,3,2");
        base.add("8,4,8");

        base.add("9,0,7");
        base.add("9,2,10");
        base.add("9,4,2");

        base.add("10,0,1");
        base.add("10,1,2");
        base.add("10,3,4");
        base.add("10,4,3");

        base.add("11,0,5");
        base.add("11,2,1");
        base.add("11,4,6");

        base.add("12,0,7");
        base.add("12,1,1");
        base.add("12,2,11");
        base.add("12,3,6");
        base.add("12,4,5");

        base.add("13,0,11");
        base.add("13,1,12");
        base.add("13,2,11");
        base.add("13,3,10");
        base.add("13,4,3");
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            // 申请打开蓝牙
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            // 打开蓝牙之后做的事
            if (!bt.isServiceAvailable()) {
                setup();
            }

        }

        //申请定位权限
        if (Build.VERSION.SDK_INT >= 23) {
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
//向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(MainActivity.this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setup() {
        bt.setupService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bt.startService(BluetoothState.DEVICE_ANDROID);
    }


    @Override
    protected void onPause() {
        super.onPause();
        bt.disconnect();
        bt.stopService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case BluetoothState.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setup();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "拒绝授权", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case BluetoothState.REQUEST_CONNECT_DEVICE:
                if(resultCode == Activity.RESULT_OK)
                {
                    String address = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
                    Log.d(TAG, "onActivityResult: "+address);
                    Intent intent=new Intent(MainActivity.this,basedPanel.class);
                    intent.putExtra("address",address);
                    startActivity(intent);
                    //测试，为了检测是否能传送intent值
                  //  bt.connect(data);
                }
                break;
        }
    }
    long lastBackPressed = 0;
    @Override
    public void onBackPressed() {
        //当前时间
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressed < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
        lastBackPressed = currentTime;
    }
}
