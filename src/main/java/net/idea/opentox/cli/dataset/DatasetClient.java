package net.idea.opentox.cli.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.InvalidInputException;
import net.idea.opentox.cli.dataset.Rights._type;
import net.idea.opentox.cli.structure.Compound;
import net.idea.opentox.cli.structure.CompoundClient;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.opentox.rest.RestException;

/**
 * Reads/writes {@link Dataset} via OpenTox Dataset API
 * @author nina
 *
 * @param <POLICY_RULE>
 */
public class DatasetClient<POLICY_RULE> extends AbstractURIClient<Dataset,POLICY_RULE> {
	protected enum _WEBFORM {
		file,
		title,
		seeAlso,
		license,
		match,
		rightsHolder
	}
	//On import, finds the same compound in the database by matching with the selected criteria 
	public enum _MATCH {
		//Match by CAS registry number
		CAS,
		//Match by EINECS registry number
		EINECS,
		//Match by PubChem Compound ID (PUBCHEM_COMPOUND_CID)
		PubChemID,
		//Match by DSSTox Chemical ID (DSSTox_CID) number uniquely assigned to a particular STRUCTURE across all DSSTox files
		DSSToxCID,
		//Match by DSSTox Record ID (DSSTox_RID) is number uniquely assigned to each DSSTox record across all DSSTox files"
		DSSToxRID,
		//Records with the same DSSTox_Generic_SID (Generic Substance ID) will share all DSSTox Standard Chemical Fields, including STRUCTURE. Field distinguishes at the level of "Test Substance" across all DSSTox data files, most often corresponding to the level of CASRN distinction, but not always.
		DSSToxGenericSID,
		//Match by InChI
		InChI,
		//Match by SMILES
		SMILES,
		//http://rdf.farmbio.uu.se/chembl/onto/#forMolecule
		ChEMBL,
		//Match by column "SAMPLE"
		SAMPLE,
		//Match by chemical name
		NAME,
		//Match by IUCLID5 Reference substance UUID
		IUCLID5_REFERENCESUBSTANCE,
		//Don't match, add as a new structure
		None
	}
	
	public DatasetClient() {
		this(null);
	}
		
	public DatasetClient(HttpClient httpclient) {
		super(httpclient);
	}
	
	@Override
	protected List<Dataset> processPayload(InputStream in, String mediaType)
			throws RestException, IOException {
		List<Dataset> list = null;
		if (mime_json.equals(mediaType)) {
			 ObjectMapper m = new ObjectMapper();
			 JsonNode node = m.readTree(in);
			 callback(node);
			 ArrayNode data = (ArrayNode)node.get("dataset");
			 if (data!=null)
			 for (int i=0; i < data.size();i++) {
				 JsonNode metadata = data.get(i);
				 Dataset dataset = new Dataset(new URL(metadata.get("URI").getTextValue()));
				 if (list==null) list = new ArrayList<Dataset>();
				 list.add(dataset);
				 try {dataset.getMetadata().setTitle(metadata.get("title").getTextValue());} catch (Exception x) {}
				 try {dataset.getMetadata().setSeeAlso(metadata.get("seeAlso").getTextValue());} catch (Exception x) {}
				 try {dataset.getMetadata().setStars(metadata.get("stars").getIntValue());} catch (Exception x) {}
				 dataset.getMetadata().setRights(new Rights());
				 try {dataset.getMetadata().getRights().setRightsHolder(metadata.get("rightsHolder").getTextValue());} catch (Exception x) {}
				 try {dataset.getMetadata().getRights().setURI(metadata.get("rights").get("URI").getTextValue());} catch (Exception x) {}
				 try {dataset.getMetadata().getRights().setType(_type.rights.valueOf(metadata.get("rights").get("type").getTextValue()));} catch (Exception x) {}
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
	

	@Override
	protected HttpEntity createPOSTEntity(Dataset dataset,
			List<POLICY_RULE> accessRights) throws InvalidInputException,
			Exception {
		if (dataset.getInputData()==null || dataset.getInputData().getInputFile()==null) throw new InvalidInputException("File to import not defined!");
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,utf8);

		entity.addPart(_WEBFORM.title.name(), new StringBody(dataset.getMetadata().getTitle(),utf8));
		entity.addPart(_WEBFORM.seeAlso.name(), new StringBody(dataset.getMetadata().getSeeAlso(),utf8));
		entity.addPart(_WEBFORM.license.name(), new StringBody(dataset.getMetadata().getRights()==null?"":(dataset.getMetadata().getRights().getURI()==null?"":dataset.getMetadata().getRights().getURI()),utf8));
		entity.addPart(_WEBFORM.match.name(), new StringBody(dataset.getInputData().getImportMatchMode().name(),utf8));
		entity.addPart(_WEBFORM.file.name(), new FileBody(dataset.getInputData().getInputFile()));
		 
		return entity;
	}
	
	@Override
	protected HttpEntity createPUTEntity(Dataset dataset,
			List<POLICY_RULE> accessRights) throws InvalidInputException,
			Exception {
		List<NameValuePair> entity = new ArrayList<NameValuePair>();
		if (dataset.getMetadata().getTitle()!=null)
			entity.add(new BasicNameValuePair(_WEBFORM.title.name(), dataset.getMetadata().getTitle()));
		if (dataset.getMetadata().getSeeAlso()!=null)
			entity.add(new BasicNameValuePair(_WEBFORM.seeAlso.name(), dataset.getMetadata().getSeeAlso()));
		if (dataset.getMetadata().getRights()!=null) {
			if (dataset.getMetadata().getRights().getRightsHolder()!=null)
				entity.add(new BasicNameValuePair(_WEBFORM.rightsHolder.name(), dataset.getMetadata().getRights().getRightsHolder()));
			if (dataset.getMetadata().getRights().getURI()!=null)
				entity.add(new BasicNameValuePair(_WEBFORM.license.name(), dataset.getMetadata().getRights().getURI()));			
		}
		if (entity.size()==0) throw new InvalidInputException("No content!");
		return new UrlEncodedFormEntity(entity, "UTF-8");
	}
	/**
	 * Retrieves the dataset metadata, given dataset object
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public List<Dataset> getMetadata(Dataset dataset) throws Exception {
		return get(new URL(String.format("%s/metadata", dataset.getResourceIdentifier())),mime_json);
	}
	/**
	 * Retrieves the dataset metadata, given dataset URI
	 * @param datasetURI
	 * @return
	 * @throws Exception
	 */
	public List<Dataset> getMetadata(String datasetURI) throws Exception {
		return get(new URL(String.format("%s/metadata", datasetURI)),mime_json);
	}	
	/**
	 * Retrieves the dataset metadata, given dataset URI
	 * @param datasetURI
	 * @return
	 * @throws Exception
	 */
	public List<Dataset> getMetadata(URL datasetURI) throws Exception {
		return get(new URL(String.format("%s/metadata", datasetURI)),mime_json);
	}
	/**
	 * Retrieves dataset compounds
	 * @param dataset
	 * @param cli
	 * @return
	 * @throws Exception
	 */
	public List<Compound> getCompounds(Dataset dataset, CompoundClient cli) throws Exception {
		return cli.getJSON(dataset.getResourceIdentifier());
	}
}
