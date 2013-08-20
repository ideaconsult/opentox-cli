package net.idea.opentox.cli.structure;

import java.net.URL;

/**
 * @deprecated Use {@link Compound}
 * @author nina
 *
 */
public class Substance extends Compound {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1373985175002711907L;

	public Substance() {
		super(null);
	}
	
	public Substance(URL url) {
		super(url);
	}

}
