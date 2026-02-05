package com.sentaroh.android.upantool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sentaroh.android.Utilities3.SafFile3;
import com.zhihu.matisse.internal.model.SelectedItemCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activity_migrate_statu extends BaseActivity {



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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migrate_statu);


        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mtype.equals("2") || mtype.equals("1") ){
                    if(migrateing){

                        toast(getString(R.string.migrate_back_tip));
                        return;
                    }
                }
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
        textDes = findViewById(R.id.tv_message);
        dataDes = findViewById(R.id.tv_progress_message);
        dataPer = findViewById(R.id.tv_percent_progress_message);
        pb0 = findViewById(R.id.pg_bar0);
        dataPer0 = findViewById(R.id.tv_percent_progress_message0);



        if(mtype.equals("2")){
            findViewById(R.id.ll_type1).setVisibility(View.GONE);
            findViewById(R.id.ll_type2).setVisibility(View.VISIBLE);
        }else if(mtype.equals("1")){
        }else if(mtype.equals("3")){
        }else if(mtype.equals("4")){
            findViewById(R.id.ll_type1).setVisibility(View.GONE);
            findViewById(R.id.ll_type2).setVisibility(View.VISIBLE);

        }


        new Thread(new Runnable() {
            @Override
            public void run() {


                if(mtype.equals("2")){
                    getData_();
                }else if(mtype.equals("1")){
                    getData();
                }else if(mtype.equals("3")){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            findViewById(R.id.ll_filePro).setVisibility(View.GONE);
                            migrateBut.setVisibility(View.GONE);

                        }
                    });
                    deleteUsb();
                }else if(mtype.equals("4")){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.ll_filePro).setVisibility(View.GONE);
                            migrateBut.setVisibility(View.GONE);
                        }
                    });
                    deleteLocal();
                }


            }
        }).start();


        migrateBut = findViewById(R.id.btn_migrate);
        migrateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cancelMigrate || !migrateing){

                    finish();
                    UsbHelper.getInstance().toStopTrans(false);
                    return;

                }
                view.setEnabled(false);
                ProgressDialog pd = new ProgressDialog(Activity_migrate_statu.this);
                pd.setMessage("");
                pd.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                        pd.dismiss();
                        //UsbHelper.getInstance().stopUsb();

                        findViewById(R.id.ll_res).setVisibility(View.VISIBLE);
                        findViewById(R.id.ll_migratepro).setVisibility(View.GONE);
                        apd.refresh(dataResList);
                    }
                }, 1000);

                UsbHelper.getInstance().toStopTrans(true);
                cancelMigrate = true;

                Button but = (Button) view;
                but.setText(R.string.confirm);

                TextView tv_res = findViewById(R.id.tv_res_des);
                tv_res.setText(getString(R.string.fm_cancel));

                TextView tv_time = findViewById(R.id.tv_res_time);
                tv_time.setText(TimeUtil.getCurrentTime("yyyy-MM-dd hh:ss"));
            }
        });



        UsbHelper.getInstance().setTransListener(new UsbHelper.LCUsbTransListener() {
            @Override
            public void showProgress(String s, String s1, long l, int postion) {

                            dataPer0.setText(l + "%");
                            pb0.setProgress((int) l);


            }

            @Override
            public void checkFinsh(String s, String s1, Boolean finsh) {

            }
        });


        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        apd = new Adapter(dataResList);
        rv.setAdapter(apd);

    }


    static int delCount = 0;

    void deleteUsb(){

        if(UsbHelper.getInstance().canCopyToU()){
            String migratePath = UsbHelper.getInstance().getUsbRootPath() + FileTool.MIGRATIONNAME; //+ "/" +  UsbHelper.getInstance().getUsbUUid();
            SafFile3 file = new SafFile3(this,migratePath);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pb.setProgress(5);
                    textDes.setText(getString(R.string.read_files_to_del));
                    dataDes.setText("");

                }
            });

            int allCount = FileTool.getAllFileInDir_(file).size();

            delCount = 0;

            while (file.exists()) {
                if (!file.exists()) {
                    //toast(get);

                    return;
                }
                try {
                    FileTool.deleteUsbFile_(file, new FileTool.Callback() {
                        @Override
                        public void callback(int i) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    delCount ++;
                                    pb.setProgress(delCount * 100 / allCount);
                                    textDes.setText(getString(R.string.deleting));
                                    dataDes.setText("(" + delCount + "/" + allCount +")");
                                    dataPer.setText((delCount * 100 / allCount) + "%");

                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent in = new Intent(Activity_migrate_statu.this,Activity_migrate_selector.class);
                in.putExtra("type","1");
                startActivity(in);
                finish();
            }
        });
    }

    void deleteLocal(){
        String migratePath = UsbHelper.getInstance().getSdRootPath() + FileTool.MIGRATIONNAME; //+ "/" +  UsbHelper.getInstance().getUsbUUid();
        SafFile3 file = new SafFile3(this,migratePath);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setProgress(5);
                textDes.setText(getString(R.string.read_files_to_del));
                dataDes.setText("");

            }
        });
        int allCount = FileTool.getAllFileInDir_(file).size();

        delCount = 0;
        while (file.exists()) {
            if (!file.exists()) {
                //toast(get);

                return;
            }
            try {
                FileTool.deleteUsbFile_(file, new FileTool.Callback() {
                    @Override
                    public void callback(int i) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                delCount ++;
                                pb.setProgress(delCount * 100 / allCount);
                                textDes.setText(getString(R.string.deleting));
                                dataDes.setText("(" + delCount + "/" + allCount +")");
                                dataPer.setText((delCount * 100 / allCount) + "%");

                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent in = new Intent(Activity_migrate_statu.this,Activity_migrate_selector.class);
                in.putExtra("type","2");
                startActivity(in);

                finish();
            }
        });
    }
    void getData_(){

        //String[] names = {getString(R.string.contact_backup_title),getString(R.string.file_pic),getString(R.string.file_video),getString(R.string.file_doc),getString(R.string.file_audio),getString(R.string.file_wx)};


        List allDatas = new ArrayList();
        allDatas.add(new Node_(getString(R.string.contact_backup_title),1,0,R.mipmap.ic_migrate_contact,"Contacts"));
        allDatas.add(new Node_(getString(R.string.file_pic),0,0,R.mipmap.ic_migrate_pic,"Pictures"));
        allDatas.add(new Node_(getString(R.string.file_video),0,0,R.mipmap.ic_migrate_video,"Videos"));
        allDatas.add(new Node_(getString(R.string.file_doc),0,0,R.mipmap.ic_migrate_doc,"Documents"));
        allDatas.add(new Node_(getString(R.string.file_audio),0,0,R.mipmap.ic_migrate_audio,"Audios"));
        allDatas.add(new Node_(getString(R.string.file_wx),0,0,R.mipmap.ic_migrate_wx,"Weixin"));
        List<String> types = new ArrayList();
        if(dataType.contains("1")){
            types.add("Contacts");

        }
        if(dataType.contains("2")){
            types.add("Pictures");

        }
        if(dataType.contains("3")){
            types.add("Videos");
        }
        if(dataType.contains("4")){
            types.add("Documents");

        }
        if(dataType.contains("5")){
            types.add("Audios");

        }
        if(dataType.contains("6")){
            types.add("Weixin");

        }

        dataList = new ArrayList();
        String migratePath = null;
        if(UsbHelper.getInstance().canCopyToU()){


            if(mtype.equals("2")){
                migratePath = UsbHelper.getInstance().getUsbRootPath() + FileTool.MIGRATIONNAME + "/" +  UsbHelper.getInstance().getUsbUUid();


                String sd_migratePath = UsbHelper.getInstance().getSdRootPath() + FileTool.MIGRATIONNAME + "/" +  UsbHelper.getInstance().getUsbUUid();
                SafFile3 sf = new SafFile3(this,migratePath);
                if(sf.exists()) {
                    List list = Arrays.asList(sf.listFiles());


                    for(Object o  : list){
                        SafFile3 s = (SafFile3) o;
                        if(s.exists()){

                            String desDir = sd_migratePath + "/" + s.getName();


                            if(types.contains(s.getName())) {
                                for (Object o1 : s.listFiles()) {
                                    SafFile3 f = (SafFile3) o1;

                                    Node node = new Node(desDir, s.getName(), f);
                                    dataList.add(node);
                                }

                                Node_ node_ = getNode_(allDatas,s.getName());
                                node_.all = s.listFiles().length;


                                dataResList.add(node_);
                                if (s.getName().equals("Contacts")) {

                                    if (s.listFiles().length > 0) {
                                        SafFile3 f = s.listFiles()[0];

                                        FileTool.readDataToSysContact(f, new FileTool.ProgressListener() {
                                            @Override
                                            public void onProgress(int var1) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        textDes.setText(R.string.contactSys);
                                                        pb.setProgress(var1);
                                                        dataPer.setText(var1 + "%");
                                                    }
                                                });
                                            }
                                        }, this);




                                    }
                                }
                            }
                        }
                    }

                }
            }




        }else {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    progressDialog.dismiss();
                    finish();
                }
            });
            return;
        }


        migrateing = true;
        for(Object o : dataList){

            if(cancelMigrate){
                migrateing = false;
                return;
            }
            Node node = (Node) o;
            int count = dataList.indexOf(o);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    progressDialog.setProgress(count * 100 / dataList.size());
//                    progressDialog.setMessage(node.type + ":" + node.safFile.getName());

                    pb.setProgress(count * 100 / dataList.size());
                    textDes.setText(node.type + ":" + node.safFile.getName() + " ("+ FileTool.getFileSize(node.safFile.length()) + ")");
                    dataDes.setText("(" + (count+1) + "/" + dataList.size() +")");
                    dataPer.setText((count * 100 / dataList.size()) + "%");

                    dataPer0.setText(0+ "%");
                    pb0.setProgress((int) 0);
                }
            });

            FileTool.copyWithPath(node.safFile.getPath(),node.des + "/" + node.safFile.getName());
            Node_ node_ = getNode(node.type);
            node_.index = node_.index + 1;


        }
        migrateing = false;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //
                migrateBut.setText(getString(R.string.migrate_done));


                TextView tv_res = findViewById(R.id.tv_res_des);

                TextView tv_time = findViewById(R.id.tv_res_time);

                tv_res.setText(getString(R.string.success));
                tv_time.setText(TimeUtil.getCurrentTime("yyyy-MM-dd hh:ss"));


                findViewById(R.id.ll_res).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_migratepro).setVisibility(View.GONE);
                apd.refresh(dataResList);
                //finish();
                //progressDialog.dismiss();
            }
        });
