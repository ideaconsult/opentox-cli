package net.idea.opentox.cli.dataset;

import java.io.File;

import net.idea.opentox.cli.dataset.DatasetClient._MATCH;

/**
 * Used to provide file to upload for POST /dataset 
 * @author nina
 *
 */
public class InputData {
	protected File inputFile;
	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	
	public InputData(File fileToImport, _MATCH matchMode) {
		setInputFile(fileToImport);
		setImportMatchMode(matchMode);
	}
	

	protected DatasetClient._MATCH importMatchMode = _MATCH.CAS;
	
	public DatasetClient._MATCH getImportMatchMode() {
		return importMatchMode;
	}

	public void setImportMatchMode(DatasetClient._MATCH importMatchMode) {
		this.importMatchMode = importMatchMode;
	}
}
