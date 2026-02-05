package com.sentaroh.android.upantool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.FilePathSelector.Activity_filePathSelector;

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

public class Activity_encryptlist extends BaseActivity {


    private Fragment_filelist l_list;
    ArrayList ldata = new ArrayList<>();
    private List locallist;
    private boolean showOper;
    private ArrayList slocalList = new ArrayList();
    private ArrayList sldata = new ArrayList();
    private Toolbar tb;
    private Button btn_allSelect;
    private LCProgressDialog mloadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryptlist);

        tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle(R.string.encrpty_list);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_allSelect = tb.findViewById(R.id.btn_allSelect);

        initFileContainer();
        new Thread(new Runnable() {
            @Override
            public void run() {

                initFileData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        l_list.setMlist(ldata);
                    }
                });

            }
        }).start();


        itemClikListener();
        operEvent();
    }


    void initFileContainer(){


        l_list = new Fragment_filelist();
        l_list.setHide_dir(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, l_list);
        transaction.commitNow();
        l_list.setMlist(ldata);

    }

    void initFileData(){
        locallist = new ArrayList();

        locallist.clear();
        ldata.clear();
        sldata.clear();
        slocalList.clear();
        File rootFile = new File(UsbHelper.getInstance().getSdRootPath());

        List<File> localList = (ArrayList<File>) FileTool.listfilesortbymodifytime(rootFile.getPath());
        if(localList != null){
            for(File f : localList){
                if(FileTool.isSelfEncryptFile(f.getPath())){
                    locallist.add(f);
                }
            }
        }

        for (int i = 0; i < locallist.size(); i++) {
            File file = (File) locallist.get(i);
            ldata.add(new BeanFile(file.getName(), file.getPath(), file.isDirectory() ? "" : FileTool.getFileSize(file.length()), "", FileTool.getResIdFromFileName(file.isDirectory(), file.getName()), file));
        }


    }
    public static void toThis(Context context){
        Intent in = new Intent(context,Activity_encryptlist.class);
        context.startActivity(in);

    }
    void itemClikListener(){
        l_list.setOnItemClickListener(new Fragment_filelist.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                File f = (File) locallist.get(position);
                unZip(f.getAbsolutePath());
            }

            @Override
            public void onItemCheckClick(int position) {
                File f = (File) locallist.get(position);
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

            }

            @Override
            public void onDirItemClick(int position, Object obj) {

            }

        });
    }

    void operEvent(){
        findViewById(R.id.btn_allSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                {
                    if(ldata.size() == sldata.size()){

                        sldata.clear();
                        l_list.setSlist(sldata);;
                        slocalList.clear();

                    }else {
                        sldata.clear();;
                        sldata.addAll(ldata);
                        l_list.setSlist(sldata);;
                        slocalList.clear();
                        slocalList.addAll(locallist);
                    }

                    showBtnAllSelectStatu(ldata.size() != sldata.size());
                }
                view.setEnabled(true);
                showOper(true);

            }
        });
        findViewById(R.id.btn_canceledit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOper(false);


            }
        });
        LinearLayout ll = findViewById(R.id.ll_oper_bottom);
        ll.findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }


                Intent in = new Intent(Activity_encryptlist.this, Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();
                                {
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
                {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }

                Intent in = new Intent(Activity_encryptlist.this, Activity_filePathSelector.class);
                startActivity(in);
                TransFileManager.getInstance().setPathSelectListener(new TransFileManager.DesPathSelectListener() {
                    @Override
                    public void desPathSelectFinsh(Context c, String sDirpath, Object other) {

                        ((AppCompatActivity)c).finish();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList fps = new ArrayList();
                                {
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

        ll.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }

                ArrayList fps = new ArrayList();
                {
                    for (Object o : slocalList) {
                        File sf = (File) o;
                        fps.add(sf.getPath());
                    }
                }
                ViewTool.confirm_to_action(Activity_encryptlist.this, null, getString(R.string.confirm_del_file), new DialogInterface.OnClickListener() {
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
                {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                }

                ArrayList fps = new ArrayList();
                {
                    for (Object o : slocalList) {
                        File sf = (File) o;
                        fps.add(sf.getPath());

                    }
                }
                ShareTools.shareWechatFriend(Activity_encryptlist.this, new File((String) fps.get(0)));


            }
        });

        ll.findViewById(R.id.btn_addpas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if(slocalList.size() == 0){
                        toast(getString(R.string.unselect_file));
                        return;
                    }
                    ///先压缩到cache,在move；



                }

                ArrayList fps = new ArrayList();
                {
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

                        LCProgressDialog pd = new LCProgressDialog(Activity_encryptlist.this,getString(R.string.addpasing),0);
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
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                initFileData();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        l_list.setMlist(ldata);
                                                    }
                                                });

                                            }
                                        }).start();




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


                        ProgressDialog pd = new ProgressDialog(Activity_encryptlist.this);
                        pd.setMessage(getString(R.string.decrypting));
                        pd.show();

                        File filezip  = new File(filePath);

                        File unzipDir = new File(filezip.getParent()  + "/" + (filezip.getName().replace(".zip","")).replace(filezip.getName().contains("加密") ? "加密" : getString(R.string.fm_add_pass),getString(R.string.decrypt)));
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

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();

                                        ViewTool.confirm_to_action_(Activity_encryptlist.this, null, getString(R.string.finsh_decrypt), getString(R.string.go_find),new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent in = new Intent(Activity_encryptlist.this,Activity_FM_.class);
                                                in.putExtra("postion", 1);
                                                in.putExtra("dirPath", unzipDir.getAbsolutePath());//ff.getParent()
                                                startActivity(in);
                                            }
                                        });
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

    void delFile(List<String> filePaths){

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.deleting));
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                for(String p : filePaths){
                    SafFile3 sf = new SafFile3(Activity_encryptlist.this,p);

                    if(!sf.isSafFile()){
                        while(sf.exists()) {
                            FileTool.deleteFile(sf.getFile());
                        }
                    }else{
                        try {
                            while(sf.exists()) {
                                FileTool.deleteUsbFile(sf);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        initFileData();
                    }
                });
            }
        }).start();
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

                mloadDialog = new LCProgressDialog(Activity_encryptlist.this,getString(R.string.add_copy_lists),0);

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
                Intent in = new Intent(Activity_encryptlist.this,Activity_Task.class);
                startActivity(in);

                mloadDialog.dismiss();
            }
        });

    }





    public void showOper(boolean show) {

        showOper = show;
        int showV = show ? View.VISIBLE : View.GONE;

        findViewById(R.id.ll_oper_bottom).setVisibility(showV);
        findViewById(R.id.btn_canceledit).setVisibility(showV);
        findViewById(R.id.btn_allSelect).setVisibility(showV);
        findViewById(R.id.ll_topoper).setVisibility(showV);

        if(show){

        }else {
            slocalList.clear();;
            sldata.clear();

            if(l_list != null) {
                l_list.setSlist(sldata);
                l_list.setMedit(false);
            }
        }

        LinearLayout ll_oper = findViewById(R.id.ll_oper_bottom);
        //ll_oper.findViewById(R.id.btn_addpas).setVisibility(View.GONE);

        if(show){
            tb.setTitle(sldata.size() + "/" + ldata.size());
        }else{
            tb.setTitle(R.string.file_manager);
            tb.setNavigationIcon(R.mipmap.ic_fm_back);
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


    void setDefaultEncrptTxt(boolean defauat) {

        TextView tv = findViewById(R.id.btn_addpas);

        if(defauat) {
            tv.setText(R.string.fm_add_pass);
        }else{
            tv.setText(R.string.decrypt);

        }

    }

}