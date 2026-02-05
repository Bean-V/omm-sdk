package com.oort.weichat.fragment.entity;

import java.io.Serializable;
import java.util.List;

public class NewsCommentRes implements Serializable {



    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    private String msg;
    private DataBean data;
    public static class DataBean implements Serializable {


        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        private int count;
        private List<ListsBean> lists;
        public static class ListsBean implements Serializable {
            /**
             * id : 22
             * created_on : 1680781759
             * modified_on : 1680781759
             * reply_id : b396ea1a-4082-4c68-9f80-296d66bbd83b
             * uid : 67
             * parent_id : 67
             * uuid : f994ba3d-b335-4d8c-8510-3fa0e9076ffa
             * content : 您好
             * likeNum : 0
             * status : 0
             * reply_type : 0
             * portrait : -1
             * phone :
             * name : yxiangke
             * score : 0
             * extend1 :
             * extend2 :
             * extend3 :
             * extend4 : 0
             * extend5 : 0
             */

            private int id;
            private int created_on;
            private int modified_on;
            private String reply_id;
            private String uid;
            private String parent_id;
            private String uuid;
            private String content;
            private int likeNum;
            private int status;
            private int reply_type;
            private String portrait;
            private String phone;
            private String name;
            private int score;
            private String extend1;
            private String extend2;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getCreated_on() {
                return created_on;
            }

            public void setCreated_on(int created_on) {
                this.created_on = created_on;
            }

            public int getModified_on() {
                return modified_on;
            }

            public void setModified_on(int modified_on) {
                this.modified_on = modified_on;
            }

            public String getReply_id() {
                return reply_id;
            }

            public void setReply_id(String reply_id) {
                this.reply_id = reply_id;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getParent_id() {
                return parent_id;
            }

            public void setParent_id(String parent_id) {
                this.parent_id = parent_id;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getLikeNum() {
                return likeNum;
            }

            public void setLikeNum(int likeNum) {
                this.likeNum = likeNum;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getReply_type() {
                return reply_type;
            }

            public void setReply_type(int reply_type) {
                this.reply_type = reply_type;
            }

            public String getPortrait() {
                return portrait;
            }

            public void setPortrait(String portrait) {
                this.portrait = portrait;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public String getExtend1() {
                return extend1;
            }

            public void setExtend1(String extend1) {
                this.extend1 = extend1;
            }

            public String getExtend2() {
                return extend2;
            }

            public void setExtend2(String extend2) {
                this.extend2 = extend2;
            }

            public String getExtend3() {
                return extend3;
            }

            public void setExtend3(String extend3) {
                this.extend3 = extend3;
            }

            public int getExtend4() {
                return extend4;
            }

            public void setExtend4(int extend4) {
                this.extend4 = extend4;
            }

            public int getExtend5() {
                return extend5;
            }

            public void setExtend5(int extend5) {
                this.extend5 = extend5;
            }

            private String extend3;
            private int extend4;
            private int extend5;
        }
    }
}
