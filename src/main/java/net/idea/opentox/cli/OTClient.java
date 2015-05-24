package net.idea.opentox.cli;

import java.net.URL;
import java.util.Hashtable;

import net.idea.opentox.cli.dataset.DatasetClient;
import net.idea.opentox.cli.feature.FeatureClient;
import net.idea.opentox.cli.model.ModelClient;
import net.idea.opentox.cli.structure.CompoundClient;
import net.idea.opentox.cli.structure.SubstanceClient;

import org.apache.http.HttpRequest;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;

/**
 * Top level OpenTox API client.
 * @author nina
 *
 */
public class OTClient extends ApplicationClient<OpenSSOToken> {
	

	public OTClient(OpenSSOToken ssoToken) {
		super(ssoToken);
	}
	public OTClient() {
		super();
	}
	
	@Override
	protected void token2header(HttpRequest request, OpenSSOToken token) {
		if (ssoToken != null)
			request.addHeader("subjectid",ssoToken.getToken());

	}
	@Override
	public boolean login(String username,String password) throws Exception {
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
	@Override
	public void logout() throws Exception {
		if (ssoToken!=null) ssoToken.logout();
	}

	
	/**
	 * @return
	 */
	public SubstanceClient getSubstanceClient() {
		return new SubstanceClient(getHttpClient());
	}
	
	public CompoundClient getCompoundClient() {
		return new CompoundClient(getHttpClient());
	}
	
	public DatasetClient getDatasetClient() {
		return new DatasetClient(getHttpClient());
	}
	
	public FeatureClient getFeatureClient() {
		return new FeatureClient(getHttpClient());
	}
	
	public ModelClient getModelClient() {
		return new ModelClient(getHttpClient());
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

}
