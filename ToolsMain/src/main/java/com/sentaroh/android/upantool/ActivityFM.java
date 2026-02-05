package com.sentaroh.android.upantool;

//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.RecyclerView;

//import static com.sentaroh.android.SMBSync2.Constants.ACTIVITY_REQUEST_CODE_SDCARD_STORAGE_ACCESS;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.storage.StorageManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.blackhao.utillibrary.log.LogHelper;
//import com.blackhao.utillibrary.usbHelper.USBBroadCastReceiver;
//import com.blackhao.utillibrary.usbHelper.UsbHelper;
//import com.github.mjdev.libaums.UsbMassStorageDevice;
//import com.github.mjdev.libaums.fs.UsbFile;
//import com.github.mjdev.libaums.fs.UsbFile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.sentaroh.android.upantool.R;

import com.sentaroh.android.Utilities3.SafFile3;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

//import me.jahnen.libaums.core.UsbMassStorageDevice;
//import me.jahnen.libaums.core.fs.UsbFile;

public class ActivityFM extends BaseActivity implements View.OnDragListener, UsbListener, USBBroadCastReceiver.UsbListener {

    private Fragment_filelist l_list;
    private Fragment_filelist u_list;
    private Fragment_file_grid l_grid;
    private Fragment_file_grid u_grid;

    private boolean isUpViewOpen = true;
    private boolean isDropped;
    private String TAG = "ActivityFM";
    private List ldata;
    private List udata;

    private List sldata = new ArrayList<>();
    private List sudata = new ArrayList<>();

    private int dataType = 0;
    private int currentPostion = -1;

    private List datas = new ArrayList();

    //本地文件列表相关
    private ArrayList<File> slocalList = new ArrayList<>();


    private ArrayList<File> localList = new ArrayList<>();
    private String localRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String localCurrentPath = "";
    private String localStartDir = "root";

    private Boolean showLList = true;


    //USB文件列表相关

    private ArrayList<SafFile3> susbList  = new ArrayList<>();
    private ArrayList<SafFile3> usbList  = new ArrayList<>();
    private UsbHelper usbHelper;

    private SafFile3 usbCurrentFile = null;
    private Boolean showUList = true;


    private ArrayList<Adapter_dir_header.RvItem> localDirList = new ArrayList<>();
    private ArrayList<Adapter_dir_header.RvItem> upDirList  = new ArrayList<>();
    private Adapter_dir_header localDirAd;
    private Adapter_dir_header upDirAd;
    private LoadingDialog mloadDialog;
    private LCProgressDialog mprogressDialogLC;
    private LinearLayout local;
    private LinearLayout uppan;
    private LinearLayout ll_nousb;
    private BroadcastReceiver usbreceiver;
    private TextView l_btn_sel;
    private TextView u_btn_sel;
    private ImageButton u_ib_arrow;
    private MyThreadPool myThreadPool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm);

        //getSupportActionBar().hide();
        setStatusBarLight(true);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        tb.setTitle(R.string.file_manager);

        if(false){
            LogHelper.getInstance().d("ACTION_USB_PERMISSION");
            File f1 = new File("/storage/F47E-6905" + "/" + "1234333");
            Boolean res = f1.mkdir();


            LogHelper.getInstance().d("U盘路径----"+ res +"####" + f1.exists() + "****" + f1.getName());


            File f2= new File("/storage/F47E-6905" + "/" + "1234333.txt");

            LogHelper.getInstance().d("U盘路径----"+ f2.getPath() +"####" + f2.exists() + "****" + f2.getName());
        }
        Intent intent =  getIntent();
        if(intent.getIntExtra("datatype",0) != 0) {
            dataType = intent.getIntExtra("datatype",0);
        }


        mloadDialog = new LoadingDialog(this,"",false);
        //mloadDialog.setCanceledOnTouchOutside(false);

        mprogressDialogLC = new LCProgressDialog(this,getString(R.string.loading),0);
        LogHelper.getInstance().initLogFile(this);
        ldata = new ArrayList();

        udata = new ArrayList();
        
        l_list = new Fragment_filelist();
//        l_list.setMedit(true);
//        l_list.longTapToDrag = true;
        u_list = new Fragment_filelist();
//        u_list.setMedit(true);
//        u_list.longTapToDrag = true;

        l_list.setMlist(ldata);
        u_list.setMlist(udata);

        l_grid = new Fragment_file_grid();
        u_grid = new Fragment_file_grid();

        l_grid.setMlist(ldata);
        u_grid.setMlist(udata);

        l_list.setListener(this);
        u_list.setListener(this);
        l_grid.setListener(this);
        u_grid.setListener(this);





//        findViewById(R.id.btn_fm_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        local = findViewById(R.id.layout_loacal_header);
        uppan = findViewById(R.id.layout_up_header);

        ll_nousb = findViewById(R.id.ll_nousb);
        ((TextView)(uppan.findViewById(R.id.tv_fm_header_local))).setText(R.string.fm_u);

        l_btn_sel = local.findViewById(R.id.btn_select);
        ImageButton l_ib_grid = local.findViewById(R.id.ib_grid);
        ImageButton l_ib_arrow = local.findViewById(R.id.ib_arrow);

        u_btn_sel = uppan.findViewById(R.id.btn_select);

        l_btn_sel.setVisibility(View.GONE);
        u_btn_sel.setVisibility(View.GONE);
        ImageButton u_ib_grid = uppan.findViewById(R.id.ib_grid);
        u_ib_arrow = uppan.findViewById(R.id.ib_arrow);

//        l_ib_list.setVisibility(View.GONE);
//        u_ib_list.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(dataType > 0){
            showLList = false;
