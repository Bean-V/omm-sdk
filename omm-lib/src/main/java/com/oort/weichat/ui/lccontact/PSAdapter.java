package com.oort.weichat.ui.lccontact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.utils.ImageLoader;

import java.util.List;

public class PSAdapter extends BaseAdapter {

    private Context mContext;
    private List items;

    public PSAdapter(Context c,List list){
        mContext = c;
        items = list;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;


        if(view == null){
            vh = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_p_item,viewGroup,false);
            vh.iv_user = view.findViewById(R.id.lab_user_header);
            vh.tv_name = view.findViewById(R.id.lab_name);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }

        UserInfo info = (UserInfo) items.get(i);
        //vh.iv_user.setBackgroundResource(R.mipmap.labuser);

        ImageLoader.loaderImage(vh.iv_user, info);
        vh.tv_name.setText(info.getOort_name());


        view.findViewById(R.id.btn_delP).setVisibility(hideDel ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.btn_delP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemDelClick(i);
            }
        });
        return view;
    }



    private ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void onItemDelClick(int position);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }
    private static class ViewHolder{
        ImageView iv_user;
        TextView tv_name;
    }
    public void refresh(List list){
        items = list;
        notifyDataSetChanged();
    }


    private boolean hideDel = false;
    public void setHideDel(boolean hide){
        hideDel = hide;
        //notifyDataSetChanged();
    }

}