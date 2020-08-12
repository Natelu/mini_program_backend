package com.info.share.mini.controller;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.WxPreOrder;
import com.info.share.mini.service.ProductService;
import com.info.share.mini.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Api(value = "商品（价格）接口",tags = {"6、商品接口（这里的商品先简单理解为价格列表）"})
@RequestMapping("/products")
public class ProductControler {

    @Resource(name = "productService")
    private ProductService productService;

    @Resource(name = "wxPayService")
    private WxPayService wxPayService;

    @ApiOperation(value = "价格列表（商品列表）", notes= "批量获取商品列表", httpMethod = "GET")
    @GetMapping(value = "/list", produces = {"application/json;charset=UTF-8"})
    public JSONObject doingTask(@RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                HttpServletResponse response){
        JSONObject res = productService.getAllProducts(page, pageSize);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    @ApiOperation(value = "上传价格(商品）", notes= "添加新价格/商品", httpMethod = "POST")
    @PostMapping(value = "/upload", produces = {"application/json;charset=UTF-8"})
    public JSONObject uploadProduct(@RequestBody JSONObject body, HttpServletRequest request,
                                    HttpServletResponse response){
        String name = body.getString("name");
        String desc = body.getString("desc");
        float fee = body.getFloat("fee");
        JSONObject res = productService.createProduct(name, desc, fee);
        response.setStatus(res.getIntValue("code"));
        return res;
    }
}
