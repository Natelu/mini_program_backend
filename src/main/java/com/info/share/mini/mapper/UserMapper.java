package com.info.share.mini.mapper;

import com.info.share.mini.entity.User;
import com.info.share.mini.entity.UserInfoBasic;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
@Service(value = "userMapper")
public interface UserMapper {

    @Insert("insert into user(id, openid, name, create_time, tel) values(" +
            "#{id}, #{openId}, #{name}, now(), #{tel})")
    void createUser(@Param("id") String id, @Param("openId") String openId, @Param("name") String name, @Param("tel") String tel);

    @Delete("delete from user where openid=#{openId}")
    void deleteUser(String openId);

    @Select("select * from user where openid is not null")
    List<User> fetchAllUser();

    @Select("select * from user where id=#{id}")
    User selectUserById(@Param("id") String id);

//    @Result()
    @Select("select * from user where openid=#{openId} limit 1")
    User getUserInfo(@Param("openId") String openId);
    
    // 获取用户简要信息
    @Select("select id, openid, name, tel, we_chat, country, province, company, position, avatar_url from user where openid=#{openId} limit 1")
    UserInfoBasic getUserInfoBasic(@Param("openId") String openId);

    @Select("select (id, openId, name, country, province, city, company, position, avatar_url) where openid = #{openId}")
    UserInfoBasic fetchUserInfoBasic(@Param("openId") String openId);

    @Select("select user.rank from user where openid = #{openId}")
    int getUserRank(String openId);

    @Update("update user set name=#{name}, company=#{company}, country=#{country},province=#{province}, city=#{city}, " +
            "tel=#{tel}, we_chat=#{weChat}, show_number=#{showNumber}, position=#{position} where openid=#{openId}")
    void updateUserInfo(String openId, String name, String company, String country, String province, String city, String tel, String weChat,
                        boolean showNumber, String position);

    // 将用户升级为vip
    @Update("update user set user.rank=1 where openid=#{openId}")
    void update2Vip(String openId);

    // 查询经用户邀请并成为vip的用户
    @Select("select * from user where invited_by=#{openId} and user.`rank`=1")
    List<User> getInviteUsers(String openId);

    // 查询用户邀请下线列表
    @Select("select * from user where invited_by=#{openId}")
    List<User> getAllReferrals(String openId);
    // 更新用户信息， by 微信获取的信息
    @Insert("insert into user (id, openid, nick_name, gender, city, province, country, avatar_url, unionid, create_time, invited_by) values(#{id}," +
            "#{openId}," +
            "#{nickName}, #{gender}, #{city}, #{province}, #{country}, #{avatarUrl}, #{unionId}, now(), #{invitedBy})")
    void createUserByWeChatInfo(String id, String openId, String nickName, int gender, String city, String province, String country,
                                String avatarUrl, String unionId, String invitedBy);
    // 更新用户手机号
    @Update("update user set tel=#{phone} where openid=#{openId}")
    void updatePhone(String openId, String phone);
}
