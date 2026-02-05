package com.sentaroh.android.upantool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ActivityWX_ extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private FmPagerAdapter pagerAdapter;
    private List<HashMap> datas;
    Button btn_allSelect = null;





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
    private Boolean showOper = false;
    private LCProgressDialog mloadDialog;
    private String[] titles;
    private Toolbar tb;
    private int allCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_layout);

        //
        // //getSupportActionBar().hide();

        tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle(R.string.file_wx);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);


        tb.setTitleTextColor(ContextCompat.getColor(this, R.color.nav_text_color)); // 设置标题颜色

// 设置导航图标和颜色
        Drawable backIcon = ContextCompat.getDrawable(this, com.sentaroh.android.upantool.R.mipmap.ic_fm_back);
        if (backIcon != null) {
            backIcon = DrawableCompat.wrap(backIcon);
            DrawableCompat.setTint(backIcon, ContextCompat.getColor(this, R.color.nav_icon_color)); // 设置图标颜色
            tb.setNavigationIcon(backIcon);
        }

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_allSelect = tb.findViewById(R.id.btn_allSelect);


        titles = new String[]{getString(R.string.file_pic),getString(R.string.file_video),getString(R.string.file_doc),getString(R.string.file_other)};


        init();
        reloadData();







        allDatas = new ArrayList<ArrayList>();
        allDatas.add(localList);
        allDatas.add(localList1);
        allDatas.add(localList2);
        allDatas.add(localList3);



        for(int i=0;i<titles.length;i++){

            List list = allDatas.get(i);
            tabLayout.getTabAt(i).setText((list.size() == 0) ? (titles[i]) : (titles[i] + "(" + list.size() + ")"));
        }

        showOper(false);

        showCopyUpan(false);
        initOperUI();
