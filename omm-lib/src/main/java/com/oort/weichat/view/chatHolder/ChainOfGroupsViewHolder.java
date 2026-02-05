package com.oort.weichat.view.chatHolder;

import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.R;
import com.oort.weichat.adapter.ChainParticipantDisplayAdapter;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.view.ChatContentView;

import java.util.ArrayList;
import java.util.List;

public class ChainOfGroupsViewHolder extends AChatHolderInterface {

    public TextView tvFireTime;
    private MotionEvent event;
    
    // 新增的UI组件
    private TextView chainTitle;
    private TextView chainSubtitle;
    private TextView expandButton;
    private TextView tvParticipateChain;
    private LinearLayout participantsContainer;
    private RecyclerView participantsList;
    private ChainParticipantDisplayAdapter participantAdapter;
    private boolean isExpanded = false;


    private ChatContentView.MessageEventListener mEventListener;

    public void set(ChatContentView.MessageEventListener eventListener){
        mEventListener =eventListener;
    }
    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_chain_of_groups : R.layout.chat_to_item_chain_fo_groups;
    }

    @Override
    public void initView(View view) {
        mRootView = view.findViewById(R.id.chat_warp_view);
        if (!isMysend) {
            tvFireTime = view.findViewById(R.id.tv_fire_time);
        }
        
        // 初始化新的UI组件
        chainTitle = view.findViewById(R.id.chain_title);
        chainSubtitle = view.findViewById(R.id.chain_subtitle);
        expandButton = view.findViewById(R.id.expand_button);
        tvParticipateChain = view.findViewById(R.id.tv_participate_chain);
        participantsContainer = view.findViewById(R.id.participants_container);
        participantsList = view.findViewById(R.id.participants_list);
        
        // 设置展开按钮点击事件
        if (expandButton != null) {
            expandButton.setOnClickListener(v -> toggleExpand());
        }
        
        // 设置参与接龙按钮点击事件
        if (tvParticipateChain != null) {
            tvParticipateChain.setOnClickListener(v -> {
                // 获取当前接龙数据
                String chainData = getCurrentChainData();
                if (mEventListener != null) {
                    mEventListener.chainOfGroups(chainData);
                }
            });
        }
        
        // 初始化参与者列表
        if (participantsList != null) {
            participantsList.setLayoutManager(new LinearLayoutManager(mContext));
            participantAdapter = new ChainParticipantDisplayAdapter(new ArrayList<>(), isMysend);
            participantsList.setAdapter(participantAdapter);
        }
        
        // 根据发送/接收设置不同的文字颜色
        if (chainTitle != null && chainSubtitle != null) {
            if (isMysend) {
                // 发送的消息使用白色文字
                chainTitle.setTextColor(mContext.getResources().getColor(android.R.color.white));
                chainSubtitle.setTextColor(mContext.getResources().getColor(android.R.color.white));
            } else {
                // 接收的消息使用深色文字
                chainTitle.setTextColor(mContext.getResources().getColor(android.R.color.black));
                chainSubtitle.setTextColor(mContext.getResources().getColor(android.R.color.black));
            }
        }
        
        // 设置展开按钮颜色
        if (expandButton != null) {
            if (isMysend) {
                // 发送的消息使用白色文字
                expandButton.setTextColor(mContext.getResources().getColor(android.R.color.white));
            } else {
                // 接收的消息使用蓝色文字
                expandButton.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
            }
        }
    }


    @Override
    public void fillData(ChatMessage message) {
        String content = StringUtils.replaceSpecialChar(message.getContent());
        
        // 解析接龙数据并显示
        parseAndDisplayChainData(content);

        // 设置点击事件
        mRootView.setOnClickListener(v -> mHolderListener.onItemClick(mRootView, ChainOfGroupsViewHolder.this, mdata));
        mRootView.setOnLongClickListener(v -> {
            mHolderListener.onItemLongClick(v, event, ChainOfGroupsViewHolder.this, mdata);
            return true;
        });

        mRootView.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    event = e;
            }
            return false;
        });
    }

    @Override
    protected void onRootClick(View v) {

    }

    @Override
    public boolean enableFire() {
        return true;
    }

    @Override
    public boolean enableSendRead() {
        return true;
    }

    public void showFireTime(boolean show) {
        if (tvFireTime != null) {
            tvFireTime.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    /**
     * 解析并显示接龙数据
     */
    private void parseAndDisplayChainData(String content) {
        if (chainTitle == null || chainSubtitle == null || expandButton == null || participantsContainer == null) {
            return;
        }
        
        // 解析接龙数据
        String[] lines = content.split("\n");
        String title = "#接龙";
        String subtitle = "";
        List<String> participants = new ArrayList<>();
        
        for (String line : lines) {
            if (line.startsWith("#接龙")) {
                title = line;
                // 提取副标题
                if (line.length() > 3) {
                    subtitle = line.substring(3).trim();
                }
            } else if (line.matches("\\d+\\.\\s+.+")) {
                // 匹配 "1. Jun" 格式
                String participant = line.substring(line.indexOf(". ") + 2);
                if (!participant.trim().isEmpty()) {
                    participants.add(participant.trim());
                }
            }
        }
        
        // 如果没有找到副标题，尝试从其他行提取
        if (subtitle.isEmpty()) {
            for (String line : lines) {
                if (!line.startsWith("#接龙") && !line.matches("\\d+\\.\\s+.+") && !line.trim().isEmpty()) {
                    subtitle = line.trim();
                    break;
                }
            }
        }
        
        // 设置标题和副标题
        chainTitle.setText(title);
        chainSubtitle.setText(subtitle);
        
        // 更新参与者列表
        if (participantAdapter != null) {
            participantAdapter.updateParticipants(participants);
        }
        
        // 设置展开按钮文本
        updateExpandButtonText(participants.size());
    }
    
    /**
     * 切换展开/收起状态
     */
    private void toggleExpand() {
        if (participantsContainer == null) return;
        
        isExpanded = !isExpanded;
        participantsContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        updateExpandButtonText(participantAdapter != null ? participantAdapter.getItemCount() : 0);
    }
    
    /**
     * 更新展开按钮文本
     */
    private void updateExpandButtonText(int participantCount) {
        if (expandButton == null) return;
        
        if (isExpanded) {
            expandButton.setText("收起");
        } else {
            expandButton.setText("...展开...");
        }
    }
    
    /**
     * 获取当前接龙数据
     */
    private String getCurrentChainData() {
        if (mdata == null) {
            return "";
        }
        
        // 返回当前消息的完整内容作为接龙数据
        String content = mdata.getContent();
        if (content == null) {
            return "";
        }
        
        // 构建接龙数据JSON格式
        StringBuilder chainData = new StringBuilder();
        chainData.append("{");
        chainData.append("\"messageId\":\"").append(mdata.getPacketId()).append("\",");
        chainData.append("\"content\":\"").append(content.replace("\"", "\\\"")).append("\",");
        chainData.append("\"fromUserId\":\"").append(mdata.getFromUserId()).append("\",");
        chainData.append("\"fromUserName\":\"").append(mdata.getFromUserName()).append("\",");
        chainData.append("\"timestamp\":").append(mdata.getTimeSend());
        chainData.append("}");
        
        return chainData.toString();
    }


}
