package com.vtls.opensource.formats;

import java.io.InputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * Usage:
 * <code>
 *    FormatPropertiesFactory source_factory = FormatPropertiesFactory.getInstance();
 *    ImageSource source = source_factory.getProperties("text/plain", (InputStream)stream);
 * </code>
 */
public class FormatPropertiesFactory
{
    // Log4j instance.
    private static final Logger m_logger = Log4JLogger.getLogger(FormatPropertiesFactory.class);

	private static FormatPropertiesFactory m_instance = null;
	
	private FormatPropertiesFactory()
	{
	}
	
	/**
	 * Get a singleton instance of this class.
	 * @return the singleton instance of the FormatPropertiesFactory 
	 */
	public static FormatPropertiesFactory getInstance()
	{
		if(m_instance == null)
		{
			m_instance = new FormatPropertiesFactory();
		}
		return m_instance;
	}
	
	/**
	 * Get a instance of {@link FormatProperties} depends on realtime
	 * file format type.
	 * @param _mime_type A string of the MIME type
	 * @param _stream the source file codestream
	 * @throws IOException
	 */
	public FormatProperties getProperties(String _mime_type, InputStream _stream) throws IOException
	{
		  if(_mime_type == null)
		  {
			return null;
		  }
				
	      if(_mime_type.equals("application/pdf"))
				return new PDFProperties(_stream);
	      else if(_mime_type.equals("text/xml"))
				return new XMLProperties(_stream);
	      else if(_mime_type.equals("image/x-mrsid-image"))
				return new MrSIDProperties(_stream);
	      else if(_mime_type.equals("image/jp2") || _mime_type.equals("image/j2k") || _mime_type.equals("image/jpeg2000"))
				return new JPEG2000Properties(_stream);
	      else if(_mime_type.equals("image/jpg") || _mime_type.equals("image/jpeg"))
				return new JPEGProperties(_stream);
	      else if(_mime_type.startsWith("image/"))
				return new ImageProperties(_stream);
	      else if(_mime_type.equals("video/x-mpeg") || _mime_type.equals("video/mpeg") || _mime_type.equals("video/mpeg4"))
				return new MediaProperties(_stream, "mpeg");
	      else if(_mime_type.equals("video/quicktime"))
				return new MediaProperties(_stream, "mov");
	      else if(_mime_type.equals("video/avi"))
				return new MediaProperties(_stream, "avi");
	      else if(_mime_type.equals("video/x-ms-wmv"))
				return new MediaProperties(_stream, "wmv");
	      else if(_mime_type.equals("audio/aiff"))
				return new MediaProperties(_stream, "aiff");
	      else if(_mime_type.equals("audio/mid") || _mime_type.equals("audio/midi"))
				return new MediaProperties(_stream, "mid");
	      else if(_mime_type.equals("audio/mp3") || _mime_type.equals("audio/x-mp3") || _mime_type.equals("audio/mpeg3"))
				return new MediaProperties(_stream, "mp3");
	      else if(_mime_type.equals("audio/wav"))
				return new MediaProperties(_stream, "wav");
	      else if(_mime_type.equals("audio/x-m4p"))
				return new MediaProperties(_stream, "m4p");
	      else if(_mime_type.equals("audio/x-ms-wma"))
				return new MediaProperties(_stream, "wma");
	      else if(_mime_type.startsWith("audio"))
				return new AudioProperties(_stream);
		  else return null;
	}
}