package com.sentaroh.android.upantool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sentaroh.android.Utilities3.SafFile3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activity_migrate_selector extends BaseActivity {

    private RecyclerView rv;
    private List mlist = new ArrayList();
    private List slist = new ArrayList();
    private List mtlist = new ArrayList();
    private List stlist = new ArrayList();
    private Adapter apd;
    private String mtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migrate_selector);

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
                finish();
                //UsbHelper.getInstance().toStopTrans(false);
            }
        });


        initData();
        initView();


    }


    private void initData(){

        Intent in = getIntent();
        mtype = in.getStringExtra("type");

        List allDatas = new ArrayList();

        String[] names = {getString(R.string.contact_backup_title), getString(R.string.file_pic), getString(R.string.file_video), getString(R.string.file_doc), getString(R.string.file_audio), getString(R.string.file_wx)};
        //mlist.addAll(Arrays.asList(names));
        String[] types = {"1", "2", "3", "4", "5", "6"};

        String[] typeDes = {"Contacts", "Pictures", "Videos", "Documents", "Audios", "Weixin"};
        int[] icons = {R.mipmap.ic_migrate_contact, R.mipmap.ic_migrate_pic, R.mipmap.ic_migrate_video, R.mipmap.ic_migrate_doc, R.mipmap.ic_migrate_audio, R.mipmap.ic_migrate_wx};
        int i = 0;
        for(String s : names){
            allDatas.add(new Node(s,types[i],typeDes[i],icons[i]));
            i ++ ;
        }
        if(mtype.equals("1")) {
            mlist.addAll(allDatas);
        }else{

            String migratePath = UsbHelper.getInstance().getUsbRootPath() + FileTool.MIGRATIONNAME + "/" +  UsbHelper.getInstance().getUsbUUid();
            SafFile3 sf = new SafFile3(this,migratePath);
            if(sf.exists()) {
                List list = Arrays.asList(sf.listFiles());


                for(Object o  : list){
                    SafFile3 s = (SafFile3) o;
                    if(s.exists()){

                        for(Object o1 : allDatas){
                            Node node = (Node) o1;
                            if(node.typeDes.equals(s.getName())){
                                mlist.add(node);
                            }
                        }

                    }
                }

            }

        }

    }

    private void initView(){
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        apd = new Adapter(mlist);
        rv.setAdapter(apd);
        apd.setItemClikListener(new Fragment_contact_files.ItemClikListener() {
            @Override
            public void itemClikListener(int postion) {
                Node node = (Node) mlist.get(postion);

                if(slist.contains(node)){
                    slist.remove(mlist.get(postion));
                }else{
                    slist.add(mlist.get(postion));
                }

                apd.refreshUserCheckStatu(slist);
            }
        });

        findViewById(R.id.btn_migrate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(slist.size() == 0){
                    toast(getString(R.string.unselect_file));
                    return;
                }
                Intent in = new Intent(Activity_migrate_selector.this,Activity_migrate_statu.class);

                String types = "";
                for(Object o : slist){
                    Node s = (Node) o;
                    if(types.equals("")){
                        types = s.type;
                    }else{
                        types = types + "," + s.type;
                    }
                }
                in.putExtra("dataList",types);

                in.putExtra("type",mtype);
                startActivity(in);
            }
        });

        if(mtype.equals("2")){
            findViewById(R.id.ll_type1).setVisibility(View.GONE);
            findViewById(R.id.ll_type2).setVisibility(View.VISIBLE);

            TextView btn = findViewById(R.id.btn_migrate);
            btn.setText(getString(R.string.migrate_recovery));
        }

    }



    public class Adapter extends RecyclerView.Adapter{


        private List<Node> mItems;
        private List<Node> sItems = new ArrayList<>();
        public Adapter(List<Node> items) {
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activty_migrate_selector,parent, false);//item_fragment_filelist

            return new Adapter.ViewHolder(v);
        }

        @SuppressLint("RecyclerView")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mholder, int position) {

            ViewHolder holder = (ViewHolder) mholder;
            Node  fileBean = mItems.get(position);

            holder.tv_name.setText(mItems.get(position).title);
            holder.iv_icon.setImageResource(fileBean.resId);

            if(sItems.contains(mItems.get(position))){
                holder.iv_selector.setImageResource(R.mipmap.ic_fm_list_item_check);
            }else{
                holder.iv_selector.setImageResource(R.mipmap.ic_fm_list_item_uncheck);
            }

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClikListener != null) {
                        itemClikListener.itemClikListener(position);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            int a = 5;
            int b =6;
            return mItems.size();
        }




        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_name;
            public ImageView iv_selector;
            public ImageView iv_icon;
            public View view;

            public ViewHolder(View v) {
                super(v);
                tv_name = v.findViewById(R.id.tv_name);
                iv_selector = v.findViewById(R.id.iv_selector);
                iv_icon = v.findViewById(R.id.iv_icon);
                view = v;

            }
        }

        public void refresh(List list) {
            mItems = list;
            notifyDataSetChanged();
        }

        public void refreshUserCheckStatu(List users) {
            sItems = users;
            notifyDataSetChanged();
        }



    }


    public class Node{
        public String title;

        public Node(String title, String type, String typeDes, int resId) {
            this.title = title;
            this.type = type;
            this.resId = resId;
            this.typeDes = typeDes;
        }

        public String type;
        public String typeDes;
        public int resId;
    }
}