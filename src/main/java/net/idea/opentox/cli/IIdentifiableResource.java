package net.idea.opentox.cli;

public interface IIdentifiableResource<IIdentifier> {
	public void setResourceIdentifier(IIdentifier identifier);
	public IIdentifier getResourceIdentifier();
}
