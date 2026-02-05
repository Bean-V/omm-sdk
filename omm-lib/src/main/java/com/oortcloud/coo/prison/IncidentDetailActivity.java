package com.oortcloud.coo.prison;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.alibaba.fastjson.JSON;
import com.jun.baselibrary.http.HttpUtils;
import com.jun.framelibrary.http.callback.HttpEngineCallBack;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.R;
import com.oort.weichat.ui.lccontact.PersonPickActivity;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.utils.StatusBarUtil;
import com.oortcloud.coo.bean.Records;
import com.oortcloud.coo.bean.Result;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IncidentDetailActivity extends BaseActivity implements View.OnClickListener {

    // UI组件
    private ImageView ivBack;

    private LinearLayout llDetail;
    private RelativeLayout llDetailHeader;
    private LinearLayout llDetailContent;
    private TextView ivCollapse; // 底部的收起按钮
    private TextView tvSeverityLevel;
    private TextView tvIncidentId;
    private TextView tvIncidentCategory;
    private TextView tvIncidentType;
    private TextView tvIncidentSubcategory;
    private TextView tvIncidentMinorCategory;
    private TextView tvReceptionTime;
    private TextView tvReporterName;
    private TextView tvReporterPhone;
    private TextView tvIncidentLocation;
    private TextView tvIncidentContent;
    private TextView tvOfficerId;
    private TextView tvLevel3Unit;
    private TextView tvLevel2Unit;
    private TextView tvLevel3LinkageUnit;
    private CheckBox cbTaskCollaboration;
    private Button btnCreateCollaboration;
    private ImageView mAddIV;
    private FrameLayout fragmentChatContainer;

    // 数据
    private Records mRecords;
    private boolean isDetailCollapsed = false;

    @Override
    public void setStatusBarLight(boolean light) {
        StatusBarUtil.setStatusBarColor(this, R.color.white);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_detail_prison);

        initViews();
        setupClickListeners();
        loadIncidentData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        llDetail = findViewById(R.id.ll_detail);
        llDetailHeader = findViewById(R.id.ll_detail_header);
        llDetailContent = findViewById(R.id.ll_detail_content);
        ivCollapse = findViewById(R.id.iv_collapse);
        tvSeverityLevel = findViewById(R.id.tv_severity_level);
        tvIncidentId = findViewById(R.id.tv_incident_id);
        tvIncidentCategory = findViewById(R.id.tv_incident_category);
        tvIncidentType = findViewById(R.id.tv_incident_type);
        tvIncidentSubcategory = findViewById(R.id.tv_incident_subcategory);
        tvIncidentMinorCategory = findViewById(R.id.tv_incident_minor_category);
        tvReceptionTime = findViewById(R.id.tv_reception_time);
        tvReporterName = findViewById(R.id.tv_reporter_name);
        tvReporterPhone = findViewById(R.id.tv_reporter_phone);
        tvIncidentLocation = findViewById(R.id.tv_incident_location);
        tvIncidentContent = findViewById(R.id.tv_incident_content);
        tvOfficerId = findViewById(R.id.tv_officer_id);
        tvLevel3Unit = findViewById(R.id.tv_level3_unit);
        tvLevel2Unit = findViewById(R.id.tv_level2_unit);
        tvLevel3LinkageUnit = findViewById(R.id.tv_level3_linkage_unit);
        cbTaskCollaboration = findViewById(R.id.cb_task_collaboration);
        mAddIV = findViewById(R.id.iv_add);
        btnCreateCollaboration = findViewById(R.id.btn_create_collaboration);
        fragmentChatContainer = findViewById(R.id.fragment_chat_container);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(this);
        llDetailHeader.setOnClickListener(this);
        ivCollapse.setOnClickListener(this); // 添加底部收起按钮的点击监听

        // 添加头部收起按钮的点击监听
        TextView headerCollapseBtn = llDetailHeader.findViewById(R.id.iv_header_collapse);
        if (headerCollapseBtn != null) {
            headerCollapseBtn.setOnClickListener(this);
        }

    }

    String receivingAlertNumber;

    private void loadIncidentData() {
        // 从Intent获取警情数据
        Intent intent = getIntent();
        if (intent != null) {
            mRecords = (Records) intent.getSerializableExtra("incident_data");
            receivingAlertNumber = intent.getStringExtra("receivingAlertNumber");
            if (mRecords != null) {
                displayRecordsData(mRecords);
            } else {
                // 如果没有传入数据，使用示例数据+-*

                loadSampleData();
            }
        } else {
            loadSampleData();
        }
    }


    private void loadSampleData() {
        if (!receivingAlertNumber.isEmpty()) {
            // 显示加载状态
            showLoadingState();

            HashMap<String, Object> params = new HashMap<>();
            params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
            params.put("receivingAlertNumber", receivingAlertNumber);

            HttpUtils.with(this)
                    .get()
                    .url(ApiConstants.COORDINATION_DETAIL)
                    .addHeader("accessToken", IMUserInfoUtil.getInstance().getToken())
                    .addBody(params)
                    .execute(new HttpEngineCallBack<Result<Records>>() {
                        @Override
                        public void onSuccess(Result<Records> objResult) {

                            if (objResult.getCode() == 200 && objResult.getData() != null) {
                                // 直接使用Records数据
                                mRecords = objResult.getData();
                                // 在主线程更新UI
                                runOnUiThread(() -> {
                                    hideLoadingState();
                                    displayRecordsData(mRecords);
                                });
                            } else {
                                runOnUiThread(() -> {
                                    hideLoadingState();
                                    showErrorState("获取数据失败: " + objResult.getMsg());
                                });
                            }
                        }

                    });
        } else {
            // 如果没有接警单编号，显示错误
            showErrorState("缺少接警单编号");
        }
    }


    private int parseSeverityLevel(String alertLevel) {
        if (alertLevel == null || alertLevel.isEmpty()) {
            return 1; // 默认一级
        }
        try {
            return Integer.parseInt(alertLevel);
        } catch (NumberFormatException e) {
            return 1; // 解析失败时默认为一级
        }
    }

    private void showLoadingState() {
        // 显示加载状态，可以添加进度条或加载提示
        // 这里可以根据实际UI需求实现
    }

    private void hideLoadingState() {
        // 隐藏加载状态
    }

    private void showErrorState(String errorMessage) {
        // 显示错误状态
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        // 可以添加重试按钮或其他错误处理UI
    }

    private void displayRecordsData(Records records) {
        if (records == null) return;

        // 设置严重程度和ID
        int severityLevel = parseSeverityLevel(records.getAlertLevel());
        tvSeverityLevel.setText(getSeverityText(severityLevel));
        tvIncidentId.setText(records.getReceivingAlertNumber());

        // 设置严重程度背景
        setSeverityBackground(severityLevel);

        // 设置其他信息 - 直接使用Records字段
        tvIncidentCategory.setText(records.getAlertCategory());
        tvIncidentType.setText(records.getAlertType());
        tvIncidentSubcategory.setText(records.getAlertSubType());
        tvIncidentMinorCategory.setText(""); // API中没有对应字段
        tvReceptionTime.setText(""); // API中没有对应字段，可以显示当前时间
        tvReporterName.setText(records.getReceivingOfficer());
        tvReporterPhone.setText(records.getReceivingOfficerPhone());
        tvIncidentLocation.setText(records.getIncidentLocation());
        tvIncidentContent.setText(records.getAlertContent());
        tvOfficerId.setText(records.getReceivingOfficerId());
        tvLevel3Unit.setText(records.getThirdResponseUnit());
        tvLevel2Unit.setText(records.getSecondResponseUnit());
        tvLevel3LinkageUnit.setText(records.getFirstResponseUnit());

        initCheckBox();
    }

    private void setSeverityBackground(int severityLevel) {
        View severityContainer = findViewById(R.id.severity_level_and_id_container);
        switch (severityLevel) {
            case 1:
                severityContainer.setBackgroundResource(R.drawable.severity_level_one_background);
                break;
            case 2:
                severityContainer.setBackgroundResource(R.drawable.severity_level_two_background);
                break;
            case 3:
                severityContainer.setBackgroundResource(R.drawable.severity_level_three_background);
                break;
            case 4:
                severityContainer.setBackgroundResource(R.drawable.severity_level_four_background);
                break;
            default:
                severityContainer.setBackgroundResource(R.drawable.severity_level_trapezoid_background);
                break;
        }
    }

    private String getSeverityText(int level) {
        switch (level) {
            case 1:
                return "一级";
            case 2:
                return "二级";
            case 3:
                return "三级";
            case 4:
                return "四级";
            default:
                return "未知";
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.ll_detail_header) {
            toggleDetailCollapse();
        } else if (id == R.id.iv_collapse) {
            toggleDetailCollapse();
        } else if (id == R.id.iv_header_collapse) {
            toggleDetailCollapse();
        } else if (id == R.id.btn_create_collaboration) {
            createCollaboration();
        } else if (id == R.id.iv_add) {
            addUser();
        }
    }

    void initCheckBox() {
        // 设置协作状态 - 默认值
        String sessionId = mRecords.getSessionId();
        Friend friend = FriendDao.getInstance().getFriend(coreManager.getSelf().getUserId(), sessionId);
        cbTaskCollaboration.setChecked(true);
        if (friend == null || sessionId == null || sessionId.isEmpty()) {
            cbTaskCollaboration.setText("任务协作");
            updateCollaborationButton();
            btnCreateCollaboration.setOnClickListener(this);
            btnCreateCollaboration.setVisibility(VISIBLE);
            cbTaskCollaboration.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // 处理任务协作复选框状态变化
                updateCollaborationButton();
            });
        } else {
            initChatFragment(sessionId, "");
            cbTaskCollaboration.setText("应急协作");
            btnCreateCollaboration.setVisibility(GONE);
            mAddIV.setVisibility(VISIBLE);
            mAddIV.setOnClickListener(this);
            cbTaskCollaboration.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // 处理任务协作复选框状态变化
                updateAdd();
            });
        }

    }

    private void updateCollaborationButton() {
        boolean isChecked = cbTaskCollaboration.isChecked();
        btnCreateCollaboration.setEnabled(isChecked);
        btnCreateCollaboration.setAlpha(isChecked ? 1.0f : 0.5f);
    }

    private void updateAdd() {
        boolean isChecked = cbTaskCollaboration.isChecked();
        mAddIV.setEnabled(isChecked);
        mAddIV.setAlpha(isChecked ? 1.0f : 0.5f);
    }

    private void createCollaboration() {
        if (!cbTaskCollaboration.isChecked()) {
            return;
        }
        // 创建协作任务
        launcher.launch(new Intent(this, PersonPickActivity.class));
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    Bundle bundle = intent.getExtras();
                    assert bundle != null;
                    ArrayList<String> imUserIds = bundle.getStringArrayList("imUserIds");
                    String names = bundle.getString("names");
                    if (imUserIds == null || imUserIds.isEmpty()) {
                        return;
                    }
                  IMUserCreateGroup imUserCreateGroup = new IMUserCreateGroup(this, this::performCreateTask);
                    imUserCreateGroup.setSelectUser(imUserIds, coreManager);
                    // 处理返回数据
                    imUserCreateGroup.createGroupChat(names, "", 0, 1, 0, 1, 1, 0);
                }
            }
    );

    private void addUser() {
        if (!cbTaskCollaboration.isChecked()) {
            return;
        }
        startActivity(new Intent(mContext, PersonPickActivity.class));

        PersonPickActivity.pickFinish = new PersonPickActivity.PickFinish() {
            @Override
            public void finish(List ids0, String names0) {
                List<String> inviteIdList = new ArrayList<>();
                List<String> inviteNameList = new ArrayList<>();
                boolean isEmity = true;

                inviteIdList.addAll(ids0);
                inviteNameList.add(names0);
                isEmity = false;
                if (isEmity) {
                    return;
                }
                // 因为ios不要这样格式["10004541","10007042"]的字符串,，为了兼容他们，我们需要另外拼接一下
                String ids = JSON.toJSONString(inviteIdList); // ["10004541","10007042"]
                mMucChatFragment.inviteFriend(ids);
            }
        };
    }

    /**
     * 切换详情收起/展开状态
     */
    private void toggleDetailCollapse() {
        isDetailCollapsed = !isDetailCollapsed;

        if (isDetailCollapsed) {
            // 收起详情 - 隐藏详情内容，保留头部区域
            llDetailContent.setVisibility(GONE);

            // 显示头部的展开按钮
            TextView headerCollapseBtn = llDetailHeader.findViewById(R.id.iv_header_collapse);
            if (headerCollapseBtn != null) {
                headerCollapseBtn.setVisibility(VISIBLE);
            }

            // 隐藏底部的收起按钮
            ivCollapse.setVisibility(GONE);
        } else {
            // 展开详情 - 显示详情内容，隐藏头部的展开按钮
            llDetailContent.setVisibility(VISIBLE);

            // 隐藏头部的展开按钮
            TextView headerCollapseBtn = llDetailHeader.findViewById(R.id.iv_header_collapse);
            if (headerCollapseBtn != null) {
                headerCollapseBtn.setVisibility(GONE);
            }

            // 显示底部的收起按钮
            ivCollapse.setVisibility(VISIBLE);
            ivCollapse.setText("收起");
        }

        // 添加动画效果
//        Animation animation = AnimationUtils.loadAnimation(this,
//            isDetailCollapsed ? R.anim.slide_up : R.anim.slide_down);
//        llDetail.startAnimation(animation);
    }

    private MucChatFragment mMucChatFragment;
    /**
     * 初始化聊天Fragment
     */
    private void initChatFragment(String jid, String name) {
        fragmentChatContainer.setVisibility(VISIBLE);
        mMucChatFragment = MucChatFragment.newInstance(jid, name, true);
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_chat_container,mMucChatFragment )
                    .setReorderingAllowed(true)
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "e-->" + e);

        }
    }

    private void performCreateTask(String jid, String name) {
        // 这里应该调用API创建任务
        HashMap<String, Object> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());

        // 使用mRecords的实际属性
        if (mRecords != null) {
            params.put("id", mRecords.getId());
            params.put("tenantId", mRecords.getTenantId() != null ? mRecords.getTenantId() : "default_tenant");
            params.put("alertLevel", mRecords.getAlertLevel());
            params.put("receivingAlertNumber", mRecords.getReceivingAlertNumber());
            params.put("receivingOfficer", mRecords.getReceivingOfficer());
            params.put("receivingOfficerId", mRecords.getReceivingOfficerId());
            params.put("receivingOfficerPhone", mRecords.getReceivingOfficerPhone());
            params.put("responseUnit", mRecords.getResponseUnit());
            params.put("alertStatus", mRecords.getAlertStatus());
            params.put("alertCategory", mRecords.getAlertCategory());
            params.put("alertType", mRecords.getAlertType());
            params.put("alertSubType", mRecords.getAlertSubType());
            params.put("incidentLocation", mRecords.getIncidentLocation());
            params.put("alertContent", mRecords.getAlertContent());
            params.put("thirdResponseUnit", mRecords.getThirdResponseUnit());
            params.put("secondResponseUnit", mRecords.getSecondResponseUnit());
            params.put("firstResponseUnit", mRecords.getFirstResponseUnit());
            params.put("sessionId", jid); // 使用传入的jid
            params.put("groupId", mRecords.getGroupId());
        }
        params.put("sessionId", jid);
        params.put("groupId", jid);
        params.put("requestType", "json");

        HttpUtils.with(this)
                .post() // 改为POST请求，因为这是创建操作
                .url(ApiConstants.COORDINATION_UPDATE)
                .addHeader("accessToken", IMUserInfoUtil.getInstance().getToken())
                .addBody(params)
                .execute(new HttpEngineCallBack<String>() {
                    @Override
                    public void onSuccess(String objResult) {
                        // 在主线程显示成功消息并跳转
                        runOnUiThread(() -> {
                            Toast.makeText(IncidentDetailActivity.this, "协作群组创建成功", Toast.LENGTH_SHORT).show();
                            initChatFragment(jid, name);
                            loadSampleData();
                        });
                    }

                });
    }
}


