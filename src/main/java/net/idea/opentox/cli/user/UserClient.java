package net.idea.opentox.cli.user;

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

public class UserClient<POLICY_RULE> extends AbstractURIClient<User, POLICY_RULE> {

    public UserClient(HttpClient httpclient) {
	super(httpclient);
    }


/**
<pre>
{"user": [
{
	"uri":"http://localhost/app/user/U666",
	"id": "U666",
	"username": "admin",
	"title": "",
	"firstname": "Admin",
	"lastname": "User",
	"email": "your@email.com",
	"homepage": "",
	"keywords": "",
	"reviewer": true,
	"status": "confirmed",
	"organisation": [
		{
		"uri":"http://lcoalhost/app/organisation/G1",
		"title": ""
		}
	]
}
]
}
</pre>
 */

    @Override
    public List<User> processPayload(InputStream in, String mediaType) throws RestException, IOException {
	List<User> list = null;
	if (mime_json.equals(mediaType)) {
	    ObjectMapper m = new ObjectMapper();
	    JsonNode node = m.readTree(in);
	    if (callback != null)
		callback.callback(node);
	    ArrayNode data = (ArrayNode) node.get("user");
	    if (data!=null)
	    for (int i = 0; i < data.size(); i++) {
		if (list == null)
		    list = new ArrayList<User>();
		User user = new User();
		user.setResourceIdentifier(new Identifier(data.get(i).get("uri").textValue()));
		try {
		    user.setTitle(data.get(i).get("title").textValue());
		} catch (Exception x) {
		    user.setTitle(null);
		}
		try {
		    user.setIdentifier(data.get(i).get("id").textValue());
		} catch (Exception x) {
		    user.setIdentifier(null);
		}
		try {
		    user.setUserName(data.get(i).get("username").textValue());
		} catch (Exception x) {
		    user.setUserName(null);
		}
		try {
		    user.setFirstName(data.get(i).get("firstname").textValue());
		} catch (Exception x) {
		    user.setFirstName(null);
		}		
		try {
		    user.setLastName(data.get(i).get("lastname").textValue());
		} catch (Exception x) {
		    user.setLastName(null);
		}
		
		try {
		    user.setHomepage(data.get(i).get("homepage").textValue());
		} catch (Exception x) {
		    user.setLastName(null);
		}		
		list.add(user);

	    }
	    return list;
	} else
	    throw new RestException(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

}
