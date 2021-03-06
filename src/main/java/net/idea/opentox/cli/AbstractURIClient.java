package net.idea.opentox.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import net.idea.opentox.cli.id.IIdentifier;
import net.idea.opentox.cli.id.Identifier;
import net.idea.opentox.cli.task.RemoteTask;
import net.idea.opentox.main.IJSONCallBack;

/**
 * An abstract client, implementing HTTP GET, PUT, POST and DELETE.
 * @author nina
 *
 * @param <T>
 */
public class AbstractURIClient<T extends IIdentifiableResource<IIdentifier>,POLICY_RULE> extends AbstractClient<IIdentifier,T> {
	protected IJSONCallBack callback = null;
	public IJSONCallBack getCallback() {
		return callback;
	}

	public void setCallback(IJSONCallBack callback) {
		this.callback = callback;
	}
	protected final static Logger LOGGER = Logger.getLogger(AbstractURIClient.class.getName());
	public AbstractURIClient(HttpClient httpclient) {
		super();
		setHttpClient(httpclient);
	}
	
	public AbstractURIClient() {
		this(null);
	}
	

	/**
	 * HTTP GET with "Accept:application/rdf+xml".  Parses the RDF and creates list of objects.
	 * @param url
	 * @return
	 * @throws Exception
	 */
	
	public List<T> getRDF_XML(IIdentifier url) throws Exception {
		return get(url,mime_rdfxml);
	}
	
	
	public List<T> getJSON(IIdentifier url) throws Exception {
		return get(url,mime_json);
	}
	/**
	 * 
	 * @param url
	 * @param query Search parameter
	 * @return
	 * @throws Exception
	 */
	public List<T> searchRDF_XML(IIdentifier url,String query) throws Exception {
		return get(url,mime_rdfxml,query==null?null:new String[] {search_param,query});
	}	
	/**
	 * HTTP GET with "Accept:text/n3".  Parses the RDF and creates list of objects.
	 * @param url
	 * @return
	 * @throws Exception
	 */
	protected List<T> getRDF_N3(IIdentifier url) throws Exception {
		return get(url,mime_n3);
	}	
	/**
	 * 
	 * @param url
	 * @param query Search parameter
	 * @return
	 * @throws Exception
	 */
	protected List<T> searchRDF_N3(IIdentifier url,String query) throws Exception {
		return get(url,mime_n3,query==null?null:new String[] {search_param,query});
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
	public List<T> get(IIdentifier url,String mediaType,String... params) throws RestException, IOException {
		String address = prepareParams(url, params);
		HttpGet httpGet = new HttpGet(address);
		if (headers!=null) for (Header header : headers) httpGet.addHeader(header);
		httpGet.addHeader("Accept",mediaType);
		httpGet.addHeader("Accept-Charset", "utf-8");
		LOGGER.log(Level.INFO,String.format("curl -H \"Accept:%s\" -X GET \"%s\"",mediaType, httpGet.getURI().toString()));
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
				try {
					return processPayload(in,mediaType);
				} catch (RestException x) {
					throw new RestException(x.getStatus(), String.format("Error retrieving %s",url),x);
				} catch (Exception x) {
					throw new IOException(String.format("Error retrieving",url),x);
				}

			} else if (response.getStatusLine().getStatusCode()== HttpStatus.SC_NOT_FOUND) {	
				return Collections.emptyList();
			} else throw new RestException(response.getStatusLine().getStatusCode(),response.getStatusLine().getReasonPhrase());
		
		} finally {
			try {if (in != null) in.close();} catch (Exception x) {}
		}
	
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
	public List<IIdentifier> listURI(IIdentifier url) throws  RestException, IOException {
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
	public List<IIdentifier> searchURI(IIdentifier url,String query) throws  RestException, IOException {
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
	public List<IIdentifier> listURI(IIdentifier url,String... params) throws  RestException, IOException {
		HttpGet httpGet = new HttpGet(prepareParams(url, params));
		if (headers!=null) for (Header header : headers) httpGet.addHeader(header);
		httpGet.addHeader("Accept","text/uri-list");
		LOGGER.log(Level.INFO,String.format("curl -H \"Accept:%s\" -X GET \"%s\"","text/uri-list", httpGet.getURI().toString()));
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
	private List<IIdentifier> readURI(InputStream in) throws IOException, MalformedURLException {
		List<IIdentifier> uris = new ArrayList<IIdentifier>();
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = r.readLine())!= null) {
			uris.add(new Identifier(line));
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
	public RemoteTask postAsync(T object, IIdentifier collection, List<POLICY_RULE> accessRights) throws Exception {
		return sendAsync(collection, createPOSTEntity(object,accessRights), HttpPost.METHOD_NAME);
	}
	/**
	 * Same as {@link #postAsync(IIdentifiableResource, URL, null)} 
	 * @param object
	 * @param collection
	 * @return
	 * @throws Exception
	 */
	public RemoteTask postAsync(T object, IIdentifier collection) throws Exception {
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
	protected RemoteTask deleteAsync(IIdentifier url) throws Exception {
		return sendAsync(url,null, HttpDelete.METHOD_NAME);
	}	
	protected RemoteTask sendAsync(IIdentifier target, HttpEntity entity, String method) throws Exception {
		return new RemoteTask(getHttpClient(),new URL(target.toString()), "text/uri-list", entity, method);
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
	public T post(T object, IIdentifier collection, List<POLICY_RULE> accessRights) throws Exception {
		RemoteTask task = postAsync(object, collection,accessRights);
		task.waitUntilCompleted(500);	
		if (task.isERROR()) throw task.getError();
		else object.setResourceIdentifier(new Identifier(task.getResult()));
		return object;
	}
	/**
	 * Same as {@link #post(IIdentifiableResource, URL, null)} 
	 * @param object
	 * @param collection
	 * @return
	 * @throws Exception
	 */
	public T post(T object, IIdentifier collection) throws Exception {
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
		else object.setResourceIdentifier(new Identifier(task.getResult()));
		return object;
	}	
	
	/**
	 * Synchronous delete
	 * @param url
	 * @throws Exception
	 */
	public void delete(IIdentifier url) throws Exception {
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
		return loginOpenAM(username,password);
	}
	public static OpenSSOToken loginOpenAM(String username,String password) throws Exception {
		OpenSSOToken token = new OpenSSOToken(AAServicesConfig.getSingleton().getOpenSSOService());
		if (token.login(username,password)) {
			//AAClient.setTokenFactory();
		}
		return token;
		
	}
	
	
}
