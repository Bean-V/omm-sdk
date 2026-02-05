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
package com.zhihu.matisse.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.zhihu.matisse.Config;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.entity.loader.AlbumMediaLoader;
import com.zhihu.matisse.internal.model.AlbumCollection;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.ui.AlbumPreviewActivity;
import com.zhihu.matisse.internal.ui.BasePreviewActivity;
import com.zhihu.matisse.internal.ui.Fragment_ablum_list;
import com.zhihu.matisse.internal.ui.MediaSelectionFragment;
import com.zhihu.matisse.internal.ui.SelectedPreviewActivity;
import com.zhihu.matisse.internal.ui.adapter.AlbumMediaAdapter;
import com.zhihu.matisse.internal.ui.adapter.AlbumsAdapter;
import com.zhihu.matisse.internal.ui.widget.AlbumsSpinner;
import com.zhihu.matisse.internal.ui.widget.CheckRadioView;
import com.zhihu.matisse.internal.ui.widget.IncapableDialog;
import com.zhihu.matisse.internal.ui.widget.LCProgressDialog;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;
import com.zhihu.matisse.internal.utils.PathUtils;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;
import com.zhihu.matisse.internal.utils.SingleMediaScanner;
import com.zhihu.matisse.listener.ItemOnCickListener;
import com.zhihu.matisse.listener.OnOperClikListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class MatisseActivity extends AppCompatActivity implements
        AlbumCollection.AlbumCallbacks, AdapterView.OnItemSelectedListener,
        MediaSelectionFragment.SelectionProvider, View.OnClickListener,
        AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener,
        AlbumMediaAdapter.OnPhotoCapture {

    public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";
    public static final String EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path";
    public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    private static final int REQUEST_CODE_PREVIEW = 23;
    private static final int REQUEST_CODE_CAPTURE = 24;
    public static final String CHECK_STATE = "checkState";
    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private MediaStoreCompat mMediaStoreCompat;
    private SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
    private SelectionSpec mSpec;

    private AlbumsSpinner mAlbumsSpinner;
    private AlbumsAdapter mAlbumsAdapter;
    private TextView mButtonPreview;
    private TextView mButtonApply;
    private View mContainer;
    private View mEmptyView;

    private LinearLayout mOriginalLayout;
    private CheckRadioView mOriginal;
    private boolean mOriginalEnable;
    private Fragment_ablum_list ablum_list;
    private boolean showEdit;

    private boolean isAllSelect = false;
    private MediaSelectionFragment fragment;
    private Album mAlbum;

    private boolean mAlbumEdit = false;

    private ArrayList mAlbums = new ArrayList<>();

    private ArrayList sAlbums = new ArrayList<>();
    private TextView mLeftTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // programmatically set theme before super.onCreate()
        mSpec = SelectionSpec.getInstance();
        setTheme(mSpec.themeId);
        super.onCreate(savedInstanceState);
        if (!mSpec.hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // 设置状态栏文字黑色
            decorView.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE); // 状态栏背景设为白色（否则可能看不清黑色文字）
        }
        setContentView(R.layout.activity_matisse);

        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }



        if (mSpec.capture) {
            mMediaStoreCompat = new MediaStoreCompat(this);
            if (mSpec.captureStrategy == null)
                throw new RuntimeException("Don't forget to set CaptureStrategy.");
            mMediaStoreCompat.setCaptureStrategy(mSpec.captureStrategy);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable navigationIcon = toolbar.getNavigationIcon();
        TypedArray ta = getTheme().obtainStyledAttributes(new int[]{R.attr.album_element_color});
        int color = ta.getColor(0, 0);
        ta.recycle();
        navigationIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);


            if (navigationIcon != null) {
                navigationIcon.setColorFilter(
                       getColor(R.color.nav_icon_color), // 直接使用黑色
                        PorterDuff.Mode.SRC_IN // 或 PorterDuff.Mode.MULTIPLY
                );
            }


        mButtonPreview = (TextView) findViewById(R.id.button_preview);
        mButtonApply = (TextView) findViewById(R.id.button_apply);
        mButtonPreview.setOnClickListener(this);
        mButtonApply.setOnClickListener(this);
        mContainer = findViewById(R.id.container);
        mEmptyView = findViewById(R.id.empty_view);
        mOriginalLayout = findViewById(R.id.originalLayout);
        mOriginal = findViewById(R.id.original);
        mOriginalLayout.setOnClickListener(this);

        mSelectedCollection.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }
        updateBottomToolbar();

        mAlbumsAdapter = new AlbumsAdapter(this, null, false);
        mAlbumsSpinner = new AlbumsSpinner(this);
        mAlbumsSpinner.setOnItemSelectedListener(this);
       // mAlbumsSpinner.setSelectedTextView((TextView) findViewById(R.id.selected_album));
        mAlbumsSpinner.setPopupAnchorView(findViewById(R.id.toolbar));
        mAlbumsSpinner.setAdapter(mAlbumsAdapter);
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
        mSpec.mAlbumCollection = mAlbumCollection;

        {
            findViewById(R.id.bottom_toolbar).setVisibility(View.GONE);
        }




        mContainer.setVisibility(View.VISIBLE);
                ablum_list = new Fragment_ablum_list();

                ablum_list.setItemOnClickListem(new ItemOnCickListener() {
                    @Override
                    public void itemOnClikListener(int postion, Object obj) {
                        if(mAlbumEdit){
                            Album a = (Album) mAlbums.get(postion);

                            if(sAlbums.contains(a)){
                                sAlbums.remove(a);
                            }else{
                                sAlbums.add(a);
                            }
                            ablum_list.getAdp().reloadSelectData(sAlbums);
                            updateCount();

                            setDefaultStypeTb(false);




                        }
                    }

                    @Override
                    public void itemOnLongClikListener(int postion, Object obj) {
                        if(Config.ImagePick){
                            return;
                        }
                        mAlbumEdit = !mAlbumEdit;
                        showOper(mAlbumEdit);
                        ablum_list.setEdit(mAlbumEdit);

                        setDefaultStypeTb(!mAlbumEdit);
                    }
                });

                //
        //android:drawableRight="@drawable/ic_arrow_drop_down_white_24dp"
         mLeftTv = findViewById(R.id.selected_album);
        findViewById(R.id.selected_album).setVisibility(View.VISIBLE);
        mLeftTv.setText(getText(mSpec.mimeTypeSet.containsAll(MimeType.ofVideo()) ? R.string.fm_movies : R.string.fm_xiangce));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, ablum_list, Fragment_ablum_list.class.getSimpleName())
                .commitAllowingStateLoss();


