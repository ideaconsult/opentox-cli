package net.idea.opentox.cli;

public interface IIdentifiableResource<IDENTIFIER> {
	public void setResourceIdentifier(IDENTIFIER identifier);
	public IDENTIFIER getResourceIdentifier();
}
