package net.idea.opentox.cli.structure;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.idea.opentox.cli.AbstractClient;

import org.apache.http.client.HttpClient;
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
	
	
	
}
