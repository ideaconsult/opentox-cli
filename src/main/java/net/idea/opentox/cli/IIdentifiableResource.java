package net.idea.opentox.cli;

public interface IIdentifiableResource<IDENTIFIER> {
	public void setResourceIdentifier(IDENTIFIER resourceURL);
	public IDENTIFIER getResourceIdentifier();
}
