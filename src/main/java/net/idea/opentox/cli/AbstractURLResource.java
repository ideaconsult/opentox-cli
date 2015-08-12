package net.idea.opentox.cli;

import java.io.Serializable;

import net.idea.opentox.cli.id.IIdentifier;

public class AbstractURLResource extends AbstractIdentifiableResource<IIdentifier> implements  Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6625229611953919751L;
	public AbstractURLResource() {
		this(null);
	}
	public AbstractURLResource(IIdentifier identifier) {
		setResourceIdentifier(identifier);
	}
	@Override
	public String toString() {
		return (getResourceIdentifier()==null)?null:getResourceIdentifier().toString();
	}
}
