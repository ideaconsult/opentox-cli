package net.idea.opentox.cli.test;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import net.idea.opentox.cli.Resources;
import net.idea.opentox.cli.dataset.Dataset;
import net.idea.opentox.cli.dataset.DatasetClient;

/**
 * Test for {@link DatasetClient}
 * @author nina
 *
 * @param <POLICY_RULE>
 */
public class DatasetClientTest<POLICY_RULE> extends AbstractClientTest<Dataset,POLICY_RULE,DatasetClient<POLICY_RULE>> {

	@Override
	protected DatasetClient<POLICY_RULE> getOTClient() {
		return otclient.getDatasetClient();
	}
	
	@Override
	public void testList() throws Exception {
		DatasetClient<POLICY_RULE> otClient = getOTClient();
		List<URL> uri = otClient.listURI(new URL(String.format("%s%s/1", TEST_SERVER,Resources.compound)));
		System.out.println(uri);
		Assert.assertTrue(uri.size()>0);
	}
}
