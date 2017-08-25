package com.tvcalenderview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import java.util.Calendar;

/**
 * Created by lyh on 2017/7/31.
 */

public class MonthView extends View {

    //日期相关
    private Calendar mCalendar;
    private int mDaysCountOfMonth;
    private int mFirstDayOfWeek;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mCurrDay;
    private int mCurrYear;
    private int mCurrMonth;
    //绘制相关
    private int startTop = (int) (20 * getResources().getDisplayMetrics().density);
    private int horizontalInterval;
    private int verticalInterval;
    private int padding;
    private int textSize ;
    private int[][] mDayStr;
    //接口回调
    private OnDateSeletedListener mOnDateSeletedListener;
    //画笔相关
    private Paint mFocusTextPaint;
    private Paint mTextPaint;
    private Paint mFocusBgPaint;
    private int mCurrentDayColor;
    private int mTextColor;
    //确定焦点相关
    private int mCurRow;
    private int mCurColumn;
    private boolean isChangeMonth = true;
    private int mTempDay;
    private boolean mHasFocus;

    private Bitmap mBitmap;

    public MonthView(Context context) {
        this(context,null);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       // init(context, attrs);
        initYearAndMonth();
        initPaint();

        mDayStr = new int[6][7];
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(mOnFocusChangeListener);
    }

