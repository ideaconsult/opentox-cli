package net.idea.opentox.cli.test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import junit.framework.Assert;
import net.idea.opentox.cli.OTClient;
import net.idea.opentox.cli.id.Identifier;
import net.idea.opentox.cli.qmrf.QMRFDocumentClient;
import net.idea.opentox.cli.structure.Compound;
import net.idea.opentox.cli.structure.CompoundClient;

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

		ExecutorService xs = Executors.newCachedThreadPool();
		int nt = 1000;

		MyThread[] threads = new MyThread[nt];
		for (int i = 0; i < nt; i++)
			try {
				threads[i] = new MyThread(String.format("thread-%d", i), 10000,
						new OTClient(), new Identifier(
						// "http://localhost:8080/ambit2/substance?type=like&search=*acid*"));
						// "http://localhost:8080/ambit2/query/similarity?search=c1cccc1"));
						// "http://localhost:8080/ambit2/bundle/29/substance"));
						// "http://localhost:8080/ambit2/bundle/29/dataset"));
						// "http://localhost:8080/ambit2/dataset/1?max=40"
								//"http://localhost:8081/qmrf/protocol?max=40"
								"http://localhost:8081/qmrf/dataset/A593"));
			} catch (Exception x) {
				logger.log(Level.SEVERE, "loop", x);
			}
		for (int i = 0; i < nt; i++)
			xs.submit(threads[i]);
		xs.awaitTermination(300, TimeUnit.SECONDS);
		xs.shutdown();
		int errors = -1;
		long time = 0;
		long maxtime = 0;
		for (int i = 0; i < nt; i++) {
			if (i == 0) {
				time = 0;
				errors = 0;
			}
			if (threads[i].getError() != null) {
				errors++;
				logger.log(Level.INFO,threads[i].getError().getMessage());
			}	
			if (maxtime < threads[i].time) maxtime = threads[i].time;
			time += threads[i].time;
		}

		logger.log(Level.INFO,String.format(
				"Time per thread:\tAverage %d\tMax %d , ms.",(time / nt),maxtime));
		Assert.assertEquals(0, errors);
	}

	class MyThread extends Thread {
		public long sleep;
		public long time;
		public Identifier dataset;
		public OTClient client;
		public Exception error = null;

		public Exception getError() {
			return error;
		}

		protected Logger threadLogger;
		protected int count = -1;

		public int getCount() {
			return count;
		}

		public MyThread(String name, long sleep, OTClient client, Identifier url) {
			super(name);
			threadLogger = Logger.getLogger(name);
			this.sleep = sleep;
			this.dataset = url;
			this.client = client;
		}

		@Override
		public void run() {
			time = System.currentTimeMillis();
			QMRFDocumentClient cli = null;
			// SubstanceClient cli = null;
			try {
				// cli = client.getSubstanceClient();
				cli = client.getQMRFDocumentClient();
				List list = cli.get(dataset,"text/uri-list");
				if (list == null || list.isEmpty()) {
					error = new Exception("Empty list");
					//threadLogger.log(Level.WARNING, "Empty list");
					count = 0;
				} else
					count = list.size();
				threadLogger.log(Level.INFO, String.format(
						"COMPLETED %s %d %s", getName(), count,
						error == null ? "" : error.getMessage()));
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
