package com.oortcloud.contacts.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.adapter.HigherDepartmentAdapter;
import com.oortcloud.contacts.adapter.UserInfoAdapter;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;


/**
 * 通讯录-公司组织架构
 */
public class CompanyOrganizationFragment extends Fragment {
    //下面正常数据，头部部门数据
    //下面正常数据，头部部门数据
    private RecyclerView rv_list, rv_parts;
    private EditText et_content;
    private RelativeLayout rl_search_delete;

    private ArrayList<Department> higherList;
    private ArrayList<Department> departmentList;
    private ArrayList<UserInfo>  userList;
    private com.oortcloud.contacts.adapter.DepartmentAdapter DepartmentAdapter;
    private UserInfoAdapter userAdapter;
    private HigherDepartmentAdapter higherAdapter;

    public static CompanyOrganizationFragment instantiation(int position) {
        CompanyOrganizationFragment fragment = new CompanyOrganizationFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dept_user_layout, container, false);
        return view;
    }

    @SuppressLint("WrongConstant")
    public void setListAdapter(BaseQuickAdapter adapter) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_list.setAdapter(adapter);
        rv_list.setLayoutManager(linearLayoutManager);
    }

    public void setTopListAdapter(BaseQuickAdapter adapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_parts.setAdapter(adapter);
        rv_parts.setLayoutManager(linearLayoutManager);
    }

    public void jumpItemchildL(final String depCode) {

        httprequest(depCode);
    }

    //跳转到指定层级position
    public void jumpTopFragment(int position, String depCode) {

       httprequest(depCode);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        rv_list = getView().findViewById(R.id.rv_list);
//        rv_parts = getView().findViewById(R.id.rv_parts);
        et_content =  getView().findViewById(R.id.et_content);
        rl_search_delete =  getView().findViewById(R.id.rl_search_delete);
        if (getArguments() != null) {


        }

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
//                    mPresenter.noSearchResult(R.string.txt_search_no_person);
                    rl_search_delete.setVisibility(View.GONE);
                    return;
                }
                rl_search_delete.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        departmentList = new ArrayList<>();
        userList = new ArrayList<>();
        higherList = new ArrayList<Department>();

        higherAdapter = new HigherDepartmentAdapter(R.layout.item_adress_company_organi_top, higherList);
        setTopListAdapter(higherAdapter);
        higherAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String depcode = ((Department) adapter.getItem(position)).getOort_dcode();
                jumpTopFragment(position, depcode);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (higherList.size() >= 1){
            getData(higherList.get(higherList.size()-1).getOort_dcode());
        }else {
            getData(null);
        }



    }

    //模拟获取数据，正常应该是请求后台返回数据
    public void getData(String depCode) {

       httprequest(depCode);

    }

    public   void httprequest(final String depCode) {

        departmentList.clear();
        userList.clear();
        if (TextUtils.isEmpty(depCode)) {
            HttpRequestCenter.post(depCode).subscribe(new RxBusSubscriber<String>() {
                @Override
                protected void onEvent(String s) {
                    Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {}.getType());
                    Log.v("msg" , s);
                    if (result.isOk()) {
                        final Department department = (Department) result.getData().getDept().get(0);
                        if ("99".equals(department.getOort_dcode())) {
                            higherList.addAll(result.getData().getDept());
                            higherAdapter.setNewData(higherList);
                            setEvent(department);
                            HttpRequestCenter.post(department.getOort_dcode()).subscribe(new RxBusSubscriber<String>() {
                                @Override
                                protected void onEvent(String s) {
                                    Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {}.getType());
                                    if (result.isOk()) {
//                                        setListAdapter(DepartmentAdapter);
//                                        DepartmentAdapter.setNewData(result.getData().getDept());
                                    }

                                }
                            });
                        }
                    } else {

                        //获取数据失败

                    }

                }
            });

        } else {
            HttpRequestCenter.post(depCode , 1).subscribe(new RxBusSubscriber<String>() {
                @Override
                protected void onEvent(String s) {

                    Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {}.getType());
                    if (result.isOk()) {

                        if (result.getData().getDept().size() > 0){
                            Department department = (Department) result.getData().getDept().get(0);
                            setHigherDepartment(department.getOort_dpath() , department.getOort_dcodepath());
//                            setListAdapter(DepartmentAdapter);
//                            DepartmentAdapter.setNewData(result.getData().getDept());

                        }else if (result.getData().getUser().size() >0){

                            UserInfo userInfo = (UserInfo) result.getData().getUser().get(0);

                            Department department = new Department(userInfo.getOort_depcode() ,userInfo.getOort_depname());

                            higherList.add(department);
                            setHigherType();
                            higherAdapter.setNewData(higherList);

                            setEvent(userInfo.getOort_depname());

//                            setListAdapter(userAdapter);
//                            userAdapter.setNewData(result.getData().getUser());
                        }
                        else {

                            //数据为空
                        }
                    }else {

                        //数据获取失败
                    }
                }
            });

        }


    }

    private void setHigherDepartment(String dPath ,String dCodePath) {

        higherList.clear();

        String dep[] = dPath.split("/");
        String code[] = dCodePath.split("/");
        for (int x = 0 ; x < dep.length -1; x++){
            if (TextUtils.isEmpty(dep[x])){
                continue;
            }
//                Department department = new Department();
//                department.setOort_dname(dep[x]);
//                department.setOort_dcode(code[x]);
//                higherList.add(department);
        }
        setHigherType();
        higherAdapter.setNewData(higherList);
        setEvent(higherList.get(higherList.size()-1));



    }

    private void setHigherType(){
        if (higherList.size() > 0 && higherList.size() <= 1) {
            higherList.get(0).setType(0);
        } else {
            for (int i = 0; i < higherList.size(); i++) {
                if (i == higherList.size() - 1) {
                    higherList.get(i).setType(0);

                } else {
                    higherList.get(i).setType(1);
                }
            }
        }
    }

    private void setEvent(Department department) {
        EventBus.getDefault().postSticky(department.getOort_dname());
    }
    private void setEvent(String departmentName) {
        EventBus.getDefault().postSticky(departmentName);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}


