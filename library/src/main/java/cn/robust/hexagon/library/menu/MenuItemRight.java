package cn.robust.hexagon.library.menu;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by robust on 2014-04-27.
 */
class MenuItemRight extends HexagonMenuItem {
    MenuItemRight(Context context, HexagonMenu menu) {
        super(context, menu, HexagonMenu.ITEM_POS_RIGHT);
//        setBackgroundColor(Color.rgb(156, 89, 184));
        setBackgroundColor(Color.rgb(238, 201, 84));
        setTextColor(Color.WHITE);
    }

//    @Override
//    public void init(float x, float y, float hexWidth, float length, float marginX, float marginY) {
//        points[0] = new Point(x + marginX + hexWidth, y - length);
//        points[1] = new Point(x + marginX + hexWidth * 1.5f, y - length / 2);
//        points[2] = new Point(x + marginX + hexWidth * 1.5f, y + length / 2);
//        points[3] = new Point(x + marginX + hexWidth, y + length);
//        points[4] = new Point(x + marginX + hexWidth / 2, y + length / 2);
//        points[5] = new Point(x + marginX + hexWidth / 2, y - length / 2);
//        backgroundColor = Color.rgb(156, 89, 184);
//        super.init(x, y, hexWidth, length, marginX, marginY);
//    }

//    @Override
//    protected void genCenterPoint(float x, float y, float length, float margin) {
//        center.x = x + SQRT_3 * length + margin;
//        center.y = y;
//    }
}
