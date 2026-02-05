package com.oortcloud.appstore.widget.listener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.db.ModuleTableManager;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.AppManager;
import com.oortcloud.appstore.widget.DownloadLoadingView;
import com.oortcloud.appstore.widget.DownloadProgressButton;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.widget.xupdate.widget.OortUpdateDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: ommadvance
 * @FileName: DownloadListener.java
 * @Function: 下载监听回调更新View进度
 * @Author: zhangzhijun / @CreateDate: 2020/5/17 3:57
 * @UpdateUser: 更新者 /@UpdateDate: 2020/5/17 3:57
 * @Version: 1.0
 */
public class DownloadListener implements com.oortcloud.appstore.download.DownloadListener {
    private Context mContext;
    private AppInfo appInfo;
    private String params;
    private int mProgress = 0;

    public DownloadProgressButton getProgressButton() {
        return progressButton;
    }

    public void setProgressButton(DownloadProgressButton progressButton) {
        if(mProgress == 0){
            tmpListeners.remove(this);
            return;
        }
        this.progressButton = progressButton;


        progressButton.setState(DownloadProgressButton.STATE_DOWNLOADING);
        progressButton.setProgressText(mContext.getString(R.string.loading), mProgress);
    }

    private DownloadProgressButton  progressButton;

    private DownloadLoadingView loadingView;

    private OortUpdateDialog updateDialog;

    private TextView mTextView;
    private ProgressDialog pd;


    public static boolean contain(String uid){

        for(Object o : tmpListeners){

            DownloadListener listener = (DownloadListener) o;

            if(listener.appInfo.getUid().equals(uid)){

                return true;
            }


        }
        return false;
    }

    public static DownloadListener getListener(String uid){

        for(Object o : tmpListeners){

            DownloadListener listener = (DownloadListener) o;

            if(listener.appInfo.getUid().equals(uid)){

                return listener;
            }


        }
        return null;
    }


    public static ArrayList tmpListeners =  new ArrayList();

    public DownloadListener(AppInfo appInfo , String params){
        this.appInfo = appInfo;
        this.params = params;
        mContext = AppStoreInit.getInstance().getApplication();
    }


    public DownloadListener(AppInfo appInfo , OortUpdateDialog dialog){
        this.appInfo = appInfo;
        this.updateDialog = dialog;
        mContext = AppStoreInit.getInstance().getApplication();
    }

    public DownloadListener(AppInfo appInfo ,  DownloadProgressButton  progressButton , String params){
        this.appInfo = appInfo;
        this.progressButton = progressButton;
        this.params = params;
        mContext = AppStoreInit.getInstance().getApplication();
        tmpListeners.add(this);
    }

    public DownloadListener(AppInfo appInfo ,  DownloadLoadingView loadingView , String params){
        this.appInfo = appInfo;
        this.loadingView = loadingView;
        this.params = params;
        mContext = AppStoreInit.getInstance().getApplication();

        tmpListeners.add(this);
    }
    @Override
    public void onStarted() {
        if (progressButton != null){
            progressButton.setState(DownloadProgressButton.STATE_DOWNLOADING);
        }
        else if(loadingView != null){
            loadingView.setVisibility(View.VISIBLE);
        } else if(updateDialog != null){
        } else {


            if(pd != null) {
                if(pd.isShowing()) {
//                    if(pd.getContext().equals(CommonApplication.topActivity)){
//
//                        return;
//                    }else{
//                        pd.dismiss();
//                        pd = null;
//                    }
                    return;
                }
                pd.dismiss();
                pd = null;


            }

           // pd = new DownLoadDialog(CommonApplication.topActivity);

            pd = new ProgressDialog(CommonApplication.topActivity);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            //FloatWindowManager.createSmallWindow(mContext  ,appInfo);
        }

    }

    @Override
    public void onPrepared(long contentLength, String downloadUrl) {

    }

