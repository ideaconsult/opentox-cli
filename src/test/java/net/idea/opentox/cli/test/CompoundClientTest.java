package net.idea.opentox.cli.test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;

import net.idea.opentox.cli.structure.Compound;
import net.idea.opentox.cli.structure.CompoundClient;
import net.idea.opentox.cli.structure.CompoundClient.QueryType;

import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.core.io.DelimitedFileWriter;
import ambit2.core.io.IteratingDelimitedFileReader;

public class CompoundClientTest<POLICY_RULE> extends AbstractClientTest<Compound,POLICY_RULE,CompoundClient<POLICY_RULE>> {
	{
		useAA = true;
	}
	@Override
	protected CompoundClient<POLICY_RULE> getOTClient() {
		return otclient.getCompoundClient();
	}
	
	
	@Test
	public void testRead() throws Exception {
		
		CompoundClient cli = getOTClient();
		//get the first record
		URL queryService = new URL("http://toxbanktest2.toxbank.net:8080/ambit2");
		
		File file = new File("F:/Downloads/Chemical data/ToxCast2014/ToxCast_Summary_Files/Chemical_Summary_141024.csv");
		IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(new FileReader(file));
		
		File w = new File("F:/Downloads/Chemical data/ToxCast2014/ToxCast_Summary_Files/Chemical_Summary_141024_TOXBANK.csv");
		DelimitedFileWriter writer = new DelimitedFileWriter(new FileWriter(w));
		int r = 0;
		while (reader.hasNext()) {
			r++;
			IMolecule mol = (IMolecule)reader.next();
			//System.out.println(mol.getProperties());
			Object cas = mol.getProperty("\"casn\"");
			List<URL> uri = cli.searchExactStructuresURI(queryService,cas.toString(),QueryType.smiles,false);
			for ( URL item : uri) {
				List<Compound> c = cli.getIdentifiersAndLinks(queryService, item);
				for (Compound cmp : c) {
					mol.setProperty("InChIKey", cmp.getInChIKey());
					String[] name =  cmp.getName().split("|");
					if (name.length==1)
						mol.setProperty("Name", cmp.getName());
					else mol.setProperty("Name", name[0]);
				}
			}
			
			writer.write(mol);
			if (r % 100 == 0) System.out.println(r);
			
		}
		reader.close();
		writer.close();
	}	
}
