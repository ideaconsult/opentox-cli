package net.idea.opentox.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import net.idea.opentox.cli.OTClient;

/**
 * Main example application
 * @author nina
 *
 */
public class MainApp {
	private static final String title = "AMBIT REST client";
	public static OTClient otclient ;
	
	enum _resource {
		querycompound,
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
	    System.out.println(example3());
	    System.out.println(example4());
	    System.out.println(example5());
	    System.out.println(example6());
	    System.out.println(example7());
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
		/*
		json {
			@Override
			public String getArgName() {
				return "json";
			}
			@Override
			public String getDescription() {
				return "print json  true | false";
			}
			@Override
			public String getShortName() {
				return "j";
			}		
		},
		*/
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
		compound_uri {
			@Override
			public String getArgName() {
				return "uri";
			}
			@Override
			public String getDescription() {
				return "compound_uri";
			}
			@Override
			public String getShortName() {
				return "u";
			}
		},		
		model_uri {
			@Override
			public String getArgName() {
				return "uri";
			}
			@Override
			public String getDescription() {
				return "model_uri";
			}
			@Override
			public String getShortName() {
				return "d";
			}
		},	
		feature_uri {
			@Override
			public String getArgName() {
				return "uri";
			}
			@Override
			public String getDescription() {
				return "feature_uri";
			}
			@Override
			public String getShortName() {
				return "e";
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
		"Retrieve compound JSON representation: \njava -jar mcli.jar %s\n",
		"-s http://localhost:8080/ambit2 -r compound -compound_uri http://localhost:8080/ambit2/compound/100"
		);
	}	
	
	protected static String example2() {
		return String.format(
		"Run model predictions on compound : \njava -jar mcli.jar %s\n",
		"-s http://localhost:8080/ambit2 -r compound --compound_uri http://localhost:8080/ambit2/compound/100 -t predict --model_uri http://localhost:8080/ambit2/model/28"
		);
	}	
	
	protected static String example3() {
		return String.format(
		"Retrieve metadata of first 10 datasets \njava -jar mcli.jar %s\n",
		"-s http://localhost:8080/ambit2 -r dataset -z 10 -p 0 -o test.csv"
		);
	}
	protected static String example4() {
		return String.format(
		"Retrieve first 5 features \njava -jar mcli.jar  %s\n",
		"-s http://localhost:8080/ambit2 -r feature -z 5"
		);
	}
	protected static String example5() {
		return String.format(
		"Retrieve first 10 models \njava -jar mcli.jar %s\n",
		"-s http://localhost:8080/ambit2 -r model -z 10 -p 0 -o test.csv"
		);
	}
	protected static String example6() {
		return String.format(
		"Search similar compounds to c1ccccc1O \njava -jar mcli.jar %s\n",
		"-s http://localhost:8080/ambit2 -r querycompound -z 10 -p 0 -q c1ccccc1O -t similarity"
		);
	}
	
	protected static String example7() {
		return String.format(
		"Search substructure c1ccccc1O \njava -jar mcli.jar %s\n",
		"-s http://localhost:8080/ambit2 -r querycompound -z 10 -p 0 -q c1ccccc1O -t smarts"
		);
	}

}
