package net.idea.opentox.cli.test;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import net.idea.opentox.cli.Resources;
import net.idea.opentox.cli.structure.Substance;
import net.idea.opentox.cli.structure.SubstanceClient;
import net.idea.opentox.cli.task.RemoteTask;

import org.apache.http.HttpStatus;


public class SubstanceClientTest<POLICY_RULE> extends AbstractClientTest<Substance,POLICY_RULE,SubstanceClient<POLICY_RULE>> {

	@Override
	protected SubstanceClient<POLICY_RULE> getOTClient() {
		return otclient.getSubstanceClient();
	}
	
	@Override
	public void testList() throws Exception {
		SubstanceClient<POLICY_RULE> otClient = getOTClient();
		List<URL> uri = otClient.listURI(new URL(String.format("%s%s", TEST_SERVER,Resources.compound)));
		System.out.println(uri);
		Assert.assertTrue(uri.size()>0);
	}
	
	@Override
	public void testRead() throws Exception {
		SubstanceClient<POLICY_RULE> otClient = getOTClient();
		//get the first record
		List<URL> uri = otClient.listURI(new URL(String.format("%s%s/1", TEST_SERVER,Resources.compound)),
				new String[] {"page","0","pagesize","1"});		
		//verify one record is retrieved
		Assert.assertEquals(1,uri.size());
		//retrieve organisation details
		List<Substance> subst = otClient.getRDF_XML(uri.get(0));
		//verify one record is retrieved
		Assert.assertEquals(1,subst.size());
		Assert.assertEquals(uri.get(0),subst.get(0).getResourceIdentifier());
		Assert.assertNotNull(subst.get(0).getTitle());
		//this fails, not implemented
		//Assert.assertNotNull(orgs.get(0).getGroupName());
	}
	/*
	public void testSearch() throws Exception {
		SubstanceClient cli = getOTClient();
		//get the first record
		List<URL> uri = cli.searchURI(new URL(String.format("%s%s", TEST_SERVER,Resources.compound)),"");
		//verify if a record is retrieved
		Assert.assertTrue(uri.size()>0);
		//retrieve project details
		List<Substance> projects = cli.getRDF_XML(uri.get(0));
		//verify one record is retrieved
		Assert.assertEquals(1,projects.size());
		Assert.assertEquals(uri.get(0),projects.get(0).getResourceIdentifier());
		Assert.assertNotNull(projects.get(0).getTitle());
		//this fails, not implemented
		//Assert.assertNotNull(project.get(0).getGroupName());
	}	
	*/
	@Override
	public void testCreate() throws Exception {
		SubstanceClient<POLICY_RULE> otClient = getOTClient();
		Substance substance = new Substance();
		substance.setCas("50-00-0");
		//POST
		RemoteTask task = otClient.registerSubstanceAsync(new URL(TEST_SERVER), substance,"TEST_ID","12345");
		task.waitUntilCompleted(500);
		//verify if ok
		Assert.assertEquals(HttpStatus.SC_OK,task.getStatus());
		Assert.assertNull(task.getError());
		System.out.println(task.getResult());

		//otClient.delete(task.getResult());
	}
	@Override
	public void testDelete() throws Exception {
		Assert.fail("TODO");
	}
	
	@Override
	public void testUpdate() throws Exception {
		SubstanceClient<POLICY_RULE> otClient = getOTClient();
		Substance substance = new Substance();
		substance.setResourceIdentifier(new URL(String.format("%s%s/%d%s/%d",TEST_SERVER,Resources.compound,1,Resources.conformer,1)));
		//POST
		RemoteTask task = otClient.setSubstancePropertyAsync(new URL(TEST_SERVER), substance,"TEST_ID","12345");
		task.waitUntilCompleted(500);
		//verify if ok
		Assert.assertEquals(HttpStatus.SC_OK,task.getStatus());
		Assert.assertNull(task.getError());
		System.out.println(task.getResult());
		
		/*
		int hits = 0;
		List<Substance> updatedSubst = otClient.get(substance.getResourceIdentifier());
		for (Substance o : updatedSubst) {
			if (substance.getTitle().equals(o.getTitle())) hits++;
		}
		Assert.assertEquals(1,hits);
		*/
	}
}