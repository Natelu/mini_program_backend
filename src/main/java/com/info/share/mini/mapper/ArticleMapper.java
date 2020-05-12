package com.info.share.mini.mapper;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.Article;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Mapper
@Service(value = "articleMapper")
public interface ArticleMapper {

    //文章分页
    @Select("select id, tag, title, author, publish_time from article where " +
            "theme=#{themeName}")
    List<Article> listArticle(String themeName);

    //最新文章列表
    @Select("select id, tag, title, author, publish_time from article order by publish_time desc")
    List<Article> listArticleByTime();

    //文章上传
    @Insert("insert into article (id, title, author, content, content_preview, theme, tag, publish_time, update_time)" +
            "values(#{id}, #{title}, #{author}, #{content}, #{preview}, #{theme}, #{tag}, #{publishTime}, now())")
    void uploadArticle(String id, String title, String author, String content, String preview,
                       String theme, String tag, String publishTime);

    // 文章详情 (by id)
    @Select("select id, author, title, content, content_preview, theme, tag, read_count, publish_time from article where id= #{id}")
    Article fetchArticleDetail(@Param("id") String id);

    // 文章详情（by name)
    @Select("select id from article where title=#{titleName}")
    String fetchArticleByName(String titleName);
}
