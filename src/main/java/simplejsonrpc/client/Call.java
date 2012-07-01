package simplejsonrpc.client;

public class Call {
	public String	serviceName;
	public String	methodName;
	public Object	arguments;

	public Client	client;
	public Call(Client client, String serviceName) {
		this.client = client;
		this.serviceName = serviceName;
	}
	public Call fn(String name) {
		this.methodName = name;
		return this;
	}

	public <T> T invoke(Object... obj) {
		this.arguments = obj;
		return (T) client.invoke(this);
	}

	public <T> T invoke() {
		return (T) client.invoke(this);
	}

}
