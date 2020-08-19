package com.info.share.mini.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

@Mapper
@Service(value = "billingMapper")
public interface BillingMapper {

    @Insert("insert billing (id, user_id, wx_number, task_id, task_name, money, type, status, wx_pay_id, create_time) values(#{id}, #{userId}," +
            "#{wxNumber}, #{taskId}, #{taskName}, #{money}, #{type}, #{status}, #{wxPayId}, now())")
    void insertBilling(String id, String userId, String wxNumber, String wxPayId, String taskId, String taskName, int money, String type, String status);

    @Update("update billing set status=#{status} where wx_pay_id=#{wxPayId}")
    void updateBillingStatus(String wxPayId, String status);
}
