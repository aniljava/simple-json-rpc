package simplejsonrpc.server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Special class of services that needs access to servlet container and
 * request/response.
 * 
 * @author root
 * 
 */
public interface INative {
	public Object serve(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
