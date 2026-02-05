package com.sentaroh.android.upantool;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
//import com.sentaroh.android.Utilities.SafFile;
import com.sentaroh.android.upantool.sysTask.TastTool;
import com.zhihu.matisse.Config;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.ui.widget.MediaGrid;
import com.zhihu.matisse.internal.ui.widget.MediaGridInset;
import com.zhihu.matisse.internal.utils.UIUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sentaroh.android.upantool.R;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_wx_nine#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_wx_nine extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private ArrayList item;
    private RvAdapter adp;

    public Fragment_wx_nine() {
        // Required empty public constructor
    }

    public View.OnDragListener getListener() {
        return mlistener;
    }

    public void setListener(View.OnDragListener listener) {
        mlistener = listener;
    }

    private View.OnDragListener mlistener;

    private List mSortList = new ArrayList();
    private List mSelectList = new ArrayList();


    public GridView getGv() {
        return gv;
    }

    public void setGv(GridView gv) {
        this.gv = gv;
    }

    private GridView gv;
    private BaseAdapter mAdapter;

    public Boolean getMedit() {
        return medit;
    }

    public void setMedit(Boolean medit) {
        this.medit = medit;
        if(adp != null) {
            adp.refreshEdit(medit);
        }
    }

    private Boolean medit = false;

    public List getMlist() {
        return mlist;
    }

    public void setMlist(List mlist) {
        this.mlist = mlist;
        if(adp != null) {
            adp.refresh(mlist);
        }

    }

    public List getSlist() {
        return slist;
    }

    public void setSlist(List slist) {
        this.slist = slist;
        if(adp != null) {
           // adp.notifyDataSetChanged();
            adp.refreshUserCheckStatu(slist);
        }
    }

    private ItemClickListener mItemClickListener;


    public interface ItemClickListener {
        void onItemClick(int position);
        void onItemCheckClick(int position);
        public void onItemLongClick(boolean edit);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;

    }


    private List mlist = new ArrayList();
    private List slist = new ArrayList();
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_wx_nine.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_wx_nine newInstance(String param1, String param2) {
        Fragment_wx_nine fragment = new Fragment_wx_nine();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wx_nine, container, false);


        adp = new RvAdapter((ArrayList) mlist);
        adp.ischeck = medit;

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);


        mRecyclerView.setHasFixedSize(true);

        int spanCount;
        SelectionSpec selectionSpec = SelectionSpec.getInstance();
        selectionSpec.gridExpectedSize =
                getResources().getDimensionPixelSize(R.dimen.grid_expected_size);
        if (selectionSpec.gridExpectedSize > 0) {
            spanCount = UIUtils.spanCount(getContext(), selectionSpec.gridExpectedSize);
        } else {
            spanCount = selectionSpec.spanCount;
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        mRecyclerView.addItemDecoration(new MediaGridInset(spanCount, spacing, false));

        mRecyclerView.setAdapter(adp);

//        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //Toast.makeText(getContext(), "你点击了~" + position + "~项", Toast.LENGTH_SHORT).show();
//                mItemClickListener.onItemClick(position);
//            }
//        });


        adp.refreshUserCheckStatu(slist);

        return view;
    }


    public class RvAdapter extends RecyclerView.Adapter{

        ArrayList<File> mItems = new ArrayList<File>();
        ArrayList<File> smItems = new ArrayList<File>();

        public boolean ischeck = false;
        public RvAdapter(ArrayList list){
            mItems = list;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_wx_grid, parent, false);
           // return MediaViewHolder(v);

            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            VH vh = (VH) holder;

            File f = mItems.get(position);

            ImageView IV = (ImageView) ((VH) holder).img_icon;
            //IV.setImageURI(Uri.parse(f.getAbsolutePath()));
            Glide.with(getContext()).load(f).into(IV);


            ((VH) holder).img_check.setImageResource(R.mipmap.ic_fm_list_item_uncheck);

            ((VH) holder).img_check.setVisibility(View.GONE);

            if(ischeck){
                ((VH) holder).img_check.setVisibility(View.VISIBLE);
            }
            if(FileTool.isVideo(f.getName())){

                ((VH) holder).img_play.setVisibility(View.VISIBLE);
            }

            if (smItems != null) {
                for (int i = 0; i < smItems.size(); i++) {
                    File info1 = (File) smItems.get(i);
                    if (info1.getPath().equals(f.getPath()) && info1.getName().equals(f.getName())) {
                        ((VH) holder).img_check.setImageResource(R.mipmap.ic_fm_list_item_check);

                        break;
                    }
                }
            }

//
//
            ((VH) holder).img_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemCheckClick(mlist.indexOf(f));
                }
            });
            ((VH) holder).img_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(mlist.indexOf(f));
                }
            });

            ((VH) holder).img_icon.setOnLongClickListener (new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    if(Config.ImagePick){
                        return true;
                    }
                    ischeck = !ischeck;
                    UsbHelper.getInstance().wxSelectEnable = ischeck;
                    notifyDataSetChanged();

                    mItemClickListener.onItemLongClick(ischeck);

                    return true;
                }

            });

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Class clazz = null;  //使用反射获取实例
//                    Method getInstance = null;
//                    Method fileIsSysed = null;
//
//                    try {
                        boolean o = TastTool.getInstance().fileIsSysed(f.getAbsolutePath());
//                        ((Activity)getContext()).runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
                                if(o ){
                                    ((VH) holder).tv_sys_flag.setVisibility(View.VISIBLE);
                                }else{
                                    ((VH) holder).tv_sys_flag.setVisibility(View.GONE);
                                }
//                            }
//                        });
//                    } catch (Exception ex) {
//
//                    }
//
//
//                }
//            }).start();


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
        public  ImageView img_check;
        public  ImageView img_play;
        public TextView tv_sys_flag;

        VH(View v) {
            super(v);
             img_icon = v.findViewById(R.id.img_icon);
             img_check = v.findViewById(R.id.img_check);
             img_play = v.findViewById(R.id.img_play);
            tv_sys_flag = v.findViewById(R.id.tv_sysed_flag);
        }
    }



}