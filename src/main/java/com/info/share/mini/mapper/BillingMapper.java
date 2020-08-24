package com.info.share.mini.mapper;

import com.info.share.mini.entity.Billing;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
@Service(value = "billingMapper")
public interface BillingMapper {

    @Insert("insert billing (id, user_id, wx_number, phone_number, task_id, task_name, money, type, status, wx_pay_id, create_time) values(#{id}, #{userId}," +
            "#{wxNumber}, #{phoneNumber}, #{taskId}, #{taskName}, #{money}, #{type}, #{status}, #{wxPayId}, now())")
    void insertBilling(String id, String userId, String wxNumber, String phoneNumber, String wxPayId, String taskId, String taskName, int money, String type, String status);

    @Update("update billing set status=#{status} where wx_pay_id=#{wxPayId}")
    void updateBillingStatus(String wxPayId, String status);

    @Select("select * from billing where status='SUCCESS' order by create_time desc")
    List<Billing> fetchALlSuccessBillings();
}
