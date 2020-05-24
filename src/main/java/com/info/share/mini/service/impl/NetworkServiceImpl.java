package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.share.mini.controller.ArticleController;
import com.info.share.mini.entity.*;
import com.info.share.mini.mapper.NetworkMapper;
import com.info.share.mini.mapper.UserMapper;
import com.info.share.mini.service.NetworkService;
import com.info.share.mini.service.UserService;
import com.info.share.mini.utils.HighLightSearch;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Api(value = "2. 人脉接口列表", tags = {"人脉接口列表"})
@Service("networkService")
@Component
public class NetworkServiceImpl implements NetworkService {

    private static final Logger logger = LogManager.getLogger(NetworkServiceImpl.class);

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "networkMapper")
    private NetworkMapper networkMapper;

    @Override
    public void addReadCountByOne(String openID) {
        int count = networkMapper.getReadCount(openID);
        count ++;
        networkMapper.updateCount(openID, count);
    }

    @Override
    public JSONObject createNull(String id, String openid, int weight){
        ResultJSON res;
        try {
            networkMapper.insertNull(id, openid, weight);
            res = ResultJSON.success(200, "create empty network create successfully.");
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            if (e.getLocalizedMessage().contains("Duplicate entry")){
                res = ResultJSON.success(401, " already has this empty network.");
            }else{
                res = ResultJSON.error(e.getLocalizedMessage());
                logger.error(openid + " create network create failed.");
            }
        }
        return JSONObject.parseObject(res.toSimpleString());
    }

    @Override
    public JSONObject updateNetwork(User userInfo, Network networkInfo){
        ResultJSON res ;
        String openId = userInfo.getOpenid();
        // 判断用户是否存在
        if (!userService.checkExists(openId)){
            res = ResultJSON.success(203, "sorry, we have none this user.");
            return JSONObject.parseObject(res.toSimpleString());
        }
        // 判断用户是否会员；
        if(!userService.checkVip(openId)){
            res = ResultJSON.success(203,"sorry, this function can only used by vip person.");
            return JSONObject.parseObject(res.toSimpleString());
        }
        // 更新用户信息
        try {
            userMapper.updateUserInfo(userInfo.getOpenid(), userInfo.getName(), userInfo.getCompany(),
                    userInfo.getCountry(), userInfo.getProvince(),
                    userInfo.getCity(), userInfo.getTel(), userInfo.getWeChat(), userInfo.isShowNumber());
            logger.info(userInfo.getName() + " " + userInfo.getOpenid() + " get userinfo update sucessfully.");
        }catch (Exception e){
            res = ResultJSON.error("failed to update user info, for more detail " + e.getLocalizedMessage());
            logger.error(userInfo.getName() + " " + userInfo.getOpenid() + " userinfo update failed. " + e.getLocalizedMessage());
            return JSONObject.parseObject(res.toSimpleString());
        }
        //更新人脉信息
        try {
            networkMapper.updateNetwork(userInfo.getOpenid(), networkInfo.get_abstract(), networkInfo.getDemand(),
                    networkInfo.getResource());
            logger.info(userInfo.getName() + " " + userInfo.getOpenid() + " network update sucessfully.");
        }catch (Exception e){
            res = ResultJSON.error("failed to update network info, for more detail " + e.getLocalizedMessage());
            logger.error(userInfo.getName() + " " + userInfo.getOpenid() + " network update failed. ");
        }
        res = ResultJSON.success(200, "updating successfully.");
        return JSONObject.parseObject(res.toSimpleString());
    }

    // 人脉列表
    @Override
    public JSONObject listNetworkByCount(int page, int pageSize){
        ResultJSON res;
        List<Network> networks;
        List<JSONObject> networksWithUserInfo = new ArrayList<>();
        try{
            PageHelper.startPage(page, pageSize);
            networks = networkMapper.listNetwork();
            PageInfo pageInfo = new PageInfo(networks);
            int totalPage = pageInfo.getPages();
            for (Network nt: networks){
                JSONObject tmp = new JSONObject();
                UserInfoBasic userInfo = userMapper.getUserInfoBasic(nt.getOpenId());
                tmp.put("network", nt);
                tmp.put("userInfo", userInfo);
                networksWithUserInfo.add(tmp);
            }
            res = ResultJSON.success(page, totalPage, pageSize, networksWithUserInfo);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toString());
    }

    @Override
    public JSONObject getNetworkDetail(String openid, String id){
        ResultJSON res ;
        if (!userService.checkExists(openid)){
            res = ResultJSON.success(404, "we have none this user!");
            return JSONObject.parseObject(res.toSimpleString());
        }
        try{
            Network network = networkMapper.fetchNetworkDetail(openid);
            UserInfoBasic userInfoBasic = userMapper.getUserInfoBasic(openid);
            logger.info("***userINFO to String " + userInfoBasic.toString());
            JSONObject tmp = new JSONObject();
            tmp.put("network", network);
            tmp.put("basicUserInfo", userInfoBasic);
            logger.info(tmp.toJSONString());
            res = ResultJSON.success(tmp);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleDataString());
    }

    @Override
    public JSONObject searchNetwork(String keyword, int page, int pageSize){
//        String index, String type, String[] fields, int page, int pageSize
        String[] fields = {"name", "company", "position", "abstract", "province", "city"};
        HighLightSearch searchClient = new HighLightSearch("network", "network", fields, page, pageSize);
        SearchHits searchHits = searchClient.search(keyword);

        long total = searchHits.getTotalHits();
        long totalPage = total % pageSize == 0 ? total / pageSize : (total / pageSize + 1 );

        SearchHit[] hits = searchHits.getHits();
        List<ElasticNetworkResult> networks = new LinkedList<>();
        if (hits.length <= 0) {
            return JSONObject.parseObject(ResultJSON.success(400, "none hit by this keyword.").toSimpleString());
        }
        for (SearchHit hit:hits){
            ElasticNetworkResult tmpNetwork = new ElasticNetworkResult();
            JSONObject temp = JSONObject.parseObject(hit.getSourceAsString());
            for(String field : fields){
                HighlightField esField = hit.getHighlightFields().get(field);
//                logger.info(temp.getString(field));
                if (esField != null){
                    temp.put(field, esField.fragments()[0].string());
                }
            }
            tmpNetwork.setId(temp.getString("id"));
            tmpNetwork.setOpenid(temp.getString("openid"));
            tmpNetwork.setCompany(temp.getString("company"));
            tmpNetwork.setPosition(temp.getString("position"));
            tmpNetwork.setAbstract(temp.getString("abstract"));
            tmpNetwork.setProvince(temp.getString("province"));
            tmpNetwork.setCity(temp.getString("city"));
            tmpNetwork.setUpdateTime(temp.getString("update_time"));
            networks.add(tmpNetwork);
        }
        ResultJSON res = ResultJSON.success(page, pageSize, (int)totalPage, networks);
        logger.info(res.toString());
        return JSONObject.parseObject(res.toString());
    }
}
