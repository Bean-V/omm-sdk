package com.oortcloud.custom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.oort.weichat.call.CallConstants;
import com.oort.weichat.call.MessageEventInitiateMeeting;
import com.oort.weichat.ui.lccontact.PersonPickActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.UUID;

public class VoiceMeet extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SelectContactsActivity.startQuicklyInitiateMeeting(this, CallConstants.Audio_Meet);


        if(true) {
            //ArrayList ids = new ArrayList();
//        ArrayList mMembers = new ArrayList();
//        for(DynamicSendActivity.Member mem : mMembers){
//            DynamicSendActivity.Member mem =
//            UserInfo info = new UserInfo();
//            info.setImuserid(mem.id);
//            info.setOort_uuid(mem.userId);
//            info.setOort_photo(mem.header);
//            info.setOort_name(mem.name);
//            ids.add(info);
//
//        }
            Intent in = new Intent(this, PersonPickActivity.class);
//
//            if (ids.size() > 0) {
//
//                String res = JSON.toJSONString(ids);
//                in.putExtra("selectUser", res);
//            }
            startActivityForResult(in, 100);
            PersonPickActivity.pickFinish = null;
            PersonPickActivity.pickFinish_v2 = null;


            PersonPickActivity.pickFinish_v2 = new PersonPickActivity.PickFinish_v2() {
                @Override
                public void finish(List imids, List userIds, List names, List headerUrls, List extrs) {


//                    // mMembers.clear();
//                    int i = 0;
//                    for (Object o : imids) {
//                        String id = (String) o;
////                    DynamicSendActivity.Member mem = new DynamicSendActivity.Member();
////                    mem.id = id;
////                    mem.name = (String) names.get(i);
////                    mem.header = (String) headerUrls.get(i);
////                    mem.userId = (String) userIds.get(i);
////                    mMembers.add(mem);
//                        i++;
//                    }

                    EventBus.getDefault().post(new MessageEventInitiateMeeting(CallConstants.Audio_Meet, imids,false, UUID.randomUUID().toString()));
                    //memAdp.notifyDataSetChanged();
                    PersonPickActivity.pickFinish_v2 = null;
                }
            };
        }
        finish();
    }
}
