package com.zcsj.rockerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Des: 加速按钮
 * Date: 2017/4/14 10:04
 */
public class CircleQuickenView extends View {

    private static final int DEFAULT_SIZE = 450;//view默认大小
    private static final int DEFAULT_PADDING_SIZE = 200;//大圆距离边界的间距
    private static final float DEFAULT_ROCKER_SCALE = 0.5f;//默认半径为背景的1/2
    private Paint mAreaBackgroundPaint;
    private Paint mRockerPaint;
    private Point mRockerPosition;
    private Point mCenterPoint;
    private int mAreaRadius;
    private float mRockerScale;
    private int mRockerRadius;
    private static final int AREA_BACKGROUND_MODE_PIC = 0;
    private static final int AREA_BACKGROUND_MODE_COLOR = 1;
    private static final int AREA_BACKGROUND_MODE_XML = 2;
    private static final int AREA_BACKGROUND_MODE_DEFAULT = 3;
    private int mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
    private Bitmap mAreaBitmap;
    private int mAreaColor;
    private static final int ROCKER_BACKGROUND_MODE_PIC = 4;
    private static final int ROCKER_BACKGROUND_MODE_COLOR = 5;
    private static final int ROCKER_BACKGROUND_MODE_XML = 6;
    private static final int ROCKER_BACKGROUND_MODE_DEFAULT = 7;
    private int mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
    private Bitmap mRockerBitmap;
    private int mRockerColor;
    private Bitmap mPressAreaBitmap;
    private int mPressAreaColor;
    private int mSaveColor;
    private OnPressChangeListener mOnPressChangeListener;
    private Bitmap mSaveAreaBitmap;
    private Bitmap mPressRockerBitmap;
    private int mPressRockerColor;
    private Bitmap mSaveBitmap;
    private int mSaveRockerColor;

