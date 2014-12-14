package net.idea.opentox.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.idea.opentox.cli.OTClient;
import net.idea.opentox.cli.dataset.Dataset;
import net.idea.opentox.cli.dataset.DatasetClient;
import net.idea.opentox.cli.feature.Feature;
import net.idea.opentox.cli.feature.FeatureClient;
import net.idea.opentox.cli.model.Model;
import net.idea.opentox.cli.model.ModelClient;
import net.idea.opentox.cli.structure.Compound;
import net.idea.opentox.cli.structure.CompoundClient;
import net.idea.opentox.main.MainApp._command;
import net.idea.opentox.main.MainApp._option;
import net.idea.opentox.main.MainApp._resource;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.FileInputState;
import ambit2.core.io.FileOutputState;
import ambit2.core.io.InteractiveIteratingMDLReader;

/**
 * The class that does the work.
 * @author nina
 *
 */
public class AmbitRESTWizard {
	protected NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	enum on_off {
		on {
			@Override
			public boolean getValue() {
				return true;
			}
		},
		off {
			@Override
			public boolean getValue() {
				return false;
			}
		};
		public abstract boolean getValue();
	}
	
	private final static Logger LOGGER = Logger.getLogger(AmbitRESTWizard.class.getName());
	
	enum _querytype { auto,similarity,smarts}; 
	
	protected _querytype querytype = _querytype.auto;
	
	public _querytype getQuerytype() {
		return querytype;
	}
	public void setQuerytype(_querytype querytype) {
		this.querytype = querytype;
	}
	protected String query;
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	protected int page = 0;
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	protected int pagesize = 10;
	protected File file;
	protected File resultFile;
	protected URL base_uri;
	
	public URL getBase_uri() {
		return base_uri;
	}
	public void setBase_uri(URL base_uri) {
		this.base_uri = base_uri;
	}
	protected _resource resource;
	public _resource getResource() {
		return resource;
	}
	public void setResource(_resource resource) {
		this.resource = resource;
	}
	protected _command command;
	
	public _command getCommand() {
		return command;
	}
	public void setCommand(_command command) {
		this.command = command;
	}
	public File getResultFile() {
		return resultFile;
	}
	public void setResultFile(File resultFile) {
		this.resultFile = resultFile;
	}
	
	
	
	public AmbitRESTWizard() {
		LOGGER.setLevel(Level.FINEST);
		//LOGGER.setLevel(Level.OFF);
	}
	/**
	 * 
	 * @return
	 */
	public File getFile() {
		return file;
	}
	/**
	 * 
	 * @param file
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * 
	 * @param option
	 * @param argument
	 * @throws Exception
	 */
	public void setOption(_option option, String argument) throws Exception {
		if (argument!=null) argument = argument.trim();
		switch (option) {
		case file: {
			if ((argument==null) || "".equals(argument.trim())) return;
			setFile(new File(argument));
			break;
		}
		case output: {
			if ((argument==null) || "".equals(argument.trim())) return;
			setResultFile(new File(argument));
			break;			
		}
		case resource: {
			if ((argument==null) || "".equals(argument.trim())) return;
			setResource(_resource.valueOf(argument.trim()));
			break;			
		}
		case command: {
			if ((argument==null) || "".equals(argument.trim())) return;
			setCommand(_command.valueOf(argument.trim()));
			break;			
		}
		case server : {
			if ((argument==null) || "".equals(argument.trim())) return;
			setBase_uri(new URL(argument.trim()));
			break;			
		}
		case query : {
			if ((argument==null) || "".equals(argument.trim())) return;
			setQuery(argument.trim());
			break;			
		}		
		case querytype: {
			if ((argument==null) || "".equals(argument.trim())) return;
			setQuerytype(_querytype.valueOf(argument.trim()));
			break;			
		}		
		case page : {
			if ((argument==null) || "".equals(argument.trim())) return;
			setPage(Integer.parseInt(argument.trim()));
			break;			
		}
		case pagesize : {
			if ((argument==null) || "".equals(argument.trim())) return;
			setPagesize(Integer.parseInt(argument.trim()));
			break;			
		}		
		default:
		}
	}

