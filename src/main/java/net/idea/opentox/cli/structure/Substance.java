package net.idea.opentox.cli.structure;

import java.net.URL;
import java.util.Hashtable;

import net.idea.opentox.cli.AbstractURLResource;

public class Substance extends AbstractURLResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6291149605331598909L;
	protected int idchemical;
	protected int idstructure;
	protected Hashtable<String, String> properties; 
	public enum _titles { //from OpenTox.owl
		Compound,
		CASRN {
			@Override
			public String getTitle() {
				return "CASRN";
			}
		},
		EINECS,
		IUPACName {
			@Override
			public String getTitle() {
				return "IUPAC name";
			}
		},
		ChemicalName {
			@Override
			public String getTitle() {
				return "Chemical Name";
			}
		},
		SMILES,
		InChI_std {
			@Override
			public String getTitle() {
				return "Standard InChI";
			}
		},
		InChIKey_std {
			@Override
			public String getTitle() {
				return "Standard InChI key";
			}
		},
		REACHRegistrationDate {
			@Override
			public String getTitle() {
				return "REACH registration date";
			}
		};
		public String getTitle() {
			return name();
		}
		@Override
		public String toString() {
			return getTitle();
		}
	}	

	public Substance() {
		super(null);
	}
	
	public Substance(URL url) {
		super(url);
	}
	
	
	public Hashtable<String, String> getProperties() {
		if (properties==null) properties = new Hashtable<String, String>();
		return properties;
	}
	public void setProperties(Hashtable<String, String> properties) {
		this.properties = properties;
	}
	public int getIdchemical() {
		return idchemical;
	}
	public void setIdchemical(int idchemical) {
		this.idchemical = idchemical;
	}

	public int getIdstructure() {
		return idstructure;
	}
	public void setIdstructure(int idstructure) {
		this.idstructure = idstructure;
	}
	String name;
	String cas;
	String einecs;
	String SMILES;
	String InChI;
	String InChIKey;
	
	protected String similarity = null;
	public String getSimilarity() {
		return similarity;
	}
	public void setSimilarity(String similarity) {
		this.similarity = similarity;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCas() {
		return cas;
	}
	public void setCas(String cas) {
		if (cas==null) this.cas = null;
		else {
			int index = cas.indexOf("|");
			this.cas = index>0?cas.substring(0,index):cas;
		}
	}
	public String getEinecs() {
		return einecs;
	}
	public void setEinecs(String einecs) {
		this.einecs = einecs;
	}
	public String getSMILES() {
		return SMILES;
	}
	public void setSMILES(String sMILES) {
		SMILES = sMILES;
	}
	public String getInChI() {
		return InChI;
	}
	public void setInChI(String inChI) {
		InChI = inChI;
	}
	
	public String getInChIKey() {
		return InChIKey;
	}
	public void setInChIKey(String inChIKey) {
		InChIKey = inChIKey;
	}
	/*
	public Object[] parseURI(String baseReference)  {
		return OpenTox.URI.conformer.getIds(getResourceURL().toString(),baseReference);

	}
	*/
	private URL resourceURL;
	private String title;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setResourceIdentifier(URL resourceURL) {
		this.resourceURL = resourceURL;
	}

	public URL getResourceIdentifier() {
		return resourceURL;
	}
	
}
