package net.idea.opentox.cli.dataset;

import net.idea.opentox.cli.AbstractURLResource;
import net.idea.opentox.cli.id.IIdentifier;

public class Dataset extends AbstractURLResource {
	protected InputData inputData = null;
	public InputData getInputData() {
		return inputData;
	}

	public void setInputData(InputData inputData) {
		this.inputData = inputData;
	}

	protected Metadata metadata = new Metadata();
	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8058860337977827865L;
	public Dataset() {
		super(null);
	}
	
	public Dataset(IIdentifier url) {
		super(url);
	}

	@Override
	public String toString() {
		return String.format("%s\t\"%s\"\t%s\n",getResourceIdentifier(),getMetadata().getTitle(),getMetadata().getSeeAlso());
	}
	
}
