package com.mts.tech.servletrpc;

import com.mts.tech.servletrpc.model.RpcError;
import com.mts.tech.servletrpc.model.RpcErrorCodes;
import com.mts.tech.servletrpc.model.RpcException;
import com.mts.tech.servletrpc.model.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by reyda_000 on 3/15/2015.
 */
public class RpcDecoder {
    static final Logger Log = LoggerFactory.getLogger(RpcDecoder.class);


    // Codec management
    protected HashMap<String, IRpcDecodeCodec> codecs = new HashMap<String, IRpcDecodeCodec>();

    public void addCodec(String httpMethod, String httpContentType, IRpcDecodeCodec codec)
    {
        codecs.put(hashForMethodAndContentType(httpMethod, httpContentType), codec);
    }
    public void removeCodec(String httpMethod, String httpContentType)
    {
        codecs.remove(hashForMethodAndContentType(httpMethod, httpContentType));
    }
    public IRpcDecodeCodec getCodec(String httpMethod, String httpContentType)
    {
        return codecs.get(hashForMethodAndContentType(httpMethod, httpContentType));
    }

    // Request decoding
    public RpcRequest decode(HttpServletRequest httpRequest) throws Exception
    {
        if (httpRequest == null)
            return null;

        return getCodecForRequest(httpRequest).decode(httpRequest);
    }

    // Utility
    private IRpcDecodeCodec getCodecForRequest(HttpServletRequest httpRequest) throws Exception
    {
        String method = httpRequest.getMethod();
        String contentType = httpRequest.getHeader("Content-Type");
        if(contentType != null)
        {
            int index = contentType.indexOf(";");
            if (index >= 0)
                contentType = contentType.substring(0, index);
        }

        IRpcDecodeCodec codec = getCodec(method, contentType);
        if (codec == null)
            throw new RpcException(RpcErrorCodes.PARSE_ERROR, "Missing codec for request "+requestHeadersToString(httpRequest));

        return codec;
    }

    private String hashForMethodAndContentType(String httpMethod, String contentType)
    {
        return httpMethod+"/"+contentType;
    }

    private String requestHeadersToString(HttpServletRequest httpRequest)
    {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements())
        {
            String headerName = headerNames.nextElement();

            Enumeration<String> headerValues = httpRequest.getHeaders(headerName);
            while (headerValues != null && headerValues.hasMoreElements())
            {
                String headerValue = headerValues.nextElement();
                result.append(headerName+":"+headerValue+NEW_LINE);
            }
        }

        return result.toString();
    }
}
