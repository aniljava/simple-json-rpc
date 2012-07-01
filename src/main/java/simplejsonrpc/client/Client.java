package simplejsonrpc.client;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class Client {
	private String	url;
	public Client(String url) {
		this.url = url;
	}

	public Call service(String name) {
		return new Call(this, name);
	}

	public static DefaultHttpClient		httpclient		= new DefaultHttpClient(new PoolingClientConnectionManager());
	public static final ObjectMapper	OBJECT_MAPPER	= new ObjectMapper();

	public Object invoke(Call call) {
		try {

			final HttpPost post = new HttpPost(url);

			final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			if (call.serviceName != null) {
				formparams.add(new BasicNameValuePair("service", call.serviceName));
			}

			if (call.methodName != null) {
				formparams.add(new BasicNameValuePair("method", call.methodName));
			}

			if (call.arguments != null) {
				final String arg = OBJECT_MAPPER.writeValueAsString(call.arguments);
				formparams.add(new BasicNameValuePair("arguments", arg));
			}

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Charset.defaultCharset().name());
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(entity);

			HttpResponse response = httpclient.execute(post);
			if (response.getStatusLine().getStatusCode() != 200) { throw new Exception(response.getStatusLine().getReasonPhrase()); }
			// TODO based on status
			System.out.println(response.getStatusLine());
			String result = EntityUtils.toString(response.getEntity());

			return OBJECT_MAPPER.readValue(result, Object.class);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}
}
