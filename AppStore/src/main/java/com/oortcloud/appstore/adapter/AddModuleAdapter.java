package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.EditorActivity;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.ItemModuleLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.db.ModuleTableManager;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.ToastUtils;
import com.oortcloud.appstore.widget.listener.MoveModuleListener;
import com.oortcloud.appstore.widget.listener.OnMoveAndSwipedListener;
import com.xuexiang.xui.widget.dialog.DialogLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

/**
 * @filename:
 * @function： MimeFragment 添加模块Adapter
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/19 10:58
 */
public class AddModuleAdapter extends BaseRecyclerViewAdapter<ModuleInfo<AppInfo>> implements OnMoveAndSwipedListener , MoveModuleListener {

    private ClickListener clickListener;
    private StartDragListener dragListener;

    private int flagPosition = -1;
    private int foPosition = -1;
    private boolean flag =false;
    public AddModuleAdapter(Context context , List moduleList ) {
        super(context);
        lists = moduleList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_module_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder =(ViewHolder) viewHolder;

        ModuleInfo moduleInfo  =  lists.get(position);

        if (moduleInfo != null){
            holder.moduleName.setText(moduleInfo.getModule_name() );
            if (moduleInfo.getHomepage_type() == 0 ){

                holder.mShowTV.setVisibility(View.VISIBLE);

            }
            if (moduleInfo.getIs_edit() == 2){
                holder.moduleEdit.setVisibility(View.INVISIBLE);
                holder.moduleDelete.setVisibility(View.INVISIBLE);

            }else {
                holder.moduleEdit.setVisibility(View.VISIBLE);
                holder.moduleDelete.setVisibility(View.VISIBLE);

            }
            if (moduleInfo.getIs_top_module() == 1){
                holder.mDragTV.setVisibility(View.INVISIBLE);
            }else {
                holder.mDragTV.setVisibility(View.VISIBLE);
            }
            List<AppInfo> appInfoList = AppInfoManager.getInstance().queryAppInfo(DBConstant.TABLE + moduleInfo.getModule_id());
            if (appInfoList != null){
                holder.moduleAppGV.setAdapter(new ModuleAppInfoAdapter(mContext , appInfoList));

            }

        }


        holder.itemView.setOnClickListener(view -> {
                if (dragListener != null)
                    dragListener.startDrag(holder);
        });


        holder.moduleEdit.setOnClickListener(view ->  {

                if (moduleInfo != null){
                    EditorActivity.actionStart(mContext , moduleInfo);
                }

        });

        holder.moduleDelete.setOnClickListener(view ->  {

            DialogLoader.getInstance().showConfirmDialog(mContext, mContext.getString(R.string.delete_module_tip), mContext.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (moduleInfo != null){
                        ModuleTableManager.getInstance().deleteData(DBConstant.TABLE + moduleInfo.getModule_id() , moduleInfo);
                        AppInfoManager.getInstance().deleteTable(DBConstant.TABLE + moduleInfo.getModule_id());
                        if (lists != null){
                            lists.remove(moduleInfo);
                        }

                        //dialogInterface.dismiss();

                        notifyDataSetChanged();
                        ToastUtils.showBottom(mContext.getString(R.string.del_suc));
                        dialogInterface.dismiss();

                        HttpRequestCenter.deleteModule(moduleInfo.getModule_id()).subscribe(new RxBus.BusObserver<String>() {
                            @Override
                            public void onNext(String s) {
                                Result result = new Gson().fromJson(s,new TypeToken<Result>(){}.getType());

                                if (result.isok()){

                                    EventBus.getDefault().post(new AppStoreChangeMessage());

                                }
                            }
                        });

                    }


                }
            },mContext.getString(R.string.base_cancel));



        });

    }

    @Override
    public int getItemViewType(int position) {
        return lists.get(position).getIs_top_module();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemModuleLayoutBinding binding; // 替换为实际生成的 Binding 类

        TextView moduleName;
        TextView mShowTV;
        TextView moduleEdit;
        TextView moduleDelete;
        GridView moduleAppGV;
        TextView mDragTV;
 // 假设 clickListener 已在其他地方定义

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemModuleLayoutBinding.bind(itemView); // 初始化 ViewBinding

            // 通过 ViewBinding 赋值给成员变量
            moduleName = binding.tvModuleName;
            mShowTV = binding.tvShow;
            moduleEdit = binding.tvEdit;
            moduleDelete = binding.tvDelete;
            moduleAppGV = binding.moduleAppGridview;
            mDragTV = binding.dragText;

            // 设置点击事件（如果需要）
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }


    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnstartDragListener(StartDragListener listener) {
        this.dragListener = listener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //需要对数据源也同时进行操作 将两个元素位置互换
        if (lists != null && lists.size() > 0){

            Collections.swap(lists, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);

        }
        flagPosition  = toPosition;
        foPosition = fromPosition;
        flag = true;

        return true;
    }

    @Override
    public void onItemDismiss(int position) {


        if(position <0 || position >= lists.size()){
            notifyItemRemoved(position);
            return;
        }
        if (lists != null && lists.size() > 0){
            lists.remove(position);
            notifyItemRemoved(position);

        }

    }

    @Override
    public void onItemDismiss_(RecyclerView.ViewHolder vh) {

        if (lists != null && lists.size() > 0){
            int position = lists.indexOf(vh);
            if(position <0 || position >= lists.size()){
                notifyItemRemoved(position);
                return;
            }
            lists.remove(vh);
            notifyItemRemoved(position);

        }
    }


    @Override
    public void shiftListener() {
        if (flag){
            new Thread(() ->{
              
                ModuleTableManager moduleTableManager = ModuleTableManager.getInstance();
                moduleTableManager.deleteData(DBConstant.MODULE_TABLE , null);
                if (lists != null){
                    for (ModuleInfo moduleInfo : lists){
                        moduleTableManager.insertData(DBConstant.MODULE_TABLE , moduleInfo);
                    }

                }
            }).start();

            HttpRequestCenter.postModuleShift(lists.get(flagPosition).getModule_id() , flagPosition + 1).subscribe(new RxBus.BusObserver<String>() {

                @Override
                public void onNext(String s) {
                    Result result = new Gson().fromJson(s,new TypeToken<Result>(){}.getType());
                    if (result.isok()){

                        flag = false;
                        flagPosition = -1;
                        EventBus.getDefault().post(new AppStoreChangeMessage());

                    }
                }
            });
            notifyDataSetChanged();
        }

    }

    public interface StartDragListener {
        void startDrag(RecyclerView.ViewHolder viewHolder);
    }
}
