package com.oortcloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.joybar.librarycalendar.data.CalendarDate;
import com.joybar.librarycalendar.fragment.CalendarViewFragment;
import com.joybar.librarycalendar.fragment.CalendarViewPagerFragment;
import com.oort.weichat.R;
import com.oort.weichat.ui.message.search.SearchDesignationContent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @Company: 奥尔特云（深圳）智慧科技有限公司
 * @Author: lukezhang
 * @Date: 2022/11/2 12:23
 */
public class MessageCalendarActivity extends AppCompatActivity implements
                CalendarViewPagerFragment.OnPageChangeListener,
                CalendarViewFragment.OnDateClickListener,
                CalendarViewFragment.OnDateCancelListener {

    private TextView tv_date;
    private boolean isChoiceModelSingle = true;
    private List<CalendarDate> mListDate = new ArrayList<>();
    private String mFriendId;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        tv_date = (TextView) findViewById(R.id.tv_date);
        mFriendId = getIntent().getStringExtra("search_objectId");
        initActionBar();
        initFragment();

    }



    private void initFragment(){
//
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction tx = fm.beginTransaction();
//        // Fragment fragment = new CalendarViewPagerFragment();
//        Fragment fragment = CalendarViewPagerFragment.newInstance(isChoiceModelSingle);
//        tx.replace(R.id.fl_content, fragment);
//        tx.commit();

    }

    @Override
    public void onDateCancel(CalendarDate calendarDate) {
        int count = mListDate.size();
        for (int i = 0; i < count; i++) {
            CalendarDate date = mListDate.get(i);
            if (date.getSolar().solarDay == calendarDate.getSolar().solarDay) {
                mListDate.remove(i);
                break;
            }
        }
        tv_date.setText(listToString(mListDate));

    }


    @Override
    public void onDateClick(CalendarDate calendarDate) {
        int year = calendarDate.getSolar().solarYear;
        int month = calendarDate.getSolar().solarMonth;
        int day = calendarDate.getSolar().solarDay;
        if (isChoiceModelSingle) {
            tv_date.setText(year + "-" + month + "-" + day);
        } else {
            //System.out.println(calendarDate.getSolar().solarDay);
            mListDate.add(calendarDate);
            tv_date.setText(listToString(mListDate));
        }

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(year,month-1,day,0,0,0);
        double time = calendar.getTimeInMillis() / 1000;
        Log.d("Date:", String.valueOf(time));
        if (!TextUtils.isEmpty(mFriendId)) {
            Intent intent = new Intent(this, SearchDesignationContent.class);
            intent.putExtra("search_objectId", mFriendId);
            intent.putExtra("search_type", SearchDesignationContent.TYPE_DATE);
            intent.putExtra("search_date", time);
            startActivity(intent);
        }

    }

    private static String listToString(List<CalendarDate> list) {
        StringBuffer stringBuffer = new StringBuffer();
        for (CalendarDate date : list) {
            stringBuffer.append(date.getSolar().solarYear + "-" + date.getSolar().solarMonth + "-" + date.getSolar().solarDay).append(" ");
        }
        return stringBuffer.toString();
    }
    @Override
    public void onPageChange(int year, int month) {
        tv_date.setText(year + "-" + month);
        mListDate.clear();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);

        tvTitle.setText("按日期查找");
    }
}
