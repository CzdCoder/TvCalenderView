package com.tvcalenderview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lyh on 2017/8/2.
 */

public class WeekView extends View {

    private int mTextColor;
    private String[] weeks = {"日","一","二","三","四","五","六"};
    private int verticalInterval;
    private int padding;
    private int textSize ;
    private Paint mPaint;
    private int startTop = (int) (10 * getResources().getDisplayMetrics().density);

    public WeekView(Context context) {
        this(context,null);
    }

    public WeekView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(mTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(heightMode == MeasureSpec.AT_MOST){
            heightSize = textSize+startTop;
        }
        if(widthMode == MeasureSpec.AT_MOST){
            widthSize = textSize*7+verticalInterval*6+padding*2;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        verticalInterval = (getWidth()-padding*2-textSize*7)/6;
        for (int i = 0; i < weeks.length; i++) {
            canvas.drawText(weeks[i],padding+i*(verticalInterval+textSize)- mPaint.measureText(weeks[i]) / 2+ mPaint.measureText("10")/2,textSize+startTop,mPaint);
        }
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mPaint.setColor(mTextColor);
    }

    public void setVerticalInterval(int verticalInterval) {
        this.verticalInterval = verticalInterval;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        mPaint.setTextSize(textSize);
    }
}
