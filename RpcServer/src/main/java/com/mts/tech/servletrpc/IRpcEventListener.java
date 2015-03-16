package com.mts.tech.servletrpc;

import com.mts.tech.servletrpc.model.RpcRequest;
import com.mts.tech.servletrpc.model.RpcResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Andrei on 1/20/2015.
 */
public interface IRpcEventListener {
    public void onStartHandlingHttpRequest(HttpServletRequest request);
    public void onFinishHandlingHttpRequest(HttpServletRequest request, HttpServletResponse response);

    public void onStartExecutingRpcRequest(RpcRequest rpcRequest);
    public void onFinishExecutingRpcRequest(RpcRequest rpcRequest, RpcResponse response);
}
