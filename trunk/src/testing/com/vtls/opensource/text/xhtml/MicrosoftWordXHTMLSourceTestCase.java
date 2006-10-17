package testing.com.vtls.opensource.text.xhtml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.ecs.xml.XML;
import com.vtls.opensource.text.xhtml.MicrosoftWordXHTMLSource;

import junit.framework.TestCase;

public class MicrosoftWordXHTMLSourceTestCase extends TestCase
{
   private static final String m_test_filename = "src/testing/data/test.doc";

	public MicrosoftWordXHTMLSourceTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

   /**
    * Note: Example usage of the MicrosoftWordXHTMLSource class.
    */
   public void testExample() throws FileNotFoundException, IOException
   {
      MicrosoftWordXHTMLSource source = new MicrosoftWordXHTMLSource(new FileInputStream(m_test_filename));
      XML xml = source.getXHTML();
      
      // Test that the String containing the HTML contents is valid...
      assertNotNull(xml.toString());
   }
   
	public void testAssert()
	{
		assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(MicrosoftWordXHTMLSourceTestCase.class);
	}
}
