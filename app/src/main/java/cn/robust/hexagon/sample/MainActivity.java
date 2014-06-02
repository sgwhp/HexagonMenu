package cn.robust.hexagon.sample;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cn.robust.hexagon.sample.R;
import cn.robust.hexagon.library.menu.HexagonMenu;
import cn.robust.hexagon.library.menu.HexagonMenuItem;


public class MainActivity extends ActionBarActivity {
    private HexagonMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(new HexagonMenu(this));
        menu = (HexagonMenu) findViewById(R.id.menu);
        HexagonMenuItem menuItem = menu.add(HexagonMenu.ITEM_POS_CENTER);
        menuItem.setIcon(R.drawable.menu_forum);
        menuItem.setText("论坛");

        menuItem = menu.add(HexagonMenu.ITEM_POS_TOP_RIGHT);
        menuItem.setIcon(R.drawable.menu_torrent);
        menuItem.setText("种子");

        menuItem = menu.add(HexagonMenu.ITEM_POS_RIGHT);
        menuItem.setIcon(R.drawable.menu_remote);
        menuItem.setText("远程控制");

        menuItem = menu.add(HexagonMenu.ITEM_POS_BOTTOM_RIGHT);
        menuItem.setIcon(R.drawable.menu_exit);
        menuItem.setText("退出");

        menuItem = menu.add(HexagonMenu.ITEM_POS_BOTTOM_LEFT);
        menuItem.setIcon(R.drawable.menu_setting);
        menuItem.setText("设置");

        menuItem = menu.add(HexagonMenu.ITEM_POS_LEFT);
        menuItem.setIcon(R.drawable.menu_message);
        menuItem.setText("消息中心");

        menuItem = menu.add(HexagonMenu.ITEM_POS_TOP_LEFT);
        menuItem.setIcon(R.drawable.menu_help);
        menuItem.setText("帮助");

        menuItem = menu.add(0x30000);
        menuItem.setText("test1");
        menuItem.setBackgroundColor(Color.rgb(241, 196, 15));
        menuItem.setTextColor(Color.WHITE);

        menuItem = menu.add(0x30002);
        menuItem.setText("test2");

        menuItem = menu.add(0x30004);
        menuItem.setText("test3");
        menuItem.setBackgroundColor(Color.rgb(156, 89, 184));
        menuItem.setTextColor(Color.WHITE);

        menuItem = menu.add(0x40001);
        menuItem.setText("test4");
        menuItem.setBackgroundColor(Color.rgb(27, 188, 157));
        menuItem.setTextColor(Color.WHITE);

        menuItem = menu.add(0x40003);
        menuItem.setText("test5");
        menuItem.setBackgroundColor(Color.rgb(53, 152, 220));
        menuItem.setTextColor(Color.WHITE);

        menuItem = menu.add(0x50000);
        menuItem.setText("test6");
        menuItem.setBackgroundColor(Color.rgb(241, 196, 15));
        menuItem.setTextColor(Color.WHITE);

        menuItem = menu.add(0x50002);
        menuItem.setText("test7");

        menuItem = menu.add(0x50004);
        menuItem.setText("test8");
        menuItem.setBackgroundColor(Color.rgb(156, 89, 184));
        menuItem.setTextColor(Color.WHITE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
//        HexagonMenuItem menuItem;
//        menuItem = menu.add(HexagonMenu.ITEM_POS_TOP_RIGHT);
//        menuItem.setIcon(R.drawable.menu_torrent);
//        menuItem.setText("种子");
//
//        menuItem = menu.add(HexagonMenu.ITEM_POS_RIGHT);
//        menuItem.setIcon(R.drawable.menu_remote);
//        menuItem.setText("远程控制");
//
//        menuItem = menu.add(HexagonMenu.ITEM_POS_BOTTOM_RIGHT);
//        menuItem.setIcon(R.drawable.menu_exit);
//        menuItem.setText("退出");
//
//        menuItem = menu.add(HexagonMenu.ITEM_POS_BOTTOM_LEFT);
//        menuItem.setIcon(R.drawable.menu_setting);
//        menuItem.setText("设置");
    }

}
