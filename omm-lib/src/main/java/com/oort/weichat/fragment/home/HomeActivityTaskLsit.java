package com.oort.weichat.fragment.home;

import static com.oortcloud.basemodule.constant.Constant.PUBLIC_NUM;
import static com.oortcloud.basemodule.constant.Constant.PUBLIC_USERID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.oort.weichat.R;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.fragment.adapter.AdapterTasks;
import com.oort.weichat.fragment.entity.OORTGANews;
import com.oort.weichat.ui.base.BaseActivity;
import com.oortcloud.appstore.activity.AppManagerService;
import com.oortcloud.basemodule.im.TaskMsgInfoBean;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeActivityTaskLsit extends BaseActivity {

    private int mTid;
    private WebView mWebView;
    private RecyclerView rvList4;
    private AdapterTasks adpTasks;
    private OORTGANews mNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tasks);

        rvList4 = findViewById(R.id.rv_task);
        adpTasks = new AdapterTasks(new ArrayList<>());
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText("任务中心");
        ImageView iv_left = (ImageView) findViewById(R.id.iv_title_left);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        adpTasks.setmContext(this);
        rvList4.setAdapter(adpTasks);
        String imuserid= UserInfoUtils.getInstance(this).getLoginUserInfo().getImuserid();

        getTaskInfo(imuserid,PUBLIC_USERID,PUBLIC_NUM);

    }

    void getTaskInfo(String userid,String publicId,int pagesize){
        List<TaskMsgInfoBean> taskInfos =  ChatMessageDao.getInstance().getTaskMsgList(userid,publicId,pagesize);
        if(taskInfos.size() > 0){
            findViewById(R.id.iv_empty).setVisibility(View.GONE);
            adpTasks.refresh(taskInfos);
            adpTasks.setOnItemClickListener(new AdapterTasks.ItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    TaskMsgInfoBean t = taskInfos.get(position);
                    startService(t.getAppid());
                    OperLogUtil.msg("startService_taskInfos" + JSON.toJSONString(taskInfos));
                }

                @Override
                public void onItemCheckClick(int position) {

                }

                @Override
                public void onItemLongClick(boolean edit) {

                }
            });
        }else{
            findViewById(R.id.iv_empty).setVisibility(View.VISIBLE);
        }

    }
    private void  startService(String packageName){


        //String packageName = "com.jwb_home.oort";
        String params = "";
        Intent intent = new Intent(this , AppManagerService.class);
        intent.putExtra("packageName" , packageName);
        intent.putExtra("params" , params);
        startService(intent);


    }
    public static void start(Context context) {
        Intent starter = new Intent(context, HomeActivityTaskLsit.class);
        context.startActivity(starter);
    }

}