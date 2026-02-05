package com.oort.weichat.fragment.vs;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/26-22:30.
 * Version 1.0
 * Description:
 */
public class SearchHistoryManager {
    private static final String PREF_NAME = "search_prefs";
    private static final String KEY_HISTORY = "search_history";
    private static final int MAX_HISTORY_SIZE = 10;

    private final SharedPreferences preferences;

    public SearchHistoryManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 保存搜索历史
    public void saveSearchHistory(String query) {
        if (!isValidQuery(query)) return;

        Set<String> history = getHistorySet();
        String trimmedQuery = query.trim();

        // 移除已存在的（如果存在）
        history.remove(trimmedQuery);

        // 添加到最前面
        Set<String> newHistory = new LinkedHashSet<>();
        newHistory.add(trimmedQuery);
        newHistory.addAll(history);

        // 限制数量
        if (newHistory.size() > MAX_HISTORY_SIZE) {
            List<String> list = new ArrayList<>(newHistory);
            newHistory = new LinkedHashSet<>(list.subList(0, MAX_HISTORY_SIZE));
        }

        preferences.edit().putStringSet(KEY_HISTORY, newHistory).apply();
    }

    // 获取搜索历史集合
    public Set<String> getHistorySet() {
        return new LinkedHashSet<>(preferences.getStringSet(KEY_HISTORY, new LinkedHashSet<>()));
    }

    // 获取搜索历史列表（新的在前）
    public List<String> getHistoryList() {
        Set<String> historySet = getHistorySet();
        List<String> historyList = new ArrayList<>(historySet);
        // LinkedHashSet保持插入顺序，所以最新的已经在最后面
        Collections.reverse(historyList); // 反转后最新的就在最前面
        return historyList;
    }

    // 获取指定数量的历史记录
    public List<String> getRecentHistory(int limit) {
        List<String> allHistory = getHistoryList();
        if (allHistory.size() <= limit) {
            return allHistory;
        }
        return allHistory.subList(0, limit);
    }

    // 清空搜索历史
    public void clearHistory() {
        preferences.edit().remove(KEY_HISTORY).apply();
    }

    // 删除单条历史记录
    public void removeHistoryItem(String query) {
        Set<String> history = getHistorySet();
        history.remove(query.trim());
        preferences.edit().putStringSet(KEY_HISTORY, history).apply();
    }

    // 检查查询是否有效
    private boolean isValidQuery(String query) {
        return query != null &&
                !query.trim().isEmpty() ;
//                && query.trim().length() >= 2;
    }

    // 获取历史记录数量
    public int getHistoryCount() {
        return getHistorySet().size();
    }

    // 检查是否包含某个搜索词
    public boolean contains(String query) {
        return getHistorySet().contains(query.trim());
    }
}
