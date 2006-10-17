package testing.com.vtls.opensource.text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.vtls.opensource.text.PDFTextSource;

import junit.framework.TestCase;

public class PDFTextSourceTestCase extends TestCase
{
   private static final String m_test_filename = "src/testing/data/test.pdf";
	private PDFTextSource m_text_source = null;
	
	public PDFTextSourceTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws FileNotFoundException, IOException
	{
		m_text_source = new PDFTextSource(new FileInputStream(m_test_filename));
	}

	protected void tearDown()
	{
	}

	public void testGetText() throws IOException
	{
		String text = m_text_source.getText();
		assertNotNull(text);
		assertTrue(text.indexOf("A B C D") >= 0);
		
		// TODO: Find a PDF extractor that handles the full Unicode set.
		// assertTrue(text.contains("豈 更 車 賈"));
	}
		
	public void testAssert()
	{
		assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(PDFTextSourceTestCase.class);
	}
}
