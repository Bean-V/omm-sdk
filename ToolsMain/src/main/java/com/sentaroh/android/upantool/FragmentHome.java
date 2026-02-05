package com.sentaroh.android.upantool;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

//import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.Settings;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

//import com.sentaroh.android.SMBSync2.CommonUtilities;
//import com.sentaroh.android.SMBSync2.GlobalParameters;
//import com.sentaroh.android.SMBSync2.GlobalWorkArea;

//import com.bytedance.sdk.openadsdk.AdSlot;
//import com.bytedance.sdk.openadsdk.TTAdConstant;
//import com.bytedance.sdk.openadsdk.TTAdDislike;
//import com.bytedance.sdk.openadsdk.TTAdLoadType;
//import com.bytedance.sdk.openadsdk.TTAdNative;
//import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
//import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sentaroh.android.upantool.Ad.AdManager;
import com.sentaroh.android.upantool.R;
//import com.sentaroh.android.SMBSync2.SyncTaskEditor;
//import com.sentaroh.android.SMBSync2.SyncTaskUtil;
//import com.sentaroh.android.Utilities.Dialog.CommonDialog;
//import com.sentaroh.android.Utilities.SafFile;
//import com.sentaroh.android.Utilities.SafManager;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.FilePathSelector.Activity_filePathSelector;
import com.sentaroh.android.upantool.contact.ContactInfo;
import com.sentaroh.android.upantool.contact.ContactUtil;
import com.sentaroh.android.upantool.contact.FileUtil;
import com.sentaroh.android.upantool.languagelib.LanguageType;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.listener.OnOperClikListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import net.lingala.zip4j.model.enums.AesKeyStrength;

