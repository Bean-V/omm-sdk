package com.sentaroh.android.upantool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import com.sentaroh.android.Utilities3.SafFile3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity_Task extends BaseActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = null;
    private FmPagerAdapter pagerAdapter;
    private List<HashMap> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle(R.string.file_transfer);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        titles = new String[]{getString(R.string.Transferring),getString(R.string.Transfer_succeeded)};

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                UsbHelper.getInstance().toStopTrans(false);
            }
        });
        findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabLayout.getSelectedTabPosition();
                if(position == 0){


                    new AlertDialog.Builder(Activity_Task.this)
                            .setTitle(getString(R.string.confirm_del_unfinsh_list))
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    TransFileManager.getInstance().removeAddunDone();


                                    UsbHelper.getInstance().toStopTrans(true);


                                }
                            })
                            .setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            }).show();


                }else {

                    final String[] menu = new String[]{getString(R.string.confirm_del_finsh_list_addtion)};

                    boolean[] checkItems = new boolean[]{false, false, false, false};

                    new AlertDialog.Builder(Activity_Task.this)
                            .setTitle(getString(R.string.confirm_del_finsh_list))
                            .setMultiChoiceItems(menu, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    checkItems[which] = isChecked;
                                }
                            }).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    List<Fragment_ft.TransFile> datas =  new ArrayList<>();
                                    datas.addAll(TransFileManager.getInstance().getTransDoneList());
                                    TransFileManager.getInstance().removeAddDone();


                                    if(checkItems[0]) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                for (Fragment_ft.TransFile tf : datas) {

                                                    SafFile3 saf = new SafFile3(Activity_Task.this, tf.getPath());
                                                    saf.deleteIfExists();

                                                }
                                                Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
                                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                                                sendBroadcast(intent);

                                            }
                                        }).start();
                                    }


                                }
                            })
                            .setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            }).show();



                    //TransFileManager.getInstance().removeAddDone();
                }
            }
        });





        init();
    }

    private void init() {

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.vg);

        for(int i=0;i<titles.length;i++){
                fragments.add(new Fragment_ft(i));
                tabLayout.addTab(tabLayout.newTab());
        }

        tabLayout.setupWithViewPager(viewPager,false);


        pagerAdapter = new FmPagerAdapter(fragments,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        for(int i=0;i<titles.length;i++){
            tabLayout.getTabAt(i).setText(titles[i]);
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

//                int position = tabLayout.getSelectedTabPosition();
//                Fragment_filelist wxl = (Fragment_filelist) fragments.get(position);
//
//                ArrayList sdatas = new ArrayList();
//                for(int i = 0;i<  UsbHelper.getInstance().getSelectFiles().size();i++){
//                    File file = (File) UsbHelper.getInstance().getSelectFiles().get(i);
//                    int icon = FileTool.getResIdFromFileName(file.isDirectory(),file.getName());
//                    sdatas.add(new BeanFile(file.getName(),file.getPath(),file.isDirectory() ? "" : FileTool.getFileSize(file.length()),"",icon,file));
//                }
//                wxl.setSlist(sdatas);
                //wxl.setMedit(showEdit);
                //updateCount();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });
    }


    public class FmPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public FmPagerAdapter(List<Fragment> fragmentList, FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragmentList = fragmentList;
        }

        @Override
        public int getCount() {
            return fragmentList != null && !fragmentList.isEmpty() ? fragmentList.size() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

}


