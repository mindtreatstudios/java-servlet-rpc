package com.mts.tech.servletrpc.impl;

import com.fasterxml.jackson.databind.JsonNode;
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

public class DecodeCodecJson implements IRpcDecodeCodec {
    static final Logger Log = LoggerFactory.getLogger(DecodeCodecJson.class);

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
            Log.error(ExceptionUtils.getStackTrace(e));
            throw new RpcException(RpcErrorCodes.PARSE_ERROR, "Invalid request", ExceptionUtils.getMessage(e));
        }

        if (!root.isObject()) {
            Log.error("Invalid root node type ("+root.getClass().getName()+"). Must be object");
            throw new RpcException(RpcErrorCodes.PARSE_ERROR, "Invalid root node type. Must be object");
        }
		
//		ObjectNode root = (ObjectNode)node;

        if (strictJsonRpc2Flag) {
            String jsonrpc = root.get("jsonrpc").asText();
            if (jsonrpc == null || !jsonrpc.equals("2.0")) {
                Log.error("Invalid jsonrpc value: "+jsonrpc);
                throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid jsonrpc value: " + jsonrpc + ". Should be 2.0");
            }
        }

		RpcRequest rpcRequest = new RpcRequest();
		
		if (root.hasNonNull("id"))
		{
			JsonNode idNode = root.get("id");
			if (!idNode.isTextual() && !idNode.isNumber()) {
                Log.error("Invalid id format ("+ idNode.getClass().getName()+"). Must be null, string or number");
                throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid id format. Must be null, string or number");
            }
			rpcRequest.id = idNode;
		}
		
		if (root.hasNonNull("method")) {
            String method = root.get("method").asText();
            String[] components = method.split("\\.");
            if (components.length != 2) {
                Log.error("Invalid method format. Could not split by `.`");
                throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid method format");
            }
            rpcRequest.methodNamespace = components[0];
            rpcRequest.methodName = components[1];
        }
        else {
            Log.error("Missing method or is null");
            throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Missing method");
        }
		
		if (root.hasNonNull("params"))
		{
			rpcRequest.params = root.get("params");
			if (!rpcRequest.params.isObject() && !rpcRequest.params.isArray()) {
                Log.error("Invalid params type ("+rpcRequest.params.getClass().getName()+"). Must be object or array");
                throw new RpcException(RpcErrorCodes.INVALID_REQUEST, "Invalid params type. Must be object or array");
            }
		}

		return rpcRequest;
	}
}