import net.lingala.zip4j.ZipFile;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    private static final int REQUEST_CODE_CHOOSE = 23;

    private final static String SUB_APPLICATION_TAG = "SyncTask ";

    private Dialog mDialog = null;
    private boolean mTerminateRequired = true;
    private Context mContext = null;


    private FragmentManager mFragMgr = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String totalSize  = "0";
    private TextView localSizeTv;
    private TextView upanSizetv;
    private TextView localSizePerTv;
    private FrameLayout mcopy;
    private LCProgressDialog mloadDialog;
    private TextView tv_tip;
    private LinearLayout ll_usb;

    public TextView getUpantip_tv() {
        return upantip_tv;
    }

    private TextView upantip_tv;

    private TextView badgeTv;

    public String getTotalSize() {
        return totalSize;


    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;

       // localSizeTv.setText(totalSize);
    }

    public String getFreeSize() {
        return freeSize;
    }

    public void setFreeSize(String freeSize) {
        this.freeSize = freeSize;
        upanSizetv.setText(freeSize);
    }

    private String freeSize = "0";

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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
        mContext = getContext();
        mFragMgr = this.getFragmentManager();
        if (mTerminateRequired) {
        }
    }



    public void shownousb(){


//        if(true){
//            return;
//        }
        ll_usb.setVisibility(View.GONE);
        upantip_tv.setVisibility(View.VISIBLE);
        upantip_tv.setText(R.string.no_usb);

        tv_tip.setText(R.string.no_usb);
        if(getContext() != null) {
            tv_tip.setTextColor(getContext().getColor(R.color.colorRed));
        }
        TransFileManager.getInstance().clear();
        if(mcopy != null){
            mcopy.setVisibility(View.GONE);
        }
    }

    public void showinusbdes(){
        upantip_tv.setVisibility(View.VISIBLE);
        upantip_tv.setText(R.string.checking);

        tv_tip.setText(R.string.checking);
        if(getContext() != null) {
            tv_tip.setTextColor(getContext().getColor(R.color.colorGray));
        }
    }

    public void showState2(){
        ll_usb.setVisibility(View.GONE);
        upantip_tv.setVisibility(View.VISIBLE);
        upantip_tv.setText(R.string.request_u_permisson);

    }

    public void showState3(){
        upantip_tv.setVisibility(View.GONE);

        ll_usb.setVisibility(View.VISIBLE);

        if(getContext() != null) {
            tv_tip.setText(getString(R.string.USB_connnect));
            tv_tip.setTextColor(getContext().getColor(R.color.colorGreen));
        }
        if(mcopy != null){
            if(UsbHelper.getInstance().canCopyToU()) {
                mcopy.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showState4(){
        ll_usb.setVisibility(View.GONE);
        upantip_tv.setVisibility(View.VISIBLE);
        upantip_tv.setText(getString(R.string.u_ejected));

        tv_tip.setText(getString(R.string.u_ejected));
        if(getContext() != null) {
            tv_tip.setTextColor(getContext().getColor(R.color.colorRed));
        }


        if(mcopy != null){
                mcopy.setVisibility(View.GONE);
        }
        TransFileManager.getInstance().clear();

    }
    public void showSize(String size){
        upanSizetv.setText(size);

    }





    private FrameLayout fl;
    private FrameLayout mExpressContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        localSizeTv = v.findViewById(R.id.tv_localsize);
        localSizePerTv = v.findViewById(R.id.tv_per);

        upanSizetv = v.findViewById(R.id.tv_upsize);
        upantip_tv = v.findViewById(R.id.tv_uppantip);
        GridView gv = v.findViewById(R.id.gv_filesrc);
        tv_tip = v.findViewById(R.id.tv_usbstatu);

        ll_usb = v.findViewById(R.id.ll_usb);



        fl = v.findViewById(R.id.fl);


        //initAd();



        v.findViewById(R.id.tv_loadList).setOnClickListener(new View.OnClickListener() {

            private PopupWindow mPop;

            @Override
            public void onClick(View view) {
//

                View popView = getLayoutInflater().inflate(R.layout.popview_home_layout, null);
                popView.findViewById(R.id.btn_sys).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPop.dismiss();
                        Intent in = new Intent( getActivity(),Activity_Task.class);
                        startActivity(in);

                    }
                });
                popView.findViewById(R.id.btn_ch).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPop.dismiss();
                        Activity_encryptlist.toThis(getActivity());


                    }
                });


                mPop = new PopupWindow(popView, ViewTool.dp2px(getContext(), 85), ViewTool.dp2px(getContext(), 82));
                mPop.setOutsideTouchable(false);
                mPop.setFocusable(true);
                mPop.showAsDropDown(v.findViewById(R.id.tv_loadList));
            }
        });


        localSizeTv.setText(FileTool.getSdSize());
        localSizePerTv.setText(FileTool.getSdSizePer());
        List mData = new ArrayList<FileSrcItem>();
        mData.add(new FileSrcItem(R.mipmap.ic_home_wx1, getString(R.string.file_wx), ""));
        mData.add(new FileSrcItem(R.mipmap.ic_home_pic1, getString(R.string.file_pic), ""));
        mData.add(new FileSrcItem(R.mipmap.ic_home_videa1, getString(R.string.file_video), ""));
        mData.add(new FileSrcItem(R.mipmap.ic_home_audio1, getString(R.string.file_audio), ""));

        mData.add(new FileSrcItem(R.mipmap.ic_home_contactbackup0, getString(R.string.back_up_contact), ""));
        mData.add(new FileSrcItem(R.mipmap.ic_home_migrate0, getString(R.string.migrate), ""));




        BaseAdapter mAdapter = new MyAdapter<FileSrcItem>((ArrayList<FileSrcItem>) mData, R.layout.layout_filesrc_item) {
            @Override
            public void bindView(ViewHolder holder, FileSrcItem obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.tv_name, obj.getiName());
                holder.setText(R.id.tv_count, obj.getIcount());
            }
        };

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "你点击了~" + position + "~项", Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT >= 23) {


                    if (Build.VERSION.SDK_INT >= 30) {

                        if (!Environment.isExternalStorageManager()) {
                            AlertDialog alert = null;
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            alert = builder.setTitle(R.string.tip)
                                    .setMessage(R.string.need_permisson_to_fm)
                                    .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                            startActivity(intent);
                                        }
                                    }).create();             //创建AlertDialog对象
                            alert.show();

                            return;
                        }
                    }

