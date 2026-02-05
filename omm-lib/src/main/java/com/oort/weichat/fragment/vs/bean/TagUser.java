package com.oort.weichat.fragment.vs.bean;


import java.util.List;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/9/5-21:26.
 * Version 1.0
 * Description:
 */
public class TagUser<T> {

    /**
     * count : 34
     * counts : 34
     * list : [{"basic_field":[],"check_id":0,"custom_field":[],"dept_list":[{"created_at":"2025-08-13 14:46:00","dept_id":"5cce87b5-6182-4c61-b516-c91d37ed8118","deptinfo":{"check_id":0,"created_at":"2025-08-07 16:41:16","dept_code":"test1","dept_code_path":"/9144030071526726XG/dsws/test1/","dept_id":"5cce87b5-6182-4c61-b516-c91d37ed8118","dept_level":3,"dept_name":"test1","dept_name_path":"/爱干净环保集团/打扫卫生项目/","dept_photo":"","dept_type":3,"ex_data":{"address":"","area":[],"bank":"","bankac":"","company_logo":"","legal_name":"","name":"","phone":"","phone1":"","remarks":"333333"},"parent_dept_id":"cdfebf30-7f44-43e0-b289-b6e1542534ca","sort":4,"status":1,"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","updated_at":"2025-08-14 19:17:29"},"ex_data":null,"job":[],"post":[],"sort":3,"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","updated_at":"2025-08-13 14:46:00","user_id":"0c647204-4a7e-4c18-b9be-b12b3d63a5be"},{"created_at":"2025-07-21 21:50:24","dept_id":"e547a062-b432-4620-a624-3c0670b00d78","deptinfo":{"check_id":0,"created_at":"2025-04-10 16:04:46","dept_code":"123456789","dept_code_path":"/123456789/","dept_id":"e547a062-b432-4620-a624-3c0670b00d78","dept_level":1,"dept_name":"技术支持部门","dept_name_path":"/","dept_photo":"","dept_type":3,"ex_data":{"address":"","area":[],"bank":"","bankac":"","company_logo":"","legal_name":"","name":"","phone":"","phone1":"","remarks":"333333"},"parent_dept_id":"","sort":1,"status":1,"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","updated_at":"2025-08-14 19:17:29"},"ex_data":{"job_id":"","mark":"","phone":"","post_id":""},"job":[],"post":[],"sort":31,"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","updated_at":"2025-07-21 21:50:45","user_id":"0c647204-4a7e-4c18-b9be-b12b3d63a5be"}],"form":3,"im_user_info":{"im_account":"","im_user_id":"","tenant_id":"","user_id":""},"is_admin":0,"is_tenant_admin":0,"login_status":0,"photo":"","status":1,"tenant":{"tenant_id":"","tenant_name":""},"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","unique_id":"d12f004b-9d16-464c-ad93-f4d85dadaced","unique_login_id":null,"user_detail":{"created_at":"2025-07-21 21:50:24","ex_data":{"address":"","area":[],"dingding":"","email":"","idcard":"","mark":"","phone":"","postCode":"","realName":"","sex":"","userCode":"","weixin":""},"tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","updated_at":"2025-08-12 14:16:39","user_id":"0c647204-4a7e-4c18-b9be-b12b3d63a5be"},"user_id":"0c647204-4a7e-4c18-b9be-b12b3d63a5be","user_ident_info":{"level":0,"two_factor":0,"user_ident":null},"user_name":"汪亚超"}]
     * page : 1
     * pages : 34
     * pagesize : 1
     */

    private int count;
    private int counts;
    private int page;
    private int pages;
    private int pageSize;
    private List<T> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

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
        return pageSize;
    }

    public void setPagesize(int pagesize) {
        this.pageSize = pagesize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

}
