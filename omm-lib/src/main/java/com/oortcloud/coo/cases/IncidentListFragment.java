package com.oortcloud.coo.cases;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jun.baselibrary.http.HttpUtils;
import com.jun.framelibrary.http.callback.HttpEngineCallBack;
import com.oort.weichat.R;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.coo.bean.Data;
import com.oortcloud.coo.bean.Records;
import com.oortcloud.coo.bean.Result;
import com.oortcloud.coo.cases.ApiConstants;
import com.oortcloud.coo.cases.IncidentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IncidentListFragment extends Fragment {

    private RecyclerView recyclerView;
    private IncidentAdapter adapter;
    private int tabType; // 0: 我参与的案件, 1: 我的案件, 2: 我单位的案件
    
    // 搜索参数
    private HashMap<String, Object> searchParams;
    
    public static IncidentListFragment newInstance(int tabType) {
        IncidentListFragment fragment = new IncidentListFragment();
        Bundle args = new Bundle();
        args.putInt("tab_type", tabType);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabType = getArguments().getInt("tab_type", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incident_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_incidents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new IncidentAdapter();
        adapter.setOnItemDeleteListener(new IncidentAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDeleted(Records record) {
                // 实际项目中应该调用删除API
                // 这里暂时重新加载数据
                refreshData();
                Log.e("zq", "onItemDeleted");
                // 可以在这里添加删除成功的提示
                Toast.makeText(getContext(), "案件已删除", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        // 根据tabType加载不同的数据
        refreshData();

        return view;
    }


    public void refreshData() {
        initData();
    }

    /**
     * 设置搜索参数并刷新数据
     */
    public void setSearchParams(HashMap<String, Object> searchParams) {
        this.searchParams = searchParams;
        refreshData();
    }

    /**
     * 清除搜索参数并刷新数据
     */
    public void clearSearchParams() {
        this.searchParams = null;
        refreshData();
    }

    private void initData(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("current", 1);
        params.put("size", 100);

        // 根据tabType添加不同的查询参数
        switch (tabType) {
            case 0: // 我参与的案件
                params.put("participantType", "involved");
                break;
            case 1: // 我的案件
                params.put("participantType", "my");
                break;
            case 2: // 我单位的案件
                params.put("participantType", "unit");
                break;
        }
        if (searchParams != null){
            params.putAll(searchParams);
        }

        HttpUtils.with(getContext())
                .get()
                .url(ApiConstants.COORDINATION_LIST)
                .addHeader("accessToken", IMUserInfoUtil.getInstance().getToken())
                .addBody(params)
                .execute(new HttpEngineCallBack<Result<Data<Records>>>() {
                    @Override
                    public void onSuccess(Result<Data<Records>> objResult) {
                        if (objResult.getCode() == 200 && objResult.getData() != null) {
                            List<Records> recordsList = objResult.getData().getRecords();
                            if (recordsList != null && !recordsList.isEmpty()) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    adapter.updateData(recordsList);
                                });
                            } else {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (adapter != null){
                                        adapter.updateData(new ArrayList<>());
                                    }

                                    Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(getContext(), "获取数据失败: " + objResult.getMsg(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                });

    }
    
    /**
     * 添加搜索参数到请求参数中（只有当值不为空时才添加）
     */
    private void addSearchParam(HashMap<String, Object> params, String key, Object value) {
        if (value != null && !value.toString().trim().isEmpty()) {
            params.put(key, value);
        }
    }
}


