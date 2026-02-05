package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ClassifyInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.ItemModuleTypeClassifyLayoutBinding;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function： 获取不同模块下的所有应用
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/19 22:05
 */
public class ModuleTypeClassifyAppAdapter extends BaseRecyclerViewAdapter<ClassifyInfo> {

    public ModuleTypeClassifyAppAdapter(Context context , List<ClassifyInfo> list) {
        super(context);
        mContext = context;
        lists = list;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_module_type_classify_layout, viewGroup , false );

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder viewHolder, int i) {
         ViewHolder holder = (ViewHolder) viewHolder;
        holder.typeName.setText(lists.get(i).getName());
        getModuleTypeClassifyApp(holder , lists.get(i).getUid());

    }



    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemModuleTypeClassifyLayoutBinding binding; // 假设布局文件为item_layout.xml
        TextView typeName;
        GridView dragGridView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemModuleTypeClassifyLayoutBinding.bind(itemView);
            typeName = binding.tvTypeName;
            dragGridView = binding.dragGridView;
        }

    }


    //切换类型下的应用
    private void getModuleTypeClassifyApp(final ViewHolder holder , String classifyUID){

        HttpRequestCenter.postModuleApplist( classifyUID).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                List<AppInfo> appInfoList = new ArrayList<>();
                Result<Data<AppInfo>> result = new Gson().fromJson(s, getType());
                if (result.isok()){

                    appInfoList = result.getData().getList();
                    if (appInfoList != null && appInfoList.size() > 0 ){

                        holder.dragGridView.setAdapter(new GridViewAdapter(mContext  , appInfoList, "add"));


                    }
                }
//
            }

        });

    }

    private Type getType(){
        return new TypeToken<Result<Data<AppInfo>>>(){}.getType();

    }

}
