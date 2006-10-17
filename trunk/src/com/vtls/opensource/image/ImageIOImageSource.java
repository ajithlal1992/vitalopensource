package com.vtls.opensource.image;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

/**
* This class gets a BufferedImage using the {@link ImageIO}
* to read different image sources.
*/
public class ImageIOImageSource implements ImageSource
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(ImageIOImageSource.class);

   private BufferedImage m_image = null;

   /**
   * Class constructor specifying an InputStream as the image source
   * @param _stream an {@link InputStream} as the source of the image
   * @throws IOException
   */	
   public ImageIOImageSource(InputStream _stream) throws IOException
   {
      m_image = ImageIO.read(_stream);
      _stream.close();
   }

   /**
   * Class constructor specifying a File as the image source
   * @param _file an {@link File} of the source image 
   * @throws IOException
   */	
   public ImageIOImageSource(File _file) throws IOException
   {
      m_image = ImageIO.read(_file);
   }
   /**
   * Class constructor specifying a URL as the image source
   * @param _url an {@link URL} of the source image
   * @throws IOException
   */
   public ImageIOImageSource(URL _url) throws IOException
   {
      m_image = ImageIO.read(_url);
   }

   /**
   * Get a BufferedImage from image source
   * @return a source-based instance of {@link BufferedImage} 
   * @throws IOException
   */
   public BufferedImage getImage() throws IOException
   {
      return m_image;
   }
}
