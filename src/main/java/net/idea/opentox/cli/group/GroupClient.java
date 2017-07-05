package net.idea.opentox.cli.group;

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
import net.idea.opentox.cli.id.Identifier;

public class GroupClient<POLICY_RULE> extends AbstractURIClient<Group, POLICY_RULE> {

    public GroupClient(HttpClient httpclient) {
	super(httpclient);
    }

    /**
     * <pre>
     * {
     * 	"uri":"http://localhost/qmrf/organisation/G1",
     * 	"id": "G1",
     * 	"groupname": "",
     * 	"title": ""
     * },
     * </pre>
     */

    @Override
    public List<Group> processPayload(InputStream in, String mediaType) throws RestException, IOException {
	List<Group> list = null;
	if (mime_json.equals(mediaType)) {
	    ObjectMapper m = new ObjectMapper();
	    JsonNode node = m.readTree(in);
	    if (callback != null)
		callback.callback(node);
	    ArrayNode data = (ArrayNode) node.get("group");
	    if (data!=null)
	    for (int i = 0; i < data.size(); i++) {
		if (list == null)
		    list = new ArrayList<Group>();
		Group group = new Group();
		group.setResourceIdentifier(new Identifier(data.get(i).get("uri").textValue()));
		try {
		    group.setTitle(data.get(i).get("title").textValue());
		} catch (Exception x) {
		    group.setTitle(null);
		}
		try {
		    group.setIdentifier(data.get(i).get("id").textValue());
		} catch (Exception x) {
		    group.setIdentifier(null);
		}
		try {
		    group.setGroupName(data.get(i).get("groupname").textValue());
		} catch (Exception x) {
		    group.setGroupName(null);
		}
		list.add(group);

	    }
	    return list;
	} else
	    throw new RestException(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

}
