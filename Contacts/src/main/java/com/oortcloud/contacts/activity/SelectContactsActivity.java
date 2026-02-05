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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.adapter.SelectContactsAdapter;
import com.oortcloud.contacts.adapter.UNSelectContactsAdapter;
import com.oortcloud.contacts.bean.DeptInfo;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.databinding.ActivitySelectContactsLayoutBinding;
import com.oortcloud.contacts.dialog.OrdCommonDialog;
import com.oortcloud.contacts.observer.DataHandle;
import com.oortcloud.contacts.observer.Observer;
import com.oortcloud.contacts.observer.SelectObserver;
import com.oortcloud.contacts.utils.PinyinUtils;
import com.oortcloud.contacts.widget.HeaderRecyclerView;
import com.oortcloud.contacts.widget.LetterIndexView;
import com.oortcloud.contacts.widget.SearchView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @ProjectName: omm-master
 * @FileName: SelectContactsActivity.java
 * @Function: 选择好友 发起群聊
 * @Author: zzj / @CreateDate: 20/03/20 12:57
 * @Version: 1.0
 */
public class SelectContactsActivity extends BaseActivity implements SearchView.SearchViewListener  , LetterIndexView.OnLetterChangedListener , Observer {

    // 在类顶部声明变量
    private ImageView mBackView;
    private TextView mTitle;
    private TextView mConfirm;
    private SearchView mSearchView;
    private TextView mPDepartmentName;
    private TextView mDepartmentName;
    private HeaderRecyclerView mFriendRV;
    private LetterIndexView mLetterIndexView;
    private RecyclerView mSelectRV;
    private RelativeLayout select_layout;
    private FrameLayout fl_empty;
    private LinearLayout higher_department_ll;
    private LinearLayout department_ll;
    private HorizontalScrollView mHsv;
    private EditText mSearchText;

    // 声明 ViewBinding 对象
// 替换为实际生成的 Binding 类名（如 ActivityMainBinding）

    private  List<DeptInfo> mDepartNameList;
    private List<UserInfo> mUserInfoList;
    private UNSelectContactsAdapter mUNSelectContactsAdapter;

    GridLayoutManager manager;
    private SelectContactsAdapter mSelectContactsAdapter;

    private DataHandle mDataHandle;
    private SelectObserver mObserver;
    private com.oortcloud.contacts.databinding.ActivitySelectContactsLayoutBinding binding;

    @Override
    protected int getLayoutId() {

        return R.layout.activity_select_contacts_layout;
    }
    protected View getRootView() {


        binding = ActivitySelectContactsLayoutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    protected void initBundle(Bundle bundle) {
        mObserver =  SelectObserver.getInstance();
        mObserver.addBuyUser(this);
        mDataHandle = DataHandle.getInstance();
        mDataHandle.addActivity(this);
        //获取数据处理
        if (getIntent() != null){
            String json = getIntent().getStringExtra("data");
            if (!TextUtils.isEmpty(json)){
                Result<List<UserInfo>> result = new Gson().fromJson(json,  new TypeToken<Result<List<UserInfo>>>() {}.getType());
                if (result.getData() != null){
                    mDataHandle.addUser(result.getData());
                }
            }
        }

    }

    @Override
    protected void initView() {


        mBackView = binding.ivTitle.imgBack;
        mTitle = binding.ivTitle.tvTitle;
        mConfirm = binding.ivTitle.confirmBtn;
        mSearchView = binding.searchView;
        mPDepartmentName = binding.tvCompanyName;
        mDepartmentName = binding.tvDepartmentName;
        mFriendRV = binding.rvHailFellow;
        mLetterIndexView = binding.letterIndexView;
        mSelectRV = binding.selectRv;
        select_layout = binding.selectLayout;
        fl_empty = binding.flEmpty;
        higher_department_ll = binding.higherDepartmentLl;
        department_ll = binding.departmentLl;
        mHsv = binding.scrollView;
        mSearchText = binding.searchEt;

        mTitle.setText(getString(R.string.select_person_c));
        manager = new GridLayoutManager(mContext, 1);
        mFriendRV.setLayoutManager(manager);
        mUNSelectContactsAdapter =  new UNSelectContactsAdapter(mContext ,mUserInfoList);
        mFriendRV.setAdapter(mUNSelectContactsAdapter);

        mSelectRV.setLayoutManager( new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL , false));
        mSelectContactsAdapter = new SelectContactsAdapter(mContext , mDataHandle.getUserData());
        mSelectRV.setAdapter(mSelectContactsAdapter);
        //监听回调
        mObserver.addBuyUser(mUNSelectContactsAdapter);
        mObserver.addBuyUser(mSelectContactsAdapter);


    }

