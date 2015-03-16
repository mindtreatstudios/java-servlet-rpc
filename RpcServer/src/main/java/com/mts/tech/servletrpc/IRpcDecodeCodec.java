package com.mts.tech.servletrpc;

import com.mts.tech.servletrpc.model.RpcRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by reyda_000 on 3/15/2015.
 */
public interface IRpcDecodeCodec {

    public RpcRequest decode(HttpServletRequest request) throws Exception;

}
