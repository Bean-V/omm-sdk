package com.oort.weichat.ui.lccontact;
import com.oort.weichat.R;
import com.oort.weichat.bean.User;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.utils.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adapter_contacts extends BaseAdapter {

    private Context mContext;
    private List items;
    private List sitems;

    public void setSelectUsers(List selectUsers) {
        this.selectUsers = selectUsers;
    }

    private List selectUsers;

    private Adapter_org_header.ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void onItemClick(int position);
    }
    public void  setOnItemClickListener(Adapter_org_header.ItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public Adapter_contacts(Context c,List list){
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
    public View getView(int p, View view, ViewGroup viewGroup) {
        ViewHolder vh;


        if(view == null){
            vh = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_pick_item,viewGroup,false);
            vh.iv_check = view.findViewById(R.id.icon_check);
            vh.iv_header = view.findViewById(R.id.lab_user_header);
            vh.tv_name = view.findViewById(R.id.lab_name);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }

        Sort s = (Sort) items.get(p);


        if(s instanceof UserInfo){


            vh.iv_check.setVisibility(View.VISIBLE);
            UserInfo info = (UserInfo) s;
            ImageLoader.loaderImage(vh.iv_header,info);
            vh.tv_name.setText(info.getOort_name());

            vh.iv_check.setBackgroundResource(R.mipmap.icon_p_default);
            if (selectUsers != null) {
                for (int i = 0; i < selectUsers.size(); i++) {
                    UserInfo info1 = (UserInfo) selectUsers.get(i);
                    if (info1.getOort_uuid().equals(((UserInfo) s).getOort_uuid())) {
                        vh.iv_check.setBackgroundResource(R.mipmap.icon_p_selected);
                        break;
                    }
                }
            }

        }else{
            vh.iv_check.setVisibility(View.GONE);
            Department dp = (Department) s;

            ImageLoader.divisionImage(vh.iv_header , dp);
            vh.tv_name.setText(dp.getOort_dname());
        }

        return view;
    }


    private static class ViewHolder{
        ImageView iv_check;
        ImageView iv_header;
        TextView tv_name;
    }
    public void refresh(List list){
        items = list;
        notifyDataSetChanged();
    }

    public void  refreshUserCheckStatu(List users){
        selectUsers = users;
        notifyDataSetChanged();
    }

}
