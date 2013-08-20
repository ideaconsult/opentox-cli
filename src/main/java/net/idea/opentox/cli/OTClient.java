package net.idea.opentox.cli;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import net.idea.opentox.cli.dataset.DatasetClient;
import net.idea.opentox.cli.structure.CompoundClient;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;

/**
 * Top level OpenTox API client.
 * @author nina
 *
 */
public class OTClient {
	protected HttpClient httpClient;
	protected OpenSSOToken ssoToken;
	
	public OTClient(OpenSSOToken ssoToken) {
		super();
		this.ssoToken = ssoToken;
	}
	public OTClient() {
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
				if (ssoToken != null)
					request.addHeader("subjectid",ssoToken.getToken());
			}
		});
		return cli;
	}
	
	public boolean login(String username,String password) throws Exception {
		//get this from client.properties file
		return login("http://opensso.in-silico.ch/opensso/identity",username,password);
	}
	/**
	 * Login
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean login(String opensso_server,String username,String password) throws Exception {
		if (ssoToken==null) ssoToken = new OpenSSOToken(opensso_server);
		//AAServicesConfig.getSingleton().getOpenSSOService());
		return ssoToken.login(username,password);
	}
	/**
	 * Logout
	 * @throws Exception
	 */
	public void logout() throws Exception {
		if (ssoToken!=null) ssoToken.logout();
	}
	
	
	public void close() throws Exception {
		if (httpClient !=null) {
			httpClient.getConnectionManager().shutdown();
			httpClient = null;
		}
	}
	
	/**
	 * @deprecated Use getCompoundClient()
	 * @return
	 */
	public CompoundClient getSubstanceClient() {
		return new CompoundClient(getHttpClient());
	}
	
	public CompoundClient getCompoundClient() {
		return new CompoundClient(getHttpClient());
	}
	
	public DatasetClient getDatasetClient() {
		return new DatasetClient(getHttpClient());
	}
	
	
	/**
	 *  Returns true if authorized
	 * @param uri
	 * @param httpAction
	 * @return
	 * @throws Exception
	 */
	public boolean authorize(URL uri, String httpAction) throws Exception {
		return ssoToken.authorize(uri.toString(), httpAction);
	}
	
	/**
	 * Returns true if post is allowed
	 * @param protocolURI  expects  "http://host/protocol"
	 * @return
	 * @throws Exception
	 */
	public boolean isProtocolUploadAllowed(URL protocolURI) throws Exception {
		return authorize(protocolURI, "POST");
	}
	
	public Hashtable<String, String> getUserAttributes() throws Exception {
		Hashtable<String, String> results = new Hashtable<String, String>();
		ssoToken.getAttributes(null,results);
		return results;
	}
	
	protected static OpenSSOPolicy getOpenSSOPolicyInstance() throws Exception {
		//TODO get form config
		return new OpenSSOPolicy("http://opensso.in-silico.ch/Pol/opensso-pol");
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
}