//            l_ib_grid.setImageResource(R.mipmap.ic_fm_header_grid);
//            l_ib_list.setImageResource(R.mipmap.ic_fm_header_list_u);
            l_ib_grid.setImageResource(R.mipmap.ic_fm_header_list_u);
            transaction.add(R.id.fm_local, l_grid, String.valueOf(0));
        }else{
            transaction.add(R.id.fm_local, l_list, String.valueOf(0));
        }
        transaction.add(R.id.fm_up, u_list, String.valueOf(0));
        transaction.commitNow();

        l_btn_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView btn = (TextView) view;
                if(btn.getText().equals(getString(R.string.selectAll))){
                    l_btn_sel.setText(R.string.not_selectAll);
                    slocalList.clear();
                    slocalList.addAll(localList);



                }else{
                    l_btn_sel.setText(R.string.selectAll);
                    slocalList.clear();
                }
                sldata.clear();
                for(int i = 0;i< slocalList.size();i++){
                    File file = slocalList.get(i);


                    int icon = showLList ? FileTool.getResIdFromFileName(file.isDirectory(),file.getName()) : FileTool.getResIdFromFileNameBig(file.isDirectory(),file.getName());
                    sldata.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
                }
                l_list.setSlist(sldata);

                l_grid.setSlist(sldata);

            }
        });


        myThreadPool = new MyThreadPool(3, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());

        u_btn_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView btn = (TextView) view;
                if(btn.getText().equals(getString(R.string.selectAll))){
                    u_btn_sel.setText(R.string.not_selectAll);
                    susbList.clear();
                    susbList.addAll(usbList);
                }else {
                    u_btn_sel.setText(R.string.selectAll);
                    susbList.clear();
                }
                sudata.clear();
                for(int i = 0;i< susbList.size();i++){
                    SafFile3 file = susbList.get(i);


                    int icon = showLList ? FileTool.getResIdFromFileName(file.isDirectory(),file.getName()) : FileTool.getResIdFromFileNameBig(file.isDirectory(),file.getName());

                    BeanFile bf = new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : "","",icon,file);

                    Runnable runnable = new Runnable(){
                        @Override
                        public void run() {
                            bf.setSize(file.isDirectory() ? "" : FileTool.getFileSize(file.length()));
                        }
                    };
                    myThreadPool.execute(runnable);

                    sudata.add(bf);


                }
                u_list.setSlist(sudata);
                u_grid.setSlist(sudata);

            }
        });
        l_ib_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLList = !showLList;
                l_ib_grid.setImageResource(showLList ? R.mipmap.ic_fm_header_grid_u : R.mipmap.ic_fm_header_list_u);
                //l_ib_list.setImageResource(R.mipmap.ic_fm_header_list_u);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fm_local, showLList ? l_list : l_grid, String.valueOf(1));
                transaction.commitNow();

                if(dataType > 0){

                    if(currentPostion > -1) {
                        reloadSpecData(currentPostion);
                    }else{
                        initSpecData();
                    }
                }else{
                    reloadLocalFile();
                }
                if(showLList){
                    l_list.setMlist(ldata);
                    l_list.setSlist(sldata);
                }else {
                    l_grid.setMlist(ldata);
                    l_grid.setSlist(sldata);
                }
            }
        });
