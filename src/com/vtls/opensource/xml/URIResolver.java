package com.vtls.opensource.xml;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.vtls.opensource.utilities.StreamUtilities;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * This class is used to resolve imports in stylesheets.
 * Its constructor can take a parameter which will tell
 * the classloader where to look for the file.
 */
public class URIResolver implements javax.xml.transform.URIResolver {
	
	/**
	 * The logger.
	 */
	private static final Logger m_logger = Log4JLogger.getLogger(URIResolver.class);
	
	/**
	 * The path inside the classloader.
	 */
	protected String path = null;
	
	/**
	 * Default with no path.  Stylesheets must be in the root of a JAR file.
	 */
	public URIResolver() {
	}
	
	/**
	 * Sets the path.  Stylesheets must be in that path in a JAR file.
	 * 
	 * @param _path The path inside a JAR file where the stylesheet(s) reside(s).
	 */
	public URIResolver(String _path) {
		path = _path;
	}
	
	/**
	 * Returns a SourceStream of the stylesheet.
	 * 
	 * @param _href 
	 * @param _base
	 * 
	 * @return A StreamSource containing the stylesheet.
	 */
	public Source resolve(String _href, String _base) {
		try {
			//Copy the _href.
			String href = _href;
			//We only want the filename.
			if (href.contains("/")) {
				href = href.substring(href.lastIndexOf("/") + 1);
			}
			//Can't really have a null path.
			if (path == null) {
				//Set it to "".
				path = "";
			}
			//Path needs to end with /
			if (!path.endsWith("/")) {
				path = path + "/";
			}
			//Grab the InputStream to the stylesheet and return it.
			return new StreamSource(StreamUtilities.getInputStreamFromResource(path + href));
		} catch (IOException ioe) {
			m_logger.error(ioe);
		}
		return null;
	}
}