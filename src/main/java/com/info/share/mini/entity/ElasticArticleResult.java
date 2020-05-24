package com.info.share.mini.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ElasticArticleResult {

        private String author;

        private String id;

        private String title;

        private String tag;

        private String contentPreview;

        private String index; // 索引

//        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private String publishTime;

        private String theme;

        private String themeImage;

        public String getTheme() {
                return theme;
        }

        public void setTheme(String theme) {
                this.theme = theme;
        }

        public String getThemeImage() {
                return themeImage;
        }

        public void setThemeImage(String themeImage) {
                this.themeImage = themeImage;
        }

        public String getAuthor() {
                return author;
        }

        public void setAuthor(String author) {
                this.author = author;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getTag() {
                return tag;
        }

        public void setTag(String tag) {
                this.tag = tag;
        }

        public String getContentPreview() {
                return contentPreview;
        }

        public void setContentPreview(String contentPreview) {
                this.contentPreview = contentPreview;
        }

        public String getIndex() {
                return index;
        }

        public void setIndex(String index) {
                this.index = index;
        }

        public String getPublishTime() {
                return publishTime;
        }

        public void setPublishTime(String publishTime) {
                this.publishTime = publishTime;
        }

        @Override
        public String toString() {
                return JSONObject.toJSONString(this, SerializerFeature.WRITE_MAP_NULL_FEATURES);
//                return "{" +
//                        "author:'" + author + '\'' +
//                        ", id:'" + id + '\'' +
//                        ", title:'" + title + '\'' +
//                        ", tag:'" + tag + '\'' +
//                        ", contentPreview:'" + contentPreview + '\'' +
//                        ", index:'" + index + '\'' +
//                        ", publishTime:'" + publishTime + '\'' +
//                        '}';
        }
}
