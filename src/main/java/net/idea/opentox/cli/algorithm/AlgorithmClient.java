package net.idea.opentox.cli.algorithm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.idea.opentox.cli.AbstractClient;
import net.idea.opentox.cli.InvalidInputException;
import net.idea.opentox.cli.dataset.Dataset;
import net.idea.opentox.cli.model.Model;
import net.idea.opentox.cli.task.RemoteTask;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.opentox.rest.RestException;


public class AlgorithmClient<POLICY_RULE> extends AbstractClient<Algorithm ,POLICY_RULE> {

	/**
	 * Create opentox Model
	 * @param algorithm
	 * @param dataset
	 * @param params
	 * @return
	 * @throws InvalidInputException
	 * @throws Exception
	 */
	public RemoteTask createModelAsync(Algorithm algorithm, Dataset dataset, String... params) throws InvalidInputException ,Exception {
		return createModelAsync(algorithm, dataset, null, params);
	}
	/**
	 * Create OpenTox model
	 * @param algorithm
	 * @param dataset
	 * @param accessRights
	 * @param params
	 * @return
	 * @throws InvalidInputException
	 * @throws Exception
	 */
	public RemoteTask createModelAsync(Algorithm algorithm, Dataset dataset, List<POLICY_RULE> accessRights, String... params) throws InvalidInputException ,Exception {
		if (algorithm.getResourceIdentifier()==null) throw new InvalidInputException("No algorithm URI");
		if (dataset.getResourceIdentifier()==null) throw new InvalidInputException("No dataset URI");
		return sendAsync(algorithm.getResourceIdentifier(), createPOSTEntity(dataset,accessRights,params), HttpPost.METHOD_NAME);
	}
	public Model createModel(Algorithm algorithm, Dataset dataset, List<POLICY_RULE> accessRights, String... params) throws InvalidInputException ,Exception {
		RemoteTask task = createModelAsync(algorithm,dataset,accessRights,params);
		task.waitUntilCompleted(500);	
		if (task.isERROR()) throw task.getError();
		else {
			Model model = new Model();
			model.setAlgorithm(algorithm);
			model.setTrainingDataset(dataset);
			model.setResourceIdentifier(task.getResult());
			return model;
		}
	}
	/**
	 * Search algorithm by type
	 * @param url
	 * @param query
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> getAlgorithmByType(URL url,String query) throws  RestException, IOException {
		return listURI(url, getAlgorithmByTypeQuery(query));
	}
	protected String[] getAlgorithmByTypeQuery(String value) {
		return new String[] {"type",value};
	}
	
	protected HttpEntity createPOSTEntity(Dataset dataset, List<POLICY_RULE> accessRights, String... params) throws UnsupportedEncodingException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("dataset_uri", dataset.getResourceIdentifier().toExternalForm()));
		//TODO params
		return new UrlEncodedFormEntity(formparams, "UTF-8");
	}
}
