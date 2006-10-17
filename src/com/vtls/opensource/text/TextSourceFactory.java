package com.vtls.opensource.text;

import java.io.InputStream;

/**
 * An abstraction for all of this package's text-extracting functionality.
 * Developers should use this instead of importing the specific TextSource
 * classes.
 */
public class TextSourceFactory
{
	private static TextSourceFactory m_instance = null;

	/**
	 * Empty, private constructor.
	 */
	private TextSourceFactory()
	{
	}

	/**
	 * Get the singleton instance of this factory.
	 */
	public static TextSourceFactory getInstance()
	{
		if(m_instance == null)
		{
			m_instance = new TextSourceFactory();
		}
		return m_instance;
	}

	///////////////////////////////////////////////////////////////////////////
	// Local Methods //////////////////////////////////////////////////////////

	/**
	 * This method delivers the appropriate {@link TextSource} for an
	 * InputStream given a MIME type.
	 * @param _stream An InputStream for a document.
	 * @param _mime_type The MIME type for the document.
	 * @return A {@link TextSource} instance.
	 */
	public TextSource newTextSource(InputStream _stream, String _mime_type)
	{
		if(_mime_type == null)
			return null;

		// Check all of the types we can handle.
		if(_mime_type.equals("text/rtf") || _mime_type.equals("application/rtf"))
			return new RTFTextSource(_stream);
		else if(_mime_type.equals("text/html") || _mime_type.equals("text/xhtml"))
			return new HTMLTextSource(_stream);
		else if(_mime_type.equals("text/xml") || _mime_type.equals("application/xml"))
			return new XMLTextSource(_stream);
		else if(_mime_type.equals("application/msword"))
			return new WordTextSource(_stream);
		else if(_mime_type.equals("application/pdf"))
			return new PDFTextSource(_stream);
		else if(_mime_type.equals("text/plain"))
			return new TextSource(_stream);
		else return new TextSource();
	}
}