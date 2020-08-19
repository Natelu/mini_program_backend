package com.info.share.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public interface TaskService {

    // 任务列表
    JSONObject getTaskList(int page, int pageSize);

    // 上传任务
    JSONObject uploadTask(String name, String wechat, String wechatCode, String introduce, float money, String taskOwner);

    // 任务搜索
    JSONObject searchTask(String keyword, int page, int pageSize);

    // 任务详情（by id)
    JSONObject getTaskDetail(String userId, String id);

    // 任务详情 (by name)
    JSONObject getTaskDetailByName(String name);

    // 任务领取
    JSONObject bindTaskWithUser(String userId, String taskId);

    // 任务完成
    JSONObject doneTaskByUser(String userId, String taskId);

    // 用户领取任务列表
    JSONObject getUserDoTasks(String userId, int page, int pageSize);
}
