package com.vtls.opensource.formats;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;
import com.vtls.opensource.utilities.XMLUtilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Comment;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This class is a collection of properties for an XML file.
 */
public class XMLProperties extends Properties implements FormatProperties
{
	//	Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(XMLProperties.class);
	private Document m_document = null;
	
  /**
    * Class constructor specifying the {@link InputStream}.
    * Gets the useful information of the given XML file and sets 
    * the mapped properties.
    * @param _stream an {@link InputStream} of the source PDF file 
    * @throws IOException 
    */
	public XMLProperties(InputStream _stream) throws IOException
	{
	   try
	   {
   	   m_document = XMLUtilities.getDocument(_stream);
   	   setProperty(FormatProperties.NamespaceURI, XMLUtilities.getNamespaceURI(m_document));
   	   setProperty(FormatProperties.RootElement, XMLUtilities.getRootElementName(m_document));

         List documentContent = m_document.getContent();
         for (Iterator i = documentContent.iterator(); i.hasNext();)
         {
            Object content = i.next();
            if(content instanceof Comment)
            {
               Comment comment = (Comment)content;
               setProperty(FormatProperties.Comments, comment.getText());
               break;
            }
         }
      }
      catch(java.io.UTFDataFormatException e)
      {
         m_logger.error(e);
         setProperty(FormatProperties.Errors, e.getMessage());
      }
      catch(org.jdom.input.JDOMParseException e)
      {
         setProperty(FormatProperties.Errors, e.getMessage());
      }
      catch(JDOMException e)
      {
         m_logger.error(e);
      }
	}
}