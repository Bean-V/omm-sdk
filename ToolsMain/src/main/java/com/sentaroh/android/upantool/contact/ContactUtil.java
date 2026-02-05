package com.sentaroh.android.upantool.contact;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sentaroh.android.upantool.BaseApplication;
import com.zhihu.matisse.internal.model.SelectedItemCollection;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/12/10 10:52
 * @version： v1.0
 * @function： 出来通讯录数据
 */
public class ContactUtil {



    public static ArrayList<MyContact> getAllContacts__(Context context, SelectedItemCollection.ProgressListioner progress) {
        ArrayList<MyContact> contacts = new ArrayList<MyContact>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            //新建一个联系人实例



            MyContact temp = new MyContact();
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String string = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            temp.name = name;
            //获取联系人电话号码
            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (phoneCursor.moveToNext()) {
                String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone = phone.replace("-", "");
                phone = phone.replace(" ", "");
                if(temp.phone == null) {
                    temp.phone = phone;
                }
            }
            temp.photo = string;

            //获取联系人备注信息
            Cursor noteCursor = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Nickname.NAME},
                    ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'",
                    new String[]{contactId}, null);
            if (noteCursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String note = noteCursor.getString(noteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                    temp.note = note;
                    //Log.i("note:", note);
                } while (noteCursor.moveToNext());
            }


            if(temp.getPhone() != null && temp.getName() != null) {
                contacts.add(temp);
            }

            i ++ ;

            if(progress != null){
                progress.progress(i * 100 / cursor.getCount());
            }
            //记得要把cursor给close掉
            phoneCursor.close();
            noteCursor.close();
        }
        cursor.close();
        return contacts;
    }



    // ContactsContract.Contacts.CONTENT_URI= content://com.android.contacts/contacts;
    // ContactsContract.Data.CONTENT_URI = content://com.android.contacts/data;
    /**
     * 获取联系人信息，并把数据转换成json数据
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("Range")
    public static File getAllContacts(Handler  handler , Context context) throws JSONException , IOException {
        HashSet<List> listData = new HashSet<>();
        List<ContactInfo<Object>> list = null;
        String mimetype ;
        int raw_contact_id;
        int oldrid = 0;
        int limit = 0 ;
        // 1.查询通讯录所有联系人信息，通过id排序，我们看下android联系人的表就知道，所有的联系人的数据是由RAW_CONTACT_ID来索引开的
        // 所以，先获取所有的人的RAW_CONTACT_ID
//        Uri uri = ContactsContract.Data.CONTENT_URI; // 联系人Uri；
        Uri uri = Data.CONTENT_URI; // 联系人Uri；
        Cursor cursor = context.getContentResolver().query(uri,
                null, null, null, Data.RAW_CONTACT_ID);
        if (cursor.moveToFirst()) {
            do {
                raw_contact_id = cursor.getInt(cursor
                        .getColumnIndex(Data.RAW_CONTACT_ID));

                if (oldrid != raw_contact_id) {
                    list = new ArrayList<>();

                    listData.add(list);

                    oldrid = raw_contact_id;
                }
                Log.v("msg" , cursor.getPosition()+"---");
                ContactInfo<Object> contactInfo = new ContactInfo<>();
                list.add(contactInfo);
                mimetype = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE)); // 取得mimetype类型,扩展的数据都在这个类型里面

                contactInfo.setRaw_contact_id( raw_contact_id);
                contactInfo.setMimetype( mimetype);
                // 1.1,拿到联系人的各种名字
                if (StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.DISPLAY_NAME));
                    contactInfo.setData1( data1);
                    String data2 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.GIVEN_NAME));
                    contactInfo.setData2( data2);
                    String data3 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.FAMILY_NAME));
                    contactInfo.setData3( data3);
                    String data4 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.PREFIX));
                    contactInfo.setData4( data4);

                    String data5 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.MIDDLE_NAME));
                    contactInfo.setData5( data5);
                    String data6 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.SUFFIX));
                    contactInfo.setData6( data6);
                    String data7 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
                    contactInfo.setData7( data7);
                    String data8 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME));
                    contactInfo.setData8( data8);
                    String data9 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME));
                    contactInfo.setData9( data9);
                }
                // 1.2 获取各种电话信息
                else if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {

                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    contactInfo.setData1( data1);
                    int data2 = cursor
                            .getInt(cursor.getColumnIndex(Phone.TYPE)); // 手机
                    contactInfo.setData2( data2);

//                    if (phoneType == Phone.TYPE_MOBILE) {
//
//                        jsonObject.put("mobile", mobile);
//                    }
//                    // 住宅电话
//                    else if (phoneType == Phone.TYPE_HOME) {
//
//                        jsonObject.put("homeNum", homeNum);
//                    }
//                    // 单位电话
//                   else if (phoneType == Phone.TYPE_WORK) {
//                        String jobNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("jobNum", jobNum);
//                    }
//                    // 单位传真
//                    else if (phoneType == Phone.TYPE_FAX_WORK) {
//                        String workFax = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("workFax", workFax);
//                    }
//                    // 住宅传真
//                    if (phoneType == Phone.TYPE_FAX_HOME) {
//                        String homeFax = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//
//                        jsonObject.put("homeFax", homeFax);
//                    } // 寻呼机
//                    if (phoneType == Phone.TYPE_PAGER) {
//                        String pager = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("pager", pager);
//                    }
//                    // 回拨号码
//                    if (phoneType == Phone.TYPE_CALLBACK) {
//                        String quickNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("quickNum", quickNum);
//                    }
//                    // 公司总机
//                    if (phoneType == Phone.TYPE_COMPANY_MAIN) {
//                        String jobTel = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("jobTel", jobTel);
//                    }
//                    // 车载电话
//                    if (phoneType == Phone.TYPE_CAR) {
//                        String carNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("carNum", carNum);
//                    } // ISDN
//                    if (phoneType == Phone.TYPE_ISDN) {
//                        String isdn = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("isdn", isdn);
//                    } // 总机
//                    if (phoneType == Phone.TYPE_MAIN) {
//                        String tel = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("tel", tel);
//                    }
//                    // 无线装置
//                    if (phoneType == Phone.TYPE_RADIO) {
//                        String wirelessDev = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//
//                        jsonObject.put("wirelessDev", wirelessDev);
//                    } // 电报
//                    if (phoneType == Phone.TYPE_TELEX) {
//                        String telegram = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("telegram", telegram);
//                    }
//                    // TTY_TDD
//                    if (phoneType == Phone.TYPE_TTY_TDD) {
//                        String tty_tdd = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("tty_tdd", tty_tdd);
//                    }
//                    // 单位手机
//                    if (phoneType == Phone.TYPE_WORK_MOBILE) {
//                        String jobMobile = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("jobMobile", jobMobile);
//                    }
//                    // 单位寻呼机
//                    if (phoneType == Phone.TYPE_WORK_PAGER) {
//                        String jobPager = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("jobPager", jobPager);
//                    } // 助理
//                    if (phoneType == Phone.TYPE_ASSISTANT) {
//                        String assistantNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("assistantNum", assistantNum);
//                    } // 彩信
//                    if (phoneType == Phone.TYPE_MMS) {
//                        String mms = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("mms", mms);
//                    }
                }
                //获取头像
                else if (Photo.CONTENT_ITEM_TYPE.equals(mimetype)) {

                    byte[] data15 = cursor.getBlob(cursor.getColumnIndex(Photo.DATA15));
                    contactInfo.setData15(data15);

                }
                //取出邮件
                else if (Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String data1 = cursor.getString(cursor.getColumnIndex(Email.DATA));
                    contactInfo.setData1( data1);
                    String data2 = cursor.getString(cursor.getColumnIndex(Email.TYPE));
                    contactInfo.setData2( data2);

                }
                // 获取备注信息
                else if (Note.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String data1 = cursor.getString(cursor.getColumnIndex(Note.NOTE));
                    contactInfo.setData1( data1);
                }
                // 获取昵称信息
                else if (Nickname.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Nickname.NAME));
                    contactInfo.setData1( data1);
                }
                // 获取组织信息
                else if (Organization.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出组织类型
                    int data2 = cursor.getInt(cursor
                            .getColumnIndex(Organization.TYPE)); // 单位
                    contactInfo.setData2( data2);
                    if (data2 == Organization.TYPE_CUSTOM) { // if (orgType ==
                        // Organization.TYPE_WORK)
                        // {
                        String data1 = cursor.getString(cursor
                                .getColumnIndex(Organization.COMPANY));
                        contactInfo.setData1( data1);
                        String data4 = cursor.getString(cursor
                                .getColumnIndex(Organization.TITLE));
                        contactInfo.setData4( data4);
                        String data5 = cursor.getString(cursor
                                .getColumnIndex(Organization.DEPARTMENT));
                        contactInfo.setData5( data5);
                    }
                }
                // 获取网站信息
                else if (Website.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出组织类型

                    int data2 = cursor.getInt(cursor.getColumnIndex(Website.TYPE)); // 主页
                    contactInfo.setData2( data2);
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Website.URL));
                    contactInfo.setData1( data1);
                }
                // 查找通讯地址
                else if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出邮件类型
                    int data2 = cursor.getInt(cursor
                            .getColumnIndex(StructuredPostal.TYPE)); // 单位通讯地址
                    contactInfo.setData2( data2);
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.FORMATTED_ADDRESS));
                    contactInfo.setData2( data1);
                    String data4 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.STREET));
                    contactInfo.setData4( data4);

                    String data5 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.POBOX));
                    contactInfo.setData5( data5);
                    String data6 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                    contactInfo.setData6( data6);
                    String data7 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.CITY));
                    contactInfo.setData7( data7);
                    String data8 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.REGION));
                    contactInfo.setData8( data8);
                    String data9 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.POSTCODE));
                    contactInfo.setData9( data9);
                    String data10 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.COUNTRY));
                    contactInfo.setData10( data10);
                }
                //查找event地址
                else if (Event.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出时间类型
                    int data2 = cursor.getInt(cursor.getColumnIndex(Event.TYPE)); // 生日
                    contactInfo.setData2( data2);
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Event.START_DATE));
                    contactInfo.setData1( data1);
                }
                //获取即时通讯消息
                else if (Im.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出即时消息类型
                    int data5 = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
                    contactInfo.setData5( data5);

                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Im.DATA));
                    contactInfo.setData1( data1);
                }
                else if (GroupMembership.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出即时消息类型
                    int data1 = cursor.getInt(cursor.getColumnIndex(GroupMembership.GROUP_ROW_ID));
                    contactInfo.setData1( data1);
                }
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                final DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.US);
                decimalFormat.setDecimalFormatSymbols(decimalSymbol);
               int progress = (int) (Float.parseFloat(decimalFormat.format(
                        (float) (cursor.getPosition() ) / ( cursor.getCount()))) * 100);

                //获取读取进度
                if (limit != progress) {
                    if (progress <= 95){
                        Message message = new Message();
                        message.what = 99;
                        message.arg1 = progress;
                        handler.sendMessage(message);
                    }

                }
                limit = progress;
            } while (cursor.moveToNext());
        }


        cursor.close();

        HashSet<List<ContactInfo<Object>>> hashSet = new Gson().fromJson(new Gson().toJson(listData),  new TypeToken <HashSet<List<ContactInfo<Object>>>>() {}.getType());
        Log.v("msg" , hashSet.size()+"----");
        File file = FileUtil.write(FileUtil.CONTACT_TXT ,new Gson().toJson(hashSet) );
        return file;
    }




    public static List getAllContacts_(Handler  handler , Context context) throws JSONException , IOException {
        HashSet<List> listData = new HashSet<>();
        List<ContactInfo<Object>> list = null;
        String mimetype ;
        int raw_contact_id;
        int oldrid = 0;
        int limit = 0 ;
        // 1.查询通讯录所有联系人信息，通过id排序，我们看下android联系人的表就知道，所有的联系人的数据是由RAW_CONTACT_ID来索引开的
        // 所以，先获取所有的人的RAW_CONTACT_ID
//        Uri uri = ContactsContract.Data.CONTENT_URI; // 联系人Uri；
        Uri uri = Data.CONTENT_URI; // 联系人Uri；
        Cursor cursor = context.getContentResolver().query(uri,
                null, null, null, Data.DISPLAY_NAME);
        if (cursor.moveToFirst()) {
            do {
                raw_contact_id = cursor.getInt(cursor
                        .getColumnIndex(Data.RAW_CONTACT_ID));

                if (oldrid != raw_contact_id) {
                    list = new ArrayList<>();

                    listData.add(list);

                    oldrid = raw_contact_id;
                }
                Log.v("msg" , cursor.getPosition()+"---");
                ContactInfo<Object> contactInfo = new ContactInfo<>();
                list.add(contactInfo);
                mimetype = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE)); // 取得mimetype类型,扩展的数据都在这个类型里面

                contactInfo.setRaw_contact_id( raw_contact_id);
                contactInfo.setMimetype( mimetype);
                // 1.1,拿到联系人的各种名字
                if (StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.DISPLAY_NAME));
                    contactInfo.setData1( data1);
                    String data2 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.GIVEN_NAME));
                    contactInfo.setData2( data2);
                    String data3 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.FAMILY_NAME));
                    contactInfo.setData3( data3);
                    String data4 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.PREFIX));
                    contactInfo.setData4( data4);

                    String data5 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.MIDDLE_NAME));
                    contactInfo.setData5( data5);
                    String data6 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.SUFFIX));
                    contactInfo.setData6( data6);
                    String data7 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
                    contactInfo.setData7( data7);
                    String data8 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME));
                    contactInfo.setData8( data8);
                    String data9 = cursor.getString(cursor
                            .getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME));
                    contactInfo.setData9( data9);
                }
                // 1.2 获取各种电话信息
                else if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {

                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    contactInfo.setData1( data1);
                    int data2 = cursor
                            .getInt(cursor.getColumnIndex(Phone.TYPE)); // 手机
                    contactInfo.setData2( data2);

//                    if (phoneType == Phone.TYPE_MOBILE) {
//
//                        jsonObject.put("mobile", mobile);
//                    }
//                    // 住宅电话
//                    else if (phoneType == Phone.TYPE_HOME) {
//
//                        jsonObject.put("homeNum", homeNum);
//                    }
//                    // 单位电话
//                   else if (phoneType == Phone.TYPE_WORK) {
//                        String jobNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("jobNum", jobNum);
//                    }
//                    // 单位传真
//                    else if (phoneType == Phone.TYPE_FAX_WORK) {
//                        String workFax = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("workFax", workFax);
//                    }
//                    // 住宅传真
//                    if (phoneType == Phone.TYPE_FAX_HOME) {
//                        String homeFax = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//
//                        jsonObject.put("homeFax", homeFax);
//                    } // 寻呼机
//                    if (phoneType == Phone.TYPE_PAGER) {
//                        String pager = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("pager", pager);
//                    }
//                    // 回拨号码
//                    if (phoneType == Phone.TYPE_CALLBACK) {
//                        String quickNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("quickNum", quickNum);
//                    }
//                    // 公司总机
//                    if (phoneType == Phone.TYPE_COMPANY_MAIN) {
//                        String jobTel = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("jobTel", jobTel);
//                    }
//                    // 车载电话
//                    if (phoneType == Phone.TYPE_CAR) {
//                        String carNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("carNum", carNum);
//                    } // ISDN
//                    if (phoneType == Phone.TYPE_ISDN) {
//                        String isdn = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("isdn", isdn);
//                    } // 总机
//                    if (phoneType == Phone.TYPE_MAIN) {
//                        String tel = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("tel", tel);
//                    }
//                    // 无线装置
//                    if (phoneType == Phone.TYPE_RADIO) {
//                        String wirelessDev = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//
//                        jsonObject.put("wirelessDev", wirelessDev);
//                    } // 电报
//                    if (phoneType == Phone.TYPE_TELEX) {
//                        String telegram = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("telegram", telegram);
//                    }
//                    // TTY_TDD
//                    if (phoneType == Phone.TYPE_TTY_TDD) {
//                        String tty_tdd = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("tty_tdd", tty_tdd);
//                    }
//                    // 单位手机
//                    if (phoneType == Phone.TYPE_WORK_MOBILE) {
//                        String jobMobile = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("jobMobile", jobMobile);
//                    }
//                    // 单位寻呼机
//                    if (phoneType == Phone.TYPE_WORK_PAGER) {
//                        String jobPager = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("jobPager", jobPager);
//                    } // 助理
//                    if (phoneType == Phone.TYPE_ASSISTANT) {
//                        String assistantNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("assistantNum", assistantNum);
//                    } // 彩信
//                    if (phoneType == Phone.TYPE_MMS) {
//                        String mms = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        jsonObject.put("mms", mms);
//                    }
                }
                //获取头像
                else if (Photo.CONTENT_ITEM_TYPE.equals(mimetype)) {

                    byte[] data15 = cursor.getBlob(cursor.getColumnIndex(Photo.DATA15));
                    contactInfo.setData15(data15);

                }
                //取出邮件
                else if (Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String data1 = cursor.getString(cursor.getColumnIndex(Email.DATA));
                    contactInfo.setData1( data1);
                    String data2 = cursor.getString(cursor.getColumnIndex(Email.TYPE));
                    contactInfo.setData2( data2);

                }
                // 获取备注信息
                else if (Note.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String data1 = cursor.getString(cursor.getColumnIndex(Note.NOTE));
                    contactInfo.setData1( data1);
                }
                // 获取昵称信息
                else if (Nickname.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Nickname.NAME));
                    contactInfo.setData1( data1);
                }
                // 获取组织信息
                else if (Organization.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出组织类型
                    int data2 = cursor.getInt(cursor
                            .getColumnIndex(Organization.TYPE)); // 单位
                    contactInfo.setData2( data2);
                    if (data2 == Organization.TYPE_CUSTOM) { // if (orgType ==
                        // Organization.TYPE_WORK)
                        // {
                        String data1 = cursor.getString(cursor
                                .getColumnIndex(Organization.COMPANY));
                        contactInfo.setData1( data1);
                        String data4 = cursor.getString(cursor
                                .getColumnIndex(Organization.TITLE));
                        contactInfo.setData4( data4);
                        String data5 = cursor.getString(cursor
                                .getColumnIndex(Organization.DEPARTMENT));
                        contactInfo.setData5( data5);
                    }
                }
                // 获取网站信息
                else if (Website.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出组织类型

                    int data2 = cursor.getInt(cursor.getColumnIndex(Website.TYPE)); // 主页
                    contactInfo.setData2( data2);
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Website.URL));
                    contactInfo.setData1( data1);
                }
                // 查找通讯地址
                else if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出邮件类型
                    int data2 = cursor.getInt(cursor
                            .getColumnIndex(StructuredPostal.TYPE)); // 单位通讯地址
                    contactInfo.setData2( data2);
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.FORMATTED_ADDRESS));
                    contactInfo.setData2( data1);
                    String data4 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.STREET));
                    contactInfo.setData4( data4);

                    String data5 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.POBOX));
                    contactInfo.setData5( data5);
                    String data6 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                    contactInfo.setData6( data6);
                    String data7 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.CITY));
                    contactInfo.setData7( data7);
                    String data8 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.REGION));
                    contactInfo.setData8( data8);
                    String data9 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.POSTCODE));
                    contactInfo.setData9( data9);
                    String data10 = cursor.getString(cursor
                            .getColumnIndex(StructuredPostal.COUNTRY));
                    contactInfo.setData10( data10);
                }
                //查找event地址
                else if (Event.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出时间类型
                    int data2 = cursor.getInt(cursor.getColumnIndex(Event.TYPE)); // 生日
                    contactInfo.setData2( data2);
                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Event.START_DATE));
                    contactInfo.setData1( data1);
                }
                //获取即时通讯消息
                else if (Im.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出即时消息类型
                    int data5 = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
                    contactInfo.setData5( data5);

                    String data1 = cursor.getString(cursor
                            .getColumnIndex(Im.DATA));
                    contactInfo.setData1( data1);
                }
                else if (GroupMembership.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出即时消息类型
                    int data1 = cursor.getInt(cursor.getColumnIndex(GroupMembership.GROUP_ROW_ID));
                    contactInfo.setData1( data1);
                }
                DecimalFormat decimalFormat = new DecimalFormat("0.00");

                final DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.US);
                decimalFormat.setDecimalFormatSymbols(decimalSymbol);
                int progress = (int) (Float.parseFloat(new DecimalFormat("0.00").format(
                        (float) (cursor.getPosition() ) / ( cursor.getCount()))) * 100);

                //获取读取进度
                if (limit != progress) {
                    if (progress <= 95){
                        Message message = new Message();
                        message.what = 99;
                        message.arg1 = progress;
                        handler.sendMessage(message);
                    }

                }
                limit = progress;
            } while (cursor.moveToNext());
        }


        cursor.close();
        return list;
    }




    //同步通讯录数据
    public  static void synContacts(Handler handler ,  Context context , List<List<ContactInfo<Object>>> list) {
        try {

//            HashSet<List<ContactInfo<Object>>> hashSet = getAllContacts(context);

//            ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
//                    .withValue(RawContacts.ACCOUNT_TYPE, accountType)
//                    .withValue(RawContacts.ACCOUNT_NAME, accountName)
//                    .build());
//
//            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
//                    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
//                    .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
//                    .withValue(StructuredName.DISPLAY_NAME, "Mike Sullivan")
//                    .build());
//
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            DecimalFormat fnum =  new DecimalFormat("0.00");
            final DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.US);
            fnum.setDecimalFormatSymbols(decimalSymbol);
            if (list != null && list.size() > 0){
                int progress = 0;
                int limit = 0;
                for (int index = 0 ; index < list.size(); index++){
                    ops.clear();
                    ops.add( ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)  // 此处传入null添加一个raw_contact空数据
                            .withValue(ContactsContract.RawContacts._ID, null)
//                            .withValue(ContactsContract.RawContacts.AGGREGATION_MODE,ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT)

                            .build()); // 此处传入null添加一个raw_contact空数据

                    List< ContactInfo<Object>> listData = list.get(index);

                    for (int i = 0 ; i < listData.size() ; i++){
                        ContactInfo contactInfo = listData.get(i);
                        ContentProviderOperation.Builder  builder =  withValule(  contactInfo );
                        if (builder != null){
                            ops.add(builder.build());
                        }

                    }
                    try {
                        context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    }catch (Exception e){
                        continue;
                    }


                    progress = (int) (Float.parseFloat(fnum.format(
                            (float) (index) / (list.size() ))) * 100);

                    if (limit !=  progress){

                        if ( progress <= 100){
                            Log.v("msg" , progress+"");
                            Message message = new Message();
                            message.what = 99;
                            message.arg1 = progress;
                            handler.sendMessage(message);

                        }
                        limit = progress;

                    }

                }

                if ( progress < 100){
                    Message message = new Message();
                    message.what = 99;
                    message.arg1 = 100;
                    handler.sendMessage(message);
                }

            }
//
        } catch (Exception e) {
            Log.v("msg"  , e.toString());
        }

    }


    //同步通讯录数据
    public static void synContacts(List<ContactInfo<Object>> list) {
        try {
            ContactInfo<Object> contactInfo = new ContactInfo<>();
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)  // 此处传入null添加一个raw_contact空数据
                    .build());
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)  // RAW_CONTACT_ID是第一个事务添加得到的，因此这里传入0，applyBatch返回的ContentProviderResult[]数组中第一项
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "张三")
//                .build());
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "15002070610")
//                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
//                .build());
            if (list != null && list.size() > 0){
                for (int index = 0 ; index < list.size(); index++){
                    ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                            .withValueBackReference(Data.RAW_CONTACT_ID, contactInfo.getRaw_contact_id())  // RAW_CONTACT_ID是第一个事务添加得到的，因此这里传入0，applyBatch返回的ContentProviderResult[]数组中第一项
                            .withValue(Data.MIMETYPE, contactInfo.getMimetype())
                            .withValue("data1", contactInfo.getData1())
                            .withValue("data2", contactInfo.getData2())
                            .withValue("data3", contactInfo.getData3())
                            .withValue("data4", contactInfo.getData4())
                            .withValue("data5", contactInfo.getData5())
                            .withValue("data6", contactInfo.getData6())
                            .withValue("data7", contactInfo.getData7())
                            .withValue("data8", contactInfo.getData8())
                            .withValue("data9", contactInfo.getData9())
                            .withValue("data10", contactInfo.getData10())
                            .withValue("data11", contactInfo.getData11())
                            .withValue("data12", contactInfo.getData12())
                            .withValue("data13", contactInfo.getData13())
                            .withValue("data14", contactInfo.getData14())
                            .withValue("data15", contactInfo.getData15())
                            .build());

                }
                BaseApplication.getInstance().getContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            }


        } catch (Exception e) {

        }

    }

    static int count = 0;
    private static ContentProviderOperation.Builder  withValule( ContactInfo contactInfo ){

        if (contactInfo != null) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, mSelectedAccount.getType())
//                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, mSelectedAccount.getName())
                    .withValue("mimetype", contactInfo.getMimetype());
            count = 0 ;
            Object data1 = contactInfo.getData1();
            if (data1 != null) {
                dataType(builder, "data1", data1 );
            }
            Object data2 = contactInfo.getData2();
            if (data2 != null) {
                dataType(builder, "data2", data2  );
            }
            Object data3 = contactInfo.getData3();
            if (data3 != null) {
                dataType(builder, "data3", data3  );
            }
            Object data4 = contactInfo.getData4();
            if (data4 != null) {
                dataType(builder, "data4", data4 );
            }
            Object data5 = contactInfo.getData5();
            if (data5 != null) {
                dataType(builder, "data5", data5  );
            }
            Object data6 = contactInfo.getData6();

            if (data6 != null) {
                dataType(builder, "data6", data6  );
            }
            Object data7 = contactInfo.getData7();
            if (data7 != null) {
                dataType(builder, "data7", data7  );
            }
            Object data8 = contactInfo.getData8();
            if (data8 != null) {
                dataType(builder, "data8", data8  );
            }
            Object data9 = contactInfo.getData9();
            if (data9 != null) {
                dataType(builder, "data9", data9  );

            }
            Object data10 = contactInfo.getData10();
            if (data10 != null) {
                dataType(builder, "data10", data10  );
            }
            Object data11 = contactInfo.getData11();
            if (data11 != null) {
                dataType(builder, "data11", data11  );
            }
            Object data12 = contactInfo.getData12();
            if (data12 != null) {
                dataType(builder, "data12", data12  );
            }
            Object data13 = contactInfo.getData13();
            if (data13 != null) {
                dataType(builder, "data13", data13  );
            }
            Object data14 = contactInfo.getData14();
            if (data14 != null) {
                dataType(builder, "data14", data14  );
            }
            Object data15 = contactInfo.getData15();
            if (data15 != null) {
                ArrayList arrayList = (ArrayList) data15;
                byte[] bytes = new byte[arrayList.size()];

                for (int i = 0; i < arrayList.size(); i++) {
                    double d = (double) arrayList.get(i);
                    bytes[i] = (byte) d;
                }
                builder.withValue("data15", bytes);
                count++;
            }
            return count > 0 ? builder: null;

        }else {
            return null;
        }
    }
    //处理数据类型
    private  static void dataType(ContentProviderOperation.Builder builder , String data ,  Object object ){

        if (object != null){
            if (object instanceof Double) {
                double d =  ((Double) object);
                builder.withValue(data,(int) d);
                count++;
            }else if (object instanceof String){
                builder.withValue(data,object);
                count++;
            }

        }

    }

    public static void deleteContact(){
        try { //根据姓名求id
//         Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//         ContentResolver resolver = BaseApplication.getInstance().getContext().getContentResolver();
//         Cursor cursor = resolver.query(uri, new String[]{Data._ID},null, null , null);
//         if(cursor.moveToFirst()){
//             int id = cursor.getInt(0);
//             //根据id删除data中的相应数据
//
//             uri = Uri.parse("content://com.android.contacts/data");
//             resolver.delete(uri, "raw_contact_id=?", new String[]{id+""});
//         }
            ContentResolver cResolver1=BaseApplication.getInstance().getContext().getContentResolver();
            Uri uri4=ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
            cResolver1.delete(uri4, null, null);
        }catch (Exception e){}

    }
}
