package com.plugins.oortcloud.imshare;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class ShareReviewContent  {

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

    /**
     * code : 200
     * msg : 操作成功
     * data : {"taskFormData":null,"assigneeType":null,"historyProcNodeList":[{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Event_0rniimm","activityName":null,"activityType":"startEvent","duration":"0秒","assigneeId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","assigneeName":"admin","assigneeInfoList":[{"assigneeName":"admin","completeTime":"2024-12-05 18:06:29"}],"candidate":null,"commentList":null,"createTime":"2024-12-05 18:06:29","endTime":"2024-12-05 18:06:29","wfCopyUser":null,"transactionOrder":1,"executionId":"98e69ce0-b2f0-11ef-b7b5-b6033e79437a"},{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Activity_1x0ptjx","activityName":"发起人","activityType":"userTask","duration":"0秒","assigneeId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","assigneeName":"admin","assigneeInfoList":[{"assigneeName":"admin","completeTime":"2024-12-05 18:06:30"}],"candidate":null,"commentList":[{"id":"98f0fd28-b2f0-11ef-b7b5-b6033e79437a","originalPersistentState":"org.flowable.engine.impl.persistence.entity.CommentEntityImpl","type":"1","userId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","time":"2024-12-05 18:06:29","taskId":"98e69ce4-b2f0-11ef-b7b5-b6033e79437a","processInstanceId":"98e675ca-b2f0-11ef-b7b5-b6033e79437a","action":"AddComment","message":"admin发起流程申请","fullMessage":"admin发起流程申请","fullMessageBytes":"YWRtaW7lj5HotbfmtYHnqIvnlLPor7c=","messageParts":["admin发起流程申请"],"persistentState":"org.flowable.engine.impl.persistence.entity.CommentEntityImpl","idPrefix":"PRC-","deleted":false,"inserted":false,"updated":false}],"createTime":"2024-12-05 18:06:29","endTime":"2024-12-05 18:06:30","wfCopyUser":null,"transactionOrder":3,"executionId":"98e69ce0-b2f0-11ef-b7b5-b6033e79437a"},{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Activity_0htxi3g","activityName":"部门领导","activityType":"userTask","duration":"1分6秒","assigneeId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","assigneeName":"admin","assigneeInfoList":[{"assigneeName":"admin","completeTime":"2024-12-05 18:07:36"}],"candidate":null,"commentList":[{"id":"c0893fff-b2f0-11ef-b7b5-b6033e79437a","originalPersistentState":"org.flowable.engine.impl.persistence.entity.CommentEntityImpl","type":"1","userId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","time":"2024-12-05 18:07:36","taskId":"9902b06c-b2f0-11ef-b7b5-b6033e79437a","processInstanceId":"98e675ca-b2f0-11ef-b7b5-b6033e79437a","action":"AddComment","message":"qq去去去去去去去去去去","fullMessage":"qq去去去去去去去去去去","fullMessageBytes":"cXHljrvljrvljrvljrvljrvljrvljrvljrvljrvljrs=","messageParts":["qq去去去去去去去去去去"],"persistentState":"org.flowable.engine.impl.persistence.entity.CommentEntityImpl","idPrefix":"PRC-","deleted":false,"inserted":false,"updated":false}],"createTime":"2024-12-05 18:06:30","endTime":"2024-12-05 18:07:36","wfCopyUser":null,"transactionOrder":2,"executionId":"98e69ce0-b2f0-11ef-b7b5-b6033e79437a"},{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Activity_1lv3422","activityName":"管理员审核","activityType":"userTask","duration":"22天19小时46分12秒","assigneeId":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","assigneeName":"Xuelian Zhang","assigneeInfoList":[{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"陈青琳2","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"李处长","completeTime":"2024-12-28 13:53:50"},{"assigneeName":"陈青琳","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"薛思正","completeTime":"2024-12-28 13:53:49"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"谭国政","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"张利民","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"张明明","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"李副主任","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"李超啥","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"ruanlingli","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"谭生","completeTime":"2024-12-28 13:53:51"},{"assigneeName":"黄宏堃","completeTime":"2024-12-28 13:53:51"},{"assigneeName":"lanjian","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"周少锋","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"李邦鸣","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"张慧劲","completeTime":"2024-12-28 13:53:51"},{"assigneeName":"高亚清","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"吴阿江二","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"卫磊雁","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"张淑芬","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"于晓强","completeTime":"2024-12-28 13:53:51"},{"assigneeName":"吴xx","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"冯燕珍","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"郭杰欣","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"张维维","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"徐金欢","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"王处长","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"奥尔特云","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"雷超群","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"李向东","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"李  俊","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"汪亚超","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"李副支队长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"刘钦粦","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"钱堂","completeTime":"2024-12-28 13:53:52"},{"assigneeName":"是对方","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"ty","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"zenqi","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"W","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"小奥","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"赖思舒","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"薛港明","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"郭成","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张中队长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"刘强","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"南湾美容美容沙龙","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张副科长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"陈镇规","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"邓彬祥","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"刘彦彤","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张支队长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"钟维聪","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"朱文含","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"曾骏聪","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"贾平","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"刘副处长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"王榕","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"杨相柯","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张上就","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"吴测试11","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"王副处长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"福保管理员","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"公冶宇全","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"卓鑫","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张志军","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"testj","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"吴亚金","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"刘惠敏","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"邱海锟","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"王枭","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"何月","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"洪柯","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"Chao li8","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"尼格买提.阿不路提，买买提","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"廖滔","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"张处长","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"李副科长","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"党军吉","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"黄司文","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"黄吉安","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"林明顺","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"赵朋芝","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"王科长","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"陈嘉缙","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"蔡纯","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"肖哥","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"蔡中军","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"兰孟舰","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"罗小文","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"小二美发","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"汤红","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"唐林","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"肖东","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"李科长","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"南园街道管理员","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"王同心","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"李桐","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张副中队长","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张迦凝","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"吴xx","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"刘放舟","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"卫力媚","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"陈青琳1","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"lanjianV","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"lanjian1","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"肖海凤","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"顾大华","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"武装干事","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"cz","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张科长","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"甘鸿林","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张慧","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"高宇","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"李佳明","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"测试2","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"陈宁","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"Mr. Lan","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"刘科长","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"于浩源","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"杜嘉豪","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"jptest","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"刘晓英","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"马钰","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张主任","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"兰舰wu","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"吴镇江","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"沈沁","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"赵  越","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"张副支队长","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"温伟洪","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"大奥","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张副主任","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"许平","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"Xuelian Zhang","completeTime":"2024-12-28 13:53:47"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"黄彩芬","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"冯东煜","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"梁瑞瑞","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"李副中队长","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"崔占强","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"李银波","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"刘副科长","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"吴定义","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"王副科长","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"卓婷","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"吕国鑫","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"qin","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"张哲","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"彭俊奇","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"孙宝威","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"林传基","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"吴阿江","completeTime":"2024-12-28 13:53:56"},{"assigneeName":"陈海怡","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"Chairman Lee","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"袁国云","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"陈健","completeTime":"2024-12-28 13:53:56"},{"assigneeName":"李瀚烨","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"}],"candidate":"普通角色","commentList":[],"createTime":"2024-12-05 18:07:38","endTime":"2024-12-28 13:53:51","wfCopyUser":null,"transactionOrder":557,"executionId":"c09463c1-b2f0-11ef-b7b5-b6033e79437a"},{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Event_15q24zj","activityName":null,"activityType":"endEvent","duration":"0秒","assigneeId":null,"assigneeName":null,"assigneeInfoList":[{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"}],"candidate":null,"commentList":null,"createTime":"2024-12-28 13:53:56","endTime":"2024-12-28 13:53:56","wfCopyUser":null,"transactionOrder":2,"executionId":"2030c4a2-c4e0-11ef-bca5-dafe0fbb6344"}],"processFormList":[{"title":"测试","formRef":"elForm","formModel":"formData","size":"medium","labelPosition":"right","labelWidth":100,"formRules":"rules","gutter":15,"disabled":true,"span":24,"formBtns":false,"fields":[{"__config__":{"label":"单行文本","labelWidth":null,"showLabel":true,"changeTag":true,"tag":"el-input","tagIcon":"input","required":true,"layout":"colFormItem","span":24,"document":"https://element.eleme.cn/#/zh-CN/component/input","regList":[],"formId":101,"renderKey":"1011730905968656","defaultValue":"少时诵诗书少时诵诗书是撒是撒是撒是撒是撒是撒"},"__slot__":{"prepend":"","append":""},"placeholder":"请输入单行文本","style":{"width":"100%"},"clearable":true,"prefix-icon":"","suffix-icon":"","maxlength":null,"show-word-limit":false,"readonly":false,"disabled":false,"__vModel__":"field101"}]}],"bpmnXml":"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<bpmn2:definitions xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" id=\"diagram_Process_1728628058277\" targetNamespace=\"http://flowable.org/bpmn\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">\n  <bpmn2:process id=\"Process_1728628058277\" name=\"腾讯区域审批2\" isExecutable=\"true\">\n    <bpmn2:extensionElements>\n      <flowable:properties>\n        <flowable:property name=\"callbackAddress\" value=\"http://23.16.41.128:48000/jk/RegionPort/areaNode\" />\n        <flowable:property name=\"areaNodeNotice\" value=\"http://23.16.41.128:48000/jk/RegionPort/areaNodeNotice\" />\n      <\/flowable:properties>\n    <\/bpmn2:extensionElements>\n    <bpmn2:startEvent id=\"Event_0rniimm\" flowable:formKey=\"key_1854180170519584770\">\n      <bpmn2:outgoing>Flow_0vy01m4<\/bpmn2:outgoing>\n    <\/bpmn2:startEvent>\n    <bpmn2:userTask id=\"Activity_1x0ptjx\" name=\"发起人\" flowable:formKey=\"\" flowable:dataType=\"INITIATOR\" flowable:assignee=\"${initiator}\" flowable:text=\"流程发起人\">\n      <bpmn2:extensionElements>\n        <flowable:propertiesBtn>\n          <flowable:property name=\"buttonOprArr\" value=\"0,1,2,3,4\" />\n        <\/flowable:propertiesBtn>\n      <\/bpmn2:extensionElements>\n      <bpmn2:incoming>Flow_0vy01m4<\/bpmn2:incoming>\n      <bpmn2:outgoing>Flow_1r1d5rl<\/bpmn2:outgoing>\n    <\/bpmn2:userTask>\n    <bpmn2:sequenceFlow id=\"Flow_0vy01m4\" sourceRef=\"Event_0rniimm\" targetRef=\"Activity_1x0ptjx\" />\n    <bpmn2:userTask id=\"Activity_0htxi3g\" name=\"部门领导\" flowable:dataType=\"USERS\" flowable:assignee=\"6799ea6d-dec6-4b34-961c-a7b5f8c6c900\" flowable:candidateGroups=\"\" flowable:text=\"admin\">\n      <bpmn2:extensionElements>\n        <flowable:propertiesBtn>\n          <flowable:property name=\"buttonOprArr\" value=\"0,1,2,3,4\" />\n        <\/flowable:propertiesBtn>\n      <\/bpmn2:extensionElements>\n      <bpmn2:incoming>Flow_1r1d5rl<\/bpmn2:incoming>\n      <bpmn2:outgoing>Flow_0814mlt<\/bpmn2:outgoing>\n    <\/bpmn2:userTask>\n    <bpmn2:sequenceFlow id=\"Flow_1r1d5rl\" sourceRef=\"Activity_1x0ptjx\" targetRef=\"Activity_0htxi3g\" />\n    <bpmn2:userTask id=\"Activity_1lv3422\" name=\"管理员审核\" flowable:dataType=\"ROLES\" flowable:assignee=\"${assignee}\" flowable:candidateGroups=\"ROLE2\" flowable:text=\"普通角色\">\n      <bpmn2:extensionElements>\n        <flowable:propertiesBtn>\n          <flowable:property name=\"buttonOprArr\" value=\"0,1,2,3,4\" />\n        <\/flowable:propertiesBtn>\n      <\/bpmn2:extensionElements>\n      <bpmn2:incoming>Flow_0814mlt<\/bpmn2:incoming>\n      <bpmn2:outgoing>Flow_0svdln8<\/bpmn2:outgoing>\n      <bpmn2:multiInstanceLoopCharacteristics flowable:collection=\"${multiInstanceHandler.getUserIds(execution)}\" flowable:elementVariable=\"assignee\">\n        <bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\">${nrOfCompletedInstances &gt; 0}<\/bpmn2:completionCondition>\n      <\/bpmn2:multiInstanceLoopCharacteristics>\n    <\/bpmn2:userTask>\n    <bpmn2:sequenceFlow id=\"Flow_0814mlt\" sourceRef=\"Activity_0htxi3g\" targetRef=\"Activity_1lv3422\" />\n    <bpmn2:endEvent id=\"Event_15q24zj\">\n      <bpmn2:extensionElements />\n      <bpmn2:incoming>Flow_0svdln8<\/bpmn2:incoming>\n    <\/bpmn2:endEvent>\n    <bpmn2:sequenceFlow id=\"Flow_0svdln8\" sourceRef=\"Activity_1lv3422\" targetRef=\"Event_15q24zj\" />\n  <\/bpmn2:process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"Process_1728628058277\">\n      <bpmndi:BPMNEdge id=\"Flow_0svdln8_di\" bpmnElement=\"Flow_0svdln8\">\n        <di:waypoint x=\"720\" y=\"490\" />\n        <di:waypoint x=\"782\" y=\"490\" />\n      <\/bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_0814mlt_di\" bpmnElement=\"Flow_0814mlt\">\n        <di:waypoint x=\"560\" y=\"490\" />\n        <di:waypoint x=\"620\" y=\"490\" />\n      <\/bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_1r1d5rl_di\" bpmnElement=\"Flow_1r1d5rl\">\n        <di:waypoint x=\"400\" y=\"490\" />\n        <di:waypoint x=\"460\" y=\"490\" />\n      <\/bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_0vy01m4_di\" bpmnElement=\"Flow_0vy01m4\">\n        <di:waypoint x=\"248\" y=\"490\" />\n        <di:waypoint x=\"300\" y=\"490\" />\n      <\/bpmndi:BPMNEdge>\n      <bpmndi:BPMNShape id=\"Event_0rniimm_di\" bpmnElement=\"Event_0rniimm\">\n        <dc:Bounds x=\"212\" y=\"472\" width=\"36\" height=\"36\" />\n      <\/bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_1x0ptjx_di\" bpmnElement=\"Activity_1x0ptjx\">\n        <dc:Bounds x=\"300\" y=\"450\" width=\"100\" height=\"80\" />\n      <\/bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_0htxi3g_di\" bpmnElement=\"Activity_0htxi3g\">\n        <dc:Bounds x=\"460\" y=\"450\" width=\"100\" height=\"80\" />\n      <\/bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_1lv3422_di\" bpmnElement=\"Activity_1lv3422\">\n        <dc:Bounds x=\"620\" y=\"450\" width=\"100\" height=\"80\" />\n      <\/bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Event_15q24zj_di\" bpmnElement=\"Event_15q24zj\">\n        <dc:Bounds x=\"782\" y=\"472\" width=\"36\" height=\"36\" />\n      <\/bpmndi:BPMNShape>\n    <\/bpmndi:BPMNPlane>\n  <\/bpmndi:BPMNDiagram>\n<\/bpmn2:definitions>\n","flowViewer":{"finishedTaskSet":["Activity_1x0ptjx","Event_0rniimm","Event_15q24zj","Activity_0htxi3g","Activity_1lv3422"],"finishedSequenceFlowSet":["Flow_0814mlt","Flow_0vy01m4","Flow_0svdln8","Flow_1r1d5rl"],"unfinishedTaskSet":[],"rejectedTaskSet":[]},"wfBasicInfoVo":{"processCategory":"cw001","processName":"腾讯区域审批2","processId":"98e675ca-b2f0-11ef-b7b5-b6033e79437a","submissionTime":"2024-12-05 18:06:29"},"extensionMap":null,"buttonsMap":null,"existTaskForm":false}
     */

    private int code;
    private String msg;
    private DataBean data;

    public static class DataBean  {
        /**
         * taskFormData : null
         * assigneeType : null
         * historyProcNodeList : [{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Event_0rniimm","activityName":null,"activityType":"startEvent","duration":"0秒","assigneeId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","assigneeName":"admin","assigneeInfoList":[{"assigneeName":"admin","completeTime":"2024-12-05 18:06:29"}],"candidate":null,"commentList":null,"createTime":"2024-12-05 18:06:29","endTime":"2024-12-05 18:06:29","wfCopyUser":null,"transactionOrder":1,"executionId":"98e69ce0-b2f0-11ef-b7b5-b6033e79437a"},{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Activity_1x0ptjx","activityName":"发起人","activityType":"userTask","duration":"0秒","assigneeId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","assigneeName":"admin","assigneeInfoList":[{"assigneeName":"admin","completeTime":"2024-12-05 18:06:30"}],"candidate":null,"commentList":[{"id":"98f0fd28-b2f0-11ef-b7b5-b6033e79437a","originalPersistentState":"org.flowable.engine.impl.persistence.entity.CommentEntityImpl","type":"1","userId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","time":"2024-12-05 18:06:29","taskId":"98e69ce4-b2f0-11ef-b7b5-b6033e79437a","processInstanceId":"98e675ca-b2f0-11ef-b7b5-b6033e79437a","action":"AddComment","message":"admin发起流程申请","fullMessage":"admin发起流程申请","fullMessageBytes":"YWRtaW7lj5HotbfmtYHnqIvnlLPor7c=","messageParts":["admin发起流程申请"],"persistentState":"org.flowable.engine.impl.persistence.entity.CommentEntityImpl","idPrefix":"PRC-","deleted":false,"inserted":false,"updated":false}],"createTime":"2024-12-05 18:06:29","endTime":"2024-12-05 18:06:30","wfCopyUser":null,"transactionOrder":3,"executionId":"98e69ce0-b2f0-11ef-b7b5-b6033e79437a"},{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Activity_0htxi3g","activityName":"部门领导","activityType":"userTask","duration":"1分6秒","assigneeId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","assigneeName":"admin","assigneeInfoList":[{"assigneeName":"admin","completeTime":"2024-12-05 18:07:36"}],"candidate":null,"commentList":[{"id":"c0893fff-b2f0-11ef-b7b5-b6033e79437a","originalPersistentState":"org.flowable.engine.impl.persistence.entity.CommentEntityImpl","type":"1","userId":"6799ea6d-dec6-4b34-961c-a7b5f8c6c900","time":"2024-12-05 18:07:36","taskId":"9902b06c-b2f0-11ef-b7b5-b6033e79437a","processInstanceId":"98e675ca-b2f0-11ef-b7b5-b6033e79437a","action":"AddComment","message":"qq去去去去去去去去去去","fullMessage":"qq去去去去去去去去去去","fullMessageBytes":"cXHljrvljrvljrvljrvljrvljrvljrvljrvljrvljrs=","messageParts":["qq去去去去去去去去去去"],"persistentState":"org.flowable.engine.impl.persistence.entity.CommentEntityImpl","idPrefix":"PRC-","deleted":false,"inserted":false,"updated":false}],"createTime":"2024-12-05 18:06:30","endTime":"2024-12-05 18:07:36","wfCopyUser":null,"transactionOrder":2,"executionId":"98e69ce0-b2f0-11ef-b7b5-b6033e79437a"},{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Activity_1lv3422","activityName":"管理员审核","activityType":"userTask","duration":"22天19小时46分12秒","assigneeId":"272f1030-7c4c-4ffc-b3f3-2870d1b06b4a","assigneeName":"Xuelian Zhang","assigneeInfoList":[{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"陈青琳2","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"李处长","completeTime":"2024-12-28 13:53:50"},{"assigneeName":"陈青琳","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"薛思正","completeTime":"2024-12-28 13:53:49"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"谭国政","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"张利民","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"张明明","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"李副主任","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"李超啥","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:50"},{"assigneeName":"ruanlingli","completeTime":"2024-12-28 13:53:50"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"谭生","completeTime":"2024-12-28 13:53:51"},{"assigneeName":"黄宏堃","completeTime":"2024-12-28 13:53:51"},{"assigneeName":"lanjian","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"周少锋","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"李邦鸣","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"张慧劲","completeTime":"2024-12-28 13:53:51"},{"assigneeName":"高亚清","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"吴阿江二","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"卫磊雁","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"张淑芬","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"于晓强","completeTime":"2024-12-28 13:53:51"},{"assigneeName":"吴xx","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"冯燕珍","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"郭杰欣","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"张维维","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"徐金欢","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":"王处长","completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:51"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"奥尔特云","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"雷超群","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"李向东","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"李  俊","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"汪亚超","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"李副支队长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"刘钦粦","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"钱堂","completeTime":"2024-12-28 13:53:52"},{"assigneeName":"是对方","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"ty","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"zenqi","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"W","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":"小奥","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"赖思舒","completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:52"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"薛港明","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"郭成","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张中队长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"刘强","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"南湾美容美容沙龙","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张副科长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"陈镇规","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"邓彬祥","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"刘彦彤","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张支队长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"钟维聪","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"朱文含","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"曾骏聪","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"贾平","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"刘副处长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"王榕","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"杨相柯","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张上就","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"吴测试11","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"王副处长","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"福保管理员","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"公冶宇全","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"卓鑫","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:53"},{"assigneeName":"张志军","completeTime":"2024-12-28 13:53:53"},{"assigneeName":"testj","completeTime":"2024-12-28 13:53:53"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"吴亚金","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"刘惠敏","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"邱海锟","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"王枭","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"何月","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"洪柯","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"Chao li8","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"尼格买提.阿不路提，买买提","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"廖滔","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"张处长","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"李副科长","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"党军吉","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"黄司文","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"黄吉安","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"林明顺","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"赵朋芝","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"王科长","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"陈嘉缙","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"蔡纯","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"肖哥","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"蔡中军","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"兰孟舰","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"罗小文","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"小二美发","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"汤红","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"唐林","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"肖东","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"李科长","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"南园街道管理员","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"王同心","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":"李桐","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张副中队长","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张迦凝","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"吴xx","completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:54"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"刘放舟","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"卫力媚","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"陈青琳1","completeTime":"2024-12-28 13:53:54"},{"assigneeName":"lanjianV","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"lanjian1","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"肖海凤","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"顾大华","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"武装干事","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"cz","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张科长","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"甘鸿林","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张慧","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"高宇","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"李佳明","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"测试2","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"陈宁","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"Mr. Lan","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"刘科长","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"于浩源","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"杜嘉豪","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"jptest","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"刘晓英","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"马钰","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张主任","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"兰舰wu","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"吴镇江","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"沈沁","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"赵  越","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"张副支队长","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"温伟洪","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"大奥","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"张副主任","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"许平","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"Xuelian Zhang","completeTime":"2024-12-28 13:53:47"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"黄彩芬","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"冯东煜","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"梁瑞瑞","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"李副中队长","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"崔占强","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"李银波","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"刘副科长","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"吴定义","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"王副科长","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"卓婷","completeTime":"2024-12-28 13:53:55"},{"assigneeName":"吕国鑫","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":"qin","completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:55"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"张哲","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"彭俊奇","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"孙宝威","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"林传基","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"吴阿江","completeTime":"2024-12-28 13:53:56"},{"assigneeName":"陈海怡","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"Chairman Lee","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"袁国云","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"},{"assigneeName":"陈健","completeTime":"2024-12-28 13:53:56"},{"assigneeName":"李瀚烨","completeTime":"2024-12-28 13:53:56"},{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"}],"candidate":"普通角色","commentList":[],"createTime":"2024-12-05 18:07:38","endTime":"2024-12-28 13:53:51","wfCopyUser":null,"transactionOrder":557,"executionId":"c09463c1-b2f0-11ef-b7b5-b6033e79437a"},{"procDefId":"Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a","activityId":"Event_15q24zj","activityName":null,"activityType":"endEvent","duration":"0秒","assigneeId":null,"assigneeName":null,"assigneeInfoList":[{"assigneeName":null,"completeTime":"2024-12-28 13:53:56"}],"candidate":null,"commentList":null,"createTime":"2024-12-28 13:53:56","endTime":"2024-12-28 13:53:56","wfCopyUser":null,"transactionOrder":2,"executionId":"2030c4a2-c4e0-11ef-bca5-dafe0fbb6344"}]
         * processFormList : [{"title":"测试","formRef":"elForm","formModel":"formData","size":"medium","labelPosition":"right","labelWidth":100,"formRules":"rules","gutter":15,"disabled":true,"span":24,"formBtns":false,"fields":[{"__config__":{"label":"单行文本","labelWidth":null,"showLabel":true,"changeTag":true,"tag":"el-input","tagIcon":"input","required":true,"layout":"colFormItem","span":24,"document":"https://element.eleme.cn/#/zh-CN/component/input","regList":[],"formId":101,"renderKey":"1011730905968656","defaultValue":"少时诵诗书少时诵诗书是撒是撒是撒是撒是撒是撒"},"__slot__":{"prepend":"","append":""},"placeholder":"请输入单行文本","style":{"width":"100%"},"clearable":true,"prefix-icon":"","suffix-icon":"","maxlength":null,"show-word-limit":false,"readonly":false,"disabled":false,"__vModel__":"field101"}]}]
         * bpmnXml : <?xml version="1.0" encoding="UTF-8"?>
         <bpmn2:definitions xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:flowable="http://flowable.org/bpmn" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="diagram_Process_1728628058277" targetNamespace="http://flowable.org/bpmn" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd">
         <bpmn2:process id="Process_1728628058277" name="腾讯区域审批2" isExecutable="true">
         <bpmn2:extensionElements>
         <flowable:properties>
         <flowable:property name="callbackAddress" value="http://23.16.41.128:48000/jk/RegionPort/areaNode" />
         <flowable:property name="areaNodeNotice" value="http://23.16.41.128:48000/jk/RegionPort/areaNodeNotice" />
         </flowable:properties>
         </bpmn2:extensionElements>
         <bpmn2:startEvent id="Event_0rniimm" flowable:formKey="key_1854180170519584770">
         <bpmn2:outgoing>Flow_0vy01m4</bpmn2:outgoing>
         </bpmn2:startEvent>
         <bpmn2:userTask id="Activity_1x0ptjx" name="发起人" flowable:formKey="" flowable:dataType="INITIATOR" flowable:assignee="${initiator}" flowable:text="流程发起人">
         <bpmn2:extensionElements>
         <flowable:propertiesBtn>
         <flowable:property name="buttonOprArr" value="0,1,2,3,4" />
         </flowable:propertiesBtn>
         </bpmn2:extensionElements>
         <bpmn2:incoming>Flow_0vy01m4</bpmn2:incoming>
         <bpmn2:outgoing>Flow_1r1d5rl</bpmn2:outgoing>
         </bpmn2:userTask>
         <bpmn2:sequenceFlow id="Flow_0vy01m4" sourceRef="Event_0rniimm" targetRef="Activity_1x0ptjx" />
         <bpmn2:userTask id="Activity_0htxi3g" name="部门领导" flowable:dataType="USERS" flowable:assignee="6799ea6d-dec6-4b34-961c-a7b5f8c6c900" flowable:candidateGroups="" flowable:text="admin">
         <bpmn2:extensionElements>
         <flowable:propertiesBtn>
         <flowable:property name="buttonOprArr" value="0,1,2,3,4" />
         </flowable:propertiesBtn>
         </bpmn2:extensionElements>
         <bpmn2:incoming>Flow_1r1d5rl</bpmn2:incoming>
         <bpmn2:outgoing>Flow_0814mlt</bpmn2:outgoing>
         </bpmn2:userTask>
         <bpmn2:sequenceFlow id="Flow_1r1d5rl" sourceRef="Activity_1x0ptjx" targetRef="Activity_0htxi3g" />
         <bpmn2:userTask id="Activity_1lv3422" name="管理员审核" flowable:dataType="ROLES" flowable:assignee="${assignee}" flowable:candidateGroups="ROLE2" flowable:text="普通角色">
         <bpmn2:extensionElements>
         <flowable:propertiesBtn>
         <flowable:property name="buttonOprArr" value="0,1,2,3,4" />
         </flowable:propertiesBtn>
         </bpmn2:extensionElements>
         <bpmn2:incoming>Flow_0814mlt</bpmn2:incoming>
         <bpmn2:outgoing>Flow_0svdln8</bpmn2:outgoing>
         <bpmn2:multiInstanceLoopCharacteristics flowable:collection="${multiInstanceHandler.getUserIds(execution)}" flowable:elementVariable="assignee">
         <bpmn2:completionCondition xsi:type="bpmn2:tFormalExpression">${nrOfCompletedInstances &gt; 0}</bpmn2:completionCondition>
         </bpmn2:multiInstanceLoopCharacteristics>
         </bpmn2:userTask>
         <bpmn2:sequenceFlow id="Flow_0814mlt" sourceRef="Activity_0htxi3g" targetRef="Activity_1lv3422" />
         <bpmn2:endEvent id="Event_15q24zj">
         <bpmn2:extensionElements />
         <bpmn2:incoming>Flow_0svdln8</bpmn2:incoming>
         </bpmn2:endEvent>
         <bpmn2:sequenceFlow id="Flow_0svdln8" sourceRef="Activity_1lv3422" targetRef="Event_15q24zj" />
         </bpmn2:process>
         <bpmndi:BPMNDiagram id="BPMNDiagram_1">
         <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1728628058277">
         <bpmndi:BPMNEdge id="Flow_0svdln8_di" bpmnElement="Flow_0svdln8">
         <di:waypoint x="720" y="490" />
         <di:waypoint x="782" y="490" />
         </bpmndi:BPMNEdge>
         <bpmndi:BPMNEdge id="Flow_0814mlt_di" bpmnElement="Flow_0814mlt">
         <di:waypoint x="560" y="490" />
         <di:waypoint x="620" y="490" />
         </bpmndi:BPMNEdge>
         <bpmndi:BPMNEdge id="Flow_1r1d5rl_di" bpmnElement="Flow_1r1d5rl">
         <di:waypoint x="400" y="490" />
         <di:waypoint x="460" y="490" />
         </bpmndi:BPMNEdge>
         <bpmndi:BPMNEdge id="Flow_0vy01m4_di" bpmnElement="Flow_0vy01m4">
         <di:waypoint x="248" y="490" />
         <di:waypoint x="300" y="490" />
         </bpmndi:BPMNEdge>
         <bpmndi:BPMNShape id="Event_0rniimm_di" bpmnElement="Event_0rniimm">
         <dc:Bounds x="212" y="472" width="36" height="36" />
         </bpmndi:BPMNShape>
         <bpmndi:BPMNShape id="Activity_1x0ptjx_di" bpmnElement="Activity_1x0ptjx">
         <dc:Bounds x="300" y="450" width="100" height="80" />
         </bpmndi:BPMNShape>
         <bpmndi:BPMNShape id="Activity_0htxi3g_di" bpmnElement="Activity_0htxi3g">
         <dc:Bounds x="460" y="450" width="100" height="80" />
         </bpmndi:BPMNShape>
         <bpmndi:BPMNShape id="Activity_1lv3422_di" bpmnElement="Activity_1lv3422">
         <dc:Bounds x="620" y="450" width="100" height="80" />
         </bpmndi:BPMNShape>
         <bpmndi:BPMNShape id="Event_15q24zj_di" bpmnElement="Event_15q24zj">
         <dc:Bounds x="782" y="472" width="36" height="36" />
         </bpmndi:BPMNShape>
         </bpmndi:BPMNPlane>
         </bpmndi:BPMNDiagram>
         </bpmn2:definitions>
         * flowViewer : {"finishedTaskSet":["Activity_1x0ptjx","Event_0rniimm","Event_15q24zj","Activity_0htxi3g","Activity_1lv3422"],"finishedSequenceFlowSet":["Flow_0814mlt","Flow_0vy01m4","Flow_0svdln8","Flow_1r1d5rl"],"unfinishedTaskSet":[],"rejectedTaskSet":[]}
         * wfBasicInfoVo : {"processCategory":"cw001","processName":"腾讯区域审批2","processId":"98e675ca-b2f0-11ef-b7b5-b6033e79437a","submissionTime":"2024-12-05 18:06:29"}
         * extensionMap : null
         * buttonsMap : null
         * existTaskForm : false
         */

        private Object taskFormData;
        private Object assigneeType;
        private String bpmnXml;
        private FlowViewerBean flowViewer;
        private WfBasicInfoVoBean wfBasicInfoVo;
        private Object extensionMap;
        private Object buttonsMap;
        private boolean existTaskForm;

        public String getBpmnXml() {
            return bpmnXml;
        }

        public void setBpmnXml(String bpmnXml) {
            this.bpmnXml = bpmnXml;
        }

        public Object getTaskFormData() {
            return taskFormData;
        }

        public void setTaskFormData(Object taskFormData) {
            this.taskFormData = taskFormData;
        }

        public Object getAssigneeType() {
            return assigneeType;
        }

        public void setAssigneeType(Object assigneeType) {
            this.assigneeType = assigneeType;
        }

        public FlowViewerBean getFlowViewer() {
            return flowViewer;
        }

        public void setFlowViewer(FlowViewerBean flowViewer) {
            this.flowViewer = flowViewer;
        }

        public WfBasicInfoVoBean getWfBasicInfoVo() {
            return wfBasicInfoVo;
        }

        public void setWfBasicInfoVo(WfBasicInfoVoBean wfBasicInfoVo) {
            this.wfBasicInfoVo = wfBasicInfoVo;
        }

        public Object getExtensionMap() {
            return extensionMap;
        }

        public void setExtensionMap(Object extensionMap) {
            this.extensionMap = extensionMap;
        }

        public Object getButtonsMap() {
            return buttonsMap;
        }

        public void setButtonsMap(Object buttonsMap) {
            this.buttonsMap = buttonsMap;
        }

        public boolean isExistTaskForm() {
            return existTaskForm;
        }

        public void setExistTaskForm(boolean existTaskForm) {
            this.existTaskForm = existTaskForm;
        }

        public List<HistoryProcNodeListBean> getHistoryProcNodeList() {
            return historyProcNodeList;
        }

        public void setHistoryProcNodeList(List<HistoryProcNodeListBean> historyProcNodeList) {
            this.historyProcNodeList = historyProcNodeList;
        }

        public List<ProcessFormListBean> getProcessFormList() {
            return processFormList;
        }

        public void setProcessFormList(List<ProcessFormListBean> processFormList) {
            this.processFormList = processFormList;
        }

        private List<HistoryProcNodeListBean> historyProcNodeList;
        private List<ProcessFormListBean> processFormList;

        public static class FlowViewerBean implements Serializable {
            private List<String> finishedTaskSet;
            private List<String> finishedSequenceFlowSet;
            private List<?> unfinishedTaskSet;
            private List<?> rejectedTaskSet;
        }

        public static class WfBasicInfoVoBean implements Serializable {
            /**
             * processCategory : cw001
             * processName : 腾讯区域审批2
             * processId : 98e675ca-b2f0-11ef-b7b5-b6033e79437a
             * submissionTime : 2024-12-05 18:06:29
             */

            private String processCategory;

            public String getProcessId() {
                return processId;
            }

            public void setProcessId(String processId) {
                this.processId = processId;
            }

            public String getSubmissionTime() {
                return submissionTime;
            }

            public void setSubmissionTime(String submissionTime) {
                this.submissionTime = submissionTime;
            }

            public String getProcessName() {
                return processName;
            }

            public void setProcessName(String processName) {
                this.processName = processName;
            }

            public String getProcessCategory() {
                return processCategory;
            }

            public void setProcessCategory(String processCategory) {
                this.processCategory = processCategory;
            }

            private String processName;
            private String processId;
            private String submissionTime;
        }


        public static class HistoryProcNodeListBean implements Serializable {
            /**
             * procDefId : Process_1728628058277:13:8dad3ae9-b2f0-11ef-b7b5-b6033e79437a
             * activityId : Event_0rniimm
             * activityName : null
             * activityType : startEvent
             * duration : 0秒
             * assigneeId : 6799ea6d-dec6-4b34-961c-a7b5f8c6c900
             * assigneeName : admin
             * assigneeInfoList : [{"assigneeName":"admin","completeTime":"2024-12-05 18:06:29"}]
             * candidate : null
             * commentList : null
             * createTime : 2024-12-05 18:06:29
             * endTime : 2024-12-05 18:06:29
             * wfCopyUser : null
             * transactionOrder : 1
             * executionId : 98e69ce0-b2f0-11ef-b7b5-b6033e79437a
             */

            private String procDefId;
            private String activityId;

            public String getTaskId() {
                return taskId;
            }

            public void setTaskId(String taskId) {
                this.taskId = taskId;
            }

            private String taskId;
            public String getAssigneeName() {
                return assigneeName;
            }

            public void setAssigneeName(String assigneeName) {
                this.assigneeName = assigneeName;
            }

            public String getProcDefId() {
                return procDefId;
            }

            public void setProcDefId(String procDefId) {
                this.procDefId = procDefId;
            }

            public String getActivityId() {
                return activityId;
            }

            public void setActivityId(String activityId) {
                this.activityId = activityId;
            }

            public String getActivityName() {
                return activityName;
            }

            public void setActivityName(String activityName) {
                this.activityName = activityName;
            }

            public String getActivityType() {
                return activityType;
            }

            public void setActivityType(String activityType) {
                this.activityType = activityType;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public String getAssigneeId() {
                return assigneeId;
            }

            public void setAssigneeId(String assigneeId) {
                this.assigneeId = assigneeId;
            }

            public Object getCandidate() {
                return candidate;
            }

            public void setCandidate(Object candidate) {
                this.candidate = candidate;
            }

            public Object getCommentList() {
                return commentList;
            }

            public void setCommentList(Object commentList) {
                this.commentList = commentList;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public Object getWfCopyUser() {
                return wfCopyUser;
            }

            public void setWfCopyUser(Object wfCopyUser) {
                this.wfCopyUser = wfCopyUser;
            }

            public int getTransactionOrder() {
                return transactionOrder;
            }

            public void setTransactionOrder(int transactionOrder) {
                this.transactionOrder = transactionOrder;
            }

            public String getExecutionId() {
                return executionId;
            }

            public void setExecutionId(String executionId) {
                this.executionId = executionId;
            }

            public List<AssigneeInfoListBean> getAssigneeInfoList() {
                return assigneeInfoList;
            }

            public void setAssigneeInfoList(List<AssigneeInfoListBean> assigneeInfoList) {
                this.assigneeInfoList = assigneeInfoList;
            }

            private String activityName;
            private String activityType;
            private String duration;
            private String assigneeId;
            private String assigneeName;
            private Object candidate;
            private Object commentList;
            private String createTime;
            private String endTime;
            private Object wfCopyUser;
            private int transactionOrder;
            private String executionId;
            private List<AssigneeInfoListBean> assigneeInfoList;


            public static class AssigneeInfoListBean implements Serializable {
                /**
                 * assigneeName : admin
                 * completeTime : 2024-12-05 18:06:29
                 */

                private String assigneeName;

                public String getCompleteTime() {
                    return completeTime;
                }

                public void setCompleteTime(String completeTime) {
                    this.completeTime = completeTime;
                }

                public String getAssigneeName() {
                    return assigneeName;
                }

                public void setAssigneeName(String assigneeName) {
                    this.assigneeName = assigneeName;
                }

                private String completeTime;
            }
        }

        public static class ProcessFormListBean implements Serializable {
            /**
             * title : 测试
             * formRef : elForm
             * formModel : formData
             * size : medium
             * labelPosition : right
             * labelWidth : 100
             * formRules : rules
             * gutter : 15
             * disabled : true
             * span : 24
             * formBtns : false
             * fields : [{"__config__":{"label":"单行文本","labelWidth":null,"showLabel":true,"changeTag":true,"tag":"el-input","tagIcon":"input","required":true,"layout":"colFormItem","span":24,"document":"https://element.eleme.cn/#/zh-CN/component/input","regList":[],"formId":101,"renderKey":"1011730905968656","defaultValue":"少时诵诗书少时诵诗书是撒是撒是撒是撒是撒是撒"},"__slot__":{"prepend":"","append":""},"placeholder":"请输入单行文本","style":{"width":"100%"},"clearable":true,"prefix-icon":"","suffix-icon":"","maxlength":null,"show-word-limit":false,"readonly":false,"disabled":false,"__vModel__":"field101"}]
             */

            private String title;
            private String formRef;
            private String formModel;
            private String size;
            private String labelPosition;
            private int labelWidth;

            public int getSpan() {
                return span;
            }

            public void setSpan(int span) {
                this.span = span;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getFormRef() {
                return formRef;
            }

            public void setFormRef(String formRef) {
                this.formRef = formRef;
            }

            public String getFormModel() {
                return formModel;
            }

            public void setFormModel(String formModel) {
                this.formModel = formModel;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getLabelPosition() {
                return labelPosition;
            }

            public void setLabelPosition(String labelPosition) {
                this.labelPosition = labelPosition;
            }

            public int getLabelWidth() {
                return labelWidth;
            }

            public void setLabelWidth(int labelWidth) {
                this.labelWidth = labelWidth;
            }

            public String getFormRules() {
                return formRules;
            }

            public void setFormRules(String formRules) {
                this.formRules = formRules;
            }

            public int getGutter() {
                return gutter;
            }

            public void setGutter(int gutter) {
                this.gutter = gutter;
            }

            public boolean isDisabled() {
                return disabled;
            }

            public void setDisabled(boolean disabled) {
                this.disabled = disabled;
            }

            public boolean isFormBtns() {
                return formBtns;
            }

            public void setFormBtns(boolean formBtns) {
                this.formBtns = formBtns;
            }

            public List<FieldsBean> getFields() {
                return fields;
            }

            public void setFields(List<FieldsBean> fields) {
                this.fields = fields;
            }

            private String formRules;
            private int gutter;
            private boolean disabled;
            private int span;
            private boolean formBtns;
            private List<FieldsBean> fields;
            public static class FieldsBean implements Serializable {
                /**
                 * __config__ : {"label":"单行文本","labelWidth":null,"showLabel":true,"changeTag":true,"tag":"el-input","tagIcon":"input","required":true,"layout":"colFormItem","span":24,"document":"https://element.eleme.cn/#/zh-CN/component/input","regList":[],"formId":101,"renderKey":"1011730905968656","defaultValue":"少时诵诗书少时诵诗书是撒是撒是撒是撒是撒是撒"}
                 * __slot__ : {"prepend":"","append":""}
                 * placeholder : 请输入单行文本
                 * style : {"width":"100%"}
                 * clearable : true
                 * prefix-icon :
                 * suffix-icon :
                 * maxlength : null
                 * show-word-limit : false
                 * readonly : false
                 * disabled : false
                 * __vModel__ : field101
                 */

                private ConfigBean __config__;
                private SlotBean __slot__;
                private String placeholder;
                private StyleBean style;

                public String getPlaceholder() {
                    return placeholder;
                }

                public void setPlaceholder(String placeholder) {
                    this.placeholder = placeholder;
                }

                public StyleBean getStyle() {
                    return style;
                }

                public void setStyle(StyleBean style) {
                    this.style = style;
                }

                public String getPrefixIcon() {
                    return prefixIcon;
                }

                public void setPrefixIcon(String prefixIcon) {
                    this.prefixIcon = prefixIcon;
                }

                public ConfigBean get__config__() {
                    return __config__;
                }

                public void set__config__(ConfigBean __config__) {
                    this.__config__ = __config__;
                }

                public SlotBean get__slot__() {
                    return __slot__;
                }

                public void set__slot__(SlotBean __slot__) {
                    this.__slot__ = __slot__;
                }

                public boolean isClearable() {
                    return clearable;
                }

                public void setClearable(boolean clearable) {
                    this.clearable = clearable;
                }

                public String getSuffixIcon() {
                    return suffixIcon;
                }

                public void setSuffixIcon(String suffixIcon) {
                    this.suffixIcon = suffixIcon;
                }

                public Object getMaxlength() {
                    return maxlength;
                }

                public void setMaxlength(Object maxlength) {
                    this.maxlength = maxlength;
                }

                public boolean isShowWordLimit() {
                    return showWordLimit;
                }

                public void setShowWordLimit(boolean showWordLimit) {
                    this.showWordLimit = showWordLimit;
                }

                public boolean isReadonly() {
                    return readonly;
                }

                public void setReadonly(boolean readonly) {
                    this.readonly = readonly;
                }

                public boolean isDisabled() {
                    return disabled;
                }

                public void setDisabled(boolean disabled) {
                    this.disabled = disabled;
                }

                public String get__vModel__() {
                    return __vModel__;
                }

                public void set__vModel__(String __vModel__) {
                    this.__vModel__ = __vModel__;
                }

                private boolean clearable;
                @JSONField(name = "prefix-icon")
                private String prefixIcon;

                @JSONField(name = "suffix-icon")
                private String suffixIcon;

                private Object maxlength;

                @JSONField(name = "show-word-limit")
                private boolean showWordLimit;
                private boolean readonly;
                private boolean disabled;
                private String __vModel__;

                public static class ConfigBean implements Serializable {
                    /**
                     * label : 单行文本
                     * labelWidth : null
                     * showLabel : true
                     * changeTag : true
                     * tag : el-input
                     * tagIcon : input
                     * required : true
                     * layout : colFormItem
                     * span : 24
                     * document : https://element.eleme.cn/#/zh-CN/component/input
                     * regList : []
                     * formId : 101
                     * renderKey : 1011730905968656
                     * defaultValue : 少时诵诗书少时诵诗书是撒是撒是撒是撒是撒是撒
                     */

                    private String label;
                    private Object labelWidth;

                    public String getFormId() {
                        return formId;
                    }

                    public void setFormId(String formId) {
                        this.formId = formId;
                    }

                    public String getLabel() {
                        return label;
                    }

                    public void setLabel(String label) {
                        this.label = label;
                    }

                    public Object getLabelWidth() {
                        return labelWidth;
                    }

                    public void setLabelWidth(Object labelWidth) {
                        this.labelWidth = labelWidth;
                    }

                    public boolean isShowLabel() {
                        return showLabel;
                    }

                    public void setShowLabel(boolean showLabel) {
                        this.showLabel = showLabel;
                    }

                    public boolean isChangeTag() {
                        return changeTag;
                    }

                    public void setChangeTag(boolean changeTag) {
                        this.changeTag = changeTag;
                    }

                    public String getTag() {
                        return tag;
                    }

                    public void setTag(String tag) {
                        this.tag = tag;
                    }

                    public String getTagIcon() {
                        return tagIcon;
                    }

                    public void setTagIcon(String tagIcon) {
                        this.tagIcon = tagIcon;
                    }

                    public boolean isRequired() {
                        return required;
                    }

                    public void setRequired(boolean required) {
                        this.required = required;
                    }

                    public String getLayout() {
                        return layout;
                    }

                    public void setLayout(String layout) {
                        this.layout = layout;
                    }

                    public int getSpan() {
                        return span;
                    }

                    public void setSpan(int span) {
                        this.span = span;
                    }

                    public String getDocument() {
                        return document;
                    }

                    public void setDocument(String document) {
                        this.document = document;
                    }

                    public String getRenderKey() {
                        return renderKey;
                    }

                    public void setRenderKey(String renderKey) {
                        this.renderKey = renderKey;
                    }

                    public String getDefaultValue() {
                        return defaultValue;
                    }

                    public void setDefaultValue(String defaultValue) {
                        this.defaultValue = defaultValue;
                    }

                    public List<?> getRegList() {
                        return regList;
                    }

                    public void setRegList(List<?> regList) {
                        this.regList = regList;
                    }

                    private boolean showLabel;
                    private boolean changeTag;
                    private String tag;
                    private String tagIcon;
                    private boolean required;
                    private String layout;
                    private int span;
                    private String document;
                    private String formId;
                    private String renderKey;
                    private String defaultValue;
                    private List<?> regList;
                }


                public static class SlotBean implements Serializable {
                    public String getPrepend() {
                        return prepend;
                    }

                    public void setPrepend(String prepend) {
                        this.prepend = prepend;
                    }

                    public String getAppend() {
                        return append;
                    }

                    public void setAppend(String append) {
                        this.append = append;
                    }

                    /**
                     * prepend :
                     * append :
                     */

                    private String prepend;
                    private String append;
                }

                public static class StyleBean implements Serializable {
                    public String getWidth() {
                        return width;
                    }

                    public void setWidth(String width) {
                        this.width = width;
                    }

                    /**
                     * width : 100%
                     */

                    private String width;
                }
            }
        }
    }
}
