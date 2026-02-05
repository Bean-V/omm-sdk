package com.sentaroh.android.upantool;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sentaroh.android.upantool.R;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.record.Record;
import com.sentaroh.android.upantool.record.RecordDao;
import com.sentaroh.android.upantool.record.RecordDatabase;
import com.sentaroh.android.upantool.record.RecordTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_ft#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_ft extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int mColumnCount = 1;

    private AdapterFMList apd;

    private RecyclerView rv;

    private int mType = 0;
    private List<TransFile> mList;


    public Fragment_ft(int type) {

        mType = type;
        // Required empty public constructor
    }

    public Fragment_ft() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_ft.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_ft newInstance(String param1, String param2) {
        Fragment_ft fragment = new Fragment_ft();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_ft, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            rv = (RecyclerView) view;
            if (mColumnCount <= 1) {
                rv.setLayoutManager(new LinearLayoutManager(context));
            } else {
                rv.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


            mList = new ArrayList();
            mList.addAll(mType == 0 ? TransFileManager.getInstance().getTransUnDoneList() : TransFileManager.getInstance().getTransDoneList());
            apd = new AdapterFMList(mList);



            rv.setAdapter(apd);

//           apd.setOnItemClickListener(new AdapterFMList.ItemClickListener() {
//               @Override
//               public void onItemClick(int position) {
//
//               }
//
//               @Override
//               public void onItemCheckClick(int position) {
//
//               }
//
//               @Override
//               public void onItemLongClick(boolean edit) {
//
//               }
//           });

            TransFileManager.getInstance().addStatuChangeListener(new StatuChangeListener() {
                @Override
                public void onStatuChange() {


//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {

//
                            List datas1 = mType == 0 ? TransFileManager.getInstance().getTransUnDoneList() : TransFileManager.getInstance().getTransDoneList();

                            List datas = new ArrayList();
                            datas.addAll(datas1);
                            mList = datas;
                            if(!isAdded()){
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    apd.refresh(datas);
                                    Log.d("lc", "run: " + datas.size());
                                }
                            });

                        }
 //                   }).start();


  //              }
            });

            if(mType == 0) {
                UsbHelper.getInstance().setTransListener(new UsbHelper.LCUsbTransListener() {
                    @Override
                    public void showProgress(String s, String s1, long l, int postion) {

//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {





                        for (TransFile tf : mList) {
                            if (tf.getPath().equals(s)) {
                                if(tf.getStatu() == 1) {
                                    tf.setStatuDes("正在复制" + l + "%");
                                    tf.setProgress((int) l);
                                }
                                break;
                            }
                        }
                        if (!isAdded()) {
                            return;
                        }

                        //                               getActivity().runOnUiThread(new Runnable() {
                        //                                  @Override
                        //                                  public void run() {
                        apd.refresh(mList);
                        //                                  }
                        //                               });
//
                        //}
                        //                       }).start();


                    }

                    @Override
                    public void checkFinsh(String s, String s1, Boolean finsh) {

                        if (!finsh) {


//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {

                           // List<TransFile> datas = TransFileManager.getInstance().getTransList();

                            if(mType == 0) {

                                for (TransFile tf : mList) {
                                    if (tf.getPath().equals(s)) {
                                        tf.setStatuDes("复制失败");
                                        tf.setStatu(3);

//                                    datas.remove(tf);
//
//                                    TransFileManager.getInstance().getTransList().remove(tf);
//                                    TransFileManager.getInstance().addTransFile(tf);
                                        break;
                                    }
                                }
                            }

                            //datas = (mType == 0 ? TransFileManager.getInstance().getTransUnDoneList() : TransFileManager.getInstance().getTransDoneList());

                            if (!isAdded()) {
                                return;
                            }
                           // List<TransFile> finalDatas = datas;
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
                            apd.refresh(mList);
                        }else{


                            if(mType == 0) {


                                TransFile tmp = null;
                                for (TransFile tf : mList) {
                                    if (tf.getPath().equals(s)) {
                                        tf.setStatuDes("复制成功");
                                        tf.setStatu(2);

                                        tmp = tf;

//                                    datas.remove(tf);
//
//                                    TransFileManager.getInstance().getTransList().remove(tf);
//                                    TransFileManager.getInstance().addTransFile(tf);
                                        break;
                                    }
                                }

                                if(tmp != null){
                                    mList.remove(tmp);
                                }
                                apd.refresh(mList);

                            }else{
                                mList = TransFileManager.getInstance().getTransDoneList();


                                apd.refresh(mList);
                            }

                        }
//                                    });
//
                    }
                    //                           }).start();


                });
            }


        }
        return view;
    }


    public static class AdapterFMList extends RecyclerView.Adapter {

        private static final String TAG = "AdapterFMList";
        // private final List<BeanFile> mValues;
        private Context mContext;

        public List<TransFile> getmItems() {
            return mItems;
        }

        public void setmItems(List<TransFile> mItems) {
            this.mItems = mItems;
        }

        private List<TransFile> mItems;
        private List sList;

        private boolean ischeck = false;

        public void setSelectUsers(List selectUsers) {
            this.selectUsers = selectUsers;
        }

        private List selectUsers;

        public Boolean longTapToDrag = false;

        public Boolean getLongTapToDrag() {
            return longTapToDrag;
        }

        public void setLongTapToDrag(Boolean longTapToDrag) {
            this.longTapToDrag = longTapToDrag;
        }

        public AdapterFMList(List<TransFile> items) {
            super();
            //super();
            mItems = items;
        }



        private ItemClickListener mItemClickListener;

        public interface ItemClickListener {
            void onItemClick(int position);

            void onItemCheckClick(int position);

            void onItemLongClick(boolean edit);
        }

        public void setOnItemClickListener(ItemClickListener listener) {
            this.mItemClickListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_file_transfer, parent, false);//item_fragment_filelist

            return new ViewHolder(v);

        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mholder, int position) {

            ViewHolder holder = (ViewHolder) mholder;
            TransFile fileBean = mItems.get(position);
            holder.iv_icon.setImageResource(mItems.get(position).getTypeIcon());
            holder.tv_name.setText(mItems.get(position).getName());
            holder.tv_size.setText(mItems.get(position).getSize());

            holder.tv_statu.setText(mItems.get(position).statuDes);
            holder.tv_statu.setTextColor(R.color.colorZT66);
            holder.tv_cancel.setVisibility(View.GONE);
            holder.pg_bar.setProgress(0);
            if(mItems.get(position).statu == 1){
                holder.tv_statu.setTextColor(Color.parseColor("#0DE423"));
                holder.tv_cancel.setVisibility(View.GONE);
                holder.pg_bar.setProgress(mItems.get(position).getProgress());
            }else if(mItems.get(position).statu == 2){
                holder.tv_statu.setTextColor(Color.parseColor("#1156A6"));
                holder.tv_cancel.setVisibility(View.GONE);
            }else if(mItems.get(position).statu == 3){
                holder.tv_statu.setTextColor(Color.parseColor("#ff0000"));
                holder.tv_cancel.setVisibility(View.VISIBLE);
                holder.tv_cancel.setText("重试");
            }

            holder.tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TransFileManager.getInstance().addTransFile(fileBean);
                }
            });

            holder.itemView.setTag(mItems.get(position));
            //holder.iv_checkIcon.setImageResource(R.mipmap.ic_fm_list_item_uncheck);
            //holder.iv_checkIcon.setVisibility(View.GONE);

