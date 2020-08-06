package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.share.mini.entity.ElasticArticleResult;
import com.info.share.mini.entity.ElasticTaskResult;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.Task;
import com.info.share.mini.mapper.TaskMapper;
import com.info.share.mini.service.TaskService;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service(value = "taskService")
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LogManager.getLogger(TaskServiceImpl.class);

    @Resource(name = "taskMapper")
    private TaskMapper taskMapper;

    @Resource(name = "highLevelClient")
    private RestHighLevelClient esClient;

    @Override
    public JSONObject getTaskList(int page, int pageSize){
        ResultJSON res ;
        try{
            PageHelper.startPage(page, pageSize);
            List<Task> tasks = taskMapper.getTaskList();
            PageInfo pageInfo = new PageInfo(tasks);
            int totalPage = pageInfo.getPages();
            res = ResultJSON.success(page, pageSize, totalPage, tasks);
        }catch (Exception e){
            logger.error(e.getMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toString());
    }

    @Override
    public JSONObject uploadTask(String name, String wechat, String wechatCode, String introduce, float money, String taskOwner) {
        ResultJSON res;
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");
        try {
            taskMapper.createTask(id, name, wechat, wechatCode, introduce, money, taskOwner);
            res = ResultJSON.success("上传成功");
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
    }

    @Override
    public JSONObject searchTask(String keyword, int page, int pageSize) {
        String field1 = "name", field2 = "introduce";
        logger.info("search keyword : " + keyword);
        String preTag = "<font color=\"#dd4b39\">";// google的高亮色值
        String postTag = "</font>";
        int from = (page - 1)*pageSize;
        if (from < 0) {from = 0;}

        MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery(keyword, field1, field2);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(field1).preTags(preTag).postTags(postTag);
        highlightBuilder.field(field2).preTags(preTag).postTags(postTag);

        //设置索引 article
        SearchRequest searchRequest = new SearchRequest("task");
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
        List<ElasticTaskResult> tasks = new LinkedList<>();
        if (response.getHits().getHits().length <= 0) {
            return JSONObject.parseObject(ResultJSON.success(202, "none hit by this keyword.").toSimpleString());
        }
        for (SearchHit hit:hits){
            ElasticTaskResult task = new ElasticTaskResult();
            JSONObject temp = JSONObject.parseObject(hit.getSourceAsString());
            HighlightField esField1 = hit.getHighlightFields().get(field1);
            HighlightField esField2 = hit.getHighlightFields().get(field2);
            logger.info(temp.getString("name"));
            if (esField1 != null){
                logger.info(esField1.fragments()[0].toString());
                temp.put(field1, esField1.fragments()[0].string());
            }
            if (esField2 != null){
                temp.put(field2, esField2.fragments()[0].string());
            }
            task.setName(temp.getString("name"));
            task.setId(temp.getString("id"));
            task.setIntroduce(temp.getString("introduce"));
            task.setPublishTime(temp.getString("publish_time"));
            task.setMoney(temp.getFloat("money"));
            tasks.add(task);
        }
        ResultJSON res = ResultJSON.success(page, pageSize, (int)totalPage, tasks);
        logger.info(res.toString());
        return JSONObject.parseObject(res.toString());
    }

    @Override
    public JSONObject getTaskDetail(String id){
        ResultJSON res ;
        try{
            Task task = taskMapper.getTaskDetail(id);
            res = ResultJSON.success(task);
        }catch (Exception e){
            logger.error(e.getMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleDataString());
    }

    @Override
    public JSONObject getTaskDetailByName(String name){
        ResultJSON res ;
        try{
            Task task = taskMapper.getTaskDetailByName(name);
            res = ResultJSON.success(task);
        }catch (Exception e){
            logger.error(e.getMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleDataString());
    }
}
