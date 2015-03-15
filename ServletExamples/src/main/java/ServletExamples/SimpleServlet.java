package ServletExamples;

import ServletExamples.api.Test;
import com.mts.tech.jsonrpc2.RpcDecoder;
import com.mts.tech.jsonrpc2.RpcEncoder;
import com.mts.tech.jsonrpc2.RpcExecutor;
import com.mts.tech.jsonrpc2.RpcServer;
import com.mts.tech.jsonrpc2.impl.DecodeCodecGet;
import com.mts.tech.jsonrpc2.impl.DecodeCodecJson;
import com.mts.tech.jsonrpc2.impl.DecodeCodecMultipart;
import com.mts.tech.jsonrpc2.impl.EncodeCodecJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class EntryPoint
 */
@WebServlet("/SimpleServlet")
@MultipartConfig(location=".", fileSizeThreshold=1024*1024,maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
public class SimpleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private RpcServer rpcServer;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SimpleServlet() throws Exception {
        super();

        RpcDecoder decoder = new RpcDecoder();
        decoder.addCodec("GET", null, new DecodeCodecGet());
        decoder.addCodec("POST", "application/json", new DecodeCodecJson());
        decoder.addCodec("POST", "multipart/form-data", new DecodeCodecMultipart());

        RpcExecutor executor = new RpcExecutor();
        executor.addService("Test", new Test());

        RpcEncoder encoder = new RpcEncoder(new EncodeCodecJson());

        rpcServer = new RpcServer(decoder, executor, encoder);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		try {
			rpcServer.handle(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			rpcServer.handle(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

}
