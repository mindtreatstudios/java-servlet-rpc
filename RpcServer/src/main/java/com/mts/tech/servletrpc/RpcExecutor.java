package com.mts.tech.servletrpc;

import com.mts.tech.servletrpc.model.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by reyda_000 on 3/15/2015.
 */
public class RpcExecutor {
    static final Logger Log = LoggerFactory.getLogger(RpcExecutor.class);


    private HashMap<String, Object> services = new HashMap<String, Object>();
    public void addService(String namespace, Object instance) {
        services.put(namespace, instance);
    }
    public void removeService(String namespace) {
        services.remove(namespace);
    }

    public RpcResponse execute(RpcRequest rpcRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        if (rpcRequest == null)
            return null;

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.id = rpcRequest.id;

        Log.debug("Searching namespace "+rpcRequest.methodNamespace);
        Object service = services.get(rpcRequest.methodNamespace);
        if (service == null) {
            Log.error("Method namespace not found");
            rpcResponse.error = new RpcError(RpcErrorCodes.METHOD_NOT_FOUND, "Method namespace not found.");
            return rpcResponse;
        }

        Method method = null;
        try {
            do {
                if (rpcRequest.params != null)
                {
                    Log.debug("Searching method "+rpcRequest.methodName+", for params class "+rpcRequest.params.getClass());

                    // Try signature 1
                    try{
                        method = service.getClass().getMethod(rpcRequest.methodName, rpcRequest.params.getClass(), HttpServletRequest.class, HttpServletResponse.class);
                    }catch (Exception e)
                    { }
                    if (method != null) {
                        rpcResponse.result = method.invoke(service, rpcRequest.params, httpRequest, httpResponse);
                        break;
                    }

                    // Try signature 2
                    try{
                        method = service.getClass().getMethod(rpcRequest.methodName, rpcRequest.params.getClass());
                    }catch (Exception e)
                    { }
                    if (method != null) {
                        rpcResponse.result = method.invoke(service, rpcRequest.params);
                        break;
                    }
                }
                else
                {
                    Log.debug("Searching method "+rpcRequest.methodName);

                    // Try signature 1
                    try{
                        method = service.getClass().getMethod(rpcRequest.methodName, HttpServletRequest.class, HttpServletResponse.class);
                    }catch (Exception e)
                    { }
                    if (method != null) {
                        rpcResponse.result = method.invoke(service, httpRequest, httpResponse);
                        break;
                    }

                    // Try signature 2
                    try{
                        method = service.getClass().getMethod(rpcRequest.methodName);
                    }catch (Exception e)
                    { }
                    if (method != null) {
                        rpcResponse.result = method.invoke(service);
                        break;
                    }
                }

                if (method == null)
                    rpcResponse.error = new RpcError(RpcErrorCodes.METHOD_NOT_FOUND, "Method not found.");
            } while(false);
        }
        catch (IllegalAccessException e) {
            Log.error(ExceptionUtils.getMessage(e)+", "+ExceptionUtils.getStackTrace(e));
            rpcResponse.error = new RpcError(RpcErrorCodes.METHOD_NOT_AVAILABLE, "Method not available.");
        }
        catch (IllegalArgumentException e) {
            Log.error(ExceptionUtils.getMessage(e)+", "+ExceptionUtils.getStackTrace(e));
            rpcResponse.error = new RpcError(RpcErrorCodes.INVALID_PARAMS, "Invalid params");
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            Log.error("RPC caught api exception "+ ExceptionUtils.getMessage(t)+", "+ExceptionUtils.getStackTrace(t));

            if (t.getClass().isInstance(RpcException.class))
                rpcResponse.error = new RpcError((RpcException)t);
            else
                rpcResponse.error = new RpcError(-32000, t);
        }


        if (method != null && (method.getReturnType() == null || method.getReturnType().equals(Void.class)))
            return null;
        else
            return rpcResponse;
    }
}
