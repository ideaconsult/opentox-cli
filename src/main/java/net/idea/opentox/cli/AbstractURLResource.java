package net.idea.opentox.cli;

import java.io.Serializable;
import java.net.URL;

public class AbstractURLResource extends AbstractIdentifiableResource<URL> implements  Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6625229611953919751L;
	public AbstractURLResource() {
		this(null);
	}
	public AbstractURLResource(URL identifier) {
		setResourceIdentifier(identifier);
	}
	@Override
	public String toString() {
		return (getResourceIdentifier()==null)?null:getResourceIdentifier().toString();
	}
}
