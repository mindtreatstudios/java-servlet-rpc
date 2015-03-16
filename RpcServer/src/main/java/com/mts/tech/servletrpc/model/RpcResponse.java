package com.mts.tech.servletrpc.model;

public class RpcResponse {
//	public String jsonrpc = "2.0";
    public Object result;
    public RpcError error;
    public Object id;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " [" + NEW_LINE);
//        result.append(" jsonrpc: " + jsonrpc + NEW_LINE);
        result.append("  result: " + this.result + NEW_LINE);
        result.append("  error: " + error + NEW_LINE);
        result.append("  id: " + id + NEW_LINE );
        result.append("]");

        return result.toString();
    }
}
