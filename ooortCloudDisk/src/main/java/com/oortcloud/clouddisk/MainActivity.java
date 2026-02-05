package com.oortcloud.clouddisk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.oortcloud.clouddisk.transfer.down.DownloadManager;
import com.oortcloud.clouddisk.permission.PmsListener;
import com.oortcloud.clouddisk.permission.PmsManager;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PmsListener {
    private static final DownloadManager downloadManager =  DownloadManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);

        new DefaultNavigationBar.Builder(this).setTitle("同步助手").setStyle(true).builder();
        if (!PmsManager.hasPermission(this , PmsManager.getPermission())){

            PmsManager.requestPms(this , 1 , PmsManager.getPermission());
        }else {

//           getContactInfo();
            try {
//                String str = new ContactUtil(this).getContactInfo();
//                Log.v("msg" , "----------"+ str);
//                new ContactsUtil().testGetAllContact(this);
//                downloadManager.startDownload(null , null);
//                DeviceIdFactory.getInstance().getDeviceUuid();//4f0a68cd-5001-3c6f-b7d9-d9c131c8376a
                //6e3d7afb-71c7-36e6-ac59-ad19e2b84691
//                ContactUtil.syncContacts();
//                ContactUtil.getContact();
            }catch (Throwable e){
                Log.v("msg" , "----------"+ e.toString());
            }

        }

//        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                downloadManager.startDownload(null , null);
//            }
//        });
    }

    /**
     * 获取所有联系人信息
     * @return
     */
    private List<Map<String, String>> getContactInfo() {


        //存放所有的联系人
        List<Map<String, String>> list  = new ArrayList<Map<String,String>>();

        // 得到一个内容解析器
        ContentResolver resolver = getContentResolver();
        // raw_contacts uri
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");

        Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
                null, null, null);


        while (cursor.moveToNext()) {
            String contact_id = cursor.getString(0);

            if (contact_id != null) {
                //具体的某一个联系人
                Map<String, String> map = new HashMap<String, String>();

                Cursor dataCursor = resolver.query(uriData, new String[] {
                                "data1", "mimetype" }, "contact_id=?",
                        new String[] { contact_id }, null);

                while (dataCursor.moveToNext()) {
                    String data1 = dataCursor.getString(0);
                    String mimetype = dataCursor.getString(1);
                   Log.v("msg" , "data1=="+data1+"==mimetype=="+mimetype);

                    if("vnd.android.cursor.item/name".equals(mimetype)){
                        //联系人的姓名
                        map.put("name", data1);
                        Log.v("msg" , data1.toString());
                    }else if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                        //联系人的电话号码
                        map.put("phone", data1);
                        Log.v("msg" , data1.toString());
                    }

                }
                list.add(map);
                dataCursor.close();
            }

        }
        cursor.close();
        return list;
    }


    @Override
    public void permissionSuccess() {
//        getContactInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PmsManager.onRequestPmsResult(requestCode , grantResults , this);
    }
}
