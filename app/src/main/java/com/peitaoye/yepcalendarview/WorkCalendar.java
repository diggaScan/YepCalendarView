package com.peitaoye.yepcalendarview;

import java.util.Calendar;

/**
 * Created by PeitaoYe on 2018/4/10.
 */

public class WorkCalendar {
    public Calendar mCalendar;
    public int mState;

    public WorkCalendar(Calendar mCalendar, int mState) {
        this.mCalendar = mCalendar;
        this.mState = mState;
    }
//    // get how many days this month has
//    private int getDaysInMonth(int year, int month) {
//        switch (month) {
//            case Calendar.JANUARY:
//            case Calendar.MARCH:
//            case Calendar.MAY:
//            case Calendar.JULY:
//            case Calendar.AUGUST:
//            case Calendar.OCTOBER:
//            case Calendar.DECEMBER:
//                return 31;
//            case Calendar.APRIL:
//            case Calendar.JUNE:
//            case Calendar.SEPTEMBER:
//            case Calendar.NOVEMBER:
//                return 30;
//            case Calendar.FEBRUARY:
//                return (year % 4 == 0) ? 29 : 28;
//            default:
//                throw new IllegalArgumentException("invalid month params");
//        }
//    }

    public static class WorkingState {
        public static final int OFF_DUTY = 1;
        public static final int ON_DUTY = 2;
        public static final int ON_BUSINESS = 3;
        public static final int OVERTIME = 4;
        public static final int ON_VACATE = 5;
        public static final int ON_GUARD = 6;
    }
}
