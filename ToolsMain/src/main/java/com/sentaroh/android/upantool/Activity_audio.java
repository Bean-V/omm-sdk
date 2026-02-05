package com.sentaroh.android.upantool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.FilePathSelector.Activity_filePathSelector;
import com.sentaroh.android.upantool.contact.FileUtil;
import com.zhihu.matisse.Config;
import com.zhihu.matisse.internal.ui.widget.CheckView;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activity_audio extends BaseActivity {

    private RecyclerView rv;
    private boolean showEdit;
    private RvAdapter adp;


    boolean isAllSelect = false;


    ArrayList slist = new ArrayList<>();
    private ArrayList datas;
    private ArrayList datas1;


    boolean showOper = false;
    private Button btn_allSelect;
    private LCProgressDialog mloadDialog;
    private Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle(R.string.file_audio);
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

        setDefaultStypeTb(true);
        btn_allSelect = tb.findViewById(R.id.btn_allSelect);

        LoadingDialog dilog = new LoadingDialog(this,"",true);
        dilog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                datas = (ArrayList) FileTool.getAudios(Activity_audio.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rv = findViewById(R.id.rv);
                        //rv.setAdapter(mAlbumsAdapter);

                        int space = ViewTool.dp2px(Activity_audio.this,10);
                        rv.addItemDecoration(new Activity_audio.SpacesItemDecoration(space));
                        rv.setPadding(space, 0, 0, 0);
                        rv.setLayoutManager(new GridLayoutManager(Activity_audio.this,3));
//        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
//        rv.addItemDecoration(new MediaGridInset(3, 5, false));


                        adp = new RvAdapter(datas);
                        rv.setAdapter(adp);


                        adp.refreshUserCheckStatu(slist);

                        if(dilog.isShowing()) {
                            dilog.dismiss();
                        }

                        showOper(false);

                        initOperUI();

                        operEvent();
                    }
                });
            }
        }).start();


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






    void operEvent(){
        findViewById(R.id.btn_allSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if(Config.ImagePick){

                    if(ItemPickManager.itemPickFinsh != null){

                        ArrayList al = new ArrayList();
                        ArrayList fs = new ArrayList();
                        for(Object o : slist){
                            String s = ((File) o).getAbsolutePath();
                            Uri uri = FileTool.getUirFromPath(Activity_audio.this,s);
                            al.add(uri.toString());
                            fs.add(s);
                        }

                        ItemPickManager.itemPickFinsh.imagePickFinsh(1,al,fs);
                        finish();
                    }
                    return;
                }
                isAllSelect = !isAllSelect;


                view.setEnabled(false);

                if(isAllSelect){
                    slist.clear();
                    slist.addAll(datas);
                }else{
                    slist.clear();
                }

                showBtnAllSelectStatu(slist.size() != datas.size());

                setDefaultStypeTb(false);

                adp.refreshUserCheckStatu(slist);
                view.setEnabled(true);

            }
        });

        findViewById(R.id.btn_canceledit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOper(false);
                setDefaultStypeTb(true);
                adp.refreshEdit(false);

            }
        });


        LinearLayout ll = findViewById(R.id.ll_oper_bottom);
        ll.findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(slist.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }



                Intent in = new Intent(Activity_audio.this, Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {
                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();

                                for (Object o : slist) {
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
                    if(slist.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }


                Intent in = new Intent(Activity_audio.this, Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();

                                    for (Object o : slist) {
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

                    if(slist.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }


                ArrayList fps = new ArrayList();

                for (Object o : slist) {
                    File sf = (File) o;
                    fps.add(sf.getPath());

                }


                zipData_(fps);
            }
        });


        ll.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(slist.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }


                ArrayList fps = new ArrayList();

                for (Object o : slist) {
                    File sf = (File) o;
                    fps.add(sf.getPath());

                }


                ShareTools.shareMoreToWechatFriend(Activity_audio.this,fps);
            }
        });






        ll.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(slist.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                ArrayList fps = new ArrayList();

                    for (Object o : slist) {
                        File sf = (File) o;
                        fps.add(sf.getPath());

                    }

                delFile(fps);


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
                    SafFile3 sf = new SafFile3(Activity_audio.this,p);
                    sf.delete();

                    FileTool.deleteMediaStore(new File(p),Activity_audio.this);
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

                        LCProgressDialog pd = new LCProgressDialog(Activity_audio.this,"正在加密",0);
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

                                        new AlertDialog.Builder(Activity_audio.this)
                                                .setTitle(getString(R.string.finsh_encrypt))
                                                .setMessage(finalName)
                                                .setPositiveButton(getString(R.string.go_find), new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // 获取输入框的内容

                                                        Intent in = new Intent(Activity_audio.this,Activity_FM_.class);
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

//    void zipData_(List<String> filePaths){
//
//        final EditText text = new EditText(this);
//        text.setText("");
//        new androidx.appcompat.app.AlertDialog.Builder(this)
//                .setTitle(R.string.input_pass)
//                .setView(text)
//
//                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String pass = text.getText().toString();
//
//                                ProgressDialog pd = new ProgressDialog(this);
//                                pd.setMessage("正在加密");
//                                pd.setProgress(0);
//                                pd.show();
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ArrayList fps = new ArrayList();
//
//                                        for (Object o : slist) {
//                                            File sf = (File) o;
//                                            fps.add(sf.getPath());
//
//                                        }
//                                        ArrayList copyCachePaths = new ArrayList();
//                                        for(String p : filePaths){
//                                            SafFile3 sf = new SafFile3(Activity_audio.this,p);
//
//                                            String des = UsbHelper.getInstance().getSdRootPath() + "/" + FileUtil.YISU_CACHE + "/" + sf.getName();
//                                            FileTool.copyWithPath(p, des);
//                                            copyCachePaths.add(new File(des));
//                                        }
//                                        ZipParameters zipParameters = new ZipParameters();
//                                        zipParameters.setEncryptFiles(true);
//                                        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
//// Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
//                                        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
//                                        zipParameters.setPassword(pass);
//
//
//
////                                        List<File> filesToAdd = Arrays.asList(
////                                                new File("somefile"),
////                                                new File("someotherfile")
////                                        );
//
//                                        String dirPath = Environment.getExternalStorageDirectory() + "/" + "yisucache";
//
//                                        File dirFile = new File(dirPath);
//                                        if(!dirFile.exists()){
//                                            dirFile.mkdir();
//                                        }
//
//
//                                        String name = "加密" + TimeUtil.getCurrentTime("yyyy_MM_ddhh_mm_ss") + ".zip";
//
//                                        String zipPath = dirPath + "/" + name;
//                                        ZipFile zipFile = new ZipFile(dirPath + "/" + name,pass.toCharArray());
//                                        try {
//                                            for(Object p : copyCachePaths){
//                                                //String path = (String) p;
//                                                File f = (File) p;
//                                                if(f.isDirectory()){
//                                                    zipFile.addFolder(f, zipParameters);
//                                                }else {
//                                                    zipFile.addFile(f, zipParameters);
//                                                }
//                                            }
//
//
//
//                                        } catch (ZipException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                        ArrayList tans = new ArrayList();
//                                        tans.add(zipPath);
//
//                                        buidTransDatas(tans,sDirpath,true);
//
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//
//                                                pd.setProgress(100);
//                                                pd.dismiss();
//                                                ((AppCompatActivity)(c)).finish();
//                                                showOper(false);
//                                            }
//                                        });
//
//
//                                    }
//                                }).start();
//
//                            }
//                        });
//
//                    }
//                })
//                .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        dialog.dismiss();
//                    }
//                }).show();
//
//    }
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


                        Intent in = new Intent(Activity_audio.this, Activity_filePathSelector.class);
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
                                        ArrayList fps = new ArrayList();

                                            for (Object o : slist) {
                                                File sf = (File) o;
                                                fps.add(sf.getPath());

                                            }
                                        ArrayList copyCachePaths = new ArrayList();
                                        for(String p : filePaths){
                                            SafFile3 sf = new SafFile3(Activity_audio.this,p);

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

                mloadDialog = new LCProgressDialog(Activity_audio.this,getString(R.string.add_copy_lists),0);

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
                Intent in = new Intent(Activity_audio.this,Activity_Task.class);
                startActivity(in);

                mloadDialog.dismiss();
            }
        });

    }



    public void showOper(boolean show) {

        if(Config.ImagePick) {
            return;
        }
        showOper = show;
        int showV = show ? View.VISIBLE : View.GONE;

        findViewById(R.id.ll_oper_bottom).setVisibility(showV);
        findViewById(R.id.btn_canceledit).setVisibility(showV);
        findViewById(R.id.btn_allSelect).setVisibility(showV);

        if(show){


        }else {
            slist.clear();
            adp.refreshUserCheckStatu(slist);
            setDefaultStypeTb(true);

        }
    }

    void showBtnAllSelectStatu(boolean show){

       // isAllSelect = show;
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































































    protected void initOperUI() {

        findViewById(R.id.tv_allSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView) view;
                isAllSelect = !isAllSelect;
                if(isAllSelect){
                    slist.clear();
                    slist.addAll(datas);
                }else{
                    slist.clear();
                }
                adp.notifyDataSetChanged();
                updateCount();

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

                List files = slist;
                if(files.size() == 0){
                    toast("未选中文件");
                    return;
                }

                String tip ="";
                for(int i = 0; i < files.size();i++){
                    File f = (File) files.get(i);
                    tip = tip + "\n" + f.getName();
                    if(i == 3){
                        tip = tip +"等"+files.size()+"个文件";
                        break;
                    }



                }

                AlertDialog alert = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_audio.this);
                alert = builder.setTitle("提示")
                        .setMessage("确定删除文件" + tip)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                List files = slist;
                                if(files.size() == 0){
                                    toast("未选中文件");
                                    return;
                                }
                                LoadingDialog mloadDialog = new LoadingDialog(Activity_audio.this,"删除中",false);
                                mloadDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        for(Object o : files){
                                            File file = (File) o;
                                            toast(FileTool.deleteMediaStore(file,Activity_audio.this));

                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                mloadDialog.dismiss();

                                                reloadData();
                                            }
                                        });

                                    }
                                }).start();
                            }
                        }).create();             //创建AlertDialog对象
                alert.show();
