package cn.robust.hexagon.library.menu;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by robust on 2014-04-27.
 */
public class MenuItemTopLeft extends HexagonMenuItem {
    MenuItemTopLeft(Context context, HexagonMenu menu) {
        super(context, menu, HexagonMenu.ITEM_POS_TOP_LEFT);
//        setBackgroundColor(Color.rgb(27, 188, 157));
        setBackgroundColor(Color.rgb(149, 204, 107));
        setTextColor(Color.WHITE);
    }

//    @Override
//    protected void genCenterPoint(float x, float y, float length, float margin) {
//        center.x = x - SQRT_3 / 2 * length - margin / 2;
//        center.y = y - 1.5f * length - margin * SQRT_3 / 2;
//    }
}
