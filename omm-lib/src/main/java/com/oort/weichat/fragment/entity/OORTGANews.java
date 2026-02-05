package com.oort.weichat.fragment.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OORTGANews implements Serializable {




            /**
             * ID : 79
             * CreatedAt : 0001-01-01T00:00:00Z
             * UpdatedAt : 0001-01-01T00:00:00Z
             * NewsType : 1
             * Title : test
             * Intro : test
             * Content :
             * CoverImg : http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20221110/10/08/4/1c4126066214019e1054bc3ddb9077d4.jpg
             * Href :
             * Time : 2023-04-27 15:23:09
             * FromWhere : tes
             * Author : test
             * AduitName :
             * AduitUUID :
             * AduitAdvice :
             * Status : 1
             * IsSpider : 0
             * ViewCount : 0
             * CommentCount : 0
             * Extends :
             * Extends1 :
             * Extends2 :
             */

            @SerializedName("ID")
            private int id;
            private String CreatedAt;
            private String UpdatedAt;
            private int NewsType;
            private String Title;
            private String Intro;
            private String Content;
            private String CoverImg;
            private String Href;
            private String Time;
            private String FromWhere;
            private String Author;
            private String AduitName;
            private String AduitUUID;
            private String AduitAdvice;
            private int Status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public int getNewsType() {
        return NewsType;
    }

    public void setNewsType(int newsType) {
        NewsType = newsType;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getIntro() {
        return Intro;
    }

    public void setIntro(String intro) {
        Intro = intro;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getCoverImg() {
        return CoverImg;
    }

    public void setCoverImg(String coverImg) {
        CoverImg = coverImg;
    }

    public String getHref() {
        return Href;
    }

    public void setHref(String href) {
        Href = href;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFromWhere() {
        return FromWhere;
    }

    public void setFromWhere(String fromWhere) {
        FromWhere = fromWhere;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getAduitName() {
        return AduitName;
    }

    public void setAduitName(String aduitName) {
        AduitName = aduitName;
    }

    public String getAduitUUID() {
        return AduitUUID;
    }

    public void setAduitUUID(String aduitUUID) {
        AduitUUID = aduitUUID;
    }

    public String getAduitAdvice() {
        return AduitAdvice;
    }

    public void setAduitAdvice(String aduitAdvice) {
        AduitAdvice = aduitAdvice;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getIsSpider() {
        return IsSpider;
    }

    public void setIsSpider(int isSpider) {
        IsSpider = isSpider;
    }

    public int getViewCount() {
        return ViewCount;
    }

    public void setViewCount(int viewCount) {
        ViewCount = viewCount;
    }

    public int getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(int commentCount) {
        CommentCount = commentCount;
    }

    public String getExtends() {
        return Extends;
    }

    public void setExtends(String anExtends) {
        Extends = anExtends;
    }

    public String getExtends1() {
        return Extends1;
    }

    public void setExtends1(String extends1) {
        Extends1 = extends1;
    }

    public String getExtends2() {
        return Extends2;
    }

    public void setExtends2(String extends2) {
        Extends2 = extends2;
    }

    private int IsSpider;
            private int ViewCount;
            private int CommentCount;
            private String Extends;
            private String Extends1;
            private String Extends2;


}
