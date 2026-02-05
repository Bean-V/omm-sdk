package com.oort.weichat.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.util.LocaleHelper;
import com.oort.weichat.util.LogUtils;
import com.oortcloud.appstore.dailog.LoadingDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dean Tao
 * @version 1.0
 */
public abstract class  EasyFragment extends BaseLoginFragment implements View.OnClickListener {
    protected View mRootView;
    // 是否重建了视图，重建了才需要重新初始化子视图，
    private boolean createView = false;
    protected Context mContext;
    protected LoadingDialog mLoadDialog;
    private FrameLayout flEmpty;

    /**
     * 是否缓存视图
     *
     * @return
     */
    protected boolean cacheView() {
        return true;
    }

    /**
     * 指定该Fragment的Layout id
     *
     * @return
     */
    protected abstract int inflateLayoutId();

    /**
     * 代替onActivityCreated的回调
     * 使用onActivityCreated初始化而不是onCreateView,
     * 因为onActivityCreated时有确保rootView, Context, Activity, CoreManager可用，
     * 而onCreateView只适用于初始化根视图，
     *
     * @param savedInstanceState anceState
     * @param createView         是否重新创建了视图，如果是，那么你需要重新findView来初始化子视图的引用等。
     */
    protected abstract void onActivityCreated(Bundle savedInstanceState, boolean createView);
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mLoadDialog = new LoadingDialog(mContext);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Activity ctx = getActivity();
        LocaleHelper.setLocale(ctx, LocaleHelper.getLanguage(ctx));
        // TODO: 出现过主页后三个fragment都是空白的问题，打个日志排查一下，
        LogUtils.log("onCreateView fragment: " + this.toString());
        boolean createView = true;
        if (cacheView() && mRootView != null) {
            // 缓存的mRootView需要判断是否已经在一个ViewGroup中， 如果在，就先移除自己，要不然会发生mRootView已经有parent的错误。
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
            createView = false;
        } else {
            mRootView = getWaterMarkView(inflateLayoutId());
        }
        this.createView = createView;
        return mRootView;
    }

    public void showEmpty(){
        if(flEmpty != null){
            flEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void hideEmpty(){
        if(flEmpty != null){
            flEmpty.setVisibility(View.GONE);
        }

    }
    /**
     * 获取水印View
     */
    private View getWaterMarkView(int layoutResID) {
        View baseView = LayoutInflater.from(getContext()).inflate(R.layout.layout_base, null, false);
        View contentView = LayoutInflater.from(getContext()).inflate(layoutResID, null, false);
        FrameLayout flContainer = baseView.findViewById(R.id.flContainer);
        FrameLayout flWater = baseView.findViewById(R.id.flWater);
        flEmpty = baseView.findViewById(R.id.fl_empty);


        SimpleDateFormat createTimeSdf1 = new SimpleDateFormat("yyyy-MM-dd");
        List<String> labels = new ArrayList<>();

       /* String name = ReportInfo.name + ReportInfo.phone;
        labels.add(name);
        *//*User user = coreManager.getSelf();
        if (user == null) {
            labels.add(getResources().getString(R.string.app_name));
        } else {
           String nick = user.getNickName();
           if (TextUtils.isEmpty(nick)){
               labels.add(getResources().getString(R.string.app_name));
           }else {
               labels.add(nick);
           }

        }*//*
        labels.add(createTimeSdf1.format(new Date()));
        flWater.setBackgroundDrawable(new WaterMarkBg(getContext(), labels, -30, 13));*/

        flContainer.addView(contentView);
        return baseView;
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!createView) {
            LogUtils.log("复用了fragment: " + this.toString());
            Reporter.post("复用了fragment");
        }
        onActivityCreated(savedInstanceState, createView);
    }

    public <T extends View> T findViewById(int id) {
        if (mRootView != null) {
            return mRootView.findViewById(id);
        }
        return null;
    }

    public void appendClick(View v) {
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
