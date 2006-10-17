package com.vtls.opensource.text;

import java.io.InputStream;
import java.io.IOException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.xml.sax.SAXException;

/**
 * Extracts text from XML documents.
 */
public class XMLTextSource extends TextSource implements Text
{
	/**
	 * Class constructor specifying a source {@link InputStream}
	 * @param _stream an input stream of a XML document
	 */
	public XMLTextSource(InputStream _stream)
	{
		// Keep the stream as a member variable.
	   super(_stream);
	}
	
	/**
	 * Gets the text from a XML document
	 * @return a string of the XML document
	 */
	public String getText() throws IOException
	{
		String _return = null;
		
		// Use com.vtls.xml.XMLTextSourceExtractionHandler as the SAX event handler.
		XMLTextSourceExtractionHandler handler = new XMLTextSourceExtractionHandler();

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);

      try
      {
         factory.setFeature("http://xml.org/sax/features/validation", false);

			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(m_stream, handler);
			
			_return = handler.toString();
		}
		catch(ParserConfigurationException pce)
		{
		   pce.printStackTrace();
	   }
	   catch(SAXException se)
	   {
	      se.printStackTrace();
      }
	   finally
	   {
         return _return;
      }
	}
}

