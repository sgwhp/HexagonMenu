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
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
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
    private static final float PRECISION = 1f;//精度
    static final int AXIS_Y_SHIFT = 16;
    private static final int ROUND_RADIUS = 10;
    private int roundRadius;
    protected Context mContext;
    protected int mPosition;
    private Paint mPaint;
    protected Point center = new Point();
    float mLength;
    protected Point[] vertexes = new Point[6];
    protected Point[] vertexesPressed = new Point[6];
    private Point[] roundVertexes = new Point[12];
    private Point[] roundVertexesPressed = new Point[12];
//    // 生成一个圆角半径的圆，圆心在原点
//    private int[][] circlePoints;
//    // 45°圆弧的点的数量
//    private int circlePointsCount;
    // 每个顶点圆角的圆心
    private float[][] circleCenter = new float[6][2];
    private float[][] circleCenterPressed = new float[6][2];
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
        mLayout = new BoringLayout(mText, mTextPaint, outer.width() - mPadding, Layout.Alignment.ALIGN_CENTER
                , 1.0f, 0f, metrics, true, TextUtils.TruncateAt.END, outer.width() - mPadding);
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
        if(backgroundImg != null && (img.getWidth() > backgroundImg.getHeight()
                || img.getHeight() > backgroundImg.getHeight())
                && (measuredWidthMode != View.MeasureSpec.EXACTLY
                || measureHeightMode != View.MeasureSpec.EXACTLY)){
            backgroundImg = img;
            mMenu.requestLayout();
        } else{
            backgroundImg = img;
            if(outer != null) {
                initBgImg();
                mMenu.invalidate();
            }
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
        bgPressedMatrix.postScale(outer.width() / 0.9f / backgroundImg.getWidth(),
                outer.height() / 0.9f / backgroundImg.getHeight(),
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

//    private void genCircle(){
//        circlePoints = new int[(roundRadius+1)*8][2];
//        int x = 0;
//        int y = roundRadius;
//        int p = 1 - roundRadius;
//        circlePoints[0][0] = x;
//        circlePoints[0][1] = y;
//        while(x <= y){
//            x++;
//            if(p < 0){
//                p += 2 * x + 1;
//            } else {
//                y--;
//                p += 2 * (x - y + 1);
//            }
//            circlePoints[x][0] = x;
//            circlePoints[x][1] = y;
//        }
//        circlePointsCount = x + 1;
//        for(int i = 0; i < circlePointsCount; i++){
//            circlePoints[circlePointsCount*2-i-1][0] = circlePoints[i][1];
//            circlePoints[circlePointsCount*2-i-1][1] = circlePoints[i][0];
//            circlePoints[circlePointsCount*2+i][0] = circlePoints[i][1];
//            circlePoints[circlePointsCount*2+i][1] = -circlePoints[i][0];
//            circlePoints[circlePointsCount*4-i-1][0] = circlePoints[i][0];
//            circlePoints[circlePointsCount*4-i-1][1] = -circlePoints[i][1];
//            circlePoints[circlePointsCount*4+i][0] = -circlePoints[i][0];
//            circlePoints[circlePointsCount*4+i][1] = -circlePoints[i][1];
//            circlePoints[circlePointsCount*6-i-1][0] = -circlePoints[i][1];
//            circlePoints[circlePointsCount*6-i-1][1] = -circlePoints[i][0];
//            circlePoints[circlePointsCount*6+i][0] = -circlePoints[i][1];
//            circlePoints[circlePointsCount*6+i][1] = circlePoints[i][0];
//            circlePoints[circlePointsCount*8-i-1][0] = -circlePoints[i][0];
//            circlePoints[circlePointsCount*8-i-1][1] = circlePoints[i][1];
//        }
//    }

    private void init(){
        mTextSize = Util.sp2px(mContext, TEXT_SIZE);
        roundRadius = Util.dip2px(mContext, ROUND_RADIUS);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        for(int i = 0; i < 6; i++){
            vertexes[i] = new Point();
            vertexesPressed[i] = new Point();
            roundVertexes[i] = new Point();
            roundVertexes[6+i] = new Point();
            roundVertexesPressed[i] = new Point();
            roundVertexesPressed[6+i] = new Point();
        }
//        genCircle();
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

    void genPath(){
        mPath.reset();
        mPressedPath.reset();

        if(mMenu.roundedCorner) {
            mPath.moveTo(roundVertexes[3].x, roundVertexes[3].y);
            RectF rect = new RectF();
            int k;
            for(int i = 0, j = 2; i < 6; i++, j++){
                j = j % 6;
                k = j * 2 + 1;
                rect.set(circleCenter[j][0] - roundRadius, circleCenter[j][1] - roundRadius
                        , circleCenter[j][0] + roundRadius, circleCenter[j][1] + roundRadius);
                mPath.arcTo(rect, i * 60, 60);
                mPath.lineTo(roundVertexes[k].x, roundVertexes[k].y);
                rect.set(circleCenterPressed[j][0] - roundRadius, circleCenterPressed[j][1] - roundRadius
                        , circleCenterPressed[j][0] + roundRadius, circleCenterPressed[j][1] + roundRadius);
                mPressedPath.arcTo(rect, i * 60, 60);
                mPressedPath.lineTo(roundVertexesPressed[k].x, roundVertexesPressed[k].y);
            }
//            mPath.moveTo(circlePoints[0][0] + circleCenter[3][0], circlePoints[0][1] + circleCenter[3][1]);
//            mPressedPath.moveTo(circlePoints[0][0] + circleCenterPressed[3][0]
//                    , circlePoints[0][1] + circleCenterPressed[3][1]);
//            int j = 1;
//            while (circlePoints[j][0] * SQRT_3 < circlePoints[j][1]) {
//                mPath.lineTo(circlePoints[j][0] + circleCenter[3][0]
//                        , circlePoints[j][1] + circleCenter[3][1]);
//                mPressedPath.lineTo(circlePoints[j][0] + circleCenterPressed[3][0]
//                        , circlePoints[j][1] + circleCenterPressed[3][1]);
//                j++;
//            }
//            int count30 = j - 1;
//            int count60 = 2 * circlePointsCount - count30;
//            mPath.lineTo(roundVertexes[4].x, roundVertexes[4].y);
//            mPressedPath.lineTo(roundVertexesPressed[4].x, roundVertexesPressed[4].y);
//            for (int i = 0; i < count60; i++, j++) {
//                mPath.lineTo(circlePoints[j][0] + circleCenter[2][0]
//                        , circlePoints[j][1] + circleCenter[2][1]);
//                mPressedPath.lineTo(circlePoints[j][0] + circleCenterPressed[2][0]
//                        , circlePoints[j][1] + circleCenterPressed[2][1]);
//            }
//            mPath.lineTo(roundVertexes[2].x, roundVertexes[2].y);
//            mPressedPath.lineTo(roundVertexesPressed[2].x, roundVertexesPressed[2].y);
//            for (int i = 0; i < count60; i++, j++) {
//                mPath.lineTo(circlePoints[j][0] + circleCenter[1][0], circlePoints[j][1] + circleCenter[1][1]);
//                mPressedPath.lineTo(circlePoints[j][0] + circleCenterPressed[1][0]
//                        , circlePoints[j][1] + circleCenterPressed[1][1]);
//            }
//            mPath.lineTo(roundVertexes[0].x, roundVertexes[0].y);
//            mPressedPath.lineTo(roundVertexesPressed[0].x, roundVertexesPressed[0].y);
//            for (int i = 0; i < count30 * 2; i++, j++) {
//                mPath.lineTo(circlePoints[j][0] + circleCenter[0][0], circlePoints[j][1] + circleCenter[0][1]);
//                mPressedPath.lineTo(circlePoints[j][0] + circleCenterPressed[0][0]
//                        , circlePoints[j][1] + circleCenterPressed[0][1]);
//            }
//            mPath.lineTo(roundVertexes[10].x, roundVertexes[10].y);
//            mPressedPath.lineTo(roundVertexesPressed[10].x, roundVertexesPressed[10].y);
//            for (int i = 0; i < count60; j++, i++) {
//                mPath.lineTo(circlePoints[j][0] + circleCenter[5][0], circlePoints[j][1] + circleCenter[5][1]);
//                mPressedPath.lineTo(circlePoints[j][0] + circleCenterPressed[5][0]
//                        , circlePoints[j][1] + circleCenterPressed[5][1]);
//            }
//            mPath.lineTo(roundVertexes[8].x, roundVertexes[8].y);
//            mPressedPath.lineTo(roundVertexesPressed[8].x, roundVertexesPressed[8].y);
//            for (int i = 0; i < count60; j++, i++) {
//                mPath.lineTo(circlePoints[j][0] + circleCenter[4][0], circlePoints[j][1] + circleCenter[4][1]);
//                mPressedPath.lineTo(circlePoints[j][0] + circleCenterPressed[4][0]
//                        , circlePoints[j][1] + circleCenterPressed[4][1]);
//            }
//            mPath.lineTo(roundVertexes[6].x, roundVertexes[6].y);
//            mPressedPath.lineTo(roundVertexesPressed[6].x, roundVertexesPressed[6].y);
//            for (; j < circlePointsCount * 8; j++) {
//                mPath.lineTo(circlePoints[j][0] + circleCenter[3][0], circlePoints[j][1] + circleCenter[3][1]);
//                mPressedPath.lineTo(circlePoints[j][0] + circleCenterPressed[3][0]
//                        , circlePoints[j][1] + circleCenterPressed[3][1]);
//            }
        } else {
            mPath.moveTo(vertexes[0].x, vertexes[0].y);
            mPressedPath.moveTo(vertexesPressed[0].x, vertexesPressed[0].y);
            for (int i = vertexes.length - 1; i > 0; i--) {
                mPath.lineTo(vertexes[i].x, vertexes[i].y);
                mPressedPath.lineTo(vertexesPressed[i].x, vertexesPressed[i].y);
            }
        }
        mPath.close();
        mPressedPath.close();
    }

    protected void onLayout(boolean changed, float length, int paddingLeft, int paddingTop){
        mLength = length;
        if(!changed){
            return;
        }
        genCenterPoint(paddingLeft, paddingTop);
        float x = center.x;
        float y = center.y;
        float tmp2 = length / 2;
        float tmp1 = SQRT_3 * tmp2;
        float tmp3 = tmp2 * 0.9f;
        float tmp4 = tmp1 * 0.9f;
        float tmp5 = roundRadius / SQRT_3;

        vertexes[0].set(x, y - length);
        circleCenter[0][0] = x;
        circleCenter[0][1] = vertexes[0].y + 2 * tmp5;
        roundVertexes[0].set(x + roundRadius / 2, vertexes[0].y + tmp5 / 2);
        roundVertexes[11].set(x - roundRadius / 2, roundVertexes[0].y);
        vertexesPressed[0].set(x, y - length * 0.9f);
        circleCenterPressed[0][0] = vertexesPressed[0].x;
        circleCenterPressed[0][1] = vertexesPressed[0].y + 2 * tmp5;
        roundVertexesPressed[0].set(vertexesPressed[0].x + roundRadius / 2, vertexesPressed[0].y + tmp5 / 2);
        roundVertexesPressed[11].set(vertexesPressed[0].x - roundRadius / 2, roundVertexesPressed[0].y);

        vertexes[1].set(x + tmp1, y - tmp2);
        circleCenter[1][0] = vertexes[1].x - roundRadius;
        circleCenter[1][1] = vertexes[1].y + tmp5;
        roundVertexes[1].set(vertexes[1].x - roundRadius / 2, vertexes[1].y - tmp5 / 2);
        roundVertexes[2].set(vertexes[1].x, vertexes[1].y + tmp5);
        vertexesPressed[1].set(x + tmp4, y - tmp3);
        circleCenterPressed[1][0] = vertexesPressed[1].x - roundRadius;
        circleCenterPressed[1][1] = vertexesPressed[1].y + tmp5;
        roundVertexesPressed[1].set(vertexesPressed[1].x - roundRadius / 2, vertexesPressed[1].y - tmp5 / 2);
        roundVertexesPressed[2].set(vertexesPressed[1].x, vertexesPressed[1].y + tmp5);

        vertexes[2].set(x + tmp1, y + tmp2);
        circleCenter[2][0] = (int) (vertexes[2].x - roundRadius);
        circleCenter[2][1] = (int) (vertexes[2].y - tmp5);
        roundVertexes[3].set(vertexes[2].x, vertexes[2].y - tmp5);
        roundVertexes[4].set(vertexes[2].x - roundRadius / 2, vertexes[2].y + tmp5 / 2);
        vertexesPressed[2].set(x + tmp4, y + tmp3);
        circleCenterPressed[2][0] = (int) (vertexesPressed[2].x - roundRadius);
        circleCenterPressed[2][1] = (int) (vertexesPressed[2].y - tmp5);
        roundVertexesPressed[3].set(vertexesPressed[2].x, vertexesPressed[2].y - tmp5);
        roundVertexesPressed[4].set(vertexesPressed[2].x - roundRadius / 2, vertexesPressed[2].y + tmp5 / 2);

        vertexes[3].set(x, y + length);
        circleCenter[3][0] = (int) vertexes[3].x;
        circleCenter[3][1] = (int) (vertexes[3].y - 2 * tmp5);
        roundVertexes[5].set(vertexes[3].x + roundRadius / 2, vertexes[3].y - tmp5 / 2);
        roundVertexes[6].set(vertexes[3].x - roundRadius / 2, roundVertexes[5].y);
        vertexesPressed[3].set(x, y + length * 0.9f);
        circleCenterPressed[3][0] = (int) vertexesPressed[3].x;
        circleCenterPressed[3][1] = (int) (vertexesPressed[3].y - 2 * tmp5);
        roundVertexesPressed[5].set(vertexesPressed[3].x + roundRadius / 2, vertexesPressed[3].y - tmp5 / 2);
        roundVertexesPressed[6].set(vertexesPressed[3].x - roundRadius / 2, roundVertexesPressed[5].y);

        vertexes[4].set(x - tmp1, y + tmp2);
        circleCenter[4][0] = (int) (vertexes[4].x + roundRadius);
        circleCenter[4][1] = (int) (vertexes[4].y - tmp5);
        roundVertexes[7].set(vertexes[4].x + roundRadius / 2, vertexes[4].y + tmp5 / 2);
        roundVertexes[8].set(vertexes[4].x, vertexes[4].y - tmp5);
        vertexesPressed[4].set(x - tmp4, y + tmp3);
        circleCenterPressed[4][0] = (int) (vertexesPressed[4].x + roundRadius);
        circleCenterPressed[4][1] = (int) (vertexesPressed[4].y - tmp5);
        roundVertexesPressed[7].set(vertexesPressed[4].x + roundRadius / 2, vertexesPressed[4].y + tmp5 / 2);
        roundVertexesPressed[8].set(vertexesPressed[4].x, vertexesPressed[4].y - tmp5);

        vertexes[5].set(x - tmp1, y - tmp2);
        circleCenter[5][0] = (int) (vertexes[5].x + roundRadius);
        circleCenter[5][1] = (int) (vertexes[5].y + tmp5);
        roundVertexes[9].set(vertexes[5].x, vertexes[5].y + tmp5);
        roundVertexes[10].set(vertexes[5].x + roundRadius / 2, vertexes[5].y - tmp5 / 2);
        vertexesPressed[5].set(x - tmp4, y - tmp3);
        circleCenterPressed[5][0] = (int) (vertexesPressed[5].x + roundRadius);
        circleCenterPressed[5][1] = (int) (vertexesPressed[5].y + tmp5);
        roundVertexesPressed[9].set(vertexesPressed[5].x, vertexesPressed[5].y + tmp5);
        roundVertexesPressed[10].set(vertexesPressed[5].x + roundRadius / 2, vertexesPressed[5].y - tmp5 / 2);

        genPath();

        outer = new Rect((int) vertexes[5].x , (int) vertexes[0].y
                , (int) vertexes[1].x + 1, (int) vertexes[3].y + 1);

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
            offset = mLength / 2 - mTextSize / 2;
        }
        if(mLayout != null){
            //draw text
            canvas.save();
            canvas.translate(outer.left, center.y + offset - mTextSize / 2);
            mLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * see if point (x,y) is inside the hexagon's outer rectangle <br/>
     * 判断点(x,y)是否在六边形的外接矩形内
     * @param x
     * @param y
     * @return true if it's inside the hexagon's outer rectangle, otherwise, return false;
     */
    public boolean isInsideOuter(float x, float y){
        return x >= outer.left && x <= outer.right && y >= outer.top && y <= outer.bottom;
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
//            if(kr != 0) {
//                bs = segmentEnd.y;
//                br = rayEnd.y - rayEnd.x * kr;
//                x = (bs - br) / kr;
//            } else {
                //segment是平行于x轴的线
                x = rayEnd.x;
//            }
        }
        double result = Math.sqrt(dsx * dsx + dsy * dsy) -
                Math.sqrt((segmentEnd.x - x) * (segmentEnd.x - x)
                        + (segmentEnd.y - y) * (segmentEnd.y - y))
                - Math.sqrt((segmentStart.x - x) * (segmentStart.x - x)
                + (segmentStart.y - y) * (segmentStart.y - y));
        //判断交点是否在线段（六边形的边）上
        if(result < -PRECISION){
            return null;
        }
        result = Math.sqrt(drx * drx  + dry * dry) -
                Math.sqrt((rayEnd.x - x) * (rayEnd.x - x)
                        + (rayEnd.y - y) * (rayEnd.y - y))
                - Math.sqrt((rayStart.x - x) * (rayStart.x - x)
                + (rayStart.y - y) * (rayStart.y - y));
        //判断交点是否在射线上
        if(result > -PRECISION){
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
     * @return true if it's inside hexagon, otherwise false
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
        for(int i = 0; i < vertexes.length; i++){
            intersection = getIntersection(point, end, vertexes[i], vertexes[(i+1)%6]);
            if(intersection != null){
                count++;
                if(Math.abs(intersection.x - vertexes[(i+1)%6].x) < PRECISION
                        && Math.abs(intersection.y - vertexes[(i+1)%6].y) < PRECISION
                        && i != 0 && i != 3){
                    //交点即是顶点，且边处于射线同一边，不重复计算
                    //当交点是顶部或底部的顶点时，相交的两条边都处于射线同一侧
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
