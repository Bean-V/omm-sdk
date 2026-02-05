package com.sentaroh.android.upantool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Data;
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

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity_contactbackup extends BaseActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = null;
    private FmPagerAdapter pagerAdapter;
    private List<HashMap> datas;
    private Fragment_contact_backuplist unBackup;
    private Fragment_contact_backuplist backuped;


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                case UploadUtil.FAIL_TYPE:
//                    ToastUtils.showContent("上传失败");
//                    mUploadDialog.dismiss();
//                    break;
//                case UploadUtil.SUCCESS_TYPE:
//                    if (msg.arg1 == UploadUtil.CONTACT_TYPE) {
//                        mContactTV.setText("通讯录已发送");
//                    } else if (msg.arg1 == UploadUtil.TEXT_TYPE) {
//                        mTxtTV.setText("文本已发送");
//                    } else if (msg.arg1 == UploadUtil.PIC_TYPE) {
//                        mTxtTV.setText("图片已发送");
//                    } else if (msg.arg1 == UploadUtil.ACCESSORY_TYPE) {
//                        mTxtTV.setText("文件已发送");
//                    }
//
//                    break;

                default:
                    progressDialog.setProgress(msg.arg1);
                    break;

            }

        }
    };
    private LCProgressDialog progressDialog;
    private List unBackuplist;
    private List backuplist;
    private String contactPath;
    private String usbDesContactPath;
    private List tmp0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactbackup);


        progressDialog = new LCProgressDialog(this,"读取中",0);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setStatusBarLight(true);
        tb.setTitle(R.string.contact_backup_title);
        tb.setNavigationIcon(R.mipmap.ic_fm_back);

        titles = new String[]{getString(R.string.back_up_contact),getString(R.string.contactSys)};

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
        //getCntact
        unBackuplist = new ArrayList<>();
        backuplist = new ArrayList();
        tmp0 = ContactUtil.getAllContacts__(this, new SelectedItemCollection.ProgressListioner() {
            @Override
            public void progress(int progress) {

            }

            @Override
            public void finsh(List<String> filePath) {

            }
        });



        Gson gson = new Gson();
        String json = gson.toJson(unBackuplist);

        try {
            String yisuDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FileUtil.YISU_CACHE;

            File yisuDirFile = new File(yisuDir);
            if(!yisuDirFile.exists()){
                yisuDirFile.mkdir();
            }

            contactPath = yisuDir+ "/" + FileUtil.CONTACT_TXT;

            usbDesContactPath = UsbHelper.getInstance().getRootFile().getPath() + "/" +"yisu" + "/" + FileUtil.CONTACT_TXT;


            SafFile3 sf = new SafFile3(this, usbDesContactPath);
            if(sf.exists()){
                FileTool.copyWithPath(usbDesContactPath,contactPath);
            }else{

            }

            if(new File(contactPath).exists()) {


            }else {
                unBackuplist.addAll(tmp0);
                unBackup.setMlist(unBackuplist);
            }



            String json01 = _read_(contactPath);

            Type listType = new TypeToken<List<MyContact>>(){}.getType();
            List  lis = gson.fromJson(json01, listType);
            if(lis != null){
                backuplist.addAll(lis);
            }
            for(Object o : tmp0){
                MyContact contact = (MyContact) o;

                boolean found = false;
                for(Object o1 : backuplist){
                    MyContact contact1 = (MyContact) o1;
                    if(contact1.getPhone().equals(contact.getPhone()) && contact1.getName().equals(contact.getName())){
                        found = true;
                        break;
                    }

                }
                if(!found){
                    unBackuplist.add(contact);
                }
            }
            unBackup.setMlist(unBackuplist);
            backuped.setMlist(backuplist);








            //_write_(contactPath,json);
        } catch (IOException e) {
            e.printStackTrace();
        }


       // backuped.setMlist(unBackuplist);
        //List tmp1



//        List backuplist = new ArrayList<>();
//
//        //copyto coche
//        SafFile3 sf = new SafFile3(this,UsbHelper.getInstance().getRootFile().getPath() + "/contactbackup/" + FileUtil.CONTACT_TXT)
//
//        String desPath = UsbHelper.getInstance().getSdRootPath() + '/' + FileUtil.YISU_CACHE +"/" + FileUtil.CONTACT_TXT;
//        if(sf.exists()){
//
//            FileTool.copyWithPath(sf.getPath(),desPath);
//        }
//
//
//        String contacts =  FileUtil.read(desPath);









    }

    private void init() {

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.vg);

       // for(int i=0;i<titles.length;i++){
        {
            unBackup = new Fragment_contact_backuplist();
            unBackup.setBtnTitle(getString(R.string.back_up_contact));
            unBackup.setBottomClickListener(new Fragment_contact_backuplist.BottomClickListener() {
                @Override
                public void click() {
                    //beifen

                    toast("beifen");
                }
            });
            fragments.add(unBackup);
            tabLayout.addTab(tabLayout.newTab());
        }
        {
            backuped =
                    new Fragment_contact_backuplist();

            backuped.setBtnTitle(getString(R.string.contactSys));
            backuped.setBottomClickListener(new Fragment_contact_backuplist.BottomClickListener() {
                @Override
                public void click() {
                    //beifen

                    toast("tongbu");
                }
            });
            fragments.add(backuped);
            tabLayout.addTab(tabLayout.newTab());
        }
       // }

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

        unBackup.setBottomClickListener(new Fragment_contact_backuplist.BottomClickListener() {
            @Override
            public void click() {

                backuplist.addAll(unBackuplist);

                try {
                    _write_(contactPath,new Gson().toJson(backuplist));

                    unBackuplist.clear();
                    unBackup.setMlist(unBackuplist);

                    backuped.setMlist(backuplist);

                    FileTool.copyWithPath(contactPath,usbDesContactPath);

                    new File(contactPath).delete();


                    Toast.makeText(Activity_contactbackup.this, "备份成功", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        backuped.setBottomClickListener(new Fragment_contact_backuplist.BottomClickListener() {
            @Override
            public void click() {

                for(Object o : backuplist){

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
                }

                Toast.makeText(Activity_contactbackup.this, "添加成功", Toast.LENGTH_SHORT).show();



            }
        });


    }


    public void addContact(String name, String phoneNumber) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();

        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
        // 联系人的Email地址
        //values.put(Email.DATA, "zhangphil@xxx.com");
        // 电子邮件的类型
        //values.put(Email.TYPE, Email.TYPE_WORK);
        // 向联系人Email URI添加Email数据
        getContentResolver().insert(Data.CONTENT_URI, values);
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