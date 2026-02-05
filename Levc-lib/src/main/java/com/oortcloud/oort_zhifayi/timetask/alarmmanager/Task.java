package com.oortcloud.oort_zhifayi.timetask.alarmmanager;

/**
 * 简单的定时任务模型：包含任务唯一id、开始/结束时间（毫秒）及可选描述。
 */
public class Task {
    public final String taskId;
    public final long startTimeMillis;
    public final long endTimeMillis;

    public final String description;

    public Task(String taskId, long startTimeMillis, long endTimeMillis, String description) {
        this.taskId = taskId;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.description = description;
    }
}


