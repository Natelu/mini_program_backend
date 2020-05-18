package com.info.share.mini.controller;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.service.ArticleService;
import com.info.share.mini.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "1. 文章接口",tags = {"1. 文章接口列表"})
public class ArticleController {
    private static final Logger logger = LogManager.getLogger(ArticleController.class);

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "articleService")
    private ArticleService articleService;

    @ApiOperation(value = "最新-文章列表", notes= "文章列表， 按日期倒序。", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "查询当前页", paramType = "query", dataType = "Integer", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页文章数", paramType = "query", dataType = "Integer", defaultValue = "10")
    })
    @GetMapping(value = "/article/listByTime", produces = {"application/json;charset=UTF-8"})
    public JSONObject fetArticleByTime(@RequestParam(value = "pageNumber", defaultValue = "1") int page,
                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                   HttpServletResponse response){
        JSONObject res = articleService.listArticleByTime(page, pageSize);
        response.setStatus(res.getIntValue("code"));
//        return JSONObject.toJSONString(res);
        return res;
    }


    @ApiOperation(value = "主题-文章列表", notes= "文章列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "theme", value = "主题", paramType = "path", required = true, defaultValue = "营销增长"),
            @ApiImplicitParam(name = "pageNumber", value = "查询当前页", paramType = "query", dataType = "Integer", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页文章数", paramType = "query", dataType = "Integer", defaultValue = "10")
    })
    @GetMapping(value = "/article/list/{theme}", produces = {"application/json;charset=UTF-8"})
    public String fetchArticleList(@PathVariable("theme") String themeName,
                                   @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                   HttpServletRequest request, HttpServletResponse response){
//        int page = Integer.parseInt(pageNumber);
        JSONObject listRes = articleService.listArticle(themeName, pageNumber, pageSize);
        response.setStatus(listRes.getIntValue("code"));
        return JSONObject.toJSONString(listRes);
    }

    @PostMapping(value = "/article/upload", produces = {"application/json;charset=UTF-8"})
    public String uploadArticle(@RequestBody JSONObject body,HttpServletRequest request, HttpServletResponse response){
        String author = body.getString("author");
        logger.info(author);
        String title = body.getString("title");
        String tag = body.getString("tag");
        String content = body.getString("content");
        String theme = body.getString("theme");
        String preview = body.getString("preview");
        String publish_time = body.getString("publish_time");
        JSONObject res = new JSONObject();
        try{
            res = articleService.uploadArticle(title, content, tag, author, theme, preview, publish_time);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
        }
        response.setStatus(res.getIntValue("status"));
        return res.toJSONString();
    }

    @ApiOperation(value = "文章详情", notes= "文章详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "主题", paramType = "path", required = true),
            })
    @GetMapping(value = "/article/{article_id}/info", produces = {"application/json;charset=UTF-8"})
    public String getArticle(@PathVariable("article_id") String articleId,
                             @RequestParam(value = "openId") String openId,
                             HttpServletResponse response){
        JSONObject articleRes = articleService.detailArticle(openId, articleId);
        response.setStatus(articleRes.getIntValue("status"));
//        return articleRes.toJSONString();
        return JSONObject.toJSONString(articleRes);
    }

    @ApiOperation(value = "文章搜索", notes= "文章搜索", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", paramType = "path", required = true),
            @ApiImplicitParam(name = "page", value = "查询当前页", paramType = "query", dataType = "Integer", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页文章数", paramType = "query", dataType = "Integer", defaultValue = "10")
    })

    @GetMapping(value = "/article/search/{keyword}", produces = {"application/json;charset=UTF-8"})
    public String searchArticle(@PathVariable("keyword") String keyword,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                HttpServletResponse response){
        JSONObject res = articleService.searchArticle(keyword, page, pageSize);
        return JSONObject.toJSONString(res);
    }
}
