package com.sentaroh.android.upantool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Activity_dupfiles extends BaseActivity {

    private Fragment_filelist l_list;
    ArrayList ldata = new ArrayList<>();
    private List locallist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dupfiles);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle(R.string.migrate_Duplicate_file);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initFileContainer();
        initFileData();
        itemClikListener();
    }


    void initFileContainer(){


        l_list = new Fragment_filelist();
        l_list.setHide_dir(true);
        l_list.setMedit(false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.container, l_list);
        transaction.commitNow();
        l_list.setMlist(ldata);

    }

    void initFileData(){
        Intent in = getIntent();

        locallist = new ArrayList();
        List<String> dats = in.getStringArrayListExtra("files");
        if(dats != null){
            for(String s : dats){
                File f = new File(s);
                locallist.add(f);
            }
        }

        for (int i = 0; i < locallist.size(); i++) {
            File file = (File) locallist.get(i);
                ldata.add(new BeanFile(file.getName(), file.getPath(), file.isDirectory() ? "" : FileTool.getFileSize(file.length()), "", FileTool.getResIdFromFileName(file.isDirectory(), file.getName()), file));
        }
    }
    public static void tosee(Context context, ArrayList<String> list){
        Intent in = new Intent(context,Activity_dupfiles.class);
        in.putStringArrayListExtra("files",list);
        context.startActivity(in);

    }
    void itemClikListener(){
        l_list.setOnItemClickListener(new Fragment_filelist.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                File f = (File) locallist.get(position);

                {
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

            }

            @Override
            public void onItemLongClick(boolean edit) {

            }

            @Override
            public void onDirItemClick(int position, Object obj) {

            }

        });
    }
}