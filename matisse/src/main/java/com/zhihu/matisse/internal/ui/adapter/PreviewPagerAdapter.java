/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import android.view.ViewGroup;

import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.ui.PreviewItemFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends FragmentPagerAdapter {

    public ArrayList<Item> getmItems() {
        return mItems;
    }

    public void setmItems(ArrayList<Item> mItems) {
        this.mItems = mItems;
    }

    private ArrayList<Item> mItems = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;
    private ArrayList <Fragment> frags = new ArrayList<Fragment>();
    private FragmentManager mfm;
    private ArrayList Fragments = new ArrayList();


    public PreviewPagerAdapter(FragmentManager manager, OnPrimaryItemSetListener listener) {
        super(manager);
        mListener = listener;
        mfm = manager;
    }

    @Override
    public Fragment getItem(int position) {
        return frags.get(position); //PreviewItemFragment.newInstance(mItems.get(position));
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



    public Item getMediaItem(int position) {


        return mItems.get(position);
    }

    public void addAll(List<Item> items) {
        mItems.addAll(items);

        for(Item i : items){
            frags.add(PreviewItemFragment.newInstance(i));
        }
    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }

    public void setDatas(List<Item> datas) {
        mItems = (ArrayList<Item>) datas;
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
        for(Item i : datas) {
            frags.add(PreviewItemFragment.newInstance(i));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
