package com.oortcloud.clouddisk.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.widget.ImageViewCompat;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.BatchActivity;
import com.oortcloud.clouddisk.activity.CopyActivity;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.http.HttpResult;
import com.oortcloud.clouddisk.transfer.TransferHelper;
import com.oortcloud.clouddisk.utils.ToastUtils;

import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/28 17:24
 * @version： v1.0
 * @function： 批量操作选项 下载/复制移除/删除操作
 */
public class CRUDPopupWindow extends PopupWindow {


    private BatchActivity mContext;
    private ViewGroup menuView;
    private LinearLayout mDownloudLL;
    private List<FileInfo> mFileInfoData;
    public CRUDPopupWindow(final BatchActivity context , List<FileInfo> data , View view){
        super(context);

        mContext = context;
        mFileInfoData = data;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         menuView = (ViewGroup) inflater.inflate(R.layout.popup_crud, null);


        // 白色背景，黑色文字图标，
        ColorStateList foreground = ColorStateList.valueOf(Color.DKGRAY);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            View child = menuView.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) child;

                for (int j = 0; j < layout.getChildCount(); j++) {
                    View lChild = layout.getChildAt(j);
                    if (lChild instanceof ImageView) {
                        ImageViewCompat.setImageTintList((ImageView) lChild, foreground);
                    } else if (lChild instanceof TextView) {
                        ((TextView) lChild).setTextColor(foreground);
                    }
                }
            }
        }
        //设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // this.setWidth(ViewPiexlUtil.dp2px(context,200));
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);

        this.setOutsideTouchable(true);

        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.Buttom_Popwindow);

        //设置SelectPicPopupWindow弹出窗体的背景
        // 透明背景，
        this.setBackgroundDrawable(new ColorDrawable(0));

        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.7f;
        context.getWindow().setAttributes(lp);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
            }
        });

        initEvent();
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        showAsDropDown(view,
                -(getContentView().getMeasuredWidth() - view.getWidth()-50),
                -60);
    }

    private void  initEvent(){
        menuView.findViewById(R.id.copy_move_ll).setOnClickListener(v ->{
            if (mFileInfoData != null && mFileInfoData.size() > 0){
                CopyActivity.actionStart(mContext ,mFileInfoData);
                dismiss();
                mContext.mDirs.clear();
                mContext.finish();
            }else {
                ToastUtils.showContent("请选择需操作的文件");
            }

        });

        menuView.findViewById(R.id.delete_ll).setOnClickListener(v ->  {
            if (mFileInfoData != null && mFileInfoData.size() > 0){

                new CommonDialog(mContext).setTitle("删除文件").setContent("确定删除所有文件?")
                        .setConfirmClick(() ->{
                           HttpResult.delete(true , mFileInfoData);
                        }).show();

                dismiss();
            }else {
                ToastUtils.showContent("请选择需操作的文件");
            }

        });

        mDownloudLL =   menuView.findViewById(R.id.download_ll);
        if (mFileInfoData != null && mFileInfoData.size() > 0){

            for (FileInfo fileInfo : mFileInfoData){
                if (fileInfo.getIs_dir() == 1){
                    mDownloudLL.setAlpha(0.3f);
                    return;
                }
            }

            mDownloudLL.setOnClickListener(v ->  {
                TransferHelper.startDownload(mFileInfoData);
                dismiss();
//                mContext.mDirs.clear();
//                mContext.finish();
            });

        }else {
            mDownloudLL.setAlpha(0.3f);
        }


    }

}
