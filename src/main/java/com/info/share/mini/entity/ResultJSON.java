package com.info.share.mini.entity;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.util.StringUtils;

public class ResultJSON {

    // code 状态码： 成功：200，失败：500, 202登录失效
    private int code;
    // 错误信息
    private String msg;
    // 返回的数据
    private Object responseData;

    private int currentPage;// 当前页码

    private int pageSize;// 每页数据条数

    private int recordCount; // 总记录数

    private int pageCount; // 总页数

    // 成功返回<无返回数据>
    public static ResultJSON success() {
        ResultJSON result = new ResultJSON(200, "操作成功", null);
        return result;
    }
    public static ResultJSON success(int code, String msg){
        ResultJSON result = new ResultJSON(code, msg, null);
        return result;
    }
    // 自定义成功状态
    public static ResultJSON success(String msg){
        return ResultJSON.success(200, msg);
    }
    // 成功返回<有返回数据>
    public static ResultJSON success(Object responseData) {
        ResultJSON result = new ResultJSON(200, "操作成功", responseData);
        return result;
    }

    // 成功返回<返回分页数据>
    public static ResultJSON success(int currentPage, int pageSize, int pageCount, Object responseData) {
        ResultJSON result = new ResultJSON(200, "操作成功", responseData, currentPage, pageSize, pageCount);
        return result;
    }

    // 代码抛异常
    public static ResultJSON error(String string) {
        ResultJSON result = new ResultJSON(200, string, null);
        result.setCode(500);
        if (StringUtils.isEmpty(string)) {
            result.setMsg("操作失败");
        }
        return result;
    }

    public static ResultJSON err(int code, Object responseData){
        ResultJSON resultJSON = new ResultJSON(code, "操作失败", responseData);
        return resultJSON;
    }

    // 自定义返回状态及返回数据
    public ResultJSON(int code, String msg, Object responseData) {
        this.code = code;
        this.msg = msg;
        this.responseData = responseData;
    }

    // 自定义返回分页状态及返回数据
    public ResultJSON(int code, String msg, Object responseData, int currentPage, int pageSize, int pageCount) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
        this.code = code;
        this.msg = msg;
        this.responseData = responseData;
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public String toString() {
        return "{" +
                "code:" + code  +
                ", msg:'" + msg + '\'' +
                ", responseData:" + JSONObject.toJSONString(responseData, SerializerFeature.WriteMapNullValue) +
                ", currentPage:" + currentPage +
                ", pageSize:" + pageSize +
                ", recordCount:" + recordCount +
                ", pageCount:" + pageCount +
                '}';
    }

    public String toSimpleDataString(){
        return "{" +
                "code:" + code  +
                ", responseData:" + JSONObject.toJSONString(responseData, SerializerFeature.WriteMapNullValue) +
                ", msg:'" + msg + '\'' +
                '}';
    }

    public String toSimpleString(){
        return "{" +
                "code:" + code  +
                ", msg:'" + msg + '\'' +
                '}';
    }
}

