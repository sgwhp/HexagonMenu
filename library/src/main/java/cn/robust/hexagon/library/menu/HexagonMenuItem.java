package cn.robust.hexagon.library.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.FloatMath;
import android.view.View;

import cn.robust.hexagon.Util;
import cn.robust.hexagon.library.Point;

/**
 * hexagon，the order of vertex is as follow:
 *        0
 *      5 /\ 1
 *       |  |
 *      4 \/ 2
 *        3
 * Created by robust on 2014-04-25.
 */
public class HexagonMenuItem {
    public static final float SQRT_3 = (float)Math.sqrt(3);
    static final int PADDING = 5;//dp
    static int mPadding;//px
    private static final float precision = 1f;//精度
    static final int AXIS_Y_SHIFT = 16;
    protected Context mContext;
    protected int mPosition;
    private Paint mPaint;
    protected Point center = new Point();
    float mLength;
    protected Point[] points = new Point[6];
    protected Point[] pressedPoints = new Point[6];
    protected Path mPath = new Path();
    protected Path mPressedPath = new Path();
    protected Rect outer;
    protected String mText;
    private int mTextColor = Color.BLACK;
    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private static final float TEXT_SIZE = 14;
    private float mTextSize;
    private BoringLayout mLayout;
    protected Bitmap mIcon;
    private Matrix iconMatrix = new Matrix();
    /**背景图片*/
    protected Bitmap backgroundImg;
    protected BitmapShader mShader;
    private Matrix bgMatrix;
    private Matrix bgPressedMatrix;
    protected boolean pressed;
    private HexagonMenu mMenu;
    static int measuredWidthMode = View.MeasureSpec.EXACTLY;
    static int measureHeightMode = View.MeasureSpec.EXACTLY;

    HexagonMenuItem(Context context, HexagonMenu menu, int position){
        mContext = context;
        mMenu = menu;
        this.mPosition = position;
        init();
    }

    public int getPosition(){
        return mPosition;
    }

    public String getText(){
        return mText;
    }

    public void setText(int text){
        setText(mContext.getString(text));
    }

    /**
     * 设置文字
     * @param text
     */
    public void setText(String text){
        if((measuredWidthMode != View.MeasureSpec.EXACTLY
                || measureHeightMode != View.MeasureSpec.EXACTLY)
                && mText != null && mTextPaint.measureText(mText) < mTextPaint.measureText(text)){
            mText = text;
            mMenu.requestLayout();
        } else {
            mText = text;
            if (outer != null) {
                initText();
            }
        }
    }

    private void initText(){
        BoringLayout.Metrics metrics = BoringLayout.isBoring(mText, mTextPaint);
        if(metrics == null){
            //中文字符并非left-to-right的unicode字符，用英文字符生成一个BoringLayout.Metrics
            metrics = BoringLayout.isBoring("boring layout sucks", mTextPaint);
            metrics.width = outer.width();
        }
        mLayout = new BoringLayout(mText, mTextPaint, outer.width(), Layout.Alignment.ALIGN_CENTER
                , 1.0f, 0f, metrics, true, TextUtils.TruncateAt.END, outer.width());
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        if(textSize != mTextSize && (measuredWidthMode != View.MeasureSpec.EXACTLY
                || measureHeightMode != View.MeasureSpec.EXACTLY)){
            this.mTextSize = textSize;
            mTextPaint.setTextSize(textSize);
            mMenu.requestLayout();
        } else {
            this.mTextSize = textSize;
            mTextPaint.setTextSize(textSize);
            initText();
            mMenu.invalidate();
        }
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        if(mTextPaint != null){
            mTextPaint.setColor(textColor);
        }
    }

    public Bitmap getIcon() {
        return mIcon;
    }

    public void setIcon(int resId){
        setIcon(BitmapFactory.decodeResource(mContext.getResources(), resId));
    }

