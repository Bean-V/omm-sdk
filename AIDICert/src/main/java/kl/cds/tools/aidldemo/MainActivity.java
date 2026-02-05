package kl.cds.tools.aidldemo;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kl.cds.utils.Settings;
import koal.cert.tools.ICertManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvAppInfo;
    private TextView tvICertManag;
    private Button btnCertManager;
    private Button btnCertManagerSVS;
    private Button little_tools;

    public static ICertManager certManager = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connCertManager);
        System.out.println("MainActivity.onDestroy === unbindService");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRemoteService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);

        tvAppInfo = findViewById(R.id.tvAppInfo);
        tvICertManag = findViewById(R.id.tvICertManag);
        btnCertManager = findViewById(R.id.cert_manage);
        btnCertManagerSVS = findViewById(R.id.cert_manager_svs);
        little_tools = findViewById(R.id.little_tools);
        findViewById(R.id.cert_manage).setOnClickListener(this);
        findViewById(R.id.cert_manager_svs).setOnClickListener(this);
        findViewById(R.id.little_tools).setOnClickListener(this);
//        findViewById(R.id.little_tools);

        loadData();
    }



    void popSvsDlg() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View dlgView = inflater.inflate(R.layout.svs_dialog, null);

        final EditText svsIp = dlgView.findViewById(R.id.etSvsIp);
        final EditText svsPort = dlgView.findViewById(R.id.etSvsPort);
        final EditText pin = dlgView.findViewById(R.id.etPin);

        svsIp.setText(Settings.SVS_SERVER_HOST);
        svsPort.setText(Settings.SVS_SERVER_PORT);
        pin.setText(Settings.PIN);

        new AlertDialog.Builder(MainActivity.this)
                .setView(dlgView)
                .setTitle("SVS相关设置")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 仅save到内存中
                        Settings.SVS_SERVER_HOST = svsIp.getText().toString();
                        Settings.SVS_SERVER_PORT = svsPort.getText().toString();
                        Settings.PIN = pin.getText().toString();

                        Intent intent = new Intent(MainActivity.this, CertManagerSVSActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private IBinder.DeathRecipient mCertManagerDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            // TODO Auto-generated method stub
            System.out.println("mCertManagerDeathRecipient.binderDied");

            tvICertManag.setText("证书管理服务：未绑定");
            btnCertManager.setEnabled(false);
            btnCertManagerSVS.setEnabled(false);

            if (certManager == null)
                return;

            try {
                certManager.asBinder().linkToDeath(mCertManagerDeathRecipient, 0);
                certManager = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // 重新绑定远程服务
            setupCertManagerService();
        }
    };

    private ServiceConnection connCertManager = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            System.out.println("ICertManager onServiceConnected ");

            try {
                service.linkToDeath(mCertManagerDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            certManager = ICertManager.Stub.asInterface(service);

            tvICertManag.setText("证书管理服务：已绑定");
            btnCertManager.setEnabled(true);
            btnCertManagerSVS.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            certManager = null;
        }
    };

    void setupRemoteService() {
        //启动证书管理服务
        setupCertManagerService();
    }

    private void setupCertManagerService() {
        Intent intent = new Intent();
        intent.setAction("koal.cert.tools.CertManagerService");
        intent.setPackage(Settings.REMOTE_APP_PKG_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, connCertManager, Context.BIND_AUTO_CREATE);
    }

    void loadData() {
        String appInfo;
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(Settings.REMOTE_APP_PKG_NAME, 0);
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            appInfo = String.format("DemoInfo:\nversion = %s%s\n移动证书助手： 已安装\n版本名称： %s\n版本号： %d", pi.versionName, "-A", packageInfo.versionName, packageInfo.versionCode);
        } catch (NameNotFoundException e) {
            appInfo = "移动证书助手：未安装";
            e.printStackTrace();
        }
        tvAppInfo.setText(appInfo);
        tvICertManag.setText("证书管理服务：未绑定");
        btnCertManager.setEnabled(false);
        btnCertManagerSVS.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        int id = view.getId();
        if (id == R.id.cert_manage) {//证书管理
            intent.setClass(this, CertManagerActivity.class);
        } else if (id == R.id.cert_manager_svs) {//SVS签名验签
            popSvsDlg();
            return;
        } else if (id == R.id.little_tools) {//小工具
            intent.setClass(this, LittleToolsActivity.class);
        }
        startActivity(intent);
    }
}







