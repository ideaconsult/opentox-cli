package net.idea.opentox.cli.dataset;

public class Metadata {
	protected String title;
	protected int stars;
	protected String seeAlso;
	protected Rights rights;
	public Rights getRights() {
		return rights;
	}

	public void setRights(Rights rights) {
		this.rights = rights;
	}

	public String getSeeAlso() {
		return seeAlso;
	}

	public void setSeeAlso(String seeAlso) {
		this.seeAlso = seeAlso;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
