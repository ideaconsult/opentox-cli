package net.idea.opentox.cli.test;

import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import net.idea.opentox.cli.Resources;
import net.idea.opentox.cli.dataset.Dataset;
import net.idea.opentox.cli.dataset.DatasetClient;
import net.idea.opentox.cli.dataset.InputData;
import net.idea.opentox.cli.dataset.Rights;
import net.idea.opentox.cli.dataset.Rights._type;
import net.idea.opentox.cli.structure.Compound;
import net.idea.opentox.cli.structure.CompoundClient;
import net.idea.opentox.cli.task.RemoteTask;

import org.apache.http.HttpStatus;
import org.junit.Test;

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
		List<URL> uri = otClient.listURI(new URL(String.format("%s%s", TEST_SERVER,Resources.dataset)));
		Assert.assertTrue(uri.size()>0);
	}
	
	@Test
	public void testGetDatasetList() throws Exception {
		DatasetClient<POLICY_RULE> otClient = getOTClient();
		List<Dataset> datasets = otClient.getJSON(new URL(String.format("%s%s", TEST_SERVER,Resources.dataset)));
		Assert.assertNotNull(datasets);
		Assert.assertTrue(datasets.size()>0);
		for (Dataset dataset:datasets) {
			Assert.assertNotNull(dataset.getResourceIdentifier());
			Assert.assertNotNull(dataset.getMetadata().getTitle());
		}
	}
	@Override
	public void testRead() throws Exception {
		DatasetClient<POLICY_RULE> otClient = getOTClient();
		List<Dataset> dataset = otClient.getJSON(new URL(String.format("%s%s/1/metadata", TEST_SERVER,Resources.dataset)));
		Assert.assertEquals(1,dataset.size());
	}
	
	@Override
	public void testCreate() throws Exception {
		//in case the TEST_SERVER uses HTTP BASIC
		otclient.setHTTPBasicCredentials("localhost", 8080,"admin", "changeit");
		
		DatasetClient<POLICY_RULE> cli = getOTClient();
		URL url = getClass().getClassLoader().getResource("net/idea/opentox/cli/test/sdf/1000-90-4.sdf");
		File fileToImport = new File(url.getFile());
		Assert.assertTrue(fileToImport.exists());
		Dataset dataset = new Dataset();
		dataset.getMetadata().setTitle("Test dataset");
		dataset.getMetadata().setSeeAlso("Test see also uri");
		dataset.getMetadata().setRights(new Rights("CC-BY-SA","http://creativecommons.org/licenses/by-sa/2.0/",_type.license));
		dataset.setInputData(new InputData(fileToImport,DatasetClient._MATCH.InChI));
		RemoteTask task = cli.postAsync(dataset,new URL(String.format("%s%s", TEST_SERVER,Resources.dataset)));
		task.waitUntilCompleted(1000);
		//verify if ok
		Assert.assertEquals(HttpStatus.SC_OK,task.getStatus());
		Assert.assertNull(task.getError());
		List<Dataset> theDataset = cli.getMetadata(task.getResult());
		Assert.assertEquals(1,theDataset.size());
		Assert.assertEquals(dataset.getMetadata().getTitle(),theDataset.get(0).getMetadata().getTitle());
		Assert.assertEquals(dataset.getMetadata().getSeeAlso(),theDataset.get(0).getMetadata().getSeeAlso());
		Assert.assertEquals(task.getResult(),theDataset.get(0).getResourceIdentifier());
		
		CompoundClient<POLICY_RULE> ccli = otclient.getCompoundClient();
		List<Compound> compounds = cli.getCompounds(theDataset.get(0), ccli);
		Assert.assertNotNull(compounds);
		for (Compound compound : compounds) {
			System.out.println(compound);
		}

		//finally delete the dataset
		cli.delete(theDataset.get(0));		
	}
}
