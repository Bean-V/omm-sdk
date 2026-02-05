package com.oort.weichat.fragment.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DynamicBean implements Serializable {



    /**
     * oort_duuid : 8732ac5d-dac9-4770-9305-8349caec1d81
     * oort_userid : 272f1030-7c4c-4ffc-b3f3-2870d1b06b4a
     * content : 打造全警钟、全平台、资源整合、系统集成、信息共享，实现接警、指挥、调度为一体的指挥中心，从而更有效的提高治安预防、控制、制止的反应能力。
     * attach : [{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/8a6db8d16723ce2983e143bb4b15d89.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/8a6db8d16723ce2983e143bb4b15d89.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809111939.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809111943.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809111959.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809112008.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809112013.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/58/4/微信图片_20210809112021.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/58/4/微信图片_20210809112024.jpg","type":"image"}]
     * comments : {"list":[{"uuid":"f1e217ca-a076-476c-8175-49209bdd351c","userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","oort_reply_userid":"","content":"我们的愿景：私有云环境下的 \u201c互联网\u201d 公司，公司300多种企业级产品已累计服务1.5亿用户\n公司网址：https://www.oortcloudsmart.com/\n部分产品已经上架华为云市场：https://marketplace.huaweicloud.com/seller/6e4b2ecca26a4ab498275e491ff04077\n\n","created_at":1668397452,"is_leader":1},{"uuid":"3dc48d4f-48d4-4d57-8f71-d4c68c3809dc","userid":"171bad1b-6018-4128-b708-e6790a77908f","oort_reply_userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","content":"","created_at":1671010915,"is_leader":0},{"uuid":"904dc872-aa75-4aa9-9fba-ba61f4c0870b","userid":"f41ae786-468a-4842-9245-51b114f8c776","oort_reply_userid":"","content":"试试看吧","created_at":1681134569,"is_leader":0}],"counts":3,"is_comment":0}
     * likes : {"list":[{"userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","created_at":1681438724,"is_leader":1},{"userid":"5f7fa26e-6335-42d2-ae22-d115601e9691","created_at":1680269078,"is_leader":0},{"userid":"171bad1b-6018-4128-b708-e6790a77908f","created_at":1680604681,"is_leader":0},{"userid":"f41ae786-468a-4842-9245-51b114f8c776","created_at":1681134558,"is_leader":0}],"counts":4,"is_like":0}
     * collects : {"list":[{"userid":"171bad1b-6018-4128-b708-e6790a77908f","created_at":1680604680,"is_leader":0}],"counts":1,"is_collect":0}
     * tag : 1667048537036
     * created_at : 1667048537
     * updated_at : 1682073482
     * oort_top : 9
     * oort_grade1 : 1
     * oort_grade2 : 1
     * oort_grade2_leader : 1
     */

    private String oort_duuid;
    private String oort_userid;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentsBean getComments() {
        return comments;
    }

    public void setComments(CommentsBean comments) {
        this.comments = comments;
    }

    public LikesBean getLikes() {
        return likes;
    }

    public void setLikes(LikesBean likes) {
        this.likes = likes;
    }

    public CollectsBean getCollects() {
        return collects;
    }

    public void setCollects(CollectsBean collects) {
        this.collects = collects;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public int getOort_top() {
        return oort_top;
    }

    public void setOort_top(int oort_top) {
        this.oort_top = oort_top;
    }

    public int getOort_grade1() {
        return oort_grade1;
    }

    public void setOort_grade1(int oort_grade1) {
        this.oort_grade1 = oort_grade1;
    }

    public int getOort_grade2() {
        return oort_grade2;
    }

    public void setOort_grade2(int oort_grade2) {
        this.oort_grade2 = oort_grade2;
    }

    public int getOort_grade2_leader() {
        return oort_grade2_leader;
    }

    public void setOort_grade2_leader(int oort_grade2_leader) {
        this.oort_grade2_leader = oort_grade2_leader;
    }

    public List<AttachBean> getAttach() {
        return attach;
    }

    public void setAttach(List<AttachBean> attach) {
        this.attach = attach;
    }

    private String content;

    public List<String> getAt() {
        return at;
    }

    public void setAt(List<String> at) {
        this.at = at;
    }

    private List<String> at;

    //private String at;
    private CommentsBean comments;
    private LikesBean likes;
    private CollectsBean collects;
    private String tag;
    private int created_at;
    private int updated_at;
    private int oort_top;
    private int oort_grade1;
    private int oort_grade2;
    private int oort_grade2_leader;

    public int getHad_auth() {
        return had_auth;
    }

    public void setHad_auth(int had_auth) {
        this.had_auth = had_auth;
    }

    private int had_auth;

    private String oort_ip_address;

    public String getOort_ip_address() {
        return oort_ip_address;
    }

    public void setOort_ip_address(String oort_ip_address) {
        this.oort_ip_address = oort_ip_address;
    }

    public String getOort_tuuid() {
        return oort_tuuid;
    }

    public void setOort_tuuid(String oort_tuuid) {
        this.oort_tuuid = oort_tuuid;
    }

    public String getOort_tname() {
        return oort_tname;
    }

    public void setOort_tname(String oort_tname) {
        this.oort_tname = oort_tname;
    }

    private String oort_tuuid;
    private String oort_tname;

    private List<AttachBean> attach;

    private List<AttachBean> attach_images;

    private List<AttachBean> attach_audios;

    public List<AttachBean> getAttach_images() {
        attach_images = new ArrayList<AttachBean>();

        if(attach == null){
            return attach_images;
        }
        for(AttachBean b : attach){
            if(b.getType().equals("image") || b.getType().equals("video")){
                attach_images.add(b);
            }
        }
        return attach_images;
    }


    public List<AttachBean> getAttach_audios() {
        attach_audios = new ArrayList<AttachBean>();
        if(attach == null){
            return attach_audios;
        }
        for(AttachBean b : attach){
            if(b.getType().equals("audio")){
                attach_audios.add(b);
            }
        }
        return attach_audios;
    }


    public List<AttachBean> getAttach_atts() {

        attach_atts = new ArrayList<AttachBean>();
        if(attach == null){
            return attach_atts;
        }
        for(AttachBean b : attach){
            if(b.getType().equals("attach")){
                attach_atts.add(b);
            }
        }
        return attach_atts;
    }



    private List<AttachBean> attach_atts;





    public static class CommentsBean implements Serializable {
        /**
         * list : [{"uuid":"f1e217ca-a076-476c-8175-49209bdd351c","userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","oort_reply_userid":"","content":"我们的愿景：私有云环境下的 \u201c互联网\u201d 公司，公司300多种企业级产品已累计服务1.5亿用户\n公司网址：https://www.oortcloudsmart.com/\n部分产品已经上架华为云市场：https://marketplace.huaweicloud.com/seller/6e4b2ecca26a4ab498275e491ff04077\n\n","created_at":1668397452,"is_leader":1},{"uuid":"3dc48d4f-48d4-4d57-8f71-d4c68c3809dc","userid":"171bad1b-6018-4128-b708-e6790a77908f","oort_reply_userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","content":"","created_at":1671010915,"is_leader":0},{"uuid":"904dc872-aa75-4aa9-9fba-ba61f4c0870b","userid":"f41ae786-468a-4842-9245-51b114f8c776","oort_reply_userid":"","content":"试试看吧","created_at":1681134569,"is_leader":0}]
         * counts : 3
         * is_comment : 0
         */

        private int counts;

        public int getCounts() {
            return counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
        }

        public int getIs_comment() {
            return is_comment;
        }

        public void setIs_comment(int is_comment) {
            this.is_comment = is_comment;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        private int is_comment;
        private List<ListBean> list;
        public static class ListBean implements Serializable {
            /**
             * uuid : f1e217ca-a076-476c-8175-49209bdd351c
             * userid : 272f1030-7c4c-4ffc-b3f3-2870d1b06b4a
             * oort_reply_userid :
             * content : 我们的愿景：私有云环境下的 “互联网” 公司，公司300多种企业级产品已累计服务1.5亿用户
             公司网址：https://www.oortcloudsmart.com/
             部分产品已经上架华为云市场：https://marketplace.huaweicloud.com/seller/6e4b2ecca26a4ab498275e491ff04077
             * created_at : 1668397452
             * is_leader : 1
             */

            private String uuid;

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getOort_reply_userid() {
                return oort_reply_userid;
            }

            public void setOort_reply_userid(String oort_reply_userid) {
                this.oort_reply_userid = oort_reply_userid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getCreated_at() {
                return created_at;
            }

            public void setCreated_at(int created_at) {
                this.created_at = created_at;
            }

            public int getIs_leader() {
                return is_leader;
            }

            public void setIs_leader(int is_leader) {
                this.is_leader = is_leader;
            }

            private String userid;
            private String oort_reply_userid;
            private String content;
            private int created_at;
            private int is_leader;
        }
    }

    public static class LikesBean implements Serializable {
        /**
         * list : [{"userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","created_at":1681438724,"is_leader":1},{"userid":"5f7fa26e-6335-42d2-ae22-d115601e9691","created_at":1680269078,"is_leader":0},{"userid":"171bad1b-6018-4128-b708-e6790a77908f","created_at":1680604681,"is_leader":0},{"userid":"f41ae786-468a-4842-9245-51b114f8c776","created_at":1681134558,"is_leader":0}]
         * counts : 4
         * is_like : 0
         */

        private int counts;

        public int getCounts() {
            return counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
        }

        public int getIs_like() {
            return is_like;
        }

        public void setIs_like(int is_like) {
            this.is_like = is_like;
        }

        public List<ListBeanX> getList() {
            return list;
        }

        public void setList(List<ListBeanX> list) {
            this.list = list;
        }

        private int is_like;
        private List<ListBeanX> list;
        public static class ListBeanX implements Serializable {
            /**
             * userid : 272f1030-7c4c-4ffc-b3f3-2870d1b06b4a
             * created_at : 1681438724
             * is_leader : 1
             */

            private String userid;

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public int getCreated_at() {
                return created_at;
            }

            public void setCreated_at(int created_at) {
                this.created_at = created_at;
            }

            public int getIs_leader() {
                return is_leader;
            }

            public void setIs_leader(int is_leader) {
                this.is_leader = is_leader;
            }

            private int created_at;
            private int is_leader;
        }
    }

    public static class CollectsBean implements Serializable {
        /**
         * list : [{"userid":"171bad1b-6018-4128-b708-e6790a77908f","created_at":1680604680,"is_leader":0}]
         * counts : 1
         * is_collect : 0
         */

        private int counts;

        public int getCounts() {
            return counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
        }

        public int getIs_collect() {
            return is_collect;
        }

        public void setIs_collect(int is_collect) {
            this.is_collect = is_collect;
        }

        public List<ListBeanXX> getList() {
            return list;
        }

        public void setList(List<ListBeanXX> list) {
            this.list = list;
        }

        private int is_collect;
        private List<ListBeanXX> list;
        public static class ListBeanXX implements Serializable {
            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public int getCreated_at() {
                return created_at;
            }

            public void setCreated_at(int created_at) {
                this.created_at = created_at;
            }

            public int getIs_leader() {
                return is_leader;
            }

            public void setIs_leader(int is_leader) {
                this.is_leader = is_leader;
            }

            /**
             * userid : 171bad1b-6018-4128-b708-e6790a77908f
             * created_at : 1680604680
             * is_leader : 0
             */

            private String userid;
            private int created_at;
            private int is_leader;
        }
    }

    public static class AttachBean implements Serializable {
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        /**
         * url : http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/8a6db8d16723ce2983e143bb4b15d89.jpg
         * type : image
         */

        private String url;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        private String name;
        private String thumb;
    }
}
