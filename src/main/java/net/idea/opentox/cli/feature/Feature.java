package net.idea.opentox.cli.feature;

import net.idea.opentox.cli.AbstractURLResource;
import net.idea.opentox.cli.id.IIdentifier;

public class Feature  extends AbstractURLResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6980937106687149654L;
	public Feature() {
		super(null);
	}
	
	public Feature(IIdentifier url) {
		super(url);
	}

	
	protected String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public boolean isNominal() {
		return nominal;
	}
	public void setNominal(boolean nominal) {
		this.nominal = nominal;
	}
	public boolean isNumeric() {
		return numeric;
	}
	public void setNumeric(boolean numeric) {
		this.numeric = numeric;
	}
	public String getSameAs() {
		return sameAs;
	}
	public void setSameAs(String sameAs) {
		this.sameAs = sameAs;
	}
	public boolean isModelPredictionFeature() {
		return isModelPredictionFeature;
	}
	public void setModelPredictionFeature(boolean isModelPredictionFeature) {
		this.isModelPredictionFeature = isModelPredictionFeature;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	protected String units;
	protected boolean nominal;
	protected boolean numeric;
	protected String sameAs;
	protected boolean isModelPredictionFeature;
	protected String creator;
	protected String source;
	protected String type;
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	protected int order;
	//annotation

	@Override
	public String toString() {
		return String.format("%s\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\n",
				getResourceIdentifier(),
				getTitle(),
				getUnits(),
				getSameAs(),
				getSource(),
				getType(),
				isModelPredictionFeature()?"Model result":"",
				isNominal()?"Nominal":"",
				isNumeric()?"Numeric":""
				);
	}
	
}