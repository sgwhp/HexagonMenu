package cn.robust.hexagon.library.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import cn.robust.hexagon.R;
import cn.robust.hexagon.Util;
import cn.robust.hexagon.library.OnMenuItemClickedListener;

/**
 * This view must not wrap by RelativeLayout, or the onLayout will work improperly.<br/>
 * Using FrameLayout and make sure this view is the only child is recommended.<br/>
 * @link http://stackoverflow.com/questions/10888466/onmeasure-being-passed-measurespec-exactly-apparently-wrongly
 * @link http://stackoverflow.com/questions/10510371/fill-remaining-space-with-fixed-aspect-ratio-surfaceview/10522282#10522282
 * <p/>
 * Created by robust on 2014-04-25.
 */
public class HexagonMenu extends View {
    public static final float SQRT_3 = (float)Math.sqrt(3);
    public static final int ITEM_POS_CENTER = 0x00010002;//高四位为纵向位置，低四位为横向位置，下同
    public static final int ITEM_POS_TOP_RIGHT = 0x00000003;
    public static final int ITEM_POS_RIGHT = 0x00010004;
    public static final int ITEM_POS_BOTTOM_RIGHT = 0x00020003;
    public static final int ITEM_POS_BOTTOM_LEFT = 0x00020001;
    public static final int ITEM_POS_LEFT = 0x00010000;
    public static final int ITEM_POS_TOP_LEFT = 0x00000001;
    private SparseArray<HexagonMenuItem> items = new SparseArray<HexagonMenuItem>();
    /**the menuItem which has intercepted the touch event*/
    private HexagonMenuItem touchedMenuItem;
    private OnMenuItemClickedListener mListener;
    /**六边形边长*/
    private float itemLength;
    private int offsetWidth;
    private int offsetHeight;
    private int mGravity = Gravity.CENTER;
    boolean roundedCorner;

    public HexagonMenu(Context context) {
        this(context, null);
    }

