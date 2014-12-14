package net.idea.opentox.cli.dataset;

import java.net.URL;

import net.idea.opentox.cli.AbstractURLResource;

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
	
	public Dataset(URL url) {
		super(url);
	}

	@Override
	public String toString() {
		return String.format("%s\t\"%s\"\t%s\n",getResourceIdentifier(),getMetadata().getTitle(),getMetadata().getSeeAlso());
	}
	
}
