package com.oort.weichat.ui.lccontact;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.oort.weichat.bean.Label;
import com.oort.weichat.helper.DialogHelper;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.utils.DeptUtils;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class    LabFragment extends Fragment implements LCBaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArrayList mLabelList;
    public int mSelectIndex = 0;
    public Adapter_lab_top adl;
    public Fragment_contacts_list cl;
    public RecyclerView recycler_view;

    public List<Sort> mSortList = new ArrayList<>();

    public List getmSelectUsers() {
        return mSelectUsers;
    }


    public void setmSelectUsers(List mSelectUsers) {
        this.mSelectUsers = mSelectUsers;
        if(cl != null) {
            cl.setSlist(mSelectUsers);
        }
    }

    public List mSelectUsers = new ArrayList();
    public List mList = new ArrayList();

    public ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void updateDatas(List datas);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public LabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LabFragment newInstance(String param1, String param2) {
        LabFragment fragment = new LabFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_lab, container, false);
        recycler_view = v.findViewById(R.id.rv_lab_top);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
       // layoutManager.setFlexDirection(FlexDirection.ROW);

        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
 //       layoutManager.setMaxLine(3);
//        layoutManager.set
        //layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        recycler_view.setLayoutManager(layoutManager);
        adl = new Adapter_lab_top(getContext(),mList);
        recycler_view.setAdapter(adl);
        adl.setOnItemClickListener(new Adapter_lab_top.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                {
                    mSelectIndex = position;
                    adl.selectIndex(mSelectIndex);
                    Label lab = (Label) mLabelList.get(mSelectIndex);
                    getUser(lab.getTid());
                }
            }
        });

        cl = new Fragment_contacts_list();

        cl.setOnItemClickListener(new Fragment_contacts_list.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Sort s = mSortList.get(position);
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
                mItemClickListener.updateDatas(mSelectUsers);
                cl.setSlist(mSelectUsers);
            }

        });

        cl.setMlist(mSortList);
        cl.setSlist(mSelectUsers);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container_lab, cl, String.valueOf(100));
        // String[] datas = (String[]) p.get(i);
        // psd.refresh(Arrays.asList(datas));

        transaction.commitNow();
        getLabs();

        return v;

    }


    public void getLabs() {
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("is_open", "0");

        HttpUtils.post().url(Constant.BASE_URL + Constant.TAG_LIST)
                .params(params)
                .build()
                .execute(new BaseCallback<HashMap>(HashMap.class) {
                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {
                        if (result.getCode() == 200 && result.getData() != null) {
                            DialogHelper.dismissProgressDialog();
                            JSONArray labelList = (JSONArray) result.getData().get("list");
                            ArrayList labs = new ArrayList();
                            Gson son = new Gson();
                            for(int i = 0;i<labelList.size();i++){
                                labs.add(son.fromJson(labelList.get(i).toString(), Label.class));
                            }
                            mLabelList = labs;
                            adl.refreshData(mLabelList);



                            if(mLabelList.size() == 0){
                                return;
                            }
                            {
                                adl.selectIndex(mSelectIndex);
                                Label lab = (Label) mLabelList.get(mSelectIndex);
                                getUser(lab.getTid());
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    public void getUser(String labId) {


        HttpRequestCenter.getTagUsers(labId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg", s);
                List<Sort> dataList = new ArrayList<>();
                Result<Data<UserInfo>> UserResult = new Gson().fromJson(s, new TypeToken<Result<Data<UserInfo>>>() {
                }.getType());
                if (UserResult.isOk()) {
                    dataList.addAll(UserResult.getData().getList());
                    mSortList = dataList;
                    cl.setMlist(dataList);

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.v("msg", e.toString());
            }
        });
    }














}