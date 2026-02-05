package com.oortcloud.oort_zhifayi.new_version.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.oort_zhifayi.R;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecordingAdapter adapter;
    private RecordingManager recordingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // 初始化录音管理器
        recordingManager = new RecordingManager();
        List<Recording> recordings = recordingManager.getRecordings();

        // 设置 RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordingAdapter(recordings, (recording, position) -> {
            // 点击条目操作（例如弹窗显示详情）
            showRecordingDetails(recording);
        });
        recyclerView.setAdapter(adapter);

        TextView tvEmpty = findViewById(R.id.tvEmpty);
        if (recordings.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void showRecordingDetails(Recording recording) {
        new AlertDialog.Builder(this)
                .setTitle("录音详情")
                .setMessage("路径: " + recording.getFilePath() + "\n时长: " + recording.getDuration() + "秒")
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopPlaying(); // 确保退出时释放播放器
        }
    }
}