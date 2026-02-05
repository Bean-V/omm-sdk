package com.oortcloud.oort_zhifayi;

import java.io.Serializable;
import java.util.List;

public class XGEvent implements Serializable {

    /**
     * created_at : 2021-05-05 11:11:11
     * describe : 每周街道巡更任务
     * id : 1
     * pics : ["string"]
     * point : {"address":"深圳市福田区松岭路57号","lat":22.71991,"lng":114.24779}
     * status : 1
     * task_id : 1
     * updated_at : 2021-05-05 11:11:11
     * uuid : c64c64a9-ab34-43b4-94e2-5689aeeb51e8
     * uuids : ["string"]
     */

    private String created_at;
    private String describe;
    private String id;
    private PointBean point;
    private int status;
    private String task_id;
    private String updated_at;
    private String uuid;
    private List<String> pics;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PointBean getPoint() {
        return point;
    }

    public void setPoint(PointBean point) {
        this.point = point;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
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

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    private List<String> uuids;

    public static class PointBean implements Serializable {
        /**
         * address : 深圳市福田区松岭路57号
         * lat : 22.71991
         * lng : 114.24779
         */

        private String address;
        private double lat;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        private double lng;
    }
}