//                if(mSpec.onOperClikListener != null){
//
//                    ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
//                    mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this,selectedPaths,1);
//                }
            }
        });
        findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                List files = slist;
                if(files.size() == 0){
                    toast("未选中文件");
                    return;
                }

                String tip ="";
                for(int i = 0; i < files.size();i++){
                    File f = (File) files.get(i);
                    tip = tip + "\n" + f.getName();
                    if(i == 3){
                        tip = tip +"等"+files.size()+"个文件";
                        break;
                    }



                }

                AlertDialog alert = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_audio.this);
                alert = builder.setTitle("提示")
                        .setMessage("确定复制文件" + tip)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                List files = slist;

                                ArrayList tfs = new ArrayList();
                                if(files.size() == 0){
                                    toast("未选中文件");
                                    return;
                                }
                                LoadingDialog mloadDialog = new LoadingDialog(Activity_audio.this,"复制中",false);
                                mloadDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        for(Object o : files){
                                            File file = (File) o;
//                                            FileTool.copyToUPanoRoot(file,Activity_audio.this);


                                            //FileTool.copyToUPanoRoot(file, ActivityWX.this);

                                            int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
                                            Fragment_ft.TransFile tf = new Fragment_ft.TransFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file);
                                            tf.setStatu(0);

                                            tf.setStatuDes("待复制");

                                            tf.setCopyDes(0);
                                            tf.setCopyDes_des("复制到U盘");
                                            tf.setFileObj(file);
                                            tf.setToFileObj(UsbHelper.getInstance().getRootFile());
                                            tfs.add(tf);
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(toList){
                                                    Intent in = new Intent(Activity_audio.this,Activity_Task.class);
                                                    startActivity(in);

                                                    //return;
                                                }

                                                TransFileManager.getInstance().addTransFiles(tfs);
                                                mloadDialog.dismiss();

                                                slist.clear();

                                                reloadData();
                                            }
                                        });

                                    }
                                }).start();
                            }
                        }).create();             //创建AlertDialog对象
                alert.show();
