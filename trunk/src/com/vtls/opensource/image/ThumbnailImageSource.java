package com.vtls.opensource.image;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
/**
 * This class resizes the BufferedImage to a thumbnail image 
 * according to a specific ratio from image source.
 */
public class ThumbnailImageSource implements ImageSource
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(ThumbnailImageSource.class);

	private BufferedImage m_image = null;

   // Image dimensions.
	public static final int UNDEFINED = -1;
	
	private int m_height = ThumbnailImageSource.UNDEFINED;
	private int m_width = ThumbnailImageSource.UNDEFINED;
	
	// Keep small images unchanged.
	private static final boolean m_is_bounded = true;
	
	/**
	 * Class constructor specifying a source {@link BufferedImage} 
	 * @param _image a source BufferedImage
	 * @throws IOException
	 */
	public ThumbnailImageSource(BufferedImage _image) throws IOException
	{
	   m_image = _image;
	}
	
	/**
	 * Gets a {@link BufferedImage} based on the source
	 * @return a {@link BufferedImage}
	 * @throws IOException
	 */
	public BufferedImage getImage() throws IOException
	{
		// Check for a valid Image.
		if(m_image == null)
		{
		   return null;
		}
		// If requesting an image with invalid dimensions, return the whole thing.
		else if(m_width <= 0 && m_height <= 0)
		{
			return m_image;
		}
		// Otherwise, return a scaled image.
		else
		{
         double x_factor = (double)m_width / (double)m_image.getWidth(null);
         double y_factor = (double)m_height / (double)m_image.getHeight(null);

         // We know that one of these is initialized...
         if(x_factor <= 0)
         {
        	 x_factor = y_factor;
         }
	
         // ... so we can safely pick the other.
         if(y_factor <= 0)
         {
        	 y_factor = x_factor;
         }

         // If we don't want to scale small images, return full image.
         if(m_is_bounded && (x_factor > 1.0) && (y_factor > 1.0))
         {
        	 return m_image;
         }

         return scaleImage(m_image, x_factor, y_factor);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Sets a specific width to scale this BufferedImage
	 * @param _width an int of the scaled width
	 */
	public void setWidth(int _width)
	{
	   m_width = _width;
	}

	/**
	 * Sets a specific height to scale this BufferedImage
	 * @param _height an int of the scaled height
	 */
	public void setHeight(int _height)
	{
	   m_height = _height;
	}
	
	/**
	 * Gets the scaled width of this BufferedImage
	 * @return an int of the scaled width
	 */
	public int getWidth()
	{
	   return m_width;
	}
	
	/**
	 * Gets the scaled width of this BufferedImage
	 * @return an int of the scaled height
	 */
	public int getHeight()
	{
	   return m_height;
	}

	///////////////////////////////////////////////////////////////////////////
	// Local Methods //////////////////////////////////////////////////////////
	/**
	 * Resizes an image by specific scale ratioes for width and height
	 * @param _image the source Image to be scaled
	 * @param scale_x the scale ratio of the width
	 * @param scale_y the scale ratio of the height 
	 */
   protected BufferedImage scaleImage(Image _image, double scale_x, double scale_y)
	{
		if(_image == null)
			return null;

		final int width = (int)(scale_x * _image.getWidth(null));
		final int height = (int)(scale_y * _image.getHeight(null));
		
		_image = _image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

		// Set up the return image.
		BufferedImage _return = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = _return.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(_image, 0,0, null);
		g.dispose();
		
		return _return;
	}
}
