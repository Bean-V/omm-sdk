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
package com.zhihu.matisse.internal.model;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.widget.CheckView;
import com.zhihu.matisse.internal.utils.PathUtils;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import java.io.File;

import android.os.Environment;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.FileNotFoundException;

@SuppressWarnings("unused")
public class SelectedItemCollection {

    public static final String STATE_SELECTION = "state_selection";
    public static final String STATE_COLLECTION_TYPE = "state_collection_type";
    /**
     * Empty collection
     */
    public static final int COLLECTION_UNDEFINED = 0x00;
    /**
     * Collection only with images
     */
    public static final int COLLECTION_IMAGE = 0x01;
    /**
     * Collection only with videos
     */
    public static final int COLLECTION_VIDEO = 0x01 << 1;
    /**
     * Collection with images and videos.
     */
    public static final int COLLECTION_MIXED = COLLECTION_IMAGE | COLLECTION_VIDEO;
    private final Context mContext;
    private Set<Item> mItems;
    private int mCollectionType = COLLECTION_UNDEFINED;

    public SelectedItemCollection(Context context) {
        mContext = context;
    }

    public void onCreate(Bundle bundle) {
        if (bundle == null) {
            mItems = new LinkedHashSet<>();
        } else {
            List<Item> saved = bundle.getParcelableArrayList(STATE_SELECTION);
            mItems = new LinkedHashSet<>(saved);
            mCollectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
        }
    }

    public void setDefaultSelection(List<Item> uris) {
        mItems.addAll(uris);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        outState.putInt(STATE_COLLECTION_TYPE, mCollectionType);
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        bundle.putInt(STATE_COLLECTION_TYPE, mCollectionType);
        return bundle;
    }


    public void addA(Item item) {
        mItems.add(item);
    }
    public boolean add(Item item) {
        if (typeConflict(item)) {
            throw new IllegalArgumentException("Can't select images and videos at the same time.");
        }
        boolean added = mItems.add(item);
        if (added) {
            if (mCollectionType == COLLECTION_UNDEFINED) {
                if (item.isImage()) {
                    mCollectionType = COLLECTION_IMAGE;
                } else if (item.isVideo()) {
                    mCollectionType = COLLECTION_VIDEO;
                }
            } else if (mCollectionType == COLLECTION_IMAGE) {
                if (item.isVideo()) {
                    mCollectionType = COLLECTION_MIXED;
                }
            } else if (mCollectionType == COLLECTION_VIDEO) {
                if (item.isImage()) {
                    mCollectionType = COLLECTION_MIXED;
                }
            }
        }
        return added;
    }

    public boolean remove(Item item) {
        boolean removed = mItems.remove(item);
        if (removed) {
            if (mItems.size() == 0) {
                mCollectionType = COLLECTION_UNDEFINED;
            } else {
                if (mCollectionType == COLLECTION_MIXED) {
                    refineCollectionType();
                }
            }
        }
        return removed;
    }

    public void clear() {
        mItems.clear();
    }

    public void overwrite(ArrayList<Item> items, int collectionType) {
        if (items.size() == 0) {
            mCollectionType = COLLECTION_UNDEFINED;
        } else {
            mCollectionType = collectionType;
        }
        mItems.clear();
        mItems.addAll(items);
    }


    public List<Item> asList() {
        return new ArrayList<>(mItems);
    }

    public List<Uri> asListOfUri() {
        List<Uri> uris = new ArrayList<>();
        for (Item item : mItems) {
            uris.add(item.getContentUri());
        }
        return uris;
    }


    public interface ProgressListioner{
        void progress(int progress);

        void finsh(List<String> filePath);
    }
    static int mcount = 0;
    static int icount = 0;