//        findViewById(R.id.iv_oper).setOnClickListener(new View.OnClickListener() {
//
//            private PopupWindow mPop;
//
//            @Override
//            public void onClick(View view) {
//                View popView = getLayoutInflater().inflate(R.layout.popview_oper_layout, null);
//                popView.findViewById(R.id.btn_sys).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //delete
//                        mPop.dismiss();
//
//                    }
//                });
//                popView.findViewById(R.id.btn_ch).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    //copytou
//                        mPop.dismiss();
//
//
//                    }
//                });
//
//
//                mPop = new PopupWindow(popView, dp2px(getBaseContext(), 110),dp2px(getBaseContext(), 123));
//                mPop.setOutsideTouchable(false);
//                mPop.setFocusable(true);
//                mPop.showAsDropDown(view);
//            }
//        });





        showOper(false);
        showCopyUpan(false);
        initOperUI();
//        TransFileManager.getInstance().addStatuChangeListener(new TransFileManager.StatuChangeListener() {
//            @Override
//            public void onStatuChange() {
//
//                TextView tv = findViewById(R.id.tv_count);
//                if(TransFileManager.getInstance().getTransUnDoneList().size() > 0){
//                    tv.setVisibility(View.VISIBLE);
//
//                    tv.setText(TransFileManager.getInstance().getTransUnDoneList().size() > 99 ? "99+" : String.valueOf(TransFileManager.getInstance().getTransUnDoneList().size()));
//
//                    toList = true;
//                }else {
//                    tv.setVisibility(View.GONE);
//                    toList = false;
//                }
//            }
//        });



    }
    static int dp2px(Context context, float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
        outState.putBoolean("checkState", mOriginalEnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAlbumCollection.onDestroy();
        mSpec.onCheckedListener = null;
        mSpec.onSelectedListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       // mSelectedCollection.clear();
        if(ablum_list.isAdded()){
            setResult(Activity.RESULT_CANCELED);
            super.onBackPressed();

        }else{
            showOper(false);
            setDefaultStypeTb(true);
            mLeftTv = findViewById(R.id.selected_album);
            mLeftTv.setText(getText(mSpec.mimeTypeSet.containsAll(MimeType.ofVideo()) ? R.string.fm_movies : R.string.fm_xiangce));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ablum_list, Fragment_ablum_list.class.getSimpleName())
                    .commitAllowingStateLoss();
            mAlbumCollection.restartLoaderAlbums();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_PREVIEW) {
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            ArrayList<Item> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
            mOriginalEnable = data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
            int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
                    SelectedItemCollection.COLLECTION_UNDEFINED);
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
                Intent result = new Intent();
                ArrayList<Uri> selectedUris = new ArrayList<>();
                ArrayList<String> selectedPaths = new ArrayList<>();
                if (selected != null) {
                    for (Item item : selected) {
                        selectedUris.add(item.getContentUri());
                        selectedPaths.add(PathUtils.getPath(this, item.getContentUri()));
                    }
                }
                result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
                result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
                result.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
                setResult(RESULT_OK, result);
                finish();
            } else {
                mSelectedCollection.overwrite(selected, collectionType);
                Fragment mediaSelectionFragment = getSupportFragmentManager().findFragmentByTag(
                        MediaSelectionFragment.class.getSimpleName());
                if (mediaSelectionFragment instanceof MediaSelectionFragment) {
                    ((MediaSelectionFragment) mediaSelectionFragment).refreshMediaGrid();
                }
                updateBottomToolbar();
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE) {
            // Just pass the data back to previous calling Activity.
            Uri contentUri = mMediaStoreCompat.getCurrentPhotoUri();
            String path = mMediaStoreCompat.getCurrentPhotoPath();
            ArrayList<Uri> selected = new ArrayList<>();
            selected.add(contentUri);
            ArrayList<String> selectedPath = new ArrayList<>();
            selectedPath.add(path);
            Intent result = new Intent();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selected);
            result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPath);
            setResult(RESULT_OK, result);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                MatisseActivity.this.revokeUriPermission(contentUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            new SingleMediaScanner(this.getApplicationContext(), path, new SingleMediaScanner.ScanListener() {
                @Override public void onScanFinish() {
                    Log.i("SingleMediaScanner", "scan finish!");
                }
            });
            finish();
        }
    }

    private void updateBottomToolbar() {

        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mButtonPreview.setEnabled(false);
            mButtonApply.setEnabled(false);
            mButtonApply.setText(getString(R.string.button_apply_default));
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonPreview.setEnabled(true);
            mButtonApply.setText(R.string.button_apply_default);
            mButtonApply.setEnabled(true);
        } else {
            mButtonPreview.setEnabled(true);
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getString(R.string.button_apply, selectedCount));
        }


        if (mSpec.originalable) {
            mOriginalLayout.setVisibility(View.VISIBLE);
            updateOriginalState();
        } else {
            mOriginalLayout.setVisibility(View.INVISIBLE);
        }


    }


    private void updateOriginalState() {
        mOriginal.setChecked(mOriginalEnable);
        if (countOverMaxSize() > 0) {

            if (mOriginalEnable) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.error_over_original_size, mSpec.originalMaxSize));
                incapableDialog.show(getSupportFragmentManager(),
                        IncapableDialog.class.getName());

                mOriginal.setChecked(false);
                mOriginalEnable = false;
            }
        }
    }


    private int countOverMaxSize() {
        int count = 0;
        int selectedCount = mSelectedCollection.count();
        for (int i = 0; i < selectedCount; i++) {
            Item item = mSelectedCollection.asList().get(i);

            if (item.isImage()) {
                float size = PhotoMetadataUtils.getSizeInMB(item.size);
                if (size > mSpec.originalMaxSize) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_preview) {
            Intent intent = new Intent(this, SelectedPreviewActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            startActivityForResult(intent, REQUEST_CODE_PREVIEW);
        } else if (v.getId() == R.id.button_apply) {
            Intent result = new Intent();
            ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
            result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
            result.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            setResult(RESULT_OK, result);
            finish();
        } else if (v.getId() == R.id.originalLayout) {
            int count = countOverMaxSize();
            if (count > 0) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.error_over_original_count, count, mSpec.originalMaxSize));
                incapableDialog.show(getSupportFragmentManager(),
                        IncapableDialog.class.getName());
                return;
            }

            mOriginalEnable = !mOriginalEnable;
            mOriginal.setChecked(mOriginalEnable);

            if (mSpec.onCheckedListener != null) {
                mSpec.onCheckedListener.onCheck(mOriginalEnable);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mAlbumCollection.setStateCurrentSelection(position);
        mAlbumsAdapter.getCursor().moveToPosition(position);
        Album album = Album.valueOf(mAlbumsAdapter.getCursor());
        if (album.isAll() && SelectionSpec.getInstance().capture) {
            album.addCaptureCount();
        }
        onAlbumSelected(album);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAlbumLoad(final Cursor cursor) {
        mAlbumsAdapter.swapCursor(cursor);
        // select default album.

        mAlbums.clear();

        boolean albumExist = false;
        List<Album> items = new ArrayList<>();
        while (cursor.moveToNext()) {

            Album album = Album.valueOf(cursor);
            items.add(Album.valueOf(cursor));
            if(mAlbum != null){
                if(album.getId().equals(mAlbum.getId())){
                    albumExist = true;
                    if(!ablum_list.isAdded()) {
                        onAlbumSelected(album);
                    }
                }
            }
        }
        mAlbums.addAll(items);
        ablum_list.setList((ArrayList<Album>) items);
        if(mAlbum != null && !albumExist){
           // onAlbumSelected(mAlbum);

            showOper(false);
            setDefaultStypeTb(true);
            mLeftTv = findViewById(R.id.selected_album);
            mLeftTv.setText(getText(mSpec.mimeTypeSet.containsAll(MimeType.ofVideo()) ? R.string.fm_movies : R.string.fm_xiangce));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ablum_list, Fragment_ablum_list.class.getSimpleName())
                    .commitAllowingStateLoss();
            //mAlbumCollection.restartLoaderAlbums();
        }


        if(true) {
            return;
        }


        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
                mAlbumsSpinner.setSelection(MatisseActivity.this,
                        mAlbumCollection.getCurrentSelection());
                Album album = Album.valueOf(cursor);
                if (album.isAll() && SelectionSpec.getInstance().capture) {
                    album.addCaptureCount();
                }
                onAlbumSelected(album);
            }
        });
    }

    @Override
    public void onAlbumReset() {
        mAlbumsAdapter.swapCursor(null);
    }

    public void onAlbumSelected(Album album) {

        mAlbum = album;

        showOper(true);

        if (album.isAll() && album.isEmpty()) {
            mContainer.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);

            if(fragment != null && fragment.isAdded()){
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment);
            }
            fragment = MediaSelectionFragment.newInstance(album);
            fragment.setmOnMediaLongClickListener(new AlbumMediaAdapter.OnMediaLongClickListener() {
                @Override
                public void onMediaLongClick(Boolean canCheck) {
                    if(Config.ImagePick){
                        return;
                    }

                    showOper(canCheck);
                    setDefaultStypeTb(!canCheck);
                }
            });

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, MediaSelectionFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }

        mLeftTv = findViewById(R.id.selected_album);
        mLeftTv.setText(mAlbum.getDisplayName(this) + "(" + mAlbum.getCount() + ")");

    }

    @Override
    public void onUpdate() {
        // notify bottom toolbar that check state changed.
        updateBottomToolbar();

        if (mSpec.onSelectedListener != null) {
            mSpec.onSelectedListener.onSelected(
                    mSelectedCollection.asListOfUri(), mSelectedCollection.asListOfString());
        }

        if(Config.ImagePick){

            TextView tv = findViewById(R.id.tv_allSelect);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextColor(getColor(R.color.nav_text_color));
            }
            if(mSelectedCollection.count() > 0) {
                tv.setText("确定" + "(" + mSelectedCollection.count() + ")");
            }else{
                tv.setText("");
            }
         return;
        }
        updateCount();
        setDefaultStypeTb(false);
    }



    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    @Override
    public void capture() {
        if (mMediaStoreCompat != null) {
            mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
        }
    }

    @Override
    public void onMediaClick(Album album, Item item, int adapterPosition, Boolean canCheck) {
            Intent intent = new Intent(this, AlbumPreviewActivity.class);
            intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album);
            intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            intent.putExtra(BasePreviewActivity.CHECK_Enable,canCheck);
            startActivityForResult(intent, REQUEST_CODE_PREVIEW);

    }


    protected void initOperUI() {
        TextView tv = findViewById(R.id.tv_allSelect);


        findViewById(R.id.tv_allSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(Config.ImagePick){

                    ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
                    mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, selectedPaths, 10, new OnOperClikListener.Callback() {
                        @Override
                        public void callback() {

                        }
                    });
                    finish();
                    return;
                }
                isAllSelect = !isAllSelect;
                if(mAlbumEdit){
                    if(isAllSelect) {
                        sAlbums.clear();
                        sAlbums.addAll(mAlbums);
                    }else{
                        sAlbums.clear();
                    }

                    ablum_list.getAdp().reloadSelectData(sAlbums);

                    updateCount();
                    setDefaultStypeTb(false);

                    return;
                }

                TextView tv = (TextView) view;

                Cursor cursor = fragment.getmAdapter().getCursor();
                cursor.moveToFirst();
                ArrayList list = new ArrayList();
                List<Item> items = new ArrayList<>();
                mSelectedCollection.clear();
                int i = 0;
                do {

                    Item item = Item.valueOf(cursor);
                    items.add(Item.valueOf(cursor));

                    i++;

                    if(isAllSelect){
//                        if (!mSelectedCollection.isSelected(item)) {
//                            mSelectedCollection.add(item);
//
//                        }

                        mSelectedCollection.addA(item);
                        if(i % 10 == 0){
                            fragment.getmAdapter().notifyDataSetChanged();
                        }



                    }else{
                        mSelectedCollection.clear();
                    }


                }while (cursor.moveToNext());

                fragment.getmAdapter().notifyDataSetChanged();

                updateCount();
                setDefaultStypeTb(false);





//
//                Item item = fragment.getmAdapter().getMediaItem(mPager.getCurrentItem());
//                if (mSelectedCollection.isSelected(item)) {
//                    mSelectedCollection.remove(item);
//                    if (mSpec.countable) {
//                        mCheckView.setCheckedNum(CheckView.UNCHECKED);
//                    } else {
//                        mCheckView.setChecked(false);
//                    }
//                } else {
//                    if (assertAddSelection(item)) {
//                        mSelectedCollection.add(item);
//                        if (mSpec.countable) {
//                            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
//                        } else {
//                            mCheckView.setChecked(true);
//                        }
//                    }
//                }


//                int position = tabLayout.getSelectedTabPosition();
//                ArrayList cl = allDatas.get(position);
//                if(tv.getText().toString().equals("全选")){
//                    UsbHelper.getInstance().addAll(cl);
//                }else{
//                    UsbHelper.getInstance().removeAll(cl);
//                }
//                updateCount();
//
//                if(position < 2){
//                    Fragment_wx_nine wxn = (Fragment_wx_nine) fragments.get(position);
//                    wxn.setSlist(UsbHelper.getInstance().getSelectFiles());
//                }else{
//                    Fragment_filelist wxl = (Fragment_filelist) fragments.get(position);
//
//                    ArrayList sdatas = new ArrayList();
//                    for(int i = 0;i<  UsbHelper.getInstance().getSelectFiles().size();i++){
//                        File file = (File) UsbHelper.getInstance().getSelectFiles().get(i);
//                        int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
//                        sdatas.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
//                    }
//                    wxl.setSlist(sdatas);
//                }

            }
        });
        findViewById(R.id.ll_oper);
        //findViewById(R.id.tv_oper_count);
        findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSpec.onOperClikListener != null){

                    findViewById(R.id.tv_del).setEnabled(false);


                    LCProgressDialog pg = new LCProgressDialog(MatisseActivity.this,getString(R.string.read_files_to_del),0);//"正在统计数据"





                    if(mAlbumEdit){
                        AlbumOper(findViewById(R.id.tv_del),1);

                        return;
                    }




                    if(!mSelectedCollection.isEmpty()){
                        pg.show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString(new SelectedItemCollection.ProgressListioner() {
                                @Override
                                public void progress(int progress) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.setProgress(progress);

                                            findViewById(R.id.tv_del).setEnabled(true);

                                        }


                                    });

                                }

                                @Override
                                public void finsh(List<String> filePath) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.dismiss();
                                            findViewById(R.id.tv_del).setEnabled(true);
                                            mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, filePath, 1, new OnOperClikListener.Callback() {
                                                @Override
                                                public void callback() {

                                                    mSelectedCollection.clear();
                                                    mAlbumCollection.restartLoaderAlbums();
                                                    updateCount();

                                                    setDefaultStypeTb(false);

                                                }
                                            });

                                        }

                                    });



                                }
                            });

                        }
                    }).start();

                //}
