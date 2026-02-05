package com.oort.weichat.fragment.entity;

import java.io.Serializable;
import java.util.List;


public class OORTDynamic implements Serializable {
    public DynamicBean getDynamic() {
        return dynamic;
    }

    public void setDynamic(DynamicBean dynamic) {
        this.dynamic = dynamic;
    }

    public List<UserInfoBean> getUserInfo() {
        return userInfo;
    }


    public UserInfoBean getCreatorInfo() {

        for(UserInfoBean info : userInfo){
            if(info.getOort_uuid().equals(dynamic.getOort_userid())){
                return info;
            }
        }
        return new UserInfoBean();
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

    /**
         * dynamic : {"oort_duuid":"8732ac5d-dac9-4770-9305-8349caec1d81","oort_userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","content":"打造全警钟、全平台、资源整合、系统集成、信息共享，实现接警、指挥、调度为一体的指挥中心，从而更有效的提高治安预防、控制、制止的反应能力。","attach":[{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/8a6db8d16723ce2983e143bb4b15d89.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/8a6db8d16723ce2983e143bb4b15d89.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809111939.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809111943.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809111959.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809112008.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/59/4/微信图片_20210809112013.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/58/4/微信图片_20210809112021.jpg","type":"image"},{"url":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221029/20/58/4/微信图片_20210809112024.jpg","type":"image"}],"comments":{"list":[{"uuid":"f1e217ca-a076-476c-8175-49209bdd351c","userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","oort_reply_userid":"","content":"我们的愿景：私有云环境下的 \u201c互联网\u201d 公司，公司300多种企业级产品已累计服务1.5亿用户\n公司网址：https://www.oortcloudsmart.com/\n部分产品已经上架华为云市场：https://marketplace.huaweicloud.com/seller/6e4b2ecca26a4ab498275e491ff04077\n\n","created_at":1668397452,"is_leader":1},{"uuid":"3dc48d4f-48d4-4d57-8f71-d4c68c3809dc","userid":"171bad1b-6018-4128-b708-e6790a77908f","oort_reply_userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","content":"","created_at":1671010915,"is_leader":0},{"uuid":"904dc872-aa75-4aa9-9fba-ba61f4c0870b","userid":"f41ae786-468a-4842-9245-51b114f8c776","oort_reply_userid":"","content":"试试看吧","created_at":1681134569,"is_leader":0}],"counts":3,"is_comment":0},"likes":{"list":[{"userid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","created_at":1681438724,"is_leader":1},{"userid":"5f7fa26e-6335-42d2-ae22-d115601e9691","created_at":1680269078,"is_leader":0},{"userid":"171bad1b-6018-4128-b708-e6790a77908f","created_at":1680604681,"is_leader":0},{"userid":"f41ae786-468a-4842-9245-51b114f8c776","created_at":1681134558,"is_leader":0}],"counts":4,"is_like":0},"collects":{"list":[{"userid":"171bad1b-6018-4128-b708-e6790a77908f","created_at":1680604680,"is_leader":0}],"counts":1,"is_collect":0},"tag":"1667048537036","created_at":1667048537,"updated_at":1682073482,"oort_top":9,"oort_grade1":1,"oort_grade2":1,"oort_grade2_leader":1}
         * userInfo : [{"oort_uuid":"171bad1b-6018-4128-b708-e6790a77908f","oort_name":"qin","oort_namepy":"qin","oort_namefl":"q","oort_code":"18145816400","oort_depname":"技术支持与运维","oort_depcode":"99999000","oort_idcard":"","oort_photo":"","oort_sex":0,"oort_phone":"18145816400","oort_pphone":"","oort_email":"","oort_postname":"","oort_jobname":"","oort_office":"","oort_tel":"","imaccount":"10017747819519","imuserid":"10017747"},{"oort_uuid":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","oort_name":"张学连","oort_namepy":"zhangxuelian","oort_namefl":"zxl","oort_code":"18938083835","oort_depname":"技术支持与运维","oort_depcode":"99999000","oort_idcard":"","oort_photo":"http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221026/18/14/4/微信截图_20221026181230.png","oort_sex":0,"oort_phone":"18938083835","oort_pphone":"","oort_email":"","oort_postname":"","oort_jobname":"","oort_office":"","oort_tel":"","imaccount":"10000277886517","imuserid":"10000277"},{"oort_uuid":"5f7fa26e-6335-42d2-ae22-d115601e9691","oort_name":"奥尔特云小战士","oort_namepy":"aoerteyunxiaozhanshi","oort_namefl":"aetyxzs","oort_code":"18938081983","oort_depname":"技术支持与运维","oort_depcode":"99999000","oort_idcard":"362201198310254039","oort_photo":"http://oort.oortcloudsmart.com:31520/avatar/o//7781/10017781.jpg?1680164638000","oort_sex":0,"oort_phone":"18938081983","oort_pphone":"","oort_email":"","oort_postname":"","oort_jobname":"","oort_office":"","oort_tel":"","imaccount":"10017781126165","imuserid":"10017781"},{"oort_uuid":"f41ae786-468a-4842-9245-51b114f8c776","oort_name":"张利民","oort_namepy":"zhanglimin","oort_namefl":"zlm","oort_code":"17097227961","oort_depname":"技术支持与运维","oort_depcode":"99999000","oort_idcard":"","oort_photo":"http://oort.oortcloudsmart.com:31520/avatar/o//273/10000273.jpg?1683974127000","oort_sex":0,"oort_phone":"17097227961","oort_pphone":"","oort_email":"","oort_postname":"aaa","oort_jobname":"专家测试","oort_office":"","oort_tel":"","imaccount":"10000273963950","imuserid":"10000273"}]
         */

        private DynamicBean dynamic;
        private List<UserInfoBean> userInfo;






}
