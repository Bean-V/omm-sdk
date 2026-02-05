package com.oortcloud.contacts.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.activity.DepartAndPersonnelActivity;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.DeptInfo;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.databinding.OrdHeadLayoutBinding;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @ProjectName: omm-master
 * @FileName: HeaderView.java
 * @Function: 通讯录RecycleView 头部
 * @Author: zhangzhijun / @CreateDate: 20/03/14 21:15
 * @Version: 1.0
 */
public class HeaderView extends LinearLayout implements View.OnClickListener {
    private OrdHeadLayoutBinding binding; // ViewBinding 对象
    private Context mContext;
    public Department mDepartment;
    public DeptInfo mDeptInfo;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        // 初始化 ViewBinding
        binding = OrdHeadLayoutBinding.inflate(LayoutInflater.from(context), this, true);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initViews();
    }

    private void initViews() {
        // 绑定点击事件
        binding.groupChatLayout.setOnClickListener(this);
        binding.newFriendsLayout.setOnClickListener(this);
        binding.labelLayout.setOnClickListener(this);
        binding.higherDepartmentLayout.setOnClickListener(this);
        binding.departmentLayout.setOnClickListener(this);

        if (UserInfoUtils.getInstance(mContext).getLoginUserInfo() == null) {
            return;
        }
        HttpRequestCenter.getDeptInfo(UserInfoUtils.getInstance(mContext).getLoginUserInfo().getOort_depcode())
                .subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String s) {
                        Result<Data<Department>> result = new Gson().fromJson(s, new TypeToken<Result<Data<Department>>>() {
                        }.getType());
                        if (result.isOk()) {
                            if (result.getData().getDeptInfo() != null) {
                                mDepartment = result.getData().getDeptInfo();
                                // 更新部门名称
                                binding.tvDepartmentName.setText(mDepartment.getOort_dname());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(EventMessage event) {
        if (event.getDeptInfo() != null) {
            mDeptInfo = event.getDeptInfo();
        }
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        if (viewID == R.id.group_chat_layout) {
            // 处理群聊布局点击
        } else if (viewID == R.id.new_friends_layout) {
            // 处理新朋友布局点击
        } else if (viewID == R.id.label_layout) {
            DepartAndPersonnelActivity.actionStart(mContext, null, null);
        } else if (viewID == R.id.higher_department_layout) {
            if (mDepartment != null) {
                DepartAndPersonnelActivity.actionStart(mContext, mDepartment, mDepartment.getOort_pdcode());
            }
        } else if (viewID == R.id.department_layout) {
            if (mDepartment != null) {
                DepartAndPersonnelActivity.actionStart(mContext, mDepartment, mDepartment.getOort_dcode());
            } else {
                Toast.makeText(mContext, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}