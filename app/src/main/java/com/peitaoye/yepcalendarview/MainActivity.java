package com.peitaoye.yepcalendarview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YepMonthView yepMonthView = (YepMonthView) findViewById(R.id.ok);
        Calendar min = Calendar.getInstance();
        min.set(Calendar.MONTH, 7);
        min.set(Calendar.YEAR, 2017);
        Calendar max = Calendar.getInstance();
        max.set(Calendar.MONTH, 9);
        max.set(Calendar.YEAR, 2018);
        yepMonthView.setMinAndMaxDate(min, max);
//        yepMonthView.setOnDayLongClikedListener(new SimpleMonthView.OnDayLongClikedListener() {
//            @Override
//            public void onDayLongClick(int day) {
//
//            }
//        });
//        yepMonthView.setOnDaySelectedListener(new SimpleMonthView.OnDaySelectedListener() {
//            @Override
//            public void onDaySelected(int day) {
//
//            }
//        });

//        SimpleMonthView simpleMonthView = findViewById(R.id.monthView);
//        List<WorkCalendar> list = new ArrayList<>();
//        Calendar calendar = Calendar.getInstance();
//        WorkCalendar workCalendar = new WorkCalendar(calendar, WorkCalendar.WorkingState.OFF_DUTY);
//        list.add(workCalendar);
//        for(int i=3;i<30;i+=3){
//            Calendar calendar1=Calendar.getInstance();
//            calendar1.set(Calendar.DAY_OF_MONTH,i);
//            switch(i){
//                case 3:
//                    WorkCalendar workCalendar1=new WorkCalendar(calendar1, WorkCalendar.WorkingState.ON_VACATE);
//                    list.add(workCalendar1);
//                    break;
//                case 6:
//                    WorkCalendar workCalendar2=new WorkCalendar(calendar1, WorkCalendar.WorkingState.ON_DUTY);
//                    list.add(workCalendar2);
//                    break;
//                case 12:
//                    WorkCalendar workCalendar3=new WorkCalendar(calendar1, WorkCalendar.WorkingState.ON_BUSINESS);
//                    list.add(workCalendar3);
//                    break;
//                case 18:
//                    WorkCalendar workCalendar4=new WorkCalendar(calendar1, WorkCalendar.WorkingState.ON_GUARD);
//                    list.add(workCalendar4);
//                    break;
//                case 27:
//                    WorkCalendar workCalendar5=new WorkCalendar(calendar1, WorkCalendar.WorkingState.OVERTIME);
//                    list.add(workCalendar5);
//                    break;
//            }
//        }
////        Log.d("info",workCalendar.mCalendar.get(Calendar.DAY_OF_MONTH)+"day of month");
//        simpleMonthView.setStateList(list);
//        //calendar.set(Calendar.MONTH,Calendar.MAY);
//        simpleMonthView.setMontheParams(calendar);
//        simpleMonthView.setOnDaySelectedListener(new SimpleMonthView.OnDaySelectedListener() {
//            @Override
//            public void onDaySelected(int day) {
//                Log.d("info", day + "");
//            }
//        });
//        simpleMonthView.setOnDayLongCLickedListener(new SimpleMonthView.OnDayLongClikedListener() {
//            @Override
//            public void onDayLongClick(int day) {
//                Log.d("info", day + "longClick");
//            }
//        });
    }

}
