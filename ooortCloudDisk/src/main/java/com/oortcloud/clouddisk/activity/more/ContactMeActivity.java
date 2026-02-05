package com.oortcloud.clouddisk.activity.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.BaseActivity;
import com.oortcloud.clouddisk.databinding.ActivityContactMeLayoutBinding;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;


/**
 * @filename:
 * @author: zzj/@date: 2020/12/14 17:30
 * @version： v1.0
 * @function：联系我们
 */
public class ContactMeActivity extends BaseActivity {
    private TextView mOfficePhone;
    private TextView mOperationPhone;

    @Override
    protected void initView(){
        mOfficePhone = findViewById(R.id.office_phone_tv);
        mOperationPhone = findViewById(R.id.operation_phone_tv);

    }

    @Override
    protected ViewBinding getViewBinding() {
        return ActivityContactMeLayoutBinding.inflate(getLayoutInflater());
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_contact_me_layout;
//    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initActionBar() {
        new DefaultNavigationBar.Builder(this).setTitle("联系我们").builder();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent(View v) {

        findViewById(R.id.office_phone_ll).setOnClickListener(view -> {
            Intent Intent =  new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + mOfficePhone.getText().toString().trim()));//跳转到拨号界面，同时传递电话号码
            startActivity(Intent);
        });


        findViewById(R.id.operation_phone_ll).setOnClickListener(view -> {
            Intent Intent =  new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + mOperationPhone.getText().toString().trim()));//跳转到拨号界面，同时传递电话号码
            startActivity(Intent);
        });

    }
}
