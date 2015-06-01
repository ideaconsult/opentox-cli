package net.idea.opentox.cli.structure;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.idea.opentox.cli.AbstractURIClient;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.opentox.rest.RestException;


/**
 * @author nina
 *
 * @param <POLICY_RULE>
 */
public class SubstanceClient<POLICY_RULE> extends AbstractURIClient<Substance,POLICY_RULE> {
	public SubstanceClient() {
		this(null);
	}
		
	public SubstanceClient(HttpClient httpclient) {
		super(httpclient);
	}
	@Override
	public List<Substance> processPayload(InputStream in, String mediaType)
			throws RestException, IOException {
		List<Substance> list = null;
		if (mime_json.equals(mediaType)) {
			 ObjectMapper m = new ObjectMapper();
			 JsonNode node = m.readTree(in);
			 if (callback!= null) callback.callback(node);
			 ArrayNode data = (ArrayNode)node.get("substance");
			 if (data!=null)
			 for (int i=0; i < data.size();i++) {
				 if (list==null) list = new ArrayList<Substance>();
				 list.add(new Substance());
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
}
