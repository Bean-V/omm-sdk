package com.oortcloud.oort_zhifayi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
        import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oortcloud.basemodule.utils.DeviceIdFactory;

import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Locale;

public class ActivityScrren extends ActivityBase {

    private TextView textTime;
    private TextView textDate;
    private TextView textDayOfWeek;
    private Handler handler;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrren);

        textTime = findViewById(R.id.textTime);
        textDate = findViewById(R.id.textDate);
        textDayOfWeek = findViewById(R.id.textDayOfWeek);
        ((TextView) findViewById(R.id.tv_gpsinfo)).setText(
                "deviceId:" + DeviceIdFactory.getSerialNumber() + "\n" +
                ReportInfo.elements + "\n" +
                        String.valueOf(ReportInfo.longitude) + "\n" +
                        String.valueOf(ReportInfo.latitude) + "\n" +
                        ReportInfo.gps_nfo+"\n"  +
                        "次数："  + String.valueOf(ReportInfo.postCount) + "\n" +
                "gpsUpdate次数："  + String.valueOf(ReportInfo.updateDataCount) + "\n" +
                "gps："  + String.valueOf(ReportInfo.gpsData));
        handler = new Handler();
        handler.post(updateTimeRunnable);


        Button btnCopy = findViewById(R.id.btn_copy);

        // 为按钮设置点击事件监听器
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取要复制的文本
                String copiedText = DeviceIdFactory.getSerialNumber();

                // 获取剪贴板管理器
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建 ClipData 对象
                ClipData clip = ClipData.newPlainText("Copied Text", copiedText);
                // 将 ClipData 对象设置到剪贴板
                clipboard.setPrimaryClip(clip);

                // 显示复制成功的提示信息
                Toast.makeText(ActivityScrren.this, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });


    }

    int time = 0;
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateDateTime();
            handler.postDelayed(this, 1000);
            time ++ ;
            if(time == 3){
                finish();
            }
        }
    };

    private void updateDateTime() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);

        textTime.setText(timeFormat.format(currentTime));
        textDate.setText(dateFormat.format(currentTime));
        textDayOfWeek.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeRunnable);
    }
}
