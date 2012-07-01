package simplejsonrpc.client;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

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

	public Call args(Object... obj) {
		this.arguments = obj;
		return this;
	}

	public <T> T invoke(Object... obj) throws JsonParseException, JsonMappingException, IOException {
		this.arguments = obj;
		InputStream in = client.makePostRequest(this);
		return (T) Client.OBJECT_MAPPER.readValue(in, Object.class);
	}

	public <T> T invoke() throws JsonParseException, JsonMappingException, IOException {
		InputStream in = client.makePostRequest(this);
		return (T) Client.OBJECT_MAPPER.readValue(in, Object.class);
	}

	public <T> T invoke(Class<T> valueType, Object... objs) throws JsonParseException, JsonMappingException, IOException {
		this.arguments = objs;
		InputStream in = client.makePostRequest(this);
		return (T) Client.OBJECT_MAPPER.readValue(in, valueType);
	}

	public <T> T invoke(Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		InputStream in = client.makePostRequest(this);
		return (T) Client.OBJECT_MAPPER.readValue(in, valueType);
	}
	
	public InputStream invokeForStream() throws IOException{
		return client.makePostRequest(this);
	}

}
