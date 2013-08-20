package net.idea.opentox.cli.dataset;

import net.idea.opentox.cli.AbstractClient;

import org.apache.http.client.HttpClient;

/**
 * Reads/writes {@link Dataset} via OpenTox Dataset API
 * @author nina
 *
 * @param <POLICY_RULE>
 */
public class DatasetClient<POLICY_RULE> extends AbstractClient<Dataset,POLICY_RULE> {

	public DatasetClient() {
		this(null);
	}
		
	public DatasetClient(HttpClient httpclient) {
		super(httpclient);
	}
	
}
