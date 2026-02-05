package com.sentaroh.android.upantool;

import android.content.Context;
import android.os.Bundle;

//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;

//import android.support.v4.app.Fragment;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sentaroh.android.upantool.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class  Fragment_filelist extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private View view;

    public RecyclerView getRv() {
        return rv;
    }

    public void setRv(RecyclerView rv) {
        this.rv = rv;
    }

    public View.OnDragListener getListener() {
        return mlistener;
    }

    public void setListener(View.OnDragListener listener) {
        mlistener = listener;
    }

    private View.OnDragListener mlistener;
    private RecyclerView rv;
    private List mSortList = new ArrayList();
    private List mSelectList = new ArrayList();


    private AdapterFMList apd;

    public Boolean getMedit() {
        return medit;
    }

    public Boolean getLongTapToDrag() {
        return longTapToDrag;
    }

    public void setLongTapToDrag(Boolean longTapToDrag) {
        this.longTapToDrag = longTapToDrag;
    }

    public Boolean longTapToDrag = false;

    public void setMedit(Boolean medit) {
        this.medit = medit;
        if(apd != null) {
            apd.refreshEdit(medit);
        }
    }

    private Boolean medit = false;

    public List getMlist() {
        return mlist;
    }

    public void setMlist(List mlist) {
        this.mlist = mlist;
        if(apd != null) {
            apd.refresh(mlist);
        }

    }

    public List getSlist() {
        return slist;
    }

    public void setSlist(List slist) {
        this.slist = slist;
        if(apd != null) {
            apd.refreshUserCheckStatu(slist);
        }
    }

    private ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void onItemClick(int position);
        void onItemCheckClick(int position);
        void onItemLongClick(boolean edit);

        void onDirItemClick(int position,Object obj);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;

    }


    private List mlist = new ArrayList();
    private List slist = new ArrayList();




    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Fragment_filelist() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Fragment_filelist newInstance(int columnCount) {
        Fragment_filelist fragment = new Fragment_filelist();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_filelist, container, false);
        // Set the adapter
        Context context = view.getContext();
        rv = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            rv.setLayoutManager(new LinearLayoutManager(context));
        } else {
            rv.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        apd = new AdapterFMList(mlist,longTapToDrag);
        apd.setmContext(context);
        apd.refreshEdit(medit);
        rv.setAdapter(apd);
        rv.setOnDragListener(mlistener);
        apd.setOnItemClickListener(new AdapterFMList.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mItemClickListener.onItemClick(position);
            }

            @Override
            public void onItemCheckClick(int position) {

                mItemClickListener.onItemCheckClick(position);
            }

            @Override
            public void onItemLongClick(boolean edit) {
                mItemClickListener.onItemLongClick(edit);
            }
        });
        apd.refreshUserCheckStatu(slist);
        initDirView();
        return view;
    }

    private LinearLayout ll_dir;
    private Adapter_dir_header dirAdp;
    private RecyclerView dir_rv ;

    public void setDir_name(String dir_name) {
        this.dir_name = dir_name;
        if(dir_tv_name != null){
            dir_tv_name.setText(dir_name);
        }
    }

    private String dir_name ;

    public void setmDirList(List mDirList) {

        this.mDirList = mDirList;

        if(dirAdp != null) {
            dirAdp.refreshData(mDirList);
            if(mDirList.size() > 0) {
                dir_rv.scrollToPosition(mDirList.size() - 1);
            }
        }


    }


    public void setHide_dir(boolean hide_dir) {
        this.hide_dir = hide_dir;

        if(ll_dir != null ){
            ll_dir.setVisibility(hide_dir ? View.GONE : View.VISIBLE);
        }
    }

    private boolean hide_dir  = false;



    private List mDirList = new ArrayList();
    TextView dir_tv_name;


    void initDirView(){
        ll_dir = view.findViewById(R.id.layout_header);
        ll_dir.setVisibility(hide_dir ? View.GONE : View.VISIBLE);

        dir_rv = ll_dir.findViewById(R.id.rv_dir_header);

        dir_tv_name = view.findViewById(R.id.tv_fm_header_local);
        if(dir_name != null){
            dir_tv_name.setText(dir_name);
        }


        dirAdp = new Adapter_dir_header(getContext(),mDirList);
        dir_rv.setAdapter(dirAdp);

        dirAdp.setOnItemClickListener(new Adapter_dir_header.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if(mDirList.size() <= position){
                    return;
                }
                Adapter_dir_header.RvItem item = (Adapter_dir_header.RvItem) mDirList.get(position);
                if(mItemClickListener != null){
                    mItemClickListener.onDirItemClick(position,item.obj.toString());
                }


            }
        });
    }


}