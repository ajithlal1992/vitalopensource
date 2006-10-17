package com.vtls.opensource.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Extracts text from plain text documents. This also serves as a super-class
 * of the specific {@link TextSource} for a particular MIME type.
 */
public class TextSource implements Text
{
	
	InputStream m_stream = null;
	
	/**
	 * Class constructor
	 */
	public TextSource()
	{
	}
   
	/**
	 * Class constructor specifying an {@link InputStream}
	 * @param _stream 
	 */
	public TextSource(InputStream _stream)
	{
		m_stream = _stream;
	}
   
	/**
	 * Gets the text from source.
	 * @return a string of the source text
	 * @throws IOException  
	 */
	public String getText() throws IOException
	{
	   if(m_stream == null)
	   {
		   return null;
	   }
	      
	   // Create a StringWriter for the return value.
      StringWriter _return = new StringWriter();

      int length = 0;
      char buffer[] = new char[1024];

      // Buffer the InputStream and write to our StringWriter instance.
      BufferedReader reader = new BufferedReader(new InputStreamReader(m_stream));
      while((length = reader.read(buffer, 0, 1024)) != -1)
      {
         _return.write(buffer, 0, length);
      }

      // Return.
      return _return.toString();
	}
}
