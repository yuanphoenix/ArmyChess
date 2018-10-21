package com.example.armychess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
/*
*
* 自定义布局的view
* */
public class customview extends View {
    private String TAG="自定义布局";
    private boolean isMove=false;
    private chess FirstChess;//记录第一次点击的位置
    private int mPanelWidth;//整个棋盘的宽度
    private float mLineHeight;//每一行的高度
    private float mLineWidth;
    private int mPanelLength;
    private int MAX_LINE=14;
    private float ratioPieceOfLineHeight=3*1.0f/4;
    float ratioOfCilcle=1*1.0f/2;//为了画圆圈而定制的比例
    private Paint mPaint = new Paint();
    private List<chess> mine = new ArrayList<>();
    private chess SecondPosition;
    private chess moveChess;

    private Button save;
    MyButtonListener myButtonListener;
    Map map=new HashMap();
    public customview(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        //对画笔进行初始化
        mPaint.setColor(0x88000000);
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle( Paint.Style.STROKE);
        init();
        loadchess();
    }

    private void init() {
        myButtonListener = new MyButtonListener();
        map.put(1,R.drawable.d_gongbing);
        map.put(2,R.drawable.d_paizhang);
        map.put(3,R.drawable.d_lianzhang);
        map.put(4,R.drawable.d_yingzhang);
        map.put(5,R.drawable.d_tuanzhang);
        map.put(6,R.drawable.d_lvzhang);
        map.put(7,R.drawable.d_shizhang);
        map.put(8,R.drawable.d_junzhang);
        map.put(9,R.drawable.d_siling);
        map.put(10,R.drawable.d_bomb);
        map.put(11,R.drawable.d_mine);
        map.put(12,R.drawable.d_flag);
    }
    private void loadchess()//该方法实现了对本方布阵棋子的加载。
    {
        mine.clear();
        List<distribution> buju=DataSupport.findAll(distribution.class);
        List<String> temp=new ArrayList<>();
        int j=0;
        for (distribution db :buju)
        {
            if (j==0)
                temp=db.getStore();
        }
        for (int i=0;i<temp.size();i++)
        {
            String gui=temp.get(i);//用于从temp中取出一个数据
            String a,b,c;
            int fir=0;int second=0;
            for (int k=0;k<gui.length();k++)
            {
                if (gui.charAt(k)==',')
                {
                    if (fir==0)
                    {
                        fir=k;
                    }else
                    {
                        second=k;
                    }
                }
            }
            a=gui.substring(0,fir);
            b=gui.substring(fir+1,second);
            c=gui.substring(second+1,gui.length());
            int pointx,pointy,wei=0;
            pointx=Integer.parseInt(a);
            pointy=Integer.parseInt(b);
            wei=Integer.parseInt(c);
            mine.add(new chess(pointx,pointy,wei));
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mPanelLength=h;
        mLineHeight=mPanelLength*1.0f/MAX_LINE;
        mLineWidth=(mPanelWidth-2*mLineHeight)/4;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        if (isMove)
            drawMove(canvas);
    }
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineWidth= mLineWidth;
        float lineHeight = mLineHeight;
        for (int i=0;i<MAX_LINE;i++)//开始画横线
        {
            if (i==6||i==7)
            {
                continue;
            }
            int StartX=(int) lineHeight;
            //  int StartY=(int) (0.5*lineHeight+0*lineHeight);//奇怪的事情，以后尽量用小数，不要用分数了.
            int StartY=(int) ((0.5+i)*lineHeight);
            int EndX=(int ) (w-lineHeight);
            if (i==3||i==10)
            {
                canvas.drawLine(StartX,StartY,StartX+2*lineWidth-ratioOfCilcle*lineHeight,StartY,mPaint);
                canvas.drawCircle(StartX+2*lineWidth,StartY,ratioOfCilcle*lineHeight,mPaint);
                canvas.drawLine(StartX+2*lineWidth+ratioOfCilcle*lineHeight,StartY,EndX,StartY,mPaint);
                continue;
            }
            if (i==2||i==4||i==9||i==11)
            {
                canvas.drawLine(StartX,StartY,StartX+lineWidth-ratioOfCilcle*lineHeight,StartY,mPaint);
                canvas.drawCircle(StartX+lineWidth,StartY,ratioOfCilcle*lineHeight,mPaint);
                canvas.drawLine(StartX+lineWidth+ratioOfCilcle*lineHeight,StartY,StartX+3*lineWidth-ratioOfCilcle*lineHeight,StartY,mPaint);
                canvas.drawCircle(StartX+3*lineWidth,StartY,ratioOfCilcle*lineHeight,mPaint);
                canvas.drawLine(w-lineHeight-lineWidth+ratioOfCilcle*lineHeight,StartY,w-lineHeight,StartY,mPaint);
                continue;
            }
            canvas.drawLine(StartX,StartY,EndX,StartY,mPaint);
        }

        for (int i=0;i<5;i++)//为了画竖线
        {
            if (i==1||i==3)
            {
                int StartX=(int) (lineHeight+i*lineWidth);
                int StartY=(int) (0.5*lineHeight);
                int EndY=(int) (2*lineHeight);
                canvas.drawLine(StartX,StartY,StartX,EndY,mPaint);
                canvas.drawLine(StartX,EndY+lineHeight,StartX,EndY+2*lineHeight,mPaint);
                canvas.drawLine(StartX,5*lineHeight,StartX,(float)5.5*lineHeight,mPaint);

                canvas.drawLine(StartX,(float)8.5*lineHeight,StartX,9*lineHeight,mPaint);
                canvas.drawLine(StartX,(float)10*lineHeight,StartX,11*lineHeight,mPaint);
                canvas.drawLine(StartX,(float)12*lineHeight,StartX,(float) 13.5*lineHeight,mPaint);
                continue;
            }
            if (i==0||i==4)
            {
                int StartX=(int) (lineHeight+i*lineWidth);
                int StartY=(int)(0.5*lineHeight);
                int EndY=(int) (13.5*lineHeight);
                canvas.drawLine(StartX,StartY,StartX,EndY,mPaint);
                continue;
            }
            int StartX=(int) (lineHeight+i*lineWidth);
            int StartY=(int) (0.5*lineHeight);
            int Endy=(int) (3*lineHeight);
            canvas.drawLine(StartX,StartY,StartX,Endy,mPaint);
            canvas.drawLine(StartX,4*lineHeight,StartX,(float) 5.5*lineHeight,mPaint);
            canvas.drawLine(StartX,(float)8.5*lineHeight,StartX,10*lineHeight,mPaint);
            canvas.drawLine(StartX,11*lineHeight,StartX,(float) 13.5*lineHeight,mPaint);
        }

        //开始画斜线
        lean((int )( lineHeight+2*lineWidth), (int) (3.5*lineHeight),canvas);
        lean((int )( lineHeight+2*lineWidth),(int) (10.5*lineHeight),canvas);
    }
    void lean(int X,int Y,Canvas canvas)
    {
        float lineWidth= mLineWidth;
        float lineHeight = mLineHeight;
        float xiebian= (float) Math.sqrt(lineHeight*lineHeight+lineWidth*lineWidth);
        float sin=lineHeight/xiebian;
        float cos=lineWidth/xiebian;
        int StartX=(int) (X-ratioOfCilcle*lineHeight*cos);
        int StartY=(int) (Y-ratioOfCilcle*lineHeight*sin);
        int EndX=(int) (X-lineWidth+ratioOfCilcle*lineHeight*cos);
        int EndY=(int)(StartY-lineHeight+lineHeight*sin);
        //第二象限
        canvas.drawLine(StartX,StartY,EndX,EndY,mPaint);
        canvas.drawLine(X-2*lineWidth,Y-2*lineHeight,X-lineWidth-ratioOfCilcle*lineHeight*cos,Y-lineHeight-ratioOfCilcle*lineHeight*sin,mPaint);
        //第一象限
        canvas.drawLine((int) (X+ratioOfCilcle*lineHeight*cos),StartY,(int) (X+lineWidth-ratioOfCilcle*lineHeight*cos),EndY,mPaint);
        canvas.drawLine(X+2*lineWidth,Y-2*lineHeight,X+lineWidth+ratioOfCilcle*lineHeight*cos,Y-lineHeight-sin*ratioOfCilcle*lineHeight,mPaint);
        //第三象限
        canvas.drawLine(StartX,(int) (Y+ratioOfCilcle*lineHeight*sin),EndX,Y+lineHeight-ratioOfCilcle*lineHeight*sin,mPaint);
        canvas.drawLine(X-lineWidth*2,Y+2*lineHeight,X-lineWidth-ratioOfCilcle*lineHeight*cos,Y+lineHeight+ratioOfCilcle*lineHeight*sin,mPaint);
        //第四象限
        canvas.drawLine((int) (X+ratioOfCilcle*lineHeight*cos),Y+ratioOfCilcle*lineHeight*sin, X+lineWidth-ratioOfCilcle*lineHeight*cos,Y+lineHeight-ratioOfCilcle*lineHeight*sin,mPaint);
        canvas.drawLine(X+2*lineWidth,Y+2*lineHeight,X+lineWidth+ratioOfCilcle*lineHeight*cos,Y+lineHeight+lineHeight*ratioOfCilcle*sin,mPaint);

        //斜线第二象限
        canvas.drawLine(X,Y-2*lineHeight,X-lineWidth+ratioOfCilcle*lineHeight*cos,Y-lineHeight-lineHeight*ratioOfCilcle*sin,mPaint);
        canvas.drawLine(X-2*lineWidth,Y,X-lineWidth-ratioOfCilcle*lineHeight*cos,Y-lineHeight+ratioOfCilcle*lineHeight*sin,mPaint);
        //斜线第一象限
        canvas.drawLine(X,Y-2*lineHeight,X+lineWidth-lineHeight*ratioOfCilcle*cos,Y-lineHeight-lineHeight*ratioOfCilcle*sin,mPaint);
        canvas.drawLine(X+2*lineWidth,Y,X+lineWidth+ratioOfCilcle*lineHeight*cos,Y-lineHeight+ratioOfCilcle*lineHeight*sin,mPaint);
        //斜线第三象限
        canvas.drawLine(X-2*lineWidth,Y,X-lineWidth-lineHeight*ratioOfCilcle*cos,Y+lineHeight-ratioOfCilcle*lineHeight*sin,mPaint);
        canvas.drawLine(X,Y+2*lineHeight,X-lineWidth+ratioOfCilcle*lineHeight*cos,Y+lineHeight+ratioOfCilcle*lineHeight*sin,mPaint);
        //斜线第四象限
        canvas.drawLine(X+2*lineWidth,Y,X+lineWidth+ratioOfCilcle*lineHeight*cos,Y+lineHeight-ratioOfCilcle*lineHeight*sin,mPaint);
        canvas.drawLine(X,Y+2*lineHeight,X+lineWidth-ratioOfCilcle*lineHeight*cos,Y+lineHeight+lineHeight*ratioOfCilcle*sin,mPaint);
    }
    private void drawPieces(Canvas canvas) {
        int pieceWidth= (int) (mLineHeight*ratioPieceOfLineHeight);
        int pieceHeight= (int )(mLineWidth*ratioPieceOfLineHeight);
        for (int i=0;i<mine.size();i++)
        {
            chess p=mine.get(i);
            canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),(int)map.get(p.getWeight())),pieceHeight,pieceWidth,false),
                    (p.y+(1-ratioPieceOfLineHeight)/2)*mLineWidth,
                    (p.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null
            );
        }
    }
    private void drawMove(Canvas canvas)
    {
        int pieceWidth = (int) (mLineHeight*ratioPieceOfLineHeight);
        int pieceHeight= (int )(mLineWidth*ratioPieceOfLineHeight);
        chess p=moveChess;
        canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),(int)map.get(p.getWeight())),pieceHeight,pieceWidth,false),
                (p.y+(1-ratioPieceOfLineHeight)/2)*mLineWidth,
                (p.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null
            );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        int x=(int)event.getX();
        int y = (int) event.getY();
        chess p=getValidPoint(x,y);

        if (action==MotionEvent.ACTION_DOWN)
        {
            if (mine.contains(p))
            {
                isMove=false;
                FirstChess=getValidPoint(x,y);
            }

        }
        if (action==MotionEvent.ACTION_MOVE)
        {
            if (mine.contains(FirstChess))
            {
                isMove=true;
                moveChess=getValidPoint(x,y);
                moveChess.setWeight(mine.get(mine.indexOf(FirstChess)).getWeight());
                invalidate();
                return true;
            }

        }

        if (action==MotionEvent.ACTION_UP)
        {
            isMove=false;
            SecondPosition=getValidPoint(x,y);
            if (mine.contains(FirstChess)&&mine.contains(SecondPosition))
            {

                int a= mine.get(mine.indexOf(FirstChess)).getWeight();
                int b=mine.get(mine.indexOf(SecondPosition)).getWeight();
                int FX=FirstChess.getX();
                int FY=FirstChess.getY();
                int SX=SecondPosition.getX();
                int SY=SecondPosition.getY();
                if ((SX==8&&a==10)||(b==10&&FX==8))
                {
                    Toast.makeText(getContext(),"炸弹不能放在第一排",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if ( (a==11&&(SX!=12&&SX!=13))||  (b==11&&(FX!=12&&FX!=13) ) )
                {
                    Toast.makeText(getContext(),"地雷只能放在后两排",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if ((a==12&&(SX!=13|| (SY!=1&&SY!=3) ) )  || (b==12&&(FX!=13|| (FY!=1&&FY!=3)))  )
                {
                    Toast.makeText(getContext(),"军棋只能放在行营",Toast.LENGTH_SHORT).show();
                    return false;
                }
                mine.get(mine.indexOf(FirstChess)).setWeight(b);
                mine.get(mine.indexOf(SecondPosition)).setWeight(a);
                invalidate();
            }

        }
        return true;
    }

    private chess getValidPoint(int x, int y) {
        return new chess((int)(y/mLineHeight),(int)(x/mLineWidth));
    }

    public void setButton(Button s)
    {
        this.save=s;
        s.setOnClickListener(myButtonListener);
    }
    class MyButtonListener implements OnClickListener
    {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.savebuju:
                    final EditText et = new EditText(getContext());
                    new AlertDialog.Builder(getContext()).setTitle("为你的阵命名吧")
                            .setIcon(android.R.drawable.sym_def_app_icon)
                            .setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //按下确定键后的事件
                                  //  Toast.makeText(getContext(), et.getText().toString(), Toast.LENGTH_LONG).show();
                                    String name=et.getText().toString();
                                    if (name.length()==0)
                                    {
                                        Toast.makeText(getContext(),"阵名不能为空",Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        distribution xijiayi=new distribution();
                                        xijiayi.setName(name);
                                        List<String > add=new ArrayList<>();
                                        for (int j=0;j<mine.size();j++)
                                        {
                                          String a= ""+ mine.get(j).getX()+","+mine.get(j).getY()+","+mine.get(j).getWeight();
                                          add.add(a);
                                        }
                                        xijiayi.setStore(add);
                                        xijiayi.save();
                                    }
                                }
                            })
                            .setNegativeButton("取消", null).show();
                    break;

                case R.id.huiqi:

            }
        }
    }
}
