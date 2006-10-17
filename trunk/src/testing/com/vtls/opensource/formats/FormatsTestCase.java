package testing.com.vtls.opensource.formats;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.vtls.opensource.formats.FormatProperties;
import com.vtls.opensource.formats.PDFProperties;
import com.vtls.opensource.formats.AudioProperties;
import com.vtls.opensource.formats.MediaProperties;
import com.vtls.opensource.formats.ImageProperties;
import com.vtls.opensource.formats.JPEG2000Properties;
import com.vtls.opensource.formats.MrSIDProperties;
import com.vtls.opensource.formats.JPEGProperties;
import com.vtls.opensource.formats.MicrosoftDocumentProperties;

public class FormatsTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(FormatsTestCase.class);

	public FormatsTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testWordProperties() throws IOException, java.io.FileNotFoundException
	{
	   MicrosoftDocumentProperties properties = null;
	   properties = new MicrosoftDocumentProperties(new FileInputStream("src/testing/data/sample-large.doc"));
		assertNotNull(properties);
		m_logger.info(properties);
	}

	public void testMrSIDProperties() throws IOException, java.io.FileNotFoundException
	{
	   MrSIDProperties properties = null;
	   properties = new MrSIDProperties(new FileInputStream("src/testing/data/sample.sid"));
		assertNotNull(properties);
		m_logger.info(properties);
	}

	public void testPDFProperties() throws IOException, java.io.FileNotFoundException
	{
	   PDFProperties properties = null;
	   properties = new PDFProperties(new FileInputStream("src/testing/data/sample-large.pdf"));
		assertNotNull(properties);
		assertNotNull(properties.getProperty(FormatProperties.Version));
		assertNotNull(properties.getProperty(FormatProperties.Title));
		assertNotNull(properties.getProperty(FormatProperties.Pages));
		assertNotNull(properties.getProperty(FormatProperties.PDFProducer));
		assertNotNull(properties.getProperty(FormatProperties.ContentCreator));
	}

	public void testAudioProperties() throws IOException, java.io.FileNotFoundException
	{
	   AudioProperties properties = null;
	   properties = new AudioProperties(new FileInputStream("src/testing/data/sample.wav"));
		assertNotNull(properties);
		assertNotNull(properties.getProperty(FormatProperties.Type));
		assertNotNull(properties.getProperty(FormatProperties.FrameRate));
		assertNotNull(properties.getProperty(FormatProperties.Channels));
		assertNotNull(properties.getProperty(FormatProperties.Endianness));
		assertNotNull(properties.getProperty(FormatProperties.Size));
		assertNotNull(properties.getProperty(FormatProperties.SampleRate));
		assertNotNull(properties.getProperty(FormatProperties.FrameSize));
		assertNotNull(properties.getProperty(FormatProperties.SampleSize));
		assertNotNull(properties.getProperty(FormatProperties.Encoding));
		assertNotNull(properties.getProperty(FormatProperties.FrameLength));
	}

	public void testMediaProperties() throws IOException, java.io.FileNotFoundException
	{
	   MediaProperties properties = null;
	   properties = new MediaProperties(new FileInputStream("src/testing/data/sample-large.mpg"), "mpg");
		assertNotNull(properties);
		assertNotNull(properties.getProperty(FormatProperties.Width));
		assertNotNull(properties.getProperty(FormatProperties.Height));
		assertNotNull(properties.getProperty(FormatProperties.Size));
		assertNotNull(properties.getProperty(FormatProperties.Dimensions));
		assertNotNull(properties.getProperty(FormatProperties.FrameRate));
		assertNotNull(properties.getProperty(FormatProperties.Encoding));

	   properties = new MediaProperties(new File("src/testing/data/sample-large.mp3"));
		assertNotNull(properties);
		assertNotNull(properties.getProperty(FormatProperties.FrameRate));
		assertNotNull(properties.getProperty(FormatProperties.Encoding));
		assertNotNull(properties.getProperty(FormatProperties.Channels));
		assertNotNull(properties.getProperty(FormatProperties.SampleRate));
		assertNotNull(properties.getProperty(FormatProperties.FrameSize));
		assertNotNull(properties.getProperty(FormatProperties.Size));
		assertNotNull(properties.getProperty(FormatProperties.Signed));
	}

	public void testImageProperties() throws IOException, java.io.FileNotFoundException
	{
	   ImageProperties properties = null;
	   properties = new ImageProperties(new FileInputStream("src/testing/data/test.jpg"));
		assertNotNull(properties);
		assertNotNull(properties.getProperty(FormatProperties.Width));
		assertNotNull(properties.getProperty(FormatProperties.Height));
		assertNotNull(properties.getProperty(FormatProperties.Resolution));
		assertNotNull(properties.getProperty(FormatProperties.BitsPerPixel));
		assertNotNull(properties.getProperty(FormatProperties.PhysicalDimensions));
		assertNotNull(properties.getProperty(FormatProperties.Progressive));
		assertNotNull(properties.getProperty(FormatProperties.NumberOfImages));
		assertNotNull(properties.getProperty(FormatProperties.Format));
	}

	public void testJPEGProperties() throws IOException, java.io.FileNotFoundException
	{
	   JPEGProperties properties = null;
	   properties = new JPEGProperties(new FileInputStream("src/testing/data/sample-exif.jpg"));
		assertNotNull(properties);
		assertNotNull(properties.getProperty(FormatProperties.Make));
		assertNotNull(properties.getProperty(FormatProperties.Model));
	}

	public void testJPEG2000Properties() throws IOException, java.io.FileNotFoundException
	{
	   JPEG2000Properties properties = null;
	   properties = new JPEG2000Properties(new FileInputStream("src/testing/data/sample-jpeg2000.jp2"));
		assertNotNull(properties);
		assertNotNull(properties.getProperty(FormatProperties.Width));
		assertNotNull(properties.getProperty(FormatProperties.Height));
		assertNotNull(properties.getProperty(FormatProperties.ResolutionLevels));
	}
}
