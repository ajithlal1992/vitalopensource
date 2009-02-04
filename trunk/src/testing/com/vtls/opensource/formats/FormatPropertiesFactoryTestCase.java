package testing.com.vtls.opensource.formats;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.FileInputStream;
import java.io.IOException;

import com.vtls.opensource.formats.FormatProperties;
import com.vtls.opensource.formats.FormatPropertiesFactory;

public class FormatPropertiesFactoryTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(FormatPropertiesFactoryTestCase.class);

	public FormatPropertiesFactoryTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testSID() throws IOException, java.io.FileNotFoundException
	{
	   FormatPropertiesFactory factory = FormatPropertiesFactory.getInstance();
	   FormatProperties properties = null;
	   
	   properties = factory.getProperties("image/x-mrsid-image", new FileInputStream("src/testing/data/sample-mrsid2.sid"));
		assertNotNull(properties);
		m_logger.info(properties);
	}
/*
	public void testLifeCycle() throws IOException, java.io.FileNotFoundException
	{
	   FormatPropertiesFactory factory = FormatPropertiesFactory.getInstance();
	   FormatProperties properties = null;
	   
	   properties = factory.getProperties("application/pdf", new FileInputStream("src/testing/data/sample-large.pdf"));
		assertNotNull(properties);
		assertNotNull(properties.getProperty(FormatProperties.Version));
		assertNotNull(properties.getProperty(FormatProperties.Title));
		assertNotNull(properties.getProperty(FormatProperties.Pages));
		assertNotNull(properties.getProperty(FormatProperties.PDFProducer));
		assertNotNull(properties.getProperty(FormatProperties.ContentCreator));
		m_logger.info(properties);
	}

	public void testXMLEAD() throws IOException, java.io.FileNotFoundException
	{
	   FormatPropertiesFactory factory = FormatPropertiesFactory.getInstance();
	   FormatProperties properties = null;
	   
	   properties = factory.getProperties("text/xml", new FileInputStream("src/testing/data/sample-ead.xml"));
		assertNotNull(properties);
		m_logger.info(properties);
	}

	public void testXMLMARCXML() throws IOException, java.io.FileNotFoundException
	{
	   FormatPropertiesFactory factory = FormatPropertiesFactory.getInstance();
	   FormatProperties properties = null;
	   
	   properties = factory.getProperties("text/xml", new FileInputStream("src/testing/data/xml/MARC.xml"));
		assertNotNull(properties);
		m_logger.info(properties);
	}

	public void testXMLMODS() throws IOException, java.io.FileNotFoundException
	{
	   FormatPropertiesFactory factory = FormatPropertiesFactory.getInstance();
	   FormatProperties properties = null;
	   
	   properties = factory.getProperties("text/xml", new FileInputStream("src/testing/data/sample-mods.xml"));
		assertNotNull(properties);
		m_logger.info(properties);
	}

	public void testXMLTEI() throws IOException, java.io.FileNotFoundException
	{
	   FormatPropertiesFactory factory = FormatPropertiesFactory.getInstance();
	   FormatProperties properties = null;
	   
	   properties = factory.getProperties("text/xml", new FileInputStream("src/testing/data/sample-tei2.xml"));
		assertNotNull(properties);
		m_logger.info(properties);
	}
	*/
}
