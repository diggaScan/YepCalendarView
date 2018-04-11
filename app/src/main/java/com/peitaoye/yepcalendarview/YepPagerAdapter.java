package com.peitaoye.yepcalendarview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

/**
 * Created by PeitaoYe on 2018/4/10.
 */

public class YepPagerAdapter extends PagerAdapter {

    private final int MONTHS_A_YEAR = 12;
    private Calendar minDate;
    private Calendar maxDate;

    private ViewGroup.LayoutParams lp;

    private Context mContext;

    private int diffMonths;

    private SimpleMonthView.OnDayLongClikedListener mOnDayLongClikedListener;
    private SimpleMonthView.OnDaySelectedListener mOnDaySelectedListener;
    private SimpleMonthView.OnClickUnableDay mOnClickUnableDay;

    public YepPagerAdapter(Context context, Calendar minDate, Calendar maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
        diffMonths = getDiffMonths(minDate, maxDate);
        this.mContext = context;
        lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        SimpleMonthView singleView = new SimpleMonthView(mContext);
        int year = getYearByPosition(position);
        int month = getMonthByPosition(position);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        singleView.setMontheParams(calendar);
        //singleView.设置数据;
        singleView.setOnDayLongCLickedListener(mOnDayLongClikedListener);
        singleView.setOnDaySelectedListener(mOnDaySelectedListener);
        singleView.setOnClickUnableDay(mOnClickUnableDay);
        singleView.setLayoutParams(lp);
//        singleView.setFocusable(true);
//        singleView.requestFocus();
        container.addView(singleView);
        return singleView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        SimpleMonthView simpleMonthView = (SimpleMonthView) object;
        container.removeView(simpleMonthView);

    }

    @Override
    public int getCount() {
        return diffMonths;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (SimpleMonthView) object;
    }

    private int getDiffMonths(Calendar minDate, Calendar maxDate) {
        int diffYears = maxDate.get(Calendar.YEAR) - minDate.get(Calendar.YEAR);
        int diffMonths = diffYears * 12 + maxDate.get(Calendar.MONTH) - minDate.get(Calendar.MONTH) + 1;
        if (isValidDiffMonths(diffMonths)) {
            return diffMonths;
        } else {
            throw new IllegalArgumentException("minDate should not bigger than maxDate");
        }
    }

    private boolean isValidDiffMonths(int diffMonths) {
        return diffMonths >= 0 ? true : false;
    }

    protected void setOnDaySelectedListener(SimpleMonthView.OnDaySelectedListener onDaySelectedListener) {
        this.mOnDaySelectedListener = onDaySelectedListener;
    }

    protected void setOnDayLongClikedListener(SimpleMonthView.OnDayLongClikedListener onDayLongClikedListener) {
        this.mOnDayLongClikedListener = onDayLongClikedListener;
    }
    protected void setOnClickUnableDay(SimpleMonthView.OnClickUnableDay onClickUnableDay){
        this.mOnClickUnableDay=onClickUnableDay;
    }

    private int getYearByPosition(int position) {
        int minDate_month = minDate.get(Calendar.MONTH) + 1;
        int minDate_year = minDate.get(Calendar.YEAR);
        int years = (minDate_month + position) / MONTHS_A_YEAR;

        return minDate_year + years;
    }

    private int getMonthByPosition(int position) {
        int minDate_month = minDate.get(Calendar.MONTH) + 1;
        int position_month;
        if ((minDate_month + position) / MONTHS_A_YEAR <= 0) {
            position_month = minDate_month + position;
        } else {
            position_month = (minDate_month + position) % MONTHS_A_YEAR;
        }
        return position_month - 1;
    }

    //set up dataSet, the type of data is still under considering
//    public void setDataSet(){
//
//    }
}
