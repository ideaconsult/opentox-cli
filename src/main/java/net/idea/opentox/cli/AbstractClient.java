package net.idea.opentox.cli;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import net.idea.opentox.cli.id.IIdentifier;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.opentox.rest.RestException;

public abstract class AbstractClient<IDENTIFIER extends IIdentifier, T extends IIdentifiableResource<IDENTIFIER>> {
	public static final Charset utf8 = Charset.forName("UTF-8");
	protected static final String mime_rdfxml = "application/rdf+xml";
	protected static final String mime_n3 = "text/n3";
	protected static final String mime_uri = "text/uri-list";
	protected static final String mime_csv = "text/csv";
	protected static final String mime_json = "application/json";
	protected static final String mime_xml = "application/xml";

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
		if (httpClient == null)
			throw new IOException("No HttpClient!");
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
	 * HTTP GET with given media type (expects one of RDF flavours).
	 * 
	 * @param url
	 * @param mediaType
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	protected List<T> get(IDENTIFIER url, String mediaType)
			throws RestException, IOException {
		return get(url, mediaType, (String[]) null);
	}

	/**
	 * @param url
	 * @return List of objects
	 * @throws Exception
	 */
	public List<T> get(IDENTIFIER url) throws Exception {
		return get(url, defaultMimeType);
	}
	/**
	 * Use {@link #getURL(IIdentifier, String, String...)}
	 * @param identifier
	 * @param mediaType
	 * @param params
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	@Deprecated
	protected List<T> getByIdentifier(IDENTIFIER identifier, String mediaType,
			String... params) throws RestException, IOException {
		return getURL(identifier, mediaType, params);

	}

	protected abstract List<T> get(IDENTIFIER url, String mediaType,
			String... params) throws RestException, IOException;

	protected List<T> getURL(IDENTIFIER url, String mediaType, String... params)
			throws RestException, IOException, MalformedURLException {
		String address = prepareParams(url, params);
		HttpGet httpGet = new HttpGet(address);
		if (headers != null)
			for (Header header : headers)
				httpGet.addHeader(header);
		httpGet.addHeader("Accept", mediaType);
		httpGet.addHeader("Accept-Charset", "utf-8");

		InputStream in = null;
		try {
			HttpResponse response = getHttpClient().execute(httpGet);
			HttpEntity entity = response.getEntity();
			in = entity.getContent();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return processPayload(in, mediaType);

			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				return Collections.emptyList();
			} else
				throw new RestException(response.getStatusLine()
						.getStatusCode(), response.getStatusLine()
						.getReasonPhrase());

		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception x) {
			}
		}

	}

	public List<T> processPayload(InputStream in, String mediaType)
			throws RestException, IOException {
		throw new RestException(HttpStatus.SC_OK,
				"Everything's fine, but parsing content is not implemented yet "
						+ mediaType);
	}

	protected String prepareParams(IIdentifier id, String... params)
			throws MalformedURLException {

		String address = id.toString();
		if (params != null) {
			StringBuilder b = new StringBuilder();

			String d = id.toURL().getQuery() == null ? "?" : "&";
			for (int i = 0; i < params.length; i += 2) {
				if ((i + 1) >= params.length)
					break;
				b.append(String.format("%s%s=%s", d, params[i],
						URLEncoder.encode(params[i + 1])));
				d = "&";
			}
			address = String.format("%s%s", address, b);
		}
		return address;
	}

}
