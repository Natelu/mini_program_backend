package com.info.share.mini.mapper;

import com.info.share.mini.entity.Task;
import com.info.share.mini.entity.TaskDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
@Service(value = "taskMapper")
public interface TaskMapper {

    // 获取任务列表
    @Select("select * from task order by publish_time desc")
    List<Task> getTaskList();

    // 上传任务
    @Insert("insert into task (id, name, service_wechat, service_wechat_code, introduce, money, publish_time, task_owner) " +
            "values(#{id}, #{name}, #{service_wechat}, #{service_wechat_code}, #{introduce}, #{money}, now(), #{taskOwner})")
    void createTask(String id, String name, String service_wechat, String service_wechat_code, String introduce,
                    float money, String taskOwner);

    // 任务详情 by taskID
    @Select("select id, name, service_wechat, service_wechat_code, introduce, money, publish_time, task_owner from task where id=#{id}")
    Task getTaskDetail(String id);

    // 任务详情 by taskName
   @Select("select id, name, service_wechat, service_wechat_code, introduce, money, publish_time, task_owner from task where name=#{name}")
    Task getTaskDetailByName(String name);

    // 任务领取
    @Insert("insert into do_task (id, user_id, user_name, task_id, task_name, task_wechat, status, create_time, task_name, base_money, extra_money) " +
            "values(#{id}, #{userId}, #{userName}, #{taskId}, #{taskWechat}, #{status}, now(), #{taskName}, #{baseMoney}, #{extraMoney})")
    void createDoTask(String id, String userId, String userName, String taskId, String taskWechat,
                      String status, String taskName, float baseMoney, float extraMoney);

    // 更改用户任务状态
    @Update("update do_task set status = #{status} where user_id=#{userId} and task_id=#{taskId}")
    void updateUserTask(String userId, String taskId, String status);

    // 查询任务领取情况
    @Select("select * from do_task where user_id = #{userId} and task_id = #{taskId}")
    TaskDo getDoTaskByUser(String userId, String taskId);

    // 查询用户领取任务列表
    @Select("select * from do_task where user_id = #{userId}")
    List<TaskDo> getDoTasksByUser(String userId);
}
