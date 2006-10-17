package com.vtls.opensource.utilities;

import java.io.ByteArrayInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * A collection of utility functions for the XML JDOM Document. 
 */
public class TinyJDOMCreationHandler extends DefaultHandler
{
	// Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(TinyJDOMCreationHandler.class);

	Document m_document = null;

	Element m_element = null;
	Element m_root = null;

	StringBuffer m_string_buffer;

	int m_content_size;
	int m_content_limit;
	int m_node_limit;

	//////////////////////////////////////////////////////////////////////////////
	// Local methods. ////////////////////////////////////////////////////////////
	/**
	 * Class constructor.
	 */
	public TinyJDOMCreationHandler()
	{
		// Initialize the StringBuffer.
		m_string_buffer = new StringBuffer();
		m_content_limit = 1024 * 128;
		m_node_limit = 1024 * 64;
	}

	/**
	 * Get the created JDOM {@link Document}
	 * @return A created JDOM {@link Document}
	 */
	// Get back the final Document after processing.
	public Document getDocument()
	{
		return m_document;
	}

	/**
	 * Set the limit for the size of text nodes.
	 * @param _content_limit  The maximum size of nodes.
	 */
	// Set the limit for the size of text nodes.
	// TODO: Examine how we can do this for document-centric XML.
	public void setContentLimit(int _content_limit)
	{
		m_content_limit = _content_limit;
	}

	//////////////////////////////////////////////////////////////////////////////
	// SAX DefaultHandler methods. ///////////////////////////////////////////////
	/**
	 * Override the default with nothing.
	 */
	public void startDocument() throws SAXException
	{
	}

	/**
	 * Override the default with nothing.
	 */
	public void endDocument() throws SAXException
	{
	}

	/**
	 * Create a new element with all copied attributes
	 * as the start node of the JDOM Document,
	 * @param namespaceURI
	 * @param sName
	 * @param qName
	 * @param attrs
	 * @throws SAXException
	 */
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException
	{
		// Create a new Element.
		m_element = new Element(getLocalName(qName));

		// Copy the Attrributes.	
		for(int i = 0; i < attrs.getLength(); i++)
		{
			String attribute_name = attrs.getQName(i);
			if(attribute_name.startsWith("xmlns"))
			{
				continue;
			}   		   	      
			m_element.setAttribute(getLocalName(attribute_name), attrs.getValue(i));
		}

		if(m_document == null)
		{
			m_document = new Document(m_element);
			m_root = m_document.getRootElement();
		}
		else
		{
			m_root.addContent(m_element);
			m_root = m_element;
		}
	}

	/**
	 * Create a new element with all copied attributes
	 * as the start node of the JDOM Document,
	 * @param namespaceURI the URI of the namespace 
	 * @param sName the 
	 * @param qName
	 * @throws SAXException
	 */
	public void endElement(String namespaceURI, String sName, String qName) throws SAXException
	{
		if(m_string_buffer.toString().length() > 0)
		{
			// Add the text we've been collecting from the characters method.
			m_element.addContent(m_string_buffer.toString().trim());

			// Clear the StringBuffer.
			m_string_buffer.delete(0, m_string_buffer.length());
		}

		// Change the active insertion point to the parent now that we're done with this node.
		Parent parent = m_element.getParent();
		if(parent instanceof Element)
		{
			m_root = m_element = (Element)parent;
		}
	}

	/**
	 * Concate a String that contains characters from an specific array of character,
	 * and ensure the content limit and node limit conditions.
	 * @param buf  array that is the source of characters
	 * @param offset  The initial offset
	 * @param len  The length of the pending string
	 * @throws SAXException 
	 */
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		// Limit the length to x-number of characters.
		// m_logger.info("[" + new String(buf, offset, len) + "]");

		// Prevent adding text beyond the content limit.
		if(m_content_size < m_content_limit)
		{
			// Prevent adding text beyond the node limit.
			if(m_string_buffer.length() < m_node_limit)
			{
				int remaining_characters = m_node_limit - m_string_buffer.length();
				if(len > remaining_characters)
				{
					len = remaining_characters;
				}

				// Add the new String. (trimmed.)
				m_string_buffer.append(new String(buf, offset, len)); // Was: .trim()
				m_content_size += len;
			}
		}
	}

	/**
	 * @param publicId
	 * @param systemId
	 * @throws SAXException
	 */
	// Don't load external DTDs.
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException
	{
		return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}

	///////////////////////////////////////////////////////////////////////////
	// Local Methods //////////////////////////////////////////////////////////

	/**
	 * Get the local name part in the Qname
	 * @qname  a Qname
	 * @return a String of the local name 
	 */
	private static String getLocalName(String qName)
	{
		String _return = qName;

		int position = _return.indexOf(":");
		if(position > 0)
			_return = _return.substring(position + 1);

		return _return;
	}
}