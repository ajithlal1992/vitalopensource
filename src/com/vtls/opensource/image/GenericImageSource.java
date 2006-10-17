package com.vtls.opensource.image;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.media.jai.JAI;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;
/**
 * This class gets a BufferedImage from an InputStream source.
 */
public class GenericImageSource implements ImageSource
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(GenericImageSource.class);

	private BufferedImage m_image = null;
	
	/**
	 * Class constructor specifying the source codestream.
	 * @param _stream a specific source {@link InputStream}
	 * @throws IOException
	 */
	public GenericImageSource(InputStream _stream) throws IOException
	{
		MemoryCacheSeekableStream stream = new MemoryCacheSeekableStream(_stream);
		m_image = JAI.create("stream", stream).getAsBufferedImage();
		_stream.close();
	}
	
	/**
	 * Gets a {@link BufferedImage} based on the source
	 * @return a {@link BufferedImage}
	 * @throws IOException
	 */
	public BufferedImage getImage() throws IOException
	{
	   return m_image;
	}
}