    @Override
    protected void initData() {
//        HttpRequestCenter.getdeptInfo(UserInfoUtil.getInstance(mContext).getLoginUserInfo().getOort_depcode()).subscribe(new RxBusSubscriber<String>() {
//            @Override
//            protected void onEvent(String s) {
//              dialog.dismiss();
//                Result<Data<DeptInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<DeptInfo>>>() {}.getType());
//                if (result.isOk()) {
//                    DeptInfo  mDeptInfo = result.getData().getDeptInfo();
//                    if (mDeptInfo != null){
//                        mDepartNameList = StringUtils.splitDeptInfo(mDeptInfo.getOort_dpath() , mDeptInfo.getOort_dcodepath());
//                        if (mDepartNameList != null && mDepartNameList.size() > 0){
//                            mPDepartmentName.setText(mDepartNameList.get(0).getOort_dname());
//                        }
//                        mDepartmentName.setText(mDeptInfo.getOort_dname());
//                        HttpResult.getPersonnel(mDeptInfo.getOort_dcode() , 1 , "selectContact");
//                        setVisibility(View.VISIBLE , View.GONE);
//                    }
//                }else {
//                    setVisibility(View.GONE , View.VISIBLE);
//                }
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//                dialog.dismiss();
//                setVisibility(View.GONE , View.VISIBLE);
//            }
//        });

//        HttpResult.getFriendList();

    }

    private void setVisibility(int visible , int emptyVisible){
        select_layout.setVisibility(visible);
        fl_empty.setVisibility(emptyVisible);
    }

    @Override
    protected void initEvent() {
        mSearchView.setSearchViewListener(this);
        mLetterIndexView.setOnLetterChangedListener(this);
        mConfirm.setOnClickListener(v ->  {

                if ( mDataHandle.getUserData().size() >  0){
                    DataHandle.getInstance().toJson();
                }else {
                    Toast.makeText(mContext , "请选择您要添加的好友" , Toast.LENGTH_SHORT).show();
                }

        });
        mBackView.setOnClickListener(v -> {

            new OrdCommonDialog(mContext).setConfirmClick(()-> {
                finish();
            }).show();

        });
        higher_department_ll.setOnClickListener(v -> {
            if (mDepartNameList != null && mDepartNameList.size() > 0){
                SelectTwoActivity.actionStart(mContext ,mDepartNameList.get(0));
            }

        });
        department_ll.setOnClickListener(v -> {
            if (mDepartNameList != null && mDepartNameList.size() > 0){
                SelectTwoActivity.actionStart(mContext ,mDepartNameList.get(mDepartNameList.size()-1));
            }
        });
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUNSelectContactsAdapter.updateList( PinyinUtils.filterUserInfo(s.toString() , mUserInfoList));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if ("selectContact".equals(event.getDataType())){
            if (event.getList() != null){
                mUserInfoList = PinyinUtils.sortUserIfo(event.getList());
                mUNSelectContactsAdapter.updateList(mUserInfoList);
            }
        }

    }

    //搜索
    @Override
    public void onSearch(CharSequence text) {
        mUNSelectContactsAdapter.updateList( PinyinUtils.filterUserInfo(text.toString() , mUserInfoList));
    }
    //字母索引
    @Override
    public void onChanged(String s, int position) {
        int pos = mUNSelectContactsAdapter.getPositionForSection(s.charAt(0));
        if (pos != -1) {
            manager.scrollToPositionWithOffset(pos, 0);
        }
    }
    public static void actionStart(Context context){
        Intent intent = new Intent(context, SelectContactsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        notifyMsg();
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

                mConfirm.setText(getString(R.string.button_ok) + "(" +data.size() + ")");
            }else {
                mConfirm.setText(getString(R.string.button_ok));
            }

        }

    }


    @Override
    public void finish() {
        super.finish();
        DataHandle.getInstance().clear();
    }

    @Override
    public void onBackPressed() {
        new OrdCommonDialog(mContext).setConfirmClick(()-> {
            super.onBackPressed();
        }).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mObserver.deleteBuyUser(this);
        //监听回调
        mObserver.deleteBuyUser(mUNSelectContactsAdapter);
        mObserver.deleteBuyUser(mSelectContactsAdapter);
        mDataHandle.removeActivity(this);
    }
}
