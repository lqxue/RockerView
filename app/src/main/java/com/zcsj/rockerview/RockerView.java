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
 * Des: 安卓虚拟控制杆
 * Date: 2017/4/14 10:04
 */
public class RockerView extends View {

    private static final int DEFAULT_SIZE = 450;
    private static final int DEFAULT_PADDING_SIZE = 200;
    private static final float DEFAULT_ROCKER_SCALE = 0.5f;//默认半径为大圆的1/2
    //大圆移动区域背景
    private Paint mAreaBackgroundPaint;
    //小圆画笔
    private Paint mRockerPaint;
    //小圆位置(存储摇杆坐标)
    private Point mRockerPosition;
    //中心点坐标
    private Point mCenterPoint;
    //大圆半径
    private int mAreaRadius;
    //小圆半径相对于大圆半径的缩放比
    private float mRockerScale;
    //小圆半径
    private int mRockerRadius;
    private CallBackMode mCallBackMode = CallBackMode.CALL_BACK_MODE_MOVE;
    private OnAngleChangeListener mOnAngleChangeListener;
    private OnShakeListener mOnShakeListener;
    private DirectionMode mDirectionMode;
    private Direction tempDirection = Direction.DIRECTION_CENTER;
    // 角度
    private static final double ANGLE_0 = 0;
    private static final double ANGLE_360 = 360;
    // 360°水平方向平分2份的边缘角度
    private static final double ANGLE_HORIZONTAL_2D_OF_0P = 90;
    private static final double ANGLE_HORIZONTAL_2D_OF_1P = 270;
    // 360°垂直方向平分2份的边缘角度
    private static final double ANGLE_VERTICAL_2D_OF_0P = 0;
    private static final double ANGLE_VERTICAL_2D_OF_1P = 180;
    // 360°平分4份的边缘角度
    private static final double ANGLE_4D_OF_0P = 0;
    private static final double ANGLE_4D_OF_1P = 90;
    private static final double ANGLE_4D_OF_2P = 180;
    private static final double ANGLE_4D_OF_3P = 270;
    // 360°平分4份的边缘角度(旋转45度)
    private static final double ANGLE_ROTATE45_4D_OF_0P = 45;
    private static final double ANGLE_ROTATE45_4D_OF_1P = 135;
    private static final double ANGLE_ROTATE45_4D_OF_2P = 225;
    private static final double ANGLE_ROTATE45_4D_OF_3P = 315;
    // 360°平分8份的边缘角度
    private static final double ANGLE_8D_OF_0P = 22.5;
    private static final double ANGLE_8D_OF_1P = 67.5;
    private static final double ANGLE_8D_OF_2P = 112.5;
    private static final double ANGLE_8D_OF_3P = 157.5;
    private static final double ANGLE_8D_OF_4P = 202.5;
    private static final double ANGLE_8D_OF_5P = 247.5;
    private static final double ANGLE_8D_OF_6P = 292.5;
    private static final double ANGLE_8D_OF_7P = 337.5;
    //大圆背景来源
    private static final int AREA_BACKGROUND_MODE_PIC = 0;
    private static final int AREA_BACKGROUND_MODE_COLOR = 1;
    private static final int AREA_BACKGROUND_MODE_XML = 2;
    private static final int AREA_BACKGROUND_MODE_DEFAULT = 3;
    private int mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
    private Bitmap mAreaBitmap;
    private int mAreaColor;
    //小圆背景来源
    private static final int ROCKER_BACKGROUND_MODE_PIC = 4;
    private static final int ROCKER_BACKGROUND_MODE_COLOR = 5;
    private static final int ROCKER_BACKGROUND_MODE_XML = 6;
    private static final int ROCKER_BACKGROUND_MODE_DEFAULT = 7;
    private int mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
    private Bitmap mRockerBitmap;
    private int mRockerColor;

