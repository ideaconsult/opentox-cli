package net.idea.opentox.cli.dataset.alerts;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.idea.opentox.cli.AbstractURIClient;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.opentox.rest.RestException;

public class AlertClient<POLICY_RULE> extends AbstractURIClient<Alert, POLICY_RULE> {

    public AlertClient(HttpClient httpclient) {
	super(httpclient);
    }

    @Override
    public List<Alert> processPayload(InputStream in, String mediaType) throws RestException, IOException {
	List<Alert> list = null;
	if (mime_json.equals(mediaType)) {
	    ObjectMapper m = new ObjectMapper();
	    JsonNode node = m.readTree(in);
	    if (callback != null)
		callback.callback(node);
	    ArrayNode data = (ArrayNode)node.get("alert");
	    for (int i =0; i < data.size(); i++) {
		if (list == null)
		    list = new ArrayList<Alert>();
		Alert alert = new Alert();
		alert.setResourceIdentifier(new URL(data.get(i).get("uri").getTextValue()));
		list.add(alert);
		//TODO

	    }
	    return list;
	} else throw new RestException(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    public List<Alert> get(URL url, String mediaType, String... params) throws RestException, IOException {
	return super.get(url, mediaType, params);
    }
}
