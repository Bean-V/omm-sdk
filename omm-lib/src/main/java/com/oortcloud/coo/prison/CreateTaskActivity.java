package com.oortcloud.coo.prison;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jun.baselibrary.http.HttpUtils;
import com.jun.framelibrary.http.callback.HttpEngineCallBack;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.R;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CreateTaskActivity extends BaseActivity implements View.OnClickListener {

    // UI组件
    private ImageView ivBack;
    private EditText etIncidentId;
    private TextView tvSeverityLevel;
    private EditText etIncidentCategory;
    private EditText etIncidentType;
    private EditText etIncidentSubcategory;
    private EditText etIncidentMinorCategory;
    private TextView tvIncidentStatus;
    private EditText etReceptionTime;
    private EditText etReporterName;
    private EditText etReporterPhone;
    private EditText etIncidentLocation;
    private EditText etIncidentContent;
    private EditText etOfficerId;
    private EditText etLevel3Unit;
    private EditText etLevel2Unit;
    private EditText etLevel3LinkageUnit;
    private Button btnCancel;
    private Button btnCreate;
    private TextView title;

    // 数据
    private int severityLevel = 1; // 默认一级
    private int selectedStatusIndex = 0; // 选中的状态索引
    private final String[] statusOptions = {"待处理", "处理中", "已完成", "已取消"};
    private final String[] severityOptions = {"一级", "二级", "三级", "四级"};
    
    // 时间选择相关
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;

    @Override
    public void setStatusBarLight(boolean light) {
        StatusBarUtil.setStatusBarColor(this, R.color.white);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_prison_task);
        
        initViews();
        setupClickListeners();
        initData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        etIncidentId = findViewById(R.id.et_incident_id);
        tvSeverityLevel = findViewById(R.id.tv_severity_level);
        etIncidentCategory = findViewById(R.id.et_incident_category);
        etIncidentType = findViewById(R.id.et_incident_type);
        etIncidentSubcategory = findViewById(R.id.et_incident_subcategory);
        etIncidentMinorCategory = findViewById(R.id.et_incident_minor_category);
        tvIncidentStatus = findViewById(R.id.tv_incident_status);
        etReceptionTime = findViewById(R.id.et_reception_time);
        etReporterName = findViewById(R.id.et_reporter_name);
        etReporterPhone = findViewById(R.id.et_reporter_phone);
        etIncidentLocation = findViewById(R.id.et_incident_location);
        etIncidentContent = findViewById(R.id.et_incident_content);
        etOfficerId = findViewById(R.id.et_officer_id);
        etLevel3Unit = findViewById(R.id.et_level3_unit);
        etLevel2Unit = findViewById(R.id.et_level2_unit);
        etLevel3LinkageUnit = findViewById(R.id.et_level3_linkage_unit);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCreate = findViewById(R.id.btn_create);
        title = findViewById(R.id.title);
        title.setText("创建狱情任务");
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(this);
        tvSeverityLevel.setOnClickListener(this);
        tvIncidentStatus.setOnClickListener(this);
        etReceptionTime.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
    }

    private void initData() {
        // 设置默认级别
        tvSeverityLevel.setText(severityOptions[severityLevel - 1]);
        
        // 设置默认状态
        tvIncidentStatus.setText(statusOptions[selectedStatusIndex]);
        
        // 初始化当前时间
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);
    }

    private void setSeverityLevel(int level) {
        severityLevel = level;
        tvSeverityLevel.setText(severityOptions[level - 1]);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.tv_severity_level) {
            showSeveritySelector();
        } else if (id == R.id.tv_incident_status) {
            showStatusSelector();
        } else if (id == R.id.et_reception_time) {
            showDatePicker();
        } else if (id == R.id.btn_cancel) {
            finish();
        } else if (id == R.id.btn_create) {
            createTask();
        }
    }

    private void showSeveritySelector() {
        // 创建级别选择对话框
        List<String> severityList = new ArrayList<>(Arrays.asList(severityOptions));

        DropdownSelectionDialog dialog = new DropdownSelectionDialog(
                this,
                "选择警情级别",
                severityList,
                severityLevel - 1 // 转换为0基索引
        );

        dialog.setOnSelectionListener(new DropdownSelectionDialog.OnSelectionListener() {
            @Override
            public void onSelection(int position, String selectedValue) {
                severityLevel = position + 1; // 转换为1基索引
                setSeverityLevel(severityLevel);
            }
        });

        dialog.show();
    }

    private void showStatusSelector() {
        // 创建状态选择对话框
        List<String> statusList = new ArrayList<>(Arrays.asList(statusOptions));

        DropdownSelectionDialog dialog = new DropdownSelectionDialog(
                this,
                "选择警情状态",
                statusList,
                selectedStatusIndex
        );

        dialog.setOnSelectionListener(new DropdownSelectionDialog.OnSelectionListener() {
            @Override
            public void onSelection(int position, String selectedValue) {
                selectedStatusIndex = position;
                tvIncidentStatus.setText(selectedValue);
            }
        });

        dialog.show();
    }
    @SuppressLint("DefaultLocale")
    private void showDatePicker() {
        // 先选择日期
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerDialog,
                (view, year, month, day) -> {
                    // 验证日期不能是未来日期
                    if (!validateSelectedDate(year, month, day)) {
                        return;
                    }
                    // 保存选择的日期
                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = day;
                    
                    // 日期选择完成后，显示时间选择器
                    showTimePicker();
                }, selectedYear, selectedMonth, selectedDay);

