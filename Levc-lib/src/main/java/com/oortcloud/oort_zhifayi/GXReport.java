package com.oortcloud.oort_zhifayi;

import java.io.Serializable;
import java.util.List;


public class GXReport implements Serializable {

    /**
     * checkpoint : [{"address":"深圳市福田区松岭路57号","describe":"每周街道巡更任务","lat":22.71991,"lng":114.24779,"report_at":1591862003,"terminal_no":"123456"}]
     * checkpoint_len : 10
     * created_at : 2021-05-05 11:11:11
     * report : [{"address":"深圳市福田区松岭路57号","lat":22.71991,"lng":114.24779,"report_at":1591862003,"terminal_no":"123456"}]
     * report_len : 10
     * task_id : 1
     * updated_at : 2021-05-05 11:11:11
     * uuid : c64c64a9-ab34-43b4-94e2-5689aeeb51e8
     */

    private int checkpoint_len;
    private String created_at;
    private int report_len;
    private String task_id;
    private String updated_at;

    public int getCheckpoint_len() {
        return checkpoint_len;
    }

    public void setCheckpoint_len(int checkpoint_len) {
        this.checkpoint_len = checkpoint_len;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getReport_len() {
        return report_len;
    }

    public void setReport_len(int report_len) {
        this.report_len = report_len;
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

    public List<CheckpointBean> getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(List<CheckpointBean> checkpoint) {
        this.checkpoint = checkpoint;
    }

    public List<ReportBean> getReport() {
        return report;
    }

    public void setReport(List<ReportBean> report) {
        this.report = report;
    }

    private String uuid;
    private List<CheckpointBean> checkpoint;

    public List<Checkpoint_set_Bean> getCheckpoint_set() {
        return checkpoint_set;
    }

    public void setCheckpoint_set(List<Checkpoint_set_Bean> checkpoint_set) {
        this.checkpoint_set = checkpoint_set;
    }

    private List<Checkpoint_set_Bean> checkpoint_set;
    private List<ReportBean> report;


    public static class CheckpointBean implements Serializable {
        /**
         * address : 深圳市福田区松岭路57号
         * describe : 每周街道巡更任务
         * lat : 22.71991
         * lng : 114.24779
         * report_at : 1591862003
         * terminal_no : 123456
         */

        private String address;
        private String describe;
        private double lat;
        private double lng;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
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

        public int getReport_at() {
            return report_at;
        }

        public void setReport_at(int report_at) {
            this.report_at = report_at;
        }

        public String getTerminal_no() {
            return terminal_no;
        }

        public void setTerminal_no(String terminal_no) {
            this.terminal_no = terminal_no;
        }

        private int report_at;
        private String terminal_no;
    }
    public static class Checkpoint_set_Bean implements Serializable {
        /**
         * address : 深圳市福田区松岭路57号
         * describe : 每周街道巡更任务
         * lat : 22.71991
         * lng : 114.24779
         */

        private String address;
        private double lat;
        private double lng;


        public boolean isIs_checkin() {
            return is_checkin;
        }

        public void setIs_checkin(boolean is_checkin) {
            this.is_checkin = is_checkin;
        }

        private boolean is_checkin;

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


    }

    public static class ReportBean implements Serializable {
        /**
         * address : 深圳市福田区松岭路57号
         * lat : 22.71991
         * lng : 114.24779
         * report_at : 1591862003
         * terminal_no : 123456
         */

        private String address;
        private double lat;
        private double lng;
        private int report_at;

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

        public int getReport_at() {
            return report_at;
        }

        public void setReport_at(int report_at) {
            this.report_at = report_at;
        }

        public String getTerminal_no() {
            return terminal_no;
        }

        public void setTerminal_no(String terminal_no) {
            this.terminal_no = terminal_no;
        }

        private String terminal_no;
    }
}
