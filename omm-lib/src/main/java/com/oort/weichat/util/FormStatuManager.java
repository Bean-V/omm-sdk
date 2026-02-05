package com.oort.weichat.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FormStatuManager {
    private static final String BASE_URL = Constant.BASE_URL;
    private static FormStatuManager instance;
    private OkHttpClient client;
    private Map<String, AppReviewResTask> formMap = new HashMap<>();
    private Map<String, List<FormObserver>> formObservers = new HashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRefreshing = false;

    // 表单状态观察者接口
    public interface FormObserver {
        void onFormStatusChanged(String formId);
    }

    private FormStatuManager() {
        // 初始化OkHttpClient
        client = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    public static synchronized FormStatuManager getInstance() {
        if (instance == null) {
            instance = new FormStatuManager();
        }
        return instance;
    }

    // 注册表单观察者
    public void registerFormObserver(String formId, FormObserver observer) {
        formObservers.computeIfAbsent(formId, k -> new ArrayList<>()).add(observer);
    }

    // 取消注册
    public void unregisterFormObserver(String formId, FormObserver observer) {
        List<FormObserver> observers = formObservers.get(formId);
        if (observers != null) {
            if(formMap.containsKey(formId)) {
                formMap.remove(formId);
            }
            observers.remove(observer);
        }
    }

    // 获取表单
    public AppReviewResTask getFormById(String formId) {
        return formMap.get(formId);
    }

    // 启动定时刷新
    public void startPeriodicRefresh() {
        if (isRefreshing) return;

        isRefreshing = true;
        refreshPeriodically();
    }

    // 停止定时刷新
    public void stopPeriodicRefresh() {
        isRefreshing = false;
        handler.removeCallbacksAndMessages(null);
    }

    // 定时刷新
    private void refreshPeriodically() {
        if (!isRefreshing) return;

        refreshAllForms();

        // 5秒后再次刷新
        handler.postDelayed(this::refreshPeriodically, 1000);
    }

    // 获取单个表单状态
    public void fetchFormDetails(String formId, FormCallback callback) {
        String url = BASE_URL + "oort/oortcloud-workflowforms/workflow/process/todoList?pageNum=1&pageSize=10&taskId=" + formId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("requestType", "app")
                .addHeader("AccessToken", FastSharedPreferences.get("USERINFO_SAVE").getString("token" , ""))
                .addHeader("appID", "14870103ffab492e8e1b3b5bc094ce7e")
                .addHeader("Authorization", FastSharedPreferences.get("USERINFO_SAVE").getString("token" , ""))
                .addHeader("secretkey", "6258893ee23c477b87ab0936fbdad9b0")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(() -> callback.onError("获取表单失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    handler.post(() -> callback.onError("HTTP错误: " + response.code()));
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    AppReviewResTask res = JSON.parseObject(jsonData, AppReviewResTask.class);

                    if (res != null && res.getRows() != null) {
                        // 确保rows列表不为空
//                        AppReviewResTask.RowsBean row = res.getRows().get(0);
                        formMap.put(formId, res);
                        handler.post(() -> callback.onSuccess(res));
                    } else {
                        handler.post(() -> callback.onError("获取表单数据为空"));
                    }
                } catch (Exception e) {
                    handler.post(() -> callback.onError("解析数据失败: " + e.getMessage()));
                }
            }
        });
    }

    // 刷新所有表单状态
    private void refreshAllForms() {
        List<String> formIds = new ArrayList<>(formMap.keySet());
        if (formIds.isEmpty()) return;

        // 对每个表单ID单独发送请求
        for (String formId : formIds) {
            String url = BASE_URL + "oort/oortcloud-workflowforms/workflow/process/todoList?pageNum=1&pageSize=10&taskId=" + formId;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("requestType", "app")
                    .addHeader("AccessToken", FastSharedPreferences.get("USERINFO_SAVE").getString("token" , ""))
                    .addHeader("appID", "14870103ffab492e8e1b3b5bc094ce7e")
                    .addHeader("Authorization", FastSharedPreferences.get("USERINFO_SAVE").getString("token" , ""))
                    .addHeader("secretkey", "6258893ee23c477b87ab0936fbdad9b0")
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("FormStatuManager", "获取表单状态失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e("FormStatuManager", "HTTP错误: " + response.code());
                        return;
                    }

                    try {
                        String jsonData = response.body().string();
                        AppReviewResTask res = JSON.parseObject(jsonData, AppReviewResTask.class);

                        if (res != null && res.getRows() != null) {


                            formMap.put(formId, res);

                            notifyFormStatusChanged(formId);



//
                        }else{

                        }
                    } catch (Exception e) {
                        Log.e("FormStatuManager", "解析表单数据失败: " + e.getMessage());
                    }
                }
            });
        }
    }

    // 从JSON更新表单数据
    private void updateFormsFromJson(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray rowsArray = jsonObject.optJSONArray("rows");

            if (rowsArray != null) {
                for (int i = 0; i < rowsArray.length(); i++) {
                    JSONObject rowObj = rowsArray.getJSONObject(i);
                    String formId = rowObj.optString("taskId");
                    String status = rowObj.optString("processStatus");

                    // 检查状态是否变化
                    AppReviewResTask oldForm = formMap.get(formId);
                    String oldStatus = oldForm != null && oldForm.getRows() != null && !oldForm.getRows().isEmpty()
                            ? oldForm.getRows().get(0).getProcessStatus() : null;

                    if (oldStatus != null && !oldStatus.equals(status)) {
                        // 通知观察者
                        notifyFormStatusChanged(formId);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 通知表单状态变更
    private void notifyFormStatusChanged(String formId) {
        List<FormObserver> observers = formObservers.get(formId);
        if (observers != null) {
            for (FormObserver observer : observers) {
                handler.post(() -> observer.onFormStatusChanged(formId));
            }
        }
    }

    // 添加或更新表单
    public void addOrUpdateForm(AppReviewResTask form) {
        if (form != null && form.getRows() != null && !form.getRows().isEmpty()) {
            formMap.put(form.getRows().get(0).getTaskId(), form);
        }
    }

    // 表单请求回调接口
    public interface FormCallback {
        void onSuccess(AppReviewResTask form);
        void onError(String error);
    }

}

