package com.oort.weichat.util;

import java.util.List;

// 数据模型类 (保持原样)
public class AppReviewResTask {
    /**
     * total : 1
     * rows : [{"taskId":"69de232c-3d32-11f0-a96a-1e6a4c298590","taskName":"发起人自己审批","taskDefKey":"Activity_0rjhphh","assigneeId":null,"deptName":null,"startDeptName":null,"assigneeName":null,"startUserId":"90180b98-a221-4574-ad12-9ebdee201113","startUserName":"Chao li8","category":"007","procVars":{"field104":"2222","nextUserIds":"8a391d35-0ccf-4b87-a5da-ce815a12cb4e","processStatus":"running","field103":"3333","field102":"6666","field101":"9999","initiator":"90180b98-a221-4574-ad12-9ebdee201113","notifyAllSteps":false,"isPushNotification":false},"taskLocalVars":null,"deployId":"819bb717-3d27-11f0-a96a-1e6a4c298590","procDefId":"Process_1743407896838:4:81de656a-3d27-11f0-a96a-1e6a4c298590","procDefKey":null,"procDefName":"123123","procDefVersion":4,"procInsId":"69d2ff86-3d32-11f0-a96a-1e6a4c298590","hisProcInsId":null,"duration":null,"comment":null,"commentList":null,"candidate":null,"createTime":"2025-05-30 16:45:18","finishTime":null,"processStatus":"running","proInsCreateTime":"2025-05-30 16:45:18"}]
     * code : 200
     * msg : 查询成功
     */

