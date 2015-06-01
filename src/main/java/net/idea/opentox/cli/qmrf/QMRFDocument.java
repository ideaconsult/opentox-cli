package net.idea.opentox.cli.qmrf;

import java.net.URL;

import net.idea.opentox.cli.AbstractURLResource;

public class QMRFDocument extends AbstractURLResource {

	/**
<pre>
{"qmrf": [
{
	"uri":"http://localhost/qmrf/protocol/Q13-33-0004",
	"visibleid": "Q13-33-0004",
	"identifier": "Q13-33-0004",
	"title": "QSAR for acute toxicity to fish (Danio rerio)",
	"published": true,
	"endpoint": {
		"parentCode" :"3.","parentName" :"Ecotoxic effects","code" :"3.3.", "name" :"Acute toxicity to fish (lethality)"
	},
	"submitted": "Jun 13 2013",
	"updated": "Jun 13 2013",
	"owner": {
		"uri" :"http://ambit.uni-plovdiv.bg:8080/qmrf/user/U68",
		"username": "editor",
		"firstname": "QMRF",
		"lastname": "Editor"
	},
	"attachments": [
	]

}
]
}
</pre>
	 */
	private static final long serialVersionUID = -8467085895344386142L;
	protected String title;
	protected String identifier;
	protected String visibleIdentifier;
	protected boolean published = false;
	
	public boolean isPublished() {
	    return published;
	}

	public void setPublished(boolean published) {
	    this.published = published;
	}

	public String getVisibleIdentifier() {
	    return visibleIdentifier;
	}

	public void setVisibleIdentifier(String visibleIdentifier) {
	    this.visibleIdentifier = visibleIdentifier;
	}

	public String getIdentifier() {
	    return identifier;
	}

	public void setIdentifier(String identifier) {
	    this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public QMRFDocument() {
		super(null);
	}

	public QMRFDocument(URL url) {
		super(url);
	}

}
