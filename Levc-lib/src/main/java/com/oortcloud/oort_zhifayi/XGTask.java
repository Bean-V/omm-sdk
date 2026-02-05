package com.oortcloud.oort_zhifayi;

import com.oortcloud.basemodule.utils.DeviceGPSUtils;

import java.util.ArrayList;
import java.util.List;

public class XGTask{
        private List<ListDTO> list;
        private Integer page;

    public List<ListDTO> getList() {
        return list;
    }


    public List<ListDTO.PointsDTO> getAllPoint(){
            List<ListDTO.PointsDTO> l = new ArrayList();
            for (ListDTO task : list) {
                l.addAll(task.getPoints());
            }
            return l;
    }


    List<ListDTO.PointsDTO> allUnCheckPoint = new ArrayList<>();

    public List<ListDTO.PointsDTO> getAllUnCheckPoint(){
        List<ListDTO.PointsDTO> l = new ArrayList();
        for (ListDTO task : list) {
            for (ListDTO.PointsDTO p  : task.getPoints()) {
                if(!p.isCheck()){
                     l.add(p);
                }
            }
        }
        return l;
    }

    public List<ListDTO.PointsDTO> getAllCheckPoint(){
        List<ListDTO.PointsDTO> l = new ArrayList();
        for (ListDTO task : list) {
            for (ListDTO.PointsDTO p  : task.getPoints()) {
                if(p.isCheck()){
                    l.add(p);
                }
            }
        }
        return l;
    }

    public int isEablePost(){


        List<ListDTO.PointsDTO> l = getAllUnCheckPoint();

        int post = 0;
        for (ListDTO.PointsDTO p  : l) {
            long d =  DeviceGPSUtils.calculateDistance(ReportInfo.latitude,ReportInfo.longitude,p.getLat(),p.getLng());
            if(d < 50){
                post = 1;
                return post;
            }

        }
        List<ListDTO.PointsDTO> l1 = getAllCheckPoint();
        for (ListDTO.PointsDTO p  : l1) {
            long d =  DeviceGPSUtils.calculateDistance(ReportInfo.latitude,ReportInfo.longitude,p.getLat(),p.getLng());
            if(d < 50){
                post = 2;
                return post;
            }

        }
        return post;
    }
    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    private Integer pages;
        private Integer pagesize;
        private Integer counts;
        private Integer count;

    public static class ListDTO {
            private String id;
            private String name;
            private String describe;
            private Integer start_at;
            private List<PointsDTO> points;
            private String uuid;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public Integer getStart_at() {
            return start_at;
        }

        public void setStart_at(Integer start_at) {
            this.start_at = start_at;
        }

        public List<PointsDTO> getPoints() {
            return points;
        }

