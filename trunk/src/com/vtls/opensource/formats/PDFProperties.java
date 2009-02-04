package com.vtls.opensource.formats;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.lowagie.text.pdf.PdfReader;
/**
 * This class is a collection of properties for a PDF file.
 */
public class PDFProperties extends Properties implements FormatProperties
{
	//	Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(PDFProperties.class);
	private PdfReader m_reader = null;
	
  /**
    * Class constructor specifying the {@link InputStream}.
    * Gets the useful information of the given PDF file and sets 
    * the mapped properties.
    * @param _stream an {@link InputStream} of the source PDF file 
    * @throws IOException 
    */
	public PDFProperties(InputStream _stream) throws IOException
	{
		m_reader = new PdfReader(_stream);

		// Add built-in properties.
		super.putAll(m_reader.getInfo());

		// Number of pages.
		setProperty(FormatProperties.Pages, String.valueOf(m_reader.getNumberOfPages()));

		// Size (in bytes)
		setProperty(FormatProperties.Size, String.valueOf(m_reader.getFileLength()));

		// Encryption
		if(m_reader.isEncrypted())
		{
			setProperty(FormatProperties.Encrypted, "true");
		}

		// Is tampered?
		if(m_reader.isTampered())
		{
			setProperty(FormatProperties.Tampered, "true");
		}

		// Version
		switch(m_reader.getPdfVersion())
		{
		case '5':
			setProperty(FormatProperties.Version, "1.5");
			break;
		case '4':
			setProperty(FormatProperties.Version, "1.4");
			break;
		case '3':
			setProperty(FormatProperties.Version, "1.3");
			break;
		default:
			setProperty(FormatProperties.Version, "1.x");
		break;
		}

		setProperty(FormatProperties.Description, "PDF Document");
		m_reader.close();
	}
	
	/**
	 * Get the xmp metadata for a PDF document.
	 * @return a JDOM {@link Document} representing the xmp metadata
	 * @throws IOException
	 * @throws JDOMException
	 */
	public Document getXMPMetadata() throws IOException, JDOMException
	{
		if(m_reader == null)
		{
			return null;
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(m_reader.getMetadata());

		SAXBuilder builder = new SAXBuilder();
		Document _return = builder.build(stream);
		return _return;
	}
}