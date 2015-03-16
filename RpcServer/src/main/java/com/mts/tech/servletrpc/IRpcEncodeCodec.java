package com.mts.tech.servletrpc;

import com.mts.tech.servletrpc.model.RpcResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by reyda_000 on 3/15/2015.
 */
public interface IRpcEncodeCodec {
    public void encode(RpcResponse rpcResponse, HttpServletResponse httpResponse) throws Exception;
}
