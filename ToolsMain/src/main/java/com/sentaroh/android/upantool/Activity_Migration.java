package com.sentaroh.android.upantool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.sentaroh.android.Utilities3.SafFile3;

import java.io.IOException;

public class Activity_Migration extends BaseActivity {

    private LCProgressDialog pd;
    private int delCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migration);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        tb.setTitle(getString(R.string.migrate));
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //UsbHelper.getInstance().toStopTrans(false);
            }
        });


        pd = new LCProgressDialog(this,"",0);

        findViewById(R.id.cv_p_d).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UsbHelper.getInstance().canCopyToU()) {
                    String migratePath = UsbHelper.getInstance().getUsbRootPath() + FileTool.MIGRATIONNAME;
                    SafFile3 file = new SafFile3(Activity_Migration.this,migratePath);
                    if(file.exists()){
                        ViewTool.confirm_to_action(Activity_Migration.this, null, getString(R.string.migrate_del_usb_tip), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent in = new Intent(Activity_Migration.this, Activity_migrate_statu.class);
                                in.putExtra("type", "3");
                                startActivity(in);
                            }
                        });

                        return;
                    }

                    Intent in = new Intent(Activity_Migration.this, Activity_migrate_selector.class);
                    in.putExtra("type", "1");
                    startActivity(in);
                }else{
                    toast(getString(R.string.no_usb));
                }
            }
        });

        findViewById(R.id.cv_d_p).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!UsbHelper.getInstance().canCopyToU()){
                    toast(getString(R.string.no_usb));
                    return;
                }
                {
                    String migratePath = UsbHelper.getInstance().getUsbRootPath() + FileTool.MIGRATIONNAME;
                    SafFile3 file = new SafFile3(Activity_Migration.this, migratePath);
                    if (!file.exists()) {

                        toast(getString(R.string.migrate_no_data));
                        return;
                    }
                }
                {
                    String migratePath = UsbHelper.getInstance().getSdRootPath() + FileTool.MIGRATIONNAME;
                    SafFile3 file = new SafFile3(Activity_Migration.this, migratePath);
                    if (file.exists()) {

                        ViewTool.confirm_to_action(Activity_Migration.this, null, getString(R.string.migrate_del_local_tip), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent in = new Intent(Activity_Migration.this, Activity_migrate_statu.class);
                                        in.putExtra("type", "4");
                                        startActivity(in);
                                    }
                                });


                        return;
                    }
                }

                Intent in = new Intent(Activity_Migration.this,Activity_migrate_selector.class);
                in.putExtra("type","2");
                startActivity(in);
            }
        });

        delCount = 0;
        findViewById(R.id.cv_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!UsbHelper.getInstance().canCopyToU()){
                    toast(getString(R.string.no_usb));
                    return;
                }
                if(UsbHelper.getInstance().canCopyToU()){

                    {
                        String migratePath = UsbHelper.getInstance().getUsbRootPath() + FileTool.MIGRATIONNAME;
                        SafFile3 file = new SafFile3(Activity_Migration.this, migratePath);
                        if (!file.exists()) {

                            toast(getString(R.string.migrate_no_data));
                            return;
                        }
                    }
                    AlertDialog alert = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Migration.this);
                    alert = builder.setTitle(R.string.tip)
                            .setMessage(R.string.confirm_del_file)
                            .setNegativeButton(R.string.fm_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            String migratePath = UsbHelper.getInstance().getUsbRootPath() + FileTool.MIGRATIONNAME; //+ "/" +  UsbHelper.getInstance().getUsbUUid();
                                            SafFile3 file = new SafFile3(Activity_Migration.this, migratePath);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.show();
                                                    pd.setProgress(0);
                                                    pd.setMessage(getString(R.string.read_files_to_del));

                                                }
                                            });
                                            int allCount = FileTool.getAllFileInDir_(file).size();

                                            delCount = 0;
                                            while (file.exists()) {
                                                if (!file.exists()) {
                                                    //toast(get);

                                                    return;
                                                }
                                                try {
                                                    FileTool.deleteUsbFile_(file, new FileTool.Callback() {
                                                        @Override
                                                        public void callback(int i) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    delCount++;
                                                                    pd.setProgress(delCount * 100 / allCount);
                                                                    pd.setMessage(getString(R.string.deleting));

                                                                }
                                                            });
                                                        }
                                                    });
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    pd.dismiss();

                                                }
                                            });
                                        }
                                    }).start();


                                }
                            }).create();             //创建AlertDialog对象
                    alert.show();



                }
            }
        });
    }
}