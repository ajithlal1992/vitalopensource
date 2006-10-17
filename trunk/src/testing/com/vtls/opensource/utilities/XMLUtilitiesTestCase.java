package testing.com.vtls.opensource.utilities;

import com.vtls.opensource.logging.Log4JLogger;
import com.vtls.opensource.utilities.StreamUtilities;
import com.vtls.opensource.utilities.XMLUtilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

public class XMLUtilitiesTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(XMLUtilitiesTestCase.class);

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}
	
	public void testGetValuesByXPath() throws FileNotFoundException, JDOMException, IOException, SAXException
	{
	   Document document = XMLUtilities.getSimpleDocument(new FileInputStream("src/testing/data/xml/simple.xml"));
	   String document_content = XMLUtilities.toString(document);
	   m_logger.info(document_content);

		assertNotNull(document);
      assertNotNull(document_content);
		
		List list = XMLUtilities.getValuesByXPath(document, "//node");
	   m_logger.info(list.toString());
	}
/*
	public void testGetNamespaceURI() throws FileNotFoundException, JDOMException, IOException
	{
	   Document document = XMLUtilities.getDocument(new FileInputStream("src/testing/data/xml/namespace.xml"));
	   String namespace_uri = XMLUtilities.getNamespaceURI(document);

       assertNotNull(document);
       assertNotNull(namespace_uri);
       assertEquals("http://www.loc.gov/MARC21/slim", namespace_uri);
	}

	public void testGetNamespaceURIDefault() throws FileNotFoundException, JDOMException, IOException
	{
	   Document document = XMLUtilities.getDocument(new FileInputStream("src/testing/data/xml/namespace_default.xml"));
	   String namespace_uri = XMLUtilities.getNamespaceURI(document);

       assertNotNull(document);
       assertNotNull(namespace_uri);
       m_logger.info(namespace_uri);
       assertEquals("", namespace_uri);
	}

	public void testGetDocument() throws FileNotFoundException, JDOMException, IOException
	{
	    Document document = XMLUtilities.getDocument(new FileInputStream("src/testing/data/xml/simple.xml"));
	    String document_content = XMLUtilities.toString(document);
	    m_logger.debug(document_content);

		assertNotNull(document);
        assertNotNull(document_content);
		assertTrue(document_content.contains("<node attribute=\"value\">Text value.</node>"));
	}

	public void testGetSimpleDocument() throws FileNotFoundException, JDOMException, IOException, SAXException
	{
	   Document document = XMLUtilities.getSimpleDocument(new FileInputStream("src/testing/data/xml/simple.xml"));
	   String document_content = XMLUtilities.toString(document);
	   m_logger.debug(document_content);

		assertNotNull(document);
      assertNotNull(document_content);
		assertTrue(document_content.contains("<node attribute=\"value\">Text value.</node>"));
	}

	public void testTransform() throws FileNotFoundException, JDOMException, IOException, TransformerException
	{
	   Document document = XMLUtilities.getDocument(new FileInputStream("src/testing/data/xml/simple.xml"));
	   StringBuffer transformation = XMLUtilities.transform(document, new FileInputStream("src/testing/data/xslt/simple.xsl"));
	   InputStream stream = StreamUtilities.getInputStreamFromString(transformation.toString());
	   Document result_document = XMLUtilities.getDocument(stream);

	   String document_content = XMLUtilities.toString(result_document);

      assertNotNull(document);
      assertNotNull(result_document);
      assertNotNull(document_content);

	   m_logger.debug(document_content);
		assertTrue(document_content.contains("<result>Two</result>"));
	}

	public void testTransformSandbox() throws FileNotFoundException, JDOMException, IOException, TransformerException
	{
	   Document document = XMLUtilities.getDocument(new FileInputStream("src/testing/data/xml/NLM.xml"));
	   StringBuffer transformation = XMLUtilities.transform(document, new FileInputStream("src/testing/data/xslt/NLM.xsl"));
	   InputStream stream = StreamUtilities.getInputStreamFromString(transformation.toString());
	   Document result_document = XMLUtilities.getDocument(stream);

	   String document_content = XMLUtilities.toString(result_document);

      assertNotNull(document);
      assertNotNull(result_document);
      assertNotNull(document_content);

	   m_logger.info(document_content);
	}
	
	public void testTransformMARC_DC() throws FileNotFoundException, JDOMException, IOException, TransformerException {
		Document document = XMLUtilities.getDocument(new FileInputStream("src/testing/data/xml/MARC.xml"));
		StringBuffer transformation = XMLUtilities.transform(document, new FileInputStream("src/testing/data/xslt/marc_dc.xsl"));
		InputStream stream = StreamUtilities.getInputStreamFromString(transformation.toString());
		Document result_document = XMLUtilities.getDocument(stream);

		String document_content = XMLUtilities.toString(result_document);
		
		assertNotNull(document);
		assertNotNull(result_document);
		assertNotNull(document_content);
		
		m_logger.info(document_content);
	}
*/
}
