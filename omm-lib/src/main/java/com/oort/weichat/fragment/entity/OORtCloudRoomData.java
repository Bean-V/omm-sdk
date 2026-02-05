package com.oort.weichat.fragment.entity;

import com.oortcloud.basemodule.constant.Constant;

import java.io.Serializable;
import java.util.List;
public class OORtCloudRoomData implements Serializable {

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public List<CourseListBean> getCourse_list() {
        return course_list;
    }

    public void setCourse_list(List<CourseListBean> course_list) {
        this.course_list = course_list;
    }

    private int counts;
        private List<CourseListBean> course_list;
        public static class CourseListBean implements Serializable {
            /**
             * id : 4
             * created_on : 1605362796
             * modified_on : 1697855130
             * uid : 2809f77b-8e9b-4eb4-a2f4-e33edb96871b
             * uuid : 6799ea6d-dec6-4b34-961c-a7b5f8c6c900
             * course_title : Police, I respect you with my youth
             * dep_name : Publicity department
             * classify : 7055975
             * classify_name :
             * son_classify : 0
             * course_intro :
             * cover_url : http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20201114/22/06/4/001.png
             * course_type : 2
             * article :
<<<<<<< HEAD
             * video_url : http://workup.szkingdom.global:31110/oort/oortwj1/group1/default/20201203/16/57/4/警察，我以芳华敬你.mp4?download=0
=======
             * video_url : http://OA.szkingdom.global:31110/oort/oortwj1/group1/default/20201203/16/57/4/警察，我以芳华敬你.mp4?download=0
>>>>>>> origin/OA警务宝
             * pdf_url :
             * ppt_url : []
             * status : 1
             * audit_uuid :
             * audit_idea :
             * submit_time :
             * audit_time : 0
             * video_time :
             * push_time : 1694937957
             * logout_time : 0
             * view_counts : 15
             * collect_counts : 2
             * comment_counts : 9
             * like_counts : 5
             * history_counts : 39
             * top_time : 0
             * recommend : 0
             * start_time : 0
             * end_time : 0
             * plate_uid :
             * plate_name :
             * column_uid :
             * column_name :
             * catalog :
             * feel_url :
             * speaker_uuid :
             * speaker_name :
             * score_content : 0
             * score_form : 0
             * score_language : 0
             * score_spirit : 0
             * score_effect : 0
             * score_all : 0
             * uuid_name :
             * no_judge : 0
             * history_type : 0
             * study_type : 0
             * collect_type : 0
             * like_type : 0
             * comment_type : 0
             * judge_counts : 0
             * percent : 0
             */

            private int id;
            private int created_on;
            private int modified_on;
            private String uid;
            private String uuid;
            private String course_title;
            private String dep_name;
            private int classify;
            private String classify_name;
            private int son_classify;
            private String course_intro;
            private String cover_url;
            private int course_type;
            private String article;
            private String video_url;
            private String pdf_url;
            private String ppt_url;
            private int status;
            private String audit_uuid;
            private String audit_idea;
            private String submit_time;
            private int audit_time;
            private String video_time;
            private int push_time;
            private int logout_time;
            private int view_counts;
            private int collect_counts;
            private int comment_counts;
            private int like_counts;
            private int history_counts;
            private int top_time;
            private int recommend;
            private int start_time;
            private int end_time;
            private String plate_uid;
            private String plate_name;
            private String column_uid;
            private String column_name;
            private String catalog;
            private String feel_url;
            private String speaker_uuid;
            private String speaker_name;
            private int score_content;
            private int score_form;
            private int score_language;
            private int score_spirit;
            private int score_effect;
            private int score_all;
            private String uuid_name;
            private int no_judge;
            private int history_type;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getCreated_on() {
                return created_on;
            }

            public void setCreated_on(int created_on) {
                this.created_on = created_on;
            }

            public int getModified_on() {
                return modified_on;
            }

