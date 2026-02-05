package com.oort.weichat.ui.lccontact;
import com.oort.weichat.R;
import com.oortcloud.contacts.bean.Sort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_contacts_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_contacts_list extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Adapter_contacts adc;

    public List getMlist() {
        return mlist;
    }

    public void setMlist(List mlist) {
        this.mlist = mlist;
        if(adc != null) {
            adc.refresh(mlist);
        }

    }

    public List getSlist() {
        return slist;
    }

    public void setSlist(List slist) {
        this.slist = slist;
        if(adc != null) {
            adc.refreshUserCheckStatu(slist);
        }
    }

    private ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void onItemClick(int position);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }


    private List mlist = new ArrayList();
    private List slist = new ArrayList();

    public Fragment_contacts_list() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_contacts_list.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_contacts_list newInstance(String param1, String param2) {
        Fragment_contacts_list fragment = new Fragment_contacts_list();
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
        View v = inflater.inflate(R.layout.fragment_lc_contacts, container, false);

        ListView lv = v.findViewById(R.id.list_contacts);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mItemClickListener.onItemClick(i);
            }
        });

        adc = new Adapter_contacts(getContext(),mlist);

        adc.setSelectUsers(slist);
        lv.setAdapter(adc);



        return v;
    }
}