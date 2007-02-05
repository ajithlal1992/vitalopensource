package com.vtls.opensource.utilities;

import java.io.ByteArrayInputStream;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * An EntityResolver that returns blank XML documents.
 */
public class NullEntityResolver implements org.xml.sax.EntityResolver
{
	// Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(TinyJDOMCreationHandler.class);

	/**
    *	Ignore external entities.
	 * @param publicId
	 * @param systemId
	 * @throws SAXException
	 */
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException
	{
		return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}
}