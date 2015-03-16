package com.mts.tech.servletrpc.model;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by reyda_000 on 3/15/2015.
 */
public class RpcError {

    protected int code;
    protected String message;
    protected Object data;

    public RpcError(int code, String message) {
        this(code, message, null);
    }

    public RpcError(RpcException e) {
        this(e.getCode(), ExceptionUtils.getMessage(e), e.getData());
    }

    public RpcError(int code, Throwable t) {
        this(code, ExceptionUtils.getMessage(t), ExceptionUtils.getStackTrace(t));
    }

    public RpcError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public Object getData() {
        return data;
    }


    @Override
    public String toString() {
        return "["+code+", "+message+", "+data+"]";
    }
}
