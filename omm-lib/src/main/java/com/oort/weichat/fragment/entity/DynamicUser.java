package com.oort.weichat.fragment.entity;

import java.io.Serializable;
public class DynamicUser implements Serializable {

    /**
     * code : 200
     * data : {"list":[{"oort_userid":"f41ae786-468a-4842-9245-51b114f8c776","created_at":1689652547,"oort_name":"张利民","oort_photo":"http://oort.oortcloudsmart.com:31520/avatar/o//273/10000273.jpg?1683974127000","oort_sex":0,"oort_depname":"技术支持与运维","oort_depcode":"99999000","oort_code":"17097227961"}],"page":1,"pages":1,"pagesize":10,"counts":1,"count":1}
     * msg : 成功
     */



            private String oort_userid;

    public String getOort_userid() {
        return oort_userid;
    }

    public void setOort_userid(String oort_userid) {
        this.oort_userid = oort_userid;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public String getOort_name() {
        return oort_name;
    }

    public void setOort_name(String oort_name) {
        this.oort_name = oort_name;
    }

    public String getOort_photo() {
        return oort_photo;
    }

    public void setOort_photo(String oort_photo) {
        this.oort_photo = oort_photo;
    }

    public int getOort_sex() {
        return oort_sex;
    }

    public void setOort_sex(int oort_sex) {
        this.oort_sex = oort_sex;
    }

    public String getOort_depname() {
        return oort_depname;
    }

    public void setOort_depname(String oort_depname) {
        this.oort_depname = oort_depname;
    }

    public String getOort_depcode() {
        return oort_depcode;
    }

    public void setOort_depcode(String oort_depcode) {
        this.oort_depcode = oort_depcode;
    }

    public String getOort_code() {
        return oort_code;
    }

    public void setOort_code(String oort_code) {
        this.oort_code = oort_code;
    }

    private int created_at;
            private String oort_name;
            private String oort_photo;
            private int oort_sex;
            private String oort_depname;
            private String oort_depcode;
            private String oort_code;
}
