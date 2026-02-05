package com.sentaroh.android.upantool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RecoverySystem;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.FilePathSelector.Activity_filePathSelector;
import com.sentaroh.android.upantool.contact.FileUtil;
import com.zhihu.matisse.Config;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activity_FM_ extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private String[] titles = null;
    private FmPagerAdapter pagerAdapter;
    private Fragment_filelist l_list;
    private Fragment_filelist u_list;


    private List ldata = new ArrayList<>();
    private List udata = new ArrayList<>();
    private List sldata = new ArrayList<>();
    private List sudata = new ArrayList<>();

    private ArrayList<File> slocalList  = new ArrayList<>();
    private ArrayList<File> localList  = new ArrayList<>();
    private ArrayList<SafFile3> usbList  = new ArrayList<>();
    private ArrayList<SafFile3> susbList  = new ArrayList<>();


    private UsbHelper usbHelper;
    private SafFile3 usbCurrentFile = null;
    private String localRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String localCurrentPath = "";



    private LinearLayout ll_nousb;
    private LCProgressDialog mprogressDialogLC;




    private boolean showOper = false;
    private Button btn_allSelect;
    private LCProgressDialog mloadDialog;
    private List localDirList = new ArrayList<>();
    private List usbDirList = new ArrayList<>();
    private Toolbar tb;
    private int delCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm_layout);
        tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(false);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        tb.setTitleTextColor(ContextCompat.getColor(this, R.color.nav_text_color)); // 设置标题颜色