    public CircleQuickenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);
        mAreaBackgroundPaint = new Paint();
        mAreaBackgroundPaint.setAntiAlias(true);
        mRockerPaint = new Paint();
        mRockerPaint.setAntiAlias(true);
        mCenterPoint = new Point();
        mRockerPosition = new Point();
    }

    /**
     * 获取属性
     *
     * @param context context
     * @param attrs   attrs
     */
    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RockerView);
        // 可移动区域背景(大圆圈背景)
        Drawable areaBackground = typedArray.getDrawable(R.styleable.RockerView_areaBackground);
        if (null != areaBackground) {
            // 设置了背景
            if (areaBackground instanceof BitmapDrawable) {
                // 设置了一张图片
                mAreaBitmap = ((BitmapDrawable) areaBackground).getBitmap();
                mSaveAreaBitmap = mAreaBitmap;
                //区域背景模式图片
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
            } else if (areaBackground instanceof GradientDrawable) {
                // XML
                mAreaBitmap = drawable2Bitmap(areaBackground);
                mSaveAreaBitmap = mAreaBitmap;
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_XML;
            } else if (areaBackground instanceof ColorDrawable) {
                // 色值
                mAreaColor = ((ColorDrawable) areaBackground).getColor();
                mSaveColor = mAreaColor;
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_COLOR;
            } else {
                // 其他形式
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置背景
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
        }
        Drawable pressAreaBackground = typedArray.getDrawable(R.styleable.RockerView_pressAreaBackground);
        if (null != pressAreaBackground) {
            // 设置了背景
            if (pressAreaBackground instanceof BitmapDrawable) {
                // 设置了一张图片
                mPressAreaBitmap = ((BitmapDrawable) pressAreaBackground).getBitmap();
//                //区域背景模式图片
            } else if (pressAreaBackground instanceof GradientDrawable) {
                // XML
                mPressAreaBitmap = drawable2Bitmap(pressAreaBackground);
            } else if (pressAreaBackground instanceof ColorDrawable) {
                // 色值
                mPressAreaColor = ((ColorDrawable) pressAreaBackground).getColor();
            } else {
                // 其他形式
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置背景
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
        }
        // 摇杆背景默认显示
        Drawable rockerBackground = typedArray.getDrawable(R.styleable.RockerView_rockerBackground);
        if (null != rockerBackground) {
            // 设置了摇杆背景(小圆)
            if (rockerBackground instanceof BitmapDrawable) {
                // 图片
                mRockerBitmap = ((BitmapDrawable) rockerBackground).getBitmap();
                mSaveBitmap = mRockerBitmap;
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_PIC;
            } else if (rockerBackground instanceof GradientDrawable) {
                // XML
                mRockerBitmap = drawable2Bitmap(rockerBackground);
                mSaveBitmap = mRockerBitmap;
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_XML;
            } else if (rockerBackground instanceof ColorDrawable) {
                // 色值
                mRockerColor = ((ColorDrawable) rockerBackground).getColor();
                mSaveRockerColor = mRockerColor;
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_COLOR;
            } else {
                // 其他形式
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置摇杆背景
            mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
        }
        // 摇杆背景，按下去
        Drawable pressRockerBackground = typedArray.getDrawable(R.styleable.RockerView_pressRockerBackground);
        if (null != pressRockerBackground) {
            // 设置了摇杆背景(小圆)
            if (pressRockerBackground instanceof BitmapDrawable) {
                // 图片
                mPressRockerBitmap = ((BitmapDrawable) pressRockerBackground).getBitmap();
            } else if (pressRockerBackground instanceof GradientDrawable) {
                // XML
                mPressRockerBitmap = drawable2Bitmap(pressRockerBackground);
            } else if (pressRockerBackground instanceof ColorDrawable) {
                // 色值
                mPressRockerColor = ((ColorDrawable) pressRockerBackground).getColor();
            } else {
                // 其他形式
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置摇杆背景
            mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
        }
        // 摇杆半径(摇杆半径比例:相对于外面大圆)
        mRockerScale = typedArray.getFloat(R.styleable.RockerView_rockerScale, DEFAULT_ROCKER_SCALE);
        //回调模式(默认是有变化就回调)
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = getMySize(widthMeasureSpec);
        final int height = getMySize(heightMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
    }

    /**
     * 获取测量大小
     */
    private int getMySize(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(DEFAULT_SIZE, specSize);
        } else {
            result = DEFAULT_SIZE;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight - DEFAULT_PADDING_SIZE;
        int height = getHeight() - paddingBottom - paddingTop - DEFAULT_PADDING_SIZE;
        int cx = width / 2 + paddingLeft + DEFAULT_PADDING_SIZE / 2;
        int cy = height / 2 + paddingTop + DEFAULT_PADDING_SIZE / 2;
        // 中心点
        mCenterPoint.set(cx, cy);
        mAreaRadius = Math.min(width, height) / 2;
        //中间小圆的半径66
        mRockerRadius = (int) (mAreaRadius * mRockerScale);
        // 摇杆位置
        if (0 == mRockerPosition.x || 0 == mRockerPosition.y) {
            mRockerPosition.set(mCenterPoint.x, mCenterPoint.y);
        }
        // 画可移动区域
        if (AREA_BACKGROUND_MODE_PIC == mAreaBackgroundMode || AREA_BACKGROUND_MODE_XML == mAreaBackgroundMode) {
            // 图片
            Rect src = new Rect(0, 0, mAreaBitmap.getWidth(), mAreaBitmap.getHeight());
            Rect dst = new Rect(mCenterPoint.x - mAreaRadius, mCenterPoint.y - mAreaRadius, mCenterPoint.x + mAreaRadius, mCenterPoint.y + mAreaRadius);
            canvas.drawBitmap(mAreaBitmap, src, dst, mAreaBackgroundPaint);
        } else if (AREA_BACKGROUND_MODE_COLOR == mAreaBackgroundMode) {
            // 色值
            mAreaBackgroundPaint.setColor(mAreaColor);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mAreaRadius, mAreaBackgroundPaint);
        } else {
            // 其他或者未设置
            mAreaBackgroundPaint.setColor(Color.GRAY);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mAreaRadius, mAreaBackgroundPaint);
        }
        // 画摇杆中心圆
        if (ROCKER_BACKGROUND_MODE_PIC == mRockerBackgroundMode || ROCKER_BACKGROUND_MODE_XML == mRockerBackgroundMode) {
            // 图片
            Rect src = new Rect(0, 0, mRockerBitmap.getWidth(), mRockerBitmap.getHeight());
            Rect dst = new Rect(mRockerPosition.x - mRockerRadius, mRockerPosition.y - mRockerRadius, mRockerPosition.x + mRockerRadius, mRockerPosition.y + mRockerRadius);
            canvas.drawBitmap(mRockerBitmap, src, dst, mRockerPaint);
        } else if (ROCKER_BACKGROUND_MODE_COLOR == mRockerBackgroundMode) {
            // 色值
            mRockerPaint.setColor(mRockerColor);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
        } else {
            // 其他或者未设置
            mRockerPaint.setColor(Color.BLACK);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
            case MotionEvent.ACTION_MOVE:// 移动
                mAreaBitmap = mPressAreaBitmap;
                mAreaColor = mPressAreaColor;
                mRockerBitmap = mPressRockerBitmap;
                mRockerColor = mPressRockerColor;
                mOnPressChangeListener.onPress();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:// 抬起
                mAreaBitmap = mSaveAreaBitmap;
                mAreaColor = mSaveColor;
                mRockerBitmap = mSaveBitmap;
                mRockerColor = mSaveRockerColor;
                mOnPressChangeListener.onFinish();
                invalidate();
                break;
        }
        return true;//view处理这个系列的事件
    }

    /**
     * Drawable 转 Bitmap
     * @param drawable Drawable
     * @return Bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 添加摇杆摇动角度的监听
     *
     * @param listener 回调接口
     */
    public void setOnPressChangeListener(OnPressChangeListener listener) {
        mOnPressChangeListener = listener;
    }

    /**
     * 摇动角度的监听接口
     */
    interface OnPressChangeListener {
        // 开始
        void onPress();
        // 结束
        void onFinish();
    }
}
