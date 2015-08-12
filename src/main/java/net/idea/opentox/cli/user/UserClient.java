package net.idea.opentox.cli.user;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.id.Identifier;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.opentox.rest.RestException;

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
		user.setResourceIdentifier(new Identifier(data.get(i).get("uri").getTextValue()));
		try {
		    user.setTitle(data.get(i).get("title").getTextValue());
		} catch (Exception x) {
		    user.setTitle(null);
		}
		try {
		    user.setIdentifier(data.get(i).get("id").getTextValue());
		} catch (Exception x) {
		    user.setIdentifier(null);
		}
		try {
		    user.setUserName(data.get(i).get("username").getTextValue());
		} catch (Exception x) {
		    user.setUserName(null);
		}
		try {
		    user.setFirstName(data.get(i).get("firstname").getTextValue());
		} catch (Exception x) {
		    user.setFirstName(null);
		}		
		try {
		    user.setLastName(data.get(i).get("lastname").getTextValue());
		} catch (Exception x) {
		    user.setLastName(null);
		}
		
		try {
		    user.setHomepage(data.get(i).get("homepage").getTextValue());
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
