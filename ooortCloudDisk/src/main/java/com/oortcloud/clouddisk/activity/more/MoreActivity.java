package com.oortcloud.clouddisk.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.BaseActivity;
import com.oortcloud.clouddisk.databinding.ActivityMoreLayoutBinding;
import com.oortcloud.clouddisk.utils.AppInfoUtil;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;


/**
 * @filename:
 * @author: zzj/@date: 2020/12/14 17:30
 * @version： v1.0
 * @function：关于
 */
public class MoreActivity extends BaseActivity {

    private TextView mVersion;

    @Override
    protected ViewBinding getViewBinding() {
        return ActivityMoreLayoutBinding.inflate(getLayoutInflater());
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_more_layout;
//    }


    @Override
    protected void initBundle(Bundle bundle) {

    }


    @Override
    protected void initActionBar() {
        new DefaultNavigationBar.Builder(this).setTitle("关于").builder();
    }

    @Override
    protected void initView(){
        mVersion = findViewById(R.id.version_tv);
       initEvent(null);


    }
    @Override
    protected void initData(){
        mVersion.setText("Version: " + AppInfoUtil.packageCode(this));
    }



    @Override
    protected void initEvent(View v) {
        findViewById(R.id.version_suggest_tv).setOnClickListener(view ->  {

            startActivity(new Intent(MoreActivity.this , VersionCodeActivity.class));
        });

        findViewById(R.id.contact_we_tv).setOnClickListener(view ->  {
            startActivity(new Intent(MoreActivity.this , ContactMeActivity.class));
        });

    }
}