// 设置导航图标和颜色
        Drawable backIcon = ContextCompat.getDrawable(this, com.sentaroh.android.upantool.R.mipmap.ic_fm_back);
        if (backIcon != null) {
            backIcon = DrawableCompat.wrap(backIcon);
            DrawableCompat.setTint(backIcon, ContextCompat.getColor(this, R.color.nav_icon_color)); // 设置图标颜色
            tb.setNavigationIcon(backIcon);
        }


        setDefaultStypeTb(true);


        titles = new String[]{getString(R.string.fm_u),getString(R.string.fm_local)};


        btn_allSelect = tb.findViewById(R.id.btn_allSelect);

        btn_allSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Config.ImagePick){
                    finish();
                    return;
                }

                view.setEnabled(false);
                int position = tabLayout.getSelectedTabPosition();
                if(position == 1) {
                    view.setEnabled(false);
                    if (localCurrentPath.equals(localRootPath)) {
                        finish();
                    } else {
                        localCurrentPath = new File(localCurrentPath).getParent();

                        localDirList.remove(localDirList.size() -1);
                        l_list.setmDirList(localDirList);
                        reloadLocalFile();
                    }

                    view.setEnabled(true);
                }else{


                    if(!UsbHelper.getInstance().canCopyToU() || usbCurrentFile == null){
                        finish();
                        return;
                    }
                    if (usbCurrentFile.getPath().equals(UsbHelper.getInstance().getUsbRootPath()) ||  !UsbHelper.getInstance().canCopyToU()) {
                        finish();
                    } else {
                        usbCurrentFile = usbCurrentFile.getParentFile();


                        usbDirList.remove(usbDirList.size() -1);
                        u_list.setmDirList(usbDirList);
                        ProgressDialog pd = new ProgressDialog(Activity_FM_.this);
                        pd.setMessage(getString(R.string.loading));
                        pd.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                buildUsbDatas();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        u_list.setMlist(udata);
                                        pd.dismiss();
                                    }
                                });

                            }
                        }).start();

                    }
                }
                view.setEnabled(true);
            }
        });


        init();
        initData();
        operEvent();
    }

    private void init() {



        //showOper(false);
        mprogressDialogLC = new LCProgressDialog(this,getString(R.string.loading),0);

        //ll_nousb = findViewById(R.id.ll_nousb);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.vg);

//        for(int i=0;i<titles.length;i++){
//            fragments.add(new Fragment_ft(i));
//            tabLayout.addTab(tabLayout.newTab());
//        }


        {
            u_list = new Fragment_filelist();
            u_list.setMedit(true);
            u_list.setDir_name(getString(R.string.fm_u));
            fragments.add(u_list);
            tabLayout.addTab(tabLayout.newTab());
        }
        {
            l_list = new Fragment_filelist();
            l_list.setMedit(true);
            fragments.add(l_list);
            tabLayout.addTab(tabLayout.newTab());
        }



        getSupportFragmentManager().beginTransaction().add(R.id.ll_container,l_list).commit();

        l_list.setMlist(ldata);
        u_list.setMlist(udata);

        tabLayout.setupWithViewPager(viewPager,false);


        pagerAdapter = new FmPagerAdapter(fragments,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        for(int i=0;i<titles.length;i++){
            tabLayout.getTabAt(i).setText(titles[i]);
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

//                int position = tabLayout.getSelectedTabPosition();
//                Fragment_filelist wxl = (Fragment_filelist) fragments.get(position);
//
//                ArrayList sdatas = new ArrayList();
//                for(int i = 0;i<  UsbHelper.getInstance().getSelectFiles().size();i++){
//                    File file = (File) UsbHelper.getInstance().getSelectFiles().get(i);
//                    int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
//                    sdatas.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
//                }
//                wxl.setSlist(sdatas);
                //wxl.setMedit(showEdit);
                //updateCount();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });


//        findViewById(R.id.btn_newfile).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                final EditText text = new EditText(Activity_FM_.this);
//                new AlertDialog.Builder(Activity_FM_.this)
//                        .setTitle(R.string.input_new_name)
//                        .setView(text)
//
//                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // 获取输入框的内容
//
//                                int position = tabLayout.getSelectedTabPosition();
//
//                                if(position == 0) {
//                                    String PATH = localCurrentPath;
//                                    // 创建src和dst文件夹
//                                    // 【注】需要有PATH目录的权限才能创建子目录
//                                    // 若PATH文件夹权限为root权限，则使用adb shell chown system:system PATH修改为system权限
//                                    File src = new File(PATH + "/" + text.getText().toString());
//                                    if (!src.exists()) {
//                                        if (!src.mkdirs()) {
//                                            // Log.e(TAG, "create directory failed.");
//                                            toast("create directory failed.");
//                                        }
//                                    }
//                                    reloadLocalFile();
//                                }else{
//
//                                    SafFile3 file = usbCurrentFile;
//
//                                    if(file == null || !file.exists()){
//                                        toast(getString(R.string.no_usb));
//                                        return;
//                                    }
//
//                                    SafFile3 dir = new SafFile3(Activity_FM_.this,file.getPath() + "/" + text.getText().toString());
//                                    file.mkdir();
//
//                                    buildUsbDatas();
//                                    u_list.setMlist(udata);
//
//                                }
//
//                            }
//                        })
//                        .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // TODO Auto-generated method stub
//                                dialog.dismiss();
//                            }
//                        }).show();
//
//
//
//            }
//        });
//
//        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = tabLayout.getSelectedTabPosition();
//                {
//                    if (TransFileManager.getInstance().getPathSelectListener() != null) {
//                        TransFileManager.getInstance().getPathSelectListener().desPathSelectFinsh(Activity_FM_.this, position == 0 ? localCurrentPath : usbCurrentFile.getPath(), null);
//                    }
//                }
//            }
//        });

        initItemListTapCallBack();
    }


    private void initData(){



        Intent in = getIntent();
        if(in != null){
            int postion = in.getIntExtra("postion",0);
            String path = in.getStringExtra("dirPath");
            if(path != null){
                if(path.length() > 0){

                    if(postion == 1) {
                       // tabLayout.setScrollPosition(postion, 0, true);

                        viewPager.setCurrentItem(1);
                        localCurrentPath = path;

                        reloadLocalFile();

                        String resPath = "";
                        List<String> ls = new ArrayList();
                        if(path.equals(localRootPath)){
                        }else{
                            resPath = path.substring(localRootPath.length() + 1);
                            String[] resPaths = resPath.split("/");

                            ls.addAll(Arrays.asList(resPaths));
                        }


                        String temPath = localRootPath;
                        File f1 = new File(temPath);
                        localDirList.add(new Adapter_dir_header.RvItem("root",f1));
                        for(String s : ls){



                            temPath = temPath + "/" + s;
                            File f = new File(temPath + "/" + s);
                            localDirList.add(new Adapter_dir_header.RvItem(f.getName(),f));

                        }
                        l_list.setmDirList(localDirList);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initUsbFile();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        u_list.setMlist(udata);
                                    }
                                });
                            }

                        }).start();
                    }
                    return;
                }
            }
        }
        mprogressDialogLC.setProgress(3);
        mprogressDialogLC.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                initLocalFile();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        l_list.setMlist(ldata);
                        l_list.setmDirList(localDirList);
                        //mprogressDialogLC.dismiss();
                    }
                });
                initUsbFile();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        u_list.setMlist(udata);
                        u_list.setmDirList(usbDirList);

                        mprogressDialogLC.dismiss();
                    }
                });
            }

        }).start();



    }



    private void initItemListTapCallBack(){
        l_list.setOnItemClickListener(new Fragment_filelist.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                File f = localList.get(position);

                if(f.isDirectory()){




                    localDirList.add(new Adapter_dir_header.RvItem(f.getName(),localCurrentPath));
                    l_list.setmDirList(localDirList);
                    //l_list.setSlist(sldata);


                    ProgressDialog pd = new ProgressDialog(Activity_FM_.this);
                    pd.setMessage(getString(R.string.loading));
                    pd.show();

                    usbDirList.add(new Adapter_dir_header.RvItem(f.getName(),f));
                    u_list.setmDirList(usbDirList);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            reloadDataWhileLocalListItemClik(f);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    l_list.setMlist(ldata);
                                    sldata.clear();
                                    slocalList.clear();
                                    l_list.setSlist(sldata);
                                    if(!Config.ImagePick) {
                                        btn_allSelect.setText(R.string.selectAll);
                                        showAllSelectStateImage(true);
                                    }
                                    pd.dismiss();
                                }
                            });

                        }
                    }).start();
                }else{
                    //打开文件



                    if(FileTool.isSelfEncryptFile(f.getName())){


                        unZip(f.getAbsolutePath());

                        return;

                    };

                    if(!f.exists()){
                        toast(getString(R.string.invaild_file));
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
                BeanFile bf = (BeanFile) ldata.get(position);
                if(slocalList.contains(f)){
                    slocalList.remove(f);

                }else {
                    slocalList.add(f);

                }
                if(sldata.contains(bf)){
                    sldata.remove(bf);
                }else{
                    sldata.add(bf);
                }
                l_list.setSlist(sldata);


                if(Config.ImagePick) {

                    if(sldata.size() > 0) {
                        btn_allSelect.setVisibility(View.VISIBLE);
                        btn_allSelect.setText("确定" + "(" + sldata.size() + ")");
                    }else{
                        btn_allSelect.setText("");
                    }

                    return;
                }
                btn_allSelect.setText(R.string.selectAll);
                showAllSelectStateImage(true);
                if(sldata.size() == ldata.size()){
                    btn_allSelect.setText(R.string.not_selectAll);
                    showAllSelectStateImage(false);
                }
                tb.setTitle(sldata.size() + "/" + ldata.size());
                if(FileTool.isSelfEncryptFile(f.getName()) && sldata.size() == 1){
                    setDefaultEncrptTxt(false);
                }else{
                    setDefaultEncrptTxt(true);
                }

            }

            @Override
            public void onItemLongClick(boolean edit) {

                showOper = !showOper;
                showOper(showOper);
                setDefaultStypeTb(!showOper);


            }

            @Override
            public void onDirItemClick(int position, Object obj) {


                Adapter_dir_header.RvItem item = (Adapter_dir_header.RvItem) localDirList.get(position);
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

                l_list.setmDirList(localDirList);

            }

        });

        u_list.setOnItemClickListener(new Fragment_filelist.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                sudata.clear();
                susbList.clear();
                u_list.setSlist(sudata);

                if(!Config.ImagePick) {
                    btn_allSelect.setText(R.string.selectAll);
                    showAllSelectStateImage(true);
                }
                SafFile3 f = usbList.get(position);
                if(f.isDirectory()){
                    //reloadDataWhileUPListItemClik(f);
                    usbCurrentFile = f;


                    ProgressDialog pd = new ProgressDialog(Activity_FM_.this);
                    pd.setMessage(getString(R.string.loading));
                    pd.show();
                    udata.clear();
                    u_list.setMlist(udata);


                    usbDirList.add(new Adapter_dir_header.RvItem(f.getName(),f));
                    u_list.setmDirList(usbDirList);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            buildUsbDatas();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    u_list.setMlist(udata);
                                    u_list.setSlist(new ArrayList<>());
                                    pd.dismiss();
                                }
                            });

                        }
                    }).start();

                }else{ new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //String path = FileTool.copyToLocalCacheToPaly(f,Activity_FM_.this);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(f.isSafFile()){
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction(Intent.ACTION_VIEW);

                                    Uri uri;
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        //记得修改com.xxx.fileprovider与androidmanifest相同
                                        uri = f.getUri();//FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));//.processName
                                    }else{
                                        uri = f.getUri();//Uri.fromFile(new File(param));
                                    }
                                    //intent.setDataAndType(uri,"*/*");

                                    FileTool.openFile_u(f.getName(),intent,uri);
                                    startActivity(intent);
                                    return;
                                }



                                Intent in = FileTool.openFile(f.getPath(),getBaseContext());
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

                }
            }

            @Override
            public void onItemCheckClick(int position) {


                BeanFile bf = (BeanFile) udata.get(position);
                SafFile3 f = usbList.get(position);
                if(susbList.contains(f)){
                    susbList.remove(f);

                }else {
                    susbList.add(f);
                }

                if(sudata.contains(bf)){
                    sudata.remove(bf);
                }else{
                    sudata.add(bf);
                }
                u_list.setSlist(sudata);


                if(Config.ImagePick) {

                    if(sldata.size() > 0) {
                        btn_allSelect.setVisibility(View.VISIBLE);
                        btn_allSelect.setText("确定" + "(" + sldata.size() + ")");
                    }else{
                        btn_allSelect.setText("");
                    }

                    return;
                }
                btn_allSelect.setText(R.string.selectAll);
                showAllSelectStateImage(true);
                if(sudata.size() == udata.size()){
                    btn_allSelect.setText(R.string.not_selectAll);
                    showAllSelectStateImage(false);
                }
                tb.setTitle(sudata.size() + "/" + udata.size());
            }

            @Override
            public void onItemLongClick(boolean edit) {
                showOper = !showOper;
                showOper(showOper);
                setDefaultStypeTb(!showOper);
            }

            @Override
            public void onDirItemClick(int position, Object obj) {



                Adapter_dir_header.RvItem item = (Adapter_dir_header.RvItem) usbDirList.get(position);
                usbCurrentFile = (SafFile3) item.obj;
                ArrayList list = new ArrayList();

                for(int i = 0;i<usbDirList.size();i++) {
                    if(i > position){
                        break;
                    }
                    list.add(usbDirList.get(i));
                }

                usbDirList = list;

                u_list.setmDirList(usbDirList);
                ProgressDialog pd = new ProgressDialog(Activity_FM_.this);
                pd.setMessage(getString(R.string.loading));
                pd.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        buildUsbDatas();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                u_list.setMlist(udata);
                                u_list.setSlist(new ArrayList<>());
                                pd.dismiss();
                            }
                        });

                    }
                }).start();
            }
        });
    }




    /**
     * 初始化本地文件列表
     */
    private String localStartDir = "root";
    private void initLocalFile() {

        localList = new ArrayList<>();

        localDirList = new ArrayList<>();
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

        localCurrentPath = localRootPath;
        if(files.length > 0) {
            buildLocalDatas();

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
                    l_list.setMlist(ldata);
                    l_list.setSlist(sldata);
                }
            });
        }else{
            //打开文件
        }
    }

    private void reloadDataWhileLocalListItemClik(File f) {
        localCurrentPath = f.getPath();
        buildLocalDatas();
    }
    private void buildLocalDatas(){

        localList.clear();
        ldata.clear();

        slocalList.clear();
        sldata.clear();

        //localList = (ArrayList<File>) FileTool.listfilesortbymodifytime(f.getPath());
        File f= new File(localCurrentPath);
        localList = (ArrayList<File>) FileTool.listfilesortbymodifytime(f.getPath());
        //localList.addAll(Arrays.asList(f.listFiles()));
        List<File> tmp = new ArrayList();
        tmp.addAll(localList);
        for (int i = 0; i < tmp.size(); i++) {
            File file = tmp.get(i);
            if(!file.getName().startsWith(".") && file.canRead()) {
                ldata.add(new BeanFile(file.getName(), file.getPath(), file.isDirectory() ? "" : FileTool.getFileSize(file.length()), "", FileTool.getResIdFromFileName(file.isDirectory(), file.getName()), file));
            }else{
                localList.remove(file);
            }
        }

    }


    private void initUsbFile() {

        LogHelper.getInstance().d("initUsbFile");

        usbHelper = UsbHelper.getInstance();
        if(!usbHelper.safMgr.isStoragePermissionRequired() && usbHelper.canCopyToU()) {


            if(usbHelper.getRootFile() != null) {
                //存在USB
                if (!usbHelper.getRootFile().exists()) {
                    return;
                }



                usbCurrentFile = usbHelper.getRootFile();

                usbDirList.add(new Adapter_dir_header.RvItem("/root",usbCurrentFile));
                buildUsbDatas();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ll_nousb.setVisibility(View.GONE);

                        u_list.setMlist(udata);
                    }
                });


            }
        }else{

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

        }
    }



    private void buildUsbDatas() {
        usbList.clear();
        udata.clear();
        susbList.clear();
        sudata.clear();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mprogressDialog.setMessage(getString(R.string.read_directory));
//                mprogressDialog.setProgress(20);
//            }
//        });

        if(!UsbHelper.getInstance().canCopyToU()){
            return;
        }


        if(usbCurrentFile == null && !usbCurrentFile.exists()){
            return;
        }


        //usbList.addAll(Arrays.asList(usbCurrentFile.listFiles()));


        usbList.addAll(Arrays.asList(usbCurrentFile.listFiles(new RecoverySystem.ProgressListener() {
            @Override
            public void onProgress(int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mprogressDialogLC.setProgress(i);
                    }
                });
            }
        })));


