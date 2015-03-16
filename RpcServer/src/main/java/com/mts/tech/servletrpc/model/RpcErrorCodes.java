package com.mts.tech.servletrpc.model;

/**
 * Created by reyda_000 on 3/15/2015.
 */
public class RpcErrorCodes {
    public static final int PARSE_ERROR = -32700;
    public static final int INVALID_REQUEST = -32600;
    public static final int METHOD_NOT_FOUND = -32601;
    public static final int METHOD_NOT_AVAILABLE = -32601;
    public static final int INVALID_PARAMS = -32602;
    public static final int INTERNAL_ERROR = -32603;

    public static final int IMPLEMENTATION_DEFINED_MIN = -32000;
    public static final int IMPLEMENTATION_DEFINED_MAX = -32099;
}
