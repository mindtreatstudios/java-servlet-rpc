package com.mts.tech.servletrpc;

import com.mts.tech.servletrpc.model.RpcRequest;
import com.mts.tech.servletrpc.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RpcServer {
    static final Logger Log = LoggerFactory.getLogger(RpcServer.class);


    protected IRpcEventListener eventListener = null;
    public void setEventListener(IRpcEventListener listener){
        eventListener = listener;
    }
    public IRpcEventListener getEventListener() {
        return eventListener;
    }


    protected RpcDecoder decoder;
    public RpcDecoder getDecoder() {
        return decoder;
    }

    protected RpcEncoder encoder;
    public RpcEncoder getEncoder() {
        return encoder;
    }

    protected RpcExecutor executor;
    public RpcExecutor getExecutor() {
        return executor;
    }


    public RpcServer(RpcDecoder decoder, RpcExecutor executor, RpcEncoder encoder) throws Exception
    {
        if (decoder == null)
            throw new Exception("Decoder is null");
        if (executor == null)
            throw new Exception("Executor is null");
        if (encoder == null)
            throw new Exception("Encoder is null");

        this.decoder = decoder;
        this.executor = executor;
        this.encoder = encoder;
    }
		
	///////////////////////////////////////////////////////////////////////////////////////////
    
	public void handle(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception
	{
        if (eventListener != null)
            eventListener.onStartHandlingHttpRequest(httpRequest);

        RpcRequest rpcRequest = decoder.decode(httpRequest);
        if (rpcRequest == null)
            throw new Exception("Invalid decoded rpc request (null)");

        if (eventListener != null)
            eventListener.onStartExecutingRpcRequest(rpcRequest);

        RpcResponse rpcResponse = executor.execute(rpcRequest, httpRequest, httpResponse);
//        if (rpcResponse == null)
//            throw new Exception("Invalid rpc response (null)");

        if (eventListener != null)
            eventListener.onFinishExecutingRpcRequest(rpcRequest, rpcResponse);

        if (rpcResponse != null)
            encoder.encode(rpcResponse, httpResponse, httpRequest);

        if (eventListener != null)
            eventListener.onFinishHandlingHttpRequest(httpRequest, httpResponse);
    }
}