//        u_ib_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showUList = true;
//                u_ib_grid.setImageResource(R.mipmap.ic_fm_header_grid_u);
//                u_ib_list.setImageResource(R.mipmap.ic_fm_header_list);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fm_up, u_list, String.valueOf(0));
//                transaction.commitNow();
//
//                reloadUsbFile();
//            }
//        });
        u_ib_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUList = !showUList;
                u_ib_grid.setImageResource(showUList ? R.mipmap.ic_fm_header_grid_u : R.mipmap.ic_fm_header_list_u);
                //u_ib_list.setImageResource(R.mipmap.ic_fm_header_list_u);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fm_up,showUList ? u_list : u_grid, String.valueOf(1));
                transaction.commitNow();
                reloadUsbFile();
            }
        });

        l_ib_arrow.setOnClickListener(new View.OnClickListener() {
            private boolean isOpen = true;
            @Override
            public void onClick(View view) {

                LinearLayout layout = findViewById(R.id.fm_local);
                if(isOpen) {
                    layout.setVisibility(View.GONE);
                    findViewById(R.id.layout_local_oper).setVisibility(View.GONE);
                    isOpen = false;
                }else{
                    layout.setVisibility(View.VISIBLE);
                    findViewById(R.id.layout_local_oper).setVisibility(View.VISIBLE);
                    isOpen = true;
                }
                l_ib_arrow.setImageResource(isOpen ? R.mipmap.ic_fm_header_toclose : R.mipmap.ic_fm_header_toopen);
            }
        });

        u_ib_arrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LinearLayout layout = findViewById(R.id.fm_up);
                if(isUpViewOpen) {
                    layout.setVisibility(View.GONE);
                    findViewById(R.id.layout_up_oper).setVisibility(View.GONE);
                    isUpViewOpen = false;
                }else{
                    layout.setVisibility(View.VISIBLE);
                    findViewById(R.id.layout_up_oper).setVisibility(View.VISIBLE);
                    isUpViewOpen = true;
                }

                u_ib_arrow.setImageResource(isUpViewOpen ? R.mipmap.ic_fm_header_toclose : R.mipmap.ic_fm_header_toopen);
            }
        });

        initItemListTapCallBack();
        LinearLayout loper = findViewById(R.id.layout_local_oper);
        LinearLayout uoper = findViewById(R.id.layout_up_oper);
        ((Button)(loper.findViewById(R.id.btn_fm_oper_copy))).setText(R.string.fm_copytoU);

        ((Button)(loper.findViewById(R.id.btn_fm_oper_copy))).setOnClickListener(new View.OnClickListener() {

            private int copyFoldercount;

            @Override
            public void onClick(View view) {


                if (!usbHelper.canCopyToU()) {
                    toast(getString(R.string.no_usb));
                    mloadDialog.dismiss();
                    mprogressDialogLC.dismiss();
                    return;
                }


                if (slocalList.size() > 0) {
//                    mloadDialog.show();
//                    mloadDialog.setMessage(getString(R.string.copying));
                    mprogressDialogLC.setProgress(5);
                    mprogressDialogLC.setMessage(getString(R.string.copying));
                    mprogressDialogLC.show();
                    copyFoldercount = 0;
                } else {
                    toast(getString(R.string.unselect_file));
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < slocalList.size(); i++) {
                                File f = slocalList.get(i);

                                if (f.isDirectory()) {

                                    FileTool.copyLocalFolder(f, usbCurrentFile, usbHelper, new CopyRes() {
                                        @Override
                                        public void copyRes(int res) {
                                        }
                                    });
                                } else if (f.isFile()) {
                                    try {
                                        copyLocalFile(f);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    toast(getString(R.string.invaild_file));
                                }

                                int progress = i *100 / slocalList.size();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        mprogressDialogLC.setProgress(progress);
                                    }
                                });
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    slocalList.clear();
                                    sldata.clear();
                                    reloadUsbFile();

                                    //reloadUsbWithFile(usbCurrentFile);
                                    mprogressDialogLC.dismiss();

                                    //mloadDialog.dismiss();
                                    // 我这里默认是扫描所有文件夹，可以具体根据自己的需求更新自己的文件夹



                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

//                reloadLocalFile();
//                reloadUsbFile();

        });
        ((Button)(loper.findViewById(R.id.btn_fm_oper_rename))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for(int i = 0;i < slocalList.size();i++) {


                if(slocalList.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }
                    File f = slocalList.get(0);


               // }


                final EditText text = new EditText(ActivityFM.this);
                text.setText(f.getName());
                new AlertDialog.Builder(ActivityFM.this)
                        .setTitle(R.string.input_new_name)
                        .setView(text)

                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 获取输入框的内容




                                //dialog.dismiss();
                                slocalList.clear();
                                sldata.clear();
                                l_list.setSlist(sldata);
                                String fileName = "";

                                if(f.isDirectory()){
                                    fileName = text.getText().toString();
                                }else {

                                    fileName =  text.getText().toString() + "." +FileTool.getExtensionName(f.getName());


                                }
                                String des = f.getParent() + "/" + fileName;
                                f.renameTo(new File(des));

                                reloadLocalFile();
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
        });
        ((Button)(loper.findViewById(R.id.btn_fm_oper_newfile))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText text = new EditText(ActivityFM.this);
                new AlertDialog.Builder(ActivityFM.this)
                        .setTitle(R.string.input_new_name)
                        .setView(text)

                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(dataType > 0){

                                    String oneDir = "";
                                    String sonDir = "";
                                    if(dataType == 1) {
                                        oneDir = "WeChat";
                                    }else if(dataType == 2) {
                                        oneDir = "Pictures";

                                    }else if(dataType == 3) {
                                        oneDir = "Videos";
                                    }
                                    File pictures = new File(localRootPath + "/" + oneDir);
                                    if(!pictures.exists()){
                                        if (!pictures.mkdirs()){
                                            // Log.e(TAG, "create directory failed.");
                                            toast("create Pictures directory failed.");
                                        }
                                    }

                                    File src = new File(localRootPath + "/" + oneDir + "/" + text.getText().toString());

                                    if(!src.exists()){
                                        if (!src.mkdirs()){
                                            // Log.e(TAG, "create directory failed.");
                                            toast(text.getText().toString() + "directory failed.");
                                        }
                                    }

                                    if(localCurrentPath == null || localCurrentPath.equals("")){

                                        HashMap map = new HashMap();
                                        map.put(text.getText().toString(),new ArrayList<>());
                                        datas.add(map);
                                        initSpecData();
                                        reloadSpecData(datas.size() - 1);
                                    }

                                    return;
                                }

                                // 获取输入框的内容
                                slocalList.clear();
                                sldata.clear();
                                l_list.setSlist(sldata);
                                String PATH = localCurrentPath;
                                // 创建src和dst文件夹
                                // 【注】需要有PATH目录的权限才能创建子目录
                                // 若PATH文件夹权限为root权限，则使用adb shell chown system:system PATH修改为system权限
                                File src = new File(PATH + "/" + text.getText().toString());
                                if (!src.exists()) {
                                    if (!src.mkdirs()){
                                       // Log.e(TAG, "create directory failed.");
                                        toast("create directory failed.");
                                    }
                                }

                                reloadLocalFile();

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
        });
        ((Button)(loper.findViewById(R.id.btn_fm_oper_del))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(slocalList.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }
//                mloadDialog.setMessage(getString(R.string.deleting));
//                mloadDialog.show();

                mprogressDialogLC.setProgress(5);
                mprogressDialogLC.setMessage(getString(R.string.deleting));
                mprogressDialogLC.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        for(int i = 0;i < slocalList.size();i++) {
                            File f = slocalList.get(i);
//                    boolean bol = f.delete();
//                    if(bol){
//                        Log.d("ss",bol + "");
//                    }
                            FileTool.deleteFile(f);
                            if(dataType > 0){
                                HashMap map = (HashMap) datas.get(currentPostion);

                                String key = (String) map.keySet().toArray()[0];
                                Object obj =  map.get(key);
                                List d = (List) obj;
                                d.remove(f);
                            }

                            int progress = i *100 / slocalList.size();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mprogressDialogLC.setProgress(progress);
                                }
                            });

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mloadDialog.dismiss();
                                mprogressDialogLC.dismiss();
                                slocalList.clear();
                                sldata.clear();
                                l_list.setSlist(sldata);
                                if(dataType > 0){
                                    reloadSpecData(currentPostion);
                                    l_list.setMlist(localList);
                                    l_grid.setMlist(localList);
                                }else {

                                    reloadLocalFile();
                                }
                            }
                        });

                    }
                }).start();




            }
        });

        ((Button)(uoper.findViewById(R.id.btn_fm_oper_copy))).setOnClickListener(new View.OnClickListener() {
            private int copyFoldercount;
            @Override
            public void onClick(View view) {


                if (susbList.size() > 0) {
//                    mloadDialog.show();
//                    mloadDialog.setMessage(getString(R.string.copying));
                    mprogressDialogLC.setProgress(5);
                    mprogressDialogLC.setMessage(getString(R.string.copying));
                    mprogressDialogLC.show();
                    copyFoldercount = 0;
                } else {
                    toast(getString(R.string.unselect_file));
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < susbList.size(); i++) {
                            SafFile3 f = susbList.get(i);
                            String path = f.getPath();

                            if (f.isDirectory()) {


                                try {
                                    FileTool.copyUsbFolder(f, localCurrentPath, usbHelper, null);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    copyUSbFile(f);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            int progress = i *100 / susbList.size();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mprogressDialogLC.setProgress(progress);
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mprogressDialogLC.dismiss();
                                susbList.clear();
                                sudata.clear();

                                reloadLocalFile();

                                mloadDialog.dismiss();

                                Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                                sendBroadcast(intent);
                            }
                        });
                    }
                }).start();
            }
        });
        ((Button)(uoper.findViewById(R.id.btn_fm_oper_rename))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //for(int i = 0;i < slocalList.size();i++) {

                if(susbList.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }

                SafFile3 f = susbList.get(0);


                // }


                final EditText text = new EditText(ActivityFM.this);
                text.setText(f.getName());
                new AlertDialog.Builder(ActivityFM.this)
                        .setTitle(getString(R.string.input_new_name))
                        .setView(text)

                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 获取输入框的内容


                                //dialog.dismiss();
                                susbList.clear();
                                sudata.clear();
                                u_list.setSlist(sudata);
                                String fileName = "";

                                if(f.isDirectory()){
                                    fileName = text.getText().toString();
                                }else {


                                    fileName =  text.getText().toString() ;//+ "." +FileTool.getExtensionName(f.getName())


                                }
                               // String des = f.getParentFile() + "/" + fileName;

                                String path = f.getPath();
                                String path2 = f.getUri().getPath();
                                String path3 = f.getName();
//                                String path4 = GlobalWorkArea.getGlobalParameters(ActivityFM.this).safMgr.getSdcardRootPath();
                                f.renameTo(new SafFile3(f.getContext(),f.getParent() + "/" + fileName));

                                reloadUsbWithFile(usbCurrentFile);
                            }
                        })
                        .setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        }).show();


            }
        });
        ((Button)(uoper.findViewById(R.id.btn_fm_oper_newfile))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText text = new EditText(ActivityFM.this);
                new AlertDialog.Builder(ActivityFM.this)
                        .setTitle(getString(R.string.input_new_name))
                        .setView(text)

                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 获取输入框的内容
                                susbList.clear();
                                sudata.clear();
                                u_list.setSlist(sudata);
                                SafFile3 file = usbCurrentFile;

                                if(file == null || !file.exists()){
                                    toast("当前目录出错，无法新建，请重新插U盘");
                                    return;
                                }
                                // 创建src和dst文件夹
                                // 【注】需要有PATH目录的权限才能创建子目录
                                // 若PATH文件夹权限为root权限，则使用adb shell chown system:system PATH修改为system权限
 //                               try {
                                SafFile3 dir = new SafFile3(ActivityFM.this,file.getPath() + "/" + text.getText().toString());
                                file.mkdir();
//                                    file.createDirectory(text.getText().toString());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                    LogHelper.getInstance().e(e.getLocalizedMessage());
//                                }

                                reloadUsbWithFile(usbCurrentFile);

                            }
                        })
                        .setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        }).show();



            }
        });
        ((Button)(uoper.findViewById(R.id.btn_fm_oper_del))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(susbList.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }
//                mloadDialog.setMessage(getString(R.string.deleting));
//                mloadDialog.show();

                mprogressDialogLC.setProgress(5);
                mprogressDialogLC.setMessage(getString(R.string.deleting));
                mprogressDialogLC.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < susbList.size(); i++) {
                            SafFile3 f = susbList.get(i);
                            try {
                                //f.delete();
                                FileTool.deleteUsbFile(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int progress = i *100 / susbList.size();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mprogressDialogLC.setProgress(progress);
                                }
                            });

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mprogressDialogLC.dismiss();
                                mloadDialog.dismiss();
                                susbList.clear();
                                sudata.clear();
                                u_list.setSlist(sudata);
                                reloadUsbWithFile(usbCurrentFile);
                            }
                        });
                    }
                }).start();

            }
        });

        localDirAd = new Adapter_dir_header(getBaseContext(),localDirList);

        RecyclerView rv = local.findViewById(R.id.rv_dir_header);
        rv.setAdapter(localDirAd);

        localDirAd.setOnItemClickListener(new Adapter_dir_header.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Department dp = mDeptList.get(position);
//                getDetDaArrayUtils.javatas(dp.getOort_dcode(), 1);
//                mDeptList = mDeptList.subList(0,position);
//                adoh.refreshData(mDeptList);


                if(dataType > 0){
                    currentPostion = -1;
                    localList.clear();
                    slocalList.clear();
                    localDirList.clear();
                    ldata.clear();
                    sldata.clear();
                    ldata.clear();
                    for(int i = 0;i< datas.size();i++){
                        HashMap map = (HashMap) datas.get(i);
                        int icon =  showLList ? FileTool.getResIdFromFileName(true,null) : FileTool.getResIdFromFileNameBig(true,null);
                        ldata.add(new BeanFile((String) map.keySet().toArray()[0],"","","",icon,null));
                    }


                    if(showLList) {
                        l_list.setMlist(ldata);
                    }else {
                        l_grid.setMlist(ldata);
                    }

                    String [] keys = {"微信文件","图片","视频","微信文件","图片","视频"};

                    localDirList.add(new Adapter_dir_header.RvItem(keys[dataType - 1],null));
                    localDirAd.refreshData(localDirList);
                    return;
                }

                Adapter_dir_header.RvItem item = localDirList.get(position);
                localCurrentPath = item.obj.toString();
                reloadLocalFile();


                ArrayList list = new ArrayList();

                for(int i = 0;i<localDirList.size();i++) {
                    if(i > position){
                        break;
                    }
                    list.add(localDirList.get(i));
                }

                localDirList = list;

                localDirAd.refreshData(localDirList);

            }
        });


        upDirAd = new Adapter_dir_header(getBaseContext(),upDirList);

        RecyclerView urv =  uppan.findViewById(R.id.rv_dir_header);
        urv.setAdapter(upDirAd);

        upDirAd.setOnItemClickListener(new Adapter_dir_header.ItemClickListener() {
            @Override
            public void onItemClick(int position) {




                Adapter_dir_header.RvItem item = upDirList.get(position);
                usbCurrentFile = (SafFile3) item.obj;
                reloadUsbWithFile(usbCurrentFile);
                u_list.setMlist(udata);
                u_grid.setMlist(udata);
                ArrayList list = new ArrayList();

                for(int i = 0;i<upDirList.size();i++) {
                    if(i > position){
                        break;
                    }
                    list.add(upDirList.get(i));
                }

                upDirList = list;

                upDirAd.refreshData(upDirList);

            }
        });


        findViewById(R.id.tv_ges).setOnTouchListener(new View.OnTouchListener() {
            int lastY = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;

//               int action = motionEvent.getAction();
//               switch (action){
//                   case MotionEvent.ACTION_DOWN:
//                       lastY = (int) motionEvent.getRawY();
//                   case MotionEvent.ACTION_MOVE:
//                       int offsetY = (int) motionEvent.getRawY() - lastY;
//
//                       Log.d(TAG, "onTouch: " + offsetY);
//
////                       LinearLayout layout = findViewById(R.id.fm_local);
////                       View.LayoutParams params = layout.getLayoutParams();
////                       params.
////                       Log.d(TAG, "onTouch: " + layout.getLayoutParams().toString());
////
////                       Log.d(TAG, "onTouch: " + view.getLayoutParams().toString());
////
////                       layout.layout(view.getLeft(),view.getTop(),view.getRight(),view.getBottom() + offsetY);
////                       layout.postInvalidate();
//                       view.layout(view.getLeft(),view.getTop() + offsetY,view.getRight(),view.getBottom() + offsetY);
//
//                       view.postInvalidate();
//                       break;
//                   default:
//                       break;
//                       //
//               }
//
//                return true;
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }, 50);

        // initData();


        usbreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                        || action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) SystemClock.sleep(1000);

                        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                            //toast("已插入");

                           // readSize();


                        }else if (action.equals(Intent.ACTION_MEDIA_REMOVED)){
                            //toast("已移除");

                            udata.clear();
                            susbList.clear();
                            u_list.setMlist(udata);
                            u_grid.setMlist(udata);
                            upDirList.clear();
                            upDirAd.refreshData(upDirList);

                        }else {
                            //toast("未成功");
                            udata.clear();
                            susbList.clear();
                            u_list.setMlist(udata);
                            u_grid.setMlist(udata);
                            upDirList.clear();
                            upDirAd.refreshData(upDirList);
                        }
                    }
            }
        };



        IntentFilter media_filter = new IntentFilter();
        media_filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        media_filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        media_filter.addAction(Intent.ACTION_MEDIA_EJECT);
        media_filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        media_filter.addDataScheme("file");
        registerReceiver(usbreceiver, media_filter);

    }



    private void initData(){

        //mloadDialog.show();






//            act.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {

        mprogressDialogLC.setProgress(3);
        mprogressDialogLC.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                initUsbFile();
                if (dataType == 0) {
                    initLocalFile();

                } else {
                    if (dataType == 1) {

                        datas = FileTool.getWeixi(ActivityFM.this);
                    }
                    if (dataType == 2) {

                        datas = FileTool.getPic(ActivityFM.this);
                    }
                    if (dataType == 3) {

                        datas = FileTool.getVideos(ActivityFM.this);
                    }

                    if (dataType == 4) {

                        Intent intent =  getIntent();
                        ArrayList filePaths = intent.getStringArrayListExtra("filePaths");
                        String className = intent.getStringExtra("class");

                        ArrayList files = new ArrayList();
                        for(Object o : filePaths){
                            String p = (String) o;


                            files.add(new File(p));


                        }

                        HashMap map = new HashMap();
                        map.put(className,files);


                        datas = new ArrayList();
                        datas.add(map);
                    }
                    //主线程更新UI

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(dataType > 0) {
                            initSpecData();
                            l_grid.setMlist(ldata);
                        }

                        l_list.setMlist(ldata);
                        u_list.setMlist(udata);

                        mprogressDialogLC.dismiss();
                        upDirAd.refreshData(upDirList);
                        localDirAd.refreshData(localDirList);
                    }
                });
            }

        }).start();



    }


    private void initSpecData(){
        localList.clear();
        slocalList.clear();
        ldata.clear();
        sldata.clear();
        ldata.clear();
        localDirList.clear();
        for(int i = 0;i< datas.size();i++){
            HashMap map = (HashMap) datas.get(i);
            int icon = showLList ? FileTool.getResIdFromFileName(true,null) : FileTool.getResIdFromFileNameBig(true,null);
            ldata.add(new BeanFile((String) map.keySet().toArray()[0],"","","",icon,null));
        }


        String [] keys = {"微信文件","图片","视频","微信文件","图片","视频"};

        localDirList.add(new Adapter_dir_header.RvItem(keys[dataType - 1],null));
        localDirAd.refreshData(localDirList );
    }

    private void reloadSpecData(int position){
        currentPostion = position;
        HashMap map = (HashMap) datas.get(position);

        String key = (String) map.keySet().toArray()[0];
        Object obj =  map.get(key);

        File src = null;
        if(dataType == 1) {
            src = new File(localRootPath + "/WeChat/" + key);
        }else if(dataType == 2) {
            src = new File(localRootPath + "/Pictures/" + key);

        }else if(dataType == 3) {
            src = new File(localRootPath + "/Videos/" + key);

        }
        if(src.exists()){
            localCurrentPath = src.getAbsolutePath();
        }



        localList.clear();
        slocalList.clear();
        ldata.clear();
        sldata.clear();

        localList.addAll((Collection<? extends File>) obj); //= (ArrayList<File>) obj;
        ldata.clear();
        for(int i = 0;i< localList.size();i++){
            File file = localList.get(i);
            int icon = showLList ? FileTool.getResIdFromFileName(file.isDirectory(),file.getName()) : FileTool.getResIdFromFileNameBig(file.isDirectory(),file.getName());
            ldata.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
        }
        if(localDirList.size() > 1){
            localDirList.remove(1);
        }
        localDirList.add(new Adapter_dir_header.RvItem(key,null));
        localDirAd.refreshData(localDirList );
    }


    private void initItemListTapCallBack(){
        l_list.setOnItemClickListener(new Fragment_filelist.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                sldata.clear();
                slocalList.clear();
                l_list.setSlist(sldata);
                l_btn_sel.setText(R.string.selectAll);




                if(dataType > 0){
                    if(localList.size() > 0){
                        File f = localList.get(position);
                        if(!f.exists()){
                            toast(getString(R.string.unselect_file));
                            return;
                        }
                        try {
                             Intent in = FileTool.openFile(f.getAbsolutePath(),getBaseContext());
                            if(in == null){
                                toast(getString(R.string.unsupport_open_file));
                            }else{
                                startActivity(in);
                            }
                        } catch (ActivityNotFoundException e) {
                            toast(getString(R.string.no_app_installed));
                        }
                        return;
                    }
                    reloadSpecData(position);

                    l_list.setMlist(ldata);
                    l_grid.setMlist(ldata);



                    return;
                }

                File f = localList.get(position);

                if(f.isDirectory()){

                    reloadDataWhileLocalListItemClik(f);
                    l_list.setMlist(ldata);
                    l_list.setSlist(sldata);
                    localDirList.add(new Adapter_dir_header.RvItem(f.getName(),localCurrentPath));
                    localDirAd.refreshData(localDirList);
                }else{
                    //打开文件

                    if(!f.exists()){
                        toast("无效文件");
                        return;
                    }
                    Intent in = FileTool.openFile(f.getAbsolutePath(),getBaseContext());
                    if(in != null) {
                        try {
                            startActivity(in);
                        } catch (ActivityNotFoundException e) {
                            toast(getString(R.string.no_app_installed));
                        }
                    }else {
                        toast(getString(R.string.unsupport_open_file));
                    }
                }
            }

            @Override
            public void onItemCheckClick(int position) {
                File f = localList.get(position);
                reloadDataWhileLocalListItemCheckClik(f);
                l_list.setSlist(sldata);
                l_btn_sel.setText(R.string.selectAll);
                if(sldata.size() == ldata.size()){
                    l_btn_sel.setText(R.string.not_selectAll);
                }

            }

            @Override
            public void onItemLongClick(boolean edit) {

                l_btn_sel.setVisibility(edit ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onDirItemClick(int position, Object obj) {

            }

        });

        u_list.setOnItemClickListener(new Fragment_filelist.ItemClickListener() {
            @Override
            public void onItemClick(int position) {


                sudata.clear();
                susbList.clear();
                u_list.setSlist(sudata);
                u_btn_sel.setText(R.string.selectAll);

                SafFile3 f = usbList.get(position);
                if(f.isDirectory()){
                    reloadDataWhileUPListItemClik(f);
                    u_list.setMlist(udata);
                    u_list.setSlist(new ArrayList<>());
                    upDirList.add(new Adapter_dir_header.RvItem(f.getName(),f));
                    upDirAd.refreshData(upDirList );
                }else{
                    //打开文件
                   // toast("需先复制到手机，才能打开")     vc;'[[



                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String path = FileTool.copyToLocalCacheToPaly(f,ActivityFM.this);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent in = FileTool.openFile(path,getBaseContext());
                                    if(in != null) {
                                        try {
                                            startActivity(in);
                                        } catch (ActivityNotFoundException e) {
                                            toast(getString(R.string.no_app_installed));
                                        }
                                    }else {
                                        toast(getString(R.string.unsupport_open_file));
                                    }
                                }
                            });

                        }
                    }).start();









