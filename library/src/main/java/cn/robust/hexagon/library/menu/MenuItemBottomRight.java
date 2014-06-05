package cn.robust.hexagon.library.menu;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by robust on 2014-04-27.
 */
public class MenuItemBottomRight extends HexagonMenuItem {
    MenuItemBottomRight(Context context, HexagonMenu menu) {
        super(context, menu, HexagonMenu.ITEM_POS_BOTTOM_RIGHT);
//        setBackgroundColor(Color.rgb(232, 76, 61));
        setBackgroundColor(Color.rgb(162, 164, 175));
        setTextColor(Color.WHITE);
    }

//    @Override
//    protected void genCenterPoint(float x, float y, float length, float margin) {
//        center.x = x + SQRT_3 / 2 * length + margin / 2;
//        center.y = y + 1.5f * length + margin * SQRT_3 / 2;
//    }
}
