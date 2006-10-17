package com.vtls.opensource.fedora;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class extends {@link DefaultHandler} and may be used to perform SAX
 * parsing of a given SPARQL XML document. This implementation is used to
 * populate a List of {@link FedoraObjectProxy} instances from an ITQL query.
 * @todo Enable ITQL variables other than 'object' to use for the PID.
 */
public class FedoraSPARQLHandler extends DefaultHandler
{
	private List m_list;
	private StringBuffer m_string = null;
	private String m_property =  null;
	private FedoraObjectProxy m_object = null;

	/**
	 * Class constructor.
	 */
	public FedoraSPARQLHandler()
	{
		m_list = new LinkedList();
		m_string = new StringBuffer();
	}

   /**
    * Returns a List of FedoraObjectProxys as given in the XML.
    */
	public List getList()
	{
		return m_list;
	}

	//////////////////////////////////////////////////////////////////////////////
	// SAX DefaultHandler methods ////////////////////////////////////////////////
	
	/**
	 * SAX DefaultHandler method
	 * @throws SAXException
	 */
	public void startDocument() throws SAXException
	{
	}
	
	/**
	 * SAX DefaultHandler method
	 * @throws SAXException
	 */
	public void endDocument() throws SAXException
	{
	}
	
	/**
	 * Receive notification of the start of an element, create 
	 * a new FedoraObjectProxy to handle every "result" element, 
	 * otherwise keep the property name until get to the {@link #endElement} method.
	 * @param attrs The specified or defaulted attributes
	 * @param namespaceURI The Namespace URI mapped to the prefix
	 * @param qName The qualified name
	 * @param sName The local name
	 * @throws SAXException
	 */
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException
	{
	   if(qName == null)
	   {
		   return;
	   }
	      
	   if(qName.equals("result"))
	   {
	      // Create a new FedoraObjectProxy to handle every '<result>'.
	      m_object = new FedoraObjectProxy();
       }
	   else
	   {
   	     // Keep the property name until we get to the {@link endElement} method.
   	     m_property = qName;

   		 // Check the attributes for the object URI.
   		 for(int i = 0; i < attrs.getLength(); i++)
   		 {
   			if(attrs.getQName(i).equals("uri"))
   			{
   			   String pid_uri = attrs.getValue(i);
   			   if(pid_uri != null && pid_uri.length() > 12)
   			   {
	      	      m_object.setProperty(m_property, pid_uri.substring(12));
	      	      
	      	      // Null the property out here so we don't re-set the value.
	      	      // This might not be necessary.
	      	      m_property = null;
      	      }
   		   }
   		 }
	   }
	}
	
	/**
	 * Receives notification of the end of an element, adds the "result"
	 * element to the list of FedoraObjectProxys, otherwise reset the property
	 * and StringBuffer.
	 * @param namespaceURI The Namespace URI mapped to the prefix
	 * @param qName The qualified name
	 * @param sName The local name
	 * @throws SAXException
	 */
	public void endElement(String namespaceURI, String sName, String qName) throws SAXException
	{
	   if(qName == null)
	   {
		   return;
	   }
	      
	   if(qName.equals("result"))
	   {
         m_list.add(m_object);
         m_object = null;
      }
      else
      {
		   if(m_object != null && m_property != null && m_string.length() > 0)
	   	   {
	   	      m_object.setProperty(m_property, m_string.toString().trim());
	   	      m_property = null;
	   	      m_string = null;
	   	      m_string = new StringBuffer();
		   }
      }
	}
	
	/**
	 * Concate a String that contains characters from an specific array of character.
	 * @param buf  array that is the source of characters
	 * @param offset  The initial offset
	 * @param len  The length of the pending string
	 * @throws SAXException 
	 */
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		m_string.append(new String(buf, offset, len));
	}
	
	/**
	 * Override this method to eliminate loading external DTDs.
	 * @param publicId The public identifer, or null if none is available
     * @param systemId The system identifier provided in the XML document
     * @return an {@link InputSource} for an XML entity. 
     * @throws SAXException
	 */
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException
	{
	   // NOTE: We override this method to eliminate loading external DTDs.
		return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}
}