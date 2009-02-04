package com.vtls.opensource.text;

import java.io.InputStream;
import java.io.IOException;

//import org.textmining.text.extraction.WordExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;

/**
 * Extracts text from Microsoft Word documents using the excellent Textmining
 * library. There aren't many great parsers available, but the POI project is
 * something to keep an eye on if development picks up.
 *
 * TODO: HWPFDocument doc = new HWPFDocument(new FileInputStream(args[0]));
 * Range r = doc.getRange();
 * String str = r.text();
 */
public class WordTextSource extends TextSource implements Text
{
	private WordExtractor m_extractor = null;
	
	/**
	 * Class constructor specifying a source {@link InputStream}
	 * @param _stream an input stream of a word document
	 */
	public WordTextSource(InputStream _stream) throws IOException
	{
		// Keep the stream as a member variable.
	   super(_stream);

		// Create extractor.
		m_extractor = new WordExtractor(m_stream);
	}
	
	/**
	 * Gets the text from a Word document
	 * @return a string of the PDF document
	 */
	public String getText() throws IOException
	{
		String _return = null;
		
		try
		{
			 _return = m_extractor.getText();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return _return;
		}
	}
}

