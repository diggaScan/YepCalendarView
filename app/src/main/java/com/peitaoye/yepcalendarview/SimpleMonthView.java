package com.peitaoye.yepcalendarview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by PeitaoYe on 2018/4/8.
 */

public class SimpleMonthView extends View {

    private final int DAYS_OF_A_WEEK = 7;
    private final int MAX_WEEK_COLS = 6;
    private final Resources mRes;
    private final Context mContext;
    private int longClickedDay;

    //info about drawing unable days
    private int day_rows = 1;
    private int forClickUnableRow = 0;
    private int startOffset;
    private int endOffset;
    private TextPaint unablePaint = new TextPaint();
    private int unable_day_color = 0xa9959691;
    //use the format like Calendar.Sunday to deliver a specific day will be preferred.
    private int days_of_month;
    private int mYear;
    private int mMonth;
    private int mFirstDayofWeek = -1;
    private int mToday = -1;
    private int startWeekDayOfMonth;
    //tiny day of week label
    private String[] tinyWeekLabel;

    //dimens as laid out
    private int mDesiredDayOfWeekHeight;
    private int mDesiredDayHeight;
    private int mDesiredCellWidth;

    //real dimens as laid out
    private float mDayOfWeekHeight;
    private float mDayHeight;
    private float mCellWidth;
    private float mSelectRadius;

    private int mPaddedWidth;
    private int mPaddedHeight;
    //initiate paint
    private TextPaint mDaysOfWeekPaint = new TextPaint();
    private TextPaint mDayPaint = new TextPaint();

    //field info of every single day's state
    private Paint mDayStatePaint = new Paint();
    private float mDayStateCircleRadius;
    private int off_duty_color = 0xff51c941;
    private int on_duty_color = 0xff7a2bb6;
    private int on_guard_color = 0xffca552a;
    private int on_vacate_color = 0xff6b666a;
    private int on_business_color = 0xff454fc4;
    private int overtime_color = 0xffdad664;
    private TextPaint mStateLabelPaint = new TextPaint();
    private float label_half_line_height;
    //color of current day
    private int current_day_color = 0xFFFF4081;
    private NumberFormat mNumberFormat;

    private int mActivatedDay = -1;
    private int mActived_circle_color = 0x378b8889;
    private Paint activeCirclePaint = new Paint();

    private int mHighlightedDay = -1;
    private int mHightlighted_circle_color = 0xc6c91c56;
    private Paint mHighlighedCirclePaint = new Paint();

    private float mHalflineHeight;

    private OnDaySelectedListener onDaySelectedListener;
    private OnDayLongClikedListener onDayLongClikedListener;
    private OnClickUnableDay onClickUnableDay;
    //List stores the state of days
    private List<WorkCalendar> mStateList;

    private boolean isLongCliked = false;
    private Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (onDayLongClikedListener != null) {
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(45);
                }
                onDayLongClikedListener.onDayLongClick(longClickedDay);
            }
            isLongCliked = true;
        }
    };

    public SimpleMonthView(Context context) {
        this(context, null);
    }

    public SimpleMonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setFocusableInTouchMode(true);