//        if(dataType.contains("1")){
//
//            String desDir = migratePath + "/Contacts";
//            try {
//                File file = FileTool.createContactFileAndToUsb(new SelectedItemCollection.ProgressListioner() {
//                    @Override
//                    public void progress(int progress) {
//
//                    }
//
//                    @Override
//                    public void finsh(List<String> filePath) {
//
//                    }
//                },this);
//
//                FileTool.copyWithPath(file.getAbsolutePath(),desDir + "/" + file.getName());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//
//        }
//        if(dataType.contains("2")){
//            String desDir = migratePath + "/Pictures";
//
//            ArrayList datas = (ArrayList) FileTool.getPic_(this);
//
//            for(Object o : datas){
//                File f = (File) o;
//                FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
//            }
//        }
//        if(dataType.contains("3")){
//
//            String desDir = migratePath + "/Videos";
//            ArrayList datas = (ArrayList) FileTool.getVideos_(this);
//        }
//        if(dataType.contains("4")){
//            String desDir = migratePath + "/Documents";
//            ArrayList datas = (ArrayList) FileTool.getDoc_(this);
//        }
//        if(dataType.contains("5")){
//            String desDir = migratePath + "/Audios";
//            ArrayList datas = (ArrayList) FileTool.getAudios_(this);
//        }
//        if(dataType.contains("6")){
//            String desDir = migratePath + "/Weixin";
//            ArrayList datas = (ArrayList) FileTool.getWeixi_(this);
//
//        }


    }

    void getData(){

        //String[] names = {getString(R.string.contact_backup_title),getString(R.string.file_pic),getString(R.string.file_video),getString(R.string.file_doc),getString(R.string.file_audio),getString(R.string.file_wx)};

        String migratePath = null;
        if(UsbHelper.getInstance().canCopyToU()){

            migratePath = UsbHelper.getInstance().getUsbRootPath() + FileTool.MIGRATIONNAME + "/" +  UsbHelper.getInstance().getUsbUUid();

        }else {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    progressDialog.dismiss();
                    finish();
                }
            });
            return;
        }

        dataList = new ArrayList();
        if(dataType.contains("1")){

            String desDir = migratePath + "/Contacts";
            try {
                File file = FileTool.createContactFileAndToUsb(new SelectedItemCollection.ProgressListioner() {
                    @Override
                    public void progress(int progress) {

                    }

                    @Override
                    public void finsh(List<String> filePath) {

                    }
                },this);


                dataList.add(new Node(desDir,"Contacts",file));
//                FileTool.copyWithPath(file.getAbsolutePath(),desDir + "/" + file.getName());


                dataResList.add(new Node_(getString(R.string.contact_backup_title),1,0,R.mipmap.ic_migrate_contact,"Contacts"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(dataType.contains("2")){
            String desDir = migratePath + "/Pictures";

            ArrayList datas = (ArrayList) FileTool.getPic_(this);

            for(Object o : datas){
                File f = (File) o;

                dataList.add(new Node(desDir,"Pictures",f));
                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
            }
            dataResList.add(new Node_(getString(R.string.file_pic),datas.size(),0,R.mipmap.ic_migrate_pic,"Pictures"));
        }
        if(dataType.contains("3")){

            String desDir = migratePath + "/Videos";
            ArrayList datas = (ArrayList) FileTool.getVideos_(this);
            for(Object o : datas){
                File f = (File) o;

                dataList.add(new Node(desDir,"Videos",f));
                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
            }

            dataResList.add(new Node_(getString(R.string.file_video),datas.size(),0,R.mipmap.ic_migrate_video,"Videos"));
        }
        if(dataType.contains("4")){
            String desDir = migratePath + "/Documents";
            ArrayList datas = (ArrayList) FileTool.getDoc_(this);
            for(Object o : datas){
                File f = (File) o;
                dataList.add(new Node(desDir,"Documents",f));
                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
            }

            dataResList.add(new Node_(getString(R.string.file_doc),datas.size(),0,R.mipmap.ic_migrate_doc,"Documents"));
        }
        if(dataType.contains("5")){
            String desDir = migratePath + "/Audios";
            ArrayList datas = (ArrayList) FileTool.getAudios_(this);
            for(Object o : datas){
                File f = (File) o;
                dataList.add(new Node(desDir,"Audios",f));
                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
            }

            dataResList.add(new Node_(getString(R.string.file_audio),datas.size(),0,R.mipmap.ic_migrate_audio,"Audios"));
        }
        if(dataType.contains("6")){
            String desDir = migratePath + "/Weixin";
            ArrayList datas = (ArrayList) FileTool.getWeixi_(this);
            for(Object o : datas){
                File f = (File) o;
                dataList.add(new Node(desDir,"Weixin",f));
                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
            }
            dataResList.add(new Node_(getString(R.string.file_wx),datas.size(),0,R.mipmap.ic_migrate_wx,"Weixin"));

        }



        migrateing = true;

        reCopyCount = 0;
        for(Object o : dataList){
            if(cancelMigrate){
                migrateing = false;
                return;
            }
            Node node = (Node) o;
            int count = dataList.indexOf(o);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    progressDialog.setProgress(count * 100 / dataList.size());
//                    progressDialog.setMessage(node.type + ":" + node.file.getName());

                    pb.setProgress(count * 100 / dataList.size());
                    textDes.setText(node.type + ":" + node.file.getName() + " ("+ FileTool.getFileSize(node.file.length()) + ")");
                    dataDes.setText("(" + (count + 1) + "/" + dataList.size() +")");
                    dataPer.setText((count * 100 / dataList.size()) + "%");
                    dataPer0.setText(0+ "%");
                    pb0.setProgress((int) 0);



                }
            });
            SafFile3 to = new SafFile3(Activity_migrate_statu.this,node.des + "/" + node.file.getName());
            if(to != null){
                if(to.exists()){
                    reCopyCount = reCopyCount + 1;

                    reCopyFilePaths.add(node.file.getAbsolutePath());
                }
            }
            FileTool.copyWithPath(node.file.getAbsolutePath(),node.des + "/" + node.file.getName());

            Node_ node_ = getNode(node.type);
            node_.index = node_.index + 1;
        }

        migrateing = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //progressDialog.dismiss();

                migrateBut.setText(getString(R.string.migrate_done));


                TextView tv_res = findViewById(R.id.tv_res_des);

                TextView tv_time = findViewById(R.id.tv_res_time);

                tv_res.setText(getString(R.string.success)+":"+  (dataList.size() - reCopyCount) + "  "+ getString(R.string.migrate_Duplicate_file)+":"+  (reCopyCount));
                tv_time.setText(TimeUtil.getCurrentTime("yyyy-MM-dd hh:ss"));


                findViewById(R.id.ll_res).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_migratepro).setVisibility(View.GONE);
                apd.refresh(dataResList);
                //textDes.setText("");

                findViewById(R.id.tv_see).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity_dupfiles.tosee(Activity_migrate_statu.this,reCopyFilePaths);
                    }
                });




            }
        });
