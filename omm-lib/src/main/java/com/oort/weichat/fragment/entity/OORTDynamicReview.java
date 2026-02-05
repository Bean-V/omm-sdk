package com.oort.weichat.fragment.entity;

import java.util.List;

public class OORTDynamicReview {

    public DynamicBean getDynamic() {
        return dynamic;
    }

    public void setDynamic(DynamicBean dynamic) {
        this.dynamic = dynamic;
    }

    public List<UserInfoBean> getUserInfo() {
        return userInfo;
    }




    public UserInfoBean getUserInfo(String uuid) {
        for(UserInfoBean info : userInfo){
            if(info.getOort_uuid().equals(uuid)){
                return info;
            }
        }
        return null;
    }



    public void setUserInfo(List<UserInfoBean> userInfo) {
        this.userInfo = userInfo;
    }

    private DynamicBean dynamic;
    private List<UserInfoBean> userInfo;
    private String oort_duuid;
    private String oort_userid;
    private String type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public String getOort_duuid() {
        return oort_duuid;
    }

    public void setOort_duuid(String oort_duuid) {
        this.oort_duuid = oort_duuid;
    }

    public String getOort_userid() {
        return oort_userid;
    }

    public void setOort_userid(String oort_userid) {
        this.oort_userid = oort_userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private String created_at;
    private String tag;

}