    @Override
    public void onPregressChanged(int progress, String downloadUrl) {

        if (progressButton != null){
            if (progressButton.getProgress() <= 100) {
                mProgress = progress;
                progressButton.setProgressText(mContext.getString(R.string.loading), progress);
            }
        }
        else if (loadingView != null){
            if (loadingView.getProgress() < 100) {
                loadingView.setProgress(progress);
            }else {
                loadingView.setVisibility(View.GONE);
            }
        }else if(updateDialog != null){
            updateDialog.setProgress(progress );
        }
        else {
            //FloatWindowManager.updateUsedPercent(progress);
            pd.setProgress(progress );
            pd.setMessage("正在下载" + " "  + progress + "%");
        }

    }




    @Override
    public void onPaused(int progress, int completeSize, String downloadUrl) {
        if (progressButton != null){
            progressButton.setCurrentText("暂停...");
        }
    }

    @Override
    public void onFinished(int completeSize, String downloadPath) {
        //应用安装

        tmpListeners.remove(this);
        if(updateDialog != null){
            updateDialog.setProgress(100);
            updateDialog.hideCancelBtn();
        }
        new Thread(() -> {


            AppManager.installAPP(appInfo , downloadPath);
            Handler mainThread = new Handler(Looper.getMainLooper());
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    if (loadingView != null){
                        loadingView.setVisibility(View.GONE);
                    }else if(updateDialog != null){
                        updateDialog.setProgress(100);
                        updateDialog.dismiss();
                    }
                    else {
                        //FloatWindowManager.updateUsedPercent(100);
                       // pd.dismiss();

                        if(pd != null) {
                            pd.setProgress(100);
                            pd.setMessage(mContext.getString(R.string.base_install));
                        }

                    }


                    new Handler().postDelayed(() -> {
                        if (progressButton != null){
                            progressButton.setState(DownloadProgressButton.STATE_OPEN);
                            progressButton.setCurrentText(mContext.getString(R.string.open_str));
                        }else {
                            //FloatWindowManager.removeSmallWindow(mContext);

                            if(pd != null) {
                                pd.dismiss();
                            }
                        }


                    }, 500);

                    //h5应用直接打开
                    if (appInfo != null){
                        if (appInfo.getTerminal() ==1 || appInfo.getTerminal() ==6){
                            if (AppManager.checkFilePath(appInfo)){
                                AppManager.open(appInfo , params);
                            }else {

                            }
                        }

                    }

                }
            });
            return;

        }).start();


        //本地更新
        new Thread(() -> {

            AppInfoManager.getInstance().insertAppInfo(DBConstant.INSTALL_TABLE, appInfo);
            List<ModuleInfo> moduleInfoList = ModuleTableManager.getInstance().queryData(DBConstant.MODULE_TABLE);
            if (moduleInfoList != null) {
                for (ModuleInfo moduleInfo : moduleInfoList) {

                    AppInfoManager.getInstance().upDateAppInfo(DBConstant.TABLE + moduleInfo.getModule_id(), appInfo);
                }
            }


            //下载记录数
            HttpRequestCenter.appinstallplusone(appInfo.getApppackage() , appInfo.getVersioncode()).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                    }.getType());
                    if (result.isok()) {

                    }
                }

            });
            //安装列表
            HttpRequestCenter.appInstall(appInfo.getApplabel()  ,appInfo.getApppackage() ,appInfo.getClassify() , appInfo.getUid() ,appInfo.getVersioncode() , appInfo.getTerminal()).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    Log.v("msg" , s);
                    Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                    }.getType());
                    if (result.isok() ||result.getCode() == 50010){
                        //数据库更新成功/已经是最高版本
                    }

                }
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    Log.v("msg" , e.toString());
                    AppStatu.getInstance().appStatu = 0;
                }
            });
        }).start();


//        AsyncTask asyncTask = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//            }
//        };



    }



    @Override
    public void onFailure() {

    }

    public String  getParams(){
        return params;
    }
    public void  setAppInfo(AppInfo appInfo){
        this.appInfo = appInfo;
    }
}