//                    if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//
//                        return;
//                    }

                    XXPermissions.with(getContext())
                            // 申请单个权限
                            .permission(Permission.WRITE_EXTERNAL_STORAGE)
                            // 设置权限请求拦截器（局部设置）
                            //.interceptor(new PermissionInterceptor())
                            // 设置不触发错误检测机制（局部设置）
                            .unchecked()
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        //toast("获取部分权限成功，但部分权限未正常授予");
                                        return;
                                    }
//                                    Intent in = new Intent(getContext(), activity_contactbackup_.class);//Activity_contactbackup
//                                    startActivity(in);

                                    itemClik(position);

                                    return;
                                    //toast("获取录音和日历权限成功");
                                }

                                @Override
                                public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                                    if (doNotAskAgain) {
                                        XXPermissions.startPermissionActivity(getContext(), permissions);
                                    } else {
                                        toast("err");
                                    }
                                }
                            });


                }



            }
        });

        gv.setAdapter(mAdapter);
        v.findViewById(R.id.btn_to_fm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {


                    if(Build.VERSION.SDK_INT >= 30){

                        if (!Environment.isExternalStorageManager()) {
                            AlertDialog alert = null;
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            alert = builder.setTitle(R.string.tip)
                                    .setMessage(R.string.need_permisson_to_fm)
                                    .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                            startActivity(intent);
                                        }
                                    }).create();             //创建AlertDialog对象
                            alert.show();

                            return;
                        }
                    }

                    if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }else{
                        //if (Build.VERSION.SDK_INT <= 32) {
                           // startActivity(new Intent(getContext(),ActivityFM.class));

                        startActivity(new Intent(getContext(),Activity_FM_.class));
//                        } else {
//                            startActivity(new Intent(getContext(),ActivityFM30.class));
//                        }
                    }
                }else{
                    Toast.makeText(getContext(),getString(R.string.Lower_version),Toast.LENGTH_LONG);
                }

            }
