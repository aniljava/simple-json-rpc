simple JSON RPC using Servlet and POJO.
--------------------------------------------

#### Serverside
Server side consists of a Servlet accepting a call at an uri. Services are plain java object without any framework dependencies. They can optionally make use of
Servlet Context, Request and Response objects.

#### Clientside
Plain java or curl/wget to uri with parameters. A Helper class "Client" is available that uses HTTP Components to make connections to remote servers.


#### Limitations
- No Exceptions over RPC
- POJO can only have primitive arguments on service methods
- No inbuilt authentication, use Servlet specification instead.


#### Example
	
	--- Service	
	public class Calculator{
		public int add(int i, int j){
			return i+j;
		}
		
		//Example with different return type
		public String upperCase(String str){
			return str.toUpperCase();
		}
			
	}
	
	--- Client	
	Client client = new Client("http://SERVICE_URL");
	
	int result = client.call("Calculator").fn("add").invoke(2,3);
	String result1 = client.call("Calculator").fn("upperCase").invoke("example text");
	

	--- Configuration, before client can make a call you need a running servlet container at SERVICE_URL with RPCServlet running.
	
	<servlet>
		<servlet-name>api</servlet-name>
		<servlet-class>simplejsonrpc.server.RPCServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>api</servlet-name>
		<url-pattern>/api</url-pattern>
	</servlet-mapping>



#### NOTES
- See pom.xml for dependencies.

#### TODO
- List as argument
- Prefix based filtering of services, currently it executes anything in the classpath.
- Better documentation of the archetecture