//        setFocusable(FOCUSABLE);
        mRes = context.getResources();
        tinyWeekLabel = mRes.getStringArray(R.array.tiny_week_label_sun);

        mDesiredDayOfWeekHeight = mRes.getDimensionPixelSize(R.dimen.day_of_week_height);
        mDesiredDayHeight = mRes.getDimensionPixelSize(R.dimen.day_height);
        mDesiredCellWidth = mRes.getDimensionPixelSize(R.dimen.cell_width);

        mNumberFormat = NumberFormat.getIntegerInstance();


        initPaint(mRes);
    }

    /**
     * initiate the Paint tool
     *
     * @param res
     */
    private void initPaint(Resources res) {
        String day_of_week_typeface = res.getString(R.string.day_of_week_typeface);
        String day_typeface = res.getString(R.string.day_typeface);

        int day_of_week_text_size = res.getDimensionPixelSize(R.dimen.day_of_week_text_size);
        int day_text_size = res.getDimensionPixelSize(R.dimen.day_text_size);
        int state_label_size = res.getDimensionPixelSize(R.dimen.state_label_size);
        mDaysOfWeekPaint.setAntiAlias(true);
        mDaysOfWeekPaint.setTypeface(Typeface.create(day_of_week_typeface, Typeface.NORMAL));
        mDaysOfWeekPaint.setTextAlign(Paint.Align.CENTER);
        mDaysOfWeekPaint.setTextSize(day_of_week_text_size);
        mDaysOfWeekPaint.setStyle(Paint.Style.FILL);

        mDayPaint.setTextSize(day_text_size);
        mDayPaint.setTypeface(Typeface.create(day_typeface, Typeface.NORMAL));
        mDayPaint.setTextAlign(Paint.Align.CENTER);
        mDayPaint.setStyle(Paint.Style.FILL);
        mDayPaint.setAntiAlias(true);

        activeCirclePaint.setColor(mActived_circle_color);
        activeCirclePaint.setAntiAlias(true);
        activeCirclePaint.setStyle(Paint.Style.FILL);

        mHighlighedCirclePaint.setColor(mHightlighted_circle_color);
        mHighlighedCirclePaint.setStyle(Paint.Style.FILL);
        mHighlighedCirclePaint.setAntiAlias(true);

        mDayStatePaint.setColor(mActived_circle_color);
        mDayStatePaint.setAntiAlias(true);
        mDayStatePaint.setStyle(Paint.Style.FILL);

        mStateLabelPaint.setColor(Color.WHITE);
        mStateLabelPaint.setAntiAlias(true);
        mStateLabelPaint.setStyle(Paint.Style.FILL);
        mStateLabelPaint.setTypeface(Typeface.create(day_typeface, Typeface.NORMAL));
        mStateLabelPaint.setTextAlign(Paint.Align.CENTER);
        mStateLabelPaint.setTextSize(state_label_size);

        unablePaint.setStyle(Paint.Style.FILL);
        unablePaint.setTypeface(Typeface.create(day_typeface, Typeface.NORMAL));
        unablePaint.setTextSize(day_text_size);
        unablePaint.setTextAlign(Paint.Align.CENTER);
        unablePaint.setAntiAlias(true);
        unablePaint.setColor(unable_day_color);

        //get the half line height of state label
        label_half_line_height = (mStateLabelPaint.ascent() + mStateLabelPaint.descent()) / 2;
    }

    /**
     * initiate month params
     *
     * @param calendar
     */
    public void setMontheParams(Calendar calendar) {
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);

        //get a valid day of the first day of the week
        if (isFirstDayValid(Calendar.MONDAY)) {
            mFirstDayofWeek = Calendar.MONDAY;
            initWeekTinyLabel(mFirstDayofWeek);
        } else {
            mFirstDayofWeek = -1;
        }

        days_of_month = getDaysInMonth(mYear, mMonth);

        //get the day of week for the first day of the month.
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startWeekDayOfMonth = mCalendar.get(Calendar.DAY_OF_WEEK);

        //get the current day if it does exist
        Calendar current_cal = Calendar.getInstance();
        if (isCurrentDate(mCalendar, current_cal)) {
            mToday = current_cal.get(Calendar.DAY_OF_MONTH);
        }


    }

    private boolean isFirstDayValid(@Nullable int firstDayofWeek) {
        return firstDayofWeek >= Calendar.SUNDAY && firstDayofWeek <= Calendar.MONDAY;
    }

    // get how many days this month has
    private int getDaysInMonth(int year, int month) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return (year % 4 == 0) ? 29 : 28;
            default:
                throw new IllegalArgumentException("invalid month params");
        }
    }

    //initiate the week tiny label basing on the param, firstDayofWeek.
    private void initWeekTinyLabel(int firstDayOfWeek) {
        switch (firstDayOfWeek) {
            case Calendar.SUNDAY:
                return;
            case Calendar.MONDAY:
                tinyWeekLabel = mRes.getStringArray(R.array.tiny_week_label_sat);

        }
    }

    //determine whether the date of today exist in the month the view is showing
    private boolean isCurrentDate(Calendar showingMonth, Calendar current_month) {
        return showingMonth.get(Calendar.YEAR) == current_month.get(Calendar.YEAR) &&
                showingMonth.get(Calendar.MONTH) == current_month.get(Calendar.MONTH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int overallHeight = mDesiredDayOfWeekHeight + mDesiredDayHeight * MAX_WEEK_COLS + getPaddingTop()
                + getPaddingBottom();
        int overallWidth = mDesiredCellWidth * DAYS_OF_A_WEEK + getPaddingLeft() + getPaddingRight();
        int resolvedHeight = resolveSize(overallHeight, heightMeasureSpec);
        int resolvedWidth = resolveSize(overallWidth, widthMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    //basing on the measured size to adjust the size of each cell's height and width
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // super.onLayout(changed, left, top, right, bottom);
        int w = right - left;
        int h = bottom - top;


        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int paddedRight = w - paddingRight;
        int paddedBottom = h - paddingBottom;

        int paddedWidth = paddedRight - paddingLeft;
        int paddedHeight = paddedBottom - paddingTop;

        mPaddedWidth = paddedWidth;
        mPaddedHeight = paddedHeight;


        //get the adjusted month view size
        int mMeasuredHeight = getMeasuredHeight() - paddingBottom - paddingTop;
        float scaleH = paddedHeight / mMeasuredHeight;
        int overallHeight = mDesiredDayOfWeekHeight + mDesiredDayHeight * MAX_WEEK_COLS + getPaddingTop()
                + getPaddingBottom();
//        Log.d("info","desiredHeight"+overallHeight);
//       float scaleH =((float)mMeasuredHeight/(float)overallHeight);
//        Log.d("info","scaleH"+scaleH);
        mDayOfWeekHeight = mDesiredDayOfWeekHeight * scaleH;
        mDayHeight = mDesiredDayHeight * scaleH;
        mCellWidth = paddedWidth / DAYS_OF_A_WEEK;
        mSelectRadius = mCellWidth * 0.28f;
        mDayStateCircleRadius = mCellWidth * 0.13f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        canvas.translate(paddingLeft, paddingTop);

        drawDayOfWeek(canvas);
        drawDays(canvas);
        drawUnableDays(canvas);

        canvas.translate(-paddingLeft, -paddingTop);

    }

    private void drawDayOfWeek(Canvas canvas) {
        TextPaint mPaint = mDaysOfWeekPaint;
        float rowHeight = mDayOfWeekHeight;
        float colWidth = mCellWidth;
        final float lineHeight = mPaint.ascent() + mPaint.descent();

        for (int col = 0; col < DAYS_OF_A_WEEK; col++) {
            String tinyLabel = tinyWeekLabel[col];
            float widthCenter = colWidth * col + colWidth / 2;
            canvas.drawText(tinyLabel, widthCenter, (rowHeight - lineHeight) / 2, mPaint);
        }
    }

    private void drawDays(Canvas canvas) {
        TextPaint mPaint = mDayPaint;
        int daysOfMonthe = days_of_month;
        int current_day = mToday;
        int startDay = startWeekDayOfMonth;
        float dayOfWeekHeight = mDayOfWeekHeight;
        float dayHeight = mDayHeight;
        float colWidth = mCellWidth;

        float rowCenter = dayOfWeekHeight + dayHeight / 2;
        startOffset = getDayOffset(startDay);
        int offsetDays = startOffset;
        final float halfLineHeight = (mPaint.ascent() + mPaint.descent()) / 2;
        mHalflineHeight = halfLineHeight;
        for (int day = 1, col = offsetDays; day <= daysOfMonthe; day++) {

            float w = col * colWidth + colWidth / 2;

            float h = rowCenter - halfLineHeight;

            //get the state of current drawing day
            int drawingDayState = getCurrentDrawingDayState(day);
            if (drawingDayState > 0) {
                int stateCircleX = (int) (w + (mDayStateCircleRadius + mSelectRadius) * 0.75);
                int stateCircleY = (int) (h + (mHalflineHeight - mDayStateCircleRadius - mSelectRadius) * 0.75);
                drawStateCircle(canvas, drawingDayState, stateCircleX, stateCircleY);
            }

            if (day == mActivatedDay) {
                canvas.drawCircle(w, h + mHalflineHeight, mSelectRadius, activeCirclePaint);
            }

            if (day == mHighlightedDay) {
                canvas.drawCircle(w, h + halfLineHeight, mSelectRadius, mHighlighedCirclePaint);
                mPaint.setColor(Color.WHITE);
                canvas.drawText(mNumberFormat.format(day), w, h, mPaint);
                col++;
                if (col == DAYS_OF_A_WEEK) {
                    day_rows += 1;
                    col = 0;
                    rowCenter += dayHeight;
                }
                continue;
            }

            if (current_day == day && mHighlightedDay < 0) {
                mPaint.setColor(Color.WHITE);
                canvas.drawCircle(w, h + halfLineHeight, mSelectRadius, mHighlighedCirclePaint);
            } else if (current_day == day) {
                mPaint.setColor(current_day_color);
            } else {
                mPaint.setColor(Color.BLACK);
            }

            canvas.drawText(mNumberFormat.format(day), w, h, mPaint);

            col++;
            if (mFirstDayofWeek == Calendar.SUNDAY && day == daysOfMonthe && col == 7) {
                day_rows -= 1;
            }
            if (col == DAYS_OF_A_WEEK) {
                day_rows += 1;
                col = 0;
                rowCenter += dayHeight;
            }
        }
    }

    private void drawUnableDays(Canvas canvas) {
        endOffset = getMonthEndOffset(days_of_month);
        int endMonthOffset = endOffset;
        int startMonthOffset = startOffset;
        int rows = day_rows;
        // initiate the value of dat row, otherwise the value will take on infinitely
        forClickUnableRow = day_rows;
        day_rows = 1;
        int previous_month;
        int year = mYear;
        if ((mMonth - 1) < 0) {
            previous_month = Calendar.DECEMBER;
            year = mYear - 1;
        } else {
            previous_month = mMonth - 1;
        }
        int days = getDaysInMonth(year, previous_month);
        float colWidth = mCellWidth;
        for (int i = 0; i < startMonthOffset; i++) {
            int day = days - startMonthOffset + 1 + i;
            canvas.drawText(mNumberFormat.format(day), colWidth * i + colWidth / 2, mDayOfWeekHeight + mDayHeight / 2 - mHalflineHeight, unablePaint);
        }
        if (rows == 6) {
            for (int i = 0; i < endMonthOffset; i++) {
                int day = i + 1;
                canvas.drawText(mNumberFormat.format(day), colWidth * (DAYS_OF_A_WEEK - endMonthOffset) + colWidth / 2 + colWidth * i, mDayOfWeekHeight + mDayHeight / 2 + 5 * mDayHeight - mHalflineHeight, unablePaint);
            }
        } else {
            for (int i = 0; i < endMonthOffset; i++) {
                int day = i + 1;
                canvas.drawText(mNumberFormat.format(day), colWidth * (DAYS_OF_A_WEEK - endMonthOffset) + colWidth / 2 + colWidth * i, mDayOfWeekHeight + mDayHeight / 2 + 4 * mDayHeight - mHalflineHeight, unablePaint);
            }
            for (int i = 0; i < DAYS_OF_A_WEEK; i++) {
                int day = endMonthOffset + 1 + i;
                canvas.drawText(mNumberFormat.format(day), colWidth / 2 + colWidth * i, mDayOfWeekHeight + mDayHeight / 2 + 5 * mDayHeight - mHalflineHeight, unablePaint);
            }
        }

        //Deal with the exception when start day is Calendar.SUNDAY.
//        if (mFirstDayofWeek == Calendar.SUNDAY && endMonthOffset == 0 && rows == 6) {
//            for (int i = 0; i < DAYS_OF_A_WEEK; i++) {
//                int day = 1 + i;
//                canvas.drawText(mNumberFormat.format(day), colWidth / 2 + colWidth * i, mDayOfWeekHeight + mDayHeight / 2 + 5 * mDayHeight - mHalflineHeight, unablePaint);
//            }
//        }

    }

    private int getCurrentDrawingDayState(int drawingDay) {
        int drawingDayState = -1;
        if (mStateList != null) {
            for (WorkCalendar workCalendar : mStateList) {
                if (drawingDay == workCalendar.mCalendar.get(Calendar.DAY_OF_MONTH)) {
                    drawingDayState = workCalendar.mState;
                }
            }
        }
        return drawingDayState;
    }

// --Commented out by Inspection START (2018/4/10 11:14):
//    /**
//     * count How many rows current month has.
//     *
//     * @return num of rows
//     */
//    private int getCurrentMonthRows() {
//        int startDayOfMonth = startWeekDayOfMonth;
//        int startDayOfWeek = mFirstDayofWeek;
//        int offset = getDayOffset(startDayOfMonth);
//        int row = 1;
//        for (int day = 1, col = offset; day < days_of_month; day++) {
//            col++;
//            if (col == DAYS_OF_A_WEEK) {
//                col = 0;
//                row++;
//            }
//        }
//        return row;
//    }
// --Commented out by Inspection STOP (2018/4/10 11:14)

    private void drawStateCircle(Canvas canvas, int state, int x, int y) {
        switch (state) {
            case WorkCalendar.WorkingState.OFF_DUTY:
                mDayStatePaint.setColor(off_duty_color);
                canvas.drawCircle(x, y, mDayStateCircleRadius, mDayStatePaint);
                canvas.drawText("休", x, y - label_half_line_height, mStateLabelPaint);
                Log.d("Off_duty", "state");
                break;
            case WorkCalendar.WorkingState.ON_BUSINESS:
                mDayStatePaint.setColor(on_business_color);
                canvas.drawCircle(x, y, mDayStateCircleRadius, mDayStatePaint);
                canvas.drawText("差", x, y - label_half_line_height, mStateLabelPaint);
                break;
            case WorkCalendar.WorkingState.ON_DUTY:
                mDayStatePaint.setColor(on_duty_color);
                canvas.drawCircle(x, y, mDayStateCircleRadius, mDayStatePaint);
                canvas.drawText("勤", x, y - label_half_line_height, mStateLabelPaint);
                break;
            case WorkCalendar.WorkingState.ON_GUARD:
                mDayStatePaint.setColor(on_guard_color);
                canvas.drawCircle(x, y, mDayStateCircleRadius, mDayStatePaint);
                canvas.drawText("值", x, y - label_half_line_height, mStateLabelPaint);
                break;
            case WorkCalendar.WorkingState.ON_VACATE:
                mDayStatePaint.setColor(on_vacate_color);
                canvas.drawCircle(x, y, mDayStateCircleRadius, mDayStatePaint);
                canvas.drawText("假", x, y - label_half_line_height, mStateLabelPaint);
                break;
            case WorkCalendar.WorkingState.OVERTIME:
                mDayStatePaint.setColor(overtime_color);
                canvas.drawCircle(x, y, mDayStateCircleRadius, mDayStatePaint);
                canvas.drawText("加", x, y - label_half_line_height, mStateLabelPaint);
                break;

        }
    }

    /**
     * get offset day of first day in a month
     *
     * @param startDay
     * @return
     */
    private int getDayOffset(int startDay) {
        switch (mFirstDayofWeek) {
            case Calendar.SUNDAY:
                return startDay - mFirstDayofWeek;
            case Calendar.MONDAY:
                int offset = startDay - mFirstDayofWeek;
                return offset >= 0 ? offset : offset + DAYS_OF_A_WEEK;
            default:
                throw new IllegalArgumentException("invalid start day for a week");
        }
    }

    private int getMonthEndOffset(int endDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.MONTH, mMonth);
        calendar.set(Calendar.DAY_OF_MONTH, endDay);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        switch (mFirstDayofWeek) {
            case Calendar.SUNDAY:
                return -(day_of_week - Calendar.SATURDAY);
            case Calendar.MONDAY:
                return -(day_of_week - Calendar.SATURDAY) + 1;
            default:
                throw new IllegalArgumentException("invalid start day for a week");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX() + 0.5f;
        final float y = event.getY() + (float) (mHalflineHeight * 0.3);
        int up_day = -1;
        int hover_day = -1;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                longClickedDay = getDayByLocation(x, y);
                if (isEnableDays(longClickedDay)) {
                    postDelayed(longClickRunnable, 500);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //  removeCallbacks(longClickRunnable);
                hover_day = getDayByLocation(x, y);
                if (longClickedDay != hover_day) {
                    removeCallbacks(longClickRunnable);
                }
                if (hover_day < 0) {
                    return false;
                }
                if (mActivatedDay != hover_day) {
                    mActivatedDay = hover_day;
                    invalidate(getDayBound(hover_day));
                }
                break;
            case MotionEvent.ACTION_UP:
                removeCallbacks(longClickRunnable);
                up_day = getDayByLocation(x, y);
                if (isUnableDays(longClickedDay) && longClickedDay == up_day) {
                    onClickUnableDay.clickUnableDay(longClickedDay);
                }
                if (!isEnableDays(up_day)) {
                    mActivatedDay = -1;
                    invalidate(getDayBound(hover_day));
                    return true;
                }
                if (mHighlightedDay != up_day) {
                    mActivatedDay = -1;
                    mHighlightedDay = up_day;
                    //cancel the click event if long Clicked event has been triggered.
                    if (!isLongCliked) {
                        if (onDaySelectedListener != null) {
                            onDaySelectedListener.onDaySelected(up_day);
                        }
                    }
                    isLongCliked = false;
                    invalidate(getDayBound(up_day));
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                removeCallbacks(longClickRunnable);
                mActivatedDay = -1;
//              mHighlightedDay=-1;
                invalidate();
                break;
        }

        return true;
    }

    /**
     * get the corresponding day by position (x,y)
     *
     * @param x
     * @param y
     * @return
     */
    private int getDayByLocation(float x, float y) {
        float paddedX = x - getPaddingLeft();
        if (paddedX < 0 || paddedX > mPaddedWidth) {
            return -1;
        }
        float paddedY = y - getPaddingTop();
        if (paddedY < 0 || paddedY > mPaddedHeight) {
            return -1;
        }
        int row = (int) ((paddedY - mDayOfWeekHeight) / mDayHeight);
        int col = (int) ((paddedX * DAYS_OF_A_WEEK) / mPaddedWidth);
        int index = row * DAYS_OF_A_WEEK + col + 1 - getDayOffset(startWeekDayOfMonth);
//        if (isValidDayInMonth(index)) {
//            return index;
//        } else {
//            return -1;
//        }
        return index;
    }


    private Rect getDayBound(int day) {
        Rect rect = new Rect();
        int index = day + getDayOffset(startWeekDayOfMonth);

        // get the col number and row number of the day position
        int col = index % 7 - 1;
        int row = index / 7;
        int left = (int) (col * mCellWidth);
        int top = (int) (mDayOfWeekHeight + row * mDayHeight);
        int right = (int) ((col + 1) * mCellWidth);
        int bottom = (int) (mDayOfWeekHeight + (row + 1) * mDayHeight);
        rect.set(left, top, right, bottom);
        return rect;


    }

    private boolean isValidDayInMonth(int index) {
        return index >= 1 && index <= days_of_month;
    }

    protected void setOnDaySelectedListener(OnDaySelectedListener onDaySelectedListener) {
        this.onDaySelectedListener = onDaySelectedListener;
    }

    protected void setOnDayLongCLickedListener(OnDayLongClikedListener onDayLongCLickedListener) {
        this.onDayLongClikedListener = onDayLongCLickedListener;
    }

    protected void setOnClickUnableDay(OnClickUnableDay onClickUnableDay) {
        this.onClickUnableDay = onClickUnableDay;
    }

    public void setStateList(List<WorkCalendar> mStateList) {
        this.mStateList = mStateList;
    }

    private boolean isEnableDays(int day) {
        return day >= 1 && day <= days_of_month;
    }

    private boolean isUnableDays(int day) {

        if (forClickUnableRow == 5) {
            boolean small = day >= (-startOffset + 1) && day <= 0;
            boolean large = day > days_of_month && day <= days_of_month + endOffset + DAYS_OF_A_WEEK;
            return small || large;
        } else {
            boolean small = day >= (-startOffset + 1) && day <= 0;
            boolean large = day > days_of_month && day <= days_of_month + endOffset;
            return small || large;
        }

    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if (mHighlightedDay > 0) {
            mHighlightedDay = mToday;
            invalidate(getDayBound(mHighlightedDay));
        }
    }

    //    public void setOnFocusChangeListener(OnFocusChangeListener l) {
//        super.setOnFocusChangeListener(l);
//    }
    public interface OnDaySelectedListener {
        void onDaySelected(int day);
    }

    public interface OnDayLongClikedListener {
        void onDayLongClick(int day);
    }

    public interface OnClickUnableDay {
        void clickUnableDay(int day);
    }
}
