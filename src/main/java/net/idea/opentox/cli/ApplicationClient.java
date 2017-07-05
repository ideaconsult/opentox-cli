package net.idea.opentox.cli;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public abstract class ApplicationClient<TOKEN> {

	protected HttpClient httpClient;
	protected TOKEN ssoToken;

	public ApplicationClient(TOKEN ssoToken) {
		super();
		this.ssoToken = ssoToken;
	}
	public ApplicationClient() {
		this(null);
		httpClient = createHTTPClient();
	}
	public HttpClient getHttpClient() {
		if (httpClient==null) httpClient = createHTTPClient();
		return httpClient;
	}

	protected HttpClient createHTTPClient() {
		HttpClient cli = new DefaultHttpClient();
		
		((DefaultHttpClient)cli).addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				token2header(request,ssoToken);
			}
		});
		return cli;
	}
	
	
	public void close() throws Exception {
		if (httpClient !=null) {
			httpClient.getConnectionManager().shutdown();
			httpClient = null;
		}
	}	
	/**
	 * Use this to authenticate with HTTP BASIC instead of OpenSSO
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public void setHTTPBasicCredentials(String host, int port, String username, String password) {
		setHTTPBasicCredentials(new AuthScope(host,port),new UsernamePasswordCredentials(username,password));
	}
	/**
	 * Use this to authenticate with HTTP BASIC instead of OpenSSO
	 * @param authScope
	 * @param credentials
	 */
	public void setHTTPBasicCredentials(AuthScope authScope,UsernamePasswordCredentials credentials) {
		((DefaultHttpClient)getHttpClient()).getCredentialsProvider().setCredentials(
				authScope,
                credentials);
	}	
	protected abstract void token2header(HttpRequest request,TOKEN token);
	public abstract boolean login(String username,String password) throws Exception;
	public abstract void logout() throws Exception;
}
