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
    JSONObject getTaskDetail(String id);

    // 任务详情 (by name)
    JSONObject getTaskDetailByName(String name);
}
