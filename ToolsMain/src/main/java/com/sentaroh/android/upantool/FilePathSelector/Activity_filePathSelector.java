package com.sentaroh.android.upantool.FilePathSelector;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.sentaroh.android.upantool.Activity_FM_;
import com.sentaroh.android.upantool.Adapter_dir_header;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.BaseActivity;
import com.sentaroh.android.upantool.BeanFile;
import com.sentaroh.android.upantool.FileTool;
import com.sentaroh.android.upantool.Fragment_filelist;
import com.sentaroh.android.upantool.LogHelper;
import com.sentaroh.android.upantool.LCProgressDialog;
import com.sentaroh.android.upantool.TransFileManager;
import com.sentaroh.android.upantool.UsbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class Activity_filePathSelector extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = null;
    private FmPagerAdapter pagerAdapter;
    private List<HashMap> datas;

    private Fragment_filelist l_list;
    private Fragment_filelist u_list;
    private List ldata = new ArrayList<>();
    private List udata = new ArrayList<>();


    private ArrayList<File> localList = new ArrayList<>();
    private String localRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String localCurrentPath = "";

    private ArrayList<SafFile3> usbList  = new ArrayList<>();
    private UsbHelper usbHelper;
    private SafFile3 usbCurrentFile = null;


    private LinearLayout ll_nousb;
    private LCProgressDialog mprogressDialogLC;

    private List localDirList = new ArrayList<>();
    private List usbDirList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_file_path_selector);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle("");
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();

                view.setEnabled(false);
                if(position == 1) {
                    if (localCurrentPath.equals(localRootPath)) {
                        finish();
                    } else {
                        localCurrentPath = new File(localCurrentPath).getParent();
                        localDirList.remove(localDirList.size() -1);
                        l_list.setmDirList(localDirList);
                        reloadLocalFile();
                    }
                }else{

                    if(!UsbHelper.getInstance().canCopyToU()){

                        finish();

                        return;
                    }
                    if (usbCurrentFile.getPath().equals(UsbHelper.getInstance().getUsbRootPath())) {
                        finish();
                    } else {
                        usbCurrentFile = usbCurrentFile.getParentFile();


                        usbDirList.remove(usbDirList.size() -1);
                        u_list.setmDirList(usbDirList);
                        ProgressDialog pd = new ProgressDialog(Activity_filePathSelector.this);
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

        titles = new String[]{getString(R.string.fm_u),getString(R.string.fm_local)};

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        init();

        initData();


    }

    private void init() {

        mprogressDialogLC = new LCProgressDialog(this,getString(R.string.loading),0);

        //ll_nousb = findViewById(R.id.ll_nousb);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.vg);

//        for(int i=0;i<titles.length;i++){
//            fragments.add(new Fragment_ft(i));
//            tabLayout.addTab(tabLayout.newTab());
//        }

        u_list = new Fragment_filelist();
        u_list.setDir_name(getString(R.string.fm_u));
        fragments.add(u_list);
        tabLayout.addTab(tabLayout.newTab());

        l_list = new Fragment_filelist();
//        l_list.setMedit(false);
//        l_list.longTapToDrag = true;
        fragments.add(l_list);
        tabLayout.addTab(tabLayout.newTab());

//        u_list.setMedit(true);
//        u_list.longTapToDrag = true;

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


        findViewById(R.id.btn_newfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();
                if(position == 0){
                    if(!UsbHelper.getInstance().canCopyToU()){
                        return;
                    }
                }



                final EditText text = new EditText(Activity_filePathSelector.this);
                new AlertDialog.Builder(Activity_filePathSelector.this)
                        .setTitle(R.string.input_new_name)
                        .setView(text)

                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 获取输入框的内容



                                closeBoard(Activity_filePathSelector.this);
                                int position = tabLayout.getSelectedTabPosition();

                                if(position == 1) {
                                    String PATH = localCurrentPath;
                                    // 创建src和dst文件夹
                                    // 【注】需要有PATH目录的权限才能创建子目录
                                    // 若PATH文件夹权限为root权限，则使用adb shell chown system:system PATH修改为system权限
                                    File src = new File(PATH + "/" + text.getText().toString());
                                    if (!src.exists()) {
                                        if (!src.mkdirs()) {
                                            // Log.e(TAG, "create directory failed.");
                                            toast("create directory failed.");
                                        }
                                    }
                                    reloadLocalFile();
                                }else{

                                    SafFile3 file = usbCurrentFile;

                                    if(file == null || !file.exists()){
                                        toast(getString(R.string.no_usb));
                                        return;
                                    }

                                    SafFile3 dir = new SafFile3(Activity_filePathSelector.this,file.getPath() + "/" + text.getText().toString());
                                    dir.mkdir();

                                    ProgressDialog pd = new ProgressDialog(Activity_filePathSelector.this);
                                    pd.setMessage(getString(R.string.fm_newfile));
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

                            }
                        })
                        .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                closeBoard(Activity_filePathSelector.this);
                                dialog.dismiss();
                            }
                        }).show();



            }
        });

        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();

                if(position == 0){
                    if(!UsbHelper.getInstance().canCopyToU()){
                        return;
                    }
                }
                {
                    if (TransFileManager.getInstance().getPathSelectListener() != null){

                            TransFileManager.getInstance().getPathSelectListener().desPathSelectFinsh(Activity_filePathSelector.this, position == 1 ? localCurrentPath : usbCurrentFile.getPath(), null);


                    }
                }
            }
        });

        initItemListTapCallBack();
    }


    private void initData(){


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
                        mprogressDialogLC.dismiss();
                        l_list.setmDirList(localDirList);
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

                    reloadDataWhileLocalListItemClik(f);
                    l_list.setMlist(ldata);

                    localDirList.add(new Adapter_dir_header.RvItem(f.getName(),localCurrentPath));
                    l_list.setmDirList(localDirList);
                }else{
                    //打开文件

                }
            }

            @Override
            public void onItemCheckClick(int position) {
                File f = localList.get(position);

            }

            @Override
            public void onItemLongClick(boolean edit) {

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

                SafFile3 f = usbList.get(position);
                if(f.isDirectory()){
                    //reloadDataWhileUPListItemClik(f);
                    usbCurrentFile = f;

                    udata.clear();
                    u_list.setMlist(udata);

                    ProgressDialog pd = new ProgressDialog(Activity_filePathSelector.this);
                    pd.setMessage(getString(R.string.loading));
                    pd.show();

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



                }else{

                }
            }

            @Override
            public void onItemCheckClick(int position) {

            }

            @Override
            public void onItemLongClick(boolean edit) {

            }

            @Override
            public void onDirItemClick(int position, Object obj) {
                Adapter_dir_header.RvItem item = (Adapter_dir_header.RvItem) usbDirList.get(position);
                usbCurrentFile = (SafFile3) item.obj;
                //buildUsbDatas();
                ArrayList list = new ArrayList();

                for(int i = 0;i<usbDirList.size();i++) {
                    if(i > position){
                        break;
                    }
                    list.add(usbDirList.get(i));
                }

                usbDirList = list;

                u_list.setmDirList(usbDirList);

                ProgressDialog pd = new ProgressDialog(Activity_filePathSelector.this);
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

        //localList = (ArrayList<File>) FileTool.listfilesortbymodifytime(f.getPath());
        File f= new File(localCurrentPath);
        localList.addAll(Arrays.asList(f.listFiles()));
        List<File> tmp = new ArrayList();
        tmp.addAll(localList);
        for (int i = 0; i < tmp.size(); i++) {
            File file = tmp.get(i);
            if(file.isDirectory() && !file.getName().startsWith(".") && file.canRead()) {
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

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mprogressDialog.setMessage(getString(R.string.read_directory));
//                mprogressDialog.setProgress(20);
//            }
//        });

        usbList.addAll(Arrays.asList(usbCurrentFile.listFiles()));

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
        for(int i = 0;i< tmpList.size();i++){
            SafFile3 file = (SafFile3) tmpList.get(i);


            if(file.getName() == null || file.getName().startsWith(".") || !file.isDirectory()){

                usbList.remove(file);
                continue;
            }

            BeanFile bf = new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : "","",FileTool.getResIdFromFileName(file.isDirectory(),file.getName()),file);
            udata.add(bf);

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