package com.sentaroh.android.upantool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.Utilities3.SafFile3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentManager;

public class ActivityWX extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"图片","视频","文档","其他"};
    private FmPagerAdapter pagerAdapter;
    private List<HashMap> datas;





    ArrayList localList = new ArrayList<File>();
    ArrayList slocalList = new ArrayList<File>();
    ArrayList ldata = new ArrayList<BeanFile>();
    ArrayList sldata = new ArrayList<BeanFile>();

    ArrayList localList1 = new ArrayList<File>();
    ArrayList slocalList1 = new ArrayList<File>();
    ArrayList ldata1 = new ArrayList<BeanFile>();
    ArrayList sldata1 = new ArrayList<BeanFile>();


    ArrayList localList2 = new ArrayList<File>();
    ArrayList slocalList2 = new ArrayList<File>();
    ArrayList ldata2 = new ArrayList<BeanFile>();
    ArrayList sldata2 = new ArrayList<BeanFile>();

    ArrayList localList3 = new ArrayList<File>();
    ArrayList slocalList3 = new ArrayList<File>();
    ArrayList ldata3 = new ArrayList<BeanFile>();
    ArrayList sldata3 = new ArrayList<BeanFile>();
    private ArrayList<ArrayList> allDatas;
    private boolean showEdit = false;
    private boolean isAllSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx);

        //
        // //getSupportActionBar().hide();

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle(R.string.file_wx);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);
        init();

//        findViewById(R.id.btn_fm_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        reloadData();



        allDatas = new ArrayList<ArrayList>();
        allDatas.add(localList);
        allDatas.add(localList1);
        allDatas.add(localList2);
        allDatas.add(localList3);

//        findViewById(com.zhihu.matisse.R.id.iv_oper).setOnClickListener(new View.OnClickListener() {
//
//            private PopupWindow mPop;
//
//            @Override
//            public void onClick(View view) {
//                View popView = getLayoutInflater().inflate(com.zhihu.matisse.R.layout.popview_oper_layout, null);
//                popView.findViewById(com.zhihu.matisse.R.id.btn_sys).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //delete
//                        mPop.dismiss();
//
//                    }
//                });
//                popView.findViewById(com.zhihu.matisse.R.id.btn_ch).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //copytou
//                        mPop.dismiss();
//
//
//                    }
//                });
//
//
//                mPop = new PopupWindow(popView, ViewTool.dp2px(getBaseContext(), 110),ViewTool.dp2px(getBaseContext(), 123));
//                mPop.setOutsideTouchable(false);
//                mPop.setFocusable(true);
//                mPop.showAsDropDown(view);
//            }
//        });

        showOper(false);

        showCopyUpan(false);
        initOperUI();
