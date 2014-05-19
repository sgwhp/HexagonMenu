package cn.robust.hexagon;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cn.robust.hexagon.library.menu.HexagonMenu;
import cn.robust.hexagon.library.menu.HexagonMenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(new HexagonMenu(this));
        HexagonMenu menu = (HexagonMenu) findViewById(R.id.menu);
        HexagonMenuItem menuItem = menu.add(HexagonMenu.ITEM_POS_CENTER);
        menuItem.setIcon(R.drawable.menu_forum);
        menuItem.setTextColor(Color.BLACK);
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

}
