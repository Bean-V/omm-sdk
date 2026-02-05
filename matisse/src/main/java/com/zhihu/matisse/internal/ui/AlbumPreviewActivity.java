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
package com.zhihu.matisse.internal.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.AlbumMediaCollection;
import com.zhihu.matisse.internal.ui.adapter.PreviewPagerAdapter;
import com.zhihu.matisse.internal.utils.PathUtils;
import com.zhihu.matisse.listener.OnOperClikListener;
import com.zhihu.matisse.ui.MatisseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumPreviewActivity extends BasePreviewActivity implements
        AlbumMediaCollection.AlbumMediaCallbacks {

    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";

    private AlbumMediaCollection mCollection = new AlbumMediaCollection();

    private boolean mIsAlreadySetPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        mCollection.onCreate(this, this);
        Album album = getIntent().getParcelableExtra(EXTRA_ALBUM);
        mCollection.load(album);

        Item item = getIntent().getParcelableExtra(EXTRA_ITEM);
        if (mSpec.countable) {
            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
        } else {
            mCheckView.setChecked(mSelectedCollection.isSelected(item));
        }
        updateSize(item);


        Boolean res = getIntent().getBooleanExtra(CHECK_Enable,false);

       if(true) {//!res
            mCheckView.setVisibility(View.GONE);

            findViewById(R.id.originalLayout).setVisibility(View.GONE);
        }

        mButtonApply.setVisibility(View.GONE);

       findViewById(R.id.tv_preview_del).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (mSpec.onOperClikListener != null) {

                   Item item = mAdapter.getMediaItem(mPager.getCurrentItem());
                   ArrayList<String> selectedPaths = new ArrayList<>();
                   selectedPaths.add(PathUtils.getPath(AlbumPreviewActivity.this, item.getContentUri()));
                   mSpec.onOperClikListener.onOperClikListener(AlbumPreviewActivity.this, selectedPaths, 1, new OnOperClikListener.Callback() {
                       @Override
                       public void callback() {
                           /// onAlbumSelected(mAlbum);

//
//                            if(fragment != null && fragment.isAdded()){
//                                getSupportFragmentManager()
//                                        .beginTransaction()
//                                        .remove(fragment);
//
//                                fragment = null;
//                            }

                           ArrayList list = mAdapter.getmItems();
                           list.remove(item);
                           mAdapter.setDatas(list);
                           mSelectedCollection.clear();
                           if(mSelectedCollection.isSelected(item)){
                               mSelectedCollection.remove(item);
                           }
                           mSpec.mAlbumCollection.restartLoaderAlbums();
                       }
                   });
               }
           }
       });



       if(mSpec.showCopy){
           findViewById(R.id.tv_preview_copy).setVisibility(View.VISIBLE);
       }
        findViewById(R.id.tv_preview_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSpec.onOperClikListener != null) {

                    Item item = mAdapter.getMediaItem(mPager.getCurrentItem());
                    ArrayList<String> selectedPaths = new ArrayList<>();
                    selectedPaths.add(PathUtils.getPath(AlbumPreviewActivity.this, item.getContentUri()));
                    mSpec.onOperClikListener.onOperClikListener(AlbumPreviewActivity.this, selectedPaths, 2, new OnOperClikListener.Callback() {
                        @Override
                        public void callback() {
                            /// onAlbumSelected(mAlbum);


                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollection.onDestroy();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        List<Item> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(Item.valueOf(cursor));
        }
//        cursor.close();

        if (items.isEmpty()) {
            return;
        }

        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mPager.getAdapter();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
        if (!mIsAlreadySetPosition) {
            //onAlbumMediaLoad is called many times..
            mIsAlreadySetPosition = true;
            Item selected = getIntent().getParcelableExtra(EXTRA_ITEM);
            int selectedIndex = items.indexOf(selected);
            mPager.setCurrentItem(selectedIndex, false);
            mPreviousPos = selectedIndex;
        }
    }

    @Override
    public void onAlbumMediaReset() {

    }
}
