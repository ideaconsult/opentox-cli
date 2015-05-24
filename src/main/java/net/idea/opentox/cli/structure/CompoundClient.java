package net.idea.opentox.cli.structure;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.InvalidInputException;
import net.idea.opentox.cli.task.RemoteTask;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.opentox.rest.RestException;

/**
 * Substance client renamed to CompoundClient
 * Reads/writes {@link Compound} via OpenTox Compound API
 * @author nina
 *
 * @param <POLICY_RULE>
 */
public class CompoundClient <POLICY_RULE> extends AbstractURIClient<Compound,POLICY_RULE> {
	public enum QueryType  {smiles,url,mol,inchikey};
	public CompoundClient() {
		this(null);
	}
		
	public CompoundClient(HttpClient httpclient) {
		super(httpclient);
	}
	
	public List<URL> searchExactStructuresURI(URL queryService, String term) throws RestException,IOException {
		return searchExactStructuresURI(queryService, term,QueryType.smiles,false);
	}	
	/**
	 * 
	 * @param queryService
	 * @param term SMILES, SMARTS, name, any other identifier. If b64 is true, expects MOL, which will be Base64 encoded
	 * @param b64 
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> searchExactStructuresURI(URL queryService, String term, QueryType qtype, boolean b64) throws RestException,IOException {
		URL ref = new URL(String.format("%s/query/compound/search/all?type=%s&page=0&pagesize=10",queryService,qtype.name()));
		return searchURI(ref, term,b64);
	}
	
	public List<URL> searchSucturesByInchikeyURI(URL queryService, String term) throws RestException,IOException {
		URL ref = new URL(String.format("%s/query/compound/inchikey/all?page=0&pagesize=10",queryService));
		return searchURI(ref, term,false);
	}
	
	public List<URL> searchSimilarStructuresURI(URL queryService, String term, double threshold) throws RestException,IOException {
		return searchSimilarStructuresURI(queryService, term, QueryType.smiles, false, threshold);
	}
	/**
	 * 
	 * @param queryService
	 * @param term SMILES, SMARTS, name, any other identifier. If b64 is true, expects MOL, which will be Base64 encoded
	 * @param b64 
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> searchSimilarStructuresURI(URL queryService, String term, QueryType qtype, boolean b64, double threshold) throws RestException,IOException {
		URL url = new URL(String.format("%s/query/similarity?type=%s&page=0&pagesize=10&threshold=%3.2f",queryService,qtype.name(),threshold));
		return searchURI(url, term,b64);
	}
	
	/**
	 * 
	 * @param queryService
	 * @param term SMILES, SMARTS, name, any other identifier. If b64 is true, expects MOL, which will be Base64 encoded
	 * @param b64 
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<URL> searchSubstructuresURI(URL queryService, String term, QueryType qtype, boolean b64) throws RestException,IOException {
		URL ref = new URL(String.format("%s/query/smarts?type=%s&page=0&pagesize=10",queryService,qtype.name()));
		return searchURI(ref, term,b64);
	}
	public List<URL> searchSubstructuresURI(URL queryService, String term) throws RestException, IOException {
		return searchSubstructuresURI(queryService, term,QueryType.smiles,false);
	}
	
	public List<URL> searchURI(URL url,String term, boolean b64) throws  RestException, IOException {
		if (b64) return listURI(url, new String[] {b64search_param,Base64.encodeBase64String(term.getBytes())});
		else return listURI(url, new String[] {search_param,term});
	}

	/**
	 * /ambit2/query/compound/url/all?search=http%3A%2F%2Ftoxbanktest2.toxbank.net%3A8080%2Fambit2%2Fcompound%2F1%2Fconformer%2F1
	 * @param queryService
	 * @param term
	 * @return
	 * @throws RestException
	 * @throws IOException
	 */
	public List<Compound> getIdentifiers(URL queryService, URL compound) throws Exception {
		URL ref = new URL(String.format("%s/query/compound/url/all?search=%s",queryService,URLEncoder.encode(compound.toExternalForm())));
		return get(ref,mime_json);
	}
	
