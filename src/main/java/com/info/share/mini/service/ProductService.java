package com.info.share.mini.service;

import com.alibaba.fastjson.JSONObject;

public interface ProductService {

    JSONObject createProduct(String name, String desc, float fee);

    JSONObject getAllProducts(int page, int pageSize);
}