//        readSize();
//        return v;
        });
        //readSize();




        v.findViewById(R.id.tv_u_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//
//                if (mContext.checkSelfPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
//
//                    requestPermissions(new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
//
//
////                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
////                    startActivityForResult(intent);
//
//                    return;
//                }else{
//                    // unmount.invoke(mStorageManager, id);
//                }

                AlertDialog alert = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                alert = builder.setTitle(R.string.tip)
                        .setMessage(R.string.eject_the_USB_stick)
                        .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


//                                if (mContext.checkSelfPermission(Manifest.permission.MOUNT_FORMAT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
//
//                                    requestPermissions(new String[]{Manifest.permission.MOUNT_FORMAT_FILESYSTEMS}, 1);
//                                    return;
//                                }else{
//                                   // unmount.invoke(mStorageManager, id);
//                                }




                                UsbHelper.getInstance().eject(mContext);
                                UsbHelper.getInstance().stopUsb();
                                ProgressDialog pd = new ProgressDialog(mContext);
                                pd.setMessage(getString(R.string.fm_u_out));
                                pd.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //UsbHelper.getInstance().stopUsb();

                                        while(!UsbHelper.sysSuccess){
                                        }
                                        pd.dismiss();

                                    }
                                }, 3000);


                            }
                        }).create();             //创建AlertDialog对象
                alert.show();
            }
        });


        setFreeSize(freeSize);
        setTotalSize(totalSize);




        TransFileManager.getInstance().addStatuChangeListener(new Fragment_ft.StatuChangeListener() {
            @Override
            public void onStatuChange() {

                TextView tv = badgeTv;
                if(tv == null){
                    return;
                }
                List datas = TransFileManager.getInstance().getTransUnDoneList();
                if(datas.size() > 0){
                    tv.setVisibility(View.VISIBLE);

                    tv.setText(datas.size() > 99 ? "99+" : String.valueOf(datas.size()));

                    toList = true;
                }else {
                    tv.setVisibility(View.GONE);
                    toList = false;
                }
            }
        });

        return v;
    }


    void itemClik(int position){
        Boolean open = true;
        int datatype = position;
        if (position == 0) {
            datatype = 1;

            if (open) {
                Intent in = new Intent(getContext(), ActivityWX_.class);//ActivityWX
                startActivity(in);
                //getActivity().startActivityForResult(in,303);
                return;
            }
        }
        if (position == 1) {
            datatype = 2;
            if (open) {
                Matisse.from(getActivity())
                        .choose(MimeType.ofImage(), false)
                        .countable(false)
                        .maxSelectable(1000000)
                        //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .setOnSelectedListener((uriList, pathList) -> {
                            Log.e("onSelected", "onSelected: pathList=" + pathList);
                        })
                        .showSingleMediaType(true)
                        .originalEnable(true)
                        .maxOriginalSize(10)
                        .showCopy(UsbHelper.getInstance().canCopyToU())
                        .autoHideToolbarOnSingleTap(true)
                        .setOnCheckedListener(isChecked -> {
                            Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                        })
                        .setOnOperClikListener(new OnOperClikListener() {
                            @Override
                            public void onOperClikListener(AppCompatActivity act, List filePaths, int type, Callback callback) {


                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (type == 2) {
//                                                    if(toList){
//                                                        Intent in = new Intent(getActivity(),Activity_Task.class);
//                                                        startActivity(in);
//
//                                                        return;
//                                                    }
                                        }

                                        if (filePaths.size() == 0) {
                                            if (type == 2) {
                                                if (toList) {
                                                    Intent in = new Intent(act, Activity_Task.class);
                                                    act.startActivity(in);

                                                    // return;
                                                }
                                            }
                                            Toast.makeText(act, R.string.unselect_file, Toast.LENGTH_LONG);
                                            return;
                                        }

                                        String tip = "";
                                        for (int i = 0; i < filePaths.size(); i++) {
                                            String p = (String) filePaths.get(i);
                                            File f = new File(p);
                                            tip = tip + "\n" + f.getName();
                                            if (i == 3) {
                                                tip = tip + "..." + filePaths.size() + act.getString(R.string.files);
                                                break;
                                            }


                                        }
                                        //alert =


                                        HashMap map = new HashMap();
                                        map.put("1",act.getString(R.string.confirm_del_file));
                                        map.put("2",act.getString(R.string.confirm_copy_file));
                                        map.put("3",act.getString(R.string.confirm_move_file));
                                        map.put("4",act.getString(R.string.confirm_addpas_file));
                                        map.put("5",act.getString(R.string.confirm_share_file));
                                        AlertDialog alert = null;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                        builder.setTitle(R.string.tip)
                                                .setMessage(map.get(String.valueOf(type)) + tip)
                                                .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        operClik(act, filePaths, type, callback);
                                                    }
                                                });//.show();

                                        try {
                                            alert = builder.show();
                                        } catch (Exception e) {
                                        };
                                    }
                                });


                            }

                            @Override
                            public void onShowBadgetener(TextView mbadgeTv) {
                                badgeTv = mbadgeTv;
                            }

                            @Override
                            public void onShowCopy(FrameLayout copy) {
                                mcopy = copy;
                            }
                        })
                        .forResult(REQUEST_CODE_CHOOSE);


                return;
            }
        }

        if (position == 2) {
            datatype = 3;

            if (open) {
                Matisse.from(getActivity())
                        .choose(MimeType.ofVideo(), false)
                        .countable(false)
                        .maxSelectable(100000000)
                        //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .setOnSelectedListener((uriList, pathList) -> {
                            Log.e("onSelected", "onSelected: pathList=" + pathList);
                        })
                        .showSingleMediaType(true)
                        .originalEnable(true)
                        .maxOriginalSize(10)
                        .showCopy(UsbHelper.getInstance().canCopyToU())
                        .autoHideToolbarOnSingleTap(true)
                        .setOnCheckedListener(isChecked -> {
                            Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                        })
                        .setOnOperClikListener(new OnOperClikListener() {
                            @Override


                            public void onOperClikListener(AppCompatActivity act, List filePaths, int type, Callback callback) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (type == 2) {

                                        }
                                        if (filePaths.size() == 0) {

                                            if (type == 2) {
                                                if (toList) {
                                                    Intent in = new Intent(act, Activity_Task.class);
                                                    act.startActivity(in);

                                                    // return;
                                                }
                                            }
                                            Toast.makeText(act, R.string.unselect_file, Toast.LENGTH_LONG);
                                            return;
                                        }

                                        String tip = "";
                                        for (int i = 0; i < filePaths.size(); i++) {
                                            String p = (String) filePaths.get(i);
                                            File f = new File(p);
                                            tip = tip + "\n" + f.getName();
                                            if (i == 3) {
                                                tip = tip + "..." + filePaths.size() + act.getString(R.string.files);
                                                break;
                                            }
                                        }

                                        HashMap map = new HashMap();
                                        map.put("1",act.getString(R.string.confirm_del_file));
                                        map.put("2",act.getString(R.string.confirm_copy_file));
                                        map.put("3",act.getString(R.string.confirm_copy_file));
                                        map.put("4",act.getString(R.string.confirm_addpas_file));
                                        map.put("5",act.getString(R.string.confirm_share_file));
                                        AlertDialog alert = null;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                        alert = builder.setTitle(R.string.tip)
                                                .setMessage(map.get(String.valueOf(type)) + tip)
                                                .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        operClik(act, filePaths, type, callback);
                                                    }
                                                }).show();
                                    }
                                });


                            }

                            @Override
                            public void onShowBadgetener(TextView mbadgeTv) {
                                badgeTv = mbadgeTv;
                            }

                            @Override
                            public void onShowCopy(FrameLayout copy) {
                                mcopy = copy;
                            }
                        })
                        .forResult(REQUEST_CODE_CHOOSE + 1);

                return;
            }

        }

        if (position == 3) {

            Intent in = new Intent(getContext(), Activity_audio.class);
            startActivity(in);
            return;
        }

        if (position == 4) {


            XXPermissions.with(getContext())
                    // 申请单个权限
                    .permission(Permission.READ_CONTACTS)
                    .permission(Permission.WRITE_CONTACTS)
                    // 设置权限请求拦截器（局部设置）
                    //.interceptor(new PermissionInterceptor())
                    // 设置不触发错误检测机制（局部设置）
                    .unchecked()
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                //toast("获取部分权限成功，但部分权限未正常授予");
                                return;
                            }
                            Intent in = new Intent(getContext(), activity_contactbackup_.class);//Activity_contactbackup
                            startActivity(in);

                            return;
                            //toast("获取录音和日历权限成功");
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            if (doNotAskAgain) {
                                //toast("被永久拒绝授权，请手动授予录音和日历权限");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(getContext(), permissions);
                            } else {
                                toast("err");
                            }
                        }
                    });

        }

        if (position == 5) {


            XXPermissions.with(getContext())
                    // 申请单个权限
                    .permission(Permission.READ_CONTACTS)
                    .permission(Permission.WRITE_CONTACTS)
                    // 设置权限请求拦截器（局部设置）
                    //.interceptor(new PermissionInterceptor())
                    // 设置不触发错误检测机制（局部设置）
                    .unchecked()
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                //toast("获取部分权限成功，但部分权限未正常授予");
                                return;
                            }
                            Intent in = new Intent(getContext(), Activity_Migration.class);//Activity_contactbackup
                            startActivity(in);

                            return;
                            //toast("获取录音和日历权限成功");
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            if (doNotAskAgain) {
                                //toast("被永久拒绝授权，请手动授予录音和日历权限");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(getContext(), permissions);
                            } else {
                                toast("err");
                            }
                        }
                    });
        }
    }

    private boolean toList = false;

    private AppCompatActivity actPick = null;

    public void operClik(AppCompatActivity act, List filePaths, int type, OnOperClikListener.Callback callback) {
        actPick = act;
        if(type == 1){

            if(filePaths.size() == 0){
                Toast.makeText(act,getString(R.string.unselect_file),Toast.LENGTH_LONG);
                return;
            }
//            LoadingDialog mloadDialog = new LoadingDialog(mContext,getString(R.string.deleting),false);
//            mloadDialog.show();

            LCProgressDialog pd = new LCProgressDialog(act,getString(R.string.deleting),0);

            if(filePaths.size() > 0){
                pd.show();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {

                    for(Object o : filePaths){
                        String path = (String) o;
                        File file = new File(path);
                        if(file == null){


                        }else {
                            if (new File(path).exists()) {
                                //toast();


                                try {
                                    FileTool.deleteMediaStore(new File(path), getContext());
                                }catch(Exception e){

                                }
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                         int progress = filePaths.indexOf(path)  * 100 / filePaths.size();

                                         pd.setProgress(progress);
                                    }
                                });
                            }
                        }

                    }

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            pd.dismiss();

                            // Toast.makeText(getContext(), "删除成功",Toast.LENGTH_LONG);
                           // act.finish();


                            //mloadDialog.dismiss();

                            if(callback != null){
                                callback.callback();
                            }
                        }
                    });

                }
            }).start();

        }else if(type > 1){


            if(filePaths.size() == 0){
                Toast.makeText(act,getString(R.string.unselect_file),Toast.LENGTH_LONG);
                return;
            }


            if(type == 2){
                Intent in = new Intent(getActivity(), Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();

                                buidTransDatas(filePaths,sDirpath,false);

                            }
                        }).start();

                    }
                });

            }
            if(type == 3){
                Intent in = new Intent(getActivity(), Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                buidTransDatas(filePaths,sDirpath,true);

                            }
                        }).start();

                    }
                });

            }

            if(type == 4){
                zipData(filePaths);
            }

            if(type == 5){
                ShareTools.shareMoreToWechatFriend(mContext,filePaths);
            }


        }

    }

    void delFile(List<String> filePaths){

        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getString(R.string.deleting));
        new Thread(new Runnable() {
            @Override
            public void run() {

                for(String p : filePaths){
                    SafFile3 sf = new SafFile3(getContext(),p);
                    sf.delete();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();

                    }
                });

            }
        }).start();



    }

    void zipData(List<String> filePaths){

        final EditText text = new EditText(actPick);
        text.setText("");
        new androidx.appcompat.app.AlertDialog.Builder(actPick)
                .setTitle(R.string.input_pass)
                .setView(text)

                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = text.getText().toString();


                        File ff = new File(filePaths.get(0));
                        String sDirpath = ff.getParent();

                        String name = "";

                        LCProgressDialog pd = new LCProgressDialog(actPick,"正在加密",0);
                        pd.show();


                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList copyCachePaths = new ArrayList();
                                long size = 0;
                                for (String p : filePaths) {

                                    File f = new File(p);
                                    copyCachePaths.add(f);
                                    size = size + f.length();
                                }
                                ZipParameters zipParameters = new ZipParameters();
                                zipParameters.setEncryptFiles(true);
                                zipParameters.setCompressionLevel(CompressionLevel.FASTEST);
                                zipParameters.setEncryptionMethod(EncryptionMethod.AES);
// Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
                                zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                                zipParameters.setPassword(pass);

                                String dirPath = Environment.getExternalStorageDirectory() + "/" + "yisucache";

//                                File dirFile = new File(dirPath);
//                                if (!dirFile.exists()) {
//                                    dirFile.mkdir();
//                                }


                                String name = ff.getName();

                                if (name.contains(".")) {
                                    String[] strs = name.split("\\.");

                                    ArrayList<String> list = new ArrayList();
                                    list.addAll(Arrays.asList(strs));
                                    list.remove(list.size() - 1);
                                    if (list.size() == 0) {
                                        name = "";
                                    } else if (list.size() == 1) {
                                        name = (String) list.get(0);
                                    } else {
                                        for (String s : list) {
                                            name = name.length() == 0 ? s : (name + "_" + s);
                                        }
                                    }
                                }
                                name = name + "[" + getString(R.string.fm_add_pass) + TimeUtil.getCurrentTime("yyyy-MM-dd-hh-mm-ss") + "].zip";

                                //String zipPath = ff.getParent() + "/" + name;

                                String zipPath = UsbHelper.getInstance().getSdRootPath() + "/" + name;
                                ZipFile zipFile = new ZipFile(zipPath, pass.toCharArray());
                                try {
                                    for (Object p : copyCachePaths) {
                                        //String path = (String) p;
                                        File f = (File) p;

                                        long finalSize = size;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                pd.setMessage(getString(R.string.addpasing) + "(" + (copyCachePaths.indexOf(p) + 1) + "/" + copyCachePaths.size() + ")"+ getString(R.string.current_file) + ":" + FileTool.getFileSize(f.length())+ "\n" +  getString(R.string.all_files) + ":" + FileTool.getFileSize(finalSize));
                                                pd.setProgress(copyCachePaths.indexOf(p) * 100/copyCachePaths.size());
                                            }
                                        });

                                        if (f.isDirectory()) {
                                            zipFile.addFolder(f, zipParameters);
                                        } else {
                                            zipFile.addFile(f, zipParameters);
                                        }




                                    }


                                } catch (ZipException e) {
                                    e.printStackTrace();

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            pd.setMessage(e.getLocalizedMessage());
                                        }
                                    });
                                }

                                ArrayList tans = new ArrayList();
                                tans.add(zipPath);