    private int total;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }

    private int code;
    private String msg;
    private List<RowsBean> rows;


    public class RowsBean {
        /**
         * taskId : 69de232c-3d32-11f0-a96a-1e6a4c298590
         * taskName : 发起人自己审批
         * taskDefKey : Activity_0rjhphh
         * assigneeId : null
         * deptName : null
         * startDeptName : null
         * assigneeName : null
         * startUserId : 90180b98-a221-4574-ad12-9ebdee201113
         * startUserName : Chao li8
         * category : 007
         * procVars : {"field104":"2222","nextUserIds":"8a391d35-0ccf-4b87-a5da-ce815a12cb4e","processStatus":"running","field103":"3333","field102":"6666","field101":"9999","initiator":"90180b98-a221-4574-ad12-9ebdee201113","notifyAllSteps":false,"isPushNotification":false}
         * taskLocalVars : null
         * deployId : 819bb717-3d27-11f0-a96a-1e6a4c298590
         * procDefId : Process_1743407896838:4:81de656a-3d27-11f0-a96a-1e6a4c298590
         * procDefKey : null
         * procDefName : 123123
         * procDefVersion : 4
         * procInsId : 69d2ff86-3d32-11f0-a96a-1e6a4c298590
         * hisProcInsId : null
         * duration : null
         * comment : null
         * commentList : null
         * candidate : null
         * createTime : 2025-05-30 16:45:18
         * finishTime : null
         * processStatus : running
         * proInsCreateTime : 2025-05-30 16:45:18
         */

        private String taskId;
        private String taskName;
        private String taskDefKey;
        private Object assigneeId;
        private Object deptName;
        private Object startDeptName;
        private Object assigneeName;
        private String startUserId;
        private String startUserName;
        private String category;
        private RowsBean.ProcVarsBean procVars;
        private Object taskLocalVars;
        private String deployId;
        private String procDefId;
        private Object procDefKey;
        private String procDefName;
        private int procDefVersion;
        private String procInsId;
        private Object hisProcInsId;
        private Object duration;
        private Object comment;
        private Object commentList;
        private Object candidate;
        private String createTime;

        public RowsBean.ProcVarsBean getProcVars() {
            return procVars;
        }

        public void setProcVars(RowsBean.ProcVarsBean procVars) {
            this.procVars = procVars;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getTaskDefKey() {
            return taskDefKey;
        }

        public void setTaskDefKey(String taskDefKey) {
            this.taskDefKey = taskDefKey;
        }

        public Object getAssigneeId() {
            return assigneeId;
        }

        public void setAssigneeId(Object assigneeId) {
            this.assigneeId = assigneeId;
        }

        public Object getDeptName() {
            return deptName;
        }

        public void setDeptName(Object deptName) {
            this.deptName = deptName;
        }

        public Object getStartDeptName() {
            return startDeptName;
        }

        public void setStartDeptName(Object startDeptName) {
            this.startDeptName = startDeptName;
        }

        public Object getAssigneeName() {
            return assigneeName;
        }

        public void setAssigneeName(Object assigneeName) {
            this.assigneeName = assigneeName;
        }

        public String getStartUserId() {
            return startUserId;
        }

        public void setStartUserId(String startUserId) {
            this.startUserId = startUserId;
        }

        public String getStartUserName() {
            return startUserName;
        }

        public void setStartUserName(String startUserName) {
            this.startUserName = startUserName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Object getTaskLocalVars() {
            return taskLocalVars;
        }

        public void setTaskLocalVars(Object taskLocalVars) {
            this.taskLocalVars = taskLocalVars;
        }

        public String getDeployId() {
            return deployId;
        }

        public void setDeployId(String deployId) {
            this.deployId = deployId;
        }

        public String getProcDefId() {
            return procDefId;
        }

        public void setProcDefId(String procDefId) {
            this.procDefId = procDefId;
        }

        public Object getProcDefKey() {
            return procDefKey;
        }

        public void setProcDefKey(Object procDefKey) {
            this.procDefKey = procDefKey;
        }

        public String getProcDefName() {
            return procDefName;
        }

        public void setProcDefName(String procDefName) {
            this.procDefName = procDefName;
        }

        public int getProcDefVersion() {
            return procDefVersion;
        }

        public void setProcDefVersion(int procDefVersion) {
            this.procDefVersion = procDefVersion;
        }

        public String getProcInsId() {
            return procInsId;
        }

        public void setProcInsId(String procInsId) {
            this.procInsId = procInsId;
        }

        public Object getHisProcInsId() {
            return hisProcInsId;
        }

        public void setHisProcInsId(Object hisProcInsId) {
            this.hisProcInsId = hisProcInsId;
        }

        public Object getDuration() {
            return duration;
        }

        public void setDuration(Object duration) {
            this.duration = duration;
        }

        public Object getComment() {
            return comment;
        }

        public void setComment(Object comment) {
            this.comment = comment;
        }

        public Object getCommentList() {
            return commentList;
        }

        public void setCommentList(Object commentList) {
            this.commentList = commentList;
        }

        public Object getCandidate() {
            return candidate;
        }

        public void setCandidate(Object candidate) {
            this.candidate = candidate;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public Object getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(Object finishTime) {
            this.finishTime = finishTime;
        }

        public String getProcessStatus() {
            return processStatus;
        }

        public void setProcessStatus(String processStatus) {
            this.processStatus = processStatus;
        }

        public String getProInsCreateTime() {
            return proInsCreateTime;
        }

        public void setProInsCreateTime(String proInsCreateTime) {
            this.proInsCreateTime = proInsCreateTime;
        }

        private Object finishTime;
        private String processStatus;
        private String proInsCreateTime;


        public class ProcVarsBean {
            /**
             * field104 : 2222
             * nextUserIds : 8a391d35-0ccf-4b87-a5da-ce815a12cb4e
             * processStatus : running
             * field103 : 3333
             * field102 : 6666
             * field101 : 9999
             * initiator : 90180b98-a221-4574-ad12-9ebdee201113
             * notifyAllSteps : false
             * isPushNotification : false
             */

            private String field104;
            private String nextUserIds;
            private String processStatus;

            public String getField102() {
                return field102;
            }

            public void setField102(String field102) {
                this.field102 = field102;
            }

            public String getField104() {
                return field104;
            }

            public void setField104(String field104) {
                this.field104 = field104;
            }

            public String getNextUserIds() {
                return nextUserIds;
            }

            public void setNextUserIds(String nextUserIds) {
                this.nextUserIds = nextUserIds;
            }

            public String getProcessStatus() {
                return processStatus;
            }

            public void setProcessStatus(String processStatus) {
                this.processStatus = processStatus;
            }

            public String getField103() {
                return field103;
            }

            public void setField103(String field103) {
                this.field103 = field103;
            }

            public String getField101() {
                return field101;
            }

            public void setField101(String field101) {
                this.field101 = field101;
            }

            public String getInitiator() {
                return initiator;
            }

            public void setInitiator(String initiator) {
                this.initiator = initiator;
            }

            public boolean isNotifyAllSteps() {
                return notifyAllSteps;
            }

            public void setNotifyAllSteps(boolean notifyAllSteps) {
                this.notifyAllSteps = notifyAllSteps;
            }

            public boolean isPushNotification() {
                return isPushNotification;
            }

            public void setPushNotification(boolean pushNotification) {
                isPushNotification = pushNotification;
            }

            private String field103;
            private String field102;
            private String field101;
            private String initiator;
            private boolean notifyAllSteps;
            private boolean isPushNotification;
        }
    }
}
