package com.vtls.opensource.text;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/**
 * Extracts text from PDF documents using the PDFBox library.
 */
public class PDFTextSource extends TextSource implements Text
{
	private PDDocument m_document = null;
	
	/**
	 * Class constructor specifying a source {@link InputStream}
	 * @param _stream an input stream of a PDF document
	 */
	public PDFTextSource(InputStream _stream)
	{
	   super(_stream);
	}
	
	/**
	 * Loads the source steam to a in-memory {@link PDDocument}, 
	 * outputs the document content using PDFTextStripper to a string
	 * @return a string of the PDF document
	 * @throws IOException  
	 */
	public String getText() throws IOException
	{
		String _return = null;
		
		// Open the PDF stream.
		m_document = PDDocument.load(m_stream);

		// Create a PrintWriter we can extract a String from.
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		Writer writer = new BufferedWriter(new OutputStreamWriter(bytes));

		PDFTextStripper stripper = new PDFTextStripper();
		
		// Set the document and writer.
		stripper.writeText(m_document, writer);
		_return = bytes.toString();
		m_document.close();
		return _return;
	}

	/**
	 * Gets a specific page in the PDF file
	 * @param _page an int of the start page value 
	 * @return a string of the selected page in the PDF file
	 */
	public String getTextForPage(int _page)
	{
		String _return = null;
		
		try
		{
			// Open the PDF.
			m_document = PDDocument.load(m_stream);

			// Create a PrintWriter we can extract a String from.
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			Writer writer = new BufferedWriter(new OutputStreamWriter(bytes));

			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setStartPage(_page);
			stripper.setEndPage(_page + 1);
			
			// Set the document and writer.
			stripper.writeText(m_document, writer);
			
			_return = bytes.toString();

			m_document.close();
		}
		catch (Exception e)
		{
			// m_logger.error(e);
		}
		finally
		{
			return _return;
		}
	}
}
