package com.oort.weichat.fragment.entity;

import com.oortcloud.basemodule.user.UserInfo;

import java.io.Serializable;

public class DynamicProfile implements Serializable {


        /**
         * userInfo : {"oort_uuid":"90180b98-a221-4574-ad12-9ebdee201113","oort_name":"李超","oort_namepy":"lichao","oort_namefl":"lc","oort_code":"999999","oort_depname":"技术支持与运维","oort_depcode":"99999000","oort_idcard":"","oort_photo":"http://map.oort.oortcloudsmart.com:31520/avatar/o//7727/10017727.jpg?1688116074000","oort_sex":0,"oort_phone":"18948726601","oort_pphone":"","oort_email":"","oort_postname":"","oort_jobname":"","oort_office":"","oort_tel":"","imaccount":"10017727297492","imuserid":"10017727"}
         * dynamic : 21
         * follow : 1
         * fans : 0
         * collect : 4
         */

        private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public int getDynamic() {
        return dynamic;
    }

    public void setDynamic(int dynamic) {
        this.dynamic = dynamic;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    private int dynamic;
        private int follow;
        private int fans;
        private int collect;

}
