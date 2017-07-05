package net.idea.opentox.cli.structure;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.opentox.rest.RestException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.facet.SubstanceStudyFacet;
import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.Resources;
import net.idea.opentox.cli.id.Identifier;

/**
 * @author nina
 * 
 * @param <POLICY_RULE>
 */
public class SubstanceClient<POLICY_RULE> extends AbstractURIClient<Substance, POLICY_RULE> {
	public SubstanceClient() {
		this(null);
	}

	public SubstanceClient(HttpClient httpclient) {
		super(httpclient);
	}

	@Override
	public List<Substance> processPayload(InputStream in, String mediaType) throws RestException, IOException {
		List<Substance> list = null;
		if (mime_json.equals(mediaType)) {
			ObjectMapper m = new ObjectMapper();
			JsonNode node = m.readTree(in);
			if (callback != null)
				callback.callback(node);
			ArrayNode data = (ArrayNode) node.get("substance");
			if (data != null)
				for (int i = 0; i < data.size(); i++) {
					if (list == null)
						list = new ArrayList<Substance>();
					SubstanceRecord record = parseSubstance(data.get(i));
					Substance substance = new Substance(new Identifier(data.get(i).get("URI").asText()));
					substance.setRecord(record);
					parseSubstanceStudySummary(data.get(i).get("URI").asText(), record,
							(ArrayNode) data.get(i).get("studysummary"));
					list.add(substance);
				}
			return list;
		} else if (mime_rdfxml.equals(mediaType)) {
			return super.processPayload(in, mediaType);
		} else if (mime_n3.equals(mediaType)) {
			return super.processPayload(in, mediaType);
		} else if (mime_csv.equals(mediaType)) {
			/*
			 * Substance substance = new Substance(); String line = null;
			 * BufferedReader reader = new BufferedReader(new
			 * InputStreamReader(in)); while ((line = reader.readLine())!=null)
			 * { QuotedTokenizer st = new QuotedTokenizer(line,','); while
			 * (st.hasMoreTokens()) header.add(st.nextToken().trim()); break; }
			 * //QuotedTokenizer tokenizer = new QuotedTokenizer(text,
			 * delimiter);
			 */
			return super.processPayload(in, mediaType);
		} else
			return super.processPayload(in, mediaType);
	}

	public SubstanceRecord parseSubstance(JsonNode node) {
		if (node == null)
			return null;
		SubstanceRecord record = new SubstanceRecord();
		record.setSubstanceName(node.get(SubstanceRecord.jsonSubstance.name.name()).textValue());
		record.setSubstanceUUID(node.get(SubstanceRecord.jsonSubstance.i5uuid.name()).textValue());
		record.setOwnerName(node.get(SubstanceRecord.jsonSubstance.ownerName.name()).textValue());
		record.setOwnerUUID(node.get(SubstanceRecord.jsonSubstance.ownerUUID.name()).textValue());
		record.setPublicName(node.get(SubstanceRecord.jsonSubstance.publicname.name()).textValue());
		record.setSubstancetype(node.get(SubstanceRecord.jsonSubstance.substanceType.name()).textValue());
		record.setFormat(node.get(SubstanceRecord.jsonSubstance.format.name()).textValue());
		JsonNode subnode = node.get(SubstanceRecord.jsonSubstance.externalIdentifiers.name());
		if (subnode instanceof ArrayNode) {
			ArrayNode ids = (ArrayNode) subnode;
			List<ExternalIdentifier> extids = new ArrayList<ExternalIdentifier>();
			record.setExternalids(extids);
			for (int i = 0; i < ids.size(); i++) {
				if (ids.get(i) instanceof ObjectNode) {
					extids.add(new ExternalIdentifier(((ObjectNode) ids.get(i)).get("type").textValue(),
							((ObjectNode) ids.get(i)).get("id").textValue()));
				}
			}
		}
		//TODO parse composition
		subnode = node.get(SubstanceRecord.jsonSubstance.referenceSubstance.name());
		if (subnode != null) {
			record.setReferenceSubstanceUUID(subnode.get(SubstanceRecord.jsonSubstance.i5uuid.name()).textValue());
		}
		return record;
	}

	public void parseSubstanceStudySummary(String uri, SubstanceRecord record, ArrayNode summary) {
		if (summary != null)
			for (int i = 0; i < summary.size(); i++) {
				JsonNode node = summary.get(i);
				// System.out.println(summary.get(i));
				SubstanceStudyFacet facet = new SubstanceStudyFacet(uri);
				facet.setCount(node.get("count").intValue());
				facet.setInterpretation_result(node.get("interpretation_result").asText());
				facet.setValue(node.get("topcategory").get("title").asText());
				facet.setSubcategoryTitle(node.get("category").get("title").asText());
				// System.out.println(facet.toJSON(uri, null));
				record.addFacet(facet);
			}

	}

	public List<Substance> getSubstancesRelatedToCompound(String base_uri, String compound_uri, boolean withStudySummary)
			throws Exception {
		Identifier url = new Identifier(String.format("%s%s?type=related&compound_uri=%s&studysummary=%s",
				base_uri, Resources.substance,URLEncoder.encode(compound_uri),withStudySummary));
		return super.getJSON(url);
	}
}
