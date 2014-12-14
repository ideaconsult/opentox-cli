package net.idea.opentox.cli.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.InvalidInputException;
import net.idea.opentox.cli.algorithm.Algorithm;
import net.idea.opentox.cli.dataset.Dataset;
import net.idea.opentox.cli.task.RemoteTask;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.opentox.rest.RestException;

public class ModelClient<POLICY_RULE> extends AbstractURIClient<Model,POLICY_RULE> {

	public ModelClient(HttpClient httpclient) {
		super(httpclient);
	}
	/**
	 * Async prediction
	 * @param model
	 * @param dataset
	 * @param accessRights
	 * @return
	 * @throws InvalidInputException
	 * @throws Exception
	 */
	public RemoteTask predictAsync(Model model, Dataset dataset, List<POLICY_RULE> accessRights) throws InvalidInputException ,Exception {
		if (model.getResourceIdentifier()==null) throw new InvalidInputException("No model URI");
		if (dataset.getResourceIdentifier()==null) throw new InvalidInputException("No dataset URI");
		return sendAsync(model.getResourceIdentifier(), createPOSTEntity(dataset,accessRights), HttpPost.METHOD_NAME);
	}
	/**
	 * Prediction
	 * @param model
	 * @param dataset
	 * @param accessRights
	 * @return
	 * @throws InvalidInputException
	 * @throws Exception
	 */
	public Dataset predict(Model model, Dataset dataset, List<POLICY_RULE> accessRights) throws InvalidInputException ,Exception {
		RemoteTask task = predictAsync(model,dataset,accessRights);
		task.waitUntilCompleted(500);	
		if (task.isERROR()) throw task.getError();
		else {
			Dataset result = new Dataset();
			result.setResourceIdentifier(task.getResult());
			return result;
		}
	}
	
	
	protected HttpEntity createPOSTEntity(Dataset dataset, List<POLICY_RULE> accessRights, String... params) throws UnsupportedEncodingException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("dataset_uri", dataset.getResourceIdentifier().toExternalForm()));
		//TODO params
		return new UrlEncodedFormEntity(formparams, "UTF-8");
	}
	/**
	 * Search model by algorithm and dataset
	 * @param url
	 * @param algorithm
	 * @param dataset
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> getModelByAlgorithmDataset(URL url,Algorithm algorithm, Dataset dataset) throws  RestException, IOException {
		return listURI(url, getModelByAlgorithmDataset(algorithm,dataset));
	}
	/**
	 * 
	 * @param algorithm
	 * @param dataset
	 * @return
	 */
	protected String[] getModelByAlgorithmDataset(Algorithm algorithm,Dataset dataset) {
		List<String> params = new ArrayList<String>();
		if (algorithm!=null) {
			params.add("algorithm");
			params.add(algorithm.getResourceIdentifier().toExternalForm());
		}
		if (dataset!=null) {
			params.add("dataset");
			params.add(dataset.getResourceIdentifier().toExternalForm());
		}		
		return (String[]) params.toArray();
	}
	
	@Override
	protected List<Model> processPayload(InputStream in, String mediaType)
			throws RestException, IOException {
		List<Model> list = null;
		if (mime_json.equals(mediaType)) {
			 ObjectMapper m = new ObjectMapper();
			 JsonNode node = m.readTree(in);
			 ArrayNode data = (ArrayNode)node.get("model");
			 if (data!=null)
			 for (int i=0; i < data.size();i++) {
				 JsonNode metadata = data.get(i);
				 Model model = new Model(new URL(metadata.get("URI").getTextValue()));
				 if (list==null) list = new ArrayList<Model>();
				 list.add(model);

				 try {model.setTitle(metadata.get("title").getTextValue());} catch (Exception x) {}
				 try {
					 Dataset dataset = new Dataset(new URL(metadata.get("trainingDataset").getTextValue()));
					 model.setTrainingDataset(dataset);
				 } catch (Exception x) {model.setTrainingDataset(null);}
				 try {
					 Algorithm algorithm = new Algorithm(new URL(metadata.get("algorithm").get("URI").getTextValue()));
					 model.setAlgorithm(algorithm);
				 } catch (Exception x) {model.setAlgorithm(null);}
			 }
			 return list;
		} else if (mime_rdfxml.equals(mediaType)) {
			return super.processPayload(in, mediaType);
		} else if (mime_n3.equals(mediaType)) {
			return super.processPayload(in, mediaType);
		} else if (mime_csv.equals(mediaType)) {
			return super.processPayload(in, mediaType);
		} else return super.processPayload(in, mediaType);
	}
	
}
