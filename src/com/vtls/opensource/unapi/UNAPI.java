package com.vtls.opensource.unapi;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.ecs.xml.XML;

/**
 * An implementation of the unAPI (Revision 2) specification. This class is
 * designed to provide simple means for creating the XML suitable for use
 * in a service framework.
 * 
 * Source: http://unapi.info/specs/
 *
 * &lt;formats&gt;
 *   &lt;format&gt;
 *     &lt;name&gt;oai_dc&lt;/name&gt;
 *     &lt;type&gt;application/xml&lt;/name&gt; 
 *     &lt;namespace_uri&gt;http://www.openarchives.org/OAI/2.0/oai_dc/&lt;/namespace_uri&gt;
 *     &lt;schema_location&gt;http://www.openarchives.org/OAI/2.0/oai_dc.xsd&lt;/schema_location&gt;
 *   &lt;/format&gt;
 *   &lt;format&gt;
 *     &lt;name&gt;mods&lt;/name&gt;
 *     &lt;type&gt;application/xml&lt;/type&gt; 
 *     &lt;docs&gt;http://www.loc.gov/standards/mods/&lt;/docs&gt;
 *   &lt;/format&gt;
 * &lt;/formats&gt;
 * @author Joe Liversedge <a href="mailto:liversedgej@vtls.com">liversedgej@vtls.com</a>
*/      
public class UNAPI
{
   private static final String FORMATS_ELEMENT = "formats";
   private static final String URI_ELEMENT = "uri";

   private Map m_format_map = null;
   private String m_format_type = null;
   private String m_format_redirect = null;
   private String m_uri = null;
   
   /**
    * Constructor.
    */
   public UNAPI()
   {
      m_format_map = new LinkedHashMap();
   }

   /**
    * Class constructor specifying a URI
    * @param _uri a string of a URI
    */
   public UNAPI(String _uri)
   {
      super();
      setURI(_uri);
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Format Type ////////////////////////////////////////////////////////////

   /**
    * Set the format type and the target for redirection.
    * @param _format_type A {@link String}.
    * @param _format_redirect A {@link String}.
    */
   public void setFormatRedirect(String _format_type, String _format_redirect)
   {
      if(_format_type == null || _format_redirect == null)
      {
    	  throw new IllegalArgumentException("The format type and the redirect URL are required in UNAPI.setFormatType(type, redirect).");
      }
         
      m_format_type = _format_type;
      m_format_redirect = _format_redirect;
   }
   
   /**
    * Get the type of format as stored by {@link #setFormatRedirect}.
    * @return A {@link String} representing the format chosen.
    */
   public String getFormatType()
   {
      return m_format_type;
   }
   
   /**
    * Get the target for redirection, or 'null' if the format is not available.
    * @return A {@link String} representing the redirect location.
    */
   public String getFormatRedirect()
   {
      UNAPIFormat _return = getFormat(m_format_type);
      return (_return != null) ? m_format_redirect : null;
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // URI ////////////////////////////////////////////////////////////////////

   /**
    * Set the given URI.
    * @param _uri A URI to an item.
    */
   public void setURI(String _uri)
   {
      if(_uri != null)
      {
    	  m_uri = _uri;
      }
   }
   
   /**
    * Get the stored URI.
    * @return A {@link String} representation of the URI.
    */
   public String getURI()
   {
      return m_uri;
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Formats ////////////////////////////////////////////////////////////////

   /**
    * Set an {@link UNAPIFormat} object in the list of available formats.
    * @param _format An {@link UNAPIFormat} instance.
    */
   public void setFormat(UNAPIFormat _format)
   {
      m_format_map.put(_format.getProperty(UNAPIFormat.Name), _format);
   }

   /**
    * Return the matching {@link UNAPIFormat} object as previously stored
    * using {@link #setFormat(UNAPIFormat)}.
    * @param _name The name of the format to return.
    * @return An {@link UNAPIFormat} object.
    */
   public UNAPIFormat getFormat(String _name)
   {
      return (UNAPIFormat)(m_format_map.get(_name));
   }
   
   /**
    * Get the XML content of this UNAPI instance.
    * @return An {@link XML} node representing the root of the 'format' element.
    */
   public XML getFormatsXML()
   {
      XML formats = new XML(FORMATS_ELEMENT);
      formats.setPrettyPrint(true);
      
      if(getURI() != null)
      {
         XML uri_element = new XML(URI_ELEMENT).addElement(getURI());
         formats.addElement(uri_element);
      }

      Iterator iterator = m_format_map.values().iterator();
      while(iterator.hasNext())
      {
         UNAPIFormat format = (UNAPIFormat)iterator.next();
         XML format_xml = format.getFormatXML();
         formats.addElement(format_xml);
      }
      
      return formats;
   }
   
   /**
    * Get a {@link Collection} of the stored {@link UNAPIFormat} objects.
    * @return A {@link Collection} suitable for traversal.
    */
   public Collection getFormats()
   {
      return m_format_map.values();
   }
   
   /**
    * An overridden {@link #toString()} method that will return the XML content
    * for the current state of the object.
    * @return A {@link String}.
    */
   public String toString()
   {
      return getFormatsXML().toString();
   }
}
