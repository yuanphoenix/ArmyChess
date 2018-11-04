package com.example.armychess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        String []data={"蓝牙连接：\n" +
                "            ①软件需要两部手机来进行蓝牙对战。因此需要你按照软件的提示打开蓝牙。（安卓6.0以上还需要定位权限，否则不能搜索到其他设备。）建议你在进入游戏前先自定义一下你自己的布局。这样可以帮助你击败对手。\n" +
                "            ②一台点击创建游戏，随后选择你的阵型，就可以等待开始游戏了。\n" +
                "            ③另一部手机点击加入游戏，在出现的界面中找到另一台手机的蓝牙名称，点击自动开始连接，当你选择好阵型的时候，游戏正式开始！\n" +
                "            ④部分手机无法搜索到对方，但是可以被对方搜索到，建议互换角色。\n" +
                "\n" +
                "        棋盘：\n" +
                "        行走路线包括公路线和铁路线，显式较细的是公路线，较粗的是铁路线，铁路上没有障碍时工兵可以在铁路上任意行走，其他棋子在铁路上只能直走，不能转弯。\n" +
                "        棋子的落点包括结点，行营，大本营。行营是个安全岛，敌方棋子不能吃行营中的棋子，军旗必须放在大本营中，进入任何大本营的任何棋子不能再移动。\n" +
                "\n" +
                "        棋子布局：\n" +
                "        炸弹不能放在第一行，地雷只能放在最后两行，军旗只能放在大本营。你可以在自定义布局中修改你喜欢的布局。\n" +
                "\n" +
                "         玩法:\n" +
                "         将25枚棋子摆放在自己范围内的兵站和大本营，杀光对方所有能移动的棋子则获得胜利；或者吃掉对方的军棋和所有地雷，也能获得胜利。\n" +
                "\n" +
                "         吃子规则：\n" +
                "         司令>军长>师长>旅长>团长>营长>连长>排长>工兵，炸弹与任何棋子相遇时，双方都消失，包括军旗。\n" +
                "         地雷，军旗不可移动。\n" +
                "         地雷小于工兵，大于所有其他棋子。\n" +
                "         任何棋子在对方不存在地雷的情况下可以扛走军旗。\n" +
                "\n" +
                "         悔棋操作：\n" +
                "         你有3次申请悔棋的操作，前5步不可以悔棋。对方如果不同意你悔棋，那么你会浪费掉一次。\n" +
                "\n" +
                "         认输操作：\n" +
                "         胜败乃兵家常事，识时务者为俊杰……"};
        ListView listView=findViewById(R.id.help);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(help.this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
    }
}
