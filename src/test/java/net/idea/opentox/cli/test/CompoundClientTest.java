package net.idea.opentox.cli.test;

import java.net.URL;
import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.Test;

import junit.framework.Assert;
import net.idea.opentox.cli.Resources;
import net.idea.opentox.cli.id.IIdentifier;
import net.idea.opentox.cli.id.Identifier;
import net.idea.opentox.cli.structure.Compound;
import net.idea.opentox.cli.structure.CompoundClient;
import net.idea.opentox.cli.structure.CompoundClient.QueryType;
import net.idea.opentox.cli.task.RemoteTask;

public class CompoundClientTest<POLICY_RULE> extends
		AbstractClientTest<Compound, POLICY_RULE, CompoundClient<POLICY_RULE>> {
	{
		useAA = true;
	}

	@Override
	protected CompoundClient<POLICY_RULE> getOTClient() {
		return otclient.getCompoundClient();
	}
	/*
	@Test
	public void testRead() throws Exception {

		CompoundClient cli = getOTClient();
		// get the first record
		URL queryService = new URL("http://toxbanktest2.toxbank.net:8080/ambit2");

		File file = new File("F:/Downloads/Chemical data/ToxCast2014/ToxCast_Summary_Files/Chemical_Summary_141024.csv");
		IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(new FileReader(file));

		File w = new File(
				"F:/Downloads/Chemical data/ToxCast2014/ToxCast_Summary_Files/Chemical_Summary_141024_TOXBANK.csv");
		DelimitedFileWriter writer = new DelimitedFileWriter(new FileWriter(w));
		int r = 0;
		while (reader.hasNext()) {
			r++;
			IMolecule mol = (IMolecule) reader.next();
			// System.out.println(mol.getProperties());
			Object cas = mol.getProperty("\"casn\"");
			List<URL> uri = cli.searchExactStructuresURI(queryService, cas.toString(), QueryType.smiles, false);
			for (URL item : uri) {
				List<Compound> c = cli.getIdentifiersAndLinks(queryService, item);
				for (Compound cmp : c) {
					mol.setProperty("InChIKey", cmp.getInChIKey());
					String[] name = cmp.getName().split("|");
					if (name.length == 1)
						mol.setProperty("Name", cmp.getName());
					else
						mol.setProperty("Name", name[0]);
				}
			}

			writer.write(mol);
			if (r % 100 == 0)
				System.out.println(r);

		}
		reader.close();
		writer.close();
	}
	*/
	@Test
	public void testSearchMol() throws Exception {

		CompoundClient cli = getOTClient();
		// get the first record
		List<URL> uri = cli.searchSimilarStructuresURI(new URL(TEST_SERVER), mol, QueryType.mol, true, 0.6);
		// verify if a record is retrieved
		Assert.assertTrue(uri.size() > 0);
		/*
		 * List<Substance> chemicals = cli.getRDF_XML(uri.get(0)); //verify one
		 * record is retrieved Assert.assertEquals(1,chemicals.size());
		 * Assert.assertEquals
		 * (uri.get(0),chemicals.get(0).getResourceIdentifier());
		 * Assert.assertNotNull(chemicals.get(0).getTitle());
		 */
	}

	static String mol = "benzene\n" + "  MOE2008           2D\n" + "\n" + "  6  6  0  0  0  0  0  0  0  0999 V2000\n"
			+ "    1.2300    0.7100    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    0.0000    1.4200    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   -1.2300    0.7100    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   -1.2300   -0.7100    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    0.0000   -1.4200    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    1.2300   -0.7100    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" + "  1  2  1  0  0  0  0\n"
			+ "  1  6  2  0  0  0  0\n" + "  2  3  2  0  0  0  0\n" + "  3  4  1  0  0  0  0\n"
			+ "  4  5  2  0  0  0  0\n" + "  5  6  1  0  0  0  0\n" + "M  END\n" + "$$$$\n";

	@Override
	public void testCreate() throws Exception {
		CompoundClient<POLICY_RULE> otClient = getOTClient();
		Compound substance = new Compound();
		substance.setCas("50-00-0");
		// POST
		RemoteTask task = otClient.registerSubstanceAsync(new URL(TEST_SERVER), substance, "TEST_ID", "12345");
		task.waitUntilCompleted(500);
		// verify if ok
		Assert.assertEquals(HttpStatus.SC_OK, task.getStatus());
		Assert.assertNull(task.getError());
		System.out.println(task.getResult());

		// otClient.delete(task.getResult());
	}

	@Override
	public void testDelete() throws Exception {
		Assert.fail("TODO");
	}

	@Override
	public void testUpdate() throws Exception {
		CompoundClient<POLICY_RULE> otClient = getOTClient();
		Compound substance = new Compound();
		substance.setResourceIdentifier(new Identifier(String.format("%s%s/%d%s/%d", TEST_SERVER, Resources.compound, 1,
				Resources.conformer, 1)));
		// POST
		RemoteTask task = otClient.setSubstancePropertyAsync(new URL(TEST_SERVER), substance, "TEST_ID", "12345");
		task.waitUntilCompleted(500);
		// verify if ok
		Assert.assertEquals(HttpStatus.SC_OK, task.getStatus());
		Assert.assertNull(task.getError());
		System.out.println(task.getResult());

		/*
		 * int hits = 0; List<Substance> updatedSubst =
		 * otClient.get(substance.getResourceIdentifier()); for (Substance o :
		 * updatedSubst) { if (substance.getTitle().equals(o.getTitle()))
		 * hits++; } Assert.assertEquals(1,hits);
		 */
	}

	@Test
	public void testReadIdentifiers() throws Exception {
		CompoundClient<POLICY_RULE> otClient = getOTClient();
		// get the first record
		List<Compound> substances = otClient.getIdentifiersAndLinks(new URL(String.format("%s", TEST_SERVER)),// bosentan
				new URL(String.format("%s%s/1", TEST_SERVER, Resources.compound)));
		Assert.assertNotNull(substances);
		for (Compound s : substances) {
			Assert.assertNotNull(s.getResourceIdentifier());
			System.out.println(s.getName());
			System.out.println(s.getResourceIdentifier());
			System.out.println(s.getProperties().get(Compound.opentox_ChEBI));
		}
	}
	
	@Test
	public void testSearch() throws Exception {
		
		CompoundClient cli = getOTClient();
		//get the first record
		List<URL> uri = cli.searchSimilarStructuresURI(new URL(TEST_SERVER),"benzene",0.6);
		//verify if a record is retrieved
		Assert.assertTrue(uri.size()>0);
		/*
		List<Substance> chemicals = cli.getRDF_XML(uri.get(0));
		//verify one record is retrieved
		Assert.assertEquals(1,chemicals.size());
		Assert.assertEquals(uri.get(0),chemicals.get(0).getResourceIdentifier());
		Assert.assertNotNull(chemicals.get(0).getTitle());
		*/
	}
	
	
	@Override
	public void testRead() throws Exception {
		CompoundClient<POLICY_RULE> otClient = getOTClient();
		//get the first record
		List<IIdentifier> uri = otClient.listURI(new Identifier(String.format("%s%s/1", TEST_SERVER,Resources.compound)),
				new String[] {"page","0","pagesize","1"});		
		//verify one record is retrieved
		Assert.assertEquals(1,uri.size());
		//retrieve organisation details
		List<Compound> subst = otClient.getRDF_XML(uri.get(0));
		//verify one record is retrieved
		Assert.assertEquals(1,subst.size());
		Assert.assertEquals(uri.get(0),subst.get(0).getResourceIdentifier());
		Assert.assertNotNull(subst.get(0).getTitle());
		//this fails, not implemented
		//Assert.assertNotNull(orgs.get(0).getGroupName());
	}

	
	@Override
	public void testList() throws Exception {
		CompoundClient<POLICY_RULE> otClient = getOTClient();
		List<IIdentifier> uri = otClient.listURI(new Identifier(String.format("%s%s/1", TEST_SERVER,Resources.compound)));
		System.out.println(uri);
		Assert.assertTrue(uri.size()>0);
	}

		
}
