package com.info.share.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.util.Date;

public interface ArticleService {

    //文章上传
    JSONObject uploadArticle(String title, String content, String tag, String author, String theme,
                             String preview, String publishTime, String themeImg) throws ParseException;

    // 按专题给出文章列表
    JSONObject listArticle(String theme, int pageNumber, int pageSize);

    // 按时间先后给出文章列表
    JSONObject listArticleByTime(int page, int pageSize);

    JSONObject detailArticle(String openId, String article);

    JSONObject addReadCountByOne(String articleId);

    JSONObject searchArticle(String keyword, int page, int pageSize);

}
