package com.oortcloud.appstore.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.ItemAppManagerLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.db.ModuleTableManager;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.AppEventUtil;
import com.oortcloud.appstore.utils.AppManager;
import com.oortcloud.appstore.widget.DownloadProgressButton;
import com.oortcloud.appstore.widget.listener.DownloadListener;
import com.xuexiang.xui.widget.dialog.DialogLoader;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/17 21:47
 */
public class AppManagerAdapter extends RecyclerView.Adapter  {
    public static final String ADAPTER_UPDATE = "update_adapter";
    public static final String ADAPTER_UNINSTALL = "uninstall_adapter";
    private String type ;
    protected List<AppInfo> lists = new ArrayList<>();
    protected Context mContext;
    public AppManagerAdapter(Context context , String type) {
        mContext = context;
        this.type =type;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_app_manager_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder( final RecyclerView.ViewHolder viewHolder, final int i) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final AppInfo appInfo = lists.get(i);
        holder.appInfo = appInfo;
        Glide.with(mContext).load(appInfo.getIcon_url()).into((holder.appIcon));
        holder.appName.setText(appInfo.getApplabel());
        holder.appVersion.setText("V" + appInfo.getVersion());
        holder.appSize.setText(String.format("%.1f", Float.parseFloat(appInfo.getApp_size())/1024/1024) +"M");
        Date date = new Date(appInfo.getModified_on() *1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        holder.appItem.setText(simpleDateFormat.format(date));
        if (ADAPTER_UPDATE.equals(type)){
            holder.install.setCurrentText(mContext.getString(R.string.app_update_str));
            holder.install.setOnClickListener(view ->  {
                //下载 安装
                String text =  holder.install.getCurrentText().toString().trim();
                if (mContext.getString(R.string.open_str).equals(text)){
                    AppManager.open(appInfo , "");
                }else if (mContext.getString(R.string.app_update_str).equals(text)){
                    AppEventUtil.onClick(appInfo , new DownloadListener(appInfo , holder.install , "") );
                }
            });
        }else if(ADAPTER_UNINSTALL.equals(type)){
            holder.install.setCurrentText(mContext.getString(R.string.del_str));
            holder.install.setTextColor(mContext.getResources().getColor(R.color.color_F51500));
            String appName = holder.appName.getText().toString().trim();
            holder.install.setOnClickListener(view ->  {

                DialogLoader.getInstance().showConfirmDialog(
                        mContext,
                        mContext.getString(R.string.del_or_not)+ "\""+ appName +  "\"",
                        mContext.getString(R.string.dialog_ok),
                        (dialog, which) -> {

                            lists.remove(appInfo);
                            notifyDataSetChanged();

                            new Thread(() ->{
                                AppManager.unInstallApp(appInfo);
                                AppInfoManager.getInstance().deleteAppInfo(DBConstant.INSTALL_TABLE , appInfo);
                                List<ModuleInfo> moduleInfoList = ModuleTableManager.getInstance().queryData(DBConstant.MODULE_TABLE);
                                if (moduleInfoList != null){
                                    for (ModuleInfo moduleInfo : moduleInfoList){

                                        AppInfoManager.getInstance().deleteAppInfo(DBConstant.TABLE + moduleInfo.getModule_id() , appInfo);
                                    }
                                }

                                HttpRequestCenter.appUNInstall(appInfo.getUid()).subscribe(new RxBus.BusObserver<String>() {
                                    @Override
                                    public void onNext(String s) {
                                        Result result = new Gson().fromJson(s,new TypeToken<Result>(){}.getType());
                                        if (result.isok()){
                                            Toast.makeText(mContext , appName+ mContext.getString(R.string.del_suc)  , Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().post(new AppStoreChangeMessage());
                                        }
                                    }
                                });
                            }).start();
                            dialog.dismiss();
                        },
                        mContext.getString(R.string.base_cancel),
                        (dialog, which) -> {
                            dialog.dismiss();
                        }
                );

            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    @Override
    public int getItemCount() {
        return lists.size();
    }
    //设置数据
    public  void
    setData(List date){
//        lists.clear();
        lists = date;
        this.notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemAppManagerLayoutBinding binding; // 对应布局 item_app.xml

        ImageView appIcon;
        TextView appName;
        TextView appVersion;
        TextView appSize;
        TextView appItem; // 注意：布局中 ID 可能为 tv_app_time
        DownloadProgressButton install;
        AppInfo appInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAppManagerLayoutBinding.bind(itemView); // 初始化 ViewBinding

            // 通过 ViewBinding 初始化视图变量
            appIcon = binding.appIcon;
            appName = binding.tvAppName;
            appVersion = binding.tvAppVersion;
            appSize = binding.tvAppSize;
            appItem = binding.tvAppTime; // 布局 ID 应为 tv_app_time
            install = binding.tvInstall;
        }


    }
}