package com.vtls.opensource.text;

import java.io.InputStream;
import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Extracts text from HTML documents.
 */
public class HTMLTextSource extends TextSource implements Text
{
	private HTMLEditorKit m_editor_kit = null;
	
	/**
	 * Class constructor specifying a source {@link InputStream}
	 * @param _stream an input stream of a HTML document
	 */
	public HTMLTextSource(InputStream _stream)
	{
		// Keep the stream as a member variable.
	   super(_stream);
		
		// Create the HTMLEditorKit object.
		m_editor_kit = new HTMLEditorKit();
	}
	
	/**
	 * Gets the source text using a HTMLEditorKit to insert content 
	 * from the given source stream to a HTML Document.
	 * @return a string of the HTML document
	 * @throws IOException  
	 */
	public String getText() throws IOException
	{
		String _return = null;

		// Source: http://www.jguru.com/faq/view.jsp?EID=1074229
		Document document = m_editor_kit.createDefaultDocument();
		document.putProperty("IgnoreCharsetDirective", new Boolean(true));

		try
		{
			m_editor_kit.read(m_stream, document, 0);
			_return = document.getText(0, document.getLength());
		}
		catch(BadLocationException ble)
		{
			ble.printStackTrace();
		}

		return _return;	   
	}
}