//                    Intent intent = new Intent("android.intent.action.VIEW");
//                    intent.addCategory("android.intent.category.DEFAULT");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    Uri uri;
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        //记得修改com.xxx.fileprovider与androidmanifest相同
//                        uri = f.getUri();//FileProvider.getUriForFile(this,getApplicationInfo().processName + ".provider",new File(param));
//                    }else{
//                        uri = f.getUri();//Uri.fromFile(new File(param));
//                    }
//                    intent.setDataAndType(uri, "application/pdf");
//
//                    if(intent != null) {
//                        try {
//                            startActivity(intent  );
//                        } catch (ActivityNotFoundException e) {
//                            toast("未安装相关可打开的应用");
//                        }
//                    }else {
//                        toast("不支持的文件类型");
//                    }
                }
            }

            @Override
            public void onItemCheckClick(int position) {
                SafFile3 f = usbList.get(position);
                reloadDataWhileUPListItemCheckClik(f);
                u_list.setSlist(sudata);

                u_btn_sel.setText(R.string.selectAll);
                if(sudata.size() == udata.size()){
                    u_btn_sel.setText(R.string.not_selectAll);
                }

            }

            @Override
            public void onItemLongClick(boolean edit) {

                u_btn_sel.setVisibility(edit ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onDirItemClick(int position, Object obj) {

            }
        });


