package com.oort.weichat.ui.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 接龙参与者适配器
 */
public class ChainParticipantAdapter extends RecyclerView.Adapter<ChainParticipantAdapter.ViewHolder> {
    
    private List<String> participants;
    private OnParticipantChangeListener listener;
    private String defaultUserName;
    
    public interface OnParticipantChangeListener {
        void onParticipantChanged(int position, String content);
        void onParticipantInputCompleted();
        void onParticipantFocusChanged(int position, boolean hasFocus);
    }
    
    public ChainParticipantAdapter() {
        this.participants = new ArrayList<>();
    }
    
    public ChainParticipantAdapter(List<String> participants) {
        this.participants = participants != null ? new ArrayList<>(participants) : new ArrayList<>();
    }
    
    public ChainParticipantAdapter(List<String> participants, String defaultUserName) {
        this.participants = participants != null ? new ArrayList<>(participants) : new ArrayList<>();
        this.defaultUserName = defaultUserName;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chain_participant, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String content = participants.get(position);
        System.out.println("绑定ViewHolder - 位置: " + position + ", 内容: " + content);
        holder.bind(position, content);
    }
    
    @Override
    public int getItemCount() {
        return participants.size();
    }
    
    public void setOnParticipantChangeListener(OnParticipantChangeListener listener) {
        this.listener = listener;
    }
    
    public void addParticipant() {
        String defaultContent = defaultUserName != null ? defaultUserName : "";
        participants.add(defaultContent);
        int position = participants.size() - 1;
        System.out.println("添加新参与者 - 位置: " + position + ", 总数: " + participants.size() + ", 默认内容: " + defaultContent);
        
        // 简化通知，只使用notifyItemInserted
        notifyItemInserted(position);
        
        // 通知监听器数据变化
        if (listener != null) {
            listener.onParticipantChanged(position, defaultContent);
        }
    }
    
    public void focusLastParticipant() {
        if (!participants.isEmpty()) {
            int lastPosition = participants.size() - 1;
            notifyItemChanged(lastPosition);
        }
    }
    
    public void addParticipant(String content) {
        participants.add(content);
        notifyItemInserted(participants.size() - 1);
    }
    
    public void removeParticipant(int position) {
        if (position >= 0 && position < participants.size()) {
            participants.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, participants.size());
        }
    }
    
    public List<String> getParticipants() {
        return new ArrayList<>(participants);
    }
    
    public void updateParticipants(List<String> newParticipants) {
        this.participants.clear();
        this.participants.addAll(newParticipants);
        notifyDataSetChanged();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber;
        private EditText etParticipant;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tv_number);
            etParticipant = itemView.findViewById(R.id.et_participant);
        }
        
        public void bind(int position, String content) {
            tvNumber.setText(String.valueOf(position + 1));
            etParticipant.setText(content);
            etParticipant.setHint("请输入接龙内容");
            
            // 移除之前的监听器，避免重复设置
            etParticipant.setOnFocusChangeListener(null);
            
            // 如果是最后一个且内容为空，自动获取焦点
            if (position == participants.size() - 1 && content.isEmpty()) {
                etParticipant.post(new Runnable() {
                    @Override
                    public void run() {
                        etParticipant.requestFocus();
                    }
                });
            }
            
            // 设置新的焦点变化监听器
            etParticipant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    System.out.println("输入框焦点变化 - 位置: " + position + ", 有焦点: " + hasFocus);
                    
                    // 通知焦点变化
                    if (listener != null) {
                        listener.onParticipantFocusChanged(position, hasFocus);
                    }
                    
                    if (!hasFocus && listener != null) {
                        String text = etParticipant.getText().toString().trim();
                        // 更新数据
                        participants.set(position, text);
                        listener.onParticipantChanged(position, text);
                        
                        // 延迟显示添加按钮，避免用户快速点击
                        etParticipant.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("显示添加按钮");
                                listener.onParticipantInputCompleted();
                            }
                        }, 300); // 延迟300ms
                    }
                }
            });
            
            // 添加文本变化监听器，实时更新数据
            etParticipant.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 实时更新数据
                    participants.set(position, s.toString());
                    if (listener != null) {
                        listener.onParticipantChanged(position, s.toString());
                    }
                }
                
                @Override
                public void afterTextChanged(android.text.Editable s) {
                }
            });
        }
    }
}
