package com.oort.weichat.ui.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.bean.Depcode;

import java.util.List;

public class DepcodeAdapter extends BaseAdapter {

    List<Depcode> depCodes;
    Context context;

    public DepcodeAdapter(List<Depcode> depCodes, Context context) {
        this.depCodes = depCodes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return depCodes.size ();
    }

    @Override
    public Object getItem(int position) {
        return depCodes.get ( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate( R.layout.item_depcode, parent, false);
        //这里写listview里面item的控件，findViewById前面要加上(view.)。
         TextView name = view.findViewById(R.id.depname);
//         TextView code = view.findViewById(R.id.code);
        // 把DataBean的数据从List里面取出来。
         Depcode depcode = depCodes.get(position);
        // 获取当前项的数据中的某个属性的值，将其设置为文本框控件的属性，完成数据的呈现。
         name.setText(depcode.getName ());
//         code.setText ( depcode.getCode () );
        return view;
    }

}
