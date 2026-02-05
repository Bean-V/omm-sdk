package com.oortcloud.appstore.fragment.table;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.EditorActivity;
import com.oortcloud.appstore.adapter.AddModuleAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.dailog.InputDialog;
import com.oortcloud.appstore.databinding.FragmentMineLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.db.DBManager;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.db.ModuleTableManager;
import com.oortcloud.appstore.db.TableDataStructure;
import com.oortcloud.appstore.fragment.BaseFragment;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.ToastUtils;
import com.oortcloud.appstore.widget.HeaderRecyclerView;
import com.oortcloud.appstore.widget.RecyclerRefreshLayout;
import com.oortcloud.appstore.widget.listener.SimpleItemTouchHelper;
import com.xuexiang.xui.widget.dialog.DialogLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function： 我的Fragment
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/13 11:13
 */
public class MineFragment extends BaseFragment implements AddModuleAdapter.StartDragListener, AddModuleAdapter.ClickListener , RecyclerRefreshLayout.SuperRefreshLayoutListener {

    private LinearLayout mMineLayout;
    private HeaderRecyclerView mModuleRV;
    private RecyclerRefreshLayout mRefreshLayout;

    private DBManager mDBManager;
    private AddModuleAdapter mModuleAdapter;
    List<ModuleInfo<AppInfo>> moduleInfoList = new ArrayList<>();

    private ItemTouchHelper mItemTouchHelper;

    private View mFoorterView;

    private Boolean dialogFlag = true;

    private ModuleTableManager
            moduleTableManager;
    private AppInfoManager appInfoManager;
    private com.oortcloud.appstore.databinding.FragmentMineLayoutBinding bind;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine_layout;
    }

    protected View getRootView() {
        bind = FragmentMineLayoutBinding.inflate(getLayoutInflater());
        return bind.getRoot();
    }

    @Override
    protected void initBundle(Bundle bundle) {

        mDBManager = DBManager.getInstance();
        moduleTableManager = ModuleTableManager.getInstance();
        appInfoManager = AppInfoManager.getInstance();

    }

    @Override
    protected void initView() {

        mMineLayout = bind.mineLl;
        mModuleRV = bind.rvAddModuleList;
        mModuleAdapter = new AddModuleAdapter(mContext, moduleInfoList);


        mModuleRV.setLayoutManager(new GridLayoutManager(mContext, 1));
        mModuleRV.setItemAnimator(new DefaultItemAnimator());


        mFoorterView = LayoutInflater.from(mContext).inflate(R.layout.footer_layout, mModuleRV, false);
        mModuleRV.addFooterView(mFoorterView);
        mModuleRV.setAdapter(mModuleAdapter);

        //设置ItemTouchHelper回调
        SimpleItemTouchHelper callback = new SimpleItemTouchHelper(mModuleAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        //关联RecyclerView与ItemTouchHelper
        mItemTouchHelper.attachToRecyclerView(mModuleRV);

        mModuleAdapter.setOnstartDragListener(this);
        mModuleAdapter.setOnItemClickListener(this);
        mModuleRV.setShiftListener(mModuleAdapter);
        mRefreshLayout = bind.refreshLayout;
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mRefreshLayout.setColorSchemeResources(
                R.color.fresh_color1, R.color.fresh_color2,R.color.fresh_color3,
                R.color.fresh_color4, R.color.fresh_color5 ,R.color.fresh_color6);


    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initData() {
      moduleinit();
    }

    @Override
    protected void initEvent() {

        mFoorterView.findViewById(R.id.tv_add_module).setOnClickListener(view -> {

            new InputDialog(mContext, new InputDialog.DialogClickListener() {
                @Override
                public void onDialogClick() {
                }

                @Override
                public void onDialogClick(final String moduleName) {


                    HttpRequestCenter.addModule(moduleName).subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {

                            Result<ModuleInfo> result = new Gson().fromJson(s, new TypeToken<Result<ModuleInfo>>() {
                            }.getType());
                            if (result.isok()) {
                                ModuleInfo moduleInfo = result.getData();
                                //排序
                                if (moduleInfo != null) {

                                    ModuleTableManager.getInstance().insertData(DBConstant.MODULE_TABLE, moduleInfo);
                                    AppInfoManager.getInstance().createTable(DBConstant.TABLE + moduleInfo.getModule_id(), TableDataStructure.APP_INFO);
                                    EditorActivity.actionStart(mContext, moduleInfo);

                                    //低效 待优化
                                    new Thread(()->{
                                        if (moduleInfoList != null){
                                            moduleTableManager.deleteData(DBConstant.MODULE_TABLE , null);
                                            moduleInfoList.add(0 , moduleInfo);
                                            for(ModuleInfo module : moduleInfoList){
                                                moduleTableManager.insertData(DBConstant.MODULE_TABLE , module);
                                            }
                                        }
                                    }).start();
                                }

                            } else {
                                ToastUtils.showBottom(result.getMsg());
                            }
                        }
                    });

                }

            }, null).show();

        });

        mFoorterView.findViewById(R.id.tv_module_init).setOnClickListener(view -> {


            DialogLoader.getInstance().showConfirmDialog(mContext, getString(R.string.restore_tip), mContext.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    HttpRequestCenter.postModuleInit().subscribe(new RxBus.BusObserver<String>() {

                        @Override
                        public void onNext(String s) {
                            Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());

                            if (result.isok()) {
                                DataInit.moduleinit(AppStoreInit.getToken(), AppStoreInit.getUUID(), new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.what == result.getCode()) {
                                            getModuleList();
                                        }
                                    }
                                });

                            }

                        }
                    });
                    ;
                }
            },mContext.getString(R.string.base_cancel)).show();

