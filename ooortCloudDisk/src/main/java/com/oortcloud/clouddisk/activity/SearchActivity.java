package com.oortcloud.clouddisk.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.adapter.FileAdapter;
import com.oortcloud.clouddisk.bean.DirData;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.bean.Result;
import com.oortcloud.clouddisk.databinding.ActivitySearchBinding;
import com.oortcloud.clouddisk.db.SharedPreferenceManager;
import com.oortcloud.clouddisk.http.HttpRequestCenter;
import com.oortcloud.clouddisk.http.bus.RxBusSubscriber;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.widget.HistoryLaberView;
import com.oortcloud.clouddisk.widget.SearchView;

import java.util.ArrayList;
import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 10:08
 * @version： v1.0
 * @function： 搜索页
 */
public class SearchActivity extends BaseActivity  implements SearchView.SearchViewListener {
    private static final String  HISTORY_KEY = "history_key";
    private SharedPreferenceManager mSharedManager;
    private HistoryLaberView mHistoryTaberView;
    private TextView mHistoryTV;
    private ImageView mDeleteImg;
    private RecyclerView mRV;
    private SearchView mSearchView;
    RecyclerView mFileRV;
    LinearLayout mImgNull;
    private String mDir;

    @Override
    protected void initActionBar() {

    }

    private List<FileInfo> mAppList;
    private List<String> mHistoryList = new ArrayList();

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mAppList != null && mAppList.size() > 0 ){
            mRV.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected ActivitySearchBinding getViewBinding() {
        return ActivitySearchBinding.inflate(getLayoutInflater());
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_search_;
//    }

    @Override
    protected void initBundle(Bundle bundle) {
        mSharedManager = SharedPreferenceManager.getInstance();
    }
    @Override
    protected void initView() {

        ActivitySearchBinding binding = getViewBinding();
        mHistoryTaberView = binding.historyTabView;
        mHistoryTV = binding.historyTv;
        mDeleteImg = binding.deleteImg;
        mRV = binding.recyclerView;
        mSearchView = binding.searchView;
        mFileRV = binding.fileRv;
        mImgNull = binding.imgNullLl;
        initHistory();
        mRV.setLayoutManager(new LinearLayoutManager(mContext));

        initEvent(null);


        mFileAdapter = new FileAdapter(mContext , mDir);
        mFileRV.setLayoutManager(new LinearLayoutManager(this));
        mFileRV.setAdapter(mFileAdapter);

        mSearchView.getEtInput().setHint("搜索文件");
    }

    @Override
    protected void initEvent(View view) {
        mSearchView.setSearchViewListener(this);
        mDeleteImg.setOnClickListener(v ->  {

            mSharedManager.putString(HISTORY_KEY , "");
            initHistory();
        });
    }

    @Override
    protected void initData() {

        mDir = getIntent().getStringExtra("dir");

    }

    /**
     * 初始化  历史记录列表
     */
    private void initHistory() {
        getHistoryList();
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 20, 15, 10);

        mHistoryTaberView.removeAllViews();
        for (int i = 0; i < mHistoryList.size(); i++) {

            //有数据往下走
            final int x = i;
            //添加分类块
            View paramItemView = getLayoutInflater().inflate(R.layout.item_history_search, null);
            final TextView keyWordTv = paramItemView.findViewById(R.id.content_tv);
            keyWordTv.setText(mHistoryList.get(i));
            mHistoryTaberView.addView(paramItemView, layoutParams);

            keyWordTv.setOnClickListener(view ->  {

                onSearch(keyWordTv.getText().toString().trim());

            });

        }
    }

    @Override
    public void onRefreshAutoComplete(String text) {

    }

    @Override
    public void onSearch(final String text) {
        //待搜索接口开发
        if (!TextUtils.isEmpty(text)){
            String order =  SharedPreferenceManager.getInstance().getString("order" );

            String dir = mDir;
            HttpRequestCenter.fileList(dir, text, order, 1, 100, "").subscribe(new RxBusSubscriber<String>() {
                @Override
                protected void onEvent(String s) {
                    Log.v("msg", s);
                    Result<DirData<FileInfo>> result = new Gson().fromJson(s, new TypeToken<Result<DirData<FileInfo>>>() {
                    }.getType());
                    if (result.isOk()) {
                        DirData data = result.getData();
                        if (data != null) {
                            //空判断处理
                            if (data.getList() == null){
                                data.setList(new ArrayList());
                            }
                        }
                        onShow(data);
                        if (!mHistoryList.contains(text)) {
                                addHistory(text);
                        }


                    } else {
                        if (result.getCode() == 4004) {
                            ToastUtils.showContent("无效的token");
                        }
                    }

                }

                @Override
                public void onError(Throwable e) {
                    if (e.toString().contains("java.net.SocketTimeoutException")) {
//                    ToastUtils.showContent("服务端连接异常");
                        ToastUtils.showContent(e.getMessage());
                    }
                }
            });
        }
    }


    private FileAdapter mFileAdapter;

    private DirData mDirData;


    private void onShow(DirData data1) {
        mDirData = data1;
        if (mDirData != null ){
            List<FileInfo> data = mDirData.getList();

            if (data != null && data.size() > 0 ){
                mFileAdapter.setData(mDirData.getList());
                mFileRV.setVisibility(View.VISIBLE);
                mImgNull.setVisibility(View.GONE);

                mHistoryTaberView.setVisibility(View.GONE);
                mDeleteImg.setVisibility(View.GONE);
                return;
            }

        }
        mFileRV.setVisibility(View.GONE);
        mImgNull.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDelete() {
        mHistoryTV.setText(getString(R.string.search_history));
        mHistoryTaberView.setVisibility(View.VISIBLE);
        mDeleteImg.setVisibility(View.VISIBLE);
        mRV.setVisibility(View.GONE);
        initHistory();
    }


    private void getHistoryList(){
        mHistoryList.clear();
        String historyStr = mSharedManager.getString(HISTORY_KEY);
        if (!TextUtils.isEmpty(historyStr)){
            String str[] = historyStr.split("&");
            for (String s: str){
                mHistoryList.add(s);
            }
        }

    }

    private void addHistory(String historyName){

        String historyStr = mSharedManager.getString(HISTORY_KEY);
        if (TextUtils.isEmpty(historyStr)){
            mSharedManager.putString(HISTORY_KEY ,historyName );
        }else {
            mSharedManager.putString(HISTORY_KEY , historyStr+ "&" +historyName);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
