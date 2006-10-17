package com.vtls.opensource.text;

import java.io.InputStream;
import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * Extracts text from RTF documents.
 */
public class RTFTextSource extends TextSource implements Text
{
	private RTFEditorKit m_editor_kit = null;

	/**
	 * Class constructor specifying a source {@link InputStream}
	 * @param _stream an input stream of a RTF document
	 */
	public RTFTextSource(InputStream _stream)
	{
		// Keep the stream as a member variable.
		super(_stream);

		// Create the RTFEditorKit object.
		m_editor_kit = new RTFEditorKit();
	}

	///////////////////////////////////////////////////////////////////////////
	// Local Methods //////////////////////////////////////////////////////////
	/**
	 * Gets the source text using a RTFEditorKit to insert content 
	 * from the given source stream to a RTF Document.
	 * @return a string of the RTF document
	 * @throws IOException  
	 */
	public String getText() throws IOException
	{
		String _return = null;

		// Source: http://www.jguru.com/faq/view.jsp?EID=1074229
		Document document = m_editor_kit.createDefaultDocument();

		try
		{
			m_editor_kit.read(m_stream, document, 0);
			_return = document.getText(0, document.getLength());
		}
		catch(BadLocationException ble)
		{
			ble.printStackTrace();
		}
		finally
		{
			return _return;
		}
	}
}

