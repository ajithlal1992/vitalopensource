package testing.com.vtls.opensource.image;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.vtls.opensource.image.GenericImageSource;
import com.vtls.opensource.image.ImageIOImageSource;
import com.vtls.opensource.image.ThumbnailImageSource;
import com.vtls.opensource.image.VideoImageSource;

public class ImageSourceTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(ImageSourceTestCase.class);

	public ImageSourceTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

   public void testGetFormats()
   {
		String[] mime_types = ImageIO.getReaderMIMETypes();
      for(int i = 0; i < mime_types.length; i++)
         m_logger.info("Supported MIME Type: " + mime_types[i].toLowerCase());
   }
	
	public void testThumbnailImageSource() throws IOException, java.io.FileNotFoundException
	{
	   GenericImageSource source = new GenericImageSource(new FileInputStream("src/testing/data/test.jpg"));
	   BufferedImage buffered_image = source.getImage();

	   ThumbnailImageSource image_source = null;
	   image_source = new ThumbnailImageSource(buffered_image);
		assertNotNull(image_source.getImage());

      // Undefined. (Implied)
		ImageIO.write(image_source.getImage(), "png", new File("src/testing/data/image/ThumbnailImageSource.png"));

      // Undefined. (Explicit)
	   image_source.setWidth(ThumbnailImageSource.UNDEFINED);
	   image_source.setHeight(ThumbnailImageSource.UNDEFINED);
		ImageIO.write(image_source.getImage(), "png", new File("src/testing/data/image/ThumbnailImageSourceUxU.png"));

      // Width/Height (Typical)
	   image_source.setWidth(320);
	   image_source.setHeight(240);
		ImageIO.write(image_source.getImage(), "png", new File("src/testing/data/image/ThumbnailImageSource320x240.png"));

      // Width (Width implied)
	   image_source.setWidth(320);
	   image_source.setHeight(ThumbnailImageSource.UNDEFINED);
		ImageIO.write(image_source.getImage(), "png", new File("src/testing/data/image/ThumbnailImageSource320xU.png"));

      // Width (Height implied)
	   image_source.setWidth(ThumbnailImageSource.UNDEFINED);
	   image_source.setHeight(240);
		ImageIO.write(image_source.getImage(), "png", new File("src/testing/data/image/ThumbnailImageSourceUx240.png"));
		
		try
		{
		   BufferedImage cropped = buffered_image.getSubimage(0,0,100,100);
		   ImageIO.write(cropped, "png", new File("src/testing/data/image/ImageSourceCropped.png"));
		}
		catch(java.awt.image.RasterFormatException e)
		{
		   m_logger.error(e);
		}
	}

	public void testVideoImageSource() throws java.net.MalformedURLException, IOException
	{
	   VideoImageSource image_source = null;

	   image_source = new VideoImageSource(new File("src/testing/data/sample-large.mov"));
		assertNotNull(image_source.getImage());
		ImageIO.write(image_source.getImage(), "png", new File("src/testing/data/image/VideoImageSourceQuicktime.png"));

      // These formats are not supported at this time.
	   image_source = new VideoImageSource(new File("src/testing/data/sample-divx.avi"));
		assertNull(image_source.getImage());

	   image_source = new VideoImageSource(new File("src/testing/data/sample-large.mpg"));
		assertNull(image_source.getImage());
	}

	public void testImageIOImageSource() throws IOException, java.io.FileNotFoundException
	{
	   ImageIOImageSource image_source = null;
	   image_source = new ImageIOImageSource(new FileInputStream("src/testing/data/test.jpg"));
		assertNotNull(image_source.getImage());
		ImageIO.write(image_source.getImage(), "png", new File("src/testing/data/image/ImageIOImageSource.png"));
	}

	public void testGenericImageSource() throws IOException, java.io.FileNotFoundException
	{
	   GenericImageSource image_source = null;
	   image_source = new GenericImageSource(new FileInputStream("src/testing/data/test.jpg"));
		assertNotNull(image_source.getImage());
		ImageIO.write(image_source.getImage(), "png", new File("src/testing/data/image/GenericImageSource.png"));
	}
}
