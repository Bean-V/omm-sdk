package com.oortcloud.basemodule.user;


import java.io.Serializable;
import java.util.List;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/7-11:24.
 * Version 1.0
 * Description:多租户 用户信息 暂存  以后的分离
 */
public class NewUserInfo implements Serializable {

    /**
     *
     * code : 200
     * data : {"user_id":"922f3955-4f16-447f-b054-9d7711f35f7d","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","unique_id":"7c7eda93-bec7-46b5-96d0-cca26e061438","user_name":"李夜","photo":"http://183.62.103.20:21410/bus/wj1/group1/default/20250728/19/13/4/9a504fc2d5628535e5e3a042cfe597c8a7ef636e.jpeg?t=1753701182979","status":1,"form":3,"is_tenant_admin":0,"check_id":0,"dept_list":[{"user_id":"922f3955-4f16-447f-b054-9d7711f35f7d","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_id":"e547a062-b432-4620-a624-3c0670b00d78","sort":13,"ex_data":{},"created_at":"2025-05-06 16:22:45","updated_at":"2025-05-06 16:22:45","deptinfo":{"dept_id":"e547a062-b432-4620-a624-3c0670b00d78","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_code":"123456789","parent_dept_id":"","dept_type":3,"dept_name":"技术支持部门","dept_code_path":"/123456789/","dept_name_path":"/","dept_level":1,"sort":1,"status":1,"dept_photo":"http://183.62.103.20:21410/bus/wj1/group1/default/20250422/23/58/4/头像","ex_data":{"address":"","area":"","bank":"","bankac":"","company_logo":"","legal_name":"","name":"","phone":"","phone1":"","remarks":"管理员于2025年4月22日23时57分编辑了组织名称信息。"},"created_at":"2025-04-10 16:04:46","updated_at":"2025-06-09 20:55:27","check_id":0},"job":[{"job_id":"f132689f-63bb-445f-9ed9-f2fa0e44dbc3","tenantId":"0e391fd7-1033-4f09-88c0-187582fee462","userId":"922f3955-4f16-447f-b054-9d7711f35f7d","deptId":"e547a062-b432-4620-a624-3c0670b00d78","name":"应用系统管理员","code":"020","type":"1","pjob_id":"","remark":"负责调用业务中台能力的请求，以及与业务中台进行接口对接与数据同步"}],"post":[{"post_id":"7202ef0b-6f2e-472f-969c-bfc7f1e65cd2","tenantId":"0e391fd7-1033-4f09-88c0-187582fee462","userId":"922f3955-4f16-447f-b054-9d7711f35f7d","deptId":"e547a062-b432-4620-a624-3c0670b00d78","name":"正国级","code":"001","type":"1","ppost_id":"","remark":"中共中央总书记、国家主席、中央军委主席\n国务院总理、全国人大常委会委员长、全国政协主席\n中央政治局常委"}]}],"user_detail":{"user_id":"922f3955-4f16-447f-b054-9d7711f35f7d","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","ex_data":{"address":"点点滴滴","area":["北京市","北京市"],"email":"5********@qq.com","mark":"新用户添加","phone":"189*****601","postCode":"34553","realName":"李*","sex":"1"},"created_at":"2025-05-06 16:22:45","updated_at":"2025-07-28 19:13:43"},"is_admin":0,"user_ident_info":{"two_factor":2,"level":0,"user_ident":[{"identity_type":0,"status":1,"remark":"弱","identifier":"********","identifier_backup":"********"},{"identity_type":1,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":2,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":3,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":4,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":5,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":7,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":10,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":20,"status":0,"remark":"","identifier":"","identifier_backup":""}]}}
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

    public static class DataBean implements Serializable {
        /**
         * user_id : 922f3955-4f16-447f-b054-9d7711f35f7d
         * tenant_id : 0e391fd7-1033-4f09-88c0-187582fee462
         * unique_id : 7c7eda93-bec7-46b5-96d0-cca26e061438
         * user_name : 李夜
         * photo : http://183.62.103.20:21410/bus/wj1/group1/default/20250728/19/13/4/9a504fc2d5628535e5e3a042cfe597c8a7ef636e.jpeg?t=1753701182979
         * status : 1
         * form : 3
         * is_tenant_admin : 0
         * check_id : 0
         * dept_list : [{"user_id":"922f3955-4f16-447f-b054-9d7711f35f7d","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_id":"e547a062-b432-4620-a624-3c0670b00d78","sort":13,"ex_data":{},"created_at":"2025-05-06 16:22:45","updated_at":"2025-05-06 16:22:45","deptinfo":{"dept_id":"e547a062-b432-4620-a624-3c0670b00d78","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_code":"123456789","parent_dept_id":"","dept_type":3,"dept_name":"技术支持部门","dept_code_path":"/123456789/","dept_name_path":"/","dept_level":1,"sort":1,"status":1,"dept_photo":"http://183.62.103.20:21410/bus/wj1/group1/default/20250422/23/58/4/头像","ex_data":{"address":"","area":"","bank":"","bankac":"","company_logo":"","legal_name":"","name":"","phone":"","phone1":"","remarks":"管理员于2025年4月22日23时57分编辑了组织名称信息。"},"created_at":"2025-04-10 16:04:46","updated_at":"2025-06-09 20:55:27","check_id":0},"job":[{"job_id":"f132689f-63bb-445f-9ed9-f2fa0e44dbc3","tenantId":"0e391fd7-1033-4f09-88c0-187582fee462","userId":"922f3955-4f16-447f-b054-9d7711f35f7d","deptId":"e547a062-b432-4620-a624-3c0670b00d78","name":"应用系统管理员","code":"020","type":"1","pjob_id":"","remark":"负责调用业务中台能力的请求，以及与业务中台进行接口对接与数据同步"}],"post":[{"post_id":"7202ef0b-6f2e-472f-969c-bfc7f1e65cd2","tenantId":"0e391fd7-1033-4f09-88c0-187582fee462","userId":"922f3955-4f16-447f-b054-9d7711f35f7d","deptId":"e547a062-b432-4620-a624-3c0670b00d78","name":"正国级","code":"001","type":"1","ppost_id":"","remark":"中共中央总书记、国家主席、中央军委主席\n国务院总理、全国人大常委会委员长、全国政协主席\n中央政治局常委"}]}]
         * user_detail : {"user_id":"922f3955-4f16-447f-b054-9d7711f35f7d","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","ex_data":{"address":"点点滴滴","area":["北京市","北京市"],"email":"5********@qq.com","mark":"新用户添加","phone":"189*****601","postCode":"34553","realName":"李*","sex":"1"},"created_at":"2025-05-06 16:22:45","updated_at":"2025-07-28 19:13:43"}
         * is_admin : 0
         * user_ident_info : {"two_factor":2,"level":0,"user_ident":[{"identity_type":0,"status":1,"remark":"弱","identifier":"********","identifier_backup":"********"},{"identity_type":1,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":2,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":3,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":4,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":5,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":7,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":10,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":20,"status":0,"remark":"","identifier":"","identifier_backup":""}]}
         */

