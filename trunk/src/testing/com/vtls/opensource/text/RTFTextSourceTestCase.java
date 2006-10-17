package testing.com.vtls.opensource.text;

import junit.framework.TestCase;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.vtls.opensource.text.RTFTextSource;

public class RTFTextSourceTestCase extends TestCase
{
   private static final String m_test_filename = "src/testing/data/test.rtf";

	private RTFTextSource m_text_source = null;
	
	public RTFTextSourceTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws FileNotFoundException
	{
      m_text_source = new RTFTextSource(new FileInputStream(m_test_filename));
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
		junit.textui.TestRunner.run(RTFTextSourceTestCase.class);
	}
}
