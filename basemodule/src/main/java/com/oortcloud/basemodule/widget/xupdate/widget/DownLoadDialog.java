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

import static com.oortcloud.basemodule.widget.xupdate.widget.UpdateDialogFragment.REQUEST_CODE_REQUEST_PERMISSIONS;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.oortcloud.basemodule.R;
import com.oortcloud.basemodule.widget.xupdate._XUpdate;
import com.oortcloud.basemodule.widget.xupdate.entity.PromptEntity;
import com.oortcloud.basemodule.widget.xupdate.entity.UpdateEntity;
import com.oortcloud.basemodule.widget.xupdate.proxy.IPrompterProxy;
import com.oortcloud.basemodule.widget.xupdate.utils.ColorUtils;
import com.oortcloud.basemodule.widget.xupdate.utils.DrawableUtils;
import com.oortcloud.basemodule.widget.xupdate.utils.UpdateUtils;
import com.oortcloud.basemodule.widget.xupdate.widget.IDownloadEventHandler;
import com.oortcloud.basemodule.widget.xupdate.widget.NumberProgressBar;
import com.oortcloud.basemodule.widget.xupdate.widget.WeakFileDownloadListener;

import java.io.File;

/**
 * 版本更新弹窗
 *
 * @author xuexiang
 * @since 2018/7/24 上午9:29
 */
public class DownLoadDialog extends Dialog implements View.OnClickListener {

    //======顶部========//
    /**
     * 顶部图片
     */
    private ImageView mIvTop;
    /**
     * 标题
     */
    private TextView mTvTitle;
    //======更新内容========//
    /**
     * 版本更新内容
     */
    private TextView mTvUpdateInfo;
    /**
     * 版本更新
     */
    private Button mBtnUpdate;
    /**
     * 后台更新
     */
    private Button mBtnBackgroundUpdate;
    /**
     * 忽略版本
     */
    private TextView mTvIgnore;
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

    /**
     * 获取更新提示
     *
     * @param updateEntity  更新信息
     * @param prompterProxy 更新代理
     * @param promptEntity  提示器参数信息
     * @return 更新提示
     */

    public DownLoadDialog(Context context) {
        super(context,R.style.Dialog_Common);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.xupdate_dialog_download);
        initViews();
        initListeners();
    }

    public DownLoadDialog setPromptEntity(PromptEntity promptEntity) {
        mPromptEntity = promptEntity;
        return this;
    }

    public void setMessage(String msg) {
        mTvTitle.setText(msg);
    }

    public void setProgress(int pro) {
        mNumberProgressBar.setProgress(pro);
    }
    protected void initViews() {
        // 顶部图片
        mIvTop = findViewById(R.id.iv_top);
        // 标题
        mTvTitle = findViewById(R.id.tv_title);
        // 提示内容
        mTvUpdateInfo = findViewById(R.id.tv_update_info);
        // 更新按钮
        mBtnUpdate = findViewById(R.id.btn_update);
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
    }

    protected void initListeners() {
        mBtnUpdate.setOnClickListener(this);
        mBtnBackgroundUpdate.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mTvIgnore.setOnClickListener(this);

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

    }
}
