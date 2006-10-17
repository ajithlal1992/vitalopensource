package testing.com.vtls.opensource.text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.vtls.opensource.text.TextSource;
import com.vtls.opensource.text.TextSourceFactory;

import junit.framework.TestCase;

public class TextSourceFactoryTestCase extends TestCase
{
   private static final String m_data_directory = "./src/testing/data/";
	private TextSourceFactory m_factory = null;
	
	public TextSourceFactoryTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws FileNotFoundException
	{
		m_factory = TextSourceFactory.getInstance();
	}

	protected void tearDown()
	{
	}

	public void testHTML() throws IOException
	{
      TextSource source = m_factory.newTextSource(new FileInputStream(m_data_directory + "test.html"), "text/html");
      assertNotNull(source.getText());
	}
		
	public void testPDFDocument() throws IOException
	{
      TextSource source = m_factory.newTextSource(new FileInputStream(m_data_directory + "test.pdf"), "application/pdf");
      assertNotNull(source.getText());
	}
		
	public void testRTFDocument() throws IOException
	{
      TextSource source = m_factory.newTextSource(new FileInputStream(m_data_directory + "test.rtf"), "text/rtf");
      assertNotNull(source.getText());
	}
		
	public void testWordDocument() throws IOException
	{
      TextSource source = m_factory.newTextSource(new FileInputStream(m_data_directory + "test.doc"), "application/msword");
      assertNotNull(source.getText());
	}
		
	public void testPlain() throws IOException
	{
      TextSource source = m_factory.newTextSource(new FileInputStream(m_data_directory + "test.txt"), "text/plain");
      assertNotNull(source.getText());
	}

	public void testXML() throws IOException
	{
      TextSource source = m_factory.newTextSource(new FileInputStream(m_data_directory + "test.xml"), "text/xml");
      assertNotNull(source.getText());
	}
		
	public void testAssert()
	{
		assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(TextSourceFactoryTestCase.class);
	}
}
