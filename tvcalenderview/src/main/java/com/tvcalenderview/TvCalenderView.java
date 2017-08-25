package com.tvcalenderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

/**
 * Created by lyh on 2017/8/2.
 */

public class TvCalenderView extends LinearLayout  {

    public int mYear;
    public int mMonth;
    private MonthView mMonthView;
    private HeadView mHeadView;

    public TvCalenderView(Context context) {
        this(context,null);
    }

    public TvCalenderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TvCalenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TvCalenderView);
        int textSize = typedArray.getDimensionPixelSize(R.styleable.TvCalenderView_calender_textSize, (int) (16 * getResources().getDisplayMetrics().density));
        int padding = typedArray.getDimensionPixelSize(R.styleable.TvCalenderView_calender_startAndEndPadding, (int) (20 * getResources().getDisplayMetrics().density));
        int currentDayColor = typedArray.getColor(R.styleable.TvCalenderView_calender_currentDayColor, Color.parseColor("#00af88"));
        int textColor = typedArray.getColor(R.styleable.TvCalenderView_calender_textColor, Color.BLACK);
        int focusResourceId = typedArray.getResourceId(R.styleable.TvCalenderView_calender_focusDrawable, R.drawable.date_btn_1);
        int horizontalInterval = typedArray.getDimensionPixelSize(R.styleable.TvCalenderView_calender_horizontalInterval, (int) (35 * getResources().getDisplayMetrics().density));
        int verticalInterval = typedArray.getDimensionPixelSize(R.styleable.TvCalenderView_calender_verticalInterval, (int) (40 * getResources().getDisplayMetrics().density));
        int weekTextColor = typedArray.getColor(R.styleable.TvCalenderView_calender_weekTextColor, Color.parseColor("#666666"));
        int headResourceId = typedArray.getResourceId(R.styleable.TvCalenderView_calender_headBg, R.drawable.date_head);
        int headTextSize = typedArray.getDimensionPixelSize(R.styleable.TvCalenderView_calender_headTextSize,20);
        int headTextColor = typedArray.getColor(R.styleable.TvCalenderView_calender_headTextColor, Color.parseColor("#666666"));
        int focusTextColor = typedArray.getColor(R.styleable.TvCalenderView_calender_focusTextColor, Color.BLACK);
        typedArray.recycle();

        mHeadView = new HeadView(context);
        mHeadView.setHeadTextSize(headTextSize);
        mHeadView.setHeadResId(headResourceId);
        mHeadView.setHeadTextColor(headTextColor);
        addView(mHeadView);

        WeekView week = new WeekView(context);
        week.setTextColor(weekTextColor);
        week.setVerticalInterval(verticalInterval);
        week.setPadding(padding);
        week.setTextSize(textSize);
        addView(week);

        View view = new View(context);
        view.setBackgroundColor(Color.parseColor("#cccccc"));
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) (1 * getResources().getDisplayMetrics().density));
        params.setMargins((int) (18 * getResources().getDisplayMetrics().density),(int) (15 * getResources().getDisplayMetrics().density),(int) (18 * getResources().getDisplayMetrics().density),0);
        view.setLayoutParams(params);
        addView(view);

        mMonthView = new MonthView(context);
        mMonthView.setFocusTextColor(focusTextColor);
        mMonthView.setTextSize(textSize);
        mMonthView.setPadding(padding);
        mMonthView.setTextColor(textColor);
        mMonthView.setCurrentDayColor(currentDayColor);
        mMonthView.setFocusResourceId(focusResourceId);
        mMonthView.setHorizontalInterval(horizontalInterval);
        mMonthView.setVerticalInterval(verticalInterval);
        addView(mMonthView);

        setPadding(0,(int) (30 * getResources().getDisplayMetrics().density),0,0);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(LinearLayout.VERTICAL);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mYear = mMonthView.getYear();
                mMonth = mMonthView.getMonth();
                mHeadView.setDate(mYear+"年"+(mMonth+1)+"月");
            }
        });
    }

    public void preMonth() {
        if (mMonth<=0){
            mMonth = 11;
            mYear--;
            reflashData();
            return;
        }
        mMonth--;
        reflashData();
    }

    public void nextMonth() {
        if (mMonth>=11){
            mMonth = 0;
            mYear++;
            reflashData();
            return;
        }
        mMonth++;
        reflashData();
    }

    private void reflashData() {
        mMonthView.setYearAndMonth(mYear,mMonth);
        mHeadView.setDate(mYear+"年"+(mMonth+1)+"月");
    }

    public void changeHeadData(int month,int year){
        mYear = year;
        mMonth = month;
        mHeadView.setDate(mYear+"年"+(mMonth+1)+"月");
    }

    public void setOnDataSelectedListener(MonthView.OnDateSeletedListener onDataSelectedListener){
        mMonthView.setOnDateSeletedListener(onDataSelectedListener);
    }


}
