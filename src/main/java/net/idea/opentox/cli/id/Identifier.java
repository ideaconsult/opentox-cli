package net.idea.opentox.cli.id;

import java.net.MalformedURLException;
import java.net.URL;

public class Identifier implements IIdentifier {
	protected String id;
	
	public Identifier(String id) {
		this.id = id;
	}
	public Identifier(URL url) {
		this.id = url.toExternalForm();
	}
	@Override
	public IIdentifier url2identifier(URL url) {
		return new Identifier(url);
	}

	@Override
	public String toExternalForm() {
		return id;
	}
	@Override
	public URL toURL() throws MalformedURLException{
		return id == null?null:new URL(id);
	}

	@Override
	public String toString() {
		return id==null?null:id.toString();
	}
	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}
}
