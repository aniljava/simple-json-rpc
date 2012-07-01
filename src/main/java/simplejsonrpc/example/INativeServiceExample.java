package simplejsonrpc.example;

import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import simplejsonrpc.server.INative;

public class INativeServiceExample implements INative {
	@Override
	public Object serve(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		writer.write("OK");
		return null;
	}
}
