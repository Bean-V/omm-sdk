package com.sentaroh.android.upantool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.contact.ContactUtil;
import com.sentaroh.android.upantool.contact.FileUtil;
import com.sentaroh.android.upantool.contact.Fragment_contact_backuplist;
import com.sentaroh.android.upantool.contact.MyContact;
import com.zhihu.matisse.internal.model.SelectedItemCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class activity_contactbackup_ extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = null;
    private FmPagerAdapter pagerAdapter;
    private List<HashMap> datas;
    private Fragment_contact_files unBackup;
    private Fragment_contact_files backuped;

    private LCProgressDialog progressDialog;
    private List unBackuplist;
    private List backuplist;
    private String contactPath;
    private String usbDesContactPath;
    private List tmp0;
    private String usbDesContactDirPath;
    private ArrayList datas1;
    private String yisuDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_contactbackup);


        progressDialog = new LCProgressDialog(this, getString(R.string.reading), 0);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle(R.string.contact_backup_title);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        titles = new String[]{getString(R.string.back_up_contact), getString(R.string.contactSys)};

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();

        initData();
    }

    private void initData() {
        if(UsbHelper.getInstance().canCopyToU()) {
            usbDesContactDirPath = UsbHelper.getInstance().getRootFile().getPath() + "/" + "yisu";
        }

        yisuDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FileUtil.YISU_CACHE;
        refreshData();

    }

    private void init() {

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.vg);

        // for(int i=0;i<titles.length;i++){
        {
            unBackup = new Fragment_contact_files();
            unBackup.setBtnTitle(getString(R.string.back_up_contact));
            unBackup.setBottomClickListener(new Fragment_contact_backuplist.BottomClickListener() {
                @Override
                public void click() {
                    //beifen

                    //toast("beifen");
                }
            });
            fragments.add(unBackup);
            tabLayout.addTab(tabLayout.newTab());
        }
        {
            backuped =
                    new Fragment_contact_files();

            backuped.setBtnTitle(getString(R.string.contactSys));

            backuped.setHideBotttom(true);
            backuped.setBottomClickListener(new Fragment_contact_backuplist.BottomClickListener() {
                @Override
                public void click() {
                    //beifen

                    //toast("tongbu");
                }
            });

            backuped.setItemClikListener(new Fragment_contact_files.ItemClikListener() {
                @Override
                public void itemClikListener(int postion) {

                    SafFile3 sf = (SafFile3) datas1.get(postion);

                    android.app.AlertDialog alert = null;
                    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(activity_contactbackup_.this);
                    alert = builder.setTitle(getString(R.string.tip))
                            .setMessage(getString(R.string.contactSys) + ":" + sf.getName())
                            .setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {



                                    LCProgressDialog pg = new LCProgressDialog(activity_contactbackup_.this,"恢复中",0);
                                    pg.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            readDataToSysContact(sf, new FileTool.ProgressListener() {
                                                @Override
                                                public void onProgress(int var1) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            pg.setProgress(var1);
                                                        }
                                                    });
                                                }
                                            });

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pg.dismiss();
                                                    toast(getString(R.string.success));


                                                }
                                            });

                                        }
                                    }).start();

                                }
                            }).show();

                }
            });
            fragments.add(backuped);
            tabLayout.addTab(tabLayout.newTab());
        }
        // }

        tabLayout.setupWithViewPager(viewPager, false);


        pagerAdapter = new FmPagerAdapter(fragments, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        for (int i = 0; i < titles.length; i++) {
            tabLayout.getTabAt(i).setText(titles[i]);
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });

        unBackup.setBottomClickListener(new Fragment_contact_backuplist.BottomClickListener() {
            @Override
            public void click() {

                LCProgressDialog pd = new LCProgressDialog(activity_contactbackup_.this,getString(R.string.reading),0);
                pd.show();




                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                createContactFile(new SelectedItemCollection.ProgressListioner() {
                                    @Override
                                    public void progress(int progress) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pd.setProgress(progress);
                                            }
                                        });
                                    }

                                    @Override
                                    public void finsh(List<String> filePath) {

                                    }
                                });

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshData();
                                        pd.dismiss();
                                        Toast.makeText(activity_contactbackup_.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }).start();





