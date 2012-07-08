Simple JSON / Servlet RPC
------------------------------------------------------------------------------

Simple Servlet, JSON and Java Reflection based RPC Mechanism. This project provides
dispatcher servlet and helper api to call remote services.


SERVER SIDE
------------------------------------------------------------------------------

At the server side of the RPC, A servlet listens to a path usually `host/api`

Example configuration on web.xml is 

	<servlet>
		<servlet-name>api</servlet-name>
		<servlet-class>simplejsonrpc.server.RPCServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>api</servlet-name>
		<url-pattern>/api</url-pattern>
	</servlet-mapping>

`RPCServlet takes three parameters, [1. service , 2.method, 3.arguments ]

`service` is the full class name of the service, it needs to be in same class
path as that of RPCServlet, usually deployed as a jar file in `WEB-INF/lib`
folder. It is the only required parameter.

If any class implementing INative exists, it will be initiated(Only once in
lifetime) and it's serve method is called.

If not, if class with name passed in `service` is looked and loaded if exists.
Appropriate method is looked and called according to the method and parameters
values.

#### Example Service
	package example;

	public class Calculator{
		public int add(int i, int j){
			return i+j;
	}

Calculator.class needs to be present in webapp classpath.


CLIENT SIDE
------------------------------------------------------------------------------

Plain java or curl/wget to uri with parameters. A Helper class "Client" is
available that uses HTTP Components to make connections to remote servers.

	Client client = new Client("http://SERVICE_URL");	
	int result = client.call("example.Calculator").fn("add").invoke(2,3);
	
Same thing can be executed as
	http://SERVICE_URL?service=example.Calculator&method=add&arguments=[1,2]

See `INative`, `simplejsonrpc.example` package


for example, if add method is not found on above example, method with
following signature is searched.

	public int add(ServletContext sc, HttpServletRequest req, HttpServletResponse res)




NOTES, TODO and LIMITATIONS
------------------------------------------------------------------------------
- No Exceptions over RPC
- POJO can only have primitive arguments on service methods
- No inbuilt authentication, use Servlet specification instead.
- pom.xml has list of depenencies
- @TODO List as Argument
- @TODO Prefix based filtering of services, currently exposes entire classpath