//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mprogressDialog.setMessage(getString(R.string.read_fileData));
//                mprogressDialog.setProgress(20);
//            }
//        });

        if(usbList.size() < 50) {

            usbList = (ArrayList<SafFile3>) FileTool.listUsbfilesortbymodifytime_list(usbList);
        }

        List tmpList = new ArrayList();
        tmpList.addAll(usbList);

        long time = System.currentTimeMillis()/1000;
        for(int i = 0;i< tmpList.size();i++){
            SafFile3 file = (SafFile3) tmpList.get(i);


            if(file.getName() == null || file.getName().startsWith(".")){

                usbList.remove(file);
                continue;
            }
            BeanFile bf = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                bf = new BeanFile(file.getName(),file.getPath(), Formatter.formatFileSize(this, file.length()),"", FileTool.getResIdFromFileName(false,file.getName()),file);
            }
            udata.add(bf);

        }

        Log.d(TAG, "updateUsbFile: "+(System.currentTimeMillis()/1000 - time) + "size" +  usbList.size());


    }


    void operEvent(){
        findViewById(R.id.btn_allSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Config.ImagePick){



                    if(Config.ImagePick){

                        if(ItemPickManager.itemPickFinsh != null){

                            ArrayList al = new ArrayList();
                            ArrayList fs = new ArrayList();
                            for(File o : slocalList){
                                String s =  o.getAbsolutePath();
                                fs.add(s);
                                Uri uri = FileTool.getUirFromPath(Activity_FM_.this,s);
                                al.add(uri.toString());
                            }

                            ItemPickManager.itemPickFinsh.imagePickFinsh(1,al,fs);
                            finish();
                        }
                        return;
                    }
                    return;
                }

                view.setEnabled(false);
                int position = tabLayout.getSelectedTabPosition();
                if(position == 1){
                    if(ldata.size() == sldata.size()){

                        sldata.clear();
                        l_list.setSlist(sldata);;
                        slocalList.clear();

                    }else {
                        sldata.clear();;
                        sldata.addAll(ldata);
                        l_list.setSlist(sldata);;
                        slocalList.clear();
                        slocalList.addAll(localList);
                    }

                    showBtnAllSelectStatu(ldata.size() != sldata.size());
                }else{
                    if(udata.size() == sudata.size()){

                        sudata.clear();
                        u_list.setSlist(sudata);;
                        susbList.clear();

                    }else {
                        sudata.clear();;
                        sudata.addAll(udata);
                        u_list.setSlist(sudata);;
                        susbList.clear();
                        susbList.addAll(usbList);
                    }
                    showBtnAllSelectStatu(udata.size() != sudata.size());

                }
                view.setEnabled(true);
                setDefaultStypeTb(false);

            }
        });

        findViewById(R.id.btn_canceledit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOper(false);
                setDefaultStypeTb(true);


            }
        });


        LinearLayout ll = findViewById(R.id.ll_oper_bottom);
        ll.findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();
                if(position == 0){
                    if(usbList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                    // 去选des



                    //回调路径

                }else {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }


                Intent in = new Intent(Activity_FM_.this, Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();
                                if(position == 0) {

                                    for (Object o : susbList) {
                                        SafFile3 sf = (SafFile3) o;
                                        fps.add(sf.getPath());

                                    }
                                }else{
                                    for (Object o : slocalList) {
                                        File sf = (File) o;
                                        fps.add(sf.getPath());

                                    }
                                }
                                buidTransDatas(fps,sDirpath,false);

                            }
                        }).start();

                    }
                });


            }
        });

        ll.findViewById(R.id.btn_fm_oper_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();
                if(position == 0){
                    if(usbList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }else {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }

                Intent in = new Intent(Activity_FM_.this, Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();
                                if(position == 0) {

                                    for (Object o : susbList) {
                                        SafFile3 sf = (SafFile3) o;
                                        fps.add(sf.getPath());

                                    }
                                }else{
                                    for (Object o : slocalList) {
                                        File sf = (File) o;
                                        fps.add(sf.getPath());

                                    }
                                }
                                buidTransDatas(fps,sDirpath,true);

                            }
                        }).start();

                    }
                });

            }
        });

        ll.findViewById(R.id.btn_addpas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();
                if(position == 0){
                    if(usbList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }

                    ///先复制到cache，再压缩,在move；
                }else {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                    ///先压缩到cache,在move；



                }

                ArrayList fps = new ArrayList();
                if(position == 0) {

                    for (Object o : susbList) {
                        SafFile3 sf = (SafFile3) o;
                        fps.add(sf.getPath());

                    }
                    zipData__(fps);
                }else{
                    for (Object o : slocalList) {
                        File sf = (File) o;
                        fps.add(sf.getPath());

                    }

                    File sf = (File) slocalList.get(0);
                    if(FileTool.isSelfEncryptFile(sf.getName()) && sldata.size() == 1){
                        unZip(sf.getPath());
                        return;
                    }
                    zipData_(fps);
                }





            }
        });





        ll.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();
                if(position == 0){
                    if(usbList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }else {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }

                ArrayList fps = new ArrayList();
                if(position == 0) {

                    for (Object o : susbList) {
                        SafFile3 sf = (SafFile3) o;
                        fps.add(sf.getPath());

                    }
                }else{
                    for (Object o : slocalList) {
                        File sf = (File) o;
                        fps.add(sf.getPath());

                    }
                }
                ViewTool.confirm_to_action(Activity_FM_.this, null, getString(R.string.confirm_del_file), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delFile(fps);
                    }
                });



            }
        });
        ll.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();
                if(position == 0){
                    if(usbList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }else {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }

                ArrayList fps = new ArrayList();
                if(position == 0) {

                    for (Object o : susbList) {
                        SafFile3 sf = (SafFile3) o;
                        fps.add(sf.getPath());

                    }
                }else{
                    for (Object o : slocalList) {
                        File sf = (File) o;
                        fps.add(sf.getPath());

                    }
                }


                ShareTools.shareWechatFriend(Activity_FM_.this, new File((String) fps.get(0)));


            }
        });




    }

    void delFile(List<String> filePaths){

        LCProgressDialog pd = new LCProgressDialog(this,"",1);
        pd.setMessage(getString(R.string.deleting));
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                for(String p : filePaths){
                    SafFile3 sf = new SafFile3(Activity_FM_.this,p);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.setProgress(5);
                            pd.setMessage(getString(R.string.read_files_to_del));

                        }
                    });



                    if(!sf.isDirectory()){//!sf.isSafFile()
                        sf.deleteIfExists();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setProgress(100);
                                pd.setMessage(getString(R.string.deleting));

                            }
                        });
                    }else{
                        int allCount = FileTool.getAllFileInDir_(sf).size();

                        delCount = 0;

                        while (sf.exists()) {
                            if (!sf.exists()) {
                                //toast(get);
                                return;
                            }
                            try {
                                FileTool.deleteUsbFile_(sf, new FileTool.Callback() {
                                    @Override
                                    public void callback(int i) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                delCount ++;
                                                pd.setProgress(delCount * 100 / allCount);
                                                pd.setMessage(getString(R.string.deleting));


                                            }
                                        });
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        int position = tabLayout.getSelectedTabPosition();

                        if(position == 0){
                            buildUsbDatas();
                            u_list.setMlist(udata);
                        }else {
                            reloadLocalFile();
                        }
                    }
                });

            }
        }).start();



    }






    void unZip(String filePath){


        final EditText text = new EditText(this);
        text.setText("");
        new AlertDialog.Builder(this)
                .setTitle(R.string.input_pass)
                .setView(text)

                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = text.getText().toString();
                        int position = tabLayout.getSelectedTabPosition();


                        ProgressDialog pd = new ProgressDialog(Activity_FM_.this);
                        pd.setMessage(getString(R.string.decrypting));
                        pd.show();

                        File filezip  = new File(filePath);

                        File unzipDir = new File(filezip.getParent()  + "/" + (filezip.getName().replace(".zip","")).replace(filezip.getName().contains("加密") ? "加密" : "Encrypt",getString(R.string.decrypt)));
                        if(!unzipDir.exists()){
                            unzipDir.mkdir();
                        }

                        String zipPath = filePath;
                        ZipFile zipFile = new ZipFile(filePath,pass.toCharArray());

                        new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        //zipFile.extractAll(filezip.getParent());
                                        zipFile.extractAll(unzipDir.getAbsolutePath());
                                    } catch (ZipException e) {
                                        e.printStackTrace();
                                    }

                                    if(position == 0){
                                        buildUsbDatas();

                                    }else{
                                        reloadLocalFile();
                                    }


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            u_list.setMlist(udata);
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


    void zipData_(List<String> filePaths){

        final EditText text = new EditText(this);
        text.setText("");
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.input_pass)
                .setView(text)

                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = text.getText().toString();


                        File ff = new File(filePaths.get(0));
                        String sDirpath = ff.getParent();

                        String name = "";

                        LCProgressDialog pd = new LCProgressDialog(Activity_FM_.this,getString(R.string.addpasing),0);
                        pd.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList copyCachePaths = new ArrayList();
                                for (String p : filePaths) {

                                    copyCachePaths.add(new File(p));
                                }
                                ZipParameters zipParameters = new ZipParameters();
                                zipParameters.setEncryptFiles(true);
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

                                String zipPath = ff.getParent() + "/" + name;
                                ZipFile zipFile = new ZipFile(zipPath, pass.toCharArray());
                                try {
                                    for (Object p : copyCachePaths) {
                                        //String path = (String) p;
                                        File f = (File) p;
                                        if (f.isDirectory()) {
                                            zipFile.addFolder(f, zipParameters);
                                        } else {
                                            zipFile.addFile(f, zipParameters);
                                        }


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                pd.setProgress(copyCachePaths.indexOf(p) * 100/copyCachePaths.size());
                                            }
                                        });

                                    }


                                } catch (ZipException e) {
                                    e.printStackTrace();
                                }

                                ArrayList tans = new ArrayList();
                                tans.add(zipPath);

