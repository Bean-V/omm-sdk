package com.oortcloud.coo.cases;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oort.weichat.R;

import java.util.Calendar;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    // UI组件
    private ImageView ivBack;
    private EditText etIncidentNumber;
    private EditText etReporterName;
    private EditText etReporterPhone;
    private EditText etHandlingUnit;
    private LinearLayout llStatusSelector;
    private TextView tvStatus;
    private LinearLayout llLevelSelector;
    private TextView tvLevel;
    private EditText etStartTime;
    private EditText etEndTime;
    private Button btnReset;
    private Button btnFilter;

    // 数据
    private String[] statusOptions = {"全部", "待处理", "处理中", "已完成", "已关闭"};
    private String[] levelOptions = {"全部", "一级", "二级", "三级", "四级"};
    private int selectedStatusIndex = 0;
    private int selectedLevelIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        etIncidentNumber = findViewById(R.id.et_incident_number);
        etReporterName = findViewById(R.id.et_reporter_name);
        etReporterPhone = findViewById(R.id.et_reporter_phone);
        etHandlingUnit = findViewById(R.id.et_handling_unit);
        llStatusSelector = findViewById(R.id.ll_status_selector);
        tvStatus = findViewById(R.id.tv_status);
        llLevelSelector = findViewById(R.id.ll_level_selector);
        tvLevel = findViewById(R.id.tv_level);
        etStartTime = findViewById(R.id.et_start_time);
        etEndTime = findViewById(R.id.et_end_time);
        btnReset = findViewById(R.id.btn_reset);
        btnFilter = findViewById(R.id.btn_filter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(this);
        llStatusSelector.setOnClickListener(this);
        llLevelSelector.setOnClickListener(this);
        etStartTime.setOnClickListener(this);
        etEndTime.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.ll_status_selector) {
            showStatusSelector();
        } else if (id == R.id.ll_level_selector) {
            showLevelSelector();
        } else if (id == R.id.et_start_time) {
            showDatePicker(etStartTime);
        } else if (id == R.id.et_end_time) {
            showDatePicker(etEndTime);
        } else if (id == R.id.btn_reset) {
            resetForm();
        } else if (id == R.id.btn_filter) {
            performSearch();
        }
    }

    private void showStatusSelector() {
        // 这里可以显示一个选择对话框或底部弹窗
        // 为了简化，这里使用简单的循环选择
        selectedStatusIndex = (selectedStatusIndex + 1) % statusOptions.length;
        tvStatus.setText(statusOptions[selectedStatusIndex]);
    }

    private void showLevelSelector() {
        // 这里可以显示一个选择对话框或底部弹窗
        // 为了简化，这里使用简单的循环选择
        selectedLevelIndex = (selectedLevelIndex + 1) % levelOptions.length;
        tvLevel.setText(levelOptions[selectedLevelIndex]);
    }

    private void showDatePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void resetForm() {
        etIncidentNumber.setText("");
        etReporterName.setText("");
        etReporterPhone.setText("");
        etHandlingUnit.setText("");
        tvStatus.setText("全部");
        tvLevel.setText("全部");
        etStartTime.setText("");
        etEndTime.setText("");
        selectedStatusIndex = 0;
        selectedLevelIndex = 0;
    }

    private void performSearch() {
        // 获取搜索条件
        String incidentNumber = etIncidentNumber.getText().toString().trim();
        String reporterName = etReporterName.getText().toString().trim();
        String reporterPhone = etReporterPhone.getText().toString().trim();
        String handlingUnit = etHandlingUnit.getText().toString().trim();
        String status = tvStatus.getText().toString();
        String level = tvLevel.getText().toString();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        // 验证搜索条件
        if (TextUtils.isEmpty(incidentNumber) && 
            TextUtils.isEmpty(reporterName) && 
            TextUtils.isEmpty(reporterPhone) && 
            TextUtils.isEmpty(handlingUnit) && 
            "全部".equals(status) && 
            "全部".equals(level) && 
            TextUtils.isEmpty(startTime) && 
            TextUtils.isEmpty(endTime)) {
            Toast.makeText(this, "请至少输入一个搜索条件", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建搜索条件对象
        SearchCriteria criteria = new SearchCriteria();
        criteria.setIncidentNumber(incidentNumber);
        criteria.setReporterName(reporterName);
        criteria.setReporterPhone(reporterPhone);
        criteria.setHandlingUnit(handlingUnit);
        criteria.setStatus(status);
        criteria.setLevel(level);
        criteria.setStartTime(startTime);
        criteria.setEndTime(endTime);

        // 返回搜索结果
        Intent resultIntent = new Intent();
        resultIntent.putExtra("search_criteria", criteria);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // 搜索条件数据类
    public static class SearchCriteria implements java.io.Serializable {
        private String incidentNumber;
        private String reporterName;
        private String reporterPhone;
        private String handlingUnit;
        private String status;
        private String level;
        private String startTime;
        private String endTime;

        // Getters and Setters
        public String getIncidentNumber() { return incidentNumber; }
        public void setIncidentNumber(String incidentNumber) { this.incidentNumber = incidentNumber; }

        public String getReporterName() { return reporterName; }
        public void setReporterName(String reporterName) { this.reporterName = reporterName; }

        public String getReporterPhone() { return reporterPhone; }
        public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

        public String getHandlingUnit() { return handlingUnit; }
        public void setHandlingUnit(String handlingUnit) { this.handlingUnit = handlingUnit; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }
}
