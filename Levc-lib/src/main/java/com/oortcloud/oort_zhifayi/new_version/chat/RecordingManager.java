package com.oortcloud.oort_zhifayi.new_version.chat;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class RecordingManager {
    private static final String RECORD_DIR = "records";
    private List<Recording> recordings = new ArrayList<>();

    public void saveRecording(Context context, String filePath, int duration) {
        // 保存到本地文件并添加记录
        recordings.add(new Recording(filePath, System.currentTimeMillis(), duration));
        // 实际开发中需持久化到数据库（如 Room）
    }

    public List<Recording> getRecordings() {
        return recordings;
    }
}

// 适配器 RecordingAdapter（类似 MemberAdapter）