    public void setIcon(Bitmap icon) {
        mIcon = icon;
        if(measuredWidthMode != View.MeasureSpec.EXACTLY
                || measureHeightMode != View.MeasureSpec.EXACTLY){
            mMenu.requestLayout();
        } else {
            initIcon();
            mMenu.invalidate();
        }
    }

    /**
     * 初始化icon的大小等参数
     */
    private void initIcon(){
        float scaleWidth = (mLength * SQRT_3 - mPadding * 2) / mIcon.getWidth();
        float height = mText == null ? 0 : mTextSize;// + mPadding;
        height = mLength - height;
        float scaleHeight = height / mIcon.getHeight();
        float scale = Math.min(scaleWidth, scaleHeight);
        iconMatrix.reset();
        iconMatrix.postScale(scale, scale);
        iconMatrix.postTranslate(center.x - mIcon.getWidth() * scale * 0.5f
                , center.y - mIcon.getHeight() * scale - mLength / 2 + height);
//        mIcon = Bitmap.createBitmap(mIcon, 0, 0, mIcon.getWidth(), mIcon.getHeight(), iconMatrix, true);
    }

    public void setBackgroundImg(int resId){
        setBackgroundImg(BitmapFactory.decodeResource(mContext.getResources(), resId));
    }

    public void setBackgroundImg(Bitmap img){
        mShader = new BitmapShader(img, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if((img.getWidth() > backgroundImg.getHeight() || img.getHeight() > backgroundImg.getHeight())
                && (measuredWidthMode != View.MeasureSpec.EXACTLY
                || measureHeightMode != View.MeasureSpec.EXACTLY)){
            backgroundImg = img;
            mMenu.requestLayout();
        } else if(outer != null){
            initBgImg();
            mMenu.invalidate();
        }
    }

    /**
     * 初始化背景图片的尺寸等数据
     */
    private void initBgImg(){
        bgMatrix = new Matrix();
        bgPressedMatrix = new Matrix();
        bgMatrix.postScale(outer.width() / (float) backgroundImg.getWidth(),
                outer.height() / (float) backgroundImg.getHeight(),
                backgroundImg.getWidth() / 2, backgroundImg.getHeight() / 2);
        bgPressedMatrix.postScale(outer.width() / (float) backgroundImg.getWidth() * 0.9f,
                outer.height() / (float) backgroundImg.getHeight() * 0.9f,
                backgroundImg.getWidth() / 2, backgroundImg.getHeight() / 2);
        bgMatrix.postTranslate(center.x - backgroundImg.getWidth() / 2,
                center.y - backgroundImg.getHeight() / 2);
        bgPressedMatrix.postTranslate(center.x - backgroundImg.getWidth() / 2,
                center.y - backgroundImg.getHeight() / 2);
        mShader.setLocalMatrix(bgMatrix);
        mPaint.setShader(mShader);
    }

    public void setBackgroundColor(int backgroundColor) {
        mPaint.setColor(backgroundColor);
        mShader = null;
        mPaint.setShader(null);
        mMenu.invalidate();
    }

    private void init(){
        mTextSize = Util.sp2px(mContext, TEXT_SIZE);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        for(int i = 0; i < 6; i++){
            points[i] = new Point();
            pressedPoints[i] = new Point();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        float desireWidth = 0;
        float desireHeight = 0;
        if(mIcon != null){
            desireWidth = mIcon.getWidth();
            desireHeight = mIcon.getHeight();
        }
        if(mText != null){
            desireWidth = Math.max(desireWidth, mTextPaint.measureText(mText));
            desireHeight += mTextSize;// + mPadding;
        }
        desireWidth += 2 * mPadding;
//        desireHeight += 2 * mPadding;
        if(backgroundImg != null){
            desireWidth = Math.max(desireWidth, backgroundImg.getWidth());
            desireHeight = Math.max(desireHeight, backgroundImg.getHeight());
        }
        mLength = Math.max(desireWidth / SQRT_3, desireHeight);
    }

    protected void onLayout(boolean changed, float length, int paddingLeft, int paddingTop){
        mLength = length;
//        if(!changed){
//            return;
//        }
        genCenterPoint(paddingLeft, paddingTop);
        float x = center.x;
        float y = center.y;
        float tmp2 = length / 2;
        float tmp1 = SQRT_3 * tmp2;
        float tmp3 = tmp2 * 0.9f;
        float tmp4 = tmp1 * 0.9f;
        points[0].set(x, y - length);
        pressedPoints[0].set(x, y - length * 0.9f);
        points[1].set(x + tmp1, y - tmp2);
        pressedPoints[1].set(x + tmp4, y - tmp3);
        points[2].set(x + tmp1, y + tmp2);
        pressedPoints[2].set(x + tmp4, y + tmp3);
        points[3].set(x, y + length);
        pressedPoints[3].set(x, y + length * 0.9f);
        points[4].set(x - tmp1, y + tmp2);
        pressedPoints[4].set(x - tmp4, y + tmp3);
        points[5].set(x - tmp1, y - tmp2);
        pressedPoints[5].set(x - tmp4, y - tmp3);
        mPath.reset();
        mPressedPath.reset();
        mPath.moveTo(points[0].x, points[0].y);
        mPressedPath.moveTo(pressedPoints[0].x, pressedPoints[0].y);
        for(int i = points.length - 1; i > 0; i--){
            mPath.lineTo(points[i].x, points[i].y);
            mPressedPath.lineTo(pressedPoints[i].x, pressedPoints[i].y);
        }
        mPath.close();
        mPressedPath.close();
        outer = new Rect((int)points[5].x , (int)points[0].y
                , (int)points[1].x + 1, (int)points[3].y + 1);

        if(mShader != null){
            initBgImg();
        }
        if(mText != null){
            initText();
        }
        if(mIcon != null){
            initIcon();
        }
    }

    protected void genCenterPoint(int paddingLeft, int paddingTop){
        int horizontalPos = mPosition & 0xffff;
        int verticalPos = mPosition >>> AXIS_Y_SHIFT;
        center.set((horizontalPos + 1) * SQRT_3 * mLength / 2
                + horizontalPos / 2.0f * mPadding + paddingLeft
                , (verticalPos + 1)* mLength + verticalPos * mLength / 2
                + verticalPos * mPadding * SQRT_3 / 2 + paddingTop);
    }

    protected void draw(Canvas canvas){
        if(!pressed){
            //draw background
            if(mShader != null){
                mShader.setLocalMatrix(bgPressedMatrix);
            }
            canvas.drawPath(mPath, mPaint);
        } else {
            //draw background
            if(mShader != null){
                mShader.setLocalMatrix(bgMatrix);
            }
            canvas.drawPath(mPressedPath, mPaint);
        }
        float offset = 0;
        if(mIcon != null){
            // draw icon
            canvas.drawBitmap(mIcon, iconMatrix, mPaint);
            offset = mLength / 2;
        }
        if(mLayout != null){
            //draw text
            canvas.save();
            canvas.translate(outer.left, center.y + offset - mTextSize);
            mLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * see if point (x,y) is inside the hexagon's outer rectangle <br/>
     * 判断点(x,y)是否在六边形的外接矩形内
     * @param x
     * @param y
     * @return
     */
    public boolean isInsideOuter(float x, float y){
        if(x >= outer.left && x <= outer.right && y >= outer.top && y <= outer.bottom){
            return true;
        }
        return false;
    }

    /**
     * Calculate the intersection of ray and segment(side of hexagon).<br/>
     * 计算射线与线段的交点，射线可取平行于x轴的一条线，不会产生与六边形的边重合的情况
     * @param rayStart 射线起点
     * @param rayEnd 射线终点
     * @param segmentStart 线段起点
     * @param segmentEnd 线段终点
     * @return 交点，无交点则返回null
     */
    private Point getIntersection(Point rayStart, Point rayEnd, Point segmentStart, Point segmentEnd){
        //判断是否平行，斜率相等，两条线平行，没有交点（重合的情况不处理）
        float drx, dry, dsx, dsy;
        drx = rayEnd.x - rayStart.x;
        dry = rayEnd.y - rayStart.y;
        dsx = segmentEnd.x - segmentStart.x;
        dsy = segmentEnd.y - segmentStart.y;
        if(dry == 0 && dsy == 0){
            return null;
        }
        float kr = 0, ks = 0;
        float br, bs;
        //交点
        float x,y;
        if(drx != 0){
            kr = dry / drx;
        }
        if(dsx != 0){
            ks = dsy / dsx;
        }
        if(kr == ks && kr != 0){
            return null;
        }
        if(kr != 0 && ks != 0){
            br = rayEnd.y - rayEnd.x * kr;
            bs = segmentEnd.y - segmentEnd.x * ks;
            x = (bs - br) / (kr - ks);
            y = kr * (bs - br) / (kr - ks) + br;
        } else if(kr == 0){
            //ray 是平行于x轴的线
            y = rayEnd.y;
            if(ks != 0){
                br = rayEnd.y;
                bs = segmentEnd.y - segmentEnd.x * ks;
                x = (br - bs) / ks;
            } else {
                //segment是平行于y轴的线
                x = segmentEnd.x;
            }
        } else{
            //ray 是平行于y轴的线
            y = segmentEnd.y;
            if(kr != 0) {
                bs = segmentEnd.y;
                br = rayEnd.y - rayEnd.x * kr;
                x = (bs - br) / kr;
            } else {
                //segment是平行于x轴的线
                x = rayEnd.x;
            }
        }
        float result = FloatMath.sqrt(dsx * dsx + dsy * dsy) -
                FloatMath.sqrt((segmentEnd.x - x) * (segmentEnd.x - x)
                        + (segmentEnd.y - y) * (segmentEnd.y - y))
                - FloatMath.sqrt((segmentStart.x - x) * (segmentStart.x - x)
                + (segmentStart.y - y) * (segmentStart.y - y));
        //判断交点是否在线段（六边形的边）上
        if(result < -precision){
            return null;
        }
        result = FloatMath.sqrt(drx * drx  + dry * dry) -
                FloatMath.sqrt((rayEnd.x - x) * (rayEnd.x - x)
                        + (rayEnd.y - y) * (rayEnd.y - y))
                - FloatMath.sqrt((rayStart.x - x) * (rayStart.x - x)
                + (rayStart.y - y) * (rayStart.y - y));
        //判断交点是否在射线上
        if(result > -precision){
            //交点在线段segment上
            return new Point(x, y);
        }
        return null;
    }

    /**
     * See if point (x,y) is inside hexagon.<p/>
     *
     * See if point (x,y) is inside the outer rectangle of hexagon, first.<br/>
     * Pick a ray going from this point through another point which outside the hexagon.
     * Then calculate the intersections of this ray and each side of hexagon.
     * If the number of intersections is 1, then this point (x,y) is inside hexagon and return true,
     * otherwise, return false.
     * 判断点(x,y)是否在六边形内
     * @param x
     * @param y
     * @return
     */
    public boolean isInsideHexagon(float x, float y){
        //先判断是否在六边形的外接矩形内
        if(!isInsideOuter(x, y)){
            return false;
        }
        //计算经过点(x,y)的射线与六边形每条边的交点总和
        int count = 0;
        Point intersection;
        //取一条(平行于x轴的)射线
        Point point = new Point(x, y);
        Point end = new Point(outer.right + 10, y);
        for(int i = 0; i < points.length; i++){
            intersection = getIntersection(point, end, points[i], points[(i+1)%6]);
            if(intersection != null){
                count++;
                if(Math.abs(intersection.x - points[(i+1)%6].x) < precision
                        && Math.abs(intersection.y - points[(i+1)%6].y) < precision){
                    //交点即是顶点，不重复计算
                    i++;
                }
            }
        }
        //若交点数为1，则在六边形内，否则在外部
        return count == 1;
    }

    void setPressed(boolean pressed){
        this.pressed = pressed;
    }
}
