package com.info.share.mini.mapper;

import com.info.share.mini.entity.WeChatAuthInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

@Mapper
@Service(value = "weChatAuthMapper")
public interface WeChatMapper {

    //获取session_key等信息
    @Select("select * from wechat_auth where openid=#{openId}")
    WeChatAuthInfo getAuthInfo(@Param(value = "openId") String openId);

    // 插入新用户的认证信息
    @Insert("insert into wechat_auth (openid, session_key, update_time, union_id) values(#{openId}, #{sessionKey}, " +
            "#{unionId}, now())")
    void insertAuthInfo(String openId, String sessionKey, String unionId);

    //更新用户的认证信息
    @Update("update wechat_auth set session_key=#{sessionKey}, update_time=NOW() where openid=#{openId}")
    void updateAuthInfo(String openId, String sessionKey);
}
