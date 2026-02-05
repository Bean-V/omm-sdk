package com.oortcloud.bean.meeting;

import java.io.Serializable;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/12/10 18:06
 * @version： v1.0
 * @function：会议信息
 */
public class MeetingInfo  implements Serializable {


        private int id;
        private long created_on;
        private long modified_on;
        private String uid;
        private String name;
        private String creator;
        private String uuid;
        private String meet_id;
         //状态(1-进行中,2-未开始,3-已结束)
        private int status;
        private int start_time;
        private int end_time;
        private int number;
        private int open;
        private String content;
        
        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setCreated_on(long created_on) {
            this.created_on = created_on;
        }
        public long getCreated_on() {
            return created_on;
        }

        public void setModified_on(long modified_on) {
            this.modified_on = modified_on;
        }
        public long getModified_on() {
            return modified_on;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }
        public String getCreator() {
            return creator;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
        public String getUuid() {
            return uuid;
        }

        public void setStatus(int status) {
            this.status = status;
        }
        public int getStatus() {
            return status;
        }

        public void setStart_time(int start_time) {
            this.start_time = start_time;
        }
        public int getStart_time() {
            return start_time;
        }

        public void setEnd_time(int end_time) {
            this.end_time = end_time;
        }
        public int getEnd_time() {
            return end_time;
        }

        public void setNumber(int number) {
            this.number = number;
        }
        public int getNumber() {
            return number;
        }

        public void setOpen(int open) {
            this.open = open;
        }
        public int getOpen() {
            return open;
        }

        public void setContent(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }

    public String getMeet_id() {
        return meet_id;
    }

    public void setMeet_id(String meet_id) {
        this.meet_id = meet_id;
    }
}
