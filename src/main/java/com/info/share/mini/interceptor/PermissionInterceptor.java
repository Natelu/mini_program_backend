package com.info.share.mini.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    private static final Logger logger = LogManager.getLogger(PermissionInterceptor.class);

    @Resource(name = "userService")
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (method.equals("GET")){
            logger.info("--> Get request dont need permission check.");
            return true;
        }
        else{
            String userId = request.getHeader("openid");
            if(!userService.checkExists(userId)){
                logger.error("无此用户 " + userId);
                ResultJSON res = ResultJSON.success(202, "抱歉，没有该用户");
                this.SetResponse(response, JSONObject.toJSONString(res));
                return false;
            }else if(!userService.checkVip(userId)){
                logger.error("该用户不是VIP " + userId);
                ResultJSON res = ResultJSON.success(202, "抱歉，该功能仅适用于会员用户");
                this.SetResponse(response, JSONObject.toJSONString(res));
                return false;
            }
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    public void SetResponse(HttpServletResponse response, String result){
        JSONObject tmp = JSONObject.parseObject(result);
        tmp.remove("currentPage");
        tmp.remove("pageCount");
        tmp.remove("pageSize");
        tmp.remove("recordCount");
        result = tmp.toJSONString();
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try{
            writer = response.getWriter();
            writer.print(result);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }finally {
            if (writer != null){
                writer.close();
            }
        }
    }
}
