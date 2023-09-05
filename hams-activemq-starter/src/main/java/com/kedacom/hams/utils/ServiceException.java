package com.kedacom.hams.utils;

public class ServiceException extends Exception{
    private int code = 0;

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(int code, String message){
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
