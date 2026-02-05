package com.oort.weichat.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.R;
import com.oortcloud.basemodule.utils.StatusBarUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 群接龙列表
 */
public class ChainOfGroupsActivity extends BaseActivity {
    
    public static final String EXTRA_ROLE = "role";
    public static final String EXTRA_CHAIN_DATA = "chain_data";
    public static final String ROLE_INITIATOR = "initiator"; // 发起接龙
    public static final String ROLE_PARTICIPANT = "participant"; // 参与接龙
    
    private ImageView ivBack;
    private TextView tvSend;
    private EditText etChainTitle;
    private EditText etSupplement;
    private RecyclerView rvChainParticipants;
    private TextView tvInitiatorInfo;
    private LinearLayout llChainList;
    
    private ChainParticipantAdapter participantAdapter;
    private final List<String> chainItems = new ArrayList<>();
    private String currentRole;
    private String existingChainData;
    private Friend mFriend;

    // 用户信息
    private String mLoginUserId;
    private String mLoginNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain_of_groups);
        Objects.requireNonNull(getSupportActionBar()).hide();
        StatusBarUtil.setStatusBarColor(this, R.color.white);
        

        // 获取角色参数和已有数据
        currentRole = getIntent().getStringExtra(EXTRA_ROLE);
        existingChainData = getIntent().getStringExtra(EXTRA_CHAIN_DATA);
        mFriend = (Friend) getIntent().getSerializableExtra("mFriend");
        
        // 获取用户信息
        mLoginUserId = getIntent().getStringExtra("mLoginUserId");
        mLoginNickName = getIntent().getStringExtra("mLoginNickName");

        if (currentRole == null) {
            currentRole = ROLE_INITIATOR; // 默认为发起者
        }
        
        initViews();
        initListeners();
        setupUIForRole();
        loadExistingChainData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvSend = findViewById(R.id.tv_send);
        etChainTitle = findViewById(R.id.et_chain_title);
        etSupplement = findViewById(R.id.et_supplement);
        rvChainParticipants = findViewById(R.id.rv_chain_participants);
        tvInitiatorInfo = findViewById(R.id.tv_initiator_info);
        llChainList = findViewById(R.id.ll_chain_list);
        
        // 设置光标位置到第二行
        etChainTitle.post(new Runnable() {
            @Override
            public void run() {
                etChainTitle.setSelection(etChainTitle.getText().length());
            }
        });
        
        // 初始化RecyclerView
        setupRecyclerView();
        
        // 添加按钮点击事件
        findViewById(R.id.ll_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 简化操作，避免复杂的滚动逻辑
                participantAdapter.addParticipant();
                // 简单的滚动到底部
                rvChainParticipants.post(new Runnable() {
                    @Override
                    public void run() {
                        if (participantAdapter.getItemCount() > 0) {
                            rvChainParticipants.smoothScrollToPosition(participantAdapter.getItemCount() - 1);
                        }
                    }
                });
            }
        });
    }
    
    private void setupRecyclerView() {
        // 创建测试数据
        List<String> testParticipants = createTestData();
        
        // 获取当前用户名作为默认值，优先使用mLoginNickName，其次使用mFriend的昵称
        String defaultUserName = "";
        if (mLoginNickName != null && !mLoginNickName.isEmpty()) {
            defaultUserName = mLoginNickName;
        } else if (mFriend != null && mFriend.getNickName() != null) {
            defaultUserName = mFriend.getNickName();
        }
        
        // 如果是创建接龙，默认添加一个参与者
        if (ROLE_INITIATOR.equals(currentRole) && testParticipants.isEmpty()) {
            testParticipants.add(defaultUserName);
        }
        
        participantAdapter = new ChainParticipantAdapter(testParticipants, defaultUserName);
        participantAdapter.setOnParticipantChangeListener(
                new ChainParticipantAdapter.OnParticipantChangeListener() {
            @Override
            public void onParticipantChanged(int position, String content) {
                // 参与者内容变化时的处理
                System.out.println("参与者 " + (position + 1) + " 内容变化: " + content);
                updateInitiatorInfo();
            }
            
            @Override
            public void onParticipantInputCompleted() {
                // 输入完成，按钮始终可见，无需特殊处理
                System.out.println("用户输入完成");
            }
            
            @Override
            public void onParticipantFocusChanged(int position, boolean hasFocus) {
                // 简化焦点处理，避免复杂的滚动逻辑
                if (hasFocus && position >= participantAdapter.getItemCount() - 2) {
                    // 简单的滚动到底部
                    rvChainParticipants.post(new Runnable() {
                        @Override
                        public void run() {
                            rvChainParticipants.smoothScrollToPosition(participantAdapter.getItemCount() - 1);
                        }
                    });
                }
            }
            
        });
        
        rvChainParticipants.setLayoutManager(new LinearLayoutManager(this));
        rvChainParticipants.setAdapter(participantAdapter);
        
        // 初始化发起人信息显示
        updateInitiatorInfo();
    }
    
    /**
     * 更新发起人信息显示
     */
    private void updateInitiatorInfo() {
        int count = participantAdapter.getItemCount();
        String initiatorName = mLoginNickName != null ? mLoginNickName : "用户";
        String info = "由" + initiatorName + "在" + mFriend.getNickName() + "发起接龙,参与接龙目前共" + count + "人";
        tvInitiatorInfo.setText(info);
        System.out.println("更新发起人信息: " + info);
    }

    /**
     * 创建测试数据
     */
    private List<String> createTestData() {
        List<String> testData = new ArrayList<>();
        // 返回空列表，让默认参与者逻辑生效
        return testData;
    }
    
    private void initListeners() {
        // 返回按钮
        ivBack.setOnClickListener(v -> finish());
        
        // 发送按钮
        tvSend.setOnClickListener(v -> handleSendChain());
    }
    
    /**
     * 处理发送接龙
     */
    private void handleSendChain() {
        String chainTitle = etChainTitle.getText().toString().trim();
        String supplement = etSupplement.getText().toString().trim();
        
        // 验证接龙标题
//        if (TextUtils.isEmpty(chainTitle) || !chainTitle.startsWith("#接龙")) {
//            Toast.makeText(this, "请输入接龙标题", Toast.LENGTH_SHORT).show();
//            return;
//        }
        
        // 收集接龙项目
        collectChainItems();
        
        // 构建接龙结果
        StringBuilder chainResult = new StringBuilder();
        chainResult.append(chainTitle).append("\n\n");
        
        // 添加接龙项目
        for (int i = 0; i < chainItems.size(); i++) {
            chainResult.append(i + 1).append(". ").append(chainItems.get(i)).append("\n");
        }
        
        // 添加补充信息
        if (!TextUtils.isEmpty(supplement)) {
            chainResult.append("\n补充信息：").append(supplement);
        }
        
        // 构建参与者列表字符串
        StringBuilder participantsBuilder = new StringBuilder();
        for (int i = 0; i < chainItems.size(); i++) {
            if (i > 0) {
                participantsBuilder.append(", ");
            }
            participantsBuilder.append(chainItems.get(i));
        }
        
        // 返回结果
        Intent resultIntent = new Intent();
        resultIntent.putExtra("chainTitle", chainTitle);
        resultIntent.putExtra("chainContent", chainResult.toString());
//        resultIntent.putExtra("chainParticipants", participantsBuilder.toString());
//        resultIntent.putExtra("supplement", supplement);
//        resultIntent.putExtra("chain_count", chainItems.size());

//        Log.e("zq", "接龙数据 - chainTitle: " + chainTitle);
//        Log.e("zq", "接龙数据 - chainContent: " + chainResult.toString());
//        Log.e("zq", "接龙数据 - chainParticipants: " + participantsBuilder.toString());
//        Log.e("zq", "接龙数据 - supplement: " + supplement);
//        Log.e("zq", "接龙数据 - chain_count: " + chainItems.size());

        setResult(RESULT_OK, resultIntent);
        finish();
    }
    
    /**
     * 收集接龙项目
     */
    private void collectChainItems() {
        chainItems.clear();
        List<String> participants = participantAdapter.getParticipants();
        for (String participant : participants) {
            if (!TextUtils.isEmpty(participant.trim())) {
                chainItems.add(participant.trim());
            }
        }
    }
    
    /**
     * 根据角色设置UI
     */
    private void setupUIForRole() {
        if (ROLE_INITIATOR.equals(currentRole)) {
            // 发起者：可以编辑标题，可以添加接龙项目
            etChainTitle.setEnabled(true);
            llChainList.setVisibility(View.VISIBLE);
            tvSend.setText("发送");
        } else if (ROLE_PARTICIPANT.equals(currentRole)) {
            // 参与者：只能查看标题，只能添加自己的接龙项目
            etChainTitle.setEnabled(false);
            llChainList.setVisibility(View.VISIBLE);
            tvSend.setText("参与接龙");
        }
    }
    
    
    /**
     * 加载已有的接龙数据
     */
    private void loadExistingChainData() {
        if (existingChainData != null && !existingChainData.isEmpty()) {
            List<String> existingParticipants = new ArrayList<>();
            // 解析已有的接龙数据
            String[] lines = existingChainData.split("\n");
            for (String line : lines) {
                if (line.matches("\\d+\\.\\s+.+")) {
                    // 匹配 "1. 二栋一单元-801" 格式
                    String item = line.substring(line.indexOf(". ") + 2);
                    if (!item.trim().isEmpty()) {
                        existingParticipants.add(item.trim());
                    }
                }
            }
            participantAdapter.updateParticipants(existingParticipants);
        }
    }
}