    public static String getPathString(Context mContext, Item item) {
        String fp = PathUtils.getPath(mContext, item.getContentUri());
        //path1s.add(fp);

        File f = new File(fp);
        if(!f.exists()) {

            String dirPath = Environment.getExternalStorageDirectory() + "/" + "yisucache";

            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            String p0 = dirPath + "/" + f.getName();
            {
                InputStream ifs = null;
                OutputStream ofs = null;

                try {
                    ifs = mContext.getContentResolver().openInputStream(item.getContentUri());
                    ofs = new FileOutputStream(new File(p0));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    return null;

                }
                {

                    long read_begin_time = System.currentTimeMillis();

                    int io_area_size = 1024 * 1024 * 2;
                    boolean show_prog = false;//(file_size > SHOW_PROGRESS_THRESHOLD_VALUE);
                    int buffer_read_bytes = 0;
                    long file_read_bytes = 0;
                    byte[] buffer = new byte[io_area_size];
                    int postion = 0;
                    while (true) {
                        try {
                            if (!((buffer_read_bytes = ifs.read(buffer)) > 0))
                                break;
                            ofs.write(buffer, 0, buffer_read_bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        file_read_bytes += buffer_read_bytes;
                        postion++;
                    }


                }
                return p0;
            }
        }


        return fp;

    }





    public List<String> asListOfString(ProgressListioner ls) {
        List<String> paths = new ArrayList<>();


        if(mItems.size() == 0){
            ls.finsh(paths);

            return paths;
        }

        List<List> spPaths = spArr(Arrays.asList(mItems.toArray()), 500);


        List<List> newPaths = new ArrayList<>();


        mcount = 0;
        icount = 0;


        long time = System.currentTimeMillis()/1000;
        for(List li : spPaths){
            List<String> path1s = new ArrayList<>();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Object o : li) {
                        Item item = (Item) o;
                        String fp = PathUtils.getPath(mContext, item.getContentUri());
                        //path1s.add(fp);
                        icount = icount + 1;

                        if(ls != null){
                            ls.progress(icount * 100 / mItems.size());
                        }

                        File f = new File(fp);
                        if(!f.exists()) {

                            String dirPath = Environment.getExternalStorageDirectory() + "/" + "yisucache";

                            File dirFile = new File(dirPath);
                            if(!dirFile.exists()){
                                dirFile.mkdir();
                            }
                            String p0 = dirPath + "/" + f.getName();
                            {
                                InputStream ifs = null;
                                OutputStream ofs = null;

                                try {
                                    ifs = mContext.getContentResolver().openInputStream(item.getContentUri());
                                    ofs = new FileOutputStream(new File(p0));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();

                                    continue;

                                }
                                {

                                    long read_begin_time = System.currentTimeMillis();

                                    int io_area_size = 1024 * 1024 * 2;
                                    boolean show_prog = false;//(file_size > SHOW_PROGRESS_THRESHOLD_VALUE);
                                    int buffer_read_bytes = 0;
                                    long file_read_bytes = 0;
                                    byte[] buffer = new byte[io_area_size];
                                    int postion = 0;
                                    while (true) {
                                        try {
                                            if (!((buffer_read_bytes = ifs.read(buffer)) > 0))
                                                break;
                                                ofs.write(buffer, 0, buffer_read_bytes);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        file_read_bytes += buffer_read_bytes;
                                        postion++;
                                     }


                                }

                                try {
                                    ifs.close();
                                    ofs.flush();
                                    ofs.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                path1s.add(p0);
                            }
                        }else{
                            path1s.add(fp);
                        }

                    }
                    mcount = mcount + 1;


                    if(mcount  == spPaths.size()){
                        for (Object o : newPaths) {
                            List items = (List) o;
                            paths.addAll(items);
                        }
                        ls.finsh(paths);
                    }


                }
            }).start();
            newPaths.add(path1s);

        }


//        while(mcount < spPaths.size()){
//
//            Log.d("4444", "updateUsbFile: "+(System.currentTimeMillis()/1000 - time) + icount  + "TTT" + mcount +"TTTT" + mItems.size());
//
//        }



//        Log.d("4444", "updateUsbFile: "+(System.currentTimeMillis()/1000 - time) + icount  + "TTT" + mcount +"TTTT" + mItems.size());
//        time = System.currentTimeMillis()/1000;
//        for (Object o : newPaths) {
//
//            List items = (List) o;
//            paths.addAll(items);
//        }

//        Log.d("4444", "updateUsbFile: "+(System.currentTimeMillis()/1000 - time));
//        time = System.currentTimeMillis()/1000;
//        for (Item item : mItems) {
//            paths.add(PathUtils.getPath(mContext, item.getContentUri()));
//        }
        return paths;
    }



