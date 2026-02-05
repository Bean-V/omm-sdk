package com.oort.weichat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.R;

import java.util.List;

/**
 * 接龙参与者显示适配器
 */
public class ChainParticipantDisplayAdapter extends RecyclerView.Adapter<ChainParticipantDisplayAdapter.ViewHolder> {
    
    private List<String> participants;
    private boolean isMysend;
    
    public ChainParticipantDisplayAdapter(List<String> participants) {
        this.participants = participants != null ? participants : new java.util.ArrayList<>();
        this.isMysend = false; // 默认为接收消息
    }
    
    public ChainParticipantDisplayAdapter(List<String> participants, boolean isMysend) {
        this.participants = participants != null ? participants : new java.util.ArrayList<>();
        this.isMysend = isMysend;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chain_participant_display, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String participant = participants.get(position);
        holder.participantNumber.setText(String.valueOf(position + 1));
        holder.participantName.setText(participant);
        
        // 根据isMysend设置文字颜色
        if (isMysend) {
            // 发送的消息使用白色文字
            holder.participantNumber.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            holder.participantName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        } else {
            // 接收的消息使用深色文字
            holder.participantNumber.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
            holder.participantName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
        }
    }
    
    @Override
    public int getItemCount() {
        return participants.size();
    }
    
    public void updateParticipants(List<String> newParticipants) {
        this.participants.clear();
        this.participants.addAll(newParticipants);
        notifyDataSetChanged();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView participantNumber;
        TextView participantName;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            participantNumber = itemView.findViewById(R.id.participant_number);
            participantName = itemView.findViewById(R.id.participant_name);
        }
    }
}
