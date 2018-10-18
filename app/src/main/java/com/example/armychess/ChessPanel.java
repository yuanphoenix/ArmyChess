package com.example.armychess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class ChessPanel extends View  {
    private String TAG="棋子";
    private boolean Who=true;//判断谁先手，谁创建对局谁先手
    private boolean IsFirst=true;//判断落子的顺序，点击棋子，再次点击要落的区域
    private boolean isFirstImport=true;//这里是指第一次连接后，要将布局发送给对方。
    private static boolean you=false;
    private String address="a";
    private BluetoothSPP st;
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
    private List<chess> enemy = new ArrayList<>();
    private List<chess> xingying=new ArrayList<>();
    private int indexofchess=0;//这是为了统计到底是修改敌人棋子，还是增添敌人棋子。以25为界限
    basedPanel mbasedPanel;
    Map map=new HashMap();
    /*
    * 构造函数，实现了蓝牙功能，包括蓝牙的接收。
    * */
    public ChessPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        //对画笔进行初始化
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mbasedPanel=(basedPanel) context;
        mPaint.setStyle( Paint.Style.STROKE);

        st=new BluetoothSPP(getContext());
        st.setupService();
        st.startService(BluetoothState.DEVICE_ANDROID);
        address=mbasedPanel.initBusiness(address);
        Log.d(TAG, "talkService: "+address);
        if (address!=null&&address.length()>6)
        {
            st.connect(address);
            Who=!Who;//此时创建者变为false
        }
        //蓝牙接受数据
        st.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                if (indexofchess < 25) {
                    String a, b, c;
                    int fir = 0;
                    int second = 0;
                    for (int k = 0; k < message.length(); k++) {
                        if (message.charAt(k) == ',') {
                            if (fir == 0) {
                                fir = k;
                            } else {
                                second = k;
                            }
                        }
                    }
                    a = message.substring(0, fir);
                    b = message.substring(fir + 1, second);
                    c = message.substring(second + 1, message.length());
                    int pointx, pointy, wei = 0;
                    pointx = Integer.parseInt(a);
                    pointy = Integer.parseInt(b);
                    wei = Integer.parseInt(c);
                    enemy.add(new chess(13 - pointx, 4 - pointy, -1 * wei));
                    Who = !Who;
                    invalidate();
                    indexofchess++;
                }
                //结束后，创建者变为true；
                else
                {
                    String a, b, c,d;
                    int fir = 0;
                    int second = 0;
                    int third=0;
                    for (int k = 0; k < message.length(); k++) {
                        if (message.charAt(k) == ',') {
                            if (fir == 0) {
                                fir = k;
                            } else if(second==0){
                                second = k;
                            }else
                            {
                                third=k;
                            }
                        }
                    }
                    a = message.substring(0, fir);
                    b = message.substring(fir + 1, second);
                    c = message.substring(second + 1, third);
                    d=message.substring(third+1,message.length());
                    int pointx, pointy, wei ,delete = 0;
                    pointx = Integer.parseInt(b);
                    pointy = Integer.parseInt(c);
                    wei = Integer.parseInt(d);
                    delete=Integer.parseInt(a);
                    enemy.remove(delete);
                    enemy.add(new chess(13 - pointx, 4 - pointy, -1 * wei));
                    if (mine.contains(new chess(13 - pointx, 4 - pointy )))
                    {
                        mine.remove(mine.indexOf(new chess(13 - pointx, 4 - pointy ) ));
                    }
                    Who = !Who;
                    invalidate();
                }
            }
        });


        //当蓝牙连接好之后，将本方的布阵发送给对方
        st.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                if (isFirstImport)
                {
                    loadchess();
                    invalidate();
                    isFirstImport=false;
                }
            }

            @Override
            public void onDeviceDisconnected() {

            }

            @Override
            public void onDeviceConnectionFailed() {

            }
        });

        init();
    }
    //初始化权重对应的图片
    private void init() {
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

        map.put(-1,R.drawable.e_gongbing);
        map.put(-2,R.drawable.e_paizhang);
        map.put(-3,R.drawable.e_lianzhang);
        map.put(-4,R.drawable.e_yingzhang);
        map.put(-5,R.drawable.e_tuanzhang);
        map.put(-6,R.drawable.e_lvzhang);
        map.put(-7,R.drawable.e_shizhang);
        map.put(-8,R.drawable.e_junzhang);
        map.put(-9,R.drawable.e_siling);
        map.put(-10,R.drawable.e_bomb);
        map.put(-11,R.drawable.e_mine);
        map.put(-12,R.drawable.e_flag);

        xingying.add(new chess(2,1));
        xingying.add(new chess(2,3));
        xingying.add(new chess(3,2));
        xingying.add(new chess(4,1));
        xingying.add(new chess(4,3));
        xingying.add(new chess(9,1));
        xingying.add(new chess(9,3));
        xingying.add(new chess(10,2));
        xingying.add(new chess(11,1));
        xingying.add(new chess(11,3));
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
            st.send(gui,true);
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

        for (int i=0;i<enemy.size();i++)
        {

            chess p=enemy.get(i);
            canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),(int)map.get(p.getWeight())),pieceHeight,pieceWidth,false),
                    (p.y+(1-ratioPieceOfLineHeight)/2)*mLineWidth,
                    (p.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null
            );
        }
    }
    /*
    * 绘制棋盘
    *
    *
    * */
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
    }
    @Override
    /*
    * 这里要求写重绘事件，并且在这里判断是否可以吃掉对方的棋子。
    * 要注意第一次点击的不是本方棋子的情况。
    * */
    public boolean onTouchEvent(MotionEvent event) {
        if (!Who)
        {
            return false;
        }
        int action=event.getAction();
        if (action==MotionEvent.ACTION_UP)
        {
            int x=(int)event.getX();
            int y = (int) event.getY();
            if (IsFirst)
            {
                FirstChess=getValidPoint(x,y);
                if (mine.contains(FirstChess))
                {
                    if (mine.get(mine.indexOf(FirstChess)).getWeight()<11)
                    {IsFirst=!IsFirst;}
                    else
                    {
                        Toast.makeText(getContext(),"地雷军旗不可移动" ,Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                   Toast.makeText(getContext(),"这里没有棋子",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            chess SecondPosition=getValidPoint(x,y);
            if (SecondPosition.getX()==6||SecondPosition.getX()==7)
            {
                Toast.makeText(getContext(),"禁止行军",Toast.LENGTH_SHORT).show();
                return false;
            }
            if (mine.contains(SecondPosition))
            {
                //换成本方的另一个棋子
               FirstChess=SecondPosition;
               return true;
            }
            else if (enemy.contains(SecondPosition))//那个位置有敌人的棋子
            {
                if(JudgeRuler(FirstChess,SecondPosition))
                {
                    //既然可以到达，那么比较权重。
                    //行营的棋子不可以吃
                    if (xingying.contains(SecondPosition))
                    {
                        Toast.makeText(getContext(),"行营的棋子不可以吃",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    int index=mine.indexOf(FirstChess);
                    int weight = mine.get(index).getWeight();
                    int indexOfenemy=enemy.indexOf(SecondPosition);
                    int enemyWeight=enemy.get(indexOfenemy).getWeight();
                    if (weight>Math.abs(enemyWeight))
                    {
                        enemy.remove(indexOfenemy);
                        IsFirst=!IsFirst;
                        mine.remove(index);
                        mine.add(new chess(SecondPosition.getX(),SecondPosition.getY(),weight));
                        String mess=""+index+","+SecondPosition.getX()+","+SecondPosition.getY()+","+weight;
                        st.send(mess,true);
                        Who=!Who;
                    }else
                    {
                        Toast.makeText(getContext(),"你打不过他",Toast.LENGTH_SHORT).show();
                        //可能需要return
                    }
                }
            }
            else//那里没有棋子
            {
               if (JudgeRuler(FirstChess,SecondPosition))
               {
                   IsFirst=!IsFirst;
                   int index=mine.indexOf(FirstChess);
                   Log.d(TAG, "onTouchEvent: "+index);
                   int  weight=mine.get(index).getWeight();
                   mine.remove(index);
                   mine.add(new chess(SecondPosition.getX(),SecondPosition.getY(),weight));
                   String mess=""+index+","+SecondPosition.getX()+","+SecondPosition.getY()+","+weight;
                   st.send(mess,true);
                   Who=!Who;
               }
               else
               {
                   Toast.makeText(getContext(),"走不到那里",Toast.LENGTH_SHORT).show();
               }
            }
        }
        invalidate();
        return true;
    }
    //迷宫算法
    private static boolean solved(int [][]a,int begin,int end,int targetX,int targetY)
    {
        if (you)
        {
            return true;
        }
        if (begin==targetX&&end==targetY)

        {
            you=true;
            return true;
        }
        a[begin][end]=2;
        if (a[begin+1][end]==0)
        {
            solved(a,begin+1,end,targetX,targetY);
        }

        if (a[begin][end+1]==0)
        {
            solved(a,begin,end+1,targetX,targetY);
        }
        if (a[begin ][end-1]==0)
        {
            solved(a,begin,end-1,targetX,targetY);
        }
        if (a[begin-1][end]==0)
        {
            solved(a,begin-1,end,targetX,targetY);
        }
        return false;
    }
    private boolean JudgeRuler(chess begin,chess end)
    {
        //工兵算法
        if (mine.get(mine.indexOf(begin)).getWeight()==1)
        {
            if ( (begin.getX()>0&&begin.getX()<13&&(begin.getY()==0||begin.getY()==4) )||begin.getX()==5||begin.getX()==8 )
            {
                if ( (end.getX()>0&&end.getX()<13&&(end.getY()==0||end.getY()==4) )||end.getX()==5||end.getX()==8 )
                {
                    if (mine.get(mine.indexOf(begin)).getWeight()==1)
                    {
                        int [][]road={
                                {1,1,1,1,1,1,1},
                                {1,0,0,0,0,0,1},
                                {1,0,1,1,1,0,1},
                                {1,0,1,1,1,0,1},
                                {1,0,1,1,1,0,1},
                                {1,0,0,0,0,0,1},
                                {1,0,1,0,1,0,1},
                                {1,0,1,0,1,0,1},
                                {1,0,0,0,0,0,1},
                                {1,0,1,1,1,0,1},
                                {1,0,1,1,1,0,1},
                                {1,0,1,1,1,0,1},
                                {1,0,0,0,0,0,1},
                                {1,1,1,1,1,1,1},
                        };
                        for (int i=0;i<mine.size();i++)
                        {
                            int x=mine.get(i).getX();
                            int y=mine.get(i).getY()+1;
                            road[x][y]=2;
                        }
                        for (int i=0;i<enemy.size();i++)
                        {
                            int x=enemy.get(i).getX();
                            int y=enemy.get(i).getY()+1;
                            road[x][y]=2;
                        }
                        you=false;

                        solved(road,begin.getX(),begin.getY()+1,end.getX(),end.getY()+1);
                        if(you){
                            return true;
                        }else
                            return false;
                    }
                }

            }
        }

        //考虑普通路线，如果相差的只有一个格子，那么就可以到达。
        if (Math.abs(begin.getY()-end.getY()+begin.getX()-end.getX())==1)
        {
            return true;
        }
        else if ((Math.abs(begin.getY()-end.getY()+begin.getX()-end.getX())==2&&Math.abs(begin.getX()-end.getX())==1)||((Math.abs(begin.getY()-end.getY()+begin.getX()-end.getX())==0&&Math.abs(begin.getX()-end.getX())==1)))//斜着进行营的情况
        {
            if (xingying.contains(new chess(end.getX(),end.getY()))||xingying.contains(new chess( begin.getX(),begin.getY())))
            {
                return true;
            }
        }
        //普通的铁路算法--最左和最右两边
        if (begin.getY()==end.getY()&&(begin.getY()==0||begin.getY()==4)&&begin.getX()>0&&begin.getX()<13&&end.getX()>0&&end.getX()<13)
        {
            int distance=Math.abs(begin.getX()-end.getX());
            int min=Math.min(begin.getX(),end.getX());
            int stableY=begin.getY();
            for (int i=1;i<distance;i++)
            {
                if (mine.contains(new chess(min+i,stableY))||enemy.contains(new chess(min+i,stableY)))
                {
                    return false;
                }
            }
            return true;
        }
        //普通的铁路算法--4个横铁路
        if (begin.getX()==end.getX()&&(begin.getX()==1||begin.getX()==5||begin.getX()==8||begin.getX()==12))
        {
            int distance=Math.abs(begin.getY()-end.getY());
            int min=Math.min(begin.getY(),end.getY());
            int stableX=begin.getX();
            for (int i=1;i<distance;i++)
            {
                if (mine.contains(new chess(stableX,min+i))||enemy.contains(new chess(stableX,min+i)))
                {
                    return false;
                }
            }
            return true;
        }
        //一条中间的竖铁路
        if (begin.getY()==end.getY()&&begin.getY()==2&&begin.getX()>4&&begin.getX()<9&&end.getX()>4&&end.getY()<9)
        {
            return true;
        }
        return false;
    }
    private chess getValidPoint(int x, int y) {
        return new chess((int)(y/mLineHeight),(int)(x/mLineWidth));
    }
}
