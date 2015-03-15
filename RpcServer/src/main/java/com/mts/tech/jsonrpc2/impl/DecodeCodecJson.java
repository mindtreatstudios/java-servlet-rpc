package com.mts.tech.jsonrpc2.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.tech.jsonrpc2.IRpcDecodeCodec;
import com.mts.tech.jsonrpc2.model.RpcErrorCodes;
import com.mts.tech.jsonrpc2.model.RpcException;
import com.mts.tech.jsonrpc2.model.RpcRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DecodeCodecJson implements IRpcDecodeCodec {

    private boolean strictJsonRpc2Flag = true;
	private ObjectMapper jsonMapper;

    public DecodeCodecJson()
    {
        this(true);
    }

    public DecodeCodecJson(boolean strictJsonRpc2Flag) {
        this.strictJsonRpc2Flag = strictJsonRpc2Flag;
        jsonMapper = new ObjectMapper();
    }

	@Override
	public RpcRequest decode(HttpServletRequest request) throws RpcException {

        JsonNode root;

        try {
            root = jsonMapper.readTree(request.getInputStream());
        } catch (IOException e) {
            throw new RpcException(RpcErrorCodes.PARSE_ERROR, "Invalid request", ExceptionUtils.getMessage(e));
        }

        if (!root.isObject())
			throw new RpcException(RpcErrorCodes.PARSE_ERROR, "Invalid root node type. Must be object");
		
//		ObjectNode root = (ObjectNode)node;

        if (strictJsonRpc2Flag) {
            String jsonrpc = root.get("jsonrpc").asText();
            if (jsonrpc == null || !jsonrpc.equals("2.0"))
                throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid jsonrpc value: "+jsonrpc+". Should be 2.0");
        }

		RpcRequest rpcRequest = new RpcRequest();
		
		if (root.hasNonNull("id"))
		{
			JsonNode idNode = root.get("id");
			if (!idNode.isTextual() && !idNode.isNumber())
				throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid id format. Must be null, string or number");
			rpcRequest.id = idNode;
		}
		
		if (root.hasNonNull("method")) {
            String method = root.get("method").asText();
            String[] components = method.split("\\.");
            if (components.length != 2)
                throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid method name format");
            rpcRequest.methodNamespace = components[0];
            rpcRequest.methodName = components[1];
        }
		
		if (root.hasNonNull("params"))
		{
			rpcRequest.params = root.get("params");
			if (!rpcRequest.params.isObject() && !rpcRequest.params.isArray())
				throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid params type. Must be object or array");
		}

		return rpcRequest;
	}
}