package com.sentaroh.android.upantool.sysTask;

public class SysNode {
        public String title = "";
        public String type = "";

        public SysNode(String title, int all, int index, int resId,String type) {
            this.title = title;
            this.all = all;
            this.index = index;
            this.resId = resId;
            this.type = type;
        }

        public int all;
        public int index;

        public int resId;
        public int resType;
}
