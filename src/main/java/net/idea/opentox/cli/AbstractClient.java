package net.idea.opentox.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.idea.opentox.cli.task.RemoteTask;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.opentox.aa.opensso.AAServicesConfig;
import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.rest.RestException;

/**
 * An abstract client, implementing HTTP GET, PUT, POST and DELETE.
 * @author nina
 *
 * @param <T>
 */
public class AbstractClient<T extends IIdentifiableResource<URL>,POLICY_RULE> {
	public static final Charset utf8 = Charset.forName("UTF-8");
	protected static final String mime_rdfxml = "application/rdf+xml";
	protected static final String mime_n3 = "text/n3";
	protected static final String mime_uri = "text/uri-list";
	protected static final String mime_csv = "text/csv";
	protected static final String mime_json = "application/json";
	
	protected static final String search_param = "search";
	protected static final String b64search_param = "b64search";
	protected static final String modified_param = "modifiedSince";
	protected String defaultMimeType = mime_uri;
	protected HttpClient httpClient;
	protected Header[] headers = null;
	
	public Header[] getHeaders() {
		return headers;
	}
	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}
	public HttpClient getHttpClient() throws IOException {
		if (httpClient==null) throw new IOException("No HttpClient!"); 
			//setHttpClient(new DefaultHttpClient());
		return httpClient;
	}
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	
	public AbstractClient(HttpClient httpclient) {
		super();
		setHttpClient(httpclient);
	}
	
	public AbstractClient() {
		this(null);
	}
	
	/**
	 * Same as {@link #getRDF_XML(URL)}
	 * @param url
	 * @return List of objects
	 * @throws Exception
	 */
	public List<T> get(URL url) throws Exception {
		return get(url,defaultMimeType);
	}
	/**
	 * HTTP GET with "Accept:application/rdf+xml".  Parses the RDF and creates list of objects.
	 * @param url
	 * @return
	 * @throws Exception
	 */
	
	public List<T> getRDF_XML(URL url) throws Exception {
		return get(url,mime_rdfxml);
	}
	/**
	 * 
	 * @param url
	 * @param query Search parameter
	 * @return
	 * @throws Exception
	 */
	public List<T> searchRDF_XML(URL url,String query) throws Exception {
		return get(url,mime_rdfxml,query==null?null:new String[] {search_param,query});
	}	
	/**
	 * HTTP GET with "Accept:text/n3".  Parses the RDF and creates list of objects.
	 * @param url
	 * @return
	 * @throws Exception
	 */
	protected List<T> getRDF_N3(URL url) throws Exception {
		return get(url,mime_n3);
	}	
	/**
	 * 
	 * @param url
	 * @param query Search parameter
	 * @return
	 * @throws Exception
	 */
	protected List<T> searchRDF_N3(URL url,String query) throws Exception {
		return get(url,mime_n3,query==null?null:new String[] {search_param,query});
	}		
	/**
	 * HTTP GET with given media type (expects one of RDF flavours). 
	 * @param url
	 * @param mediaType
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	protected List<T> get(URL url,String mediaType) throws RestException, IOException {
		return get(url,mediaType,(String[]) null);
	}
	/**
	 * 
	 * @param url
	 * @param mediaType
	 * @param params name/value pairs, sent as URI parameters
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	protected List<T> get(URL url,String mediaType,String... params) throws RestException, IOException {
		String address = prepareParams(url, params);
		HttpGet httpGet = new HttpGet(address);
		if (headers!=null) for (Header header : headers) httpGet.addHeader(header);
		httpGet.addHeader("Accept",mediaType);
		httpGet.addHeader("Accept-Charset", "utf-8");

		InputStream in = null;
		try {
			HttpResponse response = getHttpClient().execute(httpGet);
			HttpEntity entity  = response.getEntity();
			in = entity.getContent();
			if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
				/*
				Model model = ModelFactory.createDefaultModel();
				model.read(new InputStreamReader(in,"UTF-8"),OpenTox.URI);
				return getIOClass().fromJena(model);
				*/
				return processPayload(in,mediaType);

			} else if (response.getStatusLine().getStatusCode()== HttpStatus.SC_NOT_FOUND) {	
				return Collections.emptyList();
			} else throw new RestException(response.getStatusLine().getStatusCode(),response.getStatusLine().getReasonPhrase());
		
		} finally {
			try {if (in != null) in.close();} catch (Exception x) {}
		}
	
	}
	
	protected List<T>  processPayload(InputStream in, String mediaType) throws RestException, IOException {
		throw new RestException(HttpStatus.SC_OK,"Everything's fine, but parsing content is not implemented yet "+mediaType);
	}
	
	private String prepareParams(URL url,String... params) {
		String address = url.toString();
		if (params != null) {
			StringBuilder b = new StringBuilder();
			String d = url.getQuery()==null?"?":"&";
			for (int i=0; i < params.length; i+=2) {
				if ((i+1)>=params.length) break;
				b.append(String.format("%s%s=%s",d,params[i],URLEncoder.encode(params[i+1])));
				d = "&";
			}
			address = String.format("%s%s", address,b);
		}
		return address;
	}
	/**
	 * HTTP GET with "Accept:text/uri-list". 
	 * If the resource is a container, will return list URIs of contained resources.
	 * Otherwise, will return the URI of the object itself (for consistency).
	 * @param url
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> listURI(URL url) throws  RestException, IOException {
		return listURI(url,(String[])null);
	}
	/**
	 * 
	 * @param url
	 * @param query Search param
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> searchURI(URL url,String query) throws  RestException, IOException {
		return listURI(url, new String[] {search_param,query});
	}
	/**
	 * 
	 * @param url
	 * @param params  name/value pairs, sent as URI parameters
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> listURI(URL url,String... params) throws  RestException, IOException {
		HttpGet httpGet = new HttpGet(prepareParams(url, params));
		if (headers!=null) for (Header header : headers) httpGet.addHeader(header);
		httpGet.addHeader("Accept","text/uri-list");

		InputStream in = null;
		try {
			HttpResponse response = getHttpClient().execute(httpGet);
			HttpEntity entity  = response.getEntity();
			in = entity.getContent();
			if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
				return readURI(in);
			} else if (response.getStatusLine().getStatusCode()== HttpStatus.SC_NOT_FOUND) {	
				return Collections.emptyList();				
			} else throw new RestException(response.getStatusLine().getStatusCode(),response.getStatusLine().getReasonPhrase());

		} finally {
			try {if (in !=null) in.close();} catch (Exception x) {}
		}		
	}
	/**
	 * HTTP GET with "Accept:text/uri-list". 
	 * If the resource is a container, will return list URIs of contained resources.
	 * Otherwise, will return the URI of the object itself (for consistency). 
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private List<URL> readURI(InputStream in) throws IOException, MalformedURLException {
		List<URL> uris = new ArrayList<URL>();
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = r.readLine())!= null) {
			uris.add(new URL(line));
		}
		return uris;
	}	
	
	/**
	 * same as {@link #postAsync(IIdentifiableResource, URL, List)}, but let the server to decide on policies
	 * @param object
	 * @param collection
	 * @return
	 * @throws Exception
	 */
	//public RemoteTask postAsync(T object, URL collection) throws Exception {
	//	return postAsync(object, collection,null);
	//}	
	/**
	 * HTTP POST to create a new object. Asynchronous.
	 * @param object
	 * @param collection  The URL of resource collection, e.g. /protocol or /user .
     * @param accessRights
	 * The new object will be added to the collection of resources.
	 * @return  Returns {@link RemoteTask}
	 * @throws Exception if not allowed, or other error condition
	 */	
	public RemoteTask postAsync(T object, URL collection, List<POLICY_RULE> accessRights) throws Exception {
		return sendAsync(collection, createPOSTEntity(object,accessRights), HttpPost.METHOD_NAME);
	}
	/**
	 * Same as {@link #postAsync(IIdentifiableResource, URL, null)} 
	 * @param object
	 * @param collection
	 * @return
	 * @throws Exception
	 */
	public RemoteTask postAsync(T object, URL collection) throws Exception {
		return postAsync(object, collection, null);
	}
	/**
	 * HTTP PUT to update an existing object. Asynchronous.
	 * @param object
	 * @return {@link RemoteTask}
	 * @throws Exception if not allowed, or other error condition
	 */
	public RemoteTask putAsync(T object, List<POLICY_RULE> accessRights) throws Exception {
		return sendAsync(object.getResourceIdentifier(), createPUTEntity(object,accessRights), HttpPut.METHOD_NAME);
	}
	/**
	 * Same as {@link #putAsync(IIdentifiableResource, URL, null)} 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public RemoteTask putAsync(T object) throws Exception {
		return putAsync(object,null);
	}
	/**
	 * HTTP DELETE to remove an existing object. Asynchronous.
	 * @return {@link RemoteTask}
	 * @throws Exception if not allowed, or other error condition 
	 */
	protected RemoteTask deleteAsync(T object) throws Exception {
		return deleteAsync(object.getResourceIdentifier());
	}	
	/**
	 * The same as {@link #deleteAsync(IIdentifiableResource)}, but accepts an URL.
	 * @param url
	 * @return {@link RemoteTask}
	 * @throws Exception
	 */
	protected RemoteTask deleteAsync(URL url) throws Exception {
		return sendAsync(url,null, HttpDelete.METHOD_NAME);
	}	
	protected RemoteTask sendAsync(URL target, HttpEntity entity, String method) throws Exception {
		return new RemoteTask(getHttpClient(),target, "text/uri-list", entity, method);
	}	

	/**
	 * 
	 * @param object The object to be created
	 * @return {@link HttpEntity}
	 * @throws Exception
	 */
	protected HttpEntity createPOSTEntity(T object,List<POLICY_RULE> accessRights) throws InvalidInputException,Exception {
		throw new Exception("Not implemented");
	}
	/**
	 * 
	 * @param object the object to be updated
	 * @return
	 * @throws Exception
	 */
	protected HttpEntity createPUTEntity(T object, List<POLICY_RULE> accessRights) throws InvalidInputException,Exception {
		throw new Exception("Not implemented");
	}
	/**
	 * Creates a new object. Waits until the asynchronous tasks completes.
	 * @param object
	 * @param collection
	 * @param accessRights. {@link PolicyRule} Can be null.
	 * @return
	 * @throws Exception in case of error.
	 */
	public T post(T object, URL collection, List<POLICY_RULE> accessRights) throws Exception {
		RemoteTask task = postAsync(object, collection,accessRights);
		task.waitUntilCompleted(500);	
		if (task.isERROR()) throw task.getError();
		else object.setResourceIdentifier(task.getResult());
		return object;
	}
	/**
	 * Same as {@link #post(IIdentifiableResource, URL, null)} 
	 * @param object
	 * @param collection
	 * @return
	 * @throws Exception
	 */
	public T post(T object, URL collection) throws Exception {
		return post(object,collection,null);
	}
	/**
	 * Updates an existing object. Waits until the asynchronous tasks completes.
	 * @param object
	 * @param collection
	 * @param accessRights. {@link PolicyRule} Can be null.
	 * @return
	 * @throws Exception
	 */
	public T put(T object, List<POLICY_RULE> accessRights) throws Exception {
		RemoteTask task = putAsync(object,accessRights);
		task.waitUntilCompleted(500);	
		if (task.isERROR()) throw task.getError();
		else object.setResourceIdentifier(task.getResult());
		return object;
	}	
	
	/**
	 * Synchronous delete
	 * @param url
	 * @throws Exception
	 */
	public void delete(URL url) throws Exception {
		RemoteTask task = deleteAsync(url);
		task.waitUntilCompleted(500);
		if (task.isERROR()) throw task.getError();
	}
	/**
	 * 
	 * @param object
	 * @throws UniformInterfaceException
	 * @throws URISyntaxException
	 */
	public void delete(T object) throws Exception {
		delete(object.getResourceIdentifier());
	}
	
	public static OpenSSOToken login(String username,String password) throws Exception {
		OpenSSOToken token = new OpenSSOToken(AAServicesConfig.getSingleton().getOpenSSOService());
		if (token.login(username,password)) {
			//AAClient.setTokenFactory();
		}
		return token;
		
	}
	
	
}
