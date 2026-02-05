/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oortcloud.basemodule.widget.xupdate.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oortcloud.basemodule.R;
import com.oortcloud.basemodule.widget.xupdate.entity.PromptEntity;
import com.oortcloud.basemodule.widget.xupdate.entity.UpdateEntity;
import com.oortcloud.basemodule.widget.xupdate.proxy.IPrompterProxy;

/**
 * 版本更新弹窗
 *
 * @author xuexiang
 * @since 2018/7/24 上午9:29
 */
public class OortUpdateDialog extends Dialog implements View.OnClickListener {

    //======顶部========//
    /**
     * 顶部图片
     */
    private ImageView mIvTop;
    /**
     * 标题
     */
    private TextView mTvTitle;

    private TextView mTvVersion;



    //======更新内容========//
    /**
     * 版本更新内容
     */
    private TextView mTvUpdateInfo;



    /**
     * 版本更新
     */
    private Button mBtnUpdate;

    private Button mCancelUpdate;
    private FrameLayout mfl;
    /**
     * 后台更新
     */
    private Button mBtnBackgroundUpdate;
    /**
     * 忽略版本
     */
    private Button mTvIgnore;
    /**
     * 进度条
     */
    private NumberProgressBar mNumberProgressBar;
    //======底部========//
    /**
     * 底部关闭
     */
    private LinearLayout mLlClose;
    private ImageView mIvClose;

    //======更新信息========//
    /**
     * 更新信息
     */
    private UpdateEntity mUpdateEntity;
    /**
     * 更新代理
     */
    private IPrompterProxy mPrompterProxy;
    /**
     * 提示器参数信息
     */
    private PromptEntity mPromptEntity;

    public void hideCancelBtn() {
        mCancelUpdate.setVisibility(View.GONE);
    }

    /**
     * 获取更新提示
     *
     * @param updateEntity  更新信息
     * @param prompterProxy 更新代理
     * @param promptEntity  提示器参数信息
     * @return 更新提示
     */

    public interface Callback{
        public void ok();
        public void cancel();
        public void cancelDown();
    }

    public Callback getButtonClickCallback() {
        return buttonClickCallback;
    }

    public void setButtonClickCallback(Callback buttonClickCallback) {
        this.buttonClickCallback = buttonClickCallback;
    }

    private Callback buttonClickCallback;

    private int mType = 0;
    public OortUpdateDialog(Context context) {
        super(context,R.style.Dialog_Common);


    }
    public OortUpdateDialog(Context context,int type) {
        super(context,R.style.Dialog_Common);
        mType = type;


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.xupdate_dialog_update_);
        initViews();
        initListeners();
    }

    public OortUpdateDialog setPromptEntity(PromptEntity promptEntity) {
        mPromptEntity = promptEntity;
        return this;
    }

    public void setMessage(String msg) {
        mTvTitle.setText(msg);
    }


    public void setVersion(String msg) {
        mTvVersion.setText(msg);
    }

    public void setUpdateInfo(String msg) {
        mTvUpdateInfo.setText(msg);
    }


    public void setProgress(int pro) {
        mNumberProgressBar.setProgress(pro);
    }
    protected void initViews() {
        // 顶部图片
        mIvTop = findViewById(R.id.iv_top);
        // 标题
        mTvTitle = findViewById(R.id.tv_title);

        mTvVersion = findViewById(R.id.tv_version);

        mTvTitle = findViewById(R.id.tv_title);
        // 提示内容
        mTvUpdateInfo = findViewById(R.id.tv_update_info);
        // 更新按钮
        mBtnUpdate = findViewById(R.id.btn_update);
        mCancelUpdate = findViewById(R.id.btn_cancel);
        mfl = findViewById(R.id.fl_cancel);
        // 后台更新按钮
        mBtnBackgroundUpdate = findViewById(R.id.btn_background_update);
        // 忽略
        mTvIgnore = findViewById(R.id.tv_ignore);
        // 进度条
        mNumberProgressBar = findViewById(R.id.npb_progress);

        // 关闭按钮+线 的整个布局
        mLlClose = findViewById(R.id.ll_close);
        // 关闭按钮
        mIvClose = findViewById(R.id.iv_close);

        if(mType == 1){
            mTvIgnore.setVisibility(View.GONE);
        }

        if(mType == 2){
            mTvIgnore.setVisibility(View.GONE);
            mBtnUpdate.setVisibility(View.GONE);
            mNumberProgressBar.setVisibility(View.VISIBLE);
            mCancelUpdate.setVisibility(View.VISIBLE);
            mfl.setVisibility(View.VISIBLE);
        }
    }

    protected void initListeners() {
        mBtnUpdate.setOnClickListener(this);
        mBtnBackgroundUpdate.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mTvIgnore.setOnClickListener(this);
        mCancelUpdate.setOnClickListener(this);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    //====================生命周期============================//

    private String getUrl() {
        return mPrompterProxy != null ? mPrompterProxy.getUrl() : "";
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.tv_ignore){
            dismiss();
        }
        if(view.getId() == R.id.btn_update){
            mTvIgnore.setVisibility(View.GONE);
            mBtnUpdate.setVisibility(View.GONE);
            mNumberProgressBar.setVisibility(View.VISIBLE);

            if(buttonClickCallback != null){
                buttonClickCallback.ok();
            }
        }

        if(view.getId() == R.id.btn_cancel){
            dismiss();
            if(buttonClickCallback != null){
                buttonClickCallback.cancelDown();
            }
        }

    }
}
