package net.idea.opentox.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
		try {
			switch (resource) {
			case feature: {
				FeatureClient cli = otclient.getFeatureClient();
				URL url = new URL(String.format("%s/feature", getBase_uri().toExternalForm()));
				List<Feature> list = cli.get(url,"application/json","page",Integer.toString(getPage()),"pagesize",Integer.toString(getPagesize()));
				for (Feature feature:  list) {

					System.out.print(feature.toString());
				}
				
				return -1;	
			}
			case dataset: {
				FeatureClient fcli = otclient.getFeatureClient();
				DatasetClient cli = otclient.getDatasetClient();
				URL url = new URL(String.format("%s/dataset", getBase_uri().toExternalForm()));
				List<Dataset> list = cli.get(url,"application/json","page",Integer.toString(getPage()),"pagesize",Integer.toString(getPagesize()));
				for (Dataset dataset : list) {
					
					System.out.print(dataset.toString());
					URL furl = new URL(String.format("%s/feature", dataset.getResourceIdentifier().toExternalForm()));
					List<Feature> flist = fcli.get(furl,"application/json","page",Integer.toString(getPage()),"pagesize",Integer.toString(getPagesize()));
					for (Feature feature:  flist) {

						System.out.print(feature.toString());
					}

					
				}
				
				return -1;	
			}
			case compound: {
				CompoundClient cli = otclient.getCompoundClient();
				List<URL> list = cli.searchExactStructuresURI(getBase_uri(), "50-00-0");
				System.out.println(list);
			}
			}
			throw new Exception("Unsupported resource");
		} catch (Exception x) {
			LOGGER.log(Level.SEVERE,x.getMessage(),x);
			throw x;
		} finally {
			
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

}