//
//                    ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
//                    mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, selectedPaths, 1, new OnOperClikListener.Callback() {
//                        @Override
//                        public void callback() {
//                           /// onAlbumSelected(mAlbum);
//
////
////                            if(fragment != null && fragment.isAdded()){
////                                getSupportFragmentManager()
////                                        .beginTransaction()
////                                        .remove(fragment);
////
////                                fragment = null;
////                            }
//                            mSelectedCollection.clear();
//                            mAlbumCollection.restartLoaderAlbums();
//                            updateCount();
//                        }
//                    });
                }
            }
        });
        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSpec.onOperClikListener != null){

                    findViewById(R.id.btn_share).setEnabled(false);


                    LCProgressDialog pg = new LCProgressDialog(MatisseActivity.this,getString(R.string.read_files_to_share),0);//"正在统计数据"





                    if(mAlbumEdit){
                        AlbumOper(findViewById(R.id.btn_share),5);

                        return;
                    }




                    if(!mSelectedCollection.isEmpty()){
                        pg.show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString(new SelectedItemCollection.ProgressListioner() {
                                @Override
                                public void progress(int progress) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.setProgress(progress);

                                            findViewById(R.id.btn_share).setEnabled(true);

                                        }


                                    });

                                }

                                @Override
                                public void finsh(List<String> filePath) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.dismiss();
                                            findViewById(R.id.tv_del).setEnabled(true);
                                            mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, filePath, 5, new OnOperClikListener.Callback() {
                                                @Override
                                                public void callback() {

                                                    mSelectedCollection.clear();
                                                    onAlbumSelected(mAlbum);

                                                }
                                            });

                                        }

                                    });



                                }
                            });

                        }
                    }).start();

                    //}
