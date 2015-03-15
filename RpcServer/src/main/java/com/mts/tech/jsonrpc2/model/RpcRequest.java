package com.mts.tech.jsonrpc2.model;

import com.fasterxml.jackson.databind.JsonNode;


public class RpcRequest
{
    public String methodNamespace;
	public String methodName;
    public JsonNode params;
    public Object id;

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append(" Method namespace: " + methodNamespace + NEW_LINE);
        result.append(" Method name: " + methodName + NEW_LINE);
        result.append(" Params: " + params + NEW_LINE);
        result.append(" Id: " + id + NEW_LINE );
        result.append("}");

        return result.toString();
    }
}
