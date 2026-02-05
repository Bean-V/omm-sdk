package com.oort.weichat.fragment;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


// GridView gv = v.findViewById(R.id.gv_filesrc);
//         List mData = new ArrayList<MyAdapter.Gnode>();
//        mData.add(new MyAdapter.Gnode(R.mipmap.ic_home_wx1, getString(R.string.file_wx), ""));
//        mData.add(new MyAdapter.Gnode(R.mipmap.ic_home_pic1, getString(R.string.file_pic), ""));
//        mData.add(new MyAdapter.Gnode(R.mipmap.ic_home_videa1, getString(R.string.file_video), ""));
//        mData.add(new MyAdapter.Gnode(R.mipmap.ic_home_audio1, getString(R.string.file_audio), ""));
//
//        mData.add(new MyAdapter.Gnode(R.mipmap.ic_home_contactbackup0, getString(R.string.back_up_contact), ""));
//        mData.add(new MyAdapter.Gnode(R.mipmap.ic_home_migrate0, getString(R.string.migrate), ""));
//
//
//
//
//        BaseAdapter mAdapter = new MyAdapter<MyAdapter.Gnode>((ArrayList<MyAdapter.Gnode>) mData, R.layout.layout_filesrc_item) {
//@Override
//public void bindView(ViewHolder holder, MyAdapter.Gnode obj) {
//        holder.setImageResource(R.id.img_icon, obj.getiId());
//        holder.setText(R.id.tv_name, obj.getiName());
//        holder.setText(R.id.tv_count, obj.getIcount());
//        }
//        };
//
//        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//@Override
//public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        }
//        });
//
//        gv.setAdapter(mAdapter);

public abstract class MyAdapter<T> extends BaseAdapter implements View.OnLongClickListener{

    public ArrayList<T> getmData() {
        return mData;
    }

    public void setmData(ArrayList<T> mData) {
        this.mData = mData;
    }

    private ArrayList<T> mData;
    private int mLayoutRes;           //布局id

    public MyAdapter() {
    }

    public MyAdapter(ArrayList<T> mData, int mLayoutRes) {
        this.mData = mData;
        this.mLayoutRes = mLayoutRes;
    }




    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bind(parent.getContext(), convertView, parent, mLayoutRes
                , position);
        //holder.item.setOnLongClickListener(this);
        bindView(holder, getItem(position));
        return holder.getItemView();
    }

    public abstract void bindView(ViewHolder holder, T obj);

    //添加一个元素
    public void add(T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    //往特定位置，添加一个元素
    public void add(int position, T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(position, data);
        notifyDataSetChanged();
    }

    public void remove(T data) {
        if (mData != null) {
            mData.remove(data);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    public void refresh(List list){
        mData.clear();
        if(list != null) {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
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
    public static class ViewHolder {

        private SparseArray<View> mViews;   //存储ListView 的 item中的View
        public View item;                  //存放convertView
        private int position;               //游标
        private Context context;            //Context上下文

        public Object obj;
        //构造方法，完成相关初始化
        private ViewHolder(Context context, ViewGroup parent, int layoutRes) {
            mViews = new SparseArray<>();
            this.context = context;
            View convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            convertView.setTag(this);
            item = convertView;
        }

        //绑定ViewHolder与item
        public static ViewHolder bind(Context context, View convertView, ViewGroup parent,
                                      int layoutRes, int position) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(context, parent, layoutRes);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.item = convertView;
            }
            holder.position = position;
            return holder;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int id) {
            T t = (T) mViews.get(id);
            if (t == null) {
                t = (T) item.findViewById(id);
                mViews.put(id, t);
            }
            return t;
        }

        /**
         * 获取当前条目
         */
        public View getItemView() {
            return item;
        }

        /**
         * 获取条目位置
         */
        public int getItemPosition() {
            return position;
        }

        /**
         * 设置文字
         */
        public ViewHolder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        /**
         * 设置图片
         */
        public ViewHolder setImageResource(int id, int drawableRes) {
            View view = getView(id);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            } else {
                view.setBackgroundResource(drawableRes);
            }
            return this;
        }

        /**
         * 设置点击监听
         */
        public ViewHolder setOnClickListener(int id, View.OnClickListener listener) {
            getView(id).setOnClickListener(listener);
            return this;
        }

        /**
         * 设置可见
         */
        public ViewHolder setVisibility(int id, int visible) {
            getView(id).setVisibility(visible);
            return this;
        }

        /**
         * 设置标签
         */
        public ViewHolder setTag(int id, Object obj) {
            getView(id).setTag(obj);
            return this;
        }

        //其他方法可自行扩展

    }
    public class Gnode {
        private int iId;
        private String iName;

        public String getIcount() {
            return icount;
        }

        public void setIcount(String icount) {
            this.icount = icount;
        }

        private String icount;

        public Gnode() {
        }

        public Gnode(int iId, String iName,String icount) {
            this.iId = iId;
            this.iName = iName;
            this.icount = icount;
        }

        public int getiId() {
            return iId;
        }

        public String getiName() {
            return iName;
        }

        public void setiId(int iId) {
            this.iId = iId;
        }

        public void setiName(String iName) {
            this.iName = iName;
        }
    }

}
