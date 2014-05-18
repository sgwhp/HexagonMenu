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

import cn.robust.hexagon.Util;
import cn.robust.hexagon.library.Point;

/**
 * 正六边形，顶点默认顺序如下：
 *        0
 *      5 /\ 1
 *       |  |
 *      4 \/ 2
 *        3
 * Created by robust on 2014-04-25.
 */
abstract class HexagonMenuItem {
    public static final float SQRT_3 = (float)Math.sqrt(3);
    private static final float precision = 1f;//精度
    protected Context mContext;
    protected int mId;
    private Paint mPaint;
    protected Point center = new Point();
    private float mLength;
    protected Point[] points = new Point[6];
    protected Point[] pressedPoints = new Point[6];
    protected Path mPath = new Path();
    protected Path mPressedPath = new Path();
    protected Rect outer;
    protected String mText;
    private int mTextColor = Color.WHITE;
    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private static final float TEXT_SIZE = 16;
    private float mTextSize;
    private BoringLayout mLayout;
    protected Bitmap mIcon;
    /**背景图片*/
    protected Bitmap backgroundImg;
    protected BitmapShader mShader;
    private int backgroundColor;
    private Matrix bgMatrix;
    private Matrix bgPressedMatrix;
    protected boolean pressed;
    private HexagonMenu mMenu;

    HexagonMenuItem(Context context){
        mContext = context;
        init();
    }

    public void attach(HexagonMenu menu){
        mMenu = menu;
    }

    public int getId(){
        return mId;
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
        mText = text;
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        if(outer != null){
            initText();
        }
    }

    private void initText(){
        BoringLayout.Metrics metrics = BoringLayout.isBoring(mText, mTextPaint);
        if(metrics == null){
            //中文字符并非left-to-right的unicode字符，用英文字符生成一个BoringLayout.Metrics
            metrics = BoringLayout.isBoring("test", mTextPaint);
            metrics.width = outer.width();
        }
        mLayout = new BoringLayout(mText, mTextPaint, outer.width(), Layout.Alignment.ALIGN_CENTER
                , 1.0f, 0f, metrics, true, TextUtils.TruncateAt.END, outer.width());
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        if(mTextPaint != null){
            mTextPaint.setTextSize(textSize);
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
        this.mIcon = icon;
        if(mLength != 0){
            initIcon();
        }
    }

    /**
     * 初始化icon的大小等参数
     */
    private void initIcon(){
        float scaleWidth = mLength / mIcon.getWidth();
        float scaleHeight = mLength / mIcon.getHeight();
        float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
        //设置icon的大小为六边形边长的0.75
        scale *= 0.75f;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        mIcon = Bitmap.createBitmap(mIcon, 0, 0, mIcon.getWidth(), mIcon.getHeight(), matrix, true);
    }

    public void setBackgroundImg(int resId){
        setBackgroundImg(BitmapFactory.decodeResource(mContext.getResources(), resId));
    }

    public void setBackgroundImg(Bitmap img){
        backgroundImg = img;
        mShader = new BitmapShader(backgroundImg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if(outer != null){
            initBgImg();
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

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        mPaint.setColor(backgroundColor);
        mShader = null;
        mPaint.setShader(null);
    }

    private void init(){
        mTextSize = Util.dip2px(mContext, TEXT_SIZE);
        mPaint = new Paint();
        mPaint.setColor(backgroundColor);
        mPaint.setAntiAlias(true);
        for(int i = 0; i < 6; i++){
            points[i] = new Point();
            pressedPoints[i] = new Point();
        }
    }

    public void onMeasure(float x, float y, float length, float margin){
        genCenterPoint(x, y, length, margin);
        this.mLength = length;
        x = center.x;
        y = center.y;
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

    protected void genCenterPoint(float x, float y, float length, float margin){center.set(x, y);}

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
        //y坐标相对于中心点的y坐标偏移量，避免icon和文字同时出现时重合，并且保证单独出现时能够居中
        float margin = 0;
        if(mIcon != null){
            // draw icon
            if(mLayout != null && (margin = mLength / 2 - mIcon.getHeight() / 2 - mLayout.getHeight()) > 0){
                margin = 0;
            }
            canvas.drawBitmap(mIcon, center.x - mIcon.getWidth() / 2
                    , center.y - mIcon.getHeight() / 2 + margin, mPaint);
        }
        if(mLayout != null){
            //draw text
            if(mIcon != null){
                margin = mIcon.getHeight() / 2 + margin;
            } else {
                margin = -mLayout.getHeight() / 2;
            }
            canvas.save();
            canvas.translate(outer.left, center.y + margin);
            mLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
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
     * 计算射线与线段的交点，射线可取平行于x轴的一条线，不会产生与六边形的边重合的情况
     * @param rayStart
     * @param rayEnd
     * @param segmentStart
     * @param segmentEnd
     * @return
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
        double result = Math.sqrt(dsx * dsx  + dsy * dsy) -
                Math.sqrt((segmentEnd.x - x) * (segmentEnd.x - x)
                        + (segmentEnd.y - y) * (segmentEnd.y - y))
                - Math.sqrt((segmentStart.x - x) * (segmentStart.x - x)
                + (segmentStart.y - y) * (segmentStart.y - y));
//        Log.v("whp", "s " + result);
        //判断交点是否在线段（六边形的边）上
        if(result < -precision){
            return null;
        }
        result = Math.sqrt(drx * drx  + dry * dry) -
                Math.sqrt((rayEnd.x - x) * (rayEnd.x - x)
                        + (rayEnd.y - y) * (rayEnd.y - y))
                - Math.sqrt((rayStart.x - x) * (rayStart.x - x)
                + (rayStart.y - y) * (rayStart.y - y));
//        Log.v("whp", "r " + result);
        //判断交点是否在射线上
        if(result > -precision){
            //交点在线段segment上
            return new Point(x, y);
        }
        return null;
    }

    /**
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
        //取一条平行于x轴的射线
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
//        Log.v("whp", "c " + count);
        return count == 1;
    }

    void setPressed(boolean pressed){
        this.pressed = pressed;
    }
}