//                if(mSpec.onOperClikListener != null){
//
//                    ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
//                    mSpec.onOperClikListener.onOperClikListener(MatisseActivity.this,selectedPaths,1);
//                }
            }
        });;
        findViewById(R.id.tv_canceledit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOper(false);
                slist.clear();
                adp.refreshEdit(false);
                updateCount();
                setDefaultStypeTb(true);
            }
        });

    }

    private void reloadData() {

        LoadingDialog dilog = new LoadingDialog(this,"",true);
        dilog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                datas = (ArrayList) FileTool.getAudios(Activity_audio.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adp.refresh(datas);
                        adp.refreshUserCheckStatu(slist);
                        dilog.dismiss();
                        updateCount();

                        setDefaultStypeTb(false);

                    }
                });
            }
        }).start();


    }



//    protected void showOper(boolean show) {
//
//        showEdit = show;
//
//        // findViewById(R.id.tv_allSelect).setVisibility(show ? View.VISIBLE : View.GONE);
//        findViewById(R.id.ll_oper).setVisibility(show ? View.VISIBLE : View.GONE);
//        // findViewById(R.id.tv_oper_count);
//        findViewById(R.id.tv_del);
//        findViewById(R.id.fl_copy).setVisibility(View.GONE);
//        if(UsbHelper.getInstance().canCopyToU()){
//            findViewById(R.id.fl_copy).setVisibility(View.VISIBLE);
//        }
//
//
//
//    }

    protected void showCopyUpan(boolean show) {

        findViewById(R.id.fl_copy).setVisibility(show ? View.VISIBLE : View.GONE);


    }

    protected void updateCount() {

//        TextView tv = findViewById(R.id.tv_oper_count);
//        tv.setText("已选" + "(" + UsbHelper.getInstance().getSelectFiles().size() + ")");


        if(Config.ImagePick){

            TextView tv = findViewById(R.id.btn_allSelect);
            tv.setVisibility(View.GONE);
            if(slist.size() > 0) {
                tv.setVisibility(View.VISIBLE);
                tv.setText("确定" + "(" + slist.size() + ")");
            }else{
                tv.setText("");
            }
            return;
        }


        boolean found = false;
        for(Object oo : datas){
            File f = (File) oo;



            found = false;
            for(Object o : slist){
                File sf = (File) o;

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


        setDefaultStypeTb(false);
    }

    void setDefaultStypeTb(boolean defauat) {


        if(defauat) {
            tb.setTitle(R.string.file_audio);
            //tb.setNavigationIcon(R.mipmap.ic_fm_back);
        }else{
            tb.setTitle(slist.size() + "/" + datas.size());

        }



    }


    public class RvAdapter extends RecyclerView.Adapter{

        ArrayList<File> mItems = new ArrayList<File>();
        ArrayList<File> smItems = new ArrayList<File>();

        public boolean ischeck = true;
        public RvAdapter(ArrayList list){
            mItems = list;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_audio, parent, false);
            // return MediaViewHolder(v);

            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            VH vh = (VH) holder;

            File f = mItems.get(position);

            ImageView IV = (ImageView) ((VH) holder).img_icon;


           //((VH) holder).img_check.setImageResource(R.mipmap.ic_fm_list_item_uncheck);

            ((VH) holder).img_check.setVisibility(View.GONE);

            if(ischeck){
                ((VH) holder).img_check.setVisibility(View.VISIBLE);
                ((VH) holder).img_check.setCountable(true);
                ((VH) holder).img_check.setCheckedNum(CheckView.UNCHECKED);
            }
            if(FileTool.isVideo(f.getName())){

                ((VH) holder).img_play.setVisibility(View.VISIBLE);
            }

            ((VH) holder).tv_name.setText(f.getName());
            if (smItems != null) {
                for (int i = 0; i < smItems.size(); i++) {
                    File info1 = (File) smItems.get(i);
                    if (info1.getPath().equals(f.getPath()) && info1.getName().equals(f.getName())) {
                        //((VH) holder).img_check.setImageResource(R.mipmap.ic_fm_list_item_check);
                        ((VH) holder).img_check.setCheckedNum(i + 1);
                        break;
                    }
                }
            }

//
//
            ((VH) holder).img_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mItemClickListener.onItemCheckClick(mlist.indexOf(f));
                    if(slist.contains(f)){
                        slist.remove(f);
                    }else {
                        slist.add(f);
                    }
                    notifyDataSetChanged();
                    updateCount();


                }
            });
            ((VH) holder).img_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // mItemClickListener.onItemClick(mlist.indexOf(f));

                    if(!f.exists()){
                        toast("无效文件");
                        return;
                    }
                    try {
                        Intent in = FileTool.openFile(f.getAbsolutePath(),getBaseContext());
                        if(in == null){
                            toast("不支持打开此类文件");
                        }else{
                            startActivity(in);
                        }
                    } catch (ActivityNotFoundException e) {
                        toast("未安装相关可打开的应用");
                        
                        
                    }
                    return;
                }
            });

            ((VH) holder).img_icon.setOnLongClickListener (new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(Config.ImagePick) {
                        return true;
                    }
                    ischeck = !ischeck;
                    //UsbHelper.getInstance().wxSelectEnable = ischeck;
                    notifyDataSetChanged();
                    showOper(ischeck);

                    setDefaultStypeTb(!ischeck);

                   // mItemClickListener.onItemLongClick(ischeck);

                    return true;
                }

            });

            //holder.item.setOnLongClickListener(this);

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void refresh(List list){
            mItems = (ArrayList<File>) list;
            notifyDataSetChanged();
        }

        public void  refreshUserCheckStatu(List list){
            smItems = (ArrayList<File>) list;
            notifyDataSetChanged();
        }



        public void  refreshEdit(boolean edit){

            if(ischeck != edit) {
                ischeck = edit;
                notifyDataSetChanged();
            }
        }

    }

    private static class VH extends RecyclerView.ViewHolder {


        public  ImageView img_icon;
        public CheckView img_check;
        public  ImageView img_play;

        public TextView tv_name;

        VH(View v) {
            super(v);
            img_icon = v.findViewById(R.id.img_icon);
            img_check = v.findViewById(R.id.img_check);
            img_play = v.findViewById(R.id.img_play);
            tv_name = v.findViewById(R.id.tv_name);
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace = 0;
        public SpacesItemDecoration(int space) {
            mSpace = space;
        }
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if(parent.getLayoutManager() instanceof  GridLayoutManager){
                outRect.top = mSpace;
                outRect.right = mSpace;
            }
        }
    }
}