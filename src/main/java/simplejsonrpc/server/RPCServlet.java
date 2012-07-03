package simplejsonrpc.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

public class RPCServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String serviceName = request.getParameter("service");
		if (serviceName == null) {
			response.sendError(500);
			return;
		}

		Object service = getService(getServletContext(), serviceName);
		if (service == null) {
			response.sendError(500, "Service Not Found");
			return;
		}

		if (service instanceof INative) {
			try {
				Object result = ((INative) service).serve(getServletContext(), request, response);
				if (result != null) {
					writeResult(response, result);
				}
				return;
			} catch (Exception ex) {
				response.sendError(500, "Error executing INative Service");
				return;
			}
		}

		// Not an inative now.
		final String methodName = request.getParameter("method");
		final String arguments = request.getParameter("arguments");

		if (methodName == null) {
			response.sendError(500, "Method Name Missing");
			return;
		}

		Object[] argObjs = null;

		/**
		 * Arguments needs to be any of the following. 1. List containing
		 * arguments 2. Object other than list 3. Null for empty
		 * 
		 * Method with one List argument need to pass argument as List containig
		 * one list.
		 */

		Class[] argTypes = new Class[0]; // Either null or array of atleast one
											// element.

		if (arguments != null) {
			Object obj = getObject(arguments);
			if (obj != null) {
				if (obj instanceof List) {

					ArrayList<Class> argClasses = new ArrayList<Class>();
					ArrayList<Object> argObjects = new ArrayList<Object>();

					if (obj != null && obj instanceof List) {
						List list = (List) obj;
						for (Object p : list) {
							if (p == null) {
								argClasses.add(Object.class);
							} else {
								if(p instanceof List){
									argClasses.add(List.class);
								}else if(p instanceof Map){
									argClasses.add(Map.class);
								}else{
									argClasses.add(p.getClass());	
								}
								
								
								
								
							}

							argObjects.add(p);
						}

						argTypes = argClasses.toArray(new Class[argClasses.size()]);
						argObjs = argObjects.toArray();
					}
				} else {

					if (obj instanceof List) {
						argTypes = new Class[] { List.class };
					} else if (obj instanceof Map) {
						argTypes = new Class[] { Map.class };
					} else {
						argTypes = new Class[] { obj.getClass() };
					}

					argObjs = new Object[] { obj };
				}
			}
		}
		
		
		
		for (Class c : argTypes) {
			System.out.println("ARGS: " + c);
		}
		// Create signatures and put in cache.

		Method method = null;
		boolean longform = false;
		try {
			method = service.getClass().getMethod(methodName, argTypes);
		} catch (Exception ex) {
			// Try long form
			Class[] extended = new Class[argTypes.length + 3];
			extended[0] = ServletContext.class;
			extended[1] = HttpServletRequest.class;
			extended[2] = HttpServletResponse.class;
			System.arraycopy(argTypes, 0, extended, 3, argTypes.length);

			try {
				method = service.getClass().getMethod(methodName, argTypes);
				longform = true;
			} catch (Exception ex1) {
				response.sendError(500, "Problem obtaining specified method");
				return;
			}
		}

		Object result = null;

		if (longform) {
			Object[] extended = new Object[argObjs.length + 3];
			extended[0] = getServletContext();
			extended[1] = request;
			extended[2] = response;

			System.arraycopy(argTypes, 0, argObjs, 3, argObjs.length);
			try {
				result = method.invoke(service, extended);
			} catch (Exception ex) {
				response.sendError(500, "Problem executing long form method");
				return;
			}

		} else {
			try {
				result = method.invoke(service, argObjs);
			} catch (Exception ex) {
				ex.printStackTrace();
				response.sendError(500, "Problem Executing Method");
				return;
			}
		}

		if (result != null) {
			final PrintWriter out = response.getWriter();
			out.write(getJSON(result));
		}

	}

	final Map<String, Object>	commandsCache	= new HashMap<>();

	private Object getService(ServletContext context, String name) {
		Object command = commandsCache.get(name);

		if (command == null) {
			if (commandsCache.containsKey(command)) // Peviously checked.
				return null;

			try {
				Class clazz = Class.forName(name);
				command = clazz.newInstance();

				try {
					Method method = command.getClass().getMethod("init", ServletContext.class);
					method.invoke(command, context);
				} catch (Exception ex) {}

				commandsCache.put(name, command);

			} catch (Exception e) {
				commandsCache.put(name, null);
				return null;
			}
		}

		return command;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public static void writeResult(HttpServletResponse response, Object... kvs) throws IOException {
		final PrintWriter out = response.getWriter();
		final Map result = new HashMap();

		Object key = null;
		Object value = null;
		for (Object obj : kvs) {
			if (key == null) {
				key = obj;
			} else {
				value = obj;
				result.put(key, value);
				key = null;
				value = null;
			}
		}

		out.write(getJSON(result));
	}

	public static final ObjectMapper	OBJECT_MAPPER	= new ObjectMapper();

	public static String getJSON(Object result) {
		try {
			return OBJECT_MAPPER.writeValueAsString(result);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Object getObject(String data) {
		try {
			return OBJECT_MAPPER.readValue(data, Object.class);
		} catch (Exception ex) {
			return null;
		}
	}

}