	public List<Compound> getIdentifiersAndLinks(URL queryService, URL compound) throws Exception {
		URL ref = new URL(String.format("%s/query/compound/url/allnlinks?search=%s",queryService,URLEncoder.encode(compound.toExternalForm())));
		return get(ref,mime_json);
	}


	
	@Override
	protected List<Compound> processPayload(InputStream in, String mediaType)
			throws RestException, IOException {
		List<Compound> list = null;
		if (mime_json.equals(mediaType)) {
			 ObjectMapper m = new ObjectMapper();
			 JsonNode node = m.readTree(in);
			 if (callback!= null) callback.callback(node);
			 ArrayNode data = (ArrayNode)node.get("dataEntry");
			 JsonNode features = node.get("feature");
			 if (data!=null)
			 for (int i=0; i < data.size();i++) {
				 JsonNode compound = data.get(i).get("compound");
				 Compound substance = new Compound(new URL(compound.get("URI").getTextValue()));
				 try {substance.setInChI(compound.get("inchi").getTextValue());} catch (Exception x) {}
				 try {substance.setInChIKey(compound.get("inchikey").getTextValue());} catch (Exception x) {}
				 try {substance.setSMILES(compound.get("smiles").getTextValue());} catch (Exception x) {}
				 try {substance.setFormula(compound.get("formula").getTextValue());} catch (Exception x) {}
				 if (list==null) list = new ArrayList<Compound>();
				 list.add(substance);
				 JsonNode vals = data.get(i).get("values");
				 Iterator<Entry<String,JsonNode>> fields = vals.getFields();
				 while (fields.hasNext()) {
					 Entry<String,JsonNode> field = fields.next();
					 String type = features.get(field.getKey()).get("sameAs").getTextValue();
					 if ("http://www.opentox.org/api/1.1#ChemicalName".equals(type)) {
						 if (!"".equals(field.getValue().getTextValue()))
							 substance.setName(field.getValue().getTextValue());
					 } else if ("http://www.opentox.org/api/1.1#IUPACName".equals(type)) {
						 if (!"".equals(field.getValue().getTextValue()))
							 substance.setIupacName(field.getValue().getTextValue());
					 } else if ("http://www.opentox.org/api/1.1#SMILES".equals(type)) {
						 substance.setSMILES(field.getValue().getTextValue());
					 } else if ("http://www.opentox.org/api/1.1#CASRN".equals(type)) {
						 substance.setCas(field.getValue().getTextValue());
					 } else if ("http://www.opentox.org/api/1.1#EINECS".equals(type)) {
						 substance.setEinecs(field.getValue().getTextValue());						 
					 } else if ("http://www.opentox.org/api/1.1#InChI_std".equals(type)) {
						 substance.setInChI(field.getValue().getTextValue());
					 } else if ("http://www.opentox.org/api/1.1#InChIKey_std".equals(type)) {
						 substance.setInChIKey(field.getValue().getTextValue());
					 } else if ("http://www.opentox.org/api/1.1#REACHRegistrationDate".equals(type)) {
						 //
					 } else if (Compound.opentox_ChEBI.equals(type)) {
						 substance.getProperties().put(type,field.getValue().getTextValue());
					 } else if (Compound.opentox_ChEMBL.equals(type)) {
						 substance.getProperties().put(type,field.getValue().getTextValue());
					 } else if (Compound.opentox_ChemSpider.equals(type)) {
						 substance.getProperties().put(type,field.getValue().getTextValue());						 
					 } else if (Compound.opentox_ToxbankWiki.equals(type)) {
						 substance.getProperties().put(type,field.getValue().getTextValue());
					 } else if (Compound.opentox_CMS.equals(type)) {
						 substance.getProperties().put(type,field.getValue().getTextValue());			 
					 } else if (Compound.opentox_Pubchem.equals(type)) {
						 substance.getProperties().put(type,field.getValue().getTextValue());
					 } else {
						 String key = field.getKey();
						 JsonNode value = field.getValue();
						 substance.getProperties().put(key,(value.getTextValue()==null?Double.toString(value.getDoubleValue()):value.getTextValue()));
					 }
				 }
			 }
			 return list;
		} else if (mime_rdfxml.equals(mediaType)) {
			return super.processPayload(in, mediaType);
		} else if (mime_n3.equals(mediaType)) {
			return super.processPayload(in, mediaType);
		} else if (mime_csv.equals(mediaType)) {
			/*
			Substance substance = new Substance();
			String line = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while ((line = reader.readLine())!=null) {
				QuotedTokenizer st = new QuotedTokenizer(line,',');
				while (st.hasMoreTokens()) header.add(st.nextToken().trim());
				break;
			}
			//QuotedTokenizer tokenizer = new QuotedTokenizer(text, delimiter);
			 */
			return super.processPayload(in, mediaType);
		} else return super.processPayload(in, mediaType);
	}
		
