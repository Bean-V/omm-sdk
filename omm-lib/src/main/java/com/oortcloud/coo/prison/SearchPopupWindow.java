package com.oortcloud.coo.prison;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.oort.weichat.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchPopupWindow extends PopupWindow implements View.OnClickListener {

    private Activity activity;
    private View popupView;
    
    // UI组件
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

    // 回调接口
    public interface OnSearchListener {
        void onSearch(SearchCriteria criteria);
        void onReset();
    }
    
    private OnSearchListener searchListener;

    public SearchPopupWindow(Activity activity) {
        this.activity = activity;
        initView();
        setupClickListeners();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_search_prison, null);
        
        // 设置PopupWindow
        setContentView(popupView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        // 初始化UI组件
        initViews();
    }

    private void initViews() {
        etIncidentNumber = popupView.findViewById(R.id.et_incident_number);
        etReporterName = popupView.findViewById(R.id.et_reporter_name);
        etReporterPhone = popupView.findViewById(R.id.et_reporter_phone);
        etHandlingUnit = popupView.findViewById(R.id.et_handling_unit);
        llStatusSelector = popupView.findViewById(R.id.ll_status_selector);
        tvStatus = popupView.findViewById(R.id.tv_status);
        llLevelSelector = popupView.findViewById(R.id.ll_level_selector);
        tvLevel = popupView.findViewById(R.id.tv_level);
        etStartTime = popupView.findViewById(R.id.et_start_time);
        etEndTime = popupView.findViewById(R.id.et_end_time);
        btnReset = popupView.findViewById(R.id.btn_reset);
        btnFilter = popupView.findViewById(R.id.btn_filter);
    }

    private void setupClickListeners() {
        llStatusSelector.setOnClickListener(this);
        llLevelSelector.setOnClickListener(this);
        etStartTime.setOnClickListener(this);
        etEndTime.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        
        // 添加长按监听器来清除时间
        etStartTime.setOnLongClickListener(v -> {
            etStartTime.setText("");
            android.widget.Toast.makeText(activity, "已清除开始时间", android.widget.Toast.LENGTH_SHORT).show();
            return true;
        });
        
        etEndTime.setOnLongClickListener(v -> {
            etEndTime.setText("");
            android.widget.Toast.makeText(activity, "已清除结束时间", android.widget.Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_status_selector) {
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
        // 创建状态选择对话框
        List<String> statusList = new ArrayList<>();
        for (String status : statusOptions) {
            statusList.add(status);
        }
        
        DropdownSelectionDialog dialog = new DropdownSelectionDialog(
                activity, 
                "选择警情状态", 
                statusList, 
                selectedStatusIndex
        );
        
        dialog.setOnSelectionListener(new DropdownSelectionDialog.OnSelectionListener() {
            @Override
            public void onSelection(int position, String selectedValue) {
                selectedStatusIndex = position;
                tvStatus.setText(selectedValue);
            }
        });
        
        dialog.show();
    }

    private void showLevelSelector() {
        // 创建级别选择对话框
        List<String> levelList = new ArrayList<>();
        for (String level : levelOptions) {
            levelList.add(level);
        }
        
        DropdownSelectionDialog dialog = new DropdownSelectionDialog(
                activity, 
                "选择警情级别", 
                levelList, 
                selectedLevelIndex
        );
        
        dialog.setOnSelectionListener(new DropdownSelectionDialog.OnSelectionListener() {
            @Override
            public void onSelection(int position, String selectedValue) {
                selectedLevelIndex = position;
                tvLevel.setText(selectedValue);
            }
        });
        
        dialog.show();
    }

    private void showDatePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
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
        
        if (searchListener != null) {
            searchListener.onReset();
        }
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
            android.widget.Toast.makeText(activity, "请至少输入一个搜索条件", android.widget.Toast.LENGTH_SHORT).show();
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

        // 回调搜索结果
        if (searchListener != null) {
            searchListener.onSearch(criteria);
        }
        
        // 关闭弹窗
        dismiss();
    }

    public void setOnSearchListener(OnSearchListener listener) {
        this.searchListener = listener;
    }

    public void show(View anchorView) {
        showAsDropDown(anchorView, 0, 0);
    }

    // 搜索条件数据类
    public static class SearchCriteria implements java.io.Serializable {
        private String incidentNumber;  // 接警单编号
        private String reporterName;    // 报警人姓名
        private String reporterPhone;   // 报警人电话
        private String handlingUnit;    // 处警单位
        private String status;          // 警情状态
        private String level;           // 警情级别
        private String startTime;       // 开始时间
        private String endTime;         // 结束时间

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
