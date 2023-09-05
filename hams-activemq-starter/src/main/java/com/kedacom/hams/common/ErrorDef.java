package com.kedacom.hams.common;

public enum ErrorDef {

    /**
     *
     */
    ERROR_MQ_NOT_CONNECT(700100, "未连接，发布失败"),
    ERROR_MQ_INNER_EXCEPTION(700101, "mq内部异常");

    private int code;

    private String msg;

    private ErrorDef(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
}
