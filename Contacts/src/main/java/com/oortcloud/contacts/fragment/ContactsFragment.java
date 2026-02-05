package com.oortcloud.contacts.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.activity.SearchActivity;
import com.oortcloud.contacts.adapter.DepartAndUserAdapter;
import com.oortcloud.contacts.adapter.HigherDepartmentAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.DeptInfo;
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
 * @filename:
 * @author: zzj/@date: 2022/4/11 11:48
 * @version： v1.0
 * @function： SSO 部门与用户树
 */
public class ContactsFragment extends BaseFragment implements SearchView.SearchViewListener, LetterIndexView.OnLetterChangedListener {
    // 声明变量
    private TextView mTitle;
    private SearchView mSearchView;
    private TextView mSearchTV;
    private RecyclerView mDeptRv;
    private RecyclerView mDeptAndUserRv;
    private LetterIndexView mLetterIndexView;
    private FrameLayout mEmpty;



    private DeptInfo mDeptInfo;
    //当前部门编码
    private String mCurrentDeptCode;
    private List<Department> mDeptList;
    ;
    LinearLayoutManager linearLayoutManager;
    HigherDepartmentAdapter higherAdapter;
    GridLayoutManager manager;


    private List<Sort> mSortList = new ArrayList<>();
    private DepartAndUserAdapter mDepAndUsrAdapter;
    private com.oortcloud.contacts.databinding.ActivityDeptUserLayoutBinding binding;

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    private String showType;

    public List getmSelectUsers() {
        return mSelectUsers;
    }

    public void setmSelectUsers(List mSelectUsers) {
        this.mSelectUsers = mSelectUsers;
    }

    private List mSelectUsers;

    private ItemCheckListener mItemCheckListener;

    public interface ItemCheckListener {
        void onCheckInItemClick(UserInfo user);
        void onCheckOutItemClick(UserInfo user);
    }

    public void  setOnItemCheckListener(ItemCheckListener listener){
        mItemCheckListener = listener;
    }

    public void  refreshUserCheckStatu(List selectUsers){
        mDepAndUsrAdapter.refreshUserCheckStatu(selectUsers);
    }

    @Override
    protected View getRootView() {
        binding = ActivityDeptUserLayoutBinding.inflate(getLayoutInflater());
        // 通过 ViewBinding 赋值
        mTitle = binding.tvTitle;
        mSearchView = binding.searchView;
        mSearchTV = binding.searchTv;
        mDeptRv = binding.rvDepart;
        mDeptAndUserRv = binding.rvHailFellow;
        mLetterIndexView = binding.letterIndexView;
        mEmpty = binding.flEmpty;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dept_user_layout;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle != null) {
            mCurrentDeptCode = bundle.getString(Constants.DEPARTMENT_CODE);
            mDeptInfo = (DeptInfo) bundle.getSerializable(Constants.OBJ);
        }

    }

    @Override
    protected void initView() {

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mDeptRv.setLayoutManager(linearLayoutManager);
        higherAdapter = new HigherDepartmentAdapter(mDeptList);
        mDeptRv.setAdapter(higherAdapter);

        manager = new GridLayoutManager(mContext, 1);
        mDeptAndUserRv.setLayoutManager(manager);
        mDepAndUsrAdapter = new DepartAndUserAdapter(mContext, mSortList,showType == null ? Constants.DEPT_USER : Constants.SELECT_CONTACTS);
        mDeptAndUserRv.setAdapter(mDepAndUsrAdapter);

    }

    @Override
    protected void initData() {
        //获取当前用户部门
        mCurrentDeptCode = UserInfoUtils.getInstance(mContext).getLoginUserInfo().getOort_depcode();
        HttpResult.getDeptAndUserTree(mCurrentDeptCode, 1, Constants.DEPT_USER);
    }

    @Override
    protected void initEvent() {
        higherAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mCurrentDeptCode = ((Department) adapter.getItem(position)).getOort_dcode();
                HttpResult.getDeptAndUserTree(mCurrentDeptCode, 1, Constants.DEPT_USER);
            }
        });

//        mSearchView.setSearchViewListener(this);
        mLetterIndexView.setOnLetterChangedListener(this);
        mSearchTV.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SearchActivity.class));
        });
        mDepAndUsrAdapter.setOnItemClickListener(new DepartAndUserAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mCurrentDeptCode = ((Department) mSortList.get(position)).getOort_dcode();
                HttpResult.getDeptAndUserTree(mCurrentDeptCode, 1, Constants.DEPT_USER);
            }

            @Override
            public void onCheckItemClick(int statu, UserInfo user) {

                if(statu == 1){
                    mItemCheckListener.onCheckInItemClick(user);
                }
                if(statu == 0){
                    mItemCheckListener.onCheckOutItemClick(user);
                }
            }

            @Override
            public void onItemTagDelClick(int position, UserInfo user) {

            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (Constants.DEPT_USER.equals(event.getDataType())) {
            List data = event.getList();
            if (data != null && data.size() > 0 && data.get(0) instanceof Sort) {
                mSortList = data;


                mDepAndUsrAdapter.updateList(mSortList);
                if(mSelectUsers != null){
                    mDepAndUsrAdapter.refreshUserCheckStatu(mSelectUsers);
                }
                mDeptAndUserRv.smoothScrollToPosition(0);
                mDeptAndUserRv.setVisibility(View.VISIBLE);
                mEmpty.setVisibility(View.GONE);

                mLetterIndexView.setVisibility(View.GONE);
                linearLayoutManager.scrollToPosition(mSortList.size() - 1);

            } else {
                mDeptAndUserRv.setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);
            }

        } else if (mCurrentDeptCode.equals(event.getDataType())) {
            Department department = event.getDepartment();
            flushDepartNameList(department);
        }
    }

    private void flushDepartNameList(Department department) {

        mDeptList = DeptUtils.splitDepartment(department.getOort_dpath(), department.getOort_dcodepath());

        higherAdapter.setNewData(mDeptList);
        higherAdapter.notifyDataSetChanged();
        mDeptRv.smoothScrollToPosition(mDeptList.size() -1);
        mTitle.setText(mDeptList.get(mDeptList.size() - 1).getOort_dname());
        if (dialog != null) {
            dialog.dismiss();
        }


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
}
