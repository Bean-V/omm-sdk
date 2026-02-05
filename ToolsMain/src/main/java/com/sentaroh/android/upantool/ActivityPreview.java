package com.sentaroh.android.upantool;

import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sentaroh.android.upantool.R;
import com.zhihu.matisse.internal.model.AlbumMediaCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityPreview extends ActivityBasePreview {

    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";
    public static final String EXTRA_FILES = "extra_files";

    public AdapterPreview adp;

    private  ArrayList dataList = new ArrayList();

    private AlbumMediaCollection mCollection = new AlbumMediaCollection();

    private boolean mIsAlreadySetPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent in = getIntent();

        int index = in.getIntExtra("index",0);
        ArrayList list = in.getStringArrayListExtra("filePaths");


        mPreviousPos = index;

        for(Object path : list){
            String p = (String) path;
            dataList.add(new File(p));
        }

        adp = new AdapterPreview(getSupportFragmentManager(), 0,dataList);
        mPager.setAdapter(adp);


        mPager.setCurrentItem(mPreviousPos, false);
        //mCheckView.setCheckedNum(UsbHelper.getInstance().checkedNumOf((File) dataList.get(mPreviousPos)));

        mCheckView.setChecked(UsbHelper.getInstance().isSelected((File) dataList.get(mPreviousPos)));


        findViewById(R.id.tv_preview_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPreview.this);
                alert = builder.setTitle("提示")
                        .setMessage("确定删除" + "\n" + ((File) dataList.get(mPreviousPos)).getName())
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                List files = new ArrayList();
                                files.add(dataList.get(mPreviousPos));
                                if (files.size() == 0) {
                                    toast("未选中文件");
                                    return;
                                }
                                LoadingDialog mloadDialog = new LoadingDialog(ActivityPreview.this, "删除中", false);
                                mloadDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (Object o : files) {
                                            File file = (File) o;
                                            toast(FileTool.deleteMediaStore(file, ActivityPreview.this));

                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                mloadDialog.dismiss();

                                                File f = (File) dataList.get(mPreviousPos);
                                                dataList.remove(mPreviousPos);

                                                adp.setDatas(dataList);
                                                //adp.notifyDataSetChanged();


                                                if(dataList.size() == 0){
                                                    finish();

                                                }else {
                                                    mPreviousPos = dataList.size() <= mPreviousPos ? dataList.size() - 1 : mPreviousPos;
                                                    mPager.setCurrentItem(mPreviousPos, false);
                                                }
                                                if(UsbHelper.getInstance().getmPreViewListener() != null){
                                                    UsbHelper.getInstance().getmPreViewListener().delFile(f);
                                                }

                                            }
                                        });

                                    }
                                }).start();

                            }
                        }).show();
            }
            
        });


        findViewById(R.id.tv_copytou).setVisibility(View.GONE);
        if(UsbHelper.getInstance().canCopyToU()){
            findViewById(R.id.tv_copytou).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.tv_copytou).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPreview.this);
                alert = builder.setTitle("提示")
                        .setMessage("确定复制文件" + "\n" + ((File) dataList.get(mPreviousPos)).getName())
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                List files = new ArrayList();
                                files.add(dataList.get(mPreviousPos));
                                if (files.size() == 0) {
                                    toast("未选中文件");
                                    return;
                                }

                                ///
                                LoadingDialog mloadDialog = new LoadingDialog(ActivityPreview.this, "", false);
                                mloadDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (Object o : files) {
                                            File file = (File) o;
                                            FileTool.copyToUPanoRoot(file,ActivityPreview.this);
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                mloadDialog.dismiss();

                                                File f = (File) dataList.get(mPreviousPos);


                                            }
                                        });

                                    }
                                }).start();

                            }
                        }).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}