///////////////////////////////////////////////////////////////////////////////////////
        l_grid.setOnItemClickListener(new Fragment_file_grid.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                sldata.clear();
                slocalList.clear();
                l_grid.setSlist(sudata);
                l_btn_sel.setText(R.string.selectAll);



                if(dataType > 0){
                    if(localList.size() > 0){
                        File f = localList.get(position);
                        if(!f.exists()){
                            toast(getString(R.string.invaild_file));
                            return;
                        }
                        try {
                             Intent in = FileTool.openFile(f.getAbsolutePath(),getBaseContext());
                            if(in == null){
                                toast(getString(R.string.unsupport_open_file));
                            }else{
                                startActivity(in);
                            }
                        } catch (ActivityNotFoundException e) {
                            toast(getString(R.string.no_app_installed));
                        }
                        return;
                    }
                    reloadSpecData(position);
                    l_grid.setMlist(ldata);

                    return;
                }

                File f = localList.get(position);
                if(f.isDirectory()){

                    reloadDataWhileLocalListItemClik(f);
                    l_grid.setMlist(ldata);
                    l_grid.setSlist(sldata);
                    localDirList.add(new Adapter_dir_header.RvItem(f.getName(),localCurrentPath));
                    localDirAd.refreshData(localDirList);

                }else{
                    //打开文件
                    if(!f.exists()){
                        toast("无效文件");
                        return;
                    }
                    try {
                         Intent in = FileTool.openFile(f.getAbsolutePath(),getBaseContext());
                            if(in == null){
                                toast(getString(R.string.unsupport_open_file));
                            }else{
                                startActivity(in);
                            }
                    } catch (ActivityNotFoundException e) {
                        toast(getString(R.string.no_app_installed));
                    }
                }
            }

            @Override
            public void onItemCheckClick(int position) {
                File f = localList.get(position);
                reloadDataWhileLocalListItemCheckClik(f);
                l_grid.setSlist(sldata);

            }

            @Override
            public void onItemLongClick(boolean edit) {
                l_btn_sel.setVisibility(edit ? View.VISIBLE : View.GONE);
            }
        });

        u_grid.setOnItemClickListener(new Fragment_file_grid.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                sudata.clear();
                susbList.clear();
                u_grid.setSlist(sudata);
                u_btn_sel.setText(R.string.selectAll);

                SafFile3 f = usbList.get(position);
                if(f.isDirectory()){
                    reloadDataWhileUPListItemClik(f);
                    u_grid.setMlist(udata);
                    u_grid.setSlist(new ArrayList<>());
                    upDirList.add(new Adapter_dir_header.RvItem(f.getName(),f));
                    upDirAd.refreshData(upDirList );
                }else{
                    //打开文件

                    String path = FileTool.copyToLocalCacheToPaly(f,ActivityFM.this);


                    Intent in = FileTool.openFile(path,getBaseContext());
                    if(in != null) {
                        try {
                            startActivity(in);
                        } catch (ActivityNotFoundException e) {
                            toast(getString(R.string.no_app_installed));
                        }
                    }else {
                        toast(getString(R.string.unselect_file));
                    }
                   // toast("需先复制到手机，才能打开");
                }
            }
            @Override
            public void onItemCheckClick(int position) {
                SafFile3 f = usbList.get(position);
                reloadDataWhileUPListItemCheckClik(f);
                u_grid.setSlist(sudata);

            }

            @Override
            public void onItemLongClick(boolean edit) {
                l_btn_sel.setVisibility(edit ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void reloadDataWhileLocalListItemCheckClik(File f){
        if(slocalList.contains(f)){
            slocalList.remove(f);
        }else {
            slocalList.add(f);
        }

        sldata.clear();
        for(int i = 0;i< slocalList.size();i++){
            File file = slocalList.get(i);


            int icon = showLList ? FileTool.getResIdFromFileName(file.isDirectory(),file.getName()) : FileTool.getResIdFromFileNameBig(file.isDirectory(),file.getName());
            sldata.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
        }
    }

    private void reloadDataWhileUPListItemCheckClik(SafFile3 f) {
        if(susbList.contains(f)){
            susbList.remove(f);
        }else {
            susbList.add(f);
        }

        sudata.clear();
        for(int i = 0;i< susbList.size();i++){
            SafFile3 file = susbList.get(i);

            int icon = showUList ? FileTool.getResIdFromFileName(file.isDirectory(),file.getName()) : FileTool.getResIdFromFileNameBig(file.isDirectory(),file.getName());
            sudata.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
        }

    }
    private void reloadDataWhileLocalListItemClik(File f) {
        localList.clear();
        slocalList.clear();
        ldata.clear();
        sldata.clear();

        localList = (ArrayList<File>) FileTool.listfilesortbymodifytime(f.getPath());
        //Collections.addAll(localList, f.listFiles());
        localCurrentPath = f.getPath();
        ldata.clear();
        for(int i = 0;i< localList.size();i++){
            File file = localList.get(i);
            int icon = showLList ? FileTool.getResIdFromFileName(file.isDirectory(),file.getName()) : FileTool.getResIdFromFileNameBig(file.isDirectory(),file.getName());
            ldata.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
        }


    }

    private void reloadDataWhileUPListItemClik(SafFile3 f) {

        usbList.clear();
        susbList.clear();
        sudata.clear();
        udata.clear();
        usbCurrentFile = f;
 //       try {
        Collections.addAll(usbList,f.listFiles());//f.listFiles()

        if(usbList.size() < 50) {
            usbList = (ArrayList<SafFile3>) FileTool.listUsbfilesortbymodifytime_list(usbList);
        }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        for(int i = 0;i< usbList.size();i++){
            SafFile3 file = usbList.get(i);
            int icon = showUList ? FileTool.getResIdFromFileName(file.isDirectory(),file.getName()) : FileTool.getResIdFromFileNameBig(file.isDirectory(),file.getName());
            udata.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
        }


    }


    /**
     * 初始化本地文件列表
     */
    private void initLocalFile() {

        localList = new ArrayList<>();
        localDirList.add(new Adapter_dir_header.RvItem("/" + localStartDir,localRootPath));


        File f = new File(localRootPath);

        if(f == null){
            return;
        }
        File [] files = f.listFiles();

        if(files == null){
           toast("无此目录,可能未授权访问权限");
           return;
       }

        if(files.length > 0) {
            //localList = (ArrayList<File>) Arrays.asList(files);
            localList = (ArrayList<File>) FileTool.listfilesortbymodifytime(localRootPath);
            //Collections.addAll(localList, new File(localRootPath).listFiles());
            localCurrentPath = localRootPath;
            ldata.clear();
            for (int i = 0; i < localList.size(); i++) {
                File file = localList.get(i);
                ldata.add(new BeanFile(file.getName(), file.getPath(), file.isDirectory() ? "" : FileTool.getFileSize(file.length()), "", FileTool.getResIdFromFileName(file.isDirectory(), file.getName()), file));


            }

        }else{
            toast(localRootPath);
        }

    }

    private void reloadLocalFile() {

        File f = new File(localCurrentPath);

        if(f.isDirectory()){

            reloadDataWhileLocalListItemClik(f);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(showLList){
                        l_list.setMlist(ldata);
                        l_list.setSlist(new ArrayList<>());
                    }else{
                        l_grid.setMlist(ldata);
                        l_grid.setSlist(new ArrayList<>());
                    }
                    u_list.setSlist(sudata);
                    u_grid.setSlist(sudata);
                }
            });
        }else{
            //打开文件
        }
    }

    private void reloadUsbFile() {

        if(usbCurrentFile == null){
            toast(getString(R.string.no_usb));
            return;
        }
        SafFile3 f = usbCurrentFile;

        if(f.isDirectory()){
           reloadDataWhileUPListItemClik(f);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(showUList) {
                        u_list.setMlist(udata);
                        u_list.setSlist(new ArrayList<>());
                    }else{
                        u_grid.setMlist(udata);
                        u_grid.setSlist(new ArrayList<>());
                    }
                    u_list.setSlist(sudata);
                    u_grid.setSlist(sudata);
                }
            });



        }else{
            //打开文件
        }


    }

    private void reloadUsbWithFile(SafFile3 f) {

        //SafFile3 f = //usbHelper.getCurrentFolder();

        //SafFile3 file1 = (f.getAbsolunew UsbFiletePath());

        if(f.equals(usbHelper.getRootFile())){
           // updateUsbFile(0);
            initUsbFile();
            return;
        }
        if(f.isDirectory()){
            usbList.clear();
            susbList.clear();
            sudata.clear();
            udata.clear();
  //          try {
                Collections.addAll(usbList, f.listFiles());
                usbList  = (ArrayList<SafFile3>) FileTool.listUsbfilesortbymodifytime_list(usbList);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            udata.clear();
            for(int i = 0;i< usbList.size();i++){
                SafFile3 file = usbList.get(i);
                udata.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",FileTool.getResIdFromFileName(file.isDirectory(),file.getName()),file));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    u_list.setMlist(udata);
                    u_list.setSlist(new ArrayList<>());
                }
            });



        }else{
            //打开文件
        }


    }

    private void initUsbFile() {

        LogHelper.getInstance().d("initUsbFile");

        usbHelper = UsbHelper.getInstance();
        if(!usbHelper.safMgr.isStoragePermissionRequired() && usbHelper.canCopyToU()) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ll_nousb.setVisibility(View.GONE);
                }
            });

            updateUsbFile(0);
        }else{

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout layout = findViewById(R.id.fm_up);
                    if(isUpViewOpen) {
                        layout.setVisibility(View.GONE);
                        findViewById(R.id.layout_up_oper).setVisibility(View.GONE);
                        isUpViewOpen = false;
                    }

                    u_ib_arrow.setImageResource(isUpViewOpen ? R.mipmap.ic_fm_header_toclose : R.mipmap.ic_fm_header_toopen);
                }
            });

        }
    }


    private void updateUsbFile(int position) {


        if(usbHelper.getRootFile() != null){
               //存在USB
            if(!usbHelper.getRootFile().exists()){
                return;
            }

            usbList.clear();
            susbList.clear();
            sudata.clear();
            udata.clear();
            upDirList.clear();

            // toast(upDirList.toString());


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mprogressDialogLC.setMessage(getString(R.string.read_directory));
                    mprogressDialogLC.setProgress(20);
                }
            });
            long time = System.currentTimeMillis()/1000;
            usbList.addAll(Arrays.asList(usbHelper.getRootFile().listFiles()));


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mprogressDialogLC.setMessage(getString(R.string.read_fileData));
                    mprogressDialogLC.setProgress(20);
                }
            });
            Log.d(TAG, "updateUsbFile: "+(System.currentTimeMillis()/1000 - time) + "size" +  usbList.size());
            time = System.currentTimeMillis()/1000;
            if(usbList.size() < 50) {

                usbList = (ArrayList<SafFile3>) FileTool.listUsbfilesortbymodifytime_list(usbList);
            }

            Log.d(TAG, "updateUsbFile: "+(System.currentTimeMillis()/1000 - time) + "" );
            time = System.currentTimeMillis()/1000;

            upDirList.add(new Adapter_dir_header.RvItem("/root",usbHelper.getRootFile()));
            usbCurrentFile = usbHelper.getRootFile();

            Log.d(TAG, "updateUsbFile: "+(System.currentTimeMillis()/1000 - time) + "" );
            time = System.currentTimeMillis()/1000;



            List tmpList = new ArrayList();
            tmpList.addAll(usbList);
            for(int i = 0;i< tmpList.size();i++){
                SafFile3 file = (SafFile3) tmpList.get(i);


                if(file.getName() == null || file.getName().startsWith(".")){

                    usbList.remove(file);
                    continue;
                }

                BeanFile bf = new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : "","",FileTool.getResIdFromFileName(file.isDirectory(),file.getName()),file);

                Runnable runnable = new Runnable(){
                    @Override
                    public void run() {
                        bf.setSize(file.isDirectory() ? "" : FileTool.getFileSize(file.length()));
                    }
                };
                myThreadPool.execute(runnable);

                udata.add(bf);
