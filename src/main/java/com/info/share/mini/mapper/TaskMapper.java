package com.info.share.mini.mapper;

import com.info.share.mini.entity.Task;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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

    @Select("select id, name, service_wechat, service_wechat_code, introduce, money, publish_time, task_owner from task where id=#{id}")
    Task getTaskDetail(String id);

    @Select("select id, name, service_wechat, service_wechat_code, introduce, money, publish_time, task_owner from task where name=#{name}")
    Task getTaskDetailByName(String name);

    @Insert("insert into do_task (id, user_id, task_id, status, create_time) values(#{id}, #{userId}, #{taskId}," +
            " #{status}, now())")
    void createDoTask(String id, String userId, String taskId, String status);

    @Update("update do_task set status = #{status} where user_id=#{userId} and task_id=#{taskId}")
    void updateUserTask(String userId, String taskId, String status);
}