        public void setPoints(List<PointsDTO> points) {
            this.points = points;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
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

        public List<String> getUuids() {
            return uuids;
        }

        public void setUuids(List<String> uuids) {
            this.uuids = uuids;
        }

        public List<UserinfoDTO> getUserinfo() {
            return userinfo;
        }

        public void setUserinfo(List<UserinfoDTO> userinfo) {
            this.userinfo = userinfo;
        }

        private Integer status;
            private String created_at;
            private String updated_at;
            private List<String> uuids;
            private List<UserinfoDTO> userinfo;

            public static class PointsDTO {
                public Double getLng() {
                    return lng;
                }

                public void setLng(Double lng) {
                    this.lng = lng;
                }

                public Double getLat() {
                    return lat;
                }

                public void setLat(Double lat) {
                    this.lat = lat;
                }

                public String getAddress() {
                    return address;
                }

                public void setAddress(String address) {
                    this.address = address;
                }

                private Double lng;
                private Double lat;
                private String address;

                public boolean isCheck() {
                    return isvaildCheck;
                }

                public void setIsvaildCheck(boolean isvaildCheck) {
                    this.isvaildCheck = isvaildCheck;
                }

                private boolean isvaildCheck = false;

                public long getDistance(){
                    return DeviceGPSUtils.calculateDistance(ReportInfo.latitude,ReportInfo.longitude,lat,lng);
                }
                public String getDistance_(){
                    return DeviceGPSUtils.calculateDistance_(ReportInfo.latitude,ReportInfo.longitude,lat,lng);
                }
            }

            public static class UserinfoDTO {
                private String oort_uuid;
                private String oort_name;
                private String oort_namepy;
                private String oort_namefl;
                private String oort_code;
                private String oort_depname;
                private String oort_depcode;
                private String oort_idcard;
                private String oort_photo;
                private Integer oort_sex;
                private String oort_phone;
                private String oort_pphone;
                private String oort_email;
                private String oort_postname;
                private String oort_jobname;
                private String oort_office;
                private String oort_tel;
                private String imaccount;
                private String imuserid;
                private Object renzheng;
                private Integer userlevelID;
                private String userlevel;
                private String userlevelurl;
                private Integer contribute;
                private Integer honor;
                private Integer fans;
                private String oort_loginid;
                private String oort_rdepname;
                private String oort_rdepcode;
                private Integer oort_usertype;
                private Integer oort_manager;

                public String getUser_uuid() {
                    return oort_uuid;
                }

                public void setOort_uuid(String oort_uuid) {
                    this.oort_uuid = oort_uuid;
                }

                public String getUser_name() {
                    return oort_name;
                }

                public void setOort_name(String oort_name) {
                    this.oort_name = oort_name;
                }

                public String getUser_namepy() {
                    return oort_namepy;
                }

                public void setOort_namepy(String oort_namepy) {
                    this.oort_namepy = oort_namepy;
                }

                public String getUser_namefl() {
                    return oort_namefl;
                }

                public void setOort_namefl(String oort_namefl) {
                    this.oort_namefl = oort_namefl;
                }

                public String getUser_code() {
                    return oort_code;
                }

                public void setOort_code(String oort_code) {
                    this.oort_code = oort_code;
                }

                public String getUser_depname() {
                    return oort_depname;
                }

                public void setOort_depname(String oort_depname) {
                    this.oort_depname = oort_depname;
                }

                public String getUser_depcode() {
                    return oort_depcode;
                }

                public void setOort_depcode(String oort_depcode) {
                    this.oort_depcode = oort_depcode;
                }

                public String getUser_idcard() {
                    return oort_idcard;
                }

                public void setOort_idcard(String oort_idcard) {
                    this.oort_idcard = oort_idcard;
                }

                public String getUser_photo() {
                    return oort_photo;
                }

                public void setOort_photo(String oort_photo) {
                    this.oort_photo = oort_photo;
                }

                public Integer getUser_sex() {
                    return oort_sex;
                }

                public void setOort_sex(Integer oort_sex) {
                    this.oort_sex = oort_sex;
                }

                public String getUser_phone() {
                    return oort_phone;
                }

                public void setOort_phone(String oort_phone) {
                    this.oort_phone = oort_phone;
                }

                public String getUser_pphone() {
                    return oort_pphone;
                }

                public void setOort_pphone(String oort_pphone) {
                    this.oort_pphone = oort_pphone;
                }

                public String getUser_email() {
                    return oort_email;
                }

                public void setOort_email(String oort_email) {
                    this.oort_email = oort_email;
                }

                public String getUser_postname() {
                    return oort_postname;
                }

                public void setOort_postname(String oort_postname) {
                    this.oort_postname = oort_postname;
                }

                public String getUser_jobname() {
                    return oort_jobname;
                }

                public void setOort_jobname(String oort_jobname) {
                    this.oort_jobname = oort_jobname;
                }

                public String getUser_office() {
                    return oort_office;
                }

                public void setOort_office(String oort_office) {
                    this.oort_office = oort_office;
                }

                public String getUser_tel() {
                    return oort_tel;
                }

                public void setOort_tel(String oort_tel) {
                    this.oort_tel = oort_tel;
                }

                public String getImaccount() {
                    return imaccount;
                }

                public void setImaccount(String imaccount) {
                    this.imaccount = imaccount;
                }

                public String getImuserid() {
                    return imuserid;
                }

                public void setImuserid(String imuserid) {
                    this.imuserid = imuserid;
                }

                public Object getRenzheng() {
                    return renzheng;
                }

                public void setRenzheng(Object renzheng) {
                    this.renzheng = renzheng;
                }

                public Integer getUserlevelID() {
                    return userlevelID;
                }

                public void setUserlevelID(Integer userlevelID) {
                    this.userlevelID = userlevelID;
                }

                public String getUserlevel() {
                    return userlevel;
                }

                public void setUserlevel(String userlevel) {
                    this.userlevel = userlevel;
                }

                public String getUserlevelurl() {
                    return userlevelurl;
                }

                public void setUserlevelurl(String userlevelurl) {
                    this.userlevelurl = userlevelurl;
                }

                public Integer getContribute() {
                    return contribute;
                }

                public void setContribute(Integer contribute) {
                    this.contribute = contribute;
                }

                public Integer getHonor() {
                    return honor;
                }

                public void setHonor(Integer honor) {
                    this.honor = honor;
                }

                public Integer getFans() {
                    return fans;
                }

                public void setFans(Integer fans) {
                    this.fans = fans;
                }

                public String getUser_loginid() {
                    return oort_loginid;
                }

                public void setOort_loginid(String oort_loginid) {
                    this.oort_loginid = oort_loginid;
                }

                public String getUser_rdepname() {
                    return oort_rdepname;
                }

                public void setOort_rdepname(String oort_rdepname) {
                    this.oort_rdepname = oort_rdepname;
                }

                public String getUser_rdepcode() {
                    return oort_rdepcode;
                }

                public void setOort_rdepcode(String oort_rdepcode) {
                    this.oort_rdepcode = oort_rdepcode;
                }

                public Integer getUser_usertype() {
                    return oort_usertype;
                }

                public void setOort_usertype(Integer oort_usertype) {
                    this.oort_usertype = oort_usertype;
                }

                public Integer getUser_manager() {
                    return oort_manager;
                }

                public void setOort_manager(Integer oort_manager) {
                    this.oort_manager = oort_manager;
                }

                public List<?> getUser_dept_list() {
                    return oort_dept_list;
                }

                public void setOort_dept_list(List<?> oort_dept_list) {
                    this.oort_dept_list = oort_dept_list;
                }

                private List<?> oort_dept_list;
            }
        }

}
