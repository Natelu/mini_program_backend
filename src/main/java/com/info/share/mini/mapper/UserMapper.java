package com.info.share.mini.mapper;

import com.info.share.mini.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
@Service(value = "userMapper")
public interface UserMapper {
    @Insert("insert into user(id, openId, name, create_time, tel) values(" +
            "#{id}, #{name}, now(), #{tel})")
    void createUser(@Param("id") String id,@Param("openId") String openId, @Param("name") String name, @Param("tel") String tel);

    @Select("select * from user where id=#{id}")
    User selectUserById(@Param("id") String id);

//    @Result()
    @Select("select * from user where openid=#{openId}")
    List<User> getUserInfo(@Param("openId") String openId);
}
