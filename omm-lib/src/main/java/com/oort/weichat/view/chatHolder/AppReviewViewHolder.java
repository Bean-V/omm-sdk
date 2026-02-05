package com.oort.weichat.view.chatHolder;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.oort.weichat.R;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.util.AppReviewResTask;
import com.oort.weichat.util.FormStatuManager;
import com.oortcloud.appstore.activity.AppManagerService;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.plugins.oortcloud.imshare.ShareReviewContent;

import org.json.JSONException;
import org.json.JSONObject;

class AppReviewViewHolder extends AChatHolderInterface implements FormStatuManager.FormObserver {

    TextView tvTitle;  // 主标题
    TextView tvText;   // 副标题
    ImageView ivImage; // 图像
    String mLinkUrl;
    String mType;
    String mAppid;
    String mParam;
    private String duuid;

    String mPack_name;
    TextView tvApplyUser;
    TextView tvPart;
    TextView tvReason;
    TextView tvStartTime;
    TextView tvEndTime;
    TextView tv_detail;
    Button btn_apply;
    private ShareReviewContent.DataBean.HistoryProcNodeListBean node;
    private ShareReviewContent.DataBean.ProcessFormListBean form;
    private String formId = "";

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_review : R.layout.chat_to_item_review;
    }

    @Override
    public void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_approval_title);
        tvApplyUser = view.findViewById(R.id.tv_user_name);
        tvPart = view.findViewById(R.id.tv_part);
        tvReason = view.findViewById(R.id.tv_reason);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        tv_detail = view.findViewById(R.id.tv_view_detail);
        btn_apply = view.findViewById(R.id.btn_approve);
        mRootView = view.findViewById(R.id.chat_warp_view);



    }

    @Override
    public void fillData(ChatMessage message) {
        try {
            JSONObject json = new JSONObject(message.getContent());

            ShareReviewContent res = JSON.parseObject(json.optString("content"),ShareReviewContent.class);


            if(res != null && res.getData() != null && res.getData().getHistoryProcNodeList()!= null && !res.getData().getHistoryProcNodeList().isEmpty()) {


                if(formId.isEmpty()) {
                    for (ShareReviewContent.DataBean.HistoryProcNodeListBean hb : res.getData().getHistoryProcNodeList()) {


                        if (hb.getActivityType().equals("startEvent")){
                            node = hb;
                        }
                        if (hb.getAssigneeId() != null && hb.getEndTime() == null &&  hb.getTaskId() != null && hb.getAssigneeId().equals(UserInfoUtils.getInstance(mContext).getLoginUserInfo().getOort_uuid())) {
                            formId = hb.getTaskId();

                            FormStatuManager.getInstance().registerFormObserver(formId, this);
                            break;

                        }
                    }
                }



                AppReviewResTask form = FormStatuManager.getInstance().getFormById(formId);

                if (!formId.isEmpty() && form != null) {
                    updateStatus(form);

                }


                mPack_name = json.getString("package_name");
                mParam =   json.getString("path");
                //node = res.getData().getHistoryProcNodeList().get(res.getData().getHistoryProcNodeList().size() -1);
                tvTitle.setText(res.getData().getWfBasicInfoVo().getProcessName());
                tvApplyUser.setText(node.getAssigneeName());
                tvPart.setText(node.getActivityName());

                if(!res.getData().getProcessFormList().isEmpty()) {
                    tvReason.setText(res.getData().getProcessFormList().get(0).getTitle());
                }else {
                    tvReason.setText("");
                }
                tvStartTime.setText(res.getData().getWfBasicInfoVo().getSubmissionTime());

                tv_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startService(mPack_name);
                    }
                });
                btn_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startService(mPack_name);
                    }
                });
                btn_apply.setVisibility(View.GONE);
                if (!formId.isEmpty()) {
                    fetchFormDetails();
                }
            }



        } catch (JSONException e) {
           // ivImage.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    @Override
    protected void onRootClick(View v) {
       startService(mPack_name);


    }

//    @Override
//    public void onViewRecycled() {
//        super.onViewRecycled();
//        if (formId != null) {
//            FormManager.getInstance().unregisterFormObserver(formId, this);
//        }
//    }
    private void updateStatus(AppReviewResTask form) {

        if(!form.getRows().isEmpty()){
            btn_apply.setVisibility(View.VISIBLE);
        }else{

            FormStatuManager.getInstance().unregisterFormObserver(formId,this);
            btn_apply.setVisibility(View.GONE);
        }

    }

    private void  startService(String packageName){

        if(mContext == null){
            return;
        }
        //String packageName = "com.jwb_home.oort";
        String params = mParam;
        Intent intent = new Intent(mContext , AppManagerService.class);
        intent.putExtra("packageName" , packageName);
        intent.putExtra("params" , params);
        mContext.startService(intent);


    }

    @Override
    public boolean enableSendRead() {
        return true;
    }

    @Override
    public void onFormStatusChanged(String formId) {
        if (this.formId.equals(formId)) {
            AppReviewResTask form = FormStatuManager.getInstance().getFormById(formId);
            if (form != null) {
                updateStatus(form);
            }
        }
    }
    private void fetchFormDetails() {
        FormStatuManager.getInstance().fetchFormDetails(formId, new FormStatuManager.FormCallback() {


            @Override
            public void onSuccess(AppReviewResTask form) {
                updateStatus(form);
            }

            @Override
            public void onError(String error) {
//                statusTextView.setText("加载失败");
//                statusTextView.setTextColor(android.graphics.Color.RED);
            }
        });
    }
}
