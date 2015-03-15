package com.mts.tech.jsonrpc2;

import com.mts.tech.jsonrpc2.model.*;
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
            rpcResponse.error = new RpcError(RpcErrorCodes.METHOD_NOT_FOUND, "Method namespace not found.");
            return rpcResponse;
        }

        Method method = null;
        try {
            if (rpcRequest.params != null)
            {
                Log.debug("Searching method "+rpcRequest.methodName+", for params class "+rpcRequest.params.getClass());

                // Try signature 1
                try{
                    method = service.getClass().getMethod(rpcRequest.methodName, rpcRequest.params.getClass(), HttpServletRequest.class, HttpServletResponse.class);
                }catch (Exception e)
                { }
                if (method != null)
                    rpcResponse.result = method.invoke(service, rpcRequest.params, httpRequest, httpResponse);

                // Try signature 2
                try{
                    method = service.getClass().getMethod(rpcRequest.methodName, rpcRequest.params.getClass());
                }catch (Exception e)
                { }
                if (method != null)
                    rpcResponse.result = method.invoke(service, rpcRequest.params);
            }
            else
            {
                Log.debug("Searching method "+rpcRequest.methodName);

                // Try signature 1
                try{
                    method = service.getClass().getMethod(rpcRequest.methodName, HttpServletRequest.class, HttpServletResponse.class);
                }catch (Exception e)
                { }
                if (method != null)
                    rpcResponse.result = method.invoke(service, httpRequest, httpResponse);

                // Try signature 2
                try{
                    method = service.getClass().getMethod(rpcRequest.methodName);
                }catch (Exception e)
                { }
                if (method != null)
                    rpcResponse.result = method.invoke(service);
            }

            if (method == null)
                rpcResponse.error = new RpcError(RpcErrorCodes.METHOD_NOT_FOUND, "Method not found.");

        } catch (IllegalAccessException e) {
            rpcResponse.error = new RpcError(RpcErrorCodes.METHOD_NOT_AVAILABLE, "Method not available.");
        } catch (IllegalArgumentException e) {
            rpcResponse.error = new RpcError(RpcErrorCodes.INVALID_PARAMS, "Invalid params");
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
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
