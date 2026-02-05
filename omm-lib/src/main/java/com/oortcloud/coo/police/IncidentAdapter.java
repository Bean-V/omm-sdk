package com.oortcloud.coo.police;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jun.baselibrary.http.HttpUtils;
import com.jun.framelibrary.http.callback.HttpEngineCallBack;
import com.oort.weichat.R;
import com.oortcloud.appstore.dailog.DialogHelper;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.coo.bean.Records;
import com.oortcloud.coo.bean.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder> {

    public List<Records> recordsList;
    private int incidentType; // 0: 我参与的警情, 1: 我的警情, 2: 我单位的警情
    private Context context;
    private static OnItemDeleteListener onItemDeleteListener;

    public IncidentAdapter() {
        this.recordsList = new ArrayList<>();
    }

    public void setIncidentType(int type) {
        this.incidentType = type;
    }

    public void updateData(List<Records> records) {
        this.recordsList.clear();
        if (records != null) {
            this.recordsList.addAll(records);
        }
        notifyDataSetChanged();
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    /**
     * 删除项监听器接口
     */
    public interface OnItemDeleteListener {
        void onItemDeleted(Records record);
    }

    @NonNull
    @Override
    public IncidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incident_card, parent, false);
        return new IncidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidentViewHolder holder, int position) {
        Records record = recordsList.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    static class IncidentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSeverityLevel;
        private final TextView tvIncidentId;
        private final TextView tvIncidentDescription;
        private final TextView tvLocation;
        private final TextView tvReportingUnit;
        private final TextView tvTimestamp;
        private final TextView tvCategoryTag;
        private final View severityContainer;

        public IncidentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeverityLevel = itemView.findViewById(R.id.tv_severity_level);
            tvIncidentId = itemView.findViewById(R.id.tv_incident_id);
            tvIncidentDescription = itemView.findViewById(R.id.tv_incident_description);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvReportingUnit = itemView.findViewById(R.id.tv_reporting_unit);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvCategoryTag = itemView.findViewById(R.id.tv_category_tag);
            severityContainer = itemView.findViewById(R.id.severity_level_and_id_container);
        }

        public void bind(Records record) {
            // 解析警情级别
            int severityLevel = parseSeverityLevel(record.getAlertLevel());

            // 设置严重程度标签和背景
            switch (severityLevel) {
                case 1:
                    tvSeverityLevel.setText("一级");
                    severityContainer.setBackgroundResource(R.drawable.severity_level_one_background);
                    break;
                case 2:
                    tvSeverityLevel.setText("二级");
                    severityContainer.setBackgroundResource(R.drawable.severity_level_two_background);
                    break;
                case 3:
                    tvSeverityLevel.setText("三级");
                    severityContainer.setBackgroundResource(R.drawable.severity_level_three_background);
                    break;
                case 4:
                    tvSeverityLevel.setText("四级");
                    severityContainer.setBackgroundResource(R.drawable.severity_level_four_background);
                    break;
                default:
                    tvSeverityLevel.setText("未知");
                    severityContainer.setBackgroundResource(R.drawable.severity_level_trapezoid_background);
                    break;
            }

            // 设置基本信息
            tvIncidentId.setText(record.getReceivingAlertNumber());
            tvIncidentDescription.setText(record.getAlertContent());
            tvLocation.setText(record.getIncidentLocation());
            tvReportingUnit.setText(record.getThirdResponseUnit());
            tvTimestamp.setText(""); // API中没有时间字段，可以显示当前时间或空
            tvCategoryTag.setText(record.getAlertCategory());

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                openIncidentDetail(record);
            });

            itemView.setOnLongClickListener(v -> {
                showDeleteConfirmDialog(record);
                return true; // 返回true表示消费了长按事件
            });
        }

        private int parseSeverityLevel(String alertLevel) {
            if (alertLevel == null || alertLevel.isEmpty()) {
                return 1; // 默认一级
            }
            try {
                return Integer.parseInt(alertLevel);
            } catch (NumberFormatException e) {
                return 1; // 解析失败时默认为一级
            }
        }

        private void openIncidentDetail(Records record) {
            // 直接传递Records数据和接警单编号
            Intent intent = new Intent(itemView.getContext(), IncidentDetailActivity.class);
            intent.putExtra("incident_data", record);
            intent.putExtra("receivingAlertNumber", record.getReceivingAlertNumber());
            itemView.getContext().startActivity(intent);
        }

        private void showDeleteConfirmDialog(Records record) {
            DialogHelper.getConfirmDialog(itemView.getContext(),
                    "确定要删除接警单编号为 " + record.getReceivingAlertNumber() + " 的警情吗？"
                    , (dialogInterface, i) -> {

                        deleteIncident(record);
                    }, (dialogInterface, i) -> {

                    }).show();
        }

        private void deleteIncident(Records record) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
            params.put("ids", record.getId());
//            params.put("receivingAlertNumber", record.getReceivingAlertNumber());
            HttpUtils.with(itemView.getContext())
                    .post()
                    .url(ApiConstants.COORDINATION_REMOVE)
                    .addHeader("accessToken", IMUserInfoUtil.getInstance().getToken())
                    .addBody(params)
                    .execute(new HttpEngineCallBack<Result>() {
                        @Override
                        public void onSuccess(Result objResult) {
                            if (objResult.getCode() == 200) {
                                // 通知父Fragment删除成
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (onItemDeleteListener != null) {
                                        onItemDeleteListener.onItemDeleted(record);
                                    }
                                });

                            }

                        }
                    });
        }
    }

}
