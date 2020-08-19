package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.share.mini.entity.*;
import com.info.share.mini.mapper.TaskMapper;
import com.info.share.mini.mapper.UserMapper;
import com.info.share.mini.service.TaskService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
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

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "userService")
    private UserService userService;

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

        //设置索引 task
        SearchRequest searchRequest = new SearchRequest("task");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(builder);
        sourceBuilder.from(from);
        sourceBuilder.size(pageSize);
        sourceBuilder.highlighter(highlightBuilder);
        sourceBuilder.timeout(new TimeValue(1000));
        searchRequest.source(sourceBuilder);
        searchRequest.types("task");

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
    public JSONObject getTaskDetail(String userId, String id){
        ResultJSON res ;
        try{
            float percentage = userService.getBonusPercentage(userId);
            Task task = taskMapper.getTaskDetail(id);
            task.setExtraMoney(task.getMoney()*percentage);
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

    // 领取任务
    @Override public JSONObject bindTaskWithUser(String userId, String taskId) {
        ResultJSON res;
        try {
            TaskDo taskDo = taskMapper.getDoTaskByUser(userId, taskId);
            if (taskDo!=null){
                res = ResultJSON.success("用户已领取该任务");
            }else{
                String id = UUID.randomUUID().toString();
                id = id.replace("-", "");
                Task task = taskMapper.getTaskDetail(taskId);
                User user = userMapper.getUserInfo(userId);
                float extraMoney = task.getMoney()*getBonusPercentage(userId);
                taskMapper.createDoTask(id, userId, user.getNick_name(), taskId, task.getTaskOwner(),
                        TaskStatus.doing.name(), task.getName(), task.getMoney(), extraMoney);
                res = ResultJSON.success("任务领取成功");
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
    }

    // 完成任务
    @Override public JSONObject doneTaskByUser(String userId, String taskId) {
        ResultJSON res;
        try {
            TaskDo taskDo = taskMapper.getDoTaskByUser(userId, taskId);
            if (taskDo == null){
                res = ResultJSON.error("用户未领取该任务");
            }else{
                taskMapper.updateUserTask(userId, taskId, TaskStatus.done.name());
                res = ResultJSON.success("任务已完成");
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
    }

    @Override public JSONObject getUserDoTasks(String userId, int page, int pageSize) {
        ResultJSON res;
        List<TaskDo> taskDos = new ArrayList<>();
        try{
            PageHelper.startPage(page, pageSize);
            taskDos = taskMapper.getDoTasksByUser(userId);
            PageInfo<TaskDo> pageInfo = new PageInfo<>(taskDos);
            int totalPage = pageInfo.getPages();
            res = ResultJSON.success(page, pageSize, totalPage, taskDos);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toString());
    }
    // 发展下线，提升的佣金比例
    public float getBonusPercentage(String userId){
        // 获取用户徒弟数
        List<User> users = userService.getReferralList(userId);
        float percentage = 0.0f;
        percentage += users.size()*0.02;
        return Math.min(0.1f, percentage);
    }
}
enum TaskStatus {
    doing, done
}