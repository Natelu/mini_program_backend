package com.info.share.mini.entity;

public class WeChatAuthInfo {

    private String openId;

    private String sessionKey;

    private String unionId;

    @Override
    public String toString() {
        return "WeChatAuth{" +
                "openId='" + openId + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                ", unionId='" + unionId + '\'' +
                '}';
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
