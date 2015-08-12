package net.idea.opentox.cli.test;

import java.net.URL;
import java.util.List;

import ambit2.base.facet.SubstanceStudyFacet;
import junit.framework.Assert;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.opentox.cli.Resources;
import net.idea.opentox.cli.structure.Substance;
import net.idea.opentox.cli.structure.SubstanceClient;


public class SubstanceClientTest<POLICY_RULE> extends AbstractClientTest<Substance,POLICY_RULE,SubstanceClient<POLICY_RULE>> {

	@Override
	protected SubstanceClient<POLICY_RULE> getOTClient() {
		return otclient.getSubstanceClient();
	}

	@Override
	public void testRead() throws Exception {
		SubstanceClient<POLICY_RULE> otClient = getOTClient();
		//get the first record
		//URL url = new URL(String.format("%s%s?page=3&pagesize=1&studysummary=true", TEST_SERVER,Resources.substance));
		String compound_uri = String.format("%s/compound/2", TEST_SERVER);
		List<Substance> substances = otClient.getSubstancesRelatedToCompound(TEST_SERVER, compound_uri, true);
		
		Assert.assertNotNull(substances);
		Assert.assertTrue(substances.size()>0);

		Assert.assertNotNull(substances.get(0).getResourceIdentifier());
		Assert.assertNotNull(substances.get(0).getRecord());
		Assert.assertNotNull(substances.get(0).getRecord().getSubstanceName());
		Assert.assertNotNull(substances.get(0).getRecord().getPublicName());
		Assert.assertNotNull(substances.get(0).getRecord().getOwnerName());
		Assert.assertNotNull(substances.get(0).getRecord().getOwnerUUID());
		Assert.assertNotNull(substances.get(0).getRecord().getSubstancetype());
		Assert.assertNotNull(substances.get(0).getRecord().getSubstanceUUID());
		Assert.assertNotNull(substances.get(0).getRecord().getExternalids());
		
		int summaries = 0;
		for (IFacet facet : substances.get(0).getRecord().getFacets()) {
			Assert.assertNotNull(facet);
			Assert.assertTrue(facet instanceof SubstanceStudyFacet);
			Assert.assertTrue(((SubstanceStudyFacet)facet).getCount()>0);
			Assert.assertNotNull(((SubstanceStudyFacet)facet).getInterpretation_result());
			Assert.assertNotNull(((SubstanceStudyFacet)facet).getTitle());
			Assert.assertNotNull(((SubstanceStudyFacet)facet).getSubcategoryTitle());
			
			System.out.println(
			facet.toJSON(substances.get(0).getResourceIdentifier().toString(),null)
			);
			
			summaries++;
		}
		Assert.assertTrue(summaries>0);

	}
}