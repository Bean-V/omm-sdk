package com.oort.weichat.ui.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.bean.User;
import com.oort.weichat.helper.UsernameHelper;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.groupchat.FaceToFaceGroup;
import com.oort.weichat.ui.other.QRcodeActivity;
import com.oort.weichat.ui.tool.ButtonColorChange;
import com.oortcloud.basemodule.user.UserInfoUtils;

/**
 * 添加好友
 */
public class UserSearchActivity extends BaseActivity implements View.OnClickListener {
    private EditText mKeyWordEdit;
    private TextView mKeyWordText;
    private Button mSearchBtn;
    private boolean isPublicNumber;

    public static void start(Context ctx, boolean isPublicNumber) {
        Intent intent = new Intent(ctx, UserSearchActivity.class);
        intent.putExtra("isPublicNumber", isPublicNumber);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        isPublicNumber = getIntent().getBooleanExtra("isPublicNumber", false);
        initActionBar();
        initView();
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        if (isPublicNumber) {
            tvTitle.setText(R.string.search_public_number);
        } else {
            tvTitle.setText(getString(R.string.add_friend));
        }
    }

    private void initView() {
        mKeyWordEdit = (EditText) findViewById(R.id.keyword_edit);
        // 获取焦点，键盘弹出
        mKeyWordEdit.requestFocus();
        mKeyWordText = (TextView) findViewById(R.id.keyword_text);
        mSearchBtn = (Button) findViewById(R.id.search_btn);
        ButtonColorChange.colorChange(this, mSearchBtn);
        if (isPublicNumber) {
            mKeyWordText.setText(R.string.tip_search_public_number);
            mKeyWordEdit.setHint(R.string.hint_search_public_number);
        } else {
            UsernameHelper.initSearchLabel(mKeyWordText, coreManager.getConfig());
            UsernameHelper.initSearchEdit(mKeyWordEdit, coreManager.getConfig());
        }
        mSearchBtn.setOnClickListener(this);


        TextView tv_p = findViewById(R.id.tv_phone);
        tv_p.setText(UserInfoUtils.getInstance(this).getLoginUserInfo().getOort_phone());

        findViewById(R.id.ll_00).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User mUser = coreManager.getSelf();
                Intent intent2 = new Intent(UserSearchActivity.this, QRcodeActivity.class);
                intent2.putExtra("isgroup", false);
                if (!TextUtils.isEmpty(mUser.getAccount())) {
                    intent2.putExtra("userid", mUser.getAccount());
                } else {
                    intent2.putExtra("userid", mUser.getUserId());
                }
                intent2.putExtra("userAvatar", mUser.getUserId());
                intent2.putExtra("nickName", mUser.getNickName());
                intent2.putExtra("sex", mUser.getSex());
                startActivity(intent2);
                //startActivity(new Intent(UserSearchActivity.this, FaceToFaceGroup.class));
            }
        });

        findViewById(R.id.ll_01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserSearchActivity.this, FaceToFaceGroup.class));
            }
        });
        findViewById(R.id.ll_02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.requestQrCodeScan(UserSearchActivity.this);
            }
        });
        findViewById(R.id.ll_03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicNumberSearchActivity.start(mContext);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_btn) {
            if (TextUtils.isEmpty(mKeyWordEdit.getText().toString().trim())) {
                return;
            }
            Intent intent = new Intent(mContext, UserListGatherActivity.class);
            intent.putExtra("key_word", mKeyWordEdit.getText().toString());
            startActivity(intent);
        }
    }
}
