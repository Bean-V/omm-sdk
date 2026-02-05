package com.sentaroh.android.upantool;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

//import androidx.fragment.app.Fragment;

//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.Utilities3.SafFile3;
//import com.sentaroh.android.Utilities.SafFile;
//import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import me.jahnen.libaums.core.fs.UsbFile;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_file_grid#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_file_grid extends Fragment implements View.OnLongClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public boolean isIsedit() {
        return isedit;
    }

    public void setIsedit(boolean isedit) {
        this.isedit = isedit;
    }

    boolean isedit = false;


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

    public List getMlist() {
        return mlist;
    }

    public void setMlist(List mlist) {
        this.mlist = mlist;
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

    }

    public List getSlist() {
        return slist;
    }

    public void setSlist(List slist) {
        this.slist = slist;
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private ItemClickListener mItemClickListener;


    public interface ItemClickListener {
        void onItemClick(int position);
        void onItemCheckClick(int position);
        void onItemLongClick(boolean edit);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;

    }


    private List mlist = new ArrayList();
    private List slist = new ArrayList();


//    private ImageLoader imageLoader;//图片加载器
//    private DisplayImageOptions options;


    public Fragment_file_grid() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_file_grid.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_file_grid newInstance(String param1, String param2) {
        Fragment_file_grid fragment = new Fragment_file_grid();
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
        View v = inflater.inflate(R.layout.fragment_file_grid, container, false);
        gv = v.findViewById(R.id.gv_file);

////new BeanFile("学你为iv诶我v热v哦哦i诶.zip","","1234KB","zip",R.mipmap.ic_fm_list_item_zip)
//        List mData = new ArrayList<FragmentHome.FileSrcItem>();
//        mData.add(new BeanFile("学你为iv诶我v哦i诶.zip","","1234KB","zip",R.mipmap.ic_fm_item_b_zip));
//        mData.add(new BeanFile("学你为iv诶.mp4","","1234KB","mp4",R.mipmap.ic_fm_item_b_video));
//        mData.add(new BeanFile("学你为iv哦哦i诶.png","","1234KB","png",R.mipmap.ic_fm_item_b_pic));
//        mData.add(new BeanFile("学你为iv我哦哦i诶.zip","","1234KB","zip",R.mipmap.ic_fm_item_b_zip));
//        mData.add(new BeanFile("学你为iv哦哦i诶.png","","1234KB","png",R.mipmap.ic_fm_item_b_pic));
//        mData.add(new BeanFile("学你为iv我哦哦i诶.zip","","1234KB","zip",R.mipmap.ic_fm_item_b_zip));

        mAdapter = new MyAdapter<BeanFile>((ArrayList<BeanFile>) mlist, R.layout.item_fragment_files_grid) {


            @SuppressLint("MissingInflatedId")
            @Override
            public void bindView(ViewHolder holder, BeanFile obj) {
                //View v = LayoutInflater.from(gv.getContext()).inflate(R.layout.item_fragment_filelist,gv,false);
                holder.setImageResource(R.id.img_icon, obj.getTypeIcon());
                holder.setText(R.id.tv_name, obj.getName());
                holder.setText(R.id.tv_count, obj.getSize());
                holder.setImageResource(R.id.img_check,R.mipmap.ic_fm_list_item_uncheck);
                holder.setImageResource(R.id.img_play,R.mipmap.ic_fm_item_play);

                holder.obj = obj;

                Object o = obj.getObj();
                holder.getView(R.id.tv_name).setVisibility(View.VISIBLE);
                holder.getView(R.id.tv_count).setVisibility(View.VISIBLE);
                holder.getView(R.id.img_check).setVisibility(View.GONE);

                if(isedit){
                    holder.getView(R.id.img_check).setVisibility(View.VISIBLE);
                }
                if(o == null){
                    holder.getView(R.id.img_check).setVisibility(View.GONE);
                }
                ViewGroup.LayoutParams lp = holder.getView(R.id.card_icon).getLayoutParams();
                lp.height = ViewTool.dp2px(getContext(),68);

                if(o instanceof File){
                    File f = (File) o;
                    if(f.getName().toLowerCase().endsWith("jpg") || f.getName().toLowerCase().endsWith("png")){

                        ImageView IV = (ImageView) holder.getView(R.id.img_icon);
                        //IV.setImageURI(Uri.parse(f.getAbsolutePath()));

                        Glide.with(getContext()).load(f).into(IV);
//                        holder.setText(R.id.tv_name, "");
//                        holder.setText(R.id.tv_count, "");

                        holder.getView(R.id.tv_name).setVisibility(View.GONE);
                        holder.getView(R.id.tv_count).setVisibility(View.GONE);

//                        bitmapUtils.configDefaultShowOriginal(false);
//                        bitmapUtils.configDefaultBitmapMaxSize(tempWidth, tempWidth);

                        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;//ViewTool.dp2px(getContext(),0);
                    }
                }
                if(o instanceof SafFile3){
                    SafFile3 f = (SafFile3) o;
                    if(f.getName().endsWith("jpg") || f.getName().endsWith("png")){
                        ImageView IV = (ImageView) holder.getView(R.id.img_icon);
                        //Glide.with(getContext()).load(f).into(IV);
                        //IV.setImageURI(Uri.parse(f.getAbsolutePath()));
                        //holder.setText(R.id.tv_name, "");
                        //holder.setText(R.id.tv_count, "");
                        holder.getView(R.id.tv_name).setVisibility(View.GONE);
                        holder.getView(R.id.tv_count).setVisibility(View.GONE);
                        lp.height =  ViewGroup.LayoutParams.MATCH_PARENT;//ViewTool.dp2px(getContext(),0);
                    }
                }

                holder.getView(R.id.card_icon).setLayoutParams(lp);

                if (slist != null) {
                    for (int i = 0; i < slist.size(); i++) {
                        BeanFile info1 = (BeanFile) slist.get(i);
                        if (info1.getPath().equals(obj.getPath()) && info1.getName().equals(obj.getName())) {
                            holder.setImageResource(R.id.img_check,R.mipmap.ic_fm_list_item_check);

                            break;
                        }
                    }
                }

//
//
                holder.item.findViewById(R.id.img_check).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mItemClickListener.onItemCheckClick(mlist.indexOf(obj));
                    }
                });

                //holder.item.setOnLongClickListener(this);

            }


        };

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "你点击了~" + position + "~项", Toast.LENGTH_SHORT).show();
                mItemClickListener.onItemClick(position);
            }
        });
        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                ClipData data = ClipData.newPlainText("", "");
//                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    view.startDragAndDrop(data, shadowBuilder, view, 0);
//                } else {
//                    view.startDrag(data, shadowBuilder, view, 0);
//                }

                isedit = !isedit;
                mItemClickListener.onItemLongClick(isedit);
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        gv.setAdapter(mAdapter);
        gv.setOnDragListener(mlistener);
        return v;
    }

    public boolean onLongClick(View view) {
//        Log.d(TAG, "onTouch: ACTION_BUTTON_PRESS"+ event.getAction()+ "%%%%%" +event.getDownTime());
//        switch (event.getAction()) {
//
//
//            case MotionEvent.ACTION_BUTTON_PRESS:
//                Log.d(TAG, "onTouch: ACTION_BUTTON_PRESS"+ event.getDownTime());
//            case MotionEvent.ACTION_HOVER_MOVE:
//                Log.d(TAG, "onTouch: ACTION_HOVER_MOVE"+ event.getDownTime());
//            case MotionEvent.ACTION_DOWN:
//
//                Log.d(TAG, "onTouch: "+ event.getDownTime());
        {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(data, shadowBuilder, view, 0);
            } else {
                view.startDrag(data, shadowBuilder, view, 0);
            }
            return true;
        }
        //return false;
    }
}