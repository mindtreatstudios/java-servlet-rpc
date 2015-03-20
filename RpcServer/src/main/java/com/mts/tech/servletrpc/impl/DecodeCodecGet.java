package com.mts.tech.servletrpc.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.tech.servletrpc.IRpcDecodeCodec;
import com.mts.tech.servletrpc.model.RpcErrorCodes;
import com.mts.tech.servletrpc.model.RpcException;
import com.mts.tech.servletrpc.model.RpcRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DecodeCodecGet implements IRpcDecodeCodec {

    static final Logger Log = LoggerFactory.getLogger(DecodeCodecGet.class);

    private boolean strictJsonRpc2Flag = true;
	private ObjectMapper jsonMapper;

    public DecodeCodecGet()
    {
        this(true);
    }

	public DecodeCodecGet(boolean strictJsonRpc2Flag) {
        this.strictJsonRpc2Flag = strictJsonRpc2Flag;
		jsonMapper = new ObjectMapper();
	}

	@Override
	public RpcRequest decode(HttpServletRequest request) throws RpcException {


        try {
            String queryString = request.getQueryString();
            if (queryString == null)
                Log.error("Request query string is null!!!" + request.getRequestURI());
            else
                Log.trace("Decoding request with parameters: " + URLDecoder.decode(request.getQueryString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.error(ExceptionUtils.getStackTrace(e));
        }

        if (strictJsonRpc2Flag) {
            String jsonrpc = request.getParameter("jsonrpc");
            if (jsonrpc == null || !jsonrpc.equals("2.0")) {
                Log.error("Invalid jsonrpc value: "+jsonrpc);
                throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid jsonrpc value: " + jsonrpc + ". Should be 2.0");
            }
        }

		RpcRequest rpcRequest = new RpcRequest();

        // Read the request id
		rpcRequest.id = request.getParameter("id");

        // Read the method
        String method = request.getParameter("method");
        if (method == null) {
            Log.error("Missing method parameter");
            throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Missing method parameter");
        }
        String[] components = method.split("\\.");
        if (components.length != 2) {
            Log.error("Invalid method format. Could not split by `.`");
            throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid method format");
        }
		rpcRequest.methodNamespace = components[0];
        rpcRequest.methodName = components[1];

        // Read the params
		String params = request.getParameter("params");
		if (params != null && params.length() > 0)
		{
            try {
                rpcRequest.params = jsonMapper.readTree(params);
            } catch (IOException e) {
                Log.error(ExceptionUtils.getStackTrace(e));
                throw new RpcException(RpcErrorCodes.PARSE_ERROR, "Invalid params json string");
            }

            if (!rpcRequest.params.isObject() && !rpcRequest.params.isArray()) {
                Log.error("Invalid params type ("+rpcRequest.params.getClass().getName()+"). Must be object or array");
                throw new RpcException(RpcErrorCodes.INVALID_PARAMS, "Invalid params type. Must be object or array");
            }
		}

		return rpcRequest;
	}
}
