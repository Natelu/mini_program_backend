package com.info.share.mini.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Article {

    private String id;

    private String author;

    private String title;

    private String content;

    private String contentPreview;

    private String theme;

    private String tag;

    private int readCont;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentPreview() {
        return contentPreview;
    }

    public void setContentPreview(String contentPreview) {
        this.contentPreview = contentPreview;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getReadCont() {
        return readCont;
    }

    public void setReadCont(int readCont) {
        this.readCont = readCont;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "{" +
                "id:'" + id + '\'' +
                ", author:'" + author + '\'' +
                ", title:'" + title + '\'' +
                ", content:'" + content + '\'' +
                ", contentPreview:'" + contentPreview + '\'' +
                ", theme:'" + theme + '\'' +
                ", tag:'" + tag + '\'' +
                ", readCont:" + readCont +
                ", publishTime:" + publishTime +
                '}';
    }
}
