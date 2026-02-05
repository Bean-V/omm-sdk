package com.sentaroh.android.upantool;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.ui.PreviewItemFragment;
import com.zhihu.matisse.internal.ui.adapter.PreviewPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import androidx.fragment.app.FragmentTransaction;
public class AdapterPreview extends FragmentPagerAdapter {

    private ArrayList<File> mItems = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;
    private ArrayList <Fragment> frags = new ArrayList<Fragment>();
    private FragmentManager mfm;
    private ArrayList Fragments = new ArrayList();

    public AdapterPreview(@NonNull FragmentManager fm, int behavior,ArrayList datas) {
        super(fm, behavior);
        mItems = datas;
        mfm = fm;
        //setFragments();
        setDatas(datas);
    }

    @Override
    public Fragment getItem(int position) {
        return frags.get(position); //FragmentItemPreview.newInstance(mItems.get(position));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (mListener != null) {
            mListener.onPrimaryItemSet(position);
        }
    }

    public File getMediaItem(int position) {
        return mItems.get(position);
    }

    public void addAll(List<File> items) {
        mItems.addAll(items);
    }



    public void setDatas(List<File> datas) {
        if(frags != null){
            FragmentTransaction ft = mfm.beginTransaction();
            for(Fragment f:this.frags){
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            mfm.executePendingTransactions();
        }

        frags.clear();
        for(File file : datas) {
            frags.add(FragmentItemPreview.newInstance(file));
        }
        notifyDataSetChanged();
    }

//    public void setFragments(List<Fragment> fragments) {
//        if(fragments != null){
//            FragmentTransaction ft = mf.beginTransaction();
//            for(Fragment f:this.mFragmentList){
//                ft.remove(f);
//            }
//            ft.commit();
//            ft = null;
//            fm.executePendingTransactions();
//        }
//        this.mFragmentList = fragments;
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }

}
