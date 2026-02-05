package com.oortcloud.contacts.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oortcloud.contacts.R;

/**
 * Created by Administrator on 2016/4/21.
 */
public class TowInputDialogView extends BaseDialog {

    private TextView mTitleTv;
    private AutoCompleteTextView mContentEt;
    private AutoCompleteTextView mSecondEt;
    private Button mCommitBtn;

    // 显示群消息已读人数、私密群组、是否开启进群验证、是否显示群成员列表、允许普通群成员私聊
    /*
    暂且全都隐藏，设置到群组信息-群管理内设置
     */
    private int isRead = 0; // 0不显示 1显示(default - 不显示)
    private RelativeLayout mRlPublic;
    private int isLook = 1;// 0公开 1不公开(default - 不公开)
    private int isNeedVerify = 0;    // 0不需要 1需要(default - 不需要)
    private int isShowMember = 1;    // 0不显示 1显示(default - 显示)
    private int isAllowSendCard = 1; // 0不允许 1允许(default - 公开)
    private onSureClickLinsenter mOnClickListener;

    {
        RID = R.layout.dialog_create_group_layout;
    }

    public TowInputDialogView(Activity activity) {
        this(activity, "", "", "", null);
    }

    public TowInputDialogView(Activity activity, String title, String hint, String hint2, onSureClickLinsenter onClickListener) {
        mActivity = activity;
        initView();
        setView(title, hint, hint2);
        mOnClickListener = onClickListener;
    }
    protected void initView() {
        super.initView();
        mTitleTv =  mView.findViewById(R.id.title);
        mContentEt =  mView.findViewById(R.id.content);
        mSecondEt =  mView.findViewById(R.id.second_et);
        mCommitBtn =  mView.findViewById(R.id.sure_btn);

    }

    private void setView(String title, String hint, String hint2) {

        mCommitBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {

                if (mOnClickListener != null) {
                    mOnClickListener.onClick(mContentEt, mSecondEt, isRead, isLook, isNeedVerify, isShowMember, isAllowSendCard);
                }
            }
        });
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setHint(String hint) {
        mContentEt.setHint(hint);
    }

    public void setMaxLines(int maxLines) {
        mContentEt.setMaxLines(maxLines);
    }

    public String getContent() {
        return mContentEt.getText().toString();
    }

    // 外面需要对两个EditText做操作，给获取方法
    public EditText getE1() {
        return mContentEt;
    }

    public EditText getE2() {
        return mSecondEt;
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    // 这里有两个EditText，比较特殊，所以单击事件监听器也需要传两个EditText过去
    public interface onSureClickLinsenter {
        void onClick(EditText e1, EditText e2, int isRead, int isLook, int isNeedVerify, int isShowMember, int isAllowSendCard);
    }
}
