package com.oortcloud.contacts.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.adapter.DepartAndUserAdapter;
import com.oortcloud.contacts.adapter.HigherDepartmentAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.databinding.ActivityDeptUserLayoutBinding;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.utils.DeptUtils;
import com.oortcloud.contacts.widget.LetterIndexView;
import com.oortcloud.contacts.widget.SearchView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: omm-master
 * @FileName: DepartAndPersonnelActivity.java
 * @Function: 展示部门和人员信息
 * @Author: zzj / @CreateDate: 20/03/15 06:05
 * @Version: 1.0
 */
public class DepartAndPersonnelActivity extends BaseActivity implements  SearchView.SearchViewListener  , LetterIndexView.OnLetterChangedListener {
    private TextView mTitle;
    private SearchView mSearchView;
    private RecyclerView mDepartNameRV;
    private RecyclerView mDepartPersonRV;
    private LetterIndexView mLetterIndexView;

    private String mDepartID;
    private Department mDepartment;

    private  List<Department> mDepartNameList =  new ArrayList<>();;
    LinearLayoutManager linearLayoutManager;
    HigherDepartmentAdapter higherAdapter;
    GridLayoutManager manager;


    private List<Sort> mSortList = new ArrayList<>();
    private DepartAndUserAdapter mDepAndUsrAdapter;
    private com.oortcloud.contacts.databinding.ActivityDeptUserLayoutBinding binding;

    @Override
    protected View getRootView() {
        binding = ActivityDeptUserLayoutBinding.inflate(getLayoutInflater());
        // 通过 ViewBinding 赋值
        mTitle = binding.tvTitle;
        mSearchView = binding.searchView;
        mDepartNameRV = binding.rvDepart;
        mDepartPersonRV = binding.rvHailFellow;
        mLetterIndexView = binding.letterIndexView;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dept_user_layout;
    }

    @Override
    protected void initBundle(Bundle bundle) {

        mDepartID =  getIntent().getStringExtra(Constants.DEPARTMENT_CODE);
        mDepartment =  (Department) getIntent().getSerializableExtra(Constants.OBJ);

    }

    @Override
    protected void initView() {

        linearLayoutManager =  new LinearLayoutManager(mContext , LinearLayoutManager.HORIZONTAL ,false );
        mDepartNameRV.setLayoutManager(linearLayoutManager);
        higherAdapter = new HigherDepartmentAdapter(mDepartNameList);
        mDepartNameRV.setAdapter(higherAdapter);

        manager = new GridLayoutManager(mContext, 1);
        mDepartPersonRV.setLayoutManager(manager);
        mDepAndUsrAdapter = new DepartAndUserAdapter(mContext, mSortList , Constants.DEPT_USER);
        mDepartPersonRV.setAdapter(mDepAndUsrAdapter);


    }

    @Override
    protected void initData() {
        if (mDepartment != null){
            mDepartNameList = DeptUtils.splitDepartment(mDepartment.getOort_dpath() , mDepartment.getOort_dcodepath());
        }

        HttpResult.getDeptAndUserTree(mDepartID , 1 , Constants.DEPT_USER);

    }

    @Override
    protected void initEvent() {
        higherAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String depCode = ((Department) adapter.getItem(position)).getOort_dcode();
                HttpResult.getDeptAndUserTree(depCode , 1 , Constants.DEPT_USER);
            }
        });

        mSearchView.setSearchViewListener(this);
        mLetterIndexView.setOnLetterChangedListener(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (Constants.DEPT_USER.equals(event.getDataType())){
            List data =  event.getList();
            if (data != null && data.size() > 0 && data.get(0) instanceof Sort){

                mSortList = data;
                mDepAndUsrAdapter.updateList(mSortList);

                if (event.getType() == 0){
                    Department department = (Department)mSortList.get(mSortList.size() -1);
                    flushDepartNameList(department , event.getType());
                    mLetterIndexView.setVisibility(View.GONE);
                    linearLayoutManager.scrollToPosition(mSortList.size()-1);

                } else if(event.getType() == 1){

                    UserInfo userInfo = (UserInfo)mSortList.get(0);
                    flushDepartNameList(new Department(userInfo.getOort_depcode() ,userInfo.getOort_depname()) ,  event.getType());
//                mLetterIndexView.setVisibility(View.VISIBLE);
                }
            }

        }

    }

    private void flushDepartNameList(Department department , int type){
        if (type == 0){
            mDepartNameList =  DeptUtils.splitDepartment(department.getOort_dpath() ,  department.getOort_dcodepath());
        }else if(type == 1){
            if (!mDepartNameList.contains(department)){
                mDepartNameList.add(department);
            }

        }
        if (mDepartNameList.size() == 0){
            mDepartNameList.add(department);
        }
        higherAdapter.setNewData(mDepartNameList);
        higherAdapter.notifyDataSetChanged();
        mTitle.setText(mDepartNameList.get(mDepartNameList.size()-1).getOort_dname());
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
    public static void actionStart(Context context , Department deptInfo , String deptCode){
        Intent intent = new Intent(context, DepartAndPersonnelActivity.class);
        intent.putExtra(Constants.DEPARTMENT_CODE , deptCode);
        intent.putExtra(Constants.OBJ , deptInfo);
        context.startActivity(intent);

    }

}
