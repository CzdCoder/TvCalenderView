package com.tvcalenderview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by lyh on 2017/8/23.
 */

public class HeadView extends LinearLayout {

    private Handler mHandler = new Handler();
    private TextView mTvDate;
    private ImageView mIvNext;
    private ImageView mIvPre;

    public HeadView(@NonNull Context context) {
        this(context,null);
    }

    public HeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeadView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LinearLayout.LayoutParams params = new LayoutParams((int) (406 * getResources().getDisplayMetrics().density),(int) (48 * getResources().getDisplayMetrics().density));
        setLayoutParams(params);

        mIvPre = new ImageView(context);
        mIvPre.setImageResource(R.drawable.date_last_normal);
        addView(mIvPre);

        mTvDate = new TextView(context);
        mTvDate.setTextSize(20);
        mTvDate.setPadding((int) (60 * getResources().getDisplayMetrics().density),0,(int) (60 * getResources().getDisplayMetrics().density),0);
        addView(mTvDate);

        mIvNext = new ImageView(context);
        mIvNext.setImageResource(R.drawable.date_next_normal);
        addView(mIvNext);

        setGravity(Gravity.CENTER);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void setDate(String date){
        mTvDate.setText(date);
    }

    public void setHeadResId(int headResId) {
        if (headResId != -1){
            setBackgroundResource(headResId);
        }
    }

    public void setHeadTextSize(int headTextSize) {
        mTvDate.setTextSize(headTextSize);
    }

    public void setHeadTextColor(int color) {
        mTvDate.setTextColor(color);
    }

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                switch (keyCode){
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        mIvPre.setImageResource(R.drawable.date_last_focused);
                        ((TvCalenderView) getParent()).preMonth();  //按左刷新Month
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIvPre.setImageResource(R.drawable.date_last_normal);   //伪装点击效果
                            }
                        },100);
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        mIvNext.setImageResource(R.drawable.date_next_focused);
                        ((TvCalenderView) getParent()).nextMonth();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIvNext.setImageResource(R.drawable.date_next_normal);
                            }
                        },100);
                        break;
                }
                return super.onKeyDown(keyCode, event);
            }
}
