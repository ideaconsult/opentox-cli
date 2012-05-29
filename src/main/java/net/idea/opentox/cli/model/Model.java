package net.idea.opentox.cli.model;

import net.idea.opentox.cli.AbstractURLResource;
import net.idea.opentox.cli.algorithm.Algorithm;
import net.idea.opentox.cli.dataset.Dataset;

public class Model extends AbstractURLResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1940040168660924732L;
	protected Algorithm algorithm;
	protected Dataset trainingDataset;
	
	public Algorithm getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}
	public Dataset getTrainingDataset() {
		return trainingDataset;
	}
	public void setTrainingDataset(Dataset trainingDataset) {
		this.trainingDataset = trainingDataset;
	}

}
