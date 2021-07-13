package com.wang.util;

public enum LocalCacheType {
    // 异步
    ASYN(0,"asyn"),
    // 同步
    SYN(1,"syn");
    private int code;
    private String type;

    LocalCacheType(int code, String type){
        this.code=code;
        this.type=type;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