    //初始化年月日
    private void initYearAndMonth() {
        mCalendar = Calendar.getInstance();
        mCurrYear = mCalendar.get(Calendar.YEAR);
        mCurrDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mCurrMonth = mCalendar.get(Calendar.MONTH);
        mYear = mCurrYear;
        mMonth =mCurrMonth;
        mDay = 1;  //从一号开始获取焦点
        mCalendar.set(Calendar.YEAR, mYear);//先指定年份
        mCalendar.set(Calendar.MONTH, mMonth);//再指定月份 Java月份从0开始算
        mCalendar.set(Calendar.DAY_OF_MONTH,1);
        //获取指定年份中指定月份有几天
        mDaysCountOfMonth = mCalendar.getActualMaximum(Calendar.DATE);
        mFirstDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setTextSize(textSize);
        mTextPaint.setAntiAlias(true);
        mFocusTextPaint = new Paint();
        mFocusTextPaint.setTextSize(textSize);
        mFocusTextPaint.setAntiAlias(true);
        mFocusBgPaint = new Paint();
        mFocusBgPaint.setColor(mCurrentDayColor);
        mFocusBgPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(heightMode == MeasureSpec.AT_MOST){
            heightSize = textSize*6+horizontalInterval*5+startTop*2;  //6行字+5个行间距+起始和最后的距离
        }
        if(widthMode == MeasureSpec.AT_MOST){
            widthSize = textSize*7+verticalInterval*6+padding*2; //7列字+6个列间距+起始和最后的距离
        }
        setMeasuredDimension(widthSize, heightSize);
    }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mTempDay = mDay;  //临时变量记录当前的焦点日期
            for (int day = 0; day < mDaysCountOfMonth; day++) {
                /**
                 *  mFirstDayOfWeek 这月第一天是星期几，比如，效果图第一天是周二，mFirstDayOfWeek为3，
                 *  因为是从1开始，周二就是3，那么,按照下面的公式，算错来的row为0，column为2，两者都是
                 *  重0开始，1号的位置就是（0,2）
                 */
                int row = (day + mFirstDayOfWeek - 1)/7;
                int column = (day+mFirstDayOfWeek - 1) % 7;
                mDayStr[row][column] = day+1;   //记录每个月日期的行列位置
                String dayStr = String.valueOf(day+1);
                if (day == 0 && isChangeMonth){  //初始化焦点位置用，默认切换月份1号有焦点
                    isChangeMonth = false;
                    mCurRow = row;   //一开始在0行
                    mCurColumn = column;  //一开始在0列
                }
                if (day+1 == mDay){
                    if (mHasFocus){   //是选中日期，且有焦点
                        mTextPaint.setColor(mTextColor);
                        Rect rect = new Rect();
                        mTextPaint.getTextBounds(dayStr, 0, dayStr.length(), rect);
                        int height = rect.height();//文字高
                        //水平位置：起步+列数*列间距+文字大小*列间距+文字的一半-图片的一半
                        //垂直位置：起步+行数*水平间距+文字大小*（行数+1）- 文字高度/2 - 图片的一半
                        //说明一下，垂直位置，文字大小*（行数+1），+1是因为drawText传入的位置是左下角
                        //drawBitmap传入的位置的图片中心，所以要-文字高度/2
                        canvas.drawBitmap(mBitmap,padding+column*verticalInterval+textSize*column+ mTextPaint.measureText("10") / 2-mBitmap.getWidth()/2,
                                startTop+row * horizontalInterval + textSize * (row + 1) - height / 2-mBitmap.getHeight()/2,mFocusBgPaint);
                        canvas.drawText(dayStr, padding + mTextPaint.measureText("10")/2+column * verticalInterval +textSize*column - mTextPaint.measureText(dayStr) / 2,
                                startTop+ row * horizontalInterval + textSize * (row + 1), mFocusTextPaint);
                    }else {
                        drawDay(canvas, day, row, column, dayStr);
                    }
                }else {
                    drawDay(canvas, day, row, column, dayStr);
                }
            }
        }

        private void drawDay(Canvas canvas, int day, int row, int column, String dayStr) {
            mTextPaint.setColor(day+1 == mCurrDay && mYear == mCurrYear && mMonth == mCurrMonth ? mCurrentDayColor : mTextColor);
            canvas.drawText(dayStr, padding + mTextPaint.measureText("10")/2+column * verticalInterval +textSize*column - mTextPaint.measureText(dayStr) / 2,
                    startTop+row * horizontalInterval + textSize * (row + 1), mTextPaint);
        }

        //遥控事件
        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mCurRow <= 0) {   //如果是在第0行，再按上键的话，这时MonthView会失去焦点
                        return super.onKeyDown(keyCode, event);
                    }
                    mCurRow--;
                    mDay = mDayStr[mCurRow][mCurColumn]; //拿出存的day，后面调用invalidate重新绘制
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mCurRow >= mDayStr.length - 1) {  //下限
                        return super.onKeyDown(keyCode, event);
                    }
                    mCurRow++;
                    mDay = mDayStr[mCurRow][mCurColumn];
                    if (mDay == 0) {  //拿不到的情况，比如效果图25号下面没有日期
                        mDay = mTempDay;  //mTempDay记录了上一次的日期
                        mCurRow--;   //上面加了拿不到，减回去
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mCurColumn <= 0) {
                        preMonth();     //往左到界限，跳到上一个月
                        return super.onKeyDown(keyCode, event);
                    }
                    mCurColumn--;
                    mDay = mDayStr[mCurRow][mCurColumn];
                    if (mDay == 0) {   //拿不到的情况，比如效果图1号左边没有日期
                        preMonth();
                        return super.onKeyDown(keyCode, event);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mCurColumn >= mDayStr[0].length - 1) {
                        nextMonth();  //往右到界限，跳到下一个月
                        return super.onKeyDown(keyCode, event);
                    }
                    mCurColumn++;
                    mDay = mDayStr[mCurRow][mCurColumn];
                    if (mDay == 0) {   //拿不到的情况，比如效果图31号右边没有日期
                        nextMonth();
                        return super.onKeyDown(keyCode, event);
                    }
                    break;
                case KeyEvent.KEYCODE_ENTER:  //确认按钮
                    if (mOnDateSeletedListener != null) {
                        mOnDateSeletedListener.onDateSelected(mYear, mMonth, mDay);
                    }
                    return super.onKeyDown(keyCode, event);
                default:
                    return super.onKeyDown(keyCode, event);
            }
            invalidate();
            return true;
        }

        public void nextMonth() {
            if (mMonth >= 11) {
                mMonth = 0;
                mYear++;
                setYearAndMonth(mYear, mMonth);  //换月份
                ((TvCalenderView) getParent()).changeHeadData(mMonth, mYear);  //调用改变头部的日期
                return;
            }
            mMonth++;
            setYearAndMonth(mYear, mMonth);
            ((TvCalenderView) getParent()).changeHeadData(mMonth, mYear);
        }

    public void preMonth(){
        if (mMonth<=0){
            mMonth = 11;
            mYear--;
            setYearAndMonth(mYear,mMonth);
            ((TvCalenderView)getParent()).changeHeadData(mMonth,mYear);
            return;
        }
        mMonth--;
        setYearAndMonth(mYear,mMonth);
        ((TvCalenderView)getParent()).changeHeadData(mMonth,mYear);
    }

    public int  getYear(){
        return mYear;
    }

    public int  getMonth(){
        return mMonth;
    }

    public void setYearAndMonth(int year,int month){
        mYear = year;
        mMonth = month;
        mCalendar.set(Calendar.YEAR,year);//先指定年份
        mCalendar.set(Calendar.MONTH, month);//再指定月份 Java月份从0开始算
        mCalendar.set(Calendar.DAY_OF_MONTH,1);

        mDaysCountOfMonth = mCalendar.getActualMaximum(Calendar.DATE);    //获取指定年份中指定月份有几天
        mFirstDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);

        isChangeMonth = true;  //是不是切换月份，为了重新记录1号的行列位置
        mDay = 1;
        mDayStr = new int[6][7]; //
        invalidate();
    }


    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        mTextPaint.setTextSize(textSize);
        mFocusTextPaint.setTextSize(textSize);
    }

    public void setCurrentDayColor(int currentDayColor) {
        mCurrentDayColor = currentDayColor;
        mFocusBgPaint.setColor(mCurrentDayColor);
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public void setFocusTextColor(int color){
        mFocusTextPaint.setColor(color);
    }

    public void setFocusResourceId(int focusResourceId) {
        if (focusResourceId != -1){
            mBitmap = BitmapFactory.decodeResource(getResources(),focusResourceId);
            if (mBitmap == null){
                Drawable drawable = getResources().getDrawable(focusResourceId);
                mBitmap = drawableToBitmap(drawable);
            }
        }
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public void setHorizontalInterval(int horizontalInterval) {
        this.horizontalInterval = horizontalInterval;
    }

    public void setVerticalInterval(int verticalInterval) {
        this.verticalInterval = verticalInterval;
    }

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mHasFocus = hasFocus;
        }
    };

    public interface OnDateSeletedListener{
        void onDateSelected(int year, int month, int day);
    }

    public void setOnDateSeletedListener(OnDateSeletedListener onDateSeletedListener){
        mOnDateSeletedListener = onDateSeletedListener;
    }




    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                if(Math.abs(upX- mDownX) < 20 && Math.abs(upY - mDownY) < 20){//点击事件
                    float x = (upX + mDownX) / 2;
                    float y = (upY + mDownY) / 2;
                    int column = (int) (x / (verticalInterval+textSize));
                    int row = (int) (y / (horizontalInterval+textSize));
                    mDay = mDayStr[row][column];
                    invalidate();
                    if (mOnDateSeletedListener != null){
                        mOnDateSeletedListener.onDateSelected(mYear,mMonth,mDayStr[row][column]);
                    }
                }
                break;

        }
        return true;
    }*/




}
