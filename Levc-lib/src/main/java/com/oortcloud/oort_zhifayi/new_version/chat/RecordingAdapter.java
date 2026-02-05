package com.oortcloud.oort_zhifayi.new_version.chat;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.oort_zhifayi.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {

    private List<Recording> recordings;
    private OnItemClickListener listener;
    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition = -1; // 当前播放的条目位置

    public interface OnItemClickListener {
        void onItemClick(Recording recording, int position);
    }

    public RecordingAdapter(List<Recording> recordings, OnItemClickListener listener) {
        this.recordings = recordings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recording, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recording recording = recordings.get(position);
        Context context = holder.itemView.getContext();

        // 设置文件名（截取路径末尾）
        String fileName = recording.getFilePath().substring(recording.getFilePath().lastIndexOf("/") + 1);
        holder.tvFileName.setText(fileName);

        // 格式化时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String time = sdf.format(new Date(recording.getTimestamp()));
        holder.tvTime.setText(time);

        // 格式化时长
        String duration = String.format(Locale.getDefault(), "%02d:%02d",
                recording.getDuration() / 60, recording.getDuration() % 60);
        holder.tvDuration.setText(duration);

        // 设置播放按钮图标
        if (currentPlayingPosition == position) {
            holder.btnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            holder.btnPlay.setImageResource(R.drawable.ic_play);
        }

        // 点击播放按钮
        holder.btnPlay.setOnClickListener(v -> {
            if (currentPlayingPosition == position) {
                stopPlaying(); // 暂停播放
            } else {
                startPlaying(context, recording, position); // 开始播放
            }
        });

        // 点击条目其他区域
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(recording, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    // 开始播放录音
    private void startPlaying(Context context, Recording recording, int position) {
        stopPlaying(); // 停止之前的播放
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(recording.getFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentPlayingPosition = position;
            notifyDataSetChanged(); // 刷新列表

            // 播放完成监听
            mediaPlayer.setOnCompletionListener(mp -> {
                currentPlayingPosition = -1;
                notifyDataSetChanged();
            });
        } catch (IOException e) {
            Toast.makeText(context, "播放失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 停止播放
    public void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            currentPlayingPosition = -1;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btnPlay;
        TextView tvFileName, tvTime, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }
    }

    // 更新数据
    public void updateRecordings(List<Recording> newRecordings) {
        recordings = newRecordings;
        notifyDataSetChanged();
    }
}
