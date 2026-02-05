package com.oort.weichat.ui.lccontact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.contacts.adapter.DepartAndUserAdapter;
import com.oortcloud.contacts.adapter.HigherDepartmentAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.DeptInfo;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.utils.DeptUtils;
import com.oortcloud.contacts.utils.SortComparator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgFragment extends Fragment implements LCBaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private DeptInfo mDeptInfo;
    //当前部门编码
    private String mCurrentDeptCode;
    private List<Department> mDeptList = new ArrayList<Department>();
    ;
    LinearLayoutManager linearLayoutManager;
    HigherDepartmentAdapter higherAdapter;
    GridLayoutManager manager;


    private List<Sort> mSortList = new ArrayList<>();
    private DepartAndUserAdapter mDepAndUsrAdapter;
    private Adapter_org_header adoh;
    private Fragment_contacts_list cl;

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
        if(cl != null) {
            cl.setSlist(mSelectUsers);
        }
    }

    private List mSelectUsers = new ArrayList();


    private ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void updateDatas(List datas);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }


    public OrgFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrgFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgFragment newInstance(String param1, String param2) {
        OrgFragment fragment = new OrgFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mCurrentDeptCode = getArguments().getString(Constants.DEPARTMENT_CODE);
            mDeptInfo = (DeptInfo) getArguments().getSerializable(Constants.OBJ);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment




        View v = inflater.inflate(R.layout.fragment_org, container, false);


        adoh = new Adapter_org_header(getContext(),mDeptList);

        RecyclerView rv = v.findViewById(R.id.rv_org_header);
        rv.setAdapter(adoh);

        adoh.setOnItemClickListener(new Adapter_org_header.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Department dp = mDeptList.get(position);
                getDetDatas(dp.getOort_dcode(), 1);
                mDeptList = mDeptList.subList(0,position);
                adoh.refreshData(mDeptList);

            }
        });



        cl = new Fragment_contacts_list();

        cl.setOnItemClickListener(new Fragment_contacts_list.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Sort s = mSortList.get(position);
                if(s instanceof Department){
                    Department dp = (Department) s;
                    mDeptList = DeptUtils.splitDepartment(dp.getOort_dpath(), dp.getOort_dcodepath());
                    //mDeptList.add(dp);
                    getDetDatas(dp.getOort_dcode(),1);
                    adoh.refreshData(mDeptList);
                }else{
                    UserInfo user = (UserInfo) s;
                    UserInfo u = null;
                    for(int i = 0;i< mSelectUsers.size();i++){
                        UserInfo info = (UserInfo) mSelectUsers.get(i);
                        if(info.getOort_uuid().equals(user.getOort_uuid())) {
                            u = info;
                            break;
                        }
                    }
                    if(u == null) {
                        mSelectUsers.add(user);
                    }else{
                        mSelectUsers.remove(u);
                    }



//                    mHorAdapter.notifyDataSetInvalidated();
//                    mOkBtn.setText(getString(R.string.add_chat_ok_btn, mSelectPositions.size()));

                    mItemClickListener.updateDatas(mSelectUsers);
                    cl.setSlist(mSelectUsers);
                }
            }
        });

        cl.setMlist(mSortList);
        cl.setSlist(mSelectUsers);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container_org, cl, String.valueOf(100));
        // String[] datas = (String[]) p.get(i);
        // psd.refresh(Arrays.asList(datas));

        transaction.commitNow();

        mCurrentDeptCode = UserInfoUtils.getInstance(getContext()).getLoginUserInfo().getOort_depcode();
        getDet();
        return v;
    }

    private void getDet() {
        HttpRequestCenter.getDeptInfo(mCurrentDeptCode).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<Data<Department>> result = new Gson().fromJson(s, new TypeToken<Result<Data<Department>>>() {
                }.getType());
                if (result.isOk()) {
                    Department dp = result.getData().getDeptInfo();
                    mDeptList = DeptUtils.splitDepartment(dp.getOort_dpath(), dp.getOort_dcodepath());
                    //mDeptList.add(dp);
                    getDetDatas(dp.getOort_dcode(),1);
                    adoh.refreshData(mDeptList);
                }
            }

        });
    }

    private void getDetDatas(String depCode,int showUser) {

        HttpRequestCenter.post(depCode, showUser).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {}.getType());
                if (result.isOk()) {
                    List<Sort> sortList = new ArrayList<>();
                    Data data = result.getData();
                    if (data != null) {
                        List<UserInfo> userInfoData = data.getUser();
                        //添加部门人员
                        Collections.sort(userInfoData, new SortComparator());
                        sortList.addAll(userInfoData);
                        //添加子部门
                        List<Department> departments = data.getDept();
                        Collections.sort(departments, new SortComparator());
                        sortList.addAll(departments);
                    }
                    mSortList = sortList;
                    cl.setMlist(mSortList);


                }
            }

        });
    }




}