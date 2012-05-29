package net.idea.opentox.cli;


public class AbstractIdentifiableResource<IDENTIFIER> implements IIdentifiableResource<IDENTIFIER> {
	protected IDENTIFIER identifier;

	public AbstractIdentifiableResource() {
		this(null);
	}
	public AbstractIdentifiableResource(IDENTIFIER identifier) {
		setResourceIdentifier(identifier);
	}
	public IDENTIFIER getResourceIdentifier() {
		return identifier;
	}
	public void setResourceIdentifier(IDENTIFIER identifier) {
		this.identifier = identifier;
	}

}
