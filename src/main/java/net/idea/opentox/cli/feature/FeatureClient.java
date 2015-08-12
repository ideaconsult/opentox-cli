package net.idea.opentox.cli.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.id.IIdentifier;
import net.idea.opentox.cli.id.Identifier;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.opentox.rest.RestException;

public class FeatureClient<POLICY_RULE> extends AbstractURIClient<Feature, POLICY_RULE> {

    public FeatureClient(HttpClient httpclient) {
	super(httpclient);
    }

    @Override
    public List<Feature> processPayload(InputStream in, String mediaType) throws RestException, IOException {
	List<Feature> list = null;
	if (mime_json.equals(mediaType)) {
	    ObjectMapper m = new ObjectMapper();
	    JsonNode node = m.readTree(in);
	    if (callback != null)
		callback.callback(node);
	    JsonNode data = node.get("feature");

	    Iterator<String> i = data.getFieldNames();
	    while (i.hasNext()) {
		String uri = i.next();
		JsonNode metadata = data.get(uri);
		if (list == null)
		    list = new ArrayList<Feature>();
		Feature feature = new Feature(new Identifier(uri));
		list.add(feature);
		try {
		    feature.setTitle(metadata.get("title").getTextValue());
		} catch (Exception x) {
		}
		try {
		    feature.setUnits(metadata.get("units").getTextValue());
		} catch (Exception x) {
		}
		try {
		    feature.setSameAs(metadata.get("sameAs").getTextValue());
		} catch (Exception x) {
		}
		try {
		    feature.setCreator(metadata.get("creator").getTextValue());
		} catch (Exception x) {
		}
		try {
		    feature.setSource(metadata.get("source").get("URI").getTextValue());
		} catch (Exception x) {
		}
		try {
		    feature.setType(metadata.get("source").get("type").getTextValue());
		} catch (Exception x) {
		}

		try {
		    feature.setModelPredictionFeature(metadata.get("isModelPredictionFeature").getBooleanValue());
		} catch (Exception x) {
		}
		try {
		    feature.setNumeric(metadata.get("isNumeric").getBooleanValue());
		} catch (Exception x) {
		}
		try {
		    feature.setNominal(metadata.get("isNominal").getBooleanValue());
		} catch (Exception x) {
		}

	    }
	    return list;
	} else if (mime_rdfxml.equals(mediaType)) {
	    return super.processPayload(in, mediaType);
	} else if (mime_n3.equals(mediaType)) {
	    return super.processPayload(in, mediaType);
	} else if (mime_csv.equals(mediaType)) {
	    return super.processPayload(in, mediaType);
	} else
	    return super.processPayload(in, mediaType);
    }

    @Override
    public List<Feature> get(IIdentifier url, String mediaType, String... params) throws RestException, IOException {
	LOGGER.log(Level.INFO, "See API-DOCS at http://ideaconsult.github.io/examples-ambit/apidocs/#!/feature");
	return super.get(url, mediaType, params);
    }
}