//

        UsbHelper.getInstance().setmPreViewListener(new UsbHelper.PreViewListener() {
            @Override
            public void delFile(File file) {
               int pos = tabLayout.getSelectedTabPosition();

               UsbHelper.getInstance().remove(file);
               if(pos == 0){
                   File res = null;
                   for(Object o  : localList){
                       File f = (File) o;
                       if(f.getAbsolutePath().equals(file.getAbsolutePath())) {

                           res = f;
                           break;
                       }
                   }
                   if(res != null) {
                       localList.remove(res);
                       Fragment_wx_nine fragPic = (Fragment_wx_nine) fragments.get(0);
                       fragPic.setMlist((List) localList);


                   }
               }
                if(pos == 1){
                    File res = null;
                    for(Object o  : localList1){
                        File f = (File) o;
                        if(f.getAbsolutePath() == file.getAbsolutePath()) {

                            res = f;
                            break;
                        }
                    }
                    if(res != null) {
                        localList1.remove(res);
                        Fragment_wx_nine fragPic = (Fragment_wx_nine) fragments.get(1);
                        fragPic.setMlist((List) localList1);


                    }
                }
            }
        });

    }


     public void reloadData(){

          localList.clear();
          slocalList.clear();
          ldata.clear();
          sldata.clear();

          localList1.clear();
          slocalList1.clear();
          ldata1.clear();
          sldata1.clear();


          localList2.clear();
          slocalList2.clear();
          ldata2.clear();
          sldata2.clear();

          localList3.clear();
          slocalList3.clear();
          ldata3.clear();
          sldata3.clear();

         {
                Fragment_wx_nine fragPic = (Fragment_wx_nine) fragments.get(0);
                Fragment_wx_nine fragPic1 = (Fragment_wx_nine) fragments.get(1);
                Fragment_filelist fragPic2 = (Fragment_filelist) fragments.get(2);
                Fragment_filelist fragPic3 = (Fragment_filelist) fragments.get(3);
                fragPic.setMlist(new ArrayList<>());
                fragPic1.setMlist(new ArrayList<>());
                fragPic2.setMlist(new ArrayList<>());
                fragPic3.setMlist(new ArrayList<>());
         }
         slocalList = UsbHelper.getInstance().getSelectFiles();
         datas = FileTool.getWeixi(ActivityWX.this);
         String picKey = "图片";
         String videoKey = "视频";
         String textKey = "文档";
         String otherKey = "其他";

         UsbHelper.getInstance().getSelectFiles().clear();

         UsbHelper.getInstance().wxSelectEnable = false;

         for(HashMap map : datas)
        {

            String key = (String) map.keySet().toArray()[0];
            Object obj = map.get(key);


            if(key.equals(picKey)) {
                localList.addAll((ArrayList)obj);
                Fragment_wx_nine fragPic = (Fragment_wx_nine) fragments.get(0);
                fragPic.setMlist((List) obj);


                fragPic.setOnItemClickListener(new Fragment_wx_nine.ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {


                        ArrayList filePaths = new ArrayList();

                        for(Object f : localList){
                            File fi = (File) f;
                            filePaths.add(fi.getAbsolutePath());
                        }


                        Intent in = new Intent(ActivityWX.this,ActivityPreview.class);
                        in.putExtra("index",position);
                        in.putExtra("filePaths",filePaths);
                        startActivity(in);
                    }

                    @Override
                    public void onItemCheckClick(int position) {


                        File f = (File) localList.get(position);

                        if(slocalList.contains(f)){
                            slocalList.remove(f);
                        }else {
                            slocalList.add(f);
                        }
                        fragPic.setSlist(slocalList);

                        updateCount();


                    }

                    @Override
                    public void onItemLongClick(boolean edit) {
                        showOper(edit);
                    }
                });
            }
            if(key.equals(videoKey)) {
                //localList1 = (ArrayList) obj;
                localList1.addAll((ArrayList)obj);
                Fragment_wx_nine fragPic = (Fragment_wx_nine) fragments.get(1);
                fragPic.setMlist((List) obj);
                fragPic.setOnItemClickListener(new Fragment_wx_nine.ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {


                        ArrayList filePaths = new ArrayList();

                        for(Object f : localList1){
                            File fi = (File) f;
                            filePaths.add(fi.getAbsolutePath());
                        }

                        Intent in = new Intent(ActivityWX.this,ActivityPreview.class);
                        in.putExtra("index",position);
                        in.putExtra("filePaths",filePaths);
                        startActivity(in);
                    }

                    @Override
                    public void onItemCheckClick(int position) {

                        File f = (File) localList1.get(position);

                        if(slocalList.contains(f)){
                            slocalList.remove(f);
                        }else {
                            slocalList.add(f);
                        }
                        fragPic.setSlist(slocalList);
                        updateCount();
                    }

                    @Override
                    public void onItemLongClick(boolean edit) {
                        showOper(edit);
                    }
                });
            }

            if(key.equals(textKey)) {
                Fragment_filelist fragPic = (Fragment_filelist) fragments.get(2);


                List wxlist = (List) obj;
                localList2.clear();

                localList2.addAll((Collection<? extends File>) obj); //= (ArrayList<File>) obj;
                ldata2.clear();
                for(int i = 0;i< wxlist.size();i++){
                    File file = (File) wxlist.get(i);
                    int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                    ldata2.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
                }
                fragPic.setMlist(ldata2);

                fragPic.setOnItemClickListener(new Fragment_filelist.ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        File f = (File) localList2.get(position);

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

                    @Override
                    public void onItemCheckClick(int position) {
                        File f = (File) localList2.get(position);

                        if(slocalList.contains(f)){
                            slocalList.remove(f);
                            slocalList2.remove(f);
                        }else {
                            slocalList.add(f);
                            slocalList2.add(f);
                        }
                        fragPic.setSlist(slocalList2);

                        sldata2.clear();
                        for(int i = 0;i< slocalList2.size();i++){
                            File file = (File) slocalList2.get(i);
                            int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                            sldata2.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
                        }
                        fragPic.setSlist(sldata2);
                        updateCount();
                    }

                    @Override
                    public void onItemLongClick(boolean edit) {
                        showOper(edit);
                    }

                    @Override
                    public void onDirItemClick(int position, Object obj) {

                    }
                });

            }

            if(key.equals(otherKey)) {
                Fragment_filelist fragPic = (Fragment_filelist) fragments.get(3);
                fragPic.setMlist((List) obj);

                List wxlist = (List) obj;
                localList3.clear();

//
                localList3.addAll((Collection<? extends File>) obj); //= (ArrayList<File>) obj;
                ldata3.clear();
                for(int i = 0;i< wxlist.size();i++){
                    File file = (File) wxlist.get(i);
                    int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                    ldata3.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
                }
                fragPic.setMlist(ldata3);

                fragPic.setOnItemClickListener(new Fragment_filelist.ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        File f = (File) localList3.get(position);

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

                    @Override
                    public void onItemCheckClick(int position) {
                        File f = (File) localList3.get(position);

                        if(slocalList.contains(f)){
                            slocalList.remove(f);
                            slocalList3.remove(f);
                        }else {
                            slocalList.add(f);
                            slocalList3.add(f);
                        }

                        sldata3.clear();
                        for(int i = 0;i< slocalList3.size();i++){
                            File file = (File) slocalList3.get(i);
                            int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                            sldata3.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
                        }
                        fragPic.setSlist(sldata3);

                        updateCount();
                    }

                    @Override
                    public void onItemLongClick(boolean edit) {
                        showOper(edit);
                    }

                    @Override
                    public void onDirItemClick(int position, Object obj) {

                    }
                });
            }
        }


         TransFileManager.getInstance().addStatuChangeListener(new Fragment_ft.StatuChangeListener() {
             @Override
             public void onStatuChange() {

                 TextView tv = findViewById(R.id.tv_count);
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
    }

    private boolean toList = false;
    protected void initOperUI() {

        findViewById(R.id.tv_allSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView) view;

                int position = tabLayout.getSelectedTabPosition();
                ArrayList cl = allDatas.get(position);
                if(!isAllSelect){
                    UsbHelper.getInstance().addAll(cl);
                }else{
                    UsbHelper.getInstance().removeAll(cl);
                }
                isAllSelect = !isAllSelect;
                updateCount();

                if(position < 2){
                    Fragment_wx_nine wxn = (Fragment_wx_nine) fragments.get(position);
                    wxn.setSlist(UsbHelper.getInstance().getSelectFiles());
                }else{
                    Fragment_filelist wxl = (Fragment_filelist) fragments.get(position);

                    ArrayList sdatas = new ArrayList();
                    for(int i = 0;i<  UsbHelper.getInstance().getSelectFiles().size();i++){
                        File file = (File) UsbHelper.getInstance().getSelectFiles().get(i);
                        int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                        sdatas.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
                    }
                    wxl.setSlist(sdatas);
                }

            }
        });
        findViewById(R.id.ll_oper);
        //findViewById(R.id.tv_oper_count);
        findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List files = UsbHelper.getInstance().getSelectFiles();
                if (files.size() == 0) {
                    toast(getString(R.string.unselect_file));
                    return;
                }

                String tip ="";
                for(int i = 0; i < files.size();i++){
                    File f = (File) files.get(i);
                    tip = tip + "\n" + f.getName();
                    if(i == 3){
                        tip = tip +"..."+files.size()+getString(R.string.files);
                        break;
                    }



                }

                AlertDialog alert = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWX.this);
                alert = builder.setTitle(getString(R.string.tip))
                        .setMessage(getString(R.string.confirm_del_file) + tip)
                        .setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                List files = UsbHelper.getInstance().getSelectFiles();
                                if (files.size() == 0) {
                                    toast(getString(R.string.unselect_file));
                                    return;
                                }
                                LCProgressDialog pd = new LCProgressDialog(ActivityWX.this,getString(R.string.deleting),0);

                                if(files.size() > 0){
                                    pd.show();
                                }

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (Object o : files) {
                                            File file = (File) o;
                                            toast(FileTool.deleteMediaStore(file, ActivityWX.this));

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    int progress = files.indexOf(file)  * 100 / files.size();

                                                    pd.setProgress(progress);
                                                }
                                            });

                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                pd.dismiss();

                                                reloadData();
                                            }
                                        });

                                    }
                                }).start();

                            }
                        }).show();
            }

        });
        findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                List files = UsbHelper.getInstance().getSelectFiles();
                if (files.size() == 0) {
                    toast(getString(R.string.unselect_file));
                    return;
                }

                String tip ="";
                for(int i = 0; i < files.size();i++){
                    File f = (File) files.get(i);
                    tip = tip + "\n" + f.getName();
                    if(i == 3){
                        tip = tip +"..."+files.size()+getString(R.string.files);
                        break;
                    }



                }

                AlertDialog alert = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWX.this);
                alert = builder.setTitle(getString(R.string.tip))
                        .setMessage(getString(R.string.confirm_copy_file) + tip)
                        .setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                List files = UsbHelper.getInstance().getSelectFiles();
                                if(files.size() == 0){
                                    toast(getString(R.string.unselect_file));
                                    return;
                                }
