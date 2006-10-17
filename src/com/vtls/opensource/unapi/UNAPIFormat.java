package com.vtls.opensource.unapi;

import java.util.Properties;
import org.apache.ecs.xml.XML;

/**
 * An abstraction of an individual 'format' in the unAPI framework.
 * From: http://unapi.info/specs/
 *
 *   &lt;format&gt;
 *     &lt;name&gt;oai_dc&lt;/name&gt;
 *     &lt;type&gt;application/xml&lt;/name&gt; 
 *     &lt;namespace_uri&gt;http://www.openarchives.org/OAI/2.0/oai_dc/&lt;/namespace_uri&gt;
 *     &lt;schema_location&gt;http://www.openarchives.org/OAI/2.0/oai_dc.xsd&lt;/schema_location&gt;
 *   &lt;/format&gt;

 * @author Joe Liversedge <a href="mailto:liversedgej@vtls.com">liversedgej@vtls.com</a>
 *
 */
public class UNAPIFormat extends Properties
{
	private static final String FORMAT_ELEMENT = "format";

	public static final String Name = "name";
	public static final String Type = "type";
	public static final String Docs = "docs";
	public static final String NamespaceURI = "namespace_uri";
	public static final String SchemaLocation = "schema_location";

	/**
	 * Constructor.  Verfies the parameters arne't null and stores them.
	 * @param _name The name of the format of the file.
	 * @param _mime_type The mimeType of the file.
	 */
	public UNAPIFormat(String _name, String _mime_type)
	{
		if(_name == null || _mime_type == null)
			throw new IllegalArgumentException("The 'name' and 'type' are required properties of UNAPIFormat.");

		setProperty(UNAPIFormat.Name, _name);
		setProperty(UNAPIFormat.Type, _mime_type);
	}

	/**
	 * Set a property attribute. This is an extension of the {@link #setProperty(String, String)} method.
	 * @param _name The name of the property to set.
	 * @param _value The value of the property.
	 */
	public UNAPIFormat setAttribute(String _name, String _value)
	{
		setProperty(_name, _value);
		return this;
	}

	/**
	 * Returns a string of the XML content for this UNAPIFormat.
	 * @return A {@link String}.
	 */
	public String toString()
	{
		XML xml = getFormatXML();
		return xml.toString();
	}

	/**
	 * Return the XML content for this UNAPIFormat.
	 * @return An {@link XML} node representing the root of the 'format' element.
	 */
	public XML getFormatXML()
	{
		XML format = new XML(FORMAT_ELEMENT);
		format.setPrettyPrint(true);

		format.addElement(new XML(UNAPIFormat.Name).addElement(getProperty(UNAPIFormat.Name)));
		format.addElement(new XML(UNAPIFormat.Type).addElement(getProperty(UNAPIFormat.Type)));

		if(getProperty(UNAPIFormat.Docs) != null)
		{
			format.addElement(new XML(UNAPIFormat.Docs).addElement(getProperty(UNAPIFormat.Docs)));
		}

		if(getProperty(UNAPIFormat.NamespaceURI) != null)
		{
			format.addElement(new XML(UNAPIFormat.NamespaceURI).addElement(getProperty(UNAPIFormat.NamespaceURI)));
		}

		if(getProperty(UNAPIFormat.SchemaLocation) != null)
		{
			format.addElement(new XML(UNAPIFormat.SchemaLocation).addElement(getProperty(UNAPIFormat.SchemaLocation)));
		}

		return format;
	}
}