    public HexagonMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HexagonMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HexagonMenu);
            mGravity = a.getInt(R.styleable.HexagonMenu_gravity, mGravity);
            roundedCorner = a.getBoolean(R.styleable.HexagonMenu_roundedCorner, false);
            a.recycle();
        }
        init(context);
    }

    public void setOnMenuItemClickedListener(OnMenuItemClickedListener listener){
        mListener = listener;
    }

    /**
     * Do not support left|right, top|bottom
     * @param gravity
     * @see android.view.Gravity
     */
    public void setGravity(int gravity){
        if(mGravity != gravity) {
            mGravity = gravity;
            requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        HexagonMenuItem item;
        for(int i = 0; i < items.size(); i++){
            item = items.get(items.keyAt(i));
            item.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth;
        int measuredHeight;
        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        HexagonMenuItem.measuredWidthMode = wMode;
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        HexagonMenuItem.measureHeightMode = hMode;
        int minHor = Integer.MAX_VALUE;
        int maxHor = Integer.MIN_VALUE;
        int minVer = Integer.MAX_VALUE;
        int maxVer = Integer.MIN_VALUE;
        int tmp;
        HexagonMenuItem item;
        itemLength = 0;
        for(int i = 0; i < items.size(); i++){
            item = items.get(items.keyAt(i));
            if(wMode != MeasureSpec.EXACTLY || hMode != MeasureSpec.EXACTLY) {
                item.onMeasure(widthMeasureSpec, heightMeasureSpec);
                itemLength = Math.max(itemLength, item.mLength);
            }
            tmp = item.mPosition & 0xffff;
            minHor = Math.min(minHor, tmp);
            maxHor = Math.max(maxHor, tmp);
            tmp = item.mPosition >>> HexagonMenuItem.AXIS_Y_SHIFT;
            minVer = Math.min(minVer, tmp);
            maxVer = Math.max(maxVer, tmp);
        }
        float wMargin = getPaddingRight() + getPaddingLeft()
                + (maxHor - minHor) * HexagonMenuItem.mPadding / 2;
        float hMargin = getPaddingBottom() + getPaddingTop()
                + (maxVer - minVer) * SQRT_3 * HexagonMenuItem.mPadding / 2;
        //除去边距后实际的宽度
        float actualWidth = measuredWidth - wMargin;
        float horMultiple = (maxHor - minHor + 2) * SQRT_3 / 2;
        float desireWidth = actualWidth;
        //除去边距后实际的高度
        float actualHeight = measuredHeight - hMargin;
        float verMultiple = (maxVer - minVer + 1) * 1.5f + 0.5f;
        float desireHeight = actualHeight;
        offsetWidth = (int)(getPaddingLeft() - minHor * SQRT_3 / 2 * itemLength
                - minHor * HexagonMenuItem.mPadding / 2);
        offsetHeight = (int)(getPaddingTop() - minVer * 1.5f * itemLength
                - minVer * SQRT_3 * HexagonMenuItem.mPadding / 2);
        if(wMode != MeasureSpec.EXACTLY && hMode != MeasureSpec.EXACTLY){
            desireWidth = itemLength * horMultiple;
            actualWidth = desireWidth;
            measuredWidth = (int)(actualWidth + wMargin);
            desireHeight = itemLength * verMultiple;
            actualHeight = desireHeight;
            measuredHeight = (int)(actualHeight + hMargin);
        } else if(wMode == MeasureSpec.EXACTLY && hMode != MeasureSpec.EXACTLY){
            desireHeight = itemLength * verMultiple;
            actualHeight = desireHeight / verMultiple < desireWidth / horMultiple ? desireWidth / horMultiple * verMultiple : desireHeight;
            measuredHeight = (int)(actualHeight + hMargin);
        } else if(wMode != MeasureSpec.EXACTLY){
            desireWidth = itemLength * horMultiple;
            actualWidth = desireWidth / horMultiple < desireHeight / verMultiple ? desireHeight / verMultiple * horMultiple : desireWidth;
            measuredWidth = (int)(actualWidth + wMargin);
        }
        float ratio = (actualWidth * verMultiple) / (actualHeight * horMultiple);
        if (ratio < 1) {
            itemLength = actualWidth / horMultiple;
            if (hMode != MeasureSpec.EXACTLY) {
                measuredHeight = (int) (actualHeight * ratio + hMargin);
            } else if ((mGravity & (Gravity.AXIS_SPECIFIED << Gravity.AXIS_Y_SHIFT)) != 0) {
                offsetHeight += (actualHeight - itemLength * verMultiple) / 2;

                if ((mGravity & (Gravity.AXIS_PULL_BEFORE << Gravity.AXIS_Y_SHIFT)) != 0) {
                    //top
                    offsetHeight -= (actualHeight - itemLength * verMultiple) / 2;
                }
                if ((mGravity & (Gravity.AXIS_PULL_AFTER << Gravity.AXIS_Y_SHIFT)) != 0) {
                    //bottom
                    offsetHeight += (actualHeight - itemLength * verMultiple) / 2;
                }
            }
        } else {
            itemLength = actualHeight / verMultiple;
            if (wMode != MeasureSpec.EXACTLY) {
                measuredWidth = (int) (actualWidth / ratio + wMargin);
            } else if ((mGravity & Gravity.AXIS_SPECIFIED) != 0) {
                offsetWidth += (actualWidth - itemLength * horMultiple) / 2;

                if ((mGravity & Gravity.AXIS_PULL_BEFORE) != 0) {
                    //left
                    offsetWidth -= (actualWidth - itemLength * horMultiple) / 2;
                }
                if ((mGravity & Gravity.AXIS_PULL_AFTER) != 0) {
                    //right
                    offsetWidth += (actualWidth - itemLength * horMultiple) / 2;
                }
            }
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        HexagonMenuItem item;
        for(int i = 0; i < items.size(); i++){
            item = items.get(items.keyAt(i));
            item.onLayout(changed, itemLength, offsetWidth, offsetHeight);
        }
    }

    private void init(Context context) {
        HexagonMenuItem.mPadding = Util.dip2px(context, HexagonMenuItem.PADDING);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                HexagonMenuItem item;
                for(int i = 0; i < items.size(); i++){
                    item = items.get(items.keyAt(i));
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
                    touchedMenuItem = null;
                    invalidate();
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
                    touchedMenuItem = null;
                    invalidate();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private HexagonMenuItem createMenuItem(int position){
        switch (position){
            case ITEM_POS_CENTER:
                return new MenuItemCenter(getContext(), this);
            case ITEM_POS_TOP_RIGHT:
                return new MenuItemTopRight(getContext(), this);
            case ITEM_POS_RIGHT:
                return new MenuItemRight(getContext(), this);
            case ITEM_POS_BOTTOM_RIGHT:
                return new MenuItemBottomRight(getContext(), this);
            case ITEM_POS_BOTTOM_LEFT:
                return new MenuItemBottomLeft(getContext(), this);
            case ITEM_POS_LEFT:
                return new MenuItemLeft(getContext(), this);
            case ITEM_POS_TOP_LEFT:
                return new MenuItemTopLeft(getContext(), this);
            default:
                return new HexagonMenuItem(getContext(), this, position);
        }
    }

    /**
     * if the menu item with this position has already existed, the old one will be removed.
     * @param position where is the item to be put
     * @return a new menu item
     */
    public HexagonMenuItem add(int position){
        HexagonMenuItem item = createMenuItem(position);
        items.put(position, item);
        return item;
    }

    public void remove(int position){
        items.remove(position);
        requestLayout();
    }

    /**
     * Calculate menu's width & height's multiple of hexagon's length base on the horizontal mPosition
     * and vertical mPosition offset.
     * 根据横向和纵向位置的偏移量计算menu的宽和高相对于六边形边长的倍数
     * @param horPosOffset
     * @param verPosOffset
     * @return
     */
    private static float[] getMultipleOfWidthAndHeight(int horPosOffset, int verPosOffset){
        float[] widthAndHeight = new float[2];
        int horizontal = horPosOffset & 0xffff;
        int vertical = verPosOffset >>> HexagonMenuItem.AXIS_Y_SHIFT;
        widthAndHeight[0] = (1 + horizontal / 2.0f) * SQRT_3;
        widthAndHeight[1] = 0.5f + (vertical + 1) * 1.5f;
        return widthAndHeight;
    }

    public void setRoundedCorner(boolean roundedCorner){
        this.roundedCorner = roundedCorner;
        for(int i = items.size() - 1; i >= 0; i--){
            items.get(i).genPath();
        }
        invalidate();
    }
}
