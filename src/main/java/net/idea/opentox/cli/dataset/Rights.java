package net.idea.opentox.cli.dataset;

public class Rights {
	public enum _type {rights,license};
	protected String rightsHolder;
	protected _type type;
	protected String URI;
	
	public Rights() {
	}
	
	public Rights(String rightsHolder, String URI, _type type) {
		setRightsHolder(rightsHolder);
		setURI(URI);
		setType(type);
	}

	public String getRightsHolder() {
		return rightsHolder;
	}
	public void setRightsHolder(String rightsHolder) {
		this.rightsHolder = rightsHolder;
	}
	public _type getType() {
		return type;
	}
	public void setType(_type type) {
		this.type = type;
	}
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}

}
