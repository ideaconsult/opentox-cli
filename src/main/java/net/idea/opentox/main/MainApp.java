package net.idea.opentox.main;

import net.idea.opentox.cli.OTClient;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * Main example application
 * @author nina
 *
 */
public class MainApp {
	private static final String title = "AMBIT REST client";
	public static OTClient otclient ;
	
	enum _resource {
		feature,
		dataset,
		compound,
		model,
		algorithm,
		task
	}
	
	enum _command {
		get,
		put,
		post,
		delete
	}
	/**
	 * Main 
	 * @param args
	 */
	public static void main(String[] args) {
		MainApp app = new MainApp();
    	int records = app.run(args);
    	//System.err.println("Records processed "+ records);
	}
	
	public int run(String[] args) {
		Options options = createOptions();
    	AmbitRESTWizard worker = new AmbitRESTWizard();
    	final CommandLineParser parser = new PosixParser();
    	otclient = null;
		try {
		    CommandLine line = parser.parse( options, args,false );
		    if (line.hasOption(_option.help.name())) {
		    	printHelp(options, null);
		    	return -1;
		    }
		    	
	    	for (_option o: _option.values()) 
	    		if (line.hasOption(o.getShortName())) try {
	    			worker.setOption(o,line.getOptionValue(o.getShortName()));
	    		} catch (Exception x) {
	    			printHelp(options,x.getMessage());
	    			return -1;
	    		}
	    	otclient = new OTClient();	    		
	    	return worker.process(otclient);	

		} catch (Exception x ) {
			printHelp(options,x.getMessage());
			return -1;
		} finally {
			try { 
				//run whatever cleanup is needed
				//otclient.logout();
				if (otclient != null) otclient.close();
			} catch (Exception xx) {
				printHelp(options,xx.getMessage());
			}
		}
	}
	/**
	 * 
	 * @param options
	 * @param message
	 */
	protected static void printHelp(Options options,String message) {
		if (message!=null) System.out.println(message);
		System.out.println(title);
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp( MainApp.class.getName(), options );
	    System.out.println("Examples:");
	    System.out.println(example1());
	    System.out.println(example2());
	    Runtime.getRuntime().runFinalization();						 
		Runtime.getRuntime().exit(0);	
	}

	/**
	 * 
	 * @return
	 */
	protected static Options createOptions() {
    	Options options = new Options();
    	for (_option o: _option.values()) {
    		options.addOption(o.createOption());
    	}
    	return options;
	}
	/**
	 * 
	 * @author nina
	 *
	 */
	enum _option {

		file {
			@Override
			public String getArgName() {
				return "file";
			}
			@Override
			public String getDescription() {
				return "Input file name ( .sdf | .txt  | .csv | .cml ) - recognised by extension!";
			}
			@Override
			public String getShortName() {
				return "f";
			}
	
		},		
		
		output {
			@Override
			public String getArgName() {
				return "output";
			}
			@Override
			public String getDescription() {
				return "Output file name ( .sdf | .txt  | .csv | .cml | .n3 ) - recognised by extension!";
			}
			@Override
			public String getShortName() {
				return "o";
			}
	
		},				
		media {
			@Override
			public String getArgName() {
				return "MIME";
			}
			@Override
			public String getDescription() {
				return "application/json ; text/csv; next/n3; chemical/x-mdl-file; text/x-arff ; application/rdf+xml";
			}
			@Override
			public String getShortName() {
				return "t";
			}
	
		},	
		server {
			@Override
			public String getArgName() {
				return "uri";
			}
			@Override
			public String getDescription() {
				return "Root server URI e.g. http://localhost:8080/ambit2";
			}
			@Override
			public String getShortName() {
				return "s";
			}
		},	
		resource {
			@Override
			public String getArgName() {
				return "uri";
			}
			@Override
			public String getDescription() {
				return "resource: feature, compound, dataset, model, algorithm: ";
			}
			@Override
			public String getShortName() {
				return "r";
			}
		},	
	
		command {
			@Override
			public String getArgName() {
				return "uri";
			}
			@Override
			public String getDescription() {
				return "get, post, put, delete";
			}
			@Override
			public String getShortName() {
				return "a";
			}
		},			
		query {
			@Override
			public String getArgName() {
				return "query";
			}
			@Override
			public String getDescription() {
				return "search term  e.g. search=50-00-0";
			}
			@Override
			public String getShortName() {
				return "q";
			}
		},	
		querytype {
			@Override
			public String getArgName() {
				return "querytype";
			}
			@Override
			public String getDescription() {
				return "auto | similarity | smarts";
			}
			@Override
			public String getShortName() {
				return "t";
			}
		},			
		page {
			@Override
			public String getArgName() {
				return "pagenumber";
			}
			@Override
			public String getDescription() {
				return "Page number, starts with 0, default 0";
			}
			@Override
			public String getShortName() {
				return "p";
			}
		},	
		
		pagesize {
			@Override
			public String getArgName() {
				return "pagesize";
			}
			@Override
			public String getDescription() {
				return "Page size, default 10";
			}
			@Override
			public String getShortName() {
				return "z";
			}
		},	
		help {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return title;
			}
			@Override
			public String getShortName() {
				return "h";
			}
			@Override
			public String getDefaultValue() {
				return null;
			}
			public Option createOption() {
		    	Option option   = OptionBuilder.withLongOpt(name())
		        .withDescription(getDescription())
		        .create(getShortName());
		    	return option;
			}
		}				
		;
		public abstract String getArgName();
		public abstract String getDescription();
		public abstract String getShortName();
		public String getDefaultValue() { return null; }
			
		public Option createOption() {
			String defaultValue = getDefaultValue();
	    	Option option   = OptionBuilder.withLongOpt(name())
	        .hasArg()
	        .withArgName(getArgName())
	        .withDescription(String.format("%s %s %s",getDescription(),defaultValue==null?"":"Default value: ",defaultValue==null?"":defaultValue))
	        .create(getShortName());

	    	return option;
		}
	}

	/**
	 * 
	 * @return
	 */
	protected static String example1() {
		return String.format(
		"Read file and write all tautomers to the standard out : \njava -jar %s\t-f filename.sdf\n",
		"example-ambit-tautomers-jar-with-dependencies.jar"
		);

	}	
	
	/**
	 * 
	 * @return
	 */
	protected static String example2() {
		return String.format(
		"Read file and write only the best tautomers to an SDF file : \njava -jar %s\t-f filename.sdf -o tautomers.sdf -t best\n",
		"example-ambit-tautomers-jar-with-dependencies.jar"
		);

	}	
}
