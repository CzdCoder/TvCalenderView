package com.tvcalenderviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tvcalenderview.TvCalenderView;
import com.tvcalenderview.MonthView;

public class DemoActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        TvCalenderView calenderView = (TvCalenderView) findViewById(R.id.calenderView);
        calenderView.setOnDataSelectedListener(new MonthView.OnDateSeletedListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                Toast.makeText(DemoActivity1.this,year+"-"+(month+1)+"-"+day,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
