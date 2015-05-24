package net.idea.opentox.cli.test;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import net.idea.opentox.cli.OTClient;
import net.idea.opentox.cli.structure.Compound;
import net.idea.opentox.cli.structure.CompoundClient;

import org.junit.Test;

public class StressTest extends
		AbstractClientTest<Compound, String, CompoundClient<String>> {
	protected static Logger logger = Logger.getLogger(StressTest.class
			.getName());
	{
		useAA = false;
	}

	@Override
	protected CompoundClient<String> getOTClient() {
		return otclient.getCompoundClient();
	}

	@Test
	@Override
	public void testRead() throws Exception {

		int nt = 1000;
		MyThread[] threads = new MyThread[nt];
		for (int i = 0; i < nt; i++)
			try {
				threads[i] = new MyThread(String.format("thread-%d", i), 10000,
						new OTClient(), new URL(
								//"http://localhost:8080/ambit2/substance?type=like&search=*acid*"));
								//"http://localhost:8080/ambit2/query/similarity?search=c1cccc1"));
								//"http://localhost:8080/ambit2/bundle/29/substance"));
								//"http://localhost:8080/ambit2/bundle/29/dataset"));
								"http://localhost:8080/ambit2/dataset/1?max=4"));
				threads[i].start();
			} catch (Exception x) {
				logger.log(Level.SEVERE, "loop", x);
			}
		logger.log(Level.INFO, "Waiting threads to join");
		for (int i = 0; i < nt; i++)
			try {
				try {
					threads[i].join();
					logger.log(Level.INFO, String.format("JOINED %s %d %s", threads[i].toString(),threads[i].getCount(),threads[i].error==null?"":threads[i].error.getMessage()));
				} catch (InterruptedException x) {
					logger.log(Level.SEVERE, "interrupted", x);
				}
			} catch (Exception x) {
				logger.log(Level.SEVERE, "join loop", x);
			}
		logger.log(Level.INFO, "completed");
		long time = 0;
		String s = "";
		int errors = 0;
		for (int i = 0; i < nt; i++) {
			time += threads[i].time;
			s += "\n" + threads[i].time;
			if (threads[i].error!=null) {
				errors++;
				logger.log(Level.SEVERE,
						String.format("%d. %s %s",(i),threads[i].getName(),threads[i].error));
				//Assert.assertNotNull(threads[i].getName(), threads[i].dataset);
			}
		}
		//logger.log(Level.INFO, s);
		logger.log(Level.INFO,
				"Average time per thread " + Long.toString(time / nt) + " ms.");
		Assert.assertEquals(0,errors);
	}

	class MyThread extends Thread {
		public long sleep;
		public long time;
		public URL dataset;
		public OTClient client;
		public Exception error = null;
		protected Logger threadLogger;
		protected int count = -1;

		public int getCount() {
			return count;
		}

		public MyThread(String name, long sleep, OTClient client, URL url) {
			super(name);
			threadLogger = Logger.getLogger(name);
			this.sleep = sleep;
			this.dataset = url;
			this.client = client;
		}

		@Override
		public void run() {
			time = System.currentTimeMillis();
			CompoundClient cli = null;
			//SubstanceClient cli = null;
			try {
				//cli = client.getSubstanceClient();
				cli = client.getCompoundClient();
				List list = cli.getJSON(dataset);
				if (list == null || list.isEmpty()) {
					threadLogger.log(Level.WARNING, "Empty list");
					count = 0;
				} else count = list.size();
			} catch (Exception x) {
				error = x;
				threadLogger.log(Level.SEVERE, this.getName(), x);
			} finally {
				time = System.currentTimeMillis() - time;
				try {
					client.close();
				} catch (Exception x) {
				}
			}
		}
	}

	@Override
	public void testCreate() throws Exception {
	}

	public void testDelete() throws Exception {
	};

	@Override
	public void testUpdate() throws Exception {

	}
}
