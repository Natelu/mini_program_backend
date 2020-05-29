package com.info.share.mini.mapper;

import com.info.share.mini.entity.Network;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

//人脉详情接口
@Mapper
@Service(value = "networkMapper")
public interface NetworkMapper {

    // 人脉列表（阅读量降序, 仅显示会员用户）
    @Select("select * from networks where openid in (select openid from user where user.`rank`=1) order by read_count desc")
    List<Network> listNetwork();

    @Delete("delete from networks where openid=#{openId}")
    void deleteNetwork(String openId);

    @Insert("insert into networks(id, openid, abstract, demand, resource) values(#{id}, #{openid}, #{Abstract}," +
            "#{demand}, #{resource})")
    void inserNetwork(@Param("id") String id, @Param("openId") String openId, @Param("Abstract") String Abstract,
                      @Param("demand") String demand, @Param("resource") String resource);

//    新用户插入默认（空）人脉信息；
    @Insert("insert into networks(id, openid, weight, update_time) values(#{id}, #{openId}, #{weight}, now())")
    void insertNull(String id, String openId, int weight);

    // 人脉更新
    @Insert("update networks set abstract=#{Abstract}, demand=#{demand}, resource=#{resource}, update_time=now() where " +
            "openid=#{openId}")
    void updateNetwork(@Param("openId") String openId, @Param("Abstract") String Abstract,
                      @Param("demand") String demand, @Param("resource") String resource);

    // 人脉详情
    @Select("select * from networks where openid = #{openId}")
    Network fetchNetworkDetail(@Param("openId") String openId);

    // 阅读量获取
    @Select("select read_count from networks where openid=#{openId}")
    int getReadCount(String openId);

    @Update("update networks set read_count=#{count} where openid=#{openId}")
    void updateCount(String openId, int count);
}
