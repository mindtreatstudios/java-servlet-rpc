package com.mts.tech.jsonrpc2.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.tech.jsonrpc2.IRpcDecodeCodec;
import com.mts.tech.jsonrpc2.model.RpcErrorCodes;
import com.mts.tech.jsonrpc2.model.RpcException;
import com.mts.tech.jsonrpc2.model.RpcRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DecodeCodecGet implements IRpcDecodeCodec {

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

        if (strictJsonRpc2Flag) {
            String jsonrpc = request.getParameter("jsonrpc");
            if (jsonrpc == null || !jsonrpc.equals("2.0"))
                throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid jsonrpc value: "+jsonrpc+". Should be 2.0");
        }

		RpcRequest rpcRequest = new RpcRequest();

        // Read the request id
		rpcRequest.id = request.getParameter("id");

        // Read the method
        String method = request.getParameter("method");
        String[] components = method.split("\\.");
        if (components.length != 2)
            throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid method name format");
		rpcRequest.methodNamespace = components[0];
        rpcRequest.methodName = components[1];

        // Read the params
		String params = request.getParameter("params");
		if (params != null && params.length() > 0)
		{
            try {
                rpcRequest.params = jsonMapper.readTree(params);
            } catch (IOException e) {
                throw new RpcException(RpcErrorCodes.PARSE_ERROR, "Invalid params json string");
            }

            if (!rpcRequest.params.isObject() && !rpcRequest.params.isArray())
				throw new RpcException(RpcErrorCodes.INVALID_PARAMS, "Invalid params type. Must be object or array");
		}

		return rpcRequest;
	}
}