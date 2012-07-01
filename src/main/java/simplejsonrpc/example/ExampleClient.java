package simplejsonrpc.example;

import java.io.DataInputStream;
import java.io.InputStream;

import simplejsonrpc.client.Client;

public class ExampleClient {
	/**
	 * Used as a clinet as well.
	 */
	public static void main(String[] args) throws Exception {

		System.out.println("Usage : java -jar <REMOTE URL> <WORD TO REVERSE>");

		String url = "http://localhost:8080/api";
		String arg = "Example input";

		if (args.length > 0) url = args[0];
		if (args.length > 1) arg = args[1];

		System.out.println("Sample client executing: Server at : " + url);

		Client client = new Client(url);
		String reversed = client.service("simplejsonrpc.example.ExampleService").fn("reverse").invoke(arg);
		System.out.println(reversed);
		
		
		System.out.println("Testing INative");
		
		InputStream in = client.service("simplejsonrpc.example.INativeServiceExample").invokeForStream();
		DataInputStream din = new DataInputStream(in);
		byte data[] = new byte[1024];
		din.readFully(data);
		
		System.out.println(new String(data));
	}
}