//                                        buidTransDatas(tans,sDirpath,true);
//
                                String finalName = name;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        pd.dismiss();
//                                        Intent in = new Intent(getActivity(),Activity_FM_.class);
//                                        in.putExtra("postion", 1);
//                                        in.putExtra("dirPath", ff.getParent());
//                                        startActivity(in);

                                        new AlertDialog.Builder(actPick)
                                                .setTitle(getString(R.string.finsh_encrypt))
                                                .setMessage(finalName)
                                                .setPositiveButton(getString(R.string.go_find), new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // 获取输入框的内容

                                                        Intent in = new Intent(getActivity(),Activity_FM_.class);
                                                        in.putExtra("postion", 1);
                                                        in.putExtra("dirPath", UsbHelper.getInstance().getSdRootPath());//ff.getParent()
                                                        startActivity(in);


                                                    }
                                                })
                                                .setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // TODO Auto-generated method stub
                                                        dialog.dismiss();
                                                    }
                                                }).show();

                                        //showOper(false);
                                    }
                                });


                            }
                        }).start();

                    }
                })
                .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();

    }

    public void buidTransDatas(List filePaths,String toDirPath,Boolean delete){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mloadDialog = new LCProgressDialog(actPick,getString(R.string.add_copy_lists),0);

                mloadDialog.show();
            }
        });


        Object to =  new SafFile3(getContext(),toDirPath);//UsbHelper.getInstance().getRootFile();
        ArrayList tfs = new ArrayList();
        for(Object o : filePaths){

            //SafFile3 sf = (SafFile3) o;
            String path = (String) o;//sf.getPath();
            //FileTool.copyToUPanoRoot(new File(path),getContext());

            SafFile3 file = new SafFile3(getContext(),path);
            int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
            Fragment_ft.TransFile tf = new Fragment_ft.TransFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file);
            tf.setStatu(0);

            tf.setStatuDes(getString(R.string.wait_to_copy));

            tf.setCopyDes(0);
            tf.setCopyDes_des(getString(R.string.fm_copytoU));
            tf.setFileObj(file);
            tf.setDeleteWhenFinsh(delete);
            tf.setToFileObj(to);
            tf.setToDirPath(toDirPath);
            tfs.add(tf);

            int progress = filePaths.indexOf(o) *100 / filePaths.size();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mloadDialog.setProgress(progress);
                }
            });

        }
        TransFileManager.getInstance().addTransFiles(tfs);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(getActivity(),Activity_Task.class);
                startActivity(in);

                mloadDialog.dismiss();
            }
        });

    }


    public void toast(String str){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), str,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