//        if(dataType.contains("1")){
//
//            String desDir = migratePath + "/Contacts";
//            try {
//                File file = FileTool.createContactFileAndToUsb(new SelectedItemCollection.ProgressListioner() {
//                    @Override
//                    public void progress(int progress) {
//
//                    }
//
//                    @Override
//                    public void finsh(List<String> filePath) {
//
//                    }
//                },this);
//
//                FileTool.copyWithPath(file.getAbsolutePath(),desDir + "/" + file.getName());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//
//        }
//        if(dataType.contains("2")){
//            String desDir = migratePath + "/Pictures";
//
//            ArrayList datas = (ArrayList) FileTool.getPic_(this);
//
//            for(Object o : datas){
//                File f = (File) o;
//                FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
//            }
//        }
//        if(dataType.contains("3")){
//
//            String desDir = migratePath + "/Videos";
//            ArrayList datas = (ArrayList) FileTool.getVideos_(this);
//        }
//        if(dataType.contains("4")){
//            String desDir = migratePath + "/Documents";
//            ArrayList datas = (ArrayList) FileTool.getDoc_(this);
//        }
//        if(dataType.contains("5")){
//            String desDir = migratePath + "/Audios";
//            ArrayList datas = (ArrayList) FileTool.getAudios_(this);
//        }
//        if(dataType.contains("6")){
//            String desDir = migratePath + "/Weixin";
//            ArrayList datas = (ArrayList) FileTool.getWeixi_(this);
//
//        }


    }


    public class Node{
        public String des;

        public Node(String des, String type, File file) {
            this.des = des;
            this.type = type;
            this.file = file;
        }

        public String type;

        public Node(String des, String type, SafFile3 safFile) {
            this.des = des;
            this.type = type;
            this.safFile = safFile;
        }

        public File file;
        public SafFile3 safFile;
    }



    Node_ getNode_(List datas, String type){

        for(Object o : datas){
            Node_ node = (Node_) o;
            if(node.type.equals(type)){
                return node;
            }
        }
        return null;
    }


    Node_ getNode(String type){

        for(Object o : dataResList){
            Node_ node = (Node_) o;
            if(node.type.equals(type)){
                return node;
            }
        }
        return null;
    }
    public class Node_{
        public String title = "";
        public String type = "";

        public Node_(String title, int all, int index, int resId,String type) {
            this.title = title;
            this.all = all;
            this.index = index;
            this.resId = resId;
            this.type = type;
        }

        public int all;
        public int index;

        public int resId;
        public int resType;

    }


    public class Adapter extends RecyclerView.Adapter{


        private List<Node_> mItems;
        public Adapter(List<Node_> items) {
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

            return new Adapter.ViewHolder(v);
        }

        @SuppressLint("RecyclerView")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mholder, int position) {

            ViewHolder holder = (ViewHolder) mholder;
            Node_ fileBean = mItems.get(position);

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