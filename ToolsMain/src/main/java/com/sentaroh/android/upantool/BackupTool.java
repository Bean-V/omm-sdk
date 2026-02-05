package com.sentaroh.android.upantool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class BackupTool {



    public static void getContacts(Context context) {

        ArrayList contactList = new ArrayList<>();
        Cursor contacts = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        HashSet dupPhone = new HashSet();
        while(contacts.moveToNext())
        {
            @SuppressLint("Range")
            String name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range")
            String phoneNumber = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

//            @SuppressLint("Range")
//            String phoneNumber = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replaceAll("[\\-\\s]", "");
            if(phoneNumber != null)
            {
                if(dupPhone.add(phoneNumber))
                {
                    String name1 = name;
                    String p = phoneNumber;

                    //contactList.add(new SingleContact(name, phoneNumber));
                }
            }
        }
//        Comparator<SingleContact> cmpName = (SingleContact cnt1, SingleContact cnt2) -> cnt1.getRecy_contact_name().compareTo(cnt2.getRecy_contact_name());
//        Collections.sort(contactList, cmpName);
        contacts.close();
    }
}
