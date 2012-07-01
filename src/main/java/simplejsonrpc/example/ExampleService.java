package simplejsonrpc.example;


public class ExampleService {

	/**
	 * Simple service method.
	 */
	public String reverse(String str) {
		return new StringBuffer(str).reverse().toString();
	}

}