    public List<String> asListOfString() {
            List<String> paths = new ArrayList<>();
            for (Item item : mItems) {
                paths.add(PathUtils.getPath(mContext, item.getContentUri()));
            }

            return paths;
    }




    public  List<List> spArr(List arr, int num) { //arr是你要分割的数组，num是以几个为一组
        List newArr = new ArrayList<String>(); //首先创建一个新的空数组。用来存放分割好的数组
        for (int i = 0; i < arr.size();) { //注意：这里与for循环不太一样的是，没有i++
            int from  = i;
            i += num;
            if(i >= arr.size()){
                i = arr.size();
                newArr.add(arr.subList(from, i));
                break;
            }else {
                newArr.add(arr.subList(from, i));
            }
        }
        return newArr;
    }

    public boolean isEmpty() {
        return mItems == null || mItems.isEmpty();
    }

    public boolean isSelected(Item item) {
        return mItems.contains(item);
    }

    public IncapableCause isAcceptable(Item item) {
        if (maxSelectableReached()) {
            int maxSelectable = currentMaxSelectable();
            String cause;

            try {
                cause = mContext.getResources().getQuantityString(
                        R.plurals.error_over_count,
                        maxSelectable,
                        maxSelectable
                );
            } catch (Resources.NotFoundException e) {
                cause = mContext.getString(
                        R.string.error_over_count,
                        maxSelectable
                );
            } catch (NoClassDefFoundError e) {
                cause = mContext.getString(
                        R.string.error_over_count,
                        maxSelectable
                );
            }

            return new IncapableCause(cause);
        } else if (typeConflict(item)) {
            return new IncapableCause(mContext.getString(R.string.error_type_conflict));
        }

        return PhotoMetadataUtils.isAcceptable(mContext, item);
    }

    public boolean maxSelectableReached() {
        return mItems.size() == currentMaxSelectable();
    }

    // depends
    private int currentMaxSelectable() {
        SelectionSpec spec = SelectionSpec.getInstance();
        if (spec.maxSelectable > 0) {
            return spec.maxSelectable;
        } else if (mCollectionType == COLLECTION_IMAGE) {
            return spec.maxImageSelectable;
        } else if (mCollectionType == COLLECTION_VIDEO) {
            return spec.maxVideoSelectable;
        } else {
            return spec.maxSelectable;
        }
    }

    public int getCollectionType() {
        return mCollectionType;
    }

    private void refineCollectionType() {
        boolean hasImage = false;
        boolean hasVideo = false;
        for (Item i : mItems) {
            if (i.isImage() && !hasImage) hasImage = true;
            if (i.isVideo() && !hasVideo) hasVideo = true;
        }
        if (hasImage && hasVideo) {
            mCollectionType = COLLECTION_MIXED;
        } else if (hasImage) {
            mCollectionType = COLLECTION_IMAGE;
        } else if (hasVideo) {
            mCollectionType = COLLECTION_VIDEO;
        }
    }

    /**
     * Determine whether there will be conflict media types. A user can only select images and videos at the same time
     * while {@link SelectionSpec#mediaTypeExclusive} is set to false.
     */
    public boolean typeConflict(Item item) {
        return SelectionSpec.getInstance().mediaTypeExclusive
                && ((item.isImage() && (mCollectionType == COLLECTION_VIDEO || mCollectionType == COLLECTION_MIXED))
                || (item.isVideo() && (mCollectionType == COLLECTION_IMAGE || mCollectionType == COLLECTION_MIXED)));
    }

    public int count() {
        return mItems.size();
    }

    public int checkedNumOf(Item item) {
        int index = new ArrayList<>(mItems).indexOf(item);
        return index == -1 ? CheckView.UNCHECKED : index + 1;
    }
}