    public RockerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取自定义属性
        initAttribute(context, attrs);
        // 移动区域画笔
        mAreaBackgroundPaint = new Paint();
        //setAntiAlias该方法作用是抗锯齿
        mAreaBackgroundPaint.setAntiAlias(true);
        // 摇杆画笔
        mRockerPaint = new Paint();
        mRockerPaint.setAntiAlias(true);
        //中心点
        mCenterPoint = new Point();
        // 摇杆位置
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
                //区域背景模式图片
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
            } else if (areaBackground instanceof GradientDrawable) {
                // XML
                mAreaBitmap = drawable2Bitmap(areaBackground);
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_XML;
            } else if (areaBackground instanceof ColorDrawable) {
                // 色值
                mAreaColor = ((ColorDrawable) areaBackground).getColor();
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_COLOR;
            } else {
                // 其他形式
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置背景
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
        }
        // 摇杆背景
        Drawable rockerBackground = typedArray.getDrawable(R.styleable.RockerView_rockerBackground);
        if (null != rockerBackground) {
            // 设置了摇杆背景(小圆)
            if (rockerBackground instanceof BitmapDrawable) {
                // 图片
                mRockerBitmap = ((BitmapDrawable) rockerBackground).getBitmap();
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_PIC;
            } else if (rockerBackground instanceof GradientDrawable) {
                // XML
                mRockerBitmap = drawable2Bitmap(rockerBackground);
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_XML;
            } else if (rockerBackground instanceof ColorDrawable) {
                // 色值
                mRockerColor = ((ColorDrawable) rockerBackground).getColor();
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_COLOR;
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
        mCallBackMode = getCallBackMode(typedArray.getInt(R.styleable.RockerView_rockerCallBackMode, 0));
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
        // 可移动区域的半径133(根据中心点X,Y坐标控制的半径)
        //mAreaRadius = (width <= height) ? (int) (cx / (mRockerScale + 1.3)) : (int) (cy / (mRockerScale + 1.3));
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
            mRockerPaint.setColor(Color.RED);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                //方向和角度的回调开始
                callBackStart();
            case MotionEvent.ACTION_MOVE:// 移动
                float moveX = event.getX();
                float moveY = event.getY();
//                Log.e("RockerView","相对View坐标系的X值"+moveX);
//                Log.e("RockerView","相对View坐标系的Y值"+moveY);
                //控制小圆超出大圆一部分
                //mRockerPosition = getRockerPositionPoint(mCenterPoint, new Point((int) moveX, (int) moveY), mAreaRadius + mRockerRadius, mRockerRadius);
                //控制小圆不超过大圆范围
                mRockerPosition = getRockerPositionPoint(mCenterPoint, new Point((int) moveX, (int) moveY), mAreaRadius, mRockerRadius);
                moveRocker(mRockerPosition.x, mRockerPosition.y);
                break;
            case MotionEvent.ACTION_UP:// 抬起
                if (null != mOnAngleChangeListener)
                    mOnAngleChangeListener.angle(0.0);
            case MotionEvent.ACTION_CANCEL:// 移出区域
                // 回调 结束
                callBackFinish();
                if (mOnShakeListener != null) {
                    mOnShakeListener.direction(Direction.DIRECTION_CENTER);
                }
                moveRocker(mCenterPoint.x, mCenterPoint.y);
                break;
        }
        return true;//view处理这个系列的事件
    }

    /**
     * 获取摇杆实际要显示的位置（点）
     *
     * @param centerPoint  中心点
     * @param touchPoint   触摸点
     * @param regionRadius 摇杆可活动区域半径
     * @param rockerRadius 摇杆半径
     * @return 摇杆实际显示的位置（点）
     */
    public Point getRockerPositionPoint(Point centerPoint, Point touchPoint, float regionRadius, float rockerRadius) {
        // 两点在X轴的距离
        float lenX = (float) (touchPoint.x - centerPoint.x);
        // 两点在Y轴距离
        float lenY = (float) (touchPoint.y - centerPoint.y);
        // 两点距离
        float lenXY = (float) Math.sqrt((double) (lenX * lenX + lenY * lenY));
        // 计算弧度
        double radian = Math.acos(lenX / lenXY) * (touchPoint.y < centerPoint.y ? -1 : 1);//0.7853981805117192
        // 弧度变为角度角度
        double tmp = Math.round(radian / Math.PI * 180);
        double angle = tmp >= 0 ? tmp : 360 + tmp;
        if (lenXY + rockerRadius <= regionRadius) { // 触摸位置在可活动范围内
            // 回调 返回参数
            callBack(angle);
            return touchPoint;
        } else { // 触摸位置在可活动范围以外
            // 计算要显示的位置
            int showPointX = (int) (centerPoint.x + (regionRadius - rockerRadius) * Math.cos(radian));
            int showPointY = (int) (centerPoint.y + (regionRadius - rockerRadius) * Math.sin(radian));
            callBack(angle);
            return new Point(showPointX, showPointY);
        }
    }

    /**
     * 移动摇杆到指定位置
     *
     * @param x x坐标
     * @param y y坐标
     */
    private void moveRocker(float x, float y) {
        mRockerPosition.set((int) x, (int) y);
        invalidate();
    }

    /**
     * Drawable 转 Bitmap
     *
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
     * 回调
     * 开始
     */
    public void callBackStart() {
        tempDirection = Direction.DIRECTION_CENTER;
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.onStart();
        }
        if (null != mOnShakeListener) {
            mOnShakeListener.onStart();
        }
    }

    /**
     * 回调
     * 返回参数
     *
     * @param angle 摇动角度
     */
    private void callBack(double angle) {
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.angle(angle);
        }
        if (null != mOnShakeListener) {
            if (CallBackMode.CALL_BACK_MODE_MOVE == mCallBackMode) {
                switch (mDirectionMode) {
                    case DIRECTION_2_HORIZONTAL:// 左右方向
                        if (ANGLE_0 <= angle && ANGLE_HORIZONTAL_2D_OF_0P > angle || ANGLE_HORIZONTAL_2D_OF_1P <= angle && ANGLE_360 > angle) {
                            // 右
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_HORIZONTAL_2D_OF_0P <= angle && ANGLE_HORIZONTAL_2D_OF_1P > angle) {
                            // 左
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        }
                        break;
                    case DIRECTION_2_VERTICAL:// 上下方向
                        if (ANGLE_VERTICAL_2D_OF_0P <= angle && ANGLE_VERTICAL_2D_OF_1P > angle) {
                            // 下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_VERTICAL_2D_OF_1P <= angle && ANGLE_360 > angle) {
                            // 上
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    case DIRECTION_4_ROTATE_0:// 四个方向
                        if (ANGLE_4D_OF_0P <= angle && ANGLE_4D_OF_1P > angle) {
                            // 右下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
                        } else if (ANGLE_4D_OF_1P <= angle && ANGLE_4D_OF_2P > angle) {
                            // 左下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
                        } else if (ANGLE_4D_OF_2P <= angle && ANGLE_4D_OF_3P > angle) {
                            // 左上
                            mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
                        } else if (ANGLE_4D_OF_3P <= angle && ANGLE_360 > angle) {
                            // 右上
                            mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
                        }
                        break;
                    case DIRECTION_4_ROTATE_45:// 四个方向 旋转45度
                        if (ANGLE_0 <= angle && ANGLE_ROTATE45_4D_OF_0P > angle || ANGLE_ROTATE45_4D_OF_3P <= angle && ANGLE_360 > angle) {
                            // 右
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_ROTATE45_4D_OF_0P <= angle && ANGLE_ROTATE45_4D_OF_1P > angle) {
                            // 下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_ROTATE45_4D_OF_1P <= angle && ANGLE_ROTATE45_4D_OF_2P > angle) {
                            // 左
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_ROTATE45_4D_OF_2P <= angle && ANGLE_ROTATE45_4D_OF_3P > angle) {
                            // 上
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    case DIRECTION_8:// 八个方向
                        if (ANGLE_0 <= angle && ANGLE_8D_OF_0P > angle || ANGLE_8D_OF_7P <= angle && ANGLE_360 > angle) {
                            // 右
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_8D_OF_0P <= angle && ANGLE_8D_OF_1P > angle) {
                            // 右下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
                        } else if (ANGLE_8D_OF_1P <= angle && ANGLE_8D_OF_2P > angle) {
                            // 下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_8D_OF_2P <= angle && ANGLE_8D_OF_3P > angle) {
                            // 左下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
                        } else if (ANGLE_8D_OF_3P <= angle && ANGLE_8D_OF_4P > angle) {
                            // 左
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_8D_OF_4P <= angle && ANGLE_8D_OF_5P > angle) {
                            // 左上
                            mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
                        } else if (ANGLE_8D_OF_5P <= angle && ANGLE_8D_OF_6P > angle) {
                            // 上
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        } else if (ANGLE_8D_OF_6P <= angle && ANGLE_8D_OF_7P > angle) {
                            // 右上
                            mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
                        }
                        break;
                    default:
                        break;
                }
            } else if (CallBackMode.CALL_BACK_MODE_STATE_CHANGE == mCallBackMode) {
                switch (mDirectionMode) {
                    case DIRECTION_2_HORIZONTAL:// 左右方向
                        if ((ANGLE_0 <= angle && ANGLE_HORIZONTAL_2D_OF_0P > angle || ANGLE_HORIZONTAL_2D_OF_1P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
                            // 右
                            tempDirection = Direction.DIRECTION_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_HORIZONTAL_2D_OF_0P <= angle && ANGLE_HORIZONTAL_2D_OF_1P > angle && tempDirection != Direction.DIRECTION_LEFT) {
                            // 左
                            tempDirection = Direction.DIRECTION_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        }
                        break;
                    case DIRECTION_2_VERTICAL:// 上下方向
                        if (ANGLE_VERTICAL_2D_OF_0P <= angle && ANGLE_VERTICAL_2D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN) {
                            // 下
                            tempDirection = Direction.DIRECTION_DOWN;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_VERTICAL_2D_OF_1P <= angle && ANGLE_360 > angle && tempDirection != Direction.DIRECTION_UP) {
                            // 上
                            tempDirection = Direction.DIRECTION_UP;
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    case DIRECTION_4_ROTATE_0:// 四个方向
                        if (ANGLE_4D_OF_0P <= angle && ANGLE_4D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN_RIGHT) {
                            // 右下
                            tempDirection = Direction.DIRECTION_DOWN_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
                        } else if (ANGLE_4D_OF_1P <= angle && ANGLE_4D_OF_2P > angle && tempDirection != Direction.DIRECTION_DOWN_LEFT) {
                            // 左下
                            tempDirection = Direction.DIRECTION_DOWN_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
                        } else if (ANGLE_4D_OF_2P <= angle && ANGLE_4D_OF_3P > angle && tempDirection != Direction.DIRECTION_UP_LEFT) {
                            // 左上
                            tempDirection = Direction.DIRECTION_UP_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
                        } else if (ANGLE_4D_OF_3P <= angle && ANGLE_360 > angle && tempDirection != Direction.DIRECTION_UP_RIGHT) {
                            // 右上
                            tempDirection = Direction.DIRECTION_UP_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
                        }
                        break;
                    case DIRECTION_4_ROTATE_45:// 四个方向 旋转45度
                        if ((ANGLE_0 <= angle && ANGLE_ROTATE45_4D_OF_0P > angle || ANGLE_ROTATE45_4D_OF_3P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
                            // 右
                            tempDirection = Direction.DIRECTION_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_ROTATE45_4D_OF_0P <= angle && ANGLE_ROTATE45_4D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN) {
                            // 下
                            tempDirection = Direction.DIRECTION_DOWN;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_ROTATE45_4D_OF_1P <= angle && ANGLE_ROTATE45_4D_OF_2P > angle && tempDirection != Direction.DIRECTION_LEFT) {
                            // 左
                            tempDirection = Direction.DIRECTION_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_ROTATE45_4D_OF_2P <= angle && ANGLE_ROTATE45_4D_OF_3P > angle && tempDirection != Direction.DIRECTION_UP) {
                            // 上
                            tempDirection = Direction.DIRECTION_UP;
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    case DIRECTION_8:// 八个方向
                        if ((ANGLE_0 <= angle && ANGLE_8D_OF_0P > angle || ANGLE_8D_OF_7P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
                            // 右
                            tempDirection = Direction.DIRECTION_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_8D_OF_0P <= angle && ANGLE_8D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN_RIGHT) {
                            // 右下
                            tempDirection = Direction.DIRECTION_DOWN_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
                        } else if (ANGLE_8D_OF_1P <= angle && ANGLE_8D_OF_2P > angle && tempDirection != Direction.DIRECTION_DOWN) {
                            // 下
                            tempDirection = Direction.DIRECTION_DOWN;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_8D_OF_2P <= angle && ANGLE_8D_OF_3P > angle && tempDirection != Direction.DIRECTION_DOWN_LEFT) {
                            // 左下
                            tempDirection = Direction.DIRECTION_DOWN_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
                        } else if (ANGLE_8D_OF_3P <= angle && ANGLE_8D_OF_4P > angle && tempDirection != Direction.DIRECTION_LEFT) {
                            // 左
                            tempDirection = Direction.DIRECTION_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_8D_OF_4P <= angle && ANGLE_8D_OF_5P > angle && tempDirection != Direction.DIRECTION_UP_LEFT) {
                            // 左上
                            tempDirection = Direction.DIRECTION_UP_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
                        } else if (ANGLE_8D_OF_5P <= angle && ANGLE_8D_OF_6P > angle && tempDirection != Direction.DIRECTION_UP) {
                            // 上
                            tempDirection = Direction.DIRECTION_UP;
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        } else if (ANGLE_8D_OF_6P <= angle && ANGLE_8D_OF_7P > angle && tempDirection != Direction.DIRECTION_UP_RIGHT) {
                            // 右上
                            tempDirection = Direction.DIRECTION_UP_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 回调
     * 结束
     */
    private void callBackFinish() {
        tempDirection = Direction.DIRECTION_CENTER;
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.onFinish();
        }
        if (null != mOnShakeListener) {
            mOnShakeListener.onFinish();
        }
    }

    /**
     * 回调模式
     */
    enum CallBackMode {
        // 有移动就立刻回调
        CALL_BACK_MODE_MOVE,
        // 只有状态变化的时候才回调
        CALL_BACK_MODE_STATE_CHANGE,
    }

    /**
     * 摇杆支持几个方向
     */
    enum DirectionMode {
        DIRECTION_2_HORIZONTAL,// 横向 左右两个方向
        DIRECTION_2_VERTICAL, // 纵向 上下两个方向
        DIRECTION_4_ROTATE_0, // 四个方向
        DIRECTION_4_ROTATE_45, // 四个方向 旋转45度
        DIRECTION_8 // 八个方向
    }

    /**
     * 方向
     */
    enum Direction {
        DIRECTION_LEFT, // 左
        DIRECTION_RIGHT, // 右
        DIRECTION_UP, // 上
        DIRECTION_DOWN, // 下
        DIRECTION_UP_LEFT, // 左上
        DIRECTION_UP_RIGHT, // 右上
        DIRECTION_DOWN_LEFT, // 左下
        DIRECTION_DOWN_RIGHT, // 右下
        DIRECTION_CENTER // 中间
    }

    /**
     * 添加摇杆摇动角度的监听
     *
     * @param listener 回调接口
     */
    public void setOnAngleChangeListener(OnAngleChangeListener listener) {
        mOnAngleChangeListener = listener;
    }

    /**
     * 添加摇动的监听
     *
     * @param directionMode 监听的方向
     * @param listener      回调
     */
    public void setOnShakeListener(DirectionMode directionMode, OnShakeListener listener) {
        mDirectionMode = directionMode;
        mOnShakeListener = listener;
    }

    /**
     * 摇动方向监听接口
     */
    public interface OnShakeListener {
        // 开始
        void onStart();

        /**
         * 摇动方向
         *
         * @param direction 方向
         */
        void direction(Direction direction);

        // 结束
        void onFinish();
    }

    /**
     * 摇动角度的监听接口
     */
    interface OnAngleChangeListener {
        // 开始
        void onStart();

        /**
         * 摇杆角度变化
         *
         * @param angle 角度[0,360)
         */
        void angle(double angle);

        // 结束
        void onFinish();
    }

    private CallBackMode getCallBackMode(int mode) {
        switch (mode) {
            case 0:
                return CallBackMode.CALL_BACK_MODE_MOVE;
            case 1:
                return CallBackMode.CALL_BACK_MODE_STATE_CHANGE;
        }
        return mCallBackMode;
    }
}
