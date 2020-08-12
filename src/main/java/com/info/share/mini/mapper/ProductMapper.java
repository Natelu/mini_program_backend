package com.info.share.mini.mapper;

import com.info.share.mini.entity.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Mapper
@Service(value = "productMapper")
public interface ProductMapper {

    @Insert("insert into products (id, name, introduce, fee, publish_time) values(#{id}," +
            "#{name}, #{desc}, #{fee}, now())")
    void createProduct(String id, String name, String desc, float fee);

    @Select("select * from products")
    List<Product> getProductsList();

    @Select("select * from products where name=#{name} limit 1")
    Product getProductByName(String name);
}
