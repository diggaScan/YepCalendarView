package com.peitaoye.yepcalendarview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.Calendar;

/**
 * Created by PeitaoYe on 2018/4/10.
 */

public class YepMonthView extends ViewGroup {

    private Context mContext;
    private ViewPager mViewPager;
    private YepPagerAdapter mAdapter;

    private int current_position;
    //max and min date
    private Calendar minDate;
    private Calendar maxDate;

    public YepMonthView(Context context) {
        this(context, null);
    }

    public YepMonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YepMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        mViewPager = new ViewPager(mContext);
        ViewGroup.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(lp);
        mViewPager.setFocusable(false);

    }

    public void setMinAndMaxDate(Calendar minDate, Calendar maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new YepPagerAdapter(mContext, minDate, maxDate);
//        mAdapter.setDataSet();
        mAdapter.setOnClickUnableDay(onClickUnableDay);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        Calendar current_cal = Calendar.getInstance();
        int start_month = getDiffMonths(minDate, current_cal);
        current_position=start_month-1;
        mViewPager.setCurrentItem(current_position);
        addView(mViewPager);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mViewPager.layout(0, 0, r - l, b - t);

    }
    public void setCurrentPosition(int position){
        this.current_position=position;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mViewPager, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
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

    public void setOnDaySelectedListener(SimpleMonthView.OnDaySelectedListener onDaySelectedListener) {
        if (mAdapter != null) {
            mAdapter.setOnDaySelectedListener(onDaySelectedListener);
        } else {
            throw new IllegalArgumentException("please declare setMinAndMaxDate() first");
        }
    }

    public void setOnDayLongClikedListener(SimpleMonthView.OnDayLongClikedListener onDayLongClikedListener) {
        if (mAdapter != null) {
            mAdapter.setOnDayLongClikedListener(onDayLongClikedListener);
        } else {
            throw new IllegalArgumentException("please declare setMinAndMaxDate() first");
        }
    }
    private SimpleMonthView.OnClickUnableDay onClickUnableDay=new SimpleMonthView.OnClickUnableDay() {
        @Override
        public void clickUnableDay(int day) {
//            Log.d("info","previousPosition"+current_position);
            if(day<=0){
                current_position-=1;
                mViewPager.setCurrentItem(current_position);
//                Log.d("info","nowPosition"+current_position);
            }else{
                current_position+=1;
                mViewPager.setCurrentItem(current_position);
//                Log.d("info","nowPosition"+current_position);
            }

        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            current_position=position;

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    //set up dataSet, type of data is still under considering
//    public void setDataSet(){
//
//    }
}
