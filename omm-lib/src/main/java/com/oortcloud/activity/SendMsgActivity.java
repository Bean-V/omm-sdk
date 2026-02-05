package com.oortcloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.ui.message.ChatActivity;
import com.oortcloud.contacts.activity.PersonDetailActivity;

public class SendMsgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null){
            String userid = intent.getStringExtra("userid");
            String type = intent.getStringExtra("type");

            if (!TextUtils.isEmpty(userid) && !TextUtils.isEmpty(type)){


                    if (type.equals("1")) {
                        PersonDetailActivity.actionStart(this,userid);
                    } else if (type.equals("2")) {
                        Friend friend = findFriend(userid);
                        if (friend != null) {
                            Intent newintent = new Intent(SendMsgActivity.this, ChatActivity.class);
                            newintent.putExtra(ChatActivity.FRIEND, friend);
                            startActivity(newintent);
                        }else{
                            //非好友调用
                            PersonDetailActivity.actionStart(this,userid);
                        }
                    }


            }

        }
        finish();
    }

    private Friend findFriend(String userid) {
        Friend friend;
        String myuserid = FastSharedPreferences.get("USERINFO_SAVE").getString("userid","");
        if (TextUtils.isEmpty(myuserid))
            return null;
        friend = FriendDao.getInstance().getFriend(myuserid,userid);

        return friend;
    }
}