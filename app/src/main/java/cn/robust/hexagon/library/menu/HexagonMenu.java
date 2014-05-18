package cn.robust.hexagon.library.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import cn.robust.hexagon.R;
import cn.robust.hexagon.Util;
import cn.robust.hexagon.library.OnMenuItemClickedListener;

/**
 * Created by robust on 2014-04-25.
 */
public class HexagonMenu extends View {
    private static final float MARGIN = 8;//dp
    public static float margin;//px
    public static final int ITEM_POS_CENTER = 0;
    public static final int ITEM_POS_TOP_RIGHT = 1;
    public static final int ITEM_POS_RIGHT = 2;
    public static final int ITEM_POS_BOTTOM_RIGHT = 3;
    public static final int ITEM_POS_BOTTOM_LEFT = 4;
    public static final int ITEM_POS_LEFT = 5;
    public static final int ITEM_POS_TOP_LEFT = 6;
    private ArrayList<HexagonMenuItem> items = new ArrayList<HexagonMenuItem>();
    /**接收了触摸事件的menuItem*/
    private HexagonMenuItem touchedMenuItem;
    private OnMenuItemClickedListener mListener;

    public HexagonMenu(Context context) {
        super(context);
        init(context);
    }

    public HexagonMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HexagonMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setOnMenuItemClickedListener(OnMenuItemClickedListener listener){
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.rgb(37, 44, 54));
        for(HexagonMenuItem item : items){
            item.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight =MeasureSpec.getSize(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        float r;//六边形边长
        float wMargin = getPaddingRight() + getPaddingLeft() + 4 * margin;
        float hMargin = getPaddingBottom() + getPaddingTop() + 3 * margin;
        //除去边距后实际的宽度
        float actualWidth = measuredWidth - wMargin;
        //除去边距后实际的高度
        float actualHeight = measuredHeight - hMargin;
        float ratio = (actualWidth * 5f) / (actualHeight * 5.2f);
        if(ratio < 1){
            r = actualWidth / 5.2f;
            if(hMode == MeasureSpec.AT_MOST){
                measuredHeight = (int) (measuredHeight * ratio + hMargin);
            }
        } else {
            r = actualHeight / 5.0f;
            if(wMode == MeasureSpec.AT_MOST){
                measuredWidth = (int) (measuredWidth / ratio + wMargin);
            }
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
        for(HexagonMenuItem item : items){
            item.onMeasure(measuredWidth / 2, measuredHeight / 2, r, margin);
        }
    }


    private void init(Context context) {
        margin = Util.dip2px(context, MARGIN);
        //center
        HexagonMenuItem menuItem = new MenuItemCenter(this.getContext());
        menuItem.setIcon(R.drawable.menu_forum);
        menuItem.setTextColor(Color.BLACK);
        menuItem.setText("论坛");
        items.add(menuItem);
        //top-right
        menuItem = new MenuItemTopRight(this.getContext());
        menuItem.setIcon(R.drawable.menu_torrent);
        menuItem.setText("种子");
        items.add(menuItem);
        //right
        menuItem = new MenuItemRight(this.getContext());
        menuItem.setIcon(R.drawable.menu_remote);
        menuItem.setText("远程控制");
        items.add(menuItem);
        //bottom-right
        menuItem = new MenuItemBottomRight(this.getContext());
        menuItem.setIcon(R.drawable.menu_exit);
        menuItem.setText("退出");
        items.add(menuItem);
        //bottom-left
        menuItem = new MenuItemBottomLeft(this.getContext());
        menuItem.setIcon(R.drawable.menu_setting);
        menuItem.setText("设置");
        items.add(menuItem);
        //left
        menuItem = new MenuItemLeft(this.getContext());
        menuItem.setIcon(R.drawable.menu_message);
        menuItem.setText("消息中心");
        items.add(menuItem);
        //top-left
        menuItem = new MenuItemTopLeft(this.getContext());
        menuItem.setIcon(R.drawable.menu_help);
        menuItem.setText("帮助");
        items.add(menuItem);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for(HexagonMenuItem item : items){
                    if(item.isInsideHexagon(event.getX(), event.getY())){
                        touchedMenuItem = item;
                        touchedMenuItem.setPressed(true);
                        invalidate();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(touchedMenuItem != null &&
                        !touchedMenuItem.isInsideHexagon(event.getX(), event.getY())){
                    touchedMenuItem.setPressed(false);
                    invalidate();
                    touchedMenuItem = null;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(touchedMenuItem != null &&
                        touchedMenuItem.isInsideHexagon(event.getX(), event.getY())){
                    touchedMenuItem.setPressed(false);
                    invalidate();
                    if(mListener != null){
                        mListener.onClick(touchedMenuItem);
                    }
                    touchedMenuItem = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if(touchedMenuItem != null){
                    touchedMenuItem.setPressed(false);
                    invalidate();
                    touchedMenuItem = null;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private HexagonMenuItem createMenuItem(int position){
        switch (position){
            case ITEM_POS_CENTER:
                return new MenuItemCenter(getContext());
            case ITEM_POS_TOP_RIGHT:
                return new MenuItemTopRight(getContext());
            case ITEM_POS_RIGHT:
                return new MenuItemRight(getContext());
            case ITEM_POS_BOTTOM_RIGHT:
                return new MenuItemBottomRight(getContext());
            case ITEM_POS_BOTTOM_LEFT:
                return new MenuItemBottomLeft(getContext());
            case ITEM_POS_LEFT:
                return new MenuItemLeft(getContext());
            case ITEM_POS_TOP_LEFT:
                return new MenuItemTopLeft(getContext());
            default:
                return null;
        }
    }

    public HexagonMenuItem add(int position, int icon, int text){
        HexagonMenuItem item = createMenuItem(position);
        item.setIcon(icon);
        item.setText(text);
        items.add(item);
        return item;
    }

    public HexagonMenuItem add(int position, Bitmap icon, String text){
        HexagonMenuItem item = createMenuItem(position);
        item.setIcon(icon);
        item.setText(text);
        items.add(item);
        return item;
    }
}
