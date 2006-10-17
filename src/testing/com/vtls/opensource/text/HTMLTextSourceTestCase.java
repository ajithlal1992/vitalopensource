package testing.com.vtls.opensource.text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.vtls.opensource.text.HTMLTextSource;

import junit.framework.TestCase;

public class HTMLTextSourceTestCase extends TestCase
{
   private static final String m_test_filename = "src/testing/data/test.html";
	private HTMLTextSource m_text_source = null;
	
	public HTMLTextSourceTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws FileNotFoundException
	{
		m_text_source = new HTMLTextSource(new FileInputStream(m_test_filename));
	}

	protected void tearDown()
	{
	}

	public void testGetText() throws IOException
	{
		String text = m_text_source.getText();
		assertNotNull(text);
		assertTrue(text.indexOf("A B C D") >= 0);
		assertTrue(text.indexOf("豈 更 車 賈") >= 0);
	}
		
	public void testAssert()
	{
		assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(HTMLTextSourceTestCase.class);
	}
}
