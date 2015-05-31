package net.idea.opentox.cli.qmrf;

import java.net.URL;

import net.idea.opentox.cli.AbstractURLResource;

public class QMRFDocument extends AbstractURLResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8467085895344386142L;
	protected String title;
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public QMRFDocument() {
		super(null);
	}

	public QMRFDocument(URL url) {
		super(url);
	}

}
