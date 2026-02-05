package com.oortcloud.contacts.adapter;


import android.util.Log;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.Department;

import java.util.List;


/**
 * 通讯录部门目录适配器
 */
public class HigherDepartmentAdapter extends BaseQuickAdapter<Department, BaseViewHolder> {
    List<Department> data;
    public HigherDepartmentAdapter( @Nullable List<Department> data) {
        this(R.layout.item_adress_company_organi_top,data);
        this.data = data;
    }
    public HigherDepartmentAdapter(int layoutResId, @Nullable List<Department> data) {
        super(layoutResId,data);

    }

    @Override
    protected void convert(final BaseViewHolder helper, Department item) {
        if (getItemPosition(item) == (getItemCount()-1)){
            helper.setTextColor(R.id.tv_name ,getContext().getResources().getColor( R.color.color_000));
            helper.setText(R.id.tv_name,item.getOort_dname());

        }else {
            helper.setTextColor(R.id.tv_name ,getContext().getResources().getColor( R.color.color_999));
            helper.setText(R.id.tv_name,item.getOort_dname()+" / ");

        }
    }

}
