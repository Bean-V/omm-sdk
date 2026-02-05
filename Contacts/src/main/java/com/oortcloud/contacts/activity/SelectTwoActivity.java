package com.oortcloud.contacts.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.adapter.HigherDepartmentAdapter;
import com.oortcloud.contacts.adapter.SelectContactsAdapter;
import com.oortcloud.contacts.adapter.SelectDepartAndUserAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.DeptInfo;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.databinding.ActivitySelectTwoBinding;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.observer.DataHandle;
import com.oortcloud.contacts.observer.Observer;
import com.oortcloud.contacts.observer.SelectObserver;
import com.oortcloud.contacts.utils.DeptUtils;
import com.oortcloud.contacts.utils.PinyinUtils;
import com.oortcloud.contacts.widget.LetterIndexView;
import com.oortcloud.contacts.widget.SearchView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2021/4/21 17:47
 * @version： v1.0
 * @function： 选择详情人员
 */

public class SelectTwoActivity extends BaseActivity implements SearchView.SearchViewListener, LetterIndexView.OnLetterChangedListener, Observer {
    private ImageView mBackView;
    private TextView mTitle;
    private TextView mConfirm;
    private RecyclerView mDepartNameRV;
    private RecyclerView mDepartPersonRV;
    private LetterIndexView mLetterIndexView;
    private FrameLayout fl_empty;
    private RecyclerView mSelectRV;
    private HorizontalScrollView mHsv;
    private EditText mSearchText;


    private DeptInfo mDeptInfo;

    private List<Department> mDepartNameList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    HigherDepartmentAdapter higherAdapter;
    GridLayoutManager manager;


    private List<Sort> mSortList = new ArrayList<>();
    private SelectDepartAndUserAdapter mDepAndUsrAdapter;

    private SelectContactsAdapter mSelectContactsAdapter;

    private DataHandle mDataHandle;
    private SelectObserver mObserver;
    private com.oortcloud.contacts.databinding.ActivitySelectTwoBinding binding;

