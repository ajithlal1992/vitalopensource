package com.vtls.opensource.text;

import java.io.ByteArrayInputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler that just keeps the text content from an XML document.
 */
public class XMLTextSourceExtractionHandler extends DefaultHandler
{
	private StringBuffer m_string_buffer;
	
	/**
	 * Class constructor
	 */
	public XMLTextSourceExtractionHandler()
	{
		m_string_buffer = new StringBuffer();
	}

	/**
	 * Returns the content of the handled XML document
	 * @return a string of the xml document
	 */
	public String toString()
	{
		return m_string_buffer.toString();
	}

	//////////////////////////////////////////////////////////////////////////////
	// SAX DefaultHandler methods ////////////////////////////////////////////////
	/**
	 * SAX default startDocument method
	 * @throws SAXException
	 */
	public void startDocument() throws SAXException
	{
	}
	/**
	 * SAX default endDocument method
	 * @throws SAXException
	 */
	public void endDocument() throws SAXException
	{
	}
	/**
	 * SAX default startElement method
	 * @throws SAXException
	 */
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException
	{
	}
	/**
	 * SAX default endElement method
	 * @throws SAXException
	 */
	public void endElement(String namespaceURI, String sName, String qName) throws SAXException
	{
	}
	
	/**
	 * Add a piece of content in the XML document to this string buffer
	 * @param buf an array of characters
	 * @param offset an int represents the start position 
	 * @param len an int represents the length of the selected content 
	 */
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		// Add the String content.
		m_string_buffer.append(new String(buf, offset, len));
	}

	/**
	 *  Ensure that the XML document does not load external DTDs.
	 */
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException
	{
		return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}
}