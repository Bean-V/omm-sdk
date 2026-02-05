package com.oortcloud.appstore;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.oort.weichat.ui.base.BaseActivity;
import com.oortcloud.appstore.activity.AppManagerActivity;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.fragment.TableFragment;
import com.oort.weichat.R;
import com.oortcloud.appstore.utils.AppManager;
import com.oortcloud.appstore.utils.SllCallback;
import com.oortcloud.appstore.utils.SllInterface;

import koal.ssl.IAutoService;


public class AppStoreActivity extends BaseActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appstore_activity);
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        Intent intent=getIntent();
        AppStoreInit.store_token = intent.getStringExtra("token");
        AppStoreInit.store_uuid = intent.getStringExtra("UUID");

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.framelayout , new TableFragment());
        transaction.commit();
        setStatusBarLight(true);

        AppStatu.homeRefrash = 1;

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unbind service

    }




}
