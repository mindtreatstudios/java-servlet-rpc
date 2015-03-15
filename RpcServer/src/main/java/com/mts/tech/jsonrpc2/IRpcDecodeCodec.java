package com.mts.tech.jsonrpc2;

import com.mts.tech.jsonrpc2.model.RpcRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by reyda_000 on 3/15/2015.
 */
public interface IRpcDecodeCodec {

    public RpcRequest decode(HttpServletRequest request) throws Exception;

}
