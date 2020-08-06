package com.info.share.mini.controller;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.Task;
import com.info.share.mini.service.TaskService;
import com.info.share.mini.utils.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "5. 任务相关接口",tags = {"5. 任务相关接口"})
@RequestMapping("/task")
public class TaskController {

    private static final Logger logger = LogManager.getLogger(TaskController.class);

    @Resource(name = "taskService")
    private TaskService taskService;

    @ApiOperation(value = "获取任务列表", notes= "获取任务列表，默认按时间倒序排列", httpMethod = "GET")
    @GetMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public JSONObject getTaskList(@RequestParam(value = "pageNumber" , defaultValue = "1") int page,
                                @RequestParam(value = "pageSize" , defaultValue = "10") int pageSize,
                                HttpServletResponse response){
        JSONObject result = taskService.getTaskList(page, pageSize);
        response.setStatus(result.getIntValue("code"));
        return result;
    }

    @PostMapping(value = "/upload", produces = {"application/json;charset=UTF-8"})
    public JSONObject uploadTask(@RequestParam(value = "codeImage")MultipartFile file, @RequestParam(value = "name") String name,
                                 @RequestParam(value = "wechat") String wechat, @RequestParam(value = "introduce") String introduce,
                                 @RequestParam(value = "taskOwner") String taskOwner,@RequestParam(value = "money") float money,
                                 HttpServletResponse response){
        // 保存图片到指定路径
        String fileName = file.getOriginalFilename();
        logger.info(fileName);
        FileUtil fileUtil = new FileUtil();
        JSONObject uploadImageRes = fileUtil.uploadImage(fileName, file);
        if(!uploadImageRes.getBooleanValue("status")){
            response.setStatus(400);
            return JSONObject.parseObject(ResultJSON.error("图片上传失败！").toSimpleString());
        }
        // String name, String wechat, String wechatCode, String introduce, float money

        JSONObject res = taskService.uploadTask(name, wechat, uploadImageRes.getString("fileUrl"), introduce, money, taskOwner);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    // 搜索任务
    @GetMapping(value = "/search/{keyword}", produces = {"application/json;charset=UTF-8"})
    public JSONObject searchTask(@PathVariable("keyword") String keyword,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                    HttpServletResponse response){
        JSONObject res = taskService.searchTask(keyword, page, pageSize);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    // 任务详情
    @GetMapping(value = "/detail/byId/{id}", produces = {"application/json;charset=UTF-8"})
    public JSONObject getTaskDetail(@PathVariable("id") String id,
                                 HttpServletResponse response){
        JSONObject res = taskService.getTaskDetail(id);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    // 任务详情
    @GetMapping(value = "/detail/byName/{name}", produces = {"application/json;charset=UTF-8"})
    public JSONObject getTaskDetailByName(@PathVariable("name") String name,
                                    HttpServletResponse response){
        JSONObject res = taskService.getTaskDetailByName(name);
        response.setStatus(res.getIntValue("code"));
        return res;
    }
}
