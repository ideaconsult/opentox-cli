package net.idea.opentox.cli.qmrf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.opentox.rest.RestException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.idea.opentox.cli.AbstractURIClient;
import net.idea.opentox.cli.id.Identifier;

public class QMRFDocumentClient<POLICY_RULE> extends AbstractURIClient<QMRFDocument, POLICY_RULE> {

    public QMRFDocumentClient(HttpClient httpclient) {
	super(httpclient);
    }

    /**
     * <pre>
     * {"qmrf": [
     * {
     * 	"uri":"http://localhost:8081/qmrf/protocol/Q14-33-0001",
     * 	"visibleid": "Q14-33-0001",
     * 	"identifier": "Q14-33-0001",
     * 	"title": "QSAR for acute toxicity to fish (Danio rerio)",
     * 	"published": true,
     * 	"endpoint": {
     * 		"parentCode" :"3.","parentName" :"Ecotoxic effects","code" :"3.3.", "name" :"Acute toxicity to fish (lethality)"
     * 	},
     * 	"submitted": "Nov 16 2014",
     * 	"updated": "Nov 16 2014",
     * 	"owner": {
     * 		"uri" :"http://localhost:8081/qmrf/user/U123",
     * 		"username": "editor",
     * 		"firstname": "A",
     * 		"lastname": "B"
     * 	},
     * 	"attachments": [
     * 	]
     * 
     * }
     * ]
     * }
     * </pre>
     */
    @Override
    public List<QMRFDocument> processPayload(InputStream in, String mediaType) throws RestException, IOException {
	List<QMRFDocument> list = null;
	if (mime_json.equals(mediaType)) {
	    ObjectMapper m = new ObjectMapper();
	    JsonNode node = m.readTree(in);
	    if (callback != null)
		callback.callback(node);
	    ArrayNode data = (ArrayNode) node.get("qmrf");
	    if (data != null)
		for (int i = 0; i < data.size(); i++) {
		    JsonNode docNode = data.get(i).get("uri");
		    QMRFDocument document = new QMRFDocument(new Identifier(docNode.textValue()));
		    try {
			document.setTitle(docNode.get("title").textValue());
		    } catch (Exception x) {
		    }
		    try {
			document.setIdentifier(docNode.get("identifier").textValue());
		    } catch (Exception x) {
		    }
		    try {
			document.setVisibleIdentifier(docNode.get("visibleIdentifier").textValue());
		    } catch (Exception x) {
		    }
		    if (list == null)
			list = new ArrayList<QMRFDocument>();
		    list.add(document);
		}
	    return list;
	    /*
	     * } else if (mime_rdfxml.equals(mediaType)) { return
	     * super.processPayload(in, mediaType); } else if
	     * (mime_n3.equals(mediaType)) { return super.processPayload(in,
	     * mediaType);
	     */
	} else if (mime_uri.equals(mediaType)) {
	    BufferedReader r = new BufferedReader(new InputStreamReader(in));
	    String line = null;
	    while ((line = r.readLine()) != null) {
		if (list == null)
		    list = new ArrayList<QMRFDocument>();
		QMRFDocument c = new QMRFDocument(new Identifier(line.trim()));
		list.add(c);

	    }
	    return list;

	} else
	    return super.processPayload(in, mediaType);
    }
}