//                                        buidTransDatas(tans,sDirpath,true);
//
                                String finalName = name;
                                int postion = tabLayout.getSelectedTabPosition();

                                if(postion == 0){
                                    buildUsbDatas();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        pd.dismiss();


                                        if(postion == 1) {
                                            reloadLocalFile();
                                        }else{

                                            u_list.setMlist(udata);
                                            u_list.setSlist(sudata);

                                        }
//                                        Intent in = new Intent(getActivity(),Activity_FM_.class);
//                                        in.putExtra("postion", 1);
//                                        in.putExtra("dirPath", ff.getParent());
//                                        startActivity(in);


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


    void zipData__(List<String> filePaths){

        final EditText text = new EditText(this);
        text.setText("");
        new AlertDialog.Builder(this)
                .setTitle(R.string.input_pass)
                .setView(text)

                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = text.getText().toString();
                                ProgressDialog pd = new ProgressDialog(Activity_FM_.this);
                                pd.setMessage(getString(R.string.addpasing));
                                pd.setProgress(0);
                                pd.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int position = tabLayout.getSelectedTabPosition();
                                        ArrayList fps = new ArrayList();
                                        if(position == 0) {

                                            for (Object o : susbList) {
                                                SafFile3 sf = (SafFile3) o;
                                                fps.add(sf.getPath());

                                            }
                                        }else{
                                            for (Object o : slocalList) {
                                                File sf = (File) o;
                                                fps.add(sf.getPath());

                                            }
                                        }
                                        ArrayList copyCachePaths = new ArrayList();
                                        for(String p : filePaths){
                                            SafFile3 sf = new SafFile3(Activity_FM_.this,p);

                                            String des = UsbHelper.getInstance().getSdRootPath() + "/" + FileUtil.YISU_CACHE + "/" + sf.getName();
                                            FileTool.copyWithPath(p, des);
                                            copyCachePaths.add(new File(des));
                                        }
                                        ZipParameters zipParameters = new ZipParameters();
                                        zipParameters.setEncryptFiles(true);
                                        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
// Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
                                        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                                        zipParameters.setPassword(pass);



//                                        List<File> filesToAdd = Arrays.asList(
//                                                new File("somefile"),
//                                                new File("someotherfile")
//                                        );

                                        String dirPath = Environment.getExternalStorageDirectory() + "/" + "yisucache";

                                        File dirFile = new File(dirPath);
                                        if(!dirFile.exists()){
                                            dirFile.mkdir();
                                        }


                                        String name = "加密" + TimeUtil.getCurrentTime("yyyy_MM_ddhh_mm_ss") + ".zip";

                                        String zipPath = dirPath + "/" + name;
                                        ZipFile zipFile = new ZipFile(dirPath + "/" + name,pass.toCharArray());
                                        try {
                                            for(Object p : copyCachePaths){
                                                //String path = (String) p;
                                                File f = (File) p;
                                                if(f.isDirectory()){
                                                    zipFile.addFolder(f, zipParameters);
                                                }else {
                                                    zipFile.addFile(f, zipParameters);
                                                }
                                            }



                                        } catch (ZipException e) {
                                            e.printStackTrace();
                                        }

                                        ArrayList tans = new ArrayList();
                                        tans.add(zipPath);

                                        SafFile3 sf = new SafFile3(Activity_FM_.this,filePaths.get(0));
                                        buidTransDatas__(tans,sf.getParent(),true);

                                        buildUsbDatas();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                pd.setProgress(100);
                                                pd.dismiss();
                                                showOper(false);
                                                setDefaultStypeTb(true);

                                                u_list.setMlist(udata);
                                                u_list.setSlist(sudata);
                                            }
                                        });


                                    }
                                }).start();

                            }
                }).setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {

                  @Override
                 public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                    }
                }).show();


    }


    void zipData(List<String> filePaths){

        final EditText text = new EditText(this);
        text.setText("");
        new AlertDialog.Builder(this)
                .setTitle(R.string.input_pass)
                .setView(text)

                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = text.getText().toString();


                        Intent in = new Intent(Activity_FM_.this, Activity_filePathSelector.class);
                        startActivity(in);
                        TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                            @Override
                            public void desPathSelectFinsh(Context c, String sDirpath, Object other) {
                                ((AppCompatActivity)c).finish();
                                ProgressDialog pd = new ProgressDialog(c);
                                pd.setMessage("正在加密");
                                pd.setProgress(0);
                                pd.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int position = tabLayout.getSelectedTabPosition();
                                        ArrayList fps = new ArrayList();
                                        if(position == 0) {

                                            for (Object o : susbList) {
                                                SafFile3 sf = (SafFile3) o;
                                                fps.add(sf.getPath());

                                            }
                                        }else{
                                            for (Object o : slocalList) {
                                                File sf = (File) o;
                                                fps.add(sf.getPath());

                                            }
                                        }
                                        ArrayList copyCachePaths = new ArrayList();
                                        for(String p : filePaths){
                                            SafFile3 sf = new SafFile3(Activity_FM_.this,p);

                                            String des = UsbHelper.getInstance().getSdRootPath() + "/" + FileUtil.YISU_CACHE + "/" + sf.getName();
                                            FileTool.copyWithPath(p, des);
                                            copyCachePaths.add(new File(des));
                                        }
                                        ZipParameters zipParameters = new ZipParameters();
                                        zipParameters.setEncryptFiles(true);
                                        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
// Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
                                        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                                        zipParameters.setPassword(pass);



//                                        List<File> filesToAdd = Arrays.asList(
//                                                new File("somefile"),
//                                                new File("someotherfile")
//                                        );

                                        String dirPath = Environment.getExternalStorageDirectory() + "/" + "yisucache";

                                        File dirFile = new File(dirPath);
                                        if(!dirFile.exists()){
                                            dirFile.mkdir();
                                        }


                                        String name = "加密" + TimeUtil.getCurrentTime("yyyy_MM_ddhh_mm_ss") + ".zip";

                                        String zipPath = dirPath + "/" + name;
                                        ZipFile zipFile = new ZipFile(dirPath + "/" + name,pass.toCharArray());
                                        try {
                                            for(Object p : copyCachePaths){
                                                //String path = (String) p;
                                                File f = (File) p;
                                                if(f.isDirectory()){
                                                    zipFile.addFolder(f, zipParameters);
                                                }else {
                                                    zipFile.addFile(f, zipParameters);
                                                }
                                            }



                                        } catch (ZipException e) {
                                            e.printStackTrace();
                                        }

                                        ArrayList tans = new ArrayList();
                                        tans.add(zipPath);

                                        buidTransDatas(tans,sDirpath,true);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                pd.setProgress(100);
                                                pd.dismiss();
                                                ((AppCompatActivity)(c)).finish();
                                                showOper(false);
                                                setDefaultStypeTb(true);
                                            }
                                        });


                                    }
                                }).start();

                            }
                        });

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


    public void buidTransDatas__(List filePaths,String toDirPath,Boolean delete){


        Object to =  new SafFile3(this,toDirPath);//UsbHelper.getInstance().getRootFile();
        ArrayList tfs = new ArrayList();
        for(Object o : filePaths){

            //SafFile3 sf = (SafFile3) o;
            String path = (String) o;//sf.getPath();
            //FileTool.copyToUPanoRoot(new File(path),getContext());

            SafFile3 file = new SafFile3(this,path);
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

        }
        TransFileManager.getInstance().addTransFileSys((Fragment_ft.TransFile) tfs.get(0));



    }
    public void buidTransDatas(List filePaths,String toDirPath,Boolean delete){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mloadDialog = new LCProgressDialog(Activity_FM_.this,getString(R.string.add_copy_lists),0);

                mloadDialog.show();
            }
        });


        Object to =  new SafFile3(this,toDirPath);//UsbHelper.getInstance().getRootFile();
        ArrayList tfs = new ArrayList();
        for(Object o : filePaths){

            //SafFile3 sf = (SafFile3) o;
            String path = (String) o;//sf.getPath();
            //FileTool.copyToUPanoRoot(new File(path),getContext());

            SafFile3 file = new SafFile3(this,path);
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mloadDialog.setProgress(progress);
                }
            });

        }
        TransFileManager.getInstance().addTransFiles(tfs);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(Activity_FM_.this,Activity_Task.class);
                startActivity(in);

                mloadDialog.dismiss();
            }
        });

    }


    public void showOper(boolean show) {


        if(Config.ImagePick){
            return;
        }
        showOper = show;
        int showV = show ? View.VISIBLE : View.GONE;

        findViewById(R.id.ll_oper_bottom).setVisibility(showV);
        findViewById(R.id.btn_canceledit).setVisibility(showV);
        findViewById(R.id.btn_allSelect).setVisibility(showV);

        if(show){

        }else {
            slocalList.clear();;
            sldata.clear();
            susbList.clear();
            sudata.clear();
            if(l_list != null) {
                l_list.setSlist(sldata);
                l_list.setMedit(false);
            }

            if(u_list != null){
                u_list.setSlist(sudata);
                u_list.setMedit(false);
            }
        }
    }

    void showBtnAllSelectStatu(boolean show){

        btn_allSelect.setText(getString(show ? R.string.selectAll : R.string.not_selectAll));
        showAllSelectStateImage(show);
    }

    public void showAllSelectStateImage(boolean show)
    {

        Drawable rightDrawable = getResources().getDrawable(show ? com.zhihu.matisse.R.drawable.ic_wx_selectall_un : com.zhihu.matisse.R.drawable.ic_wx_selectall);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        //tv_s.setCompoundDrawables(rightDrawable, null, null, null);
        btn_allSelect.setCompoundDrawables(rightDrawable, null, null, null);
    }



    void setDefaultStypeTb(boolean defauat) {

        if(defauat) {
            tb.setTitle(R.string.file_manager);
            tb.setNavigationIcon(R.mipmap.ic_fm_back);
        }else{
            int pos = tabLayout.getSelectedTabPosition();
            if(pos == 1){
                tb.setTitle(sldata.size() + "/" + ldata.size());
            }else {
                tb.setTitle(sudata.size() + "/" + udata.size());
            }
            //tb.setNavigationIcon(null);
        }



    }


    void setDefaultEncrptTxt(boolean defauat) {


        TextView tv = findViewById(R.id.btn_addpas);

        if(defauat) {
            tv.setText(R.string.fm_add_pass);
        }else{
            tv.setText(R.string.decrypt);

        }



    }









    public class FmPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public FmPagerAdapter(List<Fragment> fragmentList, FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragmentList = fragmentList;
        }

        @Override
        public int getCount() {
            return fragmentList != null && !fragmentList.isEmpty() ? fragmentList.size() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }



}