/*
    private final long startTime = 0;
    private boolean mHasShowDownloadActive = false;
   // private TTAdNative mTTAdNative;
    public void loadAd(){
        loadBannerAd("951049956");

    }

    void initAd(){



        mExpressContainer =
                fl;
        //step2:创建TTAdNative对象
        mTTAdNative = AdManager.get().createAdNative(mContext);
        //step3:可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        //AdManager.get().requestPermissionIfNecessary(mContext);
        loadBannerAd("951049956");//951047517//951049956
    }

    private void loadBannerAd(String codeId) {
        //step4:创建广告请求参数AdSlot,注意其中的setNativeAdtype方法，具体参数含义参考文档
        int width = ViewTool.getScreenWidthInPx(mContext);
        int height = ViewTool.getScreenHeight(mContext);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1
                .setExpressViewAcceptedSize(ViewTool.px2dp(mContext,width),ViewTool.px2dp(mContext,width)/2) //期望模板广告view的size,单位dp
                .setAdLoadType(TTAdLoadType.LOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //请求失败回调
            @Override
            public void onError(int code, String message) {
                      toast( "load error : " + code + ", " + message);
                mExpressContainer.removeAllViews();
            }

            //请求成功回调
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads.get(0) == null) {
                    return;
                }

                final TTNativeExpressAd ad = ads.get(0);
                ad.setSlideIntervalTime(5 * 1000);
                bindAdListener(ad);
                ad.render();
                ////toast("load success!");
            }
        });

    }

    private void bindAdListener(TTNativeExpressAd ad) {

        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override

            public void onAdClicked(View view, int type) {
                AdManager.get().requestPermissionIfNecessary(mContext);
                //toast( "广告被点击");
            }

            @Override

            public void onAdShow(View view, int type) {
                //toast( "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                //toast( msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
                //toast( "渲染成功");
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
            }
        });
        //dislike设置
        bindDislike(ad, false);

        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                //toast( "点击开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    //toast( "下载中，点击暂停");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                //toast( "下载暂停，点击继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                //toast( "下载失败，点击重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                //toast( "安装完成，点击图片打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                //toast( "点击安装");
            }
        });
    }

//    /**
//     * 设置广告的不喜欢, 注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
//     *
//     * @param ad
//     * @param customStyle 是否自定义样式，true:样式自定义
//     */
/*
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {
                //toast( "bindDislike setDislikeCallback onShow");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                //toast("点击 " + value);
                mExpressContainer.removeAllViews();
                //用户选择不喜欢原因后，移除广告展示
                if (enforce) {
                    //toast("模版Banner 穿山甲sdk强制将view关闭了");
                }
            }

            @Override
            public void onCancel() {
                //toast("点击取消 ");
            }
        });
    }
*/



    public class FileSrcItem {
        private int iId;
        private String iName;

        public String getIcount() {
            return icount;
        }

        public void setIcount(String icount) {
            this.icount = icount;
        }

        private String icount;

        public FileSrcItem() {
        }

        public FileSrcItem(int iId, String iName,String icount) {
            this.iId = iId;
            this.iName = iName;
            this.icount = icount;
        }

        public int getiId() {
            return iId;
        }

        public String getiName() {
            return iName;
        }

        public void setiId(int iId) {
            this.iId = iId;
        }

        public void setiName(String iName) {
            this.iName = iName;
        }
    }
}