	public RemoteTask registerSubstanceAsync(URL serviceRoot,Compound substance, String customidName,String customidValue) throws InvalidInputException ,Exception {
		URL ref = new URL(String.format("%s/compound",serviceRoot));
		return sendAsync(ref, createFormEntity(substance,customidName,customidValue), HttpPost.METHOD_NAME);
	}
	
	public RemoteTask setSubstancePropertyAsync(URL serviceRoot,Compound substance, String customidName,String customidValue) throws InvalidInputException ,Exception {
		if (substance.getResourceIdentifier()==null) throw new InvalidInputException("No compound URI");
		URL ref = new URL(String.format("%s/compound",serviceRoot));
		return sendAsync(ref, createFormEntity(substance,customidName,customidValue), HttpPut.METHOD_NAME);
	}
	
	protected HttpEntity createFormEntity(Compound substance, String customidName,String customidValue) throws UnsupportedEncodingException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if (substance.getResourceIdentifier()!=null)
			formparams.add(new BasicNameValuePair("compound_uri", substance.getResourceIdentifier().toExternalForm()));
		//formparams.add(new BasicNameValuePair("molfile", ??));
		if (substance.getCas()!=null)
			formparams.add(new BasicNameValuePair(Compound._titles.CASRN.name(), substance.getCas()));
		if (substance.getEinecs()!=null)
			formparams.add(new BasicNameValuePair(Compound._titles.EINECS.name(), substance.getEinecs()));
		if (substance.getName()!=null)
			formparams.add(new BasicNameValuePair(Compound._titles.ChemicalName.name(), substance.getName()));
		if (substance.getInChI()!=null)
			formparams.add(new BasicNameValuePair(Compound._titles.InChI_std.name(), substance.getInChI()));
		if (substance.getInChIKey()!=null)
			formparams.add(new BasicNameValuePair(Compound._titles.InChIKey_std.name(), substance.getInChIKey()));
		if (substance.getIUCLID_UUID()!=null)
			formparams.add(new BasicNameValuePair(Compound._titles.IUCLID5_UUID.name(),substance.getIUCLID_UUID()));
		if ((customidName!=null) && (customidValue!=null)) {
			formparams.add(new BasicNameValuePair("customidname", customidName));
			formparams.add(new BasicNameValuePair("customid", customidValue));
		}	
		return new UrlEncodedFormEntity(formparams, "UTF-8");
	}

	@Override
	public List<Compound> get(URL url, String mediaType, String... params)
			throws RestException, IOException {
		LOGGER.log(Level.INFO, "See API-DOCS at http://ideaconsult.github.io/examples-ambit/apidocs/#!/compound");
		return super.get(url, mediaType, params);
	}
}