//


        operEvent();
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

            fragPic2.setHide_dir(true);
            Fragment_filelist fragPic3 = (Fragment_filelist) fragments.get(3);
            fragPic3.setHide_dir(true);
            fragPic.setMlist(new ArrayList<>());
            fragPic1.setMlist(new ArrayList<>());
            fragPic2.setMlist(new ArrayList<>());
            fragPic3.setMlist(new ArrayList<>());
        }
        slocalList = UsbHelper.getInstance().getSelectFiles();
        datas = FileTool.getWeixi(ActivityWX_.this);
        String picKey = "图片";
        String videoKey = "视频";
        String textKey = "文档";
        String otherKey = "其他";

        UsbHelper.getInstance().getSelectFiles().clear();

        UsbHelper.getInstance().wxSelectEnable = false;

        int count = 0;
        for(HashMap map : datas)
        {

            String key = (String) map.keySet().toArray()[0];
            Object obj = map.get(key);


            List ls = (List) obj;
            count = count + ls.size();

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


                        Intent in = new Intent(ActivityWX_.this,ActivityPreview.class);
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
                        setDefaultStypeTb(!edit);
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

                        Intent in = new Intent(ActivityWX_.this,ActivityPreview.class);
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
                        setDefaultStypeTb(!edit);
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
                        setDefaultStypeTb(!edit);
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
                        setDefaultStypeTb(!edit);
                    }

                    @Override
                    public void onDirItemClick(int position, Object obj) {

                    }
                });
            }
        }

        allCount = count;

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


                if(Config.ImagePick){



                    if(ItemPickManager.itemPickFinsh != null){

                        List<File> list = UsbHelper.getInstance().getSelectFiles();
                        ArrayList al = new ArrayList();
                        ArrayList fs = new ArrayList();
                        for(File o : list){


                            Uri uri = FileTool.getUirFromPath(ActivityWX_.this,o.getAbsolutePath());
                            al.add(uri.toString());
                            fs.add(o.getAbsolutePath());
                        }

                        ItemPickManager.itemPickFinsh.imagePickFinsh(1,al,fs);
                        finish();
                    }

                    return;
                }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWX_.this);
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
                                LCProgressDialog pd = new LCProgressDialog(ActivityWX_.this,getString(R.string.deleting),0);

                                if(files.size() > 0){
                                    pd.show();
                                }

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (Object o : files) {
                                            File file = (File) o;
                                            toast(FileTool.deleteMediaStore(file, ActivityWX_.this));

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWX_.this);
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
//                                LoadingDialog mloadDialog = new LoadingDialog(ActivityWX_.this,getString(R.string.copying),false);
//                                mloadDialog.show();

                                LCProgressDialog mloadDialog = new LCProgressDialog(ActivityWX_.this,getString(R.string.add_copy_lists),0);

                                mloadDialog.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList tfs = new ArrayList();
                                        for (Object o : files) {
                                            File file = (File) o;
                                            //FileTool.copyToUPanoRoot(file, ActivityWX_.this);

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

                                                //Toast.makeText(ActivityWX_.this, "复制成功", Toast.LENGTH_LONG);

                                                if(toList){
                                                    Intent in = new Intent(ActivityWX_.this,Activity_Task.class);
                                                    startActivity(in);

                                                    //return;
                                                }


                                                TransFileManager.getInstance().addTransFiles(tfs);
                                                UsbHelper.getInstance().getSelectFiles().clear();
                                                mloadDialog.dismiss();
                                                reloadData();

//                                                Intent in = new Intent(ActivityWX_.this,Activity_Task.class);
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
                setDefaultStypeTb(true);
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


    void operEvent(){
        findViewById(R.id.btn_allSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(Config.ImagePick){
                    if(ItemPickManager.itemPickFinsh != null){

                        List<File> list = UsbHelper.getInstance().getSelectFiles();
                        ArrayList al = new ArrayList();
                        ArrayList fs = new ArrayList();
                        for(File o : list){


                            Uri uri = FileTool.getUirFromPath(ActivityWX_.this,o.getAbsolutePath());
                            al.add(uri.toString());
                            fs.add(o.getAbsolutePath());
                        }

                        ItemPickManager.itemPickFinsh.imagePickFinsh(1,al,fs);
                        finish();
                    }
                    return;
                }
                view.setEnabled(false);
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

                showBtnAllSelectStatu(!isAllSelect);
                view.setEnabled(true);

            }
        });

        findViewById(R.id.btn_canceledit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsbHelper.getInstance().selectFiles.clear();

                showOper(false);
                setDefaultStypeTb(true);

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


        LinearLayout ll = findViewById(R.id.ll_oper_bottom);
        ll.findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List files = UsbHelper.getInstance().getSelectFiles();
                if(files.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }



                Intent in = new Intent(ActivityWX_.this, Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();

                                for (Object o : files) {
                                    File sf = (File) o;
                                    fps.add(sf.getPath());
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

                List files = UsbHelper.getInstance().getSelectFiles();
                if(files.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }


                Intent in = new Intent(ActivityWX_.this, Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();

                                for (Object o : files) {
                                    File sf = (File) o;
                                    fps.add(sf.getPath());

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

                List files = UsbHelper.getInstance().getSelectFiles();
                if(files.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }


                ArrayList fps = new ArrayList();

                for (Object o : files) {
                    File sf = (File) o;
                    fps.add(sf.getPath());

                }


                zipData_(fps);
            }
        });





        ll.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List files = UsbHelper.getInstance().getSelectFiles();

                if(files.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }
                ArrayList fps = new ArrayList();

                for (Object o : files) {
                    File sf = (File) o;
                    fps.add(sf.getPath());

                }

                delFile(fps);


            }
        });

        ll.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();

                List files = UsbHelper.getInstance().getSelectFiles();
                if(position == 0){
                    if(files.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }else {
                    if(files.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }

                ArrayList fps = new ArrayList();
                if(position == 0) {

                    for (Object o : files) {
                        SafFile3 sf = (SafFile3) o;
                        fps.add(sf.getPath());

                    }
                }else{
                    for (Object o : slocalList) {
                        File sf = (File) o;
                        fps.add(sf.getPath());

                    }
                }


                //ShareTools.shareWechatFriend(ActivityWX_.this, new File((String) fps.get(0)));
                ShareTools.shareMoreToWechatFriend(ActivityWX_.this,fps);


            }
        });



    }


    void delFile(List<String> filePaths){

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.deleting));
        new Thread(new Runnable() {
            @Override
            public void run() {

                for(String p : filePaths){
                    SafFile3 sf = new SafFile3(ActivityWX_.this,p);
                    sf.delete();

                    FileTool.deleteMediaStore(new File(p),ActivityWX_.this);
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

                        LCProgressDialog pd = new LCProgressDialog(ActivityWX_.this,"正在加密",0);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        pd.dismiss();
//                                        Intent in = new Intent(getActivity(),Activity_FM_.class);
//                                        in.putExtra("postion", 1);
//                                        in.putExtra("dirPath", ff.getParent());
//                                        startActivity(in);

                                        new AlertDialog.Builder(ActivityWX_.this)
                                                .setTitle(getString(R.string.finsh_encrypt))
                                                .setMessage(finalName)
                                                .setPositiveButton(getString(R.string.go_find), new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // 获取输入框的内容

                                                        Intent in = new Intent(ActivityWX_.this,Activity_FM_.class);
                                                        in.putExtra("postion", 1);
                                                        in.putExtra("dirPath", ff.getParent());
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
    void zipData(List<String> filePaths){

        final EditText text = new EditText(this);
        text.setText("");
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.input_pass)
                .setView(text)

                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = text.getText().toString();

                        List files = UsbHelper.getInstance().getSelectFiles();

                        Intent in = new Intent(ActivityWX_.this, Activity_filePathSelector.class);
                        startActivity(in);
                        TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                            @Override
                            public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                                ((AppCompatActivity)c).finish();
                                ProgressDialog pd = new ProgressDialog(ActivityWX_.this);
                                pd.setMessage("正在加密");
                                pd.setProgress(0);
                                pd.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList fps = new ArrayList();

                                        for (Object o : files) {
                                            File sf = (File) o;
                                            fps.add(sf.getPath());

                                        }
                                        ArrayList copyCachePaths = new ArrayList();
                                        for(String p : filePaths){
                                            SafFile3 sf = new SafFile3(ActivityWX_.this,p);

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

    public void buidTransDatas(List filePaths,String toDirPath,Boolean delete){

        runOnUiThread(new Runnable() {


            @Override
            public void run() {

                mloadDialog = new LCProgressDialog(ActivityWX_.this,getString(R.string.add_copy_lists),0);

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
                Intent in = new Intent(ActivityWX_.this,Activity_Task.class);
                startActivity(in);

                mloadDialog.dismiss();
            }
        });

    }

    protected void showOper(boolean show) {

        if(Config.ImagePick){
            return;
        }
        showOper = show;

        showEdit = show;
        int showV = show ? View.VISIBLE : View.GONE;

        findViewById(R.id.ll_oper_bottom).setVisibility(showV);
        findViewById(R.id.btn_canceledit).setVisibility(showV);
        findViewById(R.id.btn_allSelect).setVisibility(showV);

        if(show){


        }else {


            UsbHelper.getInstance().getSelectFiles().clear();
            reloadData();

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

    protected void showCopyUpan(boolean show) {

        findViewById(R.id.fl_copy).setVisibility(show ? View.VISIBLE : View.GONE);


    }

    protected void updateCount() {

//        TextView tv = findViewById(R.id.tv_oper_count);
//        tv.setText("已选" + "(" + UsbHelper.getInstance().getSelectFiles().size() + ")");


        if(Config.ImagePick){

            TextView tv = findViewById(R.id.btn_allSelect);
            tv.setVisibility(View.GONE);
            if(UsbHelper.getInstance().getSelectFiles().size() > 0) {
                tv.setVisibility(View.VISIBLE);
                tv.setText("确定" + "(" + UsbHelper.getInstance().getSelectFiles().size() + ")");
            }else{
                tv.setText("");
            }
            return;
        }


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


        setDefaultStypeTb(!showOper);
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

                Fragment_wx_nine nine = new Fragment_wx_nine();
                nine.setMedit(true);
                fragments.add(nine);

                //TabLayout.Tab tab = tabLayout.newTab();
                tabLayout.addTab(tabLayout.newTab());
            }else{
                Fragment_filelist filelist = new Fragment_filelist();
                filelist.setMedit(true);
                fragments.add(filelist);
                tabLayout.addTab(tabLayout.newTab());
            }
        }

        tabLayout.setupWithViewPager(viewPager,false);


        pagerAdapter = new FmPagerAdapter(fragments,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        for(int i=0;i<titles.length;i++){

           // List list = allDatas.get(i);
            tabLayout.getTabAt(i).setText(titles[i]);
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tabLayout.getSelectedTabPosition();

                if(position < 2){
                    Fragment_wx_nine wxn = (Fragment_wx_nine) fragments.get(position);
                    wxn.setSlist(UsbHelper.getInstance().getSelectFiles());
                    //wxn.setMedit(showEdit);
                }else{
                    Fragment_filelist wxl = (Fragment_filelist) fragments.get(position);

                    ArrayList sdatas = new ArrayList();
                    for(int i = 0;i<  UsbHelper.getInstance().getSelectFiles().size();i++){
                        File file = (File) UsbHelper.getInstance().getSelectFiles().get(i);
                        int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                        sdatas.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
                    }
                    wxl.setSlist(sdatas);
                    //wxl.setMedit(showEdit);
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


    void setDefaultStypeTb(boolean defauat) {


        if(Config.ImagePick){

            return ;
        }
        if(defauat) {
            tb.setTitle(R.string.file_wx);
            //tb.setNavigationIcon(R.mipmap.ic_fm_back);
        }else{
            int pos = tabLayout.getSelectedTabPosition();
            List files = UsbHelper.getInstance().getSelectFiles();

            tb.setTitle(files.size() + "/" + allCount);

        }



    }

}