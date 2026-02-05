package com.oort.weichat.ui.lccontact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LatestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LatestFragment extends Fragment implements LCBaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AppCompatActivity mAct;
    private Fragment_contacts_list cl;

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

    public List<Sort> mSortList = new ArrayList<>();

    private List mList = new ArrayList();
    private ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void updateDatas(List datas);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }


    public LatestFragment() {
        // Required empty public constructor
    }

    public LatestFragment(AppCompatActivity act) {
        mAct = act;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LatestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LatestFragment newInstance(String param1, String param2) {
        LatestFragment fragment = new LatestFragment();
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
        View v = inflater.inflate(R.layout.fragment_latest, container, false);

        cl = new Fragment_contacts_list();

        cl.setMlist(mSortList);
        cl.setSlist(mSelectUsers);

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

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container_latest, cl, String.valueOf(100));
        // String[] datas = (String[]) p.get(i);
        // psd.refresh(Arrays.asList(datas));

        transaction.commitNow();
        getUser();
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }


    public void getUser() {


        HttpRequestCenter.getLatestUsers().subscribe(new RxBusSubscriber<String>() {
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