    @Override
    protected View getRootView() {
        binding = ActivitySelectTwoBinding.inflate(getLayoutInflater());
        // 通过 ViewBinding 赋值
        mBackView = binding.ivTitle.imgBack;
        mTitle = binding.ivTitle.tvTitle;
        mConfirm = binding.ivTitle.confirmBtn;
        mDepartNameRV = binding.rvDepart;
        mDepartPersonRV = binding.rvHailFellow;
        mLetterIndexView = binding.letterIndexView;
        fl_empty = binding.flEmpty;
        mSelectRV = binding.selectRv;
        mHsv = binding.scrollView;
        mSearchText = binding.searchEt;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_two;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        mObserver =  SelectObserver.getInstance();
        mObserver.addBuyUser(this);
        mDataHandle = DataHandle.getInstance();
        mDataHandle.addActivity(this);
        mDeptInfo = (DeptInfo) getIntent().getSerializableExtra(Constants.OBJ);

    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.select_person_c));
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mDepartNameRV.setLayoutManager(linearLayoutManager);
        higherAdapter = new HigherDepartmentAdapter(mDepartNameList);
        mDepartNameRV.setAdapter(higherAdapter);

        manager = new GridLayoutManager(mContext, 1);
        mDepartPersonRV.setLayoutManager(manager);
        mDepAndUsrAdapter = new SelectDepartAndUserAdapter(mContext, mSortList);

        mSelectRV.setLayoutManager( new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL , false));
        mSelectContactsAdapter = new SelectContactsAdapter(mContext , mDataHandle.getUserData());
        mSelectRV.setAdapter(mSelectContactsAdapter);

        //监听回调
        mObserver.addBuyUser(mDepAndUsrAdapter);
        mObserver.addBuyUser(mSelectContactsAdapter);


    }

    @Override
    protected void initData() {
        if (mDeptInfo != null) {
            if (!TextUtils.isEmpty(mDeptInfo.getOort_dpath())) {
                mDepartNameList = DeptUtils.splitDepartment(mDeptInfo.getOort_dpath(), mDeptInfo.getOort_dcodepath());
            }
            HttpResult.getDeptAndUserTree(mDeptInfo.getOort_dcode() , 1 , Constants.SELECT_CONTACTS);
        }

    }

    @Override
    protected void initEvent() {
        higherAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {

            String depCode = ((Department) adapter.getItem(position)).getOort_dcode();
            HttpResult.getDeptAndUserTree(depCode , 1 , Constants.SELECT_CONTACTS);
        });
        mLetterIndexView.setOnLetterChangedListener(this);
        mBackView.setOnClickListener(v -> {
            finish();
        });

        mConfirm.setOnClickListener(v ->  {

            if ( mDataHandle.getUserData().size() >  0){
                DataHandle.getInstance().toJson();
            }else {
                Toast.makeText(mContext , "请选择您要添加的好友" , Toast.LENGTH_SHORT).show();
            }

        });
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDepAndUsrAdapter.updateList( PinyinUtils.filterSort(s.toString() , mSortList));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (Constants.SELECT_CONTACTS.equals(event.getDataType())){
            List data = event.getList();
            if (data != null && data.size() > 0 && data.get(0) instanceof Sort) {

                mSortList = data;
                mDepartPersonRV.setAdapter(mDepAndUsrAdapter);
                mDepAndUsrAdapter.updateList(mSortList);

                if (event.getType() == 0) {

                    Department department = (Department) mSortList.get(mSortList.size() - 1);
                    flushDepartNameList(department, event.getType());
                    mLetterIndexView.setVisibility(View.GONE);
                    linearLayoutManager.scrollToPosition(mSortList.size() - 1);
                } else if (event.getType() == 1) {

                    UserInfo userInfo = (UserInfo) mSortList.get(0);
                    flushDepartNameList(new Department(userInfo.getOort_depcode(), userInfo.getOort_depname()), event.getType());
//                mLetterIndexView.setVisibility(View.VISIBLE);
                }
            }
        }


    }

    private void flushDepartNameList(Department department, int type) {
        if (type == 0) {
            mDepartNameList = DeptUtils.splitDepartment(department.getOort_dpath(), department.getOort_dcodepath());
        } else if (type == 1) {
            if (!mDepartNameList.contains(department)) {
                mDepartNameList.add(department);
            }

        }
        if (mDepartNameList.size() == 0) {
            mDepartNameList.add(department);
        }
        higherAdapter.setNewData(mDepartNameList);
        higherAdapter.notifyDataSetChanged();
        mLoadingDialog.dismiss();

    }

    //搜索
    @Override
    public void onSearch(CharSequence text) {
//        mDepAndUsrAdapter.updateList( PinyinUtils.filterUserInfo(text.toString() , mSortList));
    }

    //字母索引
    @Override
    public void onChanged(String s, int position) {
        int pos = mDepAndUsrAdapter.getPositionForSection(s.charAt(0));
        if (pos != -1) {
            manager.scrollToPositionWithOffset(pos, 0);
        }
    }

    public static void actionStart(Context context, DeptInfo deptInfo) {
        Intent intent = new Intent(context, SelectTwoActivity.class);
        intent.putExtra(Constants.OBJ, deptInfo);
        context.startActivity(intent);

    }
    @Override
    protected void onStart() {
        super.onStart();
        notifyMsg();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mObserver.deleteBuyUser(this);
        //监听回调
        mObserver.deleteBuyUser(mDepAndUsrAdapter);
        mObserver.deleteBuyUser(mSelectContactsAdapter);
        mDataHandle.removeActivity(this);
    }

    @Override
    public void notifyMsg() {
        List data =  mDataHandle.getUserData();
        if (mDataHandle.getUserData() != null){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mHsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            });
            if (data.size() > 0){

                mConfirm.setText("确定(" +data.size() + ")");
            }else {
                mConfirm.setText("确定");
            }

        }

    }
}