//                backuplist.addAll(unBackuplist);
//
//                try {
//                    _write_(contactPath, new Gson().toJson(backuplist));
//
//                    unBackuplist.clear();
//                    unBackup.setMlist(unBackuplist);
//
//                    backuped.setMlist(backuplist);
//
//                    FileTool.copyWithPath(contactPath, usbDesContactPath);
//
//                    new File(contactPath).delete();
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

        backuped.setBottomClickListener(new Fragment_contact_backuplist.BottomClickListener() {
            @Override
            public void click() {

                for (Object o : backuplist) {

                    MyContact contact = (MyContact) o;

                    boolean found = false;
                    for (Object o1 : tmp0) {
                        MyContact contact1 = (MyContact) o1;
                        if (contact1.getPhone().equals(contact.getPhone()) && contact1.getName().equals(contact.getName())) {
                            found = true;
                            break;
                        }

                    }
                    if (!found) {
                        addContact(contact.getName(), contact.getPhone());


                    }
                }

                Toast.makeText(activity_contactbackup_.this, "添加成功", Toast.LENGTH_SHORT).show();


            }
        });


    }


    private void createContactFile(SelectedItemCollection.ProgressListioner progressListioner) throws IOException {


        tmp0 = ContactUtil.getAllContacts__(this, new SelectedItemCollection.ProgressListioner() {
            @Override
            public void progress(int progress) {
                if(progressListioner != null){
                    progressListioner.progress(progress);
                }
            }

            @Override
            public void finsh(List<String> filePath) {

            }
        });


        Gson gson = new Gson();
        String json = gson.toJson(tmp0);



        File yisuDirFile = new File(yisuDir);
        if (!yisuDirFile.exists()) {
            yisuDirFile.mkdir();
        }

        String fileName = "contacts_backup" + TimeUtil.getCurrentTime("_yyyy-MM-dd_hh_mm_ss")  + ".txt";
        contactPath = yisuDir + "/" + fileName;

        _write_(contactPath, json);




        if(UsbHelper.getInstance().canCopyToU()) {
            usbDesContactPath = UsbHelper.getInstance().getRootFile().getPath() + "/" + "yisu" + "/" + fileName;
            FileTool.copyWithPath(contactPath, usbDesContactPath);
        }


    }

    public void refreshData(){




        String path = UsbHelper.getInstance().canCopyToU() ? usbDesContactDirPath : yisuDir;
        SafFile3 sf = new SafFile3(this,  path);

        datas1 = new ArrayList();
        if (!sf.exists()) {

            sf.mkdir();
        }

        ArrayList<SafFile3> tmp = new ArrayList();

        tmp.addAll(Arrays.asList(sf.listFiles()));
        for(SafFile3 sf1 : tmp){

            if(sf1.getName().contains("contacts_backup") && sf1.getName().endsWith(".txt")){
                datas1.add(sf1);
            }
        }
        //datas1.addAll(Arrays.asList(sf.listFiles()));

        datas1 = (ArrayList) FileTool.listUsbfilesortbymodifytime_list(datas1);


        unBackup.setMlist(datas1);
        backuped.setMlist(datas1);
    }



    void readDataToSysContact(SafFile3 file, FileTool.ProgressListener progressListener){
        String yisuDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FileUtil.YISU_CACHE;
        File yisuDirFile = new File(yisuDir);
        if(!yisuDirFile.exists()){
            yisuDirFile.mkdir();
        }
        contactPath = yisuDir+ "/" + file.getName();
        FileTool.copyWithPath(file.getPath(),contactPath);
        tmp0 = ContactUtil.getAllContacts__(this, new SelectedItemCollection.ProgressListioner() {
            @Override
            public void progress(int progress) {
                if(progressListener != null){
                    progressListener.onProgress(progress);
                }
            }

            @Override
            public void finsh(List<String> filePath) {

            }
        });
        Gson gson = new Gson();
        ArrayList backuplist_ = new ArrayList<>();
        try {
            String json01 = _read_(contactPath);

            Type listType = new TypeToken<List<MyContact>>(){}.getType();
            List  lis = gson.fromJson(json01, listType);
            if(lis != null){
                backuplist_.addAll(lis);
            }

            for(Object o : backuplist_){
                MyContact contact = (MyContact) o;

                boolean found = false;
                for(Object o1 : tmp0){
                    MyContact contact1 = (MyContact) o1;
                    if(contact1.getPhone().equals(contact.getPhone()) && contact1.getName().equals(contact.getName())){
                        found = true;
                        break;
                    }

                }
                if(!found){
                    addContact(contact.getName(),contact.getPhone());
                }

                if(progressListener != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        progressListener.onProgress(backuplist_.indexOf(o) * 100 / backuplist_.size());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addContact(String name, String phoneNumber) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();

        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        // 联系人的Email地址
        //values.put(Email.DATA, "zhangphil@xxx.com");
        // 电子邮件的类型
        //values.put(Email.TYPE, Email.TYPE_WORK);
        // 向联系人Email URI添加Email数据
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    public static File _write_(String filePath , String content) throws IOException {

        String file =  filePath;
        OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");

        oStreamWriter.append(content);
        oStreamWriter.close();

        return new File(file);
    }

    public static String _read_(String path) throws IOException {

        if (!TextUtils.isEmpty(path)){
            BufferedReader bre=new BufferedReader(new FileReader(new File(path)));
            String str;

            StringBuilder sb = new StringBuilder();

            while ((str = bre.readLine()) != null){
                sb.append(str +"\n");
            }
            if (bre != null){
                bre.close();
            }

            return sb.toString();
        }
        return "";
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