        private String user_id;
        private String tenant_id;
        private String unique_id;
        private String user_name;
        private String photo;
        private int status;
        private int form;
        private int is_tenant_admin;
        private int check_id;
        private UserDetailBean user_detail;
        private int is_admin;
        private UserIdentInfoBean user_ident_info;
        private List<DeptListBean> dept_list;
        private ImUserInfoBean im_user_info;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getTenant_id() {
            return tenant_id;
        }

        public void setTenant_id(String tenant_id) {
            this.tenant_id = tenant_id;
        }

        public String getUnique_id() {
            return unique_id;
        }

        public void setUnique_id(String unique_id) {
            this.unique_id = unique_id;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getForm() {
            return form;
        }

        public void setForm(int form) {
            this.form = form;
        }

        public int getIs_tenant_admin() {
            return is_tenant_admin;
        }

        public void setIs_tenant_admin(int is_tenant_admin) {
            this.is_tenant_admin = is_tenant_admin;
        }

        public int getCheck_id() {
            return check_id;
        }

        public void setCheck_id(int check_id) {
            this.check_id = check_id;
        }

        public UserDetailBean getUser_detail() {
            return user_detail;
        }

        public void setUser_detail(UserDetailBean user_detail) {
            this.user_detail = user_detail;
        }

        public int getIs_admin() {
            return is_admin;
        }

        public void setIs_admin(int is_admin) {
            this.is_admin = is_admin;
        }

        public UserIdentInfoBean getUser_ident_info() {
            return user_ident_info;
        }

        public void setUser_ident_info(UserIdentInfoBean user_ident_info) {
            this.user_ident_info = user_ident_info;
        }

        public List<DeptListBean> getDept_list() {
            return dept_list;
        }

        public void setDept_list(List<DeptListBean> dept_list) {
            this.dept_list = dept_list;
        }

        public ImUserInfoBean getIm_user_info() {
            return im_user_info;
        }

        public void setIm_user_info(ImUserInfoBean im_user_info) {
            this.im_user_info = im_user_info;
        }

        public static class ImUserInfoBean implements Serializable{
            /**
             * user_id : 1b187874-17ef-4745-9ede-89b4145cfce6
             * tenant_id : 0e391fd7-1033-4f09-88c0-187582fee462
             * im_account : 10000007426358
             * im_user_id : 10000007
             */

            private String user_id;
            private String tenant_id;
            private String im_account;
            private String im_user_id;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getTenant_id() {
                return tenant_id;
            }

            public void setTenant_id(String tenant_id) {
                this.tenant_id = tenant_id;
            }

            public String getIm_account() {
                return im_account;
            }

            public void setIm_account(String im_account) {
                this.im_account = im_account;
            }

            public String getIm_user_id() {
                return im_user_id;
            }

            public void setIm_user_id(String im_user_id) {
                this.im_user_id = im_user_id;
            }
        }

        public static class UserDetailBean implements Serializable {
            /**
             * user_id : 922f3955-4f16-447f-b054-9d7711f35f7d
             * tenant_id : 0e391fd7-1033-4f09-88c0-187582fee462
             * ex_data : {"address":"点点滴滴","area":["北京市","北京市"],"email":"5********@qq.com","mark":"新用户添加","phone":"189*****601","postCode":"34553","realName":"李*","sex":"1"}
             * created_at : 2025-05-06 16:22:45
             * updated_at : 2025-07-28 19:13:43
             */

            private String user_id;
            private String tenant_id;
            private ExDataBean ex_data;
            private String created_at;
            private String updated_at;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getTenant_id() {
                return tenant_id;
            }

            public void setTenant_id(String tenant_id) {
                this.tenant_id = tenant_id;
            }

            public ExDataBean getEx_data() {
                return ex_data;
            }

            public void setEx_data(ExDataBean ex_data) {
                this.ex_data = ex_data;
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

            public static class ExDataBean implements Serializable {
                /**
                 * address : 点点滴滴
                 * area : ["北京市","北京市"]
                 * email : 5********@qq.com
                 * mark : 新用户添加
                 * phone : 189*****601
                 * postCode : 34553
                 * realName : 李*
                 * sex : 1
                 */

                private String address;
                private String email;
                private String mark;
                private String phone;
                private String postCode;
                private String realName;
                private String sex;
                private List<String> area;

                public String getAddress() {
                    return address;
                }

                public void setAddress(String address) {
                    this.address = address;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public String getMark() {
                    return mark;
                }

                public void setMark(String mark) {
                    this.mark = mark;
                }

                public String getPhone() {
                    return phone;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }

                public String getPostCode() {
                    return postCode;
                }

                public void setPostCode(String postCode) {
                    this.postCode = postCode;
                }

                public String getRealName() {
                    return realName;
                }

                public void setRealName(String realName) {
                    this.realName = realName;
                }

                public String getSex() {
                    return sex;
                }

                public void setSex(String sex) {
                    this.sex = sex;
                }

                public List<String> getArea() {
                    return area;
                }

                public void setArea(List<String> area) {
                    this.area = area;
                }
            }
        }

        public static class UserIdentInfoBean implements Serializable {
            /**
             * two_factor : 2
             * level : 0
             * user_ident : [{"identity_type":0,"status":1,"remark":"弱","identifier":"********","identifier_backup":"********"},{"identity_type":1,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":2,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":3,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":4,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":5,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":7,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":10,"status":0,"remark":"","identifier":"","identifier_backup":""},{"identity_type":20,"status":0,"remark":"","identifier":"","identifier_backup":""}]
             */

            private int two_factor;
            private int level;
            private List<UserIdentBean> user_ident;

            public int getTwo_factor() {
                return two_factor;
            }

            public void setTwo_factor(int two_factor) {
                this.two_factor = two_factor;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public List<UserIdentBean> getUser_ident() {
                return user_ident;
            }

            public void setUser_ident(List<UserIdentBean> user_ident) {
                this.user_ident = user_ident;
            }

            public static class UserIdentBean implements Serializable {
                /**
                 * identity_type : 0
                 * status : 1
                 * remark : 弱
                 * identifier : ********
                 * identifier_backup : ********
                 */

                private int identity_type;
                private int status;
                private String remark;
                private String identifier;
                private String identifier_backup;

                public int getIdentity_type() {
                    return identity_type;
                }

                public void setIdentity_type(int identity_type) {
                    this.identity_type = identity_type;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }

                public String getIdentifier() {
                    return identifier;
                }

                public void setIdentifier(String identifier) {
                    this.identifier = identifier;
                }

                public String getIdentifier_backup() {
                    return identifier_backup;
                }

                public void setIdentifier_backup(String identifier_backup) {
                    this.identifier_backup = identifier_backup;
                }
            }
        }

        public static class DeptListBean implements Serializable{
            /**
             * user_id : 922f3955-4f16-447f-b054-9d7711f35f7d
             * tenant_id : 0e391fd7-1033-4f09-88c0-187582fee462
             * dept_id : e547a062-b432-4620-a624-3c0670b00d78
             * sort : 13
             * ex_data : {}
             * created_at : 2025-05-06 16:22:45
             * updated_at : 2025-05-06 16:22:45
             * deptinfo : {"dept_id":"e547a062-b432-4620-a624-3c0670b00d78","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_code":"123456789","parent_dept_id":"","dept_type":3,"dept_name":"技术支持部门","dept_code_path":"/123456789/","dept_name_path":"/","dept_level":1,"sort":1,"status":1,"dept_photo":"http://183.62.103.20:21410/bus/wj1/group1/default/20250422/23/58/4/头像","ex_data":{"address":"","area":"","bank":"","bankac":"","company_logo":"","legal_name":"","name":"","phone":"","phone1":"","remarks":"管理员于2025年4月22日23时57分编辑了组织名称信息。"},"created_at":"2025-04-10 16:04:46","updated_at":"2025-06-09 20:55:27","check_id":0}
             * job : [{"job_id":"f132689f-63bb-445f-9ed9-f2fa0e44dbc3","tenantId":"0e391fd7-1033-4f09-88c0-187582fee462","userId":"922f3955-4f16-447f-b054-9d7711f35f7d","deptId":"e547a062-b432-4620-a624-3c0670b00d78","name":"应用系统管理员","code":"020","type":"1","pjob_id":"","remark":"负责调用业务中台能力的请求，以及与业务中台进行接口对接与数据同步"}]
             * post : [{"post_id":"7202ef0b-6f2e-472f-969c-bfc7f1e65cd2","tenantId":"0e391fd7-1033-4f09-88c0-187582fee462","userId":"922f3955-4f16-447f-b054-9d7711f35f7d","deptId":"e547a062-b432-4620-a624-3c0670b00d78","name":"正国级","code":"001","type":"1","ppost_id":"","remark":"中共中央总书记、国家主席、中央军委主席\n国务院总理、全国人大常委会委员长、全国政协主席\n中央政治局常委"}]
             */

            private String user_id;
            private String tenant_id;
            private String dept_id;
            private int sort;
            private ExDataBeanX ex_data;
            private String created_at;
            private String updated_at;
            private DeptinfoBean deptinfo;
            private List<JobBean> job;
            private List<PostBean> post;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getTenant_id() {
                return tenant_id;
            }

            public void setTenant_id(String tenant_id) {
                this.tenant_id = tenant_id;
            }

            public String getDept_id() {
                return dept_id;
            }

            public void setDept_id(String dept_id) {
                this.dept_id = dept_id;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
            }

            public ExDataBeanX getEx_data() {
                return ex_data;
            }

            public void setEx_data(ExDataBeanX ex_data) {
                this.ex_data = ex_data;
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

            public DeptinfoBean getDeptinfo() {
                return deptinfo;
            }

            public void setDeptinfo(DeptinfoBean deptinfo) {
                this.deptinfo = deptinfo;
            }

            public List<JobBean> getJob() {
                return job;
            }

            public void setJob(List<JobBean> job) {
                this.job = job;
            }

            public List<PostBean> getPost() {
                return post;
            }

            public void setPost(List<PostBean> post) {
                this.post = post;
            }

            public static class ExDataBeanX implements Serializable{

            }

            public static class DeptinfoBean implements Serializable {
                /**
                 * dept_id : e547a062-b432-4620-a624-3c0670b00d78
                 * tenant_id : 0e391fd7-1033-4f09-88c0-187582fee462
                 * dept_code : 123456789
                 * parent_dept_id :
                 * dept_type : 3
                 * dept_name : 技术支持部门
                 * dept_code_path : /123456789/
                 * dept_name_path : /
                 * dept_level : 1
                 * sort : 1
                 * status : 1
                 * dept_photo : http://183.62.103.20:21410/bus/wj1/group1/default/20250422/23/58/4/头像
                 * ex_data : {"address":"","area":"","bank":"","bankac":"","company_logo":"","legal_name":"","name":"","phone":"","phone1":"","remarks":"管理员于2025年4月22日23时57分编辑了组织名称信息。"}
                 * created_at : 2025-04-10 16:04:46
                 * updated_at : 2025-06-09 20:55:27
                 * check_id : 0
                 */

                private String dept_id;
                private String tenant_id;
                private String dept_code;
                private String parent_dept_id;
                private int dept_type;
                private String dept_name;
                private String dept_code_path;
                private String dept_name_path;
                private int dept_level;
                private int sort;
                private int status;
                private String dept_photo;
                private ExDataBean ex_data;
                private String created_at;
                private String updated_at;
                private int check_id;

                public String getDept_id() {
                    return dept_id;
                }

                public void setDept_id(String dept_id) {
                    this.dept_id = dept_id;
                }

                public String getTenant_id() {
                    return tenant_id;
                }

                public void setTenant_id(String tenant_id) {
                    this.tenant_id = tenant_id;
                }

                public String getDept_code() {
                    return dept_code;
                }

                public void setDept_code(String dept_code) {
                    this.dept_code = dept_code;
                }

                public String getParent_dept_id() {
                    return parent_dept_id;
                }

                public void setParent_dept_id(String parent_dept_id) {
                    this.parent_dept_id = parent_dept_id;
                }

                public int getDept_type() {
                    return dept_type;
                }

                public void setDept_type(int dept_type) {
                    this.dept_type = dept_type;
                }

                public String getDept_name() {
                    return dept_name;
                }

                public void setDept_name(String dept_name) {
                    this.dept_name = dept_name;
                }

                public String getDept_code_path() {
                    return dept_code_path;
                }

                public void setDept_code_path(String dept_code_path) {
                    this.dept_code_path = dept_code_path;
                }

                public String getDept_name_path() {
                    return dept_name_path;
                }

                public void setDept_name_path(String dept_name_path) {
                    this.dept_name_path = dept_name_path;
                }

                public int getDept_level() {
                    return dept_level;
                }

                public void setDept_level(int dept_level) {
                    this.dept_level = dept_level;
                }

                public int getSort() {
                    return sort;
                }

                public void setSort(int sort) {
                    this.sort = sort;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public String getDept_photo() {
                    return dept_photo;
                }

                public void setDept_photo(String dept_photo) {
                    this.dept_photo = dept_photo;
                }

                public ExDataBean getEx_data() {
                    return ex_data;
                }

                public void setEx_data(ExDataBean ex_data) {
                    this.ex_data = ex_data;
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

                public int getCheck_id() {
                    return check_id;
                }

                public void setCheck_id(int check_id) {
                    this.check_id = check_id;
                }

                public static class ExDataBean implements Serializable {
                    /**
                     * address :
                     * area :
                     * bank :
                     * bankac :
                     * company_logo :
                     * legal_name :
                     * name :
                     * phone :
                     * phone1 :
                     * remarks : 管理员于2025年4月22日23时57分编辑了组织名称信息。
                     */

                    private String address;
                    private List<String> area;
                    private String bank;
                    private String bankac;
                    private String company_logo;
                    private String legal_name;
                    private String name;
                    private String phone;
                    private String phone1;
                    private String remarks;

                    public String getAddress() {
                        return address;
                    }

                    public void setAddress(String address) {
                        this.address = address;
                    }

                    public List<String> getArea() {
                        return area;
                    }

                    public void setArea(List<String> area) {
                        this.area = area;
                    }

                    public String getBank() {
                        return bank;
                    }

                    public void setBank(String bank) {
                        this.bank = bank;
                    }

                    public String getBankac() {
                        return bankac;
                    }

                    public void setBankac(String bankac) {
                        this.bankac = bankac;
                    }

                    public String getCompany_logo() {
                        return company_logo;
                    }

                    public void setCompany_logo(String company_logo) {
                        this.company_logo = company_logo;
                    }

                    public String getLegal_name() {
                        return legal_name;
                    }

                    public void setLegal_name(String legal_name) {
                        this.legal_name = legal_name;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getPhone() {
                        return phone;
                    }

                    public void setPhone(String phone) {
                        this.phone = phone;
                    }

                    public String getPhone1() {
                        return phone1;
                    }

                    public void setPhone1(String phone1) {
                        this.phone1 = phone1;
                    }

                    public String getRemarks() {
                        return remarks;
                    }

                    public void setRemarks(String remarks) {
                        this.remarks = remarks;
                    }
                }
            }

            public static class JobBean implements Serializable {
                /**
                 * job_id : f132689f-63bb-445f-9ed9-f2fa0e44dbc3
                 * tenantId : 0e391fd7-1033-4f09-88c0-187582fee462
                 * userId : 922f3955-4f16-447f-b054-9d7711f35f7d
                 * deptId : e547a062-b432-4620-a624-3c0670b00d78
                 * name : 应用系统管理员
                 * code : 020
                 * type : 1
                 * pjob_id :
                 * remark : 负责调用业务中台能力的请求，以及与业务中台进行接口对接与数据同步
                 */

                private String job_id;
                private String tenantId;
                private String userId;
                private String deptId;
                private String name;
                private String code;
                private String type;
                private String pjob_id;
                private String remark;

                public String getJob_id() {
                    return job_id;
                }

                public void setJob_id(String job_id) {
                    this.job_id = job_id;
                }

                public String getTenantId() {
                    return tenantId;
                }

                public void setTenantId(String tenantId) {
                    this.tenantId = tenantId;
                }

                public String getUserId() {
                    return userId;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }

                public String getDeptId() {
                    return deptId;
                }

                public void setDeptId(String deptId) {
                    this.deptId = deptId;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getPjob_id() {
                    return pjob_id;
                }

                public void setPjob_id(String pjob_id) {
                    this.pjob_id = pjob_id;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }
            }

            public static class PostBean implements Serializable {
                /**
                 * post_id : 7202ef0b-6f2e-472f-969c-bfc7f1e65cd2
                 * tenantId : 0e391fd7-1033-4f09-88c0-187582fee462
                 * userId : 922f3955-4f16-447f-b054-9d7711f35f7d
                 * deptId : e547a062-b432-4620-a624-3c0670b00d78
                 * name : 正国级
                 * code : 001
                 * type : 1
                 * ppost_id :
                 * remark : 中共中央总书记、国家主席、中央军委主席
                 国务院总理、全国人大常委会委员长、全国政协主席
                 中央政治局常委
                 */

                private String post_id;
                private String tenantId;
                private String userId;
                private String deptId;
                private String name;
                private String code;
                private String type;
                private String ppost_id;
                private String remark;

                public String getPost_id() {
                    return post_id;
                }

                public void setPost_id(String post_id) {
                    this.post_id = post_id;
                }

                public String getTenantId() {
                    return tenantId;
                }

                public void setTenantId(String tenantId) {
                    this.tenantId = tenantId;
                }

                public String getUserId() {
                    return userId;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }

                public String getDeptId() {
                    return deptId;
                }

                public void setDeptId(String deptId) {
                    this.deptId = deptId;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getPpost_id() {
                    return ppost_id;
                }

                public void setPpost_id(String ppost_id) {
                    this.ppost_id = ppost_id;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }
            }
        }
    }
}