            public void setModified_on(int modified_on) {
                this.modified_on = modified_on;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getCourse_title() {
                return course_title;
            }

            public void setCourse_title(String course_title) {
                this.course_title = course_title;
            }

            public String getDep_name() {
                return dep_name;
            }

            public void setDep_name(String dep_name) {
                this.dep_name = dep_name;
            }

            public int getClassify() {
                return classify;
            }

            public void setClassify(int classify) {
                this.classify = classify;
            }

            public String getClassify_name() {
                return classify_name;
            }

            public void setClassify_name(String classify_name) {
                this.classify_name = classify_name;
            }

            public int getSon_classify() {
                return son_classify;
            }

            public void setSon_classify(int son_classify) {
                this.son_classify = son_classify;
            }

            public String getCourse_intro() {
                return course_intro;
            }

            public void setCourse_intro(String course_intro) {
                this.course_intro = course_intro;
            }

            public String getCover_url() {


                String url = Constant.BASE_URL + "oort/oortwj1/";
                if(cover_url.contains(url)){

                }else {
                    if(cover_url.startsWith("http") && cover_url.contains("oort/oortwj1/")){

                       String [] strs = cover_url.split("oort/oortwj1/");

                       if(strs.length > 1) {
                           cover_url = url + strs[1];
                       }
                    }else{
                        cover_url = url + cover_url;
                    }
                }
                return cover_url;
            }

            public void setCover_url(String cover_url) {
                this.cover_url = cover_url;
            }

            public int getCourse_type() {
                return course_type;
            }

            public void setCourse_type(int course_type) {
                this.course_type = course_type;
            }

            public String getArticle() {
                return article;
            }

            public void setArticle(String article) {
                this.article = article;
            }

            public String getVideo_url() {
                String url = Constant.BASE_URL + "oort/oortwj1/";
                if(video_url.contains(url)){

                }else {
                    if(video_url.startsWith("http") && video_url.contains("oort/oortwj1/")){

                        String [] strs = video_url.split("oort/oortwj1/");

                        if(strs.length > 1) {
                            video_url = url + strs[1];
                        }
                    }else{

                        video_url = url + video_url;
                    }
                }
                return video_url;
            }

            public void setVideo_url(String video_url) {
                this.video_url = video_url;
            }

            public String getPdf_url() {
                return pdf_url;
            }

            public void setPdf_url(String pdf_url) {
                this.pdf_url = pdf_url;
            }

            public String getPpt_url() {
                return ppt_url;
            }

            public void setPpt_url(String ppt_url) {
                this.ppt_url = ppt_url;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getAudit_uuid() {
                return audit_uuid;
            }

            public void setAudit_uuid(String audit_uuid) {
                this.audit_uuid = audit_uuid;
            }

            public String getAudit_idea() {
                return audit_idea;
            }

            public void setAudit_idea(String audit_idea) {
                this.audit_idea = audit_idea;
            }

            public String getSubmit_time() {
                return submit_time;
            }

            public void setSubmit_time(String submit_time) {
                this.submit_time = submit_time;
            }

            public int getAudit_time() {
                return audit_time;
            }

            public void setAudit_time(int audit_time) {
                this.audit_time = audit_time;
            }

            public String getVideo_time() {
                return video_time;
            }

            public void setVideo_time(String video_time) {
                this.video_time = video_time;
            }

            public int getPush_time() {
                return push_time;
            }

            public void setPush_time(int push_time) {
                this.push_time = push_time;
            }

            public int getLogout_time() {
                return logout_time;
            }

            public void setLogout_time(int logout_time) {
                this.logout_time = logout_time;
            }

            public int getView_counts() {
                return view_counts;
            }

            public void setView_counts(int view_counts) {
                this.view_counts = view_counts;
            }

            public int getCollect_counts() {
                return collect_counts;
            }

            public void setCollect_counts(int collect_counts) {
                this.collect_counts = collect_counts;
            }

            public int getComment_counts() {
                return comment_counts;
            }

            public void setComment_counts(int comment_counts) {
                this.comment_counts = comment_counts;
            }

            public int getLike_counts() {
                return like_counts;
            }

            public void setLike_counts(int like_counts) {
                this.like_counts = like_counts;
            }

            public int getHistory_counts() {
                return history_counts;
            }

            public void setHistory_counts(int history_counts) {
                this.history_counts = history_counts;
            }

            public int getTop_time() {
                return top_time;
            }

            public void setTop_time(int top_time) {
                this.top_time = top_time;
            }

            public int getRecommend() {
                return recommend;
            }

            public void setRecommend(int recommend) {
                this.recommend = recommend;
            }

            public int getStart_time() {
                return start_time;
            }

            public void setStart_time(int start_time) {
                this.start_time = start_time;
            }

            public int getEnd_time() {
                return end_time;
            }

            public void setEnd_time(int end_time) {
                this.end_time = end_time;
            }

            public String getPlate_uid() {
                return plate_uid;
            }

            public void setPlate_uid(String plate_uid) {
                this.plate_uid = plate_uid;
            }

            public String getPlate_name() {
                return plate_name;
            }

            public void setPlate_name(String plate_name) {
                this.plate_name = plate_name;
            }

            public String getColumn_uid() {
                return column_uid;
            }

            public void setColumn_uid(String column_uid) {
                this.column_uid = column_uid;
            }

            public String getColumn_name() {
                return column_name;
            }

            public void setColumn_name(String column_name) {
                this.column_name = column_name;
            }

            public String getCatalog() {
                return catalog;
            }

            public void setCatalog(String catalog) {
                this.catalog = catalog;
            }

            public String getFeel_url() {
                return feel_url;
            }

            public void setFeel_url(String feel_url) {
                this.feel_url = feel_url;
            }

            public String getSpeaker_uuid() {
                return speaker_uuid;
            }

            public void setSpeaker_uuid(String speaker_uuid) {
                this.speaker_uuid = speaker_uuid;
            }

            public String getSpeaker_name() {
                return speaker_name;
            }

            public void setSpeaker_name(String speaker_name) {
                this.speaker_name = speaker_name;
            }

            public int getScore_content() {
                return score_content;
            }

            public void setScore_content(int score_content) {
                this.score_content = score_content;
            }

            public int getScore_form() {
                return score_form;
            }

            public void setScore_form(int score_form) {
                this.score_form = score_form;
            }

            public int getScore_language() {
                return score_language;
            }

            public void setScore_language(int score_language) {
                this.score_language = score_language;
            }

            public int getScore_spirit() {
                return score_spirit;
            }

            public void setScore_spirit(int score_spirit) {
                this.score_spirit = score_spirit;
            }

            public int getScore_effect() {
                return score_effect;
            }

            public void setScore_effect(int score_effect) {
                this.score_effect = score_effect;
            }

            public int getScore_all() {
                return score_all;
            }

            public void setScore_all(int score_all) {
                this.score_all = score_all;
            }

            public String getUuid_name() {
                return uuid_name;
            }

            public void setUuid_name(String uuid_name) {
                this.uuid_name = uuid_name;
            }

            public int getNo_judge() {
                return no_judge;
            }

            public void setNo_judge(int no_judge) {
                this.no_judge = no_judge;
            }

            public int getHistory_type() {
                return history_type;
            }

            public void setHistory_type(int history_type) {
                this.history_type = history_type;
            }

            public int getStudy_type() {
                return study_type;
            }

            public void setStudy_type(int study_type) {
                this.study_type = study_type;
            }

            public int getCollect_type() {
                return collect_type;
            }

            public void setCollect_type(int collect_type) {
                this.collect_type = collect_type;
            }

            public int getLike_type() {
                return like_type;
            }

            public void setLike_type(int like_type) {
                this.like_type = like_type;
            }

            public int getComment_type() {
                return comment_type;
            }

            public void setComment_type(int comment_type) {
                this.comment_type = comment_type;
            }

            public int getJudge_counts() {
                return judge_counts;
            }

            public void setJudge_counts(int judge_counts) {
                this.judge_counts = judge_counts;
            }

            public int getPercent() {
                return percent;
            }

            public void setPercent(int percent) {
                this.percent = percent;
            }

            private int study_type;
            private int collect_type;
            private int like_type;
            private int comment_type;
            private int judge_counts;
            private int percent;
        }

}
