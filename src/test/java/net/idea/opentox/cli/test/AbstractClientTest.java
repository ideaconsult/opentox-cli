package net.idea.opentox.cli.test;

import java.util.Properties;

import junit.framework.Assert;
import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.AbstractURLResource;
import net.idea.opentox.cli.OTClient;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractClientTest<T extends AbstractURLResource,POLICY_RULE, C extends AbstractURIClient<T,POLICY_RULE>> {
	//public final static String TEST_SERVER = "http://localhost:8080/ambit2";
	public final static String TEST_SERVER = config();
	//should be configured in the .m2/settings.xml 
	protected static final String test_server_property = "opentox.test.server";
	protected static final String aa_server_property = "opentox.aa.opensso";
	protected static final String aa_user_property = "opentox.aa.user";
	protected static final String aa_pass_property = "opentox.aa.pass";
	protected static Properties properties;
	protected static boolean useAA = false;
	
	public final static OTClient otclient = new OTClient();
	@BeforeClass
	public static void setup() throws Exception {
		if (useAA) {
			String username = properties.getProperty(aa_user_property);
			String pass = properties.getProperty(aa_pass_property);
			//ensure maven profile properties are configured and set correctly
			Assert.assertNotNull(username);
			Assert.assertNotNull(pass);
			if (String.format("${%s}",aa_user_property).equals(username) ||
				String.format("${%s}",aa_pass_property).equals(pass)) 
				throw new Exception(String.format("The following properties are not found in the acive Maven profile ${%s} ${%s}",
						aa_user_property,aa_pass_property));
			boolean ok = otclient.login(username,pass);
			Assert.assertTrue(ok);
		}
	}
	

	@AfterClass
	public static void teardown() throws Exception {
		if (useAA) {
			otclient.logout();
			otclient.close();
		}
	}
	public static String config()  {
		String local = "http://localhost:8080/ambit2";
		try {
			properties = new Properties();
			properties.load(AbstractClientTest.class.getClassLoader().getResourceAsStream("net/idea/opentox/cli/client.properties"));
			String testServer = properties.getProperty(test_server_property);
			return testServer!=null?testServer.startsWith("http")?testServer:local:local;
		} catch (Exception x) {
			return local;
		}
	}
	
	
	protected abstract C getOTClient();
	
	
	@Test
	public void testRead() throws Exception {
		Assert.fail("Not implemented");
	}
	
	@Test
	public void testDelete() throws Exception {
		Assert.fail("Not implemented");
	}
	
	@Test
	public void testCreate() throws Exception {
		Assert.fail("Not implemented");
	}
	
	@Test
	public void testUpdate() throws Exception {
		Assert.fail("Not implemented");
	}	
	
	@Test
	public void testList() throws Exception {
		Assert.fail("Not implemented");
	}
	
	
}
