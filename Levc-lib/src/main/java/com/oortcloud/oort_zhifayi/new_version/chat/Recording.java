package com.oortcloud.oort_zhifayi.new_version.chat;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Recording {
    private String filePath;
    private long timestamp;
    private int duration; // 单位：秒

    public Recording(String filePath, long timestamp, int duration) {
        this.filePath = filePath;
        this.timestamp = timestamp;
        this.duration = duration;
    }

    // Getters
    public String getFilePath() { return filePath; }
    public long getTimestamp() { return timestamp; }
    public int getDuration() { return duration; }
}
