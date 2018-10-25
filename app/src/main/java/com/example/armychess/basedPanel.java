package com.example.armychess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
/*
* 这个活动是view的依托，view是建立在这个类上面的
*
* */
public class basedPanel extends AppCompatActivity {
    private static final String TAG="basedPanel";
    private ListView listView;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private String receive="miss";
    private int Ch=-1;
    private int huiqicishu=3;
    private Button regret;
    private Button touxiang;
    private ChessPanel chessPanel;
    List<String > whatchoice=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        List <distribution> distri=DataSupport.findAll(distribution.class);
        for (distribution dis:distri)
        {
             whatchoice.add(dis.getName());
        }
        showDialog();
        Intent intent=getIntent();
        receive=intent.getStringExtra("address");
        Log.d(TAG, "onCreate: "+receive);

        setContentView(R.layout.activity_based_panel);
        touxiang=findViewById(R.id.touxiang);
        regret=findViewById(R.id.huiqi);
        chessPanel=findViewById(R.id.drawing);
        chessPanel.setButton(regret,touxiang);
    }
    long lastBackPressed = 0;
    @Override
    public void onBackPressed() {
        //当前时间
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressed < 2000) {
            {
                super.onBackPressed();
                chessPanel.taopao();
                chessPanel.divorce();
            }
        } else {
            Toast.makeText(this, "再按一次返回界面", Toast.LENGTH_SHORT).show();
        }
        lastBackPressed = currentTime;
    }
    public void back()
    {
        finish();
    }

    public void showDialog()
    {
        Context context=basedPanel.this;
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(basedPanel.this,android.R.layout.simple_list_item_1,whatchoice);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout=inflater.inflate(R.layout.choicelistview,null);
        listView= (ListView) layout.findViewById(R.id.choice);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //  Toast.makeText(getApplicationContext(),"你点击了"+position,Toast.LENGTH_SHORT).show();
                if (alertDialog != null) {
                    Ch=position;
                    layout.setVisibility(view.GONE);
                    alertDialog.dismiss();
                }
            }
        });
        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
    }
    /*
    * 这个方法是为了给view传送蓝牙连接的信息
    * */
    public String initBusiness(String a)
    {
        a=this.receive;
        return a;
    }
    public int choice(int a)
    {
        a=this.Ch;
        return a;
    }
    public void change()
    {
            huiqicishu--;
            regret.setText("悔棋（"+huiqicishu+"）");

    }
}
