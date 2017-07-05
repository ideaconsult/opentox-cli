package net.idea.opentox.cli.dataset.alerts;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.opentox.rest.RestException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.id.IIdentifier;
import net.idea.opentox.cli.id.Identifier;

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
		alert.setResourceIdentifier(new Identifier(data.get(i).get("uri").textValue()));
		list.add(alert);
		//TODO

	    }
	    return list;
	} else throw new RestException(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    public List<Alert> get(IIdentifier url, String mediaType, String... params) throws RestException, IOException {
	return super.get(url, mediaType, params);
    }
}
