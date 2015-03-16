package com.mts.tech.servletrpc.model;

public class RpcException extends Exception {
	private static final long serialVersionUID = 1L;
	
    public RpcException(int code, String message)
    {
        this(code, message, null);
    }

    public RpcException(int code, String message, Object data)
    {
        super(message);
        this.code = code;
        this.data = data;
    }

    protected int code;
    public int getCode()
    {
        return code;
    }

    protected Object data;
    public Object getData() {
        return data;
    }

    @Override
    public String toString()
    {
        return "[code="+this.code+", "+super.toString()+"]";
    }
}
