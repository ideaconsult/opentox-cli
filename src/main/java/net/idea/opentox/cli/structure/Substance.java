package net.idea.opentox.cli.structure;

import net.idea.opentox.cli.AbstractURLResource;
import net.idea.opentox.cli.id.IIdentifier;
import ambit2.base.data.SubstanceRecord;

/**
 * @author nina
 *
 */
public class Substance extends AbstractURLResource {
	private SubstanceRecord record;
	public SubstanceRecord getRecord() {
		return record;
	}

	public void setRecord(SubstanceRecord record) {
		this.record = record;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1373985175002711907L;

	public Substance() {
		super(null);
	}
	
	public Substance(IIdentifier url) {
		super(url);
	}

}
