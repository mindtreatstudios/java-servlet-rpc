package com.mts.tech.servletrpc;

import com.mts.tech.servletrpc.model.RpcResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by reyda_000 on 3/15/2015.
 */
public class RpcEncoder {

    private IRpcEncodeCodec codec;

    public RpcEncoder(IRpcEncodeCodec codec) {
        this.codec = codec;
    }

    public void encode(RpcResponse rpcResponse, HttpServletResponse httpResponse, HttpServletRequest httpRequest) throws Exception
    {
        if (rpcResponse == null)
            return ;

        codec.encode(rpcResponse, httpResponse);
    }
}
