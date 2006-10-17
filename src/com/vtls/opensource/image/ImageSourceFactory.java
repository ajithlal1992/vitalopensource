package com.vtls.opensource.image;

import java.io.InputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * Usage:
 * <code>
 *    ImageSourceFactory source_factory = ImageSourceFactory.getInstance();
 *    ImageSource source = source_factory.getImageSource("text/plain", (InputStream)stream);
 * </code>
 */
public class ImageSourceFactory
{
	// Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(ImageSourceFactory.class);

	private static ImageSourceFactory m_instance = null;
	private Set m_set = null;

	/**
	 * Private constructor.  Loads all mime-types into its Hash.
	 */
	private ImageSourceFactory()
	{
		m_set = new HashSet();

		String[] mime_types = ImageIO.getReaderMIMETypes();
		for(int i = 0; i < mime_types.length; i++)
			m_set.add(mime_types[i].toLowerCase());
	}

	/**
	 * Get a singleton instance of this class.
	 * @return the singleton instance of the ImageSourceFactory 
	 */
	public static ImageSourceFactory getInstance()
	{
		if(m_instance == null)
		{
			m_instance = new ImageSourceFactory();
		}
		return m_instance;
	}

	/**
	 * Get a instance of {@link ImageSource} depends on realtime
	 * file format type.
	 * @param _mime_type A string of the MIME type
	 * @param _stream the source file codestream
	 * @throws IOException
	 */
	public ImageSource getImageSource(String _mime_type, InputStream _stream) throws IOException
	{
		if(_mime_type == null)
			return null;

		if(m_set.contains(_mime_type))
			return new ImageIOImageSource(_stream);
		else if(_mime_type.startsWith("image/"))
			return new GenericImageSource(_stream);
		else if(_mime_type.equals("video/quicktime"))
			return new VideoImageSource(_stream);
		else return null;
	}
}