//                                LoadingDialog mloadDialog = new LoadingDialog(ActivityWX.this,getString(R.string.copying),false);
//                                mloadDialog.show();

                                LCProgressDialog mloadDialog = new LCProgressDialog(ActivityWX.this,getString(R.string.add_copy_lists),0);

                                mloadDialog.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList tfs = new ArrayList();
                                        for (Object o : files) {
                                            File file = (File) o;
                                            //FileTool.copyToUPanoRoot(file, ActivityWX.this);

                                            SafFile3 sf = UsbHelper.getInstance().getRootFile();

                                            Object to = UsbHelper.getInstance().getRootFile();
                                            int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                                            Fragment_ft.TransFile tf = new Fragment_ft.TransFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file);
                                            tf.setStatu(0);

                                            tf.setStatuDes("待复制");

                                            tf.setCopyDes(0);
                                            tf.setCopyDes_des("复制到U盘");
                                            tf.setFileObj(file);
                                            tf.setToFileObj(to);
                                            tfs.add(tf);
                                            int progress = files.indexOf(o) *100 / files.size();

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    mloadDialog.setProgress(progress);
                                                }
                                            });
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                //Toast.makeText(ActivityWX.this, "复制成功", Toast.LENGTH_LONG);

                                                if(toList){
                                                    Intent in = new Intent(ActivityWX.this,Activity_Task.class);
                                                    startActivity(in);

                                                    //return;
                                                }


                                                TransFileManager.getInstance().addTransFiles(tfs);
                                                UsbHelper.getInstance().getSelectFiles().clear();
                                                mloadDialog.dismiss();
                                                reloadData();

