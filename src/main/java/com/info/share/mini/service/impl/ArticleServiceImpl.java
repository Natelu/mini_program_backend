package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.share.mini.entity.Article;
import com.info.share.mini.entity.ElasticArticleResult;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.User;
import com.info.share.mini.mapper.ArticleMapper;
import com.info.share.mini.mapper.UserMapper;
import com.info.share.mini.service.ArticleService;
import com.info.share.mini.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service("articleService")
@Component
public class ArticleServiceImpl implements ArticleService {

    private static final Logger logger = LogManager.getLogger(ArticleServiceImpl.class);
    @Resource(name = "articleMapper")
    private ArticleMapper articleMapper;

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "highLevelClient")
    private RestHighLevelClient esClient;

    // 访问文章详情，在相应的阅读量加1
    @Override
    public JSONObject addReadCountByOne(String articleId){
        ResultJSON res ;
        int count = 0;
        try{
            count = articleMapper.getReadCount(articleId);
            count++;
            articleMapper.updateCount(articleId, count);
            res = ResultJSON.success(200, "successfully plus one.");
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
    }
    //theme 文章列表
    @Override
    public JSONObject listArticle(String theme, int page, int pageSize){
        ResultJSON res = null;
        List<Article> list = null;
        try{
            PageHelper.startPage(page, pageSize);
            list = articleMapper.listArticle(theme);
            PageInfo pageInfo = new PageInfo(list);
            int totalPage = pageInfo.getPages();
            JSONObject temp = new JSONObject();
            temp.put("articles", list);
            res = ResultJSON.success(page, pageSize, totalPage, temp);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toString());
    }

    @Override
    public JSONObject listArticleByTime(int page, int pageSize){
        ResultJSON res = null;
        List<Article> list = null;
        try{
            PageHelper.startPage(page, pageSize);
            list = articleMapper.listArticleByTime();
            PageInfo pageInfo = new PageInfo(list);
            int totalPage = pageInfo.getPages();
            JSONObject temp = new JSONObject();
            temp.put("articles", list);
            res = ResultJSON.success(page, pageSize, totalPage, temp);
        }catch (Exception e){
            res = ResultJSON.error(e.getLocalizedMessage());
            logger.info(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toString());
    }

    @Override
    public JSONObject uploadArticle(String title, String content, String tag, String author, String theme,
                                    String preview, String publishTime, String themeImg) throws ParseException {
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");
        String contentPreview = preview;
        JSONObject res = new JSONObject();
        if (author == null){
            author = "匿名";
        }
        logger.info("title: " + title);
        try{
            String isThere = articleMapper.fetchArticleByName(title);
            logger.info("是否存在：" + isThere);
            if (isThere == null){
                articleMapper.uploadArticle(id, title, author, content, contentPreview, theme, tag, publishTime, themeImg);
                res.put("status", 200);
                res.put("message", "article has been inserted successfully!");
            }else{
                res.put("status", 200);
                res.put("message", "article has already existed");
            }
        }catch (Exception e){
            logger.info(e.getClass());
            res.put("message", e.getLocalizedMessage());
            res.put("status", 500);
        }
        return res;
    }

    // 文章详情
    @Override
    public JSONObject detailArticle(String openId, String articleId){
        JSONObject res = new JSONObject();
        Article article = null;
        User user = null;
        try{
            article = articleMapper.fetchArticleDetail(articleId);
            logger.info(article.getPublishTime());
            user = userMapper.getUserInfo(openId);
            if(!userService.checkVip(openId)){
                res.put("isVip", false);
                article.setContent("");
            }else{
                res.put("isVip", true);
            }
            res.put("data", article);
            res.put("status", 200);
            res.put("message", "successfully get the article detail.");
            logger.info("User " + openId + " successfully get the article  " + article.getTitle() + ".");
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            res.put("status", 500);
            res.put("message", e.getLocalizedMessage());
        }
        return res;
    }

    //文章搜索
    @Override
    public JSONObject searchArticle(String keyword, int page, int pageSize){
        String field1 = "title", field2 = "tag";
        logger.info("search keyword : " + keyword);
        String preTag = "<font color=\"#dd4b39\">";// google的高亮色值
        String postTag = "</font>";
        int from = (page - 1)*pageSize;
        if (from < 0) {from = 0;}

        MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery(keyword, field1, field2);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").preTags(preTag).postTags(postTag);
        highlightBuilder.field("tag").preTags(preTag).postTags(postTag);

        //设置索引 article
        SearchRequest searchRequest = new SearchRequest("article");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(builder);
        sourceBuilder.from(from);
        sourceBuilder.size(pageSize);
        sourceBuilder.highlighter(highlightBuilder);
        sourceBuilder.timeout(new TimeValue(1000));
        searchRequest.source(sourceBuilder);
        searchRequest.types("article");

        //搜索
        SearchResponse response = null;
        try {
            response = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits();
        long totalPage = total % pageSize == 0 ? total / pageSize : (total / pageSize + 1 );

        SearchHit[] hits = searchHits.getHits();
        List<ElasticArticleResult> articles = new LinkedList<>();
        if (response.getHits().getHits().length <= 0) {
            return JSONObject.parseObject(ResultJSON.success(202, "none hit by this keyword.").toSimpleString());
        }
        for (SearchHit hit:hits){
            ElasticArticleResult article = new ElasticArticleResult();
            JSONObject temp = JSONObject.parseObject(hit.getSourceAsString());
            HighlightField esField1 = hit.getHighlightFields().get(field1);
            HighlightField esField2 = hit.getHighlightFields().get(field2);
            logger.info(temp.getString("title"));
            if (esField1 != null){
                logger.info(esField1.fragments()[0].toString());
                temp.put("title", esField1.fragments()[0].string());
            }
            if (esField2 != null){
                temp.put("tag", esField2.fragments()[0].string());
            }
            article.setTitle(temp.getString("title"));
            article.setTag(temp.getString("tag"));
            article.setId(temp.getString("id"));
            article.setAuthor(temp.getString("author"));
            article.setPublishTime(temp.getString("publish_time"));
            article.setTheme(temp.getString("theme"));
            article.setThemeImage(temp.getString("theme_image"));
            articles.add(article);
        }
        ResultJSON res = ResultJSON.success(page, pageSize, (int)totalPage, articles);
        logger.info(res.toString());
        return JSONObject.parseObject(res.toString());
    }
}