//
//            if (fileBean.getObj() != null) {
//                Object o = fileBean.getObj();
//                if (o instanceof File) {
//                    File fi = (File) o;
//
//                    if (fi.isDirectory()) {
//                        if (!fi.canWrite() || !fi.canRead()) {
//                            holder.tv_info.setText("无权限");
//                        } else {
//                            holder.tv_info.setText(fi.listFiles().length + " 项");
//                        }
//                    } else {
//
//                        holder.tv_info.setText(TimeUtil.formatMsecToString(fi.lastModified(), "yyyy/MM/dd hh:mm:ss") + " " + mItems.get(position).getSize());
//                    }
//
//                }
//                if (o instanceof SafFile3) {
//                    SafFile3 fi = (SafFile3) o;
//
//                    if (fi.isDirectory()) {
//                        if (!fi.canWrite() || !fi.canRead()) {
//                            holder.tv_info.setText("无权限");
//                        } else {
//                            holder.tv_info.setText(fi.listFiles().length + " 项");
//                        }
//                    } else {
//
//                        holder.tv_info.setText(TimeUtil.formatMsecToString(fi.lastModified(), "yyyy/MM/dd hh:mm:ss") + " " + mItems.get(position).getSize());
//                    }
//
//                }
//            }


            if (ischeck) {
               // holder.iv_checkIcon.setVisibility(View.VISIBLE);
            }

            if (sList != null) {
                for (int i = 0; i < sList.size(); i++) {
                    BeanFile info1 = (BeanFile) sList.get(i);
                    if (info1.getPath().equals(mItems.get(position).getPath()) && info1.getName().equals(mItems.get(position).getName())) {
                        //holder.iv_checkIcon.setImageResource(R.mipmap.ic_fm_list_item_check);

                        break;
                    }
                }
            }

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mItemClickListener.onItemClick(position);
//                }
//            });
//
//            holder.tv_cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mItemClickListener.onItemCheckClick(position);
//                }
//            });



        }



        @Override
        public int getItemCount() {
            return mItems.size();
        }



        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView iv_icon;
            public TextView tv_name;
            //public  TextView tv_size;
            public ImageView iv_checkIcon;
            public TextView tv_size;
            public TextView tv_statu;
            public TextView tv_cancel;
            public ProgressBar pg_bar;

            public ViewHolder(View v) {
                super(v);
                iv_icon = v.findViewById(R.id.iv_fm_list_item_icon);
                tv_name = v.findViewById(R.id.iv_fm_list_item_name);
                // tv_size = v.findViewById(R.id.iv_fm_list_item_filesize);
                iv_checkIcon = v.findViewById(R.id.iv_fm_list_item_checkicon);

                tv_size = v.findViewById(R.id.tv_ft_list_size);
                tv_statu = v.findViewById(R.id.tv_ft_list_statu);
                tv_cancel = v.findViewById(R.id.tv_ft_list_cancel);
                pg_bar = v.findViewById(R.id.pg_bar);
            }

        }

        public void refresh(List list) {
            mItems = list;
             notifyDataSetChanged();
        }

        public void refreshUserCheckStatu(List users) {
            sList = users;
            notifyDataSetChanged();
        }

        public void refreshEdit(boolean edit) {

            if (ischeck != edit) {
                ischeck = edit;
                notifyDataSetChanged();
            }
        }

    }




    public static class TransFile {

        private String name;

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

        private int resId;

        public TransFile() {

        }
        public TransFile(String name, String path, String size, String type, int typeIcon) {
            this.name = name;
            this.path = path;
            this.size = size;
            this.type = type;
            this.typeIcon = typeIcon;
        }

        public TransFile(int iId,String name, String path, String size, String type, int typeIcon) {
            this.resId = iId;
            this.name = name;
            this.path = path;
            this.size = size;
            this.type = type;
            this.typeIcon = typeIcon;
        }

        public TransFile(String name, String path, String size, String type, int typeIcon,Object obj) {
            this.name = name;
            this.path = path;
            this.size = size;
            this.type = type;
            this.typeIcon = typeIcon;
            this.obj = obj;
        }
        private String path;
        private String size;
        private String type;

        public Object getObj() {
            return obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }

        private Object obj;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTypeIcon() {
            return typeIcon;
        }

        public void setTypeIcon(int typeIcon) {
            this.typeIcon = typeIcon;
        }

        private int typeIcon;

        public int getPostion() {
            return postion;
        }

        public void setPostion(int postion) {
            this.postion = postion;
        }

        private int postion = 0;

        private int statu;//0,1,2

        private String statuDes;

        private int copyDes;//0,1

        public Boolean getDeleteWhenFinsh() {
            return deleteWhenFinsh;
        }

        public void setDeleteWhenFinsh(Boolean deleteWhenFinsh) {
            this.deleteWhenFinsh = deleteWhenFinsh;
        }

        private Boolean deleteWhenFinsh = false;//0,1

        private String copyDes_des;

        public int getStatu() {
            return statu;
        }

        public void setStatu(int statu) {
            this.statu = statu;
        }

        public String getStatuDes() {
            return statuDes;
        }

        public void setStatuDes(String statuDes) {
            this.statuDes = statuDes;
        }

        public int getCopyDes() {
            return copyDes;
        }

        public void setCopyDes(int copyDes) {
            this.copyDes = copyDes;
        }

        public String getCopyDes_des() {
            return copyDes_des;
        }

        public void setCopyDes_des(String copyDes_des) {
            this.copyDes_des = copyDes_des;
        }

        public Object getFileObj() {
            return fileObj;
        }

        public void setFileObj(Object fileObj) {
            this.fileObj = fileObj;
        }

        private Object fileObj;

        public Object getToFileObj() {
            return toFileObj;
        }



        public void setToFileObj(Object toFileObj) {
            this.toFileObj = toFileObj;
        }

        private Object toFileObj;

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        private int progress = 0;


        public String getToDirPath() {
            return toDirPath;
        }

        public void setToDirPath(String toDirPath) {
            this.toDirPath = toDirPath;
        }

        private String toDirPath;


    }


    public interface StatuChangeListener {

        void onStatuChange();
    }


}