//                                                Intent in = new Intent(ActivityWX.this,Activity_Task.class);
//                                                startActivity(in);
                                            }
                                        });
                                    }
                                }).start();

                            }

                        }).show();
            }
        });
        findViewById(R.id.tv_canceledit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOper(false);
                for(Fragment f : fragments){
                    if(f instanceof Fragment_wx_nine){
                        Fragment_wx_nine fg = (Fragment_wx_nine) f;
                        fg.setMedit(false);
                    }
                    if(f instanceof Fragment_filelist){
                        Fragment_filelist fg = (Fragment_filelist) f;
                        fg.setMedit(false);
                    }
                }
            }
        });

    }

    protected void showOper(boolean show) {

        showEdit = show;

        //findViewById(R.id.tv_allSelect).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.ll_oper).setVisibility(show ? View.VISIBLE : View.GONE);
       // findViewById(R.id.tv_oper_count);
        findViewById(R.id.tv_del);
        findViewById(R.id.tv_copy);

        showCopyUpan(false);
        if(UsbHelper.getInstance().canCopyToU()){
            showCopyUpan(true);
        }

    }

    protected void showCopyUpan(boolean show) {

        findViewById(R.id.fl_copy).setVisibility(show ? View.VISIBLE : View.GONE);


    }

    protected void updateCount() {

//        TextView tv = findViewById(R.id.tv_oper_count);
//        tv.setText("已选" + "(" + UsbHelper.getInstance().getSelectFiles().size() + ")");

        int position = tabLayout.getSelectedTabPosition();
        ArrayList cl = allDatas.get(position);

            boolean found = false;
            for(Object o : cl){
                File f = (File) o;



                found = false;
                for(File sf : UsbHelper.getInstance().getSelectFiles()){

                    if(f.getAbsolutePath().equals(sf.getAbsolutePath())){
                        found = true;
                        break;

                    }
                }
                if(!found){
                    break;
                }
            }



        TextView tv_s = findViewById(R.id.tv_allSelect);
        Drawable rightDrawable = getResources().getDrawable(!found ? R.mipmap.ic_wx_selectall_un : R.mipmap.ic_wx_selectall);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        tv_s.setCompoundDrawables(rightDrawable, null, null, null);


    }

    @Override
    protected void onStart() {
        super.onStart();
//        tabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                ViewTool.setIndicator(tabLayout, 40, 40);
//            }
//        });


    }

    private void init() {

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.vg);

        for(int i=0;i<titles.length;i++){
            if(i < 2) {
                fragments.add(new Fragment_wx_nine());
                tabLayout.addTab(tabLayout.newTab());
            }else{
                fragments.add(new Fragment_filelist());
                tabLayout.addTab(tabLayout.newTab());
            }
        }

        tabLayout.setupWithViewPager(viewPager,false);


        pagerAdapter = new FmPagerAdapter(fragments,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        for(int i=0;i<titles.length;i++){
            tabLayout.getTabAt(i).setText(titles[i]);
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tabLayout.getSelectedTabPosition();

                if(position < 2){
                    Fragment_wx_nine wxn = (Fragment_wx_nine) fragments.get(position);
                    wxn.setSlist(UsbHelper.getInstance().getSelectFiles());
                    wxn.setMedit(showEdit);
                }else{
                    Fragment_filelist wxl = (Fragment_filelist) fragments.get(position);

                    ArrayList sdatas = new ArrayList();
                    for(int i = 0;i<  UsbHelper.getInstance().getSelectFiles().size();i++){
                        File file = (File) UsbHelper.getInstance().getSelectFiles().get(i);
                        int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                        sdatas.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
                    }
                    wxl.setSlist(sdatas);
                    wxl.setMedit(showEdit);
                }
                updateCount();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });
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