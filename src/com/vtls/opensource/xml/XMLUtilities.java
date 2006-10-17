package com.vtls.opensource.xml;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * A collection of utility functions for the XML JDOM Document. 
 */
public class XMLUtilities
{
	/**
	 * Returns a string representing a JDOM {@link Element}
	 * @param _element a JDOM {@link Element}
	 * @return a string representing the element
	 */
	public static String toString(Element _element)
	{
	   return XMLUtilities.toString(new Document((Element)_element.clone()));
	}
	
	/**
	 * Returns a string representing a JDOM {@link Document}
	 * @param _document a JDOM {@link Document}
	 * @return a string of the Document
	 */
	public static String toString(Document _document)
	{
	   StringWriter _return = new StringWriter();

      try
      {
         XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
         outputter.output(_document, _return);
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
      
      return _return.toString();
   }
	
	/**
	 * Returns a string of a JDOM {@link Element} in the raw format
	 * @param _element a JDOM {@link Element}
	 * @return a string representing the Element's content
	 */
	public static String getContent(Element _element)
	{
	   StringWriter writer = new StringWriter();
	   Document document = new Document((Element)_element.clone());

      // Use the predefined raw Format to process the Document.
      // In the default use, this will give us '<tag>content</tag>'.
      try
      {
         Format format = Format.getRawFormat().setOmitDeclaration(true);
         XMLOutputter outputter = new XMLOutputter(format);
         outputter.output(document, writer);
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
      
      String _return = writer.toString();
      int name_length = _element.getName().length();

      // Trim off the <tag></tag> from the ends of the String and return.
      if(_return.length() >= (name_length * 2) + 5)
         return _return.substring(name_length + 2, _return.length() - (name_length + 5));
      return _return;
   }
}
