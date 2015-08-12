package net.idea.opentox.cli.id;

import java.net.MalformedURLException;
import java.net.URL;

public interface IIdentifier {
	IIdentifier url2identifier(URL url);
	String toExternalForm();
	URL toURL() throws MalformedURLException;
}
