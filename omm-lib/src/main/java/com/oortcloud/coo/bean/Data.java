package com.oortcloud.coo.bean;

import java.util.List;

public class Data<T> {

        /**
         * current : 0
         * total : 0
         * records : [{"id":0,"tenantId":"","alertLevel":"","receivingAlertNumber":"","receivingOfficer":"","receivingOfficerId":"","receivingOfficerPhone":"","responseUnit":"","alertStatus":"","alertCategory":"","alertType":"","alertSubType":"","incidentLocation":"","alertContent":"","thirdResponseUnit":"","secondResponseUnit":"","firstResponseUnit":"","sessionId":"","groupId":""}]
         * pages : 0
         * size : 0
         */

        private int current;
        private int total;
        private int pages;
        private int size;
        private List<T> records;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public List<T> getRecords() {
            return records;
        }

        public void setRecords(List<T> records) {
            this.records = records;
        }


}
