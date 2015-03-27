package cn.robust.hexagon.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import cn.robust.hexagon.library.OnMenuItemClickedListener;
import cn.robust.hexagon.library.menu.HexagonMenu;
import cn.robust.hexagon.library.menu.HexagonMenuItem;

import static android.view.View.OnClickListener;


public class MainActivity extends ActionBarActivity implements OnClickListener, OnMenuItemClickedListener {
    private HexagonMenu menu;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.more).setOnClickListener(this);
        findViewById(R.id.less).setOnClickListener(this);
        menu = (HexagonMenu) findViewById(R.id.menu);
        menu.setOnMenuItemClickedListener(this);
        HexagonMenuItem menuItem = menu.add(HexagonMenu.ITEM_POS_CENTER);
        menuItem.setBackgroundImg(R.drawable.avatar);

        menuItem = menu.add(HexagonMenu.ITEM_POS_TOP_RIGHT);
        menuItem.setIcon(R.drawable.camera);
        menuItem.setText("camera");

        menuItem = menu.add(HexagonMenu.ITEM_POS_RIGHT);
        menuItem.setIcon(R.drawable.global);
        menuItem.setText("global");

        menuItem = menu.add(HexagonMenu.ITEM_POS_BOTTOM_RIGHT);
        menuItem.setIcon(R.drawable.mail);
        menuItem.setText("mail");

        menuItem = menu.add(HexagonMenu.ITEM_POS_BOTTOM_LEFT);
        menuItem.setIcon(R.drawable.user);
        menuItem.setText("user");

        menuItem = menu.add(HexagonMenu.ITEM_POS_LEFT);
        menuItem.setIcon(R.drawable.back);
        menuItem.setText("back");

        menuItem = menu.add(HexagonMenu.ITEM_POS_TOP_LEFT);
        menuItem.setIcon(R.drawable.calculator);
        menuItem.setText("calculator");
    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.more:
                if(count > 1){
                    return;
                }
                HexagonMenuItem menuItem;
                if(count == 1) {
                    menuItem = menu.add(0x30000);
                    menuItem.setIcon(R.drawable.cloud);
                    menuItem.setText("cloud");
                    menuItem.setBackgroundColor(Color.rgb(241, 196, 15));
                    menuItem.setTextColor(Color.WHITE);

                    menuItem = menu.add(0x30002);
                    menuItem.setText("I'm long text. I'm long text");

                    menuItem = menu.add(0x30004);
                    menuItem.setIcon(R.drawable.global);
                    menuItem.setBackgroundColor(Color.rgb(156, 89, 184));
                    menuItem.setTextColor(Color.WHITE);

                    menuItem = menu.add(0x40001);
                    menuItem.setIcon(R.drawable.mail);
                    menuItem.setText("中文测试");
                    menuItem.setBackgroundColor(Color.rgb(27, 188, 157));
                    menuItem.setTextColor(Color.WHITE);

                    menuItem = menu.add(0x40003);
                    menuItem.setIcon(R.drawable.user);
                    menuItem.setText("user");
                    menuItem.setBackgroundColor(Color.rgb(53, 152, 220));
                    menuItem.setTextColor(Color.WHITE);

                    menuItem = menu.add(0x50000);
                    menuItem.setText("test");
                    menuItem.setBackgroundColor(Color.rgb(241, 196, 15));
                    menuItem.setTextColor(Color.WHITE);

                    menuItem = menu.add(0x50002);
                    menuItem.setText("test");

                    menuItem = menu.add(0x50004);
                    menuItem.setText("test");
                    menuItem.setBackgroundColor(Color.rgb(156, 89, 184));
                    menuItem.setTextColor(Color.WHITE);

                    menuItem = menu.add(0x00060003);
                    menuItem.setBackgroundColor(Color.rgb(232, 76, 61));
                    menuItem.setIcon(R.drawable.mail);
                    menuItem.setText("test");

                    menuItem = menu.add(0x00060001);
                    menuItem.setBackgroundColor(Color.rgb(52, 73, 94));
                    menuItem.setIcon(R.drawable.user);
                    menuItem.setText("test");
                } else {
                    menuItem = menu.add(HexagonMenu.ITEM_POS_TOP_RIGHT);
                    menuItem.setIcon(R.drawable.camera);
                    menuItem.setText("camera");

                    menuItem = menu.add(HexagonMenu.ITEM_POS_RIGHT);
                    menuItem.setIcon(R.drawable.global);
                    menuItem.setText("global");

                    menuItem = menu.add(HexagonMenu.ITEM_POS_BOTTOM_RIGHT);
                    menuItem.setIcon(R.drawable.mail);
                    menuItem.setText("mail");

                    menuItem = menu.add(HexagonMenu.ITEM_POS_BOTTOM_LEFT);
                    menuItem.setIcon(R.drawable.user);
                    menuItem.setText("user");
                }
                count++;
                break;
            case R.id.less:
                if(count == 0){
                    return;
                }
                if(count == 1){
                    menu.remove(HexagonMenu.ITEM_POS_TOP_RIGHT);
                    menu.remove(HexagonMenu.ITEM_POS_RIGHT);
                    menu.remove(HexagonMenu.ITEM_POS_BOTTOM_RIGHT);
                    menu.remove(HexagonMenu.ITEM_POS_BOTTOM_LEFT);
                } else{
                    menu.remove(0x30000);
                    menu.remove(0x30002);
                    menu.remove(0x30004);
                    menu.remove(0x40001);
                    menu.remove(0x40003);
                    menu.remove(0x50000);
                    menu.remove(0x50002);
                    menu.remove(0x50004);
                    menu.remove(0x60001);
                    menu.remove(0x60003);
                }
                count--;
                break;
        }
    }

    @Override
    public void onClick(HexagonMenuItem menuItem) {
        String text = null;
        switch (menuItem.getPosition()){
            case HexagonMenu.ITEM_POS_TOP_RIGHT:
                text = "camera";
                break;
            case HexagonMenu.ITEM_POS_RIGHT:
                text = "global";
                break;
            case HexagonMenu.ITEM_POS_BOTTOM_RIGHT:
                text = "mail";
                break;
            case HexagonMenu.ITEM_POS_BOTTOM_LEFT:
                text = "user";
                break;
            case HexagonMenu.ITEM_POS_LEFT:
                text = "back";
                break;
            case HexagonMenu.ITEM_POS_TOP_LEFT:
                text = "calculator";
                break;
        }
        if(text != null){
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }
}