	protected IIteratingChemObjectReader<IAtomContainer> getReader(InputStream in, String extension) throws CDKException, AmbitIOException {
		FileInputState instate = new FileInputState();
		IIteratingChemObjectReader<IAtomContainer> reader ;
		if (extension.endsWith(FileInputState.extensions[FileInputState.SDF_INDEX])) {
			reader = new InteractiveIteratingMDLReader(in,SilentChemObjectBuilder.getInstance());
			((InteractiveIteratingMDLReader) reader).setSkip(true);
		} else reader = instate.getReader(in,extension);
		
		reader.setReaderMode(Mode.RELAXED);
		reader.setErrorHandler(new IChemObjectReaderErrorHandler() {
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd,
					Exception exception) {
				exception.printStackTrace();
			}
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd) {
				System.out.println(message);
			}
			
			@Override
			public void handleError(String message, Exception exception) {
				exception.printStackTrace();				
			}
			
			@Override
			public void handleError(String message) {
				System.out.println(message);
			}
		});
		return reader;
		
	}
	
	public int processNoFile(OTClient otclient) throws Exception {
		Writer writer = null;
		if (resultFile!=null) writer = new FileWriter(resultFile);
		else writer = new OutputStreamWriter(System.out);
		
		try {
			switch (resource) {
			case feature: {
				
				Bucket bucket = new Bucket();
				bucket.setHeader(featureHeader);
				bucket.headerToCSV(writer,",");writer.write('\n');
				FeatureClient cli = otclient.getFeatureClient();

				URL url = new URL(String.format("%s/feature", getBase_uri().toExternalForm()));
				List<Feature> list = cli.get(url,"application/json","page",Integer.toString(getPage()),"pagesize",Integer.toString(getPagesize()));
				for (Feature feature:  list) {
					bucket.clear();
					sink(feature, bucket);
					bucket.toCSV(writer,",");
					writer.write('\n');
				}
				
				return list.size();	
			}
			case dataset: {
				FeatureClient fcli = otclient.getFeatureClient();
				DatasetClient cli = otclient.getDatasetClient();
				URL url = new URL(String.format("%s/dataset", getBase_uri().toExternalForm()));
				List<Dataset> list = cli.get(url,"application/json","page",Integer.toString(getPage()),"pagesize",Integer.toString(getPagesize()));
				Bucket bucket = new Bucket();
				String[][] h = new String[][]{datasetHeader,featureHeader};
				bucket.setHeaders(h);
				bucket.headerToCSV(writer,",");writer.write('\n');
				for (Dataset dataset : list) {
					
					bucket.clear();
					sink(dataset, bucket);

					URL furl = new URL(String.format("%s/feature", dataset.getResourceIdentifier().toExternalForm()));
					List<Feature> flist = fcli.get(furl,"application/json");
					for (Feature feature:  flist) {
						sink(feature, bucket);
						bucket.toCSV(writer,",");
						writer.write('\n');
					}

					
				}
				
				return list.size();	
			}
			case model: {
				FeatureClient fcli = otclient.getFeatureClient();
				ModelClient cli = otclient.getModelClient();
				URL url = new URL(String.format("%s/model", getBase_uri().toExternalForm()));
				List<Model> list = cli.get(url,"application/json","page",Integer.toString(getPage()),"pagesize",Integer.toString(getPagesize()));
				Bucket bucket = new Bucket();
				String[][] h = new String[][]{modelHeader,featureHeader};
				bucket.setHeaders(h);
				bucket.headerToCSV(writer,",");writer.write('\n');
				for (Model model : list) {
					
					bucket.clear();
					sink(model, bucket);

					URL furl = new URL(String.format("%s/predicted", model.getResourceIdentifier().toExternalForm()));
					List<Feature> flist = fcli.get(furl,"application/json");
					for (Feature feature:  flist) {
						sink(feature, bucket);
						bucket.toCSV(writer,",");
						writer.write('\n');
					}

					
				}
				
				return list.size();	
			}			
			case compound: {
				CompoundClient cli = otclient.getCompoundClient();
				Bucket bucket = new Bucket();
				bucket.setHeader(new String[] {"URL"});
				bucket.headerToCSV(writer,",");writer.write('\n');
				List<URL> list = null;
				switch (querytype) {
				case auto: {
					list = cli.searchExactStructuresURI(getBase_uri(), getQuery());
					break;
				}
				case similarity: {
					list = cli.searchSimilarStructuresURI(getBase_uri(), getQuery(),0.9);
					break;
				}
				case smarts: {
					list = cli.searchSubstructuresURI(getBase_uri(), getQuery());
					break;
				}
				}
				if (list!=null) {
					for (URL url : list) {
						sink(url,bucket);
						bucket.toCSV(writer,",");
						writer.write('\n');
					}
					return list.size();	
				} else return -1;
			}
			}
			throw new Exception("Unsupported resource");
		} catch (Exception x) {
			LOGGER.log(Level.SEVERE,x.getMessage(),x);
			throw x;
		} finally {
			if (writer!=null) { writer.flush(); writer.close();}
		}
	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public int process(OTClient otclient) throws Exception {
		if (file==null) return processNoFile(otclient); 
			//throw new Exception("File not assigned! Use -f command line option.");
		if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
		int records_read = 0;
		int records_processed = 0;
		int records_error = 0;
		String sep = "\t";
		String sep_exc = "\t";
		long beginRecordTime = 0;
		long endRecordTime = 0;
		String generationError = null;

		InputStream in = new FileInputStream(file);

		IIteratingChemObjectReader<IAtomContainer> reader = null;
		
		IChemObjectWriter writer = null;
		writer = createWriter();
		
		if (writer != null)
			System.err.println(writer.getClass().getName());
		
		try 
		{
			reader = getReader(in,file.getName());
			LOGGER.log(Level.INFO, String.format("Reading %s",file.getAbsoluteFile()));
			//LOGGER.log(Level.INFO, String.format("Writing %s tautomer(s)",all?"all":"best"));
			while (reader.hasNext()) 
			{
				beginRecordTime = System.nanoTime();
				/**
				 * Note recent versions allow 
				 * IAtomContainer molecule  = reader.next();
				 */
				IAtomContainer molecule  = reader.next();
				records_read++;
				if (molecule==null) {
					records_error++;
					continue;
				}
				
				
			}//while
		} catch (Exception x1) {
			LOGGER.log(Level.SEVERE, String.format("[Record %d] Error %s\n", records_read, file.getAbsoluteFile()), x1);
			
		} finally {
			try { reader.close(); } catch (Exception x) {}
			if (writer != null)
				try { writer.close(); } catch (Exception x) {}
			
		}
		LOGGER.log(Level.INFO, String.format("[Records read/processed/error %d/%d/%d] %s", 
						records_read,records_processed,records_error,file.getAbsoluteFile()));
		
		return records_read;
	}
	

	protected IChemObjectWriter createWriter() throws Exception {
		if ((resultFile==null) || resultFile.getName().endsWith(FileOutputState.extensions[FileOutputState.SDF_INDEX]))
			return new SDFWriter(new OutputStreamWriter(resultFile==null?System.out:new FileOutputStream(resultFile)));
		else 
			return FileOutputState.getWriter(new FileOutputStream(resultFile),resultFile.getName());
	}

	static final String[] compoundHeader = new String[] { 
		"Compound.URI"};

	
	static final String[] modelHeader = new String[] { 
		"Model.URI","Model.title","Model.algorithm","Model.trainingdataset"};
	
	static final String[] datasetHeader = new String[] { 
		"Dataset.URI","Dataset.title","Dataset.seealso"};

	static final String[] featureHeader = new String[] { 
		 "Feature.URI","Feature.title","Feature.units","Feature.sameas", "Feature.source", "Feature.type","Feature.nominal", "Feature.numeric","Feature.ismodelprediction"};

	protected void sink(Model model, Bucket bucket) {
		bucket.put(modelHeader[0],model.getResourceIdentifier().toExternalForm());
		bucket.put(modelHeader[1],model.getTitle());
		bucket.put(modelHeader[2],model.getAlgorithm());
		bucket.put(modelHeader[3],model.getTrainingDataset());
		
	}
	
	protected void sink(Dataset dataset, Bucket bucket) {
		bucket.put(datasetHeader[0],dataset.getResourceIdentifier().toExternalForm());
		bucket.put(datasetHeader[1],dataset.getMetadata().getTitle());
		bucket.put(datasetHeader[2],dataset.getMetadata().getSeeAlso());
		
	}
	protected void sink(Feature feature, Bucket bucket) {
		bucket.put(featureHeader[0],feature.getResourceIdentifier().toExternalForm());
		bucket.put(featureHeader[1],feature.getTitle());
		bucket.put(featureHeader[2],feature.getUnits());
		bucket.put(featureHeader[3],feature.getSameAs());
		bucket.put(featureHeader[4],feature.getSource());
		bucket.put(featureHeader[5],feature.getType());
		bucket.put(featureHeader[6],feature.isNominal());
		bucket.put(featureHeader[7],feature.isNumeric());
		bucket.put(featureHeader[8],feature.isModelPredictionFeature());

	}
	protected void sink(Compound compound, Bucket bucket) {
		bucket.put(compoundHeader[0],compound.getResourceIdentifier().toExternalForm());
		
	}
	
	protected void sink(URL url, Bucket bucket) {
		bucket.put("URL",url.toExternalForm());
		
	}
}
