package net.idea.opentox.cli.structure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.idea.opentox.cli.AbstractClient;
import net.idea.opentox.cli.InvalidInputException;
import net.idea.opentox.cli.task.RemoteTask;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.opentox.rest.RestException;

public class SubstanceClient <POLICY_RULE> extends AbstractClient<Substance,POLICY_RULE> {
	public enum QueryType  {smiles,url,mol};
	public SubstanceClient() {
		this(null);
	}
		
	public SubstanceClient(HttpClient httpclient) {
		super(httpclient);
	}
	
	public List<URL> searchExactStructuresURI(URL queryService, String term) throws RestException,IOException {
		return searchExactStructuresURI(queryService, term,QueryType.smiles,false);
	}	
	/**
	 * 
	 * @param queryService
	 * @param term SMILES, SMARTS, name, any other identifier. If b64 is true, expects MOL, which will be Base64 encoded
	 * @param b64 
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> searchExactStructuresURI(URL queryService, String term, QueryType qtype, boolean b64) throws RestException,IOException {
		URL ref = new URL(String.format("%s/query/compound/search/all?type=%s&page=0&pagesize=10",queryService,qtype.name()));
		return searchURI(ref, term,b64);
	}
	
	public List<URL> searchSimilarStructuresURI(URL queryService, String term, double threshold) throws RestException,IOException {
		return searchSimilarStructuresURI(queryService, term, QueryType.smiles, false, threshold);
	}
	/**
	 * 
	 * @param queryService
	 * @param term SMILES, SMARTS, name, any other identifier. If b64 is true, expects MOL, which will be Base64 encoded
	 * @param b64 
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> searchSimilarStructuresURI(URL queryService, String term, QueryType qtype, boolean b64, double threshold) throws RestException,IOException {
		URL url = new URL(String.format("%s/query/similarity?type=%s&page=0&pagesize=10&threshold=%3.2f",queryService,qtype.name(),threshold));
		return searchURI(url, term,b64);
	}
	
	/**
	 * 
	 * @param queryService
	 * @param term SMILES, SMARTS, name, any other identifier. If b64 is true, expects MOL, which will be Base64 encoded
	 * @param b64 
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> searchSubstructuresURI(URL queryService, String term, QueryType qtype, boolean b64) throws RestException,IOException {
		URL ref = new URL(String.format("%s/query/smarts?type=%s&page=0&pagesize=10",queryService,qtype.name()));
		return searchURI(ref, term,b64);
	}
	public List<URL> searchSubstructuresURI(URL queryService, String term) throws RestException, IOException {
		return searchSubstructuresURI(queryService, term,QueryType.smiles,false);
	}
	
	public List<URL> searchURI(URL url,String term, boolean b64) throws  RestException, IOException {
		if (b64) return listURI(url, new String[] {b64search_param,Base64.encodeBase64String(term.getBytes())});
		else return listURI(url, new String[] {search_param,term});
	}

	
	public RemoteTask registerSubstanceAsync(URL serviceRoot,Substance substance, String customidName,String customidValue) throws InvalidInputException ,Exception {
		URL ref = new URL(String.format("%s/compound",serviceRoot));
		return sendAsync(ref, createFormEntity(substance,customidName,customidValue), HttpPost.METHOD_NAME);
	}
	
	public RemoteTask setSubstancePropertyAsync(URL serviceRoot,Substance substance, String customidName,String customidValue) throws InvalidInputException ,Exception {
		if (substance.getResourceIdentifier()==null) throw new InvalidInputException("No compound URI");
		URL ref = new URL(String.format("%s/compound",serviceRoot));
		return sendAsync(ref, createFormEntity(substance,customidName,customidValue), HttpPut.METHOD_NAME);
	}
	
	protected HttpEntity createFormEntity(Substance substance, String customidName,String customidValue) throws UnsupportedEncodingException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if (substance.getResourceIdentifier()!=null)
			formparams.add(new BasicNameValuePair("compound_uri", substance.getResourceIdentifier().toExternalForm()));
		//formparams.add(new BasicNameValuePair("molfile", ??));
		if (substance.getCas()!=null)
			formparams.add(new BasicNameValuePair(Substance._titles.CASRN.name(), substance.getCas()));
		if (substance.getEinecs()!=null)
			formparams.add(new BasicNameValuePair(Substance._titles.EINECS.name(), substance.getEinecs()));
		if (substance.getName()!=null)
			formparams.add(new BasicNameValuePair(Substance._titles.ChemicalName.name(), substance.getName()));
		if (substance.getInChI()!=null)
			formparams.add(new BasicNameValuePair(Substance._titles.InChI_std.name(), substance.getInChI()));
		if (substance.getInChIKey()!=null)
			formparams.add(new BasicNameValuePair(Substance._titles.InChIKey_std.name(), substance.getInChIKey()));
		if (substance.getIUCLID_UUID()!=null)
			formparams.add(new BasicNameValuePair(Substance._titles.IUCLID5_UUID.name(),substance.getIUCLID_UUID()));
		if ((customidName!=null) && (customidValue!=null)) {
			formparams.add(new BasicNameValuePair("customidname", customidName));
			formparams.add(new BasicNameValuePair("customid", customidValue));
		}	
		return new UrlEncodedFormEntity(formparams, "UTF-8");
	}
	
}
