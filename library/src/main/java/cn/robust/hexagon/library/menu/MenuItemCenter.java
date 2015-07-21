package cn.robust.hexagon.library.menu;

import android.content.Context;
import android.graphics.Color;


/**
 * Created by robust on 2014-04-27.
 */
public class MenuItemCenter extends HexagonMenuItem {
    MenuItemCenter(Context context, HexagonMenu menu){
        super(context, menu, HexagonMenu.ITEM_POS_CENTER);
        setBackgroundColor(Color.rgb(224, 227, 220));
    }

//    @Override
//    public void init(float x, float y, float hexWidth, float length, float marginX, float marginY) {
//        vertexes[0] = new Point(x, y - length);
//        vertexes[1] = new Point(x + hexWidth / 2, y - length / 2);
//        vertexes[2] = new Point(x + hexWidth / 2, y + length / 2);
//        vertexes[3] = new Point(x, y + length);
//        vertexes[4] = new Point(x - hexWidth / 2, y + length / 2);
//        vertexes[5] = new Point(x - hexWidth / 2, y - length / 2);
//        backgroundColor = Color.rgb(224, 227, 220);
//        super.init(x, y, hexWidth, length, marginX, marginY);
//    }
}
