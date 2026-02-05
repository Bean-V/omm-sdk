package com.oortcloud.oort_zhifayi;

import java.io.Serializable;
import java.util.List;


public class CUser implements Serializable {



    public static boolean in;

    public static int status = 0;

    public static CUser cUser = null;


    public int getTask_count() {
        return task_count;
    }

    public void setTask_count(int task_count) {
        this.task_count = task_count;
    }

    public int getTask_undone_count() {
        return task_undone_count;
    }

    public void setTask_undone_count(int task_undone_count) {
        this.task_undone_count = task_undone_count;
    }

    public UserinfoBean     getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserinfoBean userinfo) {
        this.userinfo = userinfo;
    }

    public static int taskCount = 0;

    public static String taskId = "";

    public static String token = "";


    public int getEvent_count() {
        return event_count;
    }

    public void setEvent_count(int event_count) {
        this.event_count = event_count;
    }

    /**
     * task_count : 100
     * task_undone_count : 100
     * userinfo : {"imaccount":"1111","imuserid":"1111","oort_code":"010001","oort_depcode":"9900","oort_depname":"所有部门","oort_email":"user@email.com","oort_idcard":"1101141414","oort_jobname":"科长","oort_name":"张某某","oort_namefl":"zmm","oort_namepy":"zhangmoumou","oort_office":"1401","oort_phone":"13800138000","oort_photo":"http://com/photo.jpg","oort_postname":"科长","oort_pphone":"13800138000","oort_sex":1,"oort_tel":"110041","oort_uuid":"c64c64a9-ab34-43b4-94e2-5689aeeb51e8"}
     */

    private int event_count;

    private int task_count;
    private int task_undone_count;
    private UserinfoBean userinfo;


    public static class UserinfoBean implements Serializable {
        private String user_id;
        private String tenant_id;
        private String unique_id;
        private String user_name;
        private String user_name_py;

        public String getUser_name_fpy() {
            return user_name_fpy;
        }

        public void setUser_name_fpy(String user_name_fpy) {
            this.user_name_fpy = user_name_fpy;
        }

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

        public String getUser_name_py() {
            return user_name_py;
        }

        public void setUser_name_py(String user_name_py) {
            this.user_name_py = user_name_py;
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

        public List<DeptListBean> getDept_list() {
            return dept_list;
        }

        public void setDept_list(List<DeptListBean> dept_list) {
            this.dept_list = dept_list;
        }

        private String user_name_fpy;
        private String photo;
        private int status;
        private List<DeptListBean> dept_list;

        public static class DeptListBean implements Serializable {
            /**
             * user_id : 5689c1e7-75f3-48e3-b633-fc63a22c153a
             * tenant_id : 0e391fd7-1033-4f09-88c0-187582fee462
             * dept_code :
             * sort : 4
             * ex_data : {}
             * created_at : 2025-02-22 10:08:22
             * updated_at : 2025-02-22 10:08:22
             * deptinfo : {"dept_id":"c440cf07-e284-4092-85d0-6abc8ff981b8","tenant_id":"0e391fd7-1033-4f09-88c0-187582fee462","dept_code":"or001","parent_dept_code":"","dept_type":3,"dept_name":"技术部门","dept_code_path":"/or001/","dept_name_path":"/","dept_level":1,"sort":1,"dept_photo":"","ex_data":null,"created_at":"2025-02-17 15:37:54","updated_at":"2025-02-17 15:37:54"}
             */

            private String user_id;

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

            public String getDept_code() {
                return dept_code;
            }

            public void setDept_code(String dept_code) {
                this.dept_code = dept_code;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
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

            public DeptinfoBean getDeptinfo() {
                return deptinfo;
            }

            public void setDeptinfo(DeptinfoBean deptinfo) {
                this.deptinfo = deptinfo;
            }

            private String tenant_id;
            private String dept_code;
            private int sort;
            private ExDataBean ex_data;
            private String created_at;
            private String updated_at;
            private DeptinfoBean deptinfo;

            public static class ExDataBean implements Serializable {
            }

            public static class DeptinfoBean implements Serializable {
                /**
                 * dept_id : c440cf07-e284-4092-85d0-6abc8ff981b8
                 * tenant_id : 0e391fd7-1033-4f09-88c0-187582fee462
                 * dept_code : or001
                 * parent_dept_code :
                 * dept_type : 3
                 * dept_name : 技术部门
                 * dept_code_path : /or001/
                 * dept_name_path : /
                 * dept_level : 1
                 * sort : 1
                 * dept_photo :
                 * ex_data : null
                 * created_at : 2025-02-17 15:37:54
                 * updated_at : 2025-02-17 15:37:54
                 */

                private String dept_id;
                private String tenant_id;
                private String dept_code;
                private String parent_dept_code;
                private int dept_type;
                private String dept_name;
                private String dept_code_path;
                private String dept_name_path;
                private int dept_level;
                private int sort;

                public String getDept_photo() {
                    return dept_photo;
                }

                public void setDept_photo(String dept_photo) {
                    this.dept_photo = dept_photo;
                }

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

                public String getParent_dept_code() {
                    return parent_dept_code;
                }

                public void setParent_dept_code(String parent_dept_code) {
                    this.parent_dept_code = parent_dept_code;
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

                public Object getEx_data() {
                    return ex_data;
                }

                public void setEx_data(Object ex_data) {
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

                private String dept_photo;
                private Object ex_data;
                private String created_at;
                private String updated_at;
            }
        }
    }
}

