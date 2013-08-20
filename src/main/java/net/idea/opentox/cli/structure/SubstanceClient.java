package net.idea.opentox.cli.structure;

import org.apache.http.client.HttpClient;


/**
 * @deprecated Use {@link CompoundClient}
 * @author nina
 *
 * @param <POLICY_RULE>
 */
public class SubstanceClient <POLICY_RULE> extends CompoundClient<POLICY_RULE> {
	public SubstanceClient() {
		this(null);
	}
		
	public SubstanceClient(HttpClient httpclient) {
		super(httpclient);
	}
}
