package com.vtls.opensource.formats;

import com.vtls.opensource.logging.Log4JLogger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
// import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.DocumentProperties;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * This class is a collection of properties for Microsoft Document files.
 */
public class MicrosoftDocumentProperties extends Properties implements FormatProperties
{
	//	Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(MicrosoftDocumentProperties.class);

	private HWPFDocument m_document = null;
	private POIFSFileSystem m_file_system = null;

	/**
	 * Class constructor specifying the source file datastream.
	 * Gets the useful information of the given file and sets 
	 * the mapped properties.
	 * @param _stream an {@link InputStream} of the source Microsoft document
	 * @throws IOException
	 */
	public MicrosoftDocumentProperties(final InputStream _stream) throws IOException
	{
		m_file_system = new POIFSFileSystem(_stream);
		m_document = new HWPFDocument(m_file_system);

		DocumentProperties properties = m_document.getDocProperties();
		DirectoryEntry dir = m_file_system.getRoot();

		try
		{
			Iterator iterator = dir.getEntries();
			while(iterator.hasNext())
			{
				Entry entry = (Entry)iterator.next();
				if(entry.isDocumentEntry() && entry.getName().endsWith("SummaryInformation"))
				{
					DocumentInputStream document_stream = new DocumentInputStream((DocumentEntry)entry);
					PropertySet property_set = new PropertySet(document_stream);
					document_stream.close();

					if(property_set.isSummaryInformation())
					{
						SummaryInformation si = new SummaryInformation(property_set);
						if(si.getTitle() != null)
						   setProperty(FormatProperties.Title, si.getTitle());

						if(si.getAuthor() != null)
						   setProperty(FormatProperties.Author, si.getAuthor());

						if(si.getApplicationName() != null)
						   setProperty(FormatProperties.Software, si.getApplicationName());

						if(si.getRevNumber() != null)
						   setProperty(FormatProperties.Revisions, String.valueOf(si.getRevNumber()));

						if(si.getKeywords() != null)
							setProperty(FormatProperties.Keywords, si.getKeywords());

						if(si.getComments() != null)
							setProperty(FormatProperties.Comments, si.getComments());
					}
					else if(property_set.isDocumentSummaryInformation())
					{
						DocumentSummaryInformation dsi = new DocumentSummaryInformation(property_set);
					}
				}
			}
		}
		catch(java.io.FileNotFoundException e)
		{
			m_logger.error(e);
		}
		catch(org.apache.poi.hpsf.NoPropertySetStreamException e)
		{
			m_logger.error(e);
		}
		catch(org.apache.poi.hpsf.UnexpectedPropertySetTypeException e)
		{
			m_logger.error(e);
		}
		catch(org.apache.poi.hpsf.MarkUnsupportedException e)
		{
			m_logger.error(e);
		}

		setProperty(FormatProperties.Words, String.valueOf(properties.getCCh()));
		// TODO: Not functional.
		// setProperty(FormatProperties.Pages, String.valueOf(properties.getCPg()));
		setProperty(FormatProperties.Paragraphs, String.valueOf(properties.getCParas()));
	}
}