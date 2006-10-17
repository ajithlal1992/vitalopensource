package testing.com.vtls.opensource.jhove;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import org.jdom.Document;
import org.jdom.JDOMException;

import com.vtls.opensource.jhove.JHOVEDocumentFactory;
import com.vtls.opensource.xml.XMLUtilities;

public class JHOVEDocumentFactoryTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(JHOVEDocumentFactoryTestCase.class);
   private JHOVEDocumentFactory m_factory = null;

	public JHOVEDocumentFactoryTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	   m_factory = JHOVEDocumentFactory.getInstance();
	}

	protected void tearDown()
	{
	}

	public void testGetDocumentURL() throws IOException, JDOMException
	{
	   Document document = m_factory.getDocument(new URL("http://hul.harvard.edu/jhove/index.html"));
	   assertNotNull(document);
	}

	public void testGetDocumentInputStream() throws IOException, JDOMException
	{
	   Document document = m_factory.getDocument(new FileInputStream("src/testing/data/test.pdf"), "example.pdf");
	   assertNotNull(document);
	}

	public void testGetDocumentFile() throws IOException, JDOMException
	{
	   Document document = m_factory.getDocument(new File("src/testing/data/test.pdf"));
	   assertNotNull(document);
	}

	public void testGetDocumentPDF() throws IOException, JDOMException
	{
	   Document document = m_factory.getDocument(new File("src/testing/data/test.pdf"));
	   m_logger.info(XMLUtilities.toString(document));
	   assertNotNull(document);
	}

	public void testGetDocumentSandbox() throws IOException, JDOMException
	{
	   Document document = m_factory.getDocument(new File("src/testing/data/sample-jpeg2000.jp2"));
	   assertNotNull(document);
	}
}
