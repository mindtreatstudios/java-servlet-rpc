package ServletExamples.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mts.tech.jsonrpc2.model.RpcException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Test {

	public boolean TestConnection()
	{
		return true;
	}
	
	public String Reflection(ArrayNode params)
	{
		String result = "";
		
		for (JsonNode obj : params)
			result += obj.asText()+" ";
		
		return result;
	}
	
	public void CustomOutput(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String output = "void method with custom output";
        response.addHeader("Content-Length", ""+output.length());
        response.addHeader("Content-Type", "text/plain");
        response.getWriter().write(output);
	}	
	
	public String Error(ObjectNode params) throws RpcException
	{
		throw new RpcException(-1, params.get("message").asText());
	}
}
