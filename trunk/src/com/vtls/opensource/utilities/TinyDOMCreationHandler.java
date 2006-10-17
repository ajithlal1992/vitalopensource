package com.vtls.opensource.utilities;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

public class TinyDOMCreationHandler extends DefaultHandler
{
	// Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(TinyDOMCreationHandler.class);

	Document m_document = null;
	Node m_element = null;
	StringBuffer m_string_buffer;

	int m_content_size;
	int m_content_limit;
	int m_node_limit;

	//////////////////////////////////////////////////////////////////////////////
	// Local methods. ////////////////////////////////////////////////////////////

	/**
	 * Class constructor.
	 */
	public TinyDOMCreationHandler()
	{
		// Initialize the StringBuffer.
		m_string_buffer = new StringBuffer();
		m_content_limit = 1024 * 128;
		m_node_limit = 1024 * 64;

		try
		{
			// Create the Document.
			DocumentBuilderFactory builder_factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builder_factory.newDocumentBuilder();
			m_document = builder.newDocument();

			// Set the active insertion point to the Document proper.
			m_element = m_document;
		}
		catch(Exception e)
		{
			m_logger.error(e);
		}
	}

	/**
	 * Get the created DOM document.
	 */
	// Get back the final Document after processing.
	public Document getDocument()
	{
		return m_document;
	}

	/**
	 * Set the limit for the size of text nodes.
	 * @param _content_limit  the limit size of text nodes
	 */
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
	 * @param namespaceURI the URI of the namespace
	 * @param sName 
	 * @param qName
	 * @param attrs
	 * @throws SAXException
	 */
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException
	{
		Element element = m_document.createElement(qName);

		// Copy the Attrributes over.	
		for(int i = 0; i < attrs.getLength(); i++)
		{
			element.setAttribute(attrs.getQName(i), attrs.getValue(i));
		}

		// Change the active insertion point to the new node.
		m_element = m_element.appendChild(element);
	}

	/**
	 * Create a new element with all copied attributes
	 * as the start node of the JDOM Document,
	 * @param namespaceURI
	 * @param sName
	 * @param qName
	 * @throws SAXException
	 */
	public void endElement(String namespaceURI, String sName, String qName) throws SAXException
	{
		// Add the text we've been collecting from the characters method.
		Text element = m_document.createTextNode(m_string_buffer.toString().trim());
		m_element.appendChild(element);

		// Clear the StringBuffer.
		m_string_buffer.delete(0, m_string_buffer.length());

		// Change the active insertion point to the parent now that we're done with this node.
		m_element = m_element.getParentNode();
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

		// Prevent adding text beyond the content limit.
		if(m_content_size < m_content_limit)
		{
			// Prevent adding text beyond the node limit.
			if(m_string_buffer.length() < m_node_limit)
			{
				int remaining_characters = m_node_limit - m_string_buffer.length();
				if(len > remaining_characters)
					len = remaining_characters;

				// Add the new String. (trimmed.)
				m_string_buffer.append(new String(buf, offset, len)); // Was: .trim()
				m_content_size += len;
			}
		}
	}

	// Don't load external DTDs.
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException
	{
		return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}
}