//                udata.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",FileTool.getResIdFromFileName(file.isDirectory(),file.getName()),file));

                int progress = i *100 / tmpList.size();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mprogressDialogLC.setProgress(progress);
                    }
                });

            }
            Log.d(TAG, "updateUsbFile: "+(System.currentTimeMillis()/1000 - time) + "" );
            time = System.currentTimeMillis()/1000;


        } else {
            Log.e("UsbTestActivity", "No Usb Device");
            usbList.clear();
            susbList.clear();
            sudata.clear();
            udata.clear();
            upDirList.clear();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    u_list.setMlist(udata);
                    upDirAd.refreshData(upDirList);
                    ll_nousb.setVisibility(View.VISIBLE);
                }
            });


        }
    }
    protected void setStatusBarLight(boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // >=5.0 背景为全透明
            /* >=5.0，this method(getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS));
            in some phone is half-transparent like vivo、nexus6p..
            in some phone is full-transparent
            so ...*/
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (light) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//| View.SYSTEM_UI_FLAG_VISIBLE
                } else {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
//                window.setStatusBarColor(Color.TRANSPARENT);
                window.setStatusBarColor(getResources().getColor(R.color.mainColor));
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                if (light) {
                    window.setStatusBarColor(getResources().getColor(R.color.mainColor));
                } else {
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4背景为渐变半透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {

        DragEvent event = dragEvent;
        switch (event.getAction()) {
            case DragEvent.ACTION_DROP:
                isDropped = true;
                int positionTarget = -1;
                View viewSource = (View) event.getLocalState();
                if(viewSource != null){
                    if(viewSource.getParent().equals(view)){

                        Log.d(TAG, "onDrag: 没有移动");
                        return true;
                    }

                    Object src = null;
                    int srcIndex = -1;
                    if(viewSource.getParent() instanceof RecyclerView){

                        RecyclerView rv = (RecyclerView) viewSource.getParent();
                        BeanFile file = (BeanFile) viewSource.getTag();
                        //Log.d(TAG, "onDrag: 获取item的index=" + index);
                        AdapterFMList adp  = (AdapterFMList) rv.getAdapter();
                        //

                        srcIndex = adp.getmItems().indexOf(file);
                        src = file.getObj();
//                        src = file;
//
//
//                        List data =  adp.getmItems();
//
//                        srcIndex = data.indexOf(file);
//                        data.remove(file);
//
//                        adp.refresh(data);
//                        Log.d(TAG, "onDrag:获取item项 ");
//
//                        Log.d(TAG, "onDrag:删除并刷新");
                    }

                    if(viewSource.getParent() instanceof GridView){

                        GridView gv = (GridView) viewSource.getParent();
                        MyAdapter.ViewHolder holder  = (MyAdapter.ViewHolder) viewSource.getTag();

                        BeanFile file = (BeanFile) holder.obj;

                        MyAdapter adp  = (MyAdapter) gv.getAdapter();

                        srcIndex = adp.getmData().indexOf(file);

                        src = file.getObj();
                        //
                        Log.d(TAG, "onDrag:获取item项 ");

                        Log.d(TAG, "onDrag:删除并刷新");
                    }
                    if(srcIndex == -1 && src != null){
                        return true;
                    }
                    if(src instanceof File){
                        //copyLocalFile(src);



                        {


                            if (usbHelper.getUsbUUid() == null) {
                                toast(getString(R.string.no_usb));
                                mloadDialog.dismiss();
                                return true;
                            }


                                mloadDialog.show();
                                mloadDialog.setMessage(getString(R.string.copying));

                            File f = (File) src;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        {


                                            if (f.isDirectory()) {

                                                FileTool.copyLocalFolder(f, usbCurrentFile, usbHelper, new CopyRes() {
                                                    @Override
                                                    public void copyRes(int res) {
                                                    }
                                                });
                                            } else if (f.isFile()) {
                                                try {
                                                    copyLocalFile(f);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                toast("无效文件");
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                slocalList.clear();
                                                sldata.clear();
                                                l_list.setSlist(sldata);
                                                l_grid.setSlist(sldata);
                                                reloadUsbFile();
                                                mloadDialog.dismiss();

                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }

                    }else {
                        {


                            mloadDialog.show();
                            mloadDialog.setMessage(getString(R.string.no_app_installed));

                            SafFile3 f = (SafFile3) src;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    {


                                        if (f.isDirectory()) {
                                            try {
                                                FileTool.copyUsbFolder(f, localCurrentPath, usbHelper, null);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                copyUSbFile(f);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            susbList.clear();
                                            sudata.clear();

                                            reloadLocalFile();

                                            mloadDialog.dismiss();
                                        }
                                    });
                                }
                            }).start();
                        }
                    }


                }
        }
        if (!isDropped && event.getLocalState() != null) {
            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void insertUsb(UsbDevice device_add) {

        LogHelper.getInstance().d("insertUsb" + device_add.toString());



        String[] result = null;
        StorageManager storageManager = (StorageManager)getSystemService(Context.STORAGE_SERVICE);
        try {
            Method method = StorageManager.class.getMethod("getVolumePaths");
            method.setAccessible(true);
            try {
                result =(String[])method.invoke(storageManager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < result.length; i++) {
                System.out.println("path----> " + result[i]+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (udata.size() == 0) {
            //toast("insertUsb" + udata.size());
            updateUsbFile(0);
        }
    }

    @Override
    public void removeUsb(UsbDevice device_remove) {
        LogHelper.getInstance().d("removeUsb" + device_remove.toString());
        ll_nousb.setVisibility(View.GONE);
    }

    @Override
    public void getReadUsbPermission(UsbDevice usbDevice) {
        //toast("getReadUsbPermission");
        updateUsbFile(0);
        LogHelper.getInstance().d("getReadUsbPermission" + usbDevice.toString());
    }

    @Override
    public void failedReadUsb(UsbDevice usbDevice) {
        LogHelper.getInstance().d("failedReadUsb" + usbDevice.toString());
        ll_nousb.setVisibility(View.GONE);
    }


    private void copyLocalFile(final File file) throws IOException {

        if(!usbHelper.canCopyToU()){

            toast(getString(R.string.unselect_file));
            return;
        }

        String usbCurrentPath = "";
        for(int i = 0;i<upDirList.size();i++){


            Adapter_dir_header.RvItem item  =  upDirList.get(i);
            if(i != 0){
                usbCurrentPath = usbCurrentPath + "/" + item.text;
            }
        }

        usbHelper.moveCopyInternalToExternal(file.getAbsolutePath().substring(localRootPath.length()), usbCurrentPath + "/" + file.getName());
        //UsbHelper.copyFileInternalToExternal (localCurrentPath,file,to_path,file.getName());

        //reloadUsbWithFile(usbCurrentFile);

        return;



        }

    private void copyUSbFile(SafFile3 file) throws IOException {
        //复制到本地的文件路径

//        toast("copyUSb");
//
//        mloadDialog.show();



        String locCurrentPath = "";
        for(int i = 0;i<localDirList.size();i++){


            Adapter_dir_header.RvItem item  =  localDirList.get(i);
            if(i != 0){
                locCurrentPath = locCurrentPath + "/" + item.text;
            }
        }



        String usbCurrentPath = "";
        for(int i = 0;i<upDirList.size();i++){


            Adapter_dir_header.RvItem item  =  upDirList.get(i);
            if(i != 0){
                usbCurrentPath = usbCurrentPath + "/" + item.text;
            }
        }

        usbHelper.moveCopyExternalToInternal(usbCurrentPath + "/" + file.getName(),locCurrentPath + "/" + file.getName());



//        reloadLocalFile();
//        mloadDialog.dismiss();


    }

    private void openUsbFile(SafFile3 file) {
        if (file.isDirectory()) {
//            //文件夹更新列表
//            usbList.clear();
//            usbList.addAll(usbHelper.getUsbFolderFileList(file));
//            usbAdapter.notifyDataSetChanged();
//            initLocalFile();
//            initUsbFile();
        } else {
            //开启线程，将文件复制到本地
           // copyUSbFile(file);
        }
    }
    private void openLocalFile(File file) {
        if (file.isDirectory()) {
//            //文件夹更新列表
//            localList.clear();
//            Collections.addAll(localList, file.listFiles());
//            localAdapter.notifyDataSetChanged();
//            localCurrentPath = file.getAbsolutePath();
//            initLocalFile();
//            initUsbFile();
        } else {
            //开启线程，将文件复制到本地
            //copyLocalFile(file);
        }
    }













}