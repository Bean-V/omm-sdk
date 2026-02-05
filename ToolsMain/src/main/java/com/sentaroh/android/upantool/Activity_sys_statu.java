package com.sentaroh.android.upantool;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStatVfs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sentaroh.android.upantool.sysTask.SysNode;
import com.sentaroh.android.upantool.sysTask.TastTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Activity_sys_statu extends BaseActivity {


    private RecyclerView rv;
    private List mlist = new ArrayList();
    private Adapter apd;


    String dataType;
    String mtype;

    List files = new ArrayList();
    private LCProgressDialog progressDialog;
    private ProgressBar pb;
    private TextView textDes;
    private TextView dataDes;
    private boolean cancelMigrate = false;
    private Button migrateBut;

    private boolean migrateing = false;
    private TextView dataPer;
    private ProgressBar pb0;
    private TextView dataPer0;
    private ArrayList dataList;
    private ArrayList dataResList = new ArrayList();
    private ArrayList dataList1;
    private int reCopyCount;
    private ArrayList<String> reCopyFilePaths = new ArrayList<>();
    private Thread thread;
    private TextView tv_res_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_statu);



        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        tb.setTitle(R.string.sys_task);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                finish();
                //UsbHelper.getInstance().toStopTrans(false);
            }
        });

        Intent in = getIntent();
        dataType = in.getStringExtra("dataList");
        mtype = in.getStringExtra("type");

        progressDialog = new LCProgressDialog(this,getString(R.string.read_fileData),0);
        //progressDialog.show();


        pb = findViewById(R.id.pg_bar);

        tv_res_des = findViewById(R.id.tv_res_des);
        textDes = findViewById(R.id.tv_message);
        dataDes = findViewById(R.id.tv_progress_message);
        dataPer = findViewById(R.id.tv_percent_progress_message);
        pb0 = findViewById(R.id.pg_bar0);
        dataPer0 = findViewById(R.id.tv_percent_progress_message0);

        migrateBut = findViewById(R.id.btn_migrate);
        migrateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        apd = new Adapter(dataResList);
        rv.setAdapter(apd);

        initTimer();

    }

    private void initTimer() {

        thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                {
                    boolean test = true;
                    while(test){
                        try {
                            Thread.sleep(1000);//每隔1s执行一次

                            List resData = new ArrayList();
                            if(TastTool.getInstance().getDataResList() != null){
                                resData.addAll(TastTool.getInstance().getDataResList());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        if(!TastTool.getInstance().isSysTask()){
                                            tv_res_des.setText(getString(R.string.sys_task_close));
                                        }else if(TastTool.getInstance().needToSys()){
                                            tv_res_des.setText(getString(R.string.sys_tasking));
                                        }else{
                                            tv_res_des.setText(getString(R.string.sys_task_stoped));
                                        }
                                        apd.refresh(resData);
                                    }
                                });

                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }



                }

            }
        });
        thread.start();
    }


    public static void goSys(Context context){
        Intent in = new Intent(context,Activity_sys_statu.class);
        context.startActivity(in);
    }
    public class Adapter extends RecyclerView.Adapter{


        private List<SysNode> mItems;
        public Adapter(List<SysNode> items) {
            //super();
            //super();
            mItems = items;
        }


        public void setItemClikListener(Fragment_contact_files.ItemClikListener itemClikListener) {
            this.itemClikListener = itemClikListener;
        }

        private Fragment_contact_files.ItemClikListener itemClikListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_migrate_res_layout,parent, false);//item_fragment_filelist

            return new ViewHolder(v);
        }

        @SuppressLint("RecyclerView")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mholder, int position) {

            ViewHolder holder = (ViewHolder) mholder;
            SysNode fileBean = mItems.get(position);

            holder.tv_name.setText(mItems.get(position).title);
            holder.iv_icon.setImageResource(fileBean.resId);
            holder.tv_count.setText(fileBean.index + "/" + fileBean.all);
            holder.tv_res.setText((fileBean.index  == fileBean.all) ? R.string.migrate_done : R.string.migrate_un_done);


        }

        @Override
        public int getItemCount() {
            int a = 5;
            int b =6;
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_name;
            public ImageView iv_icon;
            public View view;

            public TextView tv_count;
            public TextView tv_res;

            public ViewHolder(View v) {
                super(v);
                tv_name = v.findViewById(R.id.tv_name);
                tv_count = v.findViewById(R.id.tv_count);
                tv_res = v.findViewById(R.id.tv_res);
                iv_icon = v.findViewById(R.id.iv_icon);
                view = v;

            }
        }

        public void refresh(List list) {
            mItems = list;
            notifyDataSetChanged();
        }

    }



}