//        datePickerDialog.setTitle("选择日期");

        
        datePickerDialog.show();
    }
    
    @SuppressLint("DefaultLocale")
    private void showTimePicker() {
        // 选择时间
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.CustomTimePickerDialog,
                (view, hourOfDay, minute) -> {
                    // 验证时间不能是未来时间
                    if (!validateSelectedTime(hourOfDay, minute)) {
                        return;
                    }
                    
                    // 保存选择的时间
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    
                    // 格式化完整的日期时间字符串
                    String dateTime = String.format("%04d-%02d-%02d %02d:%02d:00",
                            selectedYear, selectedMonth + 1, selectedDay, 
                            selectedHour, selectedMinute);
                    
                    etReceptionTime.setText(dateTime);
                }, selectedHour, selectedMinute, true); // true表示24小时制

//        timePickerDialog.setTitle("选择时间");

        timePickerDialog.show();
    }
    
    /**
     * 验证选择的日期不能是未来日期
     */
    private boolean validateSelectedDate(int year, int month, int day) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, day, 0, 0, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);
        
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentCalendar.set(Calendar.MINUTE, 0);
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);
        
        if (selectedCalendar.after(currentCalendar)) {
            Toast.makeText(this, "接警时间不能是未来日期", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 验证日期不能超过30天前
        Calendar thirtyDaysAgo = Calendar.getInstance();
        thirtyDaysAgo.add(Calendar.DAY_OF_MONTH, -30);
        thirtyDaysAgo.set(Calendar.HOUR_OF_DAY, 0);
        thirtyDaysAgo.set(Calendar.MINUTE, 0);
        thirtyDaysAgo.set(Calendar.SECOND, 0);
        thirtyDaysAgo.set(Calendar.MILLISECOND, 0);
        
        if (selectedCalendar.before(thirtyDaysAgo)) {
            Toast.makeText(this, "接警时间不能超过30天前", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证选择的时间不能是未来时间
     */
    private boolean validateSelectedTime(int hour, int minute) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(selectedYear, selectedMonth, selectedDay, hour, minute, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);
        
        Calendar currentCalendar = Calendar.getInstance();
        
        // 如果是今天，则验证时间不能是未来时间
        if (selectedYear == currentCalendar.get(Calendar.YEAR) &&
            selectedMonth == currentCalendar.get(Calendar.MONTH) &&
            selectedDay == currentCalendar.get(Calendar.DAY_OF_MONTH)) {
            
            if (selectedCalendar.after(currentCalendar)) {
                Toast.makeText(this, "接警时间不能是未来时间", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        
        return true;
    }

    private void createTask() {
        // 验证必填字段
        if (!validateForm()) {
            return;
        }

        // 收集任务数据
        TaskData taskData = collectTaskData();

        // 创建任务
        performCreateTask(taskData);
    }

    private boolean validateForm() {
        // 验证必填字段
        if (TextUtils.isEmpty(etIncidentId.getText().toString().trim())) {
            Toast.makeText(this, "请输入警情ID", Toast.LENGTH_SHORT).show();
            etIncidentId.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etIncidentCategory.getText().toString().trim())) {
            Toast.makeText(this, "请输入报警类别", Toast.LENGTH_SHORT).show();
            etIncidentCategory.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etIncidentType.getText().toString().trim())) {
            Toast.makeText(this, "请输入报警类型", Toast.LENGTH_SHORT).show();
            etIncidentType.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etReporterName.getText().toString().trim())) {
            Toast.makeText(this, "请输入报警人姓名", Toast.LENGTH_SHORT).show();
            etReporterName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etReporterPhone.getText().toString().trim())) {
            Toast.makeText(this, "请输入报警人电话", Toast.LENGTH_SHORT).show();
            etReporterPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etIncidentLocation.getText().toString().trim())) {
            Toast.makeText(this, "请输入事发地点", Toast.LENGTH_SHORT).show();
            etIncidentLocation.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etIncidentContent.getText().toString().trim())) {
            Toast.makeText(this, "请输入报警内容", Toast.LENGTH_SHORT).show();
            etIncidentContent.requestFocus();
            return false;
        }

        return true;
    }

    private TaskData collectTaskData() {
        TaskData taskData = new TaskData();
        taskData.setSeverityLevel(severityLevel);
        taskData.setIncidentId(etIncidentId.getText().toString().trim());
        taskData.setIncidentCategory(etIncidentCategory.getText().toString().trim());
        taskData.setIncidentType(etIncidentType.getText().toString().trim());
        taskData.setIncidentSubcategory(etIncidentSubcategory.getText().toString().trim());
        taskData.setIncidentMinorCategory(etIncidentMinorCategory.getText().toString().trim());
        taskData.setIncidentStatus(tvIncidentStatus.getText().toString().trim());
        taskData.setReceptionTime(etReceptionTime.getText().toString().trim());
        taskData.setReporterName(etReporterName.getText().toString().trim());
        taskData.setReporterPhone(etReporterPhone.getText().toString().trim());
        taskData.setIncidentLocation(etIncidentLocation.getText().toString().trim());
        taskData.setIncidentContent(etIncidentContent.getText().toString().trim());
        taskData.setOfficerId(etOfficerId.getText().toString().trim());
        taskData.setLevel3Unit(etLevel3Unit.getText().toString().trim());
        taskData.setLevel2Unit(etLevel2Unit.getText().toString().trim());
        taskData.setLevel3LinkageUnit(etLevel3LinkageUnit.getText().toString().trim());

        return taskData;
    }

    private void performCreateTask(TaskData taskData) {
        // 这里应该调用API创建任务
        HashMap<String, Object> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        
        // 根据API字段规范添加所有必需字段
        // 主键id - 由服务器生成，不传
        // params.put("id", null);
        
        // 租户ID - 从用户信息获取或设置默认值
        params.put("tenantId", UserInfoUtils.getInstance(this).getUserId() != null ?
                   IMUserInfoUtil.getInstance().getUserId() : "default_tenant");
        
        // 警情级别 - 映射UI的级别选择
        params.put("alertLevel", String.valueOf(taskData.getSeverityLevel()));
        
        // 接警单编号 - 使用警情ID
        params.put("receivingAlertNumber", taskData.getIncidentId());
        
        // 接警人 - 使用报警人姓名
        params.put("receivingOfficer", taskData.getReporterName());
        
        // 接警人编号 - 使用警员ID
        params.put("receivingOfficerId", taskData.getOfficerId());
        
        // 接警人电话 - 使用报警人电话
        params.put("receivingOfficerPhone", taskData.getReporterPhone());
        
        // 处警单位 - 使用三级处警单位
        params.put("responseUnit", taskData.getLevel3Unit());
        
        // 警情状态 - 映射UI的状态选择
        params.put("alertStatus", taskData.getIncidentStatus());
        
        // 报警类别
        params.put("alertCategory", taskData.getIncidentCategory());
        
        // 报警类型
        params.put("alertType", taskData.getIncidentType());
        
        // 报警细类 - 使用子类别
        params.put("alertSubType", taskData.getIncidentSubcategory());
        
        // 事发地点
        params.put("incidentLocation", taskData.getIncidentLocation());
        
        // 报警内容
        params.put("alertContent", taskData.getIncidentContent());
        
        // 三级处警单位
        params.put("thirdResponseUnit", taskData.getLevel3Unit());
        
        // 二级处警单位
        params.put("secondResponseUnit", taskData.getLevel2Unit());
        
        // 一级处警单位 - 使用三级联动单位
        params.put("firstResponseUnit", taskData.getLevel3LinkageUnit());

        params.put("requestType", "json");

        HttpUtils.with(this)
                .post() // 改为POST请求，因为这是创建操作
                .url(ApiConstants.COORDINATION_SAVA)
                .addHeader("accessToken", IMUserInfoUtil.getInstance().getToken())
                .addBody(params)
                .execute(new HttpEngineCallBack<String>() {
                    @Override
                    public void onSuccess(String objResult) {
                        // 在主线程显示成功消息并跳转
                        runOnUiThread(() -> {
                            Toast.makeText(CreateTaskActivity.this, "任务创建成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateTaskActivity.this, IncidentDetailActivity.class);
                            intent.putExtra("receivingAlertNumber", taskData.getIncidentId());
                            startActivity(intent);
                            finish(); // 关闭当前页面
                        });
                    }

                });
    }

    // 任务数据类
    public static class TaskData implements java.io.Serializable {
        private int severityLevel;
        private String incidentId;
        private String incidentCategory;
        private String incidentType;
        private String incidentSubcategory;
        private String incidentMinorCategory;
        private String incidentStatus;
        private String receptionTime;
        private String reporterName;
        private String reporterPhone;
        private String incidentLocation;
        private String incidentContent;
        private String officerId;
        private String level3Unit;
        private String level2Unit;
        private String level3LinkageUnit;
        
        // 新增字段用于API映射
        private String tenantId;
        private String sessionId;
        private String groupId;

        // Getters and Setters
        public int getSeverityLevel() { return severityLevel; }
        public void setSeverityLevel(int severityLevel) { this.severityLevel = severityLevel; }

        public String getIncidentId() { return incidentId; }
        public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

        public String getIncidentCategory() { return incidentCategory; }
        public void setIncidentCategory(String incidentCategory) { this.incidentCategory = incidentCategory; }

        public String getIncidentType() { return incidentType; }
        public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

        public String getIncidentSubcategory() { return incidentSubcategory; }
        public void setIncidentSubcategory(String incidentSubcategory) { this.incidentSubcategory = incidentSubcategory; }

        public String getIncidentMinorCategory() { return incidentMinorCategory; }
        public void setIncidentMinorCategory(String incidentMinorCategory) { this.incidentMinorCategory = incidentMinorCategory; }

        public String getIncidentStatus() { return incidentStatus; }
        public void setIncidentStatus(String incidentStatus) { this.incidentStatus = incidentStatus; }

        public String getReceptionTime() { return receptionTime; }
        public void setReceptionTime(String receptionTime) { this.receptionTime = receptionTime; }

        public String getReporterName() { return reporterName; }
        public void setReporterName(String reporterName) { this.reporterName = reporterName; }

        public String getReporterPhone() { return reporterPhone; }
        public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

        public String getIncidentLocation() { return incidentLocation; }
        public void setIncidentLocation(String incidentLocation) { this.incidentLocation = incidentLocation; }

        public String getIncidentContent() { return incidentContent; }
        public void setIncidentContent(String incidentContent) { this.incidentContent = incidentContent; }

        public String getOfficerId() { return officerId; }
        public void setOfficerId(String officerId) { this.officerId = officerId; }

        public String getLevel3Unit() { return level3Unit; }
        public void setLevel3Unit(String level3Unit) { this.level3Unit = level3Unit; }

        public String getLevel2Unit() { return level2Unit; }
        public void setLevel2Unit(String level2Unit) { this.level2Unit = level2Unit; }

        public String getLevel3LinkageUnit() { return level3LinkageUnit; }
        public void setLevel3LinkageUnit(String level3LinkageUnit) { this.level3LinkageUnit = level3LinkageUnit; }
        
        // 新增字段的getter和setter
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
    }
}
