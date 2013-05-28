package net.idea.opentox.cli.structure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.idea.opentox.cli.AbstractClient;
import net.idea.opentox.cli.InvalidInputException;
import net.idea.opentox.cli.task.RemoteTask;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.opentox.rest.RestException;

public class SubstanceClient <POLICY_RULE> extends AbstractClient<Substance,POLICY_RULE> {

	public SubstanceClient() {
		this(null);
	}
		
	public SubstanceClient(HttpClient httpclient) {
		super(httpclient);
	}
	
	public List<URL> searchExactStructuresURI(URL queryService, String term) throws RestException,
			IOException {
		URL ref = new URL(String.format("%s/query/compound/search/all?page=0&pagesize=10",queryService));
		return super.searchURI(ref, term);
	}
	
	public List<URL> searchSimilarStructuresURI(URL queryService, String term, double threshold) throws RestException,
																						IOException {
		URL ref = new URL(String.format("%s/query/similarity?page=0&pagesize=10&threshold=%3.2f",queryService,threshold));
		return super.searchURI(ref, term);
	}
	
	public List<URL> searchSubstructuresURI(URL queryService, String term) throws RestException,
	IOException {
		URL ref = new URL(String.format("%s/query/smarts?page=0&pagesize=10",queryService));
		return super.searchURI(ref, term);
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
