package com.oortcloud.oort_zhifayi.base;


/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/30-14:38.
 * Version 1.0
 * Description:
 */
public class TaskList {

    /**
     * currentTime : 0
     * msg : 成功
     * code : 200
     * data : {"pages":3,"counts":3,"pagesize":1,"count":3,"page":1,"list":[{"end_at":1917792000,"checkpoint_len":2,"loop_run":0,"loop_exp_time":0,"loop_gap":0,"loop_week":[1,2,3,4,5,6],"created_at":"2025-08-30 10:06:53","start_at_str":"2025-08-20","start_at":1755619200,"loop_time":["10:10:00","12:00:00","15:10:00"],"uuid":"1a3d2666-e0fe-459f-81da-c3c21ef3628c","points":[{"coord_system_type":1,"address":"广东省深圳市福田区福中三路","lng":114.06389328820096,"lat":22.54783542612225},{"coord_system_type":1,"address":"广东省深圳市福田区福中三路","lng":114.1277550819852,"lat":22.5613247452307}],"loop_type":3,"loop_month":[],"updated_at":"2025-08-30 10:06:53","end_at_str":"2030-10-10","name":"每周街道巡更任务","id":"0a3b101e-2c3e-4c01-abd9-f0b2bae992ca","describe":"每周街道巡更任务","userinfo":[{"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","is_admin":1,"user_name_fpy":"","unique_id":"717ddd64-d8de-4e33-b2ed-0578e967b82f","dept_list":[{"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_code":"","ex_data":{"post_id":"","phone":"","job_id":"","mark":""},"updated_at":"2025-05-06 16:21:30","user_id":"1a3d2666-e0fe-459f-81da-c3c21ef3628c","created_at":"2025-04-10 18:23:19","sort":6,"deptinfo":{"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_code":"123456789","dept_code_path":"/123456789/","dept_photo":"","dept_name_path":"/","dept_name":"技术支持部门","created_at":"2025-04-10 16:04:46","sort":1,"dept_level":1,"ex_data":{"area":[],"bank":"","address":"","company_logo":"","phone":"","name":"","bankac":"","legal_name":"","remarks":"333333","phone1":""},"updated_at":"2025-08-14 19:17:29","parent_dept_code":"","dept_type":3,"dept_id":"e547a062-b432-4620-a624-3c0670b00d78"}}],"is_tenant_admin":1,"user_id":"1a3d2666-e0fe-459f-81da-c3c21ef3628c","user_name":"吴镇江","photo":"http://183.62.103.20:21410/bus/wj1/group1/default/20250506/16/21/4/头像","user_name_py":"","status":1},{"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","is_admin":0,"user_name_fpy":"","unique_id":"7c7eda93-bec7-46b5-96d0-cca26e061438","dept_list":[{"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_code":"","ex_data":{},"updated_at":"2025-05-06 16:22:45","user_id":"922f3955-4f16-447f-b054-9d7711f35f7d","created_at":"2025-05-06 16:22:45","sort":13,"deptinfo":{"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_code":"123456789","dept_code_path":"/123456789/","dept_photo":"","dept_name_path":"/","dept_name":"技术支持部门","created_at":"2025-04-10 16:04:46","sort":1,"dept_level":1,"ex_data":{"area":[],"bank":"","address":"","company_logo":"","phone":"","name":"","bankac":"","legal_name":"","remarks":"333333","phone1":""},"updated_at":"2025-08-14 19:17:29","parent_dept_code":"","dept_type":3,"dept_id":"e547a062-b432-4620-a624-3c0670b00d78"}}],"is_tenant_admin":0,"user_id":"922f3955-4f16-447f-b054-9d7711f35f7d","user_name":"李夜","photo":"http://183.62.103.20:21410/bus/wj1/group1/default/20250728/19/13/4/9a504fc2d5628535e5e3a042cfe597c8a7ef636e.jpeg?t=1753701182979","user_name_py":"","status":1}],"status":1,"uuids":["1a3d2666-e0fe-459f-81da-c3c21ef3628c","922f3955-4f16-447f-b054-9d7711f35f7d"]}]}
     * resultCode : 0
     */
    private int currentTime;
    private String msg;
    private int code;
    private String data;
    private int resultCode;

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
