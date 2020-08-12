package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.share.mini.entity.Product;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.mapper.ProductMapper;
import com.info.share.mini.service.ProductService;
import com.info.share.mini.utils.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service(value = "productService")
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);
    @Resource(name = "productMapper")
    private ProductMapper productMapper;

    @Override public JSONObject createProduct(String name, String desc, float fee) {
        ResultJSON res;
        try{
            Product product = productMapper.getProductByName(name);
            if (product != null){
                res = ResultJSON.success(202, "该产品已存在。");
            }else {
                String id = Tools.getRandomId();
                productMapper.createProduct(id, name, desc, fee);
                res = ResultJSON.success(200, "商品插入成功.");
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
    }

    @Override public JSONObject getAllProducts(int page, int pageSize) {
        ResultJSON res ;
        List<Product> productList;
        try{
            PageHelper.startPage(page, pageSize);
            productList = productMapper.getProductsList();
            PageInfo pageInfo = new PageInfo(productList);
            int totalPage = pageInfo.getPages();
            res = ResultJSON.success(page, pageSize, totalPage, productList);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
            return JSONObject.parseObject(res.toSimpleString());
        }
        return JSONObject.parseObject(res.toString());
    }
}
