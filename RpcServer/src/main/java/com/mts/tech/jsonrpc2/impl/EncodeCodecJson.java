package com.mts.tech.jsonrpc2.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mts.tech.jsonrpc2.IRpcEncodeCodec;
import com.mts.tech.jsonrpc2.model.RpcResponse;

import javax.servlet.http.HttpServletResponse;

public class EncodeCodecJson implements IRpcEncodeCodec {

    private boolean strictJsonRpc2Flag = true;
    private ObjectMapper jsonMapper;

    public EncodeCodecJson()
    {
        this(true);
    }

    public EncodeCodecJson(boolean strictJsonRpc2Flag) {
        this.strictJsonRpc2Flag = strictJsonRpc2Flag;
        jsonMapper = new ObjectMapper();
    }

	@Override
	public void encode(RpcResponse rpcResponse, HttpServletResponse httpResponse) throws Exception {

        String output = "";

        if (strictJsonRpc2Flag)
        {
            ObjectNode root = jsonMapper.createObjectNode();
            root.setAll((ObjectNode)jsonMapper.valueToTree(rpcResponse));
            root.put("jsonrpc", "2.0");
            output = root.toString();
        }
        else
        {
            output = jsonMapper.writeValueAsString(rpcResponse);
        }

        httpResponse.addHeader("Content-Length", "" + output.length());
        httpResponse.addHeader("Content-Type", "application/json");
        httpResponse.getWriter().write(output);
	}

}
