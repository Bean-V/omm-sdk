package com.oort.weichat.fragment.vs.bean;


import java.util.List;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/25-4:04.
 * Version 1.0
 * Description:
 */
public class DeviceList {

    /**
     * code : 200
     * data : {"list":[{"imei":"114","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-07-05 18:15:13","updated_at":"2025-07-05 18:15:13","uuid":"","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"113","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-07-05 18:14:47","updated_at":"2025-07-05 18:14:47","uuid":"1a3d2666-e0fe-459f-81da-c3c21ef3628c","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"112","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-07-05 18:14:28","updated_at":"2025-07-05 18:14:28","uuid":"","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"11111","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"备注","card_no":"商物联卡编号","created_at":"2025-06-28 20:08:49","updated_at":"2025-06-28 20:08:49","uuid":"","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":1,"disable_reason":"运营商服务到期"},{"imei":"zf1","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-06-24 17:59:34","updated_at":"2025-06-24 17:59:34","uuid":"ce7de90d-6bc9-406b-8d43-21f9b31b59de","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"zf1234","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-06-24 11:36:04","updated_at":"2025-06-24 11:36:04","uuid":"","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"123456","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-06-23 16:34:42","updated_at":"2025-06-23 16:34:42","uuid":"05cf5f38-7d11-4b18-a97a-06adbea4a041","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""}],"page":1,"pages":1,"pagesize":100,"counts":7,"count":7}
     * msg : 成功
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * list : [{"imei":"114","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-07-05 18:15:13","updated_at":"2025-07-05 18:15:13","uuid":"","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"113","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-07-05 18:14:47","updated_at":"2025-07-05 18:14:47","uuid":"1a3d2666-e0fe-459f-81da-c3c21ef3628c","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"112","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-07-05 18:14:28","updated_at":"2025-07-05 18:14:28","uuid":"","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"11111","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"备注","card_no":"商物联卡编号","created_at":"2025-06-28 20:08:49","updated_at":"2025-06-28 20:08:49","uuid":"","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":1,"disable_reason":"运营商服务到期"},{"imei":"zf1","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-06-24 17:59:34","updated_at":"2025-06-24 17:59:34","uuid":"ce7de90d-6bc9-406b-8d43-21f9b31b59de","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"zf1234","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-06-24 11:36:04","updated_at":"2025-06-24 11:36:04","uuid":"","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""},{"imei":"123456","longitude":0,"latitude":0,"speed":0,"direction":0,"elevation":0,"accuracy":0,"status":2,"last_online_at":0,"last_report_at":0,"remark":"","card_no":"","created_at":"2025-06-23 16:34:42","updated_at":"2025-06-23 16:34:42","uuid":"05cf5f38-7d11-4b18-a97a-06adbea4a041","user_name":"","photo":"","dept_name":"","dept_id":"","device_type":0,"build_at":0,"is_disable":2,"disable_reason":""}]
         * page : 1
         * pages : 1
         * pagesize : 100
         * counts : 7
         * count : 7
         */

        private int page;
        private int pages;
        private int pagesize;
        private int counts;
        private int count;
        private List<DeviceBean> list;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getPagesize() {
            return pagesize;
        }

        public void setPagesize(int pagesize) {
            this.pagesize = pagesize;
        }

        public int getCounts() {
            return counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<DeviceBean> getList() {
            return list;
        }

        public void setList(List<DeviceBean> list) {
            this.list = list;
        }

        public static class DeviceBean {
            /**
             * imei : 114
             * longitude : 0
             * latitude : 0
             * speed : 0
             * direction : 0
             * elevation : 0
             * accuracy : 0
             * status : 2
             * last_online_at : 0
             * last_report_at : 0
             * remark :
             * card_no :
             * created_at : 2025-07-05 18:15:13
             * updated_at : 2025-07-05 18:15:13
             * uuid :
             * user_name :
             * photo :
             * dept_name :
             * dept_id :
             * device_type : 0
             * build_at : 0
             * is_disable : 2
             * disable_reason :
             */

            private String imei;
            private int longitude;
            private int latitude;
            private int speed;
            private int direction;
            private int elevation;
            private int accuracy;
            private int status;
            private int last_online_at;
            private int last_report_at;
            private String remark;
            private String card_no;
            private String created_at;
            private String updated_at;
            private String uuid;
            private String user_name;
            private String photo;
            private String dept_name;
            private String dept_id;
            private int device_type;
            private int build_at;
            private int is_disable;
            private String disable_reason;

            public String getImei() {
                return imei;
            }

            public void setImei(String imei) {
                this.imei = imei;
            }

            public int getLongitude() {
                return longitude;
            }

            public void setLongitude(int longitude) {
                this.longitude = longitude;
            }

            public int getLatitude() {
                return latitude;
            }

            public void setLatitude(int latitude) {
                this.latitude = latitude;
            }

            public int getSpeed() {
                return speed;
            }

            public void setSpeed(int speed) {
                this.speed = speed;
            }

            public int getDirection() {
                return direction;
            }

            public void setDirection(int direction) {
                this.direction = direction;
            }

            public int getElevation() {
                return elevation;
            }

            public void setElevation(int elevation) {
                this.elevation = elevation;
            }

            public int getAccuracy() {
                return accuracy;
            }

            public void setAccuracy(int accuracy) {
                this.accuracy = accuracy;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getLast_online_at() {
                return last_online_at;
            }

            public void setLast_online_at(int last_online_at) {
                this.last_online_at = last_online_at;
            }

            public int getLast_report_at() {
                return last_report_at;
            }

            public void setLast_report_at(int last_report_at) {
                this.last_report_at = last_report_at;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getCard_no() {
                return card_no;
            }

            public void setCard_no(String card_no) {
                this.card_no = card_no;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

            public String getDept_name() {
                return dept_name;
            }

            public void setDept_name(String dept_name) {
                this.dept_name = dept_name;
            }

            public String getDept_id() {
                return dept_id;
            }

            public void setDept_id(String dept_id) {
                this.dept_id = dept_id;
            }

            public int getDevice_type() {
                return device_type;
            }

            public void setDevice_type(int device_type) {
                this.device_type = device_type;
            }

            public int getBuild_at() {
                return build_at;
            }

            public void setBuild_at(int build_at) {
                this.build_at = build_at;
            }

            public int getIs_disable() {
                return is_disable;
            }

            public void setIs_disable(int is_disable) {
                this.is_disable = is_disable;
            }

            public String getDisable_reason() {
                return disable_reason;
            }

            public void setDisable_reason(String disable_reason) {
                this.disable_reason = disable_reason;
            }
        }
    }
}