//
//                    ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
//                    mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, selectedPaths, 1, new OnOperClikListener.Callback() {
//                        @Override
//                        public void callback() {
//                           /// onAlbumSelected(mAlbum);
//
////
////                            if(fragment != null && fragment.isAdded()){
////                                getSupportFragmentManager()
////                                        .beginTransaction()
////                                        .remove(fragment);
////
////                                fragment = null;
////                            }
//                            mSelectedCollection.clear();
//                            mAlbumCollection.restartLoaderAlbums();
//                            updateCount();
//                        }
//                    });
                }
            }
        });


        findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSpec.onOperClikListener != null){






                    findViewById(R.id.tv_copy).setEnabled(false);


                    LCProgressDialog pg = new LCProgressDialog(MatisseActivity.this,getString(R.string.read_files_to_copy),0);//"正在统计数据"



                        if(mAlbumEdit){
                            AlbumOper(findViewById(R.id.tv_copy),2);

                            return;
                        }


                    if(!mSelectedCollection.isEmpty()){
                        pg.show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString(new SelectedItemCollection.ProgressListioner() {
                                @Override
                                public void progress(int progress) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.setProgress(progress);

                                            if(!findViewById(R.id.tv_copy).isEnabled()) {
                                                findViewById(R.id.tv_copy).setEnabled(true);
                                            }
                                        }

                                    });

                                }

                                @Override
                                public void finsh(List<String> filePath) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.dismiss();
                                            if(!findViewById(R.id.tv_copy).isEnabled()) {
                                                findViewById(R.id.tv_copy).setEnabled(true);
                                            }

                                            mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, filePath, 2, new OnOperClikListener.Callback() {
                                                @Override
                                                public void callback() {


                                                    mSelectedCollection.clear();
                                                    onAlbumSelected(mAlbum);

                                                }
                                            });
                                        }

                                    });



                                }
                            });

                        }
                    }).start();

                }
            }
        });;

        findViewById(R.id.btn_fm_oper_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSpec.onOperClikListener != null) {

                    findViewById(R.id.btn_fm_oper_move).setEnabled(false);


                    LCProgressDialog pg = new LCProgressDialog(MatisseActivity.this, getString(R.string.read_files_to_move), 0);//"正在统计数据"

                    if(mAlbumEdit){
                        if(mAlbumEdit){
                            AlbumOper(findViewById(R.id.btn_fm_oper_move),3);

                            return;
                        }
                    }
                    if (!mSelectedCollection.isEmpty()) {
                        pg.show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString(new SelectedItemCollection.ProgressListioner() {
                                @Override
                                public void progress(int progress) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.setProgress(progress);

                                            if (!findViewById(R.id.btn_fm_oper_move).isEnabled()) {
                                                findViewById(R.id.btn_fm_oper_move).setEnabled(true);
                                            }
                                        }

                                    });

                                }

                                @Override
                                public void finsh(List<String> filePath) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.dismiss();
                                            if (!findViewById(R.id.btn_fm_oper_move).isEnabled()) {
                                                findViewById(R.id.btn_fm_oper_move).setEnabled(true);
                                            }

                                            mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, filePath, 3, new OnOperClikListener.Callback() {
                                                @Override
                                                public void callback() {


                                                    mSelectedCollection.clear();
                                                    onAlbumSelected(mAlbum);

                                                }
                                            });
                                        }

                                    });


                                }
                            });

                        }
                    }).start();


                }
            }
        });



        findViewById(R.id.btn_addpas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSpec.onOperClikListener != null) {

                    findViewById(R.id.btn_addpas).setEnabled(false);


                    LCProgressDialog pg = new LCProgressDialog(MatisseActivity.this, getString(R.string.read_files_to_encrypt), 0);//"正在统计数据"


                    if(mAlbumEdit){
                        AlbumOper(findViewById(R.id.btn_addpas),4);

                        return;
                    }

                    if (!mSelectedCollection.isEmpty()) {
                        pg.show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString(new SelectedItemCollection.ProgressListioner() {
                                @Override
                                public void progress(int progress) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.setProgress(progress);

                                            if (!findViewById(R.id.btn_addpas).isEnabled()) {
                                                findViewById(R.id.btn_addpas).setEnabled(true);
                                            }
                                        }

                                    });

                                }

                                @Override
                                public void finsh(List<String> filePath) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pg.dismiss();
                                            if (!findViewById(R.id.btn_addpas).isEnabled()) {
                                                findViewById(R.id.btn_addpas).setEnabled(true);
                                            }

                                            mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, filePath, 4, new OnOperClikListener.Callback() {
                                                @Override
                                                public void callback() {


                                                    mSelectedCollection.clear();
                                                    onAlbumSelected(mAlbum);

                                                }
                                            });
                                        }

                                    });


                                }
                            });

                        }
                    }).start();


                }
            }
        });



        findViewById(R.id.tv_canceledit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mAlbumEdit){
                    sAlbums.clear();
                    mAlbumEdit = false;
                    ablum_list.setEdit(false);
                }else{
                    fragment.setMedit(false);
                }
                showOper(false);
                setDefaultStypeTb(true);

            }
        });

    }

   void AlbumOper(TextView tv ,int type ){
        {

            HashMap map = new HashMap();
            map.put("1",getString(R.string.read_files_to_del));
            map.put("2",getString(R.string.read_files_to_copy));
            map.put("3",getString(R.string.read_files_to_move));
            map.put("4",getString(R.string.read_files_to_encrypt));
            map.put("5",getString(R.string.read_files_to_share));

            LCProgressDialog pg = new LCProgressDialog(MatisseActivity.this, (String) map.get(String.valueOf(type)), 0);//"正在统计
            if(sAlbums.size() > 0){

                pg.show();
            }else{
                tv.setEnabled(true);
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList filePaths = new ArrayList();
                    for (Object o : sAlbums) {
                        Album a = (Album) o;
                        AlbumMediaLoader.getCursorOfAlbum(MatisseActivity.this, a, new SelectedItemCollection.ProgressListioner() {
                            @Override
                            public void progress(int progress) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pg.setProgress(progress);

                                        if (!tv.isEnabled()) {
                                            tv.setEnabled(true);
                                        }
                                    }

                                });

                            }

                            @Override
                            public void finsh(List<String> filePath) {
                                filePaths.addAll(filePath);

                            }
                        });
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pg.dismiss();
                            if (!tv.isEnabled()) {
                                tv.setEnabled(true);
                            }

                            mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this, filePaths, type, new OnOperClikListener.Callback() {
                                @Override
                                public void callback() {

                                    sAlbums.clear();
                                    ablum_list.getAdp().reloadSelectData(sAlbums);

                                    if(type == 1 || type == 3){
                                        mAlbumCollection.restartLoaderAlbums();
                                    }

                                }
                            });
                        }

                    });
                }
            }).start();

            return;
        }
    }

    protected void showOper(boolean show) {

        if(Config.ImagePick){


            return;
        }

        showEdit = show;

       // findViewById(R.id.tv_allSelect).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.ll_oper).setVisibility(show? View.VISIBLE : View.GONE);
        // findViewById(R.id.tv_oper_count);
        findViewById(R.id.tv_del);
       // findViewById(R.id.fl_copy).setVisibility(View.GONE);

        if(mSpec.showCopy){
            findViewById(R.id.fl_copy).setVisibility(View.VISIBLE);

            mSpec.onOperClikListener.onShowBadgetener(findViewById(R.id.tv_count));
        }



        findViewById(R.id.tv_allSelect).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.tv_canceledit).setVisibility(show ? View.VISIBLE : View.GONE);


       // mSpec.onOperClikListener.onShowCopy(findViewById(R.id.fl_copy));

    }

    protected void showCopyUpan(boolean show) {

        //findViewById(R.id.fl_copy).setVisibility(show ? View.VISIBLE : View.GONE);


    }
    protected void updateCount() {

//        TextView tv = findViewById(R.id.tv_oper_count);
//        tv.setText("已选" + "(" + UsbHelper.getInstance().getSelectFiles().size() + ")");


        if(Config.ImagePick){
            return;
        }



        if(mAlbumEdit){
            boolean notfound = true;
            if(sAlbums.size() == mAlbums.size()){
                notfound = false;
            }

            TextView tv_s = findViewById(R.id.tv_allSelect);



            tv_s.setText(notfound ? R.string.selectAll : R.string.not_selectAll);
            Drawable rightDrawable = getResources().getDrawable(notfound ? R.drawable.ic_wx_selectall_un : R.drawable.ic_wx_selectall);
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            tv_s.setCompoundDrawables(rightDrawable, null, null, null);

            return;
        }



        boolean notfound = true;

        Cursor cursor = fragment.getmAdapter().getCursor();
        cursor.moveToFirst();
        ArrayList list = new ArrayList();
        List<Item> items = new ArrayList<>();
        do {

            Item item = Item.valueOf(cursor);
            items.add(Item.valueOf(cursor));

            if (mSelectedCollection.isSelected(item)) {

                notfound = false;

            } else {
                notfound = true;
                break;
            }

        }while (cursor.moveToNext());

        TextView tv_s = findViewById(R.id.tv_allSelect);



        tv_s.setText(notfound ? R.string.selectAll : R.string.not_selectAll);
        Drawable rightDrawable = getResources().getDrawable(notfound ? R.drawable.ic_wx_selectall_un : R.drawable.ic_wx_selectall);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        tv_s.setCompoundDrawables(rightDrawable, null, null, null);


    }

    void setDefaultStypeTb(boolean defauat) {




        if(ablum_list.isAdded()){

            if(defauat) {

                mLeftTv.setText(getText(mSpec.mimeTypeSet.containsAll(MimeType.ofVideo()) ? R.string.fm_movies : R.string.fm_xiangce));
            }else{
                mLeftTv.setText(sAlbums.size()+ "/" + mAlbums.size());

            }
            return;
        }
        if(defauat) {
            mLeftTv.setText(mAlbum.getDisplayName(this) + "(" + mAlbum.getCount() + ")");
        }else{
            mLeftTv.setText(mSelectedCollection.count()+ "/" + mAlbum.getCount());

        }



    }
}