//            new CommonDialog(mContext, "", getString(R.string.restore_tip), () -> {
//
//
//
//
//                HttpRequestCenter.postModuleInit().subscribe(new RxBus.BusObserver<String>() {
//
//                    @Override
//                    public void onNext(String s) {
//                        Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
//
//                        if (result.isok()) {
//                            DataInit.moduleinit(AppStoreInit.getToken(), AppStoreInit.getUUID(), new Handler() {
//                                @Override
//                                public void handleMessage(Message msg) {
//                                    if (msg.what == result.getCode()) {
//                                        getModuleList();
//                                    }
//                                }
//                            });
//
//                        }
//
//                    }
//                });
//
//
//            }, null).show();

        });
        EventBus.getDefault().register(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setEvent(String messageEvent) {
        Log.d("TAG", "onCreate: ===========执行");

        if(messageEvent.equals("applyStatu")){
            mModuleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {

        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }


    @Override
    public void onStart() {
        if (isRequest) {
            getModuleList();
            isRequest = false;
        }
        super.onStart();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser && isInitUi) {
            getModuleList();

        } else {
            isRequest = false;

        }

    }


    @Override
    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        //手动开启拖拽
        mItemTouchHelper.startDrag(viewHolder);
        //手动开启侧滑
//        mItemTouchHelper.startSwipe(viewHolder);
    }

    @Override
    public void onItemClick(int position, View v) {
        Snackbar.make(v, position, Snackbar.LENGTH_SHORT).show();
    }


    private void getModuleList() {
        if (mLoadDialog != null){
            mLoadDialog.show();
        }
        if (moduleTableManager != null) {
            setModuleList();
        } else {
            moduleTableManager = ModuleTableManager.getInstance();
            setModuleList();
        }
        mRefreshLayout.setCanLoadMore(false);
        mRefreshLayout.onComplete();


    }

    private void setModuleList() {
         moduleInfoList = moduleTableManager.queryData(DBConstant.MODULE_TABLE);

        if (moduleInfoList != null) {
            //排序处理
            mModuleAdapter.setData(moduleInfoList);
        }
        new Handler().postDelayed(() -> {
            if (mLoadDialog.isShowing()){
                mLoadDialog.dismiss();
            }
            dialogFlag = false;
            if (mMineLayout != null){
                mMineLayout.setVisibility(View.VISIBLE);
            }

        }, 800);

    }

    @Override
    public void onRefreshing() {
        moduleinit();
    }

    @Override
    public void onLoadMore() {


    }

    @Override
    public void onScrollToBottom() {

    }

    private void  moduleinit(){
        //DataInit.installinit(DBConstant.INSTALL_TABLE , AppStoreInit.getToken(), AppStoreInit.getUUID());

        DataInit.moduleinit(AppStoreInit.getToken(), AppStoreInit.getUUID(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 200) {